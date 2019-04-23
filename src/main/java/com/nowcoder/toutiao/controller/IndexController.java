package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.aspect.LogAspect;
import com.nowcoder.toutiao.model.User;
import com.nowcoder.toutiao.service.ToutiaoService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.ws.Response;
import java.util.*;
import java.util.logging.Logger;

/**
 * @program: toutiao
 * @description: controller
 * @author: Cheng Qun
 * @create: 2019-04-18 20:49
 */
@Controller
public class IndexController {
    private static final org.slf4j.Logger logger =  LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    private ToutiaoService toutiaoService;


    @RequestMapping(path = {"/", "/index"})
    @ResponseBody
    public String inidex(HttpSession session){
        logger.info("visit index");
        return "hello world" + session.getAttribute("msg")+"<br> Say "+ toutiaoService.say();

    }

    @RequestMapping(value = "/vm")
    public String news(Model model){
        model.addAttribute("value","vvm");
        List<String> colors = Arrays.asList(new String[]{"RED", "GREEN", "BLUE"});

        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < 4; ++i) {
            map.put(String.valueOf(i), String.valueOf(i * i));
        }

        model.addAttribute("colors", colors);
        model.addAttribute("map", map);
        model.addAttribute("user",new User("tom"));
        return "news";
    }

    @RequestMapping(value = "/request")
    @ResponseBody
    public String request(HttpServletRequest request,
                          HttpServletResponse response,
                          HttpSession session){
        StringBuilder sb = new StringBuilder();
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name + "" + request.getHeader(name) + "<br>");
        }
        for(Cookie cookie:request.getCookies()){
            sb.append("Cookie:");
            sb.append(cookie.getName());
            sb.append(":");
            sb.append(cookie.getValue());
            sb.append("<br>");
        }
        return sb.toString();
    }


    @RequestMapping(value = "/response")
    @ResponseBody
    public String response(@CookieValue(value = "nowcoderid" , defaultValue = "a") String nowcoderid,
                           @RequestParam(value ="key",defaultValue = "key")String key,
                           @RequestParam(value = "value",defaultValue = "value")String value,
                           HttpServletResponse response){
        response.addCookie(new Cookie(key,value));
        response.addHeader(key,value);
        return "nowcoder id from cookie:" + nowcoderid;
    }

    @RequestMapping("/redirect/{code}")
    @ResponseBody
    public RedirectView redirect(@PathVariable("code") int code){
        RedirectView red = new RedirectView("/",true);
        if(code==301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }
    @RequestMapping("/admin")
    @ResponseBody
    public String admin(@RequestParam(value = "key",required = false) String key){
        if("admin".equals(key)){
            return "hello admin";
        }
        throw  new IllegalArgumentException(" xxxx ======error");
    }
    @ExceptionHandler
    @ResponseBody
    public String error(Exception e){
        return "error"+ e.getMessage();
    }
}
