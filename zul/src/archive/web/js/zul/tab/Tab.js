/* Tab.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:51 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
(function () {

	// ZK-886, called by unbind_ and rerender
	// this._oldId used in tab.js
	// this.$n() will be cleared during rerender
	// but LinkedPanel.firstChild will not,
	// the condition LinkedPanel.firstChild != this.$n()
	// will get the wrong result
	// delete it later for the invalidate() case
	function _logId(wgt) {
		if (!wgt._oldId) {
			wgt._oldId = wgt.uuid;
			setTimeout(function () {
				delete wgt._oldId;
			}, 0);
		}
	}
/**
 * A tab.
 * <p>
 * Default {@link #getZclass}: z-tab.
 */
zul.tab.Tab = zk.$extends(zul.LabelImageWidget, {
	$init: function () {
		this.$supers('$init', arguments);
		this.listen({onClose: this}, -1000);
	},
	$define: {
		/**
		 * Returns whether this tab is closable. If closable, a button is displayed
		 * and the onClose event is sent if an user clicks the button.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		/**
		 * Sets whether this tab is closable. If closable, a button is displayed and
		 * the onClose event is sent if an user clicks the button.
		 * <p>
		 * Default: false.
		 * @param boolean closable
		 */
		closable: _zkf = function () {
			this.rerender();
		},
		image: function (v) {
			if (v && this._preloadImag) zUtl.loadImage(v);
			this.rerender();
		},
		/**
		 * Returns whether this tab is disabled.
		 * <p>
		 * Default: false.
		 * @return boolean
		 */
		/**
		 * Sets whether this tab is disabled. If a tab is disabled, then it cann't
		 * be selected or closed by user, but it still can be controlled by server
		 * side program.
		 * @param boolean disabled
		 */
		disabled: _zkf,
		/**
		 * Returns whether this tab is selected.
		 * @return boolean
		 */
		/**
		 * Sets whether this tab is selected.
		 * @param boolean selected
		 */
		selected: function (selected) {
			this._sel();
		}
	},
	/**
	 * Returns the tabbox owns this component.
	 * @return Tabbox
	 */
	getTabbox: function () {
		return this.parent ? this.parent.parent : null;
	},
	/**
	 * Returns the index of this panel, or -1 if it doesn't belong to any tabs.
	 * @return int
	 */
	getIndex: function () {
		return this.getChildIndex();
	},
	/**
	 * Returns the panel associated with this tab.
	 * @return Tabpanel
	 */
	getLinkedPanel: function () {
		var w;
		return (w = this.getTabbox()) && (w = w.getTabpanels()) ?
			w.getChildAt(this.getIndex()) : null;
	},
	_doCloseClick: function (evt) {
		if (!this._disabled) {
			this.fire('onClose');
			evt.stop();
		}
	},
	_sel: function (notify, init) {
		var tabbox = this.getTabbox();

		/* ZK-1441
		 * If tabbox is animating (end-user click different tabs quickly), ignore this action.
		 */
		if (!tabbox || tabbox._animating) return;

		var	tabs = this.parent,
			oldtab = tabbox._selTab;
		if (oldtab != this || init) {
			if (oldtab && tabbox.inAccordionMold()) {
				var p = this.getLinkedPanel();
				if (p) p._changeSel(oldtab.getLinkedPanel());
			}
			if (oldtab && oldtab != this)
				this._setSel(oldtab, false, false, init);
			this._setSel(this, true, notify, init);
		}
	},
	_setSel: function (tab, toSel, notify, init) {
		var tabbox = this.getTabbox(),
			panel = tab.getLinkedPanel();
		if (tab.isSelected() == toSel && notify)
			return;

		if (toSel) {
			tabbox._selTab = tab; //avoid loopback
			var ps;
			if (ps = tabbox.tabpanels) {
				if (ps._selPnl && ps._selPnl != panel) ps._selPnl._sel(false, tabbox.inAccordionMold());
				ps._selPnl = panel; //stored in tabpanels
			}
		}
		tab._selected = toSel;

		if (!this.desktop) return;

		if (toSel)
			jq(tab).addClass(this.$s('selected'));
		else
			jq(tab).removeClass(this.$s('selected'));

		if (panel && panel.isVisible()) //Bug ZK-1618: not show tabpanel if visible is false
			panel._sel(toSel, !init);

		if (!tabbox.inAccordionMold()) {
			var tabs = this.parent;
			if (tabs) tabs._fixWidth(toSel); //ZK-2810: don't set height to tabbox when deselect
		}

		if (tab == this) {
			if (tabbox.isVertical())
				tabs._scrollcheck('vsel', this);
			else if (!tabbox.inAccordionMold())
				tabs._scrollcheck('sel', this);
		}

		if (notify)
			this.fire('onSelect', {items: [this], reference: this.uuid});
	},
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop) {
			this._calcHgh();
			zUtl.fireSized(this.parent);
		}
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.desktop)
			zUtl.fireSized(this.parent);
	},
	_calcHgh: function () {
		var n = this.$n(),
			tabbox = this.getTabbox();

		if (!tabbox.isVertical()) {
			var r = tabbox.$n('right'),
				l = tabbox.$n('left'),
				tb = tabbox.toolbar,
				tabs = tabbox.tabs.$n(),
				hgh = jq.px0(tabs ? tabs.offsetHeight : 0);

			if (r && l) {
				r.style.height = l.style.height = hgh;
			}
			if (tb && (tb = tb.$n())) {
				tb.style.height = hgh;
			}
		}
	},
	//protected
	doClick_: function (evt) {
		if (this._disabled) return;
		this._sel(true);
		this.$supers('doClick_', arguments);
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			if (this.isDisabled()) scls += ' ' + this.$s('disabled');
			if (this.isSelected()) scls += ' ' + this.$s('selected');
		}
		return scls;
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage(),
			iconSclass = this.domIcon_();

		if (!label) label = '&nbsp;';
		if (!img && !iconSclass) return label;
		if (!img) {
			img = iconSclass;
		} else
			img = '<img src="' + img + '" class="' + this.$s('image') + '" alt="" aria-hidden="true"/>'
			+ (iconSclass ? ' ' + iconSclass : '');
		return label ? img + ' ' + label : img;
	},
	//bug #3014664
	setVflex: function (v) { //vflex ignored for Tab
		if (v != 'min') v = false;
		this.$supers('setVflex', arguments);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for Tab
		if (v != 'min') v = false;
		this.$supers('setHflex', arguments);
	},
	bind_: function (desktop, skipper, after) {
		this.$supers(zul.tab.Tab, 'bind_', arguments);
		var closebtn = this.isClosable() ? this.$n('cls') : null,
			tab = this;
		if (closebtn) {
			this.domListen_(closebtn, 'onClick', '_doCloseClick');
		}
		if (tab.isSelected()) {
			zk.afterMount(function () {
				if (tab.desktop && tab.getTabbox().inAccordionMold()) {
					var panel = tab.getLinkedPanel(),
						cave = panel ? panel.$n('cave') : null;
					// slide down if the cave node of panel is not visible before select
					if (cave && cave.style.display == 'none')
						panel._sel(true, true);
				}
				if (tab.isSelected()) tab._sel(false, true);
			});
		}

		if (this.getHeight())
			this._calcHgh();

		//ZK-3016 make sure parent always do scrollCheck on child bind
		this.parent._shallCheck = true;
	},
	unbind_: function () {
		var closebtn = this.$n('cls');
		// ZK-886
		_logId(this);
		if (closebtn) {
			this.domUnlisten_(closebtn, 'onClick', '_doCloseClick');
		}
		this.$supers(zul.tab.Tab, 'unbind_', arguments);
	},
	//event handler//
	onClose: function () {
		if (this.getTabbox().inAccordionMold()) {
			this.getTabbox()._syncSize();
		}
	},
	deferRedrawHTML_: function (out) {
		var tbx = this.getTabbox(),
			tag = tbx.inAccordionMold() ? 'div' : 'li';
		out.push('<', tag, this.domAttrs_({domClass: 1}), ' class="z-renderdefer"></', tag,'>');
	},
	rerender: function (skipper) {
		// ZK-886
		if (this.desktop)
			_logId(this);
		this.$supers(zul.tab.Tab, 'rerender', arguments);
	},
	contentRenderer_: function (out) {
		out.push('<span id="', this.uuid, '-cnt" class="', this.$s('text'), '">', this.domContent_(), '</span>');
	}
});
/** @class zul.tab.TabRenderer
 * The renderer used to render a Tab.
 * It is designed to be overriden
 * @since 5.0.5
 */
zul.tab.TabRenderer = {
	/** Check the Tab whether to render the frame
	 */
	isFrameRequired: function () {
		return false;
	}
};
})();
