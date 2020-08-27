package com.surekam.modules.trace.TraceDayInfo.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.service.BaseService;
import com.surekam.modules.trace.TraceDayInfo.dao.TraceDayInfoDao;
import com.surekam.modules.trace.TraceDayInfo.entity.TraceDayInfo;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;

/**
 * 溯源每天信息
 * @author wangyuewen
 * @param <TraceInfoService>
 *
 */
@Component
@Transactional(readOnly = true)
public class TraceDayInfoService extends BaseService {

	@Autowired
	private TraceDayInfoDao traceDayInfoDao;
	
	public boolean isExsist(TraceDayInfo traceDayInfo) {
		DetachedCriteria dc = traceDayInfoDao.createDetachedCriteria();
		if(StringUtils.isNotBlank(traceDayInfo.getBatchId())) {
			dc.add(Restrictions.eq("officeId", traceDayInfo.getOfficeId()));
		}
		if(StringUtils.isNotBlank(traceDayInfo.getBatchId())) {
			dc.add(Restrictions.eq("productId", traceDayInfo.getProductId()));
		}
		if(StringUtils.isNotBlank(traceDayInfo.getBatchId())) {
			dc.add(Restrictions.eq("batchId", traceDayInfo.getBatchId()));
		}
		if(StringUtils.isNotBlank(traceDayInfo.getBatchId())) {
			dc.add(Restrictions.eq("date", traceDayInfo.getDate()));
		}
		List<TraceDayInfo> list = traceDayInfoDao.find(dc);
		if(list != null && list.size() > 0) {
			return true;
		} 
		return false;
	}
	
	/**
	 * 根据公司id与起止时间查询扫码次数
	 * @param beginTime
	 * @param endTime
	 * @param officeId
	 * @return
	 */
	public Integer findTraceCount(String beginTime, String endTime, String officeId) {
		DetachedCriteria dc = traceDayInfoDao.createDetachedCriteria();
		if(StringUtils.isNotBlank(officeId)) {
			dc.add(Restrictions.eq("officeId", officeId));
		}
		if(StringUtils.isNotBlank(beginTime)) {
			dc.add(Restrictions.ge("date", beginTime));
		}
		if(StringUtils.isNotBlank(endTime)) {
			dc.add(Restrictions.le ("date", endTime));
		}
		String sqlString = "select sum(traceCount) from TraceDayInfo where 1 = 1";
		if(StringUtils.isNotBlank(officeId)) {
			sqlString += " and officeId = '" + officeId + "'";
		}
		if(StringUtils.isNotBlank(beginTime)) {
			sqlString += " and date >= '" + beginTime + "'";
		}
		if(StringUtils.isNotBlank(endTime)) {
			sqlString += " and date <= '" + endTime + "'";
		}
		List<Integer> list = traceDayInfoDao.find(sqlString);
		if(list != null && list.size() > 0) {
			Number traceCount = (list.get(0)==null?0:(Number)list.get(0));
			return traceCount.intValue();
		}
		return 0;
	}
	
	@Transactional(readOnly = false)
	public void updateOrSave(TraceDayInfo traceDayInfo) throws Exception {
		if(isExsist(traceDayInfo)) {
			update(traceDayInfo);
		} else {
			save(traceDayInfo);
		}
	}
	
	/**
	 * 更新批次溯源次数
	 * @param traceDayInfo
	 */
	public void update(TraceDayInfo traceDayInfo) {
		String qlString = "update t_trace_day_info set trace_count = trace_count + 1 where office_id = '" 
	    + traceDayInfo.getOfficeId() 
	    + "' and product_id = '" + traceDayInfo.getProductId()
	    + "' and batch_id = '" + traceDayInfo.getBatchId()
		+ "' and date = '" + traceDayInfo.getDate() + "'";
		traceDayInfoDao.getSession().createSQLQuery(qlString).executeUpdate();
	}
	
	public void save(TraceDayInfo traceDayInfo) {
		String qlString = "insert into t_trace_day_info (batch_id, date, trace_count, product_id, office_id) values" 
				+ " ('" + traceDayInfo.getBatchId() + "', '" + traceDayInfo.getDate() + "', " + traceDayInfo.getTraceCount() + ", '" + traceDayInfo.getProductId() + "', '" + traceDayInfo.getOfficeId() + "')";
		traceDayInfoDao.getSession().createSQLQuery(qlString).executeUpdate();
	}
}
