/* au.js

{{IS_NOTE
	Purpose:
		ZK Client Engine
	Description:
		
	History:
		Mon Sep 29 17:17:37     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
var _zws = [], _zw; //used to load widget
zkau = { //static methods
	/** Begins the creation of a widget.
	 * After called, a global variable called _zw refers to the widget.
	 */
	begin: function (widget, props) {
		if (_zws.length)
			_zws[0].appendChild(widget);
		_zws.unshift(_zw = widget);

		for (var p in props) {
			var m = widget['set' + p.charAt(0).toUpperCase() + p.substring(1)];
			if (m) m(props[p]);
			else widget[p] = props[p];
		}
	},
	/** Ends the creation of a widget. */
	end: function () {
		_zw = null;
		var w = _zws.shift();
		if (!_zws.length) w.attach(w.id);
	}
};
