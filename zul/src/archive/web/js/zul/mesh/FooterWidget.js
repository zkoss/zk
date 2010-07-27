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
	getAlignAttrs: function () {
		return (this._align ? ' align="' + this._align + '"' : '')
			+ (this._valign ? ' valign="' + this._valign + '"' : '') ;
	},
	//super
	domStyle_: function (no) {
		var style = '';
		if (zk.ie8 && this._align)
			style += 'text-align:' + this._align + ';';
		
		return style + this.$super('domStyle_', no);
	},
	domAttrs_: function () {
		var head = this.getHeaderWidget(),
			added;
		if (head)
			added = head.getColAttrs();
		if (this._align || this._valign)
			added = this.getAlignAttrs();
		return this.$supers('domAttrs_', arguments)
			+ (this._span > 1 ? ' colspan="' + this._span + '"' : '')
			+ (added ? ' ' + added : '');
	}
});