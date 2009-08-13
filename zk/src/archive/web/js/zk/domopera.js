/* domopera.js

	Purpose:
		
	Description:
		
	History:
		Mon Jun 15 10:44:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.copy(zjq.prototype, {
	scrollOffset: function() {
		//Fix opera bug (see the page function)
		// If tag is "IMG" or "TR", the "DIV" element's scrollTop should be ignored.
		// Because the offsetTop of element "IMG" or "TR" is excluded its scrollTop.
		var el = this.jq[0],
			normaltag = el.tagName != 'TR' && el.tagName != 'IMG',
			t = 0, l = 0;
		do {
			var tag = el.tagName;
			if (tag == 'BODY' || (normaltag && tag == 'DIV')) {
				t += el.scrollTop  || 0;
				l += el.scrollLeft || 0;
			}
			el = el.parentNode;
		} while (el);
		return [l, t];
	}
});

zk.copy(zjq, {
	_cleanVisi: function (n) {
		n.style.visibility = n.getElementsByTagName('INPUT').length ? "visible": 'inherit';
			// visible will cause elements that shall be cropped become visible, but we need do it if it has INPUT
	}
});