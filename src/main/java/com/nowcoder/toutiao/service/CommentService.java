package com.nowcoder.toutiao.service;

import com.nowcoder.toutiao.dao.CommentDAO;
import com.nowcoder.toutiao.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @program: new_toutiao
 * @description: 评论comment的service
 * @author: Cheng Qun
 * @create: 2019-04-26 10:32
 */
@Service
public class CommentService {
    //通过service层将DAO的内容引进过来
    @Autowired
    private CommentDAO commentDAO;
    //选择一个评论
    public List<Comment> getCommentByEntity(int entityId,int entityType){
        return commentDAO.selectByEntity(entityId,entityType);
    }
    //增加一个评论
    public int addComment(Comment comment){
        return commentDAO.addComment(comment);
    }
    //选择获得某个实体评论的总数量
    public int getCommentCount(int entityId,int entityType){
        return commentDAO.getCommentCount(entityId,entityType);
    }

    //删除一个评论，就是更改状态
    public void deleteComment(int entityId,int entityType){
        commentDAO.updateStatus(entityId,entityType,1);
    }
}
