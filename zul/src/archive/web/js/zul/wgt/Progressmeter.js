/* Progressmeter.js

	Purpose:
		
	Description:
		
	History:
		Thu May 14 10:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A progress meter is a bar that indicates how much of a task has been completed. 
 *
 * <p>Default {@link #getZclass}: z-progressmeter.
 */
zul.wgt.Progressmeter = zk.$extends(zul.Widget, {
	_value: 0,

	$define: {
		/** Returns the current value of the progress meter.
		 * @return int
		 */
		/** Sets the current value of the progress meter.
		 * <p>Range: 0~100.
		 * @param int value
		 */
		value: function () {
			if(this.$n()) 
				this._fixImgWidth();
		}
	},

	//super//
	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: "z-progressmeter";
	},
	_fixImgWidth: _zkf = function() {
		var n = this.$n(), 
			img = this.$n("img");
		if (img) {
			if (zk.ie6_) img.style.width = ""; //Bug 1899749
			img.style.width = Math.round((n.clientWidth * this._value) / 100) + "px";
		}
	},
	onSize: _zkf,
	onShow: _zkf,
	bind_: function () {//after compose
		this.$supers('bind_', arguments); 
		this._fixImgWidth(this._value);
		zWatch.listen({onSize: this, onShow: this});
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this, onShow: this});
		this.$supers('unbind_', arguments);
	},
	setWidth : function (val){
		this.$supers('setWidth', arguments);
		this._fixImgWidth();
	}
});

