package com.surekam.modules.traceproduct.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.config.Global;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.CommonEnum;
import com.surekam.modules.tracemodel.dao.TraceModelDataGroupDao;
import com.surekam.modules.tracemodel.entity.TraceModelData;
import com.surekam.modules.tracemodel.entity.TraceModelDataGroup;
import com.surekam.modules.tracemodel.service.TraceModelService;
import com.surekam.modules.traceproduct.dao.TraceProductDao;
import com.surekam.modules.traceproduct.entity.MainModelData;
import com.surekam.modules.traceproduct.entity.ModelData;
import com.surekam.modules.traceproduct.entity.Rows;
import com.surekam.modules.traceproduct.entity.TraceData;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.entity.TraceQueryData;
import com.surekam.modules.traceproduct.entity.TraceRootBean;
import com.surekam.modules.traceproperty.entity.TraceProperty;
import com.surekam.modules.traceproperty.entity.TracePropertyData;
import com.surekam.modules.traceproperty.service.TracePropertyService;

/**
 * 溯源产品管理Service
 * 
 * @author ligm
 * @version 2018-08-21
 */
@Component
@Transactional(readOnly = true)
public class TraceShowService extends BaseService {

	@Autowired
	private TraceProductDao traceProductDao;
	
	@Autowired
	private TraceModelService traceModelService;
	
	@Autowired
	private TracePropertyService tracePropertyService;
	
	@Autowired
	private TraceModelDataGroupDao traceModelDataGroupDao;
	
	public TraceProduct get(String id) {
		return traceProductDao.get(id);
	}

