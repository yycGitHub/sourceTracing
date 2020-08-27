package com.surekam.modules.act.dao;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.modules.act.entity.Act;

/**
 * 审批DAO接口
 */
@Repository
public class ActDao extends BaseDao<Act> {

	public int updateProcInsIdByBusinessId(Act act){
		String sql = "UPDATE "+act.getBusinessTable()+" SET proc_ins_id = '"+act.getProcInsId()+"' WHERE id = '"+act.getBusinessId()+"'";
		return this.updateBySql(sql,null);
	};
	
}
