package com.okami.entities;

import javax.persistence.*;

/**
 * @author wh1t3P1g
 * @since 2017/1/18
 */
@Entity
@Table(name="Monitor")
public class Monitor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 监控类型 human／force
     */
    private String type;

    /**
     * 监控的网站根路径
     */
    private String rootPath;

    /**
     * 白名单路径
     * json格式
     */
    private String whitePath;

    /**
     * 黑名单后缀名
     */
    private String blackExt;
}
