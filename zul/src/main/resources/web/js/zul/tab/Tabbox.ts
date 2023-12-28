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
 * the {@link isTabscroll} to be true.
 *
 * @defaultValue {@link getZclass}: z-tabbox.
 * @import zul.wgt.Toolbar
 */
@zk.WrapClass('zul.tab.Tabbox')
export class Tabbox extends zul.Widget {
	/** @internal */
	_orient = 'top';
	/** @internal */
	_tabscroll = true;
	/** @internal */
	_maximalHeight = false;

	/* ZK-1441
	 * Reference: doClick_() in Tab.js, _sel() in Tabpanel.js
	 */
	/** @internal */
	_animating?: boolean = false;
	/** @internal */
	_nativebar = true;
	/** @internal */
	_panelSpacing?: string;
	tabs?: zul.tab.Tabs;
	tabpanels?: zul.tab.Tabpanels;
	toolbar?: zul.wgt.Toolbar;
	/** @internal */
	_selTab?: zul.tab.Tab;
	/** @internal */
	_scrolling?: boolean;
	/** @internal */
	_toolbarWidth?: number;
	/** @internal */
	_shallSize?: boolean;

	/**
	 * @returns whether the tab scrolling is enabled.
	 * @defaultValue `true`.
	 */
	isTabscroll(): boolean {
		return this._tabscroll;
	}

	/**
	 * Sets whether to eable the tab scrolling
	 */
	setTabscroll(tabscroll: boolean, opts?: Record<string, boolean>): this {
		const o = this._tabscroll;
		this._tabscroll = tabscroll;

		if (o !== tabscroll || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the orient.
	 * @defaultValue `"top"`.
	 * <p>
	 * Note: only the default mold supports it (not supported if accordion).
	 */
	getOrient(): string {
		return this._orient;
	}

	/**
	 * Sets the orient.
	 *
	 * @param orient - either "top", "left", "bottom or "right".
	 * @since 7.0.0 "horizontal" is renamed to "top" and "vertical" is renamed to "left".
	 */
	setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;

		if (o !== orient || opts?.force) {
			if (orient == 'horizontal')
				this._orient = 'top';
			else if (orient == 'vertical')
				this._orient = 'left';
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns whether to use maximum height of all tabpanel in initial phase or not.
	 * @defaultValue `false`.
	 * @since 7.0.0
	 */
	isMaximalHeight(): boolean {
		return this._maximalHeight;
	}

	/**
	 * Sets whether to use maximum height of all tabpanel in initial phase or not.
	 * <p>
	 * The Client ROD feature will be disabled if it is set to true.
	 * @since 7.0.0
	 */
	setMaximalHeight(maximalHeight: boolean, opts?: Record<string, boolean>): this {
		const o = this._maximalHeight;
		this._maximalHeight = maximalHeight;

		if (o !== maximalHeight || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the spacing between {@link Tabpanel}. This is used by certain
	 * molds, such as accordion.
	 * @defaultValue `null` (no spacing).
	 */
	getPanelSpacing(): string | undefined {
		return this._panelSpacing;
	}

	/**
	 * Sets the spacing between {@link Tabpanel}. This is used by certain molds,
	 * such as accordion.
	 */
	setPanelSpacing(panelSpacing: string, opts?: Record<string, boolean>): this {
		const o = this._panelSpacing;
		this._panelSpacing = panelSpacing;

		if (o !== panelSpacing || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the tabs that this tabbox owns.
	 */
	getTabs(): zul.tab.Tabs | undefined {
		return this.tabs;
	}

	/**
	 * @returns the tabpanels that this tabbox owns.
	 */
	getTabpanels(): zul.tab.Tabpanels | undefined {
		return this.tabpanels;
	}

	/**
	 * @returns the auxiliary toolbar that this tabbox owns.
	 */
	getToolbar(): zul.wgt.Toolbar | undefined {
		return this.toolbar;
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		let scHTML = super.domClass_(no);
		if (!no || !no.zclass) {
			var cls = this.inAccordionMold() ?
					this.$s('accordion') : this.$s(this.getOrient());
			scHTML += ' ' + /*safe*/ cls;
		}
		return scHTML;
	}

	/**
	 * @returns whether it is a horizontal tabbox.
	 */
	isHorizontal(): boolean {
		var orient = this.getOrient();
		return 'horizontal' == orient || 'top' == orient || 'bottom' == orient;
	}

	/**
	 * @returns whether it is the top orientation.
	 */
	isTop(): boolean {
		var orient = this.getOrient();
		return 'horizontal' == orient || 'top' == orient;
	}

	/**
	 * @returns whether it is the bottom orientation.
	 */
	isBottom(): boolean {
		return 'bottom' == this.getOrient();
	}

	/**
	 * @returns whether it is a vertical tabbox.
	 */
	isVertical(): boolean {
		var orient = this.getOrient();
		return 'vertical' == orient || 'left' == orient || 'right' == orient;
	}

	/**
	 * @returns whether it is the right orientation.
	 */
	isRight(): boolean {
		return 'right' == this.getOrient();
	}

	/**
	 * @returns whether it is the left orientation.
	 */
	isLeft(): boolean {
		var orient = this.getOrient();
		return 'vertical' == orient || 'left' == orient;
	}

	/**
	 * @returns whether it is in the accordion mold.
	 */
	inAccordionMold(): boolean {
		return this.getMold().includes('accordion');
	}

	/**
	 * @returns the selected index.
	 */
	getSelectedIndex(): number {
		return this._selTab ? this._selTab.getIndex() : -1;
	}

	/**
	 * Sets the selected index.
	 */
	setSelectedIndex(selectedIndex: number): this {
		if (this.tabs)
			this.setSelectedTab(this.tabs.getChildAt<zul.tab.Tab>(selectedIndex));
		return this;
	}

	/**
	 * @returns the selected tab panel.
	 */
	getSelectedPanel(): zul.tab.Tabpanel | undefined {
		return this._selTab ? this._selTab.getLinkedPanel() : undefined;
	}

	/**
	 * Sets the selected tab panel.
	 */
	setSelectedPanel(selectedPanel: zul.tab.Tabpanel | undefined): this {
		if (selectedPanel && selectedPanel.getTabbox() != this)
			return this;
		var tab = selectedPanel?.getLinkedTab();
		if (tab)
			this.setSelectedTab(tab);
		return this;
	}

	/**
	 * @returns the selected tab.
	 */
	getSelectedTab(): zul.tab.Tab | undefined {
		return this._selTab;
	}

	/**
	 * Sets the selected tab.
	 */
	setSelectedTab(selectedTab: zul.tab.Tab | undefined, fromServer?: boolean): this {
		if (this._selTab != selectedTab) {
			this._setSel(selectedTab, !fromServer);
			this._selTab = selectedTab;
		}
		return this;
	}

	/** @internal */
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

	/** @internal */
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

	/** @internal */
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

	/**
	 * Synchronizes the size immediately.
	 * This method is called automatically if the widget is created
	 * at the server (i.e., {@link inServer} is true).
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

	/** @internal */
	_syncSize(): void {
		if (this.desktop)
			this._shallSize = true;
	}

	/** @internal */
	override beforeChildReplaced_(oldTabs: zul.tab.Tabs, newTabs: zul.tab.Tabs): void {
		// NOTE: At this point, `this.tabs === oldTabs`. Thus, `this.setSelectedIndex()` will set for `oldTabs` not `newTabs`.
		newTabs.getChildAt<zul.tab.Tab>(this.getSelectedIndex())!.setSelected(true);
	}
	/** @internal */
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

	/** @internal */
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

	/** @internal */
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

	/** @internal */
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