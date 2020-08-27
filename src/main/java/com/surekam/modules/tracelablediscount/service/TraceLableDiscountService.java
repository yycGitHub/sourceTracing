package com.surekam.modules.tracelablediscount.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.util.HSSFColor.BLACK;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.tracelablediscount.entity.DiscountPriceVO;
import com.surekam.modules.tracelablediscount.entity.TraceLableDiscount;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.TraceDeliveryAddress.entity.TraceDeliveryAddress;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.trace.TraceLableApply.entity.TraceLableApply;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;
import com.surekam.modules.trace.TraceLableTemplate.dao.TraceLableTemplateDao;
import com.surekam.modules.trace.TraceLableTemplate.entity.TraceLableTemplate;
import com.surekam.modules.tracelablediscount.dao.TraceLableDiscountDao;

/**
 * 标签折扣优惠Service
 * @author xy
 * @version 2018-11-22
 */
@Component
@Transactional(readOnly = true)
public class TraceLableDiscountService extends BaseService {

	@Autowired
	private TraceLableDiscountDao traceLableDiscountDao;
	
	@Autowired
	private TraceLableTemplateDao traceLableTemplateDao;
	
	public TraceLableDiscount get(String id) {
		return traceLableDiscountDao.get(id);
	}
	
	public Page<TraceLableDiscount> find(Page<TraceLableDiscount> page, String labelTemplateId,String corpCode,User user) {
		
	/*	DetachedCriteria dc = traceLableDiscountDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceLableDiscount.FIELD_DEL_FLAG_XGXT, TraceLableDiscount.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(labelTemplateId)) {
			dc.add(Restrictions.eq("labelTemplateId",labelTemplateId));
		}
		dc.addOrder(Order.desc("label_template_id, discount"));
		Page<TraceLableDiscount> page2 = traceLableDiscountDao.find(page, dc);*/
		Page<TraceLableDiscount> page2 = traceLableDiscountDao.findBySql(page,getSql(labelTemplateId,corpCode,user),TraceLableDiscount.class);
		if(null != page2.getList() && 0 != page2.getList().size()) {
			for (Iterator<TraceLableDiscount> iterator = page2.getList().iterator(); iterator.hasNext();) {
				TraceLableDiscount traceLableDiscount1 = iterator.next();
				if(StringUtils.isNotBlank(traceLableDiscount1.getLabelTemplateId())) {
					traceLableDiscount1.setLabelTemplateName(traceLableTemplateDao.get(traceLableDiscount1.getLabelTemplateId()).getLabelTemplateName());
					traceLableDiscount1.setLabelTemplateOfficeId(traceLableTemplateDao.get(traceLableDiscount1.getLabelTemplateId()).getOfficeId());
				}
			}
		}
		/*Collections.sort(page2.getList(), new Comparator<TraceLableDiscount>() {  
            @Override  
            public int compare(TraceLableDiscount o1, TraceLableDiscount o2) {  
                int i = (Integer.parseInt(o1.getLabelTemplateId())-Integer.parseInt(o2.getLabelTemplateId()));  
                if(i == 0){  
                    //return Integer.parseInt(o1.getNumMin()) - Integer.parseInt(o2.getNumMin());  
                	return (Integer.parseInt(o1.getNumMax())-Integer.parseInt(o2.getNumMax()));  
                } 
                return i;  
            }  
        });*/
		return page2;
	}
	public String getSql(String labelTemplateId,String corpCode,User user) {
		StringBuffer sql = new StringBuffer(500);
		if(StringUtils.isNotBlank(labelTemplateId)) {
			sql.append("SELECT * FROM t_trace_lable_discount WHERE label_template_id = " + labelTemplateId);
			sql.append(" and states <> 'D'");
			sql.append(" ORDER BY label_template_id, num_min asc ");
		}else {
			if(StringUtils.isNotBlank(corpCode)) {
				sql.append("SELECT d.* FROM t_trace_lable_discount  d LEFT JOIN t_trace_lable_template t on d.label_template_id = t.id WHERE t.office_id = '" + corpCode + "' ");
				sql.append(" and d.states <> 'D' and t.states <> 'D'");
				sql.append("ORDER BY d.label_template_id, d.num_min asc ");
			}else {
				sql.append("SELECT d.* FROM t_trace_lable_discount  d LEFT JOIN t_trace_lable_template t on d.label_template_id = t.id ");
				sql.append(" WHERE 1=1 ");
				if(!user.isAdmin()) {
					corpCode = user.getCompany().getId();
					sql.append(" and t.office_id = '" + corpCode + "' ");
				}
				sql.append(" and d.states <> 'D' and t.states <> 'D'");
				sql.append(" ORDER BY d.label_template_id, d.num_min asc ");
			}
		}
		return sql.toString();
		
	}
	public List<TraceLableDiscount> find(String id){
		DetachedCriteria dc = traceLableDiscountDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceLableDiscount.FIELD_DEL_FLAG_XGXT, TraceLableDiscount.STATE_FLAG_DEL));
		if(StringUtils.isNotBlank(id)) {
			dc.add(Restrictions.eq("labelTemplateId",id));
		}
		dc.addOrder(Order.asc("id"));
		List<TraceLableDiscount> list = traceLableDiscountDao.find(dc);
		if(null != list && 0 != list.size()) {
			for (Iterator<TraceLableDiscount> iterator = list.iterator(); iterator.hasNext();) {
				TraceLableDiscount traceLableDiscount = iterator.next();
				if(StringUtils.isNotBlank(traceLableDiscount.getLabelTemplateId())) {
					traceLableDiscount.setLabelTemplateName(traceLableTemplateDao.get(traceLableDiscount.getLabelTemplateId()).getLabelTemplateName());
				}
				
			}
		}
		Collections.sort(list, new Comparator<TraceLableDiscount>() {  
			  
            @Override  
            public int compare(TraceLableDiscount o1, TraceLableDiscount o2) {  
                int i = Integer.parseInt(o1.getLabelTemplateId()) - Integer.parseInt(o2.getLabelTemplateId());  
                if(i == 0){  
                    return Integer.parseInt(o1.getNumMin()) - Integer.parseInt(o2.getNumMin());  
                } 
                return i;  
            }  
        });  
		return list;
	}
	
	/**
	 * 总价计算
	 * @param labletemplateId
	 * @param applyNum
	 * @return
	 */
	public DiscountPriceVO calculatedTotalPrice(String labletemplateId, String applyNum) {
		DiscountPriceVO discountPriceVO = new DiscountPriceVO();
		//获取优惠列表
		List<TraceLableDiscount> list = new ArrayList<TraceLableDiscount>();
		//获取标签单价
		TraceLableTemplate traceLableTemplate = new TraceLableTemplate();
		if(StringUtils.isNotBlank(labletemplateId)) {
			//查询优惠折扣列表
			list = this.find(labletemplateId);
			//查询标签模板，获取标签单价
			traceLableTemplate = traceLableTemplateDao.get(labletemplateId);
		}
		if(StringUtils.isNotBlank(applyNum)) {
			//数量
			Double num = Double.parseDouble(applyNum);
			//单价
			Double unitPrice = traceLableTemplate.getLableUnitPrice().doubleValue();
			
			if(list != null && list.size() > 0) {
				for(int i = 0; i < list.size(); i++) {
					if(num >= Long.parseLong(list.get(i).getNumMin()) && num < Long.parseLong(list.get(i).getNumMax())){
						discountPriceVO.setCurrentTotalPrice(multiply(num,unitPrice,list.get(i).getDiscount(),2));
						discountPriceVO.setCurrentDiscount(list.get(i).getDiscount());
						break;
					}else if(num >= Long.parseLong(list.get(list.size()-1).getNumMax())) {
						discountPriceVO.setCurrentTotalPrice(multiply(num,unitPrice,list.get(list.size()-1).getDiscount(),2));
						discountPriceVO.setCurrentDiscount(list.get(list.size()-1).getDiscount());
						break;
					}
				}
			}
		}
		return discountPriceVO;
	}
	/**
	 * 计算
	 */
	public static Double multiply(Double val1,Double val2,Double val3,int scale){
		if(null == val1){
		val1 = new Double(0);
		}
		if(null == val2){
		val2 = new Double(0);
		}
		if(null == val3){
			val3 = new Double(0);
			}
		return new BigDecimal(Double.toString(val1)).multiply(new BigDecimal(Double.toString(val2))).multiply(new BigDecimal(Double.toString(val3))).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	
	public String getSql(String id) {
		StringBuffer sql = new StringBuffer(200);
		sql.append("SELECT * FROM t_trace_lable_discount ");
		if(StringUtils.isNotBlank(id)) {
			sql.append(" WHERE labelTemplateId = " + id);
		}
		sql.append(" order by id asc ");
		String str = new String(sql); 
		return str;
		
	}
	
	@Transactional(readOnly = false)
	public void save(TraceLableDiscount traceLableDiscount) {
		traceLableDiscountDao.save(traceLableDiscount);
	}
	
	@Transactional(readOnly = false)
	public void delete(String id) {
		traceLableDiscountDao.deleteByXGXTId(id);
	}

	
}
