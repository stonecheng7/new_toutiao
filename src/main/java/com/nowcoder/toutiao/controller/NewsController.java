package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.ToutiaoUtil.ToutiaoUtil;
import com.nowcoder.toutiao.model.HostHolder;
import com.nowcoder.toutiao.model.News;
import com.nowcoder.toutiao.service.NewsService;
//import com.sun.deploy.net.HttpResponse;
import com.nowcoder.toutiao.service.QiniuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

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
    //上传图片
    @RequestMapping(path = {"/uploadImage/"}, method = {RequestMethod.POST})
   // @ResponseBody
    public String uploadImage(@RequestParam("file") MultipartFile file){
        try{
            //根据图片的文件的URL，确定的是上传图片
            String fileUrl = qiniuService.saveImage(file);
            if(fileUrl==null){
                //return ToutiaoUtil.getJSONString(1,"上传图片失败");
                return "redirect:/upload";
            }
           // return ToutiaoUtil.getJSONString(0,fileUrl);
            return "addNews";
        }catch (Exception e){
            logger.error("上传图片失败",e.getMessage());
            //return ToutiaoUtil.getJSONString(1,"上传图片失败");
            return "redirect:/upload";
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
    @RequestMapping(path = {"/addNews/"}, method = {RequestMethod.POST})
   // @ResponseBody
    public String addNews(@RequestParam("image")  MultipartFile image,
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

            String fileUrl = qiniuService.saveImage(image);
            //校验成功后，需要设置发布页面需要的内容
            news.setImage(fileUrl);
            news.setCreatedDate(new Date());
            news.setTitle(title);
            news.setLink(link);

            //将news这个对象插入到页面中，发布成功
            newsService.addNews(news);
            //return ToutiaoUtil.getJSONString(0);
            return "redirect:/";
        }catch (Exception e){
            logger.error("添加咨询错误"+ e.getMessage());
            //return ToutiaoUtil.getJSONString(1,"发布新闻信息失败");
            return "addNews";
        }
    }

//    @RequestMapping(value = "/upload",method = RequestMethod.GET)
//    public String login(){
//
//        return "upload";
//    }
    @RequestMapping(value = "/uploadImage",method = RequestMethod.GET)
    public String uploadImage(){

        return "addNews";
    }
//    @RequestMapping(value = "/user/addNews/",method = {RequestMethod.GET})
//    public String addNews(){
//        return "addNews";
//    }
}
