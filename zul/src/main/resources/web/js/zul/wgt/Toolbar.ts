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
 * A toolbar.
 *
 * <p>Mold:
 * <ol>
 * <li>default</li>
 * <li>panel: this mold is used for {@link zul.wnd.Panel} component as its
 * foot toolbar.</li>
 * </ol>
 * <p>Default {@link #getZclass}: z-toolbar
 */
@zk.WrapClass('zul.wgt.Toolbar')
export class Toolbar extends zul.Widget {
	_orient = 'horizontal';
	_align = 'start';
	_overflowPopupIconSclass = 'z-icon-ellipsis-h';
	_overflowPopup?: boolean;
	_open?: boolean;

	/**
	 * Returns the alignment of any children added to this toolbar. Valid values
	 * are "start", "end" and "center".
	 * <p>Default: "start"
	 * @return String
	 */
	getAlign(): string {
		return this._align;
	}

	/**
	 * Sets the alignment of any children added to this toolbar. Valid values
	 * are "start", "end" and "center".
	 * <p>Default: "start", if null, "start" is assumed.
	 * @param String align
	 */
	setAlign(align: string, opts?: Record<string, boolean>): this {
		const o = this._align;
		this._align = align;

		if (o !== align || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/** Returns the orient.
	 * <p>Default: "horizontal".
	 * @return String
	 */
	getOrient(): string {
		return this._orient;
	}

	/** Sets the orient.
	 * @param String orient either "horizontal" or "vertical".
	 */
	setOrient(orient: string, opts?: Record<string, boolean>): this {
		const o = this._orient;
		this._orient = orient;

		if (o !== orient || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Return whether toolbar has a button that shows a popup
	 * which contains those content weren't able to fit in the toolbar.
	 * If overflowPopup is false, toolbar will display multiple rows when content is wider than toolbar.
	 * Default: false.
	 *
	 * @return boolean
	 * @since 8.6.0
	 */
	isOverflowPopup(): boolean | undefined {
		return this._overflowPopup;
	}

	/**
	 * Set whether toolbar has a button that shows a popup
	 * which contains those content weren't able to fit in the toolbar.
	 * If overflowPopup is false, toolbar will display multiple rows when content is wider than toolbar.
	 *
	 * @param boolean overflowPopup whether toolbar has a button that shows a popup
	 * @since 8.6.0
	 */
	setOverflowPopup(overflowPopup: boolean, opts?: Record<string, boolean>): this {
		const o = this._overflowPopup;
		this._overflowPopup = overflowPopup;

		if (o !== overflowPopup || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns the overflow sclass name of overflow popup icon of this toolbar.
	 * <p>Default: "z-icon-ellipsis-h".
	 * @return String
	 * @since 9.6.0
	 */
	getOverflowPopupIconSclass(): string {
		return this._overflowPopupIconSclass;
	}

	/**
	 * Sets the overflow sclass name of overflow popup icon of this toolbar.
	 * When overflowPopup is true, toolbar has a button that shows a popup
	 * users can customize the overflow popup icon.
	 * @param String the overflow sclass name of overflow popup icon of this toolbar
	 * @since 9.6.0
	 */
	setOverflowPopupIconSclass(the: string, opts?: Record<string, boolean>): this {
		const o = this._overflowPopupIconSclass;
		this._overflowPopupIconSclass = the;

		if (o !== the || (opts && opts.force)) {
			var icon = this.$n('overflowpopup-button');
			if (this.desktop && icon)
				icon.className = this._getOverflowPopupBtnClass();
		}

		return this;
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (this.isOverflowPopup()) {
			zWatch.listen({onFloatUp: this, onCommandReady: this, onSize: this});
			this.domListen_(this.$n_('overflowpopup-button'), 'onClick', '_openPopup');
		}
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		var popup = this.$n('pp');
		if (popup) {
			this.domUnlisten_(this.$n_('overflowpopup-button'), 'onClick', '_openPopup');
			zWatch.unlisten({onFloatUp: this, onCommandReady: this, onSize: this});
		}
		super.unbind_(skipper, after, keepRod);
	}

	_getOverflowPopupBtnClass(): string {
		return this.$s('overflowpopup-button') + ' ' + this.getOverflowPopupIconSclass() + ' z-icon-fw';
	}

	_openPopup(evt: zk.Event): void {
		if (this._open)
			return;

		this._open = true;
		var popup = this.$n_('pp');
		this.setFloating_(true, {node: popup});
		zWatch.fire('onFloatUp', this);
		var topZIndex = this.setTopmost();
		popup.style.zIndex = (topZIndex > 0 ? topZIndex : 1) as unknown as string;
		jq(popup).removeClass(this.$s('popup-close')).addClass(this.$s('popup-open')).zk.makeVParent();
		this._syncPopupPosition();
	}

	_syncPopupPosition(): void {
		zk(this.$n('pp')).position(this.$n_(), 'after_end');
	}

	onFloatUp(ctl: zk.ZWatchController): void {
		if (!zUtl.isAncestor(this, ctl.origin))
			this._closePopup();
	}

	_closePopup(): void {
		if (!this._open)
			return;

		this._open = false;
		var jqPopup = jq(this.$n_('pp'));
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

	_adjustContent(): void {
		if (zUtl.isImageLoading()) {
			setTimeout(this.proxy(this._adjustContent), 20);
			return;
		}

		var jqToolbar = jq(this),
			contentWidth = jqToolbar.width()! - jq(this.$n_('overflowpopup-button')).width()!,
			popup = this.$n_('pp'),
			cave = this.$n_('cave'),
			oldToolbarChildren = jq(cave).children().toArray(),
			oldPopupChildren = jq(popup).children().toArray(),
			children = oldToolbarChildren.concat(oldPopupChildren),
			childrenAmount = children.length,
			tempChildrenWidth = 0,
			newPopupChildrenAmount = 0;

		// Calculate width to decide how many children should be displayed on popup.
		for (var i = 0; i < childrenAmount; i++) {
			tempChildrenWidth += jq(children[i]).outerWidth(true)!;
			if (tempChildrenWidth >= contentWidth) {
				newPopupChildrenAmount = childrenAmount - i;
				break;
			}
		}

		var popupChildrenDiff = newPopupChildrenAmount - oldPopupChildren.length;
		if (!popupChildrenDiff)
			return;

		// Start to move children
		var overflowpopupOn = this.$s('overflowpopup-on'),
			overflowpopupOff = this.$s('overflowpopup-off');
		if (newPopupChildrenAmount) {
			jqToolbar.removeClass(overflowpopupOff).addClass(overflowpopupOn);
			if (popupChildrenDiff > 0) {
				for (var i = 0; i < popupChildrenDiff; i++)
					popup.insertBefore(oldToolbarChildren.pop()!, popup.children[0]);
			} else {
				for (var i = 0; i < -popupChildrenDiff; i++)
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

	// super
	override domClass_(no?: zk.DomClassOptions): string {
		var sc = super.domClass_(no);
		if (!no || !no.zclass) {
			var tabs = this.parent && zk.isLoaded('zul.tab') && this.parent instanceof zul.tab.Tabbox ? this.$s('tabs') : '';

			if (tabs)
				sc += ' ' + tabs;
			if (this.inPanelMold())
				sc += ' ' + this.$s('panel');
			if ('vertical' == this.getOrient())
				sc += ' ' + this.$s('vertical');
			if (this.isOverflowPopup())
				sc += ' ' + this.$s('overflowpopup') + ' ' + this.$s('overflowpopup-off');
		}
		return sc;
	}

	// Bug ZK-1706 issue: we have to expand the width of the content div when
	// align="left", others won't support
	override setFlexSizeW_(n: HTMLElement, zkn: zk.JQZK, width: number, isFlexMin?: boolean): void {
		super.setFlexSizeW_(n, zkn, width, isFlexMin);
		if (!isFlexMin && this.getAlign() == 'start') {
			var cave = this.$n('cave');
			if (cave)
				cave.style.width = jq.px0(zk(this.$n()).contentWidth());
		}
	}

	/**
	 * Returns whether is in panel mold or not.
	 * @return boolean
	 */
	inPanelMold(): boolean {
		return this._mold == 'panel';
	}

	override appendChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		super.appendChild(child, ignoreDom);
		var popup = this.$n('pp');
		if (popup && popup.children.length > 0)
			popup.appendChild(child.$n_());
		return false;
	}

	override removeChild(child: zk.Widget, ignoreDom?: boolean): boolean {
		var popupNode = this.$n('pp'),
			childNode = child.$n();
		if (popupNode && childNode) {
			var childOnPopup = childNode.parentNode == popupNode;
			if (childOnPopup && popupNode.children.length == 1) {
				jq(this.$n_()).removeClass(this.$s('overflowpopup-on')).addClass(this.$s('overflowpopup-off'));
				this._closePopup(); // should close popup if overflowpopup turns off
			}
		}
		super.removeChild(child, ignoreDom);
		return false;
	}

	// protected
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (this.inPanelMold())
			this.rerender();
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (!this.childReplacing_ && this.inPanelMold())
			this.rerender();
	}
}