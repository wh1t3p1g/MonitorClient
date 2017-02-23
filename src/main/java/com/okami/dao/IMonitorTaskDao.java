package com.okami.dao;

import java.util.List;

import com.okami.entities.MonitorTask;
/**
 * @author orleven
 * @date 2017年2月13日
 */
public interface  IMonitorTaskDao {
	
	/**
	 * 查询所有Task
	 * @return
	 * @throws Exception
	 */
	 public List<MonitorTask> queryTask() throws Exception ;
	 
	 /**
	  * 插入Task
	  * @return
	  * @throws Exception
	  */
	 public boolean insertTask(MonitorTask task) throws Exception ;
	 
	 /**
	  * 修改Task
	  * @return
	  * @throws Exception
	  */
	 public boolean updateTask(MonitorTask task) throws Exception ;
}
