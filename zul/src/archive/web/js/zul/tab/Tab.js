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
			if (this.desktop)
				this._sel();
		}
	},
	getTabbox: function() {
		return this.parent ? this.parent.parent : null;
	},
	getIndex: function() {
		return this.getChildIndex();
	},
	getZclass: function() {
		if (this._zclass != null)
			return this._zclass;

		var tabbox = this.getTabbox();
		if (!tabbox) return 'z-tab';

		var mold = tabbox.getMold();
		return 'z-tab' + (mold == 'default' ? (tabbox.isVertical() ? '-ver': '') : '-' + mold);
	},
	getLinkedPanel: function() {
		var w;
		return (w = this.getTabbox()) && (w = w.getTabpanels()) ?
			w.getChildAt(this.getIndex()): null;
	},
	_doCloseClick : function(evt) {
		if (!this._disabled) {
			this.fire('onClose');
			evt.stop();
		}
	},
	_toggleBtnOver : function(evt) {
		jq(evt.domTarget).toggleClass(this.getZclass() + "-close-over");
	},
	_sel: function(notify, init) {
		var tabbox = this.getTabbox();

		if (!tabbox) return;

		var	tabs = this.parent,
			oldtb;

		if (!oldtb) {
			if (tabbox.inAccordionMold()) {
				oldtb = tabbox.getSelectedTab();
			} else {
				for (var w = this.parent.firstChild; w; w = w.nextSibling) {
					if (w.isSelected() && w != this) {
						oldtb = w;
						break;
					}
				}
				if (!oldtb && this.isSelected())
					 oldtb = this;
			}
		}

		if (oldtb != this || init) {
			if (tabbox.isVertical())
				tabs._scrollcheck("vsel", this);
			else if (!tabbox.inAccordionMold())
				tabs._scrollcheck("sel", this);

			if (oldtb)
				this._setSel(oldtb, false, false, notify);
			this._setSel(this, true, notify, notify);
		}
	},
	_setSel: function(tab, toSel, notify, animation) {
		var tabbox = this.getTabbox(),
			zcls = this.getZclass(),
			panel = tab.getLinkedPanel();
		if (tab.isSelected() == toSel && notify) //notify if init tab is selected
			return;

		if (toSel)
			tabbox.setSelectedTab(tab);
		tab._selected = toSel;
		if (toSel)
			jq(tab).addClass(zcls + "-seld");
		else
			jq(tab).removeClass(zcls + "-seld");

		if (panel)
			panel._sel(toSel, animation);

		if (!tabbox.inAccordionMold()) {
			var tabs = this.parent;
			if (tabs) tabs._fixWidth();
		}
		if (notify)
			this.fire('onSelect', {items: [this.uuid], reference: this.uuid});
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
			tab = this;
		if (closebtn) {
			this.domListen_(closebtn, "onClick", '_doCloseClick');
			if (!closebtn.style.cursor)
				closebtn.style.cursor = "default";
			if (zk.ie6_)
				this.domListen_(closebtn, "onMouseOver", '_toggleBtnOver')
					.domListen_(closebtn, "onMouseOut", '_toggleBtnOver');
		}

		zk.afterMount(function () {
			if (tab.isSelected()) 
				tab._sel(false, true);
			else if (tab.parent._isInited())
				tab.parent._scrollcheck("init");
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
					return;
				}
			for (var tab = this; tab = tab.previousSibling;)
				if (!tab.isDisabled()) {
					tab._sel(true);
					return;
				}
		}
	}
});
