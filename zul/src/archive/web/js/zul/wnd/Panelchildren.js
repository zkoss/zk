/* Panelchildren.js

	Purpose:
		
	Description:
		
	History:
		Mon Jan 12 18:31:03     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wnd.Panelchildren = zk.$extends(zul.Widget, {
	setHeight: zk.$void,      // readonly
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