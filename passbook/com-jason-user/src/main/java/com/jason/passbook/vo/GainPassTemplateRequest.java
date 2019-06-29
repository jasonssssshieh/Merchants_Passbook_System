package com.jason.passbook.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户领取优惠券的请求对象
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GainPassTemplateRequest {
    /**
     * 用户id: 谁来请求这个passTemplate
     */
    private Long userId;

    /**
     * 用户领取的是哪一个passTemplate
     */
    private PassTemplate passTemplate;
}
