package com.jason.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *统一的 异常信息
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorInfo <T> {
    /**
     * 错误码
     */
    public static final Integer ERROR = -1;

    /**
     * 特定错误码
     */
    private Integer code;

    /**
     * 错误 异常 信息
     */
    private String message;

    /**
     * 请求url
     */
    private String url;

    /**
     * 泛型, 请求返回的数据
     */
    private T data;
}
