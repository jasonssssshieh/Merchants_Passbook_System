package com.jason.passbook.service.impl;

import com.alibaba.fastjson.JSON;
import com.jason.passbook.constant.Constants;
import com.jason.passbook.constant.PassStatus;
import com.jason.passbook.dao.MerchantsDao;
import com.jason.passbook.entity.Merchants;
import com.jason.passbook.mapper.PassRowMapper;
import com.jason.passbook.service.IUserPassService;
import com.jason.passbook.vo.Pass;
import com.jason.passbook.vo.PassInfo;
import com.jason.passbook.vo.PassTemplate;
import com.jason.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户优惠券相关功能实现
 */

@Slf4j
@Service
public class UserPassServiceImpl implements IUserPassService {

    /**
     * HBase客户端
     */
    @Autowired
    private HbaseTemplate hbaseTemplate;

    /**
     * MerchantsDao接口 访问merchants数据库
     */
    @Autowired
    private MerchantsDao merchantsDao;

    @Override
    public Response getUserPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.UNUSED);
    }

    @Override
    public Response getUserUsedPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.USED);
    }

    @Override
    public Response getUserAllPassInfo(Long userId) throws Exception {
        return getPassInfoByStatus(userId, PassStatus.ALL);
    }

    @Override
    public Response userUsePass(Pass pass) {
        /**
         * 根据userId构造行键前缀
         */
        byte[] rowPrefix = Bytes.toBytes(new StringBuilder(String.valueOf(pass.getUserId())).reverse().toString());
        Scan scan = new Scan();
        List<Filter> filters = new ArrayList<>();
        //定义三个过滤器
        filters.add(new PrefixFilter(rowPrefix));
        filters.add(
                new SingleColumnValueFilter(
                        Constants.PassTable.FAMILY_I.getBytes(),
                        Constants.PassTable.TEMPLATE_ID.getBytes(),
                        CompareFilter.CompareOp.EQUAL,
                        Bytes.toBytes(pass.getTemplateId())
                )
        );
        filters.add(
                new SingleColumnValueFilter(
                        Constants.PassTable.FAMILY_I.getBytes(),
                        Constants.PassTable.CON_DATE.getBytes(),
                        CompareFilter.CompareOp.EQUAL,
                        Bytes.toBytes(-1)//必须是未被使用的
                )
        );

        scan.setFilter(new FilterList(filters));
        List<Pass> passes = hbaseTemplate.find(
                Constants.PassTable.TABLE_NAME,
                scan,
                new PassRowMapper()
        );

        //如果说这里我们找不到这样的一个pass或者说找到了多个重复的记录 那都是有错误的 这里就返回错误
        if(passes == null || passes.size() != 1){
            log.error("UserUsePass Error: {}", JSON.toJSONString(pass));
            return Response.failure("UserUsePass Error");
        }

        //因为这里user use 了pass 那么我们在对应的hbase中也应该做修改
        byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
        byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();

        List<Mutation> datas = new ArrayList<>();
        //这里建立一个put对象对hbase进行操作 我们传进来的pass是不带rowkey的
        // 所以我们从passes(我们从数据库中获取的list of pass)来获取行键
        Put put = new Put(passes.get(0).getRowKey().getBytes());
        put.addColumn(
                FAMILY_I,
                CON_DATE,
                Bytes.toBytes(DateFormatUtils.ISO_DATE_FORMAT.format(new Date()))
        );
        datas.add(put);
        hbaseTemplate.saveOrUpdates(Constants.PassTable.TABLE_NAME, datas);
        return Response.success();
    }


    //<PassTemplate RowKey, PassTemplate>

    /**
     * 通过获取的pass对象 去构造Map
     * @param passes {@link Pass}
     * @return Map {@link PassTemplate}
     * @throws Exception
     */
    private Map<String, PassTemplate> buildPassTemplateMap(List<Pass> passes)
            throws Exception{
        byte[] FAMILY_B = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_B);
        byte[] ID = Bytes.toBytes(Constants.PassTemplateTable.ID);
        byte[] TITLE = Bytes.toBytes(Constants.PassTemplateTable.TITLE);
        byte[] SUMMARY = Bytes.toBytes(Constants.PassTemplateTable.SUMMARY);
        byte[] DESC = Bytes.toBytes(Constants.PassTemplateTable.DESC);
        byte[] HAS_TOKEN = Bytes.toBytes(Constants.PassTemplateTable.HAS_TOKEN);
        byte[] BACKGROUND = Bytes.toBytes(Constants.PassTemplateTable.BACKGROUND);

        byte[] FAMILY_C = Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C);
        byte[] LIMIT = Bytes.toBytes(Constants.PassTemplateTable.LIMIT);
        byte[] START = Bytes.toBytes(Constants.PassTemplateTable.START);
        byte[] END = Bytes.toBytes(Constants.PassTemplateTable.END);

        List<String> templateIds = passes.stream().map(
                Pass::getTemplateId
        ).collect(Collectors.toList());

        List<Get> templateGets = new ArrayList<>(templateIds.size());
        templateIds.forEach(t->templateGets.add(new Get(Bytes.toBytes(t))));

        Result[] templateResults = hbaseTemplate.getConnection().
                getTable(TableName.valueOf(Constants.PassTemplateTable.TABLE_NAME))
                .get(templateGets);

        //构造passTemplateId -> passTemplate object的Map 用于构造PassInfo
        Map<String, PassTemplate> templateId2Object = new HashMap<>();
        for (Result result : templateResults) {
            PassTemplate passTemplate = new PassTemplate();

            //填充列族B
            passTemplate.setId(Bytes.toInt(result.getValue(FAMILY_B, ID)));
            passTemplate.setBackground(Bytes.toInt(result.getValue(FAMILY_B, BACKGROUND)));
            passTemplate.setTitle(Bytes.toString(result.getValue(FAMILY_B, TITLE)));
            passTemplate.setSummary(Bytes.toString(result.getValue(FAMILY_B, SUMMARY)));
            passTemplate.setDesc(Bytes.toString(result.getValue(FAMILY_B,DESC)));
            passTemplate.setHasToken(Bytes.toBoolean(result.getValue(FAMILY_B, HAS_TOKEN)));

            //填充列族C
            String[] patterns  = new String[]{"yyyy-MM-dd"};
            passTemplate.setLimit(Bytes.toLong(result.getValue(FAMILY_C,LIMIT)));
            passTemplate.setStart(DateUtils.parseDate(Bytes.toString(result.getValue(FAMILY_C,START)), patterns));
            passTemplate.setEnd(DateUtils.parseDate(Bytes.toString(result.getValue(FAMILY_C, END)), patterns));

            templateId2Object.put(Bytes.toString(result.getRow()), passTemplate);
        }
        return templateId2Object;
    }

    /**
     * 通过获取的PassTemplate对象 构造MerchantsMap
     * @param passTemplates {@link PassTemplate}
     * @return Map {@link Merchants}
     */
    private Map<Integer, Merchants> buildMerchantsMap
            (List<PassTemplate> passTemplates) {
        Map<Integer, Merchants> merchantsMap = new HashMap<>();
        List<Integer> merchantsIds = passTemplates.stream().map(
                PassTemplate::getId
        ).collect(Collectors.toList());

        List<Merchants> merchants = merchantsDao.findByIdIn(merchantsIds);

        /*merchantsIds.forEach(id->{
            merchants.add(merchantsDao.findById(id));
        });*/

        /*for(int i = 0; i < merchants.size(); ++i){
            merchantsMap.put(merchantsIds.get(i), merchants.get(i));
        }*/
        merchants.forEach(m->{
            merchantsMap.put(m.getId(), m);
        });

        return merchantsMap;
    }

    /**
     * 根据优惠券状态获取优惠券信息
     * @param userId 用户id
     * @param status {@link PassStatus}
     * @return {@link Response}
     * @throws Exception
     */
    private Response getPassInfoByStatus(Long userId, PassStatus status)
            throws Exception{

        //根据userId构造rowkey前缀
        byte[] rowPrefix = Bytes.toBytes(new StringBuilder(String.valueOf(userId)).reverse().toString());

        CompareFilter.CompareOp compareOp = status == PassStatus.UNUSED ?
                CompareFilter.CompareOp.EQUAL : CompareFilter.CompareOp.NOT_EQUAL;

        Scan scan = new Scan();
        List<Filter> filters = new ArrayList<>();
        //1. rowkey前缀过滤器, 找到特定用户的优惠券
        filters.add(new PrefixFilter(rowPrefix));
        //2. 基于列单元的过滤器, 找到未使用的优惠券
        if(status == PassStatus.ALL) {
            filters.add(
                    //对hbase数据库中单列的值进行过滤
                    // 等于-1就是没有被消费, 不等于-1就是已经被消费了,
                    // 所以前面的EQUAL 和 NOT_EQUAL 就是如果是UNUSED->EQUAL, USE->NOT_EQUAL对应了起来
                    new SingleColumnValueFilter(
                            Constants.PassTable.FAMILY_I.getBytes(),
                            Constants.PassTable.CON_DATE.getBytes(),
                            compareOp,
                            Bytes.toBytes("-1")
                    ));
        }
        scan.setFilter(new FilterList(filters));//默认这个filter list里面的filter关系是AND

        //从hbase中获取 pass的list
        List<Pass> passes = hbaseTemplate.find(
                Constants.PassTable.TABLE_NAME,
                scan,
                new PassRowMapper()
        );
        //根据上面写的两个方法, 把pass 转化成passtemplate, 然后用passtemplate生成 商户id 和merchants的map
        Map<String, PassTemplate> passTemplateMap = buildPassTemplateMap(passes);
        Map<Integer, Merchants> merchantsMap = buildMerchantsMap(
                new ArrayList<>(passTemplateMap.values()));

        List<PassInfo> result = new ArrayList<>();

        for (Pass pass : passes) {
            PassTemplate passTemplate = passTemplateMap.getOrDefault(
                    pass.getTemplateId(), null
            );
            if(null == passTemplate){
                log.error("PassTemplate Null: {}", passTemplate);
                continue;
            }
            Merchants merchants = merchantsMap.getOrDefault(passTemplate.getId(), null);
            if(merchants == null){
                log.error("Merchants Null: {}", passTemplate.getId());
                continue;
            }
            result.add(new PassInfo(pass, passTemplate, merchants));
        }
        return new Response(result);
    }
}
