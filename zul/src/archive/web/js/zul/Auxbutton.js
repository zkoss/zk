/* Auxbutton.js

	Purpose:
		
	Description:
		
	History:
		Mon Mar 30 13:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.Auxbutton = zk.$extends(zk.Object, {
	$init: function (wgt, btn, ref) {
		this._wgt = wgt;
		this._btn = btn;
		this._ref = ref;
		this._img = zDom.firstChild(btn, 'SPAN');

		zDom.disableSelection(btn);
		zDom.disableSelection(this._img);

		zEvt.listen(btn, 'mouseover', this.proxy(this._domOver, '_pxOver'));
		zEvt.listen(btn, 'mouseout', this.proxy(this._domOut, '_pxOut'));
		zEvt.listen(btn, 'mousedown', this.proxy(this._domDown, '_pxDown'));
	},
	cleanup: function () {
		var btn = this._btn;

		zDom.enableSelection(btn); //unlisten in IE
		zDom.enableSelection(this._img);

		zEvt.unlisten(btn, 'mouseover', this._pxOver);
		zEvt.unlisten(btn, 'mouseout', this._pxOut);
		zEvt.unlisten(btn, 'mousedown', this._pxDown);
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
	},
	_domOver: function () {
		var wgt = this._wgt;
		if (!wgt.isDisabled() && !zk.dragging)
			zDom.addClass(this._btn, wgt.getZclass() + "-btn-over");
	},
	_domOut: function () {
		var wgt = this._wgt;
		if (!wgt.isDisabled() && !zk.dragging)
			zDom.rmClass(this._btn, wgt.getZclass() + "-btn-over");
	},
	_domDown: function () {
		var wgt = this._wgt;
		if (!wgt.isDisabled() && !zk.dragging) {
			var $Auxbutton = zul.Auxbutton,
				curab = $Auxbutton._curab;
			if (curab) curab._domUp();

			zDom.addClass(this._btn, wgt.getZclass() + "-btn-clk");
			zEvt.listen(document.body, "mouseup", this.proxy(this._domUp, '_pxUp'));

			$Auxbutton._curab = this;
		}
	},
	_domUp: function () {
		var $Auxbutton = zul.Auxbutton,
			curab = $Auxbutton._curab;
		if (curab) {
			$Auxbutton._curab = null;
			zDom.rmClass(curab._btn, curab._wgt.getZclass() + "-btn-clk");
			zEvt.unlisten(document.body, "mouseup", curab._pxUp);
		}
	}
});
