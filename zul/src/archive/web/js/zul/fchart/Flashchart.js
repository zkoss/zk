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
	    _expressInstall = zk.ajaxURI('/web/js/zul/fchart/expressinstall.swf', {au: true});
	    _Axis = {
			stackingEnabled: true,
	    	type: "numeric",
	    	alwaysShowZero: true,
	    	hideOverlappinLabels: true,
	    	orientation: "horizontal",
	    	reverse: false,
	    	scale: "linear",
	    	snapToUnits: true
	    };
	
    function getDefaultStyle(){
    	return "legend-display=right,legend-padding=10,legend-spacing=5,legend-font-family=Arial,legend-font-size=13";    
    }
    
    function JSONEncode(x){
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
    function formatToJSON(str){
    	var list = str.split(','), categorys = [], result = ['{'];		
    	for (var key in list) {
    		var temp = list[key].toString().split('-'),
    			category = typeof temp[2] == 'undefined' ? temp[0] : temp[1],
    			alreadyHaveCategory = false;
    		
			if (typeof temp[1] == 'undefined') {
    			result.push("}");
    			break;
    		}
    		if (typeof temp[2] == 'undefined') {
	    		var attr = temp[1].split('=')[0],
	    		    val = temp[1].split('=')[1];
    		} else {
    			var attr = temp[2].split('=')[0],
    		    	val = temp[2].split('=')[1];
    		}
    		if(categorys.length > 0){
	    		for(var i = 0; i < categorys.length; i++){
	    			if(categorys[i].match(category)){
	    				alreadyHaveCategory = true;
	        			break;
	    			}
	    		}
    		}
    		if (alreadyHaveCategory == false) {
    			categorys.push(JSONEncode(category));	    			
    			if (typeof temp[2] == 'undefined')  				
	    			result.push(JSONEncode(category), ":");		    			
    			else
    				result.push(",", JSONEncode(category), ":");
    			
    			result.push("{", JSONEncode(attr), ":", JSONEncode(val));
    		} else {
    			result.push(",", JSONEncode(attr), ":", JSONEncode(val));
    		} 
    	}
		for (var i = 0; i < categorys.length; i++) {
			result.push('}');
		}
    	return result.join("");    	
	}
    
	/* If e.type == swfReady, then init chart data. */
	function onFlashEvent(wgt, event) {
		var _swf = wgt.$n('chart'),
			_type = wgt._type,
			_chartStyle = wgt._chartStyle,
			_data = jq.evalJSON(wgt._jsonModel),
			_dataProvider = [];
		
		_swf.setType(_type);
		if (typeof _chartStyle == 'undefined') {	
			if(_type != "bar" && _type != "line" && _type != "column") {
				_swf.setStyles(formatToJSON(getDefaultStyle()));
			}
		} else {
			_swf.setStyles(formatToJSON(_chartStyle));
		}	
		if (_type == "pie") {
			_dataProvider = [{type: _type, dataProvider: _data}];			
			_swf.setCategoryField("categoryField");
			_swf.setDataField("dataField");			
		} else if (_type == "stackbar") {
			var _series = jq.evalJSON(wgt._jsonSeries),
				_dataProvider = _seriesProvider(_series, _type, _data, _dataProvider);
			_swf.setHorizontalAxis(_Axis);
			_swf.setVerticalField("verticalField");
		} else if (_type == "stackcolumn") {
			var _series = jq.evalJSON(wgt._jsonSeries),
				_dataProvider = _seriesProvider(_series, _type, _data, _dataProvider);
			_swf.setHorizontalField("horizontalField");
		    _swf.setVerticalAxis(_Axis);
		} else {
			_dataProvider = [{type: _type, dataProvider: _data}];
			_swf.setHorizontalField("horizontalField");
			_swf.setVerticalField("verticalField");
		}
		_swf.setDataProvider(_dataProvider);
	}

	/* Refresh the data of chart */
	function _refresh(wgt, dataModel) {
		var _swf = wgt.$n('chart'),
			_type = wgt._type,
			_data = jq.evalJSON(dataModel),
			_series = jq.evalJSON(wgt._jsonSeries),
			_dataProvider = [];

		if(_type == "stackbar" || _type == "stackcolumn")
			_dataProvider = _seriesProvider(_series, _type, _data, _dataProvider);
		else
			_dataProvider = [{type: _type, dataProvider: _data}];
		
		_swf.setDataProvider(_dataProvider);
	};

	function _seriesProvider(series, type, data, dataProvider){
		var _seriesLength = series.length,
			_current = null;

		if(series){
			for(var i = 0; i < _seriesLength; i++){
				_current = series[i];
		  		var _clone = {};
		  		for(var key in _current){
		  			_clone[key] = _current[key];
		  		}
		  		dataProvider.push(_clone);
		  		_current = _clone;
			    if(!_current.type)
			    	_current.type = type;
			    _current.dataProvider = data;
		  	}
		} else {
	  		dataProvider.push({type: type, dataProvider: data});
	  	}
		return dataProvider;
	}

zul.fchart.Flashchart = zk.$extends(zul.med.Flash, {

	/* Default values */
	_width: "400px",
	_height: "200px",	

	$define: {
		chartStyle: null,
		width: null,
		height: null,
		jsonModel: null,
		jsonSeries: null,
		type: null
	},

	setRefresh: function (mod) {
		_refresh(this, mod);
	},

	bind_: function (desktop, skipper, after) {
		this.$supers('bind_', arguments);
		var _swfId = this.uuid + "-chart",
			_flashvars = "allowedDomain=localhost&elementID=" + _swfId + "&eventHandler=zul.fchart.Flashchart.onEvent",
			_params = {
				flashvars: _flashvars,
				allowScriptAccess: "always"
			},
			_attributes = {id: _swfId};

		zul.fchart.swfobject.embedSWF(_src, _swfId, this._width, this._height, "9.0.0", _expressInstall, false, _params, _attributes);
	}
}, {// static
	onEvent: function (id, event) {
		var eventType = event.type;
		if (eventType == "swfReady") {
			var comp = zk.Widget.$(id);
			if(comp)
				onFlashEvent(comp, event);
		}
	}
});
})();