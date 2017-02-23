package com.okami.plugin.scanner.bean;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author wh1t3P1g
 * @since  2017/1/1
 * 任务类 传入的任务类
 */
@Component
@Scope("prototype")
public class BaseTask {

    /**
     * 任务名
     */
    private String taskName;
    /**
     * 标识task，唯一id值
     */
    private String taskId;
    /**
     * 扫描路径
     */
    private String scanPath;
    /**
     * 白名单地址  用来扫描图片木马
     */
    private String[] whitePaths;
    /**
     * 扫描等级
     * 1=>基于静态特征扫描（不该出现的地方出现） 依赖特征库
     * 2=>基于文件相似性扫描 依赖特征库
     * 3=>基于统计分析
     * 4=>基于日志分析
     * 格式:各等级之间用等号分割,如1,2,3
     */
    private String scanLevel;
    /**
     * 任务创建时间
     */
    private String createTime;
    /**
     * 任务结束时间
     */
    private String finishTime;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getScanPath() {
        return scanPath;
    }

    public void setScanPath(String scanPath) {
        this.scanPath = scanPath;
    }

    public String[] getWhitePaths() {
        return whitePaths;
    }

    public void setWhitePaths(String[] whitePaths) {
        this.whitePaths = whitePaths;
    }

    public String getScanLevel() {
        return scanLevel;
    }

    public void setScanLevel(String scanLevel) {
        this.scanLevel = scanLevel;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
}
