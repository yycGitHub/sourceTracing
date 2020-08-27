package com.surekam.modules.trace.TraceLableContent.entity;

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

/**
 * 标签内容
 * @author wangyuewen
 */
@Entity
@Table(name = "t_trace_lable_content")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLableContent extends XGXTEntity<TraceLableContent> {
	
	private static final long serialVersionUID = 1L;
	private String id;//
	private String applyId;
	private String labelTemplateId;
	private String elementId;
	private String applyElementContent;
	
	private String elementName;
	private String sfbm;
	
	public TraceLableContent() {
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

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}

	public String getLabelTemplateId() {
		return labelTemplateId;
	}

	public void setLabelTemplateId(String labelTemplateId) {
		this.labelTemplateId = labelTemplateId;
	}

	public String getApplyElementContent() {
		return applyElementContent;
	}

	public void setApplyElementContent(String applyElementContent) {
		this.applyElementContent = applyElementContent;
	}

	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	@Transient
	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	@Transient
	public String getSfbm() {
		return sfbm;
	}

	public void setSfbm(String sfbm) {
		this.sfbm = sfbm;
	}

}


