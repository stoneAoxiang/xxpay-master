package org.xxpay.shop.controller;

import cn.com.inhand.openserver.service.InhandService;
import cn.com.inhand.openserver.util.Auto24ServiceUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.xxpay.common.constant.PayConstant;
import org.xxpay.common.util.*;
import org.xxpay.shop.dao.model.GoodsOrder;
import org.xxpay.shop.dao.model.SelfPickup;
import org.xxpay.shop.enums.DeliverStatusEnum;
import org.xxpay.shop.service.GoodsOrderService;
import org.xxpay.shop.util.Constant;
import org.xxpay.shop.util.ali.AliApi;
import org.xxpay.shop.util.vx.WxApi;
import org.xxpay.shop.util.vx.WxApiClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

//@Controller
@RestController
@RequestMapping("/goods")
public class GoodsOrderController {

    private final static MyLog _log = MyLog.getLog(GoodsOrderController.class);

    @Autowired
    private GoodsOrderService goodsOrderService;

    @Autowired
    private InhandService inhandService;

    @Autowired
    private AliApi aliApi;

    static final String mchId = "10000000";
    // 加签key
    static final String reqKey = "M86l522AV6q613Ii4W6u8K48uW8vM1N6bFgyv769220MdYe9u37N4y7rI5mQ";
    // 验签key
    static final String resKey = "Hpcl522AV6q613KIi46u6g6XuW8vM1N8bFgyv769770MdYe9u37M4y7rIpl8";

    static final String baseUrl = "http://auto24test.tianu.cn:3020/api";

//    static final String notifyUrl = "http://auto24test.tianu.cn:8081/goods/payNotify";
    static final String notifyUrl = "https://auto24test.tianu.cn/goods/payNotify";
    private AtomicLong seq = new AtomicLong(0L);

//    private final static String QR_PAY_URL = "http://auto24test.tianu.cn:8081/goods/qrPay.html";
    private final static String QR_PAY_URL = "https://auto24test.tianu.cn/goods/qrPay.html";

    static final String AppID = "wx1f7a132d0ecb66ad"; // 天友微信开发者ID
    static final String AppSecret = "a8ce0c98f2ea78c53542b360a02b5b5d"; // 天友微信开发者密码

//    private final static String GetOpenIdURL = "http://auto24test.tianu.cn:8081/goods/getOpenId";
//    private final static String GetUserIdURL = "http://auto24test.tianu.cn:8081/goods/getUserId";
    private final static String GetOpenIdURL = "https://auto24test.tianu.cn/goods/getOpenId";
    private final static String GetUserIdURL = "https://auto24test.tianu.cn/goods/getUserId";

    @RequestMapping(value = "/getSelfPickup", method = RequestMethod.GET)
    public void getSelfPickup(){
        List<SelfPickup>  selfPickups = goodsOrderService.getSelfPickupList("TY1905S0031", "6900404519259", "2088102180569783");
        for(SelfPickup s : selfPickups){
            _log.info("机器:{} 购买用户:{} 提货状态:{}", s.getAssetId(), s.getBuyerID(), s.getIsPickUp().equals("0")? "未提货": "已提货");
        }

        _log.info("{}机器上的商品{} 未取货数{}", "TY1905S0031", "6900404519259", goodsOrderService.getSelfPickupCount("TY1905S0031", "6900404519259"));

        _log.info("{}机器上的商品{} d库存数{}","TY1905S0031", "6900404519259", inhandService.getGoodsStock("TY1905S0031", "6900404519259"));
    }

