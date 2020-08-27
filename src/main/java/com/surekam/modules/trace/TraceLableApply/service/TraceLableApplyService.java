package com.surekam.modules.trace.TraceLableApply.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.DateUtils;
import com.surekam.common.utils.IdGen;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.modules.TraceDeliveryAddress.dao.TraceDeliveryAddressDao;
import com.surekam.modules.TraceDeliveryAddress.entity.TraceDeliveryAddress;
import com.surekam.modules.productbatch.dao.ProductBatchDao;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.sys.dao.OfficeDao;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.utils.TracingSourceCode;
import com.surekam.modules.trace.TraceLableApply.dao.TraceLableApplyDao;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.trace.TraceLableContent.dao.TraceLableContentDao;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;
import com.surekam.modules.trace.TraceLableTemplate.dao.TraceLableTemplateDao;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;
import com.surekam.modules.tracecode.dao.TraceCodeDao;
import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.tracecode.service.TraceCodeService;
import com.surekam.modules.traceproduct.dao.TraceProductDao;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproductaudit.dao.TraceProductAuditDao;
import com.surekam.modules.traceproductaudit.entity.SavaAuditReq;
import com.surekam.modules.traceproductauditnew.dao.TraceProductAuditNewDao;
import com.surekam.modules.traceproductauditnew.entity.TraceProductAuditNew;
import com.surekam.modules.tracewccode.service.TraceWcCodeService;

/**
 * 标签申请
 * 
 * @author wangyuewen
 * @param <TraceLableApplyService>
 *
 */
@Component
@Transactional(readOnly = true)
public class TraceLableApplyService extends BaseService {

	@Autowired
	private TraceLableApplyDao traceLableApplyDao;

	@Autowired
	private TraceLableContentDao traceLableContentDao;

	@Autowired
	private TraceLableTemplateDao traceLableTemplateDao;

	@Autowired
	private TraceDeliveryAddressDao traceDeliveryAddressDao;

	@Autowired
	private TraceProductDao traceProductDao;

	@Autowired
	private ProductBatchDao productBatchDao;

	@Autowired
	private TraceCodeDao traceCodeDao;

	@Autowired
	private OfficeDao officeDao;

	@Autowired
	private TraceCodeService traceCodeService;

	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private TraceProductAuditDao traceProductAuditDao;
	
	@Autowired
	private TraceWcCodeService traceWcCodeService;
	
	@Autowired
	private TraceProductAuditNewDao traceProductAuditNewDao;

	public TraceLableApply get(String id) {
		return traceLableApplyDao.get(id);
	}

	public List<ProductBatch> getBatchId(String name) {
		DetachedCriteria dc = productBatchDao.createDetachedCriteria();
		dc.add(Restrictions.ne(ProductBatch.FIELD_DEL_FLAG_XGXT, ProductBatch.STATE_FLAG_DEL));
		dc.add(Restrictions.like("batchName", name, MatchMode.ANYWHERE));
		List<ProductBatch> list = productBatchDao.find(dc);
		return list;
	}

	public List<TraceProduct> getTraceProductId(String name) {
		DetachedCriteria dc = traceProductDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		dc.add(Restrictions.like("productName", name, MatchMode.ANYWHERE));
		List<TraceProduct> list = traceProductDao.find(dc);
		return list;
	}

