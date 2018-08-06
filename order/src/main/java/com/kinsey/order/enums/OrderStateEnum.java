package com.kinsey.order.enums;

/**
 * Created by zj on 2018/7/27
 */
public enum OrderStateEnum {
    CONFIRMED("确定"),
    CANCELLED("取消"),
    DELETED("删除"),
    RESERVING("收到"),
    PROCESSING("进行中");

    private String description;

    OrderStateEnum(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
