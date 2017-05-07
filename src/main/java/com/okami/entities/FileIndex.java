package com.okami.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 文件索引
 * @author orleven
 * @date 2017年3月11日
 */
@Entity
@Table(name="index")
public class FileIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id") 
    private int id;
    
    /**
     * 路径
     */
    @Column(name = "Path") 
    private String path;
    
    /**
     * sha-1
     */
    @Column(name = "Sha1") 
    private String sha1;
    
    @Column(name = "Size") 
    private String size;
    
    /**
     * 修改时间
     */
    @Column(name = "Time") 
    private String time;
    
    /**
     * 类型
     */
    @Column(name = "Type") 
    private String type;
    
    /**
     * 可读
     */
    @Column(name = "Read") 
    private int read;
    
    /**
     * 可写
     */
    @Column(name = "Write") 
    private int write;
    
    /**
     * 可执行
     */
    @Column(name = "Exec") 
    private int exec;
    
    /**
     * 所有者
     */
    @Column(name = "Owner") 
    private String owner;
    
    /**
     * 所有组
     */
    @Column(name = "OwnerGroup") 
    private String ownerGroup;
    
    /**
     * 状态，1为在用，0为不用
     */
    @Column(name = "Status") 
    private int status;
    
    /**
     * 压缩文件所在的rarid
     */
    @Column(name = "RarId") 
    private String rarId;
    
    public int getId(){
        return id;
    }
    
    public int getWrite(){
        return write;
    }
    
    
    public int getRead(){
        return read;
    }
    
    public int getExec(){
        return exec;
    }
    
    
    public String getPath(){
        return path;
    }
    
    public String getSha1(){
        return sha1;
    }
    
    public String getSize(){
        return size;
    }
    
    public String getTime(){
        return time;
    }
    
    public String getType(){
        return type;
    }
    
    public String getOwner(){
        return owner;
    }
    
    public String getOwnerGroup(){
        return ownerGroup;
    }
    
    public int getStatus(){
        return status;
    } 
    
    public String getRarId(){
        return rarId;
    }
    
    public void setId(int id){
        this.id = id;
    }
    
    public void setRead(int read){
        this.read = read;
    }
    
    public void setWrite(int write){
        this.write = write;
    }
    
    public void setExec(int exec){
        this.exec = exec;
    }
    
    public void setPath(String path){
        this.path = path;
    }
    
    public void setSha1(String sha1){
        this.sha1 = sha1;
    }
    
    public void setSize(String size){
        this.size = size;
    }
    
    public void setTime(String time){
        this.time = time;
    }
    
    public void setType(String type){
        this.type = type;
    }
    
    public void setOwner(String owner){
        this.owner = owner;
    }
    
    public void setOwnerGroup(String ownerGroup){
        this.ownerGroup = ownerGroup;
    }
    
    public void setStatus(int status){
        this.status = status;
    }
    
    public void setRarId(String rarId){
        this.rarId = rarId;
    }
    
    public FileIndex(int id,String path,String sha1,String size,String type,String time,String owner,String ownerGroup,int status,int read,int write,int exec,String rarId){
    	this.id = id;
    	this.path = path;
    	this.sha1 = sha1;
    	this.size = size;
    	this.time = time;
    	this.type = type;
    	this.owner = owner;
    	this.ownerGroup = ownerGroup;
    	this.status = status;
    	this.read = read;
    	this.write = write;
    	this.exec = exec;
    	this.rarId = rarId;
    }
    
    public FileIndex(int id,String path,String sha1,String size,String type,String rarId){
    	this.id = id;
    	this.path = path;
    	this.sha1 = sha1;
    	this.size = size;
    	this.type = type;
    	this.rarId = rarId;
    }
    
    public FileIndex(){
    	
    }
}
