package com.nowcoder.toutiao.service;

import com.nowcoder.toutiao.dao.NewsDAO;
import com.nowcoder.toutiao.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: toutiao
 * @description: newsservice
 * @author: Cheng Qun
 * @create: 2019-04-22 14:02
 */
@Service
public class NewsService {
    @Autowired
    private NewsDAO newsDAO;
    public List<News> getLatestNews(int userId,int offset,int limit){
        return newsDAO.selectByUserIdAndOffset(userId,offset,limit);
    }
}
