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
	},
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
	}
});