package com.okami.bean;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.okami.util.DataUtil;

/**
 * 用户返回目录结构，与前段js的viewtree对应
 * @author orleven
 * @date 2017年4月30日
 */
public class PathTree {
	
	public String text;
	
	public String href;
	
	public boolean selectable;
	
//	public String icon;
	
//	public Map<String,Boolean> state = new HashMap<String, Boolean>();
	
	public List<PathTree> nodes;
	
	/**
	 * 获取当前目录
	 * @data 2017年4月24日
	 * @return
	 */
	public static List<PathTree> getPath(File path){
		List<PathTree> pathTreeList= new ArrayList();
		if(!path.exists()){
			return pathTreeList;
		}
		File[] paths = path.listFiles();
		for (int i = 0; i < paths.length; i++) {
			PathTree pathTree = new PathTree();
			if(paths[i].isDirectory()){
				
				pathTree.text = paths[i].getName();
				pathTree.href = paths[i].getName();
				int count = 0;
				for(File subFile:paths[i].listFiles()){
					if(subFile.isDirectory()){
						count += 1;
					}
				}
				if(count != 0){
					pathTree.nodes = new ArrayList();
				}
				pathTree.selectable = true;
//				pathTree.state.put("checked", false);
//				pathTree.state.put("disabled", false);
//				pathTree.state.put("expanded", false);
//				pathTree.state.put("selected", false);
				pathTreeList.add(pathTree);
			}

		}
		return pathTreeList;
	}

}
