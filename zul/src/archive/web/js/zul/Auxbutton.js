/* Auxbutton.js

	Purpose:
		
	Description:
		
	History:
		Mon Mar 30 13:13:24     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.Auxbutton = zk.$extends(zk.Object, {
	$init: function (wgt, btn, ref) {
		this._wgt = wgt;
		this._btn = btn;
		this._ref = ref;

		var $btn = jq(btn),
			$img = $btn.find('>span:first');
		this._img = $img[0];

		$btn.zk.disableSelection();
		$img.zk.disableSelection();

		if (!wgt.inDesign)
			$btn.mouseover(this.proxy(this._domOver))
				.mouseout(this.proxy(this._domOut))
				.mousedown(this.proxy(this._domDown));
	},
	cleanup: function () {
		var $btn = jq(this._btn);

		$btn.zk.enableSelection();
		zk(this._img).enableSelection();

		if (!this._wgt.inDesign)
			$btn.unbind('mouseover', this.proxy(this._domOver))
				.unbind('mouseout', this.proxy(this._domOut))
				.unbind('mousedown', this.proxy(this._domDown));
	},
	fixpos: function () {
		var btn = this._btn;
		if (zk(btn).isRealVisible() && btn.style.position != 'relative') {
			var ref = this._ref, img = this._img,
				refh = ref.offsetHeight,
				imgh = img.offsetHeight;
			if (!refh || !imgh) {
				setTimeout(this.proxy(this.fixpos), 66);
				return;
			}

			//Bug 1738241: don't use align="xxx"
			var v = refh - imgh;
			if (v)
				img.style.height = jq.px(zk.parseInt(jq(img).css('height')) + v);

			v = ref.offsetTop - img.offsetTop;
			btn.style.position = "relative";
			btn.style.top = v + "px"; //might be negative
			if (zk.safari) btn.style.left = "-2px";
		}
	},
	_domOver: function () {
		var wgt = this._wgt;
		if (!wgt.isDisabled() && !zk.dragging)
			jq(this._btn).addClass(wgt.getZclass() + "-btn-over");
	},
	_domOut: function () {
		var wgt = this._wgt;
		if (!wgt.isDisabled() && !zk.dragging)
			jq(this._btn).removeClass(wgt.getZclass() + "-btn-over");
	},
	_domDown: function () {
		var wgt = this._wgt;
		if (!wgt.isDisabled() && !zk.dragging) {
			var $Auxbutton = zul.Auxbutton,
				curab = $Auxbutton._curab;
			if (curab) curab._domUp();

			jq(this._btn).addClass(wgt.getZclass() + "-btn-clk");
			jq(document.body).mouseup(this.proxy(this._domUp));

			$Auxbutton._curab = this;
		}
	},
	_domUp: function () {
		var $Auxbutton = zul.Auxbutton,
			curab = $Auxbutton._curab;
		if (curab) {
			$Auxbutton._curab = null;
			jq(curab._btn).removeClass(curab._wgt.getZclass() + "-btn-clk");
			jq(document.body).unbind("mouseup", curab.proxy(this._domUp));
		}
	}
});
