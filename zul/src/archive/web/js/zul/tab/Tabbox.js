/* Tabbox.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:34 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zul.tab.Tabbox = zk.$extends(zul.Widget, {
	_orient: "horizontal",
	_tabscroll: true,

	$define: {
		tabscroll: _zkf = function () {
			this.rerender();
		},
		orient: _zkf,
		panelSpacing: function(v) {
			if (v != null && v.length == 0)
				this._panelSpacing = v = null;
			this.rerender();
		}
	},

	getTabs: function () {
		return this.tabs;
	},
	getTabpanels: function () {
		return this.tabpanels;
	},
	getToolbar: function () {
		return this.toolbar;
	},
	getZclass: function () {
		return this._zclass == null ? "z-tabbox" +
			( this.inAccordionMold() ? "-" + this.getMold() : this.isVertical() ? "-ver" : "") : this._zclass;
	},
	isHorizontal: function() {
		return "horizontal" == this.getOrient();
	},
	isVertical: function() {
		return "vertical" == this.getOrient();
	},
	inAccordionMold: function () {
		return this.getMold().indexOf("accordion") != -1;
	},
	getSelectedIndex: function() {
		var tabnode = zk(this._selTab).jq[0],
		    tab = zk.Widget.$(tabnode);
		return tab != null ? tab.getIndex() : -1 ;
	},
	setSelectedIndex: function(index) {
		var tabs = this.getTabs();
		if (!tabs) return;
		this.setSelectedTab(tabs.getChildAt(index));
	},
	getSelectedPanel: function() {
		var tabnode = zk(this._selTab).jq[0],
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
		var tabnode = zk(this._selTab).jq[0];
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
	bind_: function () {
		this.$supers('bind_', arguments);
		this.tabs = this.getTabs();
		this.tabpanels = this.getTabpanels();
		this._scrolling = false;
		zk.afterMount(
			this.proxy(function () {
				var wgt = zk(this._selTab).jq[0],
					tab = zk.Widget.$(wgt);
				if (tab)
					tab.setSelected(true);
			})
		);
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
	},
	//super//
	removeChildHTML_: function (child, prevsib) {
		this.$supers('removeChildHTML_', arguments);
		if (this.isVertical() && child.$instanceof(zul.tab.Tabs))
			jq(child.uuid + '-line', zk).remove();
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Toolbar))
			this.toolbar = child;
		else if (child.$instanceof(zul.tab.Tabs))
			this.tabs = child;
		else if (child.$instanceof(zul.tab.Tabpanels)) {
			this.tabpanels = child;
		}
		this.rerender();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.toolbar)
			this.toolbar = null;
		else if (child == this.tabs)
			this.tabs = null;
		else if (child == this.tabpanels)
			this.tabpanels = null;
		this.rerender();
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		zWatch.fireDown('onSize', this);
	},
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		zWatch.fireDown('onSize', this);
	}
});
