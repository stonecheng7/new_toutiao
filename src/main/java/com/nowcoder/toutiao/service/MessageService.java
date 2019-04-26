package com.nowcoder.toutiao.service;

import com.nowcoder.toutiao.dao.MessageDAO;
import com.nowcoder.toutiao.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: new_toutiao
 * @description:
 * @author: Cheng Qun
 * @create: 2019-04-26 14:38
 */
@Service
public class MessageService {
    @Autowired
    MessageDAO messageDAO;
    //增加消息
    public int addMessage(Message message){
        return messageDAO.addMessage(message);
    }
//    选择两个人对话的所有消息
    public List<Message> getConversationDetail(String conversationId,int offset,int limit){
        return messageDAO.getConversationDetail(conversationId,offset,limit);
    }
    //得到登录与用户相关的所有消息列表，显示的内容是最新的
    public List<Message> getConversationList(int userId,int offset,int limit){
        return messageDAO.getConversationList(userId,offset,limit);
    }
    //未读消息列表的内容个数
    public int getConversationUnreadCount(int userId, String conversationId) {
        return messageDAO.getConversationUnReadCount(userId, conversationId);
    }
}
