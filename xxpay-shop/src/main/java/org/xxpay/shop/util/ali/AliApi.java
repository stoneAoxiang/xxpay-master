package org.xxpay.shop.util.ali;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xxpay.common.util.MyLog;
import org.xxpay.shop.service.AlipayConfig;
import org.xxpay.shop.service.BaseService;

import javax.net.ssl.X509TrustManager;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * 微信 API、微信基本接口
 * 
 */
@Service
public class AliApi {

	private static final MyLog _log = MyLog.getLog(AliApi.class);

	@Autowired
	private AlipayConfig alipayConfig;

	//网页授权OAuth2.0获取code
	private static final String GET_OAUTH_CODE = "https://openauth.alipaydev.com/oauth2/publicAppAuthorize.htm?app_id=%s&scope=%s&redirect_uri=%s&state=init";
	                                            //https://openauth.alipaydev.com/oauth2/publicAppAuthorize.htm?app_id=APPID&scope=SCOPE&redirect_uri=ENCODED_URL
												//https://openauth.alipaydev.com/oauth2/publicAppAuthorize.htm?app_id=APPID&scope=SCOPE&redirect_uri=ENCODED_URL

	//网页授权OAuth2.0获取auth_code
	public String getOAuthCodeUrl(String appId ,String redirectUrl ,String scope){
//		String url = String.format(GET_OAUTH_CODE, appId, scope, urlEnodeUTF8(redirectUrl));
//		AlipayClient alipayClient = new DefaultAlipayClient(url, alipayConfig.getApp_id(), alipayConfig.getRsa_private_key(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipay_public_key(), AlipayConfig.SIGNTYPE);
//
//		AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
//		try {
//			AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
//
//		} catch (AlipayApiException e) {
//			//处理异常
//			e.printStackTrace();
//		}
		return String.format(GET_OAUTH_CODE, appId, scope, urlEnodeUTF8(redirectUrl));
	}

	//auth_code换取access_token与user_id
	public String getTokenUserId(String auth_code, String mchId, String channelId){
//		PayChannel payChannel = super.baseSelectPayChannel(mchId, channelId);
//		alipayConfig.init(payChannel.getParam());
		AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig.getUrl(), alipayConfig.getApp_id(), alipayConfig.getRsa_private_key(), AlipayConfig.FORMAT, AlipayConfig.CHARSET, alipayConfig.getAlipay_public_key(), AlipayConfig.SIGNTYPE);

		AlipaySystemOauthTokenRequest request = new AlipaySystemOauthTokenRequest();
		request.setCode(auth_code);
		request.setGrantType("authorization_code");
		try {
			AlipaySystemOauthTokenResponse oauthTokenResponse = alipayClient.execute(request);
			String userId = oauthTokenResponse.getUserId();
			String accessToken = oauthTokenResponse.getAccessToken();
			_log.info("userId:{}  accessToken:{}", userId, accessToken);
			return userId;
		} catch (AlipayApiException e) {
			//处理异常
			e.printStackTrace();
		}
		return "";
	}
	
	public static String urlEnodeUTF8(String str){
        String result = str;
        try {
            result = URLEncoder.encode(str,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
	
}

class JEEWeiXinX509TrustManager implements X509TrustManager {
	public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}
	public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
	}
	public X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}