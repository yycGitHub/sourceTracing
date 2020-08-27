package com.surekam.modules.traceproductaudit.service;

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
 * @author 唐军
 * @version 2019-07-24
 */
@Component
@Transactional(readOnly = true)
public class TraceProductAuditService extends BaseService {

	@Autowired
	private TraceProductAuditDao traceProductAuditDao;
	
	@Autowired
	private TraceProductAuditNewDao traceProductAuditNewDao;

	@Autowired
	private ProductBatchDao productBatchDao;

	@Autowired
	private TraceModelDatalDao traceModelDatalDao;

	@Autowired
	private TracePropertyNewDao tracePropertyNewDao;

	@Autowired
	private TracePropertyDataDao tracePropertyDataDao;

	@Autowired
	private TraceLableApplyDao traceLableApplyDao;
	
	@Autowired
	private TraceCodeDao traceCodeDao;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private OfficeDao officeDao;

	public TraceProductAudit get(String id) {
		return traceProductAuditDao.get(id);
	}

	@Transactional(readOnly = false)
	public void save(TraceProductAudit traceProductAudit) {
		traceProductAuditDao.save(traceProductAudit);
	}

	@Transactional(readOnly = false)
	public void delete(String id) {
		traceProductAuditDao.deleteById(id);
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
		boolean type = false;
		if ("1".equals(productBatch.getSysId())) {
			Office office = officeDao.get(productBatch.getOfficeId());
			if (null == office || StringUtils.isBlank(office.getKuid())) {
				System.out.println("农事加工数据审核kId为空");
				return ResultUtil.error(ResultEnum.DATA_NOT_EXIST.getCode(), ResultEnum.DATA_NOT_EXIST.getMessage());
			}
			// 获取审核时间
			Map<String, String> map = new HashMap<String, String>();
			map.put("sybatchCode", productBatch.getBatchCode()); // ID
			map.put("officeId", office.getKuid()); // 公司ID
			map.put("auditTime", dateTime); // 审核时间
			map.put("auditOpinions", req.getRemarks()); // 原因
			map.put("loginName", user.getLoginName()); // 登录名
			map.put("userName", user.getName() == null ? "" : user.getName()); // 姓名
			String auditStatus = "";
			if ("1".equals(req.getAuditFlag())) {
				auditStatus = "3";
			} else {
				auditStatus = "2";
			}
			map.put("auditStatus", auditStatus); // 审核状态 1-未审 2-已审未通过 3-已审通过
			JSONObject json = new JSONObject().fromObject(map);
			String url = Global.getConfig("ns_url");
			System.out.println("农事加工数据审核请求参数=====" + json);
			String post = Client.post(url + "api/dataUploadManage/updateAuditStatus", json, "UTF-8");
			System.out.println("农事加工数据审核返回参数=====" + post);
			JSONObject result  = new JSONObject().fromObject(post);
			if (null != result && !"".equals(result) && "Success".equals(result.get("bodyData").toString())) {
				type = true;
			}
		}
		if("2".equals(productBatch.getSysId())) {
			String auditStatus = "";
			if ("1".equals(req.getAuditFlag())) {
				auditStatus = "4";
			} else {
				auditStatus = "3";
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("batchCode", productBatch.getBatchCode()); // ID
			map.put("officeId", productBatch.getOfficeId()); // 公司ID
			map.put("traceDataState", auditStatus); // 3:审核不通过、4：审核通过
			map.put("reasonsForFailure", req.getRemarks()); // 原因
			JSONObject json = new JSONObject().fromObject(map);
			String url = Global.getConfig("jg_url");
			System.out.println("加工平台数据审核请求参数=====" + json);
			String post = Client.post(url + "api/packingRecord/accessDataInterfaceAudit", json, "UTF-8");
			System.out.println("加工平台数据审核返回参数=====" + post);
			JSONObject result  = new JSONObject().fromObject(post);
			if (null != result && !"".equals(result) && "0".equals(result.get("code").toString())) {
				type = true;
			}
		}
		if (type == true) {
			TraceProductAudit pojo = new TraceProductAudit();
			// 审核ID
			pojo.setAuditId(req.getBatchId());
			// 审核状态：1-审核通过，2-审核拒接
			pojo.setAuditState(req.getAuditFlag());
			// 原因
			pojo.setReason(req.getRemarks());
			// 审核人
			pojo.setAuditUserId(user.getId());
			// 审核时间
			pojo.setAuditTime(dateTime);
			traceProductAuditDao.save(pojo);
			
			if("2".equals(req.getAuditFlag())) {
				productBatch.setStates(ProductBatch.STATE_FLAG_DEL);
				productBatchDao.save(productBatch);

				List<TraceLableApply> findBybatchId = traceLableApplyDao.findBybatchId(req.getBatchId());
				for (TraceLableApply traceLableApply : findBybatchId) {
					traceLableApply.setStates(TraceLableApply.STATE_FLAG_DEL);
					traceLableApplyDao.save(traceLableApply);
				}

				List<TraceCode> findByBatchId = traceCodeDao.findByBatchId(req.getBatchId());
				for (TraceCode traceCode : findByBatchId) {
					traceCode.setStates(TraceCode.STATE_FLAG_DEL);
					traceCodeDao.save(traceCode);
				}

				List<TraceModelData> findByBatchid = traceModelDatalDao.findByBatchid(req.getBatchId());
				for (TraceModelData traceModelData : findByBatchid) {

					List<TracePropertyData> findByModelDataId = tracePropertyDataDao.findByModelDataId(traceModelData.getId());
					for (TracePropertyData tracePropertyData : findByModelDataId) {

						TracePropertyNew tracePropertyNew = tracePropertyNewDao.get(tracePropertyData.getPropertyId());
						tracePropertyNew.setStates(TracePropertyNew.STATE_FLAG_DEL);
						tracePropertyNewDao.save(tracePropertyNew);

						tracePropertyData.setStates(TracePropertyData.STATE_FLAG_DEL);
						tracePropertyDataDao.save(tracePropertyData);
					}

					traceModelData.setStates(TraceModelData.STATE_FLAG_DEL);
					traceModelDatalDao.save(traceModelData);
				}
				return ResultUtil.success("Success");
			} 
			return ResultUtil.success("Success");
		}
		return ResultUtil.error(800001, "审核失败！");
	}

	public List<TraceProductAudit> findByAuditId(String batchCode) {
		List<ProductBatch> findByProductId = productBatchDao.findByBatchCode(batchCode);
		List<TraceProductAudit> arrayList = new ArrayList<TraceProductAudit>();
		for (ProductBatch productBatch : findByProductId) {
			TraceProductAudit findByAuditId = traceProductAuditDao.findByAuditId(productBatch.getId());
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
	
	public List<TraceProductAuditNew> findByAuditIdNew(String batchCode) {
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
	
	public List<TraceProductAuditNew> findByAuditIdInfo(String id) {
		List<TraceProductAuditNew> list = traceProductAuditNewDao.findByAuditIdInfo(id);
		for(int i=0;i<list.size();i++){
			TraceProductAuditNew traceProductAuditNew = list.get(i);
			User user = userDao.get(traceProductAuditNew.getAuditUserId());
			if (null != user && StringUtils.isNotBlank(user.getName())) {
				traceProductAuditNew.setAuditUserName(user.getName());
			}
		}
		return list;
	}
}
