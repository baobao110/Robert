/**
 * 初始化
 * @param fun
 * @param obj
 * @param time
 * @returns {initLoadM}
 */
function initLoadM(fun,limittime,barid,numid){
	//最长时间，
	this.limittime=limittime;
	

	this.loadobj=document.querySelector(barid);
	this.textobj=document.querySelector(numid);
	this.circleLength = Math.floor(2 * Math.PI * this.loadobj.getAttribute("r"));
	this.fun=fun;
	
	this.showHours=function(synum,allnum){
	
	    this.textobj.textContent =allnum-synum;
		 this.loadobj.setAttribute("stroke-dasharray","" + this.circleLength *  synum/ allnum + ",10000");
	};
	

}
initLoadM.prototype.setNow=function(now){
	this.nowtime=now;//开始时间
}
initLoadM.prototype.init=function(){
	var loaddom=this;
	if(loaddom.timeobj!=null){
		clearInterval(loaddom.timeobj);
	}
	//还原
	loaddom.nowtime=0;
	if(loaddom.limittime>=0){
		
		loaddom.timeobj=setInterval(function(){

			loaddom.nowtime+=1;

			loaddom.showHours(loaddom.nowtime,loaddom.limittime);
		  
		  if(loaddom.nowtime==loaddom.limittime){
		  	
		  	clearInterval(loaddom.timeobj);
		  
		  
		  	
		  	loaddom.fun();
		  	
		  }

			
		},1000);
	}
};
/*停止*/
initLoadM.prototype.stop=function(){
	var loaddom=this;
	

	clearInterval(loaddom.timeobj)
}
