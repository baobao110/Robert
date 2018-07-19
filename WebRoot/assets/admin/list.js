var bpath=getRootPath();
var table=null;
$("#name").val("");
var json = {
		elem : '#tablearea',
		
		
		cols : [ [ {
			type : 'numbers',
				width : '10%',
				title:'序号',
		}, {
			field : 'name',
			width : '20%',
			title : '用户名'
		}, {
			field : 'phone',
			width : '30%',
			title : '联系方式'
		}, {
			field : 'status',
			title : '状态',
			width : '20%',
		}, {
			field : 'ct',
			title : '操作',
			width : '20%',
			templet : '#dowork'
			
		}

		 ] ],
		page : true,
		url : bpath + "/admin/admin.do",
		where : {
			uuid:get("UUID"),
			name:$("#name").val()
		},
		limit : 30
	};
layui.use([ 'form', 'layer', 'jquery', 'table','element' ], function() {
	var form = layui.form, layer = parent.layer === undefined ? layui.layer
			: parent.layer,element = layui.element;
	
	table= layui.table;

	table.render(json);

	$(".search_btn").on("click", function() {
		json.where.name=$("#name").val();
		table.render(json);
	});
	
	 element.on('tab(docDemoTabBrief)', function(elem){
		   state=Number($(this).attr('data'));
		   if ($("#schoolname").val() != null && $("#schoolname").val().trim() != "") {
			   schoolname = $("#schoolname").val().trim();
			   json.where.schoolname = schoolname;
			}
		   else{
			   json.where.schoolname =null;
		   }
			json.where.state=state;
			json.elem="#tablearea"+state;
			table.render(json);
	});

});


function free_it(name) {
	 $.ajax({
		  type:"post",
		  url:bpath+"/admin/delete.do",
		  data:{"name":name,"uuid":get("UUID")},
		  dataType:"json",
		  success : function(ajaxDAO) {
	            if (ajaxDAO.success) {
	            	window.location.href =bpath+ '/admin/toPage.do';
	            }
	            else{
	            	alert(ajaxDAO.message);
	           }
	          
	        }
	    });
}

function edit_it(name){
	window.location.href = bpath+"/pages/admin/setadmin.html?name="+name;
}

function add() {
	
	window.location.href = bpath+"/pages/admin/setadmin.html";
}
