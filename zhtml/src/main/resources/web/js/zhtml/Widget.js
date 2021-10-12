/* Widget.js

	Purpose:
		
	Description:
		
	History:
		Sun Jan  4 11:03:40     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zhtml.Widget = zk.$extends(zk.Widget, {
	rawId: true,
	/** The class name (<code>zhtml.Widget</code>)
	 * @type String
	 */
	className: 'zhtml.Widget',
	/** The widget name (<code>zhtml</code>).
	 * @type String
	 */
	widgetName: 'zhtml',

	setDynamicProperty: function (prop) {
		var n = this.$n();
		if (n) {
			var nm = prop[0], val = prop[1];
			switch (nm) {
			case 'checked':
				n.checked = this._defChecked = val != null;
				break;
			case 'value':
				n.value = this._defValue = val;
				break;
			case 'style':
				zk(n).clearStyles().jq.css(jq.parseStyle(val));
				break;
			case 'class':
				n.className = val;
				break;
			case 'disabled':
			case 'readOnly':
				n[nm] = 'true' == val;
				break;
			default:
				n[nm] = val;
			}
		}
	},
	doClick_: function (wevt) {
		var n = this.$n();
		if (n) {
			if (n.tagName != 'INPUT')
				this.$supers('doClick_', arguments);
			else if (!n.disabled) {
				if (n.type == 'checkbox' || n.type == 'radio')
					this._doCheck();
					//continue to fire onClick_ for backward compatibility
				this.fireX(wevt); //no propagation
			}
		}
	},
	_doCheck: function () {
		var n = this.$n();
		if (n) {
			var val = n.checked;
			if (val != this._defChecked) { //changed
				this._defChecked = val;
				this.fire('onCheck', val);
			}
		}
	},
	redraw: zk.Native.$redraw
});
