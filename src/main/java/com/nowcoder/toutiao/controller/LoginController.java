package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.ToutiaoUtil.ToutiaoUtil;
import com.nowcoder.toutiao.service.UserService;
//import com.sun.media.jfxmedia.logging.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.nowcoder.toutiao.ToutiaoUtil.ToutiaoUtil.logger;

/**
 * @program: new_toutiao
 * @description: 登录的controller
 * @author: Cheng Qun
 * @create: 2019-04-23 13:56
 */
@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    UserService userService;


    //==========================注册过程===============================================
    @RequestMapping(path = {"/reg/"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String reg(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember",defaultValue ="0" ) int rememberme,
                      HttpServletResponse response
                      ){
       try{
           Map<String ,Object> map = userService.register(username,password);
           if(map.containsKey("ticket")){
               //注册成功后需要生成cookie记录，把ticket放在cookie中，然后返回给发送端request端
               Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
               cookie.setPath("/");

               if(rememberme>0){
                   //如果remember大于0，就将cookie的有效时间设置为5天，默认的是浏览器关闭后cookie就没有了
                   cookie.setMaxAge(3600*24*5);
               }
               response.addCookie(cookie);
              return ToutiaoUtil.getJSONString(0,"注册成功");
               //return "redirect:/";
           }else {

              // model.addAttribute("error","注册失败，请重新注册");
               // return "redirect:/register";
               return ToutiaoUtil.getJSONString(1,map);
           }

       }catch (Exception e){
           logger.error("注册异常" + e.getMessage());
           //model.addAttribute("error","注册异常");
           return ToutiaoUtil.getJSONString(1,"注册异常");
           //return "redirect:/register";
       }
    }
    //==========================登录过程===============================================
    @RequestMapping(path = {"/login/"},method = {RequestMethod.GET,RequestMethod.POST})
    @ResponseBody
    public String login(Model model, @RequestParam("username") String username,
                      @RequestParam("password") String password,
                      @RequestParam(value = "rember",defaultValue ="0" ) int rememberme,
                        HttpServletResponse response
    ){
        try{
            Map<String ,Object> map = userService.login(username,password);
            if(map.containsKey("ticket")){
                Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
                cookie.setPath("/");
                if(rememberme>0){
                    //如果remember大于0，就将cookie的有效时间设置为5天，默认的是浏览器关闭后cookie就没有了
                    cookie.setMaxAge(3600*24*5);
                }

                response.addCookie(cookie);
                // return "redirect:/";
               return ToutiaoUtil.getJSONString(0,"注册成功..");
            }else {
                return ToutiaoUtil.getJSONString(1,map);
                //model.addAttribute("error","登录失败,请重新登录!");
                //return "redirect:/login";
            }

        }catch (Exception e){
            logger.error("登录异常" + e.getMessage());

            //model.addAttribute("error","登录异常");
            //return "redirect:/login";
            return  ToutiaoUtil.getJSONString(1,"登录异常");
        }
    }
//    @RequestMapping(value = "/login",method = RequestMethod.GET)
//    public String login(){
//
//        return "login";
//    }
//
//    @RequestMapping(value = "/register",method = RequestMethod.GET)
//    public String register(){
//
//        return "register";
//    }
    //==========================登出过程===============================================
    @RequestMapping(path = {"/logout/"},method = {RequestMethod.GET,RequestMethod.POST})
    public String logout(@CookieValue("ticket") String ticket){
        userService.logout(ticket);
        //logger.info("logout：跳转到首页");
        return "redirect:/";
    }
}
