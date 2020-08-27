<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>已办任务</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
        	location = '${ctx}/act/task/historic/?pageNo='+n+'&pageSize='+s;
        }
	</script>
</head>
<body>
	<div class="miandody">
	  <div class="crumb">
	  	<i class="iconfont">&#xe60d;</i><strong>个人办公</strong><span class="songti">&gt;</span><span>历史任务</span>
	  </div>
	  <div class="con">
	    <div class="con_nav">
	      	<ul class="fl">
		        <li><a class="cur" href="${ctx}/act/task/historic/">历史任务</a></li>
	      	</ul>
	    </div>
	    <form:form id="searchForm" modelAttribute="act" action="${ctx}/act/task/historic/" method="get" class="breadcrumb form-search">
	   	<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
	    <div class="search">
	         <span class="drop_down_triangle"> 
			    <form:select path="procDefKey" class="drop_down gray w200">
					<form:option value="" label="选择流程类别"/>
					<form:options items="${fns:getDictList('act_category')}" itemLabel="label" itemValue="value" htmlEscape="false"/>
				</form:select>
			</span>
	    	<span class="fl">
				<input type="text" id="beginDatePicker"  value="${fns:getDateString(act.beginDate)}" readonly="readonly" placeholder="开始时间"
				onclick="WdatePicker({maxDate:'#F{$dp.$D(\'endDatePicker\')}',dateFmt:'yyyy-MM-dd',vel:'beginDate',realDateFmt:'yyyyMMdd'})" class="Wdate input_txt w200">
				<input type="hidden" id="beginDate" name="beginDate" value="${act.beginDate }" >
				
				<input type="text" id="endDatePicker"  value="${fns:getDateString(act.endDate)}" readonly="readonly" placeholder="结束时间"
				onclick="WdatePicker({minDate:'#F{$dp.$D(\'beginDatePicker\')}',dateFmt:'yyyy-MM-dd',vel:'endDate',realDateFmt:'yyyyMMdd'})" class="Wdate input_txt w200">
				<input type="hidden" id="endDate" name="endDate" value="${act.endDate }">
	    	</span>
	      	<span><input class="btn btn_primary" name="" type="submit" value="查询"></span>
		</div>
		</form:form>
	    <div class="tabcon">
	      <table class="tab" cellpadding="0" cellspacing="0">
	        <thead>
	          <tr>
					<th>标题</th>
					<th>当前环节</th><%--
					<th>任务内容</th> --%>
					<th>流程名称</th>
					<th>流程版本</th>
					<th>完成时间</th>
					<th>操作</th>
	          </tr>
	        </thead>
	        <tbody>
	         <c:forEach items="${page.list}" var="act">
				<c:set var="task" value="${act.histTask}" />
				<c:set var="vars" value="${act.vars}" />
				<c:set var="procDef" value="${act.procDef}" /><%--
				<c:set var="procExecUrl" value="${act.procExecUrl}" /> --%>
				<c:set var="status" value="${act.status}" />
				<tr>
					<td>
						<a href="${ctx}/act/task/form?taskId=${task.id}&taskName=${fns:urlEncode(task.name)}&taskDefKey=${task.taskDefinitionKey}&procInsId=${task.processInstanceId}&procDefId=${task.processDefinitionId}&status=${status}">${fns:abbr(not empty vars.map.title ? vars.map.title : task.id, 60)}</a>
					</td>
					<td>
						<a target="_blank" href="${pageContext.request.contextPath}/act/diagram-viewer?processDefinitionId=${task.processDefinitionId}&processInstanceId=${task.processInstanceId}">${task.name}</a><%--
						<a target="_blank" href="${ctx}/act/task/trace/photo/${task.processDefinitionId}/${task.executionId}">${task.name}</a>
						<a target="_blank" href="${ctx}/act/task/trace/info/${task.processInstanceId}">${task.name}</a> --%>
					</td><%--
					<td>${task.description}</td> --%>
					<td>${procDef.name}</td>
					<td><b title='流程版本号'>V: ${procDef.version}</b></td>
					<td><fmt:formatDate value="${task.endTime}" type="both"/></td>
					<td>
						<a href="${ctx}/act/task/form?taskId=${task.id}&taskName=${fns:urlEncode(task.name)}&taskDefKey=${task.taskDefinitionKey}&procInsId=${task.processInstanceId}&procDefId=${task.processDefinitionId}&status=${status}">详情</a>
					</td>
				</tr>
			</c:forEach>
	        </tbody>
	        <tfoot>
				<td colspan="12">
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
