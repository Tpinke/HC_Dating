package com.tanhua.data;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.extra.tokenizer.Result;
import cn.hutool.extra.tokenizer.TokenizerEngine;
import cn.hutool.extra.tokenizer.TokenizerUtil;
import cn.hutool.extra.tokenizer.Word;
import com.alibaba.dubbo.config.annotation.Reference;
import com.tanhua.dubbo.server.api.*;
import com.tanhua.dubbo.server.pojo.*;
import com.tanhua.dubbo.server.vo.PageInfo;
import com.tanhua.recommend.pojo.RecommendQuanZi;
import com.tanhua.recommend.pojo.RecommendVideo;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestData {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Reference(version = "1.0.0")
    private QuanZiApi quanZiApi;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Reference(version = "1.0.0")
    private VideoApi videoApi;

    @Reference(version = "1.0.0")
    private UserLocationApi userLocationApi;

    @Reference(version = "2.0.0")
    private UserLocationApi userLocationApi2;

    @Reference(version = "1.0.0")
    private UserLikeApi userLikeApi;

    @Reference(version = "1.0.0")
    private VisitorsApi visitorsApi;
    /**
     * 构造好友数据，为1~99用户构造10个好友
     */
    @Test
    public void testUsers() {
        for (int i = 1; i <= 99; i++) {
            for (int j = 0; j < 10; j++) {
                Users users = new Users();
                users.setId(ObjectId.get());
                users.setDate(System.currentTimeMillis());
                users.setUserId(Long.valueOf(i));
                users.setFriendId(this.getFriendId(users.getUserId()));
                System.out.println(users);
                this.mongoTemplate.save(users);
            }
        }
    }

    private Long getFriendId(Long userId) {
        Long friendId = RandomUtil.randomLong(1, 100);
        if (friendId.intValue() == userId.intValue()) {
            getFriendId(userId);
        }
        return friendId;
    }


    /**
     * 每个用户随机发10~50条动态
     */
    @Test
    public void testPublish() {
        String[] pics = "https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/6/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/6/CL-3.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567248169.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567252410.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567255147.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567257572.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567265870.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567312886.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/4/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/4/2.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/4/3.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/7/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/7/1564567349498.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/7/1564567352977.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/7/1564567360406.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/11/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/11/1564567736806.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/11/1564567738955.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/11/CL-5.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/10/1564567528297.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/10/1564567533426.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/8/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/8/1564567422505.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/8/CL-4.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/1/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/1/2.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/1/3.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/1/4.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/12/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/12/1564567826947.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/12/1564567829641.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/13/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/13/1564567915766.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/13/1564567918358.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/14/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/15/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/16/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/16/1564568028572.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/16/CL-6.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/1564568188471.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/1564568194253.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/1564568200243.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/CL-7.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/18/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/18/1564568257399.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/18/1564568260441.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/19/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/19/1564568357412.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/21/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/21/1564569004349.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/1564569521051.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/1564569523892.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/1564569527845.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/CL-8.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/23/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/23/1564569713194.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/23/CL-9.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1564569748776.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1564569752979.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1564569755593.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1564569759322.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/25/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/25/1564569813123.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/25/CL-10.jpg".split(",");
        String[] titles = "所有失去的，都会以另一种方式归来。|记住：现在是你生命中最好的年纪。|别人都羡慕你自在如风，只有你知道自己无依无靠。|最悲伤却又是最痛苦的谎言，就是我还好，没有关系。|习惯了任何人的忽冷忽热，看淡任何人的渐行渐远。|任何你的不足，在你成功的那刻，都会被人说为特色。所以，坚持做你自己。|光阴似箭，时光荏苒，一转眼一年就过去了。|这世上没有人是不可或缺的，没有什么是不可替代的。|因为时光，我们懂得了衰老;因为回忆，我们知道了幼稚。|时间从来不语，却回答了所有问题。|我们不必要委屈自己去迎合别人，不开心就说，大不了就少认识一个人而已。|人多时，管住嘴。人少时，管住心。|人生不如意事十之八九，真正有格局的人，既能享受最好的，也能承受最坏的。|圈子不同，不必强融。|一个人的情绪会破坏两个人的心情，善待自己，善待别人。|人生就是见自己，见天地，见众生的过程。|时间几乎会愈合所有伤口，如果你的伤口还没有愈合，请给时间一点时间。|人生的路，虽然难走，但是没有绝境，只要寻找，总有路可走。|总不能用一身的刺去拥抱无辜的人。认识就够了，何必谈余生。|别说什么木克水，水克土，土克火，火克金，只要你穷，什么都会克你。|人就这么一辈子，欲望，就像手中的沙子，握得紧，失去得越多。|一切的矫情和埋怨，都源于两点：要么缺钱，要么缺爱。".split("\\|");
        for (int i = 1; i <= 99; i++) {
            for (int j = 0; j < RandomUtil.randomInt(10, 51); j++) {
                Publish publish = new Publish();
                publish.setText(titles[RandomUtil.randomInt(0, titles.length)]);
                publish.setMedias(new ArrayList<>());
                for (int k = 0; k < RandomUtil.randomInt(1, 10); k++) {
                    publish.getMedias().add(pics[k]);
                }

                publish.setUserId(Long.valueOf(i));
                publish.setLocationName("中国北京市昌平区建材城西路16号");
                publish.setLatitude("40.066355");
                publish.setLongitude("116.350426");
                publish.setSeeType(1);

                System.out.println(publish);
                this.quanZiApi.savePublish(publish);
            }
        }
    }

    /**
     * 每个动态随机5~10个用户对其点赞
     */
    @Test
    public void testLikeComment() {
        List<Publish> publishList = this.mongoTemplate.findAll(Publish.class);
        Set<String> publishIdList = new HashSet<>();

        for (Publish publish : publishList) {
            for (int i = 0; i < RandomUtil.randomInt(5, 11); i++) {
                Long userId = RandomUtil.randomLong(1, 100);

                System.out.println(publish);

                String publishId = publish.getId().toHexString();
                boolean b = this.quanZiApi.saveLikeComment(Long.valueOf(userId), publishId);
                if (b) {
                    publishIdList.add(publishId);
                    //记录已点赞
                    String userKey = "QUANZI_COMMENT_LIKE_USER_" + userId + "_" + publishId;
                    this.redisTemplate.opsForValue().set(userKey, "1");
                }
            }
        }


        for (String publishId : publishIdList) {
            Long count = this.quanZiApi.queryCommentCount(publishId, 1);
            String key = "QUANZI_COMMENT_LIKE_" + publishId;
            this.redisTemplate.opsForValue().set(key, String.valueOf(count));
        }

    }

    /**
     * 每个动态随机5~10个用户对其喜欢
     */
    @Test
    public void testLoveComment() {
        List<Publish> publishList = this.mongoTemplate.findAll(Publish.class);
        Set<String> publishIdList = new HashSet<>();

        for (Publish publish : publishList) {
            for (int i = 0; i < RandomUtil.randomInt(5, 11); i++) {
                Long userId = RandomUtil.randomLong(1, 100);

                System.out.println(publish);

                String publishId = publish.getId().toHexString();
                boolean b = this.quanZiApi.saveLoveComment(Long.valueOf(userId), publishId);
                if (b) {
                    publishIdList.add(publishId);
                    //记录已喜欢
                    String userKey = "QUANZI_COMMENT_LOVE_USER_" + userId + "_" + publishId;
                    this.redisTemplate.opsForValue().set(userKey, "1");
                }
            }
        }


        for (String publishId : publishIdList) {
                Long count = this.quanZiApi.queryCommentCount(publishId, 3);
                String key = "QUANZI_COMMENT_LOVE_" + publishId;
                this.redisTemplate.opsForValue().set(key, String.valueOf(count));
        }

    }

    /**
     * 每个动态随机5~10个用户对其评论
     */
    @Test
    public void testComment() {
        List<Publish> publishList = this.mongoTemplate.findAll(Publish.class);

        TokenizerEngine engine = TokenizerUtil.createEngine();
        String s = "有什么人能用绿竹作弓矢，射入云空，永不落下？我之想象，犹如长箭，向云空射去，去即不返。长箭所注，在碧蓝而明静之广大虚空。明智者若善用其明智，即可从此云空中，读示一小文，文中有微叹与沉默，色与香，爱和怨。无著者姓名。无年月。无故事。无……然而内容极柔美。虚空静寂，读者灵魂中如有音乐。虚空明蓝，读者灵魂上却光明净洁。大门前石板路有一个斜坡，坡上有绿树成行，长干弱枝，翠叶积叠，如翠等，如羽葆，如旗帜。常有山灵，秀腰白齿，往来其间。遇之者即喑哑。爱能使人喑哑——一种语言歌呼之死亡。“爱与死为邻”。然抽象的爱，亦可使人超生。爱国也需要生命，生命力充溢者方能爱国。至如阉寺性的人，实无所爱，对国家，貌作热诚，对事，马马虎虎，对人，毫无情感，对理想，异常吓怕。也娶妻生子，治学问教书，做官开会，然而精神状态上始终是个阉人。与阉人说此，当然无从了解。夜梦极可怪。见一淡绿百合花，颈弱而花柔，花身略有斑点青渍，倚立门边微微动摇。在不可知的地方好像有极熟悉的声音在招呼：“你看看好，应当有一粒星子在花中。仔细看看。”于是伸手触之。花微抖，如有所怯。亦复微笑，如有所恃。因轻轻摇触那个花柄，花蒂，花瓣。近花处几片叶子全落了。如闻叹息，低而分明。雷雨刚过。醒来后闻远处有狗吠，吠声如豹。半迷糊中卧床上默想，觉得惆怅之至。因百合花在门边动摇，被触时微抖或微笑，事实上均不可能！起身时因将经过记下，用半浮雕手法，如玉工处理一片玉石，琢刻割磨。完成时犹如一壁炉上小装饰。精美如瓷器，素朴如竹器。一般人喜用教育身份来测量一个人道德程度。尤其是有关乎性的道德。事实上这方面的事情，正复难言。有些人我们应当嘲笑的，社会却常常给以尊敬，如阉寺。有些人我们应当赞美的，社会却认为罪恶，如诚实。多数人所表现的观念，照例是与真理相反的。多数人都乐于在一种虚伪中保持安全或自足心境。因此我焚了那个稿件。我并不畏惧社会，我厌恶社会，厌恶伪君子，不想将这个完美诗篇，被伪君子眼目所污渎。百合花极静。在意象中尤静。山谷中应当有白中微带浅蓝色的百合花，弱颈长蒂，无语如语，香清而淡，躯干秀拔。花粉作黄色，小叶如翠珰。";
        Result result = engine.parse(s);
        List<String> words = new ArrayList<>();
        for (Word word : result) {
            words.add(word.getText());
        }

        for (Publish publish : publishList) {
            for (int i = 0; i < RandomUtil.randomInt(5, 11); i++) {
                Long userId = RandomUtil.randomLong(1, 100);

                System.out.println(publish);

                String publishId = publish.getId().toHexString();

                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < RandomUtil.randomInt(10, 50); j++) {
                    sb.append(words.get(RandomUtil.randomInt(0, words.size())));
                }

                this.quanZiApi.saveComment(Long.valueOf(userId), publishId, 2, sb.toString());
            }
        }
    }

    /**
     * 圈子推荐
     */
    @Test
    public void testQuanZiRecommend() {

        List<Publish> publishList = this.mongoTemplate.findAll(Publish.class);

        for (Publish publish : publishList) {
            Query query = Query.query(Criteria.where("publishId").is(publish.getId()));
            List<Comment> commentList = this.mongoTemplate.find(query, Comment.class);
            for (Comment comment : commentList) {
                //1-发动态，2-浏览动态， 3-点赞， 4-喜欢， 5-评论，6-取消点赞，7-取消喜欢
                RecommendQuanZi recommendQuanZi = new RecommendQuanZi();
                recommendQuanZi.setUserId(comment.getUserId());
                recommendQuanZi.setId(ObjectId.get());
                recommendQuanZi.setDate(System.currentTimeMillis());
                recommendQuanZi.setPublishId(publish.getPid());

                switch (comment.getCommentType()) {
                    case 1: {
                        int score = 0;
                        int count = StringUtils.length(publish.getText());
                        if (count >= 0 && count <= 50) {
                            score += 1;
                        } else if (count <= 100) {
                            score += 2;
                        } else {
                            score += 3;
                        }
                        if (!CollectionUtils.isEmpty(publish.getMedias())) {
                            score += publish.getMedias().size();
                        }
                        recommendQuanZi.setScore(Double.valueOf(score));
                        break;
                    }
                    case 3: {
                        recommendQuanZi.setScore(5d);
                        break;
                    }

                    case 2: {
                        recommendQuanZi.setScore(10d);
                        break;
                    }
                    default: {
                        recommendQuanZi.setScore(0d);
                        break;
                    }
                }

                System.out.println(recommendQuanZi.getScore() + "  " + publish);

                this.mongoTemplate.save(recommendQuanZi, "recommend_quanzi");
            }

        }

    }

    /**
     * 每个用户发布1~5个小视频
     */
    @Test
    public void testVideo() {
        String[] pics = "https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/4/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/10/1564567528297.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/1/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/10/1564567533426.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/2/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567248169.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/8/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/11/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/6/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/8/1564567422505.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567252410.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/8/CL-4.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/6/CL-3.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567255147.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/1/2.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567257572.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/1/3.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/1/4.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/12/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/2/3.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/2/4.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/4/2.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/11/1564567736806.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567265870.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/12/1564567826947.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/7/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/2/CL-1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/7/1564567349498.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/7/1564567352977.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/4/3.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/13/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/5/1564567312886.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/23/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/23/1564569713194.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/18/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/1564569521051.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/23/CL-9.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/11/1564567738955.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/7/1564567360406.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/11/CL-5.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/25/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/13/1564567915766.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/14/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1564569748776.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/18/1564568257399.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/15/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/18/1564568260441.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/12/1564567829641.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/16/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/1564569523892.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/16/1564568028572.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/16/CL-6.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/1564568188471.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/1564569527845.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/19/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/22/CL-8.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/19/1564568357412.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/1564568194253.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1564569752979.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/21/1.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1564569755593.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/24/1564569759322.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/1564568200243.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/13/1564567918358.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/25/1564569813123.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/21/1564569004349.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/17/CL-7.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/photo/25/CL-10.jpg,https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/logo/1.jpg".split(",");
        for (int i = 1; i <= 99; i++) {
            for (int j = 0; j < RandomUtil.randomInt(1, 6); j++) {
                Video video = new Video();

                video.setUserId(Long.valueOf(i));
                video.setLocationName("中国北京市昌平区建材城西路16号");
                video.setLatitude("40.066355");
                video.setLongitude("116.350426");
                video.setSeeType(1);
                video.setVideoUrl("https://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/video/1576134125940400.mp4");
                video.setPicUrl(pics[RandomUtil.randomInt(0, pics.length)]);

                System.out.println(video);
                this.videoApi.saveVideo(video);
            }
        }
    }

    /**
     * 每个小视频随机5~10个用户对其点赞
     */
    @Test
    public void testVideoLikeComment() {
        List<Video> videoList = this.mongoTemplate.findAll(Video.class);
        Set<String> publishIdList = new HashSet<>();

        for (Video video : videoList) {
            for (int i = 0; i < RandomUtil.randomInt(5, 11); i++) {
                Long userId = RandomUtil.randomLong(1, 100);

                System.out.println(video);

                String publishId = video.getId().toHexString();
                boolean b = this.quanZiApi.saveLikeComment(Long.valueOf(userId), publishId);
                if (b) {
                    publishIdList.add(publishId);
                    //记录已点赞
                    String userKey = "QUANZI_COMMENT_LIKE_USER_" + userId + "_" + publishId;
                    this.redisTemplate.opsForValue().set(userKey, "1");
                }
            }
        }


        for (String publishId : publishIdList) {
            Long count = this.quanZiApi.queryCommentCount(publishId, 1);
            String key = "QUANZI_COMMENT_LIKE_" + publishId;
            this.redisTemplate.opsForValue().set(key, String.valueOf(count));
        }

    }

    /**
     * 每个小视频随机5~10个用户对其评论
     */
    @Test
    public void testVideoComment() {
        List<Video> videoList = this.mongoTemplate.findAll(Video.class);

        TokenizerEngine engine = TokenizerUtil.createEngine();
        String s = "有什么人能用绿竹作弓矢，射入云空，永不落下？我之想象，犹如长箭，向云空射去，去即不返。长箭所注，在碧蓝而明静之广大虚空。明智者若善用其明智，即可从此云空中，读示一小文，文中有微叹与沉默，色与香，爱和怨。无著者姓名。无年月。无故事。无……然而内容极柔美。虚空静寂，读者灵魂中如有音乐。虚空明蓝，读者灵魂上却光明净洁。大门前石板路有一个斜坡，坡上有绿树成行，长干弱枝，翠叶积叠，如翠等，如羽葆，如旗帜。常有山灵，秀腰白齿，往来其间。遇之者即喑哑。爱能使人喑哑——一种语言歌呼之死亡。“爱与死为邻”。然抽象的爱，亦可使人超生。爱国也需要生命，生命力充溢者方能爱国。至如阉寺性的人，实无所爱，对国家，貌作热诚，对事，马马虎虎，对人，毫无情感，对理想，异常吓怕。也娶妻生子，治学问教书，做官开会，然而精神状态上始终是个阉人。与阉人说此，当然无从了解。夜梦极可怪。见一淡绿百合花，颈弱而花柔，花身略有斑点青渍，倚立门边微微动摇。在不可知的地方好像有极熟悉的声音在招呼：“你看看好，应当有一粒星子在花中。仔细看看。”于是伸手触之。花微抖，如有所怯。亦复微笑，如有所恃。因轻轻摇触那个花柄，花蒂，花瓣。近花处几片叶子全落了。如闻叹息，低而分明。雷雨刚过。醒来后闻远处有狗吠，吠声如豹。半迷糊中卧床上默想，觉得惆怅之至。因百合花在门边动摇，被触时微抖或微笑，事实上均不可能！起身时因将经过记下，用半浮雕手法，如玉工处理一片玉石，琢刻割磨。完成时犹如一壁炉上小装饰。精美如瓷器，素朴如竹器。一般人喜用教育身份来测量一个人道德程度。尤其是有关乎性的道德。事实上这方面的事情，正复难言。有些人我们应当嘲笑的，社会却常常给以尊敬，如阉寺。有些人我们应当赞美的，社会却认为罪恶，如诚实。多数人所表现的观念，照例是与真理相反的。多数人都乐于在一种虚伪中保持安全或自足心境。因此我焚了那个稿件。我并不畏惧社会，我厌恶社会，厌恶伪君子，不想将这个完美诗篇，被伪君子眼目所污渎。百合花极静。在意象中尤静。山谷中应当有白中微带浅蓝色的百合花，弱颈长蒂，无语如语，香清而淡，躯干秀拔。花粉作黄色，小叶如翠珰。";
        Result result = engine.parse(s);
        List<String> words = new ArrayList<>();
        for (Word word : result) {
            words.add(word.getText());
        }

        for (Video publish : videoList) {
            for (int i = 0; i < RandomUtil.randomInt(5, 11); i++) {
                Long userId = RandomUtil.randomLong(1, 100);

                System.out.println(publish);

                String publishId = publish.getId().toHexString();

                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < RandomUtil.randomInt(10, 50); j++) {
                    sb.append(words.get(RandomUtil.randomInt(0, words.size())));
                }

                this.quanZiApi.saveComment(Long.valueOf(userId), publishId, 2, sb.toString());
            }
        }
    }

    /**
     * 小视频推荐
     */
    @Test
    public void testVideoRecommend() {

        List<Video> videoList = this.mongoTemplate.findAll(Video.class);

        for (Video video : videoList) {
            Query query = Query.query(Criteria.where("publishId").is(video.getId()));

            List<Comment> commentList = this.mongoTemplate.find(query, Comment.class);
            for (Comment comment : commentList) {
                //1-发动态，2-点赞， 3-取消点赞，4-评论
                RecommendVideo recommendVideo = new RecommendVideo();
                recommendVideo.setUserId(comment.getUserId());
                recommendVideo.setId(ObjectId.get());
                recommendVideo.setDate(System.currentTimeMillis());
                recommendVideo.setVideoId(video.getVid());

                switch (comment.getCommentType()) {
                    case 1: {
                        recommendVideo.setScore(5d);
                        break;
                    }
                    case 2: {
                        recommendVideo.setScore(10d);
                        break;
                    }
                    default: {
                        recommendVideo.setScore(0d);
                        break;
                    }
                }

                System.out.println(recommendVideo);

                this.mongoTemplate.save(recommendVideo, "recommend_video");
            }
        }

    }

    /**
     * 更新每个人的地理位置
     */
    @Test
    public void testUserLocation() {
        for (int i = 1; i <= 99; i++) {
            Long userId = Long.valueOf(i);
            Double longitude = RandomUtil.randomDouble(116.350627, 121.493444);
            Double latitude = RandomUtil.randomDouble(31.240513, 40.066328);
            this.userLocationApi.updateUserLocation(userId, longitude, latitude, "中国北京市昌平区建材城西路16号");
        }
    }

    /**
     * 更新每个人的地理位置(ES)
     */
    @Test
    public void testUserLocation2() {
        for (int i = 1; i <= 99; i++) {
            Long userId = Long.valueOf(i);
            Double longitude = RandomUtil.randomDouble(116.350627, 121.493444);
            Double latitude = RandomUtil.randomDouble(31.240513, 40.066328);
            this.userLocationApi2.updateUserLocation(userId, longitude, latitude, "中国北京市昌平区建材城西路16号");
        }
    }

    /**
     * 每个用户随机10~30个喜欢
     */
    @Test
    public void testUserLike(){
        for (int i = 1; i <= 99; i++) {
            for (int j = 0; j < RandomUtil.randomInt(10, 31); j++) {
                Long userId = Long.valueOf(i);
                Long likeUserId = this.getFriendId(userId);
                System.out.println("userId: " + userId+", likeUserId: " + likeUserId);
                this.userLikeApi.saveUserLike(userId, likeUserId);
            }
        }
    }

    /**
     * 每个用户有10~30人来访
     */
    @Test
    public void testVisitors(){
        for (int i = 1; i <= 99; i++) {
            for (int j = 0; j < RandomUtil.randomInt(10, 31); j++) {
                Long userId = Long.valueOf(i);
                Long visitorUserId = this.getFriendId(userId);
                System.out.println("userId: " + userId+", visitorUserId: " + visitorUserId);

                Visitors visitors = new Visitors();
                visitors.setVisitorUserId(visitorUserId);
                visitors.setUserId(userId);
                visitors.setFrom("首页");

                this.visitorsApi.saveVisitor(visitors);
            }
        }
    }

}
