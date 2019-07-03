package com.jason.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.jason.passbook.constant.Constants;
import com.jason.passbook.mapper.PassTemplateRowMapper;
import com.jason.passbook.service.IGainPassTemplateService;
import com.jason.passbook.utils.RowKeyGenUtil;
import com.jason.passbook.vo.GainPassTemplateRequest;
import com.jason.passbook.vo.PassTemplate;
import com.jason.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 用户领取优惠券 功能实现
 */

@Slf4j
@Service
public class GainPassTemplateServiceImpl implements IGainPassTemplateService{

    /*redis客户端*/
    @Autowired
    private StringRedisTemplate redisTemplate;

    /*** hbase客户端*/
    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Override
    public Response gainPassTemplate(GainPassTemplateRequest request) throws Exception {

        String passTemplateId = RowKeyGenUtil.genPassTemplateRowKey(request.getPassTemplate());
        PassTemplate passTemplate;
        try{
            passTemplate = hbaseTemplate.get(
                    Constants.PassTemplateTable.TABLE_NAME,
                    passTemplateId,
                    new PassTemplateRowMapper()
            );
        } catch (Exception ex){
            log.error("gainPassTemplate error: {}", JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("Gain PassTemplate error.");
        }

        /**
         * 检查当前优惠券是否能被领取:
         * 1, limit = -1 || limit > 0
         * 2. 优惠券还没过期 还在可用时间范围内
         */

        if(passTemplate.getLimit() != -1 && passTemplate.getLimit() <= 0){
            //不能被领取
            log.error("PassTemplate Limit Max: {}", JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("PassTemplate Limit Max.");
        }


        Date curDate = new Date();
        if(!(curDate.getTime() >= passTemplate.getStart().getTime()
                && curDate.getTime() < passTemplate.getEnd().getTime())){
            log.error("PassTemplate Validate Time Error: {}", JSON.toJSONString(request.getPassTemplate()));
            return Response.failure("PassTemplate Validate Time Error.");
        }

        // 减去优惠券的limit
        if(passTemplate.getLimit() != -1){
            List<Mutation> datas = new ArrayList<>();
            byte[] FAMILY_C = Constants.PassTemplateTable.FAMILY_C.getBytes();
            byte[] LIMIT = Constants.PassTemplateTable.LIMIT.getBytes();

            Put put = new Put(Bytes.toBytes(passTemplateId));//passTemplateId =>行键
            put.addColumn(
                    FAMILY_C,
                    LIMIT,
                    Bytes.toBytes(passTemplate.getLimit() - 1)
            );
            datas.add(put);
            hbaseTemplate.saveOrUpdates(Constants.PassTemplateTable.TABLE_NAME, datas);
        }

        //将优惠券保存到用户优惠券的列表里: Pass Table
        if(! addPassForUser(request, passTemplate.getId(), passTemplateId)){
            return Response.failure("Gain PassTemplate Failure.");
        }
        return Response.success();
    }

    /**
     * 给用户添加优惠券
     * @param request {@link GainPassTemplateRequest}
     * @param merchantsId 商户id
     * @param passTemplateId 优惠券id
     * @return true/false
     * @throws Exception
     */
    private boolean addPassForUser(GainPassTemplateRequest request,
                                   Integer merchantsId,
                                   String passTemplateId) throws Exception {
        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] USER_ID = Constants.PassTable.USER_ID.getBytes();
        byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
        byte[] TOKEN = Constants.PassTable.TOKEN.getBytes();
        byte[] ASSIGNED_DATE = Constants.PassTable.ASSIGNED_DATE.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

        List<Mutation> datas = new ArrayList<>();
        Put put = new Put(Bytes.toBytes(RowKeyGenUtil.genPassRowKey(request)));
        put.addColumn(FAMILY_I, USER_ID, Bytes.toBytes(request.getUserId()));
        put.addColumn(FAMILY_I, TEMPLATE_ID, Bytes.toBytes(passTemplateId));

        if(request.getPassTemplate().getHasToken()){
            String token = redisTemplate.opsForSet().pop(passTemplateId);
            if(token == null){
                log.error("Token not exist: {}", passTemplateId);
                return false;
            }
            recordTokenToFile(merchantsId, passTemplateId, token);
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes(token));
        }else{
            put.addColumn(FAMILY_I, TOKEN, Bytes.toBytes("-1"));//token不存在
        }

        put.addColumn(FAMILY_I, ASSIGNED_DATE, Bytes.toBytes(
                DateFormatUtils.ISO_DATE_FORMAT.format(new Date())//领取日期 那么就是当前的时间
        ));
        put.addColumn(FAMILY_I, CON_DATE, Bytes.toBytes("-1"));//消费日期 领取还没被消费,所以设为-1

        datas.add(put);
        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, datas);

        return true;
    }


    /**
     * 将已使用的token记录到文件中
     * @param merchantsId 商户id
     * @param passTemplateId PassTemplate 优惠券id
     * @param token 分配的优惠券token
     *
     */
    private void recordTokenToFile(Integer merchantsId,
                                   String passTemplateId,
                                   String token) throws Exception {
        Files.write(
                Paths.get(Constants.TOKEN_DIR,
                        String.valueOf(merchantsId),
                        passTemplateId + Constants.USED_TOKEN_SUFFIX),
                (token + "\n").getBytes(),
                StandardOpenOption.CREATE,// 不存在的话就先创建,再追加
                StandardOpenOption.APPEND//已追加的方式去写
        );
    }

}
