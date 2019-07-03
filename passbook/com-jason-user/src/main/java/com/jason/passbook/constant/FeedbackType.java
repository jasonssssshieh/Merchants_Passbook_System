package com.jason.passbook.constant;

import lombok.Getter;

/**
 * 评论类型枚举
 */

@Getter
public enum FeedbackType {

    PASS("pass", "针对优惠券的评论"),
    APP("app", "针对卡包 APP 的评论");

    private String code;
    private String desc;

    FeedbackType(String code, String desc){
        this.code = code;
        this.desc = desc;
    }
}
