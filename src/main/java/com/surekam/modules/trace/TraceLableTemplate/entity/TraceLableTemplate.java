package com.surekam.modules.trace.TraceLableTemplate.entity;

import java.math.BigDecimal;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;
import com.google.common.collect.Lists;
import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.trace.TraceLableElement.entity.TraceLableElement;

/**
 * 标签模板
 * @author wangyuewen
 */
@Entity
@Table(name = "t_trace_lable_template")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLableTemplate extends XGXTEntity<TraceLableApply> {
	
	private static final long serialVersionUID = 1L;
	private String id;//
	private String officeId;
	private String labelTemplateCode;
	private String labelTemplateName;
	private String labelTemplateDesc;
	private String labelTemplateAddress;
	private String labelTemplateImg;
	private BigDecimal lableUnitPrice;
	private int sort;
	private String width;
	private String height;
	
	private List<TraceLableElement> lableElementList = Lists.newArrayList();
	private List<TraceLableElement> elementList = Lists.newArrayList();
	
	public TraceLableTemplate() {
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

	public String getOfficeId() {
		return officeId;
	}

	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}

	public String getLabelTemplateCode() {
		return labelTemplateCode;
	}

	public void setLabelTemplateCode(String labelTemplateCode) {
		this.labelTemplateCode = labelTemplateCode;
	}

	public String getLabelTemplateName() {
		return labelTemplateName;
	}

	public void setLabelTemplateName(String labelTemplateName) {
		this.labelTemplateName = labelTemplateName;
	}

	public String getLabelTemplateDesc() {
		return labelTemplateDesc;
	}

	public void setLabelTemplateDesc(String labelTemplateDesc) {
		this.labelTemplateDesc = labelTemplateDesc;
	}

	public String getLabelTemplateAddress() {
		return labelTemplateAddress;
	}

	public void setLabelTemplateAddress(String labelTemplateAddress) {
		this.labelTemplateAddress = labelTemplateAddress;
	}

	public String getLabelTemplateImg() {
		return labelTemplateImg;
	}

	public void setLabelTemplateImg(String labelTemplateImg) {
		this.labelTemplateImg = labelTemplateImg;
	}
	
	public BigDecimal getLableUnitPrice() {
		return lableUnitPrice;
	}

	public void setLableUnitPrice(BigDecimal lableUnitPrice) {
		this.lableUnitPrice = lableUnitPrice;
	}

	@OneToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE,CascadeType.REMOVE},fetch=FetchType.LAZY,mappedBy="template")
	@Where(clause="states!='"+STATE_FLAG_DEL+"'")
	@NotFound(action = NotFoundAction.IGNORE)
	public List<TraceLableElement> getLableElementList() {
		return lableElementList;
	}

	public void setLableElementList(List<TraceLableElement> lableElementList) {
		this.lableElementList = lableElementList;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	@Transient
	public List<TraceLableElement> getElementList() {
		return elementList;
	}

	public void setElementList(List<TraceLableElement> elementList) {
		this.elementList = elementList;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}
	
}


