
function stertup_choose(form) {

}

/**
 * 添加图片
 */
function addGOODSimgs(obj) {

	imgUtil.init(function() {
		if (this.chooseArr != null && this.chooseArr.length > 0) {

			var html = showImgHtml(this.chooseArr);

			
			$(obj).siblings(".onegimg").remove();
			$(obj).before(html);

		}

	}, merid);

}

/**
 * 选择偏好
 * @param obj
 */
function chooseINDU(obj) {
	industryChoose.init(function() {
		if (industryChoose.choose_arr && industryChoose.choose_arr.length > 0) {

			messobj.userlikelimit = industryChoose.choose_arr;

			showMess();
		}

	});
}

function saveMess(obj) {

	if (!saveMessData()) {
		//不能保存
		return;
	}

	var index = layer.load(1, {
		shade : [ 0.1, '#fff' ]
	//0.1透明度的白色背景
	});
	

	$.ajax({
		type : 'post',
		url : getRootPath() + "/music/save.do",
		data : {
			data : JSON.stringify(messobj)
		},
		dataType : "text",
		beforeSend : function() { //触发ajax请求开始时执行
			$(obj).attr("diasbled", "disabled");

		},
		success : function(data) {
			layer.close(index);
		
			if (data == "200") {

				layer.confirm('保存成功，进入列表页面', {
					btn : [ '确认' ]
				//按钮
				}, function() {
					window.location.href = getRootPath()
							+ "/pages/music/center.html";
				});
			} else {

				layer.confirm('保存出错，进入列表页面', {
					btn : [ '确认', '取消' ]
				//按钮
				}, function() {
					window.location.href = getRootPath()
							+ "/pages/music/center.html";
				}, function() {

					layer.closeAll();
				});
			}

		},
		complete : function() {
			$(obj).removeAttr("disabled");
		},

	})

}

/**
 * 根据数据显示消息
 */
function showMess(messobj) {
	
	if(null==messobj||""==messobj)
	{
		//新建
		addModalByuser();
		var html = '';
		html += '<button class="layui-btn  layui-btn-suc"  onclick="saveMess(this)"><i class="layui-icon">&#xe654;</i>创建</button>';
		$("#workdo").html(html);
	}

	/*//标题
	if (messobj.title) {
		$("#title").val(messobj.title);
	}*/
	
	/*if (messobj._id) {
		if(messobj&&messobj.uilist){
			
			
			$("#allmodal").append(addModalByPptx(messobj.uilist));
		}

		var html = '';
		html += '<button class="layui-btn  layui-btn-suc"  onclick="change(this)"><i class="layui-icon">&#xe642;</i>修改</button>';
		$("#workdo").html(html);
	}*/
	var html = '';
	html += '<button class="layui-btn  layui-btn-suc"  onclick="change(this)"><i class="layui-icon">&#xe642;</i>创建</button>';
	$("#workdo").html(html);

	form.render();
}


/*function addModalByPptx(arr){
	var html='';
	for(var i=0,len=arr.length;i<len;i++){
		var one=arr[i];
		html+=createOneModalHtml(one);
	}
	return html;
}*/

/**
 * 保存
 * @param obj
 */
function saveMess(obj) {

	if (!saveMessData()) {
		//不能保存
		return;
	}

	var index = layer.load(1, {
		shade : [ 0.1, '#fff' ]
	//0.1透明度的白色背景
	});
	

	$.ajax({
		type : 'post',
		url : getRootPath() + "/music/save.do",
		data : {
			data : JSON.stringify(messobj)
		},
		dataType : "text",
		beforeSend : function() { //触发ajax请求开始时执行
			$(obj).attr("diasbled", "disabled");

		},
		success : function(data) {
			layer.close(index);
		
			if (data == "200") {

				layer.confirm('保存成功，进入列表页面', {
					btn : [ '确认' ]
				//按钮
				}, function() {
					window.location.href = getRootPath()
					+ "/pages/music/center.html";
				});
			} else {
				
				layer.confirm('保存出错，进入列表页面', {
					btn : [ '确认', '取消' ]
				//按钮
				}, function() {
					 window.location.reload();
				}, function() {

					layer.closeAll();
				});
			}

		},
		complete : function() {
			$(obj).removeAttr("disabled");
		},

	})

}

