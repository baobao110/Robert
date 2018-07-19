
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

/**
 * 显示图片
 * @param arr
 * @returns {String}
 */
function showImgHtml(arr) {
	var html = "";
	if (arr) {
		var one = arr[0];
		html += '<div class="onegimg" data="' + one + '"  >';
		html += '<img src="' + getRootPath() + one
				+ '" onclick="imgUtil.showLgImg(this)">';
		html += '<div class="delimg" onclick="imgUtil.delthisimg(this)"></div></div>';
		
	}

	return html;
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

	//标题
	if (messobj.title) {
		$("#title").val(messobj.title);
	}
	
	if (messobj._id) {
		if(messobj&&messobj.uilist){
			
			
			$("#allmodal").append(addModalByPptx(messobj.uilist));
		}

		var html = '';
		html += '<button class="layui-btn  layui-btn-suc"  onclick="change(this)"><i class="layui-icon">&#xe642;</i>修改</button>';
		$("#workdo").html(html);
	}
	

	form.render();
}


function addModalByPptx(arr){
	var html='';
	for(var i=0,len=arr.length;i<len;i++){
		var one=arr[i];
		html+=createOneModalHtml(one);
	}
	return html;
}

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
		url : getRootPath() + "/advertise/save.do",
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
					 window.location.reload();
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
		url : getRootPath() + "/advertise/change.do",
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
					/* window.location.reload();*/
					window.location.href=getRootPath()+"/pages/advertise/manage.html";
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

	if(uilist.length>0){
		messobj.uilist=uilist;
	}else{
		
		layer.tips('请选择展示图片', '#onemodal', {
			tips : [ 1, '#3595CC' ],
			time : 2000
		});
		scrollToLocation($(".onemodal"));

		return false;
	}
	
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
 */
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

}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//是否包含
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
}
//显示商品
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

}
function delthsiCoupons(obj) {
	$(obj).closest(".couponedit").remove();
}
/**
 * 显示单张券
 */
function cmshowDIV2(one) {

	var html = '';
	html += '<div class="couponedit" data=\'' + JSON.stringify(one) + '\'>';

	html += '<i class="layui-icon del" onclick="delthsiCoupons(this)">&#x1006;</i>';

	html += '<label>' + one.name + '</label>';

	html += '<span>';

	if (one.minOrderMoney > 0) {

		html += '<b>满' + (parseFloat(one.minOrderMoney) / 100).toFixed(2)
				+ '元，</b>';

	}
	if (one.discountType == 2) {

		html += '<b>折扣</b>' + (parseFloat(one.discountRate) / 10).toFixed(1)
				+ '折';

	} else {

		html += '<b>优惠</b>' + (parseFloat(one.discountMoney) / 100).toFixed(2)
				+ '元';
	}
	html += '</span>';

	html += '<span>';

	if (one.expireType == 2) {

		html += '有效期<b>' + one.validDays + '</b>天';

	} else {

		html += '有效期至<b>' + one.stopTime + '</b>';
	}

	html += '</span>';

	html += '<div class="arrow"><div class="arrowfont"></div></div>';
	html += '</div>';

	return html;

}
//显示券模板
function showSaleCoupons(coupons) {

	var html = '';
	if (coupons && coupons.length > 0) {
		for (var i = 0, len = coupons.length; i < len; i++) {

			html += cmshowDIV2(coupons[0]);

		}
	}

	return html;

}
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
/**
 * 配置销售
 * @param obj
 */
