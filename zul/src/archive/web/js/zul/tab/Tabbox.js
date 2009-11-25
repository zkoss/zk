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
		panelSpacing: _zkf
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
		return this._selTab ? this._selTab.getIndex() : -1 ;
	},
	setSelectedIndex: function(index) {
		if (this.tabs)
			this.setSelectedTab(this.tabs.getChildAt(index));
	},
	getSelectedPanel: function() {
		return this._selTab ? this._selTab.getLinkedPanel() : null;
	},
	setSelectedPanel: function(panel) {
		if (panel && panel.getTabbox() != this)
			return
		var tab = panel.getLinkedTab();
		if (tab)
			this.setSelectedTab(tab);
	},
	getSelectedTab: function() {
		return this._selTab;
	},
	setSelectedTab: function(tab) {
		if (typeof tab == 'string')
			tab = zk.Widget.$(tab);
		if (this._selTab != tab) {
			if (tab)
				tab.setSelected(true);
				//it will set _selTab (but we still set it later just in case)
			this._selTab = tab;
		}
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		
		// used in Tabs.js
		this._scrolling = false;
		var tab = this._selTab;
		
		if (tab) {
			zk.afterMount(function() {
				tab.setSelected(true);
			});
		}
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
		if (this.desktop)
			zWatch.fireDown('onSize', this);
	},
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop)
			zWatch.fireDown('onSize', this);
	}
});
