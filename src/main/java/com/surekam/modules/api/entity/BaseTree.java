package com.surekam.modules.api.entity;

public class BaseTree {
	private String id;// 主键
	private String name;// 名称
	private String address;// 详细地址
	private String baseImg; // 基地图片地址

	public BaseTree() {
		super();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getBaseImg() {
		return baseImg;
	}

	public void setBaseImg(String baseImg) {
		this.baseImg = baseImg;
	}

}