    @RequestMapping(value = "/buy/{goodsId}", method = RequestMethod.GET)
    @ResponseBody
    public String buy(@PathVariable("goodsId") String goodsId) {
        if(!"G_0001".equals(goodsId)) {
            return "fail";
        }
        String goodsOrderId = String.format("%s%s%06d", "G", DateUtil.getSeqString(), (int) seq.getAndIncrement() % 1000000);
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setGoodsId(goodsId);
        goodsOrder.setGoodsName("XXPAY捐助商品G_0001");
        goodsOrder.setAmount(1l);
        goodsOrder.setUserId("xxpay_000001");
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_INIT);
        int result = goodsOrderService.addGoodsOrder(goodsOrder);
        _log.info("插入商品订单,返回:{}", result);
        return result+"";
    }

    @RequestMapping(value = "/pay/{goodsOrderId}", method = RequestMethod.GET)
    @ResponseBody
    public String pay(@PathVariable("goodsOrderId") String goodsOrderId) {
        GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(goodsOrderId);
        if(goodsOrder == null) return "fail";
        int status = goodsOrder.getStatus();
        if(status != Constant.GOODS_ORDER_STATUS_INIT) {
            return "fail_001";
        }
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                       // 商户ID
        paramMap.put("mchOrderNo", goodsOrderId);           // 商户订单号
        paramMap.put("channelId", "ALIPAY_WAP");             // 支付渠道ID, WX_NATIVE,ALIPAY_WAP
        paramMap.put("amount", goodsOrder.getAmount());                          // 支付金额,单位分
        paramMap.put("currency", "cny");                    // 币种, cny-人民币
        paramMap.put("clientIp", "114.112.124.236");        // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                      // 设备
        paramMap.put("subject", goodsOrder.getGoodsName());
        paramMap.put("body", goodsOrder.getGoodsName());
        paramMap.put("notifyUrl", notifyUrl);         // 回调URL
        paramMap.put("param1", "");                         // 扩展参数1
        paramMap.put("param2", "");                         // 扩展参数2
        paramMap.put("extra", "{\"productId\":\"120989823\",\"openId\":\"o2RvowBf7sOVJf8kJksUEMceaDqo\"}");  // 附加参数

        String reqSign = PayDigestUtil.getSign(paramMap, reqKey);
        _log.info("根据签名规则得到签名:{}", reqSign);
        paramMap.put("sign", reqSign);   // 签名
        String reqData = "params=" + paramMap.toJSONString();
        _log.info("->pay 请求支付中心下单接口,请求数据:" + reqData);
        String url = baseUrl + "/pay/create_order?";
        String result = XXPayUtil.call4Post(url + reqData);
        _log.info("请求支付中心下单接口,响应数据:" + result);
        Map retMap = JSON.parseObject(result);
        if("SUCCESS".equals(retMap.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, resKey, "sign", "payParams");
            String retSign = (String) retMap.get("sign");
            if(checkSign.equals(retSign)) {
                _log.info("=========支付中心下单验签成功=========");
            }else {
                _log.info("=========支付中心下单验签失败=========");
                return null;
            }
        }
        String payOrderId = retMap.get("payOrderId").toString();

        goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setPayOrderId(payOrderId);
        goodsOrder.setChannelId("ALIPAY_WAP");
        int ret = goodsOrderService.update(goodsOrder);
        _log.info("修改商品订单,返回:{}", ret);
        return result+"";
    }

    private Map createPayOrder(GoodsOrder goodsOrder, Map<String, Object> params) {
        JSONObject paramMap = new JSONObject();
        paramMap.put("mchId", mchId);                       // 商户ID
        paramMap.put("mchOrderNo", goodsOrder.getGoodsOrderId());           // 商户订单号
        paramMap.put("channelId", params.get("channelId"));             // 支付渠道ID, WX_NATIVE,ALIPAY_WAP
        paramMap.put("amount", goodsOrder.getAmount());                          // 支付金额,单位分
        paramMap.put("currency", "cny");                    // 币种, cny-人民币
        paramMap.put("clientIp", "114.112.124.236");        // 用户地址,IP或手机号
        paramMap.put("device", "WEB");                      // 设备
        paramMap.put("subject", goodsOrder.getGoodsName());
        paramMap.put("body", goodsOrder.getGoodsName());
//        paramMap.put("notifyUrl", notifyUrl + "?autoNotifyUrl="+ params.get("autoNotifyUrl"));         // 回调URL
        paramMap.put("notifyUrl", notifyUrl);         // 回调URL
        paramMap.put("param1", "");                         // 扩展参数1
        paramMap.put("param2", "");                         // 扩展参数2

        //以下2个参数为新增
        paramMap.put("autoNotifyUrl", params.get("autoNotifyUrl"));         //24小时通知出货URL
        paramMap.put("orderNo24", params.get("orderNo24"));         //24服务端生成的订单号
        _log.info("autoNotifyUrl：{} orderNo24:{}", params.get("autoNotifyUrl"), params.get("orderNo24"));

        JSONObject extra = new JSONObject();
        if(params.get("channelId").toString().contains("WX")){
            extra.put("openId", params.get("openId"));
        }else {
            extra.put("userId", params.get("userId"));
        }

        paramMap.put("extra", extra.toJSONString());  // 附加参数

        String reqSign = PayDigestUtil.getSign(paramMap, reqKey);
        paramMap.put("sign", reqSign);   // 签名
        _log.info("根据签名规则得到签名:{}", reqSign);
        String reqData = "params=" + paramMap.toJSONString();
        _log.info("请求支付中心下单接口,请求数据:{}", reqData);
        String url = baseUrl + "/pay/create_order?";
        String result = XXPayUtil.call4Post(url + reqData);
        _log.info("请求支付中心下单接口,响应数据:{}", result);
        Map retMap = JSON.parseObject(result);
        _log.info("支付中心下单签名:{}", retMap.get("sign"));
        if("SUCCESS".equals(retMap.get("retCode"))) {
            // 验签
            String checkSign = PayDigestUtil.getSign(retMap, resKey, "sign", "payParams");
            String retSign = (String) retMap.get("sign");
            if(checkSign.equals(retSign)) {
                _log.info("=========支付中心下单验签成功=========");
            }else {
                _log.info("=========支付中心下单验签失败=========");
                return null;
            }
        }
        return retMap;
    }

    @RequestMapping(value = "/outStockNotify")
    @ApiOperation(value = "出货结果通知")
    public String outStockNotify(@ApiParam(value = "身份标识", required = true) String clientId,
                            @ApiParam(value = "机器编号", required = true) String assetId,
                            @ApiParam(value = "订单编号", required = true) String orderNo,
                            @ApiParam(value = "出货状态", required = true) String deliverStatus,
                            @ApiParam(value = "随机数", required = true) String nonce_str,
                            @ApiParam(value = "签名", required = true) String sign) {

        //订单出货状态为5种，0:出货成功 2:出货失败,售货机库存不足 3:出货失败 4:出货失败,网络原因 5:出货失败,支付超时,超过了支付等待时间
        for (DeliverStatusEnum outStatus : DeliverStatusEnum.values()) {
            if (outStatus.getIndex() == Integer.valueOf(deliverStatus)) {
                _log.info("出货状态:{}", outStatus.toString());
            }
        }
        return "";
    }

    public static void main(String[] args){

//        //API的请求消息内容
//        Map<String,String> body = new HashMap<String,String>();
//        body.put("clientId", "2066180802347565");
//        body.put("assetId", "TY1905S0031");
//        body.put("nonce_str", "12124dsfer");
//
//        // API的请求参数信息
//        Map<String,String> parameter = new HashMap<String,String>();
//        parameter.put("limit", "30");
//        parameter.put("cursor", "0");
//
//        String result = Auto24ServiceUtil.request24Service(body, auto24Url + "/router/status", parameter);
//        _log.info("获取商品库存:{}", result);



        //API的请求消息内容
//        Map<String,String> body = new HashMap<String,String>();
//        body.put("payStatus", "0");
//
//        String result = Auto24ServiceUtil.request24Service(body, "http://www.dfbs-vm.com/api/pay/vipCommon/payback/5b3c66d593bd37030400039f/TY1905S0031/TY1905S0031-000317-1583393197804104", null);
//        _log.info("通知24服务端出货结果:{}", result);
    }

