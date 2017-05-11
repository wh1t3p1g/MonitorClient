package com.okami.core;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.bean.RequrieBean;
import com.okami.config.DBConfig;
import com.okami.dao.impl.FileIndexDao;
import com.okami.entities.FileIndex;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;
import net.contentobjects.jnotify.*;  

/**
 * 监控模块
 * @author orleven
 * @date 2016年12月31日
 */
@Component
@Scope("prototype")
public class MonitorThread {  
    
    // 通用配置
    private MonitorTask monitorTask;
    private FileIndexDao fileIndexDao ;
    
    // 队列
    private Queue<String> qHeartBeats;
    private Queue<String> qMonitor;
    private Stack<RequrieBean> qRepaire;
    
    // 线程属性配置
    private Listener listener;
    private int watchID;
    private int state;
    


    public boolean init(MonitorTask monitorTask,FileIndexDao fileIndexDao){
        this.monitorTask = monitorTask;
        this.fileIndexDao = fileIndexDao;
        this.listener = new Listener(monitorTask,fileIndexDao);
        this.state = 1;
       
        return true;
    }
    
    public void setQqueue(Queue<String> qHeartBeats,Queue<String> qMonitor,Stack<RequrieBean> qRepaire){
        this.qHeartBeats = qHeartBeats;
        this.qMonitor = qMonitor;
        this.qRepaire = qRepaire;
        this.listener.setQqueue(qHeartBeats,qMonitor,qRepaire);
    }
    
        
    public void setQHeartBeats(Queue<String> qHeartBeats){
        this.qHeartBeats = qHeartBeats;
        this.listener.setQHeartBeats(qHeartBeats);
    }
    
    
    public Queue<String> getQMonitor(){
        return qMonitor;
    }
    
    public void setQMonitor(Queue<String> qMonitor){
        this.qMonitor = qMonitor;
        this.listener.setQMonitor(qMonitor);
    }
    
    public void setQRepaire(Stack<RequrieBean> qRepaire){
        this.qRepaire = qRepaire;
        this.listener.setQRepaire(qRepaire);
    }
    
    
    public int  getState(){
        return state;
    }
   

    public boolean start() {  
        int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;  
        boolean watchSubtree = true;  
        try {  
            // 防篡改模式（安全模式）
            if(monitorTask.getRunMode()==2){ 
            	qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tStart Monitor(Safe Mode): " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
            	IOC.log.warn("Info: Start Monitor(Safe Mode): " + monitorTask.getMonitorPath());
            	qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tTurn To Temp Mode: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
                IOC.log.warn("Info: Turn To Temp Mode: " + monitorTask.getMonitorPath());
                this.listener.setMode("Temp");
            }
            // 人工模式
            else if(monitorTask.getRunMode()==1){
                this.listener.setMode("Human");
                qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tStart Monitor(Human Mode): " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
                IOC.log.warn("Info: Start Monitor(Human Mode): " + monitorTask.getMonitorPath());
            }
            
            watchID = JNotify.addWatch(monitorTask.getMonitorPath(), mask, watchSubtree, this.listener);  
            this.state = 1;
            return true;
        } catch (Exception e) {  
    		e.printStackTrace();
    		IOC.log.error(e.getMessage());
        	return false;
        }  
    }  
    
    public void stop() {  
        try {
            boolean res = JNotify.removeWatch(watchID);
            this.state = 0;
            if (!res) {  
                // invalid  
            } 
            monitorTask.setStatus(0);
			IOC.log.warn("Info: Stop Monitor: " +monitorTask.getMonitorPath());
			qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tStop Monitor: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
			
        	
        } catch (JNotifyException e) {
    		e.printStackTrace();
    		IOC.log.error(e.getMessage());
        }  
        
    }
    

    /**
     * 监控核心
     * @author orleven
     * @date 2017年1月5日
     */
    public class Listener implements JNotifyListener {  
  
        private FileIndexDao fileIndexDao;
        private MonitorTask monitorTask;
        private String[] whiteList ;
        private String[] blackList;
        private String mode;
        private Queue<String> qHeartBeats;
        private Queue<String> qMonitor;
        private Stack<RequrieBean> qRepaire;

        
        public Listener(MonitorTask monitorTask,FileIndexDao fileIndexDao){
            this.monitorTask = monitorTask;
            this.fileIndexDao = fileIndexDao;
            String whiteStr = monitorTask.getWhiteList();
            String blackStr = monitorTask.getBlackList();
                        
            
            if(whiteStr!=null&&!whiteStr.equals("")){
                this.whiteList = whiteStr.split(",");
            }
            else{
                this.whiteList = null;
            }
            if(blackStr!=null&&!blackStr.equals("")){
                this.blackList = blackStr.split(",");
            }else{
                this.blackList = null;
            }
        
        }
        
