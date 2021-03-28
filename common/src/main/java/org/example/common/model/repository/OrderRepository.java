package org.example.common.model.repository;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.querydsl.jpa.JPAQueryBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.example.common.model.entity.QShareOrderInfoPO;
import org.example.common.model.entity.ShareOrderInfoPO;
import org.example.common.util.PageUtil;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

@Repository
public class OrderRepository extends AbstractRepository<ShareOrderInfoPO> {

    QShareOrderInfoPO qShareOrderInfoPO = QShareOrderInfoPO.shareOrderInfoPO;
    /**
     * 保存或更新
     * @return
     */
    @Transactional
    public ShareOrderInfoPO saveOrUpdate(ShareOrderInfoPO ShareOrderInfoPO){
        return entityManager.merge(ShareOrderInfoPO);
    }

    /**
     * 分页查询
     * @param PageUtil
     * @return
     */
    public PageUtil list(PageUtil PageUtil, String filter) throws ParseException {
//        // 参数组装
        JSONObject value = JSONObject.parseObject(filter);
        JSONArray gmtCreateList = value.getJSONArray("gmtCreateList");
        JPAQueryBase query = new JPAQuery<>(entityManager).from(qShareOrderInfoPO);
        query.where(qShareOrderInfoPO.achieve.eq(0));
        // 创建日期
        if (null != gmtCreateList && gmtCreateList.size()>0){
            query.where(qShareOrderInfoPO.gmtCreate.in(new SimpleDateFormat("yyyy-MM-dd").parse(gmtCreateList.get(0).toString())
                    ,new SimpleDateFormat("yyyy-MM-dd").parse(gmtCreateList.get(1).toString())));
        }
        int count = (int)query.fetchCount();//总记录数
        query.limit(PageUtil.getRows());//每页记录数
        query.offset((long) PageUtil.getRows() * (PageUtil.getCurrentPage() - 1));//偏移量
        query.orderBy(qShareOrderInfoPO.id.desc());
        List<ShareOrderInfoPO> userPOList = query.fetch();
        //组装返回参数
        PageUtil.setCount(count);
        PageUtil.setResultList(userPOList);
        return PageUtil;
    }

    /**
     * 获取用户信息
     * @param shareOrderInfoPO
     * @return
     */
    public ShareOrderInfoPO getShareOrderInfoPO(JSONObject shareOrderInfoPO){
        JPAQuery query = new JPAQuery<>(entityManager).from(qShareOrderInfoPO);
        query.where(qShareOrderInfoPO.id.eq(shareOrderInfoPO.getLong("id")));
        query.where(qShareOrderInfoPO.achieve.eq(0));
        return (ShareOrderInfoPO)query.fetchFirst();
    }
}
