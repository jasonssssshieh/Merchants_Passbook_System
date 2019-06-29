package com.jason.passbook.service;

import com.jason.passbook.vo.GainPassTemplateRequest;
import com.jason.passbook.vo.Response;

/**
 * 用户领取优惠券功能接口
 */
public interface IGainPassTemplateService {

    /**
     * 用户领取优惠券
     * @param request {@link GainPassTemplateRequest}
     * @return {@link Response}
     * @throws Exception
     */
    Response gainPassTemplate(GainPassTemplateRequest request) throws Exception;
}
