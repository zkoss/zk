/* rating.js

	Purpose:

	Description:

	History:
		Thu Jul 12 10:24:21 CST 2018, Created by wenninghsu

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
function (out) {
	var self = this,
		uuid = self.uuid,
		ori = self._orient,
		max = self._max,
		symbol = self._symbol,
		fontAwesome = symbol.lastIndexOf('z-icon-', 0) === 0;

	out.push('<ul', self.domAttrs_(), '>');

	var pushLi = function (i) {
		out.push('<li id="', uuid , '-li-', i, '" class="', self.$s('li'), ' ',  self.$s(ori), '">');
		out.push('<a href="javascript:;" id="', uuid , '-a-', i, '" class="', self.$s('a'));
		if (fontAwesome) {
			out.push(' ', symbol);
		}
		if (i <= self._rating) {
			if (self._disabled) {
				out.push(' ', self.$s('disabled-selected'));
			} else if (self._readonly) {
				out.push(' ', self.$s('readonly-selected'));
			} else {
				out.push(' ', self.$s('selected'));
			}
		} else {
			if (self._disabled) {
				out.push(' ', self.$s('disabled'));
			} else if (self._readonly) {
				out.push(' ', self.$s('readonly'));
			}
		}
		out.push('">')
		if (!fontAwesome) {
			out.push(symbol);
		}
		out.push('</a></li>');
	};

	if (ori == 'vertical') {
		for (var i = max; i > 0; i--) {
			out.push(pushLi(i));
		}
	} else {
		for (var i = 1; i <= max; i++) {
			out.push(pushLi(i));
		}
	}

	out.push('</ul>');

}