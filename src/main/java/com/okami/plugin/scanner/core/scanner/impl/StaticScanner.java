package com.okami.plugin.scanner.core.scanner.impl;

import com.okami.plugin.scanner.core.scanner.AbstractScanner;

import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/1/1
 * 基于静态匹配
 * 匹配关键字 恶意函数 | 文件属性维度
 */
public class StaticScanner extends AbstractScanner implements Runnable{
    @Override
    public Map<String,String> calculate() {
        return null;
    }

    @Override
    public void run() {

    }
}
