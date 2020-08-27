package com.surekam.modules.trace.tracelablespecification.entity;

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
 * 标签卷纸规格表Entity
 * @author liw
 * @version 2019-10-14
 */
@Entity
@Table(name = "t_trace_lable_specification")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLableSpecification extends XGXTEntity<TraceLableSpecification> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String lableTemplateId;//标签ID
	private String officeId;//公司ID
	private String labelName;//标签描述
	private String labelWidth;//单个标签宽度（mm）
	private String labelHeight;//单个标签高度（mm）
	private String labelNumber;//单棑标签数量
	private String labelLeftmargin;//标签左边距（mm）
	private String labelHorizontalInterval;//标签水平间隔（mm）
	private String labelVerticalInterval;//标签垂直间隔（mm）
	private String materialName;//材质名称
	private String maximumPrintSpeed;//标签材质匹配的最大打印速度
	private String printingConcentration;//标签材质匹配的打印浓度（0-15）

	public TraceLableSpecification() {
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
	
	public String getOfficeId() {
		return this.officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
	public String getLabelWidth() {
		return this.labelWidth;
	}
	public void setLabelWidth(String labelWidth) {
		this.labelWidth = labelWidth;
	}
	
	public String getLabelHeight() {
		return this.labelHeight;
	}
	public void setLabelHeight(String labelHeight) {
		this.labelHeight = labelHeight;
	}
	
	public String getLabelNumber() {
		return this.labelNumber;
	}
	public void setLabelNumber(String labelNumber) {
		this.labelNumber = labelNumber;
	}
	
	public String getLabelLeftmargin() {
		return this.labelLeftmargin;
	}
	public void setLabelLeftmargin(String labelLeftmargin) {
		this.labelLeftmargin = labelLeftmargin;
	}
	
	public String getLabelHorizontalInterval() {
		return this.labelHorizontalInterval;
	}
	public void setLabelHorizontalInterval(String labelHorizontalInterval) {
		this.labelHorizontalInterval = labelHorizontalInterval;
	}
	
	public String getLabelVerticalInterval() {
		return this.labelVerticalInterval;
	}
	public void setLabelVerticalInterval(String labelVerticalInterval) {
		this.labelVerticalInterval = labelVerticalInterval;
	}
	
	public String getMaterialName() {
		return this.materialName;
	}
	public void setMaterialName(String materialName) {
		this.materialName = materialName;
	}
	
	public String getMaximumPrintSpeed() {
		return this.maximumPrintSpeed;
	}
	public void setMaximumPrintSpeed(String maximumPrintSpeed) {
		this.maximumPrintSpeed = maximumPrintSpeed;
	}
	
	public String getPrintingConcentration() {
		return this.printingConcentration;
	}
	public void setPrintingConcentration(String printingConcentration) {
		this.printingConcentration = printingConcentration;
	}

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	
}


