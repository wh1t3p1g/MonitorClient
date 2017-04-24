package com.okami.plugin.scanner.core.scanner;

import com.okami.plugin.scanner.bean.BaseTask;
import com.okami.plugin.scanner.bean.FileContent;
import java.util.List;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/1/1
 */
public abstract class AbstractScanner {

    private BaseTask task;

    /**
     * 扫描计算返回结果
     * @return RetMetaData
     */
    public abstract Map<String,String> calculate();

    public BaseTask getTask() {
        return task;
    }

    public void setTask(BaseTask task) {
        this.task = task;
    }
}
