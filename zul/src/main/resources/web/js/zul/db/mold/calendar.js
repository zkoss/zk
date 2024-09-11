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
function calendar$mold$(out) {
	var renderer = zul.db.Renderer,
		uuid = this.uuid,
		view = this._view,
		localizedSymbols = this.getLocalizedSymbols(),
		icon = this.$s('icon'),
		outRangeL = this.isOutOfRange(true) ? ' disabled="disabled" aria-disabled="true"' : '',
		outRangeR = this.isOutOfRange() ? ' disabled="disabled" aria-disabled="true"' : '',
		showTodayLink = this._showTodayLink;

	// header
	out.push('<div id="', uuid, '"', this.domAttrs_(), '><div id="', uuid,
			'-a" tabindex="-1" class="z-focus-a"></div><div class="',
			this.$s('header'), '"><a id="', uuid, '-left" href="javascript:;" class="', /*safe*/ icon, ' ',
			this.$s('left'), '"', outRangeL, '><i class="z-icon-angle-left" aria-label="', msgzul.PREV, '"></i></a>',
			'<a id="', uuid, '-title" href="javascript:;" class="', this.$s('title'), '">');

	renderer.titleHTML(this, out, localizedSymbols);

	out.push('</a><a id="', uuid, '-right" href="javascript:;" class="', /*safe*/ icon, ' ',
			this.$s('right'), '"', outRangeR, '><i class="z-icon-angle-right" aria-label="', msgzul.NEXT, '"></i></a></div>');

	switch (view) {
	case 'day':
		renderer.dayView(this, out, localizedSymbols);
		break;
	case 'month':
		renderer.monthView(this, out, localizedSymbols);
		break;
	case 'year':
		renderer.yearView(this, out, localizedSymbols);
		break;
	case 'decade':
		renderer.decadeView(this, out, localizedSymbols);
		break;
	}

	if (showTodayLink) {
		out.push('<div class="', this.$s('header'), ' ', this.$s('today'), '"><a id="', uuid, '-today" href="javascript:;" class="',
				this.$s('title'), '">');
		renderer.todayView(this, out, localizedSymbols);
		out.push('</a></div>');
	}

	out.push('</div>');
}
