package com.surekam.modules.act.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Where;

import com.google.common.collect.Lists;
import com.surekam.common.persistence.DataEntity;

import java.lang.String;
import java.util.List;

/**
 * activiti监听事件Entity
 * @author ludang
 * @version 2018-05-24
 */
@Entity
@Table(name = "act_listeners")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ActListener extends DataEntity<ActListener> {
	
	private static final long serialVersionUID = 1L;
	private String name_;//监听名称
	private String type_;//监听类型
	private String event_;//Event属性
	private String valueType_;//值类型
	private String classField_;//类路径

	
	private List<ActListenerField> listenerFields = Lists.newArrayList(); // 拥有资源列表
	
	public ActListener() {
		super();
	}
	
	public ActListener(String id) {
		this();
		this.id = id;
	}
	
	@OneToMany(mappedBy = "actListener", fetch=FetchType.LAZY)
	@Where(clause="del_flag='"+DEL_FLAG_NORMAL+"'")
	@OrderBy(value="id")
	@Fetch(FetchMode.JOIN)
	public List<ActListenerField> getListenerFields() {
		return listenerFields;
	}

	public void setListenerFields(List<ActListenerField> listenerFields) {
		this.listenerFields = listenerFields;
	}
	
	public String getName_() {
		return this.name_;
	}
	public void setName_(String name_) {
		this.name_ = name_;
	}
	public String getType_() {
		return this.type_;
	}
	public void setType_(String type_) {
		this.type_ = type_;
	}
	public String getEvent_() {
		return this.event_;
	}
	public void setEvent_(String event_) {
		this.event_ = event_;
	}
	public String getValueType_() {
		return this.valueType_;
	}
	public void setValueType_(String valueType_) {
		this.valueType_ = valueType_;
	}
	public String getClassField_() {
		return this.classField_;
	}
	public void setClassField_(String classField_) {
		this.classField_ = classField_;
	}
}


