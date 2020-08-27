package com.surekam.modules.tracecode.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
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
import com.surekam.modules.api.utils.ApiUtil;
import com.surekam.modules.productbatch.dao.ProductBatchDao;
import com.surekam.modules.productbatch.entity.ProductBatch;
import com.surekam.modules.sys.dao.OfficeDao;
import com.surekam.modules.sys.entity.Office;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.trace.TraceLableApply.dao.TraceLableApplyDao;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.tracecode.dao.TraceCodeDao;
import com.surekam.modules.tracecode.entity.CodeDataVO;
import com.surekam.modules.tracecode.entity.TraceCode;
import com.surekam.modules.tracecode.entity.TraceCodeVo;
import com.surekam.modules.traceproduct.dao.TraceProductDao;
import com.surekam.modules.traceproduct.entity.TraceProduct;

/**
 * 溯源码Service
 * 
 * @author liuyi
 * @version 2018-09-26
 */
@Component
@Transactional(readOnly = true)
public class TraceCodeService extends BaseService {

	@Autowired
	private TraceCodeDao traceCodeDao;

	@Autowired
	private OfficeDao officeDao;

	@Autowired
	private ProductBatchDao productBatchDao;

	@Autowired
	private TraceProductDao productDao;

	@Autowired
	private TraceLableApplyDao traceLableApplyDao;

	public TraceCode get(String id) {
		return traceCodeDao.get(id);
	}

	public Page<TraceCode> find(Page<TraceCode> page, TraceCode traceCode) {
		DetachedCriteria dc = traceCodeDao.createDetachedCriteria();
		dc.add(Restrictions.eq(TraceCode.FIELD_DEL_FLAG, TraceCode.DEL_FLAG_NORMAL));
		return traceCodeDao.find(page, dc);
	}

