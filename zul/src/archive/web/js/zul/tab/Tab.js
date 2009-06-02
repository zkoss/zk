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
		this.listen('onClose', this, null, -1000);
	},
	$define: {
		closable: _zkf = function() {
			this.rerender();
		},
		disabled: _zkf,
		selected: function(selected) {
			if (this.getNode())
				this._selTab(this, false);
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
	_selTab: function(tab, notify) {
		var tabbox = this.getTabbox(),
			tbx = tabbox.getNode();
		var tabs = this.parent,
			tb =  tab.getNode(),
			old = this._getSelTab(tab);
		if (!tb) return;
		if (old != tb || !notify) {
			if (tabbox.isVertical())
				tabs._scrollcheck("vsel",tb);
			else if (!tabbox.inAccordionMold())
				tabs._scrollcheck("sel",tb);
			if (old)
				this._setTabSel(old, false, false, notify);
			this._setTabSel(tb, true, notify, notify);
		}
	},
	_setTabSel: function(tb, toSel, notify, animation) {
		var tabbox = this.getTabbox(),
			tab = zk.Widget.$(tb);
		if (tab._selected == toSel && notify) //notify if init tab is selected
			return;
		tab._selected = toSel;
		zDom[toSel ? "addClass" : "rmClass"](tb, this.getZclass() + "-seld" );
		var accd = tabbox.inAccordionMold(),
			panel = tab.getLinkedPanel();
		if (panel)
			if (accd && animation) {
				var p = panel.getSubnode("real");
				zAnima[toSel ? "slideDown" : "slideUp"](p);
			} else {
				var pl = panel.getNode();
				zDom[toSel ? "show" : "hide"](pl);
			}

		if (!accd) {
			var tabs = this.parent;
			   if (tabs) tabs._fixWidth();
		}

		if (notify) {
			this.fire('onSelect', {items: [this.uuid], reference: this.uuid});
		}
		/* //oriional
		 * if (notify)
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
			var node = tab;//Notice : not DOM node
			for (node = tab; node = node.nextSibling;)
				if (node._selected)
					return node.getNode();
			for (node = tab; node = node.previousSibling;)
				if (node._selected)
					return node.getNode();
			if (tab._selected)
				return tab.getNode();
		}
		return null;
	},
	//protected
	doClick_: function(evt) {
		if (!evt) evt = window.event;
		if (this._disabled)
			return;
		//@TODO
//		if (!this._sliding(tab)) //Bug 1571408
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
	bind_: function (desktop, skipper, after) {
		this.$supers('bind_', arguments);
		var closebtn = this.getSubnode('close'),
			tab = this,
			selected = this._selected;
		if (closebtn) {
			this.domListen_(closebtn, "onClick", '_doCloseClick');
			if (!closebtn.style.cursor)
				closebtn.style.cursor = "default";
		}
		var tabs = this.parent;
		after.push(function () {
			if (selected)
				tab._selTab(tab, false);

			if (tabs._init)
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
		zk.log(this._selected);
	}
});
