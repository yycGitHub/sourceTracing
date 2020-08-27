<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>部署流程 - 流程管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<div class="miandody">
	  <div class="crumb">
	  	<i class="iconfont">&#xe60d;</i><strong>流程管理</strong><span class="songti">&gt;</span><span>流程管理</span>
	  </div>
	  <div class="con">
	    <div class="con_nav">
	    	<ul class="fl">
		        <li><a href="${ctx}/act/process/">流程管理</a></li>
				<li><a class="cur" href="${ctx}/act/process/deploy/">部署流程</a></li>
				<li><a href="${ctx}/act/process/running/">运行中的流程</a></li>
	      	</ul>
	    </div>
	<tags:message content="${message}"/>
	<form id="inputForm" action="${ctx}/act/process/deploy" method="post" enctype="multipart/form-data" class="form-horizontal">
		<div class="list_edit">
		<dl>
		  <dt>流程分类</dt>
		  <dd>
		  	<select id="category" name="category" class="required drop_down gray w200">
					<c:forEach items="${fns:getDictList('act_category')}" var="dict">
						<option value="${dict.value}">${dict.label}</option>
					</c:forEach>
		  	</select>
		  </dd>
		</dl>
		<dl>
			<dt>流程文件</dt>
		  	<dd><input type="file" id="file" name="file" class="required input_txt w200"/></dd>
		</dl>
		<dl>
		  	<dt>&nbsp;</dt>
		  	<dd>
				<input id="btnSubmit" class="btn btn_primary" type="submit" value="提 交"/>&nbsp;
				<input id="btnCancel" class="btn btn_primary" type="button" value="返 回" onclick="history.go(-1)"/>
			</dd>
		</dl>
		</div>
	</form>
	</div>
	</div>
</body>
</html>
