package org.example.common.model.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.example.common.enums.ErrorCodeEumn;
import org.example.common.model.entity.ShareOrderInfoPO;
import org.example.common.model.repository.OrderRepository;
import org.example.common.model.service.OrderService;
import org.example.common.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.text.ParseException;


@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    /**
     * 新增
     * @param shareOrderInfoPO
     * @return
     */
    public ShareOrderInfoPO saveOrUpdate(ShareOrderInfoPO shareOrderInfoPO){
        return orderRepository.saveOrUpdate(shareOrderInfoPO);
    }

    /**
     * 分页查查询所有用户
     * @param params
     * @return
     * @throws ParseException
     */
    @Override
    public PageUtil listAllOrder(String params) throws ParseException {
        JSONObject value = JSONObject.parseObject(params).getJSONObject("params");
        int rows = Integer.valueOf(value.getString("rows"));
        int page = Integer.valueOf(value.getString("page"));
        String filter = value.getString("optionParam");
        PageUtil page1 = new PageUtil();
        page1.setCurrentPage(page);
        page1.setRows(rows);
        return orderRepository.list(page1, filter);
    }


    /**
     * 获取用户信息
     *
     * @return
     */
    @Override
    public ShareOrderInfoPO getOrderInfo(ShareOrderInfoPO shareOrderInfoPO) {
        Assert.isTrue(null != shareOrderInfoPO.getId(), ErrorCodeEumn.USER_ID_NOT_NULL.getName());
        return orderRepository.getShareOrderInfoPO(JSONObject.parseObject(JSON.toJSONString(shareOrderInfoPO)));
    }

}
