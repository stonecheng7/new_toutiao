package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.ToutiaoUtil.ToutiaoUtil;
import com.nowcoder.toutiao.model.*;
import com.nowcoder.toutiao.service.*;
//import com.sun.deploy.net.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: new_toutiao
 * @description: 咨询的controller
 * @author: Cheng Qun
 * @create: 2019-04-24 19:16
 */
@Controller
public class NewsController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    NewsService newsService;
    @Autowired
    QiniuService qiniuService;
    //用于判断当前用户有木有登录账户
    @Autowired
    HostHolder hostHolder;
    @Autowired
    UserService userService;
    @Autowired
    CommentService commentService;
    @Autowired
    LikeService likeService;
    //上传图片
    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
    @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){
        try{
            //根据图片的文件的URL，确定的是上传图片
            String fileUrl = qiniuService.saveImage(file);
            if(fileUrl==null){
                return ToutiaoUtil.getJSONString(1,"上传图片失败");
                //return "redirect:/upload";
            }
            return ToutiaoUtil.getJSONString(0,fileUrl);
           // return "addNews";
        }catch (Exception e){
            logger.error("上传图片失败",e.getMessage());
            return ToutiaoUtil.getJSONString(1,"上传图片失败");
            // return "redirect:/upload";
        }
    }
    //展示图片，返回给前端显示
    @RequestMapping(path = {"/image/"}, method = {RequestMethod.GET})
    @ResponseBody
    public void getImage(@RequestParam("name") String imageName,
                         HttpServletResponse response){
        try {
            response.setContentType("image/jpeg");
            StreamUtils.copy(new FileInputStream(new
                    File(ToutiaoUtil.IMAGE_DIR+imageName)),response.getOutputStream());
        }catch (Exception e){
            logger.error("读取图片失败",e.getMessage());
        }

    }

    //增加新闻信息
    @RequestMapping(path = {"/user/addNews/"}, method = {RequestMethod.POST})
   @ResponseBody
    public String addNews(@RequestParam("image") String image,
                          @RequestParam("title") String title,
                          @RequestParam("link") String link){
        try{
            News news = new News();
            //首先判断用户是否已经登陆，和之前跳转页面之前需要校验身份一样
            if(hostHolder.getUser()!=null){
                news.setUserId(hostHolder.getUser().getId());
            }else{
                //如果校验发现用户没有登录，那么设置为匿名用户 ID = 3；
                news.setUserId(3);
            }

            //String fileUrl = qiniuService.saveImage(image);
            //校验成功后，需要设置发布页面需要的内容
            news.setImage(image);
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);

            //将news这个对象插入到页面中，发布成功
            newsService.addNews(news);
            return ToutiaoUtil.getJSONString(0);
           // return "redirect:/";
        }catch (Exception e){
            logger.error("添加咨询错误"+ e.getMessage());
            return ToutiaoUtil.getJSONString(1,"发布新闻信息失败");
            //return "addNews";
        }
    }

//    @RequestMapping(value = "/upload",method = RequestMethod.GET)
//    public String login(){
//
//        return "upload";
//    }
//    @RequestMapping(value = "/uploadImage",method = RequestMethod.GET)
//    public String uploadImage(){
//
//        return "addNews";
//    }
//    @RequestMapping(value = "/user/addNews/",method = {RequestMethod.GET})
//    public String addNews(){
//        return "addNews";
//    }

    //资讯的详情页
    @RequestMapping(path = {"/news/{newsId}"}, method = {RequestMethod.GET})
    public String newDetail(@PathVariable("newsId") int newId, Model model){
       //首先获得新闻资讯
        News news = newsService.getById(newId);
        if(news!=null){
            int localUserId = hostHolder.getUser() != null ? hostHolder.getUser().getId() : 0;
            if (localUserId != 0) {
                model.addAttribute("like", likeService.getLikeStatus(localUserId, EntityType.ENTITY_NEWS, news.getId()));
            } else {
                model.addAttribute("like", 0);
            }
            //新闻资讯不为空，那么就可以评论
            List<Comment> comments = commentService.getCommentsByEntity(news.getId(),EntityType.ENTITY_NEWS);
            //专门用来显示在页面上显示的
            List<ViewObject> commentVOs = new ArrayList<ViewObject>();
            for(Comment comment : comments){
                ViewObject vo = new ViewObject();
                vo.set("comment",comment);
                vo.set("user",userService.getUser(comment.getUserId()));
                commentVOs.add(vo);
            }
            //这个语句的作用就是让前端页面显示，传入的数据名字就是comments，内容就是commentVOs的内容；同理如下
            model.addAttribute("comments",commentVOs);
        }
        //页面展示内容
        model.addAttribute("news",news);
        model.addAttribute("owner",userService.getUser(news.getUserId()));
        return "detail";
    }

    //在资讯的详情页中==添加评论
    @RequestMapping(path = {"/addComment"}, method = {RequestMethod.POST})
    public String addComment(@RequestParam("newsId") int newsId,
                             @RequestParam("content") String content){
        try{
            Comment comment = new Comment();
            comment.setUserId(hostHolder.getUser().getId());
            comment.setContent(content);
            comment.setEntityId(newsId);
            comment.setEntityType(EntityType.ENTITY_NEWS);
            comment.setCreatedDate(new Date());
//       ====================同步处理添加评论===============================
            //添加评论内容
            commentService.addComment(comment);
            //首先得到评论的个数，然后把个数更新到news信息流里面评论的数量显示
            int count= commentService.getCommentCount(comment.getEntityId(),comment.getEntityType());
            newsService.updateCommentCount(comment.getEntityId(),count);
            //评论部分可以模仿牛客，分页显示；目前显示是全部
//       ====================异步处理添加评论===============================

        }catch (Exception e){
            logger.error("增加评论失败"+e.getMessage());
        }
        //返回到资讯详情页
        return "redirect:/news/"+String.valueOf(newsId);
    }


}
