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
import com.okami.config.DBConfig;
import com.okami.dao.impl.CacheLogDao;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.CacheLog;

/**
 * 心跳模块以及发送消息模块
 * @author orleven
 * @date 2017年1月15日
 */

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
	
	public HeartBeatsThread(ConfigBean configBean,Queue<String> qHeartBeats){
		this.configBean = configBean;
		this.qHeartBeats = qHeartBeats;
		this.heartBeatsLogPath = configBean.getLogPath()+File.separator +"heartbeats.log";
		this.monitorLogPath = configBean.getLogPath()+File.separator +"monitor.log";
		statusFlag = diffFlag = true;
		lastTime = null;
		httpHeaders = new HashMap<String, String>();
		httpHeaders.put("Charsert", "UTF-8");
		httpHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64; rv:48.0) Gecko/20100101 Firefox/48.0");
		httpHeaders.put("Accept", "*/*");
		httpHeaders.put("Accept-Encoding", "gzip, deflate");
	}
	
	public Queue<String> getQHeartBeats(){
		return qHeartBeats;
	}
	
	public void run(){
		if(configBean.getRemoteMode()==true){
			try {
				nowTime = new Date();
				FileWriter fw = new FileWriter(heartBeatsLogPath,true);
				BufferedWriter bw = new BufferedWriter(fw);
				String message = String.format("[%s][信息][连接服务器 (%s:%s) ...]\r\n",DateFormat.getDateTimeInstance().format(nowTime),configBean.getRhost(),configBean.getRport());
				bw.write(message);
				System.out.println(message);
				bw.flush();
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
						String textList = qHeartBeats.remove();
						FileWriter fw = new FileWriter(monitorLogPath,true);
						BufferedWriter bw = new BufferedWriter(fw);
						bw.write(textList+"\r\n");
						System.out.print(textList+"\r\n");
						bw.flush();
						bw.close();
						fw.close();
					}
					

					if(statusFlag ^ diffFlag){
						diffFlag = statusFlag;
						if(statusFlag){
							FileWriter fw = new FileWriter(heartBeatsLogPath,true);
							BufferedWriter bw = new BufferedWriter(fw);
							String message = String.format("[%s][信息][连接服务器 (%s:%s) 成功！]\r\n",DateFormat.getDateTimeInstance().format(nowTime),configBean.getRhost(),configBean.getRport());
							bw.write(message);
							System.out.print(message);
							bw.flush();
							bw.close();
							fw.close();
						}else{
							FileWriter fw = new FileWriter(heartBeatsLogPath,true);
							BufferedWriter bw = new BufferedWriter(fw);
							String message = String.format("[%s][信息][连接服务器 (%s:%s) 失败！]\r\n",DateFormat.getDateTimeInstance().format(nowTime),configBean.getRhost(),configBean.getRport());
							bw.write(message);
							System.out.print(message);
							bw.flush();
							bw.close();
							fw.close();
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
	
//	public boolean writeLog(String logName,String message,String mode){
//		FileWriter fw = new FileWriter(logName);
//		BufferedWriter bw = new BufferedWriter(fw);
//		//String message = String.format("[%s][信息][连接服务器 (%s:%s) ...]\n",DateFormat.getDateTimeInstance().format(nowTime),configBean.getRhost(),configBean.getRport());
//		bw.write(message);
//		System.out.println(message);
//		bw.flush();
//		bw.close();
//		fw.close();
//		return true;
//	}
	
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
