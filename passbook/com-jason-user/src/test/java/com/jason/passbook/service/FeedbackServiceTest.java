package com.jason.passbook.service;

import com.alibaba.fastjson.JSON;
import com.jason.passbook.constant.FeedbackType;
import com.jason.passbook.vo.Feedback;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.awt.print.Pageable;

/**
 * 用户反馈服务测试
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class FeedbackServiceTest extends AbstractServiceTest{

    @Autowired
    private IFeedbackService feedbackService;

    @Test
    public void testCreateFeedback(){
        Feedback appFeedback = new Feedback();
        appFeedback.setUserId(userId);
        appFeedback.setType(FeedbackType.APP.getCode());
        appFeedback.setTemplateId("-1");
        appFeedback.setComment("创建app评论成功");

        System.out.println(JSON.toJSONString(
                feedbackService.createFeedback(appFeedback)
        ));

        Feedback passFeedback = new Feedback();
        passFeedback.setUserId(userId);
        passFeedback.setType(FeedbackType.PASS.getCode());
        passFeedback.setTemplateId("d03cce342b7047bc496f2a0fc1b1cf61");
        passFeedback.setComment("创建优惠券评论成功");

        System.out.println(JSON.toJSONString(
                feedbackService.createFeedback(passFeedback)
        ));
        /*
        写入成功:
        {"errorCode":0,"errorMsg":""}
        {"errorCode":0,"errorMsg":""}
     */
    }

    @Test
    public void testGetFeedback(){
        System.out.println(
                JSON.toJSONString(feedbackService.getFeedback(userId))
        );

        /**
         * {
         "data": [
         {
         "comment": "创建优惠券评论成功",
         "templateId": "d03cce342b7047bc496f2a0fc1b1cf61",
         "type": "pass",
         "userId": 346057
         },
         {
         "comment": "创建app评论成功",
         "templateId": "-1",
         "type": "app",
         "userId": 346057
         }
         ],
         "errorCode": 0,
         "errorMsg": ""
         }
         */
    }
}
