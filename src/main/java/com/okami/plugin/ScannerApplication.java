package com.okami.plugin;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.BaseTask;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.bean.GlobalBean;
import com.okami.plugin.scanner.core.common.AssignScanner;
import com.okami.plugin.scanner.core.common.EnumFiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 整个程序只能生成一个ScannerApplication
 * 意味着扫描任务只能同时执行一个
 * 为了使扫描尽可能不影响服务器
 * @author wh1t3P1g
 * @since 2017/1/15
 */
@Component
@Scope("prototype")
public class ScannerApplication implements Runnable{

    private Thread t;

    private BaseTask task;

    /**
     * 注入枚举文件对象
     */
    @Autowired
    private EnumFiles enumFiles;
    /**
     * 注入scanner分配器
     */
    @Autowired
    private AssignScanner assignScanner;
    @Autowired
    private GlobalBean globalBean;

    /**
     * 程序的主体
     * 描述扫描的所有过程
     * @return -1 0 1
     * -1 扫描出错
     * 0  扫描正在运行
     * 1  扫描成功
     */
    @Override
    public void run(){

        if(globalBean.getStatus()==1){//检查是否是在运行
            return ;//0 表示正在运行
        }
        task.decodeAll();
        globalBean.setStatus(1);
        globalBean.setTaskId(task.getTaskId());
        globalBean.setTaskName(task.getTaskName());
        //枚举所有文件
        enumFiles.setTask(task);
        try{
            List<FileContent> fileContents=enumFiles.run();
            task.setFileContents(fileContents);
            MonitorClientApplication.log.info("共"+fileContents.size()+"待扫描文件");
            //分配scanner
            assignScanner.setTask(task);
            globalBean.setStatus(assignScanner.assignTask());//返回分配结果
            // result通知服务器端
        }catch (Exception e){
            MonitorClientApplication.log.error(e.getMessage());
            globalBean.setStatus(0);
        }
    }

    public void start () {
        if (t == null) {
        	try{
	            t = new Thread (this);
	            globalBean.setT(t);
	            t.start ();
        	}catch(Exception e){
        		t.interrupt();
        	}
        }
    }

    public BaseTask getTask() {
        return task;
    }

    public void setTask(BaseTask task) {
        this.task = task;
    }
}
