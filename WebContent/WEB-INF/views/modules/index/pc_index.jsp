<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="Content-Type" content="text/x-component" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="renderer" content="webkit">
	<title>首页</title>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/bootstrap-select.min.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/s.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/fselect.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/base.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/pcindex/css/choice.css">
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/layer/skin/layer.css">
	<style type="text/css">
		html, body {
		    width: 100%;
		    height: 100%;
		    overflow:scroll;
		    overflow-x:hidden;
		    overflow-y:hidden;
		}
		
		.show_bg {
		    background: url(${ctxStatic}/pcindex/images/bg01.png) no-repeat 0 0;
		    height: calc(100% - 15px);
		    background-size: 100% 100%;
		}
		
		.pro_title {
		    position: absolute;
		    top: 0;
		    padding: 4px 10px 0 10px;
		    line-height: 1;
		    background: #f3f3f3;
		    width: calc(22.5% - 20px);
		}
		
		.pro_title b {
		    padding-left: 5px;
		    margin: 6px 5px 0 0;
		    border-left: 3px solid #27af59;
		    font-size: 16px;
		    line-height: 1;
		}
		
		.pro_list dt p:first-child {
		    border-bottom: 1px solid #eee;
		    height: 95px;
		    overflow: hidden;
		}
		
		.pro_list .photo {
		    float: left;
		    margin-top: 5px;
		    margin-right: 10px;
		    width: 85px;
		    height: 85px;
		}
		
		.pro_list .see {
		    position: absolute;
		    right: 20px;
		    top: 130px;
		    color: #27af59;
		    height:24px;
		    width:100px;
		    text-align:right;
		    padding-right:30px;
		    line-height:24px;
		    background: url(${ctxStatic}/pcindex/images/down_24px.png) no-repeat right 0;
		}
		
		.pro_list .seecur {
		    color: #f00;
		    height:24px;
		    width:100px;
		    background: url(${ctxStatic}/pcindex/images/up_24px.png) no-repeat right 0;
		    
		}
	
		#bg{ 
			display: none; 
			position: absolute; 
			top: 0%; 
			width: 26.5%; 
			height: 100%; 
			background-color: gray; 
			z-index:1001; 
			-moz-opacity: 0.75; 
			opacity:.75; 
			filter: alpha(opacity=75);
		}
		
		#show{ 
			display: none; 
			position: absolute;
			left:35.75%;
			top: 40%;
			margin-left:-80px;
			width: 160px; 
			height: 182px;
			background-color: white; 
			z-index:1002;
			overflow: hidden;
		}
		
		.show h2 {
		    margin: 10px 0 0 0;
		    height: 30px;
		    overflow: hidden;
		}

		.code{
			padding-left:2px;
			padding-top:2px;
			text-align:center;
			overflow: hidden;
		}
		
		.radio_success{
			padding-top:20px;
		}
		optgroup {
		 color:red;
		 }
		
		:not(.input-group)>.bootstrap-select.form-control:not([class*=col-]) {
		    width: 50%;
		}
		
		.bs-searchbox .form-control {
		    margin-bottom: 0;
		    width: 70%;
		    float: none;
		}
		
		.bootstrap-select .dropdown-menu li {
		    position: relative;
		    padding-left: 5px;
		    padding-right: 5px;
		}
		
		li {
		    line-height: 15px;
		}
	</style>