//    @RequestMapping("/openQrPay.html")
//    public String openQrPay(ModelMap model) {
//        _log.info("====== 打开openQrPay页面 ======");
//        return "openQrPay";
//    }

    @RequestMapping("/test.html")
    public ModelAndView test(ModelMap model) {
        _log.info("====== testtesttest ======");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("test");
        return modelAndView;
    }

    @RequestMapping("/openQrPay.html")
    public ModelAndView openQrPay(ModelMap model) {
        _log.info("====== 打开openQrPay页面 ======");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("openQrPay");
        return modelAndView;
    }

//    public String qrPay(ModelMap model, HttpServletRequest request, Long amount) {
//public ModelAndView qrPay(ModelMap model, HttpServletRequest request, HttpServletResponse response, String assetId, String goodsName, String price, String productNum) {

    /**
     * 第一次调用：用户扫码后通过 “控制器/openQrPay.html”调用该方法，传入二维码参数如下：
     * assetId=TY1905S0031&goodsName=200ml&productNum=6900404519259&price=2&orderNo=TY1905S0031-000317-1583393197804104&notify_url=http://www.dfbs-vm.com/api/pay/vipCommon/payback/5b3c66d593bd37030400039f/TY1905S0031/TY1905S0031-000317-1583393197804104
     * 该二维码参数“orderNo”为24服务端生成的订单号，对应本服务端的“OrderNo24”；“notify_url”为支付成功后调用24服务端的回调地址，对应本服务端的“AutoNotifyUrl”
     * 第二次调用：本方法对扫码用户的身份进行获取，如果没有得到用户身份，会重定向请求“getOpenId”或“getUserId”，获取到用户信息后，又重定向到本方法。由于本服务端的订单表中存在了
     * “orderNo”（本服务端生成的订单号）和“notify_url”（调用支付成功后，通知本服务的回调）字段，为了和24服务端进行区分，“orderNo”用“OrderNo24”替换，
     * “notify_url”用“AutoNotifyUrl”替换
     * 对应第一次调用，autoNotifyUrl通过“notify_url”参数取得； OrderNo24通过“orderNo”参数取得
     * 对应第二次调用，OrderNo24“orderNo”参数取得； OrderNo24通过“OrderNo24”参数取得
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/qrPay.html")
    public ModelAndView qrPay(ModelMap model, HttpServletRequest request, HttpServletResponse response) {

        ModelAndView modelAndView = new ModelAndView();
        Map<String, String> orderMap = null;
        //机器编号
        String assetId = request.getParameter("assetId");
        String goodsName = request.getParameter("goodsName");
        //商品编号
        String productNum = request.getParameter("productNum");
        String price = request.getParameter("price");
        if(null == price || price.equals("")){
            price = request.getParameter("amount");
        }

        //对应第一次调用
        String orderNo24 = request.getParameter("orderNo");
        //对应第二次调用
        if(null == orderNo24 || orderNo24.equals("")){
            orderNo24 = request.getParameter("orderNo24");
        }

        //对应第一次调用
        String autoNotifyUrl = request.getParameter("notify_url");
        //对应第二次调用
        if(null == autoNotifyUrl || autoNotifyUrl.equals("")){
            autoNotifyUrl = request.getParameter("autoNotifyUrl");
        }

        String logPrefix = "【二维码扫码支付】";
        String view = "qrPay";
        _log.info("====== 开始接收二维码扫码支付请求 ======");
        String ua = request.getHeader("User-Agent");
//        String goodsId = "G_0001";
        String goodsId = productNum;
        _log.info("{}接收参数:goodsId={},amount={},ua={}", logPrefix, goodsId, price, ua);
        String client = null;
        String channelId = "ALIPAY_WAP";
        if(StringUtils.isBlank(ua)) {
            String errorMessage = "User-Agent为空！";
            _log.info("{}信息：{}", logPrefix, errorMessage);
            model.put("result", "failed");
            model.put("resMsg", errorMessage);
            modelAndView.setViewName(view);
            return modelAndView;
//            return view;
        }else {
            if(ua.contains("Alipay")) {
                client = "alipay";
                channelId = "ALIPAY_WAP";
            }else if(ua.contains("MicroMessenger")) {
                client = "wx";
                channelId = "WX_JSAPI";
            }
        }
        if(client == null) {
            String errorMessage = "请用微信或支付宝扫码";
            _log.info("{}信息：{}", logPrefix, errorMessage);
            model.put("result", "failed");
            model.put("resMsg", errorMessage);
//            return view;
            modelAndView.setViewName(view);
            return modelAndView;
        }
        // 先插入订单数据
        GoodsOrder goodsOrder = null;

//        String redirectUrl = QR_PAY_URL + "?amount=" + Long.valueOf(price) + "&assetId=" + assetId + "&goodsName="
//                + goodsName + "&productNum=" + productNum + "&orderNo=" + orderNo + "&autoNotifyUrl=" + autoNotifyUrl;
        String redirectUrl = QR_PAY_URL + "?amount=" + Long.valueOf(price) + "&assetId=" + assetId + "&goodsName="
                + goodsName + "&productNum=" + productNum + "&orderNo24=" + orderNo24 + "&autoNotifyUrl=" + autoNotifyUrl;

        if ("alipay".equals(client)) {
            _log.info("{}{}扫码下单", logPrefix, "支付宝");
            // 判断是否拿到user_id，如果没有则去获取
            String userId = request.getParameter("userId");
            if (StringUtils.isNotBlank(userId)) {

                //1.通过userId assetId goodsId 查询中台的订单:
                //    ①:assetId goodsId查询自提订单数  ②:userId assetId productNum查询是否有未提货的订单
                //2.通过assetId goodsId去24服务端查询机器中商品的库存
                //3.判断是否走支付流程或是否售卖
                //    没有未提货订单的用户:
                //        ①:库存数小于等于自提订单数,不走"执行支付流程",并向用户返回"该商品不参与售卖"
                //        ②:库存数大于自提订单数,则走"执行支付流程"
                //    有未提货订单的用户:
                //        ①:不走"执行支付流程", 直接通知24小时服务端出货

                //24服务端查询机器中商品的库存
                String goodsStock = inhandService.getGoodsStock(assetId, goodsId);

                //查询中台是否有未提货的订单
                String pickpuStatus = "0";
                List<SelfPickup>  selfPickups = goodsOrderService.getSelfPickupList(assetId, goodsId, userId);
                if(!selfPickups.isEmpty()){
                    pickpuStatus = selfPickups.get(0).getIsPickUp();
                }else{
                    pickpuStatus = "-1";
                }

                //查询中台自提订单数
                int pickupCount = goodsOrderService.getSelfPickupCount(assetId, goodsId);

                _log.info("机器:{} 购买用户:{} 提货状态:{}", assetId, userId, pickpuStatus.equals("0") ? "未提货": !pickpuStatus.equals("-1") ? "已提货": "无提货单");
                _log.info("{}机器上的商品{} 未取货数{}", assetId, goodsId, pickupCount);
                _log.info("{}机器上的商品{} 库存数{}",assetId, goodsId, goodsStock);


                //没有未提货订单的用户
                if(!pickpuStatus.equals("0")){
                    if(Integer.valueOf(goodsStock) <= pickupCount){
                        String errorMessage = "该商品不参与售卖";
                        orderMap = new HashMap<String, String>();
                        orderMap.put("resCode", "FALSE");
                        model.put("orderMap", orderMap);
                        _log.info("{}信息：{}", logPrefix, errorMessage);
                        model.put("result", "failed");
                        model.put("resMsg", errorMessage);
                        modelAndView.setViewName(view);
                        return modelAndView;
                    }
                }else if(pickpuStatus.equals("0")) {
                    _log.info("======  不走\"执行支付流程\", 直接通知24小时服务端出货 ======{}", autoNotifyUrl);
                    //API的请求消息内容
                    Map<String,String> body = new HashMap<String,String>();
                    body.put("payStatus", "0");
                    String result = Auto24ServiceUtil.request24Service(body, autoNotifyUrl, null);
                    _log.info("通知24服务端出货结果:{}", result);

                    String errorMessage = "已通知24服务端出货";
                    orderMap = new HashMap<String, String>();
                    orderMap.put("resCode", "FALSE");
                    model.put("orderMap", orderMap);
                    _log.info("{}信息：{}", logPrefix, errorMessage);
                    model.put("result", "failed");
                    model.put("resMsg", errorMessage);
                    modelAndView.setViewName(view);
                    return modelAndView;
                }


                //==============================执行支付流程==================================
                _log.info("{}支付宝已获取userId:{}", logPrefix, userId);
                Map params = new HashMap<>();
                params.put("userId", userId);
                params.put("channelId", channelId);
                params.put("autoNotifyUrl", autoNotifyUrl);
                params.put("orderNo24", orderNo24);
                goodsOrder = createGoodsOrder(goodsId, Long.valueOf(price));
                _log.info("{}24服务端生成的订单:{}", logPrefix, orderNo24);
                orderMap = createPayOrder(goodsOrder, params);
            }
            else{
                _log.info("{}支付宝未获取到userId", logPrefix);
                String url = GetUserIdURL + "?redirectUrl=" + redirectUrl;
                _log.info("{}支付宝未获取到userId 重定向到URL={}获取userId", logPrefix, url);
                try {
                    response.sendRedirect(url);
                    return null;
                } catch (IOException e) {
                    _log.info("跳转URL失败={}", url);
                    e.printStackTrace();
                }
            }

        }else if("wx".equals(client)){
            _log.info("{}{}扫码", logPrefix, "微信");
            // 判断是否拿到openid，如果没有则去获取
            String openId = request.getParameter("openId");
            _log.info("{}微信openId", request.getParameter("openId"));
            if (StringUtils.isNotBlank(openId)) {

                //1.通过userId assetId goodsId 查询中台的订单:
                //    ①:assetId goodsId查询自提订单数  ②:userId assetId productNum查询是否有未提货的订单
                //2.通过assetId goodsId去24服务端查询机器中商品的库存
                //3.判断是否走支付流程或是否售卖
                //    没有未提货订单的用户:
                //        ①:库存数小于等于自提订单数,不走"执行支付流程",并向用户返回"该商品不参与售卖"
                //        ②:库存数大于自提订单数,则走"执行支付流程"
                //    有未提货订单的用户:
                //        ①:不走"执行支付流程", 直接通知24小时服务端出货

                //24服务端查询机器中商品的库存
                String goodsStock = inhandService.getGoodsStock(assetId, goodsId);

                //查询中台是否有未提货的订单
                String pickpuStatus = "0";
                List<SelfPickup>  selfPickups = goodsOrderService.getSelfPickupList(assetId, goodsId, openId);
                if(!selfPickups.isEmpty()){
                    pickpuStatus = selfPickups.get(0).getIsPickUp();
                }else{
                    pickpuStatus = "-1";
                }

                //查询中台自提订单数
                int pickupCount = goodsOrderService.getSelfPickupCount(assetId, goodsId);

                _log.info("机器:{} 购买用户:{} 提货状态:{}", assetId, openId, pickpuStatus.equals("0") ? "未提货": !pickpuStatus.equals("-1") ? "已提货": "无提货单");
                _log.info("{}机器上的商品{} 未取货数{}", assetId, goodsId, pickupCount);
                _log.info("{}机器上的商品{} 库存数{}",assetId, goodsId, goodsStock);


                //没有未提货订单的用户
                if(!pickpuStatus.equals("0")){
                    if(Integer.valueOf(goodsStock) <= pickupCount){
                        String errorMessage = "该商品不参与售卖";
                        orderMap = new HashMap<String, String>();
                        orderMap.put("resCode", "FALSE");
                        model.put("orderMap", orderMap);
                        _log.info("{}信息：{}", logPrefix, errorMessage);
                        model.put("result", "failed");
                        model.put("resMsg", errorMessage);
                        modelAndView.setViewName(view);
                        return modelAndView;
                    }
                }else if(pickpuStatus.equals("0")) {
                    _log.info("======  不走\"执行支付流程\", 直接通知24小时服务端出货 ======{}", autoNotifyUrl);
                    //API的请求消息内容
                    Map<String,String> body = new HashMap<String,String>();
                    body.put("payStatus", "0");
                    String result = Auto24ServiceUtil.request24Service(body, autoNotifyUrl, null);
                    _log.info("通知24服务端出货结果:{}", result);

                    String errorMessage = "已通知24服务端出货";
                    orderMap = new HashMap<String, String>();
                    orderMap.put("resCode", "FALSE");
                    model.put("orderMap", orderMap);
                    _log.info("{}信息：{}", logPrefix, errorMessage);
                    model.put("result", "failed");
                    model.put("resMsg", errorMessage);
                    modelAndView.setViewName(view);
                    return modelAndView;
                }

                //==============================执行支付流程==================================
                _log.info("{}openId：{}", logPrefix, openId);
                Map params = new HashMap<>();
                params.put("channelId", channelId);
                params.put("openId", openId);
                params.put("autoNotifyUrl", autoNotifyUrl);
                params.put("orderNo24", orderNo24);
                goodsOrder = createGoodsOrder(goodsId, Long.valueOf(price));
                // 下单
                orderMap = createPayOrder(goodsOrder, params);
            }else {
//                String redirectUrl = QR_PAY_URL + "?amount=" + Long.valueOf(price);
                String url = GetOpenIdURL + "?redirectUrl=" + redirectUrl;
                _log.info("跳转URL={}", url);
                try {
                    response.sendRedirect(url);
                    return null;
                } catch (IOException e) {
                    _log.info("跳转URL失败={}", url);
                    e.printStackTrace();
                }
            }
        }
        model.put("goodsOrder", goodsOrder);
        model.put("amount", AmountUtil.convertCent2Dollar(goodsOrder.getAmount()+""));
        if(orderMap != null) {
            model.put("orderMap", orderMap);
            String payOrderId = orderMap.get("payOrderId");
            GoodsOrder go = new GoodsOrder();
            go.setGoodsOrderId(goodsOrder.getGoodsOrderId());
            go.setPayOrderId(payOrderId);
            go.setChannelId(channelId);
            int ret = goodsOrderService.update(go);
            _log.info("修改商品订单,返回:{}", ret);
        }
        model.put("client", client);
        model.put("autoNotifyUrl", autoNotifyUrl);
        modelAndView.setViewName(view);
        return modelAndView;
    }

//    GoodsOrder createGoodsOrder(String goodsId, Long amount, String goodsName) {
    GoodsOrder createGoodsOrder(String goodsId, Long amount) {
        // 先插入订单数据
        String goodsOrderId =  String.format("%s%s%06d", "G", DateUtil.getSeqString(), (int) seq.getAndIncrement() % 1000000);
        GoodsOrder goodsOrder = new GoodsOrder();
        goodsOrder.setGoodsOrderId(goodsOrderId);
        goodsOrder.setGoodsId(goodsId);
        goodsOrder.setGoodsName("G_0001");
//        goodsOrder.setGoodsName(goodsName);
        goodsOrder.setAmount(amount);
        goodsOrder.setUserId("xxpay_000001");
        goodsOrder.setStatus(Constant.GOODS_ORDER_STATUS_INIT);
        int result = goodsOrderService.addGoodsOrder(goodsOrder);
        _log.info("插入商品订单,返回:{}", result);
        return goodsOrder;
    }

    /**
     * 获取code 过程见文档:https://developers.weixin.qq.com/doc/offiaccount/OA_Web_Apps/Wechat_webpage_authorization.html
     * 第一步：用户同意授权，获取code 如果用户同意授权，页面将跳转至 redirect_uri/?code=CODE&state=STATE
     * 第二步：通过code换取网页授权access_token 网页授权的作用域为snsapi_base，则本步骤中获取到网页授权access_token的同时，
     * 也获取到了openid，snsapi_base式的网页授权流程即到此为止
     * @return
     */
    @RequestMapping("/getOpenId")
    public void getOpenId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String logPrefix = "【获取微信用户openId】";
        String redirectUrl = request.getParameter("redirectUrl");
        _log.info("{}重定向URL:{}", logPrefix, redirectUrl);

        String code = request.getParameter("code");
        String openId = "";

        Enumeration<String> paramNames = request.getParameterNames();
        StringBuilder urlWithParams = new StringBuilder();
        while(paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if(!paramName.equals("redirectUrl")){
                urlWithParams.append("&");
                urlWithParams.append(paramName).append("=").append(request.getParameter(paramName));
            }

        }
        redirectUrl += urlWithParams;
        _log.info("{}组装重定向redirectUrl:{}", logPrefix, redirectUrl);

        if(!StringUtils.isBlank(code)){//如果request中包括code，则是微信回调
            try {
                //第二步 网页授权的作用域为snsapi_base，则本步骤中获取到网页授权access_token的同时，也获取到了openid
                openId = WxApiClient.getOAuthOpenId(AppID, AppSecret, code);
                if(null == openId){
                    _log.info("网页授权未渠道微信返回openId");
                    return;
                }
                _log.info("调用微信返回openId={}", openId);
            } catch (Exception e) {
                _log.error(e, "调用微信查询openId异常");
            }
            if(redirectUrl.indexOf("?") > 0) {
                redirectUrl += "&openId=" + openId;
            }else {
                redirectUrl += "?openId=" + openId;
            }
            response.sendRedirect(redirectUrl);
        }else{//第一步 oauth获取code
            String redirectUrl4Vx = GetOpenIdURL + "?redirectUrl=" + redirectUrl;
            _log.info("{} 获取用户授权code后的回调地址{}", logPrefix, redirectUrl4Vx);
//            String state = OAuth2RequestParamHelper.prepareState(request);
//            _log.info("state={}",  state);
            // 网页授权的作用域为snsapi_base
            String url = WxApi.getOAuthCodeUrl(AppID, redirectUrl4Vx, "snsapi_base", "default");
            _log.info("{} 用户授权获取code的跳转URL:{}", logPrefix, url);
            response.sendRedirect(url);
        }
    }

    /**
     * 获取支付宝code 过程见文档:https://docs.open.alipay.com/289/105656 或 https://opendocs.alipay.com/open/263/105809
     * 第一步：URL拼接 授权回调地址，是经过URLENCODE转义 的url链接（url必须以http或者https开头）； 在请求之前，开发者需要先到开发者中心对应应用内，配置授权回调地址。
     *        redirect_uri与应用配置的授权回调地址域名部分必须一致
     *        url拼接规则：https://openauth.alipay.com/oauth2/publicAppAuthorize.htm?app_id=APPID&scope=SCOPE&redirect_uri=ENCODED_URL
     * 第二步：获取auth_code 当用户授权成功后，会跳转至开发者定义的回调页面，支付宝会在回调页面请求中加入参数，包括auth_code、app_id、scope等，支付宝请求开发者回调页面示例如下：
     *        http或https打头的授权回调地址? app_id=2016032301002387 &scope=auth_user&auth_code=10e20498fe5d42f18427d893fc06WX59
     * @return
     */
    @RequestMapping("/getUserId")
    public void getUserId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String logPrefix = "【获取支付宝用户userID】";
        _log.info("=============={}", logPrefix, "==============");
        String redirectUrl = request.getParameter("redirectUrl");
        _log.info("{}重定向URL:{}", logPrefix, redirectUrl);

        String auth_code = request.getParameter("auth_code");
        String userId = "";

        Enumeration<String> paramNames = request.getParameterNames();
        StringBuilder urlWithParams = new StringBuilder();
        while(paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if(!paramName.equals("redirectUrl")){
                urlWithParams.append("&");
                urlWithParams.append(paramName).append("=").append(request.getParameter(paramName));
            }

        }
        redirectUrl += urlWithParams;
        _log.info("{}组装重定向redirectUrl:{}", logPrefix, redirectUrl);

        if(!StringUtils.isBlank(auth_code)){//如果request中包括code，则是支付宝回调
            try {
                userId = aliApi.getTokenUserId(auth_code, mchId, "ALIPAY_WAP");
                _log.info("{} 用户授权auth_code回调", logPrefix);
            } catch (Exception e) {
                _log.error(e, "{} userId异常", logPrefix);
            }
            if(redirectUrl.indexOf("?") > 0) {
                redirectUrl += "&userId=" + userId;
            }else {
                redirectUrl += "?userId=" + userId;
            }

            _log.info("{} 用户授权auth_code回调得到userId:{}", logPrefix, userId);
            _log.info("{} 再次回调二维码扫码支付请求:{}", logPrefix, redirectUrl);
            response.sendRedirect(redirectUrl);
        }else{//oauth获取code
            String redirectUrl4Ali = GetUserIdURL + "?redirectUrl=" + redirectUrl;
            _log.info("{} 获取用户授权auth_code的回调地址{}", logPrefix, redirectUrl4Ali );

            String url = aliApi.getOAuthCodeUrl("2016101900720773", redirectUrl4Ali, "auth_base");
            _log.info("{} 用户授权auth_code的跳转URL:{}", logPrefix, url);
            response.sendRedirect(url);

        }
    }

