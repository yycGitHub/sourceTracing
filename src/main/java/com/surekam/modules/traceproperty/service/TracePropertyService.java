package com.surekam.modules.traceproperty.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
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
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.tracemodel.entity.TraceModel;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproperty.dao.TracePropertyDao;
import com.surekam.modules.traceproperty.dao.TracePropertyDataDao;
import com.surekam.modules.traceproperty.dao.TracePropertyNewDao;
import com.surekam.modules.traceproperty.entity.TraceProperty;
import com.surekam.modules.traceproperty.entity.TracePropertyData;
import com.surekam.modules.traceproperty.entity.TracePropertyNew;

/**
 * 溯源属性库管理Service
 * @author liw
 * @version 2018-09-06
 */
@Component
@Transactional(readOnly = true)
public class TracePropertyService extends BaseService {

	@Autowired
	private TracePropertyDao tracePropertyDao;
	
	@Autowired
	private TracePropertyNewDao tracePropertyNewDao;
	
	@Autowired
	private TracePropertyDataDao tracePropertyDataDao;
	
	
	public TraceProperty get(String id) {
		return tracePropertyDao.get(id);
	}
	
	/**
	 * 去除对象的持久态  否则 再次从数据库获取同一个对象修改属性放入List时 将修改前面所有对象数据
	 * @param property
	 * @return
	 */
	public TraceProperty evict(TraceProperty property) {
		Session session = (Session) tracePropertyDao.getSession();
		if(property!=null){
			session.evict(property);
		}
		return property;
	}
	
	/**
	 * 去除对象的持久态  否则 再次从数据库获取同一个对象修改属性放入List时 将修改前面所有对象数据
	 * @param property
	 * @return
	 */
	public TracePropertyNew evict(TracePropertyNew property) {
		Session session = (Session) tracePropertyNewDao.getSession();
		if(property!=null){
			session.evict(property);
		}
		return property;
	}
	
