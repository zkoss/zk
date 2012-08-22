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
	var uuid = this.uuid,
		view = this._view,
		zcls = this.getZclass(),
		val = this.getTime(),
		m = val.getMonth(),
		d = val.getDate(),
		y = val.getFullYear(),
		ydelta = new zk.fmt.Calendar(val, this._localizedSymbols).getYear() - y, 
		yofs = y - (y % 10 + 1),
		ydec = zk.parseInt(y/100),
		tagnm = zk.ie || zk.gecko ? "a" : "button",
		localizedSymbols = this._localizedSymbols || {
			DOW_1ST: zk.DOW_1ST,
				ERA: zk.ERA,    
			 YDELTA: zk.YDELTA,
			   SDOW: zk.SDOW,
			  S2DOW: zk.S2DOW,
			   FDOW: zk.FDOW,
			   SMON: zk.SMON,
			  S2MON: zk.S2MON,
			   FMON: zk.FMON,
				APM: zk.APM
		};

	out.push('<div id="', this.uuid, '"', this.domAttrs_(), '><table style="table-layout: fixed" width="100%"', zUtl.cellps0, '>',
			'<tr><td id="', uuid, '-tdl" class="', zcls, '-tdl');
	
	if (view == 'decade' && ydec*100 == 1900)
		out.push(' ', zcls, '-icon-disd');
		
	out.push('"><div  class="', zcls, '-left"><div id="', uuid, '-ly" class="', zcls, '-left-icon"></div></div></td>',
				'<td><table class="', zcls, '-calctrl" width="100%" border="0" cellspacing="0" cellpadding="0">',
				'<tr><td id="', uuid, '-title" class="', zcls, '-title">');
	switch(view) {
	case "day" :
		out.push('<span id="', uuid, '-tm" class="', zcls, '-ctrler">', localizedSymbols.SMON[m], '</span> <span id="', uuid, '-ty" class="', zcls, '-ctrler">', y + ydelta, '</span>');
		break;
	case "month" :
		out.push('<span id="', uuid, '-tm" class="', zcls, '-ctrler">', localizedSymbols.SMON[m], '</span> <span id="', uuid, '-ty" class="', zcls, '-ctrler">', y + ydelta, '</span>');
		break;
	case "year" :
		out.push('<span id="', uuid, '-tyd" class="', zcls, '-ctrler">', yofs + ydelta + 1, '-', yofs + ydelta + 10, '</span>');
		break;
	case "decade" :
		out.push('<span id="', uuid, '-tyd" class="', zcls, '-ctrler">', ydec*100 + ydelta, '-', ydec*100 + ydelta+ 99, '</span>');
		break;
	}
	out.push('</td></tr></table></td>',
		'<td id="', uuid, '-tdr" class="', zcls, '-tdr');
		
	if (view == 'decade' && ydec*100 == 2000)
		out.push(' ', zcls, '-icon-disd');
	
	out.push('"><div class="', zcls, '-right"><div id="', uuid, '-ry" class="', zcls, '-right-icon"></div></div></td></tr>');
	//year view
	switch(view) {
	case "day" :
		zul.db.Renderer.dayView(this, out, localizedSymbols);
		break;
	case "month" :
		zul.db.Renderer.monthView(this, out, localizedSymbols);
		break;
	case "year" :
		zul.db.Renderer.yearView(this, out, localizedSymbols);
		break;
	case "decade" :
		zul.db.Renderer.decadeView(this, out, localizedSymbols);
		break;
	}
	out.push('</table><', tagnm, ' id="', uuid,
		'-a" tabindex="-1" onclick="return false;" href="javascript:;" class="z-focus-a"></',
		tagnm, '></td></tr></table></div>');
}