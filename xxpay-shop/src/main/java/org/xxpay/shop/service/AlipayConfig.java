package org.xxpay.shop.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.xxpay.common.util.MyLog;

/**
 * @author: dingzhiwei
 * @date: 17/8/21
 * @description:
 */
@Component
public class AlipayConfig {
    private static final MyLog _log = MyLog.getLog(AlipayConfig.class);

    // 商户appid
    private String app_id = "2016101900720773";
    // 私钥 pkcs8格式的
    private String rsa_private_key =  "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCbgK2iizyxSinU2TXuhYv0ydq4qW+UVpM8hP/D9pNHSEPoimPyShBgz8mBfIK/6qe0077Q6I+XLuZFM64KOVsqonBl/vNhrDFsaxnJ4z0CZIP0PGRTOnt43G99KZA+npXOgTFPrNFZTqxM6YJx0WYrxmhN5Fb+A3EmlSLgBxX3X5u+a4tR6cBj+h1t6FHVqs4oIhruSogGV9r2lBlcpV6vYaMyyoDt97idRY0yGUlBDSCKM9G73DQTzdQP0KKnRnCX5cofD2G3HUuyDhYIOAeIqr3bZRLER6u0Utf5cKpHWVXfWWd5krHEOA4eM2XuSe07m6RepaKRj7SL/Y1l0AD3AgMBAAECggEAUyossuPYn9DUMnvAtkJCKbbS/TPQ8vqiYBmZWErFVbg2aWWqMmid/cjjMhdPLijnbHRadwwJz4dLq/MJv3oRYeniRwplS/V1sVBOSIT5mg/76mFqHoEGjAsIrV3PQU47PGgn4T7B8At9Ztzz4L9m7z57hjz6sXtRxpq4PixL+TIWoUx5pr65oZHMVjMyQI/ZJRpsn/BY/Ptq4YheM77knSO03b3VI4r5BtG/J+Hx5fq0Z4M3nxj4FUsVjgdETjEAD4DOOfWbbKXgRG5PtUnucwL4JJ5UvcZY0KeImbL1N643Ig8CxKG6WcMee7ywxuGSt70ujehFQ8uMe2SZQn7IUQKBgQDrKfIurX5xCW1uaF2th3mPmbPOYjNnWyCeKnuZ6puX0R97I6345JlyMvwXFN/evkeoZ9mrQP31SA/OYWPy6S0j3fDgg9gRjtyveS7xpWfM4p0fGE+3VxlR5sPk9yBayC5E/Wpb5d+tN77T1UMqh0MG0Idn2+gwyWWv37c5LwxVVQKBgQCpR9WAFhQ3UmcRzIf3HkdCo7tmhF7QwUU7kDHqy/DLhx8Co1wkOgpSB6Kvd3oscXiwOecMRXy+rzR0i7IwAI3pQkT6tNMYGwmbBace3gC2xyxRLvywfILd3SgpCK+XAutTm8lZ726aS8enlo4fecQd2E00SxBknq/aHSP7Qnb9GwKBgCzq+C1eSjaBHHvoR2xr4hZBv4x+SV9GkrZjWvSNUWbwNNkqeYJ+96hOgMRP4msDYWCTZYpGgbumJg6/n324eDzPmcDorg68gP6rYQIbG/aQmn5kAGX+pwFz2JsVcQKmFeBrnzMqd0z4xuRjac7nwy+8CQEXigQ3k8rCKpcrm2l5AoGAaBDKFqj8Gppk1JNZTrjlc/k1/qsHouv9xMU7eq2PRX+2t6d3wKmRqKYbBCW+DM5hNG0UNf4N1yfcZn1uuIXOuAROZZV+ZEZS3GNYPuiZpcPB4dKx80lIJl6MDtXRJFPeGZMe8FP6OY62mbc7fewcfYB4zdoLpN/NGZhqg09z2NsCgYEAwn1zlXjE7cDctFMtC8T2bOBfniAMFfonYch4t8YOCGA2s5iJnV7UyJR6fS5czrfzKH/4iAzN2nHKX/ifrd0l6w5WEe8YI9Nn+d6YrNl5z64C3CqXzAUmlFhq48zZpW2J6ZyX5/VmhOtmfJG4RzdIJHERUMh3GfC/6MwWy7DrBmw=";

    // 请求网关地址
//    private String url = "https://openapi.alipay.com/gateway.do";
    private String url = "https://openapi.alipaydev.com/gateway.do";
    // 编码
    public static String CHARSET = "UTF-8";
    // 返回格式
    public static String FORMAT = "json";
    // 支付宝公钥
    public String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtApzVNZIXvlgXcBNtYjbmpdoJjT5QVEjZW0ofDsEgEQ5sTQZTTJt/nQC/SLv66p8AhYA2AAcgf/jw1f3DCx2emPAnCRZ76VECXkw3pm0JcR+qvFJwIcbkfhVJGjGuakGOqhbsIH5rVkoQQqQGqfKuF4HlDHWdp1J8ibUuI0RrynznepAw05oLCOiT7QIlUGdkqJDXFfXJlj9lAy3oEHBcteKCOOl9SfQIJ+Z336p+fIR1628o+6QlIIKoCBx68WI40OD8PE6lrnhJB6puzSli+/u0Mfe4nIx/PAs2gyaBy7oO2RP+VrNNFfMkGR8LjRMS7VkkD7L690tqJgC4S+eLwIDAQAB";
    // RSA2
    public static String SIGNTYPE = "RSA2";

    // 是否沙箱环境,1:沙箱,0:正式环境
    private Short isSandbox = 1;

    /**
     * 初始化支付宝配置
     * @param configParam
     * @return
     */
    public AlipayConfig init(String configParam) {
        Assert.notNull(configParam, "init alipay config error");
        JSONObject paramObj = JSON.parseObject(configParam);
        this.setApp_id(paramObj.getString("appid"));
        this.setRsa_private_key(paramObj.getString("private_key"));
        this.setAlipay_public_key(paramObj.getString("alipay_public_key"));
        this.setIsSandbox(paramObj.getShortValue("isSandbox"));
        if(this.getIsSandbox() == 1) this.setUrl("https://openapi.alipaydev.com/gateway.do");
        return this;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    public String getRsa_private_key() {
        return rsa_private_key;
    }

    public void setRsa_private_key(String rsa_private_key) {
        this.rsa_private_key = rsa_private_key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Short getIsSandbox() {
        return isSandbox;
    }

    public void setIsSandbox(Short isSandbox) {
        this.isSandbox = isSandbox;
    }

    public String getAlipay_public_key() {
        return alipay_public_key;
    }

    public void setAlipay_public_key(String alipay_public_key) {
        this.alipay_public_key = alipay_public_key;
    }
}

