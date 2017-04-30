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
	  * 查询task通过taskname
	  * @data 2017年4月23日
	  * @return
	  * @throws Exception
	  */
	 public MonitorTask queryTaskByTaskName(String taskName) throws Exception ;
	 
	 /**
	  * 删除task通过taskname
	  * @data 2017年4月23日
	  * @return
	  * @throws Exception
	  */
	 public boolean deleteTask(String taskName) throws Exception ;
	 
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
	 
	 /**
	  * 创建表
	  * @data 2017年4月25日
	  * @return
	  * @throws Exception
	  */
	 public boolean createTable() throws Exception;
	 
	 /**
	  * 创建表
	  * @data 2017年4月25日
	  * @return
	  * @throws Exception
	  */
	 public boolean isTableExist() throws Exception;
	 
//	 /**
//	  * 连接数据库
//	  * @data 2017年4月23日
//	  * @return
//	  * @throws Exception
//	  */
//	 public boolean connectDB() throws Exception;
//	 
//	 /**
//	  * 关闭连接数据库
//	  * @data 2017年4月23日
//	  * @return
//	  * @throws Exception
//	  */
//	 public boolean closeConnection() throws Exception;
}
