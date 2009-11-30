/* Flashchart.js

    Purpose:

	Description:

	History:
		Nov 26, 2009 11:58:42 AM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.

*/
zul.fchart.Flashchart = zk.$extends(zul.med.Flash, {

	/* Default values */
	_version: "9.0.0",
	_width: "400",
	_height: "200",
	_src: zk.ajaxURI('/web/js/zul/fchart/charts.swf', {desktop: this.desktop, au: true}),
	_allowScriptAccess: "always",

	$define: {
		width: null,
		height: null,
		jsonModel: null,
		type: null
	},
	
	/* If e.type == swfReady, then init chart data. */
	onFlashEvent: function (event) {
		var _swf = this.$n('chart'), 
			_type = this._type,
			_data = jq.evalJSON(this._jsonModel),
			_dataProvider = [{type: _type, dataProvider: _data}];
		
		_swf.setType(_type);
		if (_type == "pie") {
			_swf.setCategoryField("categoryField");
			_swf.setDataField("dataField");
		} else {
			_swf.setHorizontalField("horizontalField");
			_swf.setVerticalField("verticalField");
		}
		_swf.setDataProvider(_dataProvider);
	},

	bind_: function (desktop, skipper, after) {
		this.$supers('bind_', arguments);
		var _swfId = this.uuid + "-chart",
			_flashvars = "allowedDomain=localhost&elementID=" + _swfId + "&eventHandler=zul.fchart.Flashchart.onEvent",
			_params = {
				flashvars: _flashvars,
				allowScriptAccess: this._allowScriptAccess
			},
			_attributes = {id: _swfId},
			_expressInstall = zk.ajaxURI('/web/js/zul/fchart/expressinstall.swf', {desktop: this.desktop, au: true});

		zul.fchart.swfobject.embedSWF(this._src, _swfId, this._width, this._height, this._version, _expressInstall, false, _params, _attributes);
	}
}, {// static
	onEvent: function (id, event) {
		var eventType = event.type;
		if (eventType == "swfReady") {
			var comp = zk.Widget.$(id);
			if(comp)
				comp.onFlashEvent(event);
		} else {
			setTimeout(this.onEvent(id, event), 100);
		}
	}	
});
