package com.okami.plugin.scanner.core.scanner.impl;

import com.okami.plugin.scanner.core.handler.scanner.statistics.Compression;
import com.okami.plugin.scanner.core.handler.scanner.statistics.Entropy;
import com.okami.plugin.scanner.core.handler.scanner.statistics.LanguageIC;
import com.okami.plugin.scanner.core.handler.scanner.statistics.LongestWord;
import com.okami.plugin.scanner.core.scanner.AbstractScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wh1t3P1g
 * @since 2017/1/3
 * 基于统计学扫描
 */
@Component
@Scope("prototype")
public class StatisticsScanner extends AbstractScanner{

    @Autowired
    private Entropy entropy;
    @Autowired
    private LanguageIC languageIC;
    @Autowired
    private LongestWord longestWord;
    @Autowired
    private Compression compression;


    @Override
    public Map<String,String> calculate() {
        Map<String,String> retData=new HashMap<>();
        return retData;
    }
}
