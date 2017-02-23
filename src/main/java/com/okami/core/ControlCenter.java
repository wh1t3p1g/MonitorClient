package com.okami.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.okami.MonitorClientApplication;
import com.okami.bean.ConfigBean;
import com.okami.bean.MonitorTaskBean;
import com.okami.bean.ThreadConfigBean;
import com.okami.common.ParameterHandle;
import com.okami.config.DBConfig;
import com.okami.dao.impl.MonitorTaskDao;
import com.okami.entities.MonitorTask;
import com.okami.common.DataUtil;

/**
 * 控制心中类
 * @author orleven
 * @date 2017年2月9日
 */
public class ControlCenter {
	
	private ConfigBean configBean;
	
	private ThreadConfigBean threadConfigBean;
	
	/**
	 * 初始化
	 * @param configBean
	 */
	public ControlCenter(ConfigBean configBean) {
		this.configBean = configBean;
		threadConfigBean =  MonitorClientApplication.ctx.getBean(ThreadConfigBean.class);
		Queue<String> qHeartBeats = new LinkedList<String>();;
		threadConfigBean.setQHeartBeats(qHeartBeats);
		HeartBeatsThread heartBeatsThread = new HeartBeatsThread(configBean, qHeartBeats);
		heartBeatsThread.start();
		threadConfigBean.setHeartBeatsThread(heartBeatsThread);
		File file;
		file = new File(configBean.getBakPath());
		if (!file.exists()) {
			file.mkdir();
		}
		file = new File(configBean.getCashPath());
		if (!file.exists()) {
			file.mkdir();
		}
		file = new File(configBean.getLogPath());
		if (!file.exists()) {
			file.mkdir();
		}
	}
	
	/**
	 * 自动加载
	 * @return
	 * @throws Exception 
	 */
	public boolean audoLoad(){
		try {
			MonitorTaskDao monitorTaskDao = new MonitorTaskDao();
			monitorTaskDao.setDataSource(new DBConfig().dataSource());
			List<MonitorTask> monitorTaskList = monitorTaskDao.queryTask();
			for(int i=0; i<monitorTaskList.size();i++){
				MonitorTaskBean monitorTaskBean =  MonitorClientApplication.ctx.getBean(MonitorTaskBean.class);
				monitorTaskBean.setMonitorTask(monitorTaskList.get(i));			
				threadConfigBean.getMonitorTaskBeans().add(monitorTaskBean);
				
				MonitorThread monitorThread = new MonitorThread(monitorTaskBean);
				monitorThread.setQueue(threadConfigBean.getQHeartBeats());
				monitorThread.start();
				threadConfigBean.getMonitorThreads().add(monitorThread);
				
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return true;
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

	/**
	 * 开始监控
	 * 
	 * @param taskbean
	 * @return
	 */
//	public boolean startMonitor(MonitorTaskBean taskbean) {
//		if (!stopMonitor(taskbean)) {
//			taskbean.setProjectName(DataUtil.getMD5(new Date().toString()));
//		}
//		Queue<String> queue = new LinkedList<>();
//		MonitorThread monitorThread = new MonitorThread(taskbean);
//		// BackupAndCheckThread backupAndCheckThread = new
//		// BackupAndCheckThread(taskbean);
//		// backupAndCheckThread.setName("BackupAndCheckThread-"+taskbean.getProjectName());
//		monitorThread.setName("MonitorThread-" + taskbean.getProjectName());
//		monitorThread.start();
//		// backupAndCheckThread.start();
//
//		taskBeanList.add(taskbean);
//		monitorThreads.add(monitorThread);
//		// backupAndCheckThreads.add(backupAndCheckThread);
//		return true;
//	}
//
//	/**
//	 * 关闭任务
//	 * 
//	 * @param taskbean
//	 * @return
//	 */
//	@SuppressWarnings("deprecation")
//	public boolean stopMonitor(MonitorTaskBean taskbean) {
//
//		for (int i = 0; i < taskBeanList.size(); i++) {
//			if (taskBeanList.get(i).getMonitorPath().equals(taskbean.getMonitorPath())) {
//				MonitorThread monitorThread = monitorThreads.get(i);
//				BackupAndCheckThread backupAndCheckThread = backupAndCheckThreads.get(i);
//
//				// 关闭原来的线程
//				if (monitorThread.getState() == 1) {
//					monitorThread.stop();
//				}
//				if (!backupAndCheckThread.getState().equals(Thread.State.TERMINATED)
//						|| !backupAndCheckThread.getState().equals(Thread.State.NEW)) {
//					backupAndCheckThread.stop();
//				}
//
//				backupAndCheckThread.destroy();
//				monitorThreads.remove(i);
//				backupAndCheckThreads.remove(i);
//
//				taskbean.setProjectName(taskBeanList.get(i).getProjectName());
//				taskBeanList.remove(i);
//				return true;
//			}
//		}
//		return false;
//	}

}
