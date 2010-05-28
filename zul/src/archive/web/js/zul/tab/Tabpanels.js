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
/**
 * A collection of tab panels.
 *
 * <p>Default {@link #getZclass}: z-tabpanels.
 */
zul.tab.Tabpanels = zk.$extends(zul.Widget, {
	/** Returns the tabbox owns this component.
	 * @return Tabbox
	 */
	getTabbox: function() {
		return this.parent;
	},
	getZclass: function() {
		if (this._zclass != null)
			return this._zclass;

		var tabbox = this.getTabbox();
		if (!tabbox) return 'z-tabpanels';

		var mold = tabbox.getMold();
		return 'z-tabpanels' + (mold == 'default' ? (tabbox.isVertical() ? '-ver': '') : '-' + mold);
	},
	setWidth: function (val) {
		var n = this.$n(),
			tabbox = this.getTabbox(),
			isVer = n && tabbox ? tabbox.isVertical() : false;
		if (isVer && !this.__width)
			n.style.width = '';

		this.$supers('setWidth', arguments);
		
		if (isVer) {
			if (n.style.width)
				this.__width = n.style.width;
				
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

		if (isVer) {
			if (n.style.width)
				this.__width = n.style.width;
				
			zWatch.fireDown('beforeSize', this);
			zWatch.fireDown('onSize', this);
		}
	},
	bind_: function () {
		this.$supers(zul.tab.Tabpanels, 'bind_', arguments);
		if (this.getTabbox().isVertical()) {
			this._zwatched = true;
			zWatch.listen({onSize: this, beforeSize: this, onShow: this});			
			var n = this.$n();
			if (n.style.width)
				this.__width = n.style.width;
		}
	},
	unbind_: function () {
		if (this._zwatched) {
			zWatch.unlisten({onSize: this, beforeSize: this, onShow: this});
			this._zwatched = false;
		}
		this.$supers(zul.tab.Tabpanels, 'unbind_', arguments);
	},
	onSize: _zkf = function () {
		var parent = this.parent.$n();
		if (this.__width || !zk(parent).isRealVisible())
			return;

		var width = parent.offsetWidth,
			n = this.$n();

		width -= jq(parent).find('>div:first')[0].offsetWidth
				+ jq(n).prev()[0].offsetWidth;

		n.style.width = jq.px0(zk(n).revisedWidth(width));
	},
	onShow: _zkf,
	beforeSize: function () {
		this.$n().style.width = this.__width || '';
	}
});