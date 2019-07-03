package com.jason.passbook.service;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 服务测试抽象基类
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public abstract class AbstractServiceTest {
    Long userId;

    @Before
    public void init(){
        userId = 346057L;
    }//统一了一个userId

}
