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
		cnt = this.domContent_(),
		tabi = this._tabindex || 0,
		btnRenderer = zul.wgt.ButtonRenderer;
	
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
	out.push('<div id="' + p.uuid + '-icons" class="' + p.$s('icons') + '">');
	if (this._isCollapsibleVisible())
		btnRenderer.redrawCollapseButton(p, out, tabi);
	if (this._isMinimizeVisible())
		btnRenderer.redrawMinimizeButton(p, out, tabi);
	if (this._isMaximizeVisible())
		btnRenderer.redrawMaximizeButton(p, out, tabi);
	if (this._isCloseVisible())
		btnRenderer.redrawCloseButton(p, out, tabi);
	out.push('</div></div>');
}