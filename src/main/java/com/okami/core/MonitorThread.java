package com.okami.core;

import java.awt.print.Printable;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.Queue;

import org.apache.tomcat.jni.OS;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.bean.MonitorTaskBean;
import com.okami.common.DataUtil;

import net.contentobjects.jnotify.*;  

/**
 * 监控模块
 * @author orleven
 * @date 2016年12月31日
 */

public class MonitorThread {  
	
	private MonitorTaskBean monitorTaskBean;
	private Queue<String> qHeartBeats;
	private String name;
	private Listener listener;
	private int watchID;
	private int state;

	public MonitorThread(MonitorTaskBean monitorTaskBean){
		this.monitorTaskBean = monitorTaskBean;
		this.listener = new Listener(monitorTaskBean);
		this.state = 1;
	}
	
	/**
	 * 设置队列
	 * @param qHeartBeats
	 */
	public void setQueue(Queue<String> qHeartBeats){
		this.qHeartBeats = qHeartBeats;
		this.listener.setQueue(qHeartBeats);
		
	}
	
//	public MonitorThread(){
//		this.state = 1;
//	}
	
//	public void setMonitorTaskBean(MonitorTaskBean monitorTaskBean){
//		this.monitorTaskBean = monitorTaskBean;
//	}
	
	public int  getState(){
		return state;
	}
	
	public String  getName(){
		return this.name;
	}
	
	public void  setName(String name){
		this.name = name;
	}

    public void start() {  
    	String modeNameString = null;
    	if(monitorTaskBean.getMonitorTask().getRunMode()==1){
    		modeNameString = "人工模式";
    	}else{
    		modeNameString = "防篡改模式";
    	}
    	System.out.println("开始监控 : " + monitorTaskBean.getMonitorTask().getMonitorPath()+"("+modeNameString+")");
        int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;  
        boolean watchSubtree = true;  
        try {  
        	watchID = JNotify.addWatch(monitorTaskBean.getMonitorTask().getMonitorPath(), mask, watchSubtree, this.listener);  
        	this.state = 1;
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
    
    public void stop() {  
		try {
			boolean res = JNotify.removeWatch(watchID);
			this.state = 0;
			if (!res) {  
	            // invalid  
				System.out.println(!res);
	        } 
			System.out.println(res);
		} catch (JNotifyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        
    }
    

    /**
     * 监控核心
     * @author orleven
     * @date 2017年1月5日
     */
    public class Listener implements JNotifyListener {  
  
    	private MonitorTaskBean monitorTaskBean;
    	private String[] whiteList;
    	private String[] blackList;
    	private String mode;
    	private Queue<String> qHeartBeats;
    	
    	public Listener(MonitorTaskBean monitorTaskBean){
    		this.monitorTaskBean = monitorTaskBean;
    		String whiteStr = monitorTaskBean.getMonitorTask().getWhiteList();
    		String blackStr = monitorTaskBean.getMonitorTask().getBlackList();

    		if(whiteStr!=null){
    			this.whiteList = whiteStr.split(";");
    		}else{
    			this.whiteList = null;
    		}
    		if(blackStr!=null){
    			this.blackList = blackStr.split(";");
    		}else{
    			this.blackList = null;
    		}
    		if(monitorTaskBean.getMonitorTask().getRunMode()==2){
    			mode = "Temp";
    			//告诉心跳，进入临时模式
    		}else if(monitorTaskBean.getMonitorTask().getRunMode()==1){
    			mode = "Human";
    			//告诉心跳，进入人工模式
    		}
    	}
    	
    	/**
    	 * 设置队列
    	 * @param qHeartBeats
    	 */
    	public void setQueue(Queue<String> qHeartBeats){
    		this.qHeartBeats = qHeartBeats;
    	}
    	
        public void fileRenamed(int wd, String rootPath, String oldName, String newName) {  
        	String time = DataUtil.getTime();
        	modeDeal("Created",rootPath,newName,time);
        	modeDeal("Delete",rootPath,oldName,time);
        }  
  
        public void fileModified(int wd, String rootPath, String name) {    
        	String time = DataUtil.getTime();
            modeDeal("Modified",rootPath,name,time);  
        }  
  
        public void fileDeleted(int wd, String rootPath, String name) {  
        	String time = DataUtil.getTime();
        	modeDeal("Delete",rootPath,name,time);
        }  
  
        public void fileCreated(int wd, String rootPath, String name) {  
        	String time = DataUtil.getTime();
        	modeDeal("Created",rootPath,name,time);
        }  
  
        
        public void modeDeal(String action,String path,String name,String time){
        	switch(mode){
        	case "Human":
        		// 告诉心跳，发送消息
        		qHeartBeats.add(time+"\t"+action+"\t"+path+File.separator +name+"\t0");
        		break;
        	case "Safe":
        		safeModeDeal(time,action,path,name);
        		break;
        	case "Temp":

        		break;
        	default:
        		break;
        	}
        }
       
        public void safeModeDeal(String time,String action,String path,String name){
    		boolean whiteFlag = false;
    		boolean blackFlag = false;
    		for(int i=0;i<whiteList.length;i++){
    			if((path+File.separator+name).length()>whiteList[i].length()){
    				if(path.indexOf(whiteList[i]) >= 0){
    					// 白名单
    					whiteFlag = true;
    					if(name.indexOf(".")>=0){
    						String suffix = name.substring(name.indexOf(".")+1).toLowerCase();
    						for(int j=0;j<blackList.length;j++){
    							if(suffix.indexOf(blackList[i].toLowerCase())>=0){
    								// 黑名单
    								blackFlag = true;
    								//repaire();
    							}else{
    								//白名单跳过
    							}
    						}
    					}
    				}
    			}
    		}
        }
        
        public void repaire(){
  
        	
        }
    }  
}  