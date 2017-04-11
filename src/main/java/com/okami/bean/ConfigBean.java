package com.okami.bean;

import java.io.File;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 配置类bean
 * @author orleven
 * @date 2016年12月31日
 */
@Component
public class ConfigBean {
	/**
	 * 服务器ip
	 */
	private String rhost;
	
	/**
	 * 连接模式： 1 为连接服务器；2 为本地运行。
	 */
	private boolean remoteMode;
	
	/**
	 * 服务器端口
	 */
	private String rport;
	
	/**
	 * 本机ip
	 */
	private String lhost;
	
	/**
	 * 服务器端口
	 */
	private String lport;
	
	/**
	 * 心跳时间,秒为单位
	 */
	private int delay;
	
	/**
	 * 存储路径,默認脚本位置
	 */
	private String storagePath;
	
	/**
	 * 备份地址
	 */
	private String bakPath;
	
	/**
	 * 缓存地址
	 */
	private String cachPath;
	
//	/**
//	 * 自检地址
//	 */
//	private String checkPath;	
	
	
	/**
	 * 日志地址
	 */
	private String logPath;

	
	public ConfigBean(){
		this.remoteMode = false;
		this.delay = 60;
		this.storagePath = System.getProperty("user.dir");
		setStoragePath(this.storagePath);
	}
	
	public String getStoragePath(){
		return this.storagePath;
	}
	
	public void setStoragePath(String storagePath){
		this.storagePath = storagePath;
		this.bakPath = this.storagePath +File.separator +"bak";
//		this.checkPath = this.storagePath + File.separator + "check";
		this.cachPath = this.storagePath +File.separator +"cach";
		this.logPath = this.storagePath +File.separator +"log";
	}
	
	public void setDelay(int delay){
		this.delay = delay;
	}

	public int getDelay(){
		return this.delay;
	}
	
	public void setLport(String lport){
		this.lport = lport;
	}

	public String getLport(){
		return this.lport;
	}
	
	public void setLhost(String lhost){
		this.lhost = lhost;
	}

	public String getLhost(){
		return this.lhost;
	}
	
	public void setRport(String rport){
		this.rport = rport;
	}

	public String getRport(){
		return this.rport;
	}
	
	public void setRhost(String rhost){
		this.rhost = rhost;
	}

	public String getRhost(){
		return this.rhost;
	}
	
	public boolean getRemoteMode(){
		return this.remoteMode;
	}
	
	public void setRemoteMode(boolean remoteMode){
		this.remoteMode = remoteMode;
	}
	
//	public void setCashPath(String cashPath){
//		this.cashPath = cashPath;
//	}
	
	public String getCachPath(){
		return cachPath;
	}
	
//	public void setBakPath(String bakPath){
//		this.bakPath = bakPath;
//	}
	
	public String getBakPath(){
		return bakPath;
	}
//	
//	public void setLogPath(String logPath){
//		this.logPath = logPath;
//	}
	
	public String getLogPath(){
		return logPath;
	}
	
//	public String getCheckPath(){
//		return checkPath;
//	}
}
