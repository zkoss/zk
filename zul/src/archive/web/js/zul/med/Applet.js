/* Applet.js

	Purpose:
		
	Description:
		
	History:
		Wed Mar 25 17:11:55     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.med.Applet = zk.$extends(zul.Widget, {
	$init: function() {
		this._params = {};
	},

	getCode: function () {
		return this._code;
	},
	setCode: function (code) {
		if (this._code != code) {
			this._code = code;
			var n = this.getNode();
			if (n) n.code = code || '';
		}
	},

	/** Sets the param. Notice that it is meaningful only if it is called
	 * before redraw.
	 */
	setParam: function (inf) {
		var nm = inf[0], val = inf.length >= 2 ? inf[1]: null;
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
});