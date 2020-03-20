package org.xxpay.boot.service;

import org.xxpay.dal.dao.model.PayOrder;
import org.xxpay.dal.dao.model.RefundOrder;

import java.util.Map;

/**
 * @author: dingzhiwei
 * @date: 17/9/9
 * @description:
 */
public interface IPayChannel4WxService {

    Map doWxPayReq(String jsonParam);
    String doWxRefundOrderReq(RefundOrder refundOrder);

}
