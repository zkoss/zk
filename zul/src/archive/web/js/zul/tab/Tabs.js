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
/**
 * A collection of tabs ({@link Tab}).
 *
 * <p>Default {@link #getZclass}: z-tabs.
 */
zul.tab.Tabs = zk.$extends(zul.Widget, {
	/** Returns the tabbox owns this component.
	 * @return Tabbox
	 */
	getTabbox: function() {
		return this.parent;
	},
	//@Override
	getWidth: function () {
		var wd = this._width;
		if (!wd) {
			var tabbox = this.getTabbox();
			if (tabbox && tabbox.isVertical())
				return "50px";
		}
		return wd;
	},
	getZclass: function() {
		if (this._zclass != null)
			return this._zclass;
			
		var tabbox = this.getTabbox();
		if (!tabbox) return 'z-tabs';
		return "z-tabs" + (tabbox.getMold() == "default" && tabbox.isVertical() ? '-ver' : '');
	},
	onSize: _zkf = function () {
		this._fixWidth();
		
		// Bug Z35-tabbox-004.zul, we need to check again.
		this._scrollcheck("init");
	},
	onShow: _zkf,
	insertChildHTML_: function (child, before, desktop) {
		var last = child.previousSibling;
		if (before) 
			jq(before).before(child.redrawHTML_());
		else if (last) 
			jq(last).after(child.redrawHTML_());
		else {
			var edge = this.$n('edge');
			if (edge) 
				jq(edge).before(child.redrawHTML_());
			else
				jq(this.getCaveNode()).append(child.redrawHTML_());
		}
		child.bind(desktop);
	},
	domClass_: function (no) {
		var zcls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var tbx = this.getTabbox(),
				added = tbx.isTabscroll() ? zcls + "-scroll" : "";
			if (added) zcls += (zcls ? ' ': '') + added;
		}
		return zcls;
	},
	//bug #3014664
	setVflex: function (v) { //vflex ignored for Tabs
		if (v != 'min') v = false;
		this.$super(zul.tab.Tabs, 'setVflex', v);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for Tabs
		if (v != 'min') v = false;
		this.$super(zul.tab.Tabs, 'setHflex', v);
	},
	bind_: function (desktop, skipper, after) {
		this.$supers(zul.tab.Tabs, 'bind_', arguments);
		zWatch.listen({onSize: this, onShow: this});

		for (var btn, key = ['right', 'left', 'down', 'up'], le = key.length; le--;)
			if ((btn = this.$n(key[le])))
				this.domListen_(btn, "onClick");
		
		// reset
		this._inited = false;
		
		var self = this;
		after.push(
			function () {
				self._inited = true;
			}
		);
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this});
		for (var btn, key = ['right', 'left', 'down', 'up'], le = key.length; le--;)
			if ((btn = this.$n(key[le])))
				this.domUnlisten_(btn, "onClick");
		this.$supers(zul.tab.Tabs, 'unbind_', arguments);
	},
	_isInited: function () {
		return this._inited;
	},
	_scrollcheck: function(way, tb) {
		var tabbox = this.getTabbox();
		if (!this.desktop || !tabbox.isRealVisible() || !tabbox.isTabscroll())
			return;

		var tbsdiv = this.$n(),
			tbx = tabbox.$n();
		if (!tbsdiv || !tbx) return;	// tabbox is delete , no need to check scroll
		if (tabbox.isVertical()) {//vertical
			var header = this.$n("header"),
				headerOffsetHeight = header.offsetHeight,
				headerScrollTop = header.scrollTop,
				childHeight = 0;
				
			jq(this.$n("cave")).children().each(function () {
				childHeight += this.offsetHeight;
			});

			if (tabbox._scrolling) { //already in scrolling status
				var btnsize = this._getArrowSize();
				if (tbsdiv.offsetHeight < btnsize)  return;
				
				var sel = tabbox.getSelectedTab(),
					node = tb ? tb.$n() : (sel ? sel.$n() : null),
					nodeOffsetTop = node ? node.offsetTop : 0,
					nodeOffsetHeight = node ? node.offsetHeight : 0;
					
				if (childHeight <= headerOffsetHeight + btnsize) {
					tabbox._scrolling = false;
					this._showbutton(false)
					header.style.height = jq.px0(tbx.offsetHeight-2);
					header.scrollTop = 0;
				}
				switch (way) {
				case "end":
					var d = childHeight - headerOffsetHeight - headerScrollTop;
					this._doScroll(d >= 0 ? "down" : "up", d >= 0 ? d : Math.abs(d));
					break;
				case "init":
				case "vsel":
					if (nodeOffsetTop < headerScrollTop) {
						this._doScroll("up", headerScrollTop - nodeOffsetTop);
					} else if (nodeOffsetTop + nodeOffsetHeight > headerScrollTop + headerOffsetHeight) {
						this._doScroll("down", nodeOffsetTop + nodeOffsetHeight - headerScrollTop - headerOffsetHeight);
					}
					break;
				}
			} else { // not enough tab to scroll
				if (childHeight - (headerOffsetHeight - 10) > 0) {
					tabbox._scrolling = true;
					this._showbutton(true);
					var btnsize = this._getArrowSize(),
						temp = tbx.offsetHeight - btnsize;
					header.style.height = temp > 0 ? temp + "px" : "";
					if (way == "end") {
						var d = childHeight - headerOffsetHeight - headerScrollTop + 2;
						if (d >= 0)
							this._doScroll(this.uuid, "down", d);
					}
				}
			}
		} else if(!tabbox.inAccordionMold()) {
			var cave = this.$n("cave"),
				header = this.$n("header"),
			 	sel = tabbox.getSelectedTab(),
				node = tb ? tb.$n() : ( sel ? sel.$n() : null),
			 	nodeOffsetLeft = node ? node.offsetLeft : 0,
				nodeOffsetWidth = node ? node.offsetWidth : 0,
				headerOffsetWidth = header.offsetWidth,
				headerScrollLeft = header.scrollLeft,
				childWidth = 0,
				toolbar = tabbox.toolbar;

			jq(cave).children().each(function () {
				childWidth += this.offsetWidth;
			});
			
			if (toolbar)
				toolbar = toolbar.$n();
			if (tabbox._scrolling) { //already in scrolling status
				var btnsize = this._getArrowSize();
				if (toolbar) {
					var outer, hgh;
						
					// fixed FF2's bug
					if (zk.gecko2_) {
						outer = toolbar.parentNode.parentNode;
						outer.style.height = '';
						hgh = outer.offsetHeight;
					}
					this.$n('right').style.right = toolbar.offsetWidth + 'px';
					if (zk.gecko2_)
						outer.style.height = jq.px0(zk(outer).revisedHeight(hgh));
				}
				
				if (tbsdiv.offsetWidth < btnsize) return;
				if (childWidth <= headerOffsetWidth + btnsize) {
					tabbox._scrolling = false;
					this._showbutton(false);
					header.style.width = jq.px0(tbx.offsetWidth - (toolbar ? toolbar.offsetWidth : 0));
					header.scrollLeft = 0;
				}
				// scroll to specific position
				switch (way) {
				case "end":
					var d = childWidth - headerOffsetWidth - headerScrollLeft;
					this._doScroll(d >= 0 ? "right" : "left", d >= 0 ? d : Math.abs(d));
					break;
				case "init":
				case "sel":
					if (nodeOffsetLeft < headerScrollLeft) {
						this._doScroll("left", headerScrollLeft - nodeOffsetLeft);
					} else if (nodeOffsetLeft + nodeOffsetWidth > headerScrollLeft + headerOffsetWidth) {
						this._doScroll("right", nodeOffsetLeft + nodeOffsetWidth - headerScrollLeft - headerOffsetWidth);
					}
					break;
				}
			} else { // not enough tab to scroll
				if (childWidth - (headerOffsetWidth - 10) > 0) {
					tabbox._scrolling = true;
					this._showbutton(true);
					var cave = this.$n("cave"),
						btnsize = this._getArrowSize(),
						temp = tbx.offsetWidth - (toolbar ? toolbar.offsetWidth : 0) - btnsize;//coz show button then getsize again
					cave.style.width = "5555px";
					header.style.width = temp > 0 ? temp + "px" : "";
					
					if (toolbar) 
						this.$n('right').style.right = toolbar.offsetWidth + 'px';
					
					if (way == "sel") {
						if (nodeOffsetLeft < headerScrollLeft) {
							this._doScroll("left", headerScrollLeft - nodeOffsetLeft);
						} else if (nodeOffsetLeft + nodeOffsetWidth > headerScrollLeft + headerOffsetWidth) {
							this._doScroll("right", nodeOffsetLeft + nodeOffsetWidth - headerScrollLeft - headerOffsetWidth);
						}
					}
				}
			}
		}
	},
	_doScroll: function(to, move) {
		if (!this._doingScroll)
			this._doingScroll = {};
		if (move <= 0 || this._doingScroll[to])
			return;
		var step,
			self = this,
			header = this.$n("header");
		
		this._doingScroll[to] = move;
		//the tab bigger , the scroll speed faster
		step = move <= 60 ? 5 : (5 * (zk.parseInt(move / 60) + 1));
		var run = setInterval(function() {
			if (!move) {
				delete self._doingScroll[to];
				clearInterval(run);
				return;
			} else {
				//high speed scroll, need break
				move < step ? goscroll(header, to, move) : goscroll(header, to, step);
				move -= step;
				move = move < 0 ? 0 : move;
			}
		}, 10);
		//Use to scroll
		goscroll = function(header, to, step) {
			switch (to) {
			case 'right':
				header.scrollLeft += step;
				break;
			case 'left':
				header.scrollLeft -= step;
				break;
			case 'up':
				header.scrollTop -= step;
				break;
			default:
				header.scrollTop += step;
			}
			header.scrollLeft = (header.scrollLeft <= 0 ? 0 : header.scrollLeft);
			header.scrollTop = (header.scrollTop <= 0 ? 0 : header.scrollTop);
		}
	},
	_getArrowSize: function() {
		var tabbox = this.getTabbox(),
			isVer = tabbox.isVertical(),
			btnA = isVer ? this.$n("up") : this.$n("left"),
			btnB = isVer ? this.$n("down") : this.$n("right");
		return btnA && btnB ?
			(isVer ? btnA.offsetHeight + btnB.offsetHeight : btnA.offsetWidth + btnB.offsetWidth) : 0;
	},
	_showbutton : function(show) {
		var tabbox = this.getTabbox(),
			zcls = this.getZclass();
		if (tabbox.isTabscroll()) {
			var header = this.$n("header");
				
			if (tabbox.isVertical()) {//vertical
				if (show) {
					jq(header).addClass(zcls + "-header-scroll");
					jq(this.$n("down")).addClass(zcls + "-down-scroll");
					jq(this.$n("up")).addClass(zcls + "-up-scroll");
				} else {
					jq(header).removeClass(zcls + "-header-scroll");
					jq(this.$n("down")).removeClass(zcls + "-down-scroll");
					jq(this.$n("up")).removeClass(zcls + "-up-scroll");
				}				
			}else {//horizontal
				if (show) {
					jq(header).addClass(zcls + "-header-scroll");
					jq(this.$n("right")).addClass(zcls + "-right-scroll");
					jq(this.$n("left")).addClass(zcls + "-left-scroll");
				} else {
					jq(header).removeClass(zcls + "-header-scroll");
					jq(this.$n("right")).removeClass(zcls + "-right-scroll");
					jq(this.$n("left")).removeClass(zcls + "-left-scroll");
				}		
			}
		}
	},
	_doClick: function(evt) {
		var cave = this.$n("cave"),
			allTab =  jq(cave).children();
		
		if (!allTab.length) return; // nothing to do	
			
		var ele = evt.domTarget,
			move = 0,
			tabbox = this.getTabbox(),
			head = this.$n("header"),
			scrollLength = tabbox.isVertical() ? head.scrollTop : head.scrollLeft,
			offsetLength = tabbox.isVertical() ? head.offsetHeight : head.offsetWidth,
			plus = scrollLength + offsetLength;
		
		//Scroll to next right tab
		if (ele.id == this.uuid + "-right") {
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetLeft + allTab[i].offsetWidth > plus) {
					move = allTab[i].offsetLeft + allTab[i].offsetWidth - scrollLength - offsetLength;
					if (!move || isNaN(move))
						return;
					this._doScroll("right", move);
					return;
				}
			}
		} else if (ele.id == this.uuid + "-left") {//Scroll to next left tab
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetLeft >= scrollLength) {
					//if no Sibling tab no sroll
					var tabli = jq(allTab[i]).prev("li")[0];
					if (!tabli)  return;
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
		} else if (ele.id == this.uuid + "-up") {
				for (var i = 0, count = allTab.length; i < count; i++) {
					if (allTab[i].offsetTop >= scrollLength) {
						var preli = jq(allTab[i]).prev("li")[0];
						if (!preli) return;
						move = scrollLength - preli.offsetTop ;
						this._doScroll("up", move);
						return;
					};
				};
				var preli = allTab[allTab.length-1];
				if (!preli) return;
				move = scrollLength - preli.offsetTop ;
				this._doScroll("up", move);
				return;
		} else if (ele.id == this.uuid + "-down") {
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetTop + allTab[i].offsetHeight > plus) {
					move = allTab[i].offsetTop + allTab[i].offsetHeight - scrollLength - offsetLength;
					if (!move || isNaN(move)) return ;
					this._doScroll("down", move);
					return;
				};
			};
		}
	},
	_fixWidth: function() {
		var tabs = this.$n();
		
		var	tabbox = this.getTabbox(),
			tbx = tabbox.$n(),
			cave = this.$n("cave"),
			head = this.$n("header"),
			l = this.$n("left"),
			r = this.$n("right"),
			btnsize = tabbox._scrolling ? l && r ? l.offsetWidth + r.offsetWidth : 0 : 0;
			this._fixHgh();
			if (this.parent.isVertical()) {
				var most = 0;
				//LI in IE doesn't have width...
				if (tabs.style.width) {
					tabs._width = tabs.style.width;
					;
				} else {
					//vertical tabs have default width 50px
					this._forceStyle(tabs, "w", tabs._width ? tabs._width : "50px");
				}
			} else if (!tabbox.inAccordionMold()) {
				if (tbx.offsetWidth < btnsize) 
					return;
				if (tabbox.isTabscroll()) {
					var toolbar = tabbox.toolbar;
					if (toolbar) 
						toolbar = toolbar.$n();
					if (!tbx.style.width) {
						this._forceStyle(tbx, "w", "100%");
						this._forceStyle(tabs, "w", jq.px0(jq(tabs).zk.revisedWidth(tbx.offsetWidth)));
						if (tabbox._scrolling) 
							this._forceStyle(head, "w", jq.px0(tbx.offsetWidth - (toolbar ? toolbar.offsetWidth : 0) - btnsize));
						else 
							this._forceStyle(head, "w", jq.px0(jq(head).zk.revisedWidth(tbx.offsetWidth - (toolbar ? toolbar.offsetWidth : 0))));
					} else {
						this._forceStyle(tabs, "w", jq.px0(jq(tabs).zk.revisedWidth(tbx.offsetWidth)));
						this._forceStyle(head, "w", tabs.style.width);
						if (tabbox._scrolling) 
							this._forceStyle(head, "w", jq.px0(head.offsetWidth - (toolbar ? toolbar.offsetWidth : 0) - btnsize));
						else 
							this._forceStyle(head, "w", jq.px0(head.offsetWidth - (toolbar ? toolbar.offsetWidth : 0)));
					}
					if (toolbar && tabbox._scrolling) 
						this.$n('right').style.right = toolbar.offsetWidth + 'px';
				} else {
					if (!tbx.style.width) {
						if (tbx.offsetWidth) {
							var ofw = jq.px0(tbx.offsetWidth);
							this._forceStyle(tbx, "w", ofw);
							this._forceStyle(tabs, "w", ofw);	
						}
					} else {
						this._forceStyle(tabs, "w", jq.px0(tbx.offsetWidth));
					}
				}
			}
	},
	_fixHgh: function () {
		var tabs = this.$n(),
			tabbox = this.getTabbox(),
			tbx = tabbox.$n(),
			head = this.$n("header"),
			u = this.$n("up"),
			d = this.$n("down"),
			cave =  this.$n("cave"),
			btnsize = u && d ? isNaN(u.offsetHeight + d.offsetHeight) ? 0 : u.offsetHeight + d.offsetHeight : 0;
		//fix tabpanels's height if tabbox's height is specified
		//Ignore accordion since its height is controlled by each tabpanel
		if (tabbox.isVertical()) {
			var child = jq(tbx).children('div');
			allTab = jq(cave).children();
			if (tbx.style.height) {
				this._forceStyle(tabs, "h", jq.px0(jq(tabs).zk.revisedHeight(tbx.offsetHeight, true)));
			} else {
				this._forceStyle(tbx, "h", jq.px0(allTab.length * 35));//give it default height
				this._forceStyle(tabs, "h", jq.px0(jq(tabs).zk.revisedHeight(tbx.offsetHeight, true)));
			}
			//coz we have to make the header full
			if (tabbox._scrolling) {
				this._forceStyle(head, "h", jq.px0(tabs.offsetHeight - btnsize));
			} else {
				this._forceStyle(head, "h", jq.px0(jq(head).zk.revisedHeight(tabs.offsetHeight, true)));
			}
			//separator(+border)
			this._forceStyle(child[1], "h", jq.px0(jq(child[1]).zk.revisedHeight(tabs.offsetHeight, true)));
			//tabpanels(+border)
			this._forceStyle(child[2], "h", 
				jq.px0(jq(child[1]).zk.revisedHeight(tabs.offsetHeight - (2 - zk.parseInt(jq(this.$n('cave')).css('padding-top'))), true)));
			// Merge breeze: now in vertical orientation Tabs has no border, but Tabpanels 
			// still has border, so we need to introduce a 2px offset
		} else {
			if (head) //accordion have no head
				head.style.height = "";
		}
	},

	_forceStyle: function(node, attr, value) {
		if (!value)	return;
		switch (attr) {
		case "h":
			node.style.height = zk.ie6_ ? "0px" : ""; // recalculate for IE6
			node.style.height = value;
			break;
		case "w":
			node.style.width = zk.ie6_ ? "0px" : ""; // recalculate for IE6
			node.style.width = "";
			node.style.width = value;
			break;
		}
	},

	onChildRemoved_: function (child) {
		var p = this.parent;
		if (p && child == p._selTab)
			p._selTab = null;
		this._scrollcheck("init");
		this.$supers("onChildRemoved_", arguments);
	},
	onChildAdded_: function () {
		this._scrollcheck("init");
		this.$supers("onChildAdded_", arguments);
	},
	
	ignoreFlexSize_: function(attr) {
		var p = this.getTabbox();
		return (p.isVertical() && 'h' == attr)
			|| (p.isHorizontal() && 'w' == attr); 
	}
});