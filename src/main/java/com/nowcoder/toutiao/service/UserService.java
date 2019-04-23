package com.nowcoder.toutiao.service;

import com.nowcoder.toutiao.dao.UserDAO;
import com.nowcoder.toutiao.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: toutiao
 * @description: userservice
 * @author: Cheng Qun
 * @create: 2019-04-19 22:02
 */
@Service
public class UserService {
    @Autowired
    private UserDAO userDAO;
    public User getUser(int id){
        return userDAO.selectById(id);
    }
}
