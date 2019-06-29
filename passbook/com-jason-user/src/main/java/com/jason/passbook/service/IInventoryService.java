package com.jason.passbook.service;

import com.jason.passbook.vo.Response;

/**
 * 获取库存信息接口: 只返回用户未领取的优惠券信息,
 * 即优惠券库存功能实现接口定义
 */
public interface IInventoryService {
    /**
     * 获取库存信息
     * @param userId 用户id
     * @return {@link Response}
     * @throws Exception
     */
    Response getInventoryInfo(Long userId) throws Exception;
}
