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
	_disabled: false,

	isDisabled: function(){
		return this._disabled;
	},
	setDisabled: function(disabled){
		if (this._disabled != disabled) {
			this._disabled = disabled;
			this.updateDomClass_();//update class and attr
		}
	},
	
	getDir: function(){
		return this._dir;
	},
	setDir: function(dir){
		if (this._dir != dir) {
			this._dir = dir;
			var n = this.getNode();
			if (n) n.innerHTML = this.domContent_();
		}
	},
	
	getHref: function(){
		return this._href;
	},
	setHref: function(href){
		if (this._href != href) {
			this._href = href;
			var n = this.getNode();
			if (n) n.href = href;
		}
	},
	
	getOrient: function(){
		return this._orient;
	},
	setOrient: function(orient){
		if (this._orient != orient) {
			this._orient = orient;
			var n = this.getNode();
			if (n) n.innerHTML = this.domContent_();
		}
	},
	
	getTarget: function(){
		return this._target;
	},
	setTarget: function(target){
		if (this._target != target) {
			this._target = target;
			var n = this.getNode();
			if (n) n.target = target;
		}
	},
	
	getTabindex: function(){
		return this._tabindex == -1 ? "" : this._tabindex;
	},
	setTabindex: function(tabindex){
		if (this._tabindex != tabindex) {
			this._tabindex = tabindex;
			var n = this.getNode();
			if (n) n.tabIndex = tabindex < 0 ? null : tabindex;
		}
	},
	// super//
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar-button";
	},

	bind_: function(){
		this.$supers('bind_', arguments);
		var n = this.getNode();
		if (!this._disabled) {
			zEvt.listen(n, "focus", this.proxy(this.domFocus_));
			zEvt.listen(n, "blur", this.proxy(this.domBlur_));
		}
	},
	unbind_: function(){
		var n = this.getNode();
		zEvt.unlisten(n, "focus", this.proxy(this.domFocus_));
		zEvt.unlisten(n, "blur", this.proxy(this.domBlur_));
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
	doClick_: function(wevt, evt){
		if (this._disabled)
			zEvt.stop(evt); //prevent default behavior
		else {
			this.fireX(wevt);
			if (wevt.stopped)
				zEvt.stop(evt); //prevent default behavior
		}
		//Unlike DOM, we don't proprogate to parent (so no calling $supers)
	}
});
