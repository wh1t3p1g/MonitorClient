package com.okami.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.springframework.stereotype.Component;

import com.okami.MonitorClientApplication;
import com.okami.bean.ConfigBean;
import com.okami.bean.GlobaVariableBean;
import com.okami.common.HttpHandler;
import com.okami.config.DBConfig;
import com.okami.dao.impl.FileIndexDao;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.MonitorTask;
import com.okami.util.DataUtil;
import com.okami.util.FileUtil;
import com.okami.util.IniUtil;

/**
 * 控制心中类,到时候名字要改成auto
 * @author orleven
 * @date 2017年2月9日
 */
@Component
public class ControlCenter {
	
	/**
	 * 初始化函数
	 * @data 2017年4月3日
	 * @return
	 */
	public boolean init(){
		// 获取配置
		ConfigBean configBean = IOC.instance().getClassobj(ConfigBean.class);
		  
        // 加载配置文件
        configBean = IniUtil.getConfig(configBean,System.getProperty("user.dir") + File.separator + "config/config.ini");
//        if(configBean == null){
//        	configBean = IOC.instance().getClassobj(ConfigBean.class);
//            configBean.setStoragePath("C:\\Users\\dell\\Desktop\\存储地址");
//            configBean.setLhost("127.0.0.1");
//            configBean.setRhost("192.168.199.183");
//            configBean.setLport("5002");
//            configBean.setRport("80");
//            configBean.setDelay(60);
//            configBean.setRemoteMode(true);
//        	IniUtil.setConfig(configBean,System.getProperty("user.dir") + File.separator + "config/config.ini");
//        	
//        	// 退出程序，并要求使用者设置配置文件
//        }
       
        // 初始化路径
        initPath();
		
		// 创建启动心跳线程
    	HeartBeatsThread heartBeatsThread = IOC.instance().getClassobj(HeartBeatsThread.class);
		heartBeatsThread.init();
		heartBeatsThread.start();
		
		
		// 恢复线程
		RepaireThread repaireThread = IOC.instance().getClassobj(RepaireThread.class);
		repaireThread.init();
		repaireThread.start();
		
		// 功能性函数初始化
		HttpHandler httpHandler = IOC.instance().getClassobj(HttpHandler.class);
		httpHandler.init();
		return true;
	}
	
	/**
	 * 初始化路径
	 * @data 2017年4月3日
	 * @return
	 */
	private boolean initPath(){
		// 获取配置
		ConfigBean configBean = IOC.instance().getClassobj(ConfigBean.class);
		
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
		file = new File(configBean.getLogPath());
		if (!file.exists()) {
			file.mkdir();
		}
//		file = new File(configBean.getCheckPath());
//		if (!file.exists()) {
//			file.mkdir();
//		}
		return true;
	}
	
//	/**
//	 * 遍历路径并且存入数据库,备份前操作
//	 * @data 2017年4月3日
//	 * @return
//	 */
//	public boolean TraversalPath(MonitorTask monitorTask){
//		// 获取配置
//		ConfigBean configBean = IOC.instance().getClassobj(ConfigBean.class);
//		
//		// 创建文件数，存入数据库
//		try {
//			FileIndexDao fileIndexDao = new FileIndexDao();;
//			String bakPath  = configBean.getBakPath()+File.separator + monitorTask.getTaskName();
//			fileIndexDao.setDataSource(new DBConfig().indexDataSource(bakPath+File.separator+monitorTask.getTaskName()));
//			fileIndexDao.connectDB();
//			
//			
//			fileIndexDao.closeConnection();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
	
