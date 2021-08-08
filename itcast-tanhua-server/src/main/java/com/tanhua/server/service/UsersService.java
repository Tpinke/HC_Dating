package com.tanhua.server.service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tanhua.common.enums.SexEnum;
import com.tanhua.common.pojo.*;
import com.tanhua.dubbo.server.api.UserLikeApi;
import com.tanhua.dubbo.server.api.UsersApi;
import com.tanhua.dubbo.server.api.VisitorsApi;
import com.tanhua.dubbo.server.pojo.UserLike;
import com.tanhua.dubbo.server.pojo.Users;
import com.tanhua.dubbo.server.pojo.Visitors;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.server.utils.UserThreadLocal;
import com.tanhua.server.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsersService {

    @Autowired
    private UserInfoService userInfoService;

    @Reference(version = "1.0.0")
    private UserLikeApi userLikeApi;

    @Reference(version = "1.0.0")
    private VisitorsApi visitorsApi;

    @Reference(version = "1.0.0")
    private UsersApi usersApi;

    @Autowired
    private IMService imService;

    @Autowired
    private RecommendUserService recommendUserService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private BlackListService blackListService;

    public UserInfoVo queryUserInfo(String userID, String huanxinID) {
        User user = UserThreadLocal.get();
        Long userId = user.getId();
        if (StringUtils.isNotBlank(userID)) {
            userId = Long.valueOf(userID);
        } else if (StringUtils.isNotBlank(huanxinID)) {
            userId = Long.valueOf(huanxinID);
        }

        UserInfo userInfo = this.userInfoService.queryById(userId);
        if (null == userInfo) {
            return null;
        }

        UserInfoVo userInfoVo = new UserInfoVo();
        userInfoVo.setAge(userInfo.getAge() != null ? userInfo.getAge().toString() : null);
        userInfoVo.setAvatar(userInfo.getLogo());
        userInfoVo.setBirthday(userInfo.getBirthday());
        userInfoVo.setEducation(userInfo.getEdu());
        userInfoVo.setCity(userInfo.getCity());
        userInfoVo.setGender(userInfo.getSex().name().toLowerCase());
        userInfoVo.setId(userInfo.getUserId());
        userInfoVo.setIncome(userInfo.getIncome() + "K");
        userInfoVo.setMarriage(StringUtils.equals(userInfo.getMarriage(), "已婚") ? 1 : 0);
        userInfoVo.setNickname(userInfo.getNickName());
        userInfoVo.setProfession(userInfo.getIndustry());
        return userInfoVo;
    }

    public Boolean updateUserInfo(UserInfoVo userInfoVo) {
        User user = UserThreadLocal.get();
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(user.getId());
        userInfo.setAge(Integer.valueOf(userInfoVo.getAge()));
        userInfo.setSex(StringUtils.equalsIgnoreCase(userInfoVo.getGender(), "man") ? SexEnum.MAN : SexEnum.WOMAN);
        userInfo.setBirthday(userInfoVo.getBirthday());
        userInfo.setCity(userInfoVo.getCity());
        userInfo.setEdu(userInfoVo.getEducation());
        userInfo.setIncome(StringUtils.replaceAll(userInfoVo.getIncome(), "K", ""));
        userInfo.setIndustry(userInfoVo.getProfession());
        userInfo.setMarriage(userInfoVo.getMarriage() == 1 ? "已婚" : "未婚");
        return this.userInfoService.updateUserInfoByUserId(userInfo);
    }

    public CountsVo queryCounts() {
        User user = UserThreadLocal.get();
        CountsVo countsVo = new CountsVo();

        countsVo.setEachLoveCount(this.userLikeApi.queryEachLikeCount(user.getId()));
        countsVo.setFanCount(this.userLikeApi.queryFanCount(user.getId()));
        countsVo.setLoveCount(this.userLikeApi.queryLikeCount(user.getId()));

        return countsVo;
    }

    public PageResult queryLikeList(Integer type, Integer page, Integer pageSize, String nickname) {
        User user = UserThreadLocal.get();
        PageResult pageResult = new PageResult();
        pageResult.setPagesize(pageSize);
        pageResult.setPage(page);
        pageResult.setPages(0);
        pageResult.setCounts(0);
        // type: 1 互相关注 2 我关注 3 粉丝 4 谁看过我

        List<Long> userIds = new ArrayList<>();
        switch (type) {
            case 1: {
                PageInfo<UserLike> pageInfo = this.userLikeApi.queryEachLikeList(user.getId(), page, pageSize);
                for (UserLike record : pageInfo.getRecords()) {
                    userIds.add(record.getUserId());
                }

                break;
            }
            case 2: {
                PageInfo<UserLike> pageInfo = this.userLikeApi.queryLikeList(user.getId(), page, pageSize);
                for (UserLike record : pageInfo.getRecords()) {
                    userIds.add(record.getLikeUserId());
                }

                break;
            }
            case 3: {
                PageInfo<UserLike> pageInfo = this.userLikeApi.queryFanList(user.getId(), page, pageSize);
                for (UserLike record : pageInfo.getRecords()) {
                    userIds.add(record.getUserId());
                }

                break;
            }
            case 4: {
                List<Visitors> visitors = this.visitorsApi.topVisitor(user.getId(), page, pageSize);
                for (Visitors visitor : visitors) {
                    userIds.add(visitor.getVisitorUserId());
                }
                //将当前时间写入到redis中，key："visitor_date_" + user.getId()
                String key = "visitor_date_" + user.getId();
                this.redisTemplate.opsForValue().set(key, String.valueOf(System.currentTimeMillis()));
                break;
            }
        }

        if (CollectionUtils.isEmpty(userIds)) {
            return pageResult;
        }


        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userIds);
        if (StringUtils.isNotBlank(nickname)) {
            queryWrapper.like("nick_name", nickname);
        }
        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoList(queryWrapper);

        List<UserLikeListVo> userLikeListVos = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            UserLikeListVo userLikeListVo = new UserLikeListVo();
            userLikeListVo.setAge(userInfo.getAge());
            userLikeListVo.setAvatar(userInfo.getLogo());
            userLikeListVo.setCity(userInfo.getCity());
            userLikeListVo.setEducation(userInfo.getEdu());
            userLikeListVo.setGender(userInfo.getSex().name().toLowerCase());
            userLikeListVo.setId(userInfo.getUserId());
            userLikeListVo.setMarriage(StringUtils.equals(userInfo.getMarriage(), "已婚") ? 1 : 0);
            userLikeListVo.setNickname(userInfo.getNickName());

            if(type == 1 || type == 2){
                userLikeListVo.setAlreadyLove(true);
            }else{
                userLikeListVo.setAlreadyLove(false);
            }

            Double score = this.recommendUserService.queryScore(user.getId(), userInfo.getUserId());
            if(score == 0){
                score = RandomUtils.nextDouble(30, 90);
            }
            userLikeListVo.setMatchRate(score.intValue());


            userLikeListVos.add(userLikeListVo);
        }

        pageResult.setItems(userLikeListVos);
        return pageResult;
    }

    public void disLike(Long userId) {
        User user = UserThreadLocal.get();
        this.userLikeApi.deleteUserLike(user.getId(), userId);

        //对方变成粉丝，解除好友关系（双向），以及解除环信中的好友关系

        if(!this.usersApi.isUsers(user.getId(), userId)){
            //非好友
            return;
        }

        //解除好友关系
        Users users = new Users();
        users.setUserId(user.getId());
        users.setFriendId(userId);
        this.usersApi.removeUsers(users);

        //解除环信好友关系
        this.imService.removeUsers(user.getId(), userId);
    }

    public void likeFan(Long userId) {
        User user = UserThreadLocal.get();
        this.userLikeApi.saveUserLike(user.getId(), userId);

        //相互喜欢，成为好友，注册到环信
        if(this.usersApi.isUsers(user.getId(), userId)){
            //已经是好友
            return;
        }

        //注册好友
        this.imService.contactUser(userId);
    }

    public SettingsVo querySettings() {
        User user = UserThreadLocal.get();

        // 查询配置
        Settings settings = this.settingsService.querySettings(user.getId());
        SettingsVo settingsVo = new SettingsVo();
        settingsVo.setId(user.getId());
        settingsVo.setPhone(user.getMobile());
        if (null != settings) {
            settingsVo.setGonggaoNotification(settings.getGonggaoNotification());
            settingsVo.setLikeNotification(settings.getLikeNotification());
            settingsVo.setPinglunNotification(settings.getPinglunNotification());
        }

        // 查询设置的问题
        Question question = this.questionService.queryQuestion(user.getId());
        if (null != question) {
            settingsVo.setStrangerQuestion(question.getTxt());
        }

        return settingsVo;
    }

    public void saveQuestions(String content) {
        User user = UserThreadLocal.get();
        this.questionService.save(user.getId(), content);
    }

    public PageResult queryBlacklist(Integer page, Integer pageSize) {
        User user = UserThreadLocal.get();

        IPage<BlackList> blackListIPage = this.blackListService.queryBlacklist(user.getId(), page, pageSize);

        PageResult pageResult = new PageResult();
        pageResult.setPage(page);
        pageResult.setPagesize(pageSize);
        pageResult.setCounts(new Long(blackListIPage.getTotal()).intValue());
        pageResult.setPages(new Long(blackListIPage.getPages()).intValue());

        List<BlackList> records = blackListIPage.getRecords();
        if(CollectionUtils.isEmpty(records)){
            return pageResult;
        }

        List<Long> userIds = new ArrayList<>();
        for (BlackList record : records) {
            userIds.add(record.getBlackUserId());
        }

        List<UserInfo> userInfoList = this.userInfoService.queryUserInfoListByUserIds(userIds);

        List<BlackListVo> blackListVos = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            BlackListVo blackListVo = new BlackListVo();
            blackListVo.setAge(userInfo.getAge());
            blackListVo.setAvatar(userInfo.getLogo());
            blackListVo.setGender(userInfo.getSex().name().toLowerCase());
            blackListVo.setId(userInfo.getUserId());
            blackListVo.setNickname(userInfo.getNickName());

            blackListVos.add(blackListVo);
        }

        pageResult.setItems(blackListVos);

        return pageResult;
    }

    public void delBlacklist(Long userId) {
        User user = UserThreadLocal.get();
        this.blackListService.delBlacklist(user.getId(), userId);
    }

    public void updateNotification(Boolean likeNotification, Boolean pinglunNotification, Boolean gonggaoNotification) {
        User user = UserThreadLocal.get();
        this.settingsService.updateNotification(user.getId(), likeNotification, pinglunNotification, gonggaoNotification);
    }
}
