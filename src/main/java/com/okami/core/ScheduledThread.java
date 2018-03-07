package com.okami.core;


import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.common.AESHander;
import com.okami.common.HttpHandler;
import com.okami.config.DBConfig;
import com.okami.dao.impl.CacheLogDao;
import com.okami.entities.CacheLog;
import com.okami.entities.DataConfig;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;

@Component
public class ScheduledThread {
    
    @Autowired
    private ConfigBean configBean;
    
    @Autowired
    private GlobaVariableBean globaVariableBean;
    
    @Autowired
    private AESHander aESHander;
    
    private int count = -0 ;
    
    private boolean statusFlag = true;
    
    private boolean diffFlag = true ; 
    
    private boolean sendLogFlag = true;
    
    private Date nowTime ;
    
    private Date lastTime ;
   
    
    private CacheLogDao cacheLogDao ; 
    
    @Autowired
    private HttpHandler httpHandler;
    

    
    public ScheduledThread(){
        count = -1;
        cacheLogDao = new CacheLogDao();
        cacheLogDao.setDataSource(new DBConfig().dataSource());
    }
    
    /**
     * 发送心跳
     * @data 2017年5月1日
     */
    public void sendHB(){
    	if(!httpHandler.getFlag()){
    		httpHandler.init();
    	}
    	String result = null;
        nowTime = new Date();
        lastTime = nowTime;
        if (count >=  configBean.getDelay()/10 || count ==-1 ){
            if(count == -1){
            	IOC.log.warn(String.format("Info: Connecting Server (%s:%s) ...",configBean.getRhost(),configBean.getRport()));
            }
            result = httpHandler.sendHB();
            if(result!=null ){
            	HashMap<String,Object> text  = null;
            	int status ;
            	try{
            		text = DataUtil.fromJson(result);
            		status = (new Double((Double)text.get("status"))).intValue();
            	}catch(Exception e){
            		status = -1;
            	}
            	
                
                if(status==1){
                    statusFlag = true; 
                }else if (status == 2||status == 3){
                    statusFlag = true;
                   	String key = text.get("_").toString().substring(0,16);
                	String iv =  text.get("_").toString().substring(16);
                	DataConfig dataConfig = new DataConfig(1,key,iv);
					try {
						aESHander.AESInit(key, iv);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
                    try {
                    	if(globaVariableBean.getDataConfigDao().queryDataConfig()!=null){
                    		globaVariableBean.getDataConfigDao().updateDataConfig(dataConfig);
                    	}
                    	else{
                    		globaVariableBean.getDataConfigDao().insertDataConfig(dataConfig);
                    	}
    				} catch (Exception e) {
    					
    				}
                    httpHandler.setHbPostParameters(); 
                    result = httpHandler.sendHB();
                }else{
                	statusFlag = false; 
                }
            }else{
            	statusFlag = false; 
            }
            
            
            if(count ==-1){
                diffFlag = !statusFlag;
            }
            if(statusFlag ^ diffFlag){
                diffFlag = statusFlag;
                if(statusFlag){
                	IOC.log.warn(String.format("Info: Connect Server (%s:%s) Success!",configBean.getRhost(),configBean.getRport()));
                }else{              
                	IOC.log.warn(String.format("Info: Connect Server (%s:%s) Failed!",configBean.getRhost(),configBean.getRport()));
                }
            }       
            count = 0 ;
        }
        count ++ ;
    }
    
    /**
     * 有消息推送过来
     * @data 2017年5月1日
     */
    public void sendMessage(){
    	
		while(!globaVariableBean.getQHeartBeats().isEmpty()){
			String result = null;;
			if(((nowTime.getTime() - lastTime.getTime())/1000) >= configBean.getDelay()){
				break;
			}
			
			// 先存入数据库
			String text = globaVariableBean.getQHeartBeats().poll();
			String[] textList =  text.split("\t");
			if(statusFlag){
				result = httpHandler.sendMonitorEvent(textList[0],textList[1],textList[2],textList[3]);
				if(result==null || result.indexOf("true")<=0){
					statusFlag = false;
				}else{
					statusFlag = true;
				}
			}
				
			// 网络不通则存入数据库
			if(!statusFlag){
				try {
					CacheLog cacheLog = new CacheLog();
					cacheLog.setTime(textList[0]);
					cacheLog.setType(textList[1]);;
					cacheLog.setEvent(textList[2]);
					cacheLog.setTaskName(textList[3]);
					cacheLogDao.insertCacheLog(cacheLog);
				} catch (Exception e) {
	        		e.printStackTrace();
	        		IOC.log.error(e.getMessage());
				}
			}
		}
        
    }
    
    public void sendCachLog(){
    	
    	if(statusFlag&&sendLogFlag){
			try {
				String result = null;;
				List<CacheLog> CacheLogs = cacheLogDao.queryCacheLog();	
				for(CacheLog cacheLog:CacheLogs){
					//发送
					result = httpHandler.sendMonitorEvent(cacheLog.getTime(),cacheLog.getType(),cacheLog.getEvent(),cacheLog.getTaskName());
					if(result==null || result.indexOf("true")<=0)
						statusFlag = false;
					else
						statusFlag = true;
					
					if(!statusFlag){
						break;
					}
					cacheLogDao.deleteCacheLog(cacheLog);
				}
			} catch (Exception e) {
        		e.printStackTrace();
        		IOC.log.error(e.getMessage());
			}
		}
		
    }
    
    /**
     * 发送文件
     * @data 2017年5月1日
     */
    public void sendFile(){
    	if(statusFlag){
    		String result = null;;
			boolean flag = true;
			
			try {
				for(MonitorTask mTask:globaVariableBean.getMonitorTaskDao().queryTask()){
					if(mTask.getUpload()==0&&mTask.getBCMode()==1){
						// 上传rar文件
						String cachFold = configBean.getCachPath() + File.separator + mTask.getTaskName();
						File[] files = new File(cachFold).listFiles();
						if(files!=null&&files.length!=0){
							for(File file:files){
								result = httpHandler.upload(file);
								if(result==null || result.indexOf("success")<=0)
								{
									statusFlag = false;
									flag = false;
									break;
								}
							}
							
							//  上传flag文件
							File file = new File(configBean.getBakPath()  + File.separator +mTask.getFlagName());
							result = httpHandler.upload(file);
							if(result==null || result.indexOf("success")<=0){
								statusFlag = false;
								flag = false;
								break;
							}
							else{
								statusFlag = true;
							}
							
							if(flag){
								// 更新数据库的上传标志
								try {
									mTask.setUpload(1);
									globaVariableBean.getMonitorTaskDao().updateTask(mTask);
								} catch (Exception e) {
									e.printStackTrace();
									IOC.log.error(e.getMessage());
								}
								
								httpHandler.sendMonitorEvent(DataUtil.getTime(),"Info","Upload Success: "+mTask.getMonitorPath(),mTask.getTaskName());
								IOC.log.warn("Info: Upload Success: " + mTask.getMonitorPath());
								
								// 上传后删除rar文件
								File cashPath= new File(cachFold);
								if(cashPath.exists()){
									FileUtil.deleteAll(cashPath);
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				IOC.log.warn(e.getMessage());
			}
		}
    }
    
    @Scheduled(fixedDelay=10*1000) 
    public void heartbeats() {
    	// 发生心跳
    	sendHB();

    	// 有消息推送过来
    	sendMessage();
    	
    	// 如果有缓存的log 则进行处理
    	sendCachLog();
		
		// 文件没有上传的继续上传
    	sendFile();

    }
    
}
