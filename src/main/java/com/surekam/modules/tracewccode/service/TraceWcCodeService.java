package com.surekam.modules.tracewccode.service;

import java.util.List;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.modules.tracewccode.entity.TraceWcCode;
import com.surekam.modules.tracewccode.dao.TraceWcCodeDao;

/**
 * 溯源码表Service
 * @author liw
 * @version 2019-06-12
 */
@Component
@Transactional(readOnly = true)
public class TraceWcCodeService extends BaseService {

	@Autowired
	private TraceWcCodeDao traceWcCodeDao;
	
	public TraceWcCode get(String id) {
		return traceWcCodeDao.get(id);
	}
	
	public Page<TraceWcCode> find(Page<TraceWcCode> page, TraceWcCode traceWcCode) {
		DetachedCriteria dc = traceWcCodeDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceWcCode.FIELD_DEL_FLAG_XGXT, TraceWcCode.STATE_FLAG_DEL));
		return traceWcCodeDao.find(page, dc);
	}
	
	public String getMaxSerialNumber(String companyCode, String baseCode, String sjbh, String productCategoryCode, String packingCode) {
		String sql = "SELECT t.count FROM t_trace_wc_code t WHERE t.states<>'D'"
				+ " and t.company_code=:p1 and t.base_code=:p2 and t.product_category_code=:p3 and t.packing_code=:p4 and t.creat_date=:p5";
		List<String> list  = traceWcCodeDao.findBySql(sql,new Parameter(companyCode,baseCode,productCategoryCode,packingCode,sjbh));
		if(list!=null && list.size()>0){
			return list.get(0).toString();
		}else{
			return "0";
		}
	}
	
	@Transactional(readOnly = false)
	public void save(String companyCode, String baseCode, String sjbh, String productCategoryCode, String packingCode, int count) {
		if("0".equals(getMaxSerialNumber(companyCode, baseCode, sjbh, productCategoryCode, packingCode))){
			TraceWcCode traceWcCode = new TraceWcCode();
			traceWcCode.setBaseCode(baseCode);
			traceWcCode.setCompanyCode(companyCode);
			traceWcCode.setCreatDate(sjbh);
			traceWcCode.setProductCategoryCode(productCategoryCode);
			traceWcCode.setPackingCode(packingCode);
			traceWcCode.setCount(count+"");
			traceWcCodeDao.save(traceWcCode);
		}else{
			String sql = "update t_trace_wc_code t set t.count=t.count+:p1 WHERE t.states<>'D'"
					+ " and t.company_code=:p2 and t.base_code=:p3 and t.product_category_code=:p4 and t.packing_code=:p5 and t.creat_date=:p6";
			traceWcCodeDao.updateBySql(sql,new Parameter(count,companyCode,baseCode,productCategoryCode,packingCode,sjbh));
			traceWcCodeDao.flush();
		}
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceWcCodeDao.deleteById(id);
	}
	
}
