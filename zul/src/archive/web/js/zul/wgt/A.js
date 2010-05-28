/* A.js

	Purpose:
		
	Description:
		
	History:
		Thu Aug  6 14:31:48     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The basic widgets, such as button and div.
 */
//zk.$package('zul.wgt');

/**
 * The same as HTML A tag.
 * <p>Default {@link #getZclass}: z-a.
 */
zul.wgt.A = zk.$extends(zul.LabelImageWidget, {
	_dir: "normal",
	_tabindex: -1,

	$define: {
		/** Returns whether it is disabled.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is disabled.
		 * @param boolean disabled
		 */
		disabled: function () {
			this.rerender(); //bind and unbind
		},
		/** Returns the direction.
		 * <p>Default: "normal".
		 * @return String
		 */
		/** Sets the direction.
		 * @param String dir either "normal" or "reverse".
		 */
		dir: _zkf = function () {
			var n = this.$n();
			if (n) n.innerHTML = this.domContent_();
		},
		/** Returns the href that the browser shall jump to, if an user clicks
		 * this button.
		 * <p>Default: null. If null, the button has no function unless you
		 * specify the onClick event listener.
		 * <p>If it is not null, the onClick event won't be sent.
		 * @return String
		 */
		/** Sets the href.
		 * @param String href
		 */
		href: function (v) {
			var n = this.$n();
			if (n) n.href = v || '';
		},
		/** Returns the target frame or window.
		 *
		 * <p>Note: it is useful only if href ({@link #setHref}) is specified
		 * (i.e., use the onClick listener).
		 *
		 * <p>Default: null.
		 * @return String
		 */
		/** Sets the target frame or window.
		 * @param String target the name of the frame or window to hyperlink.
		 */
		target: function (v) {
			var n = this.$n();
			if (n) n.target = v || '';
		},
		/** Returns the tab order of this component.
		 * <p>Default: -1 (means the same as browser's default).
		 * @return int
		 */
		/** Sets the tab order of this component.
		 * @param int tabindex
		 */
		tabindex: function (v) {
			var n = this.$n();
			if (n) n.tabIndex = v < 0 ? '' : v;
		}
	},

	// super//
	getZclass: function(){
		var zcls = this._zclass;
		return zcls ? zcls : "z-a";
	},

	bind_: function(){
		this.$supers(zul.wgt.A, 'bind_', arguments);
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

		this.$supers(zul.wgt.A, 'unbind_', arguments);
	},
	domContent_: function(){
		var label = zUtl.encodeXML(this.getLabel()), img = this.getImage();
		if (!img) 
			return label;
		
		img = '<img src="' + img + '" align="absmiddle" />';
		return this.getDir() == 'reverse' ? label + img : img + label;
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
				this.$super('doClick_', evt, true);
		}
			//Unlike DOM, we don't proprogate to parent (so not call $supers)
	}
});

