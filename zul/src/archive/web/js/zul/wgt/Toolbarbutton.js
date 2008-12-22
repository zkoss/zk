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

	// super//
	getZclass: function () {
		var zcls = this._zclass;
		return zcls ? zcls : "z-toolbar-button";
	},

	/**
	 * Sets whether it is disabled.
	 */
	setDisabled: function(disabled) {
		if (this._disabled != disabled) {
			this._disabled = disabled;
			var n = this.node;
			if (n) {
				n.disabled = true;
				this.rerender();
			}

		}
	},

	/** Returns whether it is disabled.
	 * <p>Default: false.
	 */
	isDisabled: function () {
		return this._disabled;
	},

	/** Returns the direction.
	 * <p>Default: "normal".
	 */
	getDir: function () {
		return this._dir;
	},

	/** Sets the direction.
	 * @param dir either "normal" or "reverse".
	 */
	setDir: function(dir){
		if (this._dir != dir ) {
			this._dir = dir;
			this.rerender();
		}
	},

	/** Returns the href.
	 * <p>Default: null. If null, the button has no function unless you
	 * specify the onClick handler.
	 */
	getHref: function () {
		return this._href == null ? "javascript:;" : this._href;
	},

	/** Sets the href.
	 */
	setHref: function (href){
		if (href != null && href.length == 0)
			href = null;
		if (this._href != href ) {
			this._href = href;
			var n = this.node;
			if (n)
				n.href = this._href;
		}
	},

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 */
	getOrient: function () {
		return this._orient;
	},

	/** Sets the orient.
	 * @param orient either "horizontal" or "vertical".
	 */
	setOrient: function (orient) {
		if (this._orient != orient) {
			this._orient = orient;
			this.rerender();
		}
	},

	/** Returns the target frame or window.
	 *
	 * <p>Note: it is useful only if href ({@link #setHref}) is specified
	 * (i.e., use the onClick listener).
	 *
	 * <p>Default: null.
	 */
	getTarget: function () {
		return this._target == null ? "" : this._target;
	},

	/** Sets the target frame or window.
	 * @param target the name of the frame or window to hyperlink.
	 */
	setTarget: function (target) {
		if (!target)
			target = null;

		if (this._target != target) {
			this._target = target;
			var n = this.node;
			if (n)
				n.target = this._target;
		}
	},

	/** Returns the tab order of this component.
	 * <p>Default: -1 (means the same as browser's default).
	 */
	getTabindex: function () {
		return this._tabindex == -1 ? "" : this._tabindex;
	},

    /** Sets the tab order of this component.
     */
    setTabindex: function(tabindex){
        if (this._tabindex != tabindex) {
            this._tabindex = tabindex;
            var n = this.node;
            if (tabindex < 0)
                n.tabIndex = null;
            else
                n.tabIndex = this._tabindex;
        }
    },
	/** Updates the label and image. */
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage();
		if (!img) return label;

		img = '<img src="' + img + '" align="absmiddle" />';
		var space = "vertical" == this.getOrient() ? '<br/>': '';
		return this.getDir() == 'reverse' ?
			label + space + img: img + space + label;
	},
	domAttrs_: function (no) {
		var attr = this.$super('domAttrs_', no);
		if (this.getTarget())
			attr += ' target="'+ this.getTarget() +'"';
		if (this.getTabindex())
			attr += ' tabIndex="'+ this.getTabindex() +'"';
		if (this.getHref())
			attr += ' href="'+ this.getHref() +'"';
		return attr;
	}
	}, {
	/*Static */
});