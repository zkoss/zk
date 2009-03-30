/* Dropbutton.js

	Purpose:
		
	Description:
		
	History:
		Mon Mar 30 13:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.Dropbutton = zk.$extends(zk.Object, {
	$init: function (btn, ref) {
		this._btn = btn;
		this._ref = ref;
		this._img = zDom.firstChild(btn, 'SPAN');
	},
	fixpos: function () {
		var btn = this._btn;
		if (zDom.isRealVisible(btn) && btn.style.position != 'relative') {
			var ref = this._ref, img = this._img,
				refh = ref.offsetHeight,
				imgh = img.offsetHeight;
			if (!refh || !imgh) {
				setTimeout(this.proxy(this.fixpos, '_pxfixpos'), 66);
				return;
			}

			//Bug 1738241: don't use align="xxx"
			var v = refh - imgh;
			if (v)
				img.style.height = Math.max(0,
					zk.parseInt(zDom.getStyle(img, 'height')) + v) + 'px';

			v = ref.offsetTop - img.offsetTop;
			btn.style.position = "relative";
			btn.style.top = v + "px";
			if (zk.safari) btn.style.left = "-2px";
		}
	}
});
