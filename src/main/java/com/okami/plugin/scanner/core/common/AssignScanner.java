package com.okami.plugin.scanner.core.common;

import com.okami.MonitorClientApplication;
import com.okami.common.AESHander;
import com.okami.common.HttpHandler;
import com.okami.plugin.scanner.bean.BaseTask;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.scanner.impl.FullScanner;
import com.okami.plugin.scanner.core.scanner.impl.FuzzyHashScanner;
import com.okami.plugin.scanner.core.scanner.impl.StaticScanner;
import com.okami.plugin.scanner.core.scanner.impl.StatisticsScanner;
import com.okami.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/1/3
 */
@Component
@Scope("prototype")
public class AssignScanner {

    private BaseTask task;

    private FuzzyHashScanner fuzzyHashScanner;
    private StaticScanner staticScanner;
    private StatisticsScanner statisticsScanner;
    private FullScanner fullScanner;

    @Autowired
    private AESHander aESHander;
    
    @Autowired
    private HttpHandler httpHandler;
    
    public int assignTask(){
        String data=null;
        MonitorClientApplication.log.info("Scan Start");
        switch (task.getType()){
            case 1://仅选择fuzzyhash scan + 所有文件（不包括指定的排除文件）扫描
                // load fuzzy hash scan
                fuzzyHashScanner=MonitorClientApplication.ctx.getBean(FuzzyHashScanner.class);
                fuzzyHashScanner.setTask(task);
                task.setFuzzHashScanResults(fuzzyHashScanner.calculate());
                MonitorClientApplication.log.info(
                        "FuzzHash Scan Finished, Found "+task.getFuzzHashScanResults().size()
                                +" evil file");
                System.out.println(task.getFuzzHashScanResults().toString());
                data="data="+DataUtil.encode(DataUtil.toJson(task.getFuzzHashScanResults()),aESHander)
                                +"&time="+DataUtil.getTime()+"&task_id="+task.getTaskId()
                                +"&size="+task.getFuzzHashScanResults().size();
                httpHandler.sendMessage(data);
                break;
            case 2://仅选择static scan + 所有文件（不包括指定的排除文件）扫描
                // load static scan
                staticScanner=MonitorClientApplication.ctx.getBean(StaticScanner.class);
                staticScanner.setTask(task);
                task.setStaticScanResults(staticScanner.calculate());
                MonitorClientApplication.log.info(
                        "Static Scan Finished, Found "+task.getStaticScanResults().size()
                                +" evil file");
                System.out.println(task.getStaticScanResults().toString());
                data="data="+DataUtil.encode(DataUtil.toJson(task.getStaticScanResults()),aESHander)
                        +"&time="+DataUtil.getTime()+"&task_id="+task.getTaskId()
                        +"&size="+task.getStaticScanResults().size();
                httpHandler.sendMessage(data);
                break;
            case 3://仅选择statistics scan + 所有文件（不包括指定的排除文件）扫描
                // load statistic scan
                statisticsScanner=MonitorClientApplication.ctx.getBean(StatisticsScanner.class);
                statisticsScanner.setTask(task);
                task.setStatisticsScanResults(statisticsScanner.calculate());
                MonitorClientApplication.log.info(
                        "Statistic Scan Finished, Found "+task.getStatisticsScanResults().size()
                                +" evil file");
                System.out.println(task.getStatisticsScanResults().toString());
                data="data="+DataUtil.encode(DataUtil.toJson(task.getStatisticsScanResults()),aESHander)
                        +"&time="+DataUtil.getTime()+"&task_id="+task.getTaskId()
                        +"&size="+task.getStatisticsScanResults().size();
                httpHandler.sendMessage(data);
                break;
            default://选择3种扫描算法 + (指定|所有)文件扫描 => fast/full mode 常用方式
                fullScanner=MonitorClientApplication.ctx.getBean(FullScanner.class);
                fullScanner.setTask(task);
                fullScanner.setScriptExtension(task.getScriptExtension());
                task.setFullScanResults(fullScanner.calculate());
                MonitorClientApplication.log.info(
                        "full Scan Finished, Found "+
                                (task.getFullScanResults().size())
                                +" evil file");
                System.out.println(task.getFullScanResults());
                data="data="+DataUtil.encode(DataUtil.toJson(task.getFullScanResults()),aESHander)
                        +"&time="+DataUtil.getTime()+"&task_id="+task.getTaskId()
                        +"&size="+task.getFullScanResults().size();
                httpHandler.sendMessage(data);
                break;
        }
        return 0;
    }

    public BaseTask getTask() {
        return task;
    }

    public void setTask(BaseTask task) {
        this.task = task;
    }
}
