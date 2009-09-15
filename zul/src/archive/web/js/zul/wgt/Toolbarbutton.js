/* Toolbarbutton.js

	Purpose:

	Description:

	History:
		Sat Dec 22 12:58:43	 2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Toolbarbutton = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,

	$define: {
		disabled: function () {
			this.rerender(); //bind and unbind
		},
		href: null,
		target: null,
		dir: _zkf = function () {
			this.updateDomContent_();
		},
		orient: _zkf,
		tabindex: function (v) {
			var n = this.$n();
			if (n) n.tabIndex = v < 0 ? '' : v;
		},
		upload: function (v) {
			var n = this.$n();
			if (n && !this._disabled) {
				this._cleanUpld();
				if (v && v != 'false') this._initUpld();
			}
		}
	},

	// super//
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar-button";
	},

	bind_: function(){
		this.$supers('bind_', arguments);
		if (!this._disabled) {
			var n = this.$n();
			this.domListen_(n, "onFocus", "doFocus_")
				.domListen_(n, "onBlur", "doBlur_");
		}
		if (!this._disabled && this._upload) this._initUpld();
	},
	unbind_: function(){
		if (!this._disabled && this._upload) this._cleanUpld();
		var n = this.$n();
		this.domUnlisten_(n, "onFocus", "doFocus_")
			.domUnlisten_(n, "onBlur", "doBlur_");

		this.$supers('unbind_', arguments);
	},
	_initUpld: function () {
		var v;
		if (v = this._upload)
			this._uplder = new zul.Upload(this, null, v);
	},
	_cleanUpld: function () {
		var v;
		if (v = this._uplder) {
			this._uplder = null;
			v.destroy();
		}
	},
	domContent_: function(){
		var label = zUtl.encodeXML(this.getLabel()), img = this.getImage();
		if (!img)
			return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		var space = "vertical" == this.getOrient() ? '<br/>' : '&nbsp;';
		return this.getDir() == 'reverse' ? label + space + img : img + space + label;
	},
	domClass_: function(no){
		var scls = this.$supers('domClass_', arguments);
		if (this._disabled && (!no || !no.zclass)) {
			var s = this.getZclass();
			if (s)
				scls += (scls ? ' ' : '') + s + '-disd';
		}
		return scls;
	},
	domAttrs_: function(no){
		var attr = this.$supers('domAttrs_', arguments),
			v = this.getTabindex();
		if (v)
			attr += ' tabIndex="' + v + '"';
		return attr;
	},
	doClick_: function(evt){
		if (!this.isDisabled()) {
			this.fireX(evt);

			if (!evt.stopped) {
				var href = this._href;
				if (href)
					zUtl.go(href, false, this._target || (evt.data.ctrlKey ? '_blank' : ''));
				this.$super('doClick_', evt, true);
			}
		}
	},
	doMouseOver_: function (evt) {
		if (!this.isDisabled()) {
			jq(this).addClass(this.getZclass() + '-over');
			this.$supers('doMouseOver_', arguments);
		}
	},
	doMouseOut_: function (evt) {
		if (!this.isDisabled()) {
			jq(this).removeClass(this.getZclass() + '-over');
			this.$supers('doMouseOut_', arguments);
		}
	}
});
