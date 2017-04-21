package com.okami.plugin.scanner.core.common;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wh1t3P1g
 * @since 2017/1/3
 */
public class ListFileTree extends SimpleFileVisitor<Path> {

    public List<Path> paths=new ArrayList<Path>();


    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        paths.add(file);
        return FileVisitResult.CONTINUE;
    }
}
