package com.surekam.modules.tracecomment.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.modules.tracecomment.entity.TraceComment;
import com.surekam.modules.productbatch.dao.ProductBatchDao;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.tracecomment.dao.TraceCommentDao;

/**
 * 溯源产品评论Service
 * @author huangrd
 * @version 2019-01-08
 */
@Component
@Transactional(readOnly = true)
public class TraceCommentService extends BaseService {

	@Autowired
	private TraceCommentDao traceCommentDao;
	
	@Autowired
	private ProductBatchDao productBatchDao;
	
	public TraceComment get(String id) {
		return traceCommentDao.get(id);
	}
	
	/**
	   * 评价内容查询列表sql拼接
	 * @param openId
	 * @param productId
	 * @return
	 */
	public String getSql(String productId,String openId){
		return "SELECT a.* FROM (SELECT * FROM t_trace_comment t WHERE t.product_id = '"+ productId +"' AND t.states<>'D' AND t.audit_flag<>'3' AND t.open_id = '" + openId + "' UNION ALL SELECT * FROM t_trace_comment t WHERE t.product_id = '"+ productId + "' AND t.states<>'D' AND (t.open_id <> '"+ openId +"' OR ISNULL(t.open_id)) AND t.audit_flag = '2') a ORDER BY a.creat_time DESC";
	}
	
	public Page<TraceComment> find(Page<TraceComment> page, String productId,String openId){
		return traceCommentDao.findBySql(page,getSql(productId,openId),TraceComment.class);
	}
	
