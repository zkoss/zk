/* Panelchildren.js

	Purpose:
		
	Description:
		
	History:
		Mon Jan 12 18:31:03     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Panelchildren is used for {@link zul.wnd.Panel} component to manage each
 * child who will be shown in the body of Panel.
 * Note that the size of Panelchildren is automatically calculated by Panel so both
 * {@link #setWidth(String)} and {@link #setHeight(String)} are read-only.
 * 
 * <p>Default {@link #getZclass}: z-panel-children.
 */
zul.wnd.Panelchildren = zk.$extends(zul.Widget, {
	/**
	 * This method is unsupported. Please use {@link zul.wnd.Panel#setHeight(String)} instead.
	 * @param String height
	 */
	setHeight: zk.$void,      // readonly
	/**
	 * This method is unsupported. Please use {@link zul.wnd.Panel#setWidth(String)} instead.
	 * @param String width
	 */
	setWidth: zk.$void,       // readonly

	// super
	getZclass: function () {
		return this._zclass == null ?  "z-panel-children" : this._zclass;
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var zcls = this.getZclass();
			var added = !this.parent.getTitle() && !this.parent.caption ?
				zcls + '-noheader' : '';				
			if (added) scls += (scls ? ' ': '') + added;
			added = this.parent.getBorder() == 'normal' ? '' : zcls + '-noborder';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	updateDomStyle_: function () {
		this.$supers('updateDomStyle_', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', this.parent);
			zWatch.fireDown('onSize', this.parent);
		}
	}
});