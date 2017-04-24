package com.okami.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.bean.ConfigBean;
import com.okami.core.IOC;
import com.okami.util.WebUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * http request
 * @author wh1t3P1g
 * @since 2017/1/15
 */
@Component
public class HttpHandler {

    private Map<String,String> headers;

    private ConfigBean configBean;

    private String RHost;

    public HttpHandler(){
    	headers = new HashMap<String, String>();
    	headers.put("Charsert", "UTF-8");
    	headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
    	headers.put("Accept", "*/*");
    	headers.put("Accept-Encoding", "gzip, deflate");

    }
    
    public void init(){
    	configBean = IOC.instance().getClassobj(ConfigBean.class);
		RHost="http://"+configBean.getRhost()+":"+configBean.getRport();
    }
    
    /**
     * 发送消息
     * @data 2017年4月23日
     * @param postParameters
     * @return
     */
    public String sendMessage(String postParameters){
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		HttpResponse httpResponse = WebUtil.httpPost(RHost+"/Monitor/public/api/messages/add/"+configBean.getLhost(),headers,postParameters);
		String result = WebUtil.getResponseBody(httpResponse);
		return result;
    }
    
    /**
     * 发送监控消息
     * @data 2017年4月23日
     * @param time
     * @param type
     * @param content
     * @return
     */
    public String sendMonitorEvent(String time,String type,String content){
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		String mgPostParameters = "type=" + type + "&time=" +time+"&content=" + content;
		HttpResponse httpResponse = WebUtil.httpPost(
				RHost+"/Monitor/public/api/messages/add/"+configBean.getLhost(),
				headers,mgPostParameters);
		String result = WebUtil.getResponseBody(httpResponse);
		return result;
    }
    

	/**
	 * 发送心跳
	 * @data 2017年4月19日
	 * @return
	 */
	public String sendHB(){
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		String hbPostParameters =
				"ip="+configBean.getLhost()+
				"&port="+configBean.getLport()+
				"&delay="+String.valueOf(configBean.getDelay()/60)+
				"&storage_path=" + configBean.getStoragePath();
		HttpResponse httpResponse =
					WebUtil.httpPost(RHost+"/Monitor/public/api/heartbeat",
											headers,hbPostParameters);
		String result = WebUtil.getResponseBody(httpResponse);
		return result;
	}

	/**
	 * 上传文件
	 * @data 2017年4月23日
	 * @param file
	 * @return
	 */
	public String upload(File file){
		headers.remove("Content-Type");
		HttpResponse httpResponse =
				WebUtil.uploadFile(
						RHost+"/Monitor/public/upload/up",
						headers,file.getAbsoluteFile().toString());
		String result = WebUtil.getResponseBody(httpResponse);
		return result;
	}
	
	/**
	 * 下载文件
	 * @data 2017年4月24日
	 * @param filename
	 * @param ouFile
	 * @return
	 */
	public byte[] download(String filename){
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		byte[] result = null;
		int i=3;
		while(i>0&&result==null){
			HttpResponse httpResponse = WebUtil.httpGet(RHost+"/Monitor/public/download/"+filename,headers);
			result = WebUtil.getResponseBodyBytes(httpResponse);
			i--;
		}

		return result;
	}

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

}