	/**
	 * 自动加载
	 * @return
	 * @throws Exception 
	 */
	public boolean audoLoad(){
		try {
			// 变量初始化
			GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
			
			// 读取数据库中之前的任务
			List<MonitorTask> monitorTaskList = globaVariableBean.getMonitorTaskDao().queryTask();
			
			// 循环导入之前的任务
			for(MonitorTask monitorTask:monitorTaskList){
				
				// 运行模式开启
				if(monitorTask.getRunMode() == 1 ||monitorTask.getRunMode() == 2){
					startMonitor(monitorTask);
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
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
			humanMonitor(monitorTask,fileIndexDao);
			return true;
		}
		
		// 防篡改模式
		else if(monitorTask.getRunMode() == 2){
		
			if(!safeMonitor(monitorTask,fileIndexDao)){
				GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
				globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+"\tInfo\tThe Bak Index File Is Lost : " + monitorTask.getMonitorPath());
				return false;
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
		GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
		MonitorThread monitorThread = IOC.instance().getClassobj(MonitorThread.class);
		monitorThread.init(monitorTask,fileIndexDao);
		monitorThread.setQqueue(globaVariableBean.getQHeartBeats(),qMonitor , globaVariableBean.getQRepaire());
		monitorThread.start();
		
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
		ConfigBean configBean = IOC.instance().getClassobj(ConfigBean.class);
		GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
		String bakPathStr = configBean.getBakPath()+File.separator+monitorTask.getFlagName();
		Queue<String> qMonitor = new LinkedList<String>();
		
		// 备份模式下删除原来的树
		if(monitorTask.getBCMode() == 0){
			File bakPath = new File(bakPathStr);
			if(bakPath.exists()){
				FileUtil.deleteAll(bakPath);
			}
		}
		
		// 自检模式模式下,检查文件树
		else{
			// 下载服务器的文件树
			HttpHandler httpHandler = IOC.instance().getClassobj(HttpHandler.class);
			File bakFlag = new File(configBean.getBakPath()+File.separator+monitorTask.getFlagName());
			byte[] contentBytes = httpHandler.download(monitorTask.getFlagName());
			String cachPath = configBean.getCachPath()+File.separator+monitorTask.getTaskName()+File.separator+monitorTask.getFlagName();
			if(contentBytes!=null){
				try {
					Files.write(Paths.get(cachPath), contentBytes,StandardOpenOption.CREATE);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				// 检查flag树的Sha1，与服务其那边的sha1 是否相同，不正常则恢复flag与文件
				if(!bakFlag.exists()|| !DataUtil.getSHA1ByFile(bakFlag).equals(DataUtil.getSHA1ByFile(new File(cachPath)))){
					globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+"\tInfo\tThe Bak Index File Is Inconsistent : " + monitorTask.getMonitorPath());
				    
					// 将cach下的flag文件复制到bak目录下
					File source = new File(cachPath);
					File dest = new File(configBean.getBakPath()+File.separator+monitorTask.getFlagName());
					try {
						FileUtil.deleteAll(dest);
						Files.copy(source.toPath(), dest.toPath());
					} catch (IOException e) {
						e.printStackTrace();
					}catch (Exception e) {
						e.printStackTrace();
					}
					globaVariableBean.getQHeartBeats().offer(DataUtil.getTime()+"\tInfo\tThe Bak Index File Has Fixed : " + monitorTask.getMonitorPath());
				}
				
			}else{
				if(!bakFlag.exists()){
					return false;
				}
			} 
		}
		
		// 连接数据库
		fileIndexDao.setDataSource(new DBConfig().indexDataSource(bakPathStr));
		try {
			fileIndexDao.connectDB();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// 监控线程
		MonitorThread monitorThread = IOC.instance().getClassobj(MonitorThread.class);
		monitorThread.init(monitorTask,fileIndexDao);
		monitorThread.setQqueue(globaVariableBean.getQHeartBeats(), qMonitor, globaVariableBean.getQRepaire());
		monitorThread.start();
		
		// 备份/自检线程
		BackupAndCheckThread backupAndCheckThread = new BackupAndCheckThread();
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
		// 变量初始化
		GlobaVariableBean globaVariableBean = IOC.instance().getClassobj(GlobaVariableBean.class);
		
		// 读取数据库中之前的任务
		List<MonitorTask> monitorTaskList;
		try {
			monitorTaskList = globaVariableBean.getMonitorTaskDao().queryTask();

		
			for(int i=0;i<monitorTaskList.size();i++){
				if(monitorTaskList.get(i).getTaskName().equals(taskName)){
					// 关闭
					monitorTaskList.get(i).setRunMode(0);
					
					// 更新数据库以及关闭文件树连接
					globaVariableBean.getMonitorTaskDao().updateTask(monitorTaskList.get(i));
					globaVariableBean.getFileIndexDaoList().get(i).closeConnection();
					
					// 关闭监控线程
					globaVariableBean.getMonitorThreadList().get(i).stop();
					
					// 删除相应队列
					globaVariableBean.getQMonitorList().remove(i);
					globaVariableBean.getMonitorThreadList().remove(i);
					globaVariableBean.getBackupAndCheckThreadList().remove(i);
					globaVariableBean.getFileIndexDaoList().remove(i);
					globaVariableBean.getMonitorTaskList().remove(i);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public void work() {
		
//		if (configBean.getRemoteMode()) {
//
//			while (true) {
//				try {
//
//					byte[] buf = new byte[4096];
//					int len = input.read(buf);
//					String jsonStr = new String(buf, 0, len, "UTF-8");
//					// JSONArray messageJArray = new JSONArray(jsonStr);
//
//					// JSONObject messageJObj = messageJArray.getJSONObject(0);
//					// String command = messageJObj.getString("command");
//
//					TaskBean taskBean = ParameterHandle.jsonStrTOTaskBean(jsonStr, configBean);
//
//					switch (taskBean.getRunMode()) {
//					case 0:
//						// 停止监控
//						for (int i = 0; i < taskBeanList.size(); i++) {
//							if (taskBeanList.get(i).getTaskName().equals(taskBean.getTaskName())) {
//								taskBeanList.get(i).setRunMode(0);
//								// 停止所有相关线程
//							}
//						}
//						break;
//					case 1:
//						// 人工模式
//
//						break;
//					case 2:
//						// 防篡改模式
//						if (startMonitor(taskBean)) {
//							taskBeanList.add(taskBean);
//						}
//						break;
//					case 4:
//						// 扫描
//						break;
//					case 5:
//						// 返回路径
//						break;
//					default:
//						System.out.println("False");
//					}
//
//					output.write("True".getBytes());
//					output.flush();
//
//					socket.close();
//
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//
//			}
//
//		}
//
//		// 本地模式，可用测试
//		else {
//			TaskBean taskBean = new TaskBean("test project", configBean);
//			taskBean.setMonitorPath("C:\\Users\\dell\\Desktop\\测试文件");
//			taskBean.setRunMode(1);
//
//			if (startMonitor(taskBean)) {
//				// taskBeanList.add(taskBean);
//			}
//			try {
//				Thread.sleep(100000L);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
}
