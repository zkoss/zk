/**
*	Apply ExploreCanvas Library to enable IE Browsers'
*	Canvas functionality
*/
if (zk.ie){
	zk.load("ext.excanvas");
}
 
zk.addModuleInit(function () {

	zkCanvas = {};
	
	zkCanvas.init = function (cmp) {
		/*
		*	Fire an event to trigger Excanvas's replacement of canvas under IE
		*/
		if (zk.ie){
			document.fireEvent('onreadystatechange');
		}
	
		/**
		*	In some cases (Applying ExCanvas),
		*	a canvas tag will be replaced by 3rd party library,
		*	so regain the object from DOM Tree by object's ID.
		*/
		if(!zkCanvas.ctx){	
		
			zkCanvas.ctx = 
				$e(cmp.id).getContext('2d');
		} 	
	};	//init
	
/*	zkCanvas.cleanup = function (cmp) {
	
	};*/	//cleanup
	
	/*
	*	Fill Style
	*/
	zkCanvas.fillStyle = function (cmp, value){		
		var ary = getValArr(value);		 		
		zkCanvas.ctx.fillStyle="rgba("+ary[0]+","+ary[1]+","+
											ary[2]+","+ary[3]+")";
	};	//fillStyle
	
	/*
	*	Stroke Style
	*/
	zkCanvas.strokeStyle = function (cmp, value){
		var ary = getValArr(value);		 		
		zkCanvas.ctx.strokeStyle="rgba("+ary[0]+","+ary[1]+","+
											ary[2]+","+ary[3]+")";			
	};	//strokeStyle
	
	/*
	*	Fill Rectangle
	*/
	zkCanvas.fillRect = function (cmp, value){
		var ary = getValArr(value);		 						
		zkCanvas.ctx.fillRect(ary[0],ary[1],ary[2],ary[3]);
	};	//fillRect
	
	/*
	*	Stroke Rectangle
	*/
	zkCanvas.strokeRect = function (cmp, value){
		var ary = getValArr(value);		 						
		zkCanvas.ctx.strokeRect(ary[0],ary[1],ary[2],ary[3]);
	};	//strokeRect
	
	/*
	*	Draw Line
	*/
	zkCanvas.drawLine = function (cmp, value){
		var ary = getValArr(value);		 					
		zkCanvas.ctx.beginPath();
		zkCanvas.ctx.moveTo(ary[0],ary[1]);
		zkCanvas.ctx.lineTo(ary[2],ary[3]);
		zkCanvas.ctx.stroke();		
	};	//drawLine
	
	/*
	*	Fill Arc
	*/
	zkCanvas.fillArc = function (cmp, value){
		var ary = getValArr(value);
		zkCanvas.ctx.beginPath();	
		zkCanvas.ctx.arc(ary[0],ary[1],ary[2],ary[3],ary[4], ary[5]);
		zkCanvas.ctx.fill();		
	};	//fillArc
	
	/*
	*	Stroke Arc
	*/
	zkCanvas.strokeArc = function (cmp, value){
		var ary = getValArr(value);
		zkCanvas.ctx.beginPath();
		zkCanvas.ctx.arc(ary[0],ary[1],ary[2],ary[3],ary[4], ary[5]);
		zkCanvas.ctx.stroke();		
	};	//strokeArc
	
	/*
	*	Draw Image
	*/
	zkCanvas.drawImage = function (cmp, value){
		var ary = getValArr(value);
		
		var sizeAsgn = (ary.length == 5) ;	
		var img = new Image();
		img.src = ary[0];
		/*
		*	When finished loading a pic, "onLoad" event will be triggered.
		*	However, Opera excepts, so draw it directly. 
		*/
		img.onload  = function() {
			if(sizeAsgn){
				zkCanvas.ctx.drawImage(img,ary[1],ary[2],ary[3],ary[4]);
			}else{
			
				zkCanvas.ctx.drawImage(img,ary[1],ary[2]);
			}
		};
		/**
		*	Draw a pic with readyState "complete" in IE,
		*	"onLoad" event won't be triggered, so draw it 
		*	directly.
		*/
		if(zk.ie && (img.readyState == "complete")){	
			if(sizeAsgn){
				zkCanvas.ctx.drawImage(img,ary[1],ary[2],ary[3],ary[4]);
			}else{
				zkCanvas.ctx.drawImage(img,ary[1],ary[2]);
			}			
		}else if(zk.opera){	//draw directly
			if(sizeAsgn){
				zkCanvas.ctx.drawImage(img,ary[1],ary[2],ary[3],ary[4]);
			}else{
				zkCanvas.ctx.drawImage(img,ary[1],ary[2]);
			}
		}
		
	};	//drawIMage
	
	zkCanvas.clear = function(){
		
	};	//clear
	
	/**
	*	Return an array of args
	*/
	function getValArr(value){
		var ary = new Array();
		var i,c;
				
		for (i = 0, c = 0;c < 6; c = c + 1) {
			var k = value.indexOf(',', i);
			var val = (k >= 0 ? value.substring(i, k): value.substring(i)).trim();
					
			if(isTxtStr(val)){		//text string, not a number
				if( val == 'true'){
					ary.push(true);
				}else if(val == 'false'){
					ary.push(false);
				}else{
					ary.push(val);
				}					
			}else{
				ary.push(parseFloat(val));
			}
			
			if(k < 0){
				break;		//last arg
			}else{
				i = k + 1;
			}
		}
		
		return ary;		
	}
	
	/**
	*	Return true if the string is text(not number). 
	*/
	function isTxtStr(str){
		/*
		*	Simply check the last char, if "not-a-num(NaN)" then return true
		*/		
		var istxt = false;
		var lastChr = str.charAt(str.length-1);
		var t = parseFloat(lastChr);
		if(isNaN(t)){
			istxt = true;
		}
		return istxt;
	}
	
/*	zkCanvas.setAttr = function (elm, name, value) {

    	return false;

	};*///	setAttr
	
});	// addModuleInit
