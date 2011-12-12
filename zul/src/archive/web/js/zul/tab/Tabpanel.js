/* Tabpanel.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:33:02 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
/**
 * A tab panel.
 * <p>Default {@link #getZclass}: z-tabpanel.
 */
zul.tab.Tabpanel = zk.$extends(zul.Widget, {
	/** Returns the tabbox owns this component.
	 * @return Tabbox
	 */
	getTabbox: function() {
		return this.parent ? this.parent.parent : null;
	},
	isVisible: function() {
		return this.$supers('isVisible', arguments) && this.isSelected();
	},
	getZclass: function() {
		if (this._zclass != null)
			return this._zclass;
			
		var tabbox = this.getTabbox();
		if (!tabbox) return 'z-tabpanel';
		
		var mold = tabbox.getMold();
		return 'z-tabpanel' + (mold == "default" ? (tabbox.isVertical() ? '-ver' : '') : '-' + mold);
	},
	/** Returns the tab associated with this tab panel.
	 * @return Tab
	 */
	getLinkedTab: function() {
		var tabbox =  this.getTabbox();
		if (!tabbox) return null;
		
		var tabs = tabbox.getTabs();
		return tabs ? tabs.getChildAt(this.getIndex()) : null;
	},
	/** Returns the index of this panel, or -1 if it doesn't belong to any
	 * tabpanels.
	 * @return int
	 */
	getIndex:function() {
		return this.getChildIndex();
	},
	/** Returns whether this tab panel is selected.
	 * @return boolean
	 */
	isSelected: function() {
		var tab = this.getLinkedTab();
		return tab && tab.isSelected();
	},
	// Bug 3026669
	_changeSel: function (oldPanel) {
		if (oldPanel) {
			var cave = this.$n('cave');
			if (cave && !cave.style.height && (oldPanel = oldPanel.$n('cave')))
				cave.style.height = oldPanel.style.height;
		}
	},
	_sel: function (toSel, animation) { //don't rename (zkmax counts on it)!!
		var accd = this.getTabbox().inAccordionMold();
		if (accd && animation) {
			var p = this.$n("cave");
			zk(p)[toSel ? "slideDown" : "slideUp"](this);
		} else {
			var $pl = jq(accd ? this.$n("cave") : this.$n()),
				vis = $pl.zk.isVisible();
			if (toSel) {
				if (!vis) {
					$pl.show();
					zUtl.fireShown(this);
				}
			} else if (vis) {
				zWatch.fireDown('onHide', this);
				$pl.hide();
			}
		}
	},
	_fixPanelHgh: function() {
		var tabbox = this.getTabbox();
		var tbx = tabbox.$n(),
		hgh = tbx.style.height;
		if (hgh && hgh != "auto") {
			if (!tabbox.inAccordionMold()) {
				var n = this.$n(),
					isHor = tabbox.isHorizontal();
				hgh = isHor ?
					zk(n.parentNode).vflexHeight(): n.parentNode.clientHeight;
					// B50-ZK-473: Tabpanel in vertical Tabbox should always have full height
				if (zk.ie8)
					hgh -= 1; // show the bottom border
				zk(n).setOffsetHeight(hgh);

				// Bug ZK-473
				if (zk.ie6_ && isHor) {
					var s = this.$n('cave').style,
					z = s.zoom;
					s.zoom = 1;
					s.zoom = z;
				}
			} else {
				var n = this.$n(),
					hgh = zk(tbx).revisedHeight(tbx.offsetHeight);
				hgh = zk(n.parentNode).revisedHeight(hgh);
				
				// fixed Opera 10.5+ bug
				if (zk.opera) {
					var parent;
					if ((parent = tbx.parentNode) && tbx.style.height == '100%')
						hgh = zk(parent).revisedHeight(parent.offsetHeight);
				}
				
				for (var e = n.parentNode.firstChild; e; e = e.nextSibling)
					if (e != n)
						hgh -= e.offsetHeight;
				hgh -= n.firstChild.offsetHeight;
				hgh = zk(n = n.lastChild).revisedHeight(hgh);
				if (zk.ie8)
					hgh -= 1; // show the bottom border
				var cave = this.getCaveNode(),
					s = cave.style;
				s.height = jq.px0(hgh);
			}
		}
	},
	domClass_: function () {
		var cls = this.$supers('domClass_', arguments);
		if (this.getTabbox().inAccordionMold())
			cls += ' ' + this.getZclass() + '-cnt';
		return cls;
	},
	onSize: function() {
		var tabbox = this.getTabbox();
		if (tabbox.inAccordionMold() && !zk(this.$n("cave")).isVisible())
			return;
		this._fixPanelHgh();		//Bug 2104974
		
		//Bug 2526699 - (add zk.ie7)
		if (zk.ie && !zk.ie8) zk(tabbox.$n()).redoCSS();
	},

	//bug #3014664
	setVflex: function (v) { //vflex ignored for Tabpanel
		if (v != 'min') v = false;
		this.$super(zul.tab.Tabpanel, 'setVflex', v);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for Tabpanel
		if (v != 'min') v = false;
		this.$super(zul.tab.Tabpanel, 'setHflex', v);
	},
	bind_: function(desktop, skipper, after) {
		this.$supers(zul.tab.Tabpanel, 'bind_', arguments);
		zWatch.listen({onSize: this});
		// B50-ZK-660: Dynamically generated accordion tabs cannot be closed
		var tab;
		if (this.getTabbox().inAccordionMold()
				&& (tab=this.getLinkedTab()) && !tab.$n()) {
			tab.unbind();
			tab.bind(desktop);
		}
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this});
		this.$supers(zul.tab.Tabpanel, 'unbind_', arguments);
	}
});