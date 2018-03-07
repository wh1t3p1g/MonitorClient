package com.okami.controller;

import com.okami.MonitorClientApplication;
import com.okami.bean.GlobalBean;
import com.okami.common.AESHander;
import com.okami.plugin.ScannerApplication;
import com.okami.plugin.scanner.bean.BaseTask;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.bean.WebshellFeatures;
import com.okami.plugin.scanner.core.common.EnumFiles;
import com.okami.plugin.scanner.core.trainer.GenerateArff;
import com.okami.plugin.scanner.core.trainer.NavieBayesClassifier;
import com.okami.plugin.scanner.core.trainer.TrainerDataSet;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author wh1t3P1g
 * @since 2017/4/21
 */

@RestController
public class WebshellController {

    @Autowired
    private GlobalBean globalBean;

    private ScannerApplication scannerApplication;
    
    @Autowired
    private AESHander aESHander;

    @RequestMapping(value="/webshell/task/new",method=RequestMethod.POST)
    public String newTask(HttpServletRequest request){
		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
    	if( Math.abs(token1 - token) >60){
    		 return "Are You Really A Member Of The Organization?";
    	}
    	
        if(globalBean.getStatus()==1){
            return "Task "+globalBean.getTaskName()+" is running";
        }else{
            scannerApplication=MonitorClientApplication.ctx.getBean(ScannerApplication.class);
            BaseTask task= MonitorClientApplication.ctx.getBean(BaseTask.class);
            //设置参数
            task.setTaskName(request.getParameter("task_name"));
            task.setTaskId(request.getParameter("task_id"));
            task.setFilePath(request.getParameter("file_path"));
            task.setExceptPath(request.getParameter("except_path"));
            task.setExceptExtension(request.getParameter("except_extension"));
            task.setScriptExtension(request.getParameter("script_extension"));
            task.setFilter(true);
            task.setType(Integer.parseInt(DataUtil.decode(request.getParameter("type"),aESHander)));
            task.setMode(Integer.parseInt(DataUtil.decode(request.getParameter("mode"),aESHander)));
            scannerApplication.setTask(task);
            scannerApplication.start();
            return "Task "+task.getTaskName()+" ok";
        }
    }
    @RequestMapping(value="/webshell/task/stop",method=RequestMethod.GET)
    public String stopTask(HttpServletRequest request){
		int token =  Integer.parseInt(DataUtil.decode(request.getParameter("_"),aESHander));
    	int token1 = Integer.parseInt(DataUtil.getTimeStamp());
    	if( Math.abs(token1 - token) >60){
    		 return "Are You Really A Member Of The Organization?";
    	}
    	
    	
        if(globalBean.getStatus()==1){
            globalBean.getT().interrupt();
            globalBean.setStatus(0);
            return "Stop success";
        }else{
            return "Nothing to Stop";
        }
    }
    @RequestMapping(value="/webshell/dataset/generate",method=RequestMethod.POST)
    public String generateDataSets(HttpServletRequest request){
        String path=request.getParameter("path");
        String toPath=request.getParameter("toPath");
        String flag=request.getParameter("flag");
        EnumFiles enumFiles=MonitorClientApplication.ctx.getBean(EnumFiles.class);
        BaseTask task=MonitorClientApplication.ctx.getBean(BaseTask.class);
        task.setFilePath(path);
        task.setFilter(false);
        enumFiles.setTask(task);
        List<FileContent> fileContents=enumFiles.run();
        GenerateArff generateArff=MonitorClientApplication.ctx.getBean(GenerateArff.class);
        List<TrainerDataSet> trainerDataSetList=generateArff.generateData(fileContents,flag);
        Instances instances=generateArff.generateInstances(trainerDataSetList,"train");
        generateArff.generateArffFile(instances,toPath);
        return "success";
    }

    @RequestMapping(value="/webshell/training",method=RequestMethod.POST)
    public String training(HttpServletRequest request){
        String trainPath=request.getParameter("train_path");
        String testPath=request.getParameter("test_path");
        NavieBayesClassifier navieBayesClassifier=
                MonitorClientApplication.ctx.getBean(NavieBayesClassifier.class);
        navieBayesClassifier.loadArff(trainPath);
        navieBayesClassifier.training(testPath);
        return "success";
    }

    @RequestMapping(value="/webshell/prediction",method=RequestMethod.GET)
    public String prediction(){
        NavieBayesClassifier navieBayesClassifier=
                MonitorClientApplication.ctx.getBean(NavieBayesClassifier.class);
        return "";
    }
    

    @RequestMapping(value="/webshell/addConfig",method=RequestMethod.POST)
    public String addConfig(HttpServletRequest request){
        String session = request.getParameter("session");
        String name = request.getParameter("name");
        String value = request.getParameter("value");
        WebshellFeatures webshellFeatures= MonitorClientApplication.ctx.getBean(WebshellFeatures.class);
        if(webshellFeatures.add(session, name, value)){
        	return "success";
        }
        return "failed";
    }
    
    @RequestMapping(value="/webshell/deleteConfig",method=RequestMethod.POST)
    public String deleteConfig(HttpServletRequest request){
        String session = request.getParameter("session");
        String name = request.getParameter("name");
        String value = request.getParameter("value");
        WebshellFeatures webshellFeatures= MonitorClientApplication.ctx.getBean(WebshellFeatures.class);
        if(webshellFeatures.add(session, name, value)){
        	return "success";
        }
        return "failed";
    }
    
    @RequestMapping(value="/webshell/getConfig",method=RequestMethod.GET)
    public String getConfig(){
        return FileUtil.readAll("config/webshellFeatures.ini","UTF-8");
    }
}
