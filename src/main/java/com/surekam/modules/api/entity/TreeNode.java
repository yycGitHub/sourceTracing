package com.surekam.modules.api.entity;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	String id;
	String pid;
    String name;  
    List<TreeNode> childNode = new ArrayList<TreeNode>();

    public TreeNode() {
        super();
    }
    public TreeNode(String id, String pid) {
        super();
        this.id = id;
        this.pid = pid;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
	public List<TreeNode> getChildNode() {
		return childNode;
	}
	public void setChildNode(List<TreeNode> childNode) {
		this.childNode = childNode;
	}   
    
}
