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
		tagnm = zk.ie < 11 || zk.gecko ? 'a' : 'button',
		localizedSymbols = this.getLocalizedSymbols(),
		icon = this.$s('icon'),
		outRange = this.isOutOfRange(true) ? ' disabled="disabled"' : '',
		showTodayLink = this._showTodayLink;
	
	// header
	out.push('<div id="', uuid, '"', this.domAttrs_(), '><div class="',
			this.$s('header'), '"><a id="', uuid, '-left" href="javascript:;" class="', icon, ' ',
			this.$s('left'), '"', outRange,	'><i class="z-icon-caret-left"></i></a>',
			'<a id="', uuid, '-title" href="javascript:;" class="', this.$s('title'), '">');
	
	renderer.titleHTML(this, out, localizedSymbols);

	out.push('</a><a id="', uuid, '-right" href="javascript:;" class="', icon, ' ',
			this.$s('right'), '"', outRange, '><i class="z-icon-caret-right"></i></a></div>');
	
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
	
	if (showTodayLink) {
		out.push('<div class="', this.$s('header'), '"><a id="', uuid, '-today" href="javascript:;" class="',
				this.$s('title'), '">');
		renderer.todayView(this, out, localizedSymbols);
		out.push('</a></div>');
	}
	
	out.push('<', tagnm, ' id="', uuid,
		'-a" tabindex="-1" onclick="return false;" href="javascript:;" class="z-focus-a"></',
		tagnm, '></div>');
}