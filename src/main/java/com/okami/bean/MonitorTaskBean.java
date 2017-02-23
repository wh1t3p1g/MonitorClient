package com.okami.bean;

import java.io.File;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.entities.MonitorTask;


/**
 * 任务bean
 * @author orleven
 * @date 2016年12月31日
 */

@Component
@Scope("prototype")
public class MonitorTaskBean{

	private MonitorTask monitorTask;
	/**
	 * 备份地址
	 */
	private String bakPath;
	
	/**
	 * 缓存地址
	 */
	private String cashPath;
	
	private ConfigBean configBean;
	
	public MonitorTaskBean(){
		monitorTask = new MonitorTask();

	}
	
	public MonitorTaskBean(String taskName,ConfigBean configBean){
		monitorTask = new MonitorTask();
		monitorTask.setTaskName(taskName);
		this.configBean = configBean;
		this.cashPath = configBean.getCashPath()+File.separator + taskName;
		this.bakPath = configBean.getBakPath()+File.separator + taskName;
		monitorTask.setRunMode(0);
		monitorTask.setBCMode(0);
	}
	
	public void setConfigBean(ConfigBean configBean){
		this.configBean = configBean;
	}
	
	public ConfigBean getConfigBean(){
		return configBean;
	}
	
	public void setMonitorTask(MonitorTask monitorTask){
		this.monitorTask = monitorTask;
	}
	
	public MonitorTask getMonitorTask(){
		return monitorTask;
	}

	
//	public void setCashPath(String cashPath){
//		this.cashPath = cashPath;
//	}
	
	public String getCashPath(){
		return cashPath;
	}
	
//	public void setBakPath(String bakPath){
//		this.bakPath = bakPath;
//	}
	
	public String getBakPath(){
		return bakPath;
	}

}
