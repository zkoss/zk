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
	    if (!zk.isAccord(cmp) && zk.isScroll(cmp)) {
	        zkTabs2.scrollingchk(zk.firstChild(cmp, "DIV").id, "end");
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
	    if (!zk.isAccord(tabbox) && zk.isScroll(tabbox)) {
			if (getZKAttr(cmp, "sel"))
	        	zkTab2.selTab(cmp, true);
	    }	    	    
	    //close Button 
	    var btn = $e(cmp.id + "!close");
	    if (btn) {
	        zk.listen(btn, "click", zkTab2.onCloseBtnClick);
	        if (!btn.style.cursor) 
	            btn.style.cursor = "default";
	    }
	},
	setAttr : function(cmp, name, value){    
	    switch (name) {
        case "z.disabled":
            zkTab2._disable(cmp, value);
            return true;
        case "style":
        case "style.width":
        case "style.height":
            zkau.setAttr(cmp, name, value);
            zk.beforeSizeAt(cmp);
            zkTabs2.scrollingchk($uuid($parent(cmp)));
            zkTabs2._fixWidth($uuid($parent(cmp)));
            zk.onSizeAt(cmp);
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
	    if (!zk.isAccord(tabbox)) {
			//if tab is overflow , scroll back
	        zkTabs2.scrollingchk(zk.firstChild(tabbox, "DIV").id,"sel",tab);
	    }
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
		if (getZKAttr(node, "sel") == "true") {
			return node;
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
	        tab.className = tab.className + "sel";
	    }
	    else {
	        var len = tab.className.length;
	        if (len > 3) 
	            tab.className = tab.className.replace("sel","");
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
	        if (tabs) 
	            zkTabs2._fixWidth(tabs.id);
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
	            cmp.className = clzn.substring(0, len - 3) + "dis" + "sel";
	        }
	        else {
	            cmp.className = clzn + "dis";
	        }
	    }
	    else {
	        //change style from tabdis/tabdissel to tab/tabsel
	        if (sel == "true") {
	            cmp.className = clzn.substring(0, len - 6) + "sel";
	        }
	        else {
	            cmp.className = clzn.substring(0, len - 3)
	        }
	    }
	    setZKAttr(cmp, "disabled", disabled);
	},
	
	cleanup : function(cmp){    
	    var parent = $outer($parent(cmp));
	    var tabbox = $e(getZKAttr(cmp, "box"));
	    if (!zk.isAccord(tabbox)) { //if delete tab , need scroll back !        
	    	_refn = function(){zkTabs2.scrollingchk(parent.id, "end", cmp);};			
	        zk.addCleanupLater(_refn, false, parent.id + "Tabbox2");		
	    }
	}
};

////
// tabs //
zkTabs2 = {

	init : function(cmp){    
	    zkTabs2._fixWidth(cmp.id);    
	    var btn = $e(cmp.id + "!right");
	    if (btn) {
	        zk.listen(btn, "click", zkTabs2.onClickArrow);
	    }
	    btn = $e(cmp.id + "!left");
	    if (btn) {
	        zk.listen(btn, "click", zkTabs2.onClickArrow);
	    }
	},

/** Check Tab Auto scrolling 
 * @param {string} : uuid .
 * @param {string} : use to direct scroll to end.
 * @param {cmp}	   : use to scroll to specific tab
 */
scrollingchk : function(uuid, way, cmp){
	var tbsdiv = $e(uuid);		
    if (!tbsdiv) return;	// tabbox is delete , no need to check scroll   
    var tabs = $e(uuid + "!cave"), header = $e(uuid + "!header"), a = tabs.childNodes, tabbox = $parentByType(tabs, "Tabbox2");
    if (tbsdiv.offsetWidth < 36) return; //tooooo small
    if (!getZKAttr(tabbox, "tabscroll")) {
        //tab-scrolling is disabled
    }
    else {
        //			
        var headwidth = header.offsetWidth, cldwidth = 0;
        for (var i = 0, count = a.length; i < count; i++) {
            if ($type(a[i]) == "Tab2") {
                cldwidth = cldwidth + $int(a[i].offsetWidth) + 2;
            }
        };
        if (zk.hasClass(tabbox, "z-Scrolling")) { //already in scrolling status		
            if (cldwidth <= (headwidth)) {
                zk.rmClass(tabbox, "z-Scrolling", true);
                header.style.width = tabbox.offsetWidth - 2 + "px";
                header.scrollLeft = 0;
            }
            // scroll to specific position            
            switch (way) {
            case "end":
                var d = cldwidth - header.offsetWidth - header.scrollLeft + 2;
                d >= 0 ? zkTabs2.tabscroll(uuid, "right", d) : zkTabs2.tabscroll(uuid, "left", Math.abs(d));
                break;
            case "sel":
                var osl = cmp.offsetLeft, 
					tosw = cmp.offsetWidth, 
					scl = header.scrollLeft, 
					hosw = headwidth;
                //over left
                if (osl < scl) {
                    zkTabs2.tabscroll(uuid, "left", scl - osl + 2);
                }else {
                    if (osl + tosw > scl + hosw) {
                        zkTabs2.tabscroll(uuid, "right", osl + tosw - scl - hosw + 2);
                    }
                }
            case "sta":
                break;
            }
            /*if (way =="start") maybe one day have a "Scroll To Start" button ?*/
        
        }
        else { // not enough tab to scroll
            if (cldwidth > (headwidth - 10)) {
                zk.addClass(tabbox, "z-Scrolling", true);
                header.style.width = tabbox.offsetWidth - 38 + "px";
                if (way == "end") {
                    var d = cldwidth - header.offsetWidth - header.scrollLeft + 2;
                    d < 0 ? "" : zkTabs2.tabscroll(uuid, "right", d);
                }
            }
        }
    };
},
/** Scroll to next tab  . */
onClickArrow : function(evt){
    if (!evt) 
        evt = window.event;
    var ele = Event.element(evt), uuid = $outer(ele).id, move = 0, head = $e(uuid + "!header"), scl = head.scrollLeft;
    //Scroll to next right tab 					
    if (ele.id == uuid + "!right") {
        hosw = head.offsetWidth;
        
        var a = $e(uuid + "!cave").childNodes;
        for (var i = 0, count = a.length; i < count; i++) {
            if ($type(a[i]) == "Tab2") {
                if (a[i].offsetLeft + a[i].offsetWidth > scl + hosw) {
                    move = a[i].offsetLeft + a[i].offsetWidth - scl - hosw + 2;
                    if (move == 0 || move == null || isNaN(move)) 
                        return null;
                    zkTabs2.tabscroll(uuid, "right", move);
                    return;
                };
                            }
        };
            //Scroll to next left tab
    }
    else 
        if (ele.id == uuid + "!left") {
            var a = $e(uuid + "!cave").childNodes;
            for (var i = 0, count = a.length; i < count; i++) {
                if ($type(a[i]) == "Tab2") {
                    if (a[i].offsetLeft > scl) {
                        //if no Sibling tab no sroll						
                        if (zk.previousSibling(a[i], "LI") == null) 
                            return;
                        move = scl - zk.previousSibling(a[i], "LI").offsetLeft + 2;
                        if (isNaN(move)) 
                            return;
                        zkTabs2.tabscroll(uuid, "left", move);
                        return;
                        
                    };
                                    }
            };
                    }
},

tabscroll : function(uuid, to, move){
    if (move <= 0) 
        return;    
    var step, header = $e(uuid + "!header");
    //the tab bigger , the scroll speed faster 		
    step = move <= 60 ? 4 : eval(4 * ($int(move / 60) + 1));
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
        }
        else 
            if (to == "left") {
                header.scrollLeft = header.scrollLeft - step;
            }
        header.scrollLeft = (header.scrollLeft <= 0 ? 0 : header.scrollLeft);
    }
},
	
/** Fix the width of header. */
_fixWidth : function(uuid){
    var tbx = $parent($e(uuid));
	var title =  $e(uuid);
    var head = $e(uuid + "!header");		
	
	zkTabs2._fixHgh(tbx, $e(uuid));
	if (tbx.offsetWidth < 36) return;
	if (zk.isScroll(tbx)) {
		if (!tbx.style.width) {
			tbx.style.width = tbx.offsetWidth + "px";
			title.style.width = tbx.offsetWidth - 2 + "px"
			if (zk.hasClass(tbx, "z-Scrolling")) {
				head.style.width = $int(tbx.style.width.replace("px", "")) - 38 + "px";
			}
			else {
				head.style.width = $int(tbx.style.width.replace("px", "")) - 2 + "px";
			}
		}
		else {
			title.style.width = tbx.offsetWidth - 2 + "px"
			head.style.width = title.style.width;
			if (zk.hasClass(tbx, "z-Scrolling")) {
				head.style.width = head.offsetWidth - 36 + "px";
			}
			else {
				head.style.width = head.offsetWidth + "px";
			}
		}
	}
},
_fixHgh : function (tabbox, tabs) {
	//fix tabpanels's height if tabbox's height is specified
	//Ignore accordion since its height is controlled by each tabpanel

	var hgh = tabbox.style.height;
	if (hgh && hgh != "auto" && !zk.isAccord(tabbox)){
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
					if (zk.ie) n.style.position = pos;
				}
		}
	}
},
onVisi : function(cmp){	
	cmp.style.width="";
	$parent(cmp).style.width="";
	zkTabs2._fixWidth(cmp.id);    
    zkTabs2.scrollingchk(cmp.id);    
}, 
onSize : function(cmp){	
	zkTabs2._fixWidth(cmp.id);    
    zkTabs2.scrollingchk(cmp.id);    
},
beforeSize : function(cmp){	
	cmp.style.width="";
	$parent(cmp).style.width="";
}

};
if (zk.ie6Only) 
    zkTabs2.beforeSize = function(tabs){
        var tabbox = $parentByType(tabs, "Tabbox2");
        if (!zk.isAccord(tabbox)) {
            var panels = zk.nextSibling(tabs, "DIV");
            if (panels) 
                for (var n = panels.firstChild; n; n = n.nextSibling) 
                    if (n.id) 
                        n.style.height = "";
        }
    };
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