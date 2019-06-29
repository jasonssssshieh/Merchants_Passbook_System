package com.jason.passbook.service;

import com.alibaba.fastjson.JSON;
import com.jason.passbook.constant.Constants;
import com.jason.passbook.vo.PassTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 消费Kafka中的PassTemplate
 */

@Slf4j
@Component
public class ConsumePassTemplate {

    @Autowired
    public ConsumePassTemplate(IHBasePassService passService) {
        this.passService = passService;
    }

    /**
     * pass相关的hbase服务
     */
    private final IHBasePassService passService;

    @KafkaListener(topics = {Constants.TEMPLATE_TOPIC})
    public void receive(@Payload String passTemplate, //从kafka中接受的数据是什么 原始数据
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,// kafka特有注解 用于写入分区用
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,//分区
                        @Header(KafkaHeaders.RECEIVED_TOPIC)String topic){
        log.info("Consumer receive PassTemplate: {}", passTemplate);

        PassTemplate pt = new PassTemplate();

        try{
            pt = JSON.parseObject(passTemplate, PassTemplate.class);
        } catch (Exception ex){
            log.error("Parse PassTemplate Error: {}", ex.getMessage());
        }

        log.info("Drop PassTemplate to HBase: {}", passService.dropPassTemplateToHBase(pt));
    }
}
