/* caption.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 13:02:44     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var p = this.parent,
		cnt = this.domContent_();
	
	// ZK-2209: should show correct when caption has child
	out.push('<div', this.domAttrs_(), '>',
		'<div id="', this.uuid, '-cave" class="', this.$s('content'), 
		'">', (cnt ? cnt : this.firstChild ? '' : this._getBlank())); // Bug 1688261: &nbsp required
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push('</div>');
	
	if (p._isDefault && p._isDefault()) {
		out.push('</div>');
		return; 
	}
	
	var puuid = p.uuid,
		picon = p.$s('icon');
	if (this._isCloseVisible()) {
		out.push('<div id="', puuid , '-close" class="', picon, ' ',
			p.$s('close'), '"><i class="', p.getClosableIconClass_(), '"></i></div>');
	}
	if (this._isMaximizeVisible()) {
		var maxd = this._maximized;
		out.push('<div id="', puuid, '-max" class="', picon, ' ', p.$s('maximize'));
		if (maxd)
			out.push(' ', p.$s('maximized'));
		var maxIcon = maxd ? p.getMaximizedIconClass_() : p.getMaximizableIconClass_();
		out.push('"><i class="', maxIcon, '"></i></div>');
	}
	if (this._isMinimizeVisible()) {
		out.push('<div id="', puuid , '-min" class="', picon, ' ',
				p.$s('minimize'), '" ><i class="',
				p.getMinimizableIconClass_(), '"></i></div>');
	}
	if (this._isCollapsibleVisible()) {
		var openIcon = p._open ? p.getCollapseOpenIconClass_() : p.getCollapseCloseIconClass_();
		out.push('<div id="', puuid , '-exp" class="', picon, ' ',
			p.$s('expand'), '"><i class="', openIcon, '"></i></div>');
	}
	
	out.push('</div>');
}