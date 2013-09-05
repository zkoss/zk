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
	setVisible: function() {
		this.$supers('setVisible', arguments);
		if (this.desktop && !this.isSelected()) //Bug ZK-1618: not show if current tabpanel is not selected
			this.$n().style.display = 'none';
	},
	domClass_: function() {
		var cls = this.$supers('domClass_', arguments),
			tabbox = this.getTabbox();
		if (tabbox.inAccordionMold())
			cls += ' ' + this.$s('content');
		return cls;
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
		var tabbox = this.getTabbox();
		if(!tabbox) return; //Bug ZK-1808 removed tabpanel is no longer in hierarchy, and cannot be removed
		var accd = tabbox.inAccordionMold();

		if (accd && animation) {
			var zkp = zk(this.$n('cave'));
			if (toSel) {
				/* ZK-1441
				 * When a tabpanel is animating, set tabbox.animating
				 * to block other tabpanels enter _sel().
				 * Reference: _sel() in Tab.js
				 */
				tabbox._animating = true;
				zkp.slideDown(
					this,
					{'afterAnima': function(){delete tabbox._animating;}}
				);
			} else {
				zkp.slideUp(this);
			}
		} else {
			var $pl = jq(accd ? this.$n('cave') : this.$n()),
				vis = $pl.zk.isVisible();
			if (toSel) {
				if (!vis) {
					$pl.show();
					// Bug ZK-1454: Scrollbar forgets its position when switching tabs in Tabbox
					if (zk.ie >= 8 || zk.webkit)
						$pl.scrollTop(this._lastScrollTop);
					zUtl.fireShown(this);
				}
			} else if (vis) {
				zWatch.fireDown('onHide', this);
				// Bug ZK-1454: Scrollbar forgets its position when switching tabs in Tabbox
				if (zk.ie >= 8 || zk.webkit)
					this._lastScrollTop = $pl.scrollTop();
				$pl.hide();
			}
		}
	},
	getPanelContentHeight_: function () {
		var node = this.$n(),
			tabpanelsNode = this.parent && this.parent.$n(),
			panelContentHeight = tabpanelsNode &&
				(tabpanelsNode.scrollHeight + zk(tabpanelsNode).padBorderHeight());

		return Math.max(node && node.offsetHeight,panelContentHeight) ; // B50-ZK-298: concern panel height
	},
	_fixPanelHgh: function() {
		var tabbox = this.getTabbox(),
			tbx = tabbox.$n(),
			hgh = tbx.style.height;
		
		if (hgh && hgh != 'auto') {
			if (!tabbox.inAccordionMold()) {
				var n = this.$n(),
					isHor = tabbox.isHorizontal();

				hgh = isHor ? zk(tabbox).contentHeight() - zk(tabbox.tabs).offsetHeight() 
						    : zk(tabbox).contentHeight() - zk(n.parentNode).padBorderHeight();
					// B50-ZK-473: Tabpanel in vertical Tabbox should always have full height
				n.style.height = jq.px0(hgh);
			} else {
				var n = this.$n(),
					hgh = tbx.offsetHeight,
					zkp = zk(n.parentNode);
				hgh = hgh - zkp.padBorderHeight();
				for (var e = n.parentNode.firstChild; e; e = e.nextSibling)
					if (e != n)
						hgh -= e.offsetHeight;
				hgh -= n.firstChild.offsetHeight;
				var cave = this.$n('cave'),
					s = cave.style;
				s.height = jq.px0(hgh);
			}
		}
	},
	onSize: function() {
		var tabbox = this.getTabbox();
		if (tabbox.inAccordionMold() && !zk(this.$n('cave')).isVisible())
			return;
		this._fixPanelHgh();		//Bug 2104974
	},

	//bug #3014664
	setVflex: function (v) { //vflex ignored for Tabpanel
		if (v != 'min') v = false;
		this.$supers('setVflex', arguments);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for Tabpanel
		if (v != 'min') v = false;
		this.$supers('setHflex', arguments);
	},
	bind_: function(desktop) {
		this.$supers(zul.tab.Tabpanel, 'bind_', arguments);
		zWatch.listen({onSize: this});
		// B50-ZK-660: Dynamically generated accordion tabs cannot be closed
		var tab;
		if (this.getTabbox().inAccordionMold()
				&& (tab=this.getLinkedTab())) {
			
			if (!tab.$n())
				tab.unbind().bind(desktop);
			else if (!jq.isAncestor(this.$n(), tab.$n())) {
				// not display if got wrong tab,
				// it will fixed by Tabpanels#onChildAdded_ if tab add first
				// or by afterMount in tab#bind_ if panel add first
				var cave = this.$n('cave');
				if (cave) cave.style.display = 'none';
			}
		}
	},
	unbind_: function () {
		zWatch.unlisten({onSize: this});
		this._lastScrollTop = null;
		this.$supers(zul.tab.Tabpanel, 'unbind_', arguments);
	}
});