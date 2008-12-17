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
				else this.rerender(); //bind and unbind required
		}
	},
	/** Returns the tab index
	 */
	getTabindex: function () {
		return this._tabindex;
	},
	/** Sets the tab index
	 */
	setTabindex: function(tabindex) {
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			var n = this.node;
			if (n)
				(this.ebtn || this.node).tabIndex = tabindex;
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
	domClass_: function (no) {
		var scls = this.$super('domClass_', no);
		if (this._disabled && (!no || !no.zclass)) {
			var s = this.getZclass();
			if (s) scls += (scls ? ' ': '') + s + '-disd';
		}
		return scls;
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
			zEvt.listen(n, "mousedown", $Button._doDown);
			zEvt.listen(n, "mouseup", $Button._doUp);
			zEvt.listen(n, "mouseover", $Button._doOver);
			zEvt.listen(n, "mouseout", $Button._doOut);

			this.ebtn = n = zDom.$(this.uuid + '$btn');
		}

		zEvt.listen(n, "focus", $Button._doFocus);
		zEvt.listen(n, "blur", $Button._doBlur);
	},
	unbind_: function () {
		var $Button = zul.wgt.Button, n;
		if (this._mold == 'os') {
			n = this.node;
		} else {
			n = this.ebox;
			if (n) {
				zEvt.unlisten(n, "mousedown", $Button._doDown);
				zEvt.unlisten(n, "mouseup", $Button._doUp);
				zEvt.unlisten(n, "mouseover", $Button._doOver);
				zEvt.unlisten(n, "mouseout", $Button._doOut);
			}
			n = this.ebtn;
		}

		if (n) {
			zEvt.unlisten(n, "focus", $Button._doFocus);
			zEvt.unlisten(n, "blur", $Button._doBlur);
		}

		this.ebox = this.ebtn = null;
		this.$super('unbind_');
	},
	doClick_: function (evt) {
		if (!this._disabled)
			this.fire("onClick", zEvt.mouseData(evt, this.node), {ctl:true});
		return true;
	}
},{
	_doOver: function (evt) {
		var wgt = zk.Widget.$(evt);
		zDom.addClass(wgt.ebox, wgt.getZclass() + "-over");
	},
	_doOut: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt != zul.wgt.Button._curdn)
			zDom.rmClass(wgt.ebox, wgt.getZclass() + "-over");
	},
	_doFocus: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt && wgt.domFocus_() //FF2 will cause a focus error when resize browser.
		&& wgt._mold != 'os')
			zDom.addClass(wgt.ebox, wgt.getZclass() + "-focus");
	},
	_doBlur: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt._mold != 'os')
			zDom.rmClass(wgt.ebox, wgt.getZclass() + "-focus");
		wgt.domBlur_();
	},
	_doDown: function (evt) {
		var wgt = zk.Widget.$(evt),
			box = wgt.ebox,
			zcls = wgt.getZclass();
		zDom.addClass(box, zcls + "-clk");
		zDom.addClass(box, zcls + "-over");
		zDom.focus(wgt.ebtn, 30);

		var $Button = zul.wgt.Button;
		$Button._curdn = wgt;
		zEvt.listen(document.body, "mouseup", $Button._doUp);
	},
	_doUp: function (evt) {
		var $Button = zul.wgt.Button,
			wgt = $Button._curdn;
		if (wgt) {
			$Button._curdn = null;
			var box = wgt.ebox,
				zcls = wgt.getZclass();
			zDom.rmClass(box, zcls + "-clk");
			zDom.rmClass(box, zcls + "-over");
		}
		zEvt.unlisten(document.body, "mouseup", $Button._doUp);
	}
});