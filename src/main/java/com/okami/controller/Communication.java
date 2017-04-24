package com.okami.controller;

import com.okami.MonitorClientApplication;
import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.common.PathTree;
import com.okami.config.DBConfig;
import com.okami.core.ControlCenter;
import com.okami.core.HeartBeatsThread;
import com.okami.core.IOC;
import com.okami.core.RepaireThread;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.MonitorTask;
import com.okami.plugin.ScannerApplication;
import com.okami.util.DataUtil;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
    @RequestMapping(value="/remove",method=RequestMethod.POST)
    public String remove(HttpServletRequest request){
    	Map<String,String> res = new HashMap<String, String>();
    	String indexPath = request.getParameter("indexPath");
    	String taskName = request.getParameter("taskName");
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
    	for(int i=0;i<globaVariableBean.getMonitorTaskList().size();i++){
    		if(globaVariableBean.getMonitorTaskList().get(i).getTaskName().equals(taskName)){
    			if(globaVariableBean.getBackupAndCheckThreadList().get(i).getStatus()==0){
    				res.put("message","The Monitor Task Is Backuping Or Checking !" );
    				return DataUtil.toJson(res);
    				
    			}else{
    		        RepaireThread repaireThread = IOC.instance().getClassobj(RepaireThread.class);
    		        if(repaireThread.remove(indexPath.substring(globaVariableBean.getMonitorTaskList().get(i).getMonitorPath().length()), taskName))
    		        {
        				res.put("message","Remove Success!" );
        				return DataUtil.toJson(res);
    		        }
    			}
    		}
    	}
		res.put("message","Remove Falied!" );
		return DataUtil.toJson(res);
    }
    
    /**
     * 获取监控任务信息
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/getMonitor",method=RequestMethod.POST)
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
    				Map<String,String> res = new HashMap<String, String>();
    				res.put("message","This Monirtor Task Is Not Exist!" );
    				return DataUtil.toJson(res);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
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
				e.printStackTrace();
			}
    	}
		Map<String,String> res = new HashMap<String, String>();
		res.put("message","Get Monitor Task Failed!" );
    	return DataUtil.toJson(res);
    }
    
    /**
     * 添加监控任务
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/addMonitor",method=RequestMethod.POST)
    public String addMonitor(HttpServletRequest request){
    	Map<String,String> res = new HashMap<String, String>();
    	String taskName =  request.getParameter("taskName");
    	String flagName = DataUtil.getMD5("flag"+DataUtil.getTimeStamp())+".ind";
    	String projectName =  request.getParameter("projectName");
    	String monitorPath =  request.getParameter("monitorPath");
    	String whiteList =  request.getParameter("whiteList");
    	String blackList =  request.getParameter("blackList");
    	String remark =  request.getParameter("remark");
    	int RunMode = Integer.parseInt(request.getParameter("RunMode"));
    	
    	// 路径不存在
    	if(!new File(monitorPath).exists()){
    		res.put("message","This Path Is Not Exist!" );
    		return DataUtil.toJson(res);
    	}
    	
    	// 数据库中已存在
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
    	MonitorTaskDao monitorTaskDao = globaVariableBean.getMonitorTaskDao();
    	try {
			for(MonitorTask monitorTask:monitorTaskDao.queryTask()){
				if(monitorTask.getMonitorPath().equals(monitorPath)||monitorTask.getTaskName().equals(taskName)){
		    		res.put("message","This Task Is Exist!" );
		    		return DataUtil.toJson(res);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			monitorTaskDao.insertTask(monitorTask);
		} catch (Exception e) {
			e.printStackTrace();
		}
		res.put("message","Add Task Success!" );
		return DataUtil.toJson(res);
    }
    
    /**
     * 开始监控任务
     * @data 2017年4月23日
     * @param request
     * @return
     */
    @RequestMapping(value="/startMonitor",method=RequestMethod.POST)
    public String startMonitor(HttpServletRequest request){
    	Map<String,String> res = new HashMap<String, String>();
    	String taskName =  request.getParameter("taskName");
    	
    	// 查看任务是否运行
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
    	for(MonitorTask monitorTask:globaVariableBean.getMonitorTaskList()){
    		if(monitorTask.getTaskName().equals(taskName)){
    			res.put("message","This Task Is Running!");
    			return DataUtil.toJson(res);
    		}
    	}
    	
    	// 数据库中有执行任务
    	MonitorTaskDao monitorTaskDao = globaVariableBean.getMonitorTaskDao();
    	try {
			for(MonitorTask monitorTask:monitorTaskDao.queryTask()){
				if(monitorTask.getTaskName().equals(taskName)){
			    	ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
			    	if(controlCenter.startMonitor(monitorTask)){
		    			res.put("message","Task Run Success!");
		    			return DataUtil.toJson(res);
			    	}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		res.put("message","This Task Is Not Exist!");
		return DataUtil.toJson(res);
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
    	Map<String,String> res = new HashMap<String, String>();
    	
    	// 查看任务是否运行
    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
    	for(MonitorTask monitorTask:globaVariableBean.getMonitorTaskList()){
    		if(monitorTask.getTaskName().equals(taskName)){
    			ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
    			if(controlCenter.stopMonitor(taskName)){
    				res.put("message","Stop Task Success!");
    				return DataUtil.toJson(res);
    			}
    		}
    	}
    
    	res.put("message","This Task Is Not Running Or Not Exist!");
    	return DataUtil.toJson(res);
    }
    
    @RequestMapping(value="/getPath",method=RequestMethod.POST)
    public String getPath(HttpServletRequest request){
    	String rootPath = request.getParameter("rootPath");
    	System.out.println(rootPath);        
    	File file = new File(rootPath);
		return DataUtil.toJson(PathTree.getPath(file));
    }
    
    
//    @RequestMapping(value="/startMonitor/{taskName}",method=RequestMethod.GET)
//    public String startMonitor(@PathVariable String taskName){
//    	GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
//    	MonitorTask monitorTask = IOC.instance().getClassobj(MonitorTask.class);
// 
//    	monitorTask.setFlagName(DataUtil.getMD5("flag"+DataUtil.getTimeStamp())+".ind");
//    	monitorTask.setProjectName("Test");
//    	monitorTask.setMonitorPath("C:\\Users\\dell\\Desktop\\测试文件");
//    	monitorTask.setWhiteList("Aaa");
//    	monitorTask.setBlackList("php,asp,jsp,html");
//    	monitorTask.setRemark("测试");
//    	monitorTask.setBCMode(0);
//    	monitorTask.setRunMode(1);
//    	monitorTask.setMaxSize("2097152");
//    	
//    	try {
//			globaVariableBean.getMonitorTaskDao().insertTask(monitorTask);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//    	
//    	ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
//    	controlCenter.startMonitor(monitorTask);
//
//    	return "Success ! ";
//    }
}
