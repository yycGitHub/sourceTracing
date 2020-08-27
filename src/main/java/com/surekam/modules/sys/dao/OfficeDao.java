/**
 * Copyright &copy; 2012-2013 <a href="https://github.com/sureserve/surekam">surekam</a> All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.surekam.modules.sys.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.sys.entity.Office;

/**
 * 机构DAO接口
 * 
 * @author sureserve
 * @version 2013-8-23
 */
@Repository
public class OfficeDao extends BaseDao<Office> {

	public List<Office> findByParentIdsLike(String parentIds) {
		return find("from Office where parentIds like :p1", new Parameter(parentIds));
	}
	
	public List<Office> findAllOfficesAndId(String officeid, String id) {
		String qlString = "SELECT t.* FROM sys_office t WHERE (t.PARENT_IDS like '%," + officeid + ",%' OR t.id = '" + id + "' ) AND t.DEL_FLAG != '1' ";
		return findBySql(qlString, null, Office.class);
	}

	// @Query("from Office where (id=?1 or parent.id=?1 or parentIds like ?2) and
	// delFlag='" + Office.DEL_FLAG_NORMAL + "' order by code")
	// public List<Office> findAllChild(Long parentId, String likeParentIds);

	public Office getByKuid(String kuid) {
		return getByHql("from Office where kuid =:p1", new Parameter(kuid));
	}
}
