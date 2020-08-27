package com.surekam.modules.tracelablediscount.entity;

import java.io.Serializable;

/**
 * 当前折扣总价
 */
public class DiscountPriceVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1223072052442333082L;

	/**
	 * 当前折扣
	 */
	private Double currentDiscount;
	/**
	 * 当前总价
	 */
	private Double currentTotalPrice;
	
	public Double getCurrentDiscount() {
		return currentDiscount;
	}
	public void setCurrentDiscount(Double currentDiscount) {
		this.currentDiscount = currentDiscount;
	}
	public Double getCurrentTotalPrice() {
		return currentTotalPrice;
	}
	public void setCurrentTotalPrice(Double currentTotalPrice) {
		this.currentTotalPrice = currentTotalPrice;
	}
	
	

}
