package com.okami.bean;

/**
 * require交流队列
 * @author orleven
 * @date 2017年5月5日
 */
public class RequrieBean {

	private String action;
	private String time;
	private String monitorPath;
	private String srcRename;
	private String indexPath;
	private String taskName;
	private String Sha1;
	
	
	public RequrieBean(String action,String time,String monitorPath,String indexPath,String Sha1,String taskName){
		this.action = action;
		this.time = time;
		this.monitorPath = monitorPath;
		this.taskName = taskName;
		this.Sha1 = Sha1;
		this.indexPath = indexPath;
	}
	
	public RequrieBean(String action,String time,String monitorPath,String indexPath,String Sha1,String taskName,String srcRename){
		this.action = action;
		this.time = time;
		this.monitorPath = monitorPath;
		this.taskName = taskName;
		this.Sha1 = Sha1;
		this.indexPath = indexPath;
		this.srcRename = srcRename;
	}
	
	public String getAction(){
		return this.action;
	}
	public String getTime(){
		return this.time;
	}
	public String getMonitorPath(){
		return this.monitorPath;
	}
	public String getSha1(){
		return this.Sha1;
	}
	public String getIndexPath(){
		return this.indexPath;
	}
	public String getTaskName(){
		return this.taskName;
	}
	
	public String getSrcRename(){
		return this.srcRename;
	}
	
	public String getFileName(){
		return this.monitorPath + this.indexPath;
	}
}
