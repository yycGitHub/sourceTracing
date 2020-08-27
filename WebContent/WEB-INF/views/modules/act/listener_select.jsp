<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
        <li class="fa fa-remove"></li>
    </button>
    <h4 class="modal-title">选择任务监听器</h4>
</div>
<div class="modal-body">
    <div class="box box-primary">
        <!--隐藏域保存选中的用户-->
        <div class="box-body">
            <table id="listener_select_table" class="display" style="width:100%">
            	 <thead>
		            <tr>
		            	<th></th>
		                <th>监听名称</th>
						<th>监听类型</th>
						<th>Event属性</th>
						<th>值类型</th>
		            </tr>
		        </thead>
            </table>
        </div>
    </div>
</div>
<script type="text/javascript">
		var t = window.parent.jQuery('table[id = listener_select_table]').DataTable({
			"paging": true,//开启表格分页
	        "aLengthMenu":false,  //用户可自选每页展示数量 5条或10条
	        "bLengthChange": false, 
	        "pageLength": 10,
	        "serverSide":true,  //服务端
            "retrieve":false, //意思是如果已经初始化了，则继续使用之前的Datatables实例。
		    "ordering":false,
			"language": {  
			     "sProcessing": "处理中...",  
			     "sLengthMenu": "显示 _MENU_ 项结果",  
			     "sZeroRecords": "没有匹配结果",  
			     "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",  
			     "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",  
			     "sInfoFiltered": "(由 _MAX_ 项结果过滤)",  
			     "sInfoPostFix": "",  
			     "sSearch": "搜索:",  
			     "sUrl": "",  
			     "sEmptyTable": "表中数据为空",  
			     "sLoadingRecords": "载入中...",  
			     "sInfoThousands": ",",  
			     "oPaginate": {  
			         "sFirst": "首页",  
			         "sPrevious": "上页",  
			         "sNext": "下页",  
			         "sLast": "末页"  
			     },  
			     "oAria": {  
			         "sSortAscending": ": 以升序排列此列",  
			         "sSortDescending": ": 以降序排列此列"  
			     }  
			},  
		    "ajax": {
		        //指定数据源
		        url: "${ctx}/act/listener/select/list?type_=${type}",
		        dataType	: "json"
		    },
		    "columnDefs":[{
		         'targets': 0,
		         'render': function (data, type, full, meta){
		             return '<input type="checkbox" name="listener_checkbox" class="select-checkbox" id='+data.id+'>';
		         }
		     }],
		    "columns": [
			    {
			        "data": null //此列不绑定数据源，checkbox
			    },
			    {
			        "data": "name_"
			    },
			    {
			        "data": "type_"
			    },
			    {
			        "data": "event_"
			    },
			    {
			        "data": "classField_"
			    }
		    ],
		    select: {
		        style:    'os',
		        blurable: true
		    },
		    dom: 'Bfrtip',
	        buttons: [
                {
                    text: '选择',
                    action: function ( e, dt, node, config ) {
                  	  selectListener();
                    }
    	        }
            ]
		});
</script>
<script>
    var table = window.parent.jQuery('table[id = listener_select_table]').DataTable()
    function selectListener(){
		var controllerScope = jQuery('div[ng-controller="KisBpmTaskListenersPopupCtrl"]').scope();  // Get controller's scope 
		var i=0;
		jQuery("input[name='listener_checkbox']").each(function () {
            if (this.checked) {
            	var data = table.row(jQuery(this).parent().parent()).data();
            	controllerScope.taskListeners.push({ event : data.event_,
            		implementation : data.name_,
            		className : data.classField_,
            		expression: '',
            		delegateExpression: ''});
            	controllerScope.taskListeners[i].fields = [];
//             	console.info(controllerScope.taskListeners[0]);
            	controllerScope.taskListeners[i].fields.push({ name : 'fieldName',
                     implementation : '',
                     stringValue : '',
                     expression: '',
                     string: ''});
            	i=i+1;
            }
        });
		modals.hideWin("listenersSelectWin");
		controllerScope.$apply();
	}
</script>