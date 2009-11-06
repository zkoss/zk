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
		image: _zkf,
		disabled: _zkf,
		selected: function(selected) {
			if (this.$n())
				this._sel(false);
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
		return tabpanels ? tabpanels.getChildAt(index) : null;
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
	_sel: function(notify, init) {
		var tabbox = this.getTabbox(),
			tabs = this.parent,
			tbx = tabbox.$n(),
			newtb =  this.$n(),
			oldtb = this._getSelTab();
		if (!newtb) return;
		if (oldtb != newtb || init) {
			if (tabbox.isVertical())
				tabs._scrollcheck("vsel",newtb);
			else if (!tabbox.inAccordionMold())
				tabs._scrollcheck("sel",newtb);
			if (oldtb)
				this._setSel(oldtb, false, false, notify);
			this._setSel(newtb, true, notify, notify);
		}
	},
	_setSel: function(tb, toSel, notify, animation) {
		var tabbox = this.getTabbox(),
			zcls = this.getZclass(),
			tab = zk.Widget.$(tb),
			panel = tab.getLinkedPanel();
		if (tab.isSelected() == toSel && notify) //notify if init tab is selected
			return;

		if (toSel)
			tabbox.setSelectedTab(tab);
		tab._selected = toSel;
		if (toSel)
			jq(tb).addClass(zcls + "-seld");
		else 
			jq(tb).removeClass(zcls + "-seld");

		if (panel)
			panel._sel(toSel, animation);

		if (!tabbox.inAccordionMold()) {
			var tabs = this.parent;
			if (tabs) tabs._fixWidth();
		}
		if (notify) {
			this.fire('onSelect', {items: [this.uuid], reference: this.uuid});
		}
	},
	_getSelTab: function() {
		var tabbox = this.getTabbox();
		if (!tabbox) return null;
		if (tabbox.inAccordionMold()) {
			var t = this._getSelTabFromTop()
			return t ? t.$n() : null;
		} else {
			for (var wgt = this; (wgt = wgt.nextSibling);)
				if (wgt.isSelected())
					return wgt.$n();
			for (var wgt = this; (wgt = wgt.previousSibling);)
				if (wgt.isSelected())
					return wgt.$n();
			if (this.isSelected())
				return this.$n();
		}
		return null;
	},
	_getSelTabFromTop: function() {
		var tabbox = this.getTabbox();
		return tabbox ? tabbox.getSelectedTab() : null;
	},
	//protected
	doClick_: function(evt) {
		if (this._disabled)
			return;
		this._sel(true);
		this.$supers('doClick_', arguments);
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
		var closebtn = this.$n('close'),
			tabs = this.parent,
			tab = this,
			selected = this.isSelected();
		if (closebtn) {
			this.domListen_(closebtn, "onClick", '_doCloseClick');
			if (!closebtn.style.cursor)
				closebtn.style.cursor = "default";
			if (zk.ie6_)
				this.domListen_(closebtn, "onMouseOver", '_toggleBtnOver')
					.domListen_(closebtn, "onMouseOut", '_toggleBtnOver');
		}

		after.push( function () {
			if (selected) {
				tab._sel(false, true);
			} else if (tabs._isInited())
				tabs._scrollcheck("init");

		});
	},
	unbind_: function () {
		var closebtn = this.$n('close');
		if (closebtn) {
			this.domUnlisten_(closebtn, "onClick", '_doCloseClick');
			if (zk.ie6_)
				this.domUnlisten_(closebtn, "onMouseOver", '_toggleBtnOver')
					.domUnlisten_(closebtn, "onMouseOut", '_toggleBtnOver');
		}
		this.$supers('unbind_', arguments);
	},
	//event handler//
	onClose: function () {
		if (this.isSelected()) {
			var tabbox = this.getTabbox();
			for (var tab = this; tab = tab.nextSibling;) 
				if (!tab.isDisabled()) {
					tab._sel(true);
					return null;
				}
			for (var tab = this; tab = tab.previousSibling;) 
				if (!tab.isDisabled()) {
					tab._sel(true);
					return null;
				}
		}
	}
});
