package com.okami.plugin.scanner.core.common;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.BaseTask;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.scanner.impl.FullScanner;
import com.okami.plugin.scanner.core.scanner.impl.FuzzyHashScanner;
import com.okami.plugin.scanner.core.scanner.impl.StaticScanner;
import com.okami.plugin.scanner.core.scanner.impl.StatisticsScanner;
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

    @Autowired
    private FuzzyHashScanner fuzzyHashScanner;
    @Autowired
    private StaticScanner staticScanner;
    @Autowired
    private StatisticsScanner statisticsScanner;
    @Autowired
    private FullScanner fullScanner;

    public int assignTask(){
        switch (task.getType()){
            case 1://仅选择fuzzyhash scan + 所有文件（不包括指定的排除文件）扫描
                // load fuzzy hash scan
                fuzzyHashScanner.setTask(task);
                task.setFuzzHashScanResults(fuzzyHashScanner.calculate());
                MonitorClientApplication.log.info(
                        "FuzzHash Scan Finished, Found "+task.getFuzzHashScanResults().size()
                                +" evil file");
                break;
            case 2://仅选择static scan + 所有文件（不包括指定的排除文件）扫描
                // load static scan
                staticScanner.setTask(task);
                task.setStaticScanResults(staticScanner.calculate());
                MonitorClientApplication.log.info(
                        "Static Scan Finished, Found "+task.getStaticScanResults().size()
                                +" evil file");
                break;
            case 3://仅选择statistics scan + 所有文件（不包括指定的排除文件）扫描
                // load statistic scan
                statisticsScanner.setTask(task);
                task.setStatisticsScanResults(statisticsScanner.calculate());
                MonitorClientApplication.log.info(
                        "Statistic Scan Finished, Found "+task.getStatisticsScanResults().size()
                                +" evil file");
                break;
            default://选择3种扫描算法 + (指定|所有)文件扫描 => fast/full mode 常用方式
                fullScanner.setTask(task);
                fullScanner.calculate();
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
