/* treeitem.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:31:58     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	if (this.treerow) this.treerow.redraw(out);
	if (this.treechildren) this.treechildren.redraw(out);
}
