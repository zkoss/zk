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
		dir: _zkf = function () {
			var n = this.$n();
			if (n) n.innerHTML = this.domContent_();
		},
		orient: _zkf,
		href: function (v) {
			var n = this.$n();
			if (n) n.href = v || '';
		},
		target: function (v) {
			var n = this.$n();
			if (n) n.target = v || '';
		},
		tabindex: function (v) {
			var n = this.$n();
			if (n) n.tabIndex = v < 0 ? '' : v;
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
	},
	unbind_: function(){
		var n = this.$n();
		this.domUnlisten_(n, "onFocus", "doFocus_")
			.domUnlisten_(n, "onBlur", "doBlur_");

		this.$supers('unbind_', arguments);
	},
	domContent_: function(){
		var label = zUtl.encodeXML(this.getLabel()), img = this.getImage();
		if (!img) 
			return label;
		
		img = '<img src="' + img + '" align="absmiddle" />';
		var space = "vertical" == this.getOrient() ? '<br/>' : '';
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
			v;
		if (v = this.getTarget())
			attr += ' target="' + v + '"';
		if (v = this.getTabindex()) 
			attr += ' tabIndex="' + v + '"';
		if (v = this.getHref()) 
			attr += ' href="' + v + '"';
		else 
			attr += ' href="javascript:;"';
		return attr;
	},
	doClick_: function(evt){
		if (this._disabled) 
			evt.stop(); //prevent browser default
		else {
			this.fireX(evt);
			if (!evt.stopped)
				this.$supers('doClick_', evt, true);
		}
			//Unlike DOM, we don't proprogate to parent (so not call $supers)
	}
});
