/* Textbox.js

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 23:30:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A textbox.
 * <p>Default {@link #getZclass}: z-textbox.
 */
zul.inp.Textbox = zk.$extends(zul.inp.InputWidget, {
	_value: '',
	_rows: 1,

	$define: {
		/** Returns whether it is multiline.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether it is multiline.
		 * @param boolean multiline
		 */
		multiline: function () {
			this.rerender();
		},
		/** Returns whether TAB is allowed.
		 * If true, the user can enter TAB in the textbox, rather than change
		 * focus.
		 * <p>Default: false.
		 * @return boolean
		 */
		/** Sets whether TAB is allowed.
		 * If true, the user can enter TAB in the textbox, rather than change
		 * focus.
		 * <p>Default: false.
		 * @param boolean tabbable
		 */
		tabbable: null,
		/** Returns the rows.
		 * <p>Default: 1.
		 * @return int
		 */
		/** Sets the rows.
		 * @param int rows
		 */
		rows: function (v) {
			var inp = this.getInputNode();
			if (inp && this.isMultiline())
				inp.rows = v;
		},
		/** Returns the type.
		 * <p>Default: text.
		 * @return String
		 */
		/** Sets the type.
		 * @param String type the type. Acceptable values are "text" and "password".
		 * Unlike XUL, "timed" is redudant because it is enabled as long as
		 * onChanging is added.
		 */
		type: zk.ie ? function () {
			this.rerender(); //though IE9 allows type to change but value is reset
		}: function (type) {
			var inp = this.getInputNode();
			if (inp)
				inp.type = type;
		}
	},
	onSize: function() {
		var width = this.getWidth();
		if (!width || width.indexOf('%') != -1)
			this.getInputNode().style.width = '';
		this.syncWidth();
	},

	/** Synchronizes the input element's width of this component
	 */
	syncWidth: function () {
		zul.inp.RoundUtl.syncWidth(this, this.$n('right-edge'));
	},
	//super//
	textAttrs_: function () {
		var html = this.$supers('textAttrs_', arguments);
		if (this._multiline)
			html += ' rows="' + this._rows + '"';
		return html;
	},
	getZclass: function () {
		var zcs = this._zclass;
		return zcs != null ? zcs: "z-textbox" + 
				(this.inRoundedMold() && !this.isMultiline() ? "-rounded": "");
	},
	// override domClass_ for add extra class name as need,
	// can not use getZclass because InputWidget use it to add/remove class
	domClass_: function () {
		var clsnm = this.$supers(zul.inp.Textbox, 'domClass_', arguments);
		if (this.isMultiline() && this.inRoundedMold())
			clsnm += ' ' + this.getZclass() + '-ml-rounded';
		return clsnm;
	},
	bind_: function(){
		this.$supers(zul.inp.Textbox, 'bind_', arguments);
		if (this.inRoundedMold())
			zWatch.listen({onSize: this});
	},
	unbind_: function(){
		if (this.inRoundedMold())
			zWatch.unlisten({onSize: this});
		this.$supers(zul.inp.Textbox, 'unbind_', arguments);
	}
});
