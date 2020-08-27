package com.surekam.modules.trace.tracelableprinter.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.modules.trace.tracelableprinter.entity.TraceLablePrinter;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.trace.tracelableprinter.dao.TraceLablePrinterDao;

/**
 * 标签打印机设置表Service
 * @author liw
 * @version 2019-10-14
 */
@Component
@Transactional(readOnly = true)
public class TraceLablePrinterService extends BaseService {

	@Autowired
	private TraceLablePrinterDao traceLablePrinterDao;
	
	public TraceLablePrinter get(String id) {
		return traceLablePrinterDao.get(id);
	}
	
	public Page<TraceLablePrinter> find(Page<TraceLablePrinter> page, TraceLablePrinter traceLablePrinter) {
		DetachedCriteria dc = traceLablePrinterDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceLablePrinter.FIELD_DEL_FLAG, TraceLablePrinter.DEL_FLAG_NORMAL));
		return traceLablePrinterDao.find(page, dc);
	}
	
	public List<TraceLablePrinter> findTraceLablePrinterList() {
		DetachedCriteria dc = traceLablePrinterDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceLablePrinter.FIELD_DEL_FLAG_XGXT, TraceLablePrinter.STATE_FLAG_DEL));
		return traceLablePrinterDao.find(dc);
	}
	
	@Transactional(readOnly = false)
	public void save(TraceLablePrinter traceLablePrinter) {
		traceLablePrinterDao.save(traceLablePrinter);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceLablePrinterDao.deleteById(id);
	}
	
}
