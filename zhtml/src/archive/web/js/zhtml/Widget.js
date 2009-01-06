/* Widget.js

	Purpose:
		
	Description:
		
	History:
		Sun Jan  4 11:03:40     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zhtml.Widget = zk.$extends(zk.Native, {
	setDynamicProperty: function (prop) {
		var n = this.getNode(), nm = prop[0], val = prop[1];
		if (n)
			switch (nm) {
			case 'visibility':
				if ('true' == val) zDom.show(n);
				else zDom.hide(n);
				break;
			case 'checked':
				n.checked = n.defaultChecked = 'true' == val;
				break;
			case 'value':
				n.value = n.defaultValue = val;
				break;
			case 'style':
				zDom.setStyle(n, zDom.parseStyle(val));
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
	},
	doChange_: function (devt) {
		var n = this.getNode(),
			val = n.value;
		if (val != n.defaultValue)
			this.defaultValue = val;
		this.fire('onChange', this._onChangeData(val), null, 150);
	},
	_onChangeData: function (val, selbak) {
		return {value: val,
			start: zDom.selectionRange(this.getNode())[0],
			marshal: this._onChangeMarshal}
	},
	_onChangeMarshal: function () {
		return [this.value, false, this.start];
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.$onChange)
			zEvt.listen(this.getNode(), 'change', this.proxy(this.doChange_, '_pxChange'));
	},
	unbind_: function () {
		if (this._pxChange) {
			zEvt.unlisten(this.getNode(), 'change', this._pxChange);
			this._pxChange = null;
		}
		this.$supers('unbind_', arguments);
	}
});
