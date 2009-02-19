/* grid.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:24:01     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		innerWidth = this.getInnerWidth(),
		width = innerWidth == '100%' ? ' width="100%"' : '',
		width1 =  innerWidth != '100%' ? 'width:' + innerWidth : '',
		inPaging = this._inPagingMold();
	out.push('<div', this.domAttrs_(), (this.getAlign() ? ' align="' + this.getAlign() + '"' : ''), '>');
	
	if (inPaging && this.paging
			&& (this.getPagingPosition() == 'top' || this.getPagingPosition() == 'both')) {
		out.push('<div id="', uuid, '$pgit" class="', zcls, '-pgi-t">');
		this.paging.redraw(out);
		out.push('</div>');
	}
	
	if (this.columns) {
		out.push('<div id="', uuid, '$head" class="', zcls, '-header">',
				'<table', width, zUtl.cellps0,
				' style="table-layout:fixed;', width1,'">');
		this.domFaker_(out, '$hdfaker', zcls);
		
		for (var hds = this.getHeads(), w = hds.shift(); w; w = hds.shift())
			w.redraw(out);
	
		out.push('</table></div>');
	}
	out.push('<div id="', uuid, '$body" class="', zcls, '-body"');
	
	var hgh = this.getHeight();
	if (hgh) out.push(' style="height:', hgh, '"');
	
	out.push('><table', width, zUtl.cellps0);
	
	if (this.isFixedLayout())
		out.push(' style="table-layout:fixed;', width1,'"');
		
	out.push('>');
	
	if (this.columns)
		this.domFaker_(out, '$bdfaker', zcls);
	
	if (this.rows) this.rows.redraw(out);
	
	out.push('</table></div>');
	
	if (this.foot) {
		out.push('<div id="', uuid, '$foot" class="', zcls, '-footer">',
				'<table', width, zUtl.cellps0, ' style="table-layout:fixed;', width1,'">');
		if (this.columns) 
			this.domFaker_(out, '$ftfaker', zcls);
			
		this.foot.redraw(out);
		out.push('</table></div>');
	}
	if (inPaging && this.paging
			&& (this.getPagingPosition() == 'bottom' || this.getPagingPosition() == 'both')) {
		out.push('<div id="', uuid, '$pgib" class="', zcls, '-pgi-b">');
		this.paging.redraw(out);
		out.push('</div>');
	}
	out.push('</div>');
}
