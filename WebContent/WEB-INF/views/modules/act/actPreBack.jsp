<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>任务表单</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
		});
	</script>
</head>
<body>
	<form:form method="post" class="form-horizontal">
		<table class="tab" cellpadding="0" cellspacing="0">
			<tbody>
				<tr>
					<td class="w200" align="right">回退节点</td>
					<td class="w300">
						<select id="htjd" name="htjd" class="drop_down gray w200">
							<c:forEach items="${actBackList}" var="act">
								<option value="${act.histIns.activityId}" >${act.histIns.activityName}</option>
							</c:forEach>
						</select>
					</td>
				</tr>
			</tbody>
		</table>	
	</form:form>
</body>
</html>
