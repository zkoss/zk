/* Comboitem.js

	Purpose:
		
	Description:
		
	History:
		Sun Mar 29 20:53:45     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.inp.Comboitem = zk.$extends(zul.LabelImageWidget, {
	$define: {
		disabled: function (v) {
			var n = this.getNode();
			if (n) {
				var zcls = this.getZclass() + '-disd';
				v ? jq(n).addClass(zcls): jq(n).removeClass(zcls);
			}
		},
		description: _zkf = function () {
			this.rerender();
		},
		content: _zkf
	},

	//super
	doMouseOver_: function () {
		if (!this._disabled) {
			var n = this.getNode(),
				$n = jq(n),
				zcls = this.getZclass();
			$n.addClass($n.hasClass(zcls + '-seld') ?
				zcls + "-over-seld": zcls + "-over");
		}
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (!this._disabled) this._doMouseOut();
		this.$supers('doMouseOut_', arguments);
	},
	_doMouseOut: function () {
		var n = this.getNode(),
			zcls = this.getZclass();
		jq(n).removeClass(zcls + '-over')
			.removeClass(zcls + '-over-seld');
	},

	doClick_: function (evt) {
		if (!this._disabled) {
			this._doMouseOut();

			this.parent._select(this, {sendOnSelect:true});
			this.parent.close({sendOnOpen:true});
			evt.stop();
		}
	},

	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (this._disabled && (!no || !no.zclass)) {
			var zcls = this.getZclass();
			scls += ' ' + zcls + '-disd';
		}
		return scls;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-comboitem";
	}
});
