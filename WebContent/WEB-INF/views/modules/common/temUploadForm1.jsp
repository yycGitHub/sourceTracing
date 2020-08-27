<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="renderer" content="webkit">
	<title>标签打印</title>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/zn/js/users.js"></script>
	<script type="text/javascript" src="${ctxStatic}/zn/js/stringBuilder.js"></script>
	<script type="text/javascript" src="${ctxStatic}/zn/js/jquery.qrcode.min.js"></script>
	<style type="text/css">
		html,body,ul,p{margin:0;padding:0;}
		.list{margin-left:2px;float:left;width:150px;height:113px;font-size:12px;line-height:14.3px; overflow:hidden;font-family:'黑体';}
		.list1{margin-left:8px;float:left;width:150px;height:113px;font-size:12px;line-height:14.3px; overflow:hidden;font-family:'黑体';}
		.code{margin-right:5px;margin-left:5px;float:left;width:70px;height:70px;}
		.txt{float:left;width:70px;}
		.txt ul{list-style:none;}
		.txt b{font-weight:100;display:black;}
		.con{margin-top:3px;margin-left:-5px;float:left;width:160px;font-size:10px;-webkit-transform:scale(0.80);}
		.con1{margin-top:3px;float:left;width:160px;font-size:10px;-webkit-transform:scale(0.80);}
	</style>
	<script type="text/javascript">
		$(document).ready(function() {
			getData();
		});
		
		function setButtonDisabled(){
			id = "import";
			$("#"+id).attr("class","min_btn btn_default upload_btn mouse_arrow");
			$("#"+id).attr("disabled","disabled");
		}
		
		function getData() {
			var applyId = '${applyId}';
			var token = '${token}';
			userInfo.saveToken(token);
			var serialNumberStart = '${serialNumberStart}';
			var serialNumberEnd = '${serialNumberEnd}';
			var pageno = 1;
	    	var pagesize = 10000;
	    	var param = {
	    		applyId: applyId,
	    		token: token,
	    		serialNumberStart: serialNumberStart,
	    		serialNumberEnd: serialNumberEnd,
	    		pageno: pageno,
	    		pagesize: pagesize
	    	};
	    	var url = "/sureserve-admin-dev/api/common/codelist";
	    	//var url = "/api/common/codelist";
		    $.get(url, param, function(res) {
		    	if(res.code == 0) {
					var data = res.bodyData.list;
					var applyContent = [];
					var url1 = "/sureserve-admin-dev/api/common/traceLableContentList?id="+applyId;
					//var url1 = "/api/common/traceLableContentList?id="+applyId;
					$.get(url1, {}, function(res) {
						if(res.code == 0) {
							applyContent = res.bodyData;
							dayin_new(data,applyContent);
							var param = {
								applyId: applyId,
								startSerialNumber: serialNumberStart,
								endSerialNumber: serialNumberEnd
							}
							saveTracePrintRecord(param);
						} else {
							alert("查询内容失败");
						}
					});
				} else {
					alert("查询内容失败");
				}
			});
		}
		
		var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
		
		function dayin_new(data,applyContent){
			var sb = new StringBuilder();
			for(var i=0; i<data.length; i++) {
				var traceCode = data[i].traceCode;
				var src = "http://trace.sureserve.cn/traceInfo.html?traceCode=" + traceCode;
				var fontname1 = applyContent[0].applyElementContent;
				var fontname2 = applyContent[1].applyElementContent;
				var fontname3 = applyContent[2].applyElementContent;
				var fontname4 = applyContent[3].applyElementContent;
				var fontname5 = applyContent[4].applyElementContent;
				var fontname6 = traceCode;
				
				//判断是否IE浏览器
				//设置打印内容
				if (userAgent.indexOf("Trident") > -1 || userAgent.indexOf("MSIE") > -1) {
					//setContent_ie(sb,src,traceCode,fontname1,fontname2,fontname3,fontname4,fontname5,fontname6);
					alert("请使用谷歌浏览器打印");
				}else {
					if(i % 2==0){
						setContent_chorm(sb,src,traceCode,fontname1,fontname2,fontname3,fontname4,fontname5,fontname6,'0');
					}else{
						setContent_chorm(sb,src,traceCode,fontname1,fontname2,fontname3,fontname4,fontname5,fontname6,'1');
					}
					
				}
			}
			$("body").html(sb.toString());
			for(var i=0; i<data.length; i++) {
				var traceCode = data[i].traceCode;
				var src = "http://trace.sureserve.cn/v.html?traceCode=" + traceCode;
				if (userAgent.indexOf("Trident") > -1 || userAgent.indexOf("MSIE") > -1) {
					//setCode(traceCode,src,75);
				}else {
					setCode(traceCode,src,70);
				}
			}
			window.print(); // 调用打印方法
		}
		
		//设置二维码
		function setCode(traceCode,src,len){
			jQuery('.code'+traceCode).qrcode({
				render: "canvas", 
				width: len,
				height: len,
				correctLevel: 0,
				text: src
			});
		}
		
		//设置打印内容
		function setContent_chorm(sb,src,traceCode,fontname1,fontname2,fontname3,fontname4,fontname5,fontname6,type){
			var cp = "";
			var cd = "";
			var bzq = "";
			if(fontname1.indexOf('产品名称')>-1){
				var arr = fontname1.split(":");
				if(arr!=null && arr.length>0){
					cp = arr[1];
				}
				var arr1 = fontname1.split("：");
				if(arr1!=null && arr1.length>0){
					cp = arr1[1];
				}
			}
			if(fontname2.indexOf('保质期')>-1){
				var arr = fontname2.split(":");
				if(arr!=null && arr.length>0){
					bzq = arr[1];
				}
				var arr1 = fontname2.split("：");
				if(arr1!=null && arr1.length>0){
					bzq = arr1[1];
				}
			}
			if(fontname3.indexOf('产地')>-1){
				var arr = fontname3.split(":");
				if(arr!=null && arr.length>0){
					cd = arr[1];
				}
				var arr1 = fontname3.split("：");
				if(arr1!=null && arr1.length>0){
					cd = "产地："+arr1[1];
				}
			}
			if(type=='0'){
				sb.append("<div class='list'>");
			}else{
				sb.append("<div class='list1'>");
			}
			sb.append("<div class='" + ("code" + traceCode) +" code'></div>");
			sb.append("<div class='txt'>");
			sb.append("<ul><li style='padding-top:2px;'><strong>产品名称</strong><p style='padding-top:2px;'><strong>"+ cp +"</strong></p></li>");
			sb.append("<li style='padding-top:6px;'><strong>保质期</strong><p style='padding-top:2px;'><strong>"+ bzq +"</strong></p></li></ul>");
			sb.append("</div>");
			sb.append("<div style='height:1px;background-color:gray;float:left;width:150px;margin-top:10px;'></div>");
			sb.append("<div class='con'><p><strong><strong>望城区智慧农业综合服务平台</strong></p></div>");
			sb.append("</div>");
			return sb.toString(" ");
		}
		
		function saveTracePrintRecord(param) {
			var url = "/sureserve-admin-dev/api/tracePrintRecord/saveTracePrintRecord";
			$.post(url, param, function(res) {
				if(res.code == 0) {
					if(res.code == 0) {
					} else {
						alert("保存标签记录失败");
					}
					window.opener=null;
					window.close();
				} 
			});
		}
	</script>
	
</head>
<body>
	
</body>
</html>
