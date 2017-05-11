package com.okami.core;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.common.HttpHandler;
import com.okami.config.DBConfig;
import com.okami.dao.impl.CacheLogDao;
import com.okami.dao.impl.FileIndexDao;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;


/**
 * 控制心中类,到时候名字要改成auto
 * @author orleven
 * @date 2017年2月9日
 */
@Component
public class ControlCenter {
	@Autowired
	private HttpHandler httpHandler;
	
	@Autowired
	private ConfigBean configBean;;
	
	@Autowired
	private GlobaVariableBean globaVariableBean;
	
	@Autowired
	private RepaireThread repaireThread;
	
	
	public ControlCenter(){
		CacheLogDao cacheLogDao = new CacheLogDao();
		cacheLogDao.setDataSource(new DBConfig().dataSource());
		try {
			if(!cacheLogDao.isTableExist()){
				cacheLogDao.createTable();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			IOC.log.error(e1.getMessage());
		}
	}

	/**
	 * 初始化函数
	 * @data 2017年4月3日
	 * @return
	 */
	public boolean init(){

        // 初始化路径
        initPath();
        
		// 恢复线程
		repaireThread.init();
		repaireThread.start();
		
		return true;
	}
	
	/**
	 * 初始化路径
	 * @data 2017年4月3日
	 * @return
	 */
	private boolean initPath(){
		// 获取配置
		configBean = IOC.instance().getClassobj(ConfigBean.class);
		
		// 初始化目录
		File file;
		file = new File(configBean.getStoragePath());
		if (!file.exists()) {
			file.mkdir();
		}
		file = new File(configBean.getBakPath());
		if (!file.exists()) {
			file.mkdir();
		}
		file = new File(configBean.getCachPath());
		if (!file.exists()) {
			file.mkdir();
		}

		return true;
	}
	
	
	/**
	 * 自动加载
	 * @return
	 * @throws Exception 
	 */
	public boolean audoLoad(){
		try {
			// 读取数据库中之前的任务
			List<MonitorTask> monitorTaskList = globaVariableBean.getMonitorTaskDao().queryTask();
			// 循环导入之前的任务
			for(MonitorTask monitorTask:monitorTaskList){
				// 运行模式开启
				if(monitorTask.getRunMode() == 1 ||monitorTask.getRunMode() == 2){
					if(monitorTask.getStatus()==1)
						startMonitor(monitorTask);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
			IOC.log.error(e.getMessage());
		}
		
		return true;
	}

	/**
	 * 开启监控线程
	 * @data 2017年4月10日
	 * @return
	 */
	public boolean startMonitor(MonitorTask monitorTask){
		FileIndexDao fileIndexDao = new FileIndexDao();
		
		// 人工模式
		if(monitorTask.getRunMode() == 1){
			if(humanMonitor(monitorTask,fileIndexDao)){
	    		try {
	    			monitorTask.setStatus(1);
					globaVariableBean.getMonitorTaskDao().updateTask(monitorTask);
				} catch (Exception e) {
	        		e.printStackTrace();
	        		IOC.log.error(e.getMessage());
				}
				return true;
			}
		}
		
		// 防篡改模式
		else if(monitorTask.getRunMode() == 2){

			if(!safeMonitor(monitorTask,fileIndexDao)){
				globaVariableBean.getQHeartBeats().offer(DataUtil.getTime() + 
						"\tInfo\tThe Bak Index File Is Lost: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
				globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+
						"\tInfo\tStop Monitor: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
				IOC.log.warn("Info: The Bak Index File Is Lost: "+ monitorTask.getMonitorPath());  
				IOC.log.warn("Info: Stop Monitor: " + monitorTask.getMonitorPath());  
				return false;
			}else{
	    		try {
	    			monitorTask.setStatus(1);
					globaVariableBean.getMonitorTaskDao().updateTask(monitorTask);
				} catch (Exception e) {
	        		e.printStackTrace();
	        		IOC.log.error(e.getMessage());
				}
			}

		}
		
		return true;
	}
	
	/**
	 * 人工模式
	 * @data 2017年4月10日
	 * @return
	 */
	public boolean humanMonitor(MonitorTask monitorTask,FileIndexDao fileIndexDao){
		
		Queue<String> qMonitor = new LinkedList<String>();
		
		// 创建监控线程
		MonitorThread monitorThread = IOC.instance().getClassobj(MonitorThread.class);
		monitorThread.init(monitorTask,fileIndexDao);
		monitorThread.setQqueue(globaVariableBean.getQHeartBeats(),qMonitor , globaVariableBean.getQRepaire());
		if(!monitorThread.start()){
			return false;
		}
		
		globaVariableBean.getQMonitorList().add(qMonitor);
		globaVariableBean.getMonitorThreadList().add(monitorThread);
		globaVariableBean.getBackupAndCheckThreadList().add(new BackupAndCheckThread());
		globaVariableBean.getFileIndexDaoList().add(fileIndexDao);
		globaVariableBean.getMonitorTaskList().add(monitorTask);
		return true;
	}
	
	/**
	 * 防篡改模式,备份模式
	 * @data 2017年4月10日
	 * @return
	 */
	public boolean safeMonitor(MonitorTask monitorTask,FileIndexDao fileIndexDao){
		// 初始化
		String bakPathStr = configBean.getBakPath()+File.separator+monitorTask.getFlagName();
		Queue<String> qMonitor = new LinkedList<String>();
		
		// 备份模式下删除原来的树
		if(monitorTask.getBCMode() == 0){

	
//			File bakPath = new File(bakPathStr);
//			if(bakPath.exists()){
//				FileUtil.deleteAll(bakPath);
//			}
		}
		
		// 自检模式模式下,检查文件树
		else{
			
			// 下载服务器的文件树
			IOC.log.warn("Info: Searching For Bak Index File: " + monitorTask.getMonitorPath());
			globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+
					"\tInfo\tSearching For Bak Index File: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
			File bakFlag = new File(configBean.getBakPath()+File.separator+monitorTask.getFlagName());
			byte[] contentBytes = httpHandler.download(monitorTask.getFlagName());
			String cachPath = configBean.getCachPath()+File.separator+monitorTask.getTaskName()+File.separator+monitorTask.getFlagName();
			if(contentBytes!=null){
				File file = new File(configBean.getCachPath()+File.separator+monitorTask.getTaskName());
				if(!file.exists()){
					file.mkdirs();
				}
				FileUtil.write(cachPath, contentBytes,false);
				
				// 检查flag树的Sha1，与服务其那边的sha1 是否相同，不正常则恢复flag与文件
				if(!bakFlag.exists()|| !DataUtil.getSHA1ByFile(bakFlag).equals(DataUtil.getSHA1ByFile(new File(cachPath)))){
					globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+
							"\tRepaire\tThe Bak Index File Is Inconsistent: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
					IOC.log.warn("Repaire: The Bak Index File Is Inconsistent: " + monitorTask.getMonitorPath());
					// 将cach下的flag文件复制到bak目录下
					File source = new File(cachPath);
					File dest = new File(configBean.getBakPath()+File.separator+monitorTask.getFlagName());
					try {
						FileUtil.deleteAll(dest);
						Files.copy(source.toPath(), dest.toPath());
					} catch (IOException e) {
		        		e.printStackTrace();
		        		IOC.log.error(e.getMessage());
					}catch (Exception e) {
		        		e.printStackTrace();
		        		IOC.log.error(e.getMessage());
					}
					IOC.log.warn("Repaire-Machine: The Bak Index File Has Fixed: " + monitorTask.getMonitorPath());
					globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+
							"\tRepaire-Machine\tThe Bak Index File Has Fixed: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
				}
			
			}else{
				if(!bakFlag.exists()){
					IOC.log.warn("Info: The Bak Index Index File Is Not Exist, Stop Monitor: " + monitorTask.getMonitorPath());
					globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+
							"\tInfo\tThe Bak Index Index File Is Not Exist, Stop Monitor: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
					return false;
				}
				IOC.log.warn("Info: Assume That The Backup File Is Normal Without Networking Check: " + monitorTask.getMonitorPath());
				globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+
						"\tInfo\tAssume That The Backup File Is Normal Without Networking Check: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
			} 
			IOC.log.warn("Info: Search Bak Index File Success: " + monitorTask.getMonitorPath());
			globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+
					"\tInfo\tSearch Bak Index Success: " + monitorTask.getMonitorPath()+"\t"+monitorTask.getTaskName());
		}
		
		// 连接数据库		
		try {
			fileIndexDao.setDataSource(new DBConfig().indexDataSource(bakPathStr));
		} catch (Exception e) {
			e.printStackTrace();
			IOC.log.error(e.getMessage());
		}

		
		// 监控线程
		MonitorThread monitorThread = IOC.instance().getClassobj(MonitorThread.class);
		monitorThread.init(monitorTask,fileIndexDao);
		monitorThread.setQqueue(globaVariableBean.getQHeartBeats(), qMonitor, globaVariableBean.getQRepaire());
		if(!monitorThread.start()){
			return false;
		}
		
		// 备份/自检线程
		BackupAndCheckThread backupAndCheckThread = IOC.instance().getClassobj(BackupAndCheckThread.class);
		backupAndCheckThread.init(monitorTask, fileIndexDao);
		backupAndCheckThread.setQqueue(globaVariableBean.getQHeartBeats(), qMonitor, globaVariableBean.getQRepaire());
		backupAndCheckThread.start();
		globaVariableBean.getQMonitorList().add(qMonitor);
		globaVariableBean.getMonitorThreadList().add(monitorThread);
		globaVariableBean.getBackupAndCheckThreadList().add(backupAndCheckThread);
		globaVariableBean.getFileIndexDaoList().add(fileIndexDao);
		globaVariableBean.getMonitorTaskList().add(monitorTask);
		return true;
	}
	
	/**
	 * 关闭监控
	 * @data 2017年4月11日
	 * @param monitorTask
	 * @param fileIndexDao
	 * @return
	 */
	public boolean stopMonitor(String taskName){
		
		// 读取数据库中之前的任务
		List<MonitorTask> monitorTaskList;
		
		monitorTaskList = globaVariableBean.getMonitorTaskList();

		for(int i=0;i<monitorTaskList.size();i++){
			if(monitorTaskList.get(i).getTaskName().equals(taskName)){
				// 关闭监控线程
				globaVariableBean.getMonitorThreadList().get(i).stop();
				
				monitorTaskList.get(i).setStatus(0);
				
				// 更新数据库以及关闭文件树连接
				try{
					globaVariableBean.getMonitorTaskDao().updateTask(monitorTaskList.get(i));
					globaVariableBean.getFileIndexDaoList().get(i).closeConnection();
				}catch (Exception e) {
					e.printStackTrace();
					IOC.log.error(e.getMessage());
				}
				
				

				// 删除相应队列
				globaVariableBean.getQMonitorList().remove(i);
				globaVariableBean.getMonitorThreadList().remove(i);
				globaVariableBean.getBackupAndCheckThreadList().remove(i);
				globaVariableBean.getFileIndexDaoList().remove(i);
				globaVariableBean.getMonitorTaskList().remove(i);
				i--;
				
				

				return true;
			}
		}
		
		return true;
	}
	
}
