package com.jason.passbook.service;

import com.alibaba.fastjson.JSON;
import com.jason.passbook.vo.Pass;
import org.junit.Test;
import org.junit.experimental.theories.internal.ParameterizedAssertionError;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 用户优惠券服务测试
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserPassServiceTest extends AbstractServiceTest{
    @Autowired
    private IUserPassService userPassService;


    @Test
    public void testGetUserPassInfo() throws Exception {
        System.out.println(
                JSON.toJSONString(
                        userPassService.getUserPassInfo(userId)
                )
        );
    }

    /**
     * {"errorCode":0,"errorMsg":""}
     */
    @Test
    public void testGetUserUsedPassInfo() throws Exception {
        System.out.println(
                JSON.toJSONString(
                        userPassService.getUserUsedPassInfo(userId)
                )
        );
    }

    @Test
    public void testGetUserAllPassInfo() throws Exception {
        System.out.println(
                JSON.toJSONString(
                        userPassService.getUserAllPassInfo(userId)
                )
        );
    }

    @Test
    public  void testUserUsePass(){
        Pass pass = new Pass();
        pass.setTemplateId("d03cce342b7047bc496f2a0fc1b1cf61");
        pass.setUserId(userId);

        System.out.println(
                JSON.toJSONString(
                        userPassService.userUsePass(pass)
                )
        );
    }

}
