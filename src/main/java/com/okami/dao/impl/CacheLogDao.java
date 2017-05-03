package com.okami.dao.impl;

import com.okami.dao.ICacheLogDao;
import com.okami.entities.CacheLog;
import com.okami.entities.MonitorTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

/**
 * @author orleven
 * @date 2017年2月19日
 */
@Component
@Scope("prototype")
public class CacheLogDao implements ICacheLogDao {

    @Autowired
    private DataSource dataSource;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


	@Override
	public List<CacheLog> queryCacheLog() throws Exception {
		Connection conn = null;
		 List<CacheLog> list = new ArrayList<CacheLog>();
		try {
			conn = dataSource.getConnection();
			String sql = "Select * from CacheLog limit 0,20";
		    Statement smt = conn.createStatement();
	
		    ResultSet rs = smt.executeQuery(sql);
		   
		    while (rs.next()) {
		    	Integer id = rs.getInt("Id");
		    	String event = rs.getString("Event");
		    	String type = rs.getString("Type");
		    	String time = rs.getString("Time");
		    	String taskName = rs.getString("TaskName");
		    	CacheLog cacheLog = new CacheLog(id, type, time, event,taskName);
		    	list.add(cacheLog);
		    }
		    
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	    return list;
	}


	@Override
	public boolean insertCacheLog(CacheLog cacheLog) throws Exception {
		String sql = "INSERT INTO CacheLog " +
				"(Event,Time,Type,TaskName) VALUES (?, ?, ?, ?)";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, cacheLog.getEvent());
			ps.setString(2,  cacheLog.getTime());
			ps.setString(3,  cacheLog.getType());
			ps.setString(4,  cacheLog.getTaskName());
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return true;
	}


	@Override
	public boolean deleteCacheLog(CacheLog cacheLog) throws Exception {
		String sql = "DELETE FROM CacheLog WHERE Id = ?";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, cacheLog.getId());
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return true;
	}


	@Override
	public boolean createTable() throws Exception {
		Connection conn = null;
		try {	
			String sql = "CREATE TABLE 'CacheLog' ("
					+ "'Id'  integer,"
					+ "'Event'  varchar,"
					+ "'Time'  varchar,"
					+ "'Type'  varchar,"
					+ "'TaskName'  varchar,"
					+ "PRIMARY KEY ('Id')"
					+ ");";
			
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
		
		return true;
	}


	@Override
	public boolean isTableExist() throws Exception {
		String sql = "select * from sqlite_master where type = 'table' and name = 'CacheLog'";
		Connection conn = null;
		boolean  flag = false;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				flag =  true;
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
		return flag;
	}

}
