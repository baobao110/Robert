var bpath=getRootPath();
var table=null;
$("#stuname").val("");
var state=99;//全部题目
$("#schoolcode").val("");

var json = {
		
		elem : '#tablearea99',
		
		
		cols : [ [ {
			type : 'numbers',
				width : '3%',
				title:'序号',
		}, {
			field : 'school',
			width : '15%',
			title : '学校',
			templet : '#school',
		}, {
			field : 'name',
			width : '8%',
			title : '姓名'
		},{
			
			field : 'studentid',
			
			width : '8%',
			
			title : '学号',
			
		},
		{
			field : 'class',
			
			width : '10%',
			
			title : '班级',
			
			templet : '#class',
			
		},
		
		{
			field : 'result',
			width : '13.5%',
			title : '竞答结果',
			templet : '#result'
			
		},{
			field : 'parent',
			width : '7%',
			title : '家长',
			
		},
		{
			field : 'phone',
			width : '7%',
			title : '联系方式',
			
		},
		{
			field : 'status',
			title : '状态',
			width : '10%',
		}, {
			field : 'ct',
			title : '操作',
			width : '19%',
			templet : '#dowork'
			
		}

		 ] ],
		page : true,
		url : bpath + "/school/query_student.do",
		where : {
			student_name:$("#stuname").val(),
			school_code:$("#schoolcode").val(),
			state:state,
		},
		limit : 30
	};


layui.use([ 'form', 'layer', 'jquery', 'table','element' ], function() {
	var form = layui.form, layer = parent.layer === undefined ? layui.layer
			: parent.layer,element = layui.element;
	
	table= layui.table;

	table.render(json);

	$(".search_btn").on("click", function() {
		var school_code="";
		
		if ($("#stuname").val() != null && $("#stuname").val().trim() != "") {
			student_name= $("#stuname").val().trim();
			json.where.student_name=student_name;
		}
		if ($("#schoolcode").val() != null && $("#schoolcode").val().trim() != "") {
			school_code= $("#schoolcode").val().trim();
			json.where.school_code=school_code;
		}
		json.where.state=state;
		json.elem="#tablearea"+state;
		table.render(json);
	});
	
	 element.on('tab(docDemoTabBrief)', function(elem){
		   state=Number($(this).attr('data'));
		   if ($("#stuname").val() != null && $("#stuname").val().trim() != "") {
			   student_name= $("#stuname").val().trim();
			   json.where.student_name =student_name;
			}
		   else{
			   json.where.student_name =null;
		   }
		   
		   if ($("#schoolcode").val() != null && $("#schoolcode").val().trim() != "") {
			   schoolcode= $("#schoolcode").val().trim();
			   json.where.school_code=schoolcode;
			}
		   else{
			   json.where.school_code=null;
		   }
			json.where.state=state;
			json.elem="#tablearea"+state;
			table.render(json);
	});
	
});
//删除
function delete_it(id){
	if(window.confirm("你确定删除该学生吗")){
		 $.ajax({
			  type:"post",
			  url:bpath+"/school/delete_student.do",
			  data:{"sid":id},
			  dataType:"json",
			  success:function(data){
				 
				  if(data[0].back_code == 200){
					  window.location.href = bpath+"/pages/pt/sc/student_list.html";
				  }else{
					  layui_error("提交失败");
				  }
				   
			  }
		  });
	}
	
}
function free_it(_id) {
	 $.ajax({
		  type:"post",
		  url:bpath+"/school/free_student.do",
		  data:{"sid":_id},
		  dataType:"json",
		  success:function(data){
			 
			  if(data[0].back_code == 200){
				  window.location.href = bpath+"/pages/pt/sc/student_list.html";
			  }else{
				  layui_error("提交失败");
			  }
			   
		  }
	  });
}

function edit_it(_id){
	
	window.location.href =bpath+"/pages/pt/sc/setstudent.html?sid="+_id;
}