function pz_sale(obj) {
	//可操作
	if (!$(obj).hasClass("layui-btn-disabled")) {
		var startup_sale_modal = messobj.startup_sale_modal;
		var goods = startup_sale_modal.goods;//商品
		var fhtypes = startup_sale_modal.fhtypes;//发货方式，数组，1在线发货2到店取货
		if (fhtypes == null) {
			fhtypes = [];
		}
		var cmids = startup_sale_modal.cmids;//可用券ids
		var cmmodals = startup_sale_modal.cmmodals;//可用券模板
		if (cmids == null) {
			cmids = [];
		}
		//

		var html = '<br>';
		html += '<div class="layui-form" id="coupon_edit_modal2">';

		html += ' <div class="layui-form-item">';
		html += ' <label class="layui-form-label"><b class="red">*</b>选择商品</label>';
		html += ' <div class="layui-input-block" id="onecouponedit">';

		html += showSaleGoods(goods);
		html += '<div onclick="chooseGoods(this)" id="chooseGoods_btnn" class="layui-btn layui-btn-sm layui-btn-primary addbtnmodal"><i class="fa fa-cube"></i>选择商品</div>';

		html += '</div>';
		html += '</div>';

		html += ' <div class="layui-form-item">';
		html += ' <label class="layui-form-label"><b class="red">*</b>发货方式</label>';
		html += ' <div class="layui-input-block">';

		html += ' <input name="fhtypes" ';

		if (containsID(fhtypes, 1)) {
			html += ' checked ';
		}

		html += ' lay-skin="primary" value="1" title="快递到家"  type="checkbox">';
		html += '<input name="fhtypes" ';
		if (containsID(fhtypes, 2)) {
			html += ' checked ';
		}
		html += ' lay-skin="primary" value="2" title="到店自提" type="checkbox">';

		html += '<p class="info">设置到店自提，请提前设置线下店铺</p>';
		html += '</div>';

		html += '</div>';

		html += ' <div class="layui-form-item">';
		html += ' <label class="layui-form-label">可用券</label>';
		html += ' <div class="layui-input-block">';

		html += showSaleCoupons(cmmodals);
		html += '<div onclick="selectCoupons(this)" class="layui-btn layui-btn-sm layui-btn-primary addbtnmodal"><i class="fa fa-ticket"></i>选择券模板</div>';

		html += '</div>';
		html += '</div>';

		html += '</div>';

		var sale_modal_add = layer.open({
			type : 1,
			title : "配置销售",
			skin : 'layui-layer-demo', //样式类名

			anim : 2,
			shadeClose : true, //开启遮罩关闭
			area : [ '650px', '450px' ],
			content : html,
			btn : [ "确认", "取消" ],
			yes : function() {
				var garr = [];
				$(document.getElementById("onecouponedit")).find(
						".oneChooseGood").each(function() {
					garr.push(eval("(" + $(this).attr("data") + ")"));

				});

				if (garr.length > 0) {
					messobj.startup_sale_modal.goods = garr;
				} else {
					layer.msg("请选择商品");

					return false;
				}

				var fhtypes = [];

				$("input[name='fhtypes']:checked").each(function() {
					fhtypes.push(Number($(this).val()));
				});
				if (fhtypes.length > 0) {
					messobj.startup_sale_modal.fhtypes = fhtypes;
				} else {

					layer.msg("请选择发货方式");

					return false;
				}
				var cmmodals = [];
				var cmids = [];

				$(".couponedit").each(function() {
					var oneobj = eval("(" + $(this).attr("data") + ")");
					cmmodals.push(oneobj);
					cmids.push(oneobj._id);
				});
				messobj.startup_sale_modal.cmmodals = cmmodals;
				messobj.startup_sale_modal.cmids = cmids;

				layer.close(sale_modal_add);

			},
			btn2 : function() {

				layer.close(sale_modal_add);
			}
		});

		form.render();

	}

}

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/**
 * 显示配置抢券
 * 券模板，发放数量，每人限领
 * 
 */
