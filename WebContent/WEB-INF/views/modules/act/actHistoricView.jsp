<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>状态跟踪</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#inputForm").validate();
		});
	</script>
</head>
<body>
<div class="miandody">
	<div class="crumb">
	<i class="iconfont">&#xe60d;</i><strong>个人办公</strong><span class="songti">&gt;</span><span>状态跟踪</span>
	<b class="fr"><a class="min_btn btn_success marright" href="javascript:history.go(-1)">返回</a></b>
	</div>
  	<div class="con">
	    <tags:message content="${message}"/>
		<iframe id="traceFrame" src="${pageContext.request.contextPath}/act/diagram-viewer?processDefinitionId=${task.processDefinitionId}&processInstanceId=${task.processInstanceId}" scrolling="no"  width="100%" height="80%"></iframe>
		<tags:histoicFlow procInsId="${task.processInstanceId}" />
	</div>
</div>
</body>
</html>
