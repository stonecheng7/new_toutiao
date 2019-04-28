package com.nowcoder.toutiao.async;

/**
 * @program: new_toutiao
 * @description:
 * @author: Cheng Qun
 * @create: 2019-04-27 19:43
 */
public enum EventType {
    LIKE(0),
    COMMENT(1),
    LOGIN(2),
    MAIL(3);

    private int value;

    EventType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