	/**
	 * 标签管理查询列表sql拼接
	 * 
	 * @param batchName
	 * @param productName
	 * @param time
	 * @param cancelFlag
	 * @param printFlag
	 * @param auditFlag
	 * @return
	 */
	public String getSql(String batchName, String productName, String time, String cancelFlag, String printFlag,
			String auditFlag, String officeId, List<String> findChildrenOffice, String admin) {
		StringBuffer sql = new StringBuffer(500);
		sql.append("SELECT a.* FROM (SELECT apply.* FROM t_trace_lable_apply apply , ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" t_trace_product_batch batch left join ");
		}
		sql.append(" t_trace_product product ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" on product.id = batch.product_id ");
		}
		sql.append(" WHERE apply.states<>'D' and product.states<>'D' and apply.trace_product_id = product.id ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" and batch.states<>'D' ");
		}
		// 批次名称模糊匹配
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" and  batch.batch_code = '" + batchName + "' ");
		}
		sql.append(" ) a left join t_trace_product product on a.trace_product_id = product.id WHERE 1=1 ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" and  a.batch_id <> ''  ");
		}
		if (StringUtils.isNotBlank(admin)) {// 非管理员进入
			if (StringUtils.isNotEmpty(officeId)) {// 前端页面公司下拉框是否选择
				sql.append(" and product.office_id = \"" + officeId + "\"");
			} else {
				String officeData = "'" + StringUtils.join(findChildrenOffice, "','") + "'";
				sql.append(" and product.office_id in(" + officeData + ")");
			}
		} else {
			if (StringUtils.isNotEmpty(officeId)) {// 前端页面公司下拉框是否选择
				sql.append(" and product.office_id = \"" + officeId + "\"");
			}
		}
		// 产品名称模糊匹配
		if (StringUtils.isNotBlank(productName)) {
			sql.append(" and product.product_name like '%" + productName + "%' ");
		}
		if (StringUtils.isNotBlank(cancelFlag)) {
			sql.append(" and a.cancel_flag=" + cancelFlag);
		}
		if (StringUtils.isNotBlank(printFlag)) {
			sql.append(" and a.print_flag=" + printFlag);
		}
		if (StringUtils.isNotBlank(auditFlag)) {
			sql.append(" and a.audit_flag=" + auditFlag);
		}
		if (StringUtils.isNotBlank(time) && !"0".equals(time)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
			String endTime = format.format(new Date());
			String startTime = format2.format(getDateBefore(time));
			sql.append(" and a.creat_time between " + " '" + startTime + "' " + " and " + " '" + endTime + "' ");
		}
		sql.append(" order by a.creat_time desc ");
		String str = new String(sql);
		return str;

	}

	/**
	 * 标签管理-列表查询（修复了输入批次号，查询出该批次产品的全部标签问题）
	 * 
	 * @param batchName
	 * @param productName
	 * @param time
	 * @param cancelFlag
	 * @param printFlag
	 * @param auditFlag
	 * @param officeId
	 * @param findChildrenOffice
	 * @param admin
	 * @param user
	 * @return
	 */
	public String getSqlone(String batchName, String productName, String time, String cancelFlag, String printFlag, String auditFlag, String officeId, List<String> findChildrenOffice, String admin, User user) {
		StringBuffer sql = new StringBuffer(500);
		sql.append("SELECT * FROM (SELECT a.* FROM (SELECT apply.* FROM t_trace_lable_apply apply ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" INNER JOIN t_trace_product_batch batch  on apply.batch_id = batch.id and batch.states<>'D' and batch.batch_code = '" + batchName + "' ");
		}
		sql.append(" INNER JOIN t_trace_product product on product.id = apply.trace_product_id and apply.states<>'D' and product.states <>'D' ");
		// 产品名称模糊匹配
		if (StringUtils.isNotBlank(productName)) {
			sql.append(" and product.product_name like '%" + productName + "%' ");
		}
		if (StringUtils.isNotBlank(admin)) {// 非管理员进入
			if (StringUtils.isNotEmpty(officeId)) {// 前端页面公司下拉框是否选择
				sql.append(" and product.office_id = \"" + officeId + "\"");
			} else {
				String officeData = "'" + StringUtils.join(findChildrenOffice, "','") + "'";
				sql.append(" and product.office_id in(" + officeData + ")");
			}
		} else {
			if (StringUtils.isNotEmpty(officeId)) {// 前端页面公司下拉框是否选择
				sql.append(" and product.office_id = \"" + officeId + "\"");
			}
		}
		sql.append(" ) a where 1=1 and a.sys_id = '0' ");
		// 产品负责人过滤数据
		if (user.getRoleList() != null) {
			List<Role> roleList = user.getRoleList();
			if (roleList.size() == 1) {
				Role role = roleList.get(0);
				// 只能看自己的数据的角色
				if (role.getDataScope().equals("8")) {
					sql.append(" and a.creat_userid = \"" + user.getId() + "\"");
				}
			}
		}
		if (StringUtils.isNotBlank(cancelFlag)) {
			sql.append(" and a.cancel_flag=" + cancelFlag);
		}
		if (StringUtils.isNotBlank(printFlag)) {
			sql.append(" and a.print_flag=" + printFlag);
		}
		if (StringUtils.isNotBlank(auditFlag)) {
			sql.append(" and a.audit_flag=" + auditFlag);
		}
		if (StringUtils.isNotBlank(time) && !"0".equals(time)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
			String endTime = format.format(new Date());
			String startTime = format2.format(getDateBefore(time));
			sql.append(" and a.creat_time between " + " '" + startTime + "' " + " and " + " '" + endTime + "' ");
		}
		sql.append(" union ");
		sql.append("SELECT a.* FROM (SELECT apply.* FROM t_trace_lable_apply apply ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" INNER JOIN t_trace_product_batch batch  on apply.batch_id = batch.id and batch.states<>'D' and batch.batch_code = '" + batchName + "' ");
		}
		sql.append(" INNER JOIN t_trace_product product on product.id = apply.trace_product_id and apply.states<>'D' and product.states <>'D' ");
		// 产品名称模糊匹配
		if (StringUtils.isNotBlank(productName)) {
			sql.append(" and product.product_name like '%" + productName + "%' ");
		}
		if (StringUtils.isNotBlank(admin)) {// 非管理员进入
			if (StringUtils.isNotEmpty(officeId)) {// 前端页面公司下拉框是否选择
				sql.append(" and product.office_id = \"" + officeId + "\"");
			} else {
				String officeData = "'" + StringUtils.join(findChildrenOffice, "','") + "'";
				sql.append(" and product.office_id in(" + officeData + ")");
			}
		} else {
			if (StringUtils.isNotEmpty(officeId)) {// 前端页面公司下拉框是否选择
				sql.append(" and product.office_id = \"" + officeId + "\"");
			}
		}
		sql.append(" ) a where 1=1 and a.sys_id in ('1','2') ");
		// 产品负责人过滤数据
		if (user.getRoleList() != null) {
			List<Role> roleList = user.getRoleList();
			if (roleList.size() == 1) {
				Role role = roleList.get(0);
				// 只能看自己的数据的角色
				if (role.getDataScope().equals("8")) {
					sql.append(" and a.creat_userid = \"" + user.getId() + "\"");
				}
			}
		}
		if (StringUtils.isNotBlank(cancelFlag)) {
			sql.append(" and a.cancel_flag=" + cancelFlag);
		}
		if (StringUtils.isNotBlank(printFlag)) {
			sql.append(" and a.print_flag=" + printFlag);
		}
		if (StringUtils.isNotBlank(auditFlag)) {
			sql.append(" and a.audit_flag=" + auditFlag);
		}
		if (StringUtils.isNotBlank(time) && !"0".equals(time)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
			String endTime = format.format(new Date());
			String startTime = format2.format(getDateBefore(time));
			sql.append(" and a.creat_time between " + " '" + startTime + "' " + " and " + " '" + endTime + "' ");
		}
		String findByState = traceProductAuditDao.findByState("1");
		sql.append("and a.batch_id in (" + findByState + ") ");
		sql.append(") aa order by aa.creat_time desc ");
		String str = new String(sql);
		return str;
	}
	
	/**
	 * 标签管理-列表查询（修复了输入批次号，查询出该批次产品的全部标签问题）
	 * 
	 * @param batchName
	 * @param productName
	 * @param time
	 * @param cancelFlag
	 * @param printFlag
	 * @param auditFlag
	 * @param officeId
	 * @param findChildrenOffice
	 * @param admin
	 * @param user
	 * @return
	 */
	public String getSqlNew(String batchCode, String productName, String time, String printFlag, String type, String officeId, List<String> findChildrenOffice, User user) {
		StringBuffer sql = new StringBuffer(500);
		sql.append("SELECT * FROM (SELECT a.* FROM (SELECT apply.* FROM t_trace_lable_apply apply ");
		sql.append(" INNER JOIN t_trace_product product on product.id = apply.trace_product_id and apply.states<>'D' and product.states <>'D' ");
		// 产品名称模糊匹配
		if (StringUtils.isNotBlank(productName)) {
			sql.append(" and product.product_name like '%" + productName + "%' ");
		}
		String officeData = "'" + StringUtils.join(findChildrenOffice, "','") + "'";
		sql.append(" and product.office_id in(" + officeData + ")");
		
		if (StringUtils.isNotEmpty(officeId)) {// 前端页面公司下拉框是否选择
			sql.append(" and product.office_id = \"" + officeId + "\"");
		}
		sql.append(" ) a where 1=1 and a.sys_id = '0' ");
		
		if (StringUtils.isNotBlank(batchCode)) {
			sql.append(" and a.apply_code like '%" + batchCode + "%'");
		}
		if (StringUtils.isNotBlank(printFlag)) {
			sql.append(" and a.print_flag=" + printFlag);
		}
		if (StringUtils.isNotBlank(type)) {
			sql.append(" and a.audit_flag=" + type);
		}
		if (StringUtils.isNotBlank(time) && !"0".equals(time)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
			String endTime = format.format(new Date());
			String startTime = format2.format(getDateBefore(time));
			sql.append(" and a.creat_time between " + " '" + startTime + "' " + " and " + " '" + endTime + "' ");
		}
		sql.append(") aa order by aa.creat_time desc ");
		return new String(sql);
	}

	/**
	 * 标签管理查询列表sql拼接加上条件，生产负责人只看自己申请的标签
	 * 
	 * @param batchName
	 * @param productName
	 * @param time
	 * @param cancelFlag
	 * @param printFlag
	 * @param auditFlag
	 * @return
	 */
	public String getSql(String batchName, String productName, String time, String cancelFlag, String printFlag,
			String auditFlag, String officeId, List<String> findChildrenOffice, String admin, User user) {
		StringBuffer sql = new StringBuffer(500);
		sql.append("SELECT a.* FROM (SELECT apply.* FROM t_trace_lable_apply apply , ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" t_trace_product_batch batch left join ");
		}
		sql.append(" t_trace_product product ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" on product.id = batch.product_id ");
		}
		sql.append(" WHERE apply.states<>'D' and product.states<>'D' and apply.trace_product_id = product.id ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" and batch.states<>'D' ");
		}
		// 批次名称模糊匹配
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" and  batch.batch_code = '" + batchName + "' ");
		}
		sql.append(" ) a left join t_trace_product product on a.trace_product_id = product.id WHERE 1=1 ");
		if (StringUtils.isNotBlank(batchName)) {
			sql.append(" and  a.batch_id <> ''  ");
		}
		if (StringUtils.isNotBlank(admin)) {// 非管理员进入
			if (StringUtils.isNotEmpty(officeId)) {// 前端页面公司下拉框是否选择
				sql.append(" and product.office_id = \"" + officeId + "\"");
			} else {
				String officeData = "'" + StringUtils.join(findChildrenOffice, "','") + "'";
				sql.append(" and product.office_id in(" + officeData + ")");
			}
		} else {
			if (StringUtils.isNotEmpty(officeId)) {// 前端页面公司下拉框是否选择
				sql.append(" and product.office_id = \"" + officeId + "\"");
			}
		}
		// 产品负责人过滤数据
		if (user.getRoleList() != null) {
			List<Role> roleList = user.getRoleList();
			if (roleList.size() == 1) {
				Role role = roleList.get(0);
				// 只能看自己的数据的角色
				if (role.getDataScope().equals("8")) {
					sql.append(" and a.creat_userid = \"" + user.getId() + "\"");
				}
			}

		}
		// 产品名称模糊匹配
		if (StringUtils.isNotBlank(productName)) {
			sql.append(" and product.product_name like '%" + productName + "%' ");
		}
		if (StringUtils.isNotBlank(cancelFlag)) {
			sql.append(" and a.cancel_flag=" + cancelFlag);
		}
		if (StringUtils.isNotBlank(printFlag)) {
			sql.append(" and a.print_flag=" + printFlag);
		}
		if (StringUtils.isNotBlank(auditFlag)) {
			sql.append(" and a.audit_flag=" + auditFlag);
		}
		if (StringUtils.isNotBlank(time) && !"0".equals(time)) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
			String endTime = format.format(new Date());
			String startTime = format2.format(getDateBefore(time));
			sql.append(" and a.creat_time between " + " '" + startTime + "' " + " and " + " '" + endTime + "' ");
		}
		sql.append(" order by a.creat_time desc ");
		String str = new String(sql);
		return str;

	}

	public Page<TraceLableApply> find(Page<TraceLableApply> page, TraceLableApply label) {
		DetachedCriteria dc = traceLableApplyDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceLableApply.FIELD_DEL_FLAG_XGXT, TraceLableApply.STATE_FLAG_DEL));
		/*
		 * if(StringUtils.isNotBlank(label.getBatchId())) {
		 * dc.add(Restrictions.eq("batchId", label.getBatchId())); }else{
		 * dc.add(Restrictions.eq("batchId", "")); }
		 */
		if (StringUtils.isNotBlank(label.getTraceProductId())) {
			dc.add(Restrictions.eq("traceProductId", label.getTraceProductId()));
		}
		if (StringUtils.isNotBlank(label.getCreatUserid())) {
			dc.add(Restrictions.eq("creatUserid", label.getCreatUserid()));
		}
		dc.addOrder(Order.desc("creatTime"));
		Page<TraceLableApply> lableApplyPage = traceLableApplyDao.find(page, dc);
		List<TraceLableApply> labelApplyList = lableApplyPage.getList();
		if (null != labelApplyList && 0 != labelApplyList.size()) {
			for (Iterator<TraceLableApply> iterator = labelApplyList.iterator(); iterator.hasNext();) {
				TraceLableApply traceLableApply = (TraceLableApply) iterator.next();
				if (StringUtils.isNotBlank(traceLableApply.getAddressId())) {
					traceLableApply.setDeliveryAddress(traceDeliveryAddressDao.get(traceLableApply.getAddressId()));
				}
				if (StringUtils.isNotBlank(traceLableApply.getLabelTemplateId())) {
					traceLableApply.setLableTemplate(traceLableTemplateDao.get(traceLableApply.getLabelTemplateId()));
				}
			}
		}
		return lableApplyPage;
	}

	/**
	 * PC端查询我的标签，不分页
	 * 
	 * @param page
	 * @param label
	 * @return --2019-01-10 11:20 --xy
	 */
	public List<TraceLableApply> find(TraceLableApply label) {
		DetachedCriteria dc = traceLableApplyDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceLableApply.FIELD_DEL_FLAG_XGXT, TraceLableApply.STATE_FLAG_DEL));
		if (StringUtils.isNotBlank(label.getTraceProductId())) {
			dc.add(Restrictions.eq("traceProductId", label.getTraceProductId()));
		}
		if (StringUtils.isNotBlank(label.getCreatUserid())) {
			dc.add(Restrictions.eq("creatUserid", label.getCreatUserid()));
		}
		dc.addOrder(Order.desc("creatTime"));
		List<TraceLableApply> labelApplyList = traceLableApplyDao.find(dc);
		if (null != labelApplyList && 0 != labelApplyList.size()) {
			for (Iterator<TraceLableApply> iterator = labelApplyList.iterator(); iterator.hasNext();) {
				TraceLableApply traceLableApply = (TraceLableApply) iterator.next();
				if (StringUtils.isNotBlank(traceLableApply.getAddressId())) {
					traceLableApply.setDeliveryAddress(traceDeliveryAddressDao.get(traceLableApply.getAddressId()));
				}
				if (StringUtils.isNotBlank(traceLableApply.getLabelTemplateId())) {
					traceLableApply.setLableTemplate(traceLableTemplateDao.get(traceLableApply.getLabelTemplateId()));
				}
			}
		}
		return labelApplyList;
	}

	/**
	 * 标签管理查询
	 * 
	 * @param page
	 * @param batchName
	 * @param productName
	 * @param time
	 * @param cancelFlag
	 * @param printFlag
	 * @param auditFlag
	 * @return
	 */
	public Page<TraceLableApply> find(Page<TraceLableApply> page, String batchName, String productName, String time,
			String cancelFlag, String printFlag, String auditFlag, String officeId, List<String> findChildrenOffice,
			String admin) {
		Page<TraceLableApply> lableApplyPage = traceProductDao.findBySql(page, getSql(batchName, productName, time,
				cancelFlag, printFlag, auditFlag, officeId, findChildrenOffice, admin), TraceLableApply.class);
		List<TraceLableApply> labelApplyList = lableApplyPage.getList();
		if (null != labelApplyList && 0 != labelApplyList.size()) {
			for (Iterator<TraceLableApply> iterator = labelApplyList.iterator(); iterator.hasNext();) {
				TraceLableApply traceLableApply = (TraceLableApply) iterator.next();
				if (StringUtils.isNotBlank(traceLableApply.getAddressId())) {

					TraceDeliveryAddress traceDeliveryAddress = new TraceDeliveryAddress();
					traceDeliveryAddress = traceDeliveryAddressDao.get(traceLableApply.getAddressId());

					if (traceDeliveryAddress != null) {
						StringBuffer str = new StringBuffer();
						traceLableApply.setDeliveryAddress(traceDeliveryAddress);
						if (traceDeliveryAddress.getReceiver() != null
								&& !traceDeliveryAddress.getReceiver().equals("")) {
							str.append(traceDeliveryAddress.getReceiver() + '\u0009');
						}
						if (traceDeliveryAddress.getPhoneNum() != null
								&& !traceDeliveryAddress.getPhoneNum().equals("")) {
							str.append(traceDeliveryAddress.getPhoneNum() + '\u0009');
						}
						if (traceDeliveryAddress.getProvince() != null
								&& !traceDeliveryAddress.getProvince().equals("")) {
							str.append(traceDeliveryAddress.getProvince());
						}
						if (traceDeliveryAddress.getCity() != null && !traceDeliveryAddress.getCity().equals("")) {
							str.append(traceDeliveryAddress.getCity());
						}
						if (traceDeliveryAddress.getArea() != null && !traceDeliveryAddress.getArea().equals("")) {
							str.append(traceDeliveryAddress.getArea());
						}
						if (traceDeliveryAddress.getStreet() != null && !traceDeliveryAddress.getStreet().equals("")) {
							str.append(traceDeliveryAddress.getStreet());
						}
						if (traceDeliveryAddress.getDetailAddress() != null
								&& !traceDeliveryAddress.getDetailAddress().equals("")) {
							str.append(traceDeliveryAddress.getDetailAddress());
						}
						traceLableApply.setReceivingAddress(str.toString());
					}

				}
				if (StringUtils.isNotBlank(traceLableApply.getLabelTemplateId())) {
					traceLableApply.setLableTemplate(traceLableTemplateDao.get(traceLableApply.getLabelTemplateId()));
				} else {
					traceLableApply.setLableTemplate(new TraceLableTemplate());
				}
				if (StringUtils.isNotBlank(traceLableApply.getTraceProductId())) {
					TraceProduct traceProduct = traceProductDao.get(traceLableApply.getTraceProductId());
					traceLableApply.setTraceProduct(traceProduct);
					traceLableApply.setProductName(traceProduct.getProductName());
				}

				// 获取到公司名称
				if (StringUtils.isNotBlank(traceLableApply.getOfficeId())) {
					Office office = officeService.get(traceLableApply.getOfficeId());
					if (office != null) {
						traceLableApply.setCorpName(office.getName());
					}

				}

				if (StringUtils.isNotBlank(traceLableApply.getBatchId())) {
					traceLableApply.setProductBatch(productBatchDao.get(traceLableApply.getBatchId()));
				}
				List<TraceCode> codelist = new ArrayList<TraceCode>();
				codelist = traceCodeDao.findByApplyId(traceLableApply.getId());
				if (codelist.size() > 0) {
					traceLableApply.setSerialCode(
							codelist.get(0).getTraceCode() + "--" + codelist.get(codelist.size() - 1).getTraceCode());
					// 判断溯源码list是否为空
					if (null != codelist && 0 != codelist.size()) {
						// 判断溯源码流水号是否为0，是则不显示流水号只显示批次号，否则显示流水号不显示批次号
						if (0 == codelist.get(0).getSerialNumber()) {
							// traceLableApply.setBatchNumber(productBatchDao.get(codelist.get(0).getBatchId()).getBatchCode());
						} else {
							traceLableApply.setSerialNumber(codelist.get(0).getSerialNumber() + "--"
									+ codelist.get(codelist.size() - 1).getSerialNumber());
						}
						if (null != codelist.get(0).getBatchId() && !"".equals(codelist.get(0).getBatchId())) {
							ProductBatch productBatch = new ProductBatch();
							productBatch = productBatchDao.get(codelist.get(0).getBatchId());
							if (null != productBatch.getBatchCode() && !"".equals(productBatch.getBatchCode())) {
								traceLableApply.setBatchNumber(productBatch.getBatchCode());
							}
						}

					}
				}

			}
		}
		return lableApplyPage;
	}
	
	/**
	 * 标签管理查询
	 * 
	 * @param page
	 * @param batchName
	 * @param productName
	 * @param time
	 * @param cancelFlag
	 * @param printFlag
	 * @param auditFlag
	 * @return
	 */
	public Page<TraceLableApply> findTraceLableApplyList(Page<TraceLableApply> page, String batchCode, String productName, String time,
			String printFlag, String type, String officeId, List<String> findChildrenOffice, User user) {
		Page<TraceLableApply> lableApplyPage = traceProductDao.findBySql(page, getSqlNew(batchCode, productName, time,
				printFlag, type, officeId, findChildrenOffice, user), TraceLableApply.class);
		List<TraceLableApply> labelApplyList = lableApplyPage.getList();
		if (null != labelApplyList && 0 != labelApplyList.size()) {
			for (Iterator<TraceLableApply> iterator = labelApplyList.iterator(); iterator.hasNext();) {
				TraceLableApply traceLableApply = (TraceLableApply) iterator.next();
				StringBuffer str = new StringBuffer();
				if (StringUtils.isNotBlank(traceLableApply.getAddressId())) {
					TraceDeliveryAddress traceDeliveryAddress = new TraceDeliveryAddress();
					if("1".equals(traceLableApply.getAddressId())){
						str.append("自提");
						traceLableApply.setReceivingAddress(str.toString());
					}else{
						traceDeliveryAddress = traceDeliveryAddressDao.get(traceLableApply.getAddressId());
						if (traceDeliveryAddress != null) {
							traceLableApply.setDeliveryAddress(traceDeliveryAddress);
							if (traceDeliveryAddress.getReceiver() != null && !traceDeliveryAddress.getReceiver().equals("")) {
								str.append(traceDeliveryAddress.getReceiver() + " ");
							}
							if (traceDeliveryAddress.getPhoneNum() != null && !traceDeliveryAddress.getPhoneNum().equals("")) {
								str.append(traceDeliveryAddress.getPhoneNum() + " ");
							}
							if (traceDeliveryAddress.getProvince() != null && !traceDeliveryAddress.getProvince().equals("")) {
								str.append(traceDeliveryAddress.getProvince());
							}
							if (traceDeliveryAddress.getCity() != null && !traceDeliveryAddress.getCity().equals("")) {
								str.append(traceDeliveryAddress.getCity());
							}
							if (traceDeliveryAddress.getArea() != null && !traceDeliveryAddress.getArea().equals("")) {
								str.append(traceDeliveryAddress.getArea());
							}
							if (traceDeliveryAddress.getStreet() != null && !traceDeliveryAddress.getStreet().equals("")) {
								str.append(traceDeliveryAddress.getStreet());
							}
							if (traceDeliveryAddress.getDetailAddress() != null && !traceDeliveryAddress.getDetailAddress().equals("")) {
								str.append(traceDeliveryAddress.getDetailAddress());
							}
							traceLableApply.setReceivingAddress(str.toString());
						}
					}
				}
				if (StringUtils.isNotBlank(traceLableApply.getLabelTemplateId())) {
					traceLableApply.setLableTemplate(traceLableTemplateDao.get(traceLableApply.getLabelTemplateId()));
				} else {
					traceLableApply.setLableTemplate(new TraceLableTemplate());
				}
				if (StringUtils.isNotBlank(traceLableApply.getTraceProductId())) {
					TraceProduct traceProduct = traceProductDao.get(traceLableApply.getTraceProductId());
					traceLableApply.setTraceProduct(traceProduct);
					traceLableApply.setProductName(traceProduct.getProductName());
				}

				// 获取到公司名称
				if (StringUtils.isNotBlank(traceLableApply.getOfficeId())) {
					Office office = officeService.get(traceLableApply.getOfficeId());
					if (office != null) {
						traceLableApply.setCorpName(office.getName());
					}

				}

				if (StringUtils.isNotBlank(traceLableApply.getBatchId())) {
					traceLableApply.setProductBatch(productBatchDao.get(traceLableApply.getBatchId()));
				}
				List<TraceCode> codelist = new ArrayList<TraceCode>();
				codelist = traceCodeDao.findByApplyId(traceLableApply.getId());
				if (codelist.size() > 0) {
					traceLableApply.setSerialCode(
							codelist.get(0).getTraceCode() + "--" + codelist.get(codelist.size() - 1).getTraceCode());
					// 判断溯源码list是否为空
					if (null != codelist && 0 != codelist.size()) {
						// 判断溯源码流水号是否为0，是则不显示流水号只显示批次号，否则显示流水号不显示批次号
						if (0 == codelist.get(0).getSerialNumber()) {
							// traceLableApply.setBatchNumber(productBatchDao.get(codelist.get(0).getBatchId()).getBatchCode());
						} else {
							traceLableApply.setSerialNumber(codelist.get(0).getSerialNumber() + "--"
									+ codelist.get(codelist.size() - 1).getSerialNumber());
						}
//						if (null != codelist.get(0).getBatchId() && !"".equals(codelist.get(0).getBatchId())) {
//							ProductBatch productBatch = new ProductBatch();
//							productBatch = productBatchDao.get(codelist.get(0).getBatchId());
//							if (productBatch != null) {
//								if (null != productBatch.getBatchCode() && !"".equals(productBatch.getBatchCode())) {
//									traceLableApply.setBatchNumber(productBatch.getBatchCode());
//								}
//							}
//
//						}

					}
				}

			}
		}
		return lableApplyPage;
	}

	/**
	 * 标签管理查询加生产负责人只看自己创建的标签
	 * 
	 * @param page
	 * @param batchName
	 * @param productName
	 * @param time
	 * @param cancelFlag
	 * @param printFlag
	 * @param auditFlag
	 * @return
	 */
	public Page<TraceLableApply> find(Page<TraceLableApply> page, String batchName, String productName, String time,
			String cancelFlag, String printFlag, String auditFlag, String officeId, List<String> findChildrenOffice,
			String admin, User user) {
		Page<TraceLableApply> lableApplyPage = traceProductDao.findBySql(page, getSqlone(batchName, productName, time,
				cancelFlag, printFlag, auditFlag, officeId, findChildrenOffice, admin, user), TraceLableApply.class);
		List<TraceLableApply> labelApplyList = lableApplyPage.getList();
		if (null != labelApplyList && 0 != labelApplyList.size()) {
			for (Iterator<TraceLableApply> iterator = labelApplyList.iterator(); iterator.hasNext();) {
				TraceLableApply traceLableApply = (TraceLableApply) iterator.next();
				if (StringUtils.isNotBlank(traceLableApply.getAddressId())) {

					TraceDeliveryAddress traceDeliveryAddress = new TraceDeliveryAddress();
					traceDeliveryAddress = traceDeliveryAddressDao.get(traceLableApply.getAddressId());

					if (traceDeliveryAddress != null) {
						StringBuffer str = new StringBuffer();
						traceLableApply.setDeliveryAddress(traceDeliveryAddress);
						if (traceDeliveryAddress.getReceiver() != null
								&& !traceDeliveryAddress.getReceiver().equals("")) {
							str.append(traceDeliveryAddress.getReceiver() + '\u0009');
						}
						if (traceDeliveryAddress.getPhoneNum() != null
								&& !traceDeliveryAddress.getPhoneNum().equals("")) {
							str.append(traceDeliveryAddress.getPhoneNum() + '\u0009');
						}
						if (traceDeliveryAddress.getProvince() != null
								&& !traceDeliveryAddress.getProvince().equals("")) {
							str.append(traceDeliveryAddress.getProvince());
						}
						if (traceDeliveryAddress.getCity() != null && !traceDeliveryAddress.getCity().equals("")) {
							str.append(traceDeliveryAddress.getCity());
						}
						if (traceDeliveryAddress.getArea() != null && !traceDeliveryAddress.getArea().equals("")) {
							str.append(traceDeliveryAddress.getArea());
						}
						if (traceDeliveryAddress.getStreet() != null && !traceDeliveryAddress.getStreet().equals("")) {
							str.append(traceDeliveryAddress.getStreet());
						}
						if (traceDeliveryAddress.getDetailAddress() != null
								&& !traceDeliveryAddress.getDetailAddress().equals("")) {
							str.append(traceDeliveryAddress.getDetailAddress());
						}
						traceLableApply.setReceivingAddress(str.toString());
					}

				}
				if (StringUtils.isNotBlank(traceLableApply.getLabelTemplateId())) {
					traceLableApply.setLableTemplate(traceLableTemplateDao.get(traceLableApply.getLabelTemplateId()));
				} else {
					traceLableApply.setLableTemplate(new TraceLableTemplate());
				}
				if (StringUtils.isNotBlank(traceLableApply.getTraceProductId())) {
					TraceProduct traceProduct = traceProductDao.get(traceLableApply.getTraceProductId());
					traceLableApply.setTraceProduct(traceProduct);
					traceLableApply.setProductName(traceProduct.getProductName());
				}

				// 获取到公司名称
				if (StringUtils.isNotBlank(traceLableApply.getOfficeId())) {
					Office office = officeService.get(traceLableApply.getOfficeId());
					if (office != null) {
						traceLableApply.setCorpName(office.getName());
					}

				}

				if (StringUtils.isNotBlank(traceLableApply.getBatchId())) {
					traceLableApply.setProductBatch(productBatchDao.get(traceLableApply.getBatchId()));
				}
				List<TraceCode> codelist = new ArrayList<TraceCode>();
				codelist = traceCodeDao.findByApplyId(traceLableApply.getId());
				if (codelist.size() > 0) {
					traceLableApply.setSerialCode(
							codelist.get(0).getTraceCode() + "--" + codelist.get(codelist.size() - 1).getTraceCode());
					// 判断溯源码list是否为空
					if (null != codelist && 0 != codelist.size()) {
						// 判断溯源码流水号是否为0，是则不显示流水号只显示批次号，否则显示流水号不显示批次号
						if (0 == codelist.get(0).getSerialNumber()) {
							// traceLableApply.setBatchNumber(productBatchDao.get(codelist.get(0).getBatchId()).getBatchCode());
						} else {
							traceLableApply.setSerialNumber(codelist.get(0).getSerialNumber() + "--"
									+ codelist.get(codelist.size() - 1).getSerialNumber());
						}
						if (null != codelist.get(0).getBatchId() && !"".equals(codelist.get(0).getBatchId())) {
							ProductBatch productBatch = new ProductBatch();
							productBatch = productBatchDao.get(codelist.get(0).getBatchId());
							if (productBatch != null) {
								if (null != productBatch.getBatchCode() && !"".equals(productBatch.getBatchCode())) {
									traceLableApply.setBatchNumber(productBatch.getBatchCode());
								}
							}

						}

					}
				}

			}
		}
		return lableApplyPage;
	}

	/**
	 * 标签管理查询导出数据，不分页
	 * 
	 * @param page
	 * @param batchName
	 * @param productName
	 * @param time
	 * @param cancelFlag
	 * @param printFlag
	 * @param auditFlag
	 * @return
	 */
	public List<TraceLableApply> listExport(String batchName, String productName, String time, String cancelFlag,
			String printFlag, String auditFlag, String officeId, List<String> findChildrenOffice, String admin,
			User user) {
		List<TraceLableApply> labelApplyList = traceProductDao.findBySql(getSqlone(batchName, productName, time,
				cancelFlag, printFlag, auditFlag, officeId, findChildrenOffice, admin, user), new Parameter(),
				TraceLableApply.class);
		if (null != labelApplyList && 0 != labelApplyList.size()) {
			for (Iterator<TraceLableApply> iterator = labelApplyList.iterator(); iterator.hasNext();) {
				TraceLableApply traceLableApply = (TraceLableApply) iterator.next();
				if (StringUtils.isNotBlank(traceLableApply.getAddressId())) {

					TraceDeliveryAddress traceDeliveryAddress = new TraceDeliveryAddress();
					traceDeliveryAddress = traceDeliveryAddressDao.get(traceLableApply.getAddressId());

					if (traceDeliveryAddress != null) {
						StringBuffer str = new StringBuffer();
						traceLableApply.setDeliveryAddress(traceDeliveryAddress);
						if (traceDeliveryAddress.getReceiver() != null
								&& !traceDeliveryAddress.getReceiver().equals("")) {
							str.append(traceDeliveryAddress.getReceiver() + '\u0009');
						}
						if (traceDeliveryAddress.getPhoneNum() != null
								&& !traceDeliveryAddress.getPhoneNum().equals("")) {
							str.append(traceDeliveryAddress.getPhoneNum() + '\u0009');
						}
						if (traceDeliveryAddress.getProvince() != null
								&& !traceDeliveryAddress.getProvince().equals("")) {
							str.append(traceDeliveryAddress.getProvince());
						}
						if (traceDeliveryAddress.getCity() != null && !traceDeliveryAddress.getCity().equals("")) {
							str.append(traceDeliveryAddress.getCity());
						}
						if (traceDeliveryAddress.getArea() != null && !traceDeliveryAddress.getArea().equals("")) {
							str.append(traceDeliveryAddress.getArea());
						}
						if (traceDeliveryAddress.getStreet() != null && !traceDeliveryAddress.getStreet().equals("")) {
							str.append(traceDeliveryAddress.getStreet());
						}
						if (traceDeliveryAddress.getDetailAddress() != null
								&& !traceDeliveryAddress.getDetailAddress().equals("")) {
							str.append(traceDeliveryAddress.getDetailAddress());
						}
						traceLableApply.setReceivingAddress(str.toString());
					}

				}
				if (StringUtils.isNotBlank(traceLableApply.getLabelTemplateId())) {
					traceLableApply.setLableTemplate(traceLableTemplateDao.get(traceLableApply.getLabelTemplateId()));
				} else {
					traceLableApply.setLableTemplate(new TraceLableTemplate());
				}
				if (StringUtils.isNotBlank(traceLableApply.getTraceProductId())) {
					TraceProduct traceProduct = traceProductDao.get(traceLableApply.getTraceProductId());
					traceLableApply.setTraceProduct(traceProduct);
					traceLableApply.setProductName(traceProduct.getProductName());
				}
				// 获取到公司名称
				if (StringUtils.isNotBlank(traceLableApply.getOfficeId())) {
					Office office2 = officeService.get(traceLableApply.getOfficeId());
					if (office2 != null) {
						traceLableApply.setCorpName(office2.getName());
					}
				}
				if (StringUtils.isNotBlank(traceLableApply.getBatchId())) {
					traceLableApply.setProductBatch(productBatchDao.get(traceLableApply.getBatchId()));
				}
				List<TraceCode> codelist = new ArrayList<TraceCode>();
				codelist = traceCodeDao.findByApplyId(traceLableApply.getId());
				if (codelist.size() > 0) {
					traceLableApply.setSerialCode(
							codelist.get(0).getTraceCode() + "--" + codelist.get(codelist.size() - 1).getTraceCode());
					// 判断溯源码list是否为空
					if (null != codelist && 0 != codelist.size()) {
						// 判断溯源码流水号是否为0，是则不显示流水号只显示批次号，否则显示流水号不显示批次号
						if (0 == codelist.get(0).getSerialNumber()) {
							// traceLableApply.setBatchNumber(productBatchDao.get(codelist.get(0).getBatchId()).getBatchCode());
						} else {
							traceLableApply.setSerialNumber(codelist.get(0).getSerialNumber() + "--"
									+ codelist.get(codelist.size() - 1).getSerialNumber());
						}
						if (null != codelist.get(0).getBatchId() && !"".equals(codelist.get(0).getBatchId())) {
							ProductBatch productBatch = new ProductBatch();
							productBatch = productBatchDao.get(codelist.get(0).getBatchId());
							if (productBatch != null) {
								if (null != productBatch.getBatchCode() && !"".equals(productBatch.getBatchCode())) {
									traceLableApply.setBatchNumber(productBatch.getBatchCode());
								}
							}

						}

					}
				}

			}
		}
		return labelApplyList;
	}

	/**
	 * 获取N天之前日期
	 * 
	 * @param time
	 * @return
	 */
	private Date getDateBefore(String time) {
		Date date = new Date(0);
		Calendar calendar1 = Calendar.getInstance();
		if (time.equals("1")) {
			calendar1.add(Calendar.DATE, -3);
			date = calendar1.getTime();
		} else if (time.equals("2")) {
			calendar1.add(Calendar.DATE, -7);
			date = calendar1.getTime();
		} else if (time.equals("3")) {
			calendar1.add(Calendar.MONTH, -1);
			date = calendar1.getTime();
		} else if (time.equals("4")) {
			calendar1.add(Calendar.MONTH, -3);
			date = calendar1.getTime();
		} else if (time.equals("5")) {
			calendar1.add(Calendar.MONTH, -6);
			date = calendar1.getTime();
		} else if (time.equals("6")) {
			calendar1.add(Calendar.YEAR, -1);
			date = calendar1.getTime();
		}
		return date;
	}

	public TraceLableApply get(TraceLableApply apply) {
		DetachedCriteria dc = traceLableApplyDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if (apply != null && apply.getId() != null) {
			dc.add(Restrictions.eq("id", apply.getId()));
		}
		if (apply != null && apply.getCreatUserid() != null) {
			dc.add(Restrictions.eq("creatUserid", apply.getCreatUserid()));
		}
		List<TraceLableApply> list = traceLableApplyDao.find(dc);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Transactional(readOnly = false)
	public void save(TraceLableApply apply, String officeId, User user) throws Exception {
		if (StringUtils.isNotBlank(apply.getId())) {
			apply.setStates("U");
		}
		String parentIds = user.getCompany().getParentIds();
		String flag = "0";
		if(user.isAdmin()){
			flag = "1";
		}else{
			if(parentIds.equals("0,1,")){
				flag = "1";
			}
		}
		if("0".equals(flag)){
			apply.setAuditFlag("0");
		}else{
			apply.setAuditFlag("1");
		}
		apply.setOfficeId(officeId);
		if(StringUtils.isNotBlank(apply.getAddressId()) && "1".equals(apply.getAddressId())){
			apply.setAddress(user.getName()+" "+user.getPhone()==null?"":user.getPhone()+"自提");
		}
		traceLableApplyDao.save(apply);
		
		String dateTime = DateUtils.getDateTime();
		TraceProductAuditNew pojo = new TraceProductAuditNew(); 
		// 审核ID
		pojo.setAuditId(apply.getId());
		if("0".equals(flag)){
			// 审核状态：1-审核通过，2-审核拒接
			pojo.setAuditState("3");
			// 原因
			pojo.setReason("标签申请提交");
		}else{
			// 审核状态：1-审核通过，2-审核拒接
			pojo.setAuditState("1");
			// 原因
			pojo.setReason("上级用户申请免审核");
		}
		// 审核人ID
		pojo.setAuditUserId(user.getId());
		// 审核时间
		pojo.setAuditTime(dateTime);
		traceProductAuditNewDao.save(pojo);
		
		// 修改标签内容:(先删除原有内容，重新添加新的内容）
		if (apply.getContentList() != null && apply.getContentList().size() > 0) {
			if (apply.getId() != null) {
				traceLableContentDao.deleteByApplyId(apply.getId());
			}
			for (int i = 0; i < apply.getContentList().size(); i++) {
				TraceLableContent content = apply.getContentList().get(i);
				// 元素id置空，保证即使是修改内容，也会重新添加
				content.setId(null);
				content.setApplyId(apply.getId());
				traceLableContentDao.save(content);
			}
		}

		// 添加明细
		if (apply != null && apply.getApplyNum() != null) {
			int num = Integer.valueOf(apply.getApplyNum());
			ProductBatch productBatch = new ProductBatch();
			String batchCode = "";
			if (StringUtils.isNotBlank(apply.getBatchId())) {
				productBatch = productBatchDao.get(apply.getBatchId());
				batchCode = productBatch.getBatchCode();
			}
			TraceProduct traceProduct = traceProductDao.get(apply.getTraceProductId());
			Office office = officeDao.get(traceProduct.getOfficeId());
			if(StringUtils.isNotBlank(office.getOfficeCode())){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				String sjbh = sdf.format(new Date()).replaceAll("[[\\s-:punct:]]","");
				String ycount = traceWcCodeService.getMaxSerialNumber(office.getOfficeCode(), office.getOfficeCode(), sjbh, "1", "1");
				Double f = Double.valueOf(ycount);
				int aa = (int)Math.ceil(f);
				for(int i = 0; i < num; i++) {
					String xlh = String.valueOf(i+aa+1);
					int  k = xlh.length();
					if(k <= 8) {
						for(int m = 0; m < 8-k;m++) {
							xlh = "0" + xlh;
						}
					}
					String code =TracingSourceCode.GeneratingTraceableCode(office.getOfficeCode(), office.getOfficeCode(), sjbh, "1", "1", xlh);
					TraceCode traceCode = new TraceCode();
					traceCode.setTraceCode(code);
					traceCode.setSerialNumber(Integer.parseInt(code.substring(14, 24)));
					traceCode.setApplyId(apply.getId());
					traceCode.setOfficeId(officeId);
					traceCode.setBatchId(apply.getBatchId() == null ? "" : apply.getBatchId());
					traceCode.setCreatUserid(apply.getCreatUserid());
					traceCode.setSysId(apply.getSysId());
					traceCode.setPackType("1");
					traceCode.setPackTypeName("");
					traceCode.setTraceType("1");
					traceCodeService.save(traceCode);
				}
				traceWcCodeService.save(office.getOfficeCode(), office.getOfficeCode(), sjbh, "1", "1", num);
			}else{
				String preTraceCode = traceCodeService.preTraceCode(office.getCode(), traceProduct.getProductCode(), batchCode);
				// 获取本产品最大流水号
				int serialNumber = 0;
				serialNumber = traceCodeService.getMaxExtSerialNumByProductId(apply.getTraceProductId());
				StringBuffer sbf = new StringBuffer();
				boolean first = true;
				for (int i = 1; i <= num; i++) {
					TraceCode traceCode = new TraceCode();
					String code = "";
					// 6位公司编码+4位产品流水+批次号后10位+8位16进制随机码
					String lsh = (serialNumber + i) + "";
					for (int j = 0; j < 12 - lsh.length(); j++) {
						code += "0";
					}
					code = preTraceCode + code + lsh;
					traceCode.setApplyId(apply.getId());
					traceCode.setOfficeId(officeId);
					traceCode.setBatchId(apply.getBatchId() == null ? "" : apply.getBatchId());
					traceCode.setCreatUserid(apply.getCreatUserid());
					StringBuffer sql = new StringBuffer();
					if (first) {
						sql.append(
								"INSERT INTO `t_trace_code_info`(id,trace_code,serial_number,apply_id,sys_id,office_id,batch_id,trace_type,trace_count,pack_type,anti_fake_code,STATUS,print_date,activation_date,activator,creat_time,creat_userid,update_time,update_userid,states,competitive) VALUES ('")
								.append(IdGen.uuid()).append("', '").append(code).append("', '").append(serialNumber + i)
								.append("' , '").append(traceCode.getApplyId()).append("', null, '")
								.append(traceCode.getOfficeId()).append("', '").append(traceCode.getBatchId())
								.append("', null, '0', null, null, null, null, null, null, '")
								.append(DateUtils.formatDate(traceCode.getCreatTime(), "yyyy-MM-dd HH:mm:ss"))
								.append("', '").append(traceCode.getCreatUserid()).append("', null, null, 'A', '0')");
						first = false;
					} else {
						sql.append(",('").append(IdGen.uuid()).append("', '").append(code).append("', '")
								.append(serialNumber + i).append("' , '").append(traceCode.getApplyId())
								.append("', null, '").append(traceCode.getOfficeId()).append("', '")
								.append(traceCode.getBatchId())
								.append("', null, '0', null, null, null, null, null, null, '")
								.append(DateUtils.formatDate(traceCode.getCreatTime(), "yyyy-MM-dd HH:mm:ss"))
								.append("', '").append(traceCode.getCreatUserid()).append("', null, null, 'A', '0')");
					}
					sbf.append(sql);
					if (i % 250 == 0) {
						traceCodeDao.getSession().createSQLQuery(sbf.toString()).executeUpdate();
						first = true;
						sbf.setLength(0);
					}
				}
				if (StringUtils.isNotBlank(sbf)) {
					traceCodeDao.getSession().createSQLQuery(sbf.toString()).executeUpdate();
				}
			}
		}
	}

	@Transactional(readOnly = false)
	public void save(TraceLableApply apply) throws Exception {
		if (StringUtils.isNotBlank(apply.getId())) {
			apply.setStates("U");
		}
		traceLableApplyDao.save(apply);
		// 修改标签内容:(先删除原有内容，重新添加新的内容）
		if (apply.getContentList() != null && apply.getContentList().size() > 0) {
			if (apply.getId() != null) {
				traceLableContentDao.deleteByApplyId(apply.getId());
			}
			for (int i = 0; i < apply.getContentList().size(); i++) {
				TraceLableContent content = apply.getContentList().get(i);
				// 元素id置空，保证即使是修改内容，也会重新添加
				content.setId(null);
				content.setApplyId(apply.getId());
				traceLableContentDao.save(content);
			}
		}
	}

	/**
	 * 获取批次的所有标签申请列表
	 * 
	 * @param batchId
	 * @return
	 */
	public List<TraceLableApply> getLabelApplyListByBatchId(String batchId) {
		DetachedCriteria dc = traceLableApplyDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if (StringUtils.isNotBlank(batchId)) {
			dc.add(Restrictions.eq("batchId", batchId));
		}
		// if(StringUtils.isNotBlank(printFlag)) {
		// dc.add(Restrictions.eq("printFlag", printFlag));
		// }
		List<TraceLableApply> list = traceLableApplyDao.find(dc);
		return list;
	}

	/**
	 * 逻辑删除标签申请数据 已打印的标签申请不能删除 检查是否为已打印的标签数据 如果存在 则返回1 否则为正常删除
	 * 
	 * @param batchId
	 */
	@Transactional(readOnly = false)
	public String deleteLabelApplyById(String labelApplyId, User user) {
		TraceLableApply labelApply = traceLableApplyDao.get(labelApplyId);
		if (null != labelApply && "1".equals(labelApply.getPrintFlag())) {
			return "1";
		}
		// 删除标签申请数据
		labelApply.setUpdateTime(new Date());
		if (null != user) {
			labelApply.setUpdateUserid(user.getId());
		}
		labelApply.setStates(TraceLableApply.STATE_FLAG_DEL);
		traceLableApplyDao.save(labelApply);
		return "0";
	}

	/**
	 * 打印标签 审核未通过不能打印 检查标签是否通过审核 是则返回1 否则为打印
	 * 
	 * @param batchId
	 */
	@Transactional(readOnly = false)
	public String printLabelApply(String labelApplyId, User user) {

		TraceLableApply labelApply = traceLableApplyDao.get(labelApplyId);
		/*
		 * if(null != labelApply && !"1".equals(labelApply.getAuditFlag())){ return "1";
		 * }
		 */
		// 打印标签
		labelApply.setUpdateTime(new Date());
		if (null != user) {
			labelApply.setUpdateUserid(user.getId());
		}
		labelApply.setPrintFlag(TraceLableApply.DEL_FLAG_DELETE);
		traceLableApplyDao.save(labelApply);
		return "0";
	}

	/**
	 * 作废标签 已打印的标签不能作废 是则返回1 否则为作废
	 * 
	 * @param batchId
	 */
	@Transactional(readOnly = false)
	public String cancelLabelApply(String labelApplyId, User user) {

		TraceLableApply labelApply = traceLableApplyDao.get(labelApplyId);
		if (null != labelApply && "1".equals(labelApply.getPrintFlag())) {
			return "1";
		}

		labelApply.setUpdateTime(new Date());
		if (null != user) {
			labelApply.setUpdateUserid(user.getId());
		}
		labelApply.setCancelFlag(TraceLableApply.DEL_FLAG_DELETE);
		traceLableApplyDao.save(labelApply);
		return "0";
	}

	/**
	 * 恢复
	 * 
	 * @param batchId
	 */
	@Transactional(readOnly = false)
	public String recoveryLabelApply(String labelApplyId, User user) {
		TraceLableApply labelApply = traceLableApplyDao.get(labelApplyId);
		labelApply.setUpdateTime(new Date());
		if (null != user) {
			labelApply.setUpdateUserid(user.getId());
		}
		labelApply.setCancelFlag(TraceLableApply.DEL_FLAG_NORMAL);
		traceLableApplyDao.save(labelApply);
		return "0";
	}

	/**
	 * 标签审核 是则返回1 否则为作废
	 * 
	 * @param batchId
	 */
	@Transactional(readOnly = false)
	public String sumbitData(String labelApplyId, User user) {
		TraceLableApply labelApply = traceLableApplyDao.get(labelApplyId);
		labelApply.setUpdateTime(new Date());
		if (null != user) {
			labelApply.setUpdateUserid(user.getId());
		}
		labelApply.setAuditFlag("0");
		traceLableApplyDao.save(labelApply);

		String dateTime = DateUtils.getDateTime();
		TraceProductAuditNew pojo = new TraceProductAuditNew();
		// 审核ID
		pojo.setAuditId(labelApplyId);
		// 审核状态：1-审核通过，2-审核拒接,3-提交
		pojo.setAuditState("3");
		// 原因
		pojo.setReason("标签申请提交");
		// 审核人ID
		pojo.setAuditUserId(user.getId());
		// 审核时间
		pojo.setAuditTime(dateTime);
		traceProductAuditNewDao.save(pojo);
		
		return "0";
	}
	
	/**
	 * 标签审核 是则返回1 否则为作废
	 * 
	 * @param batchId
	 */
	@Transactional(readOnly = false)
	public String auditLabelApply(String labelApplyId, String auditFlag, User user) {
		TraceLableApply labelApply = new TraceLableApply();
		if (StringUtils.isNotBlank(labelApplyId)) {
			labelApply = traceLableApplyDao.get(labelApplyId);
		}

		labelApply.setUpdateTime(new Date());
		if (null != user) {
			labelApply.setUpdateUserid(user.getId());
		}

		if ("1".equals(auditFlag)) {// 通过
			labelApply.setAuditFlag(TraceLableApply.DEL_FLAG_DELETE);
		} else if ("2".equals(auditFlag)) {// 拒绝
			labelApply.setAuditFlag(TraceLableApply.DEL_FLAG_AUDIT);
		} else if ("3".equals(auditFlag)) {// 打回
			labelApply.setAuditFlag("3");
		}
		traceLableApplyDao.save(labelApply);
		return "0";
	}

	/**
	 * 获取该申请的最大和最小标签号
	 * 
	 * @param page
	 * @param productName
	 * @param officeId
	 * @return
	 */
	public String getbqd(String applyId) {
		return traceLableApplyDao.getbqd(applyId);
	}

	/**
	 * 获取企业标签数
	 * 
	 * @param officeId
	 * @return
	 */
	public List<Map<String, Object>> getCompanyLabelCount(String officeId) {
		String sql = "";
		if(StringUtils.isNotBlank(officeId) && !"1".equals(officeId)) {
			sql+= "SELECT a.id,a.NAME FROM  sys_office a WHERE a.DEL_FLAG = '0' AND (a.id='" + officeId + "' or a.PARENT_IDS LIKE '%" + officeId + ",%') ORDER BY a.name";
		}
		if(StringUtils.isNotBlank(officeId) && "1".equals(officeId)) {
			sql+= "SELECT a.id,a.NAME FROM  sys_office a WHERE a.DEL_FLAG = '0' AND a.PARENT_IDS LIKE '%" + officeId + ",%' ORDER BY a.name";
		}
		
		List<Object> list = traceLableApplyDao.findBySql(sql);
		List<Map<String, Object>> mlist = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			Object[] obj = (Object[]) list.get(i);
			String id = (String) obj[0];
			String name = (String) obj[1];
			map.put("companyName", name);
			List<Object> objList = traceLableApplyDao.findBySql(
					"SELECT COUNT(b.trace_code) FROM t_trace_lable_apply a,t_trace_code_info b WHERE a.id = b.apply_id AND a.states <> 'D' AND b.states <> 'D' AND a.office_id='"
							+ id + "'");
			map.put("labelCount", objList.get(0));
			mlist.add(map);
		}
		return mlist;
	}

	/**
	 * 标签管理查询
	 * 
	 * @param page
	 * @param batchName
	 * @param productName
	 * @param time
	 * @param cancelFlag
	 * @param printFlag
	 * @param auditFlag
	 * @return
	 */
	public String findBySql(String officeId) {
		long num = 0;
		List<TraceLableApply> labelApplyList = new ArrayList<TraceLableApply>();
		if (officeId == null || officeId.equals("")) {
			labelApplyList = traceProductDao.findBySql(getSql2(), new Parameter(1), TraceLableApply.class);
		} else {
			labelApplyList = traceProductDao.findBySql(getSql(officeId), new Parameter(), TraceLableApply.class);
		}

		if (null != labelApplyList && 0 != labelApplyList.size()) {
			for (Iterator<TraceLableApply> iterator = labelApplyList.iterator(); iterator.hasNext();) {
				TraceLableApply traceLableApply = (TraceLableApply) iterator.next();
				num += Long.parseLong(traceLableApply.getApplyNum());
				if (StringUtils.isNotBlank(traceLableApply.getLabelTemplateId())) {
					System.out.println(traceLableApply.getLabelTemplateId());
					//traceLableApply.setLableTemplate(traceLableTemplateDao.get(traceLableApply.getLabelTemplateId()));
				} else {
					traceLableApply.setLableTemplate(new TraceLableTemplate());
				}
			}
		}
		return String.valueOf(num);
	}

	public String getSql(String officeId) {
		StringBuffer sql = new StringBuffer(500);
		sql.append(
				"SELECT a.* FROM (SELECT apply.* FROM t_trace_lable_apply apply left join t_trace_product_batch batch "
						+ "on apply.batch_id = batch.id WHERE apply.states<>'D' and batch.`states`<>'D'");
		sql.append(" ) a left join t_trace_product product on a.trace_product_id = product.id WHERE 1=1 ");
		if (StringUtils.isNotBlank(officeId) && !"1".equals(officeId)) {
			sql.append(" and product.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId
					+ "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')");
		}
		if (StringUtils.isNotBlank(officeId) && "1".equals(officeId)) {
			sql.append(" and product.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.PARENT_IDS LIKE '%" + officeId + "%')");
		}
		sql.append(" order by a.creat_time desc ");
		String str = new String(sql);
		return str;
	}

	public String getSql2() {
		StringBuffer sql = new StringBuffer(500);
		sql.append(
				"SELECT a.* FROM (SELECT apply.* FROM t_trace_lable_apply apply left join t_trace_product_batch batch "
						+ "on apply.batch_id = batch.id WHERE apply.states<>'d'");
		sql.append(" ) a left join t_trace_product product on a.trace_product_id = product.id WHERE 1=:p1 ");
		sql.append(" order by a.creat_time desc ");
		String str = new String(sql);
		return str;
	}
	
	/**
	 * Title: savaAudit Description:保存审核记录
	 * 
	 * @param user
	 *            用户信息
	 * @param remarks
	 *            原因
	 * @param auditFlag
	 *            状态
	 * @param batchId
	 *            审核ID
	 * 
	 * @return
	 */
	@Transactional(readOnly = false)
	public ResultBean<String> savaAuditInfo(User user, SavaAuditReq req) {
		TraceLableApply label = traceLableApplyDao.get(req.getApplyId());
		String auditFlag = req.getAuditFlag();
		if(null == label) {
			return ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage());
		}
		String dateTime = DateUtils.getDateTime();
		TraceProductAuditNew pojo = new TraceProductAuditNew();
		// 审核ID
		pojo.setAuditId(req.getApplyId());
		// 审核状态：1-审核通过，2-审核拒绝,3-审核驳回
		if("1".equals(auditFlag)){
			if(user.getRoleIdList().contains("1")){
				auditFlag = "1";
			}else{
				auditFlag = "0";
			}
		}
		pojo.setAuditState(req.getAuditFlag());
		// 原因
		pojo.setReason(req.getRemarks());
		// 审核人ID
		pojo.setAuditUserId(user.getId());
		// 审核时间
		pojo.setAuditTime(dateTime);
		// 审核人企业ID
		pojo.setOfficeId(user.getOffice().getId());
		traceProductAuditNewDao.save(pojo);
		
		label.setAuditFlag(auditFlag);
		traceLableApplyDao.save(label);
		
		return ResultUtil.success("Success");
	}
	
	/**
	 * 获取审核标识
	 * 
	 * @param batchId
	 * @return
	 */
	public String getAuditFlagByBatchId(String batchId) {
		String sql = "SELECT a.* FROM t_trace_product_audit_new a, t_trace_lable_apply b WHERE a.audit_id = b.id AND b.states<>'D' AND b.batch_id = :p1 AND a.audit_state = '1'";
		List<TraceProductAuditNew> list = traceProductAuditNewDao.findBySql(sql, new Parameter(batchId), TraceProductAuditNew.class);
		if(list!=null && list.size()>0){
			return "1";
		}else{
			return "0";
		}
	}
	
	/**
	 * 获取审核标识
	 * 
	 * @param officeId
	 * @return
	 */
	public String getAuditFlagByProductId(String productId) {
		String sql = "SELECT a.* FROM t_trace_product_audit_new a, t_trace_lable_apply b WHERE a.audit_id = b.id AND b.states<>'D' AND b.trace_product_id = :p1 AND a.audit_state = '1'";
		List<TraceProductAuditNew> list = traceProductAuditNewDao.findBySql(sql, new Parameter(productId), TraceProductAuditNew.class);
		if(list!=null && list.size()>0){
			return "1";
		}else{
			return "0";
		}
	}

}
