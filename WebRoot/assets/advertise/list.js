var bpath=getRootPath();
var table=null;
$("#title").val("");
var json = {
		elem : '#tablearea',
		
		
		cols : [ [ {
			type : 'numbers',
				width : '5%',
				title:'序号',
		}, {
			field : '_id',
			width : '25%',
			title : 'ID编号'
		}, {
			field : 'title',
			width : '20%',
			title : '广告语'
		}, {
			field : 'time',
			title : '创建时间',
			width : '20%',
		},  {
			field : 'status',
			title : '状态',
			width : '15%',
		}, {
			field : 'ct',
			title : '操作',
			width : '15%',
			templet : '#dowork'
			
		}

		 ] ],
		page : true,
		url : bpath + "/advertise/query.do",
		where : {
			title:$("#title").val()
		},
		limit : 30
	};
layui.use([ 'form', 'layer', 'jquery', 'table','element' ], function() {
	var form = layui.form, layer = parent.layer === undefined ? layui.layer
			: parent.layer,element = layui.element;
	
	table= layui.table;

	table.render(json);

	$(".search_btn").on("click", function() {
		json.where.title=$("#title").val();
		table.render(json);
	});

});


function free_it(_id) {
	 $.ajax({
		  type:"post",
		  url:getRootPath()+"/advertise/free.do",
		  data:{"_id":_id},
		  dataType:"json",
		  success:function(ajaxDAO){
			 
			  if(ajaxDAO.success){
				  window.location.href = bpath+"/pages/advertise/manage.html";
			  }else{
				  layui_error("删除失败");
			  }
			   
		  }
	  });
}

function edit_it(_id){
	
	window.location.href =bpath+"/pages/advertise/add.html?messid="+_id;
}

function add(){
	window.location.href = bpath+"/pages/advertise/add.html"; 
}
