/* tab2.js

{{IS_NOTE
 	Purpose:
 
 	Description:
 
 	History: 		
 		Tue Aug 09 10:21:12     2008, Created by Ryanwu
}}IS_NOTE
 
Copyright (C) 2008 Potix Corporation. All Rights Reserved.
 
{{IS_RIGHT
 	This program is distributed under GPL Version 2.0 in the hope that
 	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
////
// tabbox //
zkTabbox2 = {
	setAttr : function(cmp, name, value){
	    switch (name) {
        case "style":
        case "style.width":
        case "style.height":
            zkau.setAttr(cmp, name, value);
            var uuid = getZKAttr(cmp, "tabs");
            if (uuid) {
                zk.beforeSizeAt(cmp);
                zkTabs2._fixWidth(uuid);					
                zk.onSizeAt(cmp);
            }
            return true;
	    }
	    return false;
	},
	childchg : function(cmp){		
	    var uuid = getZKAttr(cmp, "tabs");    
	    if (uuid) {
	        setTimeout("zkTabs2._fixWidth('" + uuid + "')", 0);    
	    }		
	}
};
////
// tab //
zkTab2 = {
	init : function(cmp){    		
	    //onClick 	
	    zk.listen(cmp, "click", zkTab2.onclick);
	    var tabbox = $e(getZKAttr(cmp, "box"));	    
	    //close Button 
	    var btn = $e(cmp.id + "!close");
	    if (btn) {
	        zk.listen(btn, "click", zkTab2.onCloseBtnClick);
	        if (!btn.style.cursor) 
	            btn.style.cursor = "default";
	    }
		if (!zkTab2._toscroll) 
			zkTab2._toscroll = function () {				
				zkTabs2.scrollingchk($uuid($parent(cmp)));				
			};
		zk.addInit(zkTab2._toscroll, false, $uuid($parent(cmp)) );		
	},
	setAttr : function(cmp, name, value){		
	    switch (name) {
        case "z.disabled":
            zkTab2._disable(cmp, value);
            return true;
        case "style":
        case "style.width":
        case "style.height":
			if (cmp) {
				zkau.setAttr(cmp, name, value);
				zk.beforeSizeAt(cmp);
				zkTabs2.scrollingchk($uuid($parent(cmp)));
				zkTabs2._fixWidth($uuid($parent(cmp)));
				zk.onSizeAt(cmp);
			}
            return true;
	    }
	    return false;
	},
	/** Selects the specified tab. */
	onclick : function(evt){
	    if (!evt) evt = window.event;
	    var tab = $parentByType(Event.element(evt), "Tab2");		
	    if (getZKAttr(tab, "disabled") == "true") 
	        return;//return when disabled
	    if (!zkTab2._sliding(tab)) //Bug 1571408
	        zkTab2.selTab(tab, true);
	    
		var tabbox = $e(getZKAttr(tab, "box"));    
		
	},
	
	/** Returns the selected tab by giving any tab as the reference point. */
	_getSelTab : function(tab){
	    var tabboxId = getZKAttr(tab, "box");
	    if (tabboxId) {
	        var tabbox = $e(tabboxId);
	        if (zk.isAccord(tabbox)) 
	            return zkTab2._getSelTabFromTop(tabbox, tabboxId);
	    }
	    
	    //non-accordion: we can use sibling directly
		var node = tab;
		
	    for (node = tab; node = node.nextSibling;){ 
	        if (getZKAttr(node, "sel") == "true") 
	            return node;
		}
	    for (node = tab; node = node.previousSibling;) {
			if (getZKAttr(node, "sel") == "true") 
				return node;
		}
		if (getZKAttr(tab, "sel") == "true") {
			return tab;
		}		
	},
	/** Whether any tab is sliding. */
	_sliding : function(tab){
	    var tabboxId = getZKAttr(tab, "box");
	    if (!tabboxId) 
	        return false;
	    
	    var tabbox = $e(tabboxId);
	    if (!zk.isAccord(tabbox)) 
	        return false;
	    
	    //accordion: we must go to panel firs, and then browse its sibling
	    var panel = $e(getZKAttr(tab, "panel"));
	    for (var node = panel; node = node.nextSibling;) 
	        if (getZKAttr($real(node), "animating")) 
	            return true;
	    for (var node = panel; node = node.previousSibling;) 
	        if (getZKAttr($real(node), "animating")) 
	            return true;
	    return false;
	},
	/** Returns the selected tab by specified any HTML tag containing it. */
	_getSelTabFromTop : function(node, tabboxId){
	    if ($type(node) == "Tab2" && getZKAttr(node, "box") == tabboxId) 
	        return getZKAttr(node, "sel") == "true" ? node : null;
	    
	    for (var node = node.firstChild; node; node = node.nextSibling) {
	        var n = zkTab2._getSelTabFromTop(node, tabboxId);
	        if (n) 
	            return n;
	    }
	    return null;
	},
	
	/** Selects the specified tab (and unselect the current tab). */
	selTab : function(tab, notify){		
		var tabbox = $e(getZKAttr(tab, "box"));
		if (zk.isVertical(tabbox)){			
			zkTabs2.scrollingchk($uuid($parent(tab)),"vsel",tab);
		}else if (!zk.isAccord(tabbox)) {			
	        zkTabs2.scrollingchk($uuid($parent(tab)),"sel",tab);
	    }
	    tab = $e(tab);	
	    if (!tab) 
	        return;
	    
	    var old = zkTab2._getSelTab(tab);	
	    if (old != tab) {
	        if (old) 
	            zkTab2._setTabSel(old, false, false, notify);
			zkTab2._setTabSel(tab, true, notify, notify);			                
			//!notify is sent from the server, so no animation
	    }	
	},
	
	/** Selects or unselect the specified tab. */
	_setTabSel : function(tab, toSel, notify, animation){
	    if ((getZKAttr(tab, "sel") == "true") == toSel)
	        return; //nothing changed
	    setZKAttr(tab, "sel", toSel ? "true" : "false");	
	    if (toSel) {
	        tab.className = tab.className + "-seld";
	    }
	    else {
	        var len = tab.className.length;
	        if (len > 5) 
	            tab.className = tab.className.replace("-seld","");
	    } 
	    var tabbox = getZKAttr(tab, "box");
	    if (tabbox) 
	        tabbox = $e(tabbox);
	    var accd = tabbox && zk.isAccord(tabbox);
	    var panel = $e(getZKAttr(tab, "panel"));
	    if (panel) 
	        if (accd && animation) {
	            if (toSel) 
	                anima.slideDown($real(panel));
	            else 
	                anima.slideUp($real(panel));
	        }
	        else 
	            zk.show(accd ? $real(panel) : panel, toSel);
	    
	    if (!accd) {
	        var tabs = $parentByType(tab, "Tabs2");
	           if (tabs) zkTabs2._fixWidth(tabs.id);
	    }
	    
	    if (notify) 
	        zkau.sendasap({
	            uuid: tab.id,
	            cmd: "onSelect",
	            data: [tab.id]
	        });
	},
	
	/** close button clicked**/
	onCloseBtnClick : function(evt){
	    if (!evt) 
	        evt = window.event;
	    var tab = $parentByType(Event.element(evt), "Tab2");
	    if (getZKAttr(tab, "disabled") == "true") 
	        return;//return when disabled
	    zkau.sendOnClose(tab, true);
	    Event.stop(evt);
	},
	
	/** inner method, disable this tab 
	 * @param {Object} cmp tab element
	 * @param {string} disabled string "true" or "false"
	 */
	_disable : function(cmp, disabled){
	    var olddis = getZKAttr(cmp, "disabled");
	    if (olddis == disabled) 
	        return;
	    
	    var btn = $e(cmp.id + "!close");
	    var sel = getZKAttr(cmp, "sel");
	    
	    var clzn = cmp.className;
	    var len = clzn.length;
	    if (disabled == "true") {
	        //change style from tab/tabsel to tabdis/tabdissel
	        if (sel == "true") {
	            cmp.className = clzn.substring(0, len - 5) + "-disd" + "-seld";
	        }
	        else {
	            cmp.className = clzn + "-disd";
	        }
	    }
	    else {
	        //change style from tabdis/tabdissel to tab/tabsel
	        if (sel == "true") {
	            cmp.className = clzn.substring(0, len - 10) + "-seld";
	        }
	        else {
	            cmp.className = clzn.substring(0, len - 5)
	        }
	    }
	    setZKAttr(cmp, "disabled", disabled);
	},
	
	cleanup : function(cmp){    		
		zkTab2._toscroll = null;//clean init 
	    var parent = $outer($parent(cmp));
	    var tbx = $e(getZKAttr(cmp, "box"));
	    if (!zk.isAccord(tbx)) { //if delete tab , need scroll back !	    	         
	    	_refn = function(){
				zkTabs2.scrollingchk(parent.id, "cln", cmp);
				if (zk.isVertical(tbx)){
					zkTabs2._fixHgh(tbx, parent);						
				}						
			};					
	        zk.addCleanup(_refn, false, parent.id + "Tabbox2");		
	    }
	},
	_width : null //for record tabs' width
};

////
// tabs //
zkTabs2 = {	
	init : function(cmp){
	    zkTabs2._fixWidth(cmp.id);    
	    var btn = $e(cmp.id + "!right");
		//horizontal
	    if (btn) {
	        zk.listen(btn, "click", zkTabs2.onClickArrow);
	    }
	    btn = $e(cmp.id + "!left");
	    if (btn) {
	        zk.listen(btn, "click", zkTabs2.onClickArrow);
	    }
		//vertical
		btn = $e(cmp.id + "!down");
	    if (btn) {
	        zk.listen(btn, "click", zkTabs2.onClickArrow);
	    }
		btn = $e(cmp.id + "!up");
	    if (btn) {
	        zk.listen(btn, "click", zkTabs2.onClickArrow);
	    }		
		if (!zkTabs2.initscroll) 
			zkTabs2.initscroll = function () {							
				zkTabs2.scrollingchk(cmp.id,"init");				
			};
		zk.addInit(zkTabs2.initscroll, false, $parent(cmp).id);	
	},	
	/** Check Tab Auto scrolling 
	 * @param {string} : uuid .
	 * @param {string} : use to direct scroll to end.
	 * @param {cmp}	   : use to scroll to specific tab
	 */
	scrollingchk : function(uuid, way, cmp){				
		var tbsdiv = $e(uuid),tabbox = $parentByType($e(uuid), "Tabbox2");	
	    if (!tbsdiv) return;	// tabbox is delete , no need to check scroll
	    if (tbsdiv.offsetWidth < 36)  return; //tooooo small
	    if (zk.isVertical(tabbox)) {//vertical
	    	if (tbsdiv.offsetHeight < 36)  return; //tooooo small
			var header = $e(uuid , "header"),
				ul_si = $e(uuid , "ul"),  						
				headheight = header.offsetHeight, 
				cldheight = 0,
				tab = zk.childNodes(ul_si, function (n) {return ($type(n) == "Tab2");});			 
				for (var i=0,count=tab.length;i<count;i++){
					cldheight = cldheight + tab[i].offsetHeight;	
				}
				if (zk.hasClass(tabbox, "z-tabbox-scrolling")) { //already in scrolling status
					if (cldheight <= (headheight+18)) {
						zk.rmClass(tabbox, "z-tabbox-scrolling", true);
						header.style.height= tabbox.offsetHeight-2 + "px";
						header.scrollTop = 0;
					}
					switch (way) {
						case "init":
							if (cmp == null) cmp = zkTab2._getSelTab(zk.firstChild(ul_si,"LI"));							
							var ost = cmp.offsetTop, 
								tosh = cmp.offsetHeight, 
								sct = header.scrollTop, 
								hosh = headheight;
							if (ost < sct) {
								zkTabs2._tabscroll(uuid, "up", sct - ost );
							}
							else if (ost + tosh > sct + hosh) {
									zkTabs2._tabscroll(uuid, "down", ost + tosh - sct - hosh + 4);
							}
							break;
						case "cln":
						case "end":
							var d = cldheight - header.offsetHeight - header.scrollTop ;
							d >= 0 ? zkTabs2._tabscroll(uuid, "down", d) : zkTabs2._tabscroll(uuid, "up", Math.abs(d));						
							break;
						case "vsel":
							var ost = cmp.offsetTop, 
								tosh = cmp.offsetHeight, 
								scltop = header.scrollTop, 
								host = headheight;
							//over left
							if (ost < scltop) {
								zkTabs2._tabscroll(uuid, "up", scltop - ost);
							} else if (ost + tosh > scltop + host) {
								zkTabs2._tabscroll(uuid, "down", ost + tosh - scltop - host + 2);							
							}						
							break;
					}	
				}else { // not enough tab to scroll
					if (cldheight > (headheight - 18)) {
						zk.addClass(tabbox, "z-tabbox-scrolling", true);
						header.style.height = tabbox.offsetHeight - 36 + "px";
						if (way == "end") {
							var d = cldheight - header.offsetHeight - header.scrollTop + 2;						
							d < 0 ? "" : zkTabs2._tabscroll(uuid, "down", d);
						}
					}
				}			
		}else if(!zk.isAccord(tabbox)){
			var tabs = $e(uuid + "!cave"), header = $e(uuid + "!header"),
			 a =  zk.childNodes(tabs, function (n) {return ($type(n) == "Tab2");});
			if (!getZKAttr(tabbox, "tabscrl")) {				
				//tab-scrolling is disabled				
			} else {				
				var headwidth = header.offsetWidth, cldwidth = 0;
				for (var i = 0, count = a.length; i < count; i++) {				
					cldwidth = cldwidth + $int(a[i].offsetWidth) + 2;	
				};
				if (zk.hasClass(tabbox, "z-tabbox-scrolling")) { //already in scrolling status		
					if (cldwidth <= (headwidth)) {
						zk.rmClass(tabbox, "z-tabbox-scrolling", true);
						header.style.width = tabbox.offsetWidth - 2 + "px";
						header.scrollLeft = 0;
					}
					// scroll to specific position            
					switch (way) {
						case "init":
							if (cmp == null)cmp = zkTab2._getSelTab(zk.firstChild(tabs,"LI"));							
							var osl = cmp.offsetLeft, tosw = cmp.offsetWidth, scl = header.scrollLeft, hosw = headwidth;
							if (osl < scl) {
								zkTabs2._tabscroll(uuid, "left", scl - osl + 2);
							}
							else if (osl + tosw > scl + hosw) {
									zkTabs2._tabscroll(uuid, "right", osl + tosw - scl - hosw + 2);
							}
							break;							
						case "cln":														
							var d = cldwidth - header.offsetWidth - header.scrollLeft;							
							d >= 0 ? zkTabs2._tabscroll(uuid, "right", d) : zkTabs2._tabscroll(uuid, "left", Math.abs(d));
							break;
						case "end":
							var d = cldwidth - header.offsetWidth - header.scrollLeft + 2;
							d >= 0 ? zkTabs2._tabscroll(uuid, "right", d) : zkTabs2._tabscroll(uuid, "left", Math.abs(d));
							break;
						case "sel":
							var osl = cmp.offsetLeft, tosw = cmp.offsetWidth, scl = header.scrollLeft, hosw = headwidth;
							//over left
							if (osl < scl) {
								zkTabs2._tabscroll(uuid, "left", scl - osl + 2);
							}
							else if (osl + tosw > scl + hosw) {
									zkTabs2._tabscroll(uuid, "right", osl + tosw - scl - hosw + 2);
							}
							break;						
					}			
				
				}
				else { // not enough tab to scroll
					if (cldwidth > (headwidth - 10)) {						
						zk.addClass(tabbox, "z-tabbox-scrolling", true);
						header.style.width = tabbox.offsetWidth - 38 + "px";						
						if (way == "sel") {
							var d = cldwidth - header.offsetWidth - header.scrollLeft + 2;							
							d < 0 ? "" : zkTabs2._tabscroll(uuid, "right", d);
						}
					}
				}
			}
		};
},
/** Scroll to next tab  . */
onClickArrow : function(evt){
    if (!evt) evt = window.event;
    var ele = Event.element(evt), 
	uuid = $outer(ele).id, 
	move = 0, 
	head = $e(uuid + "!header"); 
	
    //Scroll to next right tab 		
    if (ele.id == uuid + "!right") {
		var hosw = head.offsetWidth,
			scl = head.scrollLeft,		
			a = $e(uuid + "!cave").childNodes;
		for (var i = 0, count = a.length; i < count; i++) {
			if ($type(a[i]) == "Tab2") {
				if (a[i].offsetLeft + a[i].offsetWidth > scl + hosw) {
					move = a[i].offsetLeft + a[i].offsetWidth - scl - hosw + 2;
					if (move == 0 || move == null || isNaN(move)) 
						return null;
					zkTabs2._tabscroll(uuid, "right", move);
					return;
				};
			}
		};
	}else if (ele.id == uuid + "!left") {//Scroll to next left tab			
			var a =  zk.childNodes($e(uuid + "!cave"), function (n) {return ($type(n) == "Tab2");}),
				scl = head.scrollLeft;
			for (var i = 0, count = a.length; i < count; i++) {				
				if (a[i].offsetLeft > scl) {
					//if no Sibling tab no sroll						
					if (zk.previousSibling(a[i], "LI") == null)  return;
					move = scl - zk.previousSibling(a[i], "LI").offsetLeft + 2;
					if (isNaN(move)) return;
					zkTabs2._tabscroll(uuid, "left", move);
					return;
				};				
			};
	}else if (ele.id == uuid + "!up") {		
			var scltop =  head.scrollTop,
				tab = zk.childNodes($e(uuid,"ul"), function (n) {return ($tag(n) == "LI");});
			for (var i = 0, count = tab.length; i < count; i++) {				
				if (tab[i].offsetTop >= scltop) {
					var preli = zk.previousSibling(tab[i], "LI");											
					if (preli==null) return;						
					move = scltop - preli.offsetTop ;										
					zkTabs2._tabscroll(uuid, "up", move);
					return;
				};				
			};				
	}else if (ele.id == uuid + "!down") {	
		var scltop = head.scrollTop,
		    tab = zk.childNodes($e(uuid,"ul"), function (n) {return ($tag(n) == "LI");}),
			scltop =  head.scrollTop,
			hosh = head.offsetHeight
		for (var i = 0, count = tab.length; i < count; i++) {					    
			if (tab[i].offsetTop + tab[i].offsetHeight > scltop + hosh ) {
				move = tab[i].offsetTop + tab[i].offsetHeight - scltop - hosh + 2;
				if (move == 0 || move == null || isNaN(move)) return ;					
				zkTabs2._tabscroll(uuid, "down", move);
				return;
			};			
		};			
	}
},

_tabscroll : function(uuid, to, move){	
    if (move <= 0) 
        return;    
    var step, header = $e(uuid + "!header");
    //the tab bigger , the scroll speed faster 		
    step = move <= 60 ? 5 : eval(5 * ($int(move / 60) + 1));
    var run = setInterval(function(){
        if (move == 0) {
            clearInterval(run);
            return;
        }
        else {
            //high speed scroll, need break
            move < step ? goscroll(header, to, move) : goscroll(header, to, step);
            move = move - step;
            move = move < 0 ? 0 : move;
        }
    }, 10);
    //Use to scroll
    goscroll = function(header, to, step){
        if (to == "right") {
            header.scrollLeft = header.scrollLeft + step;			
        }else if (to == "left") {
            header.scrollLeft = header.scrollLeft - step;			
		}else if (to == "up"){
			header.scrollTop = header.scrollTop - step;
		}else if (to == "down"){
			header.scrollTop = header.scrollTop + step;			
		}
		header.scrollLeft = (header.scrollLeft <= 0 ? 0 : header.scrollLeft);
		header.scrollTop = (header.scrollTop <= 0 ? 0 : header.scrollTop);
        
    }
},
	
/** Fix the width of header. */
_fixWidth : function(uuid){
    var tbx = $parent($e(uuid));
	var tabs =  $e(uuid);
    var head = $e(uuid + "!header");
	zkTabs2._fixHgh(tbx, tabs);
	if (zk.isVertical(tbx)) {
		ul = zk.firstChild($e(tabs, "header"), "UL"),
		li = zk.childNodes(ul, function (n) {return ($tag(n) == "LI");});
		var most = 0;
		 //li in IE doesn't have width...
		 if (tabs.style.width){
		 	zkTabs2._width = tabs.style.width;;
		 }else{
		 	tabs.style.width = zkTabs2._width;	
		 }		 
		//
	}else {		
		if (tbx.offsetWidth < 36) 
			return;
		if (zk.isScroll(tbx)) {
			if (!tbx.style.width) {
				zk.forceStyle(tbx,"w",tbx.offsetWidth + "px");
				zk.forceStyle(tabs,"w",tbx.offsetWidth-2 + "px");				
				if (zk.hasClass(tbx, "z-tabbox-scrolling")) {
					zk.forceStyle(head,"w",$int(tbx.style.width.replace("px", "")) - 38 + "px");					
				} else {					
					zk.forceStyle(head,"w",$int(tbx.style.width.replace("px", "")) - 2 + "px");
				}
			}
			else {				
				zk.forceStyle(tabs,"w",tbx.offsetWidth-2 + "px");	
				zk.forceStyle(head,"w",tabs.style.width);												
				if (zk.hasClass(tbx, "z-tabbox-scrolling")) {					
					zk.forceStyle(head,"w",head.offsetWidth - 36 + "px");			
				}
				else {					
					zk.forceStyle(head,"w",head.offsetWidth + "px");
				}
			}
		}else{
			if (!tbx.style.width) {
				zk.forceStyle(tbx,"w",tbx.offsetWidth + "px");
				zk.forceStyle(tabs,"w",tbx.offsetWidth + "px");
			}else{
				zk.forceStyle(tabs,"w",tbx.offsetWidth + "px");	
			}
		}
	}
},
_fixHgh : function (tabbox, tabs) {
	if ($e(tabbox.id) == null || $e(tabs) == null) return;
	//fix tabpanels's height if tabbox's height is specified
	//Ignore accordion since its height is controlled by each tabpanel
	if (zk.isVertical(tabbox)) {						
		   var child = zk.childNodes(tabbox, function (n) {return ($tag(n) == "DIV");}),
		   ul = zk.firstChild($e(tabs, "header"), "UL"),
		   li = zk.childNodes(ul, function (n) {return ($tag(n) == "LI");});
		   if (tabbox.style.height){
		   		//Tabs2(+border)	
																		
				zk.forceStyle(tabs, "h", tabbox.style.height);
		   }else{		   		
		   			   				   		
				zk.forceStyle(tabbox,"h", li.length*35+"px");
				//Tabs2(+border)
				zk.forceStyle(child[0],"h",tabbox.offsetHeight-2+"px");
		   }
		   //coz we have to make the header full
		   if (zk.hasClass(tabbox, "z-tabbox-scrolling")) {
		   		zk.forceStyle($e(tabs, "header"),"h", tabs.offsetHeight - 38 + "px");		   		
		   }else{
		   		zk.forceStyle($e(tabs, "header"),"h", tabs.offsetHeight - 2 + "px");		   	   	
		   }
		   //separator(+border)
		   //tabpanels(+border)		   
		   zk.forceStyle(child[1],"h",tabs.offsetHeight-2+"px");
		   zk.forceStyle(child[2],"h",tabs.offsetHeight-2+"px");
	}else {
		$e(tabs.id,"header").style.height="";
		var hgh = tabbox.style.height;
		if (hgh && hgh != "auto" && !zk.isAccord(tabbox)) {
			var panels = zk.nextSibling(tabs, "DIV");
			
			if (panels) {
				hgh = zk.getVflexHeight(panels);
				
				for (var pos, n = panels.firstChild; n; n = n.nextSibling) 
					if (n.id) {
						if (zk.ie) { // Bug: 1968434, this solution is very dirty but necessary. 
							pos = n.style.position;
							n.style.position = "relative";
						}
						zk.setOffsetHeight(n, hgh);
						if (zk.ie) 
							n.style.position = pos;
					}
			}
		}
	}
},
onVisi : function(cmp){			
	if (zk.isVertical($parent($e(cmp.id)))) {			
		cmp.style.height = "";
	}else{
		cmp.style.width = "";	
	}			
	zkTabs2._fixWidth(cmp.id);   	 
    zkTabs2.scrollingchk(cmp.id);    
}, 
onSize : function(cmp){			
	if (zk.isVertical($parent($e(cmp.id)))) {			
		zkTabs2._fixWidth(cmp.id);
		zkTabs2.scrollingchk(cmp.id);		
	}else {
		zkTabs2._fixWidth(cmp.id);
		zkTabs2.scrollingchk(cmp.id);
	}    
},
beforeSize : function(cmp){
	if (zk.isVertical($parent($e(cmp.id)))) {			
		cmp.style.height = "";
	}else{
		cmp.style.width = "";	
	}		
},
cleanup: function(cmp){
	zkTabs2.initscroll = null;
}

};
if (zk.ie6Only) {
	zkTabs2.beforeSize = function(tabs){		
		var tabbox = $parentByType(tabs, "Tabbox2");
		if (!zk.isAccord(tabbox)) {
			if (!zk.isVertical($parent(tabs))) {
				tabs.style.width = "0px";
			}
			var panels = zk.nextSibling(tabs, "DIV");
			if (panels) 
				for (var n = panels.firstChild; n; n = n.nextSibling) 
					if (n.id) 
						n.style.height = "";
		}
	};
}
/** Returns whether the tabbox is accordion.
 * @since 3.0.3
 */
zk.isAccord = function(tabbox){
    return getZKAttr(tabbox, "accd");
};
/** Returns whether the tabbox is scrollable.
 * @since 3.5.0
 */
zk.isScroll = function(tabbox){
    return getZKAttr(tabbox, "tabscrl");
};
/** Returns whether the tabbox is vertical.
 * @since 3.5.0
 */
zk.isVertical = function(tabbox){		
    return getZKAttr(tabbox, "orient")=="v" ? true : false;
};
zk.forceStyle = function(cmp,attr,value){	
	switch(attr) {		
	case "h":
		if (zk.ie6Only) {
			cmp.style.height = "0px";
		}else{
			cmp.style.height = "";
		}
		cmp.style.height = value;		
		break;
	case "w":
		if (zk.ie6Only) {
			cmp.style.width = "0px";
		}else{
			cmp.style.width = "";	
		}
		cmp.style.width = "";
		cmp.style.width = value;		
		break;
	}				
}
