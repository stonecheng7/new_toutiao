package com.nowcoder.toutiao.async;

import java.util.List;

/**
 * @program: new_toutiao
 * @description:
 * @author: Cheng Qun
 * @create: 2019-04-27 19:51
 */
public interface EventHandler {
    void doHandle(EventModel model);
    List<EventType> getSupportEventTypes();
}
