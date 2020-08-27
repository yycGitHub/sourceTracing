package com.surekam.modules.traceproductauditnew.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.config.Global;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.Client;
import com.surekam.common.utils.DateUtils;
import com.surekam.common.utils.ResultBean;
import com.surekam.common.utils.ResultEnum;
import com.surekam.common.utils.ResultUtil;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.productbatch.dao.ProductBatchDao;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.sys.dao.OfficeDao;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.trace.TraceLableApply.dao.TraceLableApplyDao;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.tracecode.dao.TraceCodeDao;
import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.tracemodel.dao.TraceModelDatalDao;
import com.surekam.modules.tracemodel.entity.TraceModelData;
import com.surekam.modules.traceproductaudit.dao.TraceProductAuditDao;
import com.surekam.modules.traceproductaudit.entity.SavaAuditReq;
import com.surekam.modules.traceproductaudit.entity.TraceProductAudit;
import com.surekam.modules.traceproductauditnew.dao.TraceProductAuditNewDao;
import com.surekam.modules.traceproductauditnew.entity.TraceProductAuditNew;
import com.surekam.modules.traceproperty.dao.TracePropertyDataDao;
import com.surekam.modules.traceproperty.dao.TracePropertyNewDao;
import com.surekam.modules.traceproperty.entity.TracePropertyData;
import com.surekam.modules.traceproperty.entity.TracePropertyNew;

import net.sf.json.JSONObject;

/**
 * 产品审核Service
 * 
 * @author liwei
 * @version 2020-04-10
 */
@Component
@Transactional(readOnly = true)
public class TraceProductAuditNewService extends BaseService {

	@Autowired
	private TraceProductAuditDao traceProductAuditDao;
	
	@Autowired
	private TraceProductAuditNewDao traceProductAuditNewDao;

	@Autowired
	private ProductBatchDao productBatchDao;
	
	@Autowired
	private UserDao userDao;

	public TraceProductAuditNew get(String id) {
		return traceProductAuditNewDao.get(id);
	}

	@Transactional(readOnly = false)
	public void save(TraceProductAuditNew traceProductAuditNew) {
		traceProductAuditNewDao.save(traceProductAuditNew);
	}

	@Transactional(readOnly = false)
	public void delete(String id) {
		traceProductAuditNewDao.deleteById(id);
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
	public ResultBean<String> savaAudit(User user, SavaAuditReq req) {
		ProductBatch productBatch = productBatchDao.get(req.getBatchId());
		if(null == productBatch) {
			return ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage());
		}
		String dateTime = DateUtils.getDateTime();
		TraceProductAudit pojo = new TraceProductAudit();
		// 审核ID
		pojo.setAuditId(req.getBatchId());
		// 审核状态：1-审核通过，2-审核拒接
		pojo.setAuditState(req.getAuditFlag());
		// 原因
		pojo.setReason(req.getRemarks());
		// 审核人ID
		pojo.setAuditUserId(user.getId());
		// 审核时间
		pojo.setAuditTime(dateTime);
		traceProductAuditDao.save(pojo);
		return ResultUtil.success("Success");
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
	public ResultBean<String> savaAuditNew(User user, SavaAuditReq req) {
		ProductBatch productBatch = productBatchDao.get(req.getBatchId());
		String auditFlag = req.getAuditFlag();
		if(null == productBatch) {
			return ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage());
		}
		String dateTime = DateUtils.getDateTime();
		TraceProductAuditNew pojo = new TraceProductAuditNew();
		// 审核ID
		pojo.setAuditId(req.getBatchId());
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
		
		productBatch.setAuditStatus(auditFlag);
		productBatchDao.save(productBatch);
		
		return ResultUtil.success("Success");
	}

	public List<TraceProductAuditNew> findByAuditId(String batchCode) {
		List<ProductBatch> findByProductId = productBatchDao.findByBatchCode(batchCode);
		List<TraceProductAuditNew> arrayList = new ArrayList<TraceProductAuditNew>();
		for (ProductBatch productBatch : findByProductId) {
			TraceProductAuditNew findByAuditId = traceProductAuditNewDao.findByAuditId(productBatch.getId());
			if (null != findByAuditId) {
				User user = userDao.get(findByAuditId.getAuditUserId());
				if (null != user && StringUtils.isNotBlank(user.getName())) {
					findByAuditId.setAuditUserName(user.getName());
				}
				arrayList.add(findByAuditId);
			}
		}
		return arrayList;
	}
}
