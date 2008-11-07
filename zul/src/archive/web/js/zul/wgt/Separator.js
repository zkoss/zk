/* Separator.js

	Purpose:
		
	Description:
		
	History:
		Wed Nov  5 16:58:56     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Separator = zk.$extends(zul.Widget, {
	/** Returns the zclass. */
	getZclass: function () {
		var zs = this.zclass;
		return zs ? zs: "z-separator" +
			(this.vertical ? "-ver" + (this.bar ? "-bar" : "") :
				"-hor" + (this.bar ? "-bar" : ""))
	},
	setAttr: function (nm, val) {
		this.$super('setAttr', nm, val);

		var n = this.node;
		if (n) n.className = this.getRealClass();
	}
});
