package org.xxpay.boot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.xxpay.common.constant.PayConstant;
import org.xxpay.common.util.MyLog;
import org.xxpay.dal.dao.mapper.MchInfoMapper;
import org.xxpay.dal.dao.mapper.PayChannelMapper;
import org.xxpay.dal.dao.mapper.PayOrderMapper;
import org.xxpay.dal.dao.model.*;

import java.util.List;

/**
 * @author: dingzhiwei
 * @date: 17/9/9
 * @description:
 */
@Service
public class BaseService {
    private static final MyLog _log = MyLog.getLog(BaseService.class);

    @Autowired
    private PayOrderMapper payOrderMapper;

    @Autowired
    private MchInfoMapper mchInfoMapper;

    @Autowired
    private PayChannelMapper payChannelMapper;


    public MchInfo baseSelectMchInfo(String mchId) {
        return mchInfoMapper.selectByPrimaryKey(mchId);
    }

    public PayChannel baseSelectPayChannel(String mchId, String channelId) {
        PayChannelExample example = new PayChannelExample();
        PayChannelExample.Criteria criteria = example.createCriteria();
        criteria.andChannelIdEqualTo(channelId);
        criteria.andMchIdEqualTo(mchId);
        List<PayChannel> payChannelList = payChannelMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(payChannelList)) return null;
        return payChannelList.get(0);
    }

    /**
     * 根据24小时订单号获取记录
     * @param orderNo24
     * @return
     */
    public PayOrder baseSelectPayOrderByOrderNo24(String orderNo24){
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andOrderNo24EqualTo(orderNo24);
        List<PayOrder> payOrderList = payOrderMapper.selectByExample(example);
        _log.info(payOrderList.isEmpty() ? "空记录" : payOrderList.get(0).toString());
        return payOrderList.isEmpty() ? null : payOrderList.get(0);
//        return CollectionUtils.isEmpty(payOrderList) ? null : payOrderList.get(0);
    }

    public int baseCreatePayOrder(PayOrder payOrder) {
        _log.info("创建支付订单,向t_pay_order表中插入支付订单数据");
        return payOrderMapper.insertSelective(payOrder);
    }

    public PayOrder baseSelectPayOrder(String payOrderId) {
        return payOrderMapper.selectByPrimaryKey(payOrderId);
    }

    public PayOrder baseSelectPayOrderByMchIdAndPayOrderId(String mchId, String payOrderId) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andMchIdEqualTo(mchId);
        criteria.andPayOrderIdEqualTo(payOrderId);
        List<PayOrder> payOrderList = payOrderMapper.selectByExample(example);
        _log.info("创建支付订单AutoNotifyUrl:{}", payOrderList.get(0).getAutoNotifyUrl());
        return CollectionUtils.isEmpty(payOrderList) ? null : payOrderList.get(0);
    }

    public PayOrder baseSelectPayOrderByMchIdAndMchOrderNo(String mchId, String mchOrderNo) {
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andMchIdEqualTo(mchId);
        criteria.andMchOrderNoEqualTo(mchOrderNo);
        List<PayOrder> payOrderList = payOrderMapper.selectByExample(example);
        return CollectionUtils.isEmpty(payOrderList) ? null : payOrderList.get(0);
    }

    public int baseUpdateStatus4Ing(String payOrderId, String channelOrderNo) {
        _log.info("更新支付订单:{}的渠道订单号:{}", payOrderId, channelOrderNo);
        PayOrder payOrder = new PayOrder();
        payOrder.setStatus(PayConstant.PAY_STATUS_PAYING);
        if(channelOrderNo != null) payOrder.setChannelOrderNo(channelOrderNo);
        payOrder.setPaySuccTime(System.currentTimeMillis());
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andPayOrderIdEqualTo(payOrderId);
        criteria.andStatusEqualTo(PayConstant.PAY_STATUS_INIT);
        return payOrderMapper.updateByExampleSelective(payOrder, example);
    }

    public int baseUpdateStatus4Success(String payOrderId, String channelOrderNo) {
        PayOrder payOrder = new PayOrder();
        payOrder.setPayOrderId(payOrderId);
        payOrder.setStatus(PayConstant.PAY_STATUS_SUCCESS);
        if(channelOrderNo != null) payOrder.setChannelOrderNo(channelOrderNo);
        payOrder.setPaySuccTime(System.currentTimeMillis());
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andPayOrderIdEqualTo(payOrderId);
        criteria.andStatusEqualTo(PayConstant.PAY_STATUS_PAYING);
        return payOrderMapper.updateByExampleSelective(payOrder, example);
    }

    public int baseUpdateStatus4Complete(String payOrderId) {
        PayOrder payOrder = new PayOrder();
        payOrder.setPayOrderId(payOrderId);
        payOrder.setStatus(PayConstant.PAY_STATUS_COMPLETE);
        PayOrderExample example = new PayOrderExample();
        PayOrderExample.Criteria criteria = example.createCriteria();
        criteria.andPayOrderIdEqualTo(payOrderId);
        criteria.andStatusEqualTo(PayConstant.PAY_STATUS_SUCCESS);
        return payOrderMapper.updateByExampleSelective(payOrder, example);
    }

    public int baseUpdateNotify(String payOrderId, byte count) {
        PayOrder newPayOrder = new PayOrder();
        newPayOrder.setNotifyCount(count);
        newPayOrder.setLastNotifyTime(System.currentTimeMillis());
        newPayOrder.setPayOrderId(payOrderId);
        _log.info("修改支付订单通知次数,根据订单号：{}修改t_pay_order表中支付订单通知次数", payOrderId);
        return payOrderMapper.updateByPrimaryKeySelective(newPayOrder);
    }

    public int baseUpdateNotify(PayOrder payOrder) {
        return payOrderMapper.updateByPrimaryKeySelective(payOrder);
    }

}
