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
		Connection conn = dataSource.getConnection();
		String sql = "Select * from CacheLog";
	    Statement smt = conn.createStatement();

	    ResultSet rs = smt.executeQuery(sql);
	    List<CacheLog> list = new ArrayList<CacheLog>();
	    while (rs.next()) {
	    	Integer id = rs.getInt("Id");
	    	String event = rs.getString("Event");
	    	String type = rs.getString("Type");
	    	String time = rs.getString("Time");
	    	CacheLog cacheLog = new CacheLog(id, type, time, event);
	    	list.add(cacheLog);
	    }
	    return list;
	}


	@Override
	public boolean insertCacheLog(CacheLog cacheLog) throws Exception {
		String sql = "INSERT INTO CacheLog " +
				"(Event,Time,Type) VALUES (?, ?, ?)";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, cacheLog.getEvent());
			ps.setString(2,  cacheLog.getTime());
			ps.setString(3,  cacheLog.getType());
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

}
