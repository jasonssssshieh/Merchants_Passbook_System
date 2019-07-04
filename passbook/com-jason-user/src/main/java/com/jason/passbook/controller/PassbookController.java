package com.jason.passbook.controller;

import com.jason.passbook.log.LogConstants;
import com.jason.passbook.log.LogGenerator;
import com.jason.passbook.service.IFeedbackService;
import com.jason.passbook.service.IGainPassTemplateService;
import com.jason.passbook.service.IInventoryService;
import com.jason.passbook.service.IUserPassService;
import com.jason.passbook.vo.Feedback;
import com.jason.passbook.vo.GainPassTemplateRequest;
import com.jason.passbook.vo.Pass;
import com.jason.passbook.vo.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Passbook RestController
 */

@Slf4j
@RestController
@RequestMapping("/passbook")
public class PassbookController {

    /**用户优惠券服务*/
    @Autowired
    private IUserPassService userPassService;

    /**优惠券库存服务**/
    @Autowired
    private IInventoryService inventoryService;

    /**领取优惠券服务**/
    @Autowired
    private IGainPassTemplateService gainPassTemplateService;

    /**用户反馈服务**/
    @Autowired
    private IFeedbackService feedbackService;

    /** httpServletRequest */
    @Autowired
    private HttpServletRequest httpServletRequest;


    /**
     * 获取用户个人的优惠券信息
     * @param userId 用户id
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/userpassinfo")
    Response userPassInfo(Long userId) throws Exception{
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.USER_PASS_INFO,
                null

        );
        return userPassService.getUserPassInfo(userId);
    }

    /**
     * 获取用户已经使用了的优惠券信息
     * @param userId 用户id
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/userusedpassinfo")
    Response userUsedPassInfo(Long userId) throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.USER_USED_PASS_INFO,
                null
        );
        return userPassService.getUserUsedPassInfo(userId);
    }

    /**
     * 用户使用优惠券
     * @param pass {@link Pass}
     * @return {@link Response}
     */
    @ResponseBody
    @PostMapping("/userusepass")
    Response userUsePass(@RequestBody Pass pass){
        LogGenerator.genLog(
                httpServletRequest,
                pass.getUserId(),
                LogConstants.ActionName.USER_USE_PASS,
                pass
        );
        return userPassService.userUsePass(pass);
    }


    /**
     * 获取库存信息
     * @param userId 用户id
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @GetMapping("/inventoryinfo")
    Response inventoryInfo(Long userId) throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.INVENTORY_INFO,
                null
        );
        return inventoryService.getInventoryInfo(userId);
    }

    /**
     * 用户领取优惠券
     * @param request {@link GainPassTemplateRequest}
     * @return {@link Response}
     * @throws Exception
     */
    @ResponseBody
    @PostMapping("/gainpasstemplate")
    //因为我们这里是一个post接口, post接口传递的是一个json字符串
    //而我们这里是一个GainPassTemplateRequest的对象 所以需要先进行序列化 把json字符串转化成请求对象
    //所以这里我们加了一个@RequestBody
    Response gainPassTemplate(@RequestBody GainPassTemplateRequest request)
            throws Exception {
        LogGenerator.genLog(
                httpServletRequest,
                request.getUserId(),
                LogConstants.ActionName.GAIN_PASS_TEMPLATE,
                request
        );
        return gainPassTemplateService.gainPassTemplate(request);
    }

    /**
     * 用户创建评论
     * @param feedback {@link Feedback}
     * @return {@link Response}
     */
    @ResponseBody
    @PostMapping("/createfeedback")
    Response createFeedback(@RequestBody Feedback feedback){

        LogGenerator.genLog(
                httpServletRequest,
                feedback.getUserId(),
                LogConstants.ActionName.CREATE_FEEDBACK,
                feedback
        );
        return feedbackService.createFeedback(feedback);
    }


    /**
     * 获取用户评论
     * @param userId 用户id
     * @return {@link Response}
     */
    @ResponseBody
    @GetMapping("/getfeedback")
    Response getFeedback(Long userId){
        LogGenerator.genLog(
                httpServletRequest,
                userId,
                LogConstants.ActionName.GET_FEEDBACK,
                null
        );
        return feedbackService.getFeedback(userId);
    }


    /**
     * 异常演示接口
     * @return {@link Response}
     * @throws Exception
     */
    //全局的异常捕获
    @ResponseBody
    @GetMapping("/exception")
    Response exception() throws Exception {
        throw new Exception("Welcome to Jason's System");
    }
}
