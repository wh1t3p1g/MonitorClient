package com.okami.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.config.DBConfig;
import com.okami.dao.impl.CacheLogDao;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.CacheLog;
import com.okami.util.FileUtil;

/**
 * 心跳模块以及发送消息模块
 * @author orleven
 * @date 2017年1月15日
 */
@Component
public class HeartBeatsThread extends Thread{
	
	private ConfigBean configBean;
	
	private Queue<String> qHeartBeats;
	
	private String heartBeatsLogPath ;
	
	private String monitorLogPath ;
	
	private String cashLogPath ;
	
	/**
	 * 连接状态
	 */
	private boolean statusFlag;
	
	/**
	 *  特殊标识
	 */
	private boolean diffFlag;

	private Date lastTime;
	
	private Date nowTime;
	
	private Map<String, String> httpHeaders;
	
	/**
	 * 初始化
	 */
	public boolean init(){
		GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
		this.qHeartBeats = globaVariableBean.getQHeartBeats();
		this.configBean = IOC.instance().getClassobj(ConfigBean.class);
		this.heartBeatsLogPath = configBean.getLogPath()+File.separator +"heartbeats.log";
		this.monitorLogPath = configBean.getLogPath()+File.separator +"monitor.log";
		statusFlag = diffFlag = true;
		lastTime = null;
		httpHeaders = new HashMap<String, String>();
		httpHeaders.put("Charsert", "UTF-8");
		httpHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
		httpHeaders.put("Accept", "*/*");
		httpHeaders.put("Accept-Encoding", "gzip, deflate");
		return true;
	}
	
	public Queue<String> getQHeartBeats(){
		return qHeartBeats;
	}

	public void setQHeartBeats(Queue<String> qHeartBeats){
		this.qHeartBeats = qHeartBeats;
	}
	
	public void run(){
		if(configBean.getRemoteMode()==true){
			nowTime = new Date();
			String message = String.format("[%s][信息][连接服务器 (%s:%s) ...]\r\n",DateFormat.getDateTimeInstance().format(nowTime),configBean.getRhost(),configBean.getRport());
			FileUtil.write(heartBeatsLogPath, message, true);
		}
		
		while(true){
			if(configBean.getRemoteMode()==true){
				try {
					nowTime = new Date();
					
					// 心跳
					if(lastTime==null){
						// 第一次运行
						lastTime = nowTime;
						statusFlag = httpGet("http://"+configBean.getRhost()+":"+configBean.getRport()+"/monitor/hb");
					}else if(((nowTime.getTime() - lastTime.getTime())/1000) >= configBean.getDelay()){
						// 相差大于心跳时间
						lastTime = nowTime;
						statusFlag = httpGet("http://"+configBean.getRhost()+":"+configBean.getRport()+"/monitor/hb");
					}
					
					// 有消息推送过来
					if(!qHeartBeats.isEmpty()){
						// 先存入数据库
						String text = qHeartBeats.poll();
						if(text!=null){
							String[] textList =  text.split("\t");
							try {
								System.out.print(textList[0]+"\t"+textList[1]+"\t"+textList[2]+"\r\n");
								CacheLog cacheLog = new CacheLog();
								cacheLog.setTime(textList[0]);
								cacheLog.setType(textList[1]);;
								cacheLog.setEvent(textList[2]);
								CacheLogDao cacheLogDao = new CacheLogDao();
								cacheLogDao.setDataSource(new DBConfig().dataSource());
								cacheLogDao.insertCacheLog(cacheLog);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					
					// 逻辑判断，避免重复存入log
					if(statusFlag ^ diffFlag){
						diffFlag = statusFlag;
						if(statusFlag){
							String message = String.format("[%s][信息][连接服务器 (%s:%s) 成功！]\r\n",DateFormat.getDateTimeInstance().format(nowTime),configBean.getRhost(),configBean.getRport());
							FileUtil.write(heartBeatsLogPath, message, true);
						}else{
							String message = String.format("[%s][信息][连接服务器 (%s:%s) 失败！]\r\n",DateFormat.getDateTimeInstance().format(nowTime),configBean.getRhost(),configBean.getRport());
							FileUtil.write(heartBeatsLogPath, message, true);
						}
					}
					
					// 如果有缓存的log 则进行处理
					if(statusFlag){
						try {
							CacheLogDao cacheLogDao = new CacheLogDao();
							cacheLogDao.setDataSource(new DBConfig().dataSource());
							List<CacheLog> CacheLogs = cacheLogDao.queryCacheLog();	
							for(int i=0;i<CacheLogs.size();i++){
								//发送
								CacheLog cacheLog = CacheLogs.get(i);
								String message = String.format("[%s][%s:%s][%s][%s]\r\n",cacheLog.getTime(),configBean.getRhost(),configBean.getRport(),cacheLog.getType(),cacheLog.getEvent());
								statusFlag = httpGet("http://"+configBean.getRhost()+":"+configBean.getRport()+"/monitor/hb");
								if(!statusFlag){
									break;
								}
	
								FileUtil.write(monitorLogPath, message, true);
								cacheLogDao.deleteCacheLog(cacheLog);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	public boolean httpGet(String targetUrl){
		HttpClient client = HttpClients.createDefault();
		HttpGet get = new HttpGet(targetUrl);
		for (String key : httpHeaders.keySet()) {
        	get.setHeader(key, httpHeaders.get(key));
        }
        try {
        	HttpResponse response = client.execute(get);
        	HttpEntity httpEntity = response.getEntity();
        	String result = EntityUtils.toString(httpEntity,"utf-8");
        	if(result.indexOf("success")<=0){
        		return false;
        	}
        	return true;
		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
		} catch (ClientProtocolException e) {
//			e.printStackTrace();
		} catch (IOException e) {
//			e.printStackTrace();
		}
        return false;
	}
}
