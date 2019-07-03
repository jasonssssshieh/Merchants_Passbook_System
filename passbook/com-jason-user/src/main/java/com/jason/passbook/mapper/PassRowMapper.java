package com.jason.passbook.mapper;


import com.jason.passbook.constant.Constants;
import com.jason.passbook.vo.Pass;
import com.spring4all.spring.boot.starter.hbase.api.RowMapper;
import org.apache.commons.lang.time.DateUtils;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * HBase Pass row to Pass object
 */
public class PassRowMapper implements RowMapper<Pass>{

    private static byte[] FAMILY_I = Constants.PassTable.FAMILY_I.getBytes();
    private static byte[] TEMPLATE_ID = Constants.PassTable.TEMPLATE_ID.getBytes();
    private static byte[] USER_ID = Constants.PassTable.USER_ID.getBytes();
    private static byte[] TOKEN = Constants.PassTable.TOKEN.getBytes();
    private static byte[] CON_DATE = Constants.PassTable.CON_DATE.getBytes();
    private static byte[] ASSIGNED_DATE = Constants.PassTable.ASSIGNED_DATE.getBytes();

    @Override
    public Pass mapRow(Result result, int rowNum) throws Exception {

        Pass pass = new Pass();
        pass.setTemplateId(Bytes.toString(result.getValue(FAMILY_I, TEMPLATE_ID)));
        pass.setToken(Bytes.toString(result.getValue(FAMILY_I, TOKEN)));
        pass.setUserId(Bytes.toLong(result.getValue(FAMILY_I, USER_ID)));

        String[] pattern = new String[] {"yyyy-MM-dd"};
        String conDateStr = Bytes.toString(result.getValue(FAMILY_I, CON_DATE));
        if(conDateStr.equals("-1")){
            pass.setConDate(null);//未被消费
        }else{
            //已被消费
            pass.setConDate(DateUtils.parseDate(
                    Bytes.toString(result.getValue(FAMILY_I, CON_DATE)), pattern
            ));
        }
        pass.setAssignedDate(DateUtils.parseDate(
                Bytes.toString(result.getValue(FAMILY_I, ASSIGNED_DATE)), pattern
        ));

        pass.setRowKey(Bytes.toString(result.getRow()));
        return pass;
    }
}
