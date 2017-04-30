package com.okami.controller;

import com.okami.MonitorClientApplication;
import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.bean.Message;
import com.okami.bean.PathTree;
import com.okami.config.DBConfig;
import com.okami.core.ControlCenter;
import com.okami.core.IOC;
import com.okami.core.RepaireThread;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.MonitorTask;
import com.okami.plugin.ScannerApplication;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;

import org.apache.catalina.util.URLEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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

    @RequestMapping(value="/hello",method = RequestMethod.GET)
    public String home(){
        return "hello";
    }



    @RequestMapping(value="/getStatus/{id}",method=RequestMethod.GET)
    public String getStatus(@PathVariable int id){
        return ""+id;
    }


    /**
     * 移除对应的文件
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/remove",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String remove(HttpServletRequest request){
    	String indexPath = request.getParameter("indexPath");
    	String taskName = request.getParameter("taskName");
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
    	for(int i=0;i<globaVariableBean.getMonitorTaskList().size();i++){
    		if(globaVariableBean.getMonitorTaskList().get(i).getTaskName().equals(taskName)){
    			if(globaVariableBean.getBackupAndCheckThreadList().get(i).getStatus()==0){
    				return DataUtil.toJson(returnMessage(-1,"Remove: "+indexPath));
    				
    			}else{
    		        RepaireThread repaireThread = IOC.instance().getClassobj(RepaireThread.class);
    		        if(repaireThread.remove(indexPath.substring(globaVariableBean.getMonitorTaskList().get(i).getMonitorPath().length()), taskName))
    		        {
        				return DataUtil.toJson(returnMessage(1,"Remove: "+indexPath));
    		        }
    			}
    		}
    	}
    	return DataUtil.toJson(returnMessage(-2,"Remove: "+indexPath));
    }
    
    /**
     * 获取监控任务信息
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/getMonitor",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String getMonitor(HttpServletRequest request){
    	
    	String taskName =  request.getParameter("taskName");
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
    	MonitorTaskDao monitorTaskDao = globaVariableBean.getMonitorTaskDao();
    	if(taskName!=""){
        	try {
    			MonitorTask monitorTask = monitorTaskDao.queryTaskByTaskName(taskName);
    			if(monitorTask!=null){    				
    				for(MonitorTask mTask:globaVariableBean.getMonitorTaskList()){
    					if(mTask.getTaskName().equals(taskName)){
    						monitorTask.setStatus(1);
    						break;
    					}
    				}
        			Map<String,MonitorTask> res = new HashMap<String, MonitorTask>();
        			res.put( String.valueOf(monitorTask.getTaskId()),monitorTask);
    			}else{
    				return DataUtil.toJson(returnMessage(-2,"Get Monitor: "+taskName));
    			}
    		} catch (Exception e) {
    			IOC.log.error(e.getMessage());
    		}
    	}else{
    		// 返回全部
    		try {
    			Map<String,MonitorTask> res = new HashMap<String, MonitorTask>();
				for(MonitorTask monitorTask:monitorTaskDao.queryTask()){
    				for(MonitorTask mTask:globaVariableBean.getMonitorTaskList()){
    					if(mTask.getTaskName().equals(taskName)){
    						monitorTask.setStatus(1);
    						break;
    					}
    				}
        			res.put( String.valueOf(monitorTask.getTaskId()),monitorTask);
				}
				return DataUtil.toJson(res);
			} catch (Exception e) {
				IOC.log.error(e.getMessage());
			}
    	}
    	return DataUtil.toJson(returnMessage(-7,"Get Monitor: "+taskName));
    }
    
    /**
     * 添加监控任务
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/addMonitor",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String addMonitor(HttpServletRequest request){
    	String taskName =  request.getParameter("taskName");
    	String flagName = taskName + ".ind";
    	String projectName =  request.getParameter("projectName");
    	String monitorPath =  request.getParameter("monitorPath");
    	String whiteList =  request.getParameter("whiteList");
    	String blackList =  request.getParameter("blackList");
    	String remark =  request.getParameter("remark");
    	int RunMode = Integer.parseInt(request.getParameter("RunMode"));
    	if(File.separator.equals("\\")){
    		monitorPath = monitorPath.replaceAll("/", "\\\\");
    		whiteList = whiteList.replaceAll("/", "\\\\");
    		blackList = blackList.replaceAll("/", "\\\\");
    	}else{
    		monitorPath = monitorPath.replaceAll("\\\\", "/");
    		whiteList = whiteList.replaceAll("\\\\", "/");
    		blackList = blackList.replaceAll("\\\\", "/");
    	}
    	
    	// 路径不存在
    	if(!new File(monitorPath).exists()){
        	return DataUtil.toJson(returnMessage(-4,"Add Monitor: "+taskName));
    	}
    	
    	// 数据库中已存在
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
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
        	monitorTask.setMaxSize("2097152");
        	monitorTask.setBCMode(0);
        	monitorTask.setRunMode(RunMode);
        	monitorTask.setStatus(0);
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
    	
    	String taskName =  request.getParameter("taskName");
    	String flagName = taskName + ".ind";
    	String projectName =  request.getParameter("projectName");
    	String monitorPath =  request.getParameter("monitorPath");
    	String whiteList =  request.getParameter("whiteList");
    	String blackList =  request.getParameter("blackList");
    	String remark =  request.getParameter("remark");
    	int RunMode = Integer.parseInt(request.getParameter("RunMode"));
    	
    	if(File.separator.equals("\\")){
    		monitorPath = monitorPath.replaceAll("/", "\\\\");
    		whiteList = whiteList.replaceAll("/", "\\\\");
    		blackList = blackList.replaceAll("/", "\\\\");
    	}else{
    		monitorPath = monitorPath.replaceAll("\\\\", "/");
    		whiteList = whiteList.replaceAll("\\\\", "/");
    		blackList = blackList.replaceAll("\\\\", "/");
    	}
    	
    	// 路径不存在
    	if(!new File(monitorPath).exists()){
        	return DataUtil.toJson(returnMessage(-4,"Edit Monitor: "+taskName));
    	}
    	
    	// 数据库中已存在
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
    	MonitorTaskDao monitorTaskDao = globaVariableBean.getMonitorTaskDao();
    	try {
			for(MonitorTask mTask:monitorTaskDao.queryTask()){
				if(mTask.getMonitorPath().equals(monitorPath)||mTask.getTaskName().equals(taskName)){
					// 修改数据库
					MonitorTask monitorTask = IOC.instance().getClassobj(MonitorTask.class);
		        	monitorTask.setTaskName(taskName);
		        	monitorTask.setMonitorPath(monitorPath);
		        	monitorTask.setBlackList(blackList);
		        	monitorTask.setWhiteList(whiteList);
		        	monitorTask.setProjectName(projectName);
		        	monitorTask.setFlagName(flagName);
		        	monitorTask.setRemark(remark);
		        	monitorTask.setMaxSize("2097152");
		        	monitorTask.setBCMode(0);
		        	monitorTask.setStatus(0);
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
    	String taskName =  request.getParameter("taskName");
    	
    	// 查看任务是否运行
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
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
					
			    	ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
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
    	String taskName =  request.getParameter("taskName");
    	
    	// 查看任务是否运行
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
    	for(MonitorTask monitorTask:globaVariableBean.getMonitorTaskList()){
    		if(monitorTask.getTaskName().equals(taskName)){
    			ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
    			if(controlCenter.stopMonitor(taskName)){
			    	return DataUtil.toJson(returnMessage(1,"Stop Monitor: "+taskName));
    			}
    		}
    	}
    
    	return DataUtil.toJson(returnMessage(-6,"Stop Monitor: "+taskName));
    }
    
    
    @RequestMapping(value="/deleteMonitor",method=RequestMethod.POST, produces = "application/json; charset=utf-8")
    public String deleteMonitor(HttpServletRequest request){
    	String taskName =  request.getParameter("taskName");
    	
    	// 查看任务是否运行
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
    	try {
			for(MonitorTask mTask:globaVariableBean.getMonitorTaskDao().queryTask()){
				if(mTask.getTaskName().equals(taskName)){
					if(mTask.getRunMode()==1||mTask.getRunMode()==2){
						ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
						if(!controlCenter.stopMonitor(taskName)){
							return DataUtil.toJson(returnMessage(-9,"Delete Monitor: "+taskName));
						}
					}
				}
			}
		} catch (Exception e) {
			IOC.log.error(e.getMessage());
			return DataUtil.toJson(returnMessage(-8,"Delete Monitor: "+taskName));
		}
    	
    	// 数据库中有执行任务
    	MonitorTaskDao monitorTaskDao = globaVariableBean.getMonitorTaskDao();
    	try {
    		MonitorTask monitorTask = monitorTaskDao.queryTaskByTaskName(taskName);
    		if(monitorTaskDao.deleteTask(taskName)){	
    		    // 删除备份文件
    			ConfigBean configBean = IOC.instance().getClassobj(ConfigBean.class);
    			File deleteFile = new File(configBean.getBakPath()+File.separator+taskName);
    			if(deleteFile.exists()){
    				FileUtil.deleteAll(deleteFile);
    			}
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
    public String getPath(@RequestParam(value = "rootPath", required = true) String rootPath){ 
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
    		case -8: message = "Mission Failed("+mission+"): Error occurred!"; break;
    		case -9: message = "Mission Failed("+mission+"): This Monitor Task Stop Failed!"; break;
    	}
    	
    	if(status==1){
    		IOC.log.warn(message);
    	}else{
    		IOC.log.warn(message);
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
    		IOC.log.warn(message);
    	}else{
    		IOC.log.warn(message);
    	}
    	
    	Message mg = new Message(status,message);
    	mg.setNode(node);
    	return mg;
    }
    
}
