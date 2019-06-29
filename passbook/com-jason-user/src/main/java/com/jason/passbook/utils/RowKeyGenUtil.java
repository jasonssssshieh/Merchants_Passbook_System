package com.jason.passbook.utils;

import com.jason.passbook.vo.Feedback;
import com.jason.passbook.vo.GainPassTemplateRequest;
import com.jason.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * RowKey 生成器工具类
 */

@Slf4j
public class RowKeyGenUtil {

    /**
     * 根据提供的passTemplate对象生成rowkey
     * @param passTemplate {@link PassTemplate}
     * @return String Rowkey
     */
    public static String genPassTemplateRowKey(PassTemplate passTemplate){
        //行程一个唯一的passinfo
        String passInfo = String.valueOf(passTemplate.getId()) + "_" + passTemplate.getTitle();
        String rowKey = DigestUtils.md5Hex(passInfo);//分散passInfo, 更好的load balance
        log.info("genPassTemplateRowKey: {}, {}", passInfo, rowKey);
        return rowKey;
    }


    /**
     * 根据提供的领取优惠券请求 生成rowkey, 只能在领取优惠券的时候使用
     * Pass RowKey = reversed(userId) + inverse(timestamp) + PassTemplateRowKey
     * @param request {@link GainPassTemplateRequest}
     * @return String Rowkey
     */
    public static String genPassRowKey(GainPassTemplateRequest request){

        return new StringBuilder(String.valueOf(request.getUserId())).reverse().toString()
                + (Long.MAX_VALUE - System.currentTimeMillis())
                + genPassTemplateRowKey(request.getPassTemplate());

    }

    /**
     * 根据feedback构造rowkey
     * @param feedback {@link Feedback}
     * @return String rowKey
     */
    public static String genFeedbackRowKey(Feedback feedback){
        //这里我们没有做md5 是因为我们更倾向于把同一个用户的feedback放在一起 这样方便scan操作
        //这里翻转是因为我们的前面都是一样的 递增的,所以这样不太好 所以翻转了一下
        return new StringBuilder(String.valueOf(feedback.getUserId())).reverse().toString()
                + (Long.MAX_VALUE - System.currentTimeMillis());
    }
}
