/* HeadWidget.js

	Purpose:
		
	Description:
		
	History:
		Mon Dec 29 17:15:38     2008, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A skeletal implementation for headers, the parent of
 * a group of {@link HeaderWidget}.
 *
 */
zul.mesh.HeadWidget = zk.$extends(zul.Widget, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onColSize: this}, -1000);
	},

	$define: {
		/** Returns whether the width of the child column is sizable.
		 * @return boolean
		 */
		/** Sets whether the width of the child column is sizable.
		 * If true, an user can drag the border between two columns (e.g.,
		 * {@link HeaderWidget})
		 * to change the widths of adjacent columns.
		 * <p>Default: false.
		 * @param boolean sizable
		 */
		sizable: function () {
			this.rerender();
		}
	},

	removeChildHTML_: function (child) {
		this.$supers('removeChildHTML_', arguments);
		if (!this.$instanceof(zul.mesh.Auxhead))
			for (var faker, fs = child.$class._faker, i = fs.length; i--;)
				jq(child.uuid + '-' + fs[i], zk).remove();
	},
	
	//bug #3014664
	setVflex: function (v) { //vflex ignored for Listhead/Columns/Treecols
		v = false;
		this.$super(zul.mesh.HeadWidget, 'setVflex', v);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for Listhead/Columns/Treecols
		v = false;
		this.$super(zul.mesh.HeadWidget, 'setHflex', v);
	},
	
	onColSize: function (evt) {
		var owner = this.parent;
		if (owner.isSizedByContent()) owner.$class._adjHeadWd(owner);
		evt.column._width = evt.width;
		owner._innerWidth = owner.eheadtbl.style.width;
		owner.fire('onInnerWidth', owner.eheadtbl.style.width);
		owner.fireOnRender(zk.gecko ? 200 : 60);
	},
	unbind_: function () {
		jq(this.hdfaker).remove();
		jq(this.bdfaker).remove();
		jq(this.ftfaker).remove();
		this.$supers(zul.mesh.HeadWidget, 'unbind_', arguments);
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (this.desktop) {
			if (this.parent._fixHeaders())
				this.parent.onSize();
		}
	},
	onChildRemoved_: function () {
		this.$supers('onChildRemoved_', arguments);
		if (this.desktop) {
			if (!this.childReplacing_ && this.parent._fixHeaders())
				this.parent.onSize();
		}
	},
	afterChildrenFlex_: function (hwgt) { //hflex in HeaderWidget
		var wgt = this.parent;
		if (wgt && !wgt.isSizedByContent())
			wgt._adjFlexWidth();
	}
},{ //static
	redraw: function (out) {
		out.push('<tr', this.domAttrs_(), ' align="left">');
		for (var w = this.firstChild; w; w = w.nextSibling)
			w.redraw(out);
		out.push('</tr>');
	}
});
