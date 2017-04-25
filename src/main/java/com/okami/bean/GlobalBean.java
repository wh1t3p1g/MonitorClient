package com.okami.bean;

import org.springframework.stereotype.Component;

/**
 * 全局变量
 * @author wh1t3P1g
 * @since 2017/4/21
 */
@Component
public class GlobalBean {
    /**
     * 扫描状态 0表示空闲 1表示正在扫描
     */
    private int status=0;

    private String taskName;

    private String taskId;

    private Thread t;

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Thread getT() {
        return t;
    }

    public void setT(Thread t) {
        this.t = t;
    }
}
