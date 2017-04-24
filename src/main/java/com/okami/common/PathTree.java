package com.okami.common;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.okami.util.DataUtil;

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
				pathTree.nodes = new ArrayList();
			}else{
				pathTree.nodes = null;
			}
			pathTree.text = paths[i].getName();
			pathTree.href = paths[i].getName();
			
			pathTree.selectable = true;
//			pathTree.state.put("checked", false);
//			pathTree.state.put("disabled", false);
//			pathTree.state.put("expanded", false);
//			pathTree.state.put("selected", false);
			pathTreeList.add(pathTree);
		}
		return pathTreeList;
	}

}
