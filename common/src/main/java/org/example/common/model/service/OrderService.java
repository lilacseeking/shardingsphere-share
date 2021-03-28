package org.example.common.model.service;

import org.example.common.model.entity.ShareOrderInfoPO;
import org.example.common.util.PageUtil;

import java.text.ParseException;

public interface OrderService {

    /**
     * 新增
     * @param shareOrderInfoPO
     * @return
     */
    ShareOrderInfoPO saveOrUpdate(ShareOrderInfoPO shareOrderInfoPO);

    /**
     * 分页查查询所有用户
     * @param params
     * @return
     */
    PageUtil listAllOrder(String params) throws ParseException;

    /**
     * 获取用户信息
     * @return
     */
    ShareOrderInfoPO getOrderInfo(ShareOrderInfoPO userDTO);

}
