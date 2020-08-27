<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>新建模型 - 模型管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function(){
			bootbox.setDefaults("locale","zh_CN");
			$("#inputForm").validate({
				errorElement: 'div',
				errorClass: 'help-block',
				focusInvalid: false,
				ignore: "",
				rules: {
					name: {
						required: true
					},
					key: {
						required: true
					}
				},
				messages: {
				},
				highlight: function (e) {
					$(e).closest('.form-group').removeClass('has-info').addClass('has-error');
				},
				success: function (e) {
					$(e).closest('.form-group').removeClass('has-error');//.addClass('has-info');
					$(e).remove();
				},
				errorPlacement: function (error, element) {
					if(element.is('input[type=checkbox]') || element.is('input[type=radio]')) {
						var controls = element.closest('div[class*="col-"]');
						if(controls.find(':checkbox,:radio').length > 1) controls.append(error);
						else error.insertAfter(element.nextAll('.lbl:eq(0)').eq(0));
					}
					else if(element.is('.select2')) {
						error.insertAfter(element.siblings('[class*="select2-container"]:eq(0)'));
					}
					else if(element.is('.chosen-select')) {
						error.insertAfter(element.siblings('[class*="chosen-container"]:eq(0)'));
					}
					else error.insertAfter(element.parent());
				},
				submitHandler: function(form){
					bootbox.alert('正在提交，请稍等...');
					form.submit();
					setTimeout(function(){location='${ctx}/act/model/'}, 1000);
				},
				invalidHandler: function (form) {
				}
			});
		});
		function page(n,s){
        	location = '${ctx}/act/model/?pageNo='+n+'&pageSize='+s;
        }
	</script>
</head>
<div class="main-container" id="main-container">
	<div class="main-content-inner">
		<div class="main-content">
			<div class="page-content">
				<tags:message content="${message}"/>
				<div class="row">
					<form id="inputForm" action="${ctx}/act/model/create" target="_blank" method="post" class="form-horizontal">
						<div class="col-xs-12">
							<div class="form-group">
								<label class="col-sm-3 control-label no-padding-right" for="form-field-1"> 流程分类：</label>
								<div class="col-sm-9">
									<select id="category" name="category" class="col-xs-10 col-sm-5">
										<c:forEach items="${fns:getDictList('act_category')}" var="dict">
											<option value="${dict.value}">${dict.label}</option>
										</c:forEach>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label no-padding-right" for="form-field-1"> 模块名称：</label>
									<div class="col-sm-9">
									<div class="clearfix">
									<input name="name" class="col-xs-10 col-sm-5"/>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label no-padding-right" for="form-field-1"> 模块标识：</label>
								<div class="col-sm-9">
									<div class="clearfix">
									<input name="key" class="col-xs-10 col-sm-5"/>
									</div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-sm-3 control-label no-padding-right" for="form-field-1"> 模块描述：</label>
								<div class="col-sm-9">
									<div class="clearfix">
									<textarea id="description" name="description" class="form-control"></textarea>
									</div>
								</div>
							</div>
							<shiro:hasPermission name="sys:role:edit">
								<div class="clearfix form-actions">
									<div class="col-md-offset-3 col-md-9">
									<button id="btnSubmit" class="btn btn-info" type="submit">
										<i class="ace-icon fa fa-check bigger-110"></i>
										保存
									</button>
									&nbsp; &nbsp; &nbsp;
									<button type="reset" class="btn" onclick="history.go(-1)">
										<i class="ace-icon fa fa-reply bigger-110"></i>
										返回
									</button>
									</div>
								</div>
							</shiro:hasPermission>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>


<!-- <body> -->
<!-- 	<ul class="nav nav-tabs"> -->
<%-- 		<li><a href="${ctx}/act/model/">模型管理</a></li> --%>
<%-- 		<li class="active"><a href="${ctx}/act/model/create">新建模型</a></li> --%>
<!-- 	</ul><br/> -->
<%-- 	<tags:message content="${message}"/> --%>
<%-- 	<form id="inputForm" action="${ctx}/act/model/create" target="_blank" method="post" class="form-horizontal"> --%>
<!-- 		<div class="control-group"> -->
<!-- 			<label class="control-label">流程分类：</label> -->
<!-- 			<div class="controls"> -->
<!-- 				<select id="category" name="category" class="required input-medium"> -->
<%-- 					<c:forEach items="${fns:getDictList('act_category')}" var="dict"> --%>
<%-- 						<option value="${dict.value}">${dict.label}</option> --%>
<%-- 					</c:forEach> --%>
<!-- 				</select> -->
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 		<div class="control-group"> -->
<!-- 			<label class="control-label">模块名称：</label> -->
<!-- 			<div class="controls"> -->
<!-- 				<input id="name" name="name" type="text" class="required" /> -->
<!-- 				<span class="help-inline"></span> -->
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 		<div class="control-group"> -->
<!-- 			<label class="control-label">模块标识：</label> -->
<!-- 			<div class="controls"> -->
<!-- 				<input id="key" name="key" type="text" class="required" /> -->
<!-- 				<span class="help-inline"></span> -->
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 		<div class="control-group"> -->
<!-- 			<label class="control-label">模块描述：</label> -->
<!-- 			<div class="controls"> -->
<!-- 				<textarea id="description" name="description" class="required"></textarea> -->
<!-- 			</div> -->
<!-- 		</div> -->
<!-- 		<div class="form-actions"> -->
<!-- 			<input id="btnSubmit" class="btn btn-primary" type="submit" value="提 交"/> -->
<!-- 			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/> -->
<!-- 		</div> -->
<!-- 	</form> -->
<!-- </body> -->
</html>
