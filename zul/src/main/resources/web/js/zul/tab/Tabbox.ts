/* Tabbox.ts

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

// TabboxSkipper is only used in this file. No need to export TabboxSkipper.
class TabboxSkipper extends zk.Skipper {
	skipper?: zk.Skipper;
	constructor(skipper?: zk.Skipper) {
		super();
		this.skipper = skipper;
	}
	override skipped(wgt: Tabbox, child: zk.Widget): boolean {
		return (wgt.toolbar === child && wgt.getMold() !== 'default') || !!this.skipper?.skipped(wgt, child);
	}
}

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
@zk.WrapClass('zul.tab.Tabbox')
export class Tabbox extends zul.Widget {
	_orient = 'top';
	_tabscroll = true;
	_maximalHeight = false;

	/* ZK-1441
	 * Reference: doClick_() in Tab.js, _sel() in Tabpanel.js
	 */
	_animating?: boolean = false;
	_nativebar = true;
	_panelSpacing?: string;
	tabs?: zul.tab.Tabs;
	tabpanels?: zul.tab.Tabpanels;
	toolbar?: zul.wgt.Toolbar;
	_selTab?: zul.tab.Tab;
	_scrolling?: boolean;
	_toolbarWidth?: number;
	_shallSize?: boolean;

	/**
	 * Returns whether the tab scrolling is enabled.
	 * Default: true.
	 * @return boolean
	 */
	isTabscroll(): boolean {
		return this._tabscroll;
	}

	/**
	 * Sets whether to eable the tab scrolling
	 * @param boolean tabscroll
	 */
	setTabscroll(tabscroll: boolean, opts?: Record<string, boolean>): this {
		const o = this._tabscroll;
		this._tabscroll = tabscroll;

		if (o !== tabscroll || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns the orient.
	 *
	 * <p>
	 * Default: "top".
	 *
	 * <p>
	 * Note: only the default mold supports it (not supported if accordion).
	 * @return String
	 */
	getOrient(): string {
		return this._orient;
	}

	/**
	 * Sets the orient.
	 *
	 * @param String orient either "top", "left", "bottom or "right".
	 * @since 7.0.0 "horizontal" is renamed to "top" and "vertical" is renamed to "left".
	 */
	setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;

		if (o !== orient || (opts && opts.force)) {
			if (orient == 'horizontal')
				this._orient = 'top';
			else if (orient == 'vertical')
				this._orient = 'left';
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns whether to use maximum height of all tabpanel in initial phase or not.
	 * <p>
	 * Default: false.
	 * @return boolean
	 * @since 7.0.0
	 */
	isMaximalHeight(): boolean {
		return this._maximalHeight;
	}

	/**
	 * Sets whether to use maximum height of all tabpanel in initial phase or not.
	 * <p>
	 * The Client ROD feature will be disabled if it is set to true.
	 * @param boolean maximalHeight
	 * @since 7.0.0
	 */
	setMaximalHeight(maximalHeight: boolean, opts?: Record<string, boolean>): this {
		const o = this._maximalHeight;
		this._maximalHeight = maximalHeight;

		if (o !== maximalHeight || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns the spacing between {@link Tabpanel}. This is used by certain
	 * molds, such as accordion.
	 * <p>
	 * Default: null (no spacing).
	 * @return String
	 */
	getPanelSpacing(): string | undefined {
		return this._panelSpacing;
	}

	/**
	 * Sets the spacing between {@link Tabpanel}. This is used by certain molds,
	 * such as accordion.
	 * @param String panelSpacing
	 */
	setPanelSpacing(panelSpacing: string, opts?: Record<string, boolean>): this {
		const o = this._panelSpacing;
		this._panelSpacing = panelSpacing;

		if (o !== panelSpacing || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns the tabs that this tabbox owns.
	 * @return zul.tab.Tabs
	 */
	getTabs(): zul.tab.Tabs | undefined {
		return this.tabs;
	}

	/**
	 * Returns the tabpanels that this tabbox owns.
	 * @return zul.tab.Tabpanels
	 */
	getTabpanels(): zul.tab.Tabpanels | undefined {
		return this.tabpanels;
	}

	/**
	 * Returns the auxiliary toolbar that this tabbox owns.
	 * @return zul.wgt.Toolbar
	 */
	getToolbar(): zul.wgt.Toolbar | undefined {
		return this.toolbar;
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var sc = super.domClass_(no);
		if (!no || !no.zclass) {
			var cls = this.inAccordionMold() ?
					this.$s('accordion') : this.$s(this.getOrient());
			sc += ' ' + cls;
		}
		return sc;
	}

	/**
	 * Returns whether it is a horizontal tabbox.
	 * @return boolean
	 */
	isHorizontal(): boolean {
		var orient = this.getOrient();
		return 'horizontal' == orient || 'top' == orient || 'bottom' == orient;
	}

	/**
	 * Returns whether it is the top orientation.
	 * @return boolean
	 */
	isTop(): boolean {
		var orient = this.getOrient();
		return 'horizontal' == orient || 'top' == orient;
	}

	/**
	 * Returns whether it is the bottom orientation.
	 * @return boolean
	 */
	isBottom(): boolean {
		return 'bottom' == this.getOrient();
	}

	/**
	 * Returns whether it is a vertical tabbox.
	 * @return boolean
	 */
	isVertical(): boolean {
		var orient = this.getOrient();
		return 'vertical' == orient || 'left' == orient || 'right' == orient;
	}

	/**
	 * Returns whether it is the right orientation.
	 * @return boolean
	 */
	isRight(): boolean {
		return 'right' == this.getOrient();
	}

	/**
	 * Returns whether it is the left orientation.
	 * @return boolean
	 */
	isLeft(): boolean {
		var orient = this.getOrient();
		return 'vertical' == orient || 'left' == orient;
	}

	/**
	 * Returns whether it is in the accordion mold.
	 * @return boolean
	 */
	inAccordionMold(): boolean {
		return this.getMold().indexOf('accordion') != -1;
	}

	/**
	 * Returns the selected index.
	 * @return int
	 */
	getSelectedIndex(): number {
		return this._selTab ? this._selTab.getIndex() : -1;
	}

	/**
	 * Sets the selected index.
	 * @param int index
	 */
	setSelectedIndex(index: number): this {
		if (this.tabs)
			this.setSelectedTab(this.tabs.getChildAt<zul.tab.Tab>(index));
		return this;
	}

	/**
	 * Returns the selected tab panel.
	 * @return Tabpanel
	 */
	getSelectedPanel(): zul.tab.Tabpanel | undefined {
		return this._selTab ? this._selTab.getLinkedPanel() : undefined;
	}

	/**
	 * Sets the selected tab panel.
	 * @param Tabpanel panel
	 */
	setSelectedPanel(panel: zul.tab.Tabpanel | undefined): this {
		if (panel && panel.getTabbox() != this)
			return this;
		var tab = panel?.getLinkedTab();
		if (tab)
			this.setSelectedTab(tab);
		return this;
	}

	/**
	 * Returns the selected tab.
	 * @return zul.tab.Tab
	 */
	getSelectedTab(): zul.tab.Tab | undefined {
		return this._selTab;
	}

	/**
	 * Sets the selected tab.
	 * @param tab zul.tab.Tab
	 */
	setSelectedTab(tab: zul.tab.Tab | undefined, fromServer?: boolean): this {
		if (this._selTab != tab) {
			this._setSel(tab, !fromServer);
			this._selTab = tab;
		}
		return this;
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, new TabboxSkipper(skipper), after); // F61-ZK-970.zul

		// used in Tabs.js
		this._scrolling = false;
		var toolbar = this.getToolbar();

		if (this.inAccordionMold())
			zWatch.listen({onResponse: this});
		else if (toolbar && this.getTabs()) {
			zWatch.listen({onResponse: this});
			this._toolbarWidth = jq(toolbar.$n_()).width();
		}

		for (var key = ['right', 'left', 'down', 'up'], le = key.length; le--;) {
			const btn = this.$n(key[le]);
			if (btn)
				this.domListen_(btn, 'onClick', '_doClick', key[le]);
		}
		this._fixMaxHeight();

		zk.afterMount(() => {
			const tabs = this.tabs,
				seltab = this._selTab;
			if (seltab && tabs) {
				if (this.isVertical())
					tabs._scrollcheck('vsel', seltab);
				else if (!this.inAccordionMold())
					tabs._scrollcheck('sel', seltab);
			}
		});
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onResponse: this});
		for (var key = ['right', 'left', 'down', 'up'], le = key.length; le--;) {
			const btn = this.$n(key[le]);
			if (btn)
				this.domUnlisten_(btn, 'onClick', '_doClick', key[le]);
		}
		this._toolbarWidth = undefined;
		super.unbind_(skipper, after, keepRod);
	}

	_doClick(evt: zk.Event, direction: string): void {
		if (!this.tabs || !this.tabs.nChildren) return; // nothing to do

		var cave = this.tabs.$n_('cave'),
			allTab = jq(cave).children(),
			move = 0,
			head = this.tabs.$n_(),
			isVert = this.isVertical(),
			scrollLength = isVert ? this.tabs._tabsScrollTop : this.tabs._tabsScrollLeft,
			offsetLength = isVert ? head.offsetHeight : head.offsetWidth,
			plus = scrollLength + offsetLength;
		//Scroll to next right tab
		switch (direction) {
		case 'right':
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetLeft + allTab[i].offsetWidth > plus) {
					move = allTab[i].offsetLeft + allTab[i].offsetWidth - scrollLength - offsetLength;
					if (!move || isNaN(move))
						return;
					this.tabs._doScroll('right', move);
					return;
				}
			}
			break;
		case 'left':
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetLeft >= scrollLength) {
					//if no Sibling tab no scroll
					var tabli = jq(allTab[i]).prev('li')[0];
					if (!tabli) return;
					move = scrollLength - tabli.offsetLeft;
					if (isNaN(move)) return;
					this.tabs._doScroll('left', move);
					return;
				}
			}
			move = scrollLength - allTab[allTab.length - 1].offsetLeft;
			if (isNaN(move)) return;
			this.tabs._doScroll('left', move);
			break;
		case 'up':
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetTop >= scrollLength) {
					var preli = jq(allTab[i]).prev('li')[0];
					if (!preli) return;
					move = scrollLength - preli.offsetTop;
					this.tabs._doScroll('up', move);
					return;
				}
			}
			var preli = allTab[allTab.length - 1];
			if (!preli) return;
			move = scrollLength - preli.offsetTop;
			this.tabs._doScroll('up', move);
			break;
		case 'down':
			for (var i = 0, count = allTab.length; i < count; i++) {
				if (allTab[i].offsetTop + allTab[i].offsetHeight > plus) {
					move = allTab[i].offsetTop + allTab[i].offsetHeight - scrollLength - offsetLength;
					if (!move || isNaN(move)) return;
					this.tabs._doScroll('down', move);
					return;
				}
			}
			break;
		}
	}

	/** Synchronizes the size immediately.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link #inServer} is true).
	 * You have to invoke this method only if you create this widget
	 * at client and add or remove children from this widget.
	 * @since 5.0.8
	 */
	syncSize(): void {
		this._shallSize = false;
		if (this.desktop)
			zUtl.fireSized(this, -1); //no beforeSize
	}

	onResponse(): void {
		if (this.inAccordionMold()) {
			if (this._shallSize)
				this.syncSize();
		} else if (this._toolbarWidth) { // accordion mold not support toolbar
			var toolbarWidth = jq(this.getToolbar()!.$n_()).width();
			if (toolbarWidth != this._toolbarWidth) { // toolbar width changed
				this._toolbarWidth = toolbarWidth;
				this.getTabs()!.onSize();
			}
		}
	}

	_syncSize(): void {
		if (this.desktop)
			this._shallSize = true;
	}

	//super//
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (child instanceof zul.wgt.Toolbar)
			this.toolbar = child;
		else if (child instanceof zul.tab.Tabs)
			this.tabs = child;
		else if (child instanceof zul.tab.Tabpanels) {
			this.tabpanels = child;
		}
		this.rerender();
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (child == this.toolbar)
			this.toolbar = undefined;
		else if (child == this.tabs)
			this.tabs = undefined;
		else if (child == this.tabpanels)
			this.tabpanels = undefined;
		if (!this.childReplacing_)
			this.rerender();
	}

	override setWidth(width?: string): this {
		super.setWidth(width);
		if (this.desktop)
			zUtl.fireSized(this, -1); //no beforeSize
		return this;
	}

	override setHeight(height?: string): this {
		super.setHeight(height);
		if (this.desktop)
			zUtl.fireSized(this, -1); //no beforeSize
		return this;
	}

	_fixMaxHeight(): void {
		if (this._maximalHeight) {
			var max = 0,
				pnls = this.getTabpanels()!,
				fc = pnls.firstChild;

			for (var c = fc; c; c = c.nextSibling) {
				var panel = c ? c.getCaveNode() : undefined;
				if (!panel)
					return;
				else {
					var hgh = jq(panel).outerHeight()!;
					if (hgh > max)
						max = hgh;
				}
			}

			for (var c = fc; c; c = c.nextSibling) {
				var panel = c.getCaveNode();
				if (panel)
					panel.style.height = jq.px0(max);
			}
		}
	}

	_setSel(newtab: zul.tab.Tab | undefined, notify: boolean): void {
		if (newtab) {
			var oldtab = this._selTab;
			if (oldtab != newtab) {
				if (oldtab && this.inAccordionMold()) {
					var p = newtab.getLinkedPanel();
					if (p) p._changeSel(oldtab.getLinkedPanel());
				}
				if (oldtab && oldtab != newtab)
					oldtab._sel(false, false);
				newtab._sel(true, notify);
			}
		}
	}
}