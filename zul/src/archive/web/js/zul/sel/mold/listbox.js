/* listbox.js

	Purpose:
		
	Description:
		
	History:
		Mon May  4 15:34:02     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var uuid = this.uuid,
		zcls = this.getZclass(),
		innerWidth = this.getInnerWidth(),
		wdAttr = innerWidth == '100%' ? ' width="100%"' : '',
		wdStyle =  innerWidth != '100%' ? 'width:' + innerWidth : '',
		inPaging = this.inPagingMold(), pgpos,
		tag = zk.ie || zk.gecko ? 'a' : 'button';

	out.push('<div', this.domAttrs_(), '>');

	if (inPaging && this.paging) {
		pgpos = this.getPagingPosition();
		if (pgpos == 'top' || pgpos == 'both') {
			out.push('<div id="', uuid, '-pgit" class="', this.$s('paging-top'), '">');
			this.paging.redraw(out);
			out.push('</div>');
		}
	}

	if (this.listhead) {
		out.push('<div id="', uuid, '-head" class="', this.$s('header'), '">',
			'<table id="', uuid, '-headtbl"', wdAttr, ' style="table-layout:fixed;',
			wdStyle,'">');
		this.domFaker_(out, '-hdfaker');
		
		out.push('<tbody id="', uuid, '-headrows">');
		for (var hds = this.heads, j = 0, len = hds.length; j < len;)
			hds[j++].redraw(out);
	
		out.push('</tbody></table></div><div class="', this.$s('header-border'), '"></div>');
	}
	out.push('<div id="', uuid, '-body" class="', this.$s('body'), '"');
	
	var hgh = this.getHeight();
	if (hgh)
		out.push(' style="overflow:hidden;height:', hgh, '"');
	out.push('>');
	
	if (this.domPad_ && !inPaging)
		this.domPad_(out, '-tpad');
	
	out.push('<table', wdAttr, ' id="', uuid, '-cave"', ' style="table-layout:fixed;', wdStyle,'">');
	
	if (this.listhead)
		this.domFaker_(out, '-bdfaker', zcls);

	out.push('<tbody id="',uuid,'-rows">');
	for (var item = this.firstItem; item; item = this.nextItem(item))
		item.redraw(out);
	out.push('</tbody>');
	
	this.redrawEmpty_(out);

	out.push('</table>');

	if (this.domPad_ && !inPaging)
		this.domPad_(out, '-bpad');
	
	out.push('<', tag, ' id="', uuid, 
			'-a" style="top:',jq.px0(this._anchorTop),';left:',jq.px0(this._anchorLeft),'" onclick="return false;" href="javascript:;" class="z-focus-a"></',
			tag, '>', "</div>");

	if (this.listfoot) {
		out.push('<div id="', uuid, '-foot" class="', this.$s('footer'), '">',
			'<table id="', uuid, '-foottbl"', wdAttr, ' style="table-layout:fixed;', wdStyle,'">');
		if (this.listhead) 
			this.domFaker_(out, '-ftfaker');
		
		out.push('<tbody id="', uuid, '-footrows">');
		this.listfoot.redraw(out);
		out.push('</tbody></table></div>');
	}

	if (this._nativebar && this.frozen) {
		out.push('<div id="', uuid, '-frozen" class="', this.$s('frozen'), '">');
		this.frozen.redraw(out);
		out.push('</div>');
	}

	if (pgpos == 'bottom' || pgpos == 'both') {
		out.push('<div id="', uuid, '-pgib" class="', this.$s('paging-bottom'), '">');
		this.paging.redraw(out);
		out.push('</div>');
	}
	out.push('</div>');
}