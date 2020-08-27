package com.surekam.modules.trace.TraceLableContent.dao;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.modules.trace.TraceLableContent.entity.TraceLableContent;

/**
 * 标签内容
 * @author wangyuewen
 */
@Repository
public class TraceLableContentDao extends BaseDao<TraceLableContent> {
	
	/**
	 * 根据标签id删除标签内容（物理删除）
	 * @param applyId
	 * @return
	 */
	public boolean deleteByApplyId(String applyId) {
		String qlString = "delete TraceLableContent where applyId = '" + applyId + "'";
		int n = this.update(qlString);
		if(n > 0) {
			return true;
		}
		return false;
	}
}