//    /**
//     * 获取code
//     * @return
//     */
//    @RequestMapping("/getOpenId2")
//    public void getOpenId2(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        _log.info("进入获取用户openID页面");
//        String redirectUrl = request.getParameter("redirectUrl");
//        _log.info("重定向URL:" + redirectUrl);
//        String code = request.getParameter("code");
//        String openId = "";
//        if(!StringUtils.isBlank(code)){//如果request中包括code，则是微信回调
//            try {
//                openId = WxApiClient.getOAuthOpenId(AppID, AppSecret, code);
//                _log.info("调用微信返回openId={}", openId);
//            } catch (Exception e) {
//                _log.error(e, "调用微信查询openId异常");
//            }
//            if(redirectUrl.indexOf("?") > 0) {
//                redirectUrl += "&openId=" + openId;
//            }else {
//                redirectUrl += "?openId=" + openId;
//            }
//            response.sendRedirect(redirectUrl);
//        }else{//oauth获取code
//            //http://www.abc.com/xxx/get-weixin-code.html?appid=XXXX&scope=snsapi_base&state=hello-world&redirect_uri=http%3A%2F%2Fwww.xyz.com%2Fhello-world.html
//            String redirectUrl4Vx = GetOpenIdURL2 + "?redirectUrl=" + redirectUrl;
//            _log.info("oauth获取code::::" + redirectUrl4Vx);
//            String url = String.format("http://www.xiaoshuding.com/get-weixin-code.html?appid=%s&scope=snsapi_base&state=hello-world&redirect_uri=%s", AppID, WxApi.urlEnodeUTF8(redirectUrl4Vx));
//            _log.info("跳转URL={}", url);
//            response.sendRedirect(url);
//        }
//    }

    /**
     * 接收支付中心通知
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping("/payNotify")
    public void payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        _log.info("====== 开始处理支付中心通知 ======");
        Map<String,Object> paramMap = request2payResponseMap(request, new String[]{
                "orderNo24", "payOrderId","mchId","mchOrderNo","channelId","autoNotifyUrl","amount","currency","status", "clientIp",
                "device",  "subject", "channelOrderNo", "param1",
                "param2","paySuccTime","backType","sign"
        });
        _log.info("支付中心通知请求参数,paramMap={}", paramMap);
        if (!verifyPayResponse(paramMap)) {
            String errorMessage = "verify request param failed.";
            _log.warn(errorMessage);
            outResult(response, "fail");
            return;
        }
        String payOrderId = (String) paramMap.get("payOrderId");
        String mchOrderNo = (String) paramMap.get("mchOrderNo");
        String resStr;
        try {
            GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(mchOrderNo);
            if(goodsOrder != null && goodsOrder.getStatus() == Constant.GOODS_ORDER_STATUS_COMPLETE) {
                outResult(response, "success");
                return;
            }
            // 执行业务逻辑
            int ret = goodsOrderService.updateStatus4Success(mchOrderNo);
            // ret返回结果
            // 等于1表示处理成功,返回支付中心success
            // 其他值,返回支付中心fail,让稍后再通知
            if(ret == 1) {
                ret = goodsOrderService.updateStatus4Complete(mchOrderNo);
                if(ret == 1) {
                    resStr = "success";
                }else {
                    resStr = "fail";
                }
            }else {
                resStr = "fail";
            }
        }catch (Exception e) {
            resStr = "fail";
            _log.error(e, "执行业务异常,payOrderId=%s.mchOrderNo=%s", payOrderId, mchOrderNo);
        }
        _log.info("响应支付中心通知结果:{},payOrderId={},mchOrderNo={}", resStr, payOrderId, mchOrderNo);
        outResult(response, resStr);
        _log.info("====== 支付中心通知处理完成, ======");

        _log.info("======  准备通知24小时服务端出货 ======{}", (String) paramMap.get("autoNotifyUrl"));
        //API的请求消息内容
        Map<String,String> body = new HashMap<String,String>();
        body.put("payStatus", "0");
        String result = Auto24ServiceUtil.request24Service(body, (String) paramMap.get("autoNotifyUrl"), null);
        _log.info("通知24服务端出货结果:{}", result);

    }

    @RequestMapping("/notify_test")
    public void notifyTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        outResult(response, "支付成功");
    }

    @RequestMapping("/toAliPay.html")
    @ResponseBody
    public String toAliPay(HttpServletRequest request, Long amount, String channelId) {
        String logPrefix = "【支付宝支付】";
        _log.info("====== 开始接收支付宝支付请求 ======");
        String goodsId = "G_0001";
        _log.info("{}接收参数:goodsId={},amount={},channelId={}", logPrefix, goodsId, amount, channelId);
        // 先插入订单数据
        Map params = new HashMap<>();
        params.put("channelId", channelId);
        // 下单
        GoodsOrder goodsOrder = createGoodsOrder(goodsId, amount);
        Map<String, String> orderMap = createPayOrder(goodsOrder, params);
        if(orderMap != null && "success".equalsIgnoreCase(orderMap.get("resCode"))) {
            String payOrderId = orderMap.get("payOrderId");
            GoodsOrder go = new GoodsOrder();
            go.setGoodsOrderId(goodsOrder.getGoodsOrderId());
            go.setPayOrderId(payOrderId);
            go.setChannelId(channelId);
            int ret = goodsOrderService.update(go);
            _log.info("修改商品订单,返回:{}", ret);
        }
        if(PayConstant.PAY_CHANNEL_ALIPAY_MOBILE.equalsIgnoreCase(channelId)) return orderMap.get("payParams");
        return orderMap.get("payUrl");
    }

    void outResult(HttpServletResponse response, String content) {
        response.setContentType("text/html");
        PrintWriter pw;
        try {
            pw = response.getWriter();
            pw.print(content);
            _log.info("response pay complete.");
        } catch (IOException e) {
            _log.error(e, "response pay write exception.");
        }
    }

    public Map<String, Object> request2payResponseMap(HttpServletRequest request, String[] paramArray) {
        Map<String, Object> responseMap = new HashMap<>();
        for (int i = 0;i < paramArray.length; i++) {
            String key = paramArray[i];
            String v = request.getParameter(key);
            _log.info("key:{} vaule:{}", key, v);
            if (v != null) {
                responseMap.put(key, v);
            }
        }
        return responseMap;
    }

    public boolean verifyPayResponse(Map<String,Object> map) {
        String mchId = (String) map.get("mchId");
        String payOrderId = (String) map.get("payOrderId");
        String mchOrderNo = (String) map.get("mchOrderNo");
        String amount = (String) map.get("amount");
        String sign = (String) map.get("sign");

        if (StringUtils.isEmpty(mchId)) {
            _log.warn("Params error. mchId={}", mchId);
            return false;
        }
        if (StringUtils.isEmpty(payOrderId)) {
            _log.warn("Params error. payOrderId={}", payOrderId);
            return false;
        }
        if (StringUtils.isEmpty(amount) || !NumberUtils.isNumber(amount)) {
            _log.warn("Params error. amount={}", amount);
            return false;
        }
        if (StringUtils.isEmpty(sign)) {
            _log.warn("Params error. sign={}", sign);
            return false;
        }

        // 验证签名
        if (!verifySign(map)) {
            _log.warn("verify params sign failed. payOrderId={}", payOrderId);
            return false;
        }

        // 根据payOrderId查询业务订单,验证订单是否存在
        GoodsOrder goodsOrder = goodsOrderService.getGoodsOrder(mchOrderNo);
        if(goodsOrder == null) {
            _log.warn("业务订单不存在,payOrderId={},mchOrderNo={}", payOrderId, mchOrderNo);
            return false;
        }
        // 核对金额
        if(goodsOrder.getAmount() != Long.parseLong(amount)) {
            _log.warn("支付金额不一致,dbPayPrice={},payPrice={}", goodsOrder.getAmount(), amount);
            return false;
        }
        return true;
    }

    public boolean verifySign(Map<String, Object> map) {
        String mchId = (String) map.get("mchId");
        if(!this.mchId.equals(mchId)) return false;
        String localSign = PayDigestUtil.getSign(map, resKey, "sign");
        String sign = (String) map.get("sign");
        return localSign.equalsIgnoreCase(sign);
    }

}