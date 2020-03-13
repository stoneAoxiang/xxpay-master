package cn.com.inhand.openserver.util;

import cn.com.inhand.openserver.http.HttpProtocolHandler;
import cn.com.inhand.openserver.http.HttpRequest;
import cn.com.inhand.openserver.http.HttpResponse;
import cn.com.inhand.openserver.http.HttpResultType;
import cn.com.inhand.openserver.util.RequestUtil;
import org.apache.commons.httpclient.HttpException;
import org.xxpay.common.util.MyLog;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: ServiceUtil
 * @author: aoxiang
 * @create: 2020/03/03 10:44
 **/
public class Auto24ServiceUtil {
    private final static MyLog _log = MyLog.getLog(Auto24ServiceUtil.class);

    // API秘钥 详情见: http://www.dfbs-vm.com/DeviceNetwork/applications/index.html#
    // 中的 系统->开发配置->开发者ID client_secret参数
    private final static String KEY = "LfUSAN5uZ6qVNVkI0BOfcdDasaEy4olB";

    public static String request24Service(Map<String,String> body, String url, Map<String,String> parameter){
        Map<String,String> signMap = new HashMap<String,String>();
        if(null != parameter && !parameter.isEmpty()){
            signMap.putAll(parameter);
        }
        signMap.putAll(body);
        String sign = SignUtil.sign(signMap, "MD5", KEY, "UTF-8");
//        _log.info("得到签名的字符串:{}", sign);
        body.put("sign", sign);

        HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
        HttpRequest request = new HttpRequest(HttpResultType.BYTES);
        request.setCharset("UTF-8");
        request.setBody(body);
        if(null != parameter && !parameter.isEmpty()){
            request.setParameters(RequestUtil.generatNameValuePair(parameter));
        }

        request.setUrl(url);
//        _log.info("获取商品库存请求地址:{}", url);

        try {
            HttpResponse response = httpProtocolHandler.execute(request);

            if (response != null) {
                String result = response.getStringResult();
                return result;
            }

        } catch (HttpException e) {
            _log.error("HttpException:{}", e.getMessage());
//            e.printStackTrace();
        } catch (IOException e) {
            _log.error("IOException:{}", e.getMessage());
//            e.printStackTrace();
        }

        return "";
    }
}
