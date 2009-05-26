/* Tablechildren.js

	Purpose:
		
	Description:
		
	History:
		Thu May 19 14:17:24     2009, Created by kindalu

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zkmax.layout.Tablechildren = zk.$extends(zul.Widget, {
	_colspan: 1,
	_rowspan: 1,
	$define: {
		colspan: _zkf = function(){
			if(this.parent)
				this.parent.rerender();
		},
		rowspan: _zkf
	}
});