	public Page<TraceComment> find0(Page<TraceComment> page, String productId){
		DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(productId)) {
			dc.add(Restrictions.eq("productId",productId));
		}
		dc.add(Restrictions.eq("auditFlag", "2"));
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(page, dc);
	}
	
	@Transactional(readOnly = false)
	public void save(TraceComment traceComment) {
		traceCommentDao.save(traceComment);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceCommentDao.deleteById(id);
	}
	
	/**
	 * 右上角菜单评论结论查询
	 * @param page
	 * @param corpCode
	 * @param productName
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public Page<TraceComment> find3(Page<TraceComment> page, String admin,String productName,
			String startTime,String endTime,String corpCode,List<String> findChildrenOffice) throws ParseException {
		DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}else {
				dc.add(Restrictions.in("officeId", findChildrenOffice));
			}
		}else {
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}
		}
		if(StringUtils.isNotEmpty(productName)) {
			dc.add(Restrictions.like("productName",productName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			String sdate = startTime.replace("Z", " UTC");
			String edate = endTime.replace("Z", " UTC");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
			Date sd = format.parse(sdate);
			Date ed = format.parse(edate);
			Calendar cal = Calendar.getInstance();
		    cal.setTime(ed);//设置起时间
		    cal.add(Calendar.DATE, 1);//增加一天  
			dc.add(Restrictions.between("creatTime", sd, cal.getTime()));
		}
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(page, dc);
	}

	/**
	 * 右上角菜单评论结论查询
	 * @param page
	 * @param corpCode
	 * @param productName
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public Page<TraceComment> find3(Page<TraceComment> page, String admin,String productName,
			String startTime,String endTime,String corpCode,List<String> findChildrenOffice,User user) throws ParseException {
		DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}else {
				dc.add(Restrictions.in("officeId", findChildrenOffice));
			}
		}else {
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}
		}
		// 产品负责人过滤数据
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					List<String> bacthCodeList = productBatchDao.getbatchCodeList(user);
					if(bacthCodeList.size()!=0){
						dc.add(Restrictions.in("batchCode", bacthCodeList));
					} else {
						dc.add(Restrictions.eq("batchCode", "不可能的条件返回空"));
					}
				}
			}
			
		}
		if(StringUtils.isNotEmpty(productName)) {
			dc.add(Restrictions.like("productName",productName,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			String sdate = startTime.replace("Z", " UTC");
			String edate = endTime.replace("Z", " UTC");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
			Date sd = format.parse(sdate);
			Date ed = format.parse(edate);
			Calendar cal = Calendar.getInstance();
		    cal.setTime(ed);//设置起时间
		    cal.add(Calendar.DATE, 1);//增加一天  
			dc.add(Restrictions.between("creatTime", sd, cal.getTime()));
		}
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(page, dc);
	}
	
	/**
	 * 评论导出数据查询，不分页
	 * @param page
	 * @param corpCode
	 * @param productName
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws ParseException
	 */
	public List<TraceComment> findListExport(String admin,String productName,String startTime,
			String endTime,String corpCode,List<String> findChildrenOffice,User user) throws ParseException {
		DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				
				dc.add(Restrictions.eq("officeId", corpCode));
			}else {
				dc.add(Restrictions.in("officeId", findChildrenOffice));
			}
		}else {
			if(StringUtils.isNotEmpty(corpCode)) {//前端页面公司下拉框是否选择
				dc.add(Restrictions.eq("officeId", corpCode));
			}
		}
		if(StringUtils.isNotEmpty(productName)) {
			dc.add(Restrictions.like("productName",productName,MatchMode.ANYWHERE));
		}
		// 产品负责人过滤数据
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					List<String> bacthCodeList = productBatchDao.getbatchCodeList(user);
					if(bacthCodeList.size()!=0){
						dc.add(Restrictions.in("batchCode", bacthCodeList));
					} else {
						dc.add(Restrictions.eq("batchCode", "不可能的条件返回空"));
					}
				}
			}
			
		}
		if(StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {
			String sdate = startTime.replace("Z", " UTC");
			String edate = endTime.replace("Z", " UTC");
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
			Date sd = format.parse(sdate);
			Date ed = format.parse(edate);
			Calendar cal = Calendar.getInstance();
		    cal.setTime(ed);//设置起时间
		    cal.add(Calendar.DATE, 1);//增加一天  
			dc.add(Restrictions.between("creatTime", sd, cal.getTime()));
		}
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(dc);
	}
	
	
	public long commentAllCount(String corpCode) {
		/*DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
		}
		dc.add(Restrictions.ne("auditFlag", "3"));//排除未通过的评论
		long count = traceCommentDao.count(dc);
		return count;*/
		
		String sql = "select a.* from t_trace_comment a where a.states<>'D' and a.audit_flag<>'3'";
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + corpCode + "' OR tt.PARENT_IDS LIKE '%" + corpCode + "%')";
		}
		List<Object> list = traceCommentDao.findBySql(sql);
		if(list!=null){
			return list.size();
		}else{
			return 0;
		}
		
	}
	
	public long commentHighCount(String corpCode) {
		/*DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(corpCode)) {
			dc.add(Restrictions.eq("officeId",corpCode));
		}
		dc.add(Restrictions.gt("score", "2"));//评分星级大于2
		dc.add(Restrictions.le("score", "5"));//评分星级小于等于5
		dc.add(Restrictions.ne("auditFlag", "3"));//排除未通过的评论
		long count = traceCommentDao.count(dc);
		return count;*/ 
		
		String sql = "select a.* from t_trace_comment a where a.states<>'D' and a.audit_flag<>'3' and score>2 and score<=5";
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + corpCode + "' OR tt.PARENT_IDS LIKE '%" + corpCode + "%')";
		}
		List<Object> list = traceCommentDao.findBySql(sql);
		if(list!=null){
			return list.size();
		}else{
			return 0;
		}
	}
	public long commentLowCount(String corpCode) {
		/*DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(corpCode)) {
			dc.add(Restrictions.eq("officeId",corpCode));
		}
		dc.add(Restrictions.le("score", "2"));//评分星级小于等于2
		dc.add(Restrictions.ne("auditFlag", "3"));//排除未通过的评论
		long count = traceCommentDao.count(dc);
		return count;*/
		
		String sql = "select a.* from t_trace_comment a where a.states<>'D' and a.audit_flag<>'3' and score<=2";
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + corpCode + "' OR tt.PARENT_IDS LIKE '%" + corpCode + "%')";
		}
		List<Object> list = traceCommentDao.findBySql(sql);
		if(list!=null){
			return list.size();
		}else{
			return 0;
		}
	}
	
	/**
	 * 新首页右下角框查询全部评论
	 * @param page
	 * @param corpCode
	 * @return
	 */
	public Page<TraceComment> find2(Page<TraceComment> page, String corpCode){
		/*DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(corpCode)) {
			dc.add(Restrictions.eq("officeId",corpCode));
		}
		dc.add(Restrictions.ne("auditFlag", "3"));//排除未通过的评论
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(page, dc);*/
		String sql = "select a.* from t_trace_comment a where a.states<>'D' and a.audit_flag<>'3'";
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + corpCode + "' OR tt.PARENT_IDS LIKE '%" + corpCode + "%')";
		}
		sql+=" order by a.creat_time";
		Page<TraceComment> list = traceCommentDao.findBySql(page,sql,null,TraceComment.class);
		return list;
	}

	/**
	 * 新首页右下角框查询全部评论
	 * @param page
	 * @param corpCode
	 * @return
	 */
	public Page<TraceComment> find2(Page<TraceComment> page, String corpCode,User user){
		/*DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(corpCode)) {
			dc.add(Restrictions.eq("officeId",corpCode));
		}
		dc.add(Restrictions.ne("auditFlag", "3"));//排除未通过的评论
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(page, dc);*/
		String sql = "select a.* from t_trace_comment a where a.states<>'D' and a.audit_flag<>'3'";
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + corpCode + "' OR tt.PARENT_IDS LIKE '%" + corpCode + "%')";
		}
		// 产品负责人过滤数据
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					sql+=" and a.batch_code in (select p.batch_code from t_trace_product_batch p where p.states <> 'D' and p.creat_userid  = '" + user.getId() + "')";
				}
			}
		}
		sql+=" order by a.creat_time";
		Page<TraceComment> list = traceCommentDao.findBySql(page,sql,null,TraceComment.class);
		return list;
	}
	/**
	 * 新首页右下角框查询好评评论
	 * @param page
	 * @param corpCode
	 * @return
	 */
	public Page<TraceComment> findhigh(Page<TraceComment> page, String corpCode){
		/*DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(corpCode)) {
			dc.add(Restrictions.eq("officeId",corpCode));
		}
		dc.add(Restrictions.gt("score", "2"));//评分星级大于2
		dc.add(Restrictions.le("score", "5"));//评分星级小于等于5
		dc.add(Restrictions.ne("auditFlag", "3"));//排除未通过的评论
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(page, dc);*/
		
		String sql = "select a.* from t_trace_comment a where a.states<>'D' and a.audit_flag<>'3' and a.score>2 and a.score<=5";
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + corpCode + "' OR tt.PARENT_IDS LIKE '%" + corpCode + "%')";
		}
		sql+=" order by a.creat_time";
		Page<TraceComment> list = traceCommentDao.findBySql(page,sql,null,TraceComment.class);
		return list;
	}

	/**
	 * 新首页右下角框查询好评评论-生产负责人只看自己的
	 * @param page
	 * @param corpCode
	 * @return
	 */
	public Page<TraceComment> findhigh(Page<TraceComment> page, String corpCode,User user){
		/*DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(corpCode)) {
			dc.add(Restrictions.eq("officeId",corpCode));
		}
		dc.add(Restrictions.gt("score", "2"));//评分星级大于2
		dc.add(Restrictions.le("score", "5"));//评分星级小于等于5
		dc.add(Restrictions.ne("auditFlag", "3"));//排除未通过的评论
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(page, dc);*/
		
		String sql = "select a.* from t_trace_comment a where a.states<>'D' and a.audit_flag<>'3' and a.score>2 and a.score<=5";
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + corpCode + "' OR tt.PARENT_IDS LIKE '%" + corpCode + "%')";
		}
		// 产品负责人过滤数据
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					sql+=" and a.batch_code in (select p.batch_code from t_trace_product_batch p where p.states <> 'D' and p.creat_userid  = '" + user.getId() + "')";
				}
			}
		}
		sql+=" order by a.creat_time";
		Page<TraceComment> list = traceCommentDao.findBySql(page,sql,null,TraceComment.class);
		return list;
	}
	/**
	 * 新首页右下角框查询差评评论
	 * @param page
	 * @param corpCode
	 * @return
	 */
	public Page<TraceComment> findlow(Page<TraceComment> page, String corpCode){
		/*DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(corpCode)) {
			dc.add(Restrictions.eq("officeId",corpCode));
		}
		dc.add(Restrictions.le("score", "2"));//评分星级小于等于2
		dc.add(Restrictions.ne("auditFlag", "3"));//排除未通过的评论
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(page, dc);*/
		
		String sql = "select a.* from t_trace_comment a where a.states<>'D' and a.audit_flag<>'3' and a.score<=2";
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + corpCode + "' OR tt.PARENT_IDS LIKE '%" + corpCode + "%')";
		}
		sql+=" order by a.creat_time";
		Page<TraceComment> list = traceCommentDao.findBySql(page,sql,null,TraceComment.class);
		return list;
	}

	/**
	 * 新首页右下角框查询差评评论-生产负责人只看自己的
	 * @param page
	 * @param corpCode
	 * @return
	 */
	public Page<TraceComment> findlow(Page<TraceComment> page, String corpCode,User user){
		/*DetachedCriteria dc = traceCommentDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceComment.FIELD_DEL_FLAG_XGXT, TraceComment.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(corpCode)) {
			dc.add(Restrictions.eq("officeId",corpCode));
		}
		dc.add(Restrictions.le("score", "2"));//评分星级小于等于2
		dc.add(Restrictions.ne("auditFlag", "3"));//排除未通过的评论
		dc.addOrder(Order.desc("creatTime"));
		return traceCommentDao.find(page, dc);*/
		
		String sql = "select a.* from t_trace_comment a where a.states<>'D' and a.audit_flag<>'3' and a.score<=2";
		if(StringUtils.isNotEmpty(corpCode)) {
			//dc.add(Restrictions.eq("officeId",corpCode));
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + corpCode + "' OR tt.PARENT_IDS LIKE '%" + corpCode + "%')";
		}
		// 产品负责人过滤数据
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					sql+=" and a.batch_code in (select p.batch_code from t_trace_product_batch p where p.states <> 'D' and p.creat_userid  = '" + user.getId() + "')";
				}
			}
		}
		sql+=" order by a.creat_time";
		Page<TraceComment> list = traceCommentDao.findBySql(page,sql,null,TraceComment.class);
		return list;
	}
	
	/*
	 * 按月分组统计留言量
	 * */
	public List<Object> findMonthscComment(String productId,User user,String officeId){
		String sql = "select DATE_FORMAT(creat_time,'%Y-%m') months,count(id)count from t_trace_comment WHERE audit_flag <> '3' and states <> 'D' ";
		if(StringUtils.isNotBlank(productId)){
			sql+=" and product_id = '"+productId+"'";
		}
		if(StringUtils.isNotBlank(officeId)){
			sql+=" and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		}else {
			if(!user.isAdmin()){
				if(StringUtils.isNotBlank(user.getOffice().getId())){
					sql+=" and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + user.getOffice().getId() + "' OR tt.PARENT_IDS LIKE '%" + user.getOffice().getId() + "%')";
				}
			}
		}
		sql+=" group by months ORDER BY months DESC LIMIT 0,12";
		return traceCommentDao.findBySql(sql);
	}
	
	/**
	 * 按月分组统计留言量--传入code（wangcheng或者tulufan）id
	 * */
	public List<Object> findMonthscComment3(String officeId){
		String sql = "select DATE_FORMAT(creat_time,'%Y-%m') months,count(id)count from t_trace_comment WHERE audit_flag <> '3' and states <> 'D' ";
		sql+=" and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		sql+=" group by months ORDER BY months DESC LIMIT 0,12";
		return traceCommentDao.findBySql(sql);
	}
}
