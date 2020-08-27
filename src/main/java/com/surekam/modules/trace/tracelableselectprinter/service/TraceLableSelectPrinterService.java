package com.surekam.modules.trace.tracelableselectprinter.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.modules.trace.tracelableselectprinter.entity.TraceLableSelectPrinter;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.trace.tracelableselectprinter.dao.TraceLableSelectPrinterDao;

/**
 * 用户选择打印机表Service
 * @author liw
 * @version 2019-10-14
 */
@Component
@Transactional(readOnly = true)
public class TraceLableSelectPrinterService extends BaseService {

	@Autowired
	private TraceLableSelectPrinterDao traceLableSelectPrinterDao;
	
	public TraceLableSelectPrinter get(String id) {
		return traceLableSelectPrinterDao.get(id);
	}
	
	public Page<TraceLableSelectPrinter> find(Page<TraceLableSelectPrinter> page, TraceLableSelectPrinter traceLableSelectPrinter) {
		DetachedCriteria dc = traceLableSelectPrinterDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceLableSelectPrinter.FIELD_DEL_FLAG, TraceLableSelectPrinter.DEL_FLAG_NORMAL));
		return traceLableSelectPrinterDao.find(page, dc);
	}
	
	public TraceLableSelectPrinter findTraceLableSelectPrinter(String kuid, String printId) {
		String hql = " from TraceLableSelectPrinter a where a.states<>'D' and a.officeId=:p1 and a.printerId=:p2";
		List<TraceLableSelectPrinter> list = traceLableSelectPrinterDao.find(hql,new Parameter(kuid, printId));
		if(list!=null && list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly = false)
	public void save(TraceLableSelectPrinter traceLableSelectPrinter) {
		traceLableSelectPrinterDao.save(traceLableSelectPrinter);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceLableSelectPrinterDao.deleteById(id);
	}
	
}
