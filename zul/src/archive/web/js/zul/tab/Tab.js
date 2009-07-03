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
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onClose: this}, -1000);
	},
	$define: {
		closable: _zkf = function() {
			this.rerender();
		},
		disabled: _zkf,
		selected: function(selected) {
			if (this.getNode())
				this._selTab(false);
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
		return this._zclass == null ? "z-tab" + ( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : "-" + tabbox._mold):
		this._zclass;
	},
	getLinkedPanel: function() {
		var tabbox =  this.getTabbox(),
			tabpanels = tabbox.getTabpanels(),
			index = this.getIndex();
		return tabpanels.getChildAt(index);
	},
	_doCloseClick : function(evt) {
		if (!this._disabled) {
			this.fire('onClose');
			evt.stop();
		}
	},
	_toggleBtnOver : function(evt) {
		var zcls = this.getZclass(),
			cmp = evt.domTarget;
		jq(cmp).toggleClass(zcls + "-close-over");

	},
	_selTab: function(notify, init) {
		var tabbox = this.getTabbox(),
			tabs = this.parent,
			tbx = tabbox.getNode(),
			newtb =  this.getNode(),
			oldtb = this._getSelTab();
		if (!newtb) return;
		if (oldtb != newtb || init) {
			if (tabbox.isVertical())
				tabs._scrollcheck("vsel",newtb);
			else if (!tabbox.inAccordionMold())
				tabs._scrollcheck("sel",newtb);
			if (oldtb)
				this._setTabSel(oldtb, false, false, notify);
			this._setTabSel(newtb, true, notify, notify);
		}
	},
	_setTabSel: function(tb, toSel, notify, animation) {
		var tabbox = this.getTabbox(),
			zcls = this.getZclass(),
			tab = zk.Widget.$(tb);
		if (tab._selected == toSel && notify) //notify if init tab is selected
			return;
		if (toSel)
			tabbox.setSelectedTab(tab);
		tab._selected = toSel;
		if (toSel)
			jq(tb).addClass(zcls + "-seld");
		else 
			jq(tb).removeClass(zcls + "-seld");
		var accd = tabbox.inAccordionMold(),
			panel = tab.getLinkedPanel();
		if (panel)
			if (accd && animation) {
				var p = panel.getSubnode("real");
				zk(p)[toSel ? "slideDown" : "slideUp"](this);
			} else {
				var pl = accd ? panel.getSubnode("real") : panel.getNode(); //Can't use getSubnode coz
				if (toSel) {
					jq(pl).show();
					zWatch.fireDown('onShow', null, pl);
				} else {
					jq(pl).hide();
					zWatch.fireDown('onHide', null, pl);
				}
			}
		if (!accd) {
			var tabs = this.parent;
			   if (tabs) tabs._fixWidth();
		}
		if (notify) {
			this.fire('onSelect', {items: [this.uuid], reference: this.uuid});
		}
	},
	/**
	 * Get selected tab
	 * @param {Object} tab
	 */
	_getSelTab: function() {
		var tabbox = this.getTabbox();
		if (!this) return null;
		if (tabbox.inAccordionMold()) {
			var t = this._getSelTabFromTop()
			return t.getNode();
		} else {
			var node = this;//Notice : not DOM node
			for (node = this; node = node.nextSibling;)
				if (node._selected)
					return node.getNode();
			for (node = this; node = node.previousSibling;)
				if (node._selected)
					return node.getNode();
			if (this._selected)
				return this.getNode();
		}
		return null;
	},
	_getSelTabFromTop: function() {
		var tabbox = this.getTabbox(),
			t = tabbox.getSelectedTab();
		return t;
	},
	//protected
	doClick_: function(evt) {
		if (this._disabled)
			return;
		this._selTab(true);
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
	bind_: function (desktop, skipper, after) {
		this.$supers('bind_', arguments);
		var closebtn = this.getSubnode('close'),
			tabs = this.parent,
			tab = this,
			selected = this._selected;
		if (closebtn) {
			this.domListen_(closebtn, "onClick", '_doCloseClick');
			if (!closebtn.style.cursor)
				closebtn.style.cursor = "default";
			if (zk.ie6_) {
				this.domListen_(closebtn, "onMouseOver", '_toggleBtnOver');
				this.domListen_(closebtn, "onMouseOut", '_toggleBtnOver');
			}
		}

		after.push( function () {
			if (selected) {
				tab._selTab(false, true);
			} else if (tabs._init)
				tabs._scrollcheck("init");

		});
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
		var closebtn = this.getSubnode('close');
		if (closebtn)
			this.domUnlisten_(closebtn, "onClick", '_doCloseClick');
	},
	//event handler//
	onClose: function () {
		var tabbox = this.getTabbox(),
			tab = this;
		for (tab = this; tab = tab.nextSibling;)
			if (!tab.isDisabled()) {
				tab._selTab(true);
				return null;
			}
		for (tab = this; tab = tab.previousSibling;)
			if (!tab.isDisabled()) {
				tab._selTab(true);
				return null;
			}
	}
});
