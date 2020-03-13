package cn.com.inhand.openserver.service;

import cn.com.inhand.openserver.util.Auto24ServiceUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.xxpay.common.util.MyLog;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: InhandService
 * @author: aoxiang
 * @create: 2020/03/10 09:51
 **/

@Component
public class InhandService {
    private final MyLog _log = MyLog.getLog(InhandService.class);
    //24小时机器服务端根
    private final static String AUTO_24_URL ="http://www.dfbs-vm.com/osapi";
    // API秘钥 详情见: http://www.dfbs-vm.com/DeviceNetwork/applications/index.html#
    // 中的 系统->开发配置->开发者ID client_id参数
    private final static String CLIENT_ID ="2066180802347565";


    /**
     * 获取商品库存
     * @return
     */
    public String getGoodsStock(String assetId, String goodsId){

        //API的请求消息内容
        Map<String,String> body = new HashMap<String,String>();
        body.put("clientId", CLIENT_ID);
        body.put("assetId", assetId);
        body.put("nonce_str", String.valueOf(System.currentTimeMillis()));

        // API的请求参数信息
        Map<String,String> parameter = new HashMap<String,String>();
        parameter.put("limit", "100");
        parameter.put("cursor", "0");

        String result = Auto24ServiceUtil.request24Service(body, AUTO_24_URL + "/router/status", parameter);
        JSONObject jsonObject = JSONObject.parseObject(result);

        if(jsonObject.get("result").equals("SUCCESS")){
            JSONArray array = jsonObject.getJSONArray("list");
            JSONObject item = array.getJSONObject(0);

            JSONObject vendingInfo = item.getJSONObject("vendingInfo");
            JSONArray shelvesInfoArray = vendingInfo.getJSONArray("shelvesInfo");

            JSONObject shelvesInfoObj = shelvesInfoArray.getJSONObject(0);
            JSONArray shelvesArray = shelvesInfoObj.getJSONArray("shelves");

            for (int i = 0; i < shelvesArray.size(); i++) {
                JSONObject shelvesObj = shelvesArray.getJSONObject(i);
                String number = shelvesObj.getString("number");

                if(null != number && !number.isEmpty() && number.equals(goodsId)){
                    String goodsName = shelvesObj.getString("goodsName");
                    String stock = shelvesObj.getString("stock");
                    _log.info("商品名称:{} 库存:{}", goodsName, stock);
                    return stock;

                }

            }

        }else {
            _log.info("获取商品库存失败:");
        }

        return "";
    }
}
