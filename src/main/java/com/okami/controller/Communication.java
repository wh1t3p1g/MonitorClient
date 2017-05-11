package com.okami.controller;


import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.bean.Message;
import com.okami.bean.PathTree;
import com.okami.common.AESHander;
import com.okami.common.HttpHandler;
import com.okami.core.ControlCenter;
import com.okami.core.IOC;
import com.okami.core.RepaireThread;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.MonitorTask;

import com.okami.util.DataUtil;
import com.okami.util.FileUtil;
import com.okami.util.IniUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wh1t3P1g
 * @since 2017/1/17
 */
@RestController
public class Communication {
	
	@Autowired
	ConfigBean configBean;
	
	@Autowired
	RepaireThread repaireThread;
	
	@Autowired
	GlobaVariableBean globaVariableBean;
	
	@Autowired
	ControlCenter controlCenter ;
	
    @Autowired
    private AESHander aESHander;

//    @RequestMapping(value="/hello",method = RequestMethod.GET)
//    public String home(){
//        return "hello";
//    }
//
//
//
//    @RequestMapping(value="/getStatus/{id}",method=RequestMethod.GET)
//    public String getStatus(@PathVariable int id){
//        return ""+id;
//    }
    
    /**
     * 设置配置
     * @data 2017年4月30日
     * @param request
     * @return
     */
    @RequestMapping(value="/setDelay",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String setDelay(HttpServletRequest request){
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Set Delay"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Set Delay"));
		}
    	
    	String delayStr = DataUtil.urlDecode(request.getParameter("delay"));
    	int delay = 1;
    	try {
    	    delay = (new Double(Double.valueOf(delayStr)*60)).intValue();
    	    configBean.setDelay(delay);
    	    IniUtil.setDelay(delay,configBean.getScriptPath()+ File.separator + "config/config.ini");
    	} catch (NumberFormatException e) {
    		return DataUtil.toJson(returnMessage(-8,"Set Delay: "+delayStr));
    	}
    	return DataUtil.toJson(returnMessage(1,"Set Delay: "+delayStr));
    }
    
    /**
     * 返回文件内容
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/getSuspiciousFile",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String getSuspiciousFile(HttpServletRequest request){
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Get Suspicious File"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Get Suspicious File"));
		}
    	String indexPath = DataUtil.slashDeal(DataUtil.decode(request.getParameter("filepath"),aESHander));
		File file = new File(indexPath);
		if(!file.exists()){
			return DataUtil.toJson(returnMessage(-11,"Get Suspicious File: "+indexPath));
		}
    	IOC.log.warn("Info: Mission Success(Get Suspicious File: "+indexPath+"): Deal Mission Sucess!");
    	return FileUtil.readAll(indexPath,"UTF-8");
    }

    
    /**
     * 返回文件内容
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/getSuspiciousFileSHA1",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String getSuspiciousFileSHA1(HttpServletRequest request){
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Get Suspicious File SHA1"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Get Suspicious File SHA1"));
		}

    	String indexPath = DataUtil.slashDeal(DataUtil.decode(request.getParameter("filepath"),aESHander));
		File file = new File(indexPath);
		if(!file.exists()){
			return DataUtil.toJson(returnMessage(-11,"Get Suspicious File SHA1: "+indexPath));
		}
    	IOC.log.warn("Info: Mission Success(Get Suspicious File SHA1:"+indexPath+"): Deal Mission Sucess!");
    	return DataUtil.getSHA1ByFile(file);
    }

    /**
     * 移除对应的文件
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/removeFile",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String removeFile(HttpServletRequest request){
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Remove File"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Remove File"));
		}
    	
    	String indexPath = DataUtil.slashDeal(DataUtil.decode(request.getParameter("indexPath"),aESHander));
        if(repaireThread.remove(indexPath))
        {
			return DataUtil.toJson(returnMessage(1,"Remove File: "+indexPath));
        }
    	return DataUtil.toJson(returnMessage(-2,"Remove File: "+indexPath));
    }
    
    /**
     * 移除对应的文件
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/editFile",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String editFile(HttpServletRequest request){
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Edit File"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Edit File"));
		}
    	String indexPath = DataUtil.slashDeal(DataUtil.decode(request.getParameter("indexPath"),aESHander));
    	String fileName = indexPath.substring(indexPath.lastIndexOf(File.separator));
    	byte[] contentBytes = DataUtil.urlDecode(request.getParameter("content")).getBytes();   // 暂定

    	// 先放入缓存文件中
    	ConfigBean configBean = IOC.instance().getClassobj(ConfigBean.class);
    	String cachFileStr = configBean.getCachPath()+File.separator+fileName;
    	File file = new File(cachFileStr);
    	if(file.exists()){
    		FileUtil.deleteAll(file);
    	}
		FileUtil.write(cachFileStr, contentBytes,false);
		
    	if(repaireThread.edit(indexPath,cachFileStr))
        {
			return DataUtil.toJson(returnMessage(1,"Edit File: "+indexPath));
        }    		        
   
    	return DataUtil.toJson(returnMessage(-2,"Edit File"+indexPath));
    }
    
    
    /**
     * 添加监控任务
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/addMonitor",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String addMonitor(HttpServletRequest request){
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
        		return DataUtil.toJson(returnMessage(-12,"Add Monitor"));
        	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Add Monitor"));
		}

    	String taskName =  DataUtil.decode(request.getParameter("taskName"),aESHander);
    	String flagName = taskName+ ".ind";
    	String projectName =  DataUtil.decode(request.getParameter("projectName"),aESHander);
    	String monitorPath =  DataUtil.decode(request.getParameter("monitorPath"),aESHander);
    	String whiteList =  DataUtil.decode(request.getParameter("whiteList"),aESHander);
    	String blackList =  DataUtil.decode(request.getParameter("blackList"),aESHander);
    	String remark =  DataUtil.decode(request.getParameter("remark"),aESHander);
    	int RunMode = Integer.parseInt(DataUtil.decode(request.getParameter("RunMode"),aESHander));
    	monitorPath = DataUtil.slashDeal(monitorPath);
    	whiteList = DataUtil.slashDeal(whiteList);
    	blackList = DataUtil.slashDeal(blackList);
    	
    	// 路径不存在
    	if(!new File(monitorPath).exists()){
        	return DataUtil.toJson(returnMessage(-4,"Add Monitor: "+taskName));
    	}
    	
    	// 数据库中已存在
    	MonitorTaskDao monitorTaskDao = globaVariableBean.getMonitorTaskDao();
   
    	try {
			for(MonitorTask monitorTask:monitorTaskDao.queryTask()){
				if(monitorTask.getMonitorPath().equals(monitorPath)||monitorTask.getTaskName().equals(taskName)){
			    	return DataUtil.toJson(returnMessage(-3,"Add Monitor: "+taskName));
				}
			}
		} catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Add Monitor: "+taskName));
		}
    	
    	// 添加到数据库
    	try {
        	MonitorTask monitorTask = IOC.instance().getClassobj(MonitorTask.class);
        	monitorTask.setTaskName(taskName);
        	monitorTask.setMonitorPath(monitorPath);
        	monitorTask.setBlackList(blackList);
        	monitorTask.setWhiteList(whiteList);
        	monitorTask.setProjectName(projectName);
        	monitorTask.setFlagName(flagName);
        	monitorTask.setRemark(remark);
        	monitorTask.setBCMode(0);
        	monitorTask.setRunMode(RunMode);
        	monitorTask.setStatus(0);
        	monitorTask.setUpload(0);
			monitorTaskDao.insertTask(monitorTask);
		} catch (Exception e) {
			e.printStackTrace();
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Add Monitor: "+taskName));
		}
    	return DataUtil.toJson(returnMessage(1,"Add Monitor: "+taskName));
    }
    
    /**
     * 编辑监控任务
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/editMonitor",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String editMonitor(HttpServletRequest request){
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Edit Monitor"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Edit Monitor"));
		}
    	
    	String taskName =  DataUtil.decode(request.getParameter("taskName"),aESHander);
    	String flagName = taskName+ ".ind";
    	String projectName =  DataUtil.decode(request.getParameter("projectName"),aESHander);
    	String monitorPath =  DataUtil.decode(request.getParameter("monitorPath"),aESHander);
    	String whiteList =  DataUtil.decode(request.getParameter("whiteList"),aESHander);
    	String blackList =  DataUtil.decode(request.getParameter("blackList"),aESHander);
    	String remark =  DataUtil.decode(request.getParameter("remark"),aESHander);
    	int RunMode = Integer.parseInt(DataUtil.decode(request.getParameter("RunMode"),aESHander));
    	
    	monitorPath = DataUtil.slashDeal(monitorPath);
    	whiteList = DataUtil.slashDeal(whiteList);
    	blackList = DataUtil.slashDeal(blackList);
    	
    	// 路径不存在
    	if(!new File(monitorPath).exists()){
        	return DataUtil.toJson(returnMessage(-4,"Edit Monitor: "+taskName));
    	}
    	
    	// 数据库中已存在
    	MonitorTaskDao monitorTaskDao = globaVariableBean.getMonitorTaskDao();
    	try {
			for(MonitorTask mTask:monitorTaskDao.queryTask()){
				if(mTask.getMonitorPath().equals(monitorPath)||mTask.getTaskName().equals(taskName)){
					// 还在运行中
					if(mTask.getStatus()==1){
						return DataUtil.toJson(returnMessage(-5,"Edit Monitor: "+taskName));
					}
					
					// 修改数据库
					MonitorTask monitorTask = IOC.instance().getClassobj(MonitorTask.class);
		        	monitorTask.setTaskName(taskName);
		        	monitorTask.setMonitorPath(monitorPath);
		        	monitorTask.setBlackList(blackList);
		        	monitorTask.setWhiteList(whiteList);
		        	monitorTask.setProjectName(projectName);
		        	monitorTask.setFlagName(flagName);
		        	monitorTask.setRemark(remark);
		        	monitorTask.setBCMode(0);
		        	monitorTask.setStatus(0);
		        	monitorTask.setUpload(0);
		        	monitorTask.setRunMode(RunMode);					
					monitorTaskDao.updateTask(monitorTask);
					
			    	return DataUtil.toJson(returnMessage(1,"Edit Monitor: "+taskName));
				}
			}
		} catch (Exception e) {
			IOC.log.error(e.getMessage());
		}
    	return DataUtil.toJson(returnMessage(-2,"Edit Monitor: "+taskName));
    }
    
    /**
     * 开始监控任务
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/startMonitor",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String startMonitor(HttpServletRequest request){
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Start Monitor"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Start Monitor"));
		}
    	
    	String taskName =  DataUtil.decode(request.getParameter("taskName"),aESHander);
    	
    	// 查看任务是否运行

    	for(MonitorTask monitorTask:globaVariableBean.getMonitorTaskList()){
    		if(monitorTask.getTaskName().equals(taskName)){
    			if(monitorTask.getStatus()==1){
			    	return DataUtil.toJson(returnMessage(-5,"Start Monitor: "+taskName));
        			
    			}
    		}
    	}
    	
    	// 数据库中有执行任务
    	MonitorTaskDao monitorTaskDao = globaVariableBean.getMonitorTaskDao();
    	
    	try {
			for(MonitorTask monitorTask:monitorTaskDao.queryTask()){
				if(monitorTask.getTaskName().equals(taskName)){
					// 以备份模式  测试
					monitorTask.setBCMode(0);
					monitorTask.setUpload(0);
					monitorTaskDao.updateTask(monitorTask);
			    	

			    	if(controlCenter.startMonitor(monitorTask)){
				    	return DataUtil.toJson(returnMessage(1,"Start Monitor: "+taskName));
			    	}
				}
			}
		} catch (Exception e) {
			IOC.log.error(e.getMessage());
		} 
    	return DataUtil.toJson(returnMessage(-2,"Start Monitor: "+taskName));
    }
    
    /**
     * 停止监控任务
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/stopMonitor",method=RequestMethod.POST)
    public String stopMonitor(HttpServletRequest request){    	
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Stop Monitor"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Stop Monitor"));
		}
    	
    	String taskName =  DataUtil.decode(request.getParameter("taskName"),aESHander);
    	
    	// 查看任务是否运行
    	MonitorTask mTask = null;
		try{
			mTask = globaVariableBean.getMonitorTaskDao().queryTaskByTaskName(taskName);
		} catch (Exception e) {
			e.printStackTrace();
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Delete Monitor: "+taskName));
		}
		
		if(mTask!=null&&mTask.getStatus()==1){
			if(!controlCenter.stopMonitor(taskName)){
				return DataUtil.toJson(returnMessage(-9,"Delete Monitor: "+taskName));
			}
		}
    
    	return DataUtil.toJson(returnMessage(1,"Stop Monitor: "+taskName));
    }
    
    
    @RequestMapping(value="/deleteMonitor",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String deleteMonitor(HttpServletRequest request){
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Delete Monitor"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Delete Monitor"));
		}
    	
    	String taskName =  DataUtil.decode(request.getParameter("taskName"),aESHander);
    	
    	// 查看任务是否运行
    	MonitorTask mTask = null;
		try{
			mTask = globaVariableBean.getMonitorTaskDao().queryTaskByTaskName(taskName);
		} catch (Exception e) {
			e.printStackTrace();
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Delete Monitor: "+taskName));
		}
		
		if(mTask!=null&&mTask.getStatus()==1){
			if(!controlCenter.stopMonitor(taskName)){
				return DataUtil.toJson(returnMessage(-9,"Delete Monitor: "+taskName));
			}
		}
			

    	
    	// 数据库中有执行任务
    	MonitorTaskDao monitorTaskDao = globaVariableBean.getMonitorTaskDao();
    	try {
    		if(monitorTaskDao.deleteTask(taskName)){	
    		    // 删除备份文件
    			ConfigBean configBean = IOC.instance().getClassobj(ConfigBean.class);
    			File deleteFile = new File(configBean.getBakPath()+File.separator+taskName);
    			if(deleteFile.exists()){
    				FileUtil.deleteAll(deleteFile);
    			}
    			
//    			// 测试
//    			deleteFile = new File(configBean.getBakPath()+File.separator+monitorTask.getFlagName());
//    			if(deleteFile.exists()){
//    				FileUtil.deleteAll(deleteFile);
//    			}
    			
    			deleteFile = new File(configBean.getCachPath()+File.separator+taskName);
    			if(deleteFile.exists()){
    				FileUtil.deleteAll(deleteFile);
    			}
    			return DataUtil.toJson(returnMessage(1,"Delete Monitor: "+taskName));
    		}
		} catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Delete Monitor: "+taskName));
		} 

		return DataUtil.toJson(returnMessage(-2,"Delete Monitor: "+taskName));
    }
    
    
    
    /**
     * 获取地址
     * @data 2017年4月25日
     * @param rootPath
     * @return
     */
    @RequestMapping(value="/getPath",method=RequestMethod.GET, produces = "application/json; charset=utf-8")
    public String getPath(HttpServletRequest request){ 
    	try{
    		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
	    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
	    	if( Math.abs(token1 - token) >60){
	    		return DataUtil.toJson(returnMessage(-12,"Get Path"));
	    	}
    	}catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Get Path"));
		}
    	
    	String rootPath = DataUtil.decode(request.getParameter("rootPath"),aESHander);
    	if(File.separator.equals("\\")){
    		rootPath = rootPath.replaceAll("/", "\\\\");
    	}else{
    		rootPath = rootPath.replaceAll("\\\\", "/");
    	}
    	File file = new File(rootPath);
    	List<PathTree> pathTree = PathTree.getPath(file);
    	if (pathTree == null ||pathTree.size() == 0 )
    		return DataUtil.toJson(returnMessage(-1,pathTree,"Get Path: "+rootPath));
		return DataUtil.toJson(returnMessage(1,pathTree,"Get Path: "+rootPath));
    }
    
    public Message returnMessage(int status,String mission){
    	String message = "";
    	switch(status){
    		case 1: message = "Mission Success("+mission+"): Deal Mission Sucess!"; break;
    		case -1: message = "Mission Failed("+mission+"): The Monitor Task Is Backuping Or Checking!"; break;
    		case -2: message = "Mission Failed("+mission+"): The Monitor Task Is Not Exist!"; break;
    		case -3: message = "Mission Failed("+mission+"): The Monitor Task Is Exist!";break;
    		case -4: message = "Mission Failed("+mission+"): This Monitor Task's Path Is Not Exist!"; break;
    		case -5: message = "Mission Failed("+mission+"): This Monitor Task Is Running!"; break;
    		case -6: message = "Mission Failed("+mission+"): This Monitor Task Is Not Running Or Not Exist!"; break;
    		case -7: message = "Mission Failed("+mission+"): Get Monitor Task Failed!";break;
    		case -8: message = "Mission Failed("+mission+"): Error Occurred!"; break;
    		case -9: message = "Mission Failed("+mission+"): This Monitor Task Stop Failed!"; break;
    		case -10: message = "Mission Failed("+mission+"): Upload File Failed!"; break;
    		case -11: message = "Mission Failed("+mission+"): This File Is Not Exist!"; break;
    		case -12: message = "Mission Failed("+mission+"): Are You Really A Member Of The Organization?"; break;
    	}
    	
    	if(status==1){
    		IOC.log.warn("Info: "+message);
    	}else{
    		IOC.log.warn("Info: "+message);
    	}
    	
    	return new Message(status,message);
    }
    
    public Message returnMessage(int status,Object node,String mission){
    	String message = "";
    	switch(status){
    		case 1: message = "Mission Success("+mission+"): Deal Mission Sucess!"; break;
    		case -1: message = "Mission Failed("+mission+"): This Path Is Not Exist!"; break;
    	}
    	
    	if(status==1){
    		IOC.log.warn("Info: "+message);
    	}else{
    		IOC.log.warn("Info: "+message);
    	}
    	
    	Message mg = new Message(status,message);
    	mg.setNode(node);
    	return mg;
    }
    
}
