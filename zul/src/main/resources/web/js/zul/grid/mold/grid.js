/* grid.js

	Purpose:
		
	Description:
		
	History:
		Tue Dec 23 15:24:01     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function grid$mold$(out) {
	let uuid = this.uuid,
		innerWidth = zUtl.encodeXML(this.getInnerWidth()),
		wdAttr = innerWidth === '100%' ? ' width="100%"' : '', //bug#3183182
		wdStyle = innerWidth !== '100%' ? 'width:' + innerWidth : '',
		inPaging = this.inPagingMold(), pgpos;

	out.push('<div', this.domAttrs_(), ' role="grid">');

	if (inPaging && this.paging) {
		pgpos = this.getPagingPosition();
		if (pgpos === 'top' || pgpos === 'both') {
			out.push('<div id="', uuid, '-pgit" class="', this.$s('paging-top'), '">');
			this.paging.redraw(out);
			out.push('</div>');
		}
	}

	if (this.columns) {
		out.push('<div id="', uuid, '-head" class="', this.$s('header'), '" role="none">',
			'<table id="', uuid, '-headtbl"', wdAttr, ' style="table-layout:fixed;', /*safe*/ wdStyle, '" role="none">');
		this.domFaker_(out, '-hdfaker');

		out.push('<tbody id="', uuid, '-headrows" role="rowgroup">');
		for (var hds = this.heads, j = 0, len = hds.length; j < len;)
			hds[j++].redraw(out);

		out.push('</tbody></table></div>');
	}
	out.push('<div id="', uuid, '-body" class="', this.$s('body'));

	if (this._autopaging)
		out.push(' ', this.$s('autopaging'));

	out.push('"');

	var hgh = this.getHeight(),
		iOSNativeBar = zk.ios && this._nativebar;
	if (hgh || iOSNativeBar)
		out.push(' style="', hgh ? 'height:' + zUtl.encodeXML(hgh) + ';' : '', iOSNativeBar ? '-webkit-overflow-scrolling:touch;' : '', '"');
	out.push(' role="none">');

	if (this.rows && this.domPad_ && !this.inPagingMold())
		this.domPad_(out, '-tpad');

	out.push('<table id="', uuid, '-cave"', wdAttr, ' style="table-layout:fixed;', /*safe*/ wdStyle, '" role="none">');

	if (this.columns)
		this.domFaker_(out, '-bdfaker');

	if (this.rows)
		this.rows.redraw(out);

	this.redrawEmpty_(out);

	out.push('</table>');

	if (this.rows && this.domPad_ && !this.inPagingMold())
		this.domPad_(out, '-bpad');

	out.push('</div>');

	if (this._nativebar && this.frozen) {
		out.push('<div id="', uuid, '-frozen" class="', this.$s('frozen'), '">');
		this.frozen.redraw(out);
		out.push('</div>');
	}

	if (this.foot) {
		out.push('<div id="', uuid, '-foot" class="', this.$s('footer'), '" role="none">',
			'<table id="', uuid, '-foottbl"', wdAttr, ' style="table-layout:fixed;', /*safe*/ wdStyle, '" role="none">');
		if (this.columns)
			this.domFaker_(out, '-ftfaker');

		out.push('<tbody id="', uuid, '-footrows" role="rowgroup">');
		this.foot.redraw(out);
		out.push('</tbody></table></div>');
	}

	if (pgpos === 'bottom' || pgpos === 'both') {
		out.push('<div id="', uuid, '-pgib" class="', this.$s('paging-bottom'), '">');
		this.paging.redraw(out);
		out.push('</div>');
	}
	out.push('</div>');
}