	/**
	 * @param officeId
	 * @param productId
	 * @param batchId
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public TraceRootBean findTraceData(String productId, String batchId, int count) throws Exception {
		TraceRootBean traceRootBean = new TraceRootBean();
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT ").
				append(" t2.model_data_id,")
				.append(" t2.property_value, ")
				.append(" t1.office_id,")
				.append(" t1.batch_id,")
				.append(" t1.model_id,")
				.append(" t1.model_name,")
				.append(" (CASE WHEN (model_data_group_id IS NULL OR model_data_group_id = '') THEN '1' ELSE '2' END) model_show_type,")
				.append(" (CASE t1.model_name WHEN '主模块' THEN 1 ELSE 0 END) is_main_model,")
				.append(" (SELECT ")
				.append(" t4.property_name_zh ")
				.append(" FROM")
				.append(" t_trace_property_new t4 ")
				.append(" WHERE t4.id = t2.`property_id`) property_name_zh,")
				.append(" (SELECT ")
				.append(" t4.property_name_en ")
				.append(" FROM")
				.append(" t_trace_property_new t4 ")
				.append(" WHERE t4.id = t2.`property_id`) property_name_en,")
				.append(" (SELECT ")
				.append(" t4.property_type ")
				.append(" FROM")
				.append(" t_trace_property_new t4 ")
				.append(" WHERE t4.id = t2.`property_id`) property_type ")
				.append(" FROM")
				.append(" t_trace_property_data t2,")
				.append(" t_trace_model_data t1")
				.append(" WHERE t1.batch_id =:batchId")
				.append(" AND t2.model_data_id = t1.id")
				.append(" AND t1.`states`<>'D'")
				.append(" AND t2.`states`<>'D'")
				.append(" ORDER BY t1.`sort`,t2.`sort` ASC");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Parameter param = new Parameter();
		param.put("batchId", batchId);
		list = traceProductDao.findBySql(sqlBuf.toString(), param, Map.class);
		if (list == null || list.size() == 0) {
			return traceRootBean;
		}
		List<TraceQueryData> mainReslist = new ArrayList<TraceQueryData>();
		List<TraceQueryData> reslist = new ArrayList<TraceQueryData>();
		mapToList(list, mainReslist, reslist);

		// 主模块数据对象
		MainModelData mainModelData = new MainModelData();
		for (TraceQueryData data : mainReslist) {
			if (StringUtils.isNotBlank(data.getPropertyNameZh())) {
				if (data.getPropertyNameZh().equals("生产日期")) {
					mainModelData.setDateOfManufacture(data.getPropertyValue());
				}
				if (data.getPropertyNameZh().indexOf("保质期") > -1) {
					mainModelData.setQualityGuaranteePeriod(data.getPropertyValue());
				}
			}

		}

		mainModelData.setQueryCount(String.valueOf(count));
		Map<String, List<TraceQueryData>> resultMap = this.groupByModelId(reslist);

		List<ModelData> modelDataList = new ArrayList<ModelData>();
		for (String in : resultMap.keySet()) {
			// map.keySet()返回的是所有key的值
			List<TraceQueryData> tmpList = (List<TraceQueryData>) resultMap.get(in);
			List<TraceData> traceDataList = new ArrayList<TraceData>();
			int total = 0;
			int modelTotal = 0;
			String modelName = "";
			String modelShowType = "";
			for (TraceQueryData traceQueryData : tmpList) {
				TraceData tdata = new TraceData();
				tdata.setFieldName(traceQueryData.getPropertyNameZh());
				tdata.setFieldValue(traceQueryData.getPropertyNameEn());
				tdata.setFieldType(traceQueryData.getPropertyType());
				tdata.setFieldData(traceQueryData.getPropertyValue());
				total++;
				traceDataList.add(tdata);
				modelName = traceQueryData.getModelName();
				modelShowType = traceQueryData.getModelShowType();
			}
			Rows rows = new Rows();
			rows.setTotal(total);
			rows.setTraceData(traceDataList);
			List<Rows> rowslist = new ArrayList<Rows>();
			rowslist.add(rows);
			ModelData modelData = new ModelData();
			modelData.setRows(rowslist);
			modelTotal++;
			modelData.setTotal(modelTotal);
			modelData.setModelName(modelName);
			modelData.setModelShowType(modelShowType);
			
			if("2".equals(modelShowType)){
				List<TraceModelDataGroup> groupList = new ArrayList<TraceModelDataGroup>();
				TraceModelData modelDataGrow = traceModelService.findTraceModelDataAndBatchId(modelName, batchId);
				if (null != modelDataGrow) {
					// 读取属性和属性值 封装到属性对象中
					List<String> modelDataGroupList = traceModelService.findNew(modelDataGrow.getId());
					for (int i=0;i<modelDataGroupList.size();i++) {
						String id = modelDataGroupList.get(i);
						TraceModelDataGroup traceModelDataGroup = traceModelDataGroupDao.get(id);
						List<TraceProperty> tracePropertyList = new ArrayList<TraceProperty>();// 模块对应属性 包含值
						List<TracePropertyData> propertyDataList = tracePropertyService.findPagePropertyDataListByModelDataGroupId(id);
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
						traceModelDataGroup.setTracePropertyList(tracePropertyList);
						groupList.add(traceModelDataGroup);
					}
					modelData.setGroupList(groupList);
				}
			}
			modelDataList.add(modelData);
		}
		traceRootBean.setImg("");
		traceRootBean.setMainModelData(mainModelData);
		traceRootBean.setModelData(modelDataList);
		return traceRootBean;
	}
	
	/**
	 * @param officeId
	 * @param productId
	 * @param batchId
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public TraceRootBean findTraceDataReqDel(String productId, String batchId, int count) throws Exception {
		TraceRootBean traceRootBean = new TraceRootBean();
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT ").
				append(" t2.model_data_id,")
				.append(" t2.property_value, ")
				.append(" t1.office_id,")
				.append(" t1.batch_id,")
				.append(" t1.model_id,")
				.append(" t1.model_name,")
				.append(" t3.model_show_type,")
				.append(" t3.is_main_model,")
				.append(" (SELECT ")
				.append(" t4.property_name_zh ")
				.append(" FROM")
				.append(" t_trace_property_new t4 ")
				.append(" WHERE t4.id = t2.`property_id`) property_name_zh,")
				.append(" (SELECT ")
				.append(" t4.property_name_en ")
				.append(" FROM")
				.append(" t_trace_property_new t4 ")
				.append(" WHERE t4.id = t2.`property_id`) property_name_en,")
				.append(" (SELECT ")
				.append(" t4.property_type ")
				.append(" FROM")
				.append(" t_trace_property_new t4 ")
				.append(" WHERE t4.id = t2.`property_id`) property_type ")
				.append(" FROM")
				.append(" t_trace_property_data t2,")
				.append(" t_trace_model_data t1,")
				.append(" t_trace_model t3")
				.append(" WHERE t1.batch_id =:batchId")
				.append(" AND t2.model_data_id = t1.id")
				.append(" AND t1.`model_id` = t3.`id`")
				.append(" AND t1.`states` = 'D'")
				.append(" AND t2.`states` = 'D'")
				.append(" AND t3.`states` = 'D'")
				.append(" ORDER BY t3.`sort`,t2.`sort` ASC");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Parameter param = new Parameter();
		param.put("batchId", batchId);
		list = traceProductDao.findBySql(sqlBuf.toString(), param, Map.class);
		if (list == null || list.size() == 0) {
			return traceRootBean;
		}
		List<TraceQueryData> mainReslist = new ArrayList<TraceQueryData>();
		List<TraceQueryData> reslist = new ArrayList<TraceQueryData>();
		mapToList(list, mainReslist, reslist);

		// 主模块数据对象
		MainModelData mainModelData = new MainModelData();
		for (TraceQueryData data : mainReslist) {
			/*
			 * if(data.getPropertyNameEn()!=null){
			 * if(data.getPropertyNameEn().equals(Global.TRACE_SHOW_PROPERTY_CODE_SFBH)){
			 * mainModelData.setIdentityCode(data.getPropertyValue()); }
			 * if(data.getPropertyNameEn().equals(Global.TRACE_SHOW_PROPERTY_CODE_SCRQ)){
			 * mainModelData.setDateOfManufacture(data.getPropertyValue()); }
			 * if(data.getPropertyNameEn().equals(Global.TRACE_SHOW_PROPERTY_CODE_BZQ)){
			 * mainModelData.setQualityGuaranteePeriod(data.getPropertyValue()); }
			 * if(data.getPropertyNameEn().equals(Global.TRACE_SHOW_PROPERTY_CODE_CXCS)){
			 * mainModelData.setQueryCount(data.getPropertyValue()); }
			 * if(data.getPropertyNameEn().equals(Global.TRACE_SHOW_PROPERTY_CODE_SCCD)){
			 * mainModelData.setProducePlace(data.getPropertyValue()); } }
			 */
			if (StringUtils.isNotBlank(data.getPropertyNameZh())) {
				if (data.getPropertyNameZh().equals("生产日期")) {
					mainModelData.setDateOfManufacture(data.getPropertyValue());
				}
				if (data.getPropertyNameZh().indexOf("保质期") > -1) {
					mainModelData.setQualityGuaranteePeriod(data.getPropertyValue());
				}
			}

		}

		mainModelData.setQueryCount(String.valueOf(count));
		Map<String, List<TraceQueryData>> resultMap = this.groupByModelId(reslist);

		List<ModelData> modelDataList = new ArrayList<ModelData>();
		for (String in : resultMap.keySet()) {
			// map.keySet()返回的是所有key的值
			List<TraceQueryData> tmpList = (List<TraceQueryData>) resultMap.get(in);
			List<TraceData> traceDataList = new ArrayList<TraceData>();
			int total = 0;
			int modelTotal = 0;
			String modelName = "";
			String modelShowType = "";
			for (TraceQueryData traceQueryData : tmpList) {
				TraceData tdata = new TraceData();
				tdata.setFieldName(traceQueryData.getPropertyNameZh());
				tdata.setFieldValue(traceQueryData.getPropertyNameEn());
				tdata.setFieldType(traceQueryData.getPropertyType());
				tdata.setFieldData(traceQueryData.getPropertyValue());
				total++;
				traceDataList.add(tdata);
				modelName = traceQueryData.getModelName();
				modelShowType = traceQueryData.getModelShowType();
			}
			Rows rows = new Rows();
			rows.setTotal(total);
			rows.setTraceData(traceDataList);
			List<Rows> rowslist = new ArrayList<Rows>();
			rowslist.add(rows);
			ModelData modelData = new ModelData();
			modelData.setRows(rowslist);
			modelTotal++;
			modelData.setTotal(modelTotal);
			modelData.setModelName(modelName);
			modelData.setModelShowType(modelShowType);
			modelDataList.add(modelData);
		}
		traceRootBean.setImg("");
		traceRootBean.setMainModelData(mainModelData);
		traceRootBean.setModelData(modelDataList);
		return traceRootBean;
	}
	
	
	/**
	 * @param officeId
	 * @param productId
	 * @param batchId
	 * @param count
	 * @return
	 * @throws Exception
	 */
	public TraceRootBean findDelTraceData(String productId, String batchId, int count) throws Exception {
		TraceRootBean traceRootBean = new TraceRootBean();
		StringBuffer sqlBuf = new StringBuffer();
		sqlBuf.append("SELECT ")
			.append(" t2.model_data_id,")
			.append(" t2.property_value, ")
			.append(" t1.office_id,")
			.append(" t1.batch_id,")
			.append(" t1.model_id,")
			.append(" t1.model_name,")
			.append(" t3.model_show_type,")
			.append(" t3.is_main_model,")
			.append(" (SELECT ")
			.append(" t4.property_name_zh ")
			.append(" FROM")
			.append(" t_trace_property_new t4 ")
			.append(" WHERE t4.id = t2.`property_id`) property_name_zh,")
			.append(" (SELECT ")
			.append(" t4.property_name_en ")
			.append(" FROM")
			.append(" t_trace_property_new t4 ")
			.append(" WHERE t4.id = t2.`property_id`) property_name_en,")
			.append(" (SELECT ")
			.append(" t4.property_type ")
			.append(" FROM")
			.append(" t_trace_property_new t4 ")
			.append(" WHERE t4.id = t2.`property_id`) property_type ")
			.append(" FROM")
			.append(" t_trace_property_data t2,")
			.append(" t_trace_model_data t1,")
			.append(" t_trace_model t3")
			.append(" WHERE t1.batch_id =:batchId")
			.append(" AND t2.model_data_id = t1.id")
			.append(" AND t1.`model_id` = t3.`id`")
			.append(" ORDER BY t1.`sort`,t2.`sort` ASC");
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Parameter param = new Parameter();
		param.put("batchId", batchId);
		list = traceProductDao.findBySql(sqlBuf.toString(), param, Map.class);
		if (list == null || list.size() == 0) {
			return traceRootBean;
		}
		List<TraceQueryData> mainReslist = new ArrayList<TraceQueryData>();
		List<TraceQueryData> reslist = new ArrayList<TraceQueryData>();
		mapToList(list, mainReslist, reslist);

		// 主模块数据对象
		MainModelData mainModelData = new MainModelData();
		for (TraceQueryData data : mainReslist) {
			if (StringUtils.isNotBlank(data.getPropertyNameZh())) {
				if (data.getPropertyNameZh().equals("生产日期")) {
					mainModelData.setDateOfManufacture(data.getPropertyValue());
				}
				if (data.getPropertyNameZh().indexOf("保质期") > -1) {
					mainModelData.setQualityGuaranteePeriod(data.getPropertyValue());
				}
			}
		}
		mainModelData.setQueryCount(String.valueOf(count));
		Map<String, List<TraceQueryData>> resultMap = this.groupByModelId(reslist);

		List<ModelData> modelDataList = new ArrayList<ModelData>();
		for (String in : resultMap.keySet()) {
			// map.keySet()返回的是所有key的值
			List<TraceQueryData> tmpList = (List<TraceQueryData>) resultMap.get(in);
			List<TraceData> traceDataList = new ArrayList<TraceData>();
			int total = 0;
			int modelTotal = 0;
			String modelName = "";
			String modelShowType = "";
			for (TraceQueryData traceQueryData : tmpList) {
				TraceData tdata = new TraceData();
				tdata.setFieldName(traceQueryData.getPropertyNameZh());
				tdata.setFieldValue(traceQueryData.getPropertyNameEn());
				tdata.setFieldType(traceQueryData.getPropertyType());
				tdata.setFieldData(traceQueryData.getPropertyValue());
				total++;
				traceDataList.add(tdata);
				modelName = traceQueryData.getModelName();
				modelShowType = traceQueryData.getModelShowType();
			}
			Rows rows = new Rows();
			rows.setTotal(total);
			rows.setTraceData(traceDataList);
			List<Rows> rowslist = new ArrayList<Rows>();
			rowslist.add(rows);
			ModelData modelData = new ModelData();
			modelData.setRows(rowslist);
			modelTotal++;
			modelData.setTotal(modelTotal);
			modelData.setModelName(modelName);
			modelData.setModelShowType(modelShowType);
			if("2".equals(modelShowType)){
				List<TraceModelDataGroup> groupList = new ArrayList<TraceModelDataGroup>();
				TraceModelData modelDataGrow = traceModelService.findTraceModelDataAndBatchIdFail(modelName, batchId);
				if (null != modelDataGrow) {
					// 读取属性和属性值 封装到属性对象中
					List<String> modelDataGroupList = traceModelService.findNewFail(modelDataGrow.getId());
					for (int i=0;i<modelDataGroupList.size();i++) {
						String id = modelDataGroupList.get(i);
						TraceModelDataGroup traceModelDataGroup = traceModelDataGroupDao.get(id);
						List<TraceProperty> tracePropertyList = new ArrayList<TraceProperty>();// 模块对应属性 包含值
						List<TracePropertyData> propertyDataList = tracePropertyService.findPagePropertyDataListByModelDataGroupIdFail(id);
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
						traceModelDataGroup.setTracePropertyList(tracePropertyList);
						groupList.add(traceModelDataGroup);
					}
					modelData.setGroupList(groupList);
				}
			}
			modelDataList.add(modelData);
		}
		traceRootBean.setImg("");
		traceRootBean.setMainModelData(mainModelData);
		traceRootBean.setModelData(modelDataList);
		return traceRootBean;
	}

	/**
	 * 通过批次ID查询对应的属性名称及属性值，不包括生产记录数据 add by ligm 2018-10-17
	 * 
	 * @param batchId
	 * @return
	 */
	public TraceRootBean findTraceDataByBatchId(String batchId) {
		TraceRootBean traceRootBean = new TraceRootBean();
		String sql = "select propertyData.id,propertyData.property_value,property.property_name_en "
				+ " from t_trace_property_data propertyData "
				+ " left join t_trace_property property on propertyData.property_id = property.id"
				+ " left join t_trace_model model on property.model_id = model.id "
				+ " where model.model_show_type = '1' and  propertyData.batch_id = '" + batchId + "'";

		List<Object> list = traceProductDao.findBySql(sql, null, Map.class);
		if (list == null || list.size() == 0) {
			return traceRootBean;
		}
		// 主模块数据对象
		MainModelData mainModelData = new MainModelData();
		for (Iterator<Object> iterator = list.iterator(); iterator.hasNext();) {
			Object object = (Object) iterator.next();
			Map<String, String> map = (Map<String, String>) object;
			String propertyName = map.get("property_name_en");
			if (null != propertyName) {
				if (propertyName.equals(Global.TRACE_SHOW_PROPERTY_CODE_SFBH)) {
					mainModelData.setIdentityCode(map.get("property_value"));
				}
				if (propertyName.equals(Global.TRACE_SHOW_PROPERTY_CODE_BZQ)) {
					mainModelData.setQualityGuaranteePeriod(map.get("property_value"));
				}
				if (propertyName.equals(Global.TRACE_SHOW_PROPERTY_CODE_SCRQ)) {
					mainModelData.setDateOfManufacture(map.get("property_value"));
				}
				if (propertyName.equals(Global.TRACE_SHOW_PROPERTY_CODE_SCCD)) {
					mainModelData.setProducePlace(map.get("property_value"));
				}
			}
		}

		traceRootBean.setMainModelData(mainModelData);
		return traceRootBean;
	}

	/**
	 * @param list
	 * @param mainReslist
	 * @param reslist
	 */
	public void mapToList(List<Map<String, Object>> list, List<TraceQueryData> mainReslist,
			List<TraceQueryData> reslist) {
		for (Map map : list) {
			if (map.get("is_main_model") != null && "1".equals(String.valueOf(map.get("is_main_model")))) {
				TraceQueryData data = new TraceQueryData();
				if (map.get("office_id") != null) {
					data.setOfficeId(String.valueOf(map.get("office_id")));
				}
				if (map.get("batch_id") != null) {
					data.setBatchId(String.valueOf(map.get("batch_id")));
				}
				if (map.get("model_id") != null) {
					data.setModelId(String.valueOf(map.get("model_id")));
				}
				if (map.get("model_name") != null) {
					data.setModelName(String.valueOf(map.get("model_name")));
				}
				if (map.get("is_main_model") != null) {
					data.setIsMainModel(String.valueOf(map.get("is_main_model")));
				}
				if (map.get("property_name_en") != null) {
					data.setPropertyNameEn(String.valueOf(map.get("property_name_en")));
				}
				if (map.get("property_name_zh") != null) {
					data.setPropertyNameZh(String.valueOf(map.get("property_name_zh")));
				}
				if (map.get("property_type") != null) {
					data.setPropertyType(String.valueOf(map.get("property_type")));
				}
				if (map.get("model_data_id") != null) {
					data.setModelDataId(String.valueOf(map.get("model_data_id")));
				}
				if (map.get("property_model_id") != null) {
					data.setPropertyModelId(String.valueOf(map.get("property_model_id")));
				}
				if (map.get("property_value") != null) {
					data.setPropertyValue(String.valueOf(map.get("property_value")));
				}
				mainReslist.add(data);
			}
			if (map.get("is_main_model") != null && "0".equals(String.valueOf(map.get("is_main_model")))) {
				TraceQueryData data = new TraceQueryData();
				if (map.get("office_id") != null) {
					data.setOfficeId(String.valueOf(map.get("office_id")));
				}
				if (map.get("batch_id") != null) {
					data.setBatchId(String.valueOf(map.get("batch_id")));
				}
				if (map.get("model_id") != null) {
					data.setModelId(String.valueOf(map.get("model_id")));
				}
				if (map.get("model_show_type") != null) {
					data.setModelShowType(String.valueOf(map.get("model_show_type")));
				}
				if (map.get("model_name") != null) {
					data.setModelName(String.valueOf(map.get("model_name")));
				}
				if (map.get("is_main_model") != null) {
					data.setIsMainModel(String.valueOf(map.get("is_main_model")));
				}
				if (map.get("property_name_en") != null) {
					data.setPropertyNameEn(String.valueOf(map.get("property_name_en")));
				}
				if (map.get("property_name_zh") != null) {
					data.setPropertyNameZh(String.valueOf(map.get("property_name_zh")));
				}
				if (map.get("property_type") != null) {
					// data.setPropertyType(String.valueOf(map.get("property_type")));
					if ((CommonEnum.NORMALTEXT.getCode().toString()).equals(String.valueOf(map.get("property_type")))) {
						data.setPropertyType(CommonEnum.NORMALTEXT.getMessage());
					}
					if ((CommonEnum.HTMLTEXT.getCode().toString()).equals(String.valueOf(map.get("property_type")))) {
						data.setPropertyType(CommonEnum.HTMLTEXT.getMessage());
					}
					if ((CommonEnum.IMAGE.getCode().toString()).equals(String.valueOf(map.get("property_type")))) {
						data.setPropertyType(CommonEnum.IMAGE.getMessage());
					}
					if ((CommonEnum.TIMEWIDGET.getCode().toString()).equals(String.valueOf(map.get("property_type")))) {
						data.setPropertyType(CommonEnum.TIMEWIDGET.getMessage());
					}
					if ((CommonEnum.ALBUM.getCode().toString()).equals(String.valueOf(map.get("property_type")))) {
						data.setPropertyType(CommonEnum.ALBUM.getMessage());
					}
					if ((CommonEnum.LINK.getCode().toString()).equals(String.valueOf(map.get("property_type")))) {
						data.setPropertyType(CommonEnum.LINK.getMessage());
					}
					if ((CommonEnum.RICHTXT.getCode().toString()).equals(String.valueOf(map.get("property_type")))) {
						data.setPropertyType(CommonEnum.RICHTXT.getMessage());
					}
					if ((CommonEnum.NUMBER.getCode().toString()).equals(String.valueOf(map.get("property_type")))) {
						data.setPropertyType(CommonEnum.NUMBER.getMessage());
					}
				}
				if (map.get("model_data_id") != null) {
					data.setModelDataId(String.valueOf(map.get("model_data_id")));
				}
				if (map.get("property_model_id") != null) {
					data.setPropertyModelId(String.valueOf(map.get("property_model_id")));
				}
				if (map.get("property_value") != null) {
					data.setPropertyValue(String.valueOf(map.get("property_value")));
				}
				reslist.add(data);
			}
		}
	}

	/**
	 * 按照modelId进行分组
	 * 
	 * @param billingList
	 * @return
	 * @throws Exception
	 */
	private Map<String, List<TraceQueryData>> groupByModelId(List<TraceQueryData> dataList) throws Exception {
		Map<String, List<TraceQueryData>> resultMap = new LinkedHashMap<String, List<TraceQueryData>>();
		String sysUrl = Global.getConfig("sy_img_url");
		try {
			for (TraceQueryData data : dataList) {
				if (data.getPropertyType().equals(CommonEnum.IMAGE.getMessage().toString())) {
					if (StringUtils.isNotBlank(data.getPropertyValue())) {
						data.setPropertyValue(sysUrl + data.getPropertyValue());
					} else {
						data.setPropertyValue(data.getPropertyValue());
					}
				}
				if (resultMap.containsKey(data.getModelId())) {// map中modelId已存在，将该数据存放到同一个key（key存放的是modelId）的map中
					resultMap.get(data.getModelId()).add(data);
				} else {// map中不存在，新建key，用来存放数据
					List<TraceQueryData> tmpList = new ArrayList<TraceQueryData>();
					tmpList.add(data);
					resultMap.put(data.getModelId(), tmpList);
				}
			}
		} catch (Exception e) {
			throw new Exception("按modelId分组时出现异常", e);
		}
		return resultMap;
	}

	public Map<String, Object> getAddress(String lon, String lat) throws Exception {
		String key = Global.getConfig("gaodeKey");
		String location = lon + "," + lat;
		String locationUrl = "http://restapi.amap.com/v3/geocode/regeo?key=" + key + "&location=";
		String province = "";
		String city = "";
		String area = "";
		String address = "";
		Map<String, Object> map = new HashMap<String, Object>();
		URL url = new URL(locationUrl + location);
		HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
		ucon.connect();
		InputStream in = ucon.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		String str = reader.readLine();
		JSONObject jsonObject = new JSONObject(str);
		System.out.println(str);
		JSONObject obj1 = jsonObject.getJSONObject("regeocode");
		JSONObject obj2 = obj1.getJSONObject("addressComponent");
		if (!obj2.get("province").getClass().toString().equals("class org.json.JSONArray")) {
			province = obj2.getString("province");
		}
		if (!obj2.get("city").getClass().toString().equals("class org.json.JSONArray")) {
			city = obj2.getString("city");
		}
		if (!obj2.get("district").getClass().toString().equals("class org.json.JSONArray")) {
			area = obj2.getString("district");
		}
		if (!obj1.get("formatted_address").getClass().toString().equals("class org.json.JSONArray")) {
			address = obj1.getString("formatted_address");
		}
		map.put("province", province);
		map.put("city", city);
		map.put("area", area);
		map.put("address", address);
		return map;
	}

	public static void main(String[] args) {

	}
}
