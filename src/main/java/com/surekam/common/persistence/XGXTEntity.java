package com.surekam.common.persistence;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.DateBridge;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Resolution;
import org.hibernate.search.annotations.Store;

import com.fasterxml.jackson.annotation.JsonFormat;

@MappedSuperclass
public abstract class XGXTEntity<T> extends BaseEntity<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	@ApiModelProperty(hidden = true)
	protected String states; //操作状态，数据字典：A增加； U更新；D删除
	@ApiModelProperty(hidden = true)
	private Date creatTime;//创建时间
	@ApiModelProperty(hidden = true)
	private String creatUserid;//创建人
	@ApiModelProperty(hidden = true)
	private Date updateTime;//最后修改时间
	@ApiModelProperty(hidden = true)
	private String updateUserid;//更新人
	
	public XGXTEntity() {
		super();
		this.states = STATE_FLAG_ADD;
		this.creatTime = new Date();
	}

	@Field(index=Index.YES, analyze=Analyze.NO, store=Store.YES)
	public String getStates() {
		return states;
	}

	public void setStates(String states) {
		this.states = states;
	}


	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Field(index=Index.YES, analyze=Analyze.NO, store=Store.YES)
	@DateBridge(resolution = Resolution.DAY)
	public Date getCreatTime() {
		return creatTime;
	}
	
	public void setCreatTime(Date creatTime) {
		this.creatTime = creatTime;
	}
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	@Field(index=Index.YES, analyze=Analyze.NO, store=Store.YES)
	@DateBridge(resolution = Resolution.DAY)
	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreatUserid() {
		return creatUserid;
	}

	public void setCreatUserid(String creatUserid) {
		this.creatUserid = creatUserid;
	}

	public String getUpdateUserid() {
		return updateUserid;
	}

	public void setUpdateUserid(String updateUserid) {
		this.updateUserid = updateUserid;
	}
	
}
