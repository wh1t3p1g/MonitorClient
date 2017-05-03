package com.okami.common;

import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.config.DBConfig;
import com.okami.core.IOC;
import com.okami.dao.impl.CacheLogDao;
import com.okami.entities.CacheLog;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.IniUtil;
import com.okami.util.WebUtil;
import com.okami.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
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

    @Autowired
    private ConfigBean configBean;
    
    @Autowired
    private GlobaVariableBean globaVariableBean;

    private String RHost;
    
    private String hbPostParameters ;
    
    private CacheLogDao cacheLogDao ; 

    public HttpHandler(){
    	headers = new HashMap<String, String>();
    	headers.put("Charsert", "UTF-8");
    	headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
    	headers.put("Accept", "*/*");
    	headers.put("Accept-Encoding", "gzip, deflate");
    	
		Map<String,String> config = IniUtil.getConfig(System.getProperty("user.dir") + File.separator + "config/config.ini");
		try {
			RHost="http://"+config.get("rhost")+":"+config.get("rport");
	    	hbPostParameters = "ip="+config.get("lhost")+
					"&port="+config.get("lport")+
					"&delay="+String.valueOf(Integer.valueOf( config.get("delay")).intValue()/60)+
					"&storage_path=" + config.get("storagePath") +
					"&web_root_path=" + config.get("monitorPathList" );
		} catch (NumberFormatException e) {
		    IOC.log.error(e.getMessage());
		}
		cacheLogDao = new CacheLogDao();
		cacheLogDao.setDataSource(new DBConfig().dataSource());
    }

    
    /**
     * 发送消息
     * @data 2017年4月23日
     * @param postParameters
     * @return
     */
    public String sendMessage(String postParameters){
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		HttpResponse httpResponse = WebUtil.httpPost(RHost+"/Monitor/public/api/messages/scan/"+configBean.getLhost(),headers,postParameters);
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
    public String sendMonitorEvent(String time,String type,String content,String taskName){
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		String mgPostParameters = "type=" + type + "&time=" +time+"&content=" + DataUtil.urlEncode(content)+"&task_name=" + taskName ;
		HttpResponse httpResponse = WebUtil.httpPost(
				RHost+"/Monitor/public/api/messages/add/"+configBean.getLhost(),
				headers,mgPostParameters);
		return WebUtil.getResponseBody(httpResponse);
    }
    

	/**
	 * 发送心跳
	 * @data 2017年4月19日
	 * @return
	 */
	public String sendHB(){
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		Map<String,Integer> dataParameters = new HashMap<String, Integer>();
		try {
			for(MonitorTask monitorTask:globaVariableBean.getMonitorTaskDao().queryTask()){
				dataParameters.put(monitorTask.getTaskName(), monitorTask.getStatus());
			}
		} catch (Exception e) {
			IOC.log.error(e.getMessage());
		}
		String hbData = hbPostParameters + "&data=" + DataUtil.toJson(dataParameters);
		HttpResponse httpResponse = WebUtil.httpPost(RHost+"/Monitor/public/api/heartbeat",
											headers,hbData);
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
	 * 发生可疑的文件内容
	 * @data 2017年4月23日
	 * @param file
	 * @return
	 */
	public String uploadSuspiciousFile(File file){
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		HttpResponse httpResponse =
				WebUtil.httpPost(
						RHost+"/Monitor/public/upload/up",
						headers,FileUtil.readByte(file.getAbsolutePath().toString()));
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
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		byte[] result = null;
		int i=3;
		while(i>0&&result==null){
			HttpResponse httpResponse = WebUtil.httpGet(RHost+"/Monitor/public/download/"+filename,headers);
			result = WebUtil.getResponseBodyBytes(httpResponse);
			i--;
		}
		if(result==null||(new String(result)).equals("not found")){
			return null;
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
