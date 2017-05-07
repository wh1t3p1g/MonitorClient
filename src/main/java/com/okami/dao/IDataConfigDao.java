package com.okami.dao;

import java.util.List;

import com.okami.entities.DataConfig;
import com.okami.entities.MonitorTask;

public interface IDataConfigDao {
	/**
	 * 查询所有Task
	 * @return
	 * @throws Exception
	 */
	 public DataConfig queryDataConfig() throws Exception ;
	 

	 
	 /**
	  * 删除task通过taskname
	  * @data 2017年4月23日
	  * @return
	  * @throws Exception
	  */
	 public boolean deleteDataConfig() throws Exception ;
	 
	 /**
	  * 插入Task
	  * @return
	  * @throws Exception
	  */
	 public boolean insertDataConfig(DataConfig dataConfig) throws Exception ;
	 
	 /**
	  * 修改Task
	  * @return
	  * @throws Exception
	  */
	 public boolean updateDataConfig(DataConfig dataConfig) throws Exception ;
	 
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
}
