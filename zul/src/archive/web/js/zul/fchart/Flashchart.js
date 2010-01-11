/* Flashchart.js

	Purpose:

	Description:

	History:
		Nov 26, 2009 11:58:42 AM , Created by joy

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.

*/
(function () {
	var _src = zk.ajaxURI('/web/js/zul/fchart/charts.swf', {au: true}),
		_expressInstall = zk.ajaxURI('/web/js/zul/fchart/expressinstall.swf', {au: true}),
		_axis = {
			stackingEnabled: true,
			type: "numeric",
			alwaysShowZero: true,
			hideOverlappinLabels: true,
			orientation: "horizontal",
			reverse: false,
			scale: "linear",
			snapToUnits: true
		};
		
	function JSONEncode(x) {
		switch (typeof x) {
			case "number":
				return String(x);
			case "boolean":
				return String(x);
			case "string":
				return '"' + x + '"';
			default:
				return "null";
		}
	}
	
	/* A method use to format String to JSON
	 * example:
	 * 		'legend-display=right,legend-padding=10' will be transfer to '{"legend":{"display":"right","padding":"10"}}' 
	 */
	function formatToJSON(str) {
		var list = str.split(','), 
			len = list.length, 
			categorys = [], 
			result = ['{'], 
			cl;
		
		for (var j = 0; j < len; j++) {
			var temp = list[j].split('-'),
				category = !temp[2] ? temp[0] : temp[1],
				alreadyHaveCategory = false,
				attr, val;
			if (!temp[1]) {
				result.push("}");
				break;
			}
			if (!temp[2]) {
				attr = temp[1].split('=')[0],
				val = temp[1].split('=')[1];
			} else {
				attr = temp[2].split('=')[0],
				val = temp[2].split('=')[1];
			}			
			cl = categorys.length;
			if (cl > 0) {
				for (var i = 0; i < cl; i++) {
					if (categorys[i].match(category)) {
						alreadyHaveCategory = true;
						break;
					}
				}
			}
			if (!alreadyHaveCategory) {
				categorys.push(JSONEncode(category));					
				if (!temp[2]) {
					result.push(JSONEncode(category), ":");
				} else {
					result.push(",", JSONEncode(category), ":");
				}
				result.push("{", JSONEncode(attr), ":", JSONEncode(val));
			} else {
				result.push(",", JSONEncode(attr), ":", JSONEncode(val));
			} 
		}
		cl = categorys.length;
		for (var j = 0; j < cl; j++) {
			result.push('}');
		}
		result.push('}');
		return result.join("");		
	}
	
	/* If e.type == swfReady, then init chart data. */
	function onFlashEvent(wgt, event) {
		var swf = wgt.$n('chart'),
			type = wgt._type,
			chartStyle = wgt._chartStyle,
			data = jq.evalJSON(wgt._jsonModel),
			dataProvider = [];
		
		swf.setType(type);
		if (!chartStyle) {	
			if (type != "bar" && type != "line" && type != "column") {
				swf.setStyles(formatToJSON(wgt._defaultStyle));
			}
		} else {
			swf.setStyles(formatToJSON(chartStyle));
		}	
		if (type == "pie") {
			dataProvider = [{type: type, dataProvider: data}];			
			swf.setCategoryField("categoryField");
			swf.setDataField("dataField");			
		} else if (type == "stackbar") {
			var series = jq.evalJSON(wgt._jsonSeries),
				dataProvider = seriesProvider(series, type, data, dataProvider);
			swf.setHorizontalAxis(_axis);
			swf.setVerticalField("verticalField");
		} else if (type == "stackcolumn") {
			var series = jq.evalJSON(wgt._jsonSeries),
				dataProvider = seriesProvider(series, type, data, dataProvider);
			swf.setHorizontalField("horizontalField");
			swf.setVerticalAxis(_axis);
		} else {
			dataProvider = [{type: type, dataProvider: data}];
			swf.setHorizontalField("horizontalField");
			swf.setVerticalField("verticalField");
		}
		swf.setDataProvider(dataProvider);
	}

	/* Refresh the data of chart */
	function refresh(wgt, dataModel) {
		var swf = wgt.$n('chart'),
			type = wgt._type,
			data = jq.evalJSON(dataModel),
			series = jq.evalJSON(wgt._jsonSeries),
			dataProvider = [];

		if (type == "stackbar" || type == "stackcolumn")
			dataProvider = seriesProvider(series, type, data, dataProvider);
		else
			dataProvider = seriesProvider(false, type, data, dataProvider);
		
		swf.setDataProvider(dataProvider);
	}

	function seriesProvider(series, type, data, dataProvider) {
		var seriesLength = series.length,
			current = null;

		if (series) {
			for (var i = 0; i < seriesLength; i++) {
				current = series[i];
		  		var clone = {};
		  		for (var key in current) {
		  			if (key == "style" && current.style !== null) {
		  				clone.style = formatToJSON(current.style);
		  			} else {
		  				clone[key] = current[key];
		  			}
		  		}
		  		dataProvider.push(clone);
		  		current = clone;
				if (!current.type)
					current.type = type;
				current.dataProvider = data;
		  	}
		} else {
	  		dataProvider.push({type: type, dataProvider: data});
	  	}
		return dataProvider;
	}

/**
 * The generic flash chart component. Developers set proper chart type, data model,
 * and the src attribute to draw proper chart. The model and type must match to each other;
 * or the result is unpredictable.
 *
 * <table>
 *   <tr><th>type</th></tr>
 *   <tr><td>pie</td></tr>
 *   <tr><td>bar</td></tr>
 *   <tr><td>line</td></tr>
 *   <tr><td>column</td></tr>
 *   <tr><td>stackbar</td></tr>
 *   <tr><td>stackcolumn</td></tr>
 * </table>
 *
 * <p>Default {@link #getWidth}: 400px
 * <p>Default {@link #getHeight}: 200px
 *
 */
zul.fchart.Flashchart = zk.$extends(zul.med.Flash, {

	/* Default values */
	_width: "400px",
	_height: "200px",
	_defaultStyle: "legend-display=right,legend-padding=10,legend-spacing=5,legend-font-family=Arial,legend-font-size=13",

	$define: {
		/** Sets the content style of flashchart.
		 * <p>Default format: "Category-Attribute=Value", ex."legend-display=right"
		 * @param String chartStyle
		 */
		/** Returns the content style.
		 * @return String
		 */
		chartStyle: null,
		/** Sets a catenation of a list of model of chart, 
		 * separated by comma.
		 * <p>Only implement models which matched the allowed types
		 * @param String jsonModel
		 */
		/** Returns a catenation of a list of the model's attribute of chart, 
		 * separated by comma.
		 * @return String
		 */
		jsonModel: null,
		/** Sets a catenation of a list of the model's series of chart, 
		 * separated by comma.
		 * <p>Only implement models which matched the allowed types
		 * @param String jsonModel
		 */
		/** Returns a catenation of a list of the model's series of chart, 
		 * separated by comma.
		 * @return String
		 */
		jsonSeries: null,
		/** Sets the type of chart.
		 * <p>Default: "pie"
		 * <p>Allowed Types: pie, line, bar, column, stackbar, stackcolumn
		 * @param String type
		 */
		/** Returns the type of chart
		 * @return String
		 */
		type: null
	},

	setRefresh: function (mod) {
		refresh(this, mod);
	},

	bind_: function (desktop, skipper, after) {
		this.$supers('bind_', arguments);
		var _swfId = this.uuid + "-chart",
			_flashvars = "allowedDomain=localhost&elementID=" + _swfId + "&eventHandler=zul.fchart.Flashchart.onEvent",
			_params = {				
				flashvars: _flashvars,
				allowScriptAccess: "always",
				bgcolor: "#ffffff",
				wmode: "opaque"
			},
			_attributes = {id: _swfId, classid: "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000"};

		if (zk.ie || zk.opera)		//Add a cache buster at the end of src url for IE and Opera
			_src = _src + (_src.indexOf('?') === -1 ? '?' : '&') + "_cache=" + new Date().getTime();
		zul.fchart.swfobject.embedSWF(_src, _swfId, this._width, this._height, "9.0.0", _expressInstall, false, _params, _attributes);
		
	}
}, {// static
	onEvent: function (id, event) {
		var eventType = event.type;
		if (eventType == "swfReady") {
			var comp = zk.Widget.$(id);
			if (comp)
				onFlashEvent(comp, event);
		}
	}
});
})();