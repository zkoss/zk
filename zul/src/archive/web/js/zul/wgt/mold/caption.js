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
	out.push('<div', this.domAttrs_(), '>',
		'<div id="', this.uuid, '-cave" class="', this.$s('content'), 
		'">', (cnt ? cnt : this._getBlank()), '</div>'); // Bug 1688261: &nbsp required
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	
	if (p._isDefault && p._isDefault()) {
		out.push('</div>');
		return; 
	}
	
	var puuid = p.uuid,
		picon = p.$s('icon'),
		getIcon = function(iconClass) {
			return '<i class="z-icon-' + iconClass + '"></i>';
		};
	
	if (this._isCloseVisible())
		out.push('<div id="', puuid, '-close" class="', picon, ' ', p.$s('close'),
				'">', getIcon('remove'), '</div>');
	if (this._isMaximizeVisible()) {
		out.push('<div id="', puuid, '-max" class="', picon, ' ', p.$s('maximize'));
		if (p.isMaximized())
			out.push(' ', p.$s('maximized'));
		out.push('">', this._maximized ? 
				getIcon('resize-small') : getIcon('resize-full'), '</div>');
	}
	if (this._isMinimizeVisible())
		out.push('<div id="', puuid, '-min" class="', picon, ' ', p.$s('minimize'),
				'">', getIcon('minus'), '</div>');
	if (this._isCollapsibleVisible())
		out.push('<div id="', puuid , '-exp" class="', picon, ' ', p.$s('expand'),
				'" >', p._open ? getIcon('caret-up') : getIcon('caret-down'), '</div>');
	
	out.push('</div>');
}