package com.surekam.modules.trace.entity;

import java.util.List;

/**
 * 标签内容实体类
 * @author liw
 */
public class Content{
	private String qrcode;
	private boolean isPrint;
	private List<Element> dataList;
	public String getQrcode() {
		return qrcode;
	}
	public void setQrcode(String qrcode) {
		this.qrcode = qrcode;
	}
	public boolean isPrint() {
		return isPrint;
	}
	public void setIsPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}
	public List<Element> getDataList() {
		return dataList;
	}
	public void setDataList(List<Element> dataList) {
		this.dataList = dataList;
	}
	
}


