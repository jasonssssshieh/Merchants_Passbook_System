package com.jason.passbook.service.impl;

import com.jason.passbook.constant.Constants;
import com.jason.passbook.service.IUserService;
import com.jason.passbook.vo.Response;
import com.jason.passbook.vo.User;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.hadoop.hbase.client.Mutation;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建用户服务实现
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    /**
     * 这里我们需要一个hbase的客户端 因为要写入hbase,
     * 然后还需要一个redis的客户端,因为我们要生成user的id
     */

    //HBase客户端
    @Autowired
    private HbaseTemplate hbaseTemplate;

    //Redis
    @Autowired
    private StringRedisTemplate redisTemplate;



    /**
     *
     * @param user {@link User}
     * @return
     * @throws Exception
     */

    @Override
    public Response createUser(User user) throws Exception {

        //基本信息B列族
        byte[] FAMILY_B = Constants.UserTable.FAMILY_B.getBytes();
        byte[] NAME = Constants.UserTable.NAME.getBytes();
        byte[] AGE = Constants.UserTable.AGE.getBytes();
        byte[] SEX = Constants.UserTable.SEX.getBytes();

        //O列族
        byte[] FAMILY_O = Constants.UserTable.FAMILY_O.getBytes();
        byte[] PHONE = Constants.UserTable.PHONE.getBytes();
        byte[] ADDRESS = Constants.UserTable.ADDRESS.getBytes();

        //获取系统中当前用户个数 也就是我们genUserId的prefix
        Long curCount = redisTemplate.opsForValue().increment(Constants.USE_COUNT_REDIS_KEY, 1);
        Long userId = genUserId(curCount);

        //Mutation 是put和delete的子类
        List<Mutation> datas = new ArrayList<>();
        Put put = new Put(Bytes.toBytes(userId));

        put.addColumn(FAMILY_B, NAME, Bytes.toBytes(user.getBaseInfo().getName()));
        put.addColumn(FAMILY_B, AGE, Bytes.toBytes(user.getBaseInfo().getAge()));
        put.addColumn(FAMILY_B, SEX, Bytes.toBytes(user.getBaseInfo().getSex()));

        put.addColumn(FAMILY_O, PHONE, Bytes.toBytes(user.getOtherInfo().getPhone()));
        put.addColumn(FAMILY_O, ADDRESS, Bytes.toBytes(user.getOtherInfo().getAddress()));

        datas.add(put);

        hbaseTemplate.saveOrUpdates(Constants.UserTable.TABLE_NAME, datas);

        user.setId(userId);
        //把这个user对象当做我们的Object data 然后返回
        return new Response(user);
    }

    /**
     * 生成user id
     * @param prefix 当前系统中的用户数
     * @return 用户id
     */
    private Long genUserId(Long prefix){
        String suffix = RandomStringUtils.randomNumeric(5);
        return Long.valueOf(prefix + suffix);
    }
}
