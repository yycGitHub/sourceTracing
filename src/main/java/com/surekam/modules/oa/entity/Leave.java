/**
 * There are <a href="https://github.com/sureserve/surekam">surekam</a> code generation
 */
package com.surekam.modules.oa.entity;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.common.collect.Lists;
import com.surekam.common.persistence.DataEntity;
import com.surekam.common.utils.StringUtils;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.utils.DictUtils;
import com.surekam.modules.sys.utils.UserUtils;

/**
 * 请假Entity
 * 
 * @author liuj
 * @version 2013-04-05
 */

@Entity
@Table(name = "OA_LEAVE")
@DynamicInsert
@DynamicUpdate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Leave extends DataEntity<Leave> {

	private static final long serialVersionUID = 1L;
	private String reason; // 请假原因
	private String processInstanceId; // 流程实例编号
	private Date startTime; // 请假开始日期
	private Date endTime; // 请假结束日期
	private Date realityStartTime; // 实际开始时间
	private Date realityEndTime; // 实际结束时间
	private String leaveType; // 假种

	private String ids;
	private Date createDateStart;
	private Date createDateEnd;

	// -- 临时属性 --//
	// 流程任务
	private Task task;
	private Map<String, Object> variables;
	// 运行中的流程实例
	private ProcessInstance processInstance;
	// 历史的流程实例
	private HistoricProcessInstance historicProcessInstance;
	// 流程定义
	private ProcessDefinition processDefinition;

	public Leave() {
		super();
	}

	public Leave(String id) {
		super();
		this.id = id;
	}

	public String getLeaveType() {
		return leaveType;
	}

	public void setLeaveType(String leaveType) {
		this.leaveType = leaveType;
	}

	@Transient
	public String getLeaveTypeDictLabel() {
		return DictUtils.getDictLabel(leaveType, "oa_leave_type", "");
	}

	@Length(min = 1, max = 255)
	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getProcessInstanceId() {
		return processInstanceId;
	}

	public void setProcessInstanceId(String processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRealityStartTime() {
		return realityStartTime;
	}

	public void setRealityStartTime(Date realityStartTime) {
		this.realityStartTime = realityStartTime;
	}

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getRealityEndTime() {
		return realityEndTime;
	}

	public void setRealityEndTime(Date realityEndTime) {
		this.realityEndTime = realityEndTime;
	}

	@Transient
	public User getUser() {
		return createBy;
	}

	public void setUser(User user) {
		this.createBy = user;
	}

	@Transient
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
	}

	@Transient
	public Map<String, Object> getVariables() {
		return variables;
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	@Transient
	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	@Transient
	public HistoricProcessInstance getHistoricProcessInstance() {
		return historicProcessInstance;
	}

	public void setHistoricProcessInstance(HistoricProcessInstance historicProcessInstance) {
		this.historicProcessInstance = historicProcessInstance;
	}

	@Transient
	public ProcessDefinition getProcessDefinition() {
		return processDefinition;
	}

	public void setProcessDefinition(ProcessDefinition processDefinition) {
		this.processDefinition = processDefinition;
	}

	@Transient
	public String getIds() {
		List<String> idList = Lists.newArrayList();
		if (StringUtils.isNotBlank(ids)) {
			String ss = ids.trim().replace("　", ",").replace(" ", ",").replace("，", ",").replace("'", "");
			for (String s : ss.split(",")) {
				// if(s.matches("\\d*")) {
				idList.add("'" + s + "'");
				// }
			}
		}
		return StringUtils.join(idList, ",");
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	@Transient
	public Date getCreateDateStart() {
		return createDateStart;
	}

	public void setCreateDateStart(Date createDateStart) {
		this.createDateStart = createDateStart;
	}

	@Transient
	public Date getCreateDateEnd() {
		return createDateEnd;
	}

	public void setCreateDateEnd(Date createDateEnd) {
		this.createDateEnd = createDateEnd;
	}

	/**
	 * 插入之前执行方法，需要手动调用
	 */
	public void preInsert() {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getId())) {
			this.updateBy = user;
			this.createBy = user;
		}
		this.updateDate = new Date();
		this.createDate = this.updateDate;
	}

	/**
	 * 更新之前执行方法，需要手动调用
	 */
	@Override
	public void preUpdate() {
		User user = UserUtils.getUser();
		if (StringUtils.isNotBlank(user.getId())) {
			this.updateBy = user;
		}
		this.updateDate = new Date();
	}

}
