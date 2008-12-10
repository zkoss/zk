/* Button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:16     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Button = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,

	/** Returns the orient of this button.
	 */
	getOrient: function () {
		return this._orient;
	},
	/** Sets the orient of this button.
	 */
	setOrient: function(orient) {
		if (this._orient != orient) {
			this._orient = orient;
			var n = this.node;
			if (n) this.updateDomContent_();
		}
	},
	/** Returns the dir of this button.
	 */
	getDir: function () {
		return this._dir;
	},
	/** Sets the dir of this button.
	 */
	setDir: function(dir) {
		if (this._dir != dir) {
			this._dir = dir;
			var n = this.node;
			if (n) this.updateDomContent_();
		}
	},
	/** Returns whether this button is disabled
	 */
	isDisabled: function () {
		return this._disabled;
	},
	/** Sets whether this button is disabled
	 */
	setDisabled: function(disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			var n = this.node;
			if (n)
				if (this._mold == 'os') this.node.disabled = true;
				else this.rerender();
		}
	},
	/** Returns whether this button is disabled
	 */
	getTabindex: function () {
		return this._tabindex;
	},
	/** Sets whether this button is disabled
	 */
	setTabindex: function(tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			var n = this.node;
			if (n)
				if (this._mold == 'os') this.node.tabIndex = tabindex;
				else this.rerender();
		}
	},

	//super//
	focus: function (timeout) {
		return zDom.focus(this.ebtn ? this.ebtn: this.node, timeout);
	},

	/** Updates the label and image. */
	updateDomContent_: function () {
		var n = this.ebox;
		if (n) n.tBodies[0].rows[1].cells[1].innerHTML = this.domContent_();
		else this.node.innertHTML = this.domContent_();
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage();
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		var space = "vertical" == this.getOrient() ? '<br/>': ' ';
		return this.getDir() == 'reverse' ?
			label + space + img: img + space + label;
	},

	getZclass: function () {
		var zcls = this._zclass;
		return zcls != null ? zcls: this._mold == 'os' ? "z-button-os": "z-button";
	},
	bind_: function (desktop) {
		this.$super('bind_', desktop);

		var $Button = zul.wgt.Button, n;
		if (this._mold == 'os') {
			n = this.node;
		} else {
			if (this._disabled) return;

			n = zDom.$(this.uuid + '$box');
			this.ebox = n;
			zDom.disableSelection(n);
			zEvt.listen(n, "mousedown", $Button.ondown);
			zEvt.listen(n, "mouseup", $Button.onup);
			zEvt.listen(n, "mouseover", $Button.onover);
			zEvt.listen(n, "mouseout", $Button.onout);

			this.ebtn = n = zDom.$(this.uuid + '$btn');
		}

		zEvt.listen(n, "focus", $Button.onfocus);
		zEvt.listen(n, "blur", $Button.onblur);
	},
	unbind_: function () {
		var $Button = zul.wgt.Button, n;
		if (this._mold == 'os') {
			n = this.node;
		} else {
			n = this.ebox;
			if (n) {
				zEvt.unlisten(n, "mousedown", $Button.ondown);
				zEvt.unlisten(n, "mouseup", $Button.onup);
				zEvt.unlisten(n, "mouseover", $Button.onover);
				zEvt.unlisten(n, "mouseout", $Button.onout);
			}
			n = this.ebtn;
		}

		if (n) {
			zEvt.unlisten(n, "focus", $Button.onfocus);
			zEvt.unlisten(n, "blur", $Button.onblur);
		}

		this.ebox = this.ebtn = null;
		this.$super('unbind_');
	}
},{
	onover: function (evt) {
		var wgt = zk.Widget.$(evt);
		zDom.addClass(wgt.ebox, wgt.getZclass() + "-over");
	},
	onout: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt != zul.wgt.Button._curdn)
			zDom.rmClass(wgt.ebox, wgt.getZclass() + "-over");
	},
	onfocus: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt && wgt.domFocus() //FF2 will cause a focus error when resize browser.
		&& wgt._mold != 'os')
			zDom.addClass(wgt.ebox, wgt.getZclass() + "-focus");
	},
	onblur: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt._mold != 'os')
			zDom.rmClass(wgt.ebox, wgt.getZclass() + "-focus");
		wgt.domBlur();
	},
	ondown: function (evt) {
		var wgt = zk.Widget.$(evt),
			box = wgt.ebox,
			zcls = wgt.getZclass();
		zDom.addClass(box, zcls + "-clk");
		zDom.addClass(box, zcls + "-over");
		zDom.focus(wgt.ebtn, 30);

		var $Button = zul.wgt.Button;
		$Button._curdn = wgt;
		zEvt.listen(document.body, "mouseup", $Button.onup);
	},
	onup: function (evt) {
		var $Button = zul.wgt.Button,
			wgt = $Button._curdn;
		if (wgt) {
			$Button._curdn = null;
			var box = wgt.ebox,
				zcls = wgt.getZclass();
			zDom.rmClass(box, zcls + "-clk");
			zDom.rmClass(box, zcls + "-over");
		}
		zEvt.unlisten(document.body, "mouseup", $Button.onup);
	}
});