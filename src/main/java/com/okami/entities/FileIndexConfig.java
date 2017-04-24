package com.okami.entities;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * 索引配置类
 * @author orleven
 * @date 2017年3月11日
 */
public class FileIndexConfig {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id") 
    private int id;
    
    /**
     * 当前的配置
     */
    @Column(name = "CurrentTime") 
    private String currentTime;
    
    public void setId(int id){
        this.id = id;
    }
    
    public int getId(){
        return id;
    }
    
    public String getCurrentTime(){
        return currentTime;
    }
    
    public void setCurrentTime(String currentTime){
        this.currentTime = currentTime;
    }
}
