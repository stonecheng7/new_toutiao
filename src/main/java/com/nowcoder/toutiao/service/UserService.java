package com.nowcoder.toutiao.service;

import antlr.StringUtils;
import com.nowcoder.toutiao.ToutiaoUtil.ToutiaoUtil;
import com.nowcoder.toutiao.dao.LoginTicketDAO;
import com.nowcoder.toutiao.dao.UserDAO;
import com.nowcoder.toutiao.model.LoginTicket;
import com.nowcoder.toutiao.model.User;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @program: toutiao
 * @description: userservice
 * @author: Cheng Qun
 * @create: 2019-04-19 22:02
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private LoginTicketDAO loginTicketDAO;

    //=======================注册的功能=========================
    //增加注册的用户
    public Map<String,Object> register(String username,String password){
        Map<String,Object> map = new HashMap<String,Object>();
        if(org.apache.commons.lang.StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(org.apache.commons.lang.StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user!=null){
            map.put("msgname","用户名已经被注册了");
            return map;
        }

        //可以增加的功能:检测密码的强度不够的提示


        //如果注册用户的名称和密码正确，那么就可以注册用户，将注册的姓名，加密后的密码都注册在一个对象user中，
        //然后将user对象添加到数据库中
        user = new User();
        user.setName(username);
        //根据注册密码安全的要求，在用户注册密码的基础上，添加5位的随机密码，防止被破解
        user.setSalt(UUID.randomUUID().toString().substring(0,5));
        //给用户添加头像
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png", new Random().nextInt(1000)));
        //后台给用户生成的密码====使用自己建立的头条工具类中md5方法，添加的密码
        //为用户密码+生成随机数的5位组成的密码
        user.setPassword(ToutiaoUtil.MD5(password+user.getSalt()));

        //使用userDAO对象添加注册的用户
        userDAO.addUser(user);


        //如果还用户是注册成功，那么后台都给用户发送一个ticket，相当于用户身份，便于以后登录
        //需要获取ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;



    }



    //=======================登录的功能=========================
    public Map<String,Object> login(String username,String password){
        Map<String,Object> map = new HashMap<String,Object>();
        if(org.apache.commons.lang.StringUtils.isBlank(username)){
            map.put("msgname","用户名不能为空");
            return map;
        }
        if(org.apache.commons.lang.StringUtils.isBlank(password)){
            map.put("msgpwd","密码不能为空");
            return map;
        }
        User user = userDAO.selectByName(username);
        if(user==null){
            map.put("msgname","用户名不存在");
            return map;
        }

        //验证密码是否存在
        //用户名的密码+后台生成随机5位密码通过md5加密后的结果与接收到的结果密码对比
        //判断该用户是否登录正确的
        if(!ToutiaoUtil.MD5(password+user.getSalt()).equals(user.getPassword())){
            map.put("msgpwd","密码不正确");
            return map;
        }
        //如果还用户是注册成功，那么后台都给用户发送一个ticket，相当于用户身份，便于以后登录
        //需要获取ticket
        String ticket = addLoginTicket(user.getId());
        map.put("ticket",ticket);
        return map;
    }
    //获取ticket的方法
    private String addLoginTicket(int userId){
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(userId);
        Date date = new Date();
        date.setTime(date.getTime()+1000*3600*24);
        ticket.setExpired(date);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
        loginTicketDAO.addTicket(ticket);
        return ticket.getTicket();
    }

    public User getUser(int id){
        return userDAO.selectById(id);
    }
    //=======================登出的功能=========================
    public void logout(String ticket){
        loginTicketDAO.updateStatus(ticket,1);
    }
}
