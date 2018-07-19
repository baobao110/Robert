var bpath=getRootPath();
var table=null;
var state=99;//全部题目
$("#schoolname").val("");
var json = {
		elem : '#tablearea99',
		
		
		cols : [ [ {
			type : 'numbers',
				width : '5%',
				title:'序号',
		}, {
			field : 'schoolcode',
			width : '10%',
			title : '学校编号'
		}, {
			field : 'schoolname',
			width : '20%',
			title : '学校名称'
		},{
			field : 'title',
			width : '35%',
			title : '省/市/区县',
			style:'display:block;height:auto',
			templet : '#tmdetail'
			
		}, {
			field : 'status',
			title : '状态',
			width : '10%',
		}, {
			field : 'ct',
			title : '操作',
			width : '20%',
			templet : '#dowork'
			
		}

		 ] ],
		page : true,
		url : bpath + "/school/querySchool2pt.do",
		where : {
			state:state,
			schoolname:$("#schoolname").val()
		},
		limit : 30
	};
layui.use([ 'form', 'layer', 'jquery', 'table','element' ], function() {
	var form = layui.form, layer = parent.layer === undefined ? layui.layer
			: parent.layer,element = layui.element;
	
	table= layui.table;

	table.render(json);

	$(".search_btn").on("click", function() {
		
		if ($("#schoolname").val() != null && $("#schoolname").val().trim() != "") {
			schoolname = $("#schoolname").val().trim();
		}
		json.where.schoolname=$("#schoolname").val();
		json.where.state=state;
		json.elem="#tablearea"+state;
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


function free_it(school_id) {
	 $.ajax({
		  type:"post",
		  url:bpath+"/school/free_school.do",
		  data:{"school_id":school_id},
		  dataType:"json",
		  success:function(data){
			 
			  if(data[0].back_code == 200){
				  window.location.href = getRootPath()+"/pages/pt/sc/scList.html";
			  }else{
				  layui_error("提交失败");
			  }
			   
		  }
	  });
}

function edit_it(school_id){
	window.location.href = getRootPath()+"/pages/pt/sc/setschool.html?school_id="+school_id;
}