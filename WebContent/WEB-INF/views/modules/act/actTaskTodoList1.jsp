<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>计划上报流程待办任务</title>
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
		
		//单个任务指派处理脚本；
		function delegateTask(taskId){
			top.$.jBox.open("iframe:${ctx}/sys/user/upUserTable?checkType=delegate","请您选择任务处理人",800,600,{buttons:{"确定":"ok", "取消":"clear"},showClose:false,submit:function(v,h,f){
				if(v == "ok"){
					 var userId = h.find("iframe")[0].contentWindow.$("input[name='selectedUserId']:checked");
					if(userId.val() != ""){
						$.post("${ctx}/act/task/delegateTask",{'taskId':taskId, 'userId': userId.val()},function(data){
							top.$.jBox.tip(data, 'info');
							window.location.reload();
						});
					}
					 return true;
				}
				},loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
				}
			});
		}
	</script>
</head>
<body>
	<div class="miandody">
	  <div class="con">
	    <div class="con_nav">
	    	<ul class="fl">
		        <li><a class="cur" href="${ctx}/act/task/todo?type=1">计划上报待办任务</a></li>
		        <li><a href="${ctx}/act/task/todo?type=2">计划发布待办任务</a></li>
		        <li><a href="${ctx}/act/task/todo?type=3">计划考核待办任务</a></li>
		        <li><a href="${ctx}/act/task/todo?type=4">计划调整待办任务</a></li>
		        <li><a href="${ctx}/act/task/historic/">历史任务</a></li>
	      	</ul>
	    </div>
	    <form:form id="searchForm" modelAttribute="act" action="${ctx}/act/task/todo" method="get" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<form:hidden path="type" value="1"/>
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
	            <th>工作内容</th>
	            <th>当前环节</th>
				<th>流程名称</th>
				<th>流程版本</th>
				<th class="sort-column ksrq">创建时间</th>
				<th>操作</th>
	          </tr>
	        </thead>
	        <tbody>
	         <c:forEach items="${page.list}" var="act">
				<c:set var="task" value="${act.task}" />
				<c:set var="vars" value="${act.vars}" />
				<c:set var="procDef" value="${act.procDef}" /><%--
				<c:set var="procExecUrl" value="${act.procExecUrl}" /> --%>
				<c:set var="status" value="${act.status}" />
				<c:set var="gMSCJH0103" value="${act.gMSCJH0103}" />
					<tr>
						<td>
							${vars.map.title}
						</td>
						<td>
							${gMSCJH0103.gznr}
						</td>
						<td>
							${task.name}
						</td>
						<td>${procDef.name}</td>
						<td>V: ${procDef.version}</td>
						<td>${fns:getDateString(task.createTime)}</td>
						<td>
							<a href="${ctx}/act/task/form?taskId=${task.id}&taskName=${fns:urlEncode(task.name)}&taskDefKey=${task.taskDefinitionKey}&procInsId=${task.processInstanceId}&procDefId=${task.processDefinitionId}&status=${status}">任务办理</a>
							<a href="javascript:void()"  onclick="delegateTask('${task.id}')">委派</a>
							<a href="${ctx}/act/task/flow/info/${task.id}">跟踪</a>
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
