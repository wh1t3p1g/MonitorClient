package com.okami.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.config.DBConfig;
import com.okami.core.BackupAndCheckThread;
import com.okami.core.HeartBeatsThread;
import com.okami.core.MonitorThread;
import com.okami.dao.impl.FileIndexDao;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.MonitorTask;

/**
 * 全局变量存储,线程池
 * @author orleven
 * @date 2017年2月15日
 */
@Component
public class GlobaVariableBean {

	// 任务
	private List<MonitorTask> monitorTaskList;
	
	// 队列
	private Queue<String> qHeartBeats;
	private Queue<String> qRepaire;
	private List<Queue<String>> qMonitorList;
	
	// 数据库的连接列表
	private MonitorTaskDao monitorTaskDao;
	private List<FileIndexDao> fileIndexDaoList;
	
	// 线程
	private List<MonitorThread> monitorThreadList;
	private List<BackupAndCheckThread> backupAndCheckThreadList;
	
	public GlobaVariableBean(){
		// 变量初始化
		monitorTaskList = new ArrayList<MonitorTask>();
		monitorThreadList = new ArrayList<MonitorThread>();
		backupAndCheckThreadList = new ArrayList<BackupAndCheckThread>();
		qMonitorList = new ArrayList<Queue<String>>();
		fileIndexDaoList = new ArrayList<FileIndexDao>();
		qHeartBeats = new LinkedList<String>();
		qRepaire = new LinkedList<String>();
		monitorTaskDao = new MonitorTaskDao();
		
		// 开启数据库连接
		monitorTaskDao.setDataSource(new DBConfig().dataSource());
		try {
			if(!monitorTaskDao.isTableExist()){
				monitorTaskDao.createTable();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public List<MonitorTask> getMonitorTaskList(){
		return monitorTaskList;
	}
	
	public void setgetMonitorTaskList(List<MonitorTask> monitorTaskList){
		this.monitorTaskList = monitorTaskList;
	}
	
	public Queue<String> getQHeartBeats(){
		return qHeartBeats;
	}

	public void setQHeartBeats(Queue<String> qHeartBeats){
		this.qHeartBeats = qHeartBeats;
	}
	
	public Queue<String> getQRepaire(){
		return qRepaire;
	}

	public void setQRepaire(Queue<String> qRepaire){
		this.qRepaire = qRepaire;
	}
	
	public List<Queue<String>> getQMonitorList(){
		return qMonitorList;
	}
	
	public void setQMonitorList(List<Queue<String>> qMonitorList){
		this.qMonitorList = qMonitorList;
	}
	
	public MonitorTaskDao getMonitorTaskDao(){
		return monitorTaskDao;
	}

	public void setMonitorTaskDao(MonitorTaskDao monitorTaskDao){
		this.monitorTaskDao = monitorTaskDao;
	}
	
	public List<FileIndexDao> getFileIndexDaoList(){
		return fileIndexDaoList;
	}
	
	public void setFileIndexDaoList(List<FileIndexDao> fileIndexDaoList){
		this.fileIndexDaoList = fileIndexDaoList;
	}
	
	public List<MonitorThread> getMonitorThreadList(){
		return monitorThreadList;
	}
	
	public void setMonitorThreadList(List<MonitorThread> monitorThreadList){
		this.monitorThreadList = monitorThreadList;
	}
	
	public List<BackupAndCheckThread> getBackupAndCheckThreadList(){
		return backupAndCheckThreadList;
	}
	
	public void setBackupAndCheckThreadList(List<BackupAndCheckThread> backupAndCheckThreadList){
		this.backupAndCheckThreadList = backupAndCheckThreadList;
	}
	
//	private ConfigBean configBean;

//	private Queue<String> qHeartBeats;

//	private HeartBeatsThread heartBeatsThread ;
	

	
//	public ConfigBean getConfigBean(){
//		return configBean;
//	}
//	
//	public void setConfigBean(ConfigBean configBean){
//		this.configBean = configBean;
//	}
//	
//	public Queue<String> getQHeartBeats(){
//		return qHeartBeats;
//	}
//	
//	public void setQHeartBeats(Queue<String> qHeartBeats){
//		this.qHeartBeats = qHeartBeats;
//	}
//
//	public HeartBeatsThread getHeartBeatsThread(){
//		return heartBeatsThread;
//	}
//	
//	public void setHeartBeatsThread(HeartBeatsThread heartBeatsThread){
//		this.heartBeatsThread = heartBeatsThread;
//	}
	


}