</head>
<body>
	<input id="handle" value="" hidden="hidden">
	<div class="warpper">
	  <div class="f_l pro">
	    <h2 class="pro_title">
	      <b class="f_l">产品</b>
	      	<select class="form-control selectpicker h_80" id="gsid" data-live-search="true" onchange="changeTest3()">
	      		<c:forEach items="${map}" var="item">
				    <optgroup label="${item.key}">  
				 		<c:forEach items="${item.value}" var="li">
				 		    <c:if test="${li.selected == 1}"><option value = "${li.id}">${li.name}</option></c:if>
				 		    <c:if test="${li.selected == 2}"><option selected value = "${li.id}">${li.name}</option></c:if>
				   		</c:forEach>
				    </optgroup>  
			    </c:forEach>
			</select> 
	      <a class="f_r add" onclick="addProduct()">
	        <em style="padding-top:6px">新增产品</em>
	        <img src="${ctxStatic}/pcindex/images/icon01.png" height="18" />
	      </a>
	    </h2>
	    <div class="f_l pro_list" id="product_list">
	      
	    </div>
	  </div>
	  <div class="f_l show">
	  	<div id="bg"></div>
	  	<div id="show">
		     <div class="code">
			     <img src="${ctxStatic}/pcindex/images/ewm.png" height="156" width="156" />
			     <div style="color:green;font-size:14px;">溯源示例</div>
		     </div>
	    </div>

	    <div id="yl" class="f_l maxbox show_bg">
	      <h2>
	      <b class="f_l">预览</b>
	      </h2>
	      <div class="mobile"><iframe class="framing" name="view" id="view" src='/v.html?traceCode=10002900012018114201812' frameborder="0" width="100%" height="100%"></iframe></div>
	      <div class="show_btn">
	        <ul>
	          <li><a onclick="applyLabel()">申请标签</a></li>
	          <li id="editBtn"><a onclick="editBatch()">编&nbsp;&nbsp;&nbsp;辑</a></li>
	          <li><a onclick="deleteBatch()">删&nbsp;&nbsp;&nbsp;除</a></li>
	        </ul>
	      </div>
	    </div>
	  </div>
	  <div class="f_l main">
	    <iframe id="main" class="framing" name="show" src="${pageContext.request.contextPath}/api/pcIndex/main?token=${token}" frameborder="0" allowtransparency="true"></iframe>
	  </div>
	</div>
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/pcindex/js/sureserve.js"></script>
	<script type="text/javascript" src="${ctxStatic}/pcindex/js/jquery.SuperSlide2.1.2.js"></script>
	<script type="text/javascript" src="${ctxStatic}/layer/layer.js"></script>
	<script type="text/javascript" src="${ctxStatic}/pcindex/js/jquery.charfirst.pinyin.js"></script>
	<script type="text/javascript" src="${ctxStatic}/pcindex/js/sort.js"></script>
	<script type="text/javascript" src="${ctxStatic}/pcindex/js/bootstrap.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/pcindex/js/bootstrap-select.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/pcindex/js/i18n/defaults-zh_CN.min.js"></script>
	
	<script type="text/javascript">
		var index = 0;
		var selproductId = "";
		var selbatchId = "";
		var counter = 0; /*计数器*/
	    var pageStart = 1; /*offset*/
	    var pageSize = 5; /*size*/
	    var productArr = new Array();
	    
		$(document).ready(function() {
			document.getElementById("bg").style.display ="block";
			document.getElementById("show").style.display ="block";
			//首次加载产品列表
		    getProductData(pageStart, pageSize);
		    //首次加载主题列表
		    //getThemeData();
			
		    /*监听加载更多*/
		    $(document).on('click', '.js-load-more', function () {
		    	pageStart++;
		    	getProductData(pageStart, pageSize);
		    });
		    
		    /*监听加载更多*/
		    $(document).on('click', '.js-load-more1', function () {
		    	pageStart++;
		    	changeTest2(pageStart, pageSize,1);
		    });
		    
		});
		
	    //获取产品列表数据
		function getProductData(offset,size){
			 $('.js-load-more1').remove();
			var url = '${pageContext.request.contextPath}/api/pcIndex/productList1';
			var param = {
				token: '${token}',
				pageno: offset,
				pagesize: size
	    	};
			$.get(url, param, function(res) {
				if(res.code==0){
					var picurl = "http://nanxian.sureserve.cn/upload/product_model/20190724200230639.png";
            		var data = res.bodyData.list;
            		var sum = res.bodyData.list.length;
            		var count = res.bodyData.count;
	                var result = '';
	                counter++;
	                /************业务逻辑块：实现拼接html内容并append到页面*****************/
	                console.log(offset , size, sum,count);
	                $('.js-load-more').remove();
	                for (var i = 0; i < sum; i++) {
	                	var id = "dl"+i;
	                    result+= '<dl id=\"'+ id +'\"><dt>'
                    		+ '<p>'
                    		+ '<img class="photo" src=\"'+ data[i].productPicUrl +'\" onerror="javascript:this.src=\''+picurl+'\'">'
                    		+ '<b>' + data[i].productName + '</b>'
                    		+ '<input class="btn btn_primary" style="float:right;margin-top:14px;" onclick="addBatch(\''+ data[i].id +'\')" type="button" value="新增批次"/>'
                    		+ '<em style="float:left">时间：' + data[i].cjrq + '</em><br>'
                    		+ '<em>已申请标签数：' + data[i].sqs + '个</em>'
                    		+ '<em>(其中<span style="color:gray">待审核：' + data[i].dsh + '个</span>，<span style="color:green">审核通过：' + data[i].yjhs + '个</span>，<span style="color:red">审核未通过：' + data[i].wjhs + '个</span>)</em>'
                    		+ '</p>'
                            + '<div class="show_btn1">'
                            + '<ul>';
                        if(data[i].auditFlag=="0"){
                        	result+= '<li><a onclick="editProduct(\''+ data[i].id +'\')">编辑</a></li>';
                        }
                        result+= '<li><a onclick="deleteProduct(\''+ data[i].id +'\',\''+ data[i].productName +'\')">删除</a></li>'
                            + '</ul>'
                            + '</div>'
                            + '</dt>'
                            + '<a class="see" onclick="lookPC(this,' + data[i].batchCount + ',\''+ data[i].id +'\')">查看批次(' + data[i].batchCount + '批)</a>'
                            + '<dd class="pc">'
                            + '</dd>'
                    		+ '</dl>';
	                }
	                result+= '<dl class="js-load-more" style="padding-bottom:8px;cursor:pointer;"><p align="center">加载更多</p></dl>';
	                $('#product_list').append(result);
	                /*******************************************/
	                /*隐藏more*/
	                if (offset*size >= count) {                
	                    //$(".js-load-more").hide();
	                    $('.js-load-more').remove();
	                }
            	}else{
            		//layer.alert(res.message, {icon: 2,title: "提示"});
            	}
			});
	    }
	    
		function changeTest3(){
			pageStart = 1;
			pageSize = 5;
			changeTest2(pageStart, pageSize,0);
		}
	    
	    //下拉框选择公司对应产品
	    function changeTest2(pageStart, pageSize,flag){
	    	$('.js-load-more').remove();
	    	if(flag==0){
	    		$("#product_list").empty();
	    	}
	    	
	    	var officeId = document.getElementById("gsid").value;
	    	var url2 = '${pageContext.request.contextPath}/api/pcIndex/main?token=${token}&officeId='+officeId;
	    	$('#main').attr('src', url2);
			var param = {
				token:'${token}',
				officeId:officeId,
				pageno: pageStart,
				pagesize: pageSize
			};
			layer.msg('加载中...');
	 		var url ='${pageContext.request.contextPath}/api/pcIndex/getProductList1';
	 		$.get(url,param,function(res){
	 			if(res.code==0){
	 				var data = res.bodyData.list;
            		var sum = res.bodyData.list.length;
            		var count = res.bodyData.count;
   	                var result = '';
   	                counter++;
   	             	console.log(pageStart , pageSize, sum,count);
   	                /************业务逻辑块：实现拼接html内容并append到页面*****************/
   	                $('.js-load-more1').remove();
   	                for (var i = 0; i < sum; i++) {
   	                	var id = "dl"+i;
   	                    result+= '<dl id=\"'+ id +'\"><dt>'
                       		+ '<p>'
                       		+ '<img class="photo" src=\"'+ data[i].productPicUrl +'\">'
                       		+ '<b>' + data[i].productName + '</b>'
                       		+ '<em style="float:left">时间：' + data[i].cjrq + '</em><br>'
                       		+ '<input class="btn btn_primary" style="float:right" onclick="addBatch(\''+ data[i].id +'\')" type="button" value="新增批次"/>'
                       		+ '<em>已申请标签数：' + data[i].sqs + '</em>'
                       		+ '<em>(其中<span style="color:gray">待审核：' + data[i].dsh + '个</span>，<span style="color:green">审核通过：' + data[i].yjhs + '个</span>，<span style="color:red">审核未通过：' + data[i].wjhs + '个</span>)</em>'
                       		+ '</p>'
                               + '<div class="show_btn1">'
                               + '<ul>';
	   	                if(data[i].auditFlag=="0"){
	                     	result+= '<li><a onclick="editProduct(\''+ data[i].id +'\')">编辑</a></li>';
	                    }
	   	              	result+= '<li><a onclick="deleteProduct(\''+ data[i].id +'\',\''+ data[i].productName +'\')">删除</a></li>'
                               + '</ul>'
                               + '</div>'
                               + '</dt>'
                               + '<a class="see" onclick="lookPC(this,' + data[i].batchCount + ',\''+ data[i].id +'\')">查看批次(' + data[i].batchCount + '批)</a>'
                               + '<dd class="pc">'
                               + '</dd>'
                       		+ '</dl>';
   	                }
   	             	result+= '<dl class="js-load-more1" style="padding-bottom:8px;cursor:pointer;"><p align="center">加载更多</p></dl>';
	                $('#product_list').append(result);
	                /*******************************************/
	                /*隐藏more*/
	                if (pageStart*pageSize >= count) {                
	                    //$(".js-load-more").hide();
	                    $('.js-load-more1').remove();
	                }
            	}else{
            		//$('.js-load-more').remove();
            		//layer.alert(res.message, {icon: 2,title: "提示"});
            	}
	 			layer.closeAll();
	 		});
	    }
	    
	    var flag = false;
	    //查看批次列表点击事件
	    function lookPC(obj,count,productId){
	    	selproductId = productId;
	    	if(count>0){
	    		$(obj).next(".pro_list dd").slideToggle(800).parent().siblings().children(".pro_list dd").slideUp(800);
				$(obj).toggleClass("seecur");
				if(!$(obj).hasClass("seecur")){
					$(obj).removeClass("seecur");
					if(flag){
						document.getElementById("view").src = '/v.html?traceCode=10002900012018114201812';
						document.getElementById("bg").style.display ="block";
						document.getElementById("show").style.display ="block";
						flag = false;
					}
				}else{
					getBatchData(productId);
					$(obj).addClass("seecur");
					$(obj).parent().siblings().children(".pro_list dl a").removeClass("seecur");
				}
	    	}
	    }
	    
		//获取批次列表数据
		function getBatchData(productId){
			var url = '${pageContext.request.contextPath}/api/pcIndex/getBatchList1';
			var param = {
				token: '${token}',
				productId: productId
	    	};
			$.get(url, param, function(res) {
				if(res.code==0){
					productArr.push(productId);
            		var data = res.bodyData;
            		var sum = data.length;
	                var result = '';
	                $('#product_list .pc a').remove();
	                for (var i = 0; i < sum; i++) {   
	                	/* var jhm = '无';
	                	if(data[i].jhm){
	                		jhm = data[i].jhm;
	                	} */
	                    result+= '<a onclick="pcShow(this,\''+ data[i].id +'\',\''+ data[i].auditFlag +'\')">'
	                        + '<b>批次编号：' + data[i].batchCode + '</b>'
	                        + '<em>生成日期：' + data[i].cjrq + '</em>'
	                        + '<em>待审核标签：' + data[i].dsh + '个</em>'
	                        + '<em><span style="color:green;">审核通过标签：' + data[i].yjhs + '个</span></em>'
	                        + '<em><span style="color:red;">审核未通过标签：' + data[i].wjhs + '个</span></em>'
	                        + '</a>';
	                }
	                if(result!=''){
	                	$('#product_list .pc').append(result);
	                }
            	}else{
            		layer.alert(res.message, {icon: 2,title: "提示"});
            	}
			});
	    }
	    
	  	//新增产品事件
	    function addProduct(){
	    	var officeId = document.getElementById("gsid").value;
	    	layer.open({
    	        type: 2,
    	        title: '新增产品',
    	        shadeClose: false,
    	        area: ['60%', '90%'],
    	        scrollbar: false, // 父页面 滚动条 禁止
    	        content: '${pageContext.request.contextPath}/api/pcIndex/addProductPage?token=${token}&officeId='+ officeId,
			    end: function () {
			    	var handle = $("#handle").val();
			        if ( handle && handle == '1' ) {
			        	location.reload();
			        }
 	            }
    	    });
	    }
	  	
	  	//修改产品事件
	    function editProduct(id){
	    	layer.open({
    	        type: 2,
    	        title: '编辑产品',
    	        shadeClose: false,
    	        area: ['60%', '90%'],
    	        scrollbar: false, // 父页面 滚动条 禁止
    	        content: '${pageContext.request.contextPath}/api/pcIndex/editProductPage?token=${token}&productId='+id,
			    end: function () {
			    	var handle = $("#handle").val();
			        if ( handle && handle == '2' ) {
			        	location.reload();
			        }
 	            }
    	    });
	    }
	  	
	    function deleteProduct(id,name){
	    	 layer.confirm('确认要删除'+name+'吗？', {title:"提示",
	             btn : [ '确定', '取消' ]//按钮
	         }, function(index) {
	             layer.close(index);
	             var index1 = layer.load(0,{shade: [0.7, '#393D49']}, {shadeClose: true}); //0代表加载的风格，支持0-2
	             var url = '${pageContext.request.contextPath}/api/pcIndex/deleteProduct';
				 var param = {
					 token: '${token}',
					 id: id
		    	 };
	             $.post(url, param, function(res) {
					layer.close(index1);
					if(res.code==0){
						var index2 = layer.alert('删除成功', {icon: 1,title: "提示",yes:function(){
							layer.close(index2);
							location.reload();
						}});
					}else{
						layer.alert(res.message, {icon: 2,title: "提示"});
					}
				});
	         }); 
	    }
	    
	  	//新增批次
	    function addBatch(id){
	    	layer.open({
    	        type: 2,
    	        title: '新增批次',
    	        shadeClose: false,
    	        area: ['60%', '90%'],
    	        scrollbar: false, // 父页面 滚动条 禁止
    	        content: '${pageContext.request.contextPath}/api/pcIndex/addBatchPage?token=${token}&isNew=1&productId='+id,
    	        end: function () {
    	        	var handle = $("#handle").val();
			        if ( handle && handle == '5' ) {
			        	location.reload();
			        }
  	            }
    	    });
	    }
	    
	    //编辑批次
	    function editBatch(){
	    	layer.open({
    	        type: 2,
    	        title: '编辑批次',
    	        shadeClose: false,
    	        area: ['60%', '90%'],
    	        scrollbar: false, // 父页面 滚动条 禁止
    	        content: '${pageContext.request.contextPath}/api/pcIndex/editBatchPage?token=${token}&isNew=2&batchId='+selbatchId+'&productId='+selproductId,
			    end: function () {
			    	var handle = $("#handle").val();
			        if ( handle && handle == '5' ) {
			        	location.reload();
			        }
 	            }
    	    });
	    }
	    
	    //删除批次
	    function deleteBatch(){
	    	layer.confirm('您确认要删除该批次吗？', {
	 		   btn: ['确定','取消'] //按钮
	 		}, function(){
	 			var url = '${pageContext.request.contextPath}/api/pcLabel/deleteBatch?batchId='+selbatchId;
	 			var param = {
						 token: '${token}'
			    	 };
	 			$.get(url, param, function(res){
	 				if(res.code == 0) {
							var msg = res.message;
							layer.alert(msg, {icon: 1,title: "提示",yes:function(index){
								layer.close(index);
								location.reload();
			 				}});
						} else {
							var msg = res.message;
							layer.alert(msg, {icon: 2,title: "提示",yes:function(index){
								layer.close(index);
			 				}});
						} 
	 			});
	 		}, function(){
	 			
	 		});
	    }
	    
	    //申请标签
	    function applyLabel(){
	    	layer.open({
    	        type: 2,
    	        title: '申请标签',
    	        shadeClose: false,
    	        area: ['85%', '90%'],
    	        scrollbar: false, // 父页面 滚动条 禁止
    	        content: '${pageContext.request.contextPath}/api/pcLabel/applyLablePage1?token=${token}&batchId='+selbatchId+'&productId='+selproductId,
    	        end:function (){
    	        	localStorage.clear();
    	        	var handle = $("#handle").val();
			        if ( handle && handle == '3' ) {
			        	location.reload();
			        }
    	        }
    	    });
	    }
	    
	    //标签绑定
	    function bindLabel(){
	    	layer.open({
    	        type: 2,
    	        title: '标签绑定',
    	        shadeClose: false,
    	        area: ['60%', '70%'],
    	        scrollbar: false, // 父页面 滚动条 禁止
    	        content: '${pageContext.request.contextPath}/api/pcLabel/bindLabel?token=${token}&batchId='+selbatchId+'&productId='+selproductId,
    	        end:function (){
    	        	var handle = $("#handle").val();
			        if ( handle && handle == '4' ) {
			        	location.reload();
			        }
    	        }
    	    });
	    }
	  
	    //批次选中事件
	    function pcShow(obj,batchId,auditFlag){
	    	selbatchId = batchId;
	    	if(auditFlag == '0'){
	    		$("#editBtn").show();
	    	}else{
	    		$("#editBtn").hide();
	    	}
	    	document.getElementById("bg").style.display ="none";
			document.getElementById("show").style.display ="none";
	    	$(".pro_list dd").find("a.cur").removeClass("cur");
			$(obj).addClass("cur");
			flag = true;
			//预览页面
			show(batchId, auditFlag);
	    }
	    
		function show(batchId, auditFlag){
			document.getElementById("view").src = '/src/traceInfoPreview.html?batchId='+batchId+'&auditFlag='+auditFlag;
	    }
		
	</script>
</body>
</html>
