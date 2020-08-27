package com.surekam.modules.api.utils;

import java.util.ArrayList;
import java.util.List;

import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.traceproduct.entity.TraceProduct;

public class AccessData {
	private List<TraceCode> traceCodeList = new ArrayList<TraceCode>();
	private String traceCode;   //溯源码
	private String batchCode;   //批次号
	private String productDate; //生产日期
	private String shelfLife;   //保质期
	private String officeId;    //企业ID
	private String sysId;       //系统来源（0-本系统，1-农事，2-加工）
	private String isBottomPacking; //是否底层包装，0-否，1-是
	private String packType;   //包装类型（1-底层标签，2-上级标签，3是上上级标签）
	private String packTypeName;   //包装类型名称
	private String oId;    //企业ID
	private TraceProduct traceProduct = new TraceProduct();
	
	public List<TraceCode> getTraceCodeList() {
		return traceCodeList;
	}
	public void setTraceCodeList(List<TraceCode> traceCodeList) {
		this.traceCodeList = traceCodeList;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public String getProductDate() {
		return productDate;
	}
	public void setProductDate(String productDate) {
		this.productDate = productDate;
	}
	public String getShelfLife() {
		return shelfLife;
	}
	public void setShelfLife(String shelfLife) {
		this.shelfLife = shelfLife;
	}
	public String getSysId() {
		return sysId;
	}
	public void setSysId(String sysId) {
		this.sysId = sysId;
	}
	public String getIsBottomPacking() {
		return isBottomPacking;
	}
	public void setIsBottomPacking(String isBottomPacking) {
		this.isBottomPacking = isBottomPacking;
	}
	public String getPackType() {
		return packType;
	}
	public void setPackType(String packType) {
		this.packType = packType;
	}
	public String getTraceCode() {
		return traceCode;
	}
	public void setTraceCode(String traceCode) {
		this.traceCode = traceCode;
	}
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public TraceProduct getTraceProduct() {
		return traceProduct;
	}
	public void setTraceProduct(TraceProduct traceProduct) {
		this.traceProduct = traceProduct;
	}
	public String getPackTypeName() {
		return packTypeName;
	}
	public void setPackTypeName(String packTypeName) {
		this.packTypeName = packTypeName;
	}
	public String getoId() {
		return oId;
	}
	public void setoId(String oId) {
		this.oId = oId;
	}
	
}
