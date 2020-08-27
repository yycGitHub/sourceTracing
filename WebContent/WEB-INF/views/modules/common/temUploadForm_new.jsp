<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<meta name="renderer" content="webkit">
	<title>标签打印</title>
	<link rel="stylesheet" type="text/css" href="${ctxStatic}/layer/skin/layer.css">
	<script type="text/javascript" src="${ctxStatic}/jquery/jquery-1.9.1.min.js"></script>
	<script type="text/javascript" src="${ctxStatic}/zn/js/users.js"></script>
	<script type="text/javascript" src="${ctxStatic}/zn/js/ws.js"></script>
	<script type="text/javascript" src="${ctxStatic}/layer/layer.js"></script>
	
	<style type="text/css">
		
	</style>
	<script type="text/javascript">
		var ws;
		$(document).ready(function() {
			connectServer();
			getData();
		});
		
		/** websocket接收数据函数 */
        ws.onmessage = function (message) {
            console.log("websocket:接收到服务器数据[" + message.data + "]");
            var json = JSON.parse(message.data);
            var msg = json.msg;
            var data = json.data;
            var code = json.code;
            switch(code){
                case "printComplete":
                completeFn(data);
                break;
                case "printerror":
                printerrorFn(msg);
                break;
                case "dataerror":
               	layer.alert(msg);
                break;
           }
        }

        /** websocket开启成功函数 */
        ws.onopen = function () {
            console.log("websocket:服务器连接成功");
            sendMessage(JSON.stringify({"code":"printStart"}));
        };

        /** websocket断开函数 */
        ws.onclose = function () {
            // 断开服务器后，自动重新连接
            console.log("websocket:服务器断开连接");
        }

        /** 连接服务器 */
        function connectServer() {
            if (ws) {
                ws.init({ // websokcet 对象初始化，连接下位机
                    url: "//127.0.0.1:5555"
                }).connect();
            }
        }

        /** 断开服务器 */
        function closeServer() {
            if (ws) {
                ws.close();
            }
        }

        /** 发送数据到服务器 */
        function sendMessage(msg) {
            if (ws) {
                ws.send(msg);
            }
        }
		
		function getData() {
			var applyId = '${applyId}';
			var token = '${token}';
			var printId = '${printId}';
			var jzId = '${jzId}';
			var xoffset = '${xoffset}';
			var yoffset = '${yoffset}';
			userInfo.saveToken(token);
			var serialNumberStart = '${serialNumberStart}';
			var serialNumberEnd = '${serialNumberEnd}';
			var labelId = '${labelId}';
    		var param = {
    			token: token,
    			applyId: applyId,
    			labelId: labelId,
   	    		printId: printId,
   	    		jzId: jzId,
   	    		xoffset: xoffset,
   	    		yoffset: yoffset,
   	    		serialNumberStart: serialNumberStart,
   	    		serialNumberEnd: serialNumberEnd
   	    	};
			var url1 = "${pageContext.request.contextPath}/api/common/printData";
			$.get(url1, param, function(res) {
				if(res.code == 0) {
					var map = {};
					map['code'] = "printData";
					map['data'] = JSON.stringify(res.bodyData);
					console.info(JSON.stringify(map));
					sendMessage(JSON.stringify(map));
				} else {
					layer.alert("查询内容失败");
				}
			}); 
		}
		
		/* function printContent(dataArr,applyContent,labelId){
			var map = {},data = {},labelContentList = [],pType = "",isMiddlePlace = false,labelWidth = 0,labelHeight = 0,labelNumber = 0,labelLeftmargin = 0,materialName = "",maximumPrintSpeed = 0;
			map['code'] = "printData";
			if(labelId == '1'){
				pType = "TSC TTP-346MU";
				labelWidth = 80;
				labelHeight = 40;
				labelNumber = 1;
				labelLeftmargin = 1.5;
				materialName = "亮白PET";
				maximumPrintSpeed = 4;
			}else if(labelId == '2'){
				pType = "Bar Code Printer G310";
				isMiddlePlace = true;
				labelWidth = 40;
				labelHeight = 30;
				labelNumber = 2;
				labelLeftmargin = 2;
				materialName = "亮白PET";
				maximumPrintSpeed = 4;
			}else if(labelId == '3'){
				pType = "Bar Code Printer G310";
				isMiddlePlace = true;
				labelWidth = 60;
				labelHeight = 40;
				labelNumber = 1;
				labelLeftmargin = 2;
				materialName = "普通铜版纸";
				maximumPrintSpeed = 5;
			}
			data['printer'] = {"printerType":pType,"isMiddlePlace":isMiddlePlace,"maximumPrintSpeed":10,"xOffset":0,"yOffset":-1,"resolvingPower":"300dpi","pixel":11.81};
			data['labelSpecification'] = {"labelWidth":labelWidth,"labelHeight":labelHeight,"labelNumber":labelNumber,"labelLeftmargin":labelLeftmargin,"labelHorizontalInterval":2,"labelVerticalInterval":2,"materialName":materialName,"maximumPrintSpeed":maximumPrintSpeed,"printingConcentration":10};
			if(labelId == '1'){
				data['labelElementList'] = 
				[
		            {
		                "elementID":"1",
		                "elementType":"qrCode",
		                "elementJson":"[{'resolvingPower':'203dpi','xStartPoint':6.12,'yStartPoint':8.31,'eCCLevel':'L','cellWidth':4,'mode':'A','rotation':0,'model':'M2','mask':'S3'},{'resolvingPower':'300dpi','xStartPoint':6.12,'yStartPoint':8.31,'eCCLevel':'L','cellWidth':6,'mode':'A','rotation':0,'model':'M2','mask':'S3'}]"
		            },
		            {
		                "elementID":"2",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':6.58,'yStartPoint':26.50,'fontHeight':2.54,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':'望城智慧农业'}]"
		            },
		            {
		                "elementID":"3",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':6.58,'yStartPoint':30.00,'fontHeight':2.54,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':'科技扶贫兴农'}]"
		            },
		            {
		                "elementID":"4",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':25.18,'yStartPoint':9.34,'fontHeight':2.54,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"5",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':25.18,'yStartPoint':13.15,'fontHeight':2.54,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"6",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':25.18,'yStartPoint':16.96,'fontHeight':2.54,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"7",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':25.18,'yStartPoint':20.77,'fontHeight':2.54,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"8",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':25.18,'yStartPoint':24.58,'fontHeight':2.54,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"9",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':25.18,'yStartPoint':28.39,'fontHeight':2.54,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            }
		        ];
			}else if(labelId == '2'){
				data['labelElementList'] = 
				[
		            {
		                "elementID":"1",
		                "elementType":"qrCode",
		                "elementJson":"[{'resolvingPower':'203dpi','xStartPoint':2,'yStartPoint':2,'eCCLevel':'M','cellWidth':5,'mode':'A','rotation':0,'model':'M2','mask':'S3'},{'resolvingPower':'300dpi','xStartPoint':2,'yStartPoint':2,'eCCLevel':'M','cellWidth':7,'mode':'A','rotation':0,'model':'M2','mask':'S3'}]"
		            },
		            {
		                "elementID":"2",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':24,'yStartPoint':3,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"3",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':24,'yStartPoint':7.5,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"4",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':24,'yStartPoint':12,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"5",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':24,'yStartPoint':16.5,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"6",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'203dpi','xStartPoint':2,'yStartPoint':21.8,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':'▁▁▁▁▁▁▁▁▁▁▁▁'},{'resolvingPower':'300dpi','xStartPoint':2,'yStartPoint':21,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':'▁▁▁▁▁▁▁▁▁▁▁▁'}]"
		            },
		            {
		                "elementID":"7",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':3.5,'yStartPoint':25.5,'fontHeight':2.5,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':'望城区智慧农业综合服务平台'}]"
		            }
		        ];
			}else if(labelId == '3'){
				data['labelElementList'] = 
				[
		            {
		                "elementID":"1",
		                "elementType":"qrCode",
		                "elementJson":"[{'resolvingPower':'203dpi','xStartPoint':2,'yStartPoint':4,'eCCLevel':'M','cellWidth':6,'mode':'A','rotation':0,'model':'M2','mask':'S3'},{'resolvingPower':'300dpi','xStartPoint':2,'yStartPoint':4,'eCCLevel':'M','cellWidth':9,'mode':'A','rotation':0,'model':'M2','mask':'S3'}]"
		            },
		            {
		                "elementID":"2",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':29,'yStartPoint':5,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"3",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':29,'yStartPoint':10,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"4",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':29,'yStartPoint':15,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"5",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':29,'yStartPoint':20,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"6",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':29,'yStartPoint':25,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':''}]"
		            },
		            {
		                "elementID":"7",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':1.8,'yStartPoint':29.5,'fontHeight':3,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':'▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁▁'}]"
		            },
		            {
		                "elementID":"8",
		                "elementType":"font",
		                "elementJson":"[{'resolvingPower':'default','xStartPoint':14.5,'yStartPoint':34,'fontHeight':2.5,'rotation':0,'fontShape':2,'underline':0,'fontName':'黑体','defaultfontData':'湖南望联农业科技有限公司'}]"
		            }
		        ];
			}
			
			for(var i=0; i<dataArr.length; i++) {
				var content = {};
				var traceCode = dataArr[i].traceCode;
				var url = "http://trace.sureserve.cn/v.html?traceCode=" + traceCode;
				var fontname1 = applyContent[0].applyElementContent;
				var fontname2 = applyContent[1].applyElementContent;
				var fontname3 = applyContent[2].applyElementContent;
				var fontname4 = applyContent[3].applyElementContent;
				if(labelId == '1'){
					var fontname5 = applyContent[4].applyElementContent;
					content = {
		                "qrcode": traceCode,
		                "isPrint": false,
		                "dataList": 
		                [
		                    {
		                        "elementID":"1",
		                        "elementData": url
		                    },
		                    {
		                        "elementID":"4",
		                        "elementData": fontname1
		                    },
		                    {
		                        "elementID":"5",
		                        "elementData": fontname2
		                    },
		                    {
		                        "elementID":"6",
		                        "elementData": fontname3
		                    },
		                    {
		                        "elementID":"7",
		                        "elementData": fontname4
		                    },
		                    {
		                        "elementID":"8",
		                        "elementData": fontname5
		                    },
		                    {
		                        "elementID":"9",
		                        "elementData": "身份编码:" + traceCode
		                    }
		                ]
		            };
				}else if(labelId == '2'){
					content = {
		                "qrcode": traceCode,
		                "isPrint": false,
		                "dataList": 
		                [
		                    {
		                        "elementID":"1",
		                        "elementData": url
		                    },
		                    {
		                        "elementID":"2",
		                        "elementData": fontname1
		                    },
		                    {
		                        "elementID":"3",
		                        "elementData": fontname2
		                    },
		                    {
		                        "elementID":"4",
		                        "elementData": fontname3
		                    },
		                    {
		                        "elementID":"5",
		                        "elementData": fontname4
		                    }
		                ]
		            };
				}else if(labelId == '3'){
					var fontname5 = applyContent[4].applyElementContent;
					content = {
		                "qrcode": traceCode,
		                "isPrint": false,
		                "dataList": 
		                [
		                    {
		                        "elementID":"1",
		                        "elementData": url
		                    },
		                    {
		                        "elementID":"2",
		                        "elementData": fontname1
		                    },
		                    {
		                        "elementID":"3",
		                        "elementData": fontname2
		                    },
		                    {
		                        "elementID":"4",
		                        "elementData": fontname3
		                    },
		                    {
		                        "elementID":"5",
		                        "elementData": fontname4
		                    },
		                    {
		                        "elementID":"6",
		                        "elementData": fontname5
		                    }
		                ]
		            };
				}
				labelContentList[i] = content;
			}
			if(labelContentList && labelContentList.length>0){
				data['labelContentList'] = labelContentList;
				map['data'] = JSON.stringify(data);
				console.info(JSON.stringify(map));
				sendMessage(JSON.stringify(map));
			}
		} */
		
		function completeFn(data) {
			saveTracePrintStatus(data);
			layer.msg('打印完成');
		}
		
		function printerrorFn(msg) {
			layer.confirm(msg, {
				title: false,
				btn: ['确定','取消'] //按钮
			}, function(ind){
				sendMessage(JSON.stringify({"code":"printContinue"}));
				layer.close(ind);
			}, function(inds){
				layer.close(inds);
			});
		}
		
		function saveTracePrintStatus(data) {
			var url = "${pageContext.request.contextPath}/api/common/updateCodeStatus";
			var codeStr = "";
			var jsonStr = JSON.parse(data);
			for(var i=0;i<jsonStr.length;i++){
				codeStr+= jsonStr[i].qrCode+",";
			}	
			if(codeStr){
				codeStr = codeStr.substring(0,codeStr.length-1);
			}
			var param = {code: codeStr};
			$.get(url, param, function(res) {
				if(res.code == 0) {
					if(res.bodyData){
						sendMessage(JSON.stringify({"code":"printSuccess","data":res.bodyData}));
					}
				} 
			});
		}
	</script>
	
</head>
<body>
	
</body>
</html>
