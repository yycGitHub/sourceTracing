package com.surekam.modules.tracemodel.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.tracemodel.dao.TraceModelDao;
import com.surekam.modules.tracemodel.dao.TraceModelDataGroupDao;
import com.surekam.modules.tracemodel.dao.TraceModelDatalDao;
import com.surekam.modules.tracemodel.dao.TracePropertyModelDao;
import com.surekam.modules.tracemodel.entity.TraceModel;
import com.surekam.modules.tracemodel.entity.TraceModelData;
import com.surekam.modules.tracemodel.entity.TraceModelDataGroup;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproperty.dao.TracePropertyDao;
import com.surekam.modules.traceproperty.dao.TracePropertyDataDao;
import com.surekam.modules.traceproperty.entity.TraceProperty;
import com.surekam.modules.traceproperty.entity.TracePropertyData;

/**
 * 溯源模块配置管理Service
 * @author liw
 * @version 2018-09-07
 */
@Component
@Transactional(readOnly = true)
public class TraceModelService extends BaseService {

	@Autowired
	private TraceModelDao traceModelDao;
	
	@Autowired
	private TraceModelDatalDao traceModelDatalDao;
	
	@Autowired
	private TraceModelDataGroupDao traceModelDataGroupDao;
	
	@Autowired
	private TracePropertyDataDao tracePropertyDataDao;
	
	@Autowired
	private TracePropertyModelDao tracePropertyModelDao;
	
	@Autowired
	private TracePropertyDao tracePropertyDao;
	
	public TraceModel get(String id) {
		return traceModelDao.get(id);
	}
	
	public TraceModelData getTraceModelData(String id) {
		return traceModelDatalDao.get(id);
	}
	
