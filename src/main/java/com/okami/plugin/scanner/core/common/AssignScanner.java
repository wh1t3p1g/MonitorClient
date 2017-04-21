package com.okami.plugin.scanner.core.common;

import com.okami.MonitorClientApplication;
import com.okami.plugin.scanner.bean.BaseTask;
import com.okami.plugin.scanner.bean.FileContent;
import com.okami.plugin.scanner.core.scanner.impl.FuzzyHashScanner;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wh1t3P1g
 * @since 2017/1/3
 */
@Component
@Scope("prototype")
public class AssignScanner {

    private BaseTask task;

    public int assignTask(){

        return 0;
    }

    public BaseTask getTask() {
        return task;
    }

    public void setTask(BaseTask task) {
        this.task = task;
    }
}