	/**
	 * 获取批次添加时生成的预览溯源码
	 * 
	 * @param traceCode
	 * @return
	 */
	public TraceCode getFirstTraceCode(String batchId) {
		String hql = " from TraceCode a where a.batchId=:p1 and a.states<>'D'";
		List<TraceCode> list = traceCodeDao.find(hql, new Parameter(batchId));
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	
	/**
	 * 获取删除批次添加时生成的预览溯源码
	 * 
	 * @param traceCode
	 * @return
	 */
	public TraceCode getDelFirstTraceCode(String batchId) {
		String hql = "from TraceCode a where a.batchId=:p1";
		List<TraceCode> list = traceCodeDao.find(hql, new Parameter(batchId));
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	@Transactional(readOnly = false)
	public void save(TraceCode traceCode) {
		traceCodeDao.save(traceCode);
	}

	@Transactional(readOnly = false)
	public void delete(String id) {
		TraceCode traceCode = traceCodeDao.get(id);
		traceCode.setUpdateTime(new Date());
		traceCode.setUpdateUserid(UserUtils.getUser().getId());
		traceCodeDao.save(traceCode);
		traceCodeDao.deleteByXGXTId(id);
	}

	public TraceCode getEntityByCode(String code) {
		return traceCodeDao.findByCode(code);
	}
	
	public TraceCode findByCodeAndStates(String code) {
		return traceCodeDao.findByCodeAndStates(code);
	}
	
	public void updateCodeStatus(String code) {
		String[] codeStr = code.split(",");
		for(int i=0;i<codeStr.length;i++){
			TraceCode traceCode = traceCodeDao.findByCode(codeStr[i]);
			if(StringUtils.isNotBlank(traceCode.getTraceCode())){
				traceCode.setStatus("2");
			}else{
				traceCode.setStatus("1");
			}
			traceCodeDao.save(traceCode);
		}
	}

	/**
	 * 批量保存溯源码
	 * 
	 * @param apply
	 * @param officeId
	 * @return
	 */
	@Transactional(readOnly = false)
	public boolean save(TraceLableApply apply, String officeId) {
		if (apply != null && apply.getApplyNum() != null) {
			try {
				int num = Integer.valueOf(apply.getApplyNum());
				List<TraceCode> list = new ArrayList<TraceCode>();
				Office office = officeDao.get(officeId);
				ProductBatch productBatch = new ProductBatch();
				String batchCode = "";
				if (StringUtils.isNotBlank(apply.getBatchId())) {
					productBatch = productBatchDao.get(apply.getBatchId());
					batchCode = productBatch.getBatchCode();
				}
				TraceProduct traceProduct = productDao.get(apply.getTraceProductId());
				String preTraceCode = this.preTraceCode(office.getCode(), traceProduct.getProductCode(), batchCode);
				// 获取最后8位最大值
				// int defaultInt = 0x10000000;
				// if(StringUtils.isNotBlank(apply.getBatchId())){
				// defaultInt = this.getMaxExtTraceCode(apply.getBatchId());
				// }else{
				// defaultInt = this.getMaxExtTraceCodeByProductId(apply.getTraceProductId());
				// }
				// 获取本产品最大流水号
				int serialNumber = 0;
				serialNumber = this.getMaxExtSerialNumByProductId(apply.getTraceProductId());
				StringBuffer sbf = new StringBuffer();
				boolean first = true;
				for (int i = 1; i <= num; i++) {
					TraceCode traceCode = new TraceCode();
					String code = "";
					// 6位公司编码+4位产品流水+批次号后10位+8位16进制随机码
					// if("0".equals(apply.getFlag())){//绑定批次
					// code = preTraceCode + (Integer.toHexString(defaultInt + i));
					// }else{//未绑定批次
					String lsh = (serialNumber + i) + "";
					for (int j = 0; j < 12 - lsh.length(); j++) {
						code += "0";
					}
					code = preTraceCode + code + lsh;
					// }
					// traceCode.setTraceCode(preTraceCode + (Integer.toHexString(defaultInt + i)));
					traceCode.setApplyId(apply.getId());
					traceCode.setOfficeId(officeId);
					traceCode.setBatchId(apply.getBatchId() == null ? "" : apply.getBatchId());
					traceCode.setCreatUserid(apply.getCreatUserid());
					StringBuffer sql = new StringBuffer();
					if (first) {
						// if("0".equals(apply.getFlag())){
						// sql.append("INSERT INTO
						// `t_trace_code_info`(id,trace_code,serial_number,apply_id,sys_id,office_id,batch_id,trace_type,trace_count,pack_type,anti_fake_code,STATUS,print_date,activation_date,activator,creat_time,creat_userid,update_time,update_userid,states,competitive)
						// VALUES ('").append(IdGen.uuid()).append("', '").append(code).append("',
						// 0,'").append(traceCode.getApplyId()).append("', null,
						// '").append(traceCode.getOfficeId()).append("',
						// '").append(traceCode.getBatchId()).append("', null, '0', null, null, null,
						// null, null, null, '").append(DateUtils.formatDate(traceCode.getCreatTime(),
						// "yyyy-MM-dd HH:mm:ss")).append("',
						// '").append(traceCode.getCreatUserid()).append("', null, null, 'A', '0')");
						// }else{
						sql.append(
								"INSERT INTO `t_trace_code_info`(id,trace_code,serial_number,apply_id,sys_id,office_id,batch_id,trace_type,trace_count,pack_type,anti_fake_code,STATUS,print_date,activation_date,activator,creat_time,creat_userid,update_time,update_userid,states,competitive) VALUES ('")
								.append(IdGen.uuid()).append("', '").append(code).append("', '")
								.append(serialNumber + i).append("' , '").append(traceCode.getApplyId())
								.append("', null, '").append(traceCode.getOfficeId()).append("', '")
								.append(traceCode.getBatchId())
								.append("', null, '0', null, null, null, null, null, null, '")
								.append(DateUtils.formatDate(traceCode.getCreatTime(), "yyyy-MM-dd HH:mm:ss"))
								.append("', '").append(traceCode.getCreatUserid()).append("', null, null, 'A', '0')");
						// }
						first = false;
					} else {
						// if("0".equals(apply.getFlag())){
						// sql.append(",('").append(IdGen.uuid()).append("', '").append(code).append("',
						// 0 ,'").append(traceCode.getApplyId()).append("', null,
						// '").append(traceCode.getOfficeId()).append("',
						// '").append(traceCode.getBatchId()).append("', null, '0', null, null, null,
						// null, null, null, '").append(DateUtils.formatDate(traceCode.getCreatTime(),
						// "yyyy-MM-dd HH:mm:ss")).append("',
						// '").append(traceCode.getCreatUserid()).append("', null, null, 'A', '0')");
						// }else{
						sql.append(",('").append(IdGen.uuid()).append("', '").append(code).append("', '")
								.append(serialNumber + i).append("' , '").append(traceCode.getApplyId())
								.append("', '0', '").append(traceCode.getOfficeId()).append("', '")
								.append(traceCode.getBatchId())
								.append("', null, '0', '1', null, null, null, null, null, '")
								.append(DateUtils.formatDate(traceCode.getCreatTime(), "yyyy-MM-dd HH:mm:ss"))
								.append("', '").append(traceCode.getCreatUserid()).append("', null, null, 'A', '0')");
						// }
					}
					sbf.append(sql);
					if (i % 250 == 0) {
						traceCodeDao.getSession().createSQLQuery(sbf.toString()).executeUpdate();
						first = true;
						sbf.setLength(0);
					}
					// traceCodeDao.save(traceCode);
				}
				if (sbf != null && sbf.length() > 0) {
					traceCodeDao.getSession().createSQLQuery(sbf.toString()).executeUpdate();
				}
			} catch (Exception e) {
				System.out.println(e.getMessage());
				return false;
			}
		}
		return true;
	}

	/**
	 * 获取溯源码前缀
	 * 
	 * @param officeCode
	 *            公司code
	 * @param productCode
	 *            产品code
	 * @param batchCode
	 *            批次code
	 * @return
	 */
	public String preTraceCode(String officeCode, String productCode, String batchCode) {
		String extProductCode = productCode.substring(productCode.length() - 10);
		String extBatchCode = "";
		// if(StringUtils.isNotBlank(batchCode)){
		// String [] arr = batchCode.split("-");
		// extBatchCode = arr[arr.length - 1].substring(2, 12);
		// }

		return officeCode + extProductCode + extBatchCode;
	}

	/**
	 * 根据批次获取最大溯源码后缀
	 * 
	 * @param batchId
	 * @return
	 */
	public int getMaxExtTraceCode(String batchId) {
		int defaultInt = 0x10000000;
		String qlString = "select  MAX(SUBSTR( trace_code,-8))  as code from t_trace_code_info where batch_id = '"
				+ batchId + "'";
		List<String> list = traceCodeDao.findBySql(qlString);
		if (list != null && list.size() > 0) {
			String code = (String) list.get(0);
			if (code != null && code.length() >= 8) {
				String subCode = code.substring(code.length() - 8);
				defaultInt = Integer.valueOf(subCode, 16);
			}
		}
		return defaultInt;
	}

	/**
	 * 根据产品获取最大溯源码后缀
	 * 
	 * @param productId
	 * @return
	 */
	public int getMaxExtTraceCodeByProductId(String productId) {
		int defaultInt = 0x10000000;
		String qlString = "select  MAX(SUBSTR( t1.trace_code,-8))  as code FROM t_trace_lable_apply t,t_trace_code_info t1 WHERE t.id = t1.apply_id AND t.trace_product_id =  '"
				+ productId + "'";
		List<String> list = traceCodeDao.findBySql(qlString);
		if (list != null && list.size() > 0) {
			String code = (String) list.get(0);
			if (code != null && code.length() >= 8) {
				String subCode = code.substring(code.length() - 8);
				defaultInt = Integer.valueOf(subCode, 16);
			}
		}
		return defaultInt;
	}

	/**
	 * 根据产品获取最大溯源码流水号
	 * 
	 * @param productId
	 * @return
	 */
	public int getMaxExtSerialNumByProductId(String productId) {
		int defaultInt = 0;
		String qlString = "select t1.serial_number FROM t_trace_lable_apply t,t_trace_code_info t1 WHERE t.id = t1.apply_id AND t.trace_product_id =  '"
				+ productId + "' ORDER BY t1.`serial_number` DESC";
		List<Integer> list = traceCodeDao.findBySql(qlString);
		if (list != null && list.size() > 0) {
			int code = list.get(0);
			defaultInt = code;
		}
		return defaultInt;
	}

	/**
	 * 获取产品已激活和未激活溯源码的区间信息
	 * 
	 * @param productId
	 * @return
	 */
	public List<CodeDataVO> getTraceCodeByProductId(String productId) {
		List<CodeDataVO> dataList = new ArrayList<CodeDataVO>();
		String qlString = " from TraceLableApply t where t.traceProductId = '" + productId + "' and t.states <> 'D'";
		List<TraceLableApply> applyList = traceCodeDao.find(qlString);
		for (int i = 0; i < applyList.size(); i++) {
			TraceLableApply traceLableApply = applyList.get(i);
			String batchId = traceLableApply.getBatchId();
			if (StringUtils.isNotBlank(batchId)) {
				String bqd = traceLableApplyDao.getbqd(traceLableApply.getId());
				if (StringUtils.isNotBlank(bqd)) {
					CodeDataVO data = new CodeDataVO();
					data.setActFlag(2);
					data.setMin(Integer.parseInt(bqd.split("-")[0]));
					data.setMax(Integer.parseInt(bqd.split("-")[1]));
					dataList.add(data);
				}
			} else {
				List<Integer> bdList = getBdBqd(traceLableApply.getId());
				String bdbqm = ApiUtil.getQjjhm(bdList);
				if (StringUtils.isNotBlank(bdbqm)) {
					String[] jhmArr = bdbqm.split(",");
					for (int j = 0; j < jhmArr.length; j++) {
						String jhm = jhmArr[j];
						if (jhm.contains("-")) {
							String[] arr = jhm.split("-");
							CodeDataVO data = new CodeDataVO();
							data.setActFlag(1);
							data.setMin(Integer.parseInt(arr[0]));
							data.setMax(Integer.parseInt(arr[1]));
							dataList.add(data);
						} else {
							CodeDataVO data = new CodeDataVO();
							data.setActFlag(1);
							data.setMin(Integer.parseInt(jhm));
							data.setMax(Integer.parseInt(jhm));
							dataList.add(data);
						}
					}
				}

				List<Integer> wbdList = getWbdBqd(traceLableApply.getId());
				String wbdbqm = ApiUtil.getQjjhm(wbdList);
				if (StringUtils.isNotBlank(wbdbqm)) {
					String[] jhmArr = wbdbqm.split(",");
					for (int j = 0; j < jhmArr.length; j++) {
						String jhm = jhmArr[j];
						if (jhm.contains("-")) {
							String[] arr = jhm.split("-");
							CodeDataVO data = new CodeDataVO();
							data.setActFlag(0);
							data.setMin(Integer.parseInt(arr[0]));
							data.setMax(Integer.parseInt(arr[1]));
							dataList.add(data);
						} else {
							CodeDataVO data = new CodeDataVO();
							data.setActFlag(0);
							data.setMin(Integer.parseInt(jhm));
							data.setMax(Integer.parseInt(jhm));
							dataList.add(data);
						}
					}
				}
			}
		}
		return dataList;
	}

	@Transactional(readOnly = false)
	public void activationTraceCode(String batchId, String productId, String paramString) {
		String qlString = "update t_trace_code_info t set t.batch_id = '' where t.batch_id ='" + batchId
				+ "' and t.states<>'D' "
				+ " and t.apply_id IN (select a.id FROM t_trace_lable_apply a where a.trace_product_id='" + productId
				+ "' and a.batch_id='' and a.states<>'D')";
		traceCodeDao.getSession().createSQLQuery(qlString).executeUpdate();

		String[] paramArr = paramString.split(",");
		for (int i = 0; i < paramArr.length; i++) {
			String[] numArr = paramArr[i].split("_");
			Integer min = Integer.parseInt(numArr[0]);
			Integer max = Integer.parseInt(numArr[1]);
			qlString = "update t_trace_code_info t set t.batch_id = '" + batchId + "' where t.serial_number >= " + min
					+ " and t.serial_number <= " + max + " and t.batch_id ='' and t.states<>'D' "
					+ " and t.apply_id IN (select a.id FROM t_trace_lable_apply a where a.trace_product_id='"
					+ productId + "' and a.batch_id='' and a.states<>'D')";
			traceCodeDao.getSession().createSQLQuery(qlString).executeUpdate();
		}
	}

	public String existSameBq(String batchId, String productId, String paramString) {
		String[] bqArr = paramString.split(",");
		String sql = "";
		int min = 0;
		int max = 0;
		String result = "";
		List<Object> list = new ArrayList<Object>();
		for (int i = 0; i < bqArr.length; i++) {
			String bqStr = bqArr[i];
			String[] bq = bqStr.split("_");
			min = Integer.parseInt(bq[0]);
			max = Integer.parseInt(bq[1]);
			sql = "select t.id from t_trace_code_info t where t.serial_number >= " + min + " and t.serial_number <= "
					+ max + " and t.states<>'D' "
					+ " and t.apply_id IN (select a.id from t_trace_lable_apply a where a.trace_product_id='"
					+ productId + "' and a.batch_id='' and a.states<>'D')";
			list = traceCodeDao.findBySql(sql);
			if (list.size() == 0) {
				result = "1" + "_" + min + "-" + max;
				break;
			}

			sql = "select t.id from t_trace_code_info t where t.serial_number >= " + min + " and t.serial_number <= "
					+ max + " and t.batch_id <> '" + batchId + "' and t.batch_id<>'' and t.states<>'D' "
					+ " and t.apply_id IN (select a.id from t_trace_lable_apply a where a.trace_product_id='"
					+ productId + "' and a.batch_id='' and a.states<>'D')";
			list = traceCodeDao.findBySql(sql);
			if (list.size() > 0) {
				result = "2" + "_" + min + "-" + max;
				break;
			}
		}
		return result;
	}

	public static void main(String[] args) {
		String aaa = "1100";
		String code = "";
		for (int i = 0; i < 18 - aaa.length(); i++) {
			code += "0";
		}
		System.out.println(code + aaa);
	}

	/**
	 * 溯源码列表
	 * 
	 * @param page
	 * @param productName
	 * @param officeId
	 * @return
	 */
	public Page<TraceCode> find(Page<TraceCode> page, String productName, String batchCode, String traceCode,
			String cropCode, String applyID, Integer serialNumberStart, Integer serialNumberEnd) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT");
		sqlString.append(" t.*, t1.batch_code,");
		sqlString.append(" t2.product_name");
		sqlString.append(" FROM");
		sqlString.append(" t_trace_code_info t");
		sqlString.append(" LEFT JOIN t_trace_product_batch t1 ON t1.id = t.batch_id");
		sqlString.append(" LEFT JOIN t_trace_product t2 ON t2.id = t1.product_id");
		sqlString.append(" where 1=1 and t.states !='D'");
		// TODO 防注入处理
		if (productName != null && !productName.equals("")) {
			sqlString.append(" and product_name like \"%" + productName + "%\"");
		}
		if (batchCode != null && !batchCode.equals("")) {
			sqlString.append(" and batch_code like \"%" + batchCode + "%\"");
		}
		if (traceCode != null && !traceCode.equals("")) {
			sqlString.append(" and trace_code like \"%" + traceCode + "%\"");
		}
		//if (cropCode != null && !cropCode.equals("")) {
		//	sqlString.append(" and t.office_id = \"" + cropCode + "\"");
		//}
		if (applyID != null && !applyID.equals("")) {
			sqlString.append(" and t.apply_id = \"" + applyID + "\"");
		}
		if (serialNumberStart != null && serialNumberEnd != null && serialNumberStart != 0 && serialNumberEnd != 0) {
			sqlString.append(" and t.serial_number BETWEEN " + serialNumberStart + " and " + serialNumberEnd);
		}
		sqlString.append(" order by trace_code");
		return traceCodeDao.findBySql(page, sqlString.toString(), TraceCodeVo.class);
	}
	
	/**
	 * 溯源码列表
	 * 
	 * @param page
	 * @param productName
	 * @param officeId
	 * @return
	 */
	public List<TraceCodeVo> findTraceCodeList(String applyID, Integer serialNumberStart, Integer serialNumberEnd) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT");
		sqlString.append(" t.*, t1.batch_code,");
		sqlString.append(" t2.product_name");
		sqlString.append(" FROM");
		sqlString.append(" t_trace_code_info t");
		sqlString.append(" LEFT JOIN t_trace_product_batch t1 ON t1.id = t.batch_id");
		sqlString.append(" LEFT JOIN t_trace_product t2 ON t2.id = t1.product_id");
		sqlString.append(" where 1=1 and t.states !='D'");
		
		if (applyID != null && !applyID.equals("")) {
			sqlString.append(" and t.apply_id = \"" + applyID + "\"");
		}
		if (serialNumberStart != null && serialNumberEnd != null && serialNumberStart != 0 && serialNumberEnd != 0) {
			sqlString.append(" and t.serial_number BETWEEN " + serialNumberStart + " and " + serialNumberEnd);
		}
		sqlString.append(" order by trace_code");
		return traceCodeDao.findBySql(sqlString.toString(),null, TraceCodeVo.class);
	}

