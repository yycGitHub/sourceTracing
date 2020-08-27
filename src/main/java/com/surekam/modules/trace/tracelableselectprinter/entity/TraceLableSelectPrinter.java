package com.surekam.modules.trace.tracelableselectprinter.entity;

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
 * 用户选择打印机表Entity
 * @author liw
 * @version 2019-10-14
 */
@Entity
@Table(name = "t_trace_lable_select_printer")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLableSelectPrinter extends XGXTEntity<TraceLableSelectPrinter> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String printerId;//打印机设置ID
	private String officeId;//公司ID
	private String xoffset;//x方向（水平方向）偏移量（mm）
	private String yoffset;//y方向（垂直方向）偏移量（mm）

	public TraceLableSelectPrinter() {
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
	
	public String getPrinterId() {
		return this.printerId;
	}
	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}
	
	public String getOfficeId() {
		return this.officeId;
	}
	public void setOfficeId(String officeId) {
		this.officeId = officeId;
	}
	
	public String getXoffset() {
		return this.xoffset;
	}
	public void setXoffset(String xoffset) {
		this.xoffset = xoffset;
	}
	
	public String getYoffset() {
		return this.yoffset;
	}
	public void setYoffset(String yoffset) {
		this.yoffset = yoffset;
	}
	
}


