package com.jason.passbook.service;

import com.alibaba.fastjson.JSON;
import com.jason.passbook.vo.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 用户服务测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private IUserService userService;

    @Test
    public void testCreateUser() throws Exception {

        User user = new User();
        user.setBaseInfo(
                new User.BaseInfo("jason_test20190702", 18, "m")
        );
        user.setOtherInfo(
                new User.OtherInfo("10086","San Francisco, California")
        );

        System.out.println(
                JSON.toJSONString(userService.createUser(user))
        );
        /**
         * {"data":{"baseInfo":{"age":18,"name":"jason_test20190702","sex":"m"},"id":346057,
         * "otherInfo":{"address":"San Francisco, California","phone":"10086"}},
         * "errorCode":0,"errorMsg":""}
         */
    }
}
