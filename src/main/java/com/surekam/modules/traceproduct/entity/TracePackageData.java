package com.surekam.modules.traceproduct.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 溯源包装数据
 * @author 腾农科技
 *
 */
public class TracePackageData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String traceCodeNumber; //溯源码
	private String packType;//包装类型
	private String packTypeName;//包装类型名称
	private String productDate;//生产日期
	private String shelfLife;//保质期
	private String batchId;//产品批次号（接口型在调用接口返回数据后需回写批次号）
	private String batchCode;//批次码
	private String productId;//产品id
	private String productName;//产品名称
	private String officeId;//企业id
	private String officeName;//企业名称
	private String photo;//照片
	private String queryCount;//查询次数
	
	private List<TracePackageData> list;
	private TraceRootBean traceRootBean;
	
	public String getTraceCodeNumber() {
		return traceCodeNumber;
	}
	public void setTraceCodeNumber(String traceCodeNumber) {
		this.traceCodeNumber = traceCodeNumber;
	}
	public String getPackType() {
		return packType;
	}
	public void setPackType(String packType) {
		this.packType = packType;
	}
	public String getPackTypeName() {
		return packTypeName;
	}
	public void setPackTypeName(String packTypeName) {
		this.packTypeName = packTypeName;
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
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getBatchCode() {
		return batchCode;
	}
	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getOfficeId() {
		return officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	public String getOfficeName() {
		return officeName;
	}
	public void setOfficeName(String officeName) {
		this.officeName = officeName;
	}
	public List<TracePackageData> getList() {
		return list;
	}
	public void setList(List<TracePackageData> list) {
		this.list = list;
	}
	public TraceRootBean getTraceRootBean() {
		return traceRootBean;
	}
	public void setTraceRootBean(TraceRootBean traceRootBean) {
		this.traceRootBean = traceRootBean;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getQueryCount() {
		return queryCount;
	}
	public void setQueryCount(String queryCount) {
		this.queryCount = queryCount;
	}
	
	
}
