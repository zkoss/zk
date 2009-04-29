/* Widget.js

	Purpose:
		
	Description:
		
	History:
		Sun Jan  4 11:03:40     2009, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
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
				zDom.setStyles(n, zDom.parseStyle(val));
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
	domChange_: function (devt, timeout) {
		var n = this.getNode();
		if (n) {
			var val = n.value;
			if (val != n.defaultValue) {
				this.defaultValue = val;
				this.fire('onChange', this._onChangeData(val), null,
					timeout ? timeout: 150);
			}
		}
	},
	_onChangeData: function (val, selbak) {
		return {value: val,
			start: zDom.getSelectionRange(this.getNode())[0],
			marshal: this._onChangeMarshal}
	},
	_onChangeMarshal: function () {
		return [this.value, false, this.start];
	},
	doClick_: function (wevt) {
		var n = this.getNode();
		if (zDom.tag(n) != 'INPUT')
			this.$supers('doClick_', arguments);
		else if (!n.disabled) {
			if (n.type == 'checkbox' || n.type == 'radio')
				this._doCheck();
				//continue to fire onClick_ for backward compatibility
			this.fireX(wevt); //no propagation
		}
	},
	_doCheck: function (timeout) {
		var n = this.getNode();
		if (n) {
			var val = n.checked;
			if (val != n.defaultChecked) { //changed
				n.defaultChecked = val;
				this.fire('onCheck', val, timeout);
			}
		}
	},
	bind_: function () {
		this.$supers('bind_', arguments);
		if (this.isListen('onChange', true)) {
			this.domChange_(null, -1);
			this.domListen_(this.getNode(), 'change');
		}
		if (this.isListen('onCheck', true))
			this._doCheck(-1);
	},
	unbind_: function () {
		this.domUnlisten_(this.getNode(), 'change');
		this.$supers('unbind_', arguments);
	}
});
