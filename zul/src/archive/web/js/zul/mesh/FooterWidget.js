/* FooterWidget.js

	Purpose:
		
	Description:
		
	History:
		Tue Jul 27 09:24:17 TST 2010, Created by jimmy

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for a footer.
 */
zul.mesh.FooterWidget = zk.$extends(zul.LabelImageWidget, {
	_span: 1,
	
	$define: {
		/** Returns number of columns to span this footer.
    	 * Default: 1.
    	 * @return int
    	 */
    	/** Sets the number of columns to span this footer.
    	 * <p>It is the same as the colspan attribute of HTML TD tag.
    	 * @param int span
    	 */
		span: function (v) {
			var n = this.$n();
			if (n) n.colSpan = v;
		},
		/** Returns the horizontal alignment of this footer.
    	 * <p>Default: null (system default: left unless CSS specified).
    	 * @return String
    	 */
    	/** Sets the horizontal alignment of this footer.
    	 * @param String align
    	 */
		align: function (v) {
			var n = this.$n();
			if (n) n.align = v;
		},
		/** Returns the vertical alignment of this footer.
		 * <p>Default: null (system default: top).
		 * @return String
		 */
		/** Sets the vertical alignment of this footer.
		 * @param String valign
		 */
		valign: function (v) {
			var n = this.$n();
			if (n) n.vAlign = v;
		}
	},
	/**
	 * Returns the mesh widget that this belongs to.
	 * @return zul.mesh.MeshWidget
	 */
	getMeshWidget: function () {
		return this.parent ? this.parent.parent : null;
	},
	/** Returns the column that is in the same column as
	 * this footer, or null if not available.
	 * @return zul.mesh.HeaderWidget
	 */
	getHeaderWidget: function () {
		var meshWidget = this.getMeshWidget();
		if (meshWidget) {
			var cs = meshWidget.getHeadWidget();
			if (cs)
				return cs.getChildAt(this.getChildIndex());
		}
		return null;
	},
	//super
	domStyle_: function (no) {
		var style = '',
			header = this.getHeaderWidget();
		if (this._align)
			style += 'text-align:' + this._align + ';';
		else if (header && header._align)
			style += 'text-align:' + header._align + ';';
		if (this._valign)
			style += 'vertical-align:' + this._align + ';';
		else if (header && header._valign)
			style += 'vertical-align:' + header._valign + ';';
		if (header && !header.isVisible()) //Bug ZK-1425
			style += 'display: none;';
		
		return style + this.$super('domStyle_', no);
	},
	domAttrs_: function () {
		return this.$supers('domAttrs_', arguments)
			+ (this._span > 1 ? ' colspan="' + this._span + '"' : '');
	},
	deferRedrawHTML_: function (out) {
		out.push('<td', this.domAttrs_({domClass:1}), ' class="z-renderdefer"></td>');
	}
});