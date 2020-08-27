<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>流程管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function(){
			bootbox.setDefaults("locale","zh_CN");
		});
		function page(n,s){
        	location = '${ctx}/act/process/?pageNo='+n+'&pageSize='+s;
        }
		function updateCategory(id, category){
			bootbox.dialog({ 
				message: $("#categoryBox").html(),
				title: "设置分类 ",
		        buttons:             
		        {
		        	 "success" :
		             {
		                "label" : "<i class='icon-ok'></i> 确定",
		                "className" : "btn-sm btn-success",
		                "callback": function() {
		                	$("#categoryForm").submit();
		                }
		            },
		        	"cancel": {
			            "label" : "<i class='icon-info'></i> 取消",
			            "className" : "btn-sm btn-danger"
		            }
		        }
			});
			$("#categoryBoxId").val(id);
			$("#categoryBoxCategory").val(category);
		}
	</script>
	<script type="text/template" id="categoryBox">
		<form id="categoryForm" action="${ctx}/act/process/updateCategory" method="post" enctype="multipart/form-data" class="form-search">
			<input id="categoryBoxId" type="hidden" name="procDefId" value="" />
			<div class="row">
			<div class="col-xs-12">
				<label class="col-sm-3 control-label no-padding-right" for="form-field-1"> 流程分类:</label>
				<div class="col-sm-9">
					<select id="categoryBoxCategory" name="category" class="col-xs-10 col-sm-5">
				       <option value="">无分类</option>
				       <c:forEach items="${fns:getDictList('act_category')}" var="dict">
					   <option value="${dict.value}">${dict.label}</option>
				       </c:forEach>
			       </select>
				</div>
			</div>
			</div>
		</form>
	</script>
</head>
<div class="main-container" id="main-container">
	<div class="main-content-inner">
		<div class="main-content">
			<div class="page-content">
				<tags:message content="${message}"/>
				<div class="row">
				<form id="searchForm" action="${ctx}/act/process/" method="post" class="form-horizontal">
					<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
					<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
					<tags:tableSort id="orderBy" name="orderBy" value="${page.orderBy}" callback="page();"/>
					<div class="table-responsive">
						<span class="drop_down_triangle">
				        	<select id="category" name="category" class="multiselect input-md">
								<option value="">全部分类</option>
								<c:forEach items="${fns:getDictList('act_category')}" var="dict">
									<option value="${dict.value}" ${dict.value==category?'selected':''}>${dict.label}</option>
								</c:forEach>
							</select> 
						</span>
						<span>
			          	    <button type="button" class="btn btn-info btn-sm" onclick="return page();">
								<span class="ace-icon fa fa-search icon-on-right bigger-110"></span>查询
							</button>
						</span>
					</div>
				</form>
				</div>
				<div class="hr dotted hr-double"></div>
				<div class="row">
					<div class="col-xs-12">
						<div class="tabbable">
							<div class="tab-content">
								<!-- PAGE CONTENT BEGINS -->
								<table id="contentTable" class="table table-bordered table-hover">
								<thead>
								<tr>
									<th>流程分类</th>
									<th>流程ID</th>
									<th>流程标识</th>
									<th>流程名称</th>
									<th>流程版本</th>
									<th>部署时间</th>
									<th>流程XML</th>
									<th>流程图片</th>
									<th>操作</th>
								</tr>
								</thead>
								<tbody>
									<c:forEach items="${page.list}" var="object">
										<c:set var="process" value="${object[0]}" />
										<c:set var="deployment" value="${object[1]}" />
										<tr>
											<td><a href="javascript:updateCategory('${process.id}', '${process.category}')" title="设置分类">${fns:getDictLabel(process.category,'act_category','无分类')}</a></td>
											<td>${process.id}</td>
											<td>${process.key}</td>
											<td>${process.name}</td>
											<td><b title='流程版本号'>V: ${process.version}</b></td>
											<td><fmt:formatDate value="${deployment.deploymentTime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
											<td><a target="_blank" href="${ctx}/act/process/resource/read?procDefId=${process.id}&resType=xml">${process.resourceName}</a></td>
											<td><a target="_blank" href="${ctx}/act/process/resource/read?procDefId=${process.id}&resType=image">${process.diagramResourceName}</a></td>
											<td>
												<c:if test="${process.suspended}">
													<a href="#" onclick="javascript:bootbox.confirm('确认激活吗?', function(result){if(result) location.href='${ctx}/act/process/update/active?procDefId=${process.id}';})">激活</a>
												</c:if>
												<c:if test="${!process.suspended}">
													<a href="#" onclick="javascript:bootbox.confirm('确认挂起吗?', function(result){if(result) location.href='${ctx}/act/process/update/suspend?procDefId=${process.id}';})">挂起</a>
												</c:if>
												<a href='#' onclick="javascript:bootbox.confirm('确认删除该流程吗?', function(result){if(result) location.href='${ctx}/act/process/delete?deploymentId=${process.deploymentId}';})">删除</a>
						                        <a href='#' onclick="javascript:bootbox.confirm('确认要转换为模型吗?', function(result){if(result) location.href='${ctx}/act/process/convert/toModel?procDefId=${process.id}';})">转换为模型</a>
											</td>
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
</html>
