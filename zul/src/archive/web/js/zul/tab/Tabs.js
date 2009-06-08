/* Tabs.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:43 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zul.tab.Tabs = zk.$extends(zul.Widget, {
	$init: function() {
		this.$supers('$init', arguments);
	},
	getTabbox: function() {
		return this.parent ? this.parent : null;
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabs" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : ""):
		this._zclass;
	},
	onSize: _zkf = function () {
		var tabbox = this.getTabbox();
			this._fixWidth();
		if (tabbox.getNode())
			zDom.cleanVisibility(tabbox.getNode());
	},
	onShow: _zkf,
	insertChildHTML_: function (child, before, desktop) {
		var last = child.previousSibling;
		if (before || !last) {
			zDom.insertHTMLBefore(before.getNode(), child._redrawHTML());
		} else {
			zDom.insertHTMLAfter(last.getNode(), child._redrawHTML());
		}
		child.bind(desktop);
	},
	domClass_: function (no) {
		var zcls = this.$supers('domClass_', arguments),
			tbx = this.getTabbox();
		if (!no || !no.zclass) {
			var added = tbx.isTabScroll() ? zcls + "-scroll" : "";
			if (added) zcls += (zcls ? ' ': '') + added;
		}
		return zcls;
	},
	bind_: function (desktop, skipper, after) {
		this.$supers('bind_', arguments);
		zWatch.listen({onSize: this, onShow: this});

		for (var btn, key = ['right', 'left', 'down', 'up'], le = key.length; --le >= 0;)
			if ((btn = this.getSubnode(key[le])))
				this.domListen_(btn, "onClick");

		this._init = false
		zk.afterMount(
			this.proxy(function () {
				this._scrollcheck("init");
				this._fixHgh();
				this._init = true;
			})
		);
		this._fixWidth();
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this});

		this.$supers('unbind_', arguments);
	},
	_scrollcheck: function(way, tb) {
		var tbsdiv = this.getNode(),
			tabbox = this.getTabbox(),
			tbx = tabbox.getNode();
		if (!tabbox.isRealVisible() || !tabbox.isTabScroll()) return;
		if (!tbsdiv) return;	// tabbox is delete , no need to check scroll
		if (tabbox.isVertical()) {//vertical
			var header =  this.getSubnode("header"),
				cave =  this.getSubnode("cave"),
				headerOffsetHeight = header.offsetHeight,
				headerScrollTop = header.scrollTop,
				sel = tabbox.getSelectedTab(),
				cmp = tb ? tb : ( sel ? sel.getNode() : null),
				cmpOffsetTop = cmp ? cmp.offsetTop : 0,
				cmpOffsetHeight = cmp ? cmp.offsetHeight : 0,
				childHeight = 0,
				allTab = zDom.childNodes(cave),
				btnsize = this._getArrowSize();
			for (var i = 0, count = allTab.length; i < count; i++)
				childHeight = childHeight + allTab[i].offsetHeight;

			if (tabbox._scrolling) { //already in scrolling status
				if (tbsdiv.offsetHeight < btnsize)  return;
				if (childHeight <= headerOffsetHeight + btnsize) {
					tabbox._scrolling = false;
					this._showbutton(false)
					header.style.height= Math.max(tbx.offsetHeight-2, 0) + "px";
					header.scrollTop = 0;
				}
				switch (way) {
					case "end":
						var d = childHeight - headerOffsetHeight - headerScrollTop ;
						this._doScroll(d >= 0 ? "down" : "up", d >= 0 ? d : Math.abs(d));
						break;
					case "init":
						if (cmpOffsetTop < headerScrollTop) {
							this._doScroll("up", headerScrollTop - cmpOffsetTop);
						} else if (cmpOffsetTop + cmpOffsetHeight > headerScrollTop + headerOffsetHeight) {
							this._doScroll("down", cmpOffsetTop + cmpOffsetHeight - headerScrollTop - headerOffsetHeight);
						}
						break;
					case "vsel":
						if (cmpOffsetTop < headerScrollTop) {
							this._doScroll("up", headerScrollTop - cmpOffsetTop);
						} else if (cmpOffsetTop + cmpOffsetHeight > headerScrollTop + headerOffsetHeight) {
							this._doScroll("down", cmpOffsetTop + cmpOffsetHeight - headerScrollTop - headerOffsetHeight);
						}
						break;
				}
			} else { // not enough tab to scroll
				if (childHeight - (headerOffsetHeight - 10) > 0) {
					tabbox._scrolling = true;
					this._showbutton(true);
					var temp = tbx.offsetHeight - this._getArrowSize();
					header.style.height = temp > 0 ? temp + "px" : "";
					if (way == "end") {
						var d = childHeight - headerOffsetHeight - headerScrollTop + 2;
						if (d >= 0)
							this._doScroll(this.uuid, "down", d);
					}
				}
			}
		} else if(!tabbox.inAccordionMold()) {
			var cave = this.getSubnode("cave"),
				header = this.getSubnode("header"),
			 	allTab = zDom.childNodes(cave),
			 	sel = tabbox.getSelectedTab(),
				cmp = tb ? tb : ( sel ? sel.getNode() : null),
			 	cmpOffsetLeft = cmp ? cmp.offsetLeft : null,
				cmpOffsetWidth = cmp ? cmp.offsetWidth : null,
				headerOffsetWidth = header.offsetWidth,
				headerScrollLeft = header.scrollLeft,
				childWidth = 0,
				btnsize = this._getArrowSize();
			for (var i = 0, count = allTab.length; i < count; i++)
				childWidth = childWidth + zk.parseInt(allTab[i].offsetWidth);//

			if (tabbox._scrolling) { //already in scrolling status
				if (tbsdiv.offsetWidth < btnsize)  return;
				if (childWidth <= headerOffsetWidth + btnsize) {
					tabbox._scrolling = false;
					this._showbutton(false);
					header.style.width = Math.max(tbx.offsetWidth, 0) + "px";
					header.scrollLeft = 0;
				}
				// scroll to specific position
				switch (way) {
					case "end":
						var d = childWidth - headerOffsetWidth - headerScrollLeft;
						this._doScroll( d>=0 ? "right" : "left", d>=0 ? d : Math.abs(d));
						break;
					case "init":
						if (cmpOffsetLeft < headerScrollLeft) {
							this._doScroll("left", headerScrollLeft - cmpOffsetLeft);
						} else if (cmpOffsetLeft + cmpOffsetWidth > headerScrollLeft + headerOffsetWidth) {
							this._doScroll("right", cmpOffsetLeft + cmpOffsetWidth - headerScrollLeft - headerOffsetWidth);
						}
						break;
					case "sel":
						if (cmpOffsetLeft < headerScrollLeft) {
							this._doScroll("left", headerScrollLeft - cmpOffsetLeft);
						} else if (cmpOffsetLeft + cmpOffsetWidth > headerScrollLeft + headerOffsetWidth) {
							this._doScroll("right", cmpOffsetLeft + cmpOffsetWidth - headerScrollLeft - headerOffsetWidth);
						}
						break;
				}

			} else { // not enough tab to scroll
				if (childWidth - (headerOffsetWidth - 10) > 0) {
					tabbox._scrolling = true;
					this._showbutton(true);
					var cave = this.getSubnode("cave"),
						temp = tabbox.offsetWidth - this._getArrowSize();//coz show button then getsize again
					cave.style.width = "5555px";
					header.style.width = temp > 0 ? temp + "px" : "";
					if (way == "sel") {
						if (cmpOffsetLeft < headerScrollLeft) {
							this._doScroll("left", headerScrollLeft - cmpOffsetLeft);
						} else if (cmpOffsetLeft + cmpOffsetWidth > headerScrollLeft + headerOffsetWidth) {
							this._doScroll("right", cmpOffsetLeft + cmpOffsetWidth - headerScrollLeft - headerOffsetWidth);
						}
					}
				}
			}
		};
	},
	_doScroll: function(to, move) {
		if (move <= 0)
			return;
		var step,
			header = this.getSubnode("header");
		//the tab bigger , the scroll speed faster
		step = move <= 60 ? 5 : eval(5 * (zk.parseInt(move / 60) + 1));
		var run = setInterval(function() {
			if (!move) {
				clearInterval(run);
				return;
			} else {
				//high speed scroll, need break
				move < step ? goscroll(header, to, move) : goscroll(header, to, step);
				move = move - step;
				move = move < 0 ? 0 : move;
			}
		}, 10);
		//Use to scroll
		goscroll = function(header, to, step) {
			switch(to){
			case 'right':
				header.scrollLeft = header.scrollLeft + step;
				break;
			case 'left':
				header.scrollLeft = header.scrollLeft - step;
				break;
			case 'up':
				header.scrollTop = header.scrollTop - step;
				break;
			default:
				header.scrollTop = header.scrollTop + step;
			}
			header.scrollLeft = (header.scrollLeft <= 0 ? 0 : header.scrollLeft);
			header.scrollTop = (header.scrollTop <= 0 ? 0 : header.scrollTop);
		}
	},
	_getArrowSize: function() {
		var tabbox = this.getTabbox(),
			isVer = tabbox.isVertical(),
			btnA = isVer ? this.getSubnode("up") : this.getSubnode("left"),
			btnB = isVer ? this.getSubnode("down") : this.getSubnode("right");
		return btnA && btnB ? (isVer ? btnA.offsetHeight + btnB.offsetHeight :
			btnA.offsetWidth + btnB.offsetWidth ) : 0;
	},
	_showbutton : function(show) {
		var tabbox = this.getTabbox(),
			zcls = this.getZclass();
		if (tabbox.isTabScroll()) {
			if (tabbox.isVertical()) {//vertical
				zDom[show == true? "addClass" : "rmClass"](this.getSubnode("header"), zcls + "-header-scroll");
				zDom[show == true? "addClass" : "rmClass"](this.getSubnode("down"), zcls + "-down-scroll");
				zDom[show == true? "addClass" : "rmClass"](this.getSubnode("up"), zcls + "-up-scroll");
			}else {//horizontal
				zDom[show == true? "addClass" : "rmClass"](this.getSubnode("header"), zcls + "-header-scroll");
				zDom[show == true? "addClass" : "rmClass"](this.getSubnode("right"), zcls + "-right-scroll");
				zDom[show == true? "addClass" : "rmClass"](this.getSubnode("left"), zcls + "-left-scroll");
			}
		}
	},
	_doClick: function(evt) {
		var ele = evt.domTarget;
			move = 0,
			tabbox = this.getTabbox(),
			cave = this.getSubnode("cave"),
			head = this.getSubnode("header"),
			allTab =  zDom.childNodes(cave),
			scrollLength = tabbox.isVertical() ? head.scrollTop : head.scrollLeft,
			offsetLength = tabbox.isVertical() ? head.offsetHeight : head.offsetWidth;
	//Scroll to next right tab
		if (ele.id == this.uuid + "$right") {
			if (!allTab.length) return; // nothing to do
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetLeft + allTab[i].offsetWidth > scrollLength + offsetLength) {
					move = allTab[i].offsetLeft + allTab[i].offsetWidth - scrollLength - offsetLength;
					if (!move || isNaN(move))
						return null;
					this._doScroll("right", move);
					return;
				};
			};
		} else if (ele.id == this.uuid + "$left") {//Scroll to next left tab
				if (!allTab.length) return; // nothing to do
				for (var i = 0, count = allTab.length; i < count; i++) {
					if (allTab[i].offsetLeft >= scrollLength) {
						//if no Sibling tab no sroll
						tabli = zDom.previousSibling(allTab[i], "LI");
						if (tabli == null)  return;
						move = scrollLength - tabli.offsetLeft;
						if (isNaN(move)) return;
						this._doScroll("left", move);
						return;
					};
				};
				move = scrollLength - allTab[allTab.length-1].offsetLeft;
				if (isNaN(move)) return;
				this._doScroll("left", move);
				return;
		} else if (ele.id == this.uuid + "$up") {
				if (!allTab.length) return; // nothing to do
				for (var i = 0, count = allTab.length; i < count; i++) {
					if (allTab[i].offsetTop >= scrollLength) {
						var preli = zDom.previousSibling(allTab[i], "LI");
						if (preli == null) return;
						move = scrollLength - preli.offsetTop ;
						this._doScroll("up", move);
						return;
					};
				};
				var preli = allTab[allTab.length-1];
				if (preli == null) return;
				move = scrollLength - preli.offsetTop ;
				this._doScroll("up", move);
				return;
		} else if (ele.id == this.uuid + "$down") {
			if (!allTab.length) return; //nothing to do
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetTop + allTab[i].offsetHeight > scrollLength + offsetLength ) {
					move = allTab[i].offsetTop + allTab[i].offsetHeight - scrollLength - offsetLength;
					if (!move || isNaN(move)) return ;
					this._doScroll("down", move);
					return;
				};
			};
		}
	},
	_fixWidth: function() {
		var tabs = this.getNode(),
			tabbox = this.getTabbox(),
			tbx = tabbox.getNode(),
			cave = this.getSubnode("cave"),
			head = this.getSubnode("header"),
			l = this.getSubnode("left"),
			r = this.getSubnode("right"),
			btnsize = tabbox._scrolling ? l && r ? l.offsetWidth + r.offsetWidth : 0 : 0;
			this._fixHgh();
			if (this.parent.isVertical()) {
				var most = 0;
				 //LI in IE doesn't have width...
				 if (tabs.style.width) {
				 	tabs._width = tabs.style.width;;
				 } else {
					 //vertical tabs have default width 50px
					this._forceStyle(tabs,"w",tabs._width != null ? tabs._width : "50px");
				 }
			} else if(!tabbox.inAccordionMold()) {
				if (tbx.offsetWidth < btnsize) return;
				if (tabbox.isTabScroll()) {
					if (!tbx.style.width) {
						this._forceStyle(tbx,"w","100%");
						this._forceStyle(tabs,"w",zDom.revisedWidth(tabs,tbx.offsetWidth)+ "px");
						this._forceStyle(head,"w",(tabbox._scrolling ? tbx.offsetWidth - btnsize : zDom.revisedWidth(head,tbx.offsetWidth)) + "px");
					} else {
						this._forceStyle(tabs,"w",zDom.revisedWidth(tabs,tbx.offsetWidth)+ "px");
						this._forceStyle(head,"w",tabs.style.width);
						this._forceStyle(head,"w",(tabbox._scrolling ? head.offsetWidth - btnsize : head.offsetWidth) + "px");
					}
				} else {
					if (!tbx.style.width) {
						this._forceStyle(tbx,"w",tbx.offsetWidth + "px");
						this._forceStyle(tabs,"w",tbx.offsetWidth + "px");
					} else {
						this._forceStyle(tabs,"w",tbx.offsetWidth + "px");
					}
				}
			}
	},
	_fixHgh: function () {
		var tabs = this.getNode(),
			tabbox = this.getTabbox(),
			tbx = tabbox.getNode(),
			head = this.getSubnode("header"),
			u = this.getSubnode("up"),
			d = this.getSubnode("down"),
			btnsize = u && d ? isNaN(u.offsetHeight + d.offsetHeight) ? 0 : u.offsetHeight + d.offsetHeight : 0;
		//fix tabpanels's height if tabbox's height is specified
		//Ignore accordion since its height is controlled by each tabpanel
		if (tabbox.isVertical()) {
			var child = zDom.childNodes(tbx, function (n) {return (zDom.tag(n) == "DIV");}),
			allTab = zDom.childNodes(this.getSubnode("cave"));
			if (tbx.style.height) {
				this._forceStyle(tabs, "h", zDom.revisedHeight(tabs,tbx.offsetHeight,true)+"px");
			} else {
				this._forceStyle(tbx,"h", allTab.length*35+"px");//give it default height
				this._forceStyle(tabs, "h", zDom.revisedHeight(tabs,tbx.offsetHeight,true)+"px");
			}
			//coz we have to make the header full
			if (tabbox._scrolling) {
				this._forceStyle(head,"h", tabs.offsetHeight - btnsize + "px");
			} else {
				this._forceStyle(head,"h", zDom.revisedHeight(head,tabs.offsetHeight,true) + "px");
			}
			//separator(+border)
			this._forceStyle(child[1],"h",zDom.revisedHeight(child[1],tabs.offsetHeight,true)+"px");
			//tabpanels(+border)
			this._forceStyle(child[2],"h",zDom.revisedHeight(child[1],tabs.offsetHeight,true)+"px");
		} else {
			if (head) //accordion have no head
				head.style.height="";
		}
	},

	_forceStyle: function(cmp,attr,value) {
		if ( zk.parseInt(value) < 0 || value == null) return;
		switch(attr) {
		case "h":
			cmp.style.height = zk.ie6Only ? "0px" : ""; // recalculate for IE6
			cmp.style.height = value;
			break;
		case "w":
			cmp.style.width = zk.ie6Only ? "0px" : ""; // recalculate for IE6
			cmp.style.width = "";
			cmp.style.width = value;
			break;
		}
	}

});