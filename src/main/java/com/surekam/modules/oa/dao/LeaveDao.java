package com.surekam.modules.oa.dao;

import org.springframework.stereotype.Repository;

import com.surekam.common.persistence.BaseDao;
import com.surekam.common.persistence.Parameter;
import com.surekam.modules.oa.entity.Leave;

/**
 * 请假DAO接口
 */
@Repository
public class LeaveDao extends BaseDao<Leave> {
	
	/**
	 * 更新流程实例ID
	 * @param leave
	 * @return
	 */
	public int updateProcessInstanceId(Leave leave){
		return this.updateBySql("UPDATE oa_leave SET process_instance_id = :p1 WHERE id = :p2"
				,new Parameter(leave.getProcessInstanceId(),leave.getId()));
	};
	
	/**
	 * 更新实际开始结束时间
	 * @param leave
	 * @return
	 */
	public int updateRealityTime(Leave leave){
		return this.updateBySql("UPDATE oa_leave "
				+ "SET reality_start_time = :p1,reality_end_time = :p2, "
				+ "update_by = :p3,update_date = :p4,"
				+ "remarks = :p5 WHERE id = :p6"
				,new Parameter(leave.getRealityStartTime(),leave.getRealityEndTime(),leave.getUpdateBy().getId(),leave.getUpdateDate()
						,leave.getRemarks(),leave.getId()));
	};
	
}
