/* Tabpanel.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:33:02 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zul.tab.Tabpanel = zk.$extends(zul.Widget, {
	getTabbox: function() {
		return this.parent ? this.parent.parent : null;
	},
	isVisible: function() {
		return this.$supers('isVisible', arguments) && this.isSelected();
	},
	getZclass: function() {
		if (this._zclass != null)
			return this._zclass;
			
		var tabbox = this.getTabbox();
		if (!tabbox) return 'z-tabpanel';
		
		var mold = tabbox.getMold();
		return 'z-tabpanel' + (mold == "default" ? (tabbox.isVertical() ? '-ver' : '') : '-' + mold);
	},
	getLinkedTab: function() {
		var tabbox =  this.getTabbox();
		if (!tabbox) return null;
		
		var tabs = tabbox.getTabs();
		return tabs ? tabs.getChildAt(this.getIndex()) : null;
	},
	getIndex:function() {
		return this.getChildIndex();
	},
	isSelected: function() {
		var tab = this.getLinkedTab();
		return tab && tab.isSelected();
	},
	_sel: function (toSel, animation) { //don't rename (zkmax counts on it)!!
		var accd = this.getTabbox().inAccordionMold();
		if (accd && animation) {
			var p = this.$n("real"); //accordion uses 'real'
			zk(p)[toSel ? "slideDown" : "slideUp"](this);
		} else {
			var pl = accd ? this.$n("real") : this.$n();
			if (toSel) {
				jq(pl).show();
				zWatch.fireDown('onShow', this);
			} else {
				zWatch.fireDown('onHide', this);
				jq(pl).hide();
			}
		}
	},
	_fixPanelHgh: function() {
		var tabbox = this.getTabbox();
		if (!tabbox.inAccordionMold()) {
			var tbx = tabbox.$n(),
				hgh = tbx.style.height;
			
			if (hgh && hgh != "auto") {//tabbox has height
				var n = this.$n();
				hgh = zk(n.parentNode).vflexHeight();
				zk(n).setOffsetHeight(hgh);
			}
		}
	},
	domClass_: function () {
		var cls = this.$supers('domClass_', arguments);
		if (this.getTabbox().inAccordionMold())
			cls += ' ' + this.getZclass() + '-cnt';
		return cls;
	},
	onSize: _zkf = function() {
		this._fixPanelHgh();		//Bug 2104974
		if (zk.ie && !zk.ie8) zk(this.getTabbox().$n()).redoCSS(); //Bug 2526699 - (add zk.ie7)
	},
	onShow: _zkf,
	bind_: function() {
		this.$supers('bind_', arguments);
		if (this.getTabbox().isHorizontal()) {
			this._zwatched = true;
			zWatch.listen({onSize: this, onShow: this});
		}
	},
	unbind_: function () {
		if (this._zwatched) {
			zWatch.unlisten({onSize: this, onShow: this});
			this._zwatched = false;
		}
		this.$supers('unbind_', arguments);
	}
});