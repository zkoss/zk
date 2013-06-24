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
	var parent = this.parent,
		uuid = this.uuid,
		zcls = this.getZclass(),
		cnt = this.domContent_();
	if (parent._isDefault && parent._isDefault()) {
		out.push('<div', this.domAttrs_(), '><div id="', uuid, '-cnt" class="', 
				zcls, '-content">', cnt);
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</div></div>');
		return;
	}

	var puuid = parent.uuid,
		picon = parent.$s('icon'),
		getIcon = function(iconClass) {
			return '<i class="z-' + iconClass + '"></i>';
		};
	out.push('<div', this.domAttrs_(), zUtl.cellps0, '>' + 
				'<div id="', uuid, '-cave" class="', this.$s('content'), '">', 
					'<div id="', uuid, '-head" class="', this.$s('head'), '">', (cnt ? cnt : this._getBlank()), '</div>'); // Bug 1688261: nbsp required
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	out.push(   '</div>');
	
	if (this._isCollapsibleVisible())
		out.push('<div id="', puuid, '-exp" class="', picon, ' ', parent.$s('expand'), '">', getIcon('icon-remove'), '</div>');
	if (this._isMinimizeVisible())
		out.push('<div id="', puuid, '-min" class="', picon, ' ', parent.$s('minimize'), '">', getIcon('icon-minus'), '</div>');
	if (this._isMaximizeVisible()) {
		out.push('<div id="', puuid, '-max" class="', picon, ' ', parent.$s('maximize'));
		if (parent.isMaximized())
			out.push(' ', parent.$s('maximized'));
		out.push('">', this._maximized ? getIcon('icon-resize-small') : getIcon('icon-fullscreen') , '</div>');
	}
	if (this._isCloseVisible())
		out.push('<div id="', puuid, '-close" class="', picon, ' ', parent.$s('close'), '">', getIcon('icon-remove'), '</div>');

	out.push('</div>');
}