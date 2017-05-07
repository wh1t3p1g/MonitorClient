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
import com.okami.dao.IMonitorTaskDao;
import com.okami.entities.FileIndex;
import com.okami.entities.MonitorTask;


/**
 * @author orleven
 * @date 2017年2月13日
 */
@Component
@Scope("prototype")
public class MonitorTaskDao implements IMonitorTaskDao{
	
    @Autowired
    private DataSource dataSource;
    
//    private Connection conn;
    
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


	@Override
	public List<MonitorTask> queryTask() throws Exception {
		Connection conn = null;
		List<MonitorTask> list = new ArrayList<MonitorTask>();
		try {
			conn = dataSource.getConnection();
			String sql = "Select * from MonitorTask";
		    Statement smt = conn.createStatement();
	
		    ResultSet rs = smt.executeQuery(sql);
		    while (rs.next()) {
		    	Integer taskId = rs.getInt("TaskId");
		    	String taskName = rs.getString("TaskName");
		    	String projectName = rs.getString("ProjectName");
		    	String monitorPath = rs.getString("MonitorPath");
		    	String whiteList = rs.getString("WhiteList");
		    	String blackList = rs.getString("BlackList");
		    	String flagName = rs.getString("FlagName");
		    	int runMode = rs.getInt("RunMode");
		    	int BCMode = rs.getInt("BCMode");
		    	String remark = rs.getString("Remark");
		    	String maxSize = rs.getString("MaxSize");
		    	int status = rs.getInt("Status");
		    	int upload = rs.getInt("Upload");
		    	MonitorTask task = new MonitorTask(taskId, taskName, projectName,monitorPath,whiteList,blackList,flagName,runMode,BCMode,remark,maxSize,status,upload);
		    	list.add(task);
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
	    return list;
	}


	@Override
	public boolean insertTask(MonitorTask monitorTask) throws Exception {
		String sql = "INSERT INTO MonitorTask " +
				"(TaskName,ProjectName,MonitorPath,WhiteList,BlackList,FlagName,RunMode,BCMode,Remark,MaxSize,Status,Upload) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, monitorTask.getTaskName());
			ps.setString(2, monitorTask.getProjectName());
			ps.setString(3, monitorTask.getMonitorPath());
			ps.setString(4, monitorTask.getWhiteList());
			ps.setString(5, monitorTask.getBlackList());
			ps.setString(6, monitorTask.getFlagName());
			ps.setInt(7, monitorTask.getRunMode());
			ps.setInt(8, monitorTask.getBCMode());
			ps.setString(9, monitorTask.getRemark());
			ps.setString(10, monitorTask.getMaxSize());
			ps.setInt(11, monitorTask.getStatus());
			ps.setInt(12, monitorTask.getUpload());
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
	public boolean updateTask(MonitorTask monitorTask) throws Exception {
		String sql = "UPDATE MonitorTask SET ProjectName=?,MonitorPath=?,WhiteList=?,BlackList=?,FlagName=?,RunMode=?,BCMode=?,Remark=?,MaxSize=?,Status=?,Upload=? WHERE TaskName = ?";
		Connection conn = null;
		
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, monitorTask.getProjectName());
			ps.setString(2, monitorTask.getMonitorPath());
			ps.setString(3, monitorTask.getWhiteList());
			ps.setString(4, monitorTask.getBlackList());
			ps.setString(5, monitorTask.getFlagName());
			ps.setInt(6, monitorTask.getRunMode());
			ps.setInt(7, monitorTask.getBCMode());
			ps.setString(8, monitorTask.getRemark());
			ps.setString(9, monitorTask.getMaxSize());
			ps.setInt(10, monitorTask.getStatus());
			ps.setInt(11, monitorTask.getUpload());
			ps.setString(12, monitorTask.getTaskName());

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
	public MonitorTask queryTaskByTaskName(String taskname) throws Exception {
		Connection conn = null;
		MonitorTask monitorTask = null ;
		try {
			conn = dataSource.getConnection();
			String sql = "Select * from MonitorTask where TaskName = ?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, taskname);
		    
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
		    	Integer taskId = rs.getInt("TaskId");
		    	String taskName = rs.getString("TaskName");
		    	String projectName = rs.getString("ProjectName");
		    	String monitorPath = rs.getString("MonitorPath");
		    	String whiteList = rs.getString("WhiteList");
		    	String blackList = rs.getString("BlackList");
		    	String flagName = rs.getString("FlagName");
		    	int runMode = rs.getInt("RunMode");
		    	int BCMode = rs.getInt("BCMode");
		    	String remark = rs.getString("Remark");
		    	String maxSize = rs.getString("MaxSize");
		    	int status = rs.getInt("Status");
		    	int upload = rs.getInt("Upload");
		    	monitorTask = new MonitorTask(taskId, taskName, projectName,monitorPath,whiteList,blackList,flagName,runMode,BCMode,remark,maxSize,status,upload);
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
		return monitorTask;
	}
	
	@Override
	public boolean createTable() throws Exception {
		Connection conn = null;
		try {
			String sql = "CREATE TABLE 'MonitorTask' ("
					+ "'TaskId'  INTEGER NOT NULL,"
					+ "'TaskName'  TEXT NOT NULL,"
					+ "'ProjectName'  TEXT,"
					+ "'MonitorPath'  TEXT,"
					+ "'WhiteList'  TEXT,"
					+ "'BlackList'  TEXT,"
					+ "'FlagName'  TEXT,"
					+ "'RunMode'  INTEGER,"
					+ "'BCMode'  INTEGER,"
					+ "'Remark'  TEXT,"
					+ "'MaxSize'  TEXT,"
					+ "'Status'  INTEGER,"
					+ "'Upload'  INTEGER,"
					+ "PRIMARY KEY ('TaskId' ASC)"
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
			String sql = "select * from sqlite_master where type = 'table' and name = 'MonitorTask'";
		
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


	@Override
	public boolean deleteTask(String taskName) throws Exception {
		String sql = "Delete from MonitorTask where TaskName = ?";
		Connection conn = null;
		int flag = 0;
		try {
			conn = dataSource.getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,taskName);
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

}
