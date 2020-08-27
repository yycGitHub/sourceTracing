package com.surekam.modules.traceresumetheme.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.surekam.common.config.Global;
import com.surekam.common.persistence.Page;
import com.surekam.common.persistence.Parameter;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.traceresumetheme.entity.TraceResumeTheme;
import com.surekam.modules.api.utils.DeCompressUtil;
import com.surekam.modules.sys.utils.UserUtils;
import com.surekam.modules.traceproduct.entity.TraceProduct;
import com.surekam.modules.traceresumetheme.dao.TraceResumeThemeDao;

/**
 * 溯源产品主题管理Service
 * @author liw
 * @version 2018-08-23
 */
@Component
@Transactional(readOnly = true)
public class TraceResumeThemeService extends BaseService {

	@Autowired
	private TraceResumeThemeDao traceResumeThemeDao;
	
	public TraceResumeTheme get(String id) {
		return traceResumeThemeDao.get(id);
	}
	
	public Page<TraceResumeTheme> find(Page<TraceResumeTheme> page, String themeName, String themeType, String status, String templet_product_id, String officeId) {
		DetachedCriteria dc = traceResumeThemeDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(themeName)){
			dc.add(Restrictions.like("themeName", themeName, MatchMode.ANYWHERE));
		}
		if(StringUtils.isNotEmpty(themeType)){
			dc.add(Restrictions.eq("themeType", themeType));
		}
		if(StringUtils.isNotEmpty(status)){
			dc.add(Restrictions.eq("status", status));
		}
		if(StringUtils.isNotBlank(templet_product_id)) {
			dc.add(Restrictions.eq("templet_product_id", templet_product_id));
		}
		if(StringUtils.isNotEmpty(officeId)){
			dc.add(Restrictions.eq("officeId", officeId));
		}
		dc.addOrder(Order.asc("themeCode"));
		return traceResumeThemeDao.find(page, dc);
	}
	
	public List<TraceResumeTheme> findAllThemes(String officeId) {
		DetachedCriteria dc = traceResumeThemeDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("status", TraceProduct.OPEN));
		if(StringUtils.isNotBlank(officeId)){
			//dc.add(Restrictions.eq("officeId", officeId));
			dc.add(Restrictions.in("officeId", new String[]{officeId,Global.CONTROLLING_OFFICE_ID}));
		}
		return traceResumeThemeDao.find(dc);
	}
	
	/**
	 * 根据主题名称、templet_product_id查询主题
	 * @param themeName
	 * @param templet_product_id
	 * @return
	 */
	public TraceResumeTheme find(String themeName, String templet_product_id) {
		DetachedCriteria dc = traceResumeThemeDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceResumeTheme.FIELD_DEL_FLAG_XGXT, TraceResumeTheme.STATE_FLAG_DEL));
		if(StringUtils.isNotEmpty(themeName)){
			dc.add(Restrictions.eq("themeName", themeName));
		}
		if(StringUtils.isNotEmpty(templet_product_id)){
			dc.add(Restrictions.eq("templet_product_id", templet_product_id));
		}
		List<TraceResumeTheme> list = traceResumeThemeDao.find(dc);
		if(list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}
	/**
	 * 获取公司的主题以及农科院信息所公司对应开启状态的通用主题
	 * @param officeId
	 * @return
	 */
	public List<TraceResumeTheme> findCommonAndOwnedThemes(String officeId) {
		DetachedCriteria dc = traceResumeThemeDao.createDetachedCriteria();
		dc.add(Restrictions.ne(TraceProduct.FIELD_DEL_FLAG_XGXT, TraceProduct.STATE_FLAG_DEL));
		dc.add(Restrictions.eq("status", TraceProduct.OPEN));
		if(StringUtils.isNotBlank(officeId)){
			String[] targetOfficeArray = {officeId,Global.CONTROLLING_OFFICE_ID};
			dc.add(Restrictions.in("officeId",targetOfficeArray));
		}
		return traceResumeThemeDao.find(dc);
	}
	
	public TraceResumeTheme findTraceResumeThemeByCode(String themeCode) {
		List<TraceResumeTheme> list = traceResumeThemeDao.find("from TraceResumeTheme a where a.states<>'D' and a.themeCode=:p1", new Parameter(themeCode));
		if(list.size()>0){
			return list.get(0);
		}else{
			return new TraceResumeTheme();
		}
	}
	
	@Transactional(readOnly = false)
	public void save(TraceResumeTheme traceResumeTheme) {
		if(StringUtils.isNotBlank(traceResumeTheme.getId())){
			traceResumeTheme.setStates("U");
		}
		traceResumeThemeDao.save(traceResumeTheme);
	}
	
	@Transactional(readOnly = false)
	public void enable(String id) {
		TraceResumeTheme theme = traceResumeThemeDao.get(id);
		theme.setStatus(TraceResumeTheme.OPEN);
		theme.setUpdateTime(new Date());
		theme.setUpdateUserid(UserUtils.getUser().getId());
		traceResumeThemeDao.save(theme);
	}
	
	@Transactional(readOnly = false)
	public void disable(String id) {
		TraceResumeTheme theme = traceResumeThemeDao.get(id);
		theme.setUpdateTime(new Date());
		theme.setUpdateUserid(UserUtils.getUser().getId());
		theme.setStatus(TraceResumeTheme.CLOSE);
		traceResumeThemeDao.save(theme);
	}
	
	@Transactional(readOnly = false)
	public String delete(String id) {
		TraceResumeTheme theme = traceResumeThemeDao.get(id);
		
		theme.setUpdateTime(new Date());
		theme.setUpdateUserid(UserUtils.getUser().getId());
		//被产品使用的主题不能删除
		if(theme.getTemplet_product_id() != null) {
					return "1";
		}
		traceResumeThemeDao.save(theme);
		traceResumeThemeDao.deleteByXGXTId(id);
		return "0";
	}
	
	public String getMaxThemeCode(String type){
		List<String> list = traceResumeThemeDao.find("select max(t.themeCode) from TraceResumeTheme t where t.states!='D' and t.themeCode like '"+type+"%'");
		if(list.size()>0){
			return list.get(0);
		}else{
			return "";
		}
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
			result = "/upload/theme_model/"+new_name;
			// 转存文件 
			file.transferTo(new File(path+new_name));
		}
		return result;
	}
	
}
