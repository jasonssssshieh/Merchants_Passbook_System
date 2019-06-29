package com.jason.passbook.service;

import com.jason.passbook.vo.Response;
import com.jason.passbook.vo.User;

/**
 * 用户服务: 创建User服务 其实就是用户在我们平台注册, 我们将用户信息写入hbase
 */
public interface IUserService {

    /**
     * 创建用户
     * @param user {@link User}
     * @return {@link Response}
     * @throws Exception
     */
    Response createUser(User user) throws Exception;
}
