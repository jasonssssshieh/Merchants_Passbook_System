package com.jason.passbook.vo;

import lombok.Data;

/**
 * Controller统一响应 通用返回对象
 */
@Data
public class Response {
    /**
     * 错误码, 正确返回0
     */
    private Integer errorCode = 0;

    /**
     * 错误信息 正确返回空字符串
     */
    private String errorMsg = "";

    /**
     * 返回值对象
     */
    private Object data;

    /**
     * 正确响应构造函数
     * @param data
     */
    public Response(Object data){
        this.data = data;
    }
    /**
     * 空响应 没有数据对象 的响应
     * @return
     */
    public static Response success(){
        return new Response();
    }

    /**
     * 便捷的错误响应
     * @param errorMsg
     * @return
     */
    public static Response failure(String errorMsg){
        return new Response(-1, errorMsg, null);
    }

    public Response(){ }
    public Response(Integer errorCode, String errorMsg, Object data){
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
        this.data = data;
    }
}
