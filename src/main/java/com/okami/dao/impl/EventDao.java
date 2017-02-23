package com.okami.dao.impl;

import com.okami.dao.IEventDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * @author wh1t3P1g
 * @since 2017/1/18
 */
@Component
@Scope("prototype")
public class EventDao implements IEventDao {

    @Autowired
    private DataSource dataSource;


}
