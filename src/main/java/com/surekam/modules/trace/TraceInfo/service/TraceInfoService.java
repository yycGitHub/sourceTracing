package com.surekam.modules.trace.TraceInfo.service;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.trace.TraceInfo.dao.TraceInfoDao;
import com.surekam.modules.trace.TraceInfo.entity.TraceInfo;

/**
 * 标签申请
 * @author wangyuewen
 * @param <TraceInfoService>
 *
 */
@Component
@Transactional(readOnly = true)
public class TraceInfoService extends BaseService {

	@Autowired
	private TraceInfoDao TraceInfoDao;
	
	public TraceInfo get(String id) {
		return TraceInfoDao.get(id);
	}
	
	public Integer getTraceCount(String officeId,String productId){
		List<String> list = TraceInfoDao.find("select a.id from TraceInfo a where a.officeId=:p1 and a.productId=:p2",new Parameter(officeId,productId));
		return list.size();
	}
	
	@Transactional(readOnly = false)
	public void save(TraceInfo traceInfo) throws Exception {
		TraceInfoDao.save(traceInfo);
	}
	
	public Integer getCxcs(String code){
		List<String> list = TraceInfoDao.find("select a.id from TraceInfo a where a.traceCode=:p1",new Parameter(code));
		if(list!=null && list.size()>0){
			return list.size();
		}else{
			return 0;
		}
	}

	/*
	 * 根据产品来查询扫码数据
	 * */
	public List<Object> findAllTraceInfo(String productId,User user,String officeId){
		String sql = "SELECT a.province,COUNT(a.id) sl FROM t_trace_info a WHERE a.province IS NOT NULL ";
		if(StringUtils.isNotBlank(productId)){
			sql+=" and a.product_id = '"+productId+"'";
		}
		if(StringUtils.isNotBlank(officeId)){
			sql += " and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		}else {
			if(!user.isAdmin()){
				if(StringUtils.isNotBlank(user.getOffice().getId())){
					sql += " and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + user.getOffice().getId() + "' OR tt.PARENT_IDS LIKE '%" + user.getOffice().getId() + "%')";
				}
			}
		}
		sql+=" GROUP BY a.province";
		return TraceInfoDao.findBySql(sql);
	}
	
	/*
	 * 按月分组统计扫码量
	 * */
	public List<Object> findMonthsTraceInfo(String productId,User user,String officeId){
		String sql = "select DATE_FORMAT(datetime,'%Y-%m') months,count(id)count from t_trace_info WHERE 1=1 ";
		if(StringUtils.isNotBlank(productId)){
			sql+=" and product_id = '"+productId+"'";
		}
		if(StringUtils.isNotBlank(officeId)){
			sql += " and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		}else {
			if(!user.isAdmin()){
				if(StringUtils.isNotBlank(user.getOffice().getId())){
					sql += " and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + user.getOffice().getId() + "' OR tt.PARENT_IDS LIKE '%" + user.getOffice().getId() + "%')";
				}
			}
		}
		sql+=" group by months ORDER BY months DESC LIMIT 0,12";
		return TraceInfoDao.findBySql(sql);
	}
	
	/**
	 * 根据公司id与起止时间查询扫码次数
	 * @param beginTime
	 * @param endTime
	 * @param officeId
	 * @return
	 */
	public Integer findTraceCount(String beginTime, String endTime, String officeId) {
		String sqlString = "select count(id) from t_trace_info where 1 = 1";
		if(StringUtils.isNotBlank(officeId) && !"1".equals(officeId)) {
			sqlString += " and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.DEL_FLAG='0' and tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + ",%')";
		}
		if(StringUtils.isNotBlank(officeId) && "1".equals(officeId)) {
			sqlString += " and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.DEL_FLAG='0' and tt.PARENT_IDS LIKE '%" + officeId + ",%')";
		}
		if(StringUtils.isNotBlank(beginTime)) {
			sqlString += " and datetime >= '" + beginTime + "'";
		}
		if(StringUtils.isNotBlank(endTime)) {
			sqlString += " and datetime <= '" + endTime + "'";
		}
		
		List<Integer> list = TraceInfoDao.findBySql3(sqlString);
		if(list != null && list.size() > 0) {
			Number traceCount = (list.get(0)==null?0:(Number)list.get(0));
			return traceCount.intValue();
		}
		return 0;
	}
	
	/**
	 * 根据产品id查询扫码次数
	 * @param beginTime
	 * @param year
	 * @param productId
	 * @return
	 */
	public String findTraceCount_new(String beginTime, String year, String officeId) {
		String sqlString = "select count(id) from t_trace_info where 1 = 1";
		if(StringUtils.isNotBlank(officeId)) {
			sqlString += " and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		}
		if(StringUtils.isNotBlank(beginTime)) {
			sqlString += " and datetime >= '" + beginTime + "'";
		}
		if(StringUtils.isNotBlank(year)) {
			sqlString += " and substr(datetime,1,4) = " + year;
		}
		
		List<Object> list = TraceInfoDao.findBySql(sqlString);
		return list.get(0).toString();
	}
	
	/*
	 * 按月分组统计扫码量-- 传入code（wangcheng或者tulufan）id
	 * */
	public List<Object> findMonthsTraceInfo3(String officeId){
		String sql = "select DATE_FORMAT(datetime,'%Y-%m') months,count(id)count from t_trace_info WHERE 1=1 ";
		sql+=" and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		sql+=" group by months ORDER BY months DESC LIMIT 0,12";
		return TraceInfoDao.findBySql(sql);
	}
}
