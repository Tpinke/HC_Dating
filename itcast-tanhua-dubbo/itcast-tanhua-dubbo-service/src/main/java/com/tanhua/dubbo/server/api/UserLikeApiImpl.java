package com.tanhua.dubbo.server.api;

import com.alibaba.dubbo.config.annotation.Service;
import com.mongodb.client.result.DeleteResult;
import com.tanhua.dubbo.server.pojo.UserLike;
import com.tanhua.dubbo.server.vo.PageInfo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Service(version = "1.0.0")
public class UserLikeApiImpl implements UserLikeApi {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String saveUserLike(Long userId, Long likeUserId) {
        Query query = Query.query(Criteria
                .where("userId")
                .is(userId)
                .and("likeUserId").is(likeUserId));

        if (this.mongoTemplate.count(query, UserLike.class) > 0) {
            return null;
        }

        UserLike userLike = new UserLike();
        userLike.setId(ObjectId.get());
        userLike.setCreated(System.currentTimeMillis());
        userLike.setUserId(userId);
        userLike.setLikeUserId(likeUserId);

        this.mongoTemplate.save(userLike);
        return userLike.getId().toHexString();
    }

    @Override
    public Boolean isMutualLike(Long userId, Long likeUserId) {
        Criteria criteria1 = Criteria.where("userId").is(userId).and("likeUserId").is(likeUserId);
        Criteria criteria2 = Criteria.where("userId").is(likeUserId).and("likeUserId").is(userId);
        Criteria criteria = new Criteria().orOperator(criteria1, criteria2);
        return this.mongoTemplate.count(Query.query(criteria), UserLike.class) == 2;
    }

    @Override
    public Boolean deleteUserLike(Long userId, Long likeUserId) {
        Query query = Query.query(Criteria
                .where("userId")
                .is(userId)
                .and("likeUserId").is(likeUserId));
        DeleteResult deleteResult = this.mongoTemplate.remove(query, UserLike.class);
        return deleteResult.getDeletedCount() == 1;
    }

    @Override
    public Long queryEachLikeCount(Long userId) {
        // 我喜欢的列表
        List<UserLike> userLikeList = this.mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);

        // 获取到所有我喜欢的列表的用户id
        List<Long> likeUserIdList = new ArrayList<>();
        for (UserLike userLike : userLikeList) {
            likeUserIdList.add(userLike.getLikeUserId());
        }

        Query query = Query.query(Criteria.where("userId").in(likeUserIdList).and("likeUserId").is(userId));
        return this.mongoTemplate.count(query, UserLike.class);
    }

    @Override
    public Long queryLikeCount(Long userId) {
        return this.mongoTemplate.count(Query.query(Criteria.where("userId").is(userId)), UserLike.class);
    }

    @Override
    public Long queryFanCount(Long userId) {
        return this.mongoTemplate.count(Query.query(Criteria.where("likeUserId").is(userId)), UserLike.class);
    }

    @Override
    public PageInfo<UserLike> queryEachLikeList(Long userId, Integer page, Integer pageSize) {
        // 我喜欢的列表
        List<UserLike> userLikeList = this.mongoTemplate.find(Query.query(Criteria.where("userId").is(userId)), UserLike.class);

        // 获取到所有我喜欢的列表的用户id
        List<Long> likeUserIdList = new ArrayList<>();
        for (UserLike userLike : userLikeList) {
            likeUserIdList.add(userLike.getLikeUserId());
        }

        Query query = Query.query(Criteria.where("userId").in(likeUserIdList).and("likeUserId").is(userId));
        return this.queryList(query, page, pageSize);

    }

    @Override
    public PageInfo<UserLike> queryLikeList(Long userId, Integer page, Integer pageSize) {
        Query query = Query.query(Criteria.where("userId").is(userId));
        return this.queryList(query, page, pageSize);
    }

    @Override
    public PageInfo<UserLike> queryFanList(Long userId, Integer page, Integer pageSize) {
        return this.queryList(Query.query(Criteria.where("likeUserId").is(userId)), page, pageSize);
    }

    private PageInfo<UserLike> queryList(Query query, Integer page, Integer pageSize) {
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("created")));
        query.with(pageRequest);
        List<UserLike> userLikeList = this.mongoTemplate.find(query, UserLike.class);

        PageInfo<UserLike> pageInfo = new PageInfo<>();
        pageInfo.setRecords(userLikeList);
        pageInfo.setTotal(0);
        pageInfo.setPageSize(pageSize);
        pageInfo.setPageNum(page);
        return pageInfo;
    }
}
