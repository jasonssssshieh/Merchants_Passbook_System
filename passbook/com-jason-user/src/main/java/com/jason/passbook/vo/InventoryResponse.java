package com.jason.passbook.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 *
 * 库存请求响应
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryResponse {
    /**
     * 用户id: 因为每个用户看到的库存是不一样的 比如1号用户领取了A优惠券, 那么A就不会出现在1的结果里
     */
    private Long userId;

    /**
     * 用户可以看到的(未领取的)优惠券模板信息
     */
    private List<PassTemplateInfo> passTemplateInfos;
}
