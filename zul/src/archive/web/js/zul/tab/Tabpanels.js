/* Tabpanels.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:57 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/

(function () {
	function _syncSelectedPanels(panels) {
		//Note: _selTab is in tabbox, while _selPnl is in tabpanels
		var box;
		if (panels.desktop && (box = panels.getTabbox())) {
			var oldSel = panels._selPnl,
				sel = box._selTab;
			if (oldSel != (sel && (sel = sel.getLinkedPanel()))) {
				if (oldSel && oldSel.desktop) oldSel._sel(false, true);
				if (sel) sel._sel(true, true);
				panels._selPnl = sel;
			}
		}
	}

/**
 * A collection of tab panels.
 *
 * <p>Default {@link #getZclass}: z-tabpanels.
 */
zul.tab.Tabpanels = zk.$extends(zul.Widget, {
	/** Returns the tabbox owns this component.
	 * @return Tabbox
	 */
	getTabbox: function () {
		return this.parent;
	},
	//bug #3014664
	setVflex: function (v) { //vflex ignored for Tabpanels
		if (v != 'min') v = false;
		this.$supers('setVflex', arguments);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for Tabpanels
		if (v != 'min') v = false;
		this.$supers('setHflex', arguments);
	},
	bind_: function () {
		this.$supers(zul.tab.Tabpanels, 'bind_', arguments);
		zWatch.listen({onResponse: this});
	},
	unbind_: function () {
		zWatch.unlisten({onResponse: this});
		this.$supers(zul.tab.Tabpanels, 'unbind_', arguments);
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		this._shallSync = true;
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		// sync select status if tabbox not in accordion mold or
		// the child cave is already visible
		var tabbox = this.getTabbox(),
			cave;
		if (tabbox && (!tabbox.inAccordionMold()
				|| (cave = child.$n('cave')) && cave.style.display != 'none'))
			this._shallSync = true;
	},
	onResponse: function () {
		//bug B65-ZK-1785 synchronize selection only once in the end after all removes have finished
		if (this._shallSync) {
			_syncSelectedPanels(this);
			this._shallSync = false;
		}
	}
});
})();
