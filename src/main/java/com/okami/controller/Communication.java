package com.okami.controller;

import com.okami.MonitorClientApplication;
import com.okami.bean.ConfigBean;
import com.okami.bean.MonitorTaskBean;
import com.okami.common.DataUtil;
import com.okami.config.DBConfig;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.MonitorTask;
import com.okami.plugin.ScannerApplication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping(value="/getPath",method=RequestMethod.POST)
    public String getPath(HttpServletRequest request){
        return request.getParameter("rootPath");
    }

    @RequestMapping(value="/getStatus/{id}",method=RequestMethod.GET)
    public String getStatus(@PathVariable int id){
        return ""+id;
    }

    @RequestMapping(value="/setTask")
    public String setTask(){
        ScannerApplication scannerApplication= MonitorClientApplication.ctx.getBean(ScannerApplication.class);
        scannerApplication.run();
        return "";
    }
    @RequestMapping(value="/startMonitor/{taskName}",method=RequestMethod.GET)
    public String startMonitor(@PathVariable String taskName){
    	ConfigBean configBean = MonitorClientApplication.ctx.getBean(ConfigBean.class);
    	MonitorTaskBean monitorTaskBean = new MonitorTaskBean(taskName,configBean);
    	monitorTaskBean.getMonitorTask().setProjectName("Test");
    	monitorTaskBean.getMonitorTask().setMonitorPath("C:\\Users\\dell\\Desktop\\测试文件");
    	monitorTaskBean.getMonitorTask().setBlackList("php,asp,jsp,html");
    	monitorTaskBean.getMonitorTask().setFlagName(DataUtil.getMD5("flag"+DataUtil.getTimeStamp()));
    	monitorTaskBean.getMonitorTask().setBCMode(0);
    	monitorTaskBean.getMonitorTask().setRemark("测试");
    	monitorTaskBean.getMonitorTask().setRunMode(1);
		try {
			MonitorTaskDao monitorTaskDao = new MonitorTaskDao();
			monitorTaskDao.setDataSource(new DBConfig().dataSource());
			monitorTaskDao.insertTask(monitorTaskBean.getMonitorTask());
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return "Success ! ";
    }

}
