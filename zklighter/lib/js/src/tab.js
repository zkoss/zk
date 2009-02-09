_z='zul.tab';if(!zk.$import(_z)){try{_zkpk=zk.$package(_z);

zul.tab.Tabbox = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	_tabscroll: true,
	getTabs: function () {
		//The tabs must in index 0
		return this.getChildAt(0);
	},
	getTabpanels: function () {
		//The tabpanels must in index 1
		return this.getChildAt(1);
	},
	isTabscroll: function() {
		return this._tabscroll;
	},
	setTabscroll: function(tabscroll) {
		if (this._tabscroll != tabscroll) {
			this._tabscroll = tabscroll;
			this.rerender();
		}
	},
	getZclass: function () {
		return this._zclass == null ? "z-tabbox" +
			( this.inAccordionMold() ? "-" + this.getMold() : this.isVertical() ? "-ver" : "") : this._zclass;
	},
	setOrient: function(orient) {
		if ("horizontal" == orient || "vertical" == orient || !this.inAccordionMold()) {
			if (this._orient != orient) {
				this._orient = orient;
				this.rerender();
			}
		}
	},
	getOrient: function () {
		return this._orient;
	},
	isHorizontal: function() {
		return "horizontal" == this.getOrient();
	},
	isVertical: function() {
		return "vertical" == this.getOrient();
	},
	inAccordionMold: function () {
		return this.getMold().indexOf("accordion") < 0 ? false : true;
	},
	getSelectedIndex: function() {
		var tabnode = zDom.$(this._seltab),
		    tab = zk.Widget.$(tabnode);
		return tab != null ? tab.getIndex() : -1 ;
	},
	setSelectedIndex: function(index) {
		var tabs = this.getTabs();
		if (!tabs) return;
		this.setSelectedTab(tabs.getChildAt(index));
	},
	getSelectedPanel: function() {
		var tabnode = zDom.$(this._seltab),
		    tab = zk.Widget.$(tabnode);
		return tab != null ? tab.getLinkedPanel() : null;
	},
	setSelectedPanel: function(panel) {
		if (panel != null && panel.getTabbox() != this)
			return
		var tab = panel.getLinkedTab();
		if (!tab) return
		this.setSelectedTab(tab);
	},
	getSelectedTab: function() {
		var tabnode = zDom.$(this._seltab);
		return zk.Widget.$(tabnode);
	},
	setSelectedTab: function(tab) {
        if (zul.tab.Tab.isInstance(tab))
            tab = tab.uuid;
        if (this._selTab != tab) {
            this._selTab = tab;
            var wgt = zk.Widget.$(tab);
            if (wgt) {
                wgt.setSelected(true);
            }
        }
	},
	getPanelSpacing: function() {
		return this._panelSpacing;
	},
	setPanelSpacing: function(panelSpacing) {
		if (panelSpacing != null && panelSpacing.length == 0)
			panelSpacing = null;
		if (this._panelSpacing != panelSpacing) {
			this._panelSpacing = panelSpacing;
			this.rerender();
		}
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		this.tabs = this.getTabs();
		this.tabpanels = this.getTabpanels();
//		if (this.inAccordionMold()) {
//			zDom.cleanVisibility(this.getNode());
//		}
		zk.afterMount(
			this.proxy(function () {
				if (this.inAccordionMold()) {
					;
				} else {
					var x = this._selTab, wgt = zDom.$(x), tab = zk.Widget.$(wgt);
					tab.setSelected(true);
				}
			})
		);
	}
});
(_zkwg=_zkpk.Tabbox).prototype.className='zul.tab.Tabbox';_zkmd={};
_zkmd['accordion-lite']=
function (out) {
	out.push('<div ', this.domAttrs_(), '>');
	var tps = this.getTabpanels();
	if (tps) {
		tps.redraw(out);
	}
	out.push("</div>");
}
_zkmd['default']=
function (out) {
	out.push('<div ', this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push("</div>");
}
_zkmd['accordion']=
function (out) {
	out.push('<div ', this.domAttrs_(), '>');
	var tps = this.getTabpanels();
	if (tps) {
		tps.redraw(out);
	}
	out.push("</div>");
}
zkmld(_zkwg,_zkmd);
zul.tab.Tabs = zk.$extends(zul.Widget, {
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
		if (tabbox.getNode())
			zDom.cleanVisibility(tabbox.getNode());
	},
	onVisible: _zkf, 
	insertChildHTML_: function (child, before, desktop) {
		var last = child.previousSibling;
		if (before || !last) {
			zDom.insertHTMLBefore(before.getNode(), child._redrawHTML());
		} else {
			zDom.insertHTMLAfter(last.getNode(), child._redrawHTML());
		}
		child.bind_(desktop);
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		zWatch.listen("onSize", this);
		zWatch.listen("onVisible", this);
	},
	unbind_: function () {
		zWatch.unlisten("onSize", this);
		zWatch.unlisten("onVisible", this);
		this.$supers('unbind_', arguments);
	}
});
(_zkwg=_zkpk.Tabs).prototype.className='zul.tab.Tabs';_zkmd={};
_zkmd['default']=
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid;
	out.push('<div ', this.domAttrs_(), '>' ,'<div id="', uuid, '$right">','</div>',
		'<div id="', uuid, '$left">','</div>','<div id="', uuid, '$header"',
		' class="', zcls, '-header" >','<ul id="', uuid, '$cave"','class="', zcls, '-cnt">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
	out.push('<li id="', uuid,'$edge"',
		' class="', zcls, '-edge" ></li><div class="z-clear"></div></ul>',
		'</div><div id="', uuid, '$line"',
		' class="', zcls, '-space" ></div></div>');
}
zkmld(_zkwg,_zkmd);
zul.tab.Tab = zk.$extends(zul.LabelImageWidget, {
	_selected : false,
	_closable : false,
	_disabled : false,
	
	isClosable: function() {
		return this._closable;
	},
	setClosable: function(closable) {
		if(this._closable != closable) {
			this._closable = closable;
			this.rerender();
		}
	},
	isSelected: function() {
		return this._selected;
	},
	setSelected: function(selected) {
		if (this._selected != selected) {
			if (this.getNode()) {
				this._selected = selected;
				this._selTab(this, false);
			}
		}
	},
	isDisabled: function() {
		return this._disabled;
	},
	setDisabled: function(disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			this.rerender();
		}
	},
	getTabbox: function() {
		return this.parent ? this.parent.parent : null;
	},
	getIndex: function() {
		return this.getChildIndex();
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tab" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : "-" + tabbox._mold):
		this._zclass;
	},
	getLinkedPanel: function() {
		var tabbox =  this.getTabbox(),
			tabpanels = tabbox.getTabpanels(),
			index = this.getIndex();
		return tabpanels.getChildAt(index);
	},
	_doClosebtnClick : function(evt) {
		if (!evt) evt = window.event;
		if (this._disabled)
			return;
		this.fire('onClose', true);
		zEvt.stop(evt);
	},
	_sliding: function(tab) {
		var tabbox = this.getTabbox(),
			panel = this.getLinkedPanel();
		if (!panel || !tabbox || !tabbox.inAccordionMold())
			return false;

		/*for (var node = panel; node = node.nextSibling;)
			if (getZKAttr($real(node), "animating"))
				return true;

		for (var node = panel; node = node.previousSibling;)
			if (getZKAttr($real(node), "animating"))
				return true;*/
		return false;
	},
	_selTab: function(tb, notify) {
		var tabbox = this.getTabbox(),
			tab =  tb.getNode();
			old = this._getSelTab(tab);
		/*
		 * if (zkTabbox2._isVert(tabbox))
			zkTabs2.scrollingchk($uuid($parent(tab)),"vsel",tab);
		else if (!zkTabbox2._isAccord(tabbox))
			zkTabs2.scrollingchk($uuid($parent(tab)),"sel",tab);
		 */
		if (!tab) return;
		if (old != tab) {
			if (old)
				this._setTabSel(old, false, false, notify);
			this._setTabSel(tab, true, notify, notify);
		}
	},
	_setTabSel: function(tab, toSel, notify, animation) {
		if (tab._selected == toSel)
			return;
		tab._selected = toSel;
		zDom[toSel ? "addClass" : "rmClass"](tab, this.getZclass() + "-seld" );
		var tabbox = this.getTabbox(),
			accd = tabbox.inAccordionMold(),
			panel = zk.Widget.$(tab).getLinkedPanel();
		if (panel)
			if (accd && animation) {
				var p = panel.getSubnode("real");
				zAnima[toSel ? "slideDown" : "slideUp"](p);
			} else {
				var pl = panel.getNode();
				zDom[toSel ? "show" : "hide"](pl);
			}
		/*
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
		 */
	},
	/**
	 * Get selected tab
	 * @param {Object} tab
	 */
	_getSelTab: function(tab) {
		var tabbox = this.getTabbox();
		if (!tab) return null;
		if (tabbox.inAccordionMold()) {
			//@TODO Accordion
		} else {
			var node = tab;
			for (node = tab; node = node.nextSibling;)
				if (node._selected)
					return node;
			for (node = tab; node = node.previousSibling;)
				if (node._selected)
					return node;
			if (tab._selected) return tab;
		}
		return null;
	},
	//protected
	doClick_: function(evt) {
		if (!evt) evt = window.event;
		if (this._disabled)
			return;
		//@TODO
//		if (!zkTab2._sliding(tab)) //Bug 1571408
			this._selTab(this, true);
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this.isDisabled() ? this.getZclass() + '-disd' : '';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage();
		if (!img) return label;
		img = '<img src="' + img + '" align="absmiddle" class="' + this.getZclass() + '-img"/>';
		return label ? img + ' ' + label: img;
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		var uuid = this.uuid,
			closebtn = zDom.$(uuid, 'close');
        if (closebtn) {
			zEvt.listen(closebtn, "click", this.proxy(this._doClosebtnClick, '_tabClose'));
			if (!closebtn.style.cursor)
				closebtn.style.cursor = "default";
		//				if (zk.ie6Only) {
		//					zEvt.listen(closebtn, "mouseover", this.proxy(this._doMouseOver, '_tabMouseOver'));
		//					zEvt.listen(closebtn, "mouseout", this.proxy(this._doMouseOut, '_tabMouseOut'));
		//            	}
//
//	 var meta = $parent(cmp);
//	 if (!meta._toscroll)
//	 meta._toscroll = function () {
//	 zkTabs2.scrollingchk($uuid(meta));
//	 };
//	 zk.addInit(meta._toscroll, false, $uuid(meta) );

		}
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
		var closebtn = zDom.$(this.uuid, 'close');
		if (closebtn) {
			zWatch.unlisten(closebtn, "click", this.proxy(this._doClosebtnClick, '_tabClose'));		
		}
	}
});
(_zkwg=_zkpk.Tab).prototype.className='zul.tab.Tab';_zkmd={};
_zkmd['default']=
function (out) {
	var zcls = this.getZclass(),
		uuid = this.uuid;;
	out.push('<li ', this.domAttrs_(), '>');
	if (this.isClosable()) {
		out.push('<a id="', uuid, '$close" class="', zcls, '-close"', 'onClick="return false;" ></a>');
	}
	out.push('<a id="', uuid, '$a" class="', zcls, '-body"', 'onClick="return false;" href="#">','<em id="', uuid, '$em">');
	if (this.isClosable())
		out.push('<span id="',uuid, '$inner" class="',zcls, '-inner ', zcls, '-close-inner">');
	else
		out.push('<span id="',uuid, '$inner" class="',zcls, '-inner ">');
	out.push('<span class="', zcls, '-text">',this.domContent_(),'</span></span></em></a></li>');
}
zkmld(_zkwg,_zkmd);
zul.tab.Tabpanels = zk.$extends(zul.Widget, {
	getTabbox: function() {
		return this.parent ? this.parent : null;
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabpanels" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : "-" + tabbox._mold):
		this._zclass;
	}
});
(_zkwg=_zkpk.Tabpanels).prototype.className='zul.tab.Tabpanels';_zkmd={};
_zkmd['default']=
function (out) {
	out.push('<div id="', this.uuid,'"' ,this.domAttrs_(), '>');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div>');
}
zkmld(_zkwg,_zkmd);
zul.tab.Tabpanel = zk.$extends(zul.Widget, {
	getTabbox: function() {
		return this.parent ? this.parent.parent : null;
	},
	isVisible: function() {
		return this.$supers('isVisible', arguments) && this.isSelected();
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabpanel" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : "-" + tabbox._mold):
		this._zclass;
	},
	bind_: function() {
		this.$supers('bind_', arguments);
		this._fixPanelHgh();
	},
	getLinkedTab: function() {
		var tabbox =  this.getTabbox(),
			tabs = tabbox.getTabs(),
			index = this.getIndex();
		return tabs.getChildAt(index);
	},
	getIndex:function() {
		return this.getChildIndex();
	},
	isSelected: function() {
		var tab = this.getLinkedTab();
		return tab != null && tab.isSelected();
	},
	_fixPanelHgh: function() {
		var tabbox = this.getTabbox();
		//@TODO Fix
		/*if (!tabbox.inAccordionMold()) {
			var hgh = zDom.getStyle(tabbox.getNode(),"height");
			var panels = this.parent;
			if (panels) {
                for (var pos, n = panels.firstChild; n; n = n.nextSibling) {
                    if (n.id) {
                        if (zk.ie) { // Bug: 1968434, this solution is very dirty but necessary.
                            pos = n.style.position;
                            n.style.position = "relative";
                        }
                        if (hgh && hgh != "auto") {//tabbox has height
                            hgh = zDom.vflexHeight(panels);
                            zDom.revisedHeight(n, hgh);
                        }
                        //let real div 100% height
                        zk.log(zDom.$(this.uuid, "$real"));
                        zDom.addClass(zDom.$(this.uuid, "$real"), this.getZclass() + "-cnt");
                        if (zk.ie)
                            n.style.position = pos;
                    }
                }
			}
		}*/
	}

});
(_zkwg=_zkpk.Tabpanel).prototype.className='zul.tab.Tabpanel';_zkmd={};
_zkmd['default']=
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		tab = this.getLinkedTab(),
		tabzcs = tab.getZclass(),
		tabbox = this.getTabbox();
	if (tabbox.inAccordionMold()) {//Accordion
		out.push('<div id="',uuid ,'"' ,' class="',zcls ,'-outer">' );
		if (tabbox.getMold() == "accordion" && tabbox.getPanelSpacing() != null && this.getIndex() != 0) {
			out.push('<div style="margin:0;display:list-item;width:100%;height:',tabbox.getpanelSpacing(),';"></div>');
		}
		out.push('<div id="',tab.uuid,'"',tab.domAttrs_(),'>',
		'<div align="left" class="',tabzcs,'-header" >');
		if (tab.isClosable()) {
			out.push('<a id="',tab.uuid,'$close"  class="',tabzcs,'-close"></a>');
		}
		out.push('<a href="javascript:;" id="',tab.uuid,'$a" class="',tabzcs,'-tl">',
		'<em class="',tabzcs,'-tr">','<span class="',tabzcs,'-tm">','<span class="',tabzcs,'-text">',
		tab.domContent_(),'</span></span></em></a></div></div>'
		);
		out.push('<div id="',uuid,'$real"', this.domAttrs_(),'>',
			'<div id="',uuid,'$cave" >');
		for (var w = this.firstChild; w; w = w.nextSibling)
				w.redraw(out);
		out.push('</div></div></div>');
	} else {//Default Mold
		out.push('<div id="', uuid,'"' ,this.domAttrs_(), '>', '<div id="', uuid, '$real">')
			for (var w = this.firstChild; w; w = w.nextSibling)
				w.redraw(out);
		out.push('</div></div>');
	}
}
zkmld(_zkwg,_zkmd);
}finally{zPkg.end(_z);}}