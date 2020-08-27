package com.surekam.modules.trace.TraceLableElement.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;
import com.surekam.common.utils.excel.annotation.ExcelField;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;

/**
 * 标签元素
 * @author wangyuewen
 */
@Entity
@Table(name = "t_trace_lable_element")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLableElement extends XGXTEntity<TraceLableElement> {
	
	private static final long serialVersionUID = 1L;
	private String id;//
	private TraceLableTemplate template;
	private String elementCode;
	private String elementName;
	private String defaultContent;
	private String elementMaxLength;
	private String isAllowChange;
	private String xStartPoint;
	private String yStartPoint;
	private String rotation;
	//前端要求添加字段
	private String tempContent;
	
	public TraceLableElement() {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="label_template_id")
	@NotFound(action = NotFoundAction.IGNORE)
	@NotNull(message="标签模板id不能为空")
	@JsonIgnore
	public TraceLableTemplate getTemplate() {
		return template;
	}

	public void setTemplate(TraceLableTemplate template) {
		this.template = template;
	}

	public String getElementCode() {
		return elementCode;
	}

	public void setElementCode(String elementCode) {
		this.elementCode = elementCode;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}

	public String getDefaultContent() {
		return defaultContent;
	}

	public void setDefaultContent(String defaultContent) {
		this.defaultContent = defaultContent;
	}

	public String getElementMaxLength() {
		return elementMaxLength;
	}

	public void setElementMaxLength(String elementMaxLength) {
		this.elementMaxLength = elementMaxLength;
	}

	public String getIsAllowChange() {
		return isAllowChange;
	}

	public void setIsAllowChange(String isAllowChange) {
		this.isAllowChange = isAllowChange;
	}

	public String getxStartPoint() {
		return xStartPoint;
	}

	public void setxStartPoint(String xStartPoint) {
		this.xStartPoint = xStartPoint;
	}

	public String getyStartPoint() {
		return yStartPoint;
	}

	public void setyStartPoint(String yStartPoint) {
		this.yStartPoint = yStartPoint;
	}

	public String getRotation() {
		return rotation;
	}

	public void setRotation(String rotation) {
		this.rotation = rotation;
	}

	@Transient
	public String getTempContent() {
		return tempContent;
	}

	public void setTempContent(String tempContent) {
		this.tempContent = tempContent;
	}

}


