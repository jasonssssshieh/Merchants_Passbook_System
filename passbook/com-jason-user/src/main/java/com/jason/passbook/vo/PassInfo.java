package com.jason.passbook.vo;

import com.jason.passbook.entity.Merchants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户领取的优惠券信息
 * (未领取的叫PassTemplateInfo)
 * hbase返回给用户响应的填充
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PassInfo {

    /**
     * 优惠券信息
     */
    private Pass pass;

    /**
     * 这张优惠券属于哪个模板的模板信息
     */
    private PassTemplate passTemplate;

    /**
     * 优惠券所属商户信息
     */
    private Merchants merchants;
}
