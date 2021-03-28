package org.example.distributed.transaction.controller;

import org.example.common.model.entity.ShareOrderInfoPO;
import org.example.common.model.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping(value = "/order")
public class MainController {

    @Autowired
    private OrderService orderService;

    @RequestMapping(value = "/insert", method = RequestMethod.POST)
    public void insert(String[] args) {
        for (int i = 0; i < 1; i++) {
            ShareOrderInfoPO shareOrderInfoPO = new ShareOrderInfoPO();
            shareOrderInfoPO.setDataId(new Random().nextInt(999999));
            shareOrderInfoPO.setTableId(new Random().nextInt(999999));
            shareOrderInfoPO.setUserId("lilacseeking");
            shareOrderInfoPO.setOrderNo("2021032806215948" + new Random().nextInt(6));
            shareOrderInfoPO.setPayId("18803838075");
            shareOrderInfoPO.setSellerId("18803838000");
            shareOrderInfoPO.setOrderStatus("INIT");
            shareOrderInfoPO.setFinishTime(null);
            shareOrderInfoPO.setTradeMethod("WECHAT");
            shareOrderInfoPO.setDescription("测试下单");
            shareOrderInfoPO.setTradeId("2021032806215948" + new Random().nextInt(6));
            orderService.saveOrUpdate(shareOrderInfoPO);
        }
    }
}
