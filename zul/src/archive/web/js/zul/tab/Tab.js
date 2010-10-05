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
			if (oldtab && tabbox.inAccordionMold()) {
				var p = this.getLinkedPanel();
				if (p) p._changeSel(oldtab.getLinkedPanel());
			}
			if (oldtab && oldtab != this)
				this._setSel(oldtab, false, false, init);
			this._setSel(this, true, notify, init);
		}
	},
	_setSel: function(tab, toSel, notify, init) {
		var tabbox = this.getTabbox(),
			zcls = this.getZclass(),
			panel = tab.getLinkedPanel(),
			bound = this.desktop;
		if (tab.isSelected() == toSel && notify)
			return;

		if (toSel)
			tabbox._selTab = tab; //avoid loopback
		tab._selected = toSel;
		
		if (!bound) return;
		
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
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', this.parent);
			zWatch.fireDown('onSize', this.parent);
		}
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.desktop) {
			zWatch.fireDown('beforeSize', this.parent);
			zWatch.fireDown('onSize', this.parent);
		}
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
	//bug #3014664
	setVflex: function (v) { //vflex ignored for Tab
		if (v != 'min') v = false;
		this.$super(zul.tab.Tab, 'setVflex', v);
	},
	//bug #3014664
	setHflex: function (v) { //hflex ignored for Tab
		if (v != 'min') v = false;
		this.$super(zul.tab.Tab, 'setHflex', v);
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

		after.push(function () {tab.parent._fixHgh();});
			//Bug 3022274: required so it is is called before, say, panel's slideDown
			//_sel will invoke _fixWidth but it is too late since it uses afterMount
		after.push(function () {
			zk.afterMount(function () {
    			if (tab.isSelected()) 
    				tab._sel(false, true);
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
		if (this.getTabbox().inAccordionMold()) {
			this.getTabbox()._syncSize();
		}
	}
});
