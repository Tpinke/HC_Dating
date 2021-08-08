package com.tanhua.server.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tanhua.common.pojo.Question;
import com.tanhua.server.mapper.QuestionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionService {

    @Autowired
    private QuestionMapper questionMapper;


    public Question queryQuestion(Long userId) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("user_id", userId);
        return this.questionMapper.selectOne(queryWrapper);
    }

    public void save(Long userId, String content) {
        Question question = this.queryQuestion(userId);
        if(null != question){
            question.setTxt(content);
            this.questionMapper.updateById(question);
        }else {
            question = new Question();
            question.setUserId(userId);
            question.setTxt(content);
            this.questionMapper.insert(question);
        }
    }
}
