package com.okami.entities;

import javax.persistence.*;

import com.okami.util.DataUtil;

/**
 * 缓存日志实体
 * @author orleven
 * @date 2017年2月19日
 */
@Entity
@Table(name="CacheLog")
public class CacheLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id") 
    private Integer id;

    @Column(name = "Type") 
    private String type;

    @Column(name = "Event") 
    private String event;

    @Column(name = "Time") 
    private String time;
    
    @Column(name = "TaskName") 
    private String taskName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }
    
    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
	public CacheLog(Integer id,String type,String time,String event,String taskName){
		this.id = id;
		this.type = type;
		this.time = time;
		this.event = event;
		this.taskName = taskName;
	}
	
	public void setLog(String type,String event,String taskName){
		this.type = type;
		this.time = DataUtil.getTime();
		this.event = event;
		this.taskName = taskName;
	}
	
	public CacheLog(){

	}
    
}
