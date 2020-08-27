package com.surekam.modules.trace.tracelableelementnew.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;


import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;

import java.lang.String;

/**
 * 标签元素表Entity
 * @author liw
 * @version 2019-10-14
 */
@Entity
@Table(name = "t_trace_lable_element_new")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLableElementNew extends XGXTEntity<TraceLableElementNew> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String lableTemplateId;//标签ID
	private String elementId;//元素编号
	private String elementType;//元素类型 font：文字   qrCode：二维码  barcode：条码
	private String elementJson;//elementType=font，fontElementLocation列表序列化的Json格式数据；elementType=qrCode，qrCodeElementLocation列表序列化的Json格式数据；elementType=barcode，barcodeElementLocation列表序列化的Json格式数据

	public TraceLableElementNew() {
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
	
	public String getLableTemplateId() {
		return this.lableTemplateId;
	}
	public void setLableTemplateId(String lableTemplateId) {
		this.lableTemplateId = lableTemplateId;
	}
	
	public String getElementId() {
		return this.elementId;
	}
	public void setElementId(String elementId) {
		this.elementId = elementId;
	}
	
	public String getElementType() {
		return this.elementType;
	}
	public void setElementType(String elementType) {
		this.elementType = elementType;
	}
	
	public String getElementJson() {
		return this.elementJson;
	}
	public void setElementJson(String elementJson) {
		this.elementJson = elementJson;
	}
	
}


