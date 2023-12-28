/* tree.js

	Purpose:
		
	Description:
		
	History:
		Wed Jun 10 15:30:46     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function tree$mold$(out) {
	var uuid = this.uuid,
		innerWidth = zUtl.encodeXML(this.getInnerWidth()),
		width = innerWidth === '100%' ? ' width="100%"' : '',
		wdStyle = innerWidth !== '100%' ? 'width:' + innerWidth : '',
		inPaging = this.inPagingMold(), pgpos,
		tag = 'button';

	out.push('<div', this.domAttrs_({tabindex: 1}), '>');//tabindex attribute will be set in the button
	//top paging
	if (inPaging && this.paging) {
		pgpos = this.getPagingPosition();
		if (pgpos === 'top' || pgpos === 'both') {
			out.push('<div id="', uuid, '-pgit" class="', this.$s('paging-top'), '">');
			this.paging.redraw(out);
			out.push('</div>');
		}
	}

	//head
	if (this.treecols) {
		out.push('<div id="', uuid, '-head" class="', this.$s('header'), '">',
			'<table id="', uuid, '-headtbl"', width,
			' style="table-layout:fixed;', /*safe*/ wdStyle, '">');
		this.domFaker_(out, '-hdfaker');

		out.push('<tbody id="', uuid, '-headrows">');
		for (var hds = this.heads, j = 0, len = hds.length; j < len;)
			hds[j++].redraw(out);

		out.push('</tbody></table></div>');
	}
	//body
	out.push('<div id="', uuid, '-body" class="', this.$s('body'));

	if (this._autopaging)
		out.push(' ', this.$s('autopaging'));

	out.push('"');

	var hgh = this.getHeight(),
		iOSNativeBar = zk.ios && this._nativebar;
	if (hgh || iOSNativeBar)
		out.push(' style="', hgh ? 'height:' + zUtl.encodeXML(hgh) + ';' : '', iOSNativeBar ? '-webkit-overflow-scrolling:touch;' : '', '"');
	out.push('>');

	if (this.domPad_ && !inPaging)
		this.domPad_(out, '-tpad');

	out.push('<table id="', uuid, '-cave"', width,
		' style="table-layout:fixed;', /*safe*/ wdStyle, '">');

	if (this.treecols)
		this.domFaker_(out, '-bdfaker');

	if (this.treechildren)
		this.treechildren.redraw(out);
	else
		out.push('<tbody id="', this.uuid, '-rows"></tbody>');

	out.push('</table>');

	if (this.domPad_ && !inPaging)
		this.domPad_(out, '-bpad');

	out.push('<', tag, ' style="top:', jq.px(this._anchorTop), ';left:', jq.px(this._anchorLeft), '" id="', uuid,
		'-a"  onclick="return false;" href="javascript:;" class="z-focus-a"');
	var tabindex = this._tabindex; // Feature ZK-2531
	if (tabindex != undefined)
		out.push(' tabindex="' + /*safe*/ tabindex + '"');
	out.push('></', tag, '>', '</div>');

	if (this._nativebar && this.frozen) {
		out.push('<div id="', uuid, '-frozen" class="', this.$s('frozen'), '">');
		this.frozen.redraw(out);
		out.push('</div>');
	}

	//foot
	if (this.treefoot) {
		out.push('<div id="', uuid, '-foot" class="', this.$s('footer'), '">',
			'<table id="', uuid, '-foottbl"', width, ' style="table-layout:fixed;', /*safe*/ wdStyle, '">');
		if (this.treecols)
			this.domFaker_(out, '-ftfaker');

		out.push('<tbody id="', uuid, '-footrows">');
		this.treefoot.redraw(out);
		out.push('</tbody></table></div>');
	}

	//bottom paging
	if (pgpos === 'bottom' || pgpos === 'both') {
		out.push('<div id="', uuid, '-pgib" class="', this.$s('paging-bottom'), '">');
		this.paging.redraw(out);
		out.push('</div>');
	}
	out.push('</div>');
}
