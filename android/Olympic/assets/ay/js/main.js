/*function orient() {
	var width = document.documentElement.clientWidth;
	var height = document.documentElement.clientHeight;
	//alert(width+","+height);
	var op = parseFloat(width / height);
	var test = window.location.href;
	if(op >= 1) {
		//横屏
		if(test.indexOf("pbindex.html") == -1) {
			window.location.href = "pbindex.html";
		}

	} else {
		if(test.indexOf("pbindex.html") != -1) {
			window.location.href = "index.html";
		}

	}
}
//页面加载时调用
orient();
//用户变化屏幕方向时调用
$(window).bind('orientationchange', function(e) {
	orient();
});
*/

//比赛规则
var rootPath = "http://www.hongyishenmei.com/ay/";
//var rootPath = "http://192.168.0.108:8080/ay";
var limittime = 10; //比赛单题限时
var jdstart = true; //答题开始false是旧题目，new 新题目，第一次默认新的

var nowitem = null; //当前答题
var nowindex = 1; //当前答题序号
/*var starttime = 0;
var stoptime = 0;*/
var myanswer = null;

var userinfo = null;
var uid = null;

var otheruid = "";
var loadinter = null;
var myuser = null; //我的
var otheruser = null; //对方

var passnum = GetQueryString("passnum");
var nowstatus = 0;

var needShowPZ = true; //需要显示碰撞信息

var isSpeakStop=true;//允许执行开始方法

var INTERWORK = {};
INTERWORK.type = "login";

//请求比赛	
INTERWORK.ajaxSatus = function() {

	　
	$.ajax({
		type: "get",
		url: rootPath + "/jdgame/ajaxData.do?callback=initReq(data)",
		dataType: "jsonp",
		data: {
			uid: uid,
			type: INTERWORK.type,
			passnum: passnum
		}
	});

}

function initReq(data) {

	INTERWORK.type = "continue";

	$("#loadfirst").hide();

	if(data != "404") {

		nowstatus = parseInt(data.status);

		if(nowstatus == 0) {

			showStartPage(data);

			setTimeout(function() {
				INTERWORK.ajaxSatus();
			}, 1500);

		} else if(nowstatus == 1) {

			if(needShowPZ) {

				showStartPage(data);
				setTimeout(function() {
					INTERWORK.ajaxSatus();
				}, 1500);
			} else {
				//答题中
				showAnswerpage(data);
				
				
				if(nowitem.user_res&&nowitem.user_res[uid].onestate == 1) {

					//已答题获得结果
					setTimeout(function() {
						INTERWORK.ajaxSatus();
					}, 1000);

				}
			}

		} else if(nowstatus == 2) {
			//答题结束
			setTimeout(function() {
				showJDRes(data);
			}, 2000);

		} else if(nowstatus == 3) {
			//答题结束
			setTimeout(function() {
				showJDRes(data);
			}, 2000);

		}

	} else {

		if(nowstatus < 2) {
			$("#main").hide();
			$("#resarea").hide();

			$("#loadfirst").show();
			setTimeout(function() {
				INTERWORK.ajaxSatus();
			}, 2000);
		}

	}

}

function video_suc() {
	//document.getElementById("rightem").play(); 

	if(typeof(OlymlpicAndroid) != "undefined") {
		OlymlpicAndroid.video_suc();
	}
}

function video_error() {
	
	//document.getElementById("errorem").play();  
	if(typeof(OlymlpicAndroid) != "undefined") {
		OlymlpicAndroid.video_error();
	}
}

function GetQueryString(name) {

	var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r != null) return decodeURI(r[2]); //对参数进行decodeURI解码
	return null;
}
//识别
function checkUser(data) {
	var userlist = data.userlist;
	var one = userlist[0];
	var one2 = userlist[1];

	if(one.userid == uid) {
		myuser = one;
		otheruser = one2;
	} else {
		myuser = one2;
		otheruser = one;
	}
	if(otheruid == null || otheruid == "") {
		otheruid = otheruser.userid;
	}

}

