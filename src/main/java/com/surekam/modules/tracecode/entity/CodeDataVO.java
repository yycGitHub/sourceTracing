package com.surekam.modules.tracecode.entity;

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

import java.util.Date;
import java.lang.String;

/**
 * 溯源码Entity
 * @author liuyi
 * @version 2018-09-26
 */
public class CodeDataVO  {
	
	private Integer actFlag;//激活标志 0未激活  1已激活
	private Integer min;
	private Integer max;

	//private String states;//操作状态：A-新增，U-修改，D-删除

	public CodeDataVO() {
		super();
	}

	/**
	 * @return the actFlag
	 */
	public Integer getActFlag() {
		return actFlag;
	}

	/**
	 * @param actFlag the actFlag to set
	 */
	public void setActFlag(Integer actFlag) {
		this.actFlag = actFlag;
	}

	/**
	 * @return the min
	 */
	public Integer getMin() {
		return min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(Integer min) {
		this.min = min;
	}

	/**
	 * @return the max
	 */
	public Integer getMax() {
		return max;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(Integer max) {
		this.max = max;
	}

	
	
	
	
}


