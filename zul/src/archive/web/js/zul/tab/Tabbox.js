/* Tabbox.js

{{IS_NOTE
	Purpose:

	Description:

	History:
		Fri Jan 23 10:32:34 TST 2009, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
/** The tabbox related widgets, such as tabbox and tabpanel.
 */
//zk.$package('zul.tab');
/**
 * A tabbox.
 *
 * <p>
 * Event:
 * <ol>
 * <li>onSelect is sent when user changes the tab.</li>
 * </ol>
 *
 * <p>
 * Mold:
 * <dl>
 * <dt>default</dt>
 * <dd>The default tabbox.</dd>
 * <dt>accordion</dt>
 * <dd>The accordion tabbox.</dd>
 * </dl>
 *
 * <p>{@link Toolbar} only works in the horizontal default mold and
 * the {@link #isTabscroll()} to be true.
 *  
 * <p>Default {@link #getZclass}: z-tabbox.
 * @import zul.wgt.Toolbar
 */
zul.tab.Tabbox = zk.$extends(zul.Widget, {
	_orient: 'horizontal',
	_tabscroll: true,
	/* ZK-1441
	 * Reference: _sel() in Tab.js, Tabpanel.js
	 */
	_animating: false,

	$define: {
    	/**
    	 * Returns whether the tab scrolling is enabled.
    	 * Default: true.
    	 * @return boolean
    	 */
    	/**
    	 * Sets whether to eable the tab scrolling
    	 * @param boolean tabscroll
    	 */
		tabscroll: _zkf = function () {
			this.rerender();
		},
		/**
		 * Returns the orient.
		 *
		 * <p>
		 * Default: "horizontal".
		 *
		 * <p>
		 * Note: only the default mold supports it (not supported if accordion).
		 * @return String
		 */
		/**
		 * Sets the orient.
		 *
		 * @param String orient
		 *            either "horizontal" or "vertical".
		 */
		orient: _zkf,
		/**
		 * Returns the spacing between {@link Tabpanel}. This is used by certain
		 * molds, such as accordion.
		 * <p>
		 * Default: null (no spacing).
		 * @return String
		 */
		/**
		 * Sets the spacing between {@link Tabpanel}. This is used by certain molds,
		 * such as accordion.
		 * @param String panelSpacing
		 */
		panelSpacing: _zkf
	},
	/**
	 * Returns the tabs that this tabbox owns.
	 * @return Tabs
	 */
	getTabs: function () {
		return this.tabs;
	},
	/**
	 * Returns the tabpanels that this tabbox owns.
	 * @return Tabpanels
	 */
	getTabpanels: function () {
		return this.tabpanels;
	},
	/**
	 * Returns the auxiliary toolbar that this tabbox owns.
	 * @return zul.wgt.Toolbar
	 */
	getToolbar: function () {
		return this.toolbar;
	},
	
	domClass_: function (no) {
		var cls = this.$supers('domClass_', arguments),
			orientCls = this.isHorizontalTop() ? '' : ' ' + this.$s(this.getOrient());
		cls += this.inAccordionMold() ? ' ' + this.$s(this.getMold()) : orientCls;
		return cls; 
	},
	/**
	 * Returns whether it is a horizontal tabbox.
	 * @return boolean
	 */
	isHorizontal: function() {
		return this.getOrient().indexOf('horizontal') != -1;
	},
	isHorizontalTop: function() {
		return 'horizontal' == this.getOrient();
	},
	isHorizontalBottom: function() {
		return 'horizontal-bottom' == this.getOrient();
	},
	/**
	 * Returns whether it is a vertical tabbox.
	 * @return boolean
	 */
	isVertical: function() {
		return this.getOrient().indexOf('vertical') != -1;
	},
	isVerticalRight: function() {
		return 'vertical-right' == this.getOrient();
	},
	isVerticalLeft: function() {
		return 'vertical' == this.getOrient();
	},
	/**
	 * Returns whether it is in the accordion mold.
	 * @return boolean
	 */
	inAccordionMold: function () {
		return this.getMold().indexOf('accordion') != -1;
	},
	/**
	 * Returns the selected index.
	 * @return int
	 */
	getSelectedIndex: function() {
		return this._selTab ? this._selTab.getIndex() : -1 ;
	},
	/**
	 * Sets the selected index.
	 * @param int index
	 */
	setSelectedIndex: function(index) {
		if (this.tabs)
			this.setSelectedTab(this.tabs.getChildAt(index));
	},
	/**
	 * Returns the selected tab panel.
	 * @return Tabpanel
	 */
	getSelectedPanel: function() {
		return this._selTab ? this._selTab.getLinkedPanel() : null;
	},
	/**
	 * Sets the selected tab panel.
	 * @param Tabpanel panel
	 */
	setSelectedPanel: function(panel) {
		if (panel && panel.getTabbox() != this)
			return
		var tab = panel.getLinkedTab();
		if (tab)
			this.setSelectedTab(tab);
	},
	/**
	 * Returns the selected tab.
	 * @return Tab
	 */
	getSelectedTab: function() {
		return this._selTab;
	},
	/**
	 * Sets the selected tab.
	 * @param Tab tab
	 */
	setSelectedTab: function(tab) {
		if (this._selTab != tab) {
			if (tab)
				tab.setSelected(true);
				//it will set _selTab (but we still set it later just in case)
			this._selTab = tab;
		}
	},
	bind_: function (desktop, skipper, after) {
		this.$supers(zul.tab.Tabbox, 'bind_', arguments);
		
		// used in Tabs.js
		this._scrolling = false;
		var tab = this._selTab,
			toolbar = this.getToolbar();
		
		if (this.inAccordionMold())
			zWatch.listen({onResponse: this});
		else if (toolbar && this.getTabs()) {
			zWatch.listen({onResponse: this});
			this._toolbarWidth = jq(toolbar.$n()).width();
		}
		
		for (var btn, key = ['right', 'left', 'down', 'up'], le = key.length; le--;) 
			if (btn = this.$n(key[le])) 				
				this.domListen_(btn, 'onClick', '_doClick');
		
		if (tab)
			after.push(function() {
				tab.setSelected(true);
			});

		
	},
	unbind_: function () {
		zWatch.unlisten({onResponse: this});
		for (var btn, key = ['right', 'left', 'down', 'up'], le = key.length; le--;)
			if (btn = this.$n(key[le]))
				this.domUnlisten_(btn, 'onClick');
		this._toolbarWidth = null;
		this.$supers(zul.tab.Tabbox, 'unbind_', arguments);
	},
	_doClick: function(evt) {
		var cave = this.tabs.$n('cave'),
			allTab =  jq(cave).children();
		
		if (!allTab.length) return; // nothing to do	
			
		var ele = evt.domTarget,
			$parent = jq(ele).parent(),
			move = 0,
			tabbox = this,
			head = this.tabs.$n(),
			scrollLength = tabbox.isVertical() ? head.scrollTop : head.scrollLeft,
			offsetLength = tabbox.isVertical() ? head.offsetHeight : head.offsetWidth,
			plus = scrollLength + offsetLength,
			self = this,
			isParentIdEq = function(id) {
				return $parent && $parent.attr('id') == (self.uuid + '-' + id);
			};
		//Scroll to next right tab
		if (ele.id == this.uuid + '-right' || isParentIdEq('right')) {
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetLeft + allTab[i].offsetWidth > plus) {
					move = allTab[i].offsetLeft + allTab[i].offsetWidth - scrollLength - offsetLength;
					if (!move || isNaN(move))
						return;
					this.tabs._doScroll('right', move);
					return;
				}
			}
		} else if (ele.id == this.uuid + '-left' || isParentIdEq('left')) {//Scroll to next left tab
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetLeft >= scrollLength) {
					//if no Sibling tab no sroll
					var tabli = jq(allTab[i]).prev('li')[0];
					if (!tabli)  return;
					move = scrollLength - tabli.offsetLeft;
					if (isNaN(move)) return;
					this.tabs._doScroll('left', move);
					return;
				};
			};
			move = scrollLength - allTab[allTab.length-1].offsetLeft;
			if (isNaN(move)) return;
			this.tabs._doScroll('left', move);
			return;
		} else if (ele.id == this.uuid + '-up' || isParentIdEq('up')) {
				for (var i = 0, count = allTab.length; i < count; i++) {
					if (allTab[i].offsetTop >= scrollLength) {
						var preli = jq(allTab[i]).prev('li')[0];
						if (!preli) return;
						move = scrollLength - preli.offsetTop ;
						this.tabs._doScroll('up', move);
						return;
					};
				};
				var preli = allTab[allTab.length-1];
				if (!preli) return;
				move = scrollLength - preli.offsetTop ;
				this.tabs._doScroll('up', move);
				return;
		} else if (ele.id == this.uuid + '-down' || isParentIdEq('down')) {
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetTop + allTab[i].offsetHeight > plus) {
					move = allTab[i].offsetTop + allTab[i].offsetHeight - scrollLength - offsetLength;
					if (!move || isNaN(move)) return ;
					this.tabs._doScroll('down', move);
					return;
				};
			};
		}
	},
	/** Synchronizes the size immediately.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link #inServer} is true).
	 * You have to invoke this method only if you create this widget
	 * at client and add or remove children from this widget.
	 * @since 5.0.8
	 */
	syncSize: function () {
		this._shallSize = false;
		if (this.desktop)
			zUtl.fireSized(this, -1); //no beforeSize
	},
	onResponse: function () {
		if (this.inAccordionMold()) {
			if (this._shallSize)
				this.syncSize();
		} else if (this._toolbarWidth) { // accordion mold not support toolbar
			var toolbarWidth = jq(this.getToolbar().$n()).width();
			if (toolbarWidth != this._toolbarWidth) { // toolbar width changed
				this._toolbarWidth = toolbarWidth;
				this.getTabs().onSize();
			}
		}
	},
	_syncSize: function () {
		if (this.desktop)
			this._shallSize = true;
	},
	//super//
	removeChildHTML_: function (child) {
		this.$supers('removeChildHTML_', arguments);
		if (this.isVertical() && child.$instanceof(zul.tab.Tabs))
			jq(child.uuid + '-line', zk).remove();
	},
	onChildAdded_: function (child) {
		this.$supers('onChildAdded_', arguments);
		if (child.$instanceof(zul.wgt.Toolbar))
			this.toolbar = child;
		else if (child.$instanceof(zul.tab.Tabs))
			this.tabs = child;
		else if (child.$instanceof(zul.tab.Tabpanels)) {
			this.tabpanels = child;
		}
		this.rerender();
	},
	onChildRemoved_: function (child) {
		this.$supers('onChildRemoved_', arguments);
		if (child == this.toolbar)
			this.toolbar = null;
		else if (child == this.tabs)
			this.tabs = null;
		else if (child == this.tabpanels)
			this.tabpanels = null;
		if (!this.childReplacing_)
			this.rerender();
	},
	setWidth: function (width) {
		this.$supers('setWidth', arguments);
		if (this.desktop)
			zUtl.fireSized(this, -1); //no beforeSize
	},
	setHeight: function (height) {
		this.$supers('setHeight', arguments);
		if (this.desktop)
			zUtl.fireSized(this, -1); //no beforeSize
	}
});
