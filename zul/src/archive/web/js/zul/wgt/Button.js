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
		if (this.isVisible() && this.canActivate({checkOnly:true})) {
			zDom.focus(this.ebtn ? this.ebtn: this.node, timeout);
			return true;
		}
		return false;
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

			this.ebtn = n = zDom.$(this.uuid + '$btn');
		}

		zEvt.listen(n, "focus", $Button._doFocus);
		zEvt.listen(n, "blur", $Button._doBlur);
	},
	unbind_: function () {
		var $Button = zul.wgt.Button,
			n = this._mold == 'os' ? this.node: this.ebtn;
		if (n) {
			zEvt.unlisten(n, "focus", $Button._doFocus);
			zEvt.unlisten(n, "blur", $Button._doBlur);
		}

		this.ebox = this.ebtn = null;
		this.$super('unbind_');
	},
	doClick_: function (wevt) {
		if (!this._disabled)
			this.fireX(wevt);
		//Unlike DOM, we don't proprogate to parent (so no calling $supers)
	},
	doMouseOver_: function () {
		zDom.addClass(this.ebox, this.getZclass() + "-over");
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this != zul.wgt.Button._curdn)
			zDom.rmClass(this.ebox, this.getZclass() + "-over");
		this.$supers('doMouseOut_', arguments);
	},
	doMouseDown_: function () {
		var box = this.ebox,
			zcls = this.getZclass();
		zDom.addClass(box, zcls + "-clk");
		zDom.addClass(box, zcls + "-over");
		zDom.focus(this.ebtn, 30);

		zk.mouseCapture = this; //capture mouse up
		this.$supers('doMouseDown_', arguments);
	},
	doMouseUp_: function () {
		var box = this.ebox,
			zcls = this.getZclass();
		zDom.rmClass(box, zcls + "-clk");
		zDom.rmClass(box, zcls + "-over");
		this.$supers('doMouseUp_', arguments);
	}
},{
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
	}
});