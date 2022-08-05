/* domopera.ts

	Purpose:

	Description:

	History:
		Mon Jun 15 10:44:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
Object.assign<zk.JQZK, Partial<zk.JQZK>>(zjq.prototype, {
	scrollOffset(): zk.Offset {
		//Fix opera bug (see the page function)
		// If tag is "img" or "tr", the "div" element's scrollTop should be ignored.
		// Because the offsetTop of element "img" or "tr" is excluded its scrollTop.
		var node = this.jq![0],
			normaltag = !jq.nodeName(node, 'tr', 'img'),
			t = 0, l = 0,
			// eslint-disable-next-line zk/noNull
			el: HTMLElement | null = node;
		do {
			var tag = jq.nodeName(el as Node);
			if (tag == 'body' || (normaltag && tag == 'div')) {
				t += el?.scrollTop || 0;
				l += el?.scrollLeft || 0;
			}
			el = el.parentElement;
		} while (el);
		return [l, t];
	}
});

Object.assign(zjq, {
	_cleanVisi(n: HTMLElement): void {
		n.style.visibility = n.getElementsByTagName('input').length ? 'visible' : 'inherit';
		// visible will cause elements that shall be cropped become visible, but we need do it if it has INPUT
	},
	_fixCSS(el: HTMLElement): void {
		var old = el.style.display,
			top = el.scrollTop,
			lft = el.scrollLeft;
		el.style.display = 'none'; //force redraw
		if (el.offsetWidth) {
			//force recalc
		}
		el.style.display = old;
		el.scrollTop = top;
		el.scrollLeft = lft;
	}
});