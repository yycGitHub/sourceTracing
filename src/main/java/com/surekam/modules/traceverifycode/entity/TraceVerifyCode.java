package com.surekam.modules.traceverifycode.entity;

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
 * 溯源码表Entity
 * @author liw
 * @version 2019-07-10
 */
@Entity
@Table(name = "t_trace_verify_code")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceVerifyCode extends XGXTEntity<TraceVerifyCode> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String phone;//手机号
	private String formula;//公式
	private String answer;//答案

	public TraceVerifyCode() {
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
	
	public String getPhone() {
		return this.phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public String getFormula() {
		return this.formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	public String getAnswer() {
		return this.answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
}


