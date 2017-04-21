package com.okami.plugin;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.BaseTask;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.common.AssignScanner;
import com.okami.plugin.scanner.core.common.EnumFiles;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ScannerApplication implements Runnable{

    private Thread t;

    private BaseTask task;
    /**
     * 扫描状态 0表示空闲 1表示正在扫描
     */
    private int status=0;
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

    public ScannerApplication(){
        status=0;
    }

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

        if(this.isRunning()){//检查是否是在运行
            // 通知服务器端
            // todo
            return ;//0 表示正在运行
        }
        status=1;
        //枚举所有文件
        enumFiles.setTask(task);
        List<FileContent> fileContents=enumFiles.run();
        task.setFileContents(fileContents);
        MonitorClientApplication.log.info("共"+fileContents.size()+"待扫描文件");
        //分配scanner
        assignScanner.setTask(task);
        int result=assignScanner.assignTask();//返回分配结果
        // result通知服务器端
        // todo
    }

    public void start () {
        if (t == null) {
            t = new Thread (this);
            t.start ();
        }
    }

    public Boolean isRunning(){
        return status==1;
    }

    public BaseTask getTask() {
        return task;
    }

    public void setTask(BaseTask task) {
        this.task = task;
    }
}
