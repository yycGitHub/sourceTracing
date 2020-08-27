<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">
        <li class="fa fa-remove"></li>
    </button>
    <h4 class="modal-title">选择候选组(多选)</h4>
</div>
<div class="modal-body">
    <div class="row" style="margin-top:-10px;margin-bottom: 10px;">
        <input type="hidden" id="groupIds">
        <input type="text" readonly id="groupNames" class="form-control" style="width:90%;float:left;margin-right:10px;" placeholder="已选用户组">
        <button type="button" class="btn btn-primary" data-btn-type="select" id="selectUser">选择</button>
    </div>
	<div class="box box-primary">
	    <!--隐藏域保存选中的用户-->
	   <div class="box-body">
	       <table id="group_select_table" class="display" style="width:100%">
	         <thead>
	   			 <tr>
			        <th>选择</th>
			        <th>角色ID</th>
			        <th>角色名</th>
	    		</tr>
			</thead>
	        </table>
	    </div>
    </div>

</div>
<script type="text/javascript">
	var groupSelectTable;
	var userSelectTable;
	var groupSelectTable = window.parent.jQuery('table[id = "group_select_table"]').DataTable({
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
	        url: "${ctx}/act/role/list"
	    },
	    "columnDefs":[{
	         'targets': 0,
	         'render': function (data, type, full, meta){
	             return '<input type="checkbox" value="1" id='+data.id+' onchange="userSelectCtrl.selectThis(this)">';
	         }
	     }],
	    "columns": [
	    {
	        "data": null //此列不绑定数据源，checkbox
	    },
	    {
	        "data": "id"
	    },
	    {
	        "data": "name"
	    }]
	});
	//前台添加序号
	groupSelectTable.on('order.dt search.dt',
	function() {
		groupSelectTable.column(0, {
	        "search": 'applied',
	        "order": 'applied'
	    }).nodes().each(function(cell, i) {
	        cell.innerHTML = i + 1;
	    });
	}).draw();
    var ids = "${ids}";//回填ids
    ids = ids == 0 ? '' : ids;
    //用户选择控制器
    var userSelectCtrl = {
        //查询 换页选择框回填
        setCheckBoxState: function () {
            var selectUserIds = jQuery("#groupIds").val();
            if (selectUserIds) {
                var userIdArr = selectUserIds.split(",");
                //选中增加的用户
                jQuery.each(userIdArr, function (index, userId) {
                    if (groupSelectTable.table.$("#" + userId).length > 0) {
                        groupSelectTable.table.$("#" + userId).find(":checkbox.checkbox_group").prop("checked", true);
                    }
                });
                //删除已经选中的
                groupSelectTable.table.$("tr").find(":checkbox.checkbox_group:checked").each(function () {
                    var curUserId = jQuery(this).parents("tr").attr("id");
                    //找不到，已经被删除
                    if (selectUserIds.indexOf(curUserId) == -1) {
                        groupSelectTable.table.$("#" + curUserId).find(":checkbox.checkbox_group").prop("checked", false);
                    }
                });
            } else {
                jQuery(":checkbox.checkbox_group").prop("checked", false);
            }
        },
        //绑定用户组选择事件
        bindSelectUserEvent: function () {
            jQuery("#selectUser").click(function () {
                var controllerScope = jQuery('div[ng-controller="KisBpmAssignmentPopupCtrl"]').scope();  // Get controller's scope
                var userIds = jQuery("#groupIds").val();
                var userNames = jQuery("#groupNames").val();
                controllerScope.setCandidateGroups(userIds, userNames);
                modals.hideWin("groupSelectWin");
            })
        },
        selectThis: function (obj) {
            var isChecked = jQuery(obj).is(":checked");
            //单选
            var userIds = jQuery("#groupIds").val();
            var value = jQuery(obj).attr("id");
            var userArr = this.getSelectedUserArr(userIds, value, isChecked);
            jQuery("#groupIds").val(userArr.join(","));
            this.updateSelectedUserNames();
        },
        updateSelectedUserNames: function () {
            var userIds = jQuery("#groupIds").val();
            if (userIds == 0 || !userIds) {
                jQuery("#groupNames").val("");
            } else {
                ajaxPost(basePath + "/act/group/names", {ids: userIds}, function (map) {
                    jQuery("#groupNames").val(map.name);
                });
            }
        },
        getSelectedUserArr: function (userIdss, curValue, isChecked) {
            var userArr = [];
            if (userIdss)
                userArr = userIdss.split(",");
            if (isChecked) {
                var flag = true;
                for (var i = 0; i < userArr.length; i++) {
                    if (userArr[i] == curValue) {
                        flag = false;
                        break;
                    }
                }
                if (flag)
                    userArr.push(curValue);
            } else {
                for (var i = 0; i < userArr.length; i++) {
                    var userIdValue = userArr[i];
                    if (userIdValue == curValue) {
                        userArr.splice(i, 1);
                        break;
                    }
                }
            }
            return userArr;
        }


    }
    //方法入口
    jQuery(function () {
        userSelectCtrl.bindSelectUserEvent();
    })
</script>
