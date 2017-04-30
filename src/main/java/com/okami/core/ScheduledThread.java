package com.okami.core;


import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.common.HttpHandler;
import com.okami.config.DBConfig;
import com.okami.dao.impl.CacheLogDao;
import com.okami.entities.CacheLog;

@Component
public class ScheduledThread {
    
    @Autowired
    private ConfigBean configBean;
    
    @Autowired
    private GlobaVariableBean globaVariableBean;
    
    private int count = -0 ;
    
    private boolean statusFlag = true;
    
    private boolean diffFlag = true ; 
    
    private boolean sendLogFlag = true;
   
    
    private CacheLogDao cacheLogDao ; 
    
    @Autowired
    private HttpHandler httpHandler;
    

    
    public ScheduledThread(){
        count = -1;
        cacheLogDao = new CacheLogDao();
        cacheLogDao.setDataSource(new DBConfig().dataSource());
    }

    
    @Scheduled(fixedDelay=60*1000) 
    public void heartbeats() {
        String result;
        Date nowTime = new Date();
        Date lastTime = nowTime;
        if (count >=  configBean.getDelay()/60 || count ==-1 ){
            if(count == -1){
            	IOC.log.warn(String.format("Connecting Server (%s:%s) ...",configBean.getRhost(),configBean.getRport()));

            }
    
            result = httpHandler.sendHB();
            if(result==null || result.indexOf("success")<=0){
                statusFlag = false; 
            }else{
                statusFlag = true;
            }
            if(count ==-1){
                diffFlag = !statusFlag;
            }
            if(statusFlag ^ diffFlag){
                diffFlag = statusFlag;
                if(statusFlag){
                	IOC.log.warn(String.format("Connect Server (%s:%s) Success!",configBean.getRhost(),configBean.getRport()));
                }else{              
                	IOC.log.warn(String.format("Connect Server (%s:%s) Failed!",configBean.getRhost(),configBean.getRport()));
                }
            }       
            count = 0 ;
        }
        count ++ ;
        
     // 有消息推送过来
		while(!globaVariableBean.getQHeartBeats().isEmpty()){
			
			if(((nowTime.getTime() - lastTime.getTime())/1000) >= configBean.getDelay()){
				break;
			}
			
			// 先存入数据库
			String text = globaVariableBean.getQHeartBeats().poll();
			String[] textList =  text.split("\t");
			result = httpHandler.sendMonitorEvent(textList[0],textList[1],textList[2]);
			if(result==null || result.indexOf("true")<=0){
				statusFlag = false;
			}else{
				statusFlag = true;
			}
				
			// 网络不通则存入数据库
			if(!statusFlag){
				try {

					CacheLog cacheLog = new CacheLog();
					cacheLog.setTime(textList[0]);
					cacheLog.setType(textList[1]);;
					cacheLog.setEvent(textList[2]);
					cacheLogDao.insertCacheLog(cacheLog);
				} catch (Exception e) {
					IOC.log.error(e.getMessage());
				}
			}
		}
        
    	// 如果有缓存的log 则进行处理
		if(statusFlag&&sendLogFlag){
			try {
				List<CacheLog> CacheLogs = cacheLogDao.queryCacheLog();	
				for(CacheLog cacheLog:CacheLogs){
					//发送
					result = httpHandler.sendMonitorEvent(cacheLog.getTime(),cacheLog.getType(),cacheLog.getEvent());
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
				IOC.log.error(e.getMessage());
			}
		}

    }
    
}
