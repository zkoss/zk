/* Toolbar.js

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43	 2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * A toolbar.
 * 
 * <p>Mold:
 * <ol>
 * <li>default</li>
 * <li>panel: this mold is used for {@link zul.wnd.Panel} component as its
 * foot toolbar.</li>
 * </ol>
 * <p>Default {@link #getZclass}: z-toolbar
 */
zul.wgt.Toolbar = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_align: 'start',

	$define: {
		/**
		 * Returns the alignment of any children added to this toolbar. Valid values
		 * are "start", "end" and "center".
		 * <p>Default: "start"
		 * @return String
		 */
		/**
		 * Sets the alignment of any children added to this toolbar. Valid values
		 * are "start", "end" and "center".
		 * <p>Default: "start", if null, "start" is assumed.
		 * @param String align
		 */
		align: _zkf = function () {
			this.rerender();
		},
		/** Returns the orient.
		 * <p>Default: "horizontal".
		 * @return String
		 */
		/** Sets the orient.
		 * @param String orient either "horizontal" or "vertical".
		 */
		orient: _zkf
	},
	// super
	domClass_: function (no) {
		var sc = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var tabs = this.parent && zk.isLoaded('zul.tab') && this.parent.$instanceof(zul.tab.Tabbox) ? this.$s('tabs') : '';
				
			if (tabs)
				sc += ' ' + tabs;
			if (this.inPanelMold())
				sc += ' ' + this.$s('panel');
		}
		return sc;
	},
	// Bug ZK-1706 issue: we have to expand the width of the content div when
	// align="left", others won't support
	setFlexSizeW_: function (n, zkn, width, isFlexMin) {
		this.$supers('setFlexSizeW_', arguments);
		if (!isFlexMin && this.getAlign() == 'start') {
			var cave = this.$n('cave');
			if (cave)
				cave.style.width = jq.px0(zk(this.$n()).contentWidth());
		}
	},
	/**
	 * Returns whether is in panel mold or not.
	 * @return boolean
	 */
	inPanelMold: function () {
		return this._mold == 'panel';
	},
	// protected
	onChildAdded_: function () {
		this.$supers('onChildAdded_', arguments);
		if (this.inPanelMold()) 
			this.rerender();
	},
	onChildRemoved_: function () {
		this.$supers('onChildRemoved_', arguments);
		if (!this.childReplacing_ && this.inPanelMold())
			this.rerender();
	}	
});