function change(obj) {

	if (!saveMessData()) {
		//不能保存
		return;
	}

	var index = layer.load(1, {
		shade : [ 0.1, '#fff' ]
	//0.1透明度的白色背景
	});
	

	$.ajax({
		type : 'post',
		url : getRootPath() + "/music/save.do",
		data : {
			data : JSON.stringify(messobj)
		},
		dataType : "text",
		beforeSend : function() { //触发ajax请求开始时执行
			$(obj).attr("diasbled", "disabled");

		},
		success : function(data) {
			layer.close(index);
		
			if (data == "200") {

				layer.confirm('保存成功，进入列表页面', {
					btn : [ '确认' ]
				//按钮
				}, function() {
					window.location.href = getRootPath()
					+ "/pages/music/center.html";
				});
			} else {
				
				layer.confirm('保存出错，进入列表页面', {
					btn : [ '确认', '取消' ]
				//按钮
				}, function() {
					 window.location.reload();
				}, function() {

					layer.closeAll();
				});
			}

		},
		complete : function() {
			$(obj).removeAttr("disabled");
		},

	})

}

/**
 * 校验数据
 * @returns {Boolean}
 */
function saveMessData() {

	var title = $("#title").val();

	if (title != null && title.trim() != "") {

		messobj.title = title;
	} else {
		layer.tips('课程标题不能为空', '#title', {
			tips : [ 1, '#3595CC' ],
			time : 2000
		});
		scrollToLocation($("#title"));

		return false;
	}
	
	var uilist=[];
	if ($(".onemodal").length>0) {
		$(".onemodal").each(function(){
			var one={};
			if($(this).find(".onegimg").length>0){
				one.img=$(this).find(".onegimg").attr("data");
			}else{
				uilist=[];
				return false;
			}
			
			one.audio=null;
			if($(this).find(".myaudio")!= null){
				one.audio = $(this).find(".myaudio").attr("srcurl");
			}else{
				uilist=[];
				return false;
			}
			
			one.info=$(this).find(".content").val();
			uilist.push(one);
		});
	}
		scrollToLocation($(".onemodal"));

	
	return true;
}

/**
 * 滚动到指定位置
 * @param obj
 */
function scrollToLocation(obj) {

	var container = $('#bodyarea');
	var scrollTo = $(obj);
	var h = scrollTo.offset().top - container.offset().top
			+ container.scrollTop();
	$("html,body").animate({
		scrollTop : h
	}, 10);
}

