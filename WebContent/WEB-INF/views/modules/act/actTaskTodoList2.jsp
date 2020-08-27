<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>计划发布流程待办任务</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		/**
		 * 签收任务
		 */
		function claim(taskId) {
			$.get('${ctx}/act/task/claim' ,{taskId: taskId}, function(data) {
				if (data == 'true'){
		        	top.$.jBox.tip('签收完成');
		            location = '${ctx}/act/task/todo/';
				}else{
		        	top.$.jBox.tip('签收失败');
				}
		    });
		}
		function page(n,s){
        	location = '${ctx}/act/task/todo/?pageNo='+n+'&pageSize='+s;
        }
	</script>
</head>
<body>
	<div class="miandody">
	  <div class="con">
	    <div class="con_nav">
	    	<ul class="fl">
		        <li><a href="${ctx}/act/task/todo?type=1">计划上报待办任务</a></li>
		        <li><a class="cur" href="${ctx}/act/task/todo?type=2">计划发布待办任务</a></li>
		        <li><a href="${ctx}/act/task/todo?type=3">计划考核待办任务</a></li>
		        <li><a href="${ctx}/act/task/todo?type=4">计划调整待办任务</a></li>
		        <li><a href="${ctx}/act/task/historic/">历史任务</a></li>
	      	</ul>
	    </div>
	    <form:form id="searchForm" modelAttribute="act" action="${ctx}/act/task/todo?type=2" method="get" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<form:hidden path="type" value="2"/>
		<tags:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
	    <div class="search">
	    	<span class="fl">
				<input type="text" id="beginDatePicker"  value="${fns:getDateString(act.beginDate)}" readonly="readonly" placeholder="开始时间"
				onclick="WdatePicker({maxDate:'#F{$dp.$D(\'endDatePicker\')}',dateFmt:'yyyy-MM-dd',vel:'beginDate',realDateFmt:'yyyyMMdd'})" class="Wdate input_txt w200">
				<input type="hidden" id="beginDate" name="beginDate" value="${act.beginDate }" >
				
				<input type="text" id="endDatePicker"  value="${fns:getDateString(act.endDate)}" readonly="readonly" placeholder="结束时间"
				onclick="WdatePicker({minDate:'#F{$dp.$D(\'beginDatePicker\')}',dateFmt:'yyyy-MM-dd',vel:'endDate',realDateFmt:'yyyyMMdd'})" class="Wdate input_txt w200">
				<input type="hidden" id="endDate" name="endDate" value="${act.endDate }">
	    	</span>
	      	<span><input id="btnSubmit" class="btn btn_primary" name="" type="submit" value="查询"></span>
		</div>
		</form:form>
	    <tags:message content="${message}"/>
	    <div class="tabcon">
	      <table class="tab" cellpadding="0" cellspacing="0">
	        <thead>
	          <tr>
	            <th>任务标题</th>
	            <th>当前环节</th>
				<th>流程名称</th>
				<th>流程版本</th>
				<th class="sort-column ksrq">创建时间</th>
				<th>操作</th>
	          </tr>
	        </thead>
	        <tbody>
	         <c:forEach items="${list}" var="act">
				<c:set var="task" value="${act.task}" />
				<c:set var="vars" value="${act.vars}" />
				<c:set var="procDef" value="${act.procDef}" /><%--
				<c:set var="procExecUrl" value="${act.procExecUrl}" /> --%>
				<c:set var="status" value="${act.status}" />
					<tr>
						<td>
							<c:if test="${empty task.assignee}">
								<a href="javascript:claim('${task.id}');" 
								title="签收任务">${fns:abbr(not empty act.vars.map.title ? act.vars.map.title : task.id, 60)}</a>
							</c:if>
							<c:if test="${not empty task.assignee}">
								<a href="${ctx}/act/task/form?taskId=${task.id}&taskName=${fns:urlEncode(task.name)}&taskDefKey=${task.taskDefinitionKey}&procInsId=${task.processInstanceId}&procDefId=${task.processDefinitionId}&status=${status}">
								${fns:abbr(not empty vars.map.title ? vars.map.title : task.id, 60)}</a>
							</c:if>
						</td>
						<td>
							<a target="_blank" 
								href="${pageContext.request.contextPath}/act/diagram-viewer?processDefinitionId=${task.processDefinitionId}&processInstanceId=${task.processInstanceId}">
								${task.name}</a>
						</td>
						<td>${procDef.name}</td>
						<td>V: ${procDef.version}</td>
						<td>${fns:getDateString(task.createTime)}</td>
						<td>
							<c:if test="${empty task.assignee}">
								<a href="javascript:claim('${task.id}');">签收任务</a>
							</c:if>
							<c:if test="${not empty task.assignee}"><%--
								<a href="${ctx}${procExecUrl}/exec/${task.taskDefinitionKey}?procInsId=${task.processInstanceId}&act.taskId=${task.id}">办理</a> --%>
								<a href="${ctx}/act/task/form?taskId=${task.id}&taskName=${fns:urlEncode(task.name)}&taskDefKey=${task.taskDefinitionKey}&procInsId=${task.processInstanceId}&procDefId=${task.processDefinitionId}&status=${status}">任务办理</a>
							</c:if>
							<shiro:hasPermission name="act:process:edit">
								<c:if test="${empty task.executionId}">
									<a href="${ctx}/act/task/deleteTask?taskId=${task.id}&reason=" onclick="return promptx('删除任务','删除原因',this.href);">删除任务</a>
								</c:if>
							</shiro:hasPermission>
							<a target="_blank" href="${pageContext.request.contextPath}/act/diagram-viewer?processDefinitionId=${task.processDefinitionId}&processInstanceId=${task.processInstanceId}">跟踪</a><%-- 
							<a target="_blank" href="${ctx}/act/task/trace/photo/${task.processDefinitionId}/${task.executionId}">跟踪2</a> 
							<a target="_blank" href="${ctx}/act/task/trace/info/${task.processInstanceId}">跟踪信息</a> --%>	
						</td>
					</tr>
				</c:forEach>
	        </tbody>
	        <tfoot>
			<td colspan="40">
				<div class="fr">
					<div class="page">${page}</div>
				</div>
			</td>
			</tfoot>
	      </table>
	    </div>
	  </div>
	</div>
</body>
</html>
