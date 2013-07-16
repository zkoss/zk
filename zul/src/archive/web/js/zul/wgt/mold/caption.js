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
		cnt = this.domContent_();
	out.push('<div', this.domAttrs_(), '>',
			   '<div id="', this.uuid, '-cave" class="', this.$s('content'), '">', (cnt ? cnt : this._getBlank()), '</div>'); // Bug 1688261: nbsp required
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);
	
	if (parent._isDefault && parent._isDefault()) {
		out.push('</div>');
		return; 
	}
	
	var puuid = parent.uuid,
		picon = parent.$s('icon'),
		getIcon = function(iconClass) {
			return '<i class="z-' + iconClass + '"></i>';
		};
	
	if (this._isCollapsibleVisible())
		out.push('<div id="', puuid , '-exp" class="', picon, ' ',  parent.$s('expand'), '" >', parent._open ? getIcon('icon-caret-up') : getIcon('icon-caret-down'), '</div>');
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