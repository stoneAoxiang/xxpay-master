package cn.com.inhand.openserver.http;

import java.io.IOException;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.util.IdleConnectionTimeoutThread;

import cn.com.inhand.openserver.util.RequestUtil;

public class HttpProtocolHandler {
	
	private static String DEFAULT_CHARSET = "UTF-8";
	
	private int defaultConnectionTimeout  = 8000;
	
	/** ��Ӧ��ʱʱ��, ��bean factory���ã�ȱʡΪ30���� */
    private int defaultSoTimeout = 30000;

    /** �������ӳ�ʱʱ��, ��bean factory���ã�ȱʡΪ60���� */
    private int defaultIdleConnTimeout = 60000;

    private int defaultMaxConnPerHost = 30;

    private int defaultMaxTotalConn = 80;

    /** Ĭ�ϵȴ�HttpConnectionManager�������ӳ�ʱ��ֻ���ڴﵽ���������ʱ�����ã���1��*/
    private static final long defaultHttpConnectionManagerTimeout = 3 * 1000;

    /**
     * HTTP���ӹ������������ӹ������������̰߳�ȫ��.
     */
    private HttpConnectionManager      connectionManager;

    private static HttpProtocolHandler httpProtocolHandler  = new HttpProtocolHandler();

    /**
     * ��������
     * 
     * @return
     */
    public static HttpProtocolHandler getInstance() {
        return httpProtocolHandler;
    }
    
    /**
     * ˽�еĹ��췽��
     */
    private HttpProtocolHandler() {
        // ����һ���̰߳�ȫ��HTTP���ӳ�
        connectionManager = new MultiThreadedHttpConnectionManager();
        connectionManager.getParams().setDefaultMaxConnectionsPerHost(defaultMaxConnPerHost);
        connectionManager.getParams().setMaxTotalConnections(defaultMaxTotalConn);

        IdleConnectionTimeoutThread ict = new IdleConnectionTimeoutThread();
        ict.addConnectionManager(connectionManager);
        ict.setConnectionTimeout(defaultIdleConnTimeout);

        ict.start();
    }
    
    
    public HttpResponse execute(HttpRequest request) throws HttpException, IOException {
        HttpClient httpclient = new HttpClient(connectionManager);
        int connectionTimeout = defaultConnectionTimeout;
        if (request.getConnectionTimeout() > 0) {
            connectionTimeout = request.getConnectionTimeout();
        }
        httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);

        // ���û�Ӧ��ʱ
        int soTimeout = defaultSoTimeout;
        if (request.getTimeout() > 0) {
            soTimeout = request.getTimeout();
        }
        httpclient.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);
        
        String charset = request.getCharset();
        charset = charset == null ? DEFAULT_CHARSET : charset;
        HttpMethod method = null;
        
        if (request.getMethod().equals(HttpRequest.METHOD_GET)) {
            method = new GetMethod(request.getUrl());
            method.getParams().setCredentialCharset(charset);
            method.setQueryString(request.getQueryString());
        } else {
            method = new PostMethod(request.getUrl());
            
            if(request.getParameters() != null){
            	((PostMethod) method).setQueryString(request.getParameters());
            }
            if(request.getBody() != null){
            	((PostMethod) method).setRequestEntity(new StringRequestEntity(RequestUtil.parameterToJSON(request.getBody()),"application/json",null));
            }
//            
//            if(request.getCsvBody() != null){
//            	((PostMethod) method).setRequestEntity(new StringRequestEntity(request.getCsvBody(),"text/csv",null));
//            }
//            method.addRequestHeader("Content-Type", "text/csv");
            method.addRequestHeader("Content-Type", "application/json;");
        }

        // ����Http Header�е�User-Agent����
        method.addRequestHeader("User-Agent", "Mozilla/4.0");
        HttpResponse response = new HttpResponse();
        try {
            httpclient.executeMethod(method);
            if (request.getResultType().equals(HttpResultType.STRING)) {
                response.setStringResult(method.getResponseBodyAsString());
            } else if (request.getResultType().equals(HttpResultType.BYTES)) {
                response.setByteResult(method.getResponseBody());
            }
            response.setResponseHeaders(method.getResponseHeaders());
        } catch (UnknownHostException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        } catch (Exception ex) {
            return null;
        } finally {
            method.releaseConnection();
        }
        return response;
    }
}
