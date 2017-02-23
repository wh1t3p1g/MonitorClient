package com.okami.common;

import java.io.File;

/**
 * 文件操作
 * 
 * @author orleven
 * @date 2016年12月31日
 */
public class fileHandle {
	public static void deleteAll(File file) {
		if (file.isFile() || file.list().length == 0) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				deleteAll(files[i]);
				files[i].delete();
			}


		}
		if (file.exists()) // 如果文件本身就是目录 ，就要删除目录
			file.delete();
	}
}
