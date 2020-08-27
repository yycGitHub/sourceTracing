package com.surekam.modules.productbatch.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.config.Global;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.DateUtil;
import com.surekam.common.utils.DateUtils;
import com.surekam.common.utils.UniqueCodeUtil;
import com.surekam.modules.api.entity.BaseTree;
import com.surekam.modules.api.utils.AccessData;
import com.surekam.modules.productbatch.dao.ProductBatchDao;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.sys.dao.OfficeDao;
import com.surekam.modules.sys.dao.UserDao;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.trace.TraceLableApply.dao.TraceLableApplyDao;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.trace.TraceLableApply.service.TraceLableApplyService;
import com.surekam.modules.trace.TraceLableContent.dao.TraceLableContentDao;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;
import com.surekam.modules.tracecode.dao.TraceCodeDao;
import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.tracecode.service.TraceCodeService;
import com.surekam.modules.tracecoderelationship.dao.TraceCodeRelationshipDao;
import com.surekam.modules.tracecoderelationship.entity.TraceCodeRelationship;
import com.surekam.modules.tracemodel.dao.TraceModelDataGroupDao;
import com.surekam.modules.tracemodel.dao.TraceModelDatalDao;
import com.surekam.modules.tracemodel.entity.TraceModel;
import com.surekam.modules.tracemodel.entity.TraceModelData;
import com.surekam.modules.tracemodel.entity.TraceModelDataGroup;
import com.surekam.modules.tracemodel.service.TraceModelService;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproductaudit.dao.TraceProductAuditDao;
import com.surekam.modules.traceproductaudit.entity.TraceProductAudit;
import com.surekam.modules.traceproductauditnew.dao.TraceProductAuditNewDao;
import com.surekam.modules.traceproductauditnew.entity.TraceProductAuditNew;
import com.surekam.modules.traceproperty.dao.TracePropertyDataDao;
import com.surekam.modules.traceproperty.dao.TracePropertyNewDao;
import com.surekam.modules.traceproperty.entity.TraceProperty;
import com.surekam.modules.traceproperty.entity.TracePropertyData;
import com.surekam.modules.traceproperty.entity.TracePropertyNew;
import com.surekam.modules.traceproperty.service.TracePropertyService;
import com.surekam.modules.traceresumetheme.dao.TraceResumeThemeDao;
import com.surekam.modules.traceresumetheme.entity.TraceResumeTheme;
import com.thoughtworks.xstream.core.util.Base64Encoder;

/**
 * 产品批次Service
 * 
 * @author liuyi
 * @version 2018-09-04
 */
@Component
@Transactional(readOnly = true)
public class ProductBatchService extends BaseService {

	@Autowired
	private OfficeDao officeDao;

	@Autowired
	private ProductBatchDao productBatchDao;

	@Autowired
	private TraceResumeThemeDao traceResumeThemeDao;

	@Autowired
	private TraceModelDatalDao traceModelDatalDao;

	@Autowired
	private TracePropertyService tracePropertyService;

	@Autowired
	private TracePropertyNewDao tracePropertyNewDao;

	@Autowired
	private TracePropertyDataDao tracePropertyDataDao;

	@Autowired
	private TraceModelService traceModelService;

	@Autowired
	private TraceLableApplyDao traceLableApplyDao;

	@Autowired
	private TraceLableApplyService traceLableApplyService;

	@Autowired
	private TraceModelDataGroupDao traceModelDataGroupDao;

	@Autowired
	private TraceCodeService traceCodeService;

	@Autowired
	private TraceCodeDao traceCodeDao;

	@Autowired
	private TraceCodeRelationshipDao traceCodeRelationshipDao;

	@Autowired
	private OfficeService officeService;

	@Autowired
	private TraceProductAuditDao traceProductAuditDao;
	
	@Autowired
	private TraceProductAuditNewDao traceProductAuditNewDao;
	
	@Autowired
	private TraceLableContentDao traceLableContentDao;

	public ProductBatch get(String id) {
		return productBatchDao.get(id);
	}

	public Page<ProductBatch> find(Page<ProductBatch> page, String batchName, String batchCode, String isEnd,
			String officeId, String productId) {
		DetachedCriteria dc = productBatchDao.createDetachedCriteria();
		dc.add(Restrictions.ne(ProductBatch.FIELD_DEL_FLAG_XGXT, ProductBatch.STATE_FLAG_DEL));
		if (StringUtils.isNotEmpty(batchName)) {
			dc.add(Restrictions.like("batchName", batchName, MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotEmpty(batchCode)) {
			dc.add(Restrictions.like("batchCode", batchCode, MatchMode.ANYWHERE));
		}
		if (StringUtils.isNotEmpty(isEnd)) {
			dc.add(Restrictions.eq("isEnd", isEnd));
		}
		if (StringUtils.isNotEmpty(officeId)) {
			dc.add(Restrictions.eq("officeId", officeId));
		}
		if (StringUtils.isNotEmpty(productId)) {
			dc.add(Restrictions.eq("productId", productId));
		}
		dc.addOrder(Order.desc("creatTime"));
		return productBatchDao.find(page, dc);
	}

	public Page<ProductBatch> find(Page<ProductBatch> page, String batchName, String batchCode, String isEnd, String officeId, String productId, User user) {
		String findByState = traceProductAuditDao.findByState("1");
		Page<ProductBatch> find = productBatchDao.find(page, batchName, batchCode, isEnd, officeId, productId, user, findByState);
		return find;
	}

	@Transactional(readOnly = false)
	public void save(ProductBatch productBatch) {
		productBatchDao.save(productBatch);
	}

	@Transactional(readOnly = false)
	public void delete(String id) {
		ProductBatch batch = productBatchDao.get(id);
		batch.setUpdateTime(new Date());
		batch.setUpdateUserid(UserUtils.getUser().getId());
		productBatchDao.save(batch);
		productBatchDao.deleteByXGXTId(id);
	}

	/**
	 * 逻辑删除批次及批次的模块数据和模块属性数据 包括未打印的标签相关数据 检查是否存在已打印的标签数据 如果存在 则返回1 否则为正常删除
	 * 
	 * @param batchId
	 */
	@Transactional(readOnly = false)
	public String deleteBatchWithModelDatas(String batchId, User user) {
		ProductBatch batch = productBatchDao.get(batchId);
		// 获取标签申请数据
		List<TraceLableApply> labelApplyList = traceLableApplyService.getLabelApplyListByBatchId(batchId);
		if (null != labelApplyList && 0 != labelApplyList.size()) {
			return "1";
		}

		List<TraceCode> labelCodeList = traceCodeService.getLabelCodeListByBatchId(batchId);
		if (null != labelCodeList && 0 != labelCodeList.size()) {
			return "1";
		}

		// 删除批次对应的的模块属性数据
		String hqlDeletePropertyData = "update TracePropertyData propertyData set propertyData.states = '"
				+ TracePropertyData.STATE_FLAG_DEL + "' " + " where propertyData.batchId ='" + batch.getId() + "'";

		// 删除批次对应模块数据
		String hqDeleteModelData = "update TraceModelData modelData set modelData.states='"
				+ TraceModelData.STATE_FLAG_DEL + "' " + " where modelData.batchId ='" + batchId + "'";
		tracePropertyDataDao.update(hqlDeletePropertyData);
		traceModelDatalDao.update(hqDeleteModelData);
		// 删除标签数据
		String hqDeleteLabelApply = "update TraceLableApply labelApply set labelApply.states='"
				+ TraceLableApply.STATE_FLAG_DEL + "' " + " where labelApply.batchId ='" + batchId + "'";
		traceLableApplyDao.update(hqDeleteLabelApply);
		// 删除批次数据
		batch.setUpdateTime(new Date());
		if (null != user) {
			batch.setUpdateUserid(user.getId());
		}
		batch.setStates(TraceProduct.STATE_FLAG_DEL);
		productBatchDao.save(batch);
		return "0";
	}

	/**
	 * 结束产品批次
	 * 
	 * @param id
	 */
	@Transactional(readOnly = false)
	public void disable(String id) {
		ProductBatch batch = productBatchDao.get(id);
		batch.setUpdateTime(new Date());
		batch.setUpdateUserid(UserUtils.getUser().getId());
		batch.setIsEnd(TraceProduct.YES);
		productBatchDao.save(batch);
	}

	/**
	 * 恢复产品批次
	 * 
	 * @param id
	 */
	@Transactional(readOnly = false)
	public void Recovery(String id) {
		ProductBatch batch = productBatchDao.get(id);
		batch.setUpdateTime(new Date());
		batch.setUpdateUserid(UserUtils.getUser().getId());
		batch.setIsEnd(TraceProduct.NO);
		productBatchDao.save(batch);
	}

	/**
	 * 通过产品ID获取对应所有批次数据 检查是否产品当前有批次数据 add by ligm 2018-10-19
	 * 
	 * @param productId
	 * @return
	 */
	public List<ProductBatch> findBatchListByProductId(String productId) {
		DetachedCriteria dc = productBatchDao.createDetachedCriteria();
		dc.add(Restrictions.ne(ProductBatch.FIELD_DEL_FLAG_XGXT, ProductBatch.STATE_FLAG_DEL));
		if (StringUtils.isNotEmpty(productId)) {
			dc.add(Restrictions.eq("productId", productId));
		}
		dc.addOrder(Order.desc("creatTime"));
		return productBatchDao.find(dc);
	}

	/**
	 * 通过产品ID获取对应所有批次数据 检查是否产品当前有批次数据和审核通过
	 * 
	 * @param productId
	 * @return
	 */
	public List<ProductBatch> newFindBatchListByProductId(String productId) {
		String findByState = traceProductAuditDao.findByState("1");
		List<ProductBatch> findByProductId = productBatchDao.findByProductId(productId, findByState);
		return findByProductId;
	}

	/**
	 * add by ligm 2018-10-11 新增或保存批次 模块 属性数据
	 * 
	 * @param traceProduct
	 */
	@Transactional(readOnly = false)
	public ProductBatch saveBatchWithModelsAndProperties(TraceProduct traceProduct, User user) {

		ProductBatch batch = traceProduct.getProductBatch();
		Office office = user.getCompany();
		// 新增批次 判断是否为新增批次及相关数据 ID为空则表示需新增批次并分配唯一编号
		if (null == batch || StringUtils.isBlank(batch.getId())) {
			batch = new ProductBatch();
			// 分配产品的唯一编码
			String batchCode = UniqueCodeUtil.getUniqueCode("ProductBatch", "batchCode", office.getCode());
			batch.setBatchCode(office.getCode() + "-" + UniqueCodeUtil.BATCH_PRE + "-" + batchCode);
			batch.setOfficeId(office.getId());
			batch.setCorpName(office.getName());
			batch.setProductId(traceProduct.getId());
			batch.setProductName(traceProduct.getProductName());
			if (StringUtils.isNotBlank(traceProduct.getThemeId())) {
				TraceResumeTheme traceResumeTheme = traceResumeThemeDao.get(traceProduct.getThemeId());
				batch.setThemeId(traceResumeTheme.getId());
				batch.setThemeName(traceResumeTheme.getThemeName());
			}
			batch.setStates(ProductBatch.STATE_FLAG_ADD);
			batch.setCreatTime(new Date());
			batch.setCreatUserid(user.getId());
			batch.setCreatUserName(user.getName());
			batch.setIsEnd("0");
			productBatchDao.save(batch);

		} else {
			if (StringUtils.isNotBlank(traceProduct.getThemeId())) {
				TraceResumeTheme traceResumeTheme = traceResumeThemeDao.get(traceProduct.getThemeId());
				batch.setThemeId(traceResumeTheme.getId());
				batch.setThemeName(traceResumeTheme.getThemeName());
			}
			batch.setUpdateTime(new Date());
			batch.setUpdateUserid(user.getId());
			productBatchDao.save(batch);
			// 生长记录模块不删除 model modelShowType为2的模块数据
			String hqDeleteModelData = "delete from TraceModelData " + " where batchId ='" + batch.getId() + "'"
					+ " and modelId not in"
					+ " (select model.id from TraceModel model where model.modelShowType = '2')";

			// 删除已经存在的模块和属性数据 生长记录模块的属性值不删除
			String hqlDeletePropertyData = "update TracePropertyData propertyData set propertyData.states = '"
					+ TracePropertyData.STATE_FLAG_DEL + "' " + " where propertyData.batchId ='" + batch.getId() + "'"
					+ " and propertyData.modelDataId in"
					+ "(select modelData.id from TraceModelData modelData where modelData.batchId = '" + batch.getId()
					+ "'" + " and modelData.states ='" + TraceModelData.STATE_FLAG_DEL + "'" + ")";

			tracePropertyDataDao.update(hqlDeletePropertyData);
			traceModelDatalDao.update(hqDeleteModelData);
		}
		// 保存模块和模块属性数据
		if (null != traceProduct.getTraceModelList() && 0 != traceProduct.getTraceModelList().size()) {
			List<TraceModel> traceModelList = traceProduct.getTraceModelList();
			// 迭代模块数据
			for (Iterator<TraceModel> iterator = traceModelList.iterator(); iterator.hasNext();) {
				TraceModel traceModel = (TraceModel) iterator.next();
				if (StringUtils.isNotBlank(traceModel.getModelShowType())
						&& "2".equals(traceModel.getModelShowType())) {

					// TODO 之前代码每次保存都会清除掉生产过程的t_trace_model_data数据，导致每次新增生长日记后之前的生长日记数据无法查出来。故注释一下代码

					// 检查是否存在生长记录模块 如果不存在 则保存
					TraceModelData modelData = traceModelService
							.findTraceModelDataByModelIdAndBatchId(traceModel.getId(), batch.getId());

					if (null != modelData) {

						/*
						 * traceModelDatalDao.
						 * update("delete from TraceModelData  where batchId=:p1 and modelId=:p2", new
						 * Parameter(batch.getId(), traceModel.getId())); //检查是否为生长记录 不存在则保存 存在则不保存
						 * TraceModelData modelData1 = new TraceModelData();
						 * modelData1.setBatchId(batch.getId());
						 * modelData1.setModelId(traceModel.getId());
						 * modelData1.setModelName(traceModel.getModelName());
						 * modelData1.setOfficeId(office.getId()); modelData1.setCreatTime(new Date());
						 * modelData1.setCreatUserid(user.getId()); String checkedArray = "{" +
						 * traceProduct.getCheckedArray() + "}"; boolean contains =
						 * checkedArray.contains(traceModel.getId()); if(contains == true) {
						 * modelData1.setStates(TraceModelData.STATE_FLAG_ADD); }else if(contains ==
						 * false){ modelData1.setStates(TraceModelData.STATE_FLAG_ADD); }
						 * modelData1.setSort(traceModel.getSort());
						 * traceModelDatalDao.save(modelData1);
						 */
						continue;
					} else {
						// 生长记录只保存模块 在此不保存属性值
						TraceModelData modelDataGrow = new TraceModelData();
						modelDataGrow.setBatchId(batch.getId());
						modelDataGrow.setModelId(traceModel.getId());
						modelDataGrow.setModelName(traceModel.getModelName());
						modelDataGrow.setOfficeId(office.getId());
						modelDataGrow.setCreatTime(new Date());
						modelDataGrow.setCreatUserid(user.getId());
						modelDataGrow.setStates(TraceModelData.STATE_FLAG_ADD);
						modelDataGrow.setSort(traceModel.getSort());
						traceModelDatalDao.save(modelDataGrow);
						continue;
					}
				}

				// 检查是否为生长记录 不存在则保存 存在则不保存
				TraceModelData modelData = new TraceModelData();
				modelData.setBatchId(batch.getId());
				modelData.setModelId(traceModel.getId());
				modelData.setModelName(traceModel.getModelName());
				modelData.setOfficeId(office.getId());
				modelData.setCreatTime(new Date());
				modelData.setCreatUserid(user.getId());

				String checkedArray = "{" + traceProduct.getCheckedArray() + "}";
				boolean contains = checkedArray.contains(traceModel.getId());
				if (traceModel.getModelName().equals("主模块") || contains == true) {
					modelData.setStates(TraceModelData.STATE_FLAG_ADD);
				} else if (contains == false) {
					// TODO 前代码为 TraceModelData.STATE_FLAG_DEL
					// 不知道以前哪位大神写的非主模块第一次保存都给存入D状态，导致溯源预览没有模块数据
					modelData.setStates(TraceModelData.STATE_FLAG_ADD);
				}
				modelData.setSort(traceModel.getSort());
				traceModelDatalDao.save(modelData);

				int Counter = 1;// 计数器--sort排序
				List<TracePropertyNew> tracePropertyList = traceModel.getTracePropertyNewList();
				if (tracePropertyList.size() > 0) {
					// 迭代模块下属性数据
					for (Iterator<TracePropertyNew> iterator2 = tracePropertyList.iterator(); iterator2.hasNext();) {
						TracePropertyNew traceProperty = (TracePropertyNew) iterator2.next();
						saveTracePropertyNewByTraceProperty(traceProperty, office.getId(), user);

						TracePropertyData propertyData = new TracePropertyData();
						propertyData.setBatchId(batch.getId());
						propertyData.setModelDataId(modelData.getId());
						propertyData.setPropertyId(traceProperty.getId());
						propertyData.setPropertyValue(traceProperty.getPropertyValue());
						propertyData.setCreatTime(new Date());
						propertyData.setCreatUserid(user.getId());
						propertyData.setStates(TracePropertyData.STATE_FLAG_ADD);
						propertyData.setSort(Counter);
						tracePropertyDataDao.save(propertyData);
						Counter++;
					}
				} else {
					List<TraceProperty> propertyList = traceModel.getTracePropertyList();
					for (Iterator<TraceProperty> iterator2 = propertyList.iterator(); iterator2.hasNext();) {
						TraceProperty traceProperty = (TraceProperty) iterator2.next();
						TracePropertyNew tracePropertyNew = new TracePropertyNew();
						tracePropertyNew.setId(traceProperty.getId());
						// TODO 这也不知道是哪位大神写的，下面三个属性在库里面都是必填项，这里却不传参进去。这是刚刚才给加上之后才不报错的
						tracePropertyNew.setModelId(traceProperty.getModelId());
						tracePropertyNew.setPropertyNameZh(traceProperty.getPropertyNameZh());
						tracePropertyNew.setPropertyNameEn(traceProperty.getPropertyNameEn());
						tracePropertyNew.setPropertyType(traceProperty.getPropertyType());
						saveTracePropertyNewByTraceProperty(tracePropertyNew, office.getId(), user);

						TracePropertyData propertyData = new TracePropertyData();
						propertyData.setBatchId(batch.getId());
						propertyData.setModelDataId(modelData.getId());
						propertyData.setPropertyId(traceProperty.getId());
						propertyData.setPropertyValue(traceProperty.getPropertyValue());
						propertyData.setCreatTime(new Date());
						propertyData.setCreatUserid(user.getId());
						propertyData.setStates(TracePropertyData.STATE_FLAG_ADD);
						propertyData.setSort(Counter);
						tracePropertyDataDao.save(propertyData);
						Counter++;
					}
				}

			}
		}
		return batch;
	}

	/**
	 * add by liw 2019-1-21 新增或保存批次 模块 属性数据
	 * 
	 * @param traceProduct
	 */
	@Transactional(readOnly = false)
	public ProductBatch saveBatchModelProperties(TraceProduct traceProduct, String batchId, String isNew, String ifxz,
			Map map, String[] result, String[] modelArr, String[] propertyArr, String[] mkArr, String[] rjArr,
			String[] rjresult, String modelId) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		Office office = officeService.get(traceProduct.getOfficeId());
		System.out.println("333        "+sdf.format(new Date()));
		ProductBatch batch = new ProductBatch();
		// 新增批次 判断是否为新增批次及相关数据 ID为空则表示需新增批次并分配唯一编号
		if ("1".equals(isNew) || StringUtils.isBlank(batchId)) {// 新增
			// 分配产品的唯一编码
			String batchCode = UniqueCodeUtil.getUniqueCode("ProductBatch", "batchCode", office.getCode());
			batch.setBatchCode(office.getCode() + "-" + UniqueCodeUtil.BATCH_PRE + "-" + batchCode);
			batch.setOfficeId(office.getId());
			batch.setCorpName(office.getName());
			batch.setProductId(traceProduct.getId());
			batch.setProductName(traceProduct.getProductName());
			if (StringUtils.isNotBlank(traceProduct.getThemeId())) {
				TraceResumeTheme traceResumeTheme = traceResumeThemeDao.get(traceProduct.getThemeId());
				batch.setThemeId(traceResumeTheme.getId());
				batch.setThemeName(traceResumeTheme.getThemeName());
			}
			batch.setStates(ProductBatch.STATE_FLAG_ADD);
			batch.setCreatTime(new Date());
			batch.setCreatUserid(map.get("id").toString());
			batch.setCreatUserName(map.get("name").toString());
			batch.setIsEnd("0");
			productBatchDao.save(batch);
			System.out.println("444        "+sdf.format(new Date()));

		} else {// 修改
			batch = productBatchDao.get(batchId);
			if (StringUtils.isNotBlank(traceProduct.getThemeId())) {
				TraceResumeTheme traceResumeTheme = traceResumeThemeDao.get(traceProduct.getThemeId());
				batch.setThemeId(traceResumeTheme.getId());
				batch.setThemeName(traceResumeTheme.getThemeName());
			}
			batch.setUpdateTime(new Date());
			batch.setUpdateUserid(map.get("id").toString());
			productBatchDao.save(batch);
			// 删除已经存在的属性
			String hqlDeleteProperty = "delete from TracePropertyNew "
					+ " where modelId in (select modelId from TraceModelData modelData where modelData.states<>'D' and modelData.batchId ='" + batch.getId() + "')";
			// 生长记录模块不删除 model modelShowType为2的模块数据
			String hqDeleteModelData = "delete from TraceModelData " + " where batchId ='" + batch.getId() + "'";
			String hqlDeletePropertyData = "update TracePropertyData propertyData set propertyData.states = 'D' "
					+ " where propertyData.batchId ='" + batch.getId() + "'";
			System.out.println("444        "+sdf.format(new Date()));
			//tracePropertyNewDao.update(hqlDeleteProperty);
			System.out.println("888        "+sdf.format(new Date()));
			tracePropertyDataDao.update(hqlDeletePropertyData);
			System.out.println("999        "+sdf.format(new Date()));
			traceModelDatalDao.update(hqDeleteModelData);
			System.out.println("000        "+sdf.format(new Date()));
		}

		for (int m = 0; m < mkArr.length; m++) {
			String[] mks = mkArr[m].split("_");
			String mkid = mks[0];
			String mkmc = mks[1];
			String mklx = mks[2];
			System.out.println("mkmc==" + mkmc + "-----/mkid==" + mkid);
			TraceModel traceModel = traceModelService.get(mkid);
			TraceModel traceModel2 = new TraceModel();
			if (traceModel == null) {
				traceModel2.setModelName(mkmc);
				traceModel2.setModelShowType(mklx);
				traceModel2.setOfficeId(office.getId());
				traceModel2.setProductId(traceProduct.getId());
				traceModel2.setModelCode(getCode(TraceModel.SY_MODEL_CODE));
				int s = m + 1;
				traceModel2.setSort(String.valueOf(s));
				traceModel2.setIsMainModel("0");
				traceModel2.setStatus("1");
				traceModel2.setCreatTime(new Date());
				traceModel2.setCreatUserid(map.get("id").toString());
				traceModel2.setStates("A");
				traceModelService.save(traceModel2);
			}
			TraceModelData modelData = new TraceModelData();
			modelData.setBatchId(batch.getId());
			if (traceModel == null) {
				modelData.setModelId(traceModel2.getId());
			} else {
				modelData.setModelId(mkid);
			}
			modelData.setModelName(mkmc);
			modelData.setOfficeId(office.getId());
			modelData.setCreatTime(new Date());
			modelData.setCreatUserid(map.get("id").toString());
			if (StringUtils.isNotBlank(ifxz)) {
				if (ifxz.contains(",")) {
					String[] st = ifxz.split(",");
					int s = 0;
					for (s = 0; s < st.length; s++) {
						String g = st[s];
						if (g.equals(mkid)) {
							modelData.setStates(TraceModelData.STATE_FLAG_ADD);
							break;
						}
					}
					if (s == st.length) {
						modelData.setStates(TraceModelData.STATE_FLAG_DEL);
					}
				} else {
					if (ifxz.equals(mkid)) {
						modelData.setStates(TraceModelData.STATE_FLAG_ADD);
					} else {
						modelData.setStates(TraceModelData.STATE_FLAG_DEL);
					}
				}
			} else {
				modelData.setStates(TraceModelData.STATE_FLAG_DEL);
			}
			modelData.setSort(m + 1 + "");
			traceModelDatalDao.flush();
			traceModelDatalDao.save(modelData);
			
			System.out.println("666        "+sdf.format(new Date()));
			
			if ("1".equals(mklx)) {// 其他模块
				if (StringUtils.isNotBlank(modelId) && mkid.equals(modelId)) {// 判断用户评论模块
					TracePropertyNew tracePropertyNew = new TracePropertyNew();
					tracePropertyNew.setOfficeId(office.getId());
					tracePropertyNew.setModelId(modelId);
					tracePropertyNew.setPropertyNameZh("用户评论内容");
					tracePropertyNew.setPropertyNameEn("content");
					tracePropertyNew.setPropertyType("1");
					tracePropertyNew.setTimeFlag("0");
					tracePropertyNew.setStatus("1");
					tracePropertyNew.setCreatTime(new Date());
					tracePropertyNew.setCreatUserid(map.get("id").toString());
					tracePropertyNew.setStates(TracePropertyNew.STATE_FLAG_ADD);
					tracePropertyNew.setSort(99);
					tracePropertyNewDao.flush();
					tracePropertyNewDao.save(tracePropertyNew);

					TracePropertyData propertyData = new TracePropertyData();
					propertyData.setBatchId(batch.getId());
					propertyData.setModelDataId(modelData.getId());
					propertyData.setPropertyId(tracePropertyNew.getId());
					propertyData.setPropertyValue("");
					propertyData.setCreatTime(new Date());
					propertyData.setCreatUserid(map.get("id").toString());
					propertyData.setStates(TracePropertyData.STATE_FLAG_ADD);
					propertyData.setSort(99);
					tracePropertyDataDao.flush();
					tracePropertyDataDao.save(propertyData);
				}
				for (int i = 0; i < modelArr.length; i++) {
					String[] arr = modelArr[i].split("_");
					String mkid1 = arr[0];
					String sxid = arr[1];
					String type = arr[2];
					String[] proArr = propertyArr[i].split("_");
					// String[] vals = result.split(",");
					String[] vals = result;
					String sxmc = proArr[1];
					if (mkid1.equals(mkid)) {
						if (i < vals.length && StringUtils.isNotBlank(vals[i])) {
							TracePropertyNew propertyNew = tracePropertyService.findPropertyBySxid(sxid);

							TracePropertyNew tracePropertyNew = new TracePropertyNew();
							tracePropertyNew.setOfficeId(office.getId());
							if (traceModel == null) {
								tracePropertyNew.setModelId(traceModel2.getId());
							} else {
								tracePropertyNew.setModelId(mkid);
							}
							tracePropertyNew.setPropertyNameZh(sxmc);
							tracePropertyNew.setPropertyNameEn(propertyNew.getPropertyNameEn());
							tracePropertyNew.setPropertyType(type);
							tracePropertyNew.setTimeFlag("0");
							tracePropertyNew.setStatus("1");
							tracePropertyNew.setCreatTime(new Date());
							tracePropertyNew.setCreatUserid(map.get("id").toString());
							tracePropertyNew.setStates(TracePropertyNew.STATE_FLAG_ADD);
							tracePropertyNew.setSort(i + 1);
							tracePropertyNewDao.flush();
							tracePropertyNewDao.save(tracePropertyNew);

							TracePropertyData propertyData = new TracePropertyData();
							propertyData.setBatchId(batch.getId());
							propertyData.setModelDataId(modelData.getId());
							propertyData.setPropertyId(tracePropertyNew.getId());
							propertyData.setPropertyValue(vals[i]);
							propertyData.setCreatTime(new Date());
							propertyData.setCreatUserid(map.get("id").toString());
							propertyData.setStates(TracePropertyData.STATE_FLAG_ADD);
							propertyData.setSort(i + 1);
							tracePropertyDataDao.flush();
							tracePropertyDataDao.save(propertyData);
						}
					}
				}
			} else if ("2".equals(mklx)) { // 生产过程模块
				if (rjresult != null && rjresult.length > 0) {
					// String[] vals = rjresult.split(",");
					String[] vals = rjresult;
					for (int i = 0; i < rjArr.length; i = i + 3) {
						String[] rj = rjArr[i].split("_");
						String mkid1 = rj[0];
						if (mkid1.equals(mkid)) {
							TraceModelDataGroup traceModelDataGroup = new TraceModelDataGroup();
							traceModelDataGroup.setModelDataId(modelData.getId());
							traceModelDataGroup.setCreatTime(addOneSecond(i));
							traceModelDataGroup.setCreatUserid(map.get("id").toString());
							traceModelDataGroup.setStates(TraceModelDataGroup.STATE_FLAG_ADD);
							traceModelDataGroupDao.flush();
							traceModelDataGroupDao.save(traceModelDataGroup);

							String[] arr = { "11", "111", "12" };
							for (int j = 0; j < 3; j++) {
								String value = vals[i + j];
								if (!value.equals("0_0")) {
									TracePropertyData tracePropertyData = new TracePropertyData();
									tracePropertyData.setBatchId(modelData.getBatchId());
									tracePropertyData.setModelDataGroupId(traceModelDataGroup.getId());
									tracePropertyData.setModelDataId(modelData.getId());
									tracePropertyData.setPropertyId(arr[j]);
									tracePropertyData.setPropertyValue(value);
									tracePropertyData.setStates(TracePropertyData.STATE_FLAG_ADD);
									tracePropertyData.setCreatTime(new Date());
									tracePropertyData.setCreatUserid(map.get("id").toString());
									tracePropertyData.setSort(i + j + 1);
									tracePropertyDataDao.flush();
									tracePropertyDataDao.save(tracePropertyData);
								}
							}
						}
					}
				}
			}
		}
		System.out.println("777        "+sdf.format(new Date()));
		return batch;
	}

	/**
	 * add by liw 2019-7-3 保存批次、模块、属性数据
	 * 
	 * @param traceProduct
	 */
	@Transactional(readOnly = false)
	public void saveBatchAndModel(HttpServletRequest request, TraceProduct traceProduct, AccessData accessData,
			User user) throws Exception {
		List<Map<String,Object>> contentList = new ArrayList<Map<String,Object>>();
		ProductBatch batch = getBatchByCode(accessData.getOfficeId(), accessData.getBatchCode(), user, traceProduct.getId(), accessData.getSysId());
		if (batch != null) {
			if (StringUtils.isBlank(batch.getProductId())) {
				// 保存批次信息
				batch.setProductId(traceProduct.getId());
				productBatchDao.save(batch);
			}
			
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("cpmc", traceProduct.getProductName());
			contentList.add(map);

			// 保存模块及属性信息
			int m = 0;
			List<TraceModel> traceModelList = traceProduct.getTraceModelList();
			for (TraceModel traceModel : traceModelList) {
				int s = m++;
				String mid = "";
				TraceModel traceModel2 = new TraceModel();
				// 判断模块名称是否存在，若不存在则新增一条模块数据
				TraceModel model = traceModelService.find(traceModel.getModelName(),
						"ddc96d3f55474fdb85ecd6706fe45379");
				if (model == null) {
					traceModel2.setModelName(traceModel.getModelName());
					traceModel2.setModelShowType(traceModel.getModelShowType());
					traceModel2.setOfficeId(traceProduct.getOfficeId());
					traceModel2.setProductId(traceProduct.getId());
					traceModel2.setModelCode(getCode(TraceModel.SY_MODEL_CODE));
					traceModel2.setSort(String.valueOf(s));
					traceModel2.setIsMainModel("0");
					traceModel2.setStatus("1");
					traceModel2.setCreatUserid(traceProduct.getCreatUserid());
					traceModel2.setStates("A");
					traceModelService.save(traceModel2);

					mid = traceModel2.getId();
				} else {
					mid = model.getId();
				}
				
				// 新增模块data数据
				TraceModelData modelData = new TraceModelData();
				modelData.setBatchId(batch.getId());
				modelData.setModelId(mid);
				modelData.setModelName(traceModel.getModelName());
				modelData.setOfficeId(traceProduct.getOfficeId());
				modelData.setCreatUserid(traceProduct.getCreatUserid());
				modelData.setSort(String.valueOf(s));
				traceModelDatalDao.flush();
				traceModelDatalDao.save(modelData);
				// 添加属性数据
				List<TracePropertyNew> tracePropertyNewList = traceModel.getTracePropertyNewList();
				if ("1".equals(traceModel.getModelShowType())) {// 其他模块
					// 添加用户评价模块的属性
					if ("用户评价".equals(traceModel.getModelName())) {// 判断用户评论模块
						TracePropertyNew tracePropertyNew = new TracePropertyNew();
						tracePropertyNew.setOfficeId(traceProduct.getOfficeId());
						tracePropertyNew.setModelId(mid);
						tracePropertyNew.setPropertyNameZh("用户评论内容");
						tracePropertyNew.setPropertyNameEn("content");
						tracePropertyNew.setPropertyType("1");
						tracePropertyNew.setTimeFlag("0");
						tracePropertyNew.setStatus("1");
						tracePropertyNew.setCreatUserid(traceProduct.getCreatUserid());
						tracePropertyNew.setSort(99);
						tracePropertyNewDao.flush();
						tracePropertyNewDao.save(tracePropertyNew);

						TracePropertyData propertyData = new TracePropertyData();
						propertyData.setBatchId(batch.getId());
						propertyData.setModelDataId(modelData.getId());
						propertyData.setPropertyId(tracePropertyNew.getId());
						propertyData.setPropertyValue("");
						propertyData.setCreatUserid(traceProduct.getCreatUserid());
						propertyData.setSort(99);
						tracePropertyDataDao.flush();
						tracePropertyDataDao.save(propertyData);
					}
					if (tracePropertyNewList != null && tracePropertyNewList.size() > 0) {
						for (int i = 0; i < tracePropertyNewList.size(); i++) {
							TracePropertyNew propertyNew = tracePropertyNewList.get(i);
							TracePropertyNew tracePropertyNew = new TracePropertyNew();
							tracePropertyNew.setPropertyNameEn(propertyNew.getPropertyNameEn());
							tracePropertyNew.setOfficeId(traceProduct.getOfficeId());
							tracePropertyNew.setModelId(mid);
							tracePropertyNew.setPropertyNameZh(propertyNew.getPropertyNameZh());
							tracePropertyNew.setPropertyType(propertyNew.getPropertyType());
							tracePropertyNew.setTimeFlag("0");
							tracePropertyNew.setStatus("1");
							tracePropertyNew.setCreatUserid(traceProduct.getCreatUserid());
							tracePropertyNew.setStates(TracePropertyNew.STATE_FLAG_ADD);
							tracePropertyNew.setSort(i + 1);
							tracePropertyNewDao.flush();
							tracePropertyNewDao.save(tracePropertyNew);

							TracePropertyData propertyData = new TracePropertyData();
							propertyData.setBatchId(batch.getId());
							propertyData.setModelDataId(modelData.getId());
							propertyData.setPropertyId(tracePropertyNew.getId());
							if ("3".equals(propertyNew.getPropertyType())) {// 图片
								String imgUrl = "";
								if (StringUtils.isNotBlank(propertyNew.getPropertyValue())) {
									imgUrl = imageUpload(request, propertyNew.getPropertyValue());
								}
								propertyData.setPropertyValue(imgUrl);
							} else {
								if (propertyNew.getPropertyNameZh().equals("生产日期")) {
									boolean res = propertyNew.getPropertyValue().contains("-");
									if(res == true) {
										Date d1 = new SimpleDateFormat("yyyy-MM-dd").parse(propertyNew.getPropertyValue());
										String formatSmsDt = DateUtil.formatSmsDt(d1);
										propertyData.setPropertyValue(formatSmsDt);
									} else {
										propertyData.setPropertyValue(propertyNew.getPropertyValue());
									}
								} else {
									propertyData.setPropertyValue(propertyNew.getPropertyValue());
								}
							}
							propertyData.setCreatUserid(traceProduct.getCreatUserid());
							propertyData.setSort(i + 1);
							tracePropertyDataDao.flush();
							tracePropertyDataDao.save(propertyData);
							
							if ("基本信息".equals(traceModel.getModelName()) || "主模块".equals(traceModel.getModelName())) {
								Map<String,Object> map1 = new HashMap<String, Object>();
								if(propertyNew.getPropertyNameZh().contains("产地")){
									map1.put("cd", propertyNew.getPropertyValue());
								}
								if(propertyNew.getPropertyNameZh().contains("保质期")){
									map1.put("bzq", propertyNew.getPropertyValue());
								}
								if(propertyNew.getPropertyNameZh().contains("电话")){
									map1.put("dh", propertyNew.getPropertyValue());
								}
								if(propertyNew.getPropertyNameZh().contains("负责人") || propertyNew.getPropertyNameZh().contains("经办人")){
									map1.put("fzr", propertyNew.getPropertyValue());
								}
								if(map1.size()>0){
									contentList.add(map1);
								}
							}
						}
					}
				} else if ("2".equals(traceModel.getModelShowType())) { // 生产过程模块
					String[] arr = { "11", "111", "12" };
					if (tracePropertyNewList != null && tracePropertyNewList.size() > 0) {
						for (TracePropertyNew property : tracePropertyNewList) {
							String bCode = property.getBatchCode();
							BaseTree baseTree = property.getBaseTree();
							if (property.getRecordList() != null && property.getRecordList().size() > 0) {
								int len = property.getRecordList().size();
								for (int i = 0; i < len; i++) {
									TracePropertyNew propertyNew = property.getRecordList().get(i);
									// 添加分组表数据
									TraceModelDataGroup traceModelDataGroup = new TraceModelDataGroup();
									traceModelDataGroup.setModelDataId(modelData.getId());
									traceModelDataGroup.setBatchCode(bCode);
									traceModelDataGroup.setBaseName(baseTree.getName());
									traceModelDataGroup.setBaseAddress(baseTree.getAddress());
									traceModelDataGroup.setCreatUserid(traceProduct.getCreatUserid());
									traceModelDataGroupDao.flush();
									traceModelDataGroupDao.save(traceModelDataGroup);

									// 添加属性值数据
									// 存操作时间
									String type1 = propertyNew.getPropertyType1();
									String value1 = propertyNew.getPropertyValue1();
									TracePropertyData tracePropertyData = new TracePropertyData();
									tracePropertyData.setBatchId(modelData.getBatchId());
									tracePropertyData.setModelDataGroupId(traceModelDataGroup.getId());
									tracePropertyData.setModelDataId(modelData.getId());
									if ("5".equals(type1)) {
										tracePropertyData.setPropertyId(arr[0]);
										tracePropertyData.setPropertyValue(value1);
									}
									tracePropertyData.setCreatUserid(traceProduct.getCreatUserid());
									tracePropertyData.setSort(i * 3 + 1);
									tracePropertyDataDao.flush();
									tracePropertyDataDao.save(tracePropertyData);

									// 存操作内容
									String type2 = propertyNew.getPropertyType2();
									String value2 = propertyNew.getPropertyValue2();
									tracePropertyData = new TracePropertyData();
									tracePropertyData.setBatchId(modelData.getBatchId());
									tracePropertyData.setModelDataGroupId(traceModelDataGroup.getId());
									tracePropertyData.setModelDataId(modelData.getId());
									if ("1".equals(type2)) {
										tracePropertyData.setPropertyId(arr[1]);
										tracePropertyData.setPropertyValue(value2);
									}
									tracePropertyData.setCreatUserid(traceProduct.getCreatUserid());
									tracePropertyData.setSort(i * 3 + 2);
									tracePropertyDataDao.flush();
									tracePropertyDataDao.save(tracePropertyData);

									String type3 = propertyNew.getPropertyType3();
									String value3 = propertyNew.getPropertyValue3();
									tracePropertyData = new TracePropertyData();
									tracePropertyData.setBatchId(modelData.getBatchId());
									tracePropertyData.setModelDataGroupId(traceModelDataGroup.getId());
									tracePropertyData.setModelDataId(modelData.getId());
									if ("3".equals(type3)) {
										tracePropertyData.setPropertyId(arr[2]);
										String imgUrl = "";
										if (StringUtils.isNotBlank(value3)) {
											imgUrl = imageUpload(request, value3);
										}
										tracePropertyData.setPropertyValue(imgUrl);
									}
									tracePropertyData.setCreatUserid(traceProduct.getCreatUserid());
									tracePropertyData.setSort(i * 3 + 3);
									tracePropertyDataDao.flush();
									tracePropertyDataDao.save(tracePropertyData);
								}
							}
						}
					}
				}
			}
		} else {
			batch = getBatchByCode1(accessData.getOfficeId(), accessData.getBatchCode(), user, traceProduct.getId());
		}
		saveLableData(accessData, traceProduct, batch, contentList);
	}
	// 保存标签数据
	@Transactional(readOnly = false)
	public void saveLableData(AccessData accessData, TraceProduct traceProduct, ProductBatch batch, List<Map<String, Object>> contentList) throws Exception {
		String applyId = "";
		String code = accessData.getTraceCode();
		String[] arr = {"cpmc","cd","bzq","dh","fzr"};
		TraceCode tCode = traceCodeService.getEntityByCode(code);
		if (tCode == null) {
			TraceLableApply applyObj = traceCodeService.getApplyLableByCode(traceProduct.getId(), batch.getId());
			if (applyObj != null) {
				applyObj.setApplyNum((Integer.parseInt(applyObj.getApplyNum()) + 1) + "");
				applyObj.setTotalPrice(applyObj.getTotalPrice().add(new BigDecimal("0.90")).setScale(2, RoundingMode.HALF_UP));
				traceLableApplyDao.save(applyObj);
				applyId = applyObj.getId();
			} else {
				// 添加标签申请数据
				TraceLableApply traceLableApply = new TraceLableApply();
				traceLableApply.setApplyNum("1");
				traceLableApply.setTotalPrice(new BigDecimal("0.90").setScale(2, RoundingMode.HALF_UP));
				traceLableApply.setSysId(accessData.getSysId());
				traceLableApply.setTraceProductId(traceProduct.getId());
				traceLableApply.setBatchId(batch.getId());
				traceLableApply.setLabelTemplateId("1");
				traceLableApply.setAuditFlag("1");
				traceLableApply.setCancelFlag("0");
				traceLableApply.setPrintFlag("0");
				traceLableApply.setCreatUserid(traceProduct.getCreatUserid());
				traceLableApply.setOfficeId(traceProduct.getOfficeId());
				traceLableApplyDao.save(traceLableApply);
				applyId = traceLableApply.getId();
				
				for(int i=0;i<arr.length;i++){
					String key = arr[i];
					int j = 0;
					for (j = 0;j<contentList.size();j++) {
						Map<String,Object> map = contentList.get(j);
						if(map.containsKey(key)){
							TraceLableContent traceLableContent = new TraceLableContent();
							if(key.equals("cpmc")){
								traceLableContent.setElementId("11");
								traceLableContent.setApplyElementContent("产品名称："+(map.get("cpmc")==null?"":map.get("cpmc").toString()));
							}else if(key.equals("cd")){
								traceLableContent.setElementId("14");
								traceLableContent.setApplyElementContent("产地："+(map.get("cd")==null?"":map.get("cd").toString()));
							}else if(key.equals("bzq")){
								traceLableContent.setElementId("13");
								traceLableContent.setApplyElementContent("保质期："+(map.get("bzq")==null?"":map.get("bzq").toString()));
							}else if(key.equals("dh")){
								traceLableContent.setElementId("16");
								traceLableContent.setApplyElementContent("电话："+(map.get("dh")==null?"":map.get("dh").toString()));
							}else if(key.equals("fzr")){
								traceLableContent.setElementId("15");
								traceLableContent.setApplyElementContent("负责人："+(map.get("fzr")==null?"":map.get("fzr").toString()));
							}
							traceLableContent.setLabelTemplateId("1");
							traceLableContent.setApplyId(applyId);
							traceLableContent.setCreatUserid(traceProduct.getCreatUserid());
							traceLableContentDao.save(traceLableContent);
							break;
						}
					}
					if(j == contentList.size()){
						TraceLableContent traceLableContent = new TraceLableContent();
						if(key.equals("cpmc")){
							traceLableContent.setElementId("11");
							traceLableContent.setApplyElementContent("产品名称：");
						}else if(key.equals("cd")){
							traceLableContent.setElementId("14");
							traceLableContent.setApplyElementContent("产地：");
						}else if(key.equals("bzq")){
							traceLableContent.setElementId("13");
							traceLableContent.setApplyElementContent("保质期：");
						}else if(key.equals("dh")){
							traceLableContent.setElementId("16");
							traceLableContent.setApplyElementContent("电话：");
						}else if(key.equals("fzr")){
							traceLableContent.setElementId("15");
							traceLableContent.setApplyElementContent("负责人：");
						}
						traceLableContent.setLabelTemplateId("1");
						traceLableContent.setApplyId(applyId);
						traceLableContent.setCreatUserid(traceProduct.getCreatUserid());
						traceLableContentDao.save(traceLableContent);
					}
				}
			}

			TraceCode traceCode = new TraceCode();
			traceCode.setTraceCode(code);
			traceCode.setSerialNumber(Integer.parseInt(code.substring(14, 24)));
			traceCode.setPackType(accessData.getPackType());
			traceCode.setSysId(accessData.getSysId());
			traceCode.setApplyId(applyId);
			traceCode.setOfficeId(traceProduct.getOfficeId());
			traceCode.setBatchId(batch.getId());
			traceCode.setTraceCount(0);
			traceCode.setStatus("2");
			traceCode.setActivationDate(new Date());
			traceCode.setActivator(traceProduct.getCreatUserid());
			traceCodeDao.save(traceCode);
			
		}

	}

	// 保存标签数据
	@Transactional(readOnly = false)
	public void saveCodeRelationship(AccessData accessData, TraceCode traceCode, ProductBatch batch) throws Exception {
		List<TraceCode> list = accessData.getTraceCodeList();
		for (TraceCode code : list) {
			TraceCodeRelationship traceCodeRelationship = new TraceCodeRelationship();
			traceCodeRelationship.setTraceCode(code.getTraceCode());
			traceCodeRelationship.setPackType(code.getPackType());
			traceCodeRelationship.setParentId(traceCode.getId());
			traceCodeRelationship.setCreatUserid(traceCode.getCreatUserid());
			traceCodeRelationshipDao.save(traceCodeRelationship);
		}
	}
	
	public String getCode(String type) {
		String code = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String day = df.format(new Date());
		String maxCode = getMaxModelCode(type);
		if (StringUtils.isNotBlank(maxCode)) {// CP20180828
			if (maxCode.substring(0, 10).equals(type + day)) {
				String ws = maxCode.substring(10, 14);
				code = type + day + String.format("%0" + 4 + "d", Integer.parseInt(ws) + 1);
			} else {
				code = type + day + "0001";
			}
		} else {
			code = type + day + "0001";
		}

		return code;
	}

	public String getMaxModelCode(String type) {
		List<String> list = productBatchDao.find(
				"select max(t.modelCode) from TraceModel t where t.states!='D' and t.modelCode like '" + type + "%'");
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return "";
		}
	}

	public Date addOneSecond(int i) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.add(Calendar.SECOND, i);
		return calendar.getTime();
	}

	/**
	 * 通过产品ID获取最近的批次信息 add by liw 2018-12-06
	 * 
	 * @param productId
	 * @return
	 */
	public ProductBatch getProductBatchByProductId(String productId) {
		String sql = "select t.* from t_trace_product_batch t where t.product_id = :p1 and t.states<>'D' order by t.creat_time desc";
		List<ProductBatch> list = productBatchDao.findBySql(sql, new Parameter(productId), ProductBatch.class);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public Integer getBatchCount(String officeId, String productId, String states) {
		String hql = "select a.id from ProductBatch a where a.productId=:p1 ";
		if (StringUtils.isNotBlank(states) && states.equals("A")) {
			hql += " and a.states <> 'D'";
		}
		if (StringUtils.isNotBlank(states) && states.equals("D")) {
			hql += " and a.states = 'D'";
		}
		List<String> list = productBatchDao.find(hql, new Parameter(productId));
		return list.size();
	}

	// 产品负责人只看自己的
	public Integer getBatchCount(String officeId, String productId, String states, User user) {
		String hql = "select a.id from ProductBatch a where a.productId='" + productId + "' ";
		if (StringUtils.isNotBlank(states) && states.equals("A")) {
			hql += " and a.states <> 'D'";
		}
		if (StringUtils.isNotBlank(states) && states.equals("D")) {
			hql += " and a.states = 'D'";
		}
		// 产品负责人过滤数据
		if (user.getRoleList() != null) {
			List<Role> roleList = user.getRoleList();
			if (roleList.size() == 1) {
				Role role = roleList.get(0);
				// 只能看自己的数据的角色
				if (role.getDataScope().equals("8")) {
					hql += " and a.creatUserid = '" + user.getId() + "'";
				}
			}

		}
		List<String> list = productBatchDao.find(hql, null);
		return list.size();
	}

	// 产品负责人只看自己的 tangjun
	public Integer newGetBatchCount(String officeId, String productId, String states, User user) {
		String hql = "select a.id from t_trace_product_batch a where a.sys_id = '0' and a.product_id='" + productId
				+ "' ";
		if (StringUtils.isNotBlank(states) && states.equals("A")) {
			hql += " and a.states <> 'D' ";
		}
		if (StringUtils.isNotBlank(states) && states.equals("D")) {
			hql += " and a.states = 'D' ";
		}
		// 产品负责人过滤数据
		if (user.getRoleList() != null) {
			List<Role> roleList = user.getRoleList();
			if (roleList.size() == 1) {
				Role role = roleList.get(0);
				// 只能看自己的数据的角色
				if (role.getDataScope().equals("8")) {
					hql += " and a.creatUserid = '" + user.getId() + "'";
				}
			}
		}
		hql += " union ";
		hql += "select a.id from t_trace_product_batch a where (a.sys_id='2' or a.sys_id='1') and a.product_id='" + productId + "' ";
		if (StringUtils.isNotBlank(states) && states.equals("A")) {
			hql += " and a.states <> 'D' ";
		}
		if (StringUtils.isNotBlank(states) && states.equals("D")) {
			hql += " and a.states = 'D' ";
		}
		// 产品负责人过滤数据
		if (user.getRoleList() != null) {
			List<Role> roleList = user.getRoleList();
			if (roleList.size() == 1) {
				Role role = roleList.get(0);
				// 只能看自己的数据的角色
				if (role.getDataScope().equals("8")) {
					hql += " and a.creatUserid = '" + user.getId() + "' ";
				}
			}
		}
		String findByState = traceProductAuditDao.findByState("1");
		hql += " and a.id in (" + findByState + ") ";
		List<String> list = productBatchDao.findBySql(hql);
		return list.size();
	}

	@Transactional(readOnly = false)
	public void saveTracePropertyNewByTraceProperty(TracePropertyNew traceProperty, String officeId, User user) {
		String hql = "from TracePropertyNew a where a.id=:p1 and a.states<>'D'";
		List<TracePropertyNew> list = tracePropertyNewDao.find(hql, new Parameter(traceProperty.getId()));
		if (list == null || list.size() == 0) {
			TracePropertyNew tracePropertyNew = new TracePropertyNew();
			tracePropertyNew.setId(traceProperty.getId());
			tracePropertyNew.setOfficeId(officeId);
			tracePropertyNew.setModelId(traceProperty.getModelId());
			tracePropertyNew.setPropertyNameZh(traceProperty.getPropertyNameZh());
			tracePropertyNew.setPropertyNameEn(traceProperty.getPropertyNameEn());
			tracePropertyNew.setPropertyType(traceProperty.getPropertyType());
			tracePropertyNew.setTimeFlag("0");
			tracePropertyNew.setStatus("1");
			tracePropertyNew.setCreatTime(new Date());
			tracePropertyNew.setCreatUserid(user.getId());
			tracePropertyNew.setStates(TracePropertyNew.STATE_FLAG_ADD);
			tracePropertyNew.setSort(traceProperty.getSort());
			tracePropertyNewDao.save(tracePropertyNew);
		}
	}

	public ProductBatch getBatchByCode(String officeId, String batchCode, User user, String productId, String sysId) {
		String hql = "from ProductBatch a where a.states<>'D' and a.batchCode=:p1 and a.officeId=:p2 and a.productId=:p3";
		List<ProductBatch> list = tracePropertyNewDao.find(hql, new Parameter(batchCode, officeId, productId));
		if (list!=null && list.size() > 0) {
			return null;
		} else {
			ProductBatch batch = new ProductBatch();
			batch.setBatchCode(batchCode);
			batch.setOfficeId(officeId);
			batch.setProductId(productId);
			batch.setIsAudit("1");
			batch.setCreatUserid(user.getId());
			batch.setIsEnd("0");
			batch.setSysId(sysId);
			productBatchDao.save(batch);
			return batch;
		}
	}

	public ProductBatch getBatchByCode1(String officeId, String batchCode, User user, String productId) {
		String hql = "from ProductBatch a where a.states<>'D' and a.batchCode=:p1 and a.officeId=:p2 and a.productId=:p3";
		List<ProductBatch> list = tracePropertyNewDao.find(hql, new Parameter(batchCode, officeId, productId));
		if (list.size() > 0) {
			return list.get(0);
		} else {
			ProductBatch batch = new ProductBatch();
			batch.setBatchCode(batchCode);
			batch.setOfficeId(officeId);
			batch.setProductId(productId);
			batch.setIsAudit("1");
			batch.setCreatUserid(user.getId());
			batch.setIsEnd("0");
			productBatchDao.save(batch);
			return batch;
		}
	}

	public void saveToFile(String destUrl) {
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		HttpURLConnection httpUrl = null;
		URL url = null;
		int BUFFER_SIZE = 1024;
		byte[] buf = new byte[BUFFER_SIZE];
		int size = 0;
		try {
			url = new URL(destUrl);
			httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			fos = new FileOutputStream("c:\\haha.gif");
			while ((size = bis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
			fos.flush();
		} catch (Exception e) {

		} finally {
			try {
				fos.close();
				bis.close();
				httpUrl.disconnect();
			} catch (Exception e) {

			}
		}
	}

	public String imageUpload(HttpServletRequest request, String imgStr) {
		FileOutputStream fos = null;
		BufferedInputStream bis = null;
		HttpURLConnection httpUrl = null;
		URL url = null;
		String extName = "png";
		int BUFFER_SIZE = 1024;
		byte[] buf = new byte[BUFFER_SIZE];
		int size = 0;
		if (imgStr.contains(".")) {
			extName = imgStr.substring(imgStr.lastIndexOf("."));
		} else {
			return "";
		}
		try {
			String save_path = request.getSession().getServletContext().getRealPath("");
			String relative_path = "/upload/interface/" + DateUtils.getDate("yyyy-MM") + "/";
			String path = save_path.substring(0, save_path.lastIndexOf(File.separator)) + relative_path;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();// 如不存在路径则创建路径
			}
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String dateStr = sdf.format(new Date());
			path = path + dateStr + extName;
			relative_path = relative_path + dateStr + extName;
			url = new URL(imgStr);
			httpUrl = (HttpURLConnection) url.openConnection();
			httpUrl.connect();
			bis = new BufferedInputStream(httpUrl.getInputStream());
			fos = new FileOutputStream(path);
			while ((size = bis.read(buf)) != -1) {
				fos.write(buf, 0, size);
			}
			fos.flush();
			fos.close();
			bis.close();
			httpUrl.disconnect();
			return relative_path;
			/*
			 * InputStream imgStream = url.openStream(); String base64ImgStr =
			 * getImageStr(imgStream); String base64 = ""; String extName = "png";
			 * if(StringUtils.isNotBlank(base64ImgStr) ){
			 * if(base64ImgStr.indexOf("data:image") >= 0){ base64 =
			 * base64ImgStr.replace("data:image/jpeg;base64,", ""); }else{ base64 =
			 * base64ImgStr; } String save_path =
			 * request.getSession().getServletContext().getRealPath(""); String
			 * relative_path = "/upload/interface/"+DateUtils.getDate("yyyy-MM") + "/";
			 * String path = save_path.substring(0,save_path.lastIndexOf(File.separator)) +
			 * relative_path; File file = new File(path); if(!file.exists()){
			 * file.mkdirs();//如不存在路径则创建路径 } SimpleDateFormat sdf = new
			 * SimpleDateFormat("yyyyMMddHHmmssSSS"); String dateStr = sdf.format(new
			 * Date()); path = path + dateStr + "." + extName; relative_path = relative_path
			 * + dateStr + "." + extName; Boolean flag = base64ToImage.generateImage(base64,
			 * path); if(!flag) { return ""; } return relative_path; }else{ return ""; }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public String getImageStr(InputStream in) throws Exception {
		byte[] data = null;
		data = new byte[in.available()];
		in.read(data);
		in.close();
		Base64Encoder encoder = new Base64Encoder();
		return encoder.encode(data);
	}

	/**
	 * Title: getDataAudit Description: 数据审核刘表查询接口
	 * 
	 * @param pageno
	 *            页数
	 * @param pagesize
	 *            条数
	 * @param user
	 *            用户信息
	 * @return
	 */
	public Page<Map<String, String>> getDataAudit(Integer pageno, Integer pagesize, User user, String type) {
		int no = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int size = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;

		String ofStr = "";
		List<Office> findAllOfficesAndId = officeDao.findAllOfficesAndId(user.getCompany().getId(), user.getCompany().getId());
		for (Office office : findAllOfficesAndId) {
			ofStr += "'" + office.getId() + "'" + ",";
		}
		if (StringUtils.isNotBlank(ofStr)) {
			ofStr = ofStr.substring(0, ofStr.length() - 1);
		} else {
			ofStr = "''";
		}
		String idStr = "";
		// type 0: 未审核 1:通过 2：拒接 
		if("0".equals(type)) {
			List<TraceProductAudit> findAll = traceProductAuditDao.findAll();
			for (TraceProductAudit traceProductAudit : findAll) {
				idStr += "'" + traceProductAudit.getAuditId() + "'" + ",";
			}
			if (StringUtils.isNotBlank(idStr)) {
				idStr = idStr.substring(0, idStr.length() - 1);
			} else {
				idStr = "''";
			}
		}
		if("1".equals(type)) {
			List<TraceProductAudit> findAll = traceProductAuditDao.findByAuditState1("1");
			for (TraceProductAudit traceProductAudit : findAll) {
				idStr += "'" + traceProductAudit.getAuditId() + "'" + ",";
			}
			if (StringUtils.isNotBlank(idStr)) {
				idStr = idStr.substring(0, idStr.length() - 1);
			} else {
				idStr = "''";
			}
		}
		if ("2".equals(type)) {
			List<ProductBatch> findBy = productBatchDao.findBy("A");
			for (ProductBatch productBatch : findBy) {
				idStr += "'" + productBatch.getBatchCode() + "'" + ",";
			}
			if (StringUtils.isNotBlank(ofStr)) {
				idStr = idStr.substring(0, idStr.length() - 1);
			} else {
				idStr = "''";
			}
		}
		
		Page<Map<String, String>> pageList = traceLableApplyDao.pageList(no, size, ofStr, idStr, type);
		List<Map<String, String>> list = pageList.getList();
		for (Map<String, String> map : list) {
			String propertyDataId = map.get("propertyDataId");
			String officeId = map.get("officeId");
			String batchId = map.get("batchId");

			if (StringUtils.isNotBlank(propertyDataId) && StringUtils.isNotBlank(officeId) && StringUtils.isNotBlank(batchId)) {
				Map<String, String> param = traceLableApplyDao.getParam(propertyDataId, officeId, batchId);
				if (null != param) {
					map.put("propertyValue", param.get("propertyValue"));
					map.put("packTypeName", param.get("packTypeName"));
				}
			}
			
			if (StringUtils.isNotBlank(officeId) && StringUtils.isNotBlank(batchId)) {
				TraceCode traceCode = traceCodeDao.findByOfficeIdAndBatchId(officeId, batchId);
				String packTypeName = "";
				if (null != traceCode) {
					if ("1".equals(traceCode.getPackType())) {
						packTypeName = "底层标签";
					}
					if ("2".equals(traceCode.getPackType())) {
						packTypeName = "上级标签";
					}
					if ("3".equals(traceCode.getPackType())) {
						packTypeName = "上上级标签";
					}
				}
				map.put("packTypeName", packTypeName);
				List<Map<String, String>> paramList = traceLableApplyDao.getParam(batchId);
				for(int i=0;i<paramList.size();i++){
					Map<String, String> pojo = paramList.get(i);
					if("经办人".equals(pojo.get("propertyName"))||"负责人".equals(pojo.get("propertyName"))){
						map.put("name", pojo.get("propertyValue"));
					}
					if("联系电话".equals(pojo.get("propertyName"))){
						map.put("phone", pojo.get("propertyValue"));
					}
				}
			}
		}
		return pageList;
	}
	
	/**
	 * Title: getDataAudit Description: 数据审核刘表查询接口
	 * 
	 * @param pageno
	 *            页数
	 * @param pagesize
	 *            条数
	 * @param user
	 *            用户信息
	 * @return
	 */
	public Page<Map<String, String>> getDataAuditNew(Integer pageno, Integer pagesize, User user, String type) {
		int no = pageno == null ? Global.DEFAULT_PAGENO : pageno;
		int size = pagesize == null ? Global.DEFAULT_PAGESIZE : pagesize;

		String ofStr = "";
		List<Office> findAllOfficesAndId = officeDao.findAllOfficesAndId(user.getCompany().getId(), user.getCompany().getId());
		for (Office office : findAllOfficesAndId) {
			ofStr += "'" + office.getId() + "'" + ",";
		}
		if (StringUtils.isNotBlank(ofStr)) {
			ofStr = ofStr.substring(0, ofStr.length() - 1);
		} else {
			ofStr = "''";
		}
		
		Page<Map<String, String>> pageList = traceLableApplyDao.pageListNew(no, size, ofStr, type);
		List<Map<String, String>> list = pageList.getList();
		for (Map<String, String> map : list) {
			String propertyDataId = map.get("propertyDataId");
			String officeId = map.get("officeId");
			String batchId = map.get("batchId");
			
			//驳回理由
			ProductBatch batch = productBatchDao.get(batchId);
			if("3".equals(batch.getAuditStatus())){
				map.put("bhreason", traceProductAuditNewDao.getBhReason(batchId));
			}

			if (StringUtils.isNotBlank(propertyDataId)) {
				Map<String, String> param = traceLableApplyDao.getParamNew(propertyDataId);
				if (null != param) {
					map.put("propertyValue", param.get("propertyValue"));
				}
			}
			
			if (StringUtils.isNotBlank(officeId) && StringUtils.isNotBlank(batchId)) {
				List<Map<String, String>> paramList = traceLableApplyDao.getParam(batchId);
				for(int i=0;i<paramList.size();i++){
					Map<String, String> pojo = paramList.get(i);
					if("经办人".equals(pojo.get("propertyName"))||"负责人".equals(pojo.get("propertyName"))){
						map.put("name", pojo.get("propertyValue"));
					}
					if("联系电话".equals(pojo.get("propertyName"))){
						map.put("phone", pojo.get("propertyValue"));
					}
				}
			}
		}
		return pageList;
	}

	/**
	 * Title: getModelGrowPropertiesInformation Description: 根据批次ID获取生长记录模块数据信息
	 * 
	 * @param batchId
	 *            批次id
	 * @return
	 */
	public List<TraceModelDataGroup> getModelGrowPropertiesInformation(String batchId) {
		List<TraceModelDataGroup> modelDataGroupListAft = new ArrayList<TraceModelDataGroup>();
		// 查找生长记录模块
		TraceModelData modelDataGrow = traceModelService.getTraceModelDataGrowByBatchId(batchId);
		if (null != modelDataGrow) {
			// 读取属性和属性值 封装到属性对象中
			List<TraceModelDataGroup> modelDataGroupList = traceModelService.find(modelDataGrow.getId());
			for (Iterator<TraceModelDataGroup> iterator = modelDataGroupList.iterator(); iterator.hasNext();) {
				TraceModelDataGroup traceModelDataGroup = (TraceModelDataGroup) iterator.next();

				List<TraceProperty> tracePropertyList = new ArrayList<TraceProperty>();// 模块对应属性 包含值
				List<TracePropertyData> propertyDataList = tracePropertyService.findPagePropertyDataListByModelDataGroupId(traceModelDataGroup.getId());
				for (Iterator<TracePropertyData> iterator2 = propertyDataList.iterator(); iterator2.hasNext();) {
					TracePropertyData tracePropertyData = (TracePropertyData) iterator2.next();
					TraceProperty property = tracePropertyService.get(tracePropertyData.getPropertyId());
					property = tracePropertyService.evict(property);
					if (property != null) {
						property.setPropertyValue(tracePropertyData.getPropertyValue());
						// 可能需要返回删除或修改对应某条生长记录值
						property.setPropertyDataId(tracePropertyData.getId());
						tracePropertyList.add(property);
					}
				}
				tracePropertyService.ListSortArrayList(tracePropertyList);
				traceModelDataGroup.setTracePropertyList(tracePropertyList);
				modelDataGroupListAft.add(traceModelDataGroup);
			}
		}
		return modelDataGroupListAft;
	}

}
