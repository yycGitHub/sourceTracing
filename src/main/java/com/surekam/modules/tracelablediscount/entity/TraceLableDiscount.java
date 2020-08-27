package com.surekam.modules.tracelablediscount.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;

import java.lang.Double;
import java.lang.String;

/**
 * 标签折扣优惠Entity
 * @author xy
 * @version 2018-11-22
 */
@Entity
@Table(name = "t_trace_lable_discount")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLableDiscount extends XGXTEntity<TraceLableDiscount> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String labelTemplateId;//标签模板id
	private String numMin;//优惠数量下限，包含等于
	private String numMax;//优惠数量上限，例如 min=1000,max=2000,那么购买数量在1000-1999都属于这个优惠区间
	private Double discount;//折扣率, 0-1之间的小数，例如85折就填0.85;  0表示免费，1表示没有折扣
	//标签模板
	private String labelTemplateName;
	private String labelTemplateOfficeId;

	public TraceLableDiscount() {
		super();
	}
	
	@PrePersist
	public void prePersist(){
		this.id = IdGen.uuid();
	}
		
	@Id
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getLabelTemplateId() {
		return this.labelTemplateId;
	}
	public void setLabelTemplateId(String labelTemplateId) {
		this.labelTemplateId = labelTemplateId;
	}
	
	public String getNumMin() {
		return this.numMin;
	}
	public void setNumMin(String numMin) {
		this.numMin = numMin;
	}
	
	public String getNumMax() {
		return this.numMax;
	}
	public void setNumMax(String numMax) {
		this.numMax = numMax;
	}
	
	public Double getDiscount() {
		return this.discount;
	}
	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	@Transient
	public String getLabelTemplateName() {
		return labelTemplateName;
	}

	public void setLabelTemplateName(String labelTemplateName) {
		this.labelTemplateName = labelTemplateName;
	}

	@Transient
	public String getLabelTemplateOfficeId() {
		return labelTemplateOfficeId;
	}

	public void setLabelTemplateOfficeId(String labelTemplateOfficeId) {
		this.labelTemplateOfficeId = labelTemplateOfficeId;
	}
	
	
	
	
	
}


