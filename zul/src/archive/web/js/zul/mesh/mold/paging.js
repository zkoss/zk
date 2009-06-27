/* paging.js

	Purpose:
		
	Description:	
		
	History:
		Fri Jan 23 15:00:45     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	if (this.getMold() == "os") {
		out.push('<div', this.domAttrs_(), '>', this._innerTags(), '</div>');
		return;
	}
	var uuid = this.uuid,
		zcls = this.getZclass();
	out.push('<div name="', uuid, '"', this.domAttrs_(), '>', '<table', zUtl.cellps0,
			'><tbody><tr><td><table id="', uuid, '-first" name="', uuid, '-first"',
			zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
			'<td class="', zcls, '-btn-l"><i>&#160;</i></td>',
			'<td class="', zcls, '-btn-m"><em unselectable="on">',
			'<button type="button" class="', zcls, '-first"> </button></em></td>',
			'<td class="', zcls, '-btn-r"><i>&#160;</i></td></tr></tbody></table></td>',
			'<td><table id="', uuid, '-prev" name="', uuid, '-prev"', zUtl.cellps0,
			' class="', zcls, '-btn"><tbody><tr><td class="', zcls, '-btn-l"><i>&#160;</i></td>',
			'<td class="', zcls, '-btn-m"><em unselectable="on"><button type="button" class="',
			zcls, '-prev"> </button></em></td><td class="', zcls, '-btn-r"><i>&#160;</i></td>',
			'</tr></tbody></table></td><td><span class="', zcls, '-sep"/></td>',
			'<td><span class="', zcls, '-text"></span></td><td><input id="',
			uuid, '-real" name="', uuid, '-real" type="text" class="', zcls,
			'-inp" value="', this.getActivePage() + 1, '" size="3"/></td>',
			'<td><span class="', zcls, '-text">/ ', this.getPageCount(), '</span></td>',
			'<td><span class="', zcls, '-sep"/></td><td><table id="', uuid,
			'-next" name="', uuid, '-next"', zUtl.cellps0, ' class="', zcls, '-btn">',
			'<tbody><tr><td class="', zcls, '-btn-l"><i>&#160;</i></td><td class="',
			zcls, '-btn-m"><em unselectable="on"><button type="button" class="',
			zcls, '-next"> </button></em></td><td class="', zcls, '-btn-r"><i>&#160;</i></td>',
			'</tr></tbody></table></td><td><table id="', uuid, '-last" name="',
			uuid, '-last"', zUtl.cellps0, ' class="', zcls, '-btn"><tbody><tr>',
			'<td class="', zcls, '-btn-l"><i>&#160;</i></td><td class="', zcls,
			'-btn-m"><em unselectable="on"><button type="button" class="', zcls,
			'-last"> </button></em></td><td class="', zcls, '-btn-r"><i>&#160;</i></td>',
			'</tr></tbody></table></td></tr></tbody></table>');
			
	if (this.isDetailed()) out.push(this._infoTags());
	out.push('</div>');
}
