package com.surekam.modules.trace.tracelableprinter.entity;

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

import java.lang.String;
import java.math.BigDecimal;

/**
 * 标签打印机设置表Entity
 * @author liw
 * @version 2019-10-14
 */
@Entity
@Table(name = "t_trace_lable_printer")
@DynamicInsert @DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class TraceLablePrinter extends XGXTEntity<TraceLablePrinter> {
	
	private static final long serialVersionUID = 1L;
	private String id;//主键
	private String printerType;//打印机型号
	private BigDecimal isMiddlePlace;//打印机标签是否居中放置
	private String maximumPrintSpeed;//最大打印速度
	private String resolvingPower;//分辨率 203dpi 300dpi 600dpi 【300dpi的意思是 每英寸有300点, 1英寸=2.54厘米 =25.4毫米 ,约11.81个像素每毫米】
	private String pixel;//1mm的像素（203dpi 7.99；300dpi 11.81；600dpi 23.62）
	private BigDecimal xoffset;//x方向（水平方向）偏移量（mm）
	private BigDecimal yoffset;//y方向（垂直方向）偏移量（mm）
	private BigDecimal labelVerticalIntervalIncrement;//标签垂直间隔增量

	public TraceLablePrinter() {
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
	
	public String getPrinterType() {
		return this.printerType;
	}
	public void setPrinterType(String printerType) {
		this.printerType = printerType;
	}
	
	public BigDecimal getIsMiddlePlace() {
		return this.isMiddlePlace;
	}
	public void setIsMiddlePlace(BigDecimal isMiddlePlace) {
		this.isMiddlePlace = isMiddlePlace;
	}
	
	public String getMaximumPrintSpeed() {
		return this.maximumPrintSpeed;
	}
	public void setMaximumPrintSpeed(String maximumPrintSpeed) {
		this.maximumPrintSpeed = maximumPrintSpeed;
	}
	
	public String getResolvingPower() {
		return this.resolvingPower;
	}
	public void setResolvingPower(String resolvingPower) {
		this.resolvingPower = resolvingPower;
	}
	
	public String getPixel() {
		return this.pixel;
	}
	public void setPixel(String pixel) {
		this.pixel = pixel;
	}

	@Transient
	public BigDecimal getXoffset() {
		return xoffset;
	}

	public void setXoffset(BigDecimal xoffset) {
		this.xoffset = xoffset;
	}

	@Transient
	public BigDecimal getYoffset() {
		return yoffset;
	}

	public void setYoffset(BigDecimal yoffset) {
		this.yoffset = yoffset;
	}

	public BigDecimal getLabelVerticalIntervalIncrement() {
		return labelVerticalIntervalIncrement;
	}

	public void setLabelVerticalIntervalIncrement(BigDecimal labelVerticalIntervalIncrement) {
		this.labelVerticalIntervalIncrement = labelVerticalIntervalIncrement;
	}
	
}