function showOneCM_ROLL(oneroll) {
	var html = '';
	html += '<div class="layui-container one_coupon_modal" data=\''
			+ JSON.stringify(oneroll) + '\'>';
	html += '<i class="layui-icon del" title="删除" onclick="delThisCoupon(this)">&#x1006;</i>';
	html += '<div class="layui-row"><span class="title"><b class="fa fa-ticket"></b>'
			+ oneroll.cm.name + '</span><br></div>';
	html += '<div class="layui-row"><label>发放总数:</label><span>' + oneroll.limit
			+ '</span></div>';
	html += '<div class="layui-row"><label>每人限领:</label><span>'
			+ oneroll.onelimit + '</span></div>';

	html += '<div class="layui-row wei" ><div class="layui-btn layui-btn-sm layui-btn-primary" onclick="editRollModal(this)"><i class="layui-icon">&#xe642;</i>编辑</div></div>';
	html += '</div>';
	return html;
}
function delThisCoupon(obj) {
	$(obj).closest(".one_coupon_modal").remove();

}
function editRollModal(obj) {
	var mobj = $(obj).closest(".one_coupon_modal");
	var str = mobj.attr("data");
	addCouponsModal(eval("(" + str + ")"), mobj);
}

function pz_coupon(obj) {
	//可操作
	if (!$(obj).hasClass("layui-btn-disabled")) {
		//券领取设置
		var html = '';
		html += '<div id="allcoupons_area">';
		var startup_coupon_modal = messobj.startup_coupon_modal;
		var rolls = startup_coupon_modal.rolls;
		if (rolls && rolls.length > 0) {

			for (var i = 0, len = rolls.length; i < len; i++) {

				var oneroll = rolls[i];

				html += showOneCM_ROLL(oneroll);

			}

		}

		html += '</div>';
		var coupon_modal = layer.open({
			type : 1,
			title : "发放券配置",
			skin : 'layui-layer-demo', //样式类名

			anim : 2,
			shadeClose : true, //开启遮罩关闭
			area : [ '650px', '400px' ],
			content : html,
			btn : [ "选择券模板", "确认设置", "取消" ],
			yes : function() {

				addCouponsModal();

			},
			btn2 : function() {

				var arr = [];

				$(".one_coupon_modal").each(function() {

					arr.push(eval("(" + $(this).attr("data") + ")"));

				});

				messobj.startup_coupon_modal.rolls = arr;

				layer.close(coupon_modal);

			},
			btn3 : function() {

				layer.close(coupon_modal);
			}
		});

	}

}
function cmshow(roll) {
	var html = '';
	if (roll) {

		html += cmshowDIV(roll.cm);

	} else {

		html += '<div class="layui-btn layui-btn layui-btn-primary" onclick="selCouponModal(this)"><i class="fa fa-ticket"></i>选择券模板</div>';
	}

	return html;

}
function cmshowDIV(one) {

	var html = '';
	html += '<div class="couponedit" onclick="selCouponModal(this)" data=\''
			+ JSON.stringify(one) + '\'>';

	html += '<label>' + one.name + '</label>';

	html += '<span>';

	if (one.minOrderMoney > 0) {

		html += '<b>满' + (parseFloat(one.minOrderMoney) / 100).toFixed(2)
				+ '元，</b>';

	}
	if (one.discountType == 2) {

		html += '<b>折扣</b>' + (parseFloat(one.discountRate) / 10).toFixed(1)
				+ '折';

	} else {

		html += '<b>优惠</b>' + (parseFloat(one.discountMoney) / 100).toFixed(2)
				+ '元';
	}
	html += '</span>';

	html += '<span>';

	if (one.expireType == 2) {

		html += '有效期<b>' + one.validDays + '</b>天';

	} else {

		html += '有效期至<b>' + one.stopTime + '</b>';
	}

	html += '</span>';

	html += '<div class="arrow"><div class="arrowfont"></div></div>';
	html += '</div>';

	return html;
}

/**
 * 调用券选择工具
 */
