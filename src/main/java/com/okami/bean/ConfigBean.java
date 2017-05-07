package com.okami.bean;

import java.io.File;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.okami.core.IOC;
import com.okami.util.IniUtil;

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
	 * 脚本位置
	 */
	private String scriptPath;
	
	/**
	 * 备份地址
	 */
	private String bakPath;
	
	/**
	 * 缓存地址
	 */
	private String cachPath;
	
	/**
	 * 将此配置发送给服务器，便于服务器设置监控目录。
	 */
	private String monitorPathList;
	
	public ConfigBean(){
		this.scriptPath = System.getProperty("user.dir");
		Map<String,String> config = IniUtil.getConfig(scriptPath + File.separator + "config/config.ini");
		this.lhost = config.get("lhost");
		this.lport = config.get("lport");
		this.rhost = config.get("rhost");
		this.rport = config.get("rport");
		this.monitorPathList =  config.get("monitorPathList");
		
		try {
			this.delay =Integer.valueOf(config.get("delay")).intValue();
		} catch (NumberFormatException e) {
			IOC.log.error(e.getMessage());
			this.delay = 60;
		}
		this.storagePath = config.get("storagePath");
		setStoragePath(this.storagePath);
	}
	
	public String getStoragePath(){
		return this.storagePath;
	}
	
	private void setStoragePath(String storagePath){
		this.storagePath = storagePath;
		this.bakPath = this.storagePath +File.separator +"bak";
		this.cachPath = this.storagePath +File.separator +"cach";
	}
	
	public String getScriptPath(){
		return this.scriptPath;
	}
	
	
	public void setDelay(int delay){
		this.delay = delay;
	}

	public int getDelay(){
		return this.delay;
	}

	public String getLport(){
		return this.lport;
	}

	public String getLhost(){
		return this.lhost;
	}
	
	public String getRport(){
		return this.rport;
	}
	
	public void setRhost(String rhost){
		this.rhost = rhost;
	}

	public String getMonitorPathList(){
		return this.monitorPathList;
	}
	
	public void setMonitorPathList(String monitorPathList){
		this.monitorPathList = monitorPathList;
	}

	public String getRhost(){
		return this.rhost;
	}
	
	public boolean getRemoteMode(){
		return this.remoteMode;
	}
	
	
	public String getCachPath(){
		return cachPath;
	}
	
	
	public String getBakPath(){
		return bakPath;
	}

	
//	public String getLogPath(){
//		return logPath;
//	}
	
}
