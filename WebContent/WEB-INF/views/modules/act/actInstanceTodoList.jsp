<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>待办任务</title>
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
        	location = '${ctx}/act/task/instanceList?pageNo='+n+'&pageSize='+s;
        }
		//任务回退；
		function preBack(reqUrl,taskId,procInsId){     
			// 正常打开	
			top.$.jBox.open("iframe:"+reqUrl,"任务回退",700, 220, {
				buttons:{"确定":"ok","关闭":true}, submit:function(v, h, f){
					if(v == "ok"){
						 var taskDefKey = h.find("iframe")[0].contentWindow.$("#htjd").val();
						 $.get('${ctx}/act/task/back' ,{taskId: taskId,taskDefKey:taskDefKey,procInsId:procInsId}, function(data) {
								if (data == 'true'){
						        	top.$.jBox.tip('签收完成');
						            location = '${ctx}/act/task/todo/';
								}else{
						        	top.$.jBox.tip('签收失败');
								}
						    });
						 return true;
					}else{
					}
				}, loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
				}
			});
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
		//前、后加签；
		function addNode(procDefId,procInsId,targetTaskDefinitionKey,taskName,type){
			var url = "iframe:${ctx}/act/task/addNodeForm?procDefId="+ procDefId +"&procInsId=" + procInsId + "&targetTaskDefinitionKey=" + targetTaskDefinitionKey + "&taskName="+taskName; 
			top.$.jBox.open(url,"添加节点",600,300,{buttons:{"确定":"ok", "取消":"clear"},showClose:false,submit:function(v,h,f){
				if(v == "ok"){
					 var userId = h.find("iframe")[0].contentWindow.$("input[name='assignee']"); 
					if(userId.val() != ""){
						$.post("${ctx}/act/task/addNode",{'procDefId':procDefId, 'procInsId':procInsId, 'targetTaskDefinitionKey':targetTaskDefinitionKey, 'assignee': userId.val(),'type': type},function(data){
							top.$.jBox.tip(data, 'info');
							window.location.reload();
						});
					}else{
						top.$.jBox.tip("加签失败！", 'info');
					}
					 return true;
				}
				},loaded:function(h){
					$(".jbox-content", top.document).css("overflow-y","hidden");
				}
			});
		}
		
		//启动，关闭，暂停；
		function updateProcInsState(message, processInstanceId, state){
			top.$.jBox.confirm(message,"提示信息",function(v,h,f){
				if(v == 'ok'){
					$.post("${ctx}/act/process/updateProcInsState",{'procInsId':processInstanceId,'state':state},function(data){
						top.$.jBox.tip(data, 'info');
						window.location.reload();
					});
				}
			});
		}
	</script>
</head>
<body>
	<div class="miandody">
	  <div class="con">
	    <form:form id="searchForm" modelAttribute="act" action="${ctx}/act/task/instanceList" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
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
	            <th>实例名称</th>
	            <th>当前环节</th>
				<th>流程名称</th>
				<th>当前处理人</th>
				<th class="sort-column ksrq">创建时间</th>
				<th>状态</th>
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
					<tr>
						<td>
							<a href="${ctx}/act/task/form?taskId=${task.id}&taskName=${fns:urlEncode(task.name)}&taskDefKey=${task.taskDefinitionKey}&procInsId=${task.processInstanceId}&procDefId=${task.processDefinitionId}&status=${status}">
							${fns:abbr(not empty vars.map.title ? vars.map.title : task.id, 60)}</a>
						
						</td>
						<td>
							<a target="_blank" 
								href="${pageContext.request.contextPath}/act/diagram-viewer?processDefinitionId=${task.processDefinitionId}&processInstanceId=${task.processInstanceId}">
								${task.name}</a>
						</td>
						<td>${procDef.name}</td>
						<td>${act.assigneeName}</td>
						<td><fmt:formatDate value="${task.createTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
						<td> 
							<c:if test="${act.task.suspensionState eq '2'}">
								暂停
							</c:if>
							<c:if test="${act.task.suspensionState ne '2'}">
								启动
							</c:if>
						</td>
						<td>
							<!--  <a href="${ctx}/act/task/form?taskId=${task.id}&taskName=${fns:urlEncode(task.name)}&taskDefKey=${task.taskDefinitionKey}&procInsId=${task.processInstanceId}&procDefId=${task.processDefinitionId}&status=${status}">任务办理</a>-->
							<shiro:hasPermission name="act:process:edit">
								<c:if test="${empty task.executionId}">
									<a href="${ctx}/act/task/deleteTask?taskId=${task.id}&reason=" onclick="return promptx('删除任务','删除原因',this.href);">删除任务</a>
								</c:if>
							</shiro:hasPermission>
							<c:if test="${act.task.suspensionState ne '2'}">
								<a href="javascript:void();" onclick="updateProcInsState('您确定要暂停该实例吗？','${act.task.processInstanceId}','suspend')">暂停</a>
							</c:if>
							<c:if test="${act.task.suspensionState eq '2'}">
								<a href="javascript:void();" onclick="updateProcInsState('您确定要启动该实例吗？','${act.task.processInstanceId}','active')">启动</a>
							</c:if>
							<a href="javascript:void();" onclick="updateProcInsState('您确定要作废该实例吗？','${act.task.processInstanceId}','close')">关闭</a>
							<a target="_blank" href="${ctx}/act/task/flow/info/${task.id}">跟踪</a>
							<a href="javascript:void()"  onclick="delegateTask('${task.id}')">委派</a>
							<a target="_blank" onclick="preBack('${ctx}/act/task/preBack?taskId=${task.id}&taskName=${fns:urlEncode(task.name)}&taskDefKey=${task.taskDefinitionKey}&procInsId=${task.processInstanceId}&procDefId=${task.processDefinitionId}&status=${status}','${task.id}','${task.processInstanceId}');">回退</a>
							<%-- <a href="javascript:void()"  onclick="addNode('${act.procDef.id}','${act.task.processInstanceId}','${act.task.taskDefinitionKey}','${act.task.name}')">添加节点</a> --%>
							<c:if test="${act.task.suspensionState eq '1'}">
								<a href="javascript:void();"  onclick="addNode('${act.procDef.id}','${act.task.processInstanceId}','${act.task.taskDefinitionKey}','${act.task.name}','before')">前加签</a>
								<a href="javascript:void();"  onclick="addNode('${act.procDef.id}','${act.task.processInstanceId}','${act.task.taskDefinitionKey}','${act.task.name}','after')">后加签</a>
							</c:if>
						</td>
					</tr>
				</c:forEach>
	        </tbody>
	        <tfoot>
	        <tr>
			<td colspan="40">
				<div class="fr">
					<div class="page">${page}</div>
				</div>
			</td>
			</tr>
			</tfoot>
	      </table>
	    </div>
	  </div>
	</div>
</body>
</html>
