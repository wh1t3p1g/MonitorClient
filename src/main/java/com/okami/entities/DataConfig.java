package com.okami.entities;


import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="DataConfig")
public class DataConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id") 
    private Integer id;
    /**
	 * 任务名，方便区分控制端的备份文件
	 */
    @Column(name = "Key") 
	private String key;
	
	/**
	 * 项目名，即监控的文件夹名称
	 */
    @Column(name = "Iv") 
	private String iv;
    
	public void setKey(String key){
		this.key = key;
	}
	
	public String getKey(){
		return key;
	}
	
	public void setIv(String iv){
		this.iv = iv;
	}
	
	public String getIv(){
		return iv;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public int  getId(){
		return id;
	}
	
	public DataConfig(int id,String key,String iv){
		this.id = id;
		this.key = key;
		this.iv = iv;
	}
}
