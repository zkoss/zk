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

	/* If e.type == swfReady, then init chart data. */
	function onFlashEvent(wgt, event) {
		var _swf = wgt.$n('chart'),
			_type = wgt._type,
			_data = jq.evalJSON(wgt._jsonModel),
			_dataProvider = [];
		zk.log(_type);
		_swf.setType(_type);
		if (_type == "pie") {
			_dataProvider = [{type: _type, dataProvider: _data}];
			_swf.setCategoryField("categoryField");
			_swf.setDataField("dataField");
		} else if (_type == "stackbar") {			
			_dataProvider = _seriesProvider(wgt, _type, _data, _dataProvider);
			_swf.setHorizontalAxis(_Axis);
			_swf.setVerticalField("verticalField");
		} else if (_type == "stackcolumn") {
			_dataProvider = _seriesProvider(wgt, _type, _data, _dataProvider);
			_swf.setHorizontalField("horizontalField");
		    _swf.setVerticalAxis(_Axis);
		} else {
			_dataProvider = [{type: _type, dataProvider: _data}];
			_swf.setHorizontalField("horizontalField");
			_swf.setVerticalField("verticalField");
		}
		_swf.setDataProvider(_dataProvider);
	};

	/* Refresh the data of chart */
	function _refresh(wgt, dataModel) {
		var _swf = wgt.$n('chart'),
			_type = wgt._type,
			_data = jq.evalJSON(dataModel),
			_dataProvider = [];
		
		if(_type == "stackbar" || _type == "stackcolumn"){
			_dataProvider = _seriesProvider(wgt, _type, _data, _dataProvider);			
		} else {
			_dataProvider = [{type: _type, dataProvider: _data}];
		}
		_swf.setDataProvider(_dataProvider);
	};
	
	function _seriesProvider(wgt, type, data, dataProvider){
		var _series = jq.evalJSON(wgt._jsonSeries),
			_seriesCount = 0,
			_currentSeries = null;
	
		if(_series){
			_seriesCount = _series.length;
			for(var i = 0; i < _seriesCount; i++){
				_currentSeries = _series[i];
		  		var _clonedSeries = {};
		  		for(var prop in _currentSeries){
		  			_clonedSeries[prop] = _currentSeries[prop];
		  		}
		  		dataProvider.push(_clonedSeries);
		  	}
		}
	
		if(_seriesCount > 0){
		    for(var i = 0; i < _seriesCount; i++){
			    _currentSeries = dataProvider[i];
			    if(!_currentSeries.type){
			       _currentSeries.type = type;
			    }
			    _currentSeries.dataProvider = data;
		    }
	  	} else {
	  		dataProvider.push({type: type, dataProvider: data});
	  	}
		return dataProvider;
	};

zul.fchart.Flashchart = zk.$extends(zul.med.Flash, {

	/* Default values */
	_width: "400px",
	_height: "200px",

	$define: {
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