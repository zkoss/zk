/* Datebox.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:34 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zPkg.load('zul.inp');

zul.db.Datebox = zk.$extends(zul.inp.FormatWidget, {
	_compact: false,
	_buttonVisible: true,
	_lenient: true,
	$define: {
		buttonVisible: function () {
			var n = this.getSubnode('btn');
			if (n)
				v ? jq(n).show(): jq(n).hide();
		}
	},
	onSize: _zkf = function () {
		this._auxb.fixpos();
	},
	onShow: _zkf,
	onFloatUp: function (wgt) {
		if (!zUtl.isAncestor(this, wgt))
			this.close({sendOnOpen:true});
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-datebox";
	},
	getDateFormat: function () {
		
	},
	setConstraint : function () {
		
	},
	open: function (opts) {
		this.$supers('open', arguments);		
	},
	close: function (opts) {
	
	},
	coerceFromString_: function (value) {
		/*if (!value) return null;

		var info = zNumFormat.unformat(this._format, value),
			val = parseInt(info.raw);
		if (info.raw != ''+val)
			return {error: zMsgFormat.format(msgzul.INTEGER_REQUIRED, value)};

		if (info.divscale) val = Math.round(val / Math.pow(10, info.divscale));
		return val;*/
	},
	coerceToString_: function (value) {
		/*var fmt = this._format;
		return fmt ? zNumFormat.format(fmt, value): value ? ''+value: '';*/
	},
	getInputNode_: function () {
		return this.getSubnode('real');
	},
	bind_: function (){
		this.$supers('bind_', arguments);
		var btn = this.getSubnode('btn'),
			inp = this.getInputNode_(); 
		if (btn) {
			this._auxb = new zul.Auxbutton(this, btn, inp);
			this.domListen_(btn, 'onClick', '_doBtnClick');
		}
		zWatch.listen({onSize: this, onShow: this, onFloatUp: this});
	},
	unbind_: function () {	
		var btn = this.getSubnode('btn');
		if (btn) {
			this._auxb.cleanup();
			this._auxb = null;
			this.domUnlisten_(btn, 'onClick', '_doBtnClick');
		}
		zWatch.unlisten({onSize: this, onShow: this, onFloatUp: this});
		this.$supers('unbind_', arguments);
	},
	_doBtnClick: function (evt) {
		evt.stop();
	}
});
