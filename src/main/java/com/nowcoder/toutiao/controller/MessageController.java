package com.nowcoder.toutiao.controller;

import com.nowcoder.toutiao.ToutiaoUtil.ToutiaoUtil;
import com.nowcoder.toutiao.dao.MessageDAO;
import com.nowcoder.toutiao.model.*;
import com.nowcoder.toutiao.service.MessageService;
import com.nowcoder.toutiao.service.UserService;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.javassist.bytecode.stackmap.BasicBlock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: new_toutiao
 * @description: 消息的controller
 * @author: Cheng Qun
 * @create: 2019-04-26 14:21
 */
@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Autowired
    MessageService messageService;
    @Autowired
    UserService userService;
    @Autowired
    HostHolder hostHolder;
    //添加站内消息
    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addMessage(@RequestParam("fromId") int fromId,
                             @RequestParam("toId") int toId,
                             @RequestParam("content") String content){
        try{
            Message message = new Message();
            message.setContent(content);
            message.setFromId(fromId);
            message.setToId(toId);
            message.setCreatedDate(new Date());
            //对话conversationId的作用：确定发送和接收的不同，两人肯定有发送方和接收方，但是会有两种情况a=>b和b=>a
            message.setConversationId(fromId<toId ? String.format("%d_%d",fromId,toId):String.format("%d_%d",toId,fromId));
            messageService.addMessage(message);
            return ToutiaoUtil.getJSONString(message.getId());
        }catch (Exception e){
            logger.error("增加消息失败",e.getMessage());
            return ToutiaoUtil.getJSONString(1,"插入消息失败");
        }
    }


    //获取详情消息
    // 消息的详情列表显示出来
    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    //@ResponseBody
    public String conversationDetail(Model model, @Param("conversationId") String conversationId){
        try {
            List<Message> conversationList = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> messages = new ArrayList<>();
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                User user = userService.getUser(msg.getFromId());
                if (user == null) {
                    continue;
                }
                vo.set("headUrl", user.getHeadUrl());
                vo.set("userId", user.getId());
                messages.add(vo);
            }
            model.addAttribute("messages", messages);
        }catch(Exception e){
            logger.error("获取详情消息失败" + e.getMessage());
        }
        return "letterDetail";
    }
    //获取站内信列表
    // 得到登录与用户相关的所有消息列表，显示的内容是最新的，
    // 其中包括未读消息的个数
    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String conversationDetail(Model model) {
        try {
            int localUserId = hostHolder.getUser().getId();
            List<ViewObject> conversations = new ArrayList<ViewObject>();
            List<Message> conversationList = messageService.getConversationList(localUserId, 0, 10);
            for (Message msg : conversationList) {
                ViewObject vo = new ViewObject();
                vo.set("conversation", msg);
                //聊天对话框需要显示对话人信息，需要确定是你发送给别人，还是别人发送给你。找到目标ID
                int targetId = msg.getFromId() == localUserId ? msg.getToId() : msg.getFromId();
                User user = userService.getUser(targetId);
                vo.set("user", user);
                vo.set("unread", messageService.getConversationUnreadCount(localUserId, msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);
        } catch (Exception e) {
            logger.error("获取站内信列表失败" + e.getMessage());
        }
        return "letter";
    }
}
