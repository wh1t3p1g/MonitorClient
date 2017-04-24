package com.okami.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @author wh1t3P1g
 * @since 2017/1/17
 */
@Configuration
public class DBConfig {

//    @Autowired
//    private DataSource dataSource;
//    
//    public void setDataSource(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
	
    @Bean
    public DataSource dataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:config/config.db");
        return dataSourceBuilder.build();
    }
    
    /**
     * 索引数据库配置
     * @data 2017年3月11日
     * @param indexPath 数据库存储位置
     * @return
     */
    public DataSource indexDataSource(String indexPath) {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:"+indexPath);
        return dataSourceBuilder.build();
    }
    
}
