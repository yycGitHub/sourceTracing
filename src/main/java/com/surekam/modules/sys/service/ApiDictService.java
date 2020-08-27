package com.surekam.modules.sys.service;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.surekam.common.persistence.Page;
import com.surekam.common.service.BaseService;
import com.surekam.common.utils.CacheUtils;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.sys.dao.DictDao;
import com.surekam.modules.sys.entity.Dict;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.utils.DictUtils;

/**
 * 字典Service
 * @author 腾农科技
 *
 */
@Service
@Transactional(readOnly = true)
public class ApiDictService extends BaseService {
	
	@Autowired
	private DictDao dictDao;
	
	/**
	 * 查询字典
	 * @param id
	 * @return
	 */
	@Transactional(readOnly = false)
	public Dict get(String id) {
		return dictDao.get(id);
	}
	
	
	/**
	 * 保存字典
	 * @param dict
	 */
	@Transactional(readOnly = false)
	public void saveDict(Dict dict) {
		dictDao.save(dict);
	}
	
	/**
	 * 删除字典
	 */
	@Transactional(readOnly = false)
	public void delete(String id) {
		dictDao.deleteById(id);
		CacheUtils.remove(DictUtils.CACHE_DICT_MAP);
	}
	
	/**
	 * 更新 标记:delFlag
	 * @param id
	 * @param delFlag
	 */
	@Transactional(readOnly = false)
	public void dictChangeState(String id,String delFlag) {
		dictDao.updateDelFlag(id, delFlag);
	}

	/**
	 * 分页查询字典
	 * @param page
	 * @param currentUser
	 * @param label
	 * @param delFlag
	 * @return
	 */
	public Page<Dict> findPageDictlist(Page<Dict> page, User currentUser,
			String label, String delFlag) {
		DetachedCriteria dc = dictDao.createDetachedCriteria();
		if(label != null && StringUtils.isNotBlank(label)){
			dc.add(Restrictions.like("label", "%" + label + "%"));
		}
		if(!currentUser.isAdmin()){
			dc.add(dataScopeFilter(currentUser, dc.getAlias(), ""));
		}
		if(delFlag != null && StringUtils.isNotBlank(delFlag)){
			dc.add(Restrictions.eq(Dict.FIELD_DEL_FLAG, delFlag));
		}else{
			//dc.add(Restrictions.eq(Dict.FIELD_DEL_FLAG, Dict.DEL_FLAG_NORMAL));
		}
		return dictDao.find(page, dc);
	}
	

}
