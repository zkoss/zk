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
var _zws = []; //used to load widget
zkau = { //static methods
	/** Begins the creation of a widget.
	 */
	begin: function (type, uuid, mold, props, embedAs) {
		if (embedAs) {
			var embed = zkDOM.$(uuid).firstChild;
			if (embed && zkDOM.$(embed) == "SPAN")
				props[embedAs] = embed.innerHTML;
			else if (zk.debugJS)
				throw "No embedAs, "+embedAs;
		}

		var wgt = new (zk.$import(type))(uuid, mold);
		wgt.inServer = true;
		if (_zws.length)
			_zws[0].appendChild(wgt);
		_zws.unshift(wgt);

		for (var p in props) {
			var m = wgt['set' + p.charAt(0).toUpperCase() + p.substring(1)];
			if (m) m(props[p]);
			else wgt[p] = props[p];
		}
	},
	/** Ends the creation of a widget. */
	end: function () {
		var wgt = _zws.shift();
		if (!_zws.length) wgt.attach(wgt.uuid);
	}
};
