package com.surekam.modules.traceproduct.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.UniqueCodeUtil;
import com.surekam.modules.productbatch.dao.ProductBatchDao;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.OfficeService;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.trace.TraceLableApply.dao.TraceLableApplyDao;
import com.surekam.modules.tracecode.dao.TraceCodeDao;
import com.surekam.modules.tracemodel.dao.TraceModelDao;
import com.surekam.modules.tracemodel.entity.TraceModel;
import com.surekam.modules.tracemodel.service.TraceModelService;
import com.surekam.modules.traceproduct.dao.TraceProductDao;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceproduct.entity.TraceProductP;
import com.surekam.modules.traceproperty.dao.TracePropertyDao;

/**
 * 溯源产品管理Service
 * @author ligm
 * @version 2018-08-21
 */
@Component
@Transactional(readOnly = true)
public class TraceProductService extends BaseService {

	@Autowired
	private TraceProductDao traceProductDao;
	
	@Autowired
	private TraceModelDao traceModelDao;
	
	@Autowired
	private TracePropertyDao tracePropertyDao;
	
	@Autowired
	private TraceModelService traceModelService;
	
	@Autowired
	private ProductBatchDao productBatchDao;
	
	@Autowired
	private OfficeService officeService;
	
	@Autowired
	private TraceLableApplyDao traceLableApplyDao;
	
	@Autowired
	private TraceCodeDao traceCodeDao;
	
	public TraceProduct get(String id) {
		return traceProductDao.get(id);
	}
	
