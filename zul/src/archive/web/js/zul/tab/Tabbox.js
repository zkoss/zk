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
		//The tabs must in index 0
		return this.getChildAt(0);
	},
	getTabpanels: function () {
		//The tabpanels must in index 1
		return this.getChildAt(1);
	},
	getZclass: function () {
		return this._zclass == null ? "z-tabbox" +
			( this.inAccordionMold() ? "-" + this.getMold() : this.isVertical() ? "-ver" : "") : this._zclass;
	},
	isHorizontal: function() {
		return "horizontal" == this.getOrient();
	},
	isTabScroll: function() {
		return true == this._tabscroll;
	},
	isVertical: function() {
		return "vertical" == this.getOrient();
	},
	inAccordionMold: function () {
		return this.getMold().indexOf("accordion") < 0 ? false : true;
	},
	getSelectedIndex: function() {
		var tabnode = zDom.$(this._selTab),
		    tab = zk.Widget.$(tabnode);
		return tab != null ? tab.getIndex() : -1 ;
	},
	setSelectedIndex: function(index) {
		var tabs = this.getTabs();
		if (!tabs) return;
		this.setSelectedTab(tabs.getChildAt(index));
	},
	getSelectedPanel: function() {
		var tabnode = zDom.$(this._selTab),
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
		var tabnode = zDom.$(this._selTab);
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
	onSize: _zkf = function() {
		this.getTabs().onSize();
	},
	onVisi: _zkf,
	bind_: function () {
		this.$supers('bind_', arguments);
		this.tabs = this.getTabs();
		this.tabpanels = this.getTabpanels();
		this._scrolling = false;
		zWatch.listen({onSize: this});
		if (this.inAccordionMold()) {
			zDom.cleanVisibility(this.getNode());
		}
		zk.afterMount(
			this.proxy(function () {
				var x = this._selTab,
					wgt = zDom.$(x),
					tab = zk.Widget.$(wgt);
				if (tab)
					tab.setSelected(true);
			})
		);
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this});
		this.$supers('unbind_', arguments);
	},
	//super//
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		zWatch.fireDown('onSize', null, this);
	},
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		zWatch.fireDown('onSize', null, this);
	}
});
