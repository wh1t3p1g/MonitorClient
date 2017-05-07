package com.okami.dao;

import java.util.List;

import com.okami.entities.FileIndex;;

/**
 * @author orleven
 * @date 2017年3月11日
 */
public interface IFileIndexDao {
	
	/**
	 * 连接数据库
	 * @data 2017年3月11日
	 * @return
	 * @throws Exception
	 */
	public boolean connectDB() throws Exception;
	
	/**
	 * 查询所有索引
	 * @data 2017年3月11日
	 * @return
	 * @throws Exception
	 */
	public List<FileIndex> queryIndex() throws Exception ;
	
	/**
	 * 查询索引透过路径
	 * @data 2017年4月11日
	 * @return
	 * @throws Exception
	 */
	public FileIndex queryIndexByPath(String path) throws Exception ;
	
	/**
	 * 查询单个索引
	 * @data 2017年4月19日
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public FileIndex queryOneIndexByPath(String path) throws Exception ;
	
	/**
	 * 查询单个索引
	 * @data 2017年5月5日
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public  List<FileIndex>  queryIndexBySHA1(String Sha1) throws Exception ;
	
	/**
	 * 查询索引 like path
	 * @data 2017年4月11日
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public List<FileIndex> queryIndexLikePath(String path) throws Exception ;
	
	/**
	 * 插入索引
	 * @data 2017年3月11日
	 * @param fileIndex
	 * @return
	 * @throws Exception
	 */
	public boolean insertIndex(FileIndex fileIndex) throws Exception ;
	
	
	
	/**
	 * 批量插入索引
	 * @data 2017年3月11日
	 * @param fileIndex
	 * @return
	 * @throws Exception
	 */
	public boolean insertIndex(List<FileIndex> fileIndexList) throws Exception;
	
	/**
	 * 更新索引,根据path
	 * @data 2017年3月11日
	 * @param fileIndex
	 * @return
	 * @throws Exception
	 */
	public boolean updateIndex(FileIndex fileIndex) throws Exception ;
	
	/**
	 * 删除索引,根据path
	 * @data 2017年3月11日
	 * @param fileIndex
	 * @return
	 * @throws Exception
	 */
	public boolean deleteIndex(FileIndex fileIndex) throws Exception ;
	
	/**
	 * 删除查询到的索引
	 * @data 2017年4月11日
	 * @param fileIndex
	 * @return
	 * @throws Exception
	 */
	public boolean deleteIndexLikePath(String path) throws Exception ;
	
	/**
	 * 创建索引表
	 * @data 2017年3月11日
	 * @param fileIndex
	 * @return
	 * @throws Exception
	 */
	public boolean createTable() throws Exception ;
	
	/**
	 * 删除数据表里的数据
	 * @data 2017年5月1日
	 * @param fileIndex
	 * @return
	 * @throws Exception
	 */
	public boolean deleteAll() throws Exception ;
	
	/**
	 * 删除数据表
	 * @data 2017年5月1日
	 * @return
	 * @throws Exception
	 */
	public boolean deleteTable() throws Exception ;
	
	/**
	 * 关闭连接
	 * @data 2017年3月11日
	 * @return
	 * @throws Exception
	 */
	public boolean closeConnection() throws Exception;
	
	/**
	 * 判断是否存在此表
	 * @data 2017年5月1日
	 * @return
	 * @throws Exception
	 */
	public boolean isTableExist() throws Exception;
}
