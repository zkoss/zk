/* domopera.js

	Purpose:
		
	Description:
		
	History:
		Mon Jun 15 10:44:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.copy(zjq.prototype, {
	scrollOffset: function() {
		//Fix opera bug (see the page function)
		// If tag is "img" or "tr", the "div" element's scrollTop should be ignored.
		// Because the offsetTop of element "img" or "tr" is excluded its scrollTop.
		var el = this.jq[0],
			normaltag = !jq.nodeName(el, 'tr', 'img'),
			t = 0, l = 0;
		do {
			var tag = jq.nodeName(el);
			if (tag == 'body' || (normaltag && tag == 'div')) {
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
		n.style.visibility = n.getElementsByTagName('input').length ? 'visible': 'inherit';
			// visible will cause elements that shall be cropped become visible, but we need do it if it has INPUT
	},
	_fixCSS: function (el) { 
		var old = el.style.display,
			top = el.scrollTop,
			lft = el.scrollLeft;
		el.style.display = 'none'; //force redraw
		var dummy = el.offsetWidth; //force recalc
		el.style.display = old;
		el.scrollTop = top;
		el.scrollLeft = lft;
	}
});