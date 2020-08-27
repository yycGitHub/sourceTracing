package com.surekam.modules.act.entity;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.surekam.common.persistence.DataEntity;

import java.lang.String;

/**
 * activiti监听事件参数Entity
 * @author ludang
 * @version 2018-06-12
 */
@Entity
@Table(name = "act_listener_field")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class ActListenerField extends DataEntity<ActListenerField> {
	
	private static final long serialVersionUID = 1L;
	private ActListener actListener;
	private String name_;//字段名称
	private String stringValue_;//字段值
	private String expression_;//表达式
	private String string_;//

	public ActListenerField() {
		super();
	}
	
	public String getName_() {
		return this.name_;
	}
	public void setName_(String name_) {
		this.name_ = name_;
	}
	public String getStringValue_() {
		return this.stringValue_;
	}
	public void setStringValue_(String stringValue_) {
		this.stringValue_ = stringValue_;
	}
	public String getExpression_() {
		return this.expression_;
	}
	public void setExpression_(String expression_) {
		this.expression_ = expression_;
	}
	public String getString_() {
		return this.string_;
	}
	public void setString_(String string_) {
		this.string_ = string_;
	}

	@ManyToOne
	@JoinColumn(name="LISTENER_ID")
	@JsonIgnore
	public ActListener getActListener() {
		return actListener;
	}

	public void setActListener(ActListener actListener) {
		this.actListener = actListener;
	}
}


