/* Input.js

	Purpose:
		
	Description:
		
	History:
		Fri Aug 21 15:06:06 CST 2015, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
(function () {

zhtml.Input = zk.$extends(zhtml.Widget, {
	_doChange: function (evt) {
		var n = this.$n();
		if (n) {
			var val = n.value;
			if (val != this._defValue) {
				this._defValue = val;
				this.fire('onChange', this._onChangeData(val), null);
			}
		}
	},
	_onChangeData: function (val, selbak) {
		return {value: val,
			start: zk(this.$n()).getSelectionRange()[0],
			marshal: this._onChangeMarshal};
	},
	_onChangeMarshal: function () {
		return [this.value, false, this.start];
	},
	bind_: function () {
		this.$supers(zhtml.Input, 'bind_', arguments);
		var n;

		if (this.isListen('onChange', {any: true}) && (n = this.$n())) {
			this._defValue = n.value;
			this.domListen_(n, 'onChange');
		}
		if (this.isListen('onCheck', {any: true}) && (n = this.$n())) {
			this._defChecked = n.checked;
			this.domListen_(n, 'onCheck');
		}
	},
	unbind_: function () {
		if (this._defValue !== undefined)
			this.domUnlisten_(this.$n(), 'onChange');
		this.$supers(zhtml.Input, 'unbind_', arguments);
	}
});

})();