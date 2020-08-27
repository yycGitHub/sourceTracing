<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>选择审核人</title>
	<meta name="decorator" content="default"/>
	<script src="${ctxStatic}/common/pony.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(document).ready(function() {			
		});
	</script>
</head>
<body>
	<div class="miandody">
	  	<div class="con">
	  	    <div class="tabcon">
		      <table class="tab" cellpadding="0" cellspacing="0">
		        <thead>
		          <tr>
		            <td><input type="checkbox"  onclick="Pn.checkbox('userId',this.checked)"/>选择</td>
		            <td>任务环节</td>
					<td>用户工号</td>
					<td>用户姓名</td>
		          </tr>
		        </thead>
		        <tbody>
		         <c:forEach items="${actList}" var="act">
						<tr>
							<td colspan="4">${act.task.name}</td>
						</tr>
						<c:forEach items="${act.candidateUser}" var="user">
						<tr>
							<td><input type="checkbox" name="userId" value="${user.id}" taskId="${act.taskId }" taskName="${act.taskName }"/></td>
							<td>${act.task.name}</td>
							<td>${user.loginName}</td>
							<td>${user.name}</td>
						</tr>
					 	</c:forEach>
					</c:forEach>
		        </tbody>
		      </table>
		    </div>
		</div>
	</div>
</body>
</html>
