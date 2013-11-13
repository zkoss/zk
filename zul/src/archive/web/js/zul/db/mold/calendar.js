/* calendar.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri June 9 10:29:16 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
function (out) {
	var renderer = zul.db.Renderer,
		uuid = this.uuid,
		view = this._view,
		zcls = this.getZclass(),
		tagnm = zk.ie < 11 || zk.gecko ? "a" : "button",
		localizedSymbols = this.getLocalizedSymbols();

	// header
	out.push('<div id="', this.uuid, '"', this.domAttrs_(), '><table style="table-layout: fixed" width="100%"', 
			zUtl.cellps0, '><tr><td id="', uuid, '-tdl" class="', zcls, '-tdl');
	
	out.push(' ',this.isOutOfRange(true) ? zcls + '-icon-disd' : '');
		
	out.push('"><div  class="', zcls, '-left"><div id="', uuid, '-ly" class="', zcls, '-left-icon"></div></div></td>',
				'<td><table class="', zcls, '-calctrl" width="100%" border="0" cellspacing="0" cellpadding="0">',
				'<tr><td id="', uuid, '-title" class="', zcls, '-title">');
	
	renderer.titleHTML(this, out, localizedSymbols);
	
	out.push('</td></tr></table></td>',
		'<td id="', uuid, '-tdr" class="', zcls, '-tdr');
		
	out.push(' ',this.isOutOfRange() ? zcls + '-icon-disd' : '');
	out.push('"><div class="', zcls, '-right"><div id="', uuid, '-ry" class="', zcls,
			'-right-icon"></div></div></td></tr>');

	switch(view) {
	case "day" :
		renderer.dayView(this, out, localizedSymbols);
		break;
	case "month" :
		renderer.monthView(this, out, localizedSymbols);
		break;
	case "year" :
		renderer.yearView(this, out, localizedSymbols);
		break;
	case "decade" :
		renderer.decadeView(this, out, localizedSymbols);
		break;
	}
	out.push('</table><', tagnm, ' id="', uuid,
		'-a" tabindex="-1" onclick="return false;" href="javascript:;" class="z-focus-a"></',
		tagnm, '></td></tr></table></div>');
}