package com.okami.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.util.ArrayList;
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
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.common.HttpHandler;
import com.okami.config.DBConfig;
import com.okami.dao.impl.CacheLogDao;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.CacheLog;
import com.okami.util.FileUtil;
import com.okami.util.WebUtil;

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
	
	private boolean sendLogFlag = false ;
	
	
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
	
	/**
	 * 初始化
	 */
	public boolean init(){
		GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
		this.qHeartBeats = globaVariableBean.getQHeartBeats();
		this.configBean = IOC.instance().getClassobj(ConfigBean.class);
		this.heartBeatsLogPath = configBean.getLogPath()+File.separator +"heartbeats.log";
		this.monitorLogPath = configBean.getLogPath()+File.separator +"monitor.log";
		this.statusFlag = this.diffFlag = true;
		this.sendLogFlag = true;
		
		lastTime = null;
		return true;
	}

	public Queue<String> getQHeartBeats(){
		return qHeartBeats;
	}

	public void setQHeartBeats(Queue<String> qHeartBeats){
		this.qHeartBeats = qHeartBeats;
	}
	
	public boolean getSendLogFlag(){
		return sendLogFlag;
	}

	public void setSendLogFlag(boolean sendLogFlag){
		this.sendLogFlag = sendLogFlag;
	}
	
	/**
	 * 发送心跳
	 * @data 2017年4月19日
	 * @return
	 */
	public boolean sendHB(){
		lastTime = nowTime;
		HttpHandler httpHandler = IOC.instance().getClassobj(HttpHandler.class);
		String result =  httpHandler.sendHB();
		if(result==null || result.indexOf("success")<=0){
			statusFlag = false;	
		}else{
			statusFlag = true;
		}
		return true;
	}
	
	/**
	 * 发送消息，并记录到log中
	 * @data 2017年4月20日
	 * @param time
	 * @param type
	 * @param content
	 * @return
	 */
	public boolean sendMonitorEvent(String time,String type,String content){
		HttpHandler httpHandler = IOC.instance().getClassobj(HttpHandler.class);
		String result = httpHandler.sendMonitorEvent(time, type, content);
		if(result==null || result.indexOf("true")<=0)
			statusFlag = false;
		else
			statusFlag = true;
		return true;
	}
	
	/**
	 * 记录日志
	 * @data 2017年4月20日
	 * @param message
	 * @return
	 */
	public boolean writelog(String message){
		System.out.print(message);
		FileUtil.write(monitorLogPath, message, true);
		return true;
	}
	
	public void run(){
		// 初始化

		
		if(configBean.getRemoteMode()==true){
			nowTime = new Date();
			String message = String.format("[%s][信息][连接服务器 (%s:%s) ...]\r\n",DateFormat.getDateTimeInstance().format(nowTime),configBean.getRhost(),configBean.getRport());
			writelog(message);
		}
		
		while(true){
			if(configBean.getRemoteMode()==true){
				try {
					nowTime = new Date();
					
					// 第一次运行
					if(lastTime==null){
						sendHB();
						diffFlag = !statusFlag;
					}
					// 相差大于心跳时间
					else if(((nowTime.getTime() - lastTime.getTime())/1000) >= configBean.getDelay()){
						sendHB();
					}
					
					// 有消息推送过来
					if(!qHeartBeats.isEmpty()){
						// 先存入数据库
						String text = qHeartBeats.poll();
						String[] textList =  text.split("\t");
						String message = String.format("[%s][%s:%s][%s][%s]\r\n",textList[0],configBean.getRhost(),configBean.getRport(),textList[1] ,textList[2]);
						writelog(message);
						sendMonitorEvent(textList[0],textList[1],textList[2]);
						
						// 网络不通则存入数据库
						if(!statusFlag){
							try {
								CacheLogDao cacheLogDao = new CacheLogDao();
								cacheLogDao.setDataSource(new DBConfig().dataSource());
								CacheLog cacheLog = new CacheLog();
								cacheLog.setTime(textList[0]);
								cacheLog.setType(textList[1]);;
								cacheLog.setEvent(textList[2]);
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
							System.out.print(message);
							FileUtil.write(heartBeatsLogPath, message, true);
						}else{
							String message = String.format("[%s][信息][连接服务器 (%s:%s) 失败！]\r\n",DateFormat.getDateTimeInstance().format(nowTime),configBean.getRhost(),configBean.getRport());
							System.out.print(message);
							FileUtil.write(heartBeatsLogPath, message, true);
						}
					}
					
					// 如果有缓存的log 则进行处理
					if(statusFlag&&sendLogFlag){
						try {
							CacheLogDao cacheLogDao = new CacheLogDao();
							cacheLogDao.setDataSource(new DBConfig().dataSource());
							List<CacheLog> CacheLogs = cacheLogDao.queryCacheLog();	
							for(CacheLog cacheLog:CacheLogs){
								//发送
								sendMonitorEvent(cacheLog.getTime(),cacheLog.getType(),cacheLog.getEvent());
								
								if(!statusFlag){
									break;
								}
								
								cacheLogDao.deleteCacheLog(cacheLog);
								
								// 到了心跳时间
								if(((nowTime.getTime() - lastTime.getTime())/1000) >= configBean.getDelay()){
									break;
								}
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

}