        public void setQHeartBeats(Queue<String> qHeartBeats){
            this.qHeartBeats = qHeartBeats;
        }
        
        public void setQMonitor(Queue<String> qMonitor){
            this.qMonitor = qMonitor;
        }
        
        public void setQRepaire(Stack<RequrieBean> qRepaire){
            this.qRepaire = qRepaire;
        }
        
        public void setQqueue(Queue<String> qHeartBeats,Queue<String> qMonitor,Stack<RequrieBean> qRepaire){
            this.qHeartBeats = qHeartBeats;
            this.qMonitor = qMonitor;
            this.qRepaire = qRepaire;
        }
        
        public void setMode(String mode){
            this.mode = mode;
        }
        
    
        public void fileRenamed(int wd, String rootPath, String oldName, String newName) {  
            String time = DataUtil.getTime();
            // rename 
            modeDeal(time,"Renamed",rootPath,oldName,newName);
//            modeDeal("Deleted",rootPath,oldName,time);
//            modeDeal("Created",rootPath,oldName,time);
        }  
  
        public void fileModified(int wd, String rootPath, String name) {    
            String time = DataUtil.getTime();
            modeDeal(time,"Modified",rootPath,name,null);  
        }  
  
        public void fileDeleted(int wd, String rootPath, String name) {  
            String time = DataUtil.getTime();
            modeDeal(time,"Deleted",rootPath,name,null);
        }  
  
        public void fileCreated(int wd, String rootPath, String name) {  
            String time = DataUtil.getTime();
            modeDeal(time,"Created",rootPath,name,null);
        }  
  
