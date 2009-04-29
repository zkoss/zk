/* Button.js

	Purpose:
		
	Description:
		
	History:
		Sat Nov  8 22:58:16     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
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
			this.updateDomContent_();
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
			this.updateDomContent_();
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
			if (this.desktop)
				if (this._mold == 'os') this.getNode().disabled = true;
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
			var n = this.getNode();
			if (n) (this.getSubnode('btn') || n).tabIndex = tabindex;
		}
	},

	getHref: function () {
		return this._href;
	},
	setHref: function (href) {
		this._href = href;
	},
	getTarget: function () {
		return this._target;
	},
	setTarget: function (target) {
		this._target = target;
	},

	//super//
	focus: function (timeout) {
		if (this.isVisible() && this.canActivate({checkOnly:true})) {
			zDom.focus(this.getSubnode('btn') ? this.getSubnode('btn'): this.getNode(), timeout);
			return true;
		}
		return false;
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
		var scls = this.$supers('domClass_', arguments);
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
	bind_: function () {
		this.$supers('bind_', arguments);

		var n;
		if (this._mold == 'os') {
			n = this.getNode();
		} else {
			if (this._disabled) return;

			zDom.disableSelection(this.getSubnode('box'));

			n = this.getSubnode('btn');
		}

		zEvt.listen(n, "focus", this.proxy(this.domFocus_));
		zEvt.listen(n, "blur", this.proxy(this.domBlur_));
	},
	unbind_: function () {
		var n = this._mold == 'os' ? this.getNode(): this.getSubnode('btn');
		if (n) {
			zEvt.unlisten(n, "focus", this.proxy(this.domFocus_));
			zEvt.unlisten(n, "blur", this.proxy(this.domBlur_));
		}

		this.$supers('unbind_', arguments);
	},

	doFocus_: function (evt) {
		if (this._mold != 'os')
			zDom.addClass(this.getSubnode('box'), this.getZclass() + "-focus");
		this.$supers('doFocus_', arguments);
	},
	doBlur_: function (evt) {
		if (this._mold != 'os')
			zDom.rmClass(this.getSubnode('box'), this.getZclass() + "-focus");
		this.$supers('doBlur_', arguments);
	},
	doClick_: function (wevt) {
		if (!this._disabled) {
			this.fireX(wevt);

			if (!wevt.stopped) {
				var href = this._href;
				if (href)
					zUtl.go(href, false, this.getTarget(), "target");
			}
		}
		//Unlike DOM, we don't proprogate to parent (so no calling $supers)
	},
	doMouseOver_: function () {
		zDom.addClass(this.getSubnode('box'), this.getZclass() + "-over");
		this.$supers('doMouseOver_', arguments);
	},
	doMouseOut_: function () {
		if (this != zul.wgt.Button._curdn)
			zDom.rmClass(this.getSubnode('box'), this.getZclass() + "-over");
		this.$supers('doMouseOut_', arguments);
	},
	doMouseDown_: function () {
		var box = this.getSubnode('box'),
			zcls = this.getZclass();
		zDom.addClass(box, zcls + "-clk");
		zDom.addClass(box, zcls + "-over");
		zDom.focus(this.getSubnode('btn'), 30);

		zk.mouseCapture = this; //capture mouse up
		this.$supers('doMouseDown_', arguments);
	},
	doMouseUp_: function () {
		var box = this.getSubnode('box'),
			zcls = this.getZclass();
		zDom.rmClass(box, zcls + "-clk");
		zDom.rmClass(box, zcls + "-over");
		this.$supers('doMouseUp_', arguments);
	}
});