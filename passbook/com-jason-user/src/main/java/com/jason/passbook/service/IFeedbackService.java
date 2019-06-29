package com.jason.passbook.service;

import com.jason.passbook.vo.Feedback;
import com.jason.passbook.vo.Response;

/**
 * 评论功能: 即用户评论相关功能接口
 */
public interface IFeedbackService {
    /**
     *创建评论
     * @param feedback {@link Feedback}
     * @return {@link Response}
     */
    Response createFeedback(Feedback feedback);

    /**
     * 获取用户评论
     * @param userId 用户id
     * @return {@link Response}
     */
    Response getFeedback(Long userId);

}
