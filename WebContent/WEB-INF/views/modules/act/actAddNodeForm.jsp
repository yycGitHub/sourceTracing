<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>任务表单</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function(){
			$("#inputForm").validate();
		});

		function selectUser(){
			top.$.jBox.open("iframe:${ctx}/sys/user/upUserTable?checkType=delegate","请您选择任务办理人",800,600,{buttons:{"确定":"ok", "取消":"clear"},showClose:false,submit:function(v,h,f){
				if(v == "ok"){
					 var userId = h.find("iframe")[0].contentWindow.$("input[name='selectedUserId']:checked");
					if(userId.val() != ""){
						$("#assignee").val(userId.val());
						$("#assigneeName").val(userId.attr("class"));
					}else{
						$("#assignee").val("");
						$("#assigneeName").val("");
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
		<form id="inputForm"  action="#" method="post" class="form-horizontal">
			<input type="hidden" name="procDefId"  id="procDefId"  value="${procDefId}">
			<input type="hidden" name="procInsId"  id="procInsId"  value="${procInsId}">
			<input type="hidden" name="targetTaskDefinitionKey"  id="targetTaskDefinitionKey"  value="${targetTaskDefinitionKey}">
			<tags:message content="${message}"/>
			<div class="tab_door">
				<div class="tab_door_con">
					<div class="tab_door_show">
						<table class="tab" cellpadding="0" cellspacing="0">
							<tbody>
								<tr>
									<td align="right">当前节点标识：</td>
									<td align="left">${targetTaskDefinitionKey}</td>
								</tr>
								<tr>
									<td align="right">当前节点名称：</td>
									<td align="left">${taskName}</td>
								</tr>
								<tr>
									<td align="right"><span class="red font18 martop">&lowast;&nbsp;</span>新增节点办理人：</td>
									<td align="left">
										<input type="hidden"  id="assignee"  name="assignee" />
	        							<input type="text" id="assigneeName"  name="assigneeName"  class="input_txt w150" readOnly onclick="selectUser()" />
									</td>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</div>
		</form>
	</div>
</div>

</body>
</html>