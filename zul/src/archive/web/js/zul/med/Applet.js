/* Applet.js

	Purpose:
		
	Description:
		
	History:
		Wed Mar 25 17:11:55     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.def(zul.med.Applet = zk.$extends(zul.Widget, {
	$init: function() {
		this._params = {};
		this.$supers('$init', arguments);
	},

	invoke: zk.ie ? function() {
		var n = this.getNode();
		if (n && arguments.length >= 1) {
			var expr = "n." + arguments[0] + '(';
			for (var j = 1; j < arguments.length;) {
				if (j != 1) expr += ',';
				var s = arguments[j++];
				expr += '"' + (s ? s.replace('"', '\\"'): '') + '"';
			}
			try {
				eval(expr + ')');
			} catch (e) {
				zk.error("Failed to invoke applet's method: "+expr+'\n'+e.message);
			}
		}
	}: function(){
		var n = this.getNode();
		if (n && arguments.length >= 1) {
			var fn = arguments[0],
				func = n[fn];
			if (!func) {
				zk.error("Method not found: "+fn);
				return;
			}
			try {
				var args = [];
				for (var j = 1; j < arguments.length;)
					args.push(arguments[j++]);
				func.apply(n, args);
			} catch (e) {
				zk.error("Failed to invoke applet's method: "+fn+'\n'+e.message);
			}
		}
	},
	getField: function (name) {
		var n = this.getNode();
		return n ? n[name]: null;
	},
	setField: function (name, value) {
		var n = this.getNode();
		if (n)
			try {
				n[name] = value;
			} catch(e) {
				zk.error("Failed to set applet's field: "+ name+'\n'+e.message);
			}
	},

	/** Sets the param. Notice that it is meaningful only if it is called
	 * before redraw.
	 * There are two format:
	 * setParam(nm, val)
	 * and
	 * setParam([nm, val])
	 */
	setParam: function (nm, val) {
		if (arguments.length == 1) {
			val = nm[1];
			nm = nm[0];
		}
		if (val != null) this._params[nm] = val;
		else delete this._params[nm];
	},

	//super
	domAttrs_: function(no){
		return this.$supers('domAttrs_', arguments)
				+ ' code="' + (this._code || '') + '"';
	},

	_outParamHtml: function (out) {
		var params = this._params;
		for (var nm in params)
			out.push('<param name="', zUtl.encodeXML(nm), '" value="', zUtl.encodeXML(params[nm]), '"/>');
	}
}), { //zk.def
	code: function (v) {
		var n = this.getNode();
		if (n) n.code = v || '';
	}
});
