<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>流程管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<div class="main-container" id="main-container">
	<div class="main-content-inner">
		<div class="main-content">
			<div class="page-content">
				<tags:message content="${message}"/>
				<div class="row">
					<div class="col-xs-12">
						<div class="tabbable">
							<div class="tab-content">
								<!-- PAGE CONTENT BEGINS -->
								<table id="contentTable" class="table table-bordered table-hover">
								<thead>
								<tr>
									<td><nobr>流程定义主键</nobr></td>
									<td><nobr>部署主键</nobr></td>
									<td><nobr>流程名称</nobr></td>
									<td><nobr>KEY</nobr></td>
									<td><nobr>版本号</nobr></td>
									<th>XML</th>
									<td><nobr>流程图</nobr></td>
									<td><nobr>部署时间</nobr></td>
									<td><nobr>是否挂起</nobr></td>
									<td><nobr>操作</nobr></td>
								</tr>
								</thead>
								<tbody>
									<c:forEach items="${objects }" var="object">
											<c:set var="process" value="${object[0] }" />
											<c:set var="deployment" value="${object[1] }" />
											<%
											pageContext.setAttribute("isSuspended", ((org.activiti.engine.repository.ProcessDefinition)pageContext.getAttribute("process")).isSuspended());
											%>
											<tr>
											<td title="${process.id }">${fns:abbr(process.id,25) }</td>
											<td title="${process.deploymentId }">${fns:abbr(process.deploymentId,20) }</td>
											<td title="${process.name }">${fns:abbr(process.name,25) }</td>
											<td>${process.key }</td>
											<td>${process.version }</td>
											<td title="${process.resourceName }"><a target="_blank" href='${ctx }/sys/workflow/resource/deployment?deploymentId=${process.deploymentId}&resourceName=${process.resourceName }'>${fns:abbr(process.resourceName,20) }</a></td>
											<td title="${process.diagramResourceName }"><a target="_blank" href='${ctx }/sys/workflow/resource/deployment?deploymentId=${process.deploymentId}&resourceName=${process.diagramResourceName }'>${fns:abbr(process.diagramResourceName,20) }</a></td>
											<td><fmt:formatDate value="${deployment.deploymentTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td>
											<td>${isSuspended} | 
												<c:if test="${isSuspended }">
													<a href="${ctx }/sys/workflow/processdefinition/update/active/${process.id}">激活</a>
												</c:if>
												<c:if test="${!isSuspended }">
													<a href="${ctx }/sys/workflow/processdefinition/update/suspend/${process.id}">挂起</a>
												</c:if>
											</td>
											<td><a href='${ctx }/sys/workflow/process/delete?deploymentId=${process.deploymentId}'>删除</a></td>
										</tr>
									</c:forEach>
								</tbody>
								</table>
								<div class="col-xs-6"></div>
								<div class="col-xs-6">
									<div class="dataTables_paginate paging_simple_numbers">
										${page}
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<!-- <body> -->
	
<!-- 	<div class="miandody"> -->
<!-- 		<div class="crumb"> -->
<!-- 			<i class="iconfont">&#xe60d;</i><strong>流程管理</strong><span -->
<!-- 				class="songti">&gt;</span><span>流程设置</span> -->
<!-- 		</div> -->
<!-- 		<div class="con"> -->
<!-- 			<div class="tabcon"> -->
<!-- 				<table id="treeTable" class="tab" cellpadding="0" cellspacing="0"> -->
<!-- 					<thead> -->
<!-- 						<tr> -->
<!-- 							<td><nobr>流程定义主键</nobr></td> -->
<!-- 							<td><nobr>部署主键</nobr></td> -->
<!-- 							<td><nobr>流程名称</nobr></td> -->
<!-- 							<td><nobr>KEY</nobr></td> -->
<!-- 							<td><nobr>版本号</nobr></td> -->
<!-- 							<th>XML</th> -->
<!-- 							<td><nobr>流程图</nobr></td> -->
<!-- 							<td><nobr>部署时间</nobr></td> -->
<!-- 							<td><nobr>是否挂起</nobr></td> -->
<!-- 							<td><nobr>操作</nobr></td> -->
<!-- 						</tr> -->
<!-- 					</thead> -->
<!-- 					<tbody> -->
<%-- 						<c:forEach items="${objects }" var="object"> --%>
<%-- 							<c:set var="process" value="${object[0] }" /> --%>
<%-- 							<c:set var="deployment" value="${object[1] }" /> --%>
<%-- 							<% --%>
<%-- 							pageContext.setAttribute("isSuspended", ((org.activiti.engine.repository.ProcessDefinition)pageContext.getAttribute("process")).isSuspended());--%>
<%-- 							%> --%>
<!-- 						<tr> -->
<%-- 							<td title="${process.id }">${fns:abbr(process.id,25) }</td> --%>
<%-- 							<td title="${process.deploymentId }">${fns:abbr(process.deploymentId,20) }</td> --%>
<%-- 							<td title="${process.name }">${fns:abbr(process.name,25) }</td> --%>
<%-- 							<td>${process.key }</td> --%>
<%-- 							<td>${process.version }</td> --%>
<%-- 							<td title="${process.resourceName }"><a target="_blank" href='${ctx }/sys/workflow/resource/deployment?deploymentId=${process.deploymentId}&resourceName=${process.resourceName }'>${fns:abbr(process.resourceName,20) }</a></td> --%>
<%-- 							<td title="${process.diagramResourceName }"><a target="_blank" href='${ctx }/sys/workflow/resource/deployment?deploymentId=${process.deploymentId}&resourceName=${process.diagramResourceName }'>${fns:abbr(process.diagramResourceName,20) }</a></td> --%>
<%-- 							<td><fmt:formatDate value="${deployment.deploymentTime }" pattern="yyyy-MM-dd HH:mm:ss"/></td> --%>
<%-- 							<td>${isSuspended} |  --%>
<%-- 								<c:if test="${isSuspended }"> --%>
<%-- 									<a href="${ctx }/sys/workflow/processdefinition/update/active/${process.id}">激活</a> --%>
<%-- 								</c:if> --%>
<%-- 								<c:if test="${!isSuspended }"> --%>
<%-- 									<a href="${ctx }/sys/workflow/processdefinition/update/suspend/${process.id}">挂起</a> --%>
<%-- 								</c:if> --%>
<!-- 							</td> -->
<%-- 							<td><a href='${ctx }/sys/workflow/process/delete?deploymentId=${process.deploymentId}'>删除</a></td> --%>
<!-- 						</tr> -->
<%-- 					</c:forEach> --%>
						
<!-- 					</tbody> -->
<!-- 				</table> -->
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 	</div> -->
<!-- </body> -->
</html>
