/* Datebox.js

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
		yofs = y - (y % 10 + 1),
		ydec = zk.parseInt(y/100);
	out.push('<div id="', this.uuid, '"', this.domAttrs_(), '><table style="table-layout: fixed" width="100%"', zUtl.cellps0, '>',
			'<tr><td class="', zcls, '-tdl"><div  class="', zcls, '-left"><div id="', uuid, '-ly" class="', zcls, '-left-icon');

	if (view == 'decade' && ydec*100 == 1900)
		out.push(' ', zcls, '-left-icon-disd');
		
	out.push('"></div></div></td>',
				'<td><table class="', zcls, '-calctrl" width="100%" border="0" cellspacing="0" cellpadding="0">',
				'<tr><td id="', uuid, '-title">');
	switch(view) {
	case "day" :
		out.push('<span id="', uuid, '-tm" class="', zcls, '-ctrler">', zk.SMON[m], '</span>, <span id="', uuid, '-ty" class="', zcls, '-ctrler">', y, '</span>');
		break;
	case "month" :
		out.push('<span id="', uuid, '-tm" class="', zcls, '-ctrler">', zk.SMON[m], '</span>, <span id="', uuid, '-ty" class="', zcls, '-ctrler">', y, '</span>');
		break;
	case "year" :
		out.push('<span id="', uuid, '-tyd" class="', zcls, '-ctrler">', yofs + 1, '-', yofs + 10, '</span>');
		break;
	case "decade" :
		out.push('<span id="', uuid, '-tyd" class="', zcls, '-ctrler">', ydec*100, '-', ydec*100 + 99, '</span>');
		break;
	}
	out.push('</td></tr></table></td>',
		'<td class="', zcls, '-tdr"><div class="', zcls, '-right"><div id="', uuid, '-ry" class="', zcls, '-right-icon');
		
	if (view == 'decade' && ydec*100 == 2000)
		out.push(' ', zcls, '-right-icon-disd');
	
	out.push('"></div></div></td></tr>');
	//year view
	switch(view) {
	case "day" :
		out.push('<tr><td colspan="3"><table id="', uuid, '-mid" class="', zcls, '-calday" width="100%" border="0" cellspacing="0" cellpadding="0">',
				'<tr class="', zcls, '-caldow">');
			var sun = (7 - zk.DOW_1ST) % 7, sat = (6 + sun) % 7;
			for (var j = 0 ; j < 7; ++j) {
				out.push('<td');
				if (j == sun || j == sat) out.push(' style="color:red"');
				out.push( '>' + zk.S2DOW[j] + '</td>');
			}
			out.push('</tr>');
			for (var j = 0; j < 6; ++j) { //at most 7 rows
				out.push('<tr class="', zcls, '-caldayrow" id="', uuid, '-w', j, '" >');
				for (var k = 0; k < 7; ++k)
					out.push ('<td></td>');
				out.push('</tr>');
			}
		out.push('</table></td></tr>');
		break;
	case "month" :
		out.push('<tr><td colspan="3" ><table id="', uuid, '-mid" class="', zcls, '-calmon" width="100%" border="0" cellspacing="0" cellpadding="0">');
		for (var j = 0 ; j < 12; ++j) {
			if (!(j % 4)) out.push('<tr>');
			out.push('<td id="', uuid, '-m', j, '"_dt="', j ,'"><a href="javascript:;">', zk.SMON[j] + '</a></td>');
			if (!((j + 1) % 4)) out.push('</tr>');
		}
		out.push('</table></td></tr>');
		break;
	case "year" :
		out.push('<tr><td colspan="3" ><table id="', uuid, '-mid" class="', zcls, '-calyear" width="100%" border="0" cellspacing="0" cellpadding="0">');

		for (var j = 0 ; j < 12; ++j) {
			if (!(j % 4)) out.push('<tr>');
			out.push('<td _dt="', yofs ,'" id="', uuid, '-y', j, '"', (j == 0 || j == 11) ? ' style="color:gray;"' : '', ' ><a href="javascript:;">', yofs, '</a></td>');
			if (!((j + 1) % 4)) out.push('</tr>');
			yofs++;
		}
		out.push('</table></td></tr>');
		break;
	case "decade" :
		out.push('<tr><td colspan="3" ><table id="', uuid, '-mid" class="', zcls, '-calyear" width="100%" border="0" cellspacing="0" cellpadding="0">');
		var temp = ydec*100 - 10;
		for (var j = 0 ; j < 12; ++j, temp += 10) {
			if (!(j % 4)) out.push('<tr>');
			if (temp < 1900 || temp > 2090) {
				out.push('<td>&nbsp;</td>');
				if (j + 1 == 12)
					out.push('</tr>'); 
				continue;
			}
			
			out.push('<td _dt="', temp ,'" id="', uuid, '-de', j, '" class="', (y >= temp && y <= (temp + 9)) ? zcls + '-seld' : '', '"',
					' ><a href="javascript:;"', (j == 0 || j == 11) ? 'style="color:gray;"' : '', '>', temp, '-<br />', temp + 9, '</a></td>');
			if (!((j + 1) % 4)) out.push('</tr>');
		}
		out.push('</tr></table></td></tr>');
		break;
	}
	out.push('</table></div>');
}