	public Page<TraceModel> find(Page<TraceModel> page, TraceModel traceModel) {
		DetachedCriteria dc = traceModelDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceModel.FIELD_DEL_FLAG, TraceModel.DEL_FLAG_NORMAL));
		return traceModelDao.find(page, dc);
	}
	
	public Page<TraceModel> find(Page<TraceModel> page, String modelName, String modelCode, String status, String productId,String officeId) {
		DetachedCriteria dc = traceModelDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceModel.FIELD_DEL_FLAG_XGXT, TraceModel.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(modelName)){
			dc.add(Restrictions.like("modelName", modelName, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotEmpty(modelCode)){
			dc.add(Restrictions.like("modelCode", modelCode));
		}
		if(StringUtils.isNotEmpty(status)){
			dc.add(Restrictions.eq("status", status));
		}
		if(StringUtils.isNotEmpty(productId)) {
			dc.add(Restrictions.eq("productId", productId));
		}
		if(StringUtils.isNotEmpty(officeId)){
			dc.add(Restrictions.eq("officeId", officeId));
		}
		dc.addOrder(Order.asc("sort"));
		return traceModelDao.find(page, dc);
	}
	
	/**
	 * 根据模块名称、productId查询模块
	 * @param modelName
	 * @param productId
	 * @return
	 */
	public TraceModel find(String modelName, String productId) {
		DetachedCriteria dc = traceModelDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceModel.FIELD_DEL_FLAG_XGXT, TraceModel.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(modelName)){
			dc.add(Restrictions.eq("modelName", modelName));
		}
		if(StringUtils.isNotEmpty(productId)){
			dc.add(Restrictions.eq("productId", productId));
		}
		List<TraceModel> list = traceModelDao.find(dc);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	@Transactional(readOnly = false)
	public void save(TraceModel traceModel) {
		traceModelDao.save(traceModel);
		//保存模块对应属性
		List<TraceProperty> lists = Lists.newArrayList();
		List<TraceProperty> tracePropertyList = traceModel.getTracePropertyList();
		for(int i=0;i<tracePropertyList.size();i++) {
			TraceProperty property = tracePropertyList.get(i);
			TraceProperty traceProperty = new TraceProperty();
			traceProperty.setOfficeId(traceModel.getOfficeId());
			traceProperty.setPropertyCode("");
			traceProperty.setModelId(traceModel.getId());
			traceProperty.setPropertyNameEn(property.getPropertyNameEn());
			traceProperty.setSort(property.getSort());
			traceProperty.setPropertyNameZh(property.getPropertyNameZh());
			traceProperty.setPropertyType(property.getPropertyType());
			traceProperty.setCreatUserid(traceModel.getUserid());
			traceProperty.setCreatTime(new Date());
			traceProperty.setStatus(TraceProperty.OPEN);
			lists.add(traceProperty);		
		}
		tracePropertyDao.save(lists);
	}
	
	@SuppressWarnings("unused")
	@Transactional(readOnly = false)
	public void update(TraceModel model) {
		//修改数据之前先删除原来关联的属性	
		int count = tracePropertyDao.deleteByModelId(model.getId());
		
		if(StringUtils.isNotBlank(model.getId())) {
			model.setStates("U");
		}
		traceModelDao.save(model);
		
		List<TraceProperty> lists = Lists.newArrayList();
		List<TraceProperty> tracePropertyList = model.getTracePropertyList();
		for (int i=0;i<tracePropertyList.size();i++) {
			TraceProperty property = tracePropertyList.get(i);
			TraceProperty traceProperty = new TraceProperty();
			traceProperty.setOfficeId(model.getOfficeId());
			traceProperty.setPropertyCode("");
			traceProperty.setModelId(model.getId());
			traceProperty.setPropertyNameEn(property.getPropertyNameEn());
			traceProperty.setSort(property.getSort());
			traceProperty.setPropertyNameZh(property.getPropertyNameZh());
			traceProperty.setPropertyType(property.getPropertyType());
			traceProperty.setStatus(TraceProperty.OPEN);
			traceProperty.setCreatUserid(model.getUserid());
			traceProperty.setUpdateUserid(model.getUserid());
			traceProperty.setUpdateTime(new Date());
			lists.add(traceProperty);
			
		}
		tracePropertyDao.save(lists);
	}
	
	@Transactional(readOnly = false)
	public void enable(String id) {
		TraceModel model = traceModelDao.get(id);
		model.setStatus(TraceModel.OPEN);
		model.setUpdateTime(new Date());
		model.setUpdateUserid(UserUtils.getUser().getId());
		traceModelDao.save(model);
	}
	
	@Transactional(readOnly = false)
	public void disable(String id) {
		TraceModel model = traceModelDao.get(id);
		model.setUpdateTime(new Date());
		model.setUpdateUserid(UserUtils.getUser().getId());
		model.setStatus(TraceModel.CLOSE);
		traceModelDao.save(model);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		TraceModel model = traceModelDao.get(id);
		model.setUpdateTime(new Date());
		model.setUpdateUserid(UserUtils.getUser().getId());
		/*//在使用的模块不能删除
		if(model != null && "1".equals(model.getStatus())) {
			return "1";
		}*/
		traceModelDao.save(model);
		traceModelDao.deleteByXGXTId(id);
		/*return "0";*/
	}
	
	public String getMaxModelCode(String type){
		List<String> list = traceModelDao.find("select max(t.modelCode) from TraceModel t where t.states!='D' and t.modelCode like '"+type+"%'");
		if(list.size()>0){
			return list.get(0);
		}else{
			return "";
		}
	}
	
	/**
	 * 保存model数据分组对象
	 * @param traceModelDataGroup
	 */
	@Transactional(readOnly = false)
	public void saveModelDataGroup(TraceModelDataGroup traceModelDataGroup) {
		traceModelDataGroupDao.save(traceModelDataGroup);
	}
	
	/**
	 * 根据modelDataId获取对应的分组分页数据
	 * @param page
	 * @param modelDataId
	 * @return
	 */
	public Page<TraceModelDataGroup> find(Page<TraceModelDataGroup> page, String modelDataId) {
		DetachedCriteria dc = traceModelDataGroupDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceModelDataGroup.FIELD_DEL_FLAG_XGXT, TraceModelDataGroup.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(modelDataId)){
			dc.add(Restrictions.eq("modelDataId", modelDataId));
		}
		dc.addOrder(Order.asc("creatTime"));
		return traceModelDataGroupDao.find(page, dc);
	}
	
	/**
	 * 根据modelDataId获取对应的数据
	 * @param page
	 * @param modelDataId
	 * @return
	 */
	public List<TraceModelDataGroup> find(String modelDataId) {
		DetachedCriteria dc = traceModelDataGroupDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceModelDataGroup.FIELD_DEL_FLAG_XGXT, TraceModelDataGroup.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(modelDataId)){
			dc.add(Restrictions.eq("modelDataId", modelDataId));
		}
		dc.addOrder(Order.asc("creatTime"));
		return traceModelDataGroupDao.find(dc);
	}
	
	/**
	 * 根据modelDataId获取对应的数据
	 * @param page
	 * @param modelDataId
	 * @return
	 */
	public List<TraceModelDataGroup> findTraceModelDataGroupByModelDataId(String modelDataId) {
		String sql = "SELECT a.* FROM t_trace_model_data_group a, t_trace_property_data b "
			+" WHERE a.model_data_id = :p1 AND a.id = b.model_data_group_id"
			+"  AND b.property_id = '11' ORDER BY b.sort";
		return traceModelDataGroupDao.findBySql(sql, new Parameter(modelDataId), TraceModelDataGroup.class );
	}
	
	/**
	 * 根据modelDataId获取对应的数据
	 * @param page
	 * @param modelDataId
	 * @return
	 */
	public List<String> findNew(String modelDataId) {
		String sql = "SELECT t.id FROM t_trace_model_data_group t, t_trace_property_data a "
				+" WHERE t.id = a.model_data_group_id AND a.states <> 'D' AND t.states <> 'D' "
                +" AND t.model_data_id = :p1 GROUP BY t.id ORDER BY MIN(a.sort) ";
		return traceModelDataGroupDao.findBySql(sql, new Parameter(modelDataId));
	}

	/**
	 * 根据modelDataId获取对应的数据未通过
	 * @param page
	 * @param modelDataId
	 * @return
	 */
	public List<String> findNewFail(String modelDataId) {
		String sql = "SELECT t.id FROM t_trace_model_data_group t, t_trace_property_data a "
				+" WHERE t.id = a.model_data_group_id "
                +" AND t.model_data_id = :p1 GROUP BY t.id ORDER BY MIN(a.sort) ";
		return traceModelDataGroupDao.findBySql(sql, new Parameter(modelDataId));
	}
	/**
	 * 获取模块数据list
	 * */
	public List<TraceModelData> findTraceModelDatas(String officeId,String batchId,String flag){
		DetachedCriteria dc = traceModelDatalDao.createDetachedCriteria();
//		if(flag.equals("2")){
//			dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
//		}
		if(StringUtils.isNotBlank(officeId)){
			dc.add(Restrictions.eq("officeId", officeId));
		}
		if(StringUtils.isNotBlank(batchId)){
			dc.add(Restrictions.eq("batchId", batchId));
		}
		dc.addOrder(Order.asc("sort"));
		return traceModelDatalDao.find(dc);
	}
	
	/**
	 * 根据产品ID获取模块list
	 * */
	public List<TraceModel> findTraceModelsByProductId(String productId){
		DetachedCriteria dc = traceModelDao.createDetachedCriteria();
		//dc.add(Restrictions.ne(TraceModel.FIELD_DEL_FLAG_XGXT, TraceModel.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(productId)){
			dc.add(Restrictions.eq("productId", productId));
		}
		dc.addOrder(Order.asc("sort"));
		return traceModelDao.find(dc);
	}
	
	/**
	 * 根据某个modelId获取对应产品配置的所有modelId list
	 * */
	public List<TraceModel> findProductTraceModelsByOneModelId(String modelId){
		String hql = "from TraceModel model where model.productId in("
				+ " select model1.productId from TraceModel model1 where model1.id='" + modelId + "'"
				+ ")";
		
		return traceModelDao.find(hql);
	}
	
	/**
	 * 通过modelId获取唯一有效的TraceModelData 用于获取生长记录模块数据
	 * */
	public TraceModelData findTraceModelDataByModelIdAndBatchId(String modelId, String batchId){
		DetachedCriteria dc = traceModelDatalDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(modelId)){
			dc.add(Restrictions.eq("modelId", modelId));
		}
		if(StringUtils.isNotBlank(batchId)){
			dc.add(Restrictions.eq("batchId", batchId));
		}
		List<TraceModelData> modelDataList = traceModelDatalDao.find(dc);
		if(null != modelDataList && 0 != modelDataList.size()){
			return modelDataList.get(0);
		}
		return null;
	}
	
	/**
	 * 通过modelId获取唯一有效的TraceModelData 用于获取生长记录模块数据
	 * */
	public TraceModelData findTraceModelDataAndBatchId(String modelName, String batchId){
		DetachedCriteria dc = traceModelDatalDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(modelName)){
			dc.add(Restrictions.eq("modelName", modelName));
		}
		if(StringUtils.isNotBlank(batchId)){
			dc.add(Restrictions.eq("batchId", batchId));
		}
		List<TraceModelData> modelDataList = traceModelDatalDao.find(dc);
		if(null != modelDataList && 0 != modelDataList.size()){
			return modelDataList.get(0);
		}
		return null;
	}

	/**
	 * 通过modelId获取唯一有效的TraceModelData 用于获取生长记录模块数据未通过
	 * */
	public TraceModelData findTraceModelDataAndBatchIdFail(String modelName, String batchId){
		DetachedCriteria dc = traceModelDatalDao.createDetachedCriteria();
		if(StringUtils.isNotBlank(modelName)){
			dc.add(Restrictions.eq("modelName", modelName));
		}
		if(StringUtils.isNotBlank(batchId)){
			dc.add(Restrictions.eq("batchId", batchId));
		}
		List<TraceModelData> modelDataList = traceModelDatalDao.find(dc);
		if(null != modelDataList && 0 != modelDataList.size()){
			return modelDataList.get(0);
		}
		return null;
	}
	/**
	 * 通过batchId获取唯一可能存在的有效的生长记录TraceModelData
	 * */
	public TraceModelData getTraceModelDataGrowByBatchId(String batchId){
		String hql = "from TraceModelData modelData "
				//TODO model_data表中生长过程总是经常变成D状态。故注释状态验证 
				+ " where " // modelData.states !='" + TraceModelData.STATE_FLAG_DEL + "' and
				+ "  modelData.batchId ='" + batchId + "'"
				+ " and modelData.modelId in"
				+ " (select model.id from TraceModel model where model.id = modelData.modelId "
				+ " 	and model.modelShowType = '2')"
				+ " order by modelData.sort asc";
		
		List<TraceModelData> modelDataList = traceModelDatalDao.find(hql);
		if(null != modelDataList && 0 != modelDataList.size()){
			return modelDataList.get(0);
		}
		return null;
	}
	
	/**
	 * 通过batchId获取唯一可能存在的有效的生长记录TraceModelData
	 * */
	public TraceModelData getTraceModelDataGrowByBatchId_new(String batchId, String modelId){
		String hql = "from TraceModelData modelData where modelData.states<>'D' and modelData.batchId ='" + batchId + "' and modelData.modelId ='" + modelId + "'";
		List<TraceModelData> modelDataList = traceModelDatalDao.find(hql);
		if(null != modelDataList && 0 != modelDataList.size())
			return modelDataList.get(0);
		return null;
	}
	
	/**
	 * 根据批次ID保存生长记录数据
	 * @param traceModelDataGroup
	 */
	@Transactional(readOnly = false)
	public void savePropertyDataWithTraceModelDataGroup(TraceModelDataGroup traceModelDataGroup,User user) {
		if ("0".equals(traceModelDataGroup.getIsNew())) {
			TraceModelDataGroup traceModelDataGroupSave = new TraceModelDataGroup();
			traceModelDataGroupSave.setModelDataId(traceModelDataGroup.getModelDataId());
			traceModelDataGroupSave.setCreatTime(new Date());
			traceModelDataGroupSave.setCreatUserid(user.getId());
			traceModelDataGroupSave.setStates(TraceModelDataGroup.STATE_FLAG_ADD);
			traceModelDataGroupDao.save(traceModelDataGroupSave);

			// 保存对应的属性值数据
			List<TraceProperty> tracePropertyList = traceModelDataGroup.getTracePropertyList();
			int Counter = 1;// 计数器--sort排序
			for (Iterator<TraceProperty> iterator = tracePropertyList.iterator(); iterator.hasNext();) {
				TraceProperty traceProperty = (TraceProperty) iterator.next();
//				if (StringUtils.isNotBlank(traceProperty.getPropertyValue())) {
					TracePropertyData tracePropertyData = new TracePropertyData();
					TraceModelData modelData = traceModelDatalDao.get(traceModelDataGroup.getModelDataId());
					tracePropertyData.setBatchId(modelData.getBatchId());
					tracePropertyData.setModelDataGroupId(traceModelDataGroupSave.getId());
					tracePropertyData.setModelDataId(traceModelDataGroup.getModelDataId());
					tracePropertyData.setPropertyId(traceProperty.getId());
					tracePropertyData.setPropertyValue(traceProperty.getPropertyValue());
					tracePropertyData.setStates(TracePropertyData.STATE_FLAG_ADD);
					tracePropertyData.setCreatTime(new Date());
					tracePropertyData.setCreatUserid(user.getId());
					tracePropertyData.setSort(Counter);
					tracePropertyDataDao.save(tracePropertyData);
					Counter++;
//				}
			}
		} else if ("1".equals(traceModelDataGroup.getIsNew())) {
			for (int i = 0; i < traceModelDataGroup.getTracePropertyList().size(); i++) {
				String id = traceModelDataGroup.getTracePropertyList().get(i).getId();
				if(StringUtils.isNotBlank(id)) {
					TracePropertyData tracePropertyData = tracePropertyDataDao.get(id);
					tracePropertyData.setPropertyValue(traceModelDataGroup.getTracePropertyList().get(i).getPropertyValue());
					tracePropertyData.setCreatUserid(user.getId());
					tracePropertyData.setUpdateTime(new Date());
					tracePropertyDataDao.save(tracePropertyData);
				}
			}
		}
	}
	
	/**
	 * 逻辑删除模块数据，包括对应的模块属性数据
	 * @param batchId
	 */
	@Transactional(readOnly = false)
	public String deleteModelDataWithPropertyDatas(String modelDataId, User user) {
		TraceModelData modelData = traceModelDatalDao.get(modelDataId);
		//删除批次对应的的模块属性数据
		String hqlDeletePropertyData = "update TracePropertyData propertyData set propertyData.states = '" + TracePropertyData.STATE_FLAG_DEL + "' "
				+ " where propertyData.modelDataId ='" + modelDataId + "'";
		
		tracePropertyDataDao.update(hqlDeletePropertyData);
		//删除批次数据
		modelData.setUpdateTime(new Date());
		if(null != user){
			modelData.setUpdateUserid(user.getId());
		}
		modelData.setStates(TraceModelData.STATE_FLAG_DEL);
		traceModelDatalDao.save(modelData);
		return "0";
	}
	
	/**
	 * 获取某组生长日记数据
	 * 
	 * @param groupId
	 * @return
	 */
	@Transactional(readOnly = false)
	public List<TracePropertyData> getPropertyDataByGroupId(String groupId) {
		List<TracePropertyData> resultList = new ArrayList<TracePropertyData>();
		List<TracePropertyData> list = tracePropertyDataDao.find(" from TracePropertyData a where a.modelDataGroupId=:p1 and a.states<>'D'", new Parameter(groupId));
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				TracePropertyData tracePropertyData = list.get(i);
				TraceProperty traceProperty = tracePropertyDao.get(tracePropertyData.getPropertyId());
				tracePropertyData.setDataType(traceProperty.getPropertyType());
				resultList.add(tracePropertyData);
			}
			return resultList;
		}
		return null;
	}
	
	/**
	 * 逻辑删除某组生长日记数据，包括对应的分组数据
	 * @param batchId
	 */
	@Transactional(readOnly = false)
	public String deletePropertyDataByGroupId(String groupId, User user) {
		TraceModelDataGroup modelDataGroup = traceModelDataGroupDao.get(groupId);
		//删除批次对应的的模块属性数据
		String hqlDeletePropertyData = "update TracePropertyData propertyData set propertyData.states = '" + TracePropertyData.STATE_FLAG_DEL + "' "
				+ " where propertyData.modelDataGroupId ='" + groupId + "'";
		
		tracePropertyDataDao.update(hqlDeletePropertyData);
		//删除分组数据
		modelDataGroup.setUpdateTime(new Date());
		if(null != user){
			modelDataGroup.setUpdateUserid(user.getId());
		}
		modelDataGroup.setStates(TraceModelDataGroup.STATE_FLAG_DEL);
		traceModelDataGroupDao.save(modelDataGroup);
		return "0";
	}
	
	/**
	 * 保存模块数据排序
	 * add by ligm 2018-10-11 新增或保存批次 模块 属性数据
	 * @param traceProduct
	 */
	@Transactional(readOnly = false)
	public String saveModelsWithSort(TraceProduct traceProduct, User user) {
		//保存模块和模块属性数据
		if(null != traceProduct.getTraceModelList() && 0 != traceProduct.getTraceModelList().size()){
			List<TraceModel> traceModelList = traceProduct.getTraceModelList();
			for (Iterator<TraceModel> iterator = traceModelList.iterator(); iterator
					.hasNext();) {
				TraceModel traceModel = (TraceModel) iterator.next();
				//检查是否为生长记录  不存在则保存 存在则不保存
				String modelDataId = traceModel.getModelDataId();
				String sort = traceModel.getSort();
				if(StringUtils.isNotBlank(modelDataId) && StringUtils.isNotBlank(sort)){
					TraceModelData modelData = traceModelDatalDao.get(modelDataId);
					modelData.setSort(sort);
					modelData.setUpdateTime(new Date());
					modelData.setUpdateUserid(user.getId());
					modelData.setStates(TraceModelData.STATE_FLAG_UPDATE);
					traceModelDatalDao.save(modelData);
				}else{
					//表示modelDataId或sort数据不完整
					return "1";
				}
				
			}
		}
		return "0";
	}
	
	public List<TraceModel> findModelByOfficeId(String OfficeId){
		DetachedCriteria dc = traceModelDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if (StringUtils.isNotBlank(OfficeId)){
			dc.add(Restrictions.eq("officeId", OfficeId));
		}
		dc.addOrder(Order.asc("creatTime"));
		return traceModelDao.find(dc);
		
	}

	
}
