package com.okami.controller;

import com.okami.MonitorClientApplication;
import com.okami.plugin.ScannerApplication;
import com.okami.plugin.scanner.bean.BaseTask;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @author wh1t3P1g
 * @since 2017/4/21
 */

@RestController
public class WebshellController {

    @RequestMapping(value="/webshell/newTask",method=RequestMethod.POST)
    public String test(HttpServletRequest request){
        BaseTask task= MonitorClientApplication.ctx.getBean(BaseTask.class);
        //设置参数
        task.setTaskName(request.getParameter("task_name"));
        task.setTaskId(request.getParameter("task_id"));
        task.setFilePath(request.getParameter("file_path"));
        task.setExceptPath(request.getParameter("except_path"));
        task.setExceptExtension(request.getParameter("except_extension"));
        task.setScriptExtension(request.getParameter("script_extension"));
        task.setFilter(request.getParameter("filter").equals("true"));
        task.setType(Integer.parseInt(request.getParameter("type")));

        ScannerApplication scannerApplication=
                MonitorClientApplication.ctx.getBean(ScannerApplication.class);
        if(scannerApplication.isRunning()){
            return "Task "+scannerApplication.getTask().getTaskName()+"running";
        }else{
            scannerApplication.setTask(task);
            scannerApplication.start();
            return "ok";
        }
    }
}
