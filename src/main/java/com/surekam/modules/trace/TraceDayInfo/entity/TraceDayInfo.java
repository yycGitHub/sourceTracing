package com.surekam.modules.trace.TraceDayInfo.entity;

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * 溯源每天信息
 * @author wangyuewen
 */
@Entity
@Embeddable
@Table(name = "t_trace_day_info")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceDayInfo implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	private String officeId;
	private String productId;
	private String batchId;
	private String date;
	private Integer traceCount;
	
	public TraceDayInfo() {
		super();
	}
	
	@Id	
	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	@Id	
	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getBatchId() {
		return batchId;
	}
	@Id	
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return super.equals(obj);
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Integer getTraceCount() {
		return traceCount;
	}

	public void setTraceCount(Integer traceCount) {
		this.traceCount = traceCount;
	}

	
}


