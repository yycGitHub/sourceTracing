package com.surekam.modules.trace.TraceLableApply.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;
import com.surekam.modules.TraceDeliveryAddress.entity.TraceDeliveryAddress;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;
import com.surekam.modules.traceproduct.entity.TraceProduct;

/**
 * 标签申请
 * 
 * @author wangyuewen
 */
@Entity
@Table(name = "t_trace_lable_apply")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLableApply extends XGXTEntity<TraceLableApply> {

	private static final long serialVersionUID = 1L;
	private String id;//
	private String applyCode;
	private String applyNum;
	private String sysId;
	private String traceProductId;
	private String batchId;
	private String batchCode;
	private String labelTemplateId;
	private String addressId;
	private String address;
	private String auditFlag;
	private String cancelFlag;
	private String printFlag;
	private BigDecimal totalPrice;
	private String flag; // 0-无需标签号，1-需要标签号
	private String bqd;
	private String productName;
	private String officeId;

	private List<TraceLableContent> contentList;
	// 以下用于封装模板和收货地址对象
	private TraceDeliveryAddress deliveryAddress;
	private TraceLableTemplate lableTemplate;
	// 溯源产品
	private TraceProduct traceProduct;
	// 产品批次
	private ProductBatch productBatch;

	// 拼接后的地址
	private String receivingAddress;

	// 批次号
	private String batchNumber;

	// 溯源流水号
	private String serialNumber;

	// 溯源码号段
	private String serialCode;

	// 公司名称
	private String corpName;

	public TraceLableApply() {
		super();
	}

	@PrePersist
	public void prePersist() {
		this.id = IdGen.uuid();
	}

	@Id
	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getApplyCode() {
		return applyCode;
	}

	public void setApplyCode(String applyCode) {
		this.applyCode = applyCode;
	}

	public String getApplyNum() {
		return applyNum;
	}

	public void setApplyNum(String applyNum) {
		this.applyNum = applyNum;
	}

	public String getSysId() {
		return sysId;
	}

	public void setSysId(String sysId) {
		this.sysId = sysId;
	}

	public String getTraceProductId() {
		return traceProductId;
	}

	public void setTraceProductId(String traceProductId) {
		this.traceProductId = traceProductId;
	}

	public String getBatchId() {
		return batchId;
	}

	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public String getLabelTemplateId() {
		return labelTemplateId;
	}

	public void setLabelTemplateId(String labelTemplateId) {
		this.labelTemplateId = labelTemplateId;
	}

	public String getAddressId() {
		return addressId;
	}

	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}

	public String getAuditFlag() {
		return auditFlag;
	}

	public void setAuditFlag(String auditFlag) {
		this.auditFlag = auditFlag;
	}

	public String getCancelFlag() {
		return cancelFlag;
	}

	public void setCancelFlag(String cancelFlag) {
		this.cancelFlag = cancelFlag;
	}

	public String getPrintFlag() {
		return printFlag;
	}

	public void setPrintFlag(String printFlag) {
		this.printFlag = printFlag;
	}

	@Transient
	public List<TraceLableContent> getContentList() {
		return contentList;
	}

	public void setContentList(List<TraceLableContent> contentList) {
		this.contentList = contentList;
	}

	@Transient
	public TraceDeliveryAddress getDeliveryAddress() {
		return deliveryAddress;
	}

	public void setDeliveryAddress(TraceDeliveryAddress deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	@Transient
	public TraceLableTemplate getLableTemplate() {
		return lableTemplate;
	}

	public void setLableTemplate(TraceLableTemplate lableTemplate) {
		this.lableTemplate = lableTemplate;
	}

	@Transient
	public TraceProduct getTraceProduct() {
		return traceProduct;
	}

	public void setTraceProduct(TraceProduct traceProduct) {
		this.traceProduct = traceProduct;
	}

	@Transient
	public ProductBatch getProductBatch() {
		return productBatch;
	}

	public void setProductBatch(ProductBatch productBatch) {
		this.productBatch = productBatch;
	}

	public BigDecimal getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(BigDecimal totalPrice) {
		this.totalPrice = totalPrice;
	}

	@Transient
	public String getReceivingAddress() {
		return receivingAddress;
	}

	public void setReceivingAddress(String receivingAddress) {
		this.receivingAddress = receivingAddress;
	}

	@Transient
	public String getBatchCode() {
		return batchCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	@Transient
	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	@Transient
	public String getBqd() {
		return bqd;
	}

	public void setBqd(String bqd) {
		this.bqd = bqd;
	}

	@Transient
	public String getBatchNumber() {
		return batchNumber;
	}

	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}

	@Transient
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	@Transient
	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	@Transient
	public String getSerialCode() {
		return serialCode;
	}

	public void setSerialCode(String serialCode) {
		this.serialCode = serialCode;
	}

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	@Transient
	public String getCorpName() {
		return corpName;
	}

	public void setCorpName(String corpName) {
		this.corpName = corpName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
