package com.jason.passbook.controller;

import com.jason.passbook.log.LogConstants;
import com.jason.passbook.log.LogGenerator;
import com.jason.passbook.service.IUserService;
import com.jason.passbook.vo.Response;
import com.jason.passbook.vo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 创建用户服务
 */
@Slf4j
@RestController
@RequestMapping("/passbook")
public class CreateUserController {

    /**
     * 创建用户服务
     */
    @Autowired
    private IUserService userService;

    /*HttpServletRequest*/
    @Autowired
    private HttpServletRequest httpServletRequest;


    /**
     *
     * @param user
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/createuser")
    Response createUser(@RequestBody User user) throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                -1L,
                LogConstants.ActionName.CREATE_USER,
                user
        );
        return userService.createUser(user);
    }
}
