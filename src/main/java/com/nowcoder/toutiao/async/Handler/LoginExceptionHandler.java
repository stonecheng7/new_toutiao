package com.nowcoder.toutiao.async.Handler;

import com.nowcoder.toutiao.async.EventHandler;
import com.nowcoder.toutiao.async.EventModel;
import com.nowcoder.toutiao.async.EventType;
import com.nowcoder.toutiao.model.Message;
import com.nowcoder.toutiao.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.*;
import com.nowcoder.toutiao.ToutiaoUtil.MailSender;

/**
 * @program: new_toutiao
 * @description:
 * @author: Cheng Qun
 * @create: 2019-04-27 20:08
 */
@Component
public class LoginExceptionHandler implements EventHandler{

    @Autowired
    MailSender mailSender;

    @Autowired
    MessageService messageService;

    @Override
    public void doHandle(EventModel eventModel) {
        Message message = new Message();
        message.setToId(eventModel.getActorId());
        message.setCreatedDate(new Date());
        message.setContent("登录异常");
        message.setFromId(eventModel.getEntityOwnerId());
        messageService.addMessage(message);

        Map<String, Object> map = new HashMap<>();
        map.put("username", eventModel.getExt("username"));
        mailSender.sendWithHTMLTemplate(eventModel.getExt("email"),"登录异常","mails/welcome.html", map);
    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList(EventType.LOGIN);
    }
}