        /**
         * 统一处理
         * @param action
         * @param path
         * @param name
         * @param time
         */
        public void modeDeal(String time,String action,String path, String oldName, String newName){
            
        	String oldFilename = path+ File.separator + oldName;
        	String newFilename = null;
        	if(newName!=null){
        		newFilename = path+ File.separator + newName;
        	}
        	if(monitorTask.getStatus()==0){
        		ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
        		controlCenter.stopMonitor(monitorTask.getTaskName());
        	}
            switch(mode){
            case "Human":
            	if(action.equals("Renamed")){
            		qHeartBeats.offer(time+"\t"+action+"\t"+ oldFilename+" To "+path + newFilename+"\t"+monitorTask.getTaskName());
          			IOC.log.warn(action + ": " + oldFilename+" To "+path + newFilename);
            	}else{
            		qHeartBeats.offer(time+"\t"+action+"\t"+ oldFilename+"\t"+monitorTask.getTaskName());
          			IOC.log.warn(action + ": " + oldFilename);
            	}
              
                break;
            case "Safe":
                checkNameList(time,action,oldFilename,newFilename);
                break;
            case "Temp":
                if(qMonitor.isEmpty()){
                    // 备份时发生文件操作，停止备份、监控、还原线程
                    if(monitorTask.getBCMode()==0){
                        IOC.log.warn(action+": "+oldFilename);
                        qHeartBeats.offer(time+"\t"+action+"\t"+oldFilename+"\t"+monitorTask.getTaskName());
                        qHeartBeats.offer(DataUtil.getTime()+"\tInfo\tDon't operate files when backing up: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
                    	IOC.log.warn("Info: Don't operate files when backing up:" + monitorTask.getMonitorPath());
                		ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
                		controlCenter.stopMonitor(monitorTask.getTaskName());
                    }
                    
                    // 自检
                    else{
                        checkNameList(time,action,oldFilename,newFilename);
                    }
                }else{
                    if(qMonitor.poll() == "True"){
                        
                        mode = "Safe";
                    }
                    checkNameList(time,action,oldFilename,newFilename);
                }
                break;
            default:
                break;
            }
        }
       
        /**
         * 判断是否属于黑白名单
         * @param time
         * @param action
         * @param path
         * @param name
         */
        public void checkNameList(String time,String action,String filename,String newFilename){
            // 安全模式

            if(mode.equals("Safe")){
                boolean whiteFlag = false;
                boolean blackFlag = false;
                boolean renameFlag = false;
                String parentPath = filename.substring(0,filename.lastIndexOf(File.separator));
                if(whiteList!=null){
	                for(int i=0;i<whiteList.length;i++){
	                	if(parentPath.indexOf(whiteList[i]) == 0){  // 不应该大于0
	                        // 白名单
	                        whiteFlag = true;
	                        if(blackList!=null){
		                        String name = filename.substring(filename.lastIndexOf(File.separator));
		                        if(name.indexOf(".")>=0){
		                            String suffix = name.substring(name.indexOf(".")+1).toLowerCase();
		                            for(int j=0;j<blackList.length;j++){
		                                // 黑名单
		                                if(suffix.indexOf(blackList[j].toLowerCase())>=0){
		                                    blackFlag = true;
		                                    repaire(time,action,filename,newFilename);
		                                    break;
		                                }
		                            }
		                        }
		                        if(newFilename!=null){
		                        	String newName = newFilename.substring(newFilename.lastIndexOf(File.separator));
			                        if(newName.indexOf(".")>=0){
			                            String suffix = newName.substring(newName.indexOf(".")+1).toLowerCase();
			                            for(int j=0;j<blackList.length;j++){
			                                // 黑名单
			                                if(suffix.indexOf(blackList[j].toLowerCase())>=0){
			                                	renameFlag = true;
			                                	repaire(time,action,filename,newFilename);
			                                    break;
			                                }
			                            }
			                        }
		                        }
		                        
	                        }
	                        // 不在黑名单，即白名单里面
	                        if(!blackFlag&&whiteFlag&&!renameFlag){
	                            qHeartBeats.offer(time+"\t"+action+"\t"+filename+"\t"+monitorTask.getTaskName());
	                            IOC.log.warn(action + ": " + filename);
	                        }
	                        break;
	                    }
	                }
                }
                // 不在白名单
                if(!whiteFlag){
                  repaire(time,action,filename,newFilename);
                }
            }
            
            // 自检模式
            else{
              repaire(time,action,filename,newFilename);
            }
        }
        
        /**
         * 修复函数
         */
        /**
         * 修复函数
         */
        public void repaire(String time,String action,String filename,String newFilename) {
        	if(monitorTask.getStatus()==0){
        		ControlCenter controlCenter = IOC.instance().getClassobj(ControlCenter.class);
        		controlCenter.stopMonitor(monitorTask.getTaskName());
        	}
        	try {
        		// 默认选择第一个，以后如果有版本区别的话再根据版本查询
        		String indexPath = filename.substring(monitorTask.getMonitorPath().length());  // 数据库中的path
        		FileIndex fileIndex = fileIndexDao.queryIndexByPath(indexPath);
				
                // 检测到文件被创建
                if(action.equals("Created")){
                	// 如果flag文件中没有该文件，则进行删除
                	if(fileIndex==null){
                		qRepaire.push(new RequrieBean(action, time,monitorTask.getMonitorPath(),indexPath ,"", monitorTask.getTaskName()));
	                }
	                // flag文件中存在，则进行md5校验
	                else{
	                	if(fileIndex.getType().equals("File")){
	                		qRepaire.push(new RequrieBean(action, time,monitorTask.getMonitorPath(),indexPath ,fileIndex.getSha1(), monitorTask.getTaskName()));
		                }
	                }
                }
				
                
	            else if(action.equals("Deleted")){
	            	// 如果flag文件中有该文件，则进行还原
		            if(fileIndex!=null){
		            	qRepaire.push(new RequrieBean(action, time,monitorTask.getMonitorPath(),indexPath ,fileIndex.getSha1(), monitorTask.getTaskName()));
		            }
	            }
                
	            else if(action.equals("Modified")){
	            	// 如果flag文件中有该文件，则进行还原
	            	if(fileIndex!=null){
	                	if(fileIndex.getType().equals("File")){
	                		qRepaire.push(new RequrieBean(action, time,monitorTask.getMonitorPath(),indexPath ,fileIndex.getSha1(), monitorTask.getTaskName()));
		                }
	            	}
	            }
                
	            else if(action.equals("Renamed")){

	            	File file = new File(newFilename);
	        		String newIndexPath = newFilename.substring(monitorTask.getMonitorPath().length());  // 数据库中的path
	        		FileIndex newFileIndex = fileIndexDao.queryIndexByPath(newIndexPath);
	            	if(fileIndex!=null){
	            		if(newFileIndex!=null){
	            			if(file.isFile()){
	            				if(DataUtil.getSHA1ByFile(file).equals(fileIndex.getSha1())){
	            					if(DataUtil.getSHA1ByFile(file).equals(newFileIndex.getSha1())){
	            						qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
		    	            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
		    	            			File distFile = new File(filename);
		    	            			Files.copy(file.toPath(), distFile.toPath());
		            					qHeartBeats.offer(time+"\t"+action+"-Machine\t"+filename+" To "+newFilename+" Deal Success!"+"\t"+monitorTask.getTaskName());
		    	    					IOC.log.warn(action + "-Machine: " + filename+" To "+newFilename+" Deal Success!");
	            					}else{
	            						qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
		    	            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
		            					file.renameTo(new File(filename));
		    	            			// 处理原来被覆盖掉的文件
		    	            			qRepaire.push(new RequrieBean(action, time,monitorTask.getMonitorPath(),newIndexPath ,newFileIndex.getSha1(), monitorTask.getTaskName(),filename));
	            					}
	            				}
	            			}else{
		                		qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
		            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
	            				file.renameTo(new File(filename));
	            				qRepaire.push(new RequrieBean(action, time,monitorTask.getMonitorPath(),newIndexPath ,newFileIndex.getSha1(), monitorTask.getTaskName(),filename));
	            			}
	            		}else{
	                		qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
	            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
	            			file.renameTo(new File(filename));
	            			qHeartBeats.offer(time+"\t"+action+"-Machine\t"+filename+" To "+newFilename+" Deal Success!"+"\t"+monitorTask.getTaskName());
	    					IOC.log.warn(action + "-Machine: " + filename+" To "+newFilename+" Deal Success!");
	            		}
	            	}else{
	            		if(newFileIndex!=null){
	            			if(file.isFile()){
		            			if(!DataUtil.getSHA1ByFile(file).equals(newFileIndex.getSha1())){
			                		qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
			            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
		            				qRepaire.push(new RequrieBean(action, time,monitorTask.getMonitorPath(),newIndexPath ,newFileIndex.getSha1(), monitorTask.getTaskName(),filename));
		            			}
	            			}
	            		}else{
	                		qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
	            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
	            			FileUtil.deleteAll(file);
	            			qHeartBeats.offer(time+"\t"+action+"-Machine\t"+filename+" To "+newFilename+" Deal Success!"+"\t"+monitorTask.getTaskName());
	    					IOC.log.warn(action + "-Machine: " + filename+" To "+newFilename+" Deal Success!");
	            		}
	            	}
	            		
//	            		if(newFileIndex!=null&&!DataUtil.getSHA1ByFile(file).equals(newFileIndex.getSha1())){
//	                		qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
//	            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
//	            			file.renameTo(new File(filename));
//	            			// 处理原来被覆盖掉的文件
//	            			qRepaire.push(new RequrieBean(action, time,monitorTask.getMonitorPath(),newIndexPath ,newFileIndex.getSha1(), monitorTask.getTaskName(),filename));
//	            		}
//	            		else if(newFileIndex==null){
//	                		qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
//	            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
//	            			file.renameTo(new File(filename));
//	            			qHeartBeats.offer(time+"\t"+action+"-Machine\t"+filename+" To "+newFilename+" Deal Success!"+"\t"+monitorTask.getTaskName());
//	    					IOC.log.warn(action + "-Machine: " + filename+" To "+newFilename+" Deal Success!");
//	            		}
//
//	            	}else{
//	            		if(newFileIndex!=null&&!DataUtil.getSHA1ByFile(file).equals(newFileIndex.getSha1())){
//	            			if(file.isDirectory()){
//	            				
//	            			}else{
//	            				
//	            			}
//	                		qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
//	            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
//	            			// 恢复原来的文件
//	            			qRepaire.push(new RequrieBean(action, time,monitorTask.getMonitorPath(),newIndexPath ,newFileIndex.getSha1(), monitorTask.getTaskName(),filename));
//	            	
//	            		}
//	            		else if(newFileIndex==null){
//	                		qHeartBeats.offer(time+"\t"+action+"\t"+ filename+" To "+newFilename+"\t"+monitorTask.getTaskName());
//	            			IOC.log.warn(action + ": " + filename+"s To "+newFilename);
//	            			FileUtil.deleteAll(file);
//	            			qHeartBeats.offer(time+"\t"+action+"-Machine\t"+filename+" To "+newFilename+" Deal Success!"+"\t"+monitorTask.getTaskName());
//	    					IOC.log.warn(action + "-Machine: " + filename+" To "+newFilename+" Deal Success!");
//	            		}
//	            	}
	            }
                
        	} catch (Exception e) {
        	
        		e.printStackTrace();
        		IOC.log.error(e.getMessage());

        	}

        }
    }  
}  