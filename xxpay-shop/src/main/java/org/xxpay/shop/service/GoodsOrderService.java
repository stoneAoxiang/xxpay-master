package org.xxpay.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xxpay.shop.dao.mapper.GoodsOrderMapper;
import org.xxpay.shop.dao.mapper.SelfPickupMapper;
import org.xxpay.shop.dao.model.GoodsOrder;
import org.xxpay.shop.dao.model.GoodsOrderExample;
import org.xxpay.shop.dao.model.SelfPickup;
import org.xxpay.shop.dao.model.SelfPickupExample;
import org.xxpay.shop.util.Constant;

import java.util.List;

/**
 * Created by dingzhiwei on 17/6/2.
 */
@Component
public class GoodsOrderService {

    @Autowired
    private GoodsOrderMapper goodsOrderMapper;

    @Autowired
    private SelfPickupMapper selfPickupMapper;

   /*
    `Status` tinyint(6) NOT NULL DEFAULT '0' COMMENT '订单状态,订单生成(0),支付成功(1),处理完成(2),处理失败(-1)',
    */

    public int addGoodsOrder(GoodsOrder goodsOrder) {
        return goodsOrderMapper.insertSelective(goodsOrder);
    }

    public GoodsOrder getGoodsOrder(String goodsOrderId) {
        return goodsOrderMapper.selectByPrimaryKey(goodsOrderId);
    }

//    public List<GoodsOrder> getGoodsOrder(String orderNo24) {
//        return goodsOrderMapper.selectByExample(orderNo24);
//    }

    public List<SelfPickup> getSelfPickupList(String assetId, String goodsId, String BuyerId){
        SelfPickupExample example = new SelfPickupExample();
        SelfPickupExample.Criteria cri = example.createCriteria();
        cri.andAssetIdEqualTo(assetId);
        cri.andGoodsIdEqualTo(goodsId);
        cri.andBuyerIDEqualTo(BuyerId);

        return selfPickupMapper.selectByExample(example);
    }

    public int getSelfPickupCount(String assetId, String goodsId){
        SelfPickupExample example = new SelfPickupExample();
        SelfPickupExample.Criteria cri = example.createCriteria();
        cri.andAssetIdEqualTo(assetId);
        cri.andGoodsIdEqualTo(goodsId);
        cri.andIsPickUpEqualTo("0");
        return selfPickupMapper.countByExample(example);
    }

    public int updateStatus4Success(String goodsOrderId) {
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_SUCCESS);
        GoodsOrderExample example = new GoodsOrderExample();
        GoodsOrderExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsOrderIdEqualTo(goodsOrderId);
        criteria.andStatusEqualTo(Constant.GOODS_ORDER_STATUS_INIT);
        return goodsOrderMapper.updateByExampleSelective(goodsOrder, example);
    }

    public int updateStatus4Complete(String goodsOrderId) {
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_COMPLETE);
        GoodsOrderExample example = new GoodsOrderExample();
        GoodsOrderExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsOrderIdEqualTo(goodsOrderId);
        criteria.andStatusEqualTo(Constant.GOODS_ORDER_STATUS_SUCCESS);
        return goodsOrderMapper.updateByExampleSelective(goodsOrder, example);
    }

    public int updateStatus4Fail(String goodsOrderId) {
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_FAIL);
        GoodsOrderExample example = new GoodsOrderExample();
        GoodsOrderExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsOrderIdEqualTo(goodsOrderId);
        //criteria.andStatusEqualTo(Constant.GOODS_ORDER_STATUS_SUCCESS);
        return goodsOrderMapper.updateByExampleSelective(goodsOrder, example);
    }

    public int update(GoodsOrder goodsOrder) {
        return goodsOrderMapper.updateByPrimaryKeySelective(goodsOrder);
    }

}
