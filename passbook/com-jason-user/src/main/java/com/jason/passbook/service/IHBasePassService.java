package com.jason.passbook.service;

import com.jason.passbook.vo.PassTemplate;

/**
 * pass hbase 服务
 */

public interface IHBasePassService {
    /**
     * 将PassTemplate写入HBase
     * @param passTemplate {@link PassTemplate}
     * @return true/false
     */
    boolean dropPassTemplateToHBase(PassTemplate passTemplate);
}
