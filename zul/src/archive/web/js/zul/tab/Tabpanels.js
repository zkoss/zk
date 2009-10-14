/* Tabpanels.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:57 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zul.tab.Tabpanels = zk.$extends(zul.Widget, {
	getTabbox: function() {
		return this.parent ? this.parent : null;
	},
	getZclass: function() {
		var tabbox = this.getTabbox();
		return this._zclass == null ? "z-tabpanels" +
		( tabbox._mold == "default" ? ( tabbox.isVertical() ? "-ver": "" ) : "-" + tabbox._mold):
		this._zclass;
	},
	setWidth: function (val) {
		var n = this.$n(),
			tabbox = this.getTabbox(),
			isVer = n && tabbox ? tabbox.isVertical() : false;
		if (isVer && !this.__width) {
			n.style.width = '';
		}
		this.$supers('setWidth', arguments);
		
		if (isVer && n.style.width) {
			this.__width = n.style.width;
		}
		if (isVer) {
			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this);
		}
	},
	setStyle: function (val) {
		var n = this.$n(),
			tabbox = this.getTabbox(),
			isVer = n && tabbox ? tabbox.isVertical() : false;
		if (isVer && !this.__width) {
			n.style.width = '';
		}
		this.$supers('setStyle', arguments);
		
		if (isVer && n.style.width) {
			this.__width = n.style.width;
		}
		if (isVer) {
			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this);
		}
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.getTabbox().isVertical()) {
			zWatch.listen({
				onSize: this,
				beforeSize: this,
				onShow: this
			});
			var n = this.$n();
			if (n.style.width)
				this.__width = n.style.width;
		}
	},
	unbind_: function () {
		this.$supers('unbind_', arguments);
		if (this.getTabbox().isVertical()) {
			zWatch.unlisten({
				onSize: this,
				beforeSize: this,
				onShow: this
			});
		}
	},
	onSize: _zkf = function () {
		var parent = this.parent.$n();
		if (!zk(parent).isRealVisible() || this.__width)
			return;
			
		var width = parent.offsetWidth,
			n = this.$n();
		
		width -= jq(parent).find('>div:first')[0].offsetWidth
				+ jq(n).prev()[0].offsetWidth;
		
		n.style.width = jq.px(zk(n).revisedWidth(width));
	},
	onShow: _zkf,
	beforeSize: function () {
		this.$n().style.width = this.__width ? this.__width : '';
	}
});