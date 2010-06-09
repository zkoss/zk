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
		closable: _zkf = function() {
			this.rerender();
		},
		image: _zkf,
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
		selected: function(selected) {
			if (this.desktop)
				this._sel();
		}
	},
	/**
	 * Returns the tabbox owns this component.
	 * @return Tabbox
	 */
	getTabbox: function() {
		return this.parent ? this.parent.parent : null;
	},
	/**
	 * Returns the index of this panel, or -1 if it doesn't belong to any tabs.
	 * @return int
	 */
	getIndex: function() {
		return this.getChildIndex();
	},
	getZclass: function() {
		if (this._zclass != null)
			return this._zclass;

		var tabbox = this.getTabbox();
		if (!tabbox) return 'z-tab';

		var mold = tabbox.getMold();
		return 'z-tab' + (mold == 'default' ? (tabbox.isVertical() ? '-ver': '') : '-' + mold);
	},
	/**
	 * Returns the panel associated with this tab.
	 * @return Tabpanel
	 */
	getLinkedPanel: function() {
		var w;
		return (w = this.getTabbox()) && (w = w.getTabpanels()) ?
			w.getChildAt(this.getIndex()): null;
	},
	_doCloseClick : function(evt) {
		if (!this._disabled) {
			this.fire('onClose');
			evt.stop();
		}
	},
	_toggleBtnOver : function(evt) {
		jq(evt.domTarget).toggleClass(this.getZclass() + "-close-over");
	},
	_sel: function(notify, init) {
		var tabbox = this.getTabbox();
		if (!tabbox) return;

		var	tabs = this.parent,
			oldtab = tabbox._selTab;
		if (oldtab != this || init) {
			if (oldtab && oldtab != this)
				this._setSel(oldtab, false, false, init);
			this._setSel(this, true, notify, init);
		}
	},
	_setSel: function(tab, toSel, notify, init) {
		var tabbox = this.getTabbox(),
			zcls = this.getZclass(),
			panel = tab.getLinkedPanel();
		if (tab.isSelected() == toSel && notify)
			return;

		if (toSel)
			tabbox._selTab = tab; //avoid loopback
		tab._selected = toSel;
		if (toSel)
			jq(tab).addClass(zcls + "-seld");
		else
			jq(tab).removeClass(zcls + "-seld");

		if (panel)
			panel._sel(toSel, !init);

		if (!tabbox.inAccordionMold()) {
			var tabs = this.parent;
			if (tabs) tabs._fixWidth();
		}
		
		if (tab == this) {
			if (tabbox.isVertical())
				tabs._scrollcheck("vsel", this);
			else if (!tabbox.inAccordionMold())
				tabs._scrollcheck("sel", this);
		}
		
		if (notify)
			this.fire('onSelect', {items: [this], reference: this.uuid});
	},
	//protected
	doClick_: function(evt) {
		if (this._disabled)
			return;
		this._sel(true);
		this.$supers('doClick_', arguments);
	},
	domClass_: function (no) {
		var scls = this.$supers('domClass_', arguments);
		if (!no || !no.zclass) {
			var added = this.isDisabled() ? this.getZclass() + '-disd' : '';
			if (added) scls += (scls ? ' ': '') + added;
		}
		return scls;
	},
	domContent_: function () {
		var label = zUtl.encodeXML(this.getLabel()),
			img = this.getImage();
		if (!label) label = '&nbsp;';
		if (!img) return label;
		img = '<img src="' + img + '" align="absmiddle" class="' + this.getZclass() + '-img"/>';
		return label ? img + ' ' + label: img;
	},
	bind_: function (desktop, skipper, after) {
		this.$supers(zul.tab.Tab, 'bind_', arguments);
		var closebtn = this.$n('close'),
			tab = this;
		if (closebtn) {
			this.domListen_(closebtn, "onClick", '_doCloseClick');
			if (!closebtn.style.cursor)
				closebtn.style.cursor = "default";
			if (zk.ie6_)
				this.domListen_(closebtn, "onMouseOver", '_toggleBtnOver')
					.domListen_(closebtn, "onMouseOut", '_toggleBtnOver');
		}

		after.push(function () {
			zk.afterMount(function () {
    			if (tab.isSelected()) 
    				tab._sel(false, true);
    			else if (tab.parent._isInited())
    				tab.parent._scrollcheck("init");
			});
		});
	},
	unbind_: function () {
		var closebtn = this.$n('close');
		if (closebtn) {
			this.domUnlisten_(closebtn, "onClick", '_doCloseClick');
			if (zk.ie6_)
				this.domUnlisten_(closebtn, "onMouseOver", '_toggleBtnOver')
					.domUnlisten_(closebtn, "onMouseOut", '_toggleBtnOver');
		}
		this.$supers(zul.tab.Tab, 'unbind_', arguments);
	},
	//event handler//
	onClose: function () {
		if (this.isSelected()) {
			var self = this,
				p = this.parent;
			
			// Bug 2931212, send onSelect after onClose
			setTimeout(function () {
    			if (!self.parent || self.parent != p)
    				return; // nothing to do
    			for (var tab = self; tab = tab.nextSibling;)
    				if (!tab.isDisabled()) {
    					tab._sel(true);
    					return;
    				}
    			for (var tab = self; tab = tab.previousSibling;)
    				if (!tab.isDisabled()) {
    					tab._sel(true);
    					return;
    				}
    		});
		}
	}
});
