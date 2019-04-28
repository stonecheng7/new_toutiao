package com.nowcoder.toutiao.service;

import com.nowcoder.toutiao.ToutiaoUtil.JedisAdapter;
import com.nowcoder.toutiao.ToutiaoUtil.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: new_toutiao
 * @description:
 * @author: Cheng Qun
 * @create: 2019-04-27 10:38
 */
@Service
public class LikeService {
    @Autowired
    JedisAdapter jedisAdapter;
    /**
        *@Description: 对于某项东西是否喜欢；喜欢返回1，不喜欢返回-1，否则返回0
        *@Param:
        *@return:
        *@Author: cqun
        *@date:
    */
    public int getLikeStatus(int userId, int entityType, int entityId) {
        //获得喜欢的like的set列表，列表名字的格式就是RedisKeyUtil定义的
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        if(jedisAdapter.sismember(likeKey, String.valueOf(userId))) {
            return 1;
        }
        //获得不喜欢的dislike的set列表，列表名字的格式就是RedisKeyUtil定义的
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        return jedisAdapter.sismember(disLikeKey, String.valueOf(userId)) ? -1 : 0;
    }

    public long like(int userId, int entityType, int entityId) {
        // 在喜欢集合里增加
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.sadd(likeKey, String.valueOf(userId));
        // 从反对里删除
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.srem(disLikeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }
    public long disLike(int userId, int entityType, int entityId) {
        // 在反对集合里增加
        String disLikeKey = RedisKeyUtil.getDisLikeKey(entityId, entityType);
        jedisAdapter.sadd(disLikeKey, String.valueOf(userId));
        // 从喜欢里删除
        String likeKey = RedisKeyUtil.getLikeKey(entityId, entityType);
        jedisAdapter.srem(likeKey, String.valueOf(userId));
        return jedisAdapter.scard(likeKey);
    }
}
