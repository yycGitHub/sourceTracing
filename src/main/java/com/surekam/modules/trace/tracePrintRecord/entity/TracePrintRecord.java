package com.surekam.modules.trace.tracePrintRecord.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.surekam.common.persistence.XGXTEntity;
import com.surekam.common.utils.IdGen;

/**
 * 溯源打印记录Entity
 * @author xiaowangzi
 */
@Entity
@Table(name = "t_trace_print_record")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TracePrintRecord extends XGXTEntity<TracePrintRecord> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String applyId;//标签申请id
	private String printerId;//打印机id
	private String lableRollSpecId;//标签卷纸规格id
	private String resolutionPowerId;//分辨率id
	private String materialId;//标签材质id
	private Integer printNum;//打印数量
	private int startSerialNumber;//打印开始序列号
	private int endSerialNumber;//打印序列号

	public TracePrintRecord() {
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

	public String getApplyId() {
		return applyId;
	}

	public void setApplyId(String applyId) {
		this.applyId = applyId;
	}

	public String getPrinterId() {
		return printerId;
	}

	public void setPrinterId(String printerId) {
		this.printerId = printerId;
	}

	public String getLableRollSpecId() {
		return lableRollSpecId;
	}

	public void setLableRollSpecId(String lableRollSpecId) {
		this.lableRollSpecId = lableRollSpecId;
	}

	public String getResolutionPowerId() {
		return resolutionPowerId;
	}

	public void setResolutionPowerId(String resolutionPowerId) {
		this.resolutionPowerId = resolutionPowerId;
	}

	public String getMaterialId() {
		return materialId;
	}

	public void setMaterialId(String materialId) {
		this.materialId = materialId;
	}

	public Integer getPrintNum() {
		return printNum;
	}

	public void setPrintNum(Integer printNum) {
		this.printNum = printNum;
	}

	public int getStartSerialNumber() {
		return startSerialNumber;
	}

	public void setStartSerialNumber(int startSerialNumber) {
		this.startSerialNumber = startSerialNumber;
	}

	public int getEndSerialNumber() {
		return endSerialNumber;
	}

	public void setEndSerialNumber(int endSerialNumber) {
		this.endSerialNumber = endSerialNumber;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}


