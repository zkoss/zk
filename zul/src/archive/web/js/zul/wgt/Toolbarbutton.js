/* Toolbarbutton.js

	Purpose:

	Description:

	History:
		Sat Dec 22 12:58:43     2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zul.wgt.Toolbarbutton = zk.$extends(zul.LabelImageWidget, {
    _orient: "horizontal",
    _dir: "normal",
    _href: null,
    _target: null,
    _tabindex: -1,
    _disabled: false,
    
    isDisabled: function(){
        return this._disabled;
    },
    
    setDisabled: function(disabled){
        if (this._disabled != disabled) {
            this._disabled = disabled;
            var n = this.node;
            if (n) {
                n.disabled = true;
                this.rerender();
            }
        }
    },
    
    getDir: function(){
        return this._dir;
    },
    
    setDir: function(dir){
        if (this._dir != dir) {
            this._dir = dir;
            var n = this.node;
            if (n) 
                this.rerender();
        }
    },
    
    getHref: function(){
        return this._href == null ? "" : this._href;
    },
    
    setHref: function(href){
        if (this._href != href) {
            this._href = href;
            var n = this.node;
            if (n) 
                n.href = href;
        }
    },
    
    getOrient: function(){
        return this._orient;
    },
    
    setOrient: function(orient){
        if (this._orient != orient) {
            this._orient = orient;
            var n = this.node;
            if (n) 
                this.rerender();
        }
    },
    
    getTarget: function(){
        return this._target == null ? "" : this._target;
    },
    
    setTarget: function(target){
        if (this._target != target) {
            this._target = target;
            var n = this.node;
            if (n) 
                n.target = target;
        }
    },
    
    getTabindex: function(){
        return this._tabindex == -1 ? "" : this._tabindex;
    },
    
    setTabindex: function(tabindex){
        if (this._tabindex != tabindex) {
            this._tabindex = tabindex;
            var n = this.node;
            if (n) 
                n.tabIndex = tabindex < 0 ? null : tabindex;
        }
    },
    // super//
    getZclass: function(){
        var zcls = this._zclass;
        return zcls ? zcls : "z-toolbar-button";
    },
    // protected //
    bind_: function(desktop){
        this.$super('bind_', desktop);
        var n = this.node;
        if (!this._disabled) {
            zEvt.listen(n, "focus", this.proxy(this.domFocus_, '_fxFocus'));
            zEvt.listen(n, "blur", this.proxy(this.domBlur_, '_fxBlur'));
        }
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
        var scls = this.$super('domClass_', no);
        if (this._disabled && (!no || !no.zclass)) {
            var s = this.getZclass();
            if (s) 
                scls += (scls ? ' ' : '') + s + '-disd';
        }
        return scls;
    },
    domAttrs_: function(no){
        var attr = this.$super('domAttrs_', no);
        if (this.getTarget()) 
            attr += ' target="' + this.getTarget() + '"';
        if (this.getTabindex()) 
            attr += ' tabIndex="' + this.getTabindex() + '"';
        if (this.isDisabled()) 
            attr += ' disabled="' + this.isDisabled() + '"';
        if (this.getHref()) 
            attr += ' href="' + this.getHref() + '"';
        else 
            attr += ' href="javascript:;"';
        return attr;
    },
    doClick_: function(evt){
        if (!evt) 
            evt = window.event;
        if (this.isDisabled()) {
            zEvt.stop(evt);
            return;
        }
        this.fire("onClick", zEvt.mouseData(evt, this.node), {
            ctl: true
        });
    }
});
