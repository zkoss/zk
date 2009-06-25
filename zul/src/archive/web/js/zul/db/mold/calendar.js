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
		val = this._getDateObj(), 
		m = val.getMonth(), 
		d = val.getDate(), 
		y = val.getFullYear(), 
		yofs = y - (y % 10 + 1), 
		ydec = zk.parseInt(y/100);
	out.push('<table id="', this.uuid, '"', this.domAttrs_(), '>', 
			'<tr><td align="right" class="', zcls, '-tdl"><div id="', uuid, '-ly" class="', zcls, '-left"/></td>', 
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
					'<td align="left" class="', zcls, '-tdr"><div id="', uuid, '-ry" class="', zcls, '-right"/></td></tr>');
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
					out.push('<tr><td colspan="3" ><table id="', uuid, '-mid" class="', zcls, '-calmon" width="100%" border="0" cellspacing="0" cellpadding="0"><tr>');
					for (var j = 0 ; j < 12; ++j) {
						out.push('<td id="', uuid, '-m', j, '"><a href="javascript:;">', zk.S2MON[j] + '</a></td>');
						if (j > 0 && (j + 1) % 4 == 0) out.push('</tr><tr>');
					}
					out.push('</tr></table></td></tr>');
					break;
				case "year" :
					out.push('<tr><td colspan="3" ><table id="', uuid, '-mid" class="', zcls, '-calyear" width="100%" border="0" cellspacing="0" cellpadding="0"><tr>');

					for (var j = 0 ; j < 12; ++j) {
						out.push('<td id="', uuid, '-y', j, '"', (j == 0 || j == 11) ? 'style="color:gray;"' : '', ' ><a href="javascript:;">', yofs, '</a></td>');
						if (j > 0 && (j + 1) % 4 == 0) out.push('</tr><tr>');
						yofs++;
					}
					out.push('</tr></table></td></tr>');
					break;
				case "decade" :
					out.push('<tr><td colspan="3" ><table id="', uuid, '-mid" class="', zcls, '-calyear" width="100%" border="0" cellspacing="0" cellpadding="0"><tr>');
					var temp = ydec*100 - 10;
					for (var j = 0 ; j < 12; ++j) {
						out.push('<td id="', uuid, '-de', j, '" class="', (y >= temp && y <= (temp + 9)) ? zcls + '-seld' : '', '"', 
								' ><a href="javascript:;"', (j == 0 || j == 11) ? 'style="color:gray;"' : '', '>', temp, '-<br />', temp + 9, '</a></td>');
						if (j > 0 && (j + 1) % 4 == 0) out.push('</tr><tr>');
						temp += 10;
					}
					out.push('</tr></table></td></tr>');
					break;
			}
	out.push('</table>');
}