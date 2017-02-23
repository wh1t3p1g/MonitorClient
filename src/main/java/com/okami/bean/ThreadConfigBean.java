package com.okami.bean;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.core.BackupAndCheckThread;
import com.okami.core.HeartBeatsThread;
import com.okami.core.MonitorThread;

/**
 * 线程配置类
 * @author orleven
 * @date 2017年2月15日
 */

@Component
public class ThreadConfigBean {

	private ConfigBean configBean;
	
	private List<MonitorTaskBean> monitorTaskBeans;

	private List<MonitorThread> monitorThreads;
	
	private List<BackupAndCheckThread> backupAndCheckThreads;
	
	/**
	 * 心跳线程
	 */
	private HeartBeatsThread heartBeatsThread ;
	
	private Queue<String> qHeartBeats;

	public ThreadConfigBean(ConfigBean configBean){
		this.configBean = configBean;
		monitorTaskBeans = new ArrayList<MonitorTaskBean>();
		backupAndCheckThreads = new ArrayList<BackupAndCheckThread>();
		monitorThreads = new ArrayList<MonitorThread>();
		heartBeatsThread = new HeartBeatsThread(configBean, qHeartBeats);
		qHeartBeats = new LinkedList<String>();;
	}
	
	public Queue<String> getQHeartBeats(){
		return qHeartBeats;
	}
	
	public void setQHeartBeats(Queue<String> qHeartBeats){
		this.qHeartBeats = qHeartBeats;
	}
	
	public List<MonitorTaskBean> getMonitorTaskBeans(){
		return monitorTaskBeans;
	}
	

	public List<MonitorThread> getMonitorThreads(){
		return monitorThreads;
	}
	

	public List<BackupAndCheckThread> getBackupAndCheckThreads(){
		return backupAndCheckThreads;
	}
	
	public HeartBeatsThread getHeartBeatsThread(){
		return heartBeatsThread;
	}
	
	public void setHeartBeatsThread(HeartBeatsThread heartBeatsThread){
		this.heartBeatsThread = heartBeatsThread;
	}

}