	public Page<TraceProperty> find(Page<TraceProperty> page, String propertyNameEn, String propertyNameZh, String propertyCode, String propertyType, String status, String officeId) {
		DetachedCriteria dc = tracePropertyDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(propertyNameEn)){
			dc.add(Restrictions.like("propertyNameEn", propertyNameEn,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotEmpty(propertyNameZh)){
			dc.add(Restrictions.like("propertyNameZh", propertyNameZh,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotEmpty(propertyCode)){
			dc.add(Restrictions.like("productCode", propertyCode,MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotEmpty(propertyType)){
			dc.add(Restrictions.eq("propertyType", propertyType));
		}
		if(StringUtils.isNotEmpty(status)){
			dc.add(Restrictions.eq("status", status));
		}
		if(StringUtils.isNotEmpty(officeId)){
			dc.add(Restrictions.eq("officeId", officeId));
		}
		dc.addOrder(Order.asc("sort"));
		return tracePropertyDao.find(page, dc);
	}
	
	/**
	 * 获取属性列表
	 * @return
	 */
	public List<TraceProperty> findTraceProperty(String officeId){	
		String sql = "SELECT * FROM t_trace_property t"
				+ " where (t.office_id = '2' "
				+ " or  t.office_id = '" + officeId + "') "
				+ "and t.status ='1'"
				+ "and t.states <> 'D'";
		
		return tracePropertyDao.findBySql(sql,null,TraceProperty.class);
	}
	
	/**
	 * 根据模板ID 获取属性
	 * @return
	 */
	public List<TraceProperty> findByPropertyId(String id){
		String sql = "select * from t_trace_property t"
				+ " where "+ " t.model_id = '" + id + "'"
				+ "and t.status = '1'"
				+ "and t.states <> 'D'";
		
		return tracePropertyDao.findBySql(sql, null,TraceProperty.class);
		
	}
	
	@Transactional(readOnly = false)
	public void save(TraceProperty traceProperty) {
		if(StringUtils.isNotBlank(traceProperty.getId())){
			traceProperty.setStates("U");
		}
		tracePropertyDao.save(traceProperty);
	}
	
	@Transactional(readOnly = false)
	public void enable(String id) {
		TraceProperty traceProperty = tracePropertyDao.get(id);
		traceProperty.setStatus(TraceProduct.OPEN);
		traceProperty.setUpdateTime(new Date());
		traceProperty.setUpdateUserid(UserUtils.getUser().getId());
		tracePropertyDao.save(traceProperty);
	}
	
	@Transactional(readOnly = false)
	public void disable(String id) {
		TraceProperty traceProperty = tracePropertyDao.get(id);
		traceProperty.setUpdateTime(new Date());
		traceProperty.setUpdateUserid(UserUtils.getUser().getId());
		traceProperty.setStatus(TraceProduct.CLOSE);
		tracePropertyDao.save(traceProperty);
	}
	
	@Transactional(readOnly = false)
	public String delete(String id) {
		TraceProperty traceProperty = tracePropertyDao.get(id);
		traceProperty.setUpdateTime(new Date());
		traceProperty.setUpdateUserid(UserUtils.getUser().getId());
		//启用的属性不能删除
		if(traceProperty !=null && "1".equals(traceProperty.getStatus())) {
			return "1";
		}
		tracePropertyDao.save(traceProperty);
		tracePropertyDao.deleteByXGXTId(id);
		return "0";
	}
	
	/**
	 * 根据ModelId获取属性List
	 * */
	public List<TraceProperty> findPropertyListByModelId(String modelId){
		DetachedCriteria dc = tracePropertyDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProperty.FIELD_DEL_FLAG_XGXT, TraceProperty.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(modelId)){
			dc.add(Restrictions.eq("modelId", modelId));
		}
		dc.addOrder(Order.asc("sort"));
		return tracePropertyDao.find(dc);
	}
	
	/**
	 * 根据modelDataId获取属性TracePropertyData List
	 * */
	public List<TracePropertyData> findPropertyDataListByModelDataId(String modelDataId){
		DetachedCriteria dc = tracePropertyDataDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TracePropertyData.FIELD_DEL_FLAG_XGXT, TracePropertyData.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(modelDataId)){
			dc.add(Restrictions.eq("modelDataId", modelDataId));
		}
		dc.addOrder(Order.asc("sort"));
		return tracePropertyDataDao.find(dc);
	}
	
	/**
	 * 根据modelDataGroupId获取属性TracePropertyData List
	 * */
	public List<TracePropertyData> findPagePropertyDataListByModelDataGroupId(String modelDataGroupId){
		DetachedCriteria dc = tracePropertyDataDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TracePropertyData.FIELD_DEL_FLAG_XGXT, TracePropertyData.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(modelDataGroupId)){
			dc.add(Restrictions.eq("modelDataGroupId", modelDataGroupId));
		}
		dc.addOrder(Order.asc("sort"));
		return tracePropertyDataDao.find(dc);
	}

	/**
	 * 根据modelDataGroupId获取属性TracePropertyData List未通过
	 * */
	public List<TracePropertyData> findPagePropertyDataListByModelDataGroupIdFail(String modelDataGroupId){
		DetachedCriteria dc = tracePropertyDataDao.createDetachedCriteria();
		if(StringUtils.isNotBlank(modelDataGroupId)){
			dc.add(Restrictions.eq("modelDataGroupId", modelDataGroupId));
		}
		dc.addOrder(Order.asc("sort"));
		return tracePropertyDataDao.find(dc);
	}
	/**
	 * 根据modelDataGroupId获取属性TracePropertyData List
	 * */
	public List<TracePropertyData> findPagePropertyDataListByModelDataGroupIdReqDel(String modelDataGroupId){
		DetachedCriteria dc = tracePropertyDataDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TracePropertyData.FIELD_DEL_FLAG_XGXT, TracePropertyData.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(modelDataGroupId)){
			dc.add(Restrictions.eq("modelDataGroupId", modelDataGroupId));
		}
		dc.addOrder(Order.asc("sort"));
		return tracePropertyDataDao.find(dc);
	}
	
	/**
	 * 按照sortId对tracePropertyList列表进行排序 按从小到大排列
	 * @param list
	 */
	public void ListSortArrayList(List<TraceProperty> tracePropertyList) {
		Collections.sort(tracePropertyList, new Comparator<TraceProperty>() {
			@Override
			public int compare(TraceProperty o1, TraceProperty o2) {
				try {
					return o1.getSort() - o2.getSort();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}
	
	/**
	 * 按照sortId对tracePropertyList列表进行排序 按从小到大排列
	 * @param list
	 */
	public void ListSortArrayList1(List<TracePropertyNew> tracePropertyNewList) {
		Collections.sort(tracePropertyNewList, new Comparator<TracePropertyNew>() {
			@Override
			public int compare(TracePropertyNew o1, TracePropertyNew o2) {
				try {
					return o1.getSort() - o2.getSort();
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}
	
	/**
	 * 按照sortId对traceModelList列表进行排序 按从小到大排列
	 * @param list
	 */
	public void ListSortArrayListTraceModel(List<TraceModel> traceModelList) {
		Collections.sort(traceModelList, new Comparator<TraceModel>() {
			@Override
			public int compare(TraceModel o1, TraceModel o2) {
				try {
					return o1.getSort().compareTo(o2.getSort());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}
	
	/**
	 * 按照sortId对含有已配置模块和未配置模块的traceModelList列表进行排序 已配置模块排在前  再按sort从小到大排列
	 * @param list
	 */
	public void ListSortArrayListTraceModelWithNew(List<TraceModel> traceModelList) {
		Collections.sort(traceModelList, new Comparator<TraceModel>() {
			@Override
			public int compare(TraceModel o1, TraceModel o2) {
				try {
					if(StringUtils.isNotBlank(o1.getModelDataId()) && StringUtils.isNotBlank(o2.getModelDataId())){
						return o1.getSort().compareTo(o2.getSort());
					}else if(StringUtils.isNotBlank(o1.getModelDataId())){
						return -1;
					}else if(StringUtils.isNotBlank(o2.getModelDataId())){
						return 1;
					}
					return o1.getSort().compareTo(o2.getSort());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return 0;
			}
		});
	}

	/**
	 * 获取属性对象
	 * @return
	 */
	public TracePropertyNew findPropertyBySxid(String id){	
		String sql = "select * from t_trace_property_new t where t.id=:p1 ";//and t.states<>'D'
		List<TracePropertyNew> propertyList = tracePropertyNewDao.findBySql(sql,new Parameter(id),TracePropertyNew.class);
		if(propertyList.size()>0){
			return propertyList.get(0);
		}else{
			return new TracePropertyNew();
		}
	}
	
	/**
	 * 通过属性名称和模块ID获取属性对象
	 * @return
	 */
	public TraceProperty findPropertyBySxmcAndMId(String propertyName,String modelId){	
		String sql = "SELECT * FROM t_trace_property t WHERE t.states<>'D' AND t.model_id=:p1 AND t.property_name_zh=:p2 ";
		List<TraceProperty> propertyList = tracePropertyDao.findBySql(sql,new Parameter(modelId,propertyName),TraceProperty.class);
		if(propertyList!=null && propertyList.size()>0){
			return propertyList.get(0);
		}else{
			return null;
		}
	}
	
	
}
