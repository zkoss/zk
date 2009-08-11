/* caption.js

	Purpose:
		
	Description:
		
	History:
		Sun Nov 16 13:02:44     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function (out) {
	var parent = this.parent;
	if (parent.isLegend && parent.isLegend()) {
		out.push('<legend', this.domAttrs_(), '>', this.domContent_());
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</legend>');
		return;
	}

	var zcls = this.getZclass(),
		cnt = this.domContent_(),
		puuid = parent.uuid,
		pzcls = parent.getZclass();
	out.push('<table', this.domAttrs_(), zUtl.cellps0,
			' width="100%"><tr valign="middle"><td align="left" class="',
			zcls, '-l">', (cnt?cnt:'&nbsp;'), //Bug 1688261: nbsp required
			'</td><td align="right" class="', zcls,
			'-r" id="', this.uuid, '-cave">');
	for (var w = this.firstChild; w; w = w.nextSibling)
		w.redraw(out);

	out.push('</td>');
	if (this._isCollapsibleVisible())
		out.push('<td width="16"><div id="', puuid, '-exp" class="',
				pzcls, '-icon ', pzcls, '-exp"></div></td>');
	if (this._isMinimizeVisible())
		out.push('<td width="16"><div id="', puuid, '-min" class="',
				pzcls, '-icon ', pzcls, '-min"></div></td>');
	if (this._isMaximizeVisible()) {
		out.push('<td width="16"><div id="', puuid, '-max" class="',
				pzcls, '-icon ', pzcls, '-max');
		if (parent.isMaximized())
			out.push(' ', pzcls, '-maximized');
		out.push('"></div></td>');
	}
	if (this._isCloseVisible())
		out.push('<td width="16"><div id="', puuid, '-close" class="',
				pzcls, '-icon ', pzcls, '-close"></div></td>');

	out.push('</tr></table>');
}