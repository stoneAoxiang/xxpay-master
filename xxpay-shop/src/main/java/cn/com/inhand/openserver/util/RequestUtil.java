package cn.com.inhand.openserver.util;

import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.apache.commons.httpclient.NameValuePair;

public class RequestUtil {
	
	public static NameValuePair[] generatNameValuePair(Map<String, String> properties) {
        NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : properties.entrySet()) {
            nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
        }
        return nameValuePair;
    }
	
	public static String parameterToJSON(Map<String,String> parameter){
        return JSON.toJSONString(parameter);
//		return JSONObject.fromObject(parameter).toString();
	}
	
}
