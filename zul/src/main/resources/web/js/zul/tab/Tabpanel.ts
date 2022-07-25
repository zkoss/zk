/* Tabpanel.ts

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
@zk.WrapClass('zul.tab.Tabpanel')
export class Tabpanel extends zul.ContainerWidget {
	override parent!: zul.tab.Tabpanels | undefined;
	_lastScrollTop?: number | undefined;

	/** Returns the tabbox owns this component.
	 * @return zul.tab.Tabbox
	 */
	getTabbox(): zul.tab.Tabbox | undefined {
		return this.parent ? this.parent.parent : undefined;
	}

	override isVisible(strict?: boolean): boolean {
		return super.isVisible() && this.isSelected();
	}

	override setVisible(visible: boolean): this {
		super.setVisible(visible);
		if (this.desktop && !this.isSelected()) //Bug ZK-1618: not show if current tabpanel is not selected
			this.$n_().style.display = 'none';
		return this;
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var cls = super.domClass_(no),
			tabbox = this.getTabbox()!;
		if (tabbox.inAccordionMold())
			cls += ' ' + this.$s('content');
		return cls;
	}

	/** Returns the tab associated with this tab panel.
	 * @return zul.tab.Tab
	 */
	getLinkedTab(): zul.tab.Tab | undefined {
		var tabbox = this.getTabbox();
		if (!tabbox) return undefined;

		var tabs = tabbox.getTabs();
		return tabs ? tabs.getChildAt<zul.tab.Tab>(this.getIndex()) : undefined;
	}

	/** Returns the index of this panel, or -1 if it doesn't belong to any
	 * tabpanels.
	 * @return int
	 */
	getIndex(): number {
		return this.getChildIndex();
	}

	/** Returns whether this tab panel is selected.
	 * @return boolean
	 */
	isSelected(): boolean {
		return !!this.getLinkedTab()?.isSelected();
	}

	// Bug 3026669
	_changeSel(oldPanel: zul.tab.Tabpanel | undefined): void {
		if (oldPanel) {
			var cave = this.$n('cave'),
				panel: HTMLElement | undefined;
			if (cave && !cave.style.height && (panel = oldPanel.$n('cave')))
				cave.style.height = panel.style.height;
		}
	}

	_sel(toSel: boolean, animation: boolean): void { //don't rename (zkmax counts on it)!!
		var tabbox = this.getTabbox();

		// B95-ZK-4695.zul, the Tabpanel is removed so that its desktop should be null.
		if (!tabbox || !this.desktop) return; //Bug ZK-1808 removed tabpanel is no longer in hierarchy, and cannot be removed
		var accd = tabbox.inAccordionMold();

		if (accd && animation) {
			var zkp = zk(this.$n('cave'));
			if (toSel) {
				/* ZK-1441
				 * When a tabpanel is animating, set tabbox.animating
				 * to block other tabpanels enter _sel().
				 * Reference: doClick_() in Tab.js
				 */
				tabbox._animating = true;
				zkp.slideDown(
					this,
					{'afterAnima': function () {delete tabbox!._animating;}}
				);
			} else {
				zkp.slideUp(this);
			}
		} else {
			var $pl = jq(accd ? this.$n_('cave') : this.$n_()),
				vis = $pl.zk.isVisible();
			if (toSel) {
				if (!vis) {
					$pl.show();
					// Bug ZK-1454: Scrollbar forgets its position when switching tabs in Tabbox
					if (zk.ie9 || zk.webkit)
						$pl.scrollTop(this._lastScrollTop!);
					zUtl.fireShown(this);
				}
			} else if (vis) {
				zWatch.fireDown('onHide', this);
				// Bug ZK-1454: Scrollbar forgets its position when switching tabs in Tabbox
				if (zk.ie9 || zk.webkit)
					this._lastScrollTop = $pl.scrollTop();
				$pl.hide();
			}
		}
	}

	// Could return NaN. Should validate the return value before using it.
	getPanelContentHeight_(): number {
		// NOTE: Adding undefined to a number results in NaN. If any argument of
		// Math.max is undefined, the result is NaN. Fortunately, zul.tab.Tabs.prototype._fixHgh
		// validates the return value of this function before using it.
		const node = this.$n(),
			tabpanelsNode = this.parent?.$n(),
			panelContentHeight = (tabpanelsNode?.scrollHeight as number) + zk(tabpanelsNode).padBorderHeight();

		return Math.max(node?.offsetHeight as number, panelContentHeight); // B50-ZK-298: concern panel height
	}

	_fixPanelHgh(): void {
		var tabbox = this.getTabbox()!,
			tbx = tabbox.$n_(),
			hgh: string | number = tbx.style.height;
		if (!hgh && tabbox._vflex)
			hgh = tbx.offsetHeight;

		if (hgh && hgh != 'auto') {
			if (!tabbox.inAccordionMold()) {
				const n = this.$n_(),
					extraHgh = tabbox.isHorizontal() ? zk(tabbox.tabs).offsetHeight()
													 : zk(n.parentNode).padBorderHeight();
				// B50-ZK-473: Tabpanel in vertical Tabbox should always have full height
				n.style.height = jq.px0(zk(tabbox).contentHeight() - extraHgh);
			} else {
				const n = this.$n_();
				let hgh = tbx.offsetHeight - zk(n.parentNode).padBorderHeight();
				for (let e = n.parentNode!.firstChild; e; e = e.nextSibling)
					if (e != n)
						hgh -= (e as HTMLElement).offsetHeight;
				hgh -= (n.firstChild as HTMLElement).offsetHeight;
				this.$n_('cave').style.height = jq.px0(hgh);
			}
		}
	}

	override onSize(): void {
		var tabbox = this.getTabbox()!;
		if (tabbox.inAccordionMold() && !zk(this.$n('cave')).isVisible())
			return;
		this._fixPanelHgh();		//Bug 2104974
	}

	//bug #3014664
	override setVflex(v: boolean | string | undefined): this { //vflex ignored for Tabpanel
		if (v != 'min') v = false;
		return super.setVflex(v);
	}

	//bug #3014664
	override setHflex(v: boolean | string | undefined): this { //hflex ignored for Tabpanel
		if (v != 'min') v = false;
		return super.setHflex(v);
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onSize: this});
		// B50-ZK-660: Dynamically generated accordion tabs cannot be closed
		var tab: zul.tab.Tab | undefined;
		if (this.getTabbox()!.inAccordionMold()
				&& (tab = this.getLinkedTab())) {

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
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onSize: this});
		this._lastScrollTop = undefined;
		super.unbind_(skipper, after, keepRod);
	}
}