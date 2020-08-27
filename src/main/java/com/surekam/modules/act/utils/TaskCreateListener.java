package com.surekam.modules.act.utils;

import java.util.ArrayList;
import java.util.List;

import org.activiti.engine.HistoryService;
import org.activiti.engine.delegate.event.ActivitiEntityEvent;
import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.impl.task.TaskDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import com.surekam.modules.act.service.EventHandler;
import com.surekam.modules.sys.entity.Role;
import com.surekam.modules.sys.entity.User;
import com.surekam.modules.sys.service.SystemService;


public class TaskCreateListener implements EventHandler {

	@Autowired
	private SystemService systemService;
	@Autowired
	private HistoryService historyService;
	
	@Override
	public void handle(ActivitiEvent event) {
		ActivitiEntityEvent entityEvent = (ActivitiEntityEvent) event;
		TaskEntity taskEntity = (TaskEntity) entityEvent.getEntity();
		TaskDefinition taskDefinition =  taskEntity.getTaskDefinition();
		Object[] groupArr = taskDefinition.getCandidateGroupIdExpressions().toArray();
		ArrayList<User> users = new ArrayList<User>();
		//查询流程信息
		HistoricProcessInstance hi =  historyService.createHistoricProcessInstanceQuery().processInstanceId(event.getProcessInstanceId()).singleResult();;
		if("lcfqr".equals(taskEntity.getAssignee()))
		{
			taskEntity.setAssignee(hi.getStartUserId());
		}
      	if (groupArr != null && groupArr.length >0) {
  			for (int i=0;i<groupArr.length;i++) {
  					// 根据组获得对应人员
  					Role role = systemService.getRole(groupArr[i].toString());
  					if(role != null)
  					{
    					List<User> userList = role.getUserList();
    					if (userList != null && userList.size() > 0)
    						users.addAll(userList);
  					}
  			}
  			//候选用户数为1时直接设置为代理人
  	      	if(users.size() == 1)
  	      	{
  	      		taskEntity.setAssignee(users.get(0).getId());
  	      		for (int i=0;i<groupArr.length;i++) {
  	      			taskEntity.deleteCandidateGroup(groupArr[i].toString());
  	      		}
  	      	}

  		}
	}

}
