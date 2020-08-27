package com.surekam.modules.tracesendmessagecode.entity;

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
 * 手机验证码表Entity
 * @author liw
 * @version 2019-07-15
 */
@Entity
@Table(name = "t_trace_send_message_code")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceSendMessageCode extends XGXTEntity<TraceSendMessageCode> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String phone;//手机号
	private String code;//手机验证码

	public TraceSendMessageCode() {
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
	
	public String getCode() {
		return this.code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
}


