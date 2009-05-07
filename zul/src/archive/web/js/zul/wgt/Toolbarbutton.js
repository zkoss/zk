/* Toolbarbutton.js

	Purpose:

	Description:

	History:
		Sat Dec 22 12:58:43	 2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
_zkc = zul.wgt.Toolbarbutton = zk.$extends(zul.LabelImageWidget, {
	_orient: "horizontal",
	_dir: "normal",
	_tabindex: -1,
	
	// super//
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar-button";
	},

	bind_: function(){
		this.$supers('bind_', arguments);
		if (!this._disabled) {
			var n = this.getNode();
			this.domListen_(n, "focus");
			this.domListen_(n, "blur");
		}
	},
	unbind_: function(){
		var n = this.getNode();
		this.domUnlisten_(n, "focus");
		this.domUnlisten_(n, "blur");

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
		else
			this.fireX(evt);
			//Unlike DOM, we don't proprogate to parent (so not call $supers)
	}
});

zk.def(_zkc, {
	disabled: function () {
		this.rerender(); //bind and unbind
	},
	dir: _zkf = function () {
		var n = this.getNode();
		if (n) n.innerHTML = this.domContent_();
	},
	orient: _zkf,
	href: function (v) {
		var n = this.getNode();
		if (n) n.href = v || '';
	},
	target: function (v) {
		var n = this.getNode();
		if (n) n.target = v || '';
	},
	tabindex: function (v) {
		var n = this.getNode();
		if (n) n.tabIndex = v < 0 ? '' : v;
	}
});