function selCouponModal() {

	couponUtil.init(function() {

		$("#onecouponedit").html(cmshowDIV(this.chooseArr[0]));

	}, merid, 1);
}
function addCouponsModal(roll, mmobj) {
	var html = '<br>';
	html += '<div class="layui-form" id="coupon_edit_modal2">';

	html += ' <div class="layui-form-item">';
	html += ' <label class="layui-form-label"><b class="red">*</b>选择券模板</label>';
	html += ' <div class="layui-input-block" id="onecouponedit">';
	html += cmshow(roll);

	html += '</div>';
	html += '</div>';

	html += ' <div class="layui-form-item">';
	html += ' <label class="layui-form-label"><b class="red">*</b>发放数量</label>';
	html += ' <div class="layui-input-block">';
	var limit = "";
	if (roll && roll.limit) {
		limit = roll.limit;
	}

	html += ' <input name="limit" onkeyup="value=value.replace(/[^\\d]/g,\'\')" id="limit" value="'
			+ limit
			+ '"  autocomplete="off" placeholder="张" class="layui-input width150" type="text">';
	html += '</div>';
	html += '</div>';

	html += ' <div class="layui-form-item">';
	html += ' <label class="layui-form-label"><b class="red">*</b>每人限领</label>';
	html += ' <div class="layui-input-block">';

	var onelimit = "";
	if (roll && roll.onelimit) {
		onelimit = roll.onelimit;
	}
	html += ' <input name="onelimit"  onkeyup="value=value.replace(/[^\\d]/g,\'\')"  id="onelimit" value="'
			+ onelimit
			+ '" autocomplete="off" placeholder="张" class="layui-input width150" type="text">';
	html += '</div>';
	html += '</div>';

	html += '</div>';

	var coupon_modal_add = layer.open({
		type : 1,
		title : "编辑券发放",
		skin : 'layui-layer-demo', //样式类名

		anim : 2,
		shadeClose : true, //开启遮罩关闭
		area : [ '400px', '350px' ],
		content : html,
		btn : [ "确认", "取消" ],
		yes : function() {
			if ($("#onecouponedit").find(".couponedit").length == 1) {
				var cm = JSON.parse($("#onecouponedit").find(".couponedit")
						.attr("data"));
				var mobj = $(document.getElementById("coupon_edit_modal2"));
				if (mobj.find("#limit").val() == ""
						|| mobj.find("#onelimit").val() == "") {
					layer.msg("请输入发放数量");
					return;
				} else {
					var limit = Number(mobj.find("#limit").val());
					var onelimit = Number(mobj.find("#onelimit").val());

					if (isNaN(limit) || isNaN(onelimit)) {
						layer.msg("请输入数字");
						return;
					} else {
						if (onelimit > limit) {

							layer.msg("单人领取数量不能大于发放总数");
							return;
						} else {

							if (roll == null) {
								roll = {};
								roll.cm = cm;
								roll.limit = limit;
								roll.onelimit = onelimit;

								$("#allcoupons_area").append(
										showOneCM_ROLL(roll));

							} else {

								roll.cm = cm;
								roll.limit = limit;
								roll.onelimit = onelimit;

								$("#allcoupons_area").append(
										showOneCM_ROLL(roll));
								$(mmobj).remove();
							}

							layer.close(coupon_modal_add);

						}

					}

				}

			} else {

				layer.msg("请选择券模板");
				return;
			}

		},
		btn2 : function() {

			layer.close(coupon_modal_add);
		}
	});

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

	
	

	html += '<div class="layui-form-item">';
	html += '<label class="layui-form-label">图片</label>';
	html += '<div class="layui-input-block">';

	html += '<div id="imgsdiv">';
	
	if (imgs.length > 0) {
		html += showImgHtml(imgs);
	}

	html += '<div class="imgdivshow" onclick="addGOODSimgs(this)">';
	html += '<img src="/ay/assets/css/pc_mess/147cac6faffcfb9e91559391300c6b9f.png" ';
	html += 'style="width:41%">';
	html += '</div>';
	html += '</div>';

	html += '<p class="info">图片推荐尺寸：<b>1400*900</b>';
	html += '</p>';

	html += '</div>';
	html += '</div>';
	html += '</li>';

	return html;

}
function delthisModal(obj){
	$(obj).closest(".onemodal").remove();
	
}