//pk对抗页
function showStartPage(data) {
	//初始化
	$("#main").hide();
	$("#resarea").hide();
	$("#pzpage").show();

	//识别用户
	checkUser(data);

	$("#myuser").find(".name").text(myuser.username);
	$("#otheruser").find(".name").text(otheruser.username);

	otheruid = otheruser.userid;
	needShowPZ = false;
}

//答题页
function showAnswerpage(data) {
	
	//识别用户
	checkUser(data);
	
	

	$("#pzpage").hide();

	$("#main").show();

	showNewItemPage(data)

}

function showNewItemPage(data) {
	//切换新题目
	var yindex = nowindex;

	changeNewItem(data);

	if(nowindex != yindex) {
		
		
		//显示分数和用户
		showMainUserInfo(data, data.itemlist[yindex - 1]);
		
		
		//更新
		setTimeout(function() {
			changeNewItemPage();
		}, 2000);
		
		

	} else {
		
		//显示分数和用户
		showMainUserInfo(data);

		changeNewItemPage();

	}

}
//新题目
function changeNewItemPage() {
	//新题目
	if(jdstart) {
		
		if(myanswer!=null){
			 myanswer = null;
		}
		 
		  
		  
		jdstart = false;

		$(".chooseone").removeClass("otherSucchoose");
		$(".chooseone").removeClass("othererrorchoose");
		$(".chooseone").removeClass("successchoose");
		$(".chooseone").removeClass("errorchoose");
		$(".chooseone").removeClass("sucRes");
		
		$("#iteminfo").find("img").remove();
		
		$("#question").html("");
		$("#chooseA").find("span").text("");
		$("#chooseB").find("span").text("");
		$("#chooseC").find("span").text("");
		$("#chooseD").find("span").text("");

		//第几题
		$("#showIndex").find("b").text(nowindex);
		$("#showIndex").show();

		test_speekword("第" + nowindex + "题," + nowitem.question);

		setTimeout(function() {
			
			$("#question").html(nowitem.question);
			
		}, 1000);
		
		
		loadinter = new initLoadM(function() {

				loadinter.textobj.textContent = "0";

				//已经提交自己的成绩
				myanswer = "";
				
				sendThisItem();

			   }, limittime,"#J_progress_bar","#loadnum");
			   

	}

}




function startJDshow(res) {
	

	if(res.status<2) {
	
		
		changeNewItem(res);
		//开始竞答

		//显示题目
		$("#showIndex").hide();

		$("#chooseA").find("span").text(nowitem.chooseA);
		$("#chooseB").find("span").text(nowitem.chooseB);
		$("#chooseC").find("span").text(nowitem.chooseC);
		$("#chooseD").find("span").text(nowitem.chooseD);
		
		//图片
		if(nowitem.img&&nowitem.img.length>3){
			$("#iteminfo").append("<img src='"+rootPath+"/"+nowitem.img+"'>");
		}
		

		$("#iteminfo").show();

		//页面加载完成，显示倒计时
			var stime=0;
			
			var nitemur=nowitem.user_res;
			
			if(nitemur[uid].uid==uid){
				stime=nitemur[uid].stime;
			}else{
				stime=nitemur[otheruid].stime;
			}
			
			var cha=(new Date().getTime()-stime)/1000-1;
			
			if(cha<=10){
				
				
			   loadinter.setNow(cha);
			   
			   loadinter.init();
			  
			}else{
				if(loadinter!=null){
					loadinter.stop();
				}
				

				//已经提交自己的成绩
				myanswer = "";
				sendThisItem();
				
			}

		

		INTERWORK.ajaxSatus();

	} else {
		//结束
		layer.open({
			content: '对方已离开,比赛结束',
			skin: 'msg',
			time: 2 //2秒后自动关闭
		});
		setTimeout(function() {
			showJDRes(res);
		}, 1000);

	}

}
//用户分数
function showMainUserInfo(data, oneitem) {

	//识别用户
	checkUser(data);

	$("#leftdiv").find(".uname").html(myuser.username);
	$("#leftdiv").find(".uscore").text(myuser.userscore);
	$("#rightdiv").find(".uname").text(otheruser.username);
	$("#rightdiv").find(".uscore").text(otheruser.userscore);

	//答题结果 展示
	return showItemresult(oneitem);
}
//答题结果显示
function showItemresult(oneitem) {

	var showitem = nowitem;
	if(oneitem != null) {
		showitem = oneitem;
	}
	if(showitem.user_res != null) {

		var myres = showitem.user_res[uid];
		var otherres = showitem.user_res[otheruid];
		if(myres != null && myres.choose != null && myres.choose != "") {
			showSucRes($("#choose" + showitem.answer));

			//我的答题结果
			if(myres.sc > 0) {
				myobjToSuc($("#choose" + myres.choose));
			} else {
				myobjToError($("#choose" + myres.choose))
			}

			if(otherres != null && otherres.choose != null && otherres.choose != "") {
				if(otherres.sc > 0) {
					otherobjToSuc($("#choose" + otherres.choose));
				} else {
					otherobjToError($("#choose" + otherres.choose))
				}

			}
		}

		if(myres != null && otherres != null && myres.etime != null && otherres.etime != null) {
			
			if(loadinter != null) {

				loadinter.stop();

			}
			
			
			return true;
		}

	}

	return false;
}

//切换新题目
function changeNewItem(data) {

	
	nowitem=data.itemlist[nowindex-1];
	
	if(nowitem.hadtask != null&&nowitem.hadtask==1) {
		
		nowindex++;
		nowitem=data.itemlist[nowindex-1];
		
		jdstart = true;
		//重新答题
		myanswer = null;
	}
	
	
	
}

//答题：如果只有本人答题 结果，保持当前这步骤，等待倒计时结束
function chooseThisAnswer(answer, obj) {
	
		if(typeof(OlymlpicAndroid) != "undefined") {
			OlymlpicAndroid.SpeekStop();
		}
	
	if(myanswer == null) {
		
		myanswer = answer;
		//console.log(myanswer)
		sendThisItem();
		
		
		if(nowitem.answer == myanswer) {

			//答案一致
			myobjToSuc($(obj));
			video_suc();
		} else {
			//答案不一致
			myobjToError($(obj));
			video_error();

		}
		
		
		showSucRes($("#choose" + nowitem.answer));
	
	}

	
}
//成功答案显示
function showSucRes(obj) {
	if(!obj.hasClass("sucRes")) {
		obj.addClass("sucRes");
	}
}
//我成功
function myobjToSuc(obj) {
	if(!obj.hasClass("successchoose")) {
		obj.addClass("successchoose");
	}
}
//我失败
function myobjToError(obj) {
	if(!obj.hasClass("errorchoose")) {
		obj.addClass("errorchoose");
	}
}
//对方成功
function otherobjToSuc(obj) {
	if(!obj.hasClass("otherSucchoose")) {
		obj.addClass("otherSucchoose");
	}
}
//对方失败
function otherobjToError(obj) {
	if(!obj.hasClass("othererrorchoose")) {
		obj.addClass("othererrorchoose");
	}
}

//答题结果上传并查看
function sendThisItem() {

	
	$.ajax({
		type: "get",
		url: rootPath + "/jdgame/sendThisItem.do?callback=sendThisItemShow(data)",
		dataType: "jsonp",
		data: {
			uid: uid,
			choose: myanswer,
			index: nowindex
		}
	});

}

//上传答题
function sendThisItemShow(data) {

	nowitem = data.itemlist[nowindex - 1];

	//显示分数和用户
	showMainUserInfo(data);

	if(parseInt(data.status) == 2) {

		setTimeout(function() {

			showJDRes(data);

		}, 1500);

	} else {

		INTERWORK.ajaxSatus();
	}

}

//竞答结果
function showJDRes(data) {

	if(loadinter != null) {

		loadinter.stop();

	}
	//识别用户
	checkUser(data);

	$(".jgleft").find(".uname").text(myuser.username);
	$(".jgright").find(".uname").text(otheruser.username);

	$("#headinfo_leftdiv").find(".jgleft").find(".myusore").text(myuser.userscore);
	$("#headinfo_rightdiv").find(".jgright").find(".myusore").text(otheruser.userscore);

	$("#headinfo_leftdiv").find(".ucore").find("em").text(myuser.user_suc_num);
	$("#headinfo_rightdiv").find(".ucore").find("em").text(otheruser.user_suc_num);

	if(myuser.userscore > otheruser.userscore) {
		//成功
		$("#headinfo").removeClass("fail");
		$("#headinfo").removeClass("pj");
	} else if(myuser.userscore < otheruser.userscore) {
		$("#headinfo").removeClass("pj");
		if(!$("#headinfo").hasClass("fail")) {
			$("#headinfo").toggleClass("fail")
		}
	} else if(myuser.userscore == otheruser.userscore) {
		//平局
		$("#headinfo").removeClass("fail");
		if(!$("#headinfo").hasClass("pj")) {
			$("#headinfo").toggleClass("pj")
		}
	}

	//显示结果 
	$("#jfnum").text(myuser.jf);
	$("#expnum").text(myuser.exp);
	//同步最新的用户消息
	if(typeof(OlymlpicAndroid) != "undefined") {
		OlymlpicAndroid.SyncStudent();
	}

	$("#main").hide();
	$("#resarea").show();

}

/*****************************/
function loading() {

	//初始化用户
	if(typeof(OlymlpicAndroid) != "undefined") {
		userinfo = JSON.parse(OlymlpicAndroid.GetStudent())
		uid = userinfo.sid;

		if(userinfo.studentname != null) {
			//学生名称
			$("#loadfirst").find(".textdiv").find(".username").text(userinfo.studentname);
		}
		if(userinfo.schoolname != null) {
			//学校
			$("#loadfirst").find(".textdiv").find(".fromwhere").text("来自：" + userinfo.schoolname);
		}
		if(userinfo.rank != null) {
			//等级
			$("#loadfirst").find(".textdiv").find(".level").text("等级 " + userinfo.rank);
		}
		if(userinfo.img != null) {
			//用户头像
			$("#loadfirst").find("#logo").attr("src", userinfo.img);
		}
	} else {
		//模拟测试,正式时删除
		uid = GetQueryString("uid");
	}

	//比赛请求
	INTERWORK.ajaxSatus();

}
//开始比赛,列表页面
function goStart() {
	window.location.href = "pass.html";

}
//退出比赛
function returnFirst() {

	OlymlpicAndroid.IntoChoose();
}


//确认开始
function ajaxSatrtTime() {
	if(isSpeakStop){
		$.ajax({
		type: "get",
		url: rootPath + "/jdgame/startJD.do?callback=startJDshow(res)",
		dataType: "jsonp",
		data: {
			uid: uid,
			index: nowindex
		}
	   });
	
	//执行一次,不再执行
	   isSpeakStop=false;
	}
	

}

//播放结束	
function SpeekEnd() {

	//同步开始时间，当两人一起开始之后开始
	ajaxSatrtTime();
	
}
//语音合成
function test_speekword(str) {
	
	
	if(typeof(OlymlpicAndroid) != "undefined") {
		OlymlpicAndroid.SpeekWord(str);
	}
	
	//等待播放完成,执行开始方法
	isSpeakStop=true;
	
	
	setTimeout(function() {
		ajaxSatrtTime();
	},9000);
	
}



/**************初始化*****************************************************/


$(function() {

	loading();
	LOGIN.ajaxLogin();

			
});

/*
			$("#loadfirst").hide();
			$("#main").show();
			loadinter = new initLoadM(function() {

				loadinter.textobj = "0";

			}, 10,"#J_progress_bar","#loadnum");
			 loadinter.setNow(0);
			loadinter.init();
			
*/