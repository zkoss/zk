/* Toolbar.ts

	Purpose:

	Description:

	History:
		Sat Dec 24 12:58:43	 2008, Created by Flyworld

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

/**
 * The interface {@link zul.wgt.ToolbarChild} deduced from `za11y/zul/wgt-a11y.ts`.
 * According to {@link https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Toolbar},
 * children of {@link zul.wgt.Toolbar} are mainly {@link zul.wgt.Toolbarbutton}, but
 * {@link zul.wgt.Button} and {@link zul.wgt.Space} are also possible.
 */
export interface ToolbarChild extends zul.Widget {
	previousSibling?: ToolbarChild;
	nextSibling?: ToolbarChild;
	/** @internal */
	_disabled?: boolean;
}

// The framework breakpoint scale (doc/spec/responsive-design.md §5); the zkmax build fails when it
// differs from the CSS width conditions or from zkmax Grid/Listbox's constant of the same name.
const _DEFAULT_BREAKPOINTS: Record<string, number> = {
	sm: 576,
	md: 768,
	lg: 992,
	xl: 1200
};

/**
 * A toolbar.
 *
 * <p>Mold:
 * <ol>
 * <li>default</li>
 * <li>panel: this mold is used for {@link zul.wnd.Panel} component as its
 * foot toolbar.</li>
 * </ol>
 * @defaultValue {@link getZclass}: z-toolbar
 */
@zk.WrapClass('zul.wgt.Toolbar')
export class Toolbar extends zul.Widget {
	override firstChild?: ToolbarChild;
	override lastChild?: ToolbarChild;
	/** @internal */
	_orient = 'horizontal';
	/** @internal */
	_align = 'start';
	/** @internal */
	_overflowPopupIconSclass = 'z-icon-ellipsis-h';
	/** @internal */
	_overflowPopup?: boolean;
	/** @internal */
	_open?: boolean;
	/** @internal */
	_responsive?: string;
	/** @internal */
	_resizeObserver?: ResizeObserver;
	/** @internal */
	_iconOnly?: boolean;

	/**
	 * @returns the alignment of any children added to this toolbar. Valid values
	 * are "start", "end" and "center".
	 * @defaultValue `"start"`
	 */
	getAlign(): string {
		return this._align;
	}

	/**
	 * Sets the alignment of any children added to this toolbar. Valid values
	 * are "start", "end" and "center".
	 * @defaultValue `"start"`, if null, "start" is assumed.
	 */
	setAlign(align: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = align;

		if (o !== align || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the orient.
	 * @defaultValue `"horizontal"`.
	 */
	getOrient(): string {
		return this._orient;
	}

	/**
	 * Sets the orient.
	 * @param orient - either "horizontal" or "vertical".
	 */
	setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;

		if (o !== orient || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns whether toolbar has a button that shows a popup
	 * which contains those content weren't able to fit in the toolbar.
	 * If overflowPopup is false, toolbar will display multiple rows when content is wider than toolbar.
	 * @defaultValue `false`.
	 * @since 8.6.0
	 */
	isOverflowPopup(): boolean {
		return !!this._overflowPopup;
	}

	/**
	 * Set whether toolbar has a button that shows a popup
	 * which contains those content weren't able to fit in the toolbar.
	 * If overflowPopup is false, toolbar will display multiple rows when content is wider than toolbar.
	 *
	 * @param overflowPopup - whether toolbar has a button that shows a popup
	 * @since 8.6.0
	 */
	setOverflowPopup(overflowPopup: boolean, opts?: Record<string, boolean>): this {
		const o = this._overflowPopup;
		this._overflowPopup = overflowPopup;

		if (o !== overflowPopup || opts?.force) {
			this.rerender();
		}

		return this;
	}

	/**
	 * @returns the overflow sclass name of overflow popup icon of this toolbar.
	 * @defaultValue `"z-icon-ellipsis-h"`.
	 * @since 9.6.0
	 */
	getOverflowPopupIconSclass(): string {
		return this._overflowPopupIconSclass;
	}

	/**
	 * Sets the overflow sclass name of overflow popup icon of this toolbar.
	 * When overflowPopup is true, toolbar has a button that shows a popup
	 * users can customize the overflow popup icon.
	 * @param overflowPopupIconSclass - the overflow sclass name of overflow popup icon of this toolbar
	 * @since 9.6.0
	 */
	setOverflowPopupIconSclass(overflowPopupIconSclass: string, opts?: Record<string, boolean>): this {
		const o = this._overflowPopupIconSclass;
		this._overflowPopupIconSclass = overflowPopupIconSclass;

		if (o !== overflowPopupIconSclass || opts?.force) {
			const icon = this.$n('overflowpopup-button');
			if (this.desktop && icon)
				icon.className = this._getOverflowPopupBtnClass();
		}

		return this;
	}

	/**
	 * @returns the responsive policy: `"iconOnly"`, `"none"`, or `undefined` if neither
	 * this toolbar nor the `org.zkoss.zul.toolbar.responsive` library property sets one.
	 * @since 11.1.0
	 */
	getResponsive(): string | undefined {
		return this._responsive;
	}

	/**
	 * Sets the responsive policy. With `"iconOnly"`, while the toolbar is narrower than the
	 * framework's `md` breakpoint (768px), the toolbarbuttons and buttons in it that have an
	 * icon or image show only it; their labels are visually hidden but still name the buttons.
	 * `"none"` disables it.
	 * The width compared with `md` is the toolbar's own: a toolbar sized by its content, such as
	 * one in an `<hlayout>` or `<hbox>`, stays icon-only once its labels are hidden; give it a
	 * `width` or `hflex`.
	 * @since 11.1.0
	 */
	setResponsive(responsive: string | undefined, opts?: Record<string, boolean>): this {
		const o = this._responsive;
		this._responsive = responsive;

		if (o !== responsive || opts?.force) {
			// the children render their labels differently under iconOnly
			this.rerender();
		}

		return this;
	}

	/** @internal */
	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (this.isOverflowPopup()) {
			zWatch.listen({ onFloatUp: this, onCommandReady: this, onSize: this });
			this.domListen_(this.$n_('overflowpopup-button'), 'onClick', '_openPopup');
		}
		if (this._responsive == 'iconOnly') {
			// catches every width change, a server setWidth included
			this._resizeObserver = new ResizeObserver(this.proxy(this._syncIconOnly));
			this._resizeObserver.observe(this.$n_());
		}
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		const popup = this.$n('pp');
		if (popup) {
			this.domUnlisten_(this.$n_('overflowpopup-button'), 'onClick', '_openPopup');
			zWatch.unlisten({ onFloatUp: this, onCommandReady: this, onSize: this });
		}
		this._resizeObserver?.disconnect();
		this._resizeObserver = undefined;
		this._iconOnly = undefined;
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_getOverflowPopupBtnClass(): string {
		return this.$s('overflowpopup-button') + ' ' + this.getOverflowPopupIconSclass() + ' z-icon-fw';
	}

	/** @internal */
	_openPopup(evt?: zk.Event): void {
		if (this._open)
			return;

		this._open = true;
		const popup = this.$n_('pp');
		this.setFloating_(true, { node: popup });
		zWatch.fire('onFloatUp', this);
		const topZIndex = this.setTopmost();
		popup.style.zIndex = String(topZIndex > 0 ? topZIndex : 1);
		jq(popup).removeClass(this.$s('popup-close')).addClass(this.$s('popup-open')).zk.makeVParent();
		this._syncPopupPosition();
	}

	/** @internal */
	_syncPopupPosition(): void {
		zk(this.$n('pp')).position(this.$n_(), 'after_end');
	}

	onFloatUp(ctl: zk.ZWatchController): void {
		if (!zUtl.isAncestor(this, ctl.origin))
			this._closePopup();
	}

	/** @internal */
	_closePopup(): void {
		if (!this._open)
			return;

		this._open = false;
		const jqPopup = jq(this.$n_('pp'));
		jqPopup.removeClass(this.$s('popup-open')).addClass(this.$s('popup-close')).zk.undoVParent();
		this.setFloating_(false);
	}

	onCommandReady(): void {
		if (this.desktop && this.isOverflowPopup())
			this._adjustContent();
	}

	override onSize(): void {
		super.onSize();
		if (this.desktop && this.isOverflowPopup()) {
			this._adjustContent();
			if (this._open)
				this._syncPopupPosition();
		}
	}

	/** @internal */
	_adjustContent(): void {
		if (zUtl.isImageLoading()) {
			setTimeout(this.proxy(this._adjustContent), 20);
			return;
		}

		const jqToolbar = jq(this),
			contentWidth = jqToolbar.width()! - jq(this.$n_('overflowpopup-button')).width()!,
			popup = this.$n_('pp'),
			cave = this.$n_('cave'),
			oldToolbarChildren = jq(cave).children().toArray(),
			oldPopupChildren = jq(popup).children().toArray(),
			children = oldToolbarChildren.concat(oldPopupChildren),
			childrenAmount = children.length;
		let tempChildrenWidth = 0,
			newPopupChildrenAmount = 0;

		// Calculate width to decide how many children should be displayed on popup.
		for (let i = 0; i < childrenAmount; i++) {
			const child = children[i];
			tempChildrenWidth += jq(child).outerWidth(true)!;
			// a popup child shows its label, which icon-only hides once it is back in the toolbar
			if (this._iconOnly && i >= oldToolbarChildren.length) {
				const wgt = zk.Widget.$(child);
				if (wgt)
					tempChildrenWidth -= jq(child).find('.' + wgt.$s('label')).outerWidth() ?? 0;
			}
			if (tempChildrenWidth >= contentWidth) {
				newPopupChildrenAmount = childrenAmount - i;
				break;
			}
		}

		const popupChildrenDiff = newPopupChildrenAmount - oldPopupChildren.length;
		if (!popupChildrenDiff)
			return;

		// Start to move children
		const overflowpopupOn = this.$s('overflowpopup-on'),
			overflowpopupOff = this.$s('overflowpopup-off');
		if (newPopupChildrenAmount) {
			jqToolbar.removeClass(overflowpopupOff).addClass(overflowpopupOn);
			if (popupChildrenDiff > 0) {
				for (let i = 0; i < popupChildrenDiff; i++)
					popup.insertBefore(oldToolbarChildren.pop()!, popup.children[0]);
			} else {
				for (let i = 0; i < -popupChildrenDiff; i++)
					cave.appendChild(oldPopupChildren.shift()!);
			}
		} else {
			jqToolbar.removeClass(overflowpopupOn).addClass(overflowpopupOff);
			if (this._open)
				this._closePopup();
			while (oldPopupChildren.length)
				cave.appendChild(oldPopupChildren.shift()!);
		}
	}

	/** @internal */
	_syncIconOnly(): void {
		const n = this.$n_(),
			width = n.offsetWidth;
		// hidden (display:none, collapsed tab): defer
		if (width <= 0)
			return;
		const iconOnly = width < _DEFAULT_BREAKPOINTS.md;
		if (!!this._iconOnly == iconOnly)
			return;
		this._iconOnly = iconOnly;
		jq(n).toggleClass(this.$s('icon-only'), iconOnly);
		if (this.isOverflowPopup())
			this._adjustContent();
	}

	// super
	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		let /*safe*/ sc = super.domClass_(no);
		if (!no?.zclass) {
			const tabs = this.parent && zk.isLoaded('zul.tab') && this.parent instanceof zul.tab.Tabbox ? this.$s('tabs') : '';

			if (tabs)
				sc += ' ' + tabs;
			if (this.inPanelMold())
				sc += ' ' + this.$s('panel');
			if ('vertical' == this.getOrient())
				sc += ' ' + this.$s('vertical');
			if (this.isOverflowPopup())
				sc += ' ' + this.$s('overflowpopup') + ' ' + this.$s('overflowpopup-off');
			// rendered here too, or updateDomClass_ (e.g. setSclass) drops it while the size stays put
			if (this._iconOnly)
				sc += ' ' + this.$s('icon-only');
		}
		return sc;
	}

	// Bug ZK-1706 issue: we have to expand the width of the content div when
	// align="left", others won't support
	// eslint-disable-next-line zk/javaStyleSetterSignature
	/** @internal */
	override setFlexSizeW_(flexSizeW: HTMLElement, zkn: zk.JQZK, width: number, isFlexMin?: boolean): void {
		super.setFlexSizeW_(flexSizeW, zkn, width, isFlexMin);
		if (!isFlexMin && this.getAlign() == 'start') {
			const cave = this.$n('cave');
			if (cave)
				cave.style.width = jq.px0(zk(this.$n()).contentWidth());
		}
	}

	/**
	 * @returns whether is in panel mold or not.
	 */
	inPanelMold(): boolean {
		return this._mold == 'panel';
	}

	override appendChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		super.appendChild(child, ignoreDom);
		const popup = this.$n('pp');
		if (popup && popup.children.length > 0)
			popup.appendChild(child.$n_());
		return false;
	}

	override removeChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		const popupNode = this.$n('pp'),
			childNode = child.$n();
		if (popupNode && childNode) {
			const childOnPopup = childNode.parentNode == popupNode;
			if (childOnPopup && popupNode.children.length == 1) {
				jq(this.$n_()).removeClass(this.$s('overflowpopup-on')).addClass(this.$s('overflowpopup-off'));
				this._closePopup(); // should close popup if overflowpopup turns off
			}
		}
		super.removeChild(child, ignoreDom);
		return false;
	}

	// protected
	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (this.inPanelMold())
			this.rerender();
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (!this.childReplacing_ && this.inPanelMold())
			this.rerender();
	}
}