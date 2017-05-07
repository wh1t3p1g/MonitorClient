package com.okami.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.okami.dao.IDataConfigDao;
import com.okami.entities.DataConfig;

@Component
@Scope("prototype")
public class DataConfigDao implements IDataConfigDao{
    @Autowired
    private DataSource dataSource;
    
//    private Connection conn;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
	@Override
	public DataConfig queryDataConfig() throws Exception {
		Connection conn = null;
		DataConfig dataConfig = null;
		try {
			conn = dataSource.getConnection();
			String sql = "Select * from DataConfig";
		    Statement smt = conn.createStatement();
		    
		    ResultSet rs = smt.executeQuery(sql);
		    if (rs.next()) {
		    	int id = rs.getInt("Id");
		    	String key = rs.getString("Key");
		    	String iv = rs.getString("Iv");
		    	dataConfig = new DataConfig(id,key,iv);
		    }
			rs.close();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {}
			}
		}
	    return dataConfig;
	}

	@Override
	public boolean deleteDataConfig() throws Exception {
		String sql = "Delete from DataConfig where Id = ?";
		Connection conn = null;
		int flag = 0;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1,1);
			flag = ps.executeUpdate();
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
		if(flag != 0 )
			return true;
		return false;
	}

	@Override
	public boolean insertDataConfig(DataConfig dataConfig) throws Exception {
		String sql = "INSERT INTO DataConfig " +
				"(Id,Key,Iv) VALUES (?, ?, ?)";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, 1);
			ps.setString(2, dataConfig.getKey());
			ps.setString(3, dataConfig.getIv());
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
	public boolean updateDataConfig(DataConfig dataConfig) throws Exception {
		String sql = "UPDATE DataConfig SET Key=?,Iv=? WHERE Id = 1";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, dataConfig.getKey());
			ps.setString(2, dataConfig.getIv());
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
			String sql = "CREATE TABLE 'DataConfig' ("
					+ "'Id'  INTEGER NOT NULL,"
					+ "'Key'  TEXT NOT NULL,"
					+ "'Iv'  TEXT NOT NULL,"
					+ "PRIMARY KEY ('Id' ASC)"
					+ ")";
					
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
		}catch (SQLException e) {
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
		Connection conn = null;
		boolean flag = false;
		try {
			String sql = "select * from sqlite_master where type = 'table' and name = 'DataConfig'";
		
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				flag = true;
			}
			rs.close();
			ps.close();
		}catch (SQLException e) {
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
