package com.surekam.modules.tracesendmessagecode.service;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.modules.tracesendmessagecode.entity.TraceSendMessageCode;
import com.surekam.modules.tracesendmessagecode.dao.TraceSendMessageCodeDao;

/**
 * 手机验证码表Service
 * @author liw
 * @version 2019-07-15
 */
@Component
@Transactional(readOnly = true)
public class TraceSendMessageCodeService extends BaseService {

	@Autowired
	private TraceSendMessageCodeDao traceSendMessageCodeDao;
	
	public TraceSendMessageCode get(String id) {
		return traceSendMessageCodeDao.get(id);
	}
	
	public Page<TraceSendMessageCode> find(Page<TraceSendMessageCode> page, TraceSendMessageCode traceSendMessageCode) {
		DetachedCriteria dc = traceSendMessageCodeDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceSendMessageCode.FIELD_DEL_FLAG, TraceSendMessageCode.DEL_FLAG_NORMAL));
		return traceSendMessageCodeDao.find(page, dc);
	}
	
	public boolean findSendMessageCount(){
		String sql = "SELECT t.id FROM t_trace_send_message_code t WHERE DATE_FORMAT(t.creat_time,'%Y%m%d%H') = DATE_FORMAT(NOW(),'%Y%m%d%H')";
		List<Object> list =  traceSendMessageCodeDao.findBySql(sql);
		if(list!=null && list.size()>30){
			return true;
		}else{
			return false;
		}
	}
	
	@Transactional(readOnly = false)
	public void save(TraceSendMessageCode traceSendMessageCode) {
		traceSendMessageCodeDao.save(traceSendMessageCode);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceSendMessageCodeDao.deleteById(id);
	}
	
}
