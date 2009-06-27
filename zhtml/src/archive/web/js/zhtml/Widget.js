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
				if ('true' == val) jq(n).show();
				else jq(n).hide();
				break;
			case 'checked':
				n.checked = n.defaultChecked = 'true' == val;
				break;
			case 'value':
				n.value = n.defaultValue = val;
				break;
			case 'style':
				zk(n).setStyles(jq.parseStyle(val));
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
	_doChange: function (evt, timeout) {
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
			start: zk(this.getNode()).getSelectionRange()[0],
			marshal: this._onChangeMarshal}
	},
	_onChangeMarshal: function () {
		return [this.value, false, this.start];
	},
	doClick_: function (wevt) {
		var n = this.getNode();
		if (n.tagName != 'INPUT')
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
		if (this.isListen('onChange', {any:true})) {
			this._doChange(null, -1);
			this.domListen_(this.getNode(), 'onChange');
		}
		if (this.isListen('onCheck', {any:true}))
			this._doCheck(-1);
	},
	unbind_: function () {
		this.domUnlisten_(this.getNode(), 'onChange');
		this.$supers('unbind_', arguments);
	}
});
