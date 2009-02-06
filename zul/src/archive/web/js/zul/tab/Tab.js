/* Tab.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:51 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
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