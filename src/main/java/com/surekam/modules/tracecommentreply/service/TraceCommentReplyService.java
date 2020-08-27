package com.surekam.modules.tracecommentreply.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.tracecommentreply.entity.TraceCommentReply;
import com.surekam.modules.tracecommentreply.dao.TraceCommentReplyDao;

/**
 *    评论回复管理Service
 * @author xy
 * @version 2019-01-29
 */
@Component
@Transactional(readOnly = true)
public class TraceCommentReplyService extends BaseService {

	@Autowired
	private TraceCommentReplyDao traceCommentReplyDao;
	
	public TraceCommentReply get(String id) {
		return traceCommentReplyDao.get(id);
	}
	
	public Page<TraceCommentReply> find(Page<TraceCommentReply> page, String commentId) {
		DetachedCriteria dc = traceCommentReplyDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceCommentReply.FIELD_DEL_FLAG_XGXT, TraceCommentReply.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("commentId", commentId));
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentReplyDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(TraceCommentReply traceCommentReply) {
		traceCommentReplyDao.save(traceCommentReply);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceCommentReplyDao.deleteById(id);
	}
	
}