	/**
	 * 查询产品详情
	 * @param id 产品id
	 * @return
	 */
	public TraceProductP getP(String id) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT");
		sqlString.append(" t.*, tt1.batch_count,");
		sqlString.append(" tt1.trace_count");
		sqlString.append(" FROM");
		sqlString.append(" t_trace_product t");
		sqlString.append(" LEFT JOIN (");
		sqlString.append(" SELECT");
		sqlString.append(" t1.product_id,");
		sqlString.append(" count(t1.product_id) AS batch_count,");
		sqlString.append(" sum(t2.trace_count) AS trace_count");
		sqlString.append(" FROM");
		sqlString.append(" t_trace_product_batch t1");
		sqlString.append(" LEFT JOIN t_trace_code_info t2 ON t2.batch_id = t1.id");
		sqlString.append(" GROUP BY");
		sqlString.append(" t1.product_id");
		sqlString.append(" ) tt1 ON tt1.product_id = t.id");
		sqlString.append(" where t.id = \"" + id + "\"");
		List<TraceProductP> listP = traceProductDao.findBySql(sqlString.toString(), new Parameter(), TraceProduct.class);
		if(listP != null && listP.size() > 0) {
			StringBuffer sqlString2 = new StringBuffer();
			//TODO 
			//缺少批次产地、生产日期、3日扫码次数等数据
		}
		return null;
	}
	
	public Page<TraceProduct> find(Page<TraceProduct> page, String productName, String productCode, String status, String officeId, String officeName) {
		String sql = "select a.* from t_trace_product a, sys_office b where a.states<>'D' and a.office_id = b.id AND b.DEL_FLAG='0'";
		if(StringUtils.isNotEmpty(productName)){
			sql+=" and a.product_name like '%"+ productName +"%'";
		}
		if(StringUtils.isNotEmpty(productCode)){
			sql+=" and a.product_code like '%"+ productCode +"%'";
		}
		if(StringUtils.isNotEmpty(officeName)){
			sql+=" and b.name like '%"+ officeName +"%'";
		}
//		if(StringUtils.isNotEmpty(status)){
//			sql+=" and a.status = '"+ status +"'";
//		}
		if(StringUtils.isNotEmpty(officeId)){
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		}
		sql+= " order by creat_time desc";
		return traceProductDao.findBySql(page, sql,null,TraceProduct.class);
	}
	// 生产负责人只能看自己的
	public Page<TraceProduct> find(Page<TraceProduct> page, String productName, String productCode, String status, String officeId,User user) {
		String sql = "select a.* from t_trace_product a where a.states<>'D'";
		if(StringUtils.isNotEmpty(productName)){
			sql+=" and a.product_name like '%"+ productName +"%'";
		}
		if(StringUtils.isNotEmpty(productCode)){
			sql+=" and a.product_code like '%"+ productCode +"%'";
		}
		if(StringUtils.isNotEmpty(status)){
			sql+=" and a.status = '"+ status +"'";
		}
		if(StringUtils.isNotEmpty(officeId)){
			//sql+=" and FIND_IN_SET(a.office_id,getChildOfficeLst2 ('" + officeId + "'))";
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		}
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					sql+=" and a.creat_userid = '"+ user.getId() +"'";
				}
			}
			
		}
		sql+= " order by creat_time desc";
		return traceProductDao.findBySql(page, sql,null,TraceProduct.class);
	}
	public long findCount(String productName, String productCode, String status, String officeId) {
		String sql = "select a.* from t_trace_product a where a.states<>'D'";
		if(StringUtils.isNotEmpty(productName)){
			sql+=" and a.product_name like '%"+ productName +"%'";
		}
		if(StringUtils.isNotEmpty(productCode)){
			sql+=" and a.product_code like '%"+ productCode +"%'";
		}
		if(StringUtils.isNotEmpty(status)){
			sql+=" and a.status = '"+ status +"'";
		}
		if(StringUtils.isNotEmpty(officeId)){
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		}
		sql+= " order by creat_time desc";
		List<TraceProduct> list = traceProductDao.findBySql(sql,null,TraceProduct.class);
		if(list!=null){
			return list.size();
		}else{
			return 0;
		}
	}

	// 生产负责人只能看自己的
	public long findCount(String productName, String productCode, String status, String officeId,User user) {
		String sql = "select a.* from t_trace_product a where a.states<>'D'";
		if(StringUtils.isNotEmpty(productName)){
			sql+=" and a.product_name like '%"+ productName +"%'";
		}
		if(StringUtils.isNotEmpty(productCode)){
			sql+=" and a.product_code like '%"+ productCode +"%'";
		}
		if(StringUtils.isNotEmpty(status)){
			sql+=" and a.status = '"+ status +"'";
		}
		if(StringUtils.isNotEmpty(officeId)){
			sql+=" and a.office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')";
		}
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					sql+=" and a.creat_userid = '"+ user.getId() +"'";
				}
			}
			
		}
		sql+= " order by creat_time desc";
		List<TraceProduct> list = traceProductDao.findBySql(sql,null,TraceProduct.class);
		if(list!=null){
			return list.size();
		}else{
			return 0;
		}
	}
	/**
	 * 根据产品名称、officeId查询产品
	 * @param productName
	 * @param officeId
	 * @return
	 */
	public TraceProduct find(TraceProduct traceProduct, String officeId) {
		DetachedCriteria dc = traceProductDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(traceProduct.getProductName())){
			dc.add(Restrictions.eq("productName", traceProduct.getProductName()));
		}
		if(StringUtils.isNotEmpty(traceProduct.getId())){
			dc.add(Restrictions.ne("id", traceProduct.getId()));
		}
		if(StringUtils.isNotEmpty(officeId)){
			dc.add(Restrictions.eq("officeId", officeId));
		}
		List<TraceProduct> list = traceProductDao.find(dc);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 产品列表（手机端）
	 * @param page
	 * @param productName
	 * @return
	 */
	public Page<TraceProductP> find(Page<TraceProductP> page, String productName, String officeId, String states) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT t.* FROM t_trace_product t");
		sqlString.append(" where 1=1");
		
		if(states != null && states.equals("A")) {
			sqlString.append(" and t.states <> 'D'");
		}
		if(states != null && states.equals("D")) {
			sqlString.append(" and t.states = 'D'");
		}
		if(productName != null && !productName.equals("")) {
			sqlString.append(" and t.product_name like \"%" + productName+ "%\"");
		}
		if(officeId != null && !officeId.equals("")) {
			sqlString.append(" and t.office_id = \"" + officeId + "\"");
		}
		sqlString.append(" ORDER BY");
		sqlString.append(" t.update_time DESC");
		return traceProductDao.findBySql(page, sqlString.toString(), TraceProductP.class);
	}
	
	/**
	 * 产品列表（pc端）
	 * @param page
	 * @param productName
	 * @return
	 */
	public Page<TraceProductP> PC_find(Page<TraceProductP> page, String productName, String officeId, String states,List<String> findChildrenOffice,String admin) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT t.* FROM t_trace_product t");
		sqlString.append(" where 1=1");
		
		if(states != null && states.equals("A")) {
			sqlString.append(" and t.states <> 'D'");
		}
		if(states != null && states.equals("D")) {
			sqlString.append(" and t.states = 'D'");
		}
		if(productName != null && !productName.equals("")) {
			sqlString.append(" and t.product_name like \"%" + productName+ "%\"");
		}
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(officeId)) {//前端页面公司下拉框是否选择
				sqlString.append(" and t.office_id = \"" + officeId + "\"");
			}else {
				String officeData = "'" + StringUtils.join(findChildrenOffice, "','") + "'";
				sqlString.append(" and t.office_id in(" + officeData + ")" );
			}
		}else {
			if(StringUtils.isNotEmpty(officeId)) {//前端页面公司下拉框是否选择
				sqlString.append(" and t.office_id = \"" + officeId + "\"");
			}
		}
		sqlString.append(" ORDER BY");
		sqlString.append(" t.update_time DESC");
		return traceProductDao.findBySql(page, sqlString.toString(), TraceProductP.class);
	}

	/**
	 * 产品列表（pc端）,产品负责人只看自己的数据
	 * @param page
	 * @param productName
	 * @return
	 */
	public Page<TraceProductP> PC_find(Page<TraceProductP> page, String productName, String officeId, String states,List<String> findChildrenOffice,String admin,User user) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT t.* FROM t_trace_product t");
		sqlString.append(" where 1=1");
		
		if(states != null && states.equals("A")) {
			sqlString.append(" and t.states <> 'D'");
		}
		if(states != null && states.equals("D")) {
			sqlString.append(" and t.states = 'D'");
		}
		if(productName != null && !productName.equals("")) {
			sqlString.append(" and t.product_name like \"%" + productName+ "%\"");
		}
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(officeId)) {//前端页面公司下拉框是否选择
				sqlString.append(" and t.office_id = \"" + officeId + "\"");
			}else {
				String officeData = "'" + StringUtils.join(findChildrenOffice, "','") + "'";
				sqlString.append(" and t.office_id in(" + officeData + ")" );
			}
		}else {
			if(StringUtils.isNotEmpty(officeId)) {//前端页面公司下拉框是否选择
				sqlString.append(" and t.office_id = \"" + officeId + "\"");
			}
		}
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					sqlString.append(" and t.creat_userid = '"+ user.getId() +"'");
				}
			}
			
		}
		sqlString.append(" ORDER BY");
		sqlString.append(" t.update_time DESC");
		return traceProductDao.findBySql(page, sqlString.toString(), TraceProductP.class);
	}
	/**
	 * 回收站产品(PC端)导出，不分页
	 * @param page
	 * @param productName
	 * @return
	 */
	public List<TraceProductP> findListExport(String productName, String officeId, String states,List<String> findChildrenOffice,String admin,User user) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT t.* FROM t_trace_product t");
		sqlString.append(" where 1=1");
		
		if(states != null && states.equals("A")) {
			sqlString.append(" and t.states <> 'D'");
		}
		if(states != null && states.equals("D")) {
			sqlString.append(" and t.states = 'D'");
		}
		if(productName != null && !productName.equals("")) {
			sqlString.append(" and t.product_name like \"%" + productName+ "%\"");
		}
		if(StringUtils.isNotBlank(admin)) {//非管理员进入
			if(StringUtils.isNotEmpty(officeId)) {//前端页面公司下拉框是否选择
				sqlString.append(" and t.office_id = \"" + officeId + "\"");
			}else {
				String officeData = "'" + StringUtils.join(findChildrenOffice, "','") + "'";
				sqlString.append(" and t.office_id in(" + officeData + ")" );
			}
		}else {
			if(StringUtils.isNotEmpty(officeId)) {//前端页面公司下拉框是否选择
				sqlString.append(" and t.office_id = \"" + officeId + "\"");
			}
		}
		if (user.getRoleList()!=null){
			List<Role> roleList = user.getRoleList();
			if(roleList.size()==1){
				Role role=roleList.get(0);
				// 只能看自己的数据的角色
				if(role.getDataScope().equals("8")){
					sqlString.append(" and t.creat_userid = '"+ user.getId() +"'");
				}
			}
			
		}
		sqlString.append(" ORDER BY");
		sqlString.append(" t.update_time DESC");
		return traceProductDao.findBySql(sqlString.toString(),new Parameter(), TraceProductP.class);
	}
	
	
	/**
	 * 回收站产品列表查询（PC端）
	 * @param page
	 * @param productName
	 * @return
	 * --xy
	 * --2019-01-10 15:20
	 */
	public List<TraceProductP> find(String productName, String officeId, String states) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT t.* FROM t_trace_product t");
		sqlString.append(" where 1=1");
		
		if(states != null && states.equals("A")) {
			sqlString.append(" and t.states <> 'D'");
		}
		if(states != null && states.equals("D")) {
			sqlString.append(" and t.states = 'D'");
		}
		if(productName != null && !productName.equals("")) {
			sqlString.append(" and t.product_name like \"%" + productName+ "%\"");
		}
		if(officeId != null && !officeId.equals("")) {
			sqlString.append(" and t.office_id = :p1");
		}
		sqlString.append(" ORDER BY");
		sqlString.append(" t.update_time DESC");
		return traceProductDao.findBySql2(sqlString.toString(),new Parameter(officeId),TraceProductP.class);
	}
	
	/**
	 * 获取精品溯源产品
	 * @return
	 */
	public List<TraceProduct> getCompetitiveProduct() {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT");
		sqlString.append(" t2.*,t.trace_code");
		sqlString.append(" FROM");
		sqlString.append(" t_trace_code_info t");
		sqlString.append(" LEFT JOIN t_trace_product_batch t1 ON t1.id = t.batch_id");
		sqlString.append(" LEFT JOIN t_trace_product t2 ON t2.id = t1.product_id");
		sqlString.append(" WHERE");
		sqlString.append(" t.competitive = '1'");
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		list = traceProductDao.findBySql(sqlString.toString(), new Parameter(), Map.class);
		if(list==null || list.size()==0){
			return null;
		}
		List<TraceProduct> productList = new ArrayList<TraceProduct>();
		for (Map map : list) {
			TraceProduct product = new TraceProduct();
			if(map.get("product_name") != null){
				product.setProductName(String.valueOf(map.get("product_name")));;
			}
			if(map.get("product_pic") != null){
				product.setProductPic(String.valueOf(map.get("product_pic")));;
			}
			if(map.get("trace_code") != null){
				product.setTraceCode(String.valueOf(map.get("trace_code")));
			}
			if(map.get("id") != null){
				product.setId(String.valueOf(map.get("id")));
			}
			productList.add(product);
		}
		return productList;
	}
	
	@Transactional(readOnly = false)
	public void save(TraceProduct traceProduct) throws Exception {
		if(StringUtils.isNotBlank(traceProduct.getId())){
			traceProduct.setStates("U");
		}
		traceProductDao.save(traceProduct);
	}
	/**
	 * 专供外部调用-保存产品信息
	 * @param traceProduct
	 * @throws Exception
	 */
	@Transactional(readOnly = false)
	public void saveProduct(TraceProduct traceProduct) throws Exception {
		traceProduct.setStates("A");
		traceProductDao.save(traceProduct);
	}
	
	@Transactional(readOnly = false)
	public String saveAttach(MultipartFile file,String path) throws Exception {
		String result = "";
		//图片上传
		if(file!=null && !file.getOriginalFilename().isEmpty()){
			//保存附件,生成文件名
			SimpleDateFormat sdfdate = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			String time = sdfdate.format(new Date());
			//文件名
			String name = file.getOriginalFilename();
			//文件后缀
			String extName =  name.substring(name.lastIndexOf(".")); 
			//变更上传文件文件名
			String new_name = time+extName;
			result = "/upload/product_model/"+new_name;
			// 转存文件 
			file.transferTo(new File(path+new_name));
		}
		return result;
	}
	
	@Transactional(readOnly = false)
	public void enable(String id) {
		TraceProduct traceProduct = traceProductDao.get(id);
		traceProduct.setStatus(TraceProduct.OPEN);
		traceProduct.setUpdateTime(new Date());
		traceProduct.setUpdateUserid(UserUtils.getUser().getId());
		traceProductDao.save(traceProduct);
	}
	
	@Transactional(readOnly = false)
	public void disable(String id) {
		TraceProduct traceProduct = traceProductDao.get(id);
		traceProduct.setUpdateTime(new Date());
		traceProduct.setUpdateUserid(UserUtils.getUser().getId());
		traceProduct.setStatus(TraceProduct.CLOSE);
		traceProductDao.save(traceProduct);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		TraceProduct traceProduct = traceProductDao.get(id);
		traceProduct.setUpdateTime(new Date());
		traceProduct.setUpdateUserid(UserUtils.getUser().getId());
		traceProductDao.save(traceProduct);
		traceProductDao.deleteByXGXTId(id);
	}
	
	/**
	 * 逻辑删除产品及产品的模块和模块属性数据  
	 * 检查是否存在批次数据 如果存在 则返回1  否则为正常删除
	 * @param productId
	 */
	@Transactional(readOnly = false)
	public String deleteProductWithModelsAndProperties(String productId, User user) {
		//原来为判断是否有批次数据  修改为是否有已打印标签的批次数据
		//String hqlFindLabelApplyPrinted = "from TraceLableApply labelApply where labelApply.printFlag = '1' and labelApply.states !='D' "
		//		+ " and labelApply.batchId in (select batch.id from ProductBatch batch where batch.productId = '" + productId + "')";
		//List<TraceLableApply> labelApplyPrintedList = traceLableApplyDao.find(hqlFindLabelApplyPrinted);
		//if(null != labelApplyPrintedList && 0 != labelApplyPrintedList.size()){
		//	return "1";
		//}
		
		//删除所有批次及批次对应的标签申请信息
		//删除标签数据
		String hqDeleteTraceCode = "update TraceCode traceCode set traceCode.states='D' "
				+ " where traceCode.applyId in (select labelApply.id from TraceLableApply labelApply where labelApply.traceProductId  = '" + productId + "' and labelApply.states <> 'D')";
		traceCodeDao.update(hqDeleteTraceCode);
		
		String hqDeleteLabelApply = "update TraceLableApply labelApply set labelApply.states='D' "
				+ " where labelApply.traceProductId  = '" + productId + "' and labelApply.states <> 'D'";
		traceLableApplyDao.update(hqDeleteLabelApply);
		
		//删除模块对应属性值
		String hqlDeleteProperty = "update TracePropertyData propertyData set propertyData.states = 'D' "
				+ " where propertyData.batchId in"
				+ " (select batch.id from ProductBatch batch where batch.productId = '" + productId + "' and batch.states<>'D')";
		tracePropertyDao.update(hqlDeleteProperty);
		
		//删除批次数据
		String hqDeleteBatch = "update ProductBatch batch set batch.states='D' "
				+ " where batch.productId = '" + productId + "' and batch.states<>'D'";
		productBatchDao.update(hqDeleteBatch);
		
		//删除模块
		//String hqDeleteModel = "update TraceModel model set model.states='D' "
		//		+ " where model.productId ='" + productId + "' and model.states<>'D'";
		//traceModelDao.update(hqDeleteModel);
		
		TraceProduct traceProduct = traceProductDao.get(productId);
		traceProduct.setUpdateTime(new Date());
		if(null != user){
			traceProduct.setUpdateUserid(user.getId());
		}
		traceProduct.setStates("D");
		traceProductDao.save(traceProduct);
		return "0";
	}
	
	/**
	 * 逻辑删除产品及产品的模块和模块属性数据  
	 * 检查是否存在批次数据 如果存在 则返回1  否则为正常删除
	 * @param productId
	 */
	@Transactional(readOnly = false)
	public void recoverProduct(String productId, User user) {
		//恢复标签数据
		String hqDeleteTraceCode = "update TraceCode traceCode set traceCode.states='U' "
				+ " where traceCode.applyId in (select labelApply.id from TraceLableApply labelApply where labelApply.traceProductId  = '" + productId + "' and labelApply.states = 'D')";
		traceCodeDao.update(hqDeleteTraceCode);
		
		String hqDeleteLabelApply = "update TraceLableApply labelApply set labelApply.states='U' "
				+ " where labelApply.traceProductId  = '" + productId + "' and labelApply.states = 'D'";
		traceLableApplyDao.update(hqDeleteLabelApply);
		
		//恢复模块对应属性值
		String hqlDeleteProperty = "update TracePropertyData propertyData set propertyData.states = 'U' "
				+ " where propertyData.batchId in"
				+ " (select batch.id from ProductBatch batch where batch.productId = '" + productId + "' and batch.states='D')";
		tracePropertyDao.update(hqlDeleteProperty);
		
		//恢复批次数据
		String hqDeleteBatch = "update ProductBatch batch set batch.states='U' "
				+ " where batch.productId = '" + productId + "' and batch.states='D'";
		productBatchDao.update(hqDeleteBatch);
		
		TraceProduct traceProduct = traceProductDao.get(productId);
		traceProduct.setUpdateTime(new Date());
		if(null != user){
			traceProduct.setUpdateUserid(user.getId());
		}
		traceProduct.setStates("U");
		traceProductDao.save(traceProduct);
	}
	
	public String getMaxProductCode(String type){
		List<String> list = traceProductDao.find("select max(t.productCode) from TraceProduct t where t.states!='D' and t.productCode like '"+type+"%'");
		if(list.size()>0){
			return list.get(0);
		}else{
			return "";
		}
	}
	
	
	/**
	 * 复制产品、主题、模块、模块属性数据
	 * @param productId 
	 * @param officeId 拷贝的数据设置的公司ID
	 * @param userId 保存数据的人员
	 */
	@Transactional(readOnly = false)
	public void copyProductData(String productId, String officeId, String userId) {
		TraceProduct traceProductCopy = new TraceProduct();
		TraceProduct traceProduct = traceProductDao.get(productId);
		//去除持久态
		Session session = (Session) traceProductDao.getSession();
		session.evict(traceProduct);
		try {
			BeanUtils.copyProperties(traceProductCopy, traceProduct);
			traceProductCopy.setId(null);
			traceProductCopy.setProductName(traceProduct.getProductName() + "-复制");
			traceProductCopy.setOfficeId(officeId);
			traceProductCopy.setCreatTime(new Date());
			traceProductCopy.setUpdateTime(new Date());
			//分配产品的唯一编码
			Office office = officeService.get(officeId);
			String productCode = UniqueCodeUtil.getUniqueCode("TraceProduct", "productCode",office.getCode());
			traceProductCopy.setProductCode(office.getCode() + "-" +  UniqueCodeUtil.PRODUCT_PRE + "-" + productCode);
		} catch (Exception e) {
			throw new RuntimeException(); 
		}
		traceProductDao.save(traceProductCopy);
		//模块
		List<TraceModel> traceModelList = traceModelService.findTraceModelsByProductId(productId);
		for (Iterator<TraceModel> iterator = traceModelList.iterator(); iterator.hasNext();) {
			TraceModel traceModel = (TraceModel) iterator.next();
			TraceModel traceModelCopy = new TraceModel();
			session.evict(traceModel);
			try {
				ConvertUtils.register(new DateConverter(null), java.util.Date.class);//重新注册一个日期转换器
				BeanUtils.copyProperties(traceModelCopy, traceModel);
				traceModelCopy.setId(null);
				traceModelCopy.setOfficeId(officeId);
				traceModelCopy.setProductId(traceProductCopy.getId());
				traceModelCopy.setCreatTime(new Date());
				traceModelCopy.setUpdateTime(new Date());
			} catch (Exception e) {
				throw new RuntimeException(); 
			}
			traceModelDao.save(traceModelCopy);
			//复制属性
			String sql = "INSERT INTO t_trace_property(id, office_id, property_code, model_id, property_name_en, property_name_zh,"
					+ " property_type, time_flag, sort, status, creat_time, creat_userid, update_time, update_userid, states) "
					+ " select UUID(),'" + officeId + "',propertysource.property_code,'" + traceModelCopy.getId() 
					+ "',propertysource.property_name_en,propertysource.property_name_zh,"
					+ " propertysource.property_type,propertysource.time_flag,propertysource.sort,propertysource.status,"
					+ " NOW(),'" + userId + "',NOW(),'" + userId + "','" + TraceModel.STATE_FLAG_ADD + "'"
					+ " from t_trace_property propertysource where propertysource.model_id='" + traceModel.getId() + "'";
			traceModelDao.getSession().createSQLQuery(sql).executeUpdate();
		}
		
		traceProduct.setUpdateTime(new Date());
		traceProduct.setUpdateUserid(UserUtils.getUser().getId());
		traceProductDao.save(traceProduct);
	}
	
	/**
	 * 获取poName表的poCode字段的最大值
	 * @param poName
	 * @param poCode
	 * @param corpCode  为空时表示此表的编码没有多个公司前缀 
	 * @return
	 */
	public String getMaxNum(String poName, String poCode, String corpCode) {
		// TODO Auto-generated method stub
		String returnNUm;
		String hqlStr = "select " + poCode + " from " + poName+" t";
		if(StringUtils.isNotBlank(corpCode)){
			hqlStr += " where " + poCode + " like '%" + corpCode + "%' ";
		}
		hqlStr += " order by " + poCode + " desc";
		List<String> findList = traceProductDao.find(hqlStr);
		if(findList.isEmpty()) {
			returnNUm = "";
		}else {
			returnNUm = (String) findList.get(0);
		}
		return returnNUm;
	}
	
	public Page<TraceProduct> findAllProducts(Page<TraceProduct> page,String officeId) {
		DetachedCriteria dc = traceProductDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("status", TraceProduct.OPEN));
		if(StringUtils.isNotBlank(officeId)){
			dc.add(Restrictions.eq("officeId", officeId));
		}
		dc.addOrder(Order.desc("creatTime"));
		return traceProductDao.find(page,dc);
	}
	
	public long findAllProductsCount(String officeId) {
		DetachedCriteria dc = traceProductDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("status", TraceProduct.OPEN));
		if(StringUtils.isNotBlank(officeId)){
			dc.add(Restrictions.eq("officeId", officeId));
		}
		return traceProductDao.count(dc);
	}
	
	public List<TraceProduct> findAllProducts(String officeId) {
		/*DetachedCriteria dc = traceProductDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("status", TraceProduct.OPEN));*/
		StringBuffer sql = new StringBuffer(200);
		sql.append("SELECT * FROM t_trace_product WHERE states<>'D' and status = '1'");
		if(StringUtils.isNotBlank(officeId)){
			//dc.add(Restrictions.eq("officeId", officeId));
			sql.append(" and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%')");
		}
		sql.append(" order by creat_time desc");
		return traceProductDao.findBySql(sql.toString(),null,TraceProduct.class);
	}
	/**
	 * 传入code（wangcheng或者tulufan）id
	 * @param officeId
	 * @return
	 */
	public List<TraceProduct> findAllProductsTwo(String officeId) {
		StringBuffer sql = new StringBuffer(200);
		if (StringUtils.isNotBlank(officeId) && !"1".equals(officeId)) {
			sql.append("SELECT * FROM t_trace_product WHERE states<>'D' and status = '1' and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.id = '" + officeId + "' OR tt.PARENT_IDS LIKE '%" + officeId + "%') order by creat_time desc");
		}
		if (StringUtils.isNotBlank(officeId) && "1".equals(officeId)) {
			sql.append("SELECT * FROM t_trace_product WHERE states<>'D' and status = '1' and office_id in (SELECT tt.id FROM sys_office tt WHERE tt.PARENT_IDS LIKE '%" + officeId + "%') order by creat_time desc");
		}
		return traceProductDao.findBySql(sql.toString(),new Parameter(), TraceProduct.class);
	}
	
	
	public List<Object> getProductCount(String officeId){
		List<Object> list = traceProductDao.findBySql("select substr(t.creat_time,1,4),count(t.id) from t_trace_product t where t.states<>'d' and t.office_id=:p1 group by substr(t.creat_time,1,4) order by substr(t.creat_time,1,4)"
				,new Parameter(officeId));
		return list;
	}

}