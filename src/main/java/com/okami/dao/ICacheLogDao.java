package com.okami.dao;

import java.util.List;

import com.okami.entities.CacheLog;;

/**
 * 缓存日志
 * @author orleven
 * @date 2017年2月19日
 */
public interface ICacheLogDao {
	/**
	 * 查询所有CacheLog
	 * @return
	 * @throws Exception
	 */
	 public List<CacheLog> queryCacheLog() throws Exception ;
	 
	 /**
	  * 插入CacheLog
	  * @return
	  * @throws Exception
	  */
	 public boolean insertCacheLog(CacheLog cacheLog) throws Exception ;
	 
	 /**
	  * 删除CacheLog
	  * @return
	  * @throws Exception
	  */
	 public boolean deleteCacheLog(CacheLog cacheLog) throws Exception ;
	 
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
