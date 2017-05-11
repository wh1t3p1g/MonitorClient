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
import com.okami.entities.DataConfig;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.IniUtil;
import com.okami.util.WebUtil;
import com.okami.util.FileUtil;

import java.io.File;
import java.text.DecimalFormat;
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
    
    @Autowired
    private AESHander aESHander;
    
    private CacheLogDao cacheLogDao ; 

    private String remoteHost;
    
    private String hbPostParameters ;
    
    private String hbUrl ;
    
    private String messageUrl ;
    
    private String monitorUrl;
    
    private String upUrl;
    
    private String downUrl;
    
    private String lhost;
    private String lport;
    private String storagePath;
    private String monitorPathList;
    
    private boolean flag = false;
    
    public HttpHandler(){
    	headers = new HashMap<String, String>();
    	headers.put("Charsert", "UTF-8");
    	headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
    	headers.put("Accept", "*/*");
    	headers.put("Accept-Encoding", "gzip, deflate");
    	
    	Map<String,String> config = IniUtil.getConfig(System.getProperty("user.dir") + File.separator + "config/config.ini");
    	remoteHost="http://"+config.get("rhost")+":"+config.get("rport");	
    	lhost = config.get("lhost");
    	lport = config.get("lport");
    	storagePath = config.get("storagePath");
    	monitorPathList = config.get("monitorPathList");
    	hbPostParameters = "ip="+lhost+"&port="+lport;

    	hbUrl = remoteHost+"/Monitor/public/api/heartbeat";
    	messageUrl = remoteHost+"/Monitor/public/api/messages/scan/"+config.get("lhost");
    	monitorUrl = remoteHost+"/Monitor/public/api/messages/add/"+config.get("lhost");
    	upUrl= remoteHost+"/Monitor/public/upload/up";
    	downUrl = remoteHost+"/Monitor/public/download/";
    	
		cacheLogDao = new CacheLogDao();
		cacheLogDao.setDataSource(new DBConfig().dataSource());
    }

    public void init(){
    	try {
    		DataConfig DataConfig = globaVariableBean.getDataConfigDao().queryDataConfig();
    		if(DataConfig!=null){
    			aESHander.AESInit(DataConfig.getKey(),DataConfig.getIv());
    		}
		} catch (Exception e) {
    		e.printStackTrace();
    		IOC.log.error(e.getMessage());
		}
    	
    	
		flag = true;
    }
    
    public void setHbPostParameters(){
    	hbPostParameters = "ip="+lhost+"&port="+lport+
				"&storage_path=" + DataUtil.encode(storagePath,aESHander) + 
				"&web_root_path=" + DataUtil.encode(monitorPathList,aESHander);

    }
    

    
    /**
     * 发送消息
     * @data 2017年4月23日
     * @param postParameters
     * @return
     */
    public String sendMessage(String postParameters){
		headers.put("Cookie", "_="+DataUtil.encode(DataUtil.getTimeStamp(),aESHander));
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		HttpResponse httpResponse = WebUtil.httpPost(messageUrl,headers,postParameters);
		return WebUtil.getResponseBody(httpResponse);
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
		headers.put("Cookie", "_="+DataUtil.encode(DataUtil.getTimeStamp(),aESHander));
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		String mgPostParameters = "type=" + type + "&time=" +time+"&content=" +DataUtil.encode(content,aESHander)+"&task_name=" + taskName ;
		HttpResponse httpResponse = WebUtil.httpPost(monitorUrl,headers,mgPostParameters);
		return WebUtil.getResponseBody(httpResponse);
    }
    

	/**
	 * 发送心跳
	 * @data 2017年4月19日
	 * @return
	 */
	public String sendHB(){
		if(aESHander.getFlag()){
			headers.put("Cookie", "_="+DataUtil.encode(DataUtil.getTimeStamp(),aESHander));
			setHbPostParameters();
		}
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		Map<String,Integer> dataParameters = new HashMap<String, Integer>();
		try {
			for(MonitorTask monitorTask:globaVariableBean.getMonitorTaskDao().queryTask()){
				dataParameters.put(monitorTask.getTaskName(), monitorTask.getStatus());
			}
		} catch (Exception e) {
    		e.printStackTrace();
    		IOC.log.error(e.getMessage());
		}
		double delay = configBean.getDelay();
		delay = delay/60;
		DecimalFormat    df   = new DecimalFormat("######0.00");   
		
		String hbData = hbPostParameters + "&data=" + DataUtil.encode(DataUtil.toJson(dataParameters),aESHander) + "&delay="+df.format(delay) ;
		HttpResponse httpResponse = WebUtil.httpPost(hbUrl,headers,hbData);
		return WebUtil.getResponseBody(httpResponse);
	}

	/**
	 * 上传文件
	 * @data 2017年4月23日
	 * @param file
	 * @return
	 */
	public String upload(File file){
		headers.put("Cookie", "_="+DataUtil.encode(DataUtil.getTimeStamp(),aESHander));
		headers.remove("Content-Type");
		HttpResponse httpResponse = WebUtil.uploadFile( upUrl,headers,file.getAbsoluteFile().toString());
		return WebUtil.getResponseBody(httpResponse);
	}
	
	/**
	 * 发生可疑的文件内容
	 * @data 2017年4月23日
	 * @param file
	 * @return
	 */
	public String uploadSuspiciousFile(File file){
		headers.put("Cookie", "_="+DataUtil.encode(DataUtil.getTimeStamp(),aESHander));
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		HttpResponse httpResponse = WebUtil.httpPost(upUrl,headers,FileUtil.readByte(file.getAbsolutePath().toString()));
		return WebUtil.getResponseBody(httpResponse);
	}
	
	/**
	 * 下载文件
	 * @data 2017年4月24日
	 * @param filename
	 * @param ouFile
	 * @return
	 */
	public byte[] download(String filename){
		headers.put("Cookie", "_="+DataUtil.encode(DataUtil.getTimeStamp(),aESHander));
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
		byte[] result = null;
		int i=3;
		while(i>0&&result==null){
			HttpResponse httpResponse = WebUtil.httpGet(downUrl+filename,headers);
			result = WebUtil.getResponseBodyBytes(httpResponse);
			i--;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(result==null||(new String(result)).equals("not found")){
			return null;
		}

		return result;
	}
	
    public boolean getFlag(){
    	return this.flag;
    }
}
