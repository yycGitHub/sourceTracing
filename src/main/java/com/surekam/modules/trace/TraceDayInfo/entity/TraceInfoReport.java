package com.surekam.modules.trace.TraceDayInfo.entity;

/**
* Created by wangyuewen on 2018年11月7日
*/
public class TraceInfoReport {
	
	private Integer weekTraceCount;//本周扫码
	private Integer monthTraceCount;//本月扫码
	private Integer traceCount;//总扫码
	public Integer getWeekTraceCount() {
		return weekTraceCount;
	}
	public void setWeekTraceCount(Integer weekTraceCount) {
		this.weekTraceCount = weekTraceCount;
	}
	public Integer getMonthTraceCount() {
		return monthTraceCount;
	}
	public void setMonthTraceCount(Integer monthTraceCount) {
		this.monthTraceCount = monthTraceCount;
	}
	public Integer getTraceCount() {
		return traceCount;
	}
	public void setTraceCount(Integer traceCount) {
		this.traceCount = traceCount;
	}
	
	
}


