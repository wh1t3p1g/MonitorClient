package com.okami.entities;

import java.util.List;
import javax.persistence.*;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 任务实体
 * @author orleven
 * @date 2017年2月13日
 */

@Component
@Scope("prototype")
@Entity
@Table(name="MonitorTask")
public class MonitorTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TaskId") 
    private Integer taskId;

    /**
	 * 任务名，方便区分控制端的备份文件
	 */
    @Column(name = "TaskName") 
	private String taskName;
	
	/**
	 * 项目名，即监控的文件夹名称
	 */
    @Column(name = "ProjectName") 
	private String projectName;
	/**
	 * 监控目录
	 */
    @Column(name = "MonitorPath") 
	private String monitorPath;
	
	/**
	 * 白名单路径列表
	 */
    @Column(name = "WhiteList") 
	private String whiteList;
	
	/**
	 * 黑名单后缀列表
	 */
    @Column(name = "BlackList") 
	private String blackList;
	

	/**
	 * flag 文件名
	 */
    @Column(name = "FlagName") 
	private String flagName;
	
	/**
	 * 监控模式: 0 停止；1 人工模式；2 防篡改模式； (3 扫描；4 返回路径 暂定)
	 */
    @Column(name = "RunMode") 
	private int runMode;
	
	/**
	 * BC模式： 0 备份模式；1 自检模式
	 */
    @Column(name = "BCMode") 
	private int BCMode;
    
    /**
     * 备注
     */
    @Column(name = "Remark") 
	private String remark;
    
    /**
     * 最大文件大小，超过就不上传
     */
    @Column(name = "MaxSize") 
	private String maxSize;
    
    /**
     * 当前运行状态
     */
    @Column(name = "Status") 
    private int status;
    
    /**
     * 当前备份文件上传状态
     */
    @Column(name = "Upload") 
    private int upload;
	
	public void setTaskId(Integer taskId){
		this.taskId = taskId;
	}
	
	public Integer getTaskId(){
		return taskId;
	}
	
	public void setUpload(Integer upload){
		this.upload = upload;
	}
	
	public Integer getUpload(){
		return upload;
	}
	
	public void setStatus(Integer status){
		this.status = status;
	}
	
	public Integer getStatus(){
		return status;
	}
	
	public void setTaskName(String taskName){
		this.taskName = taskName;
	}
	
	public String getTaskName(){
		return taskName;
	}
	
	public void setMaxSize(String maxSize){
		this.maxSize = maxSize;
	}
	
	public String getMaxSize(){
		return maxSize;
	}
	
	public void setProjectName(String projectName){
		this.projectName = projectName;
	}
	
	public String getProjectName(){
		return projectName;
	}
	
	
	public void setFlagName(String flagName){
		this.flagName = flagName;
	}
	
	public String getFlagName(){
		return flagName;
	}
	
	public void setMonitorPath(String monitorPath){
		this.monitorPath = monitorPath;
	}
	
	public String getMonitorPath(){
		return monitorPath;
	}
	
	public void setWhiteList(String whiteList){
		this.whiteList = whiteList;
	}
	
	public String getWhiteList(){
		if(whiteList==null){
			return null;
		}
		return whiteList;
		
	}
	
	public void setBlackList(String blackList){
		this.blackList = blackList;
	}
	
	public String getBlackList(){
		if(whiteList==null){
			return null;
		}
		return blackList;
	}
	
	public void setRunMode(int runMode){
		this.runMode = runMode;
	}
	
	public int getRunMode(){
		return runMode;
	}
	
	public void setBCMode(int BCMode){
		this.BCMode = BCMode;
	}
	
	public int getBCMode(){
		return BCMode;
	}
	
	
	public void setRemark(String remark){
		this.remark = remark;
	}
	
	public String getRemark(){
		return remark;
	}
	
	public MonitorTask(){

	}
	
	
	public MonitorTask(Integer taskId,String taskName,String projectName,String monitorPath,String whiteList,String blackList,String flagName,int runMode,int BCMode,String remark,String maxSize,int status,int upload){
		this.taskId = taskId;
		this.taskName = taskName;
		this.projectName = projectName;
		this.monitorPath = monitorPath;
		this.whiteList = whiteList;
		this.blackList = blackList;
		this.flagName = flagName;
		this.runMode = runMode;
		this.BCMode = BCMode;
		this.remark = remark;
		this.maxSize = "2000000";
		this.status = status;
		this.upload = upload;
	}
	
	public MonitorTask(String taskName){
		this.taskName = taskName;
		this.maxSize = "2000000";
	}
	
	@Override
	public boolean equals(Object obj){
		if (this == obj){
			return true;
		}
		MonitorTask task = (MonitorTask) obj;
		if (taskName != null ?
				!taskName.equals(task.taskName)
				:task.taskName != null){
			return false;
		}
		else {
			return true;
		}	
	}
	
}
