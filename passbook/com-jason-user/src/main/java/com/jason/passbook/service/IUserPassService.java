package com.jason.passbook.service;

import com.jason.passbook.vo.Pass;
import com.jason.passbook.vo.Response;

/**
 * 获取用户个人优惠券信息
 */
public interface IUserPassService {

    /**
     * 获取用户个人优惠券信息, 即"我的优惠券"功能实现
     * @param userId 用户id
     * @return {@link Response}
     * @throws Exception
     */
    Response getUserPassInfo(Long userId) throws Exception;

    /**
     * 获取用户已经使用的优惠券信息, 即"已使用优惠券"功能实现
     * @param userId 用户id
     * @return {@link Response}
     * @throws Exception
     */
    Response getUserUsedPassInfo(Long userId) throws Exception;

    /**
     * 获取用户全部的优惠券信息
     * @param userId 用户id
     * @return {@link Response}
     * @throws Exception
     */
    Response getUserAllPassInfo(Long userId) throws Exception;

    /**
     * 用户使用优惠券
     * @param pass {@link Pass}
     * @return {@link Response}
     */
    Response userUsePass(Pass pass);
}