	public Page<TraceCode> find(Page<TraceCode> page, String applyId) {
		DetachedCriteria dc = traceCodeDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceCode.FIELD_DEL_FLAG_XGXT, TraceCode.STATE_FLAG_DEL));
		if (StringUtils.isNotEmpty(applyId)) {
			dc.add(Restrictions.like("applyId", applyId));
		}
		dc.addOrder(Order.asc("traceCode"));
		return traceCodeDao.find(page, dc);
	}

	/**
	 * 获取该产品的申请标签数，激活数，未激活数
	 * 
	 * @param productId
	 * @return
	 */
	public List<Object> getBqsl(String productId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" select sum(c.apply_num) applyNum,sum(c.yjhs) yjhs,sum(c.wjhs) wjhs FROM (SELECT ");
		sqlString.append(" a.apply_num, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND b.batch_id<>'') yjhs, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND b.batch_id='') wjhs ");
		sqlString.append(" FROM ");
		sqlString.append(" t_trace_lable_apply a ");
		sqlString.append(" WHERE a.trace_product_id = :p1 ");
		sqlString.append(" AND a.states<>'D') c ");
		return traceCodeDao.findBySql(sqlString.toString(), new Parameter(productId));
	}
	
	/**
	 * 获取该产品的申请标签数，激活数，未激活数
	 * 
	 * @param productId
	 * @return
	 */
	public List<Object> newGetBqsl(String productId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" select sum(c.apply_num) applyNum,sum(c.yjhs) yjhs,sum(c.wjhs) wjhs FROM (SELECT ");
		sqlString.append(" a.apply_num, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND b.batch_id <> '') yjhs, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND b.batch_id = '') wjhs ");
		sqlString.append(" FROM ");
		sqlString.append(" t_trace_lable_apply a ");
		sqlString.append(" WHERE a.trace_product_id = :p1 AND a.sys_id='0' ");
		sqlString.append(" AND a.states<>'D') c ");
		sqlString.append(" UNION ");
		sqlString.append(" select sum(c.apply_num) applyNum,sum(c.yjhs) yjhs,sum(c.wjhs) wjhs FROM (SELECT ");
		sqlString.append(" a.apply_num, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND b.batch_id <> '') yjhs, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND b.batch_id = '') wjhs ");
		sqlString.append(" FROM ");
		sqlString.append(" t_trace_lable_apply a inner join t_trace_product_audit b on a.batch_id = b.audit_id and b.audit_state ='1' ");
		sqlString.append(" WHERE a.trace_product_id =:p1 AND (a.sys_id='1' or a.sys_id='2') ");
		sqlString.append(" AND a.states<>'D') c ");
		return traceCodeDao.findBySql(sqlString.toString(), new Parameter(productId));
	}
	
	/**
	 * 获取该产品的申请标签数，激活数，未激活数
	 * 
	 * @param productId
	 * @return
	 */
	public List<Object> newGetBqsl1(String productId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" select sum(c.apply_num) applyNum,sum(c.dsh) dsh,sum(c.yjhs) yjhs,sum(c.wjhs) wjhs FROM (SELECT ");
		sqlString.append(" a.apply_num, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND a.audit_flag = '0') dsh, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND a.audit_flag = '1') yjhs, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND a.audit_flag = '2') wjhs ");
		sqlString.append(" FROM ");
		sqlString.append(" t_trace_lable_apply a ");
		sqlString.append(" WHERE a.trace_product_id = :p1 AND a.sys_id='0' ");
		sqlString.append(" AND a.states<>'D') c ");
		return traceCodeDao.findBySql(sqlString.toString(), new Parameter(productId));
	}
	
	/**
	 * 获取该产品的申请标签数，激活数，未激活数
	 * 
	 * @param productId
	 * @return
	 */
	public List<Object> newGetBqsl2(String batchId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" select sum(c.apply_num) applyNum,sum(c.dsh) dsh,sum(c.yjhs) yjhs,sum(c.wjhs) wjhs FROM (SELECT ");
		sqlString.append(" a.apply_num, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND a.audit_flag = '0') dsh, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND a.audit_flag = '1') yjhs, ");
		sqlString.append(" (SELECT COUNT(b.id) FROM t_trace_code_info b WHERE b.apply_id=a.id AND b.states<>'D' AND a.audit_flag = '2') wjhs ");
		sqlString.append(" FROM ");
		sqlString.append(" t_trace_lable_apply a ");
		sqlString.append(" WHERE a.batch_id = :p1 AND a.sys_id='0' ");
		sqlString.append(" AND a.states<>'D') c ");
		return traceCodeDao.findBySql(sqlString.toString(), new Parameter(batchId));
	}

	/**
	 * 获取该批次的已激活码
	 * 
	 * @param productId
	 * @param batchId
	 * @return
	 */
	public List<Integer> getBqjhm(String productId, String batchId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT b.serial_number FROM t_trace_code_info b ");
		sqlString.append(" WHERE b.states <> 'D' AND b.batch_id = :p1 ");
		sqlString.append(" AND b.apply_id IN (SELECT a.id FROM t_trace_lable_apply a ");
		sqlString.append(" WHERE a.trace_product_id = :p2 AND a.states<>'D' AND a.batch_id='') ");
		sqlString.append(" ORDER BY b.serial_number ");
		return traceCodeDao.findBySql(sqlString.toString(), new Parameter(batchId, productId));
	}

	/**
	 * 已绑定标签数（不可改）
	 * 
	 * @param productId
	 * @param batchId
	 * @return
	 */
	public List<Integer> getYbdbqs(String productId, String batchId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" select count(b.id) from t_trace_code_info b ");
		sqlString.append(" WHERE b.states <> 'D' AND b.batch_id = :p1 ");
		sqlString.append(" AND b.apply_id IN (SELECT a.id FROM t_trace_lable_apply a ");
		sqlString.append(" WHERE a.trace_product_id = :p2 AND a.states<>'D' AND a.batch_id!='') ");
		sqlString.append(" ORDER BY b.serial_number ");
		return traceCodeDao.findBySql(sqlString.toString(), new Parameter(batchId, productId));
	}

	/**
	 * 获取绑定的标签段
	 * 
	 * @param applyId
	 * @return
	 */
	public List<Integer> getBdBqd(String applyId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT b.serial_number FROM t_trace_code_info b ");
		sqlString.append(" WHERE b.states <> 'D' AND b.batch_id !='' ");
		sqlString.append(" AND b.apply_id = :p1 ");
		sqlString.append(" ORDER BY b.serial_number ");
		return traceCodeDao.findBySql(sqlString.toString(), new Parameter(applyId));
	}

	/**
	 * 获取未绑定的标签段
	 * 
	 * @param applyId
	 * @return
	 */
	public List<Integer> getWbdBqd(String applyId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(" SELECT b.serial_number FROM t_trace_code_info b ");
		sqlString.append(" WHERE b.states <> 'D' AND b.batch_id = '' ");
		sqlString.append(" AND b.apply_id = :p1 ");
		sqlString.append(" ORDER BY b.serial_number ");
		return traceCodeDao.findBySql(sqlString.toString(), new Parameter(applyId));
	}

	/**
	 * 获取批次的所有标签明细列表
	 * 
	 * @param batchId
	 * @return
	 */
	public List<TraceCode> getLabelCodeListByBatchId(String batchId) {
		DetachedCriteria dc = traceCodeDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceCode.FIELD_DEL_FLAG_XGXT, TraceCode.STATE_FLAG_DEL));
		if (StringUtils.isNotBlank(batchId)) {
			dc.add(Restrictions.eq("batchId", batchId));
		}
		List<TraceCode> list = traceCodeDao.find(dc);
		return list;
	}

	public Integer getLabelCount(String officeId, String productId, String states) {
		String sql = "select b.id from t_trace_lable_apply a, t_trace_code_info b "
				+ "where a.id = b.apply_id and a.trace_product_id = :p1 ";
		if (StringUtils.isNotBlank(states) && states.equals("A")) {
			sql += " and a.states <> 'D' and b.states <> 'D'";
		}
		if (StringUtils.isNotBlank(states) && states.equals("D")) {
			sql += " and a.states = 'D' and b.states = 'D'";
		}
		List<String> list = productBatchDao.findBySql(sql, new Parameter(productId));
		return list.size();
	}

	/**
	 * 根据公司ID查询标签次数
	 * 
	 * @param officeId
	 * @param year
	 * @return
	 */
	public String getLableCount(String officeId, String year) {
		String sqlString = "SELECT COUNT(t.id) FROM t_trace_code_info t WHERE t.states<>'D'";
		if (StringUtils.isNotBlank(officeId)) {
			sqlString += " and t.office_id = '" + officeId + "'";
		}
		if (StringUtils.isNotBlank(year)) {
			sqlString += " and substr(t.creat_time,1,4) >= " + year;
		}
		List<Object> list = traceCodeDao.findBySql(sqlString);
		return list.get(0).toString();
	}

	@Transactional(readOnly = false)
	public void Activation(String code) {
		traceCodeDao.updateGXBZByCode(code, "A");
	}

	@Transactional(readOnly = false)
	public void Disabled(String code) {
		traceCodeDao.updateGXBZByCode(code, "D");
	}

	/**
	 * 已绑定标签数（不可改）
	 * 
	 * @param productId
	 * @param batchId
	 * @return
	 */
	public TraceLableApply getApplyLableByCode(String productId, String batchId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(
				" SELECT a.* FROM t_trace_lable_apply a WHERE a.states<>'D' AND a.trace_product_id=:p1 AND a.batch_id=:p2");
		List<TraceLableApply> list = traceLableApplyDao.findBySql(sqlString.toString(),
				new Parameter(productId, batchId), TraceLableApply.class);
		if (list.size() > 0) {
			return list.get(0);
		} else {
			return null;
		}
	}

	public List<TraceCode> getTraceCodeList(String parentId) {
		StringBuffer sqlString = new StringBuffer();
		sqlString.append(
				" SELECT ci.* FROM t_trace_code_info ci LEFT JOIN t_trace_code_relationship cr on ci.trace_code = cr.trace_code   WHERE parent_id = :p1 ");
		return traceCodeDao.findBySql(sqlString.toString(), new Parameter(parentId), TraceCode.class);
	}

}
