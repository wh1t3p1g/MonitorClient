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
		Connection conn = dataSource.getConnection();
		String sql = "Select * from MonitorTask";
	    Statement smt = conn.createStatement();

	    ResultSet rs = smt.executeQuery(sql);
	    List<MonitorTask> list = new ArrayList<MonitorTask>();
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
	    	MonitorTask task = new MonitorTask(taskId, taskName, projectName,monitorPath,whiteList,blackList,flagName,runMode,BCMode,remark,maxSize);
	    	list.add(task);
	    }
	    return list;
	}


	@Override
	public boolean insertTask(MonitorTask monitorTask) throws Exception {
		String sql = "INSERT INTO MonitorTask " +
				"(TaskName,ProjectName,MonitorPath,WhiteList,BlackList,FlagName,RunMode,BCMode,Remark,MaxSize) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
		String sql = "UPDATE MonitorTask SET TaskName=?,ProjectName=?,MonitorPath=?,WhiteList=?,BlackList=?,FlagName=?,RunMode=?,BCMode=?,Remark=?,MaxSize=? WHERE TaskId = ?";
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
			ps.setInt(11, monitorTask.getTaskId());

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
		Connection conn = dataSource.getConnection();
		String sql = "Select * from MonitorTask where TaskName = ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, taskname);
	    MonitorTask monitorTask = null ;
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
	    	monitorTask = new MonitorTask(taskId, taskName, projectName,monitorPath,whiteList,blackList,flagName,runMode,BCMode,remark,maxSize);
		}
		rs.close();
		ps.close();
		return monitorTask;
	}
	


}
