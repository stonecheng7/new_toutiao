package com.nowcoder.toutiao.model;

import org.springframework.stereotype.Component;

/**
 * @program: new_toutiao
 * @description: 当前线程访问的时候,这个用户是谁，用一个类来表示
 * @author: Cheng Qun
 * @create: 2019-04-23 21:31
 */
@Component
public class HostHolder {
    private static ThreadLocal<User> users = new ThreadLocal<User>();
    public User getUser(){
        return users.get();
    }
    public void setUser(User user){
        users.set(user);
    }
    public void clear() {
        users.remove();
    }
}