/////////////////////////////////////////////////////////////
/**
 * 配置销售名额
 *//*
function pz_salename(obj) {

	if (!$(obj).hasClass("layui-btn-disabled")) {
		var note = '';
		var limit = 0;
		var price = 0;
		if (messobj.startup_salename_modal) {

			if (messobj.startup_salename_modal.note) {
				note = messobj.startup_salename_modal.note;
			}
			if (messobj.startup_salename_modal.limit) {
				limit = messobj.startup_salename_modal.limit;
			}
			if (messobj.startup_salename_modal.price) {
				price = messobj.startup_salename_modal.price;
			}

		}

		var html = '<br>';
		html += '<div class="layui-form" id="salename_modal">';

		html += ' <div class="layui-form-item">';
		html += ' <label class="layui-form-label"><b class="red">*</b>名额说明</label>';
		html += ' <div class="layui-input-block" id="onecouponedit">';

		html += '<input name="salename_note" value="'
				+ note
				+ '" id="salename_note" autocomplete="off" placeholder="请输入名额说明" class="layui-input width200" type="text">';

		html += '</div>';
		html += '</div>';

		html += ' <div class="layui-form-item">';
		html += ' <label class="layui-form-label"><b class="red">*</b>发放数量</label>';
		html += ' <div class="layui-input-block">';

		html += ' <input name="limit" onkeyup="value=value.replace(/[^\\d]/g,\'\')" id="limit" value="'
				+ limit
				+ '"  autocomplete="off" placeholder="张" class="layui-input width150" type="text">';
		html += '</div>';
		html += '</div>';

		html += ' <div class="layui-form-item">';
		html += ' <label class="layui-form-label"><b class="red">*</b>购买单价</label>';
		html += ' <div class="layui-input-block">';

		html += '<input name="price" autocomplete="off" value="'
				+ (parseFloat(price) / 100)
				+ '" class="layui-input width150"   type="text" id="price"  placeholder="￥"  onFocus="nstchange(this)"   onKeyUp="nst(this)"  onBlur="nstblurchange(this)" >';
		html += '</div>';
		html += '</div>';

		html += '</div>';

		var sale_modal_add = layer.open({
			type : 1,
			title : "配置销售名额",
			skin : 'layui-layer-demo', //样式类名

			anim : 2,
			shadeClose : true, //开启遮罩关闭
			area : [ '500px', '350px' ],
			content : html,
			btn : [ "确认", "取消" ],
			yes : function() {
				var mobj = $(document.getElementById("salename_modal"));
				var note = "";
				if (mobj.find("#salename_note").val() != "") {
					note = mobj.find("#salename_note").val();
				}
				var limit = 0;
				if (mobj.find("#limit").val() != "") {

					limit = Number(mobj.find("#limit").val());
				}
				var price = 0;
				if (mobj.find("#price").attr("data") != "") {
					price = parseInt(parseFloat(mobj.find("#price")
							.attr("data")) * 100);
				}

				messobj.startup_salename_modal = {
					note : note,
					limit : limit,
					price : price
				};
				layer.close(sale_modal_add);

			},
			btn2 : function() {

				layer.close(sale_modal_add);
			}
		});

		form.render();

	}

}*/

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/*//是否包含
function containsID(arr, id) {
	if (arr) {
		for (var i = 0, len = arr.length; i < len; i++) {
			var one = Number(arr[i]);
			if (one == id) {
				return true;
			}

		}
	}

	return false;
}*/
/*//显示商品
function showSaleGoods(goods) {
	var html = '';
	if (goods) {
		var pathurrl = getRootPath();
		for (var i = 0, len = goods.length; i < len; i++) {
			var one = goods[i];

			html += '<div class="oneChooseGood" onclick="goodsUtil.chooseImg(this)" data=\''
					+ JSON.stringify(one) + '\'>';

			html += '<i class="layui-icon del" onclick="goodsUtil.deleteThisChooseGoods(this)" >&#x1006;</i>';

			if (one.imgs && one.imgs.length > 0) {
				html += '<div class="imgshow"><img src="' + pathurrl
						+ one.imgs[0] + '"></div>';

			}
			html += '<div class="leftdiv">';

			html += '<label>' + one.name + '</label>';

			var price = 0;
			if (one.price) {
				price = one.price;
			} else if (one.siblists[0].price) {
				price = one.siblists[0].price;
			}

			html += '<label class="price">'
					+ (parseFloat(price) / 100).toFixed(2) + '元/份</label>';
			html += '</div>';

			html += '<div class="arrow"><div class="arrowfont"></div></div>';
			html += '</div>';

		}
	}

	return html;

}*/
function delthsiCoupons(obj) {
	$(obj).closest(".couponedit").remove();
}
/**
 * 显示单张券
 *//*

//显示券模板
function showSaleCoupons(coupons) {

	var html = '';
	if (coupons && coupons.length > 0) {
		for (var i = 0, len = coupons.length; i < len; i++) {

			html += cmshowDIV2(coupons[0]);

		}
	}

	return html;

}*/
function chooseGoods(obj) {
	goodsUtil.init(function() {
		$(obj).siblings(".oneChooseGood").remove();
		var html = showSaleGoods(this.chooseArr);
		$(obj).before(html);

	}, merid, 1);

}
function selectCoupons(obj) {

	couponUtil.init(function() {

		var html = '';
		for (var i = 0, len = this.chooseArr.length; i < len; i++) {

			html += cmshowDIV2(this.chooseArr[0]);

		}

		$(obj).before(html);
	}, merid, 2);

}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

//单独添加
function addModalByuser() {

	var onehtml = createOneModalHtml();
	$("#allmodal").append(onehtml);

	layer.closeAll();
}
//单个数据
function createOneModalHtml(data) {
	var html = '';
	var content = "";
	if (data && data.info) {

		content = data.info;
	}
	var imgs = [];
	if (data && data.img) {
		imgs.push(data.img);
	}
	
	var audio = null;
	if(data && data.audio){
		audio =data.audio;
	}
	 
	html += '<li class="layui-timeline-item onemodal"><i';
	html += ' class="layui-icon layui-timeline-axis" data="&#xe656;">&#xe656;</i>';
	html += '<div class="layui-timeline-content layui-text">';

	
	html += '<div class="layui-tab-item layui-show" id="startupdiv">';
	
	html+='<div class="layui-form-item"><label class="layui-form-label">音频</label>';
	html+='<div class="layui-input-block" id="mp3">';
	/*if(audio != null){
		html+='<span><audio name="myaudio" class="myaudio" srcurl="'+audio+'" src="'+getRootPath()+audio+'" controls=""></audio></span>';
	}else{
		html+='<span></span>';
	}*/

	html+='<center><button class="layui-btn layui-btn-primary layui-btn-sm ump3"  onclick="uploadmy_show(this);">上传音频文件</button></div><div>';

	return html;

}
function delthisModal(obj){
	$(obj).closest(".onemodal").remove();
	
}