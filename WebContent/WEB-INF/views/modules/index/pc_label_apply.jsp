<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="renderer" content="webkit">
	<title>标签申请</title>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/zn/css/base.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/zn/css/s+.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/hui/hui.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/s.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/base.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/choice.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/style.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/zn/js/layui/css/layui.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/zn/js/layui/extend/steps/style.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/jquery-validation/1.11.1/jquery.validate.min.css"/>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/picker/css/image-picker.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/sucail/css/style.css">
	<style type="text/css">
		html, body {
		    width: 100%;
		    height: 100%;
		    overflow-x:hidden;
		    overflow-y:hidden;
		}
		
		.step-body{
			width: 100%;
		    height: 100%;
		    overflow-x:hidden;
			overflow-y:scroll;
		}
		
		.maxbox {
		    width: 75%;
    		padding-top: 15px;
		}
		
		.maxbox1 {
		    width: 25%;
		    padding-top: 15px;
		}
		
		.input_file {
		    position: absolute;
		    left: -8px;
		    top: 2px;
		    padding: 7px 0;
		    cursor: pointer;
		    z-index: 1;
		    outline: none;
		    opacity: 0;
		    filter: alpha(opacity=0);
		}
		
		.upload_con .upload_txt_show {
		    position: absolute;
		    left: 5px;
		    top: 22px;
		    z-index: 10;
		}
		
		.step-header {
		    width: 100%;
		    margin: 0 auto;
		    margin-left: 26%;
		    margin-bottom: 40px;
		}
		
		.rollbox {
		    position: relative;
		    float: left;
		    margin: 20 10px;
		    height: 285px;
		    overflow: hidden;
		}
		
		.roll_main {
		    -webkit-transition: all 0.5s ease;
		    -moz-transition: all 0.5s ease;
		    -ms-transition: all 0.5s ease;
		    -o-transition: all 0.5s ease;
		    transition: all 0.5s ease;
		    margin-left: 80px;
		}
		
		.roll_main li {
		    float: left;
		    width: 175px;
		    text-align: center;
		}
		
		.roll_main .photo, .roll_main li em {
		    display: block;
		    margin: 0 0 5px 0;
		}

		.picker1{
			text-align: center;
			padding-left:16%;
		}
		
		.picker2{
			text-align: center;
			padding-left:22%;
		}
		
		.picker3{
			text-align: center;
			padding-left:24%;
		}
		
		ul.thumbnails.image_picker_selector{overflow:auto;list-style-image:none;list-style-position:outside;list-style-type:none;padding:0px;margin-left:-70px;margin-top:60px;}
		
		.image_picker_image{
			width:300px;
			border: 1px solid #eadede;
		}
		
		.input_txt1 {
		    padding: 7px 0;
		    height: 30px;
		    line-height: 30px;
		}
		
		.label1{
			padding-left:100px;
			padding-top:50px;
		}
		.label2{
			padding-left:190px;
			padding-top:23px;
		}
		.label3{
			padding-left:175px;
			padding-top:20px;
		}
		.divpic1{
			height:200px;
			background: url('http://sh.sureserve.cn/upload/product_model/20190118161037354.png') no-repeat 0px 0px;
			display:block;
			background-size:100% 100%;
		}
		.divpic2{
			height:200px;
			background: url('http://sh.sureserve.cn/upload/product_model/20190118161037355.png') no-repeat 0px 0px;
			display:block;
			background-size:100% 100%;
		}
		.divpic3{
			width:360px;
			height:220px;
			background: url('http://sh.sureserve.cn/upload/product_model/20190118161037356.png') no-repeat 0px 0px;
			display:block;
			background-size:100% 100%;
		}
		.tr{height:40px}
		.tdOne{width:150px}
		.tdTwo{width:350px;text-align: left;}
		.divOne{margin-right: 10px;text-align: right;}
		.divTwo{padding-left: 20px;}
		.divla{height:23px;width:200px}
		.divla2{height:28px;width:150px}
		.divla3{height:28px;width:180px;}
		.trTemplate{height:240px}
		.trAddress{height:50px}
		.input_label_content{width:178px;font-size:13px;}
		.input_label_content2{width:140px;font-size:16px;}
		.input_label_content3{width:180px;font-size:12px;}
	</style>
</head>
<body>
	<div id="step_demo" class="step-body" style="padding-top:30px;">
		<div class="step-header" style="width:80%;overflow: hidden;pointer-events: none;">
			<!-- <div class="step-header" style="width:80%;overflow: hidden;"> -->
	        <ul>
	            <li>
	                <span class="step-name">选择标签</span>
	            </li>
	            <li>
	                <span class="step-name">录入数据</span>
	            </li>
	        </ul>
	    </div>
	    
	    <div class="step-content" align="center">
	        <div class="step-list">
	        	<div id="container" style="text-align:center;">
					<div id="picker">
						<select id="lableId" class="image-picker show-labels">
							<c:forEach items="${lableList}" var="lable">
								<option data-img-src='${lable.labelTemplateImg}' style="width:300px;" value='${lable.id}'>${lable.labelTemplateName}</option>
							</c:forEach>
						</select>
					</div>
				</div>
	        	<div class="tab_btn_show" style="padding-top:150px;">
					<input class="btn btn_primary" id="saveBtn1" type="button" value="下一步"/>
				</div>
	        </div>
	        <div class="step-list">
		    	<div class="suCai17-content" align="center">
		            <div class="suCai17-background clearfix" align="left">
		            	<form id="sxForm" action="" method="post" enctype="multipart/form-data" class="form-horizontal">
		                <div class="suCai17-info bd"></div>
		                </form>
		            </div>
					<div class="tab_btn_show">
						<input class="btn btn_primary" id="saveBtn2" type="button" value="完成"/>
					</div>
		        </div>
		    </div>
	    </div>
	</div>
	
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/zn/js/layui/layui.js"></script>
	<script type="text/javascript" src="${ctxStatic}/layer/layer.js"></script>
	<script type="text/javascript" src="${ctxStatic}/hui/hui.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery-validation/1.11.1/jquery.validate.js"></script>
	<script type="text/javascript" src="${ctxStatic}/jquery-validation/1.11.1/jquery.validate.method.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/zn/js/common_file.js"></script>
	<script type="text/javascript" src="${ctxStatic}/zn/js/common_file1.js"></script>
	<script type="text/javascript" src="${ctxStatic}/pcindex/js/sureserve.js"></script>
	<script type="text/javascript" src="${ctxStatic}/pcindex/js/jquery.SuperSlide2.1.2.js"></script>
	<script type="text/javascript" src="${ctxStatic}/picker/js/image-picker.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/My97DatePicker/WdatePicker.js"></script>
	
	<script type="text/javascript">
		var storage = window.localStorage;
		var token = '${token}';
		var lableId = '';
		$(document).ready(function() {
		    $("select.image-picker.show-labels").imagepicker({
				hide_select:true, 
				show_label:true
			});
		    
		    jQuery(".suCai17-background").slide({
                autoPlay: false,
                trigger: "click",
                easing: "easeOutCirc",
                delayTime: 1000
            });
		    
		    
		    if(screen.width>1440 && screen.widt<=1680){
		    	$("#picker").addClass("picker2");
		    }else if(screen.width<=1440){
		    	$("#picker").addClass("picker1");
		    }else{
		    	$("#picker").addClass("picker3");
		    }
		    
		});
		
		layui.config({
		    version: 222222
		    ,debug: true
		    ,base: '${pageContext.request.contextPath}/static/zn/js/layui/extend/'
		}).extend({
		    steps:"steps/steps"
		});
		
		layui.use(['jquery','laydate', 'steps'], function(){
		    var $ = layui.$;
		    var $step= $("#step_demo").step();
		    $("#saveBtn1").click(function(event) {
		    	var value = $("#lableId option:selected").val();
		    	if(!value){
		    		layer.alert('请选择标签样式', {icon: 2,title: "提示"});
		    	}else{
		    		lableId = value;
		    		getLableData();
		    		parent.$("#handle").val('3');
					$step.nextStep();//下一步
		    	}
		    });
		    
		    $("#saveBtn2").click(function(event) {
				var labelTemplateId = lableId;
				var applylableNum = $("#applylableNum").val();
				var addressId = $("#dzid").val();
				var traceProductId = '${productId}';
				var obj = document.getElementById("pcid");
				var index = obj.selectedIndex;
				var batchId = obj.options[index].value;
				var applyCode = obj.options[index].text;
				var totalPrice = document.getElementById("currentTotalPrice").innerText;
				var productName = "";
				var qualityGuaranteePeriod = "";
				var plantGround = "";
				var farmerName = "";
				var phoneNum = "";
				debugger
				if(labelTemplateId == '1' || labelTemplateId == '3'){
					productName = $("#productName").val();
					qualityGuaranteePeriod = $("#qualityGuaranteePeriod").val();
					plantGround = $("#plantGround").val();
					farmerName = $("#farmerName").val();
					phoneNum = $("#phoneNum").val();
				}else{
					productName = $("#cpmc").val();
					qualityGuaranteePeriod = $("#productName").val();
					plantGround = $("#bzq").val();
					farmerName = $("#qualityGuaranteePeriod").val();
				}
				
				if(!applylableNum){
					layer.alert('标签数量不能为空!', {
					    skin: 'layui-layer-lan'
					    ,closeBtn: 0
					    ,anim: 4 //动画类型
					  });
					return false;
				}
				if(!productName){
					layer.alert('标签内容需要填写完整，否则标签不可打印!', {
					    skin: 'layui-layer-lan'
					    ,closeBtn: 0
					    ,anim: 4 //动画类型
					  });
					return false;
				}
				if(!qualityGuaranteePeriod){
					layer.alert('标签内容需要填写完整，否则标签不可打印!', {
					    skin: 'layui-layer-lan'
					    ,closeBtn: 0
					    ,anim: 4 //动画类型
					  });
					return false;
				}
				
				if(!plantGround){
					layer.alert('标签内容需要填写完整，否则标签不可打印!', {
					    skin: 'layui-layer-lan'
					    ,closeBtn: 0
					    ,anim: 4 //动画类型
					  });
					return false;
				}
				if(!farmerName){
					layer.alert('标签内容需要填写完整，否则标签不可打印!', {
					    skin: 'layui-layer-lan'
					    ,closeBtn: 0
					    ,anim: 4 //动画类型
					  });
					return false;
				}
				
				if(labelTemplateId == '1' || labelTemplateId == '3'){
					if(phoneNum.length<4){
						layer.alert('标签内容需要填写完整，否则标签不可打印!', {
						    skin: 'layui-layer-lan'
						    ,closeBtn: 0
						    ,anim: 4 //动画类型
						  });
						return false;
					}
					if(labelTemplateId == '3'){
						if(productName.length>9){
							layer.alert('标签内容不能超过9个字，否则标签不可打印!', {
							    skin: 'layui-layer-lan'
							    ,closeBtn: 0
							    ,anim: 4 //动画类型
							  });
							return false;
						}
						if(qualityGuaranteePeriod.length>9){
							layer.alert('标签内容不能超过9个字，否则标签不可打印!', {
							    skin: 'layui-layer-lan'
							    ,closeBtn: 0
							    ,anim: 4 //动画类型
							  });
							return false;
						}
						
						if(plantGround.length>9){
							layer.alert('标签内容不能超过9个字，否则标签不可打印!', {
							    skin: 'layui-layer-lan'
							    ,closeBtn: 0
							    ,anim: 4 //动画类型
							  });
							return false;
						}
						if(farmerName.length>9){
							layer.alert('标签内容不能超过9个字，否则标签不可打印!', {
							    skin: 'layui-layer-lan'
							    ,closeBtn: 0
							    ,anim: 4 //动画类型
							  });
							return false;
						}
						if(phoneNum.length>14){
							layer.alert('电话长度不能超过14个字，否则标签不可打印', {
							    skin: 'layui-layer-lan'
							    ,closeBtn: 0
							    ,anim: 4 //动画类型
							  });
							return false;
						}
					}
					
					if(productName && productName.indexOf('产品名称') == -1){
						productName = "产品名称：" + productName;
					}
					if(qualityGuaranteePeriod && qualityGuaranteePeriod.indexOf('保质期') == -1){
						qualityGuaranteePeriod = "保质期：" + qualityGuaranteePeriod;
					}
					if(plantGround && plantGround.indexOf('产地') == -1){
						plantGround = "产地：" + plantGround;
					}
					if(farmerName.indexOf('负责人') == -1){
						if(farmerName.indexOf('经办人') == -1){
							farmerName = "负责人：" + farmerName;
						}
					}
					if(phoneNum.indexOf('电话') == -1){
						if(phoneNum.indexOf('联系方式') == -1){
							phoneNum = "电话：" + phoneNum;
						}
					}
				}
				
				$("#saveBtn2").attr("class","btn btn_default mouse_arrow");
				$("#saveBtn2").attr("disabled","disabled");
				
				var contentList = new Array();
				contentList.push({ applyElementContent: productName, elementId: '11', labelTemplateId: labelTemplateId});
				contentList.push({ applyElementContent: qualityGuaranteePeriod, elementId: '13', labelTemplateId: labelTemplateId});
				contentList.push({ applyElementContent: plantGround, elementId: '14', labelTemplateId: labelTemplateId});
				contentList.push({ applyElementContent: farmerName, elementId: '15', labelTemplateId: labelTemplateId});
				contentList.push({ applyElementContent: phoneNum, elementId: '16', labelTemplateId: labelTemplateId});
				var url = '${pageContext.request.contextPath}/api/pcLabel/saveLableApply?token='+token;
				var param = {
					labelTemplateId: labelTemplateId,
					applyNum: applylableNum,
					addressId: addressId,
					applyCode: applyCode,
					batchId: batchId,
					traceProductId: traceProductId,
					totalPrice: totalPrice,
					contentList: contentList
		    	};
				$.ajax({
			        type:"POST",
			        url: url,
			        dataType:"json",
			        contentType:"application/json",
			        data:JSON.stringify(param),
			        success:function (data) {
		            	$("#saveBtn2").attr("class","btn btn_primary");
						$("#saveBtn2").removeAttr("disabled");
			            if(data.code == 0){
			            	layer.alert('申请标签成功!', {icon: 1,title: "提示",yes:function(){
			            		parent.$("#handle").val('3');
								var index = parent.layer.getFrameIndex(window.name); 
								parent.layer.close(index);
							}});
			            }
			        },
			        error:function (i,n,m) {
			        	$("#saveBtn2").attr("class","btn btn_primary");
						$("#saveBtn2").removeAttr("disabled");
			            layer.alert('出现错误啦，请联系管理员!!!', {icon: 2,title: "提示"});
			        }
			 });
		    });
		    
		});
		
		//获取批次列表数据
		function getLableData(){
			var url = '${pageContext.request.contextPath}/api/pcLabel/getLabelInformation';
			var param = {
				token: token,
				productId: '${productId}',
				lableId: lableId
	    	};
			hui.loading();
			$.post(url, param, function(res) {
				if(res.code==0){
					var data = res.bodyData;
					var product = data.traceProduct;
					var batchList = data.batchList;
					var addressList = data.addressList;
					var traceLableTemplate = data.traceLableTemplate;
					
					var productName = data.productName;
					var qualityGuaranteePeriod = data.qualityGuaranteePeriod;
					var plantGround = data.plantGround;
					var farmerName = data.farmerName;
					var phoneNum = data.phoneNum;
					
					$('.suCai17-info table').remove();
	                var sxhtml = '<table border=1 style="cellspacing:10px;cellpadding:10px;background-color: #D9D9D9;" align="center">'
	                	+'<tr class="tr">'
	                	+'	<td class="tdOne">'
	                	+'		<div class="divOne">'
	                	+'			<p><span>产品名称：</span></p>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'	<td class="tdTwo">'
	                	+'		<div class="divTwo">'
	                	+'			<p><span>' + product.productName + '</span></p>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'	<td class="tdOne">'
	                	+'		<div class="divOne">'
	                	+'			<p><span>标签单价：</span></p>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'	<td class="tdTwo">'
	                	+'		<div class="divTwo">'
	                	+'			<p><span>' + traceLableTemplate.lableUnitPrice + '  元</span></p>'
	                	+'		</div>'
	                	+'			</td>'
	                	+'</tr>'
	                	+'<tr class="tr">'
	                	+'	<td class="tdOne">'
	                	+'		<div class="divOne">'
	                	+'			<p><span class="red font18 martop">&lowast;&nbsp;</span><span>标签个数：</span></p>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'	<td class="tdTwo">'
	                	+'		<div class="divTwo">'
	                	+'			<div style="float: left;margin-bottom: 4px;">'
	                	+'				<input id="applylableNum" type="number" min="1" max="10000" onchange="handleChange(this.value,' + lableId + ')" style="font-size:18px;height: 25px;">'
	                	+'			</div>'
	                	+'			<div style="float: left;margin-top: 4px;">'
	                	+'				<span style="font-size:12px;width:30px;">&nbsp;(个)</span>'
	                	+'				<span style="font-size:8px;color: red;">&nbsp;(注：输入个数自动计算总价)</span>'
	                	+'			</div>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'	<td class="tdOne">'
	                	+'		<div class="divOne">'
	                	+'			<p><span>标签总价：</span></p>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'	<td class="tdTwo">'
	                	+'		<div class="divTwo">'
	                	+'			<p><span id="currentTotalPrice">0</span><span>  元</span></p>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'</tr>'
	                	+'<tr class="tr">'
	                	+'	<td class="tdOne">'
	                	+'		<div class="divOne">'
	                	+'			<p><span>批 次 号：</span></p>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'	<td class="tdTwo" colspan="3">'
	                	+'		<div class="divTwo">'
	                	+'			<select id="pcid" style="font-size:18px;height: 30px;min-width: 250px">'
	                	+'				<option value="">请选择</option>';
	                for(var i=0;i<batchList.length;i++){
	                	sxhtml+='		<option value="' + batchList[i].id + '">' + batchList[i].batchCode + '</option>';
	                }
	                sxhtml+='		</select>'
	                	+'			<span style="font-size:8px;color: red;font-family:Microsoft YaHei">(注：选定批次号则申请的是绑定该批次号的标签，否则是无绑定批次号标签)</span>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'</tr>'
	                	+'<tr class="trTemplate">'
	                	+'	<td class="tdOne">'
	                	+'		<div class="divOne">'
	                	+'			<p><span class="red font18 martop">&lowast;&nbsp;</span><span>标签模板：</span></p>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'	<td class="tdTwo" colspan="3">'
	                	+'		<div class="divTwo">';
	                if(lableId == '1'){
	                	sxhtml+='	<div style="float: left;">'
		                	+'			 <div class="divpic1">'
		                	+'				    <div class="label1">'
		                	+'	  					<div id="u39_div" class="divla">'
		                	+'            				<p><input class="input_label_content" id="productName"  type="text" value="产品名称：' + productName + '" placeholder="自定义值,例如“产品名称：大黄桃”"  ></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla">'
		                	+'            				<p><input class="input_label_content" id="qualityGuaranteePeriod" value="保质期：' + qualityGuaranteePeriod + '" type="text" placeholder="自定义值,例如“保质期：30天”" ></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla">'
		                	+'            				<p><input class="input_label_content" id="plantGround"  type="text" value="产地：' + plantGround + '" placeholder="自定义值,例如“产地：湖南浏阳”" ></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla">'
		                	+'            				<p><input class="input_label_content" id="farmerName"  type="text" value="负责人：' + farmerName + '" placeholder="自定义值,例如“负责人：王一”" ></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla">'
		                	+'            				<p><input class="input_label_content" id="phoneNum"  type="text" value="电话：' + phoneNum + '" placeholder="自定义值,例如“电话号码：18588889999”"  ></input></p>'
		                	+'            			</div>'
		                	+'					</div>';
	                }
					if(lableId == '2'){
						sxhtml+='	<div style="float: left;">'
		                	+'			 <div class="divpic2">'
		                	+'				    <div class="label2">'
							+' 						<div id="u39_div" class="divla2">'
							+'            				<p><input class="input_label_content2" id="cpmc" value="产品名称" type="text"></input></p>'
	                		+'            			</div>'
		                	+'	  					<div id="u39_div" class="divla2">'
		                	+'            				<p><input class="input_label_content2" id="productName" value="' + productName + '" type="text"></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla2" style="margin-top:10px;">'
		                	+'            				<p><input class="input_label_content2" id="bzq" value="保质期" type="text"></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla2">'
		                	+'            				<p><input class="input_label_content2" id="qualityGuaranteePeriod" value="' + qualityGuaranteePeriod + '" type="text"></input></p>'
		                	+'            			</div>'
		                	+'            			<input class="input_label_content" id="plantGround" type="text" style="display:none" value="' + plantGround + '"></input>'
		                	+'            			<input class="input_label_content" id="farmerName" type="text" style="display:none" value="' + farmerName + '"></input>'
		                	+'            			<input class="input_label_content" id="phoneNum" type="text" style="display:none" value="' + phoneNum + '"></input>'
		                	+'					</div>';
	                }
					if(lableId == '3'){
	                	sxhtml+='	<div style="float: left;">'
		                	+'			 <div class="divpic3">'
		                	+'				    <div class="label3">'
		                	+'	  					<div id="u39_div" class="divla3">'
		                	+'            				<p><input class="input_label_content3" id="productName"  type="text" value="产品名称：' + productName + '" placeholder="自定义值,例如“产品名称：大黄桃”"  ></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla3">'
		                	+'            				<p><input class="input_label_content3" id="qualityGuaranteePeriod" value="保质期：' + qualityGuaranteePeriod + '" type="text" placeholder="自定义值,例如“保质期：30天”" ></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla3">'
		                	+'            				<p><input class="input_label_content3" id="plantGround"  type="text" value="产地：' + plantGround + '" placeholder="自定义值,例如“产地：湖南浏阳”" ></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla3">'
		                	+'            				<p><input class="input_label_content3" id="farmerName"  type="text" value="负责人：' + farmerName + '" placeholder="自定义值,例如“负责人：王一”" ></input></p>'
		                	+'            			</div>'
		                	+'            			<div id="u39_div" class="divla3">'
		                	+'            				<p><input class="input_label_content3" id="phoneNum"  type="text" value="电话：' + phoneNum + '" placeholder="自定义值,例如“电话号码：18588889999”"  ></input></p>'
		                	+'            			</div>'
		                	+'					</div>';
	                }
					sxhtml+='			</div>'
	                	+'			</div>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'</tr>'
	                	+'<tr class="trAddress">'
	                	+'	<td class="tdOne">'
	                	+'		<div class="divOne">'
	                	+'			<p><span>收货地址：</span></p>'
	                	+'		</div>'
	                	+'	</td>'
	                	+'	<td class="tdTwo" colspan="3">'
	                	+'		<div class="divTwo">'
	                	+'			<select id="dzid" style="font-size:15px;height: 34px;min-width: 480px;float:left;margin-top:2px;">'
	                	+'			    <option value="">请选择</option>';
                	for(var i=0;i<addressList.length;i++){
	                	sxhtml+='		<option value="' + addressList[i].id + '">' + addressList[i].addressMosaicing + '</option>';
	                }
	                sxhtml+='		</select>'
	                	+'			<div style="float:left;margin-left: 10px;padding-top: 4px;">'
	                	+'				<img alt="${ctxStatic}/images/plus.png" src="${ctxStatic}/images/plus.png" onclick="addAddress()">'
	                	+'			</div>'
	                	+'		</div>'
	                	+'	</td>'
		          	    +'</tr>'
	                    +'</table>';
	                if(sxhtml!=''){
	                	$('.suCai17-info').append(sxhtml);
	                }
            	}else{
            		layer.alert(res.message, {icon: 2,title: "提示"});
            	}
				hui.closeLoading();
			});
	    }
		
		function handleChange(num, labelId){
			var url ='${pageContext.request.contextPath}/api/pcLabel/traceLableCalculation';
			var param = {
				lableTemplateId: labelId,
				applyNum: num
	    	};
			$.get(url, param, function(res) {
				$("#currentTotalPrice").html(res.bodyData.currentTotalPrice);
			});
		}
		
		function addAddress(){
			var productId = '${productId}';
			storage.setItem("productName",$("#productName").val()); 
			storage.setItem("qualityGuaranteePeriod",$("#qualityGuaranteePeriod").val()); 
			storage.setItem("plantGround",$("#plantGround").val()); 
			storage.setItem("farmerName",$("#farmerName").val()); 
			storage.setItem("phoneNum",$("#phoneNum").val()); 
			storage.setItem("applylableNum",$("#applylableNum").val()); 
			storage.setItem("currentTotalPrice",document.getElementById("currentTotalPrice").innerText);
			var obj = document.getElementById("pcid");
			var index = obj.selectedIndex;
			storage.setItem("index",index);
			layer.open({
    	        type: 2,
    	        title: '新增地址',
    	        shadeClose: true,
    	        area: ['60%', '75%'],
    	        scrollbar: false, // 父页面 滚动条 禁止
    	        content:'${pageContext.request.contextPath}/api/pcLabel/newAddress?token='+token+'&productId='+productId,
    	    });
		};
		
	</script>
</body>
</html>
