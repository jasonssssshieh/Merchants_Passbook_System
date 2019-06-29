package com.jason.passbook.service.impl;
/**
 * 评论功能实现
 */

import com.alibaba.fastjson.JSON;
import com.jason.passbook.constant.Constants;
import com.jason.passbook.mapper.FeebackRowMapper;
import com.jason.passbook.service.IFeedbackService;
import com.jason.passbook.utils.RowKeyGenUtil;
import com.jason.passbook.vo.Feedback;
import com.jason.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class FeedbackServiceImpl implements IFeedbackService {

    // feedback是需要从HBase中获取的 或者需要写入hbase的
    /**
     * HBASE客户端
     */
    @Autowired
    HbaseTemplate hbaseTemplate;

    @Override
    public Response createFeedback(Feedback feedback) {
        if(!feedback.validate()){
            log.error("createFeedback Error: {}", JSON.toJSONString(feedback));
            return Response.failure("Feedback Error");
        }
        Put put = new Put(Bytes.toBytes(
                RowKeyGenUtil.genFeedbackRowKey(feedback)
        ));
        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.USER_ID),
                Bytes.toBytes(feedback.getUserId())
        );

        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.COMMENT),
                Bytes.toBytes(feedback.getComment())
        );

        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.TEMPLATE_ID),
                Bytes.toBytes(feedback.getTemplateId())
        );


        put.addColumn(
                Bytes.toBytes(Constants.Feedback.FAMILY_I),
                Bytes.toBytes(Constants.Feedback.TYPE),
                Bytes.toBytes(feedback.getType())
        );

        hbaseTemplate.saveOrUpdate(Constants.Feedback.TABLE_NAME, put);

        return Response.success();
    }

    @Override
    public Response getFeedback(Long userId) {
        //这里我们需要将HBase中的record转化成Object=> RowMapper
        //rowkeygen的方法可以参考{@link RowKeyGenUtil这个class}

        //因为我们这里hbase中的前缀就是userId的翻转
        /**
         * 下面是我们实现得到user ID的方法, 就是我们先传入了一个prefix 然后后面的5位数是一个随机数
         private Long genUserId(Long prefix){
         String suffix = RandomStringUtils.randomNumeric(5);
         return Long.valueOf(prefix + suffix);
         }

         下面是产生feedback的rowkey方法: 比如给定一个userId = 12345 那么和这个用户相关的feedback就是:
         1. 首先翻转12345-> 54321
         2. 然后加上LONG_MAX- currentTime
         那么前缀就是userId的翻转,因此我们如果给了一个userId, 需要去找到他相关的feedback 就是需要进行前缀扫描
         也就是先翻转userId 然后前缀扫描即可

         public static String genFeedbackRowKey(Feedback feedback){
         //这里我们没有做md5 是因为我们更倾向于把同一个用户的feedback放在一起 这样方便scan操作
         //这里翻转是因为我们的前面都是一样的 递增的,所以这样不太好 所以翻转了一下
         return new StringBuilder(String.valueOf(feedback.getUserId())).reverse().toString()
         + (Long.MAX_VALUE - System.currentTimeMillis());
         }
         */
        byte[] reverseUserId = new StringBuilder(String.valueOf(userId)).reverse().toString().getBytes();

        Scan scan = new Scan();//对hbase的表进行扫描
        scan.setFilter(new PrefixFilter(reverseUserId));//定义前缀扫描器

        List<Feedback> feedbacks = hbaseTemplate.find(
                Constants.Feedback.TABLE_NAME,
                scan,
                new FeebackRowMapper()
        );

        return new Response(feedbacks);
    }
}
