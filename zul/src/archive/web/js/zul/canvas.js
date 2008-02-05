/**
*	Apply ExploreCanvas Library to enable IE Browsers'
*	Canvas functionality
*/
if (zk.ie){
	zk.load("ext.excanvas");
}

zk.addModuleInit(function () {

	zkDrawCanvas = {};
	
	zkDrawCanvas.init = function (cmp) {
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
		if(!zkDrawCanvas.ctx){	
		
			zkDrawCanvas.ctx = 
				document.getElementById(cmp.id).getContext('2d');
		} 	
	};	//init
	
	zkDrawCanvas.cleanup = function (cmp) {
	
	};	//cleanup
	
	/*
	*	Fill Style
	*/
	zkDrawCanvas.fillStyle = function (cmp, value){
		var r,g,b,a;
		for (var i = 0, c = 0;c < 4; c = c + 1) {
			var k = value.indexOf(',', i);
			var val = (k >= 0 ? value.substring(i, k): value.substring(i)).trim();
			
			switch(c){
				case 0:
				{
					r=parseInt(val);
					break;
				}
				case 1:
				{
					g=parseInt(val);
					break;
				}
				case 2:
				{
					b=parseInt(val);
					break;
				}
				case 3:
				{
					a=parseFloat(val);
					break;
				}
			}
			
			i = k + 1;
		}
		 		
		zkDrawCanvas.ctx.fillStyle="rgba("+r+","+g+","+
											b+","+a+")";
	};	//fillStyle
	
	/*
	*	Stroke Style
	*/
	zkDrawCanvas.strokeStyle = function (cmp, value){
		var r,g,b,a;
		for (var i = 0, c = 0;c < 4; c = c + 1) {
			var k = value.indexOf(',', i);
			var val = (k >= 0 ? value.substring(i, k): value.substring(i)).trim();
			
			switch(c){
				case 0:
				{
					r=parseInt(val);
					break;
				}
				case 1:
				{
					g=parseInt(val);
					break;
				}
				case 2:
				{
					b=parseInt(val);
					break;
				}
				case 3:
				{
					a=parseFloat(val);
					break;
				}
			}
			
			i = k + 1;
		}
		
		zkDrawCanvas.ctx.strokeStyle="rgba("+r+","+g+","+
											b+","+a+")";				
	};	//strokeStyle
	
	/*
	*	Fill Rectangle
	*/
	zkDrawCanvas.fillRect = function (cmp, value){
		var x1,y1,width,height;
		for (var i = 0, c = 0;c < 4; c = c + 1) {
					var k = value.indexOf(',', i);
					var val = (k >= 0 ? value.substring(i, k): value.substring(i)).trim();
					
					switch(c){
						case 0:
						{
							x1=parseInt(val);
							break;
						}
						case 1:
						{
							y1=parseInt(val);
							break;
						}
						case 2:
						{
							width =  parseInt(val);
							break;
						}
						case 3:
						{
							height = parseInt(val);
							break;
						}
					}
					
					i = k + 1;
				}
				
				zkDrawCanvas.ctx.fillRect(x1,y1,width,height);
	};	//fillRect
	
	/*
	*	Stroke Rectangle
	*/
	zkDrawCanvas.strokeRect = function (cmp, value){
		var x1,y1,width,height;
		for (var i = 0, c = 0;c < 4; c = c + 1) {
			var k = value.indexOf(',', i);
			var val = (k >= 0 ? value.substring(i, k): value.substring(i)).trim();
				
			switch(c){
				case 0:
				{
					x1=parseInt(val);
					break;
				}
				case 1:
				{
					y1=parseInt(val);
					break;
				}
				case 2:
				{
					width=parseInt(val);
					break;
				}
				case 3:
				{
					height=parseInt(val);
					break;
				}
			}
					
			i = k + 1;
		}
				
		zkDrawCanvas.ctx.strokeRect(x1,y1,width,height);
	};	//strokeRect
	
	/*
	*	Draw Line
	*/
	zkDrawCanvas.drawLine = function (cmp, value){
		var x1,y1,x2,y2;
		for (var i = 0, c = 0;c < 4; c = c + 1) {
			var k = value.indexOf(',', i);
			var val = (k >= 0 ? value.substring(i, k): value.substring(i)).trim();
					
			switch(c){
				case 0:
				{
					x1=parseInt(val);
					break;
				}
				case 1:
				{
					y1=parseInt(val);
					break;
				}
				case 2:
				{
					x2=parseInt(val);
					break;
				}
				case 3:
				{
					y2=parseInt(val);
					break;
				}
			}
			
			i = k + 1;
		}
				
		zkDrawCanvas.ctx.beginPath();
		zkDrawCanvas.ctx.moveTo(x1,y1);
		zkDrawCanvas.ctx.lineTo(x2,y2);
		zkDrawCanvas.ctx.stroke();
		
	};	//drawLine
	
	/*
	*	Fill Arc
	*/
	zkDrawCanvas.fillArc = function (cmp, value){
		var x,y,radius,startAngle,endAngle, anticlockwise;
		for (var i = 0, c = 0;c < 6; c = c + 1) {
			var k = value.indexOf(',', i);
			var val = (k >= 0 ? value.substring(i, k): value.substring(i)).trim();
			
			switch(c){
				case 0:
				{
					x=parseInt(val);
					break;
				}
				case 1:
				{
					y=parseInt(val);
					break;
				}
				case 2:
				{
					radius=parseFloat(val);
					break;
				}
				case 3:
				{
					startAngle=parseFloat(val);
					break;
				}
				case 4:
				{
					endAngle=parseFloat(val);
					break;
				}
				case 5:
				{
					if(val == 'true'){
						anticlockwise = true;
					}else{
						anticlockwise = false;
					}
					break;
				}
			}
			
			i = k + 1;
		}
	
		zkDrawCanvas.ctx.beginPath();
		zkDrawCanvas.ctx.arc(x,y,radius,startAngle,endAngle, anticlockwise);
		zkDrawCanvas.ctx.fill();
		
	};	//fillArc
	
	/*
	*	Stroke Arc
	*/
	zkDrawCanvas.strokeArc = function (cmp, value){
		var x,y,radius,startAngle,endAngle, anticlockwise;
		for (var i = 0, c = 0;c < 6; c = c + 1) {
			var k = value.indexOf(',', i);
			var val = (k >= 0 ? value.substring(i, k): value.substring(i)).trim();
			
			switch(c){
				case 0:
				{
					x=parseInt(val);
					break;
				}
				case 1:
				{
					y=parseInt(val);
					break;
				}
				case 2:
				{
					radius=parseFloat(val);
					break;
				}
				case 3:
				{
					startAngle=parseFloat(val);
					break;
				}
				case 4:
				{
					endAngle=parseFloat(val);
					break;
				}
				case 5:
				{
					if(val == 'true'){
						anticlockwise = true;
					}else{
						anticlockwise = false;
					}
					break;
				}
			}
			
			i = k + 1;
		}
						
		zkDrawCanvas.ctx.beginPath();
		zkDrawCanvas.ctx.arc(x,y,radius,startAngle,endAngle, anticlockwise);
		zkDrawCanvas.ctx.stroke();
		
	};	//strokeArc
	
	/*
	*	Draw Image
	*/
	zkDrawCanvas.drawImage = function (cmp, value){
		var imgPath,x,y,width,height,sizeAsgn=false;
		for (var i = 0, c = 0;c < 5; c = c + 1) {
			var k = value.indexOf(',', i);
			var val = (k >= 0 ? value.substring(i, k): value.substring(i)).trim();
						
			switch(c){
				case 0:
				{
					imgPath=val;
					break;
				}
				case 1:
				{
					x=parseInt(val);
					break;
				}
				case 2:
				{
					y =  parseInt(val);
					break;
				}
				case 3:
				{
					sizeAsgn = true;		//size is assigned			
					width =  parseInt(val);					
					break;
				}
				case 4:
				{
					height =  parseInt(val);
					break;
				}
			}
			
			if(k <= 0){
				break;				//image size not assigned
			}else{
				i = k + 1;
			}			
		}
		
		var img = new Image();
		img.src = imgPath;

		/*
		*	When finished loading a pic,
		*	"onLoad" event will be triggered.
		*	However, Opera excepts, so draw it directly. 
		*/
		img.onload  = function() {
			if(sizeAsgn){
				zkDrawCanvas.ctx.drawImage(img,x,y,width,height);
			}else{
				zkDrawCanvas.ctx.drawImage(img,x,y);
			}
		};
		/**
		*	Draw a pic with readyState "complete" in IE,
		*	"onLoad" event won't be triggered, so draw it 
		*	directly.
		*/
		if(zk.ie && (img.readyState == "complete")){	
			if(sizeAsgn){
				zkDrawCanvas.ctx.drawImage(img,x,y,width,height);
			}else{
				zkDrawCanvas.ctx.drawImage(img,x,y);
			}			
		}else if(zk.opera){	//draw directly
			if(sizeAsgn){
				zkDrawCanvas.ctx.drawImage(img,x,y,width,height);
			}else{			
				zkDrawCanvas.ctx.drawImage(img,x,y);
			}
		}
		
	};	//drawIMage
	
	zkDrawCanvas.setAttr = function (elm, name, value) {

    	return false;

	};//	setAttr

	
});	// addModuleInit
