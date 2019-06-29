package com.jason.passbook.service.impl;

import com.jason.passbook.constant.Constants;
import com.jason.passbook.mapper.PassTemplateRowMapper;
import com.jason.passbook.service.IInventoryService;
import com.jason.passbook.utils.RowKeyGenUtil;
import com.jason.passbook.vo.PassTemplate;
import com.jason.passbook.vo.Response;
import com.spring4all.spring.boot.starter.hbase.api.HbaseTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.LongComparator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 库存服务 获取库存信息: 只返回用户没有领取的
 */

@Slf4j
@Service
public class InventoryServiceImpl implements IInventoryService{

    //HBase客户端
    @Autowired
    private HbaseTemplate hbaseTemplate;

    @Override
    public Response getInventoryInfo(Long userId) throws Exception {
        return null;
    }

    /**
     * 获取系统用可用的优惠券
     * @param excludeIds 需要排除的优惠券ids
     * @return {@link PassTemplate}
     */
    private List<PassTemplate> getAvailablePassTemplate(List<String> excludeIds){

        FilterList filterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);//过滤器之间是OR的关系
        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.GREATER,
                        new LongComparator(0L)//limit > 0
                )
        );
        filterList.addFilter(
                new SingleColumnValueFilter(
                        Bytes.toBytes(Constants.PassTemplateTable.FAMILY_C),
                        Bytes.toBytes(Constants.PassTemplateTable.LIMIT),
                        CompareFilter.CompareOp.EQUAL,
                        Bytes.toBytes("-1")//= -1(不存在上限)
                )
        );
        Scan scan = new Scan();
        scan.setFilter(filterList);

        List<PassTemplate> validPassTemplates = hbaseTemplate.find(
                Constants.PassTemplateTable.TABLE_NAME,
                scan,
                new PassTemplateRowMapper()
        );

        List<PassTemplate> availablePassTemplates = new ArrayList<>();
        Date curTime = new Date();

        for (PassTemplate validPassTemplate : validPassTemplates) {
            String rowKey = RowKeyGenUtil.genPassTemplateRowKey(validPassTemplate);
            //必须是不在[排除ids]的
            if(excludeIds.contains(rowKey)){
                continue;
            }
            //必须依然有效
            if(curTime.getTime() >= validPassTemplate.getStart().getTime()
                    && curTime.getTime() <= validPassTemplate.getStart().getTime()){
                availablePassTemplates.add(validPassTemplate);
            }
        }

        return availablePassTemplates;
    }
}
