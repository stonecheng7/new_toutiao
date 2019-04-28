package com.nowcoder.toutiao.service;

import com.nowcoder.toutiao.ToutiaoUtil.ToutiaoUtil;
import com.nowcoder.toutiao.dao.NewsDAO;
import com.nowcoder.toutiao.model.News;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Random;
import java.util.UUID;

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
    //得到信息流放入集合中
    public List<News> getLatestNews(int userId,int offset,int limit){
        return newsDAO.selectByUserIdAndOffset(userId,offset,limit);
    }
    //增加新闻流
    public int addNews(News news) {
        newsDAO.addNews(news);
        return news.getId();
    }
    //通过ID得到信息
    public News getById(int newsId) {
        return newsDAO.getById(newsId);
    }
    //上传图片以及校验
    public String saveImage(MultipartFile file) throws IOException {
        //首先需要校验文件上传的是否为图片；
        // 方法为：校验文件后缀名
        int doPos = file.getOriginalFilename().lastIndexOf(".");
        //如果dopos小于0，意思就是文件名不符合条件
        if(doPos<0){
            return null;
        }
        //如果文件名字符合条件，那么就取文件格式的后缀，全部转化为小写的格式
        String fileExt = file.getOriginalFilename().substring(doPos+1).toLowerCase();
        //使用工具类中的判断格式是否符合图片格式
        if(!ToutiaoUtil.isFileAllowed(fileExt)){
            return null;
        }
        //判断正确的图片，系统都会分配一个随机的文件名称
        String fileName = UUID.randomUUID().toString().replaceAll("-","")+"."+fileExt;
        //将文件的二进制的流写入到指定的文件内，目录中。如果图片是存在的就替换掉
        Files.copy(file.getInputStream(),new File(ToutiaoUtil.IMAGE_DIR+fileName).toPath(),
                StandardCopyOption.REPLACE_EXISTING);
        //生成图片的地址发送给用户
        return ToutiaoUtil.TOUTIAO_DOMAIN+"image?name="+fileName;
    }
    //更新信息页面的评论个数
    public int updateCommentCount(int id, int count) {
        return newsDAO.updateCommentCount(id, count);
    }
    //点赞数喜欢的额个数
    public int updateLikeCount(int id, int count) {
        return newsDAO.updateLikeCount(id, count);
    }
}
