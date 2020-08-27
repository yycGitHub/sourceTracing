package com.surekam.modules.trace.tracePrintRecord.service;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.trace.tracePrintRecord.dao.TracePrintRecordDao;
import com.surekam.modules.trace.tracePrintRecord.entity.TracePrintRecord;
import com.surekam.modules.traceproduct.entity.TraceProduct;

/**
 * 溯源标签打印记录service
 * @author wangyuewen
 *
 */
@Component
@Transactional(readOnly = true)
public class TracePrintRecordService extends BaseService {

	@Autowired
	private TracePrintRecordDao tracePrintRecordDao;
	
	public TracePrintRecord get(String id) {
		return tracePrintRecordDao.get(id);
	}
	
	@Transactional(readOnly = false)
	public void save(TracePrintRecord tracePrintRecord) {
		tracePrintRecord.setPrintNum(tracePrintRecord.getEndSerialNumber()  - tracePrintRecord.getStartSerialNumber() + 1);
		tracePrintRecordDao.save(tracePrintRecord);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		TracePrintRecord tracePrintRecord = tracePrintRecordDao.get(id);
		tracePrintRecord.setUpdateTime(new Date());
		tracePrintRecord.setUpdateUserid(UserUtils.getUser().getId());
		tracePrintRecordDao.save(tracePrintRecord);
		tracePrintRecordDao.deleteByXGXTId(id);
	}
	
	public Page<TracePrintRecord> find(Page<TracePrintRecord> page, TracePrintRecord tracePrintRecord) {
		DetachedCriteria dc = tracePrintRecordDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(!StringUtils.isNotBlank(tracePrintRecord.getApplyId())) {
			dc.add(Restrictions.eq("applyId", tracePrintRecord.getApplyId()));
		}
		dc.addOrder(Order.desc("creatTime"));
		return tracePrintRecordDao.find(page, dc);
	}
	
}
