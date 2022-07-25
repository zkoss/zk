/* Groupbox.ts

	Purpose:

	Description:

	History:
		Sun Nov 16 12:39:24     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/**
 * Groups a set of child elements to have a visual effect.
 * <p>Default {@link #getZclass}: "z-groupbox". If {@link #getMold()} is 3d,
 * "z-groupbox-3d" is assumed.
 *
 * <p>Events: onOpen.
 *
 */
@zk.WrapClass('zul.wgt.Groupbox')
export class Groupbox extends zul.ContainerWidget {
	_open = true;
	_closable = true;
	_nativebar = true;
	_contentStyle?: string;
	_contentSclass?: string;
	_title?: string;
	caption?: zul.wgt.Caption;

	/** Opens or closes this groupbox.
	 * @param boolean open
	 */
	setOpen(open: boolean, fromServer?: boolean, opts?: Record<string, boolean>): this {
		const o = this._open;
		this._open = open;

		if (o !== open || (opts && opts.force)) {
			var node = this.$n();
			if (node && this._closable) {
				var caveNode = this.getCaveNode()!;
				if (open) {
					jq(node).removeClass(this.$s('collapsed'));
					zk(this).redoCSS(-1, {'fixFontIcon': true});
				}
				if (zk(this).getAnimationSpeed() === 0) {
					jq(caveNode)[open ? 'show' : 'hide']();
					this._afterOpen(open);
				} else
					zk(caveNode)[open ? 'slideDown' : 'slideUp'](this);

				if (!fromServer) this.fire('onOpen', {open: open});
			}
		}

		return this;
	}

	/** Returns whether this groupbox is open.
	 * <p>Default: true.
	 * @return boolean
	 */
	isOpen(): boolean {
		return this._open;
	}

	/** Returns whether user can open or close the group box.
	 * In other words, if false, users are no longer allowed to
	 * change the open status (by clicking on the title).
	 *
	 * <p>Default: true.
	 * @return boolean
	 */
	isClosable(): boolean {
		return this._closable;
	}

	/** Sets whether user can open or close the group box.
	 * @param boolean closable
	 */
	setClosable(closable: boolean, opts?: Record<string, boolean>): this {
		const o = this._closable;
		this._closable = closable;

		if (o !== closable || (opts && opts.force)) {
			this._updDomOuter();
		}

		return this;
	}

	/** Returns the CSS style for the content block of the groupbox.
	 * Used only if {@link #getMold} is not default.
	 * @return String
	 */
	getContentStyle(): string | undefined {
		return this._contentStyle;
	}

	/** Sets the CSS style for the content block of the groupbox.
	 * Used only if {@link #getMold} is not default.
	 *
	 * <p>Default: null.
	 * @param String contentStyle
	 */
	setContentStyle(contentStyle: string, opts?: Record<string, boolean>): this {
		const o = this._contentStyle;
		this._contentStyle = contentStyle;

		if (o !== contentStyle || (opts && opts.force)) {
			this._updDomOuter();
		}

		return this;
	}

	/** Returns the style class used for the content block of the groupbox.
	 * Used only if {@link #getMold} is not default.
	 * @return String
	 */
	getContentSclass(): string | undefined {
		return this._contentSclass;
	}

	/** Sets the style class used for the content block.
	 * @param String contentSclass
	 */
	setContentSclass(contentSclass: string, opts?: Record<string, boolean>): this {
		const o = this._contentSclass;
		this._contentSclass = contentSclass;

		if (o !== contentSclass || (opts && opts.force)) {
			this._updDomOuter();
		}

		return this;
	}

	/** Returns the title of the groupbox.
	 * @return String
	 * @since 6.0
	 */
	getTitle(): string | undefined {
		return this._title;
	}

	/** Sets the title of the groupbox.
	 * @param String title
	 * @since 6.0
	 */
	setTitle(title: string, opts?: Record<string, boolean>): this {
		const o = this._title;
		this._title = title;

		if (o !== title || (opts && opts.force)) {
			this._updDomOuter();
		}

		return this;
	}

	_isDefault(): boolean {
		return this._mold == 'default';
	}

	_updDomOuter(): void {
		this.rerender(zk.Skipper.nonCaptionSkipper);
	}

	_contentAttrs(): string {
		var html = ' class="', s = this._contentSclass;
		if (s)
			html += s + ' ';
		html += this.$s('content') + '"';

		s = this._contentStyle;
		if (this.caption || this.getTitle()) // B60-ZK-987
			s = 'border-top:0;' + (s || '');
		if (!this._open)
			s = 'display:none;' + (s || '');
		if (s)
			html += ' style="' + s + '"';
		return html;
	}

	_redrawCave(out: string[], skipper: boolean): void { //reserve for customizing
		out.push('<div id="', this.uuid, '-cave"', this._contentAttrs(), '>');

		if (!skipper)
			for (var w = this.firstChild, cap = this.caption; w; w = w.nextSibling)
				if (w != cap)
					w.redraw(out);

		out.push('</div>');
	}

	override setHeight(height?: string): this {
		super.setHeight(height);
		if (this.desktop) this._fixHgh();
		return this;
	}

	_fixHgh(): void {
		var n = this.$n_(),
			hgh: string | number = n.style.height;
		if (!hgh && this._cssflex && this._vflex) // due to css flex, need to use offsetHeight
			hgh = n.offsetHeight;
		if (hgh && hgh != 'auto' && this.isOpen()) {
			const n = this.$n('cave');
			if (n) {
				var $n = zk(n);
				// B50-ZK-487: height isuue in the groupbox (with specified caption)
				n.style.height = ($n.revisedHeight($n.vflexHeight(), true)
								- (this._isDefault() ? parseInt(jq(this).css('padding-top')) : 0)) + 'px';
					//if (zk.gecko) setTimeout(fix, 0);
					//Gecko bug: height is wrong if the browser visits the page first time
					//(reload won't reproduce the problem) test case: test/z5.zul
			}
		}
		if (this._isDefault()) {
			var title = this.$n('title'),
				cap = this.caption;
			if (cap)
				cap.$n_().style.top = jq.px(zk(cap.$n('cave')).offsetHeight() / 2 * -1);
			if (title)
				title.style.top = jq.px(zk(this.$n('title-cnt')).offsetHeight() / 2 * -1);
		}
	}

	// B60-ZK-562: Groupbox vflex=min is wrong
	override setFlexSizeH_(n: HTMLElement, zkn: zk.JQZK, height: number, isFlexMin?: boolean): void {
		if (isFlexMin && (this.caption || this._title)) {
			// B60-ZK-562
			height = this._isDefault() ? parseInt(jq(this).css('padding-top')) : 0;
			for (var c = n.firstChild; c; c = c.nextSibling)
				height += jq(c).outerHeight()!;
		}

		super.setFlexSizeH_(n, zkn, height, isFlexMin);
	}

	//watch//
	override onSize(): void {
		this._fixHgh();
		// B50-ZK-487
		// classicblue is deprecated and
		// shadow not used in breeze, sapphire and silvertail,
	}

	override clearCachedSize_(): void {
		super.clearCachedSize_();
		this.$n_('cave').style.height = '';
	}

	override updateDomStyle_(): void {
		super.updateDomStyle_();
		if (this.desktop) this.onSize();
	}

	//super//
	override focus_(timeout?: number): boolean {
		var cap = this.caption;
		for (var w = this.firstChild; w; w = w.nextSibling)
			if (w.desktop && w != cap && w.focus_(timeout))
				return true;
		return !!cap?.focus_(timeout);
	}

	override bind_(desktop?: zk.Desktop, skipper?: zk.Skipper, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		zWatch.listen({onSize: this});
		var tt: HTMLElement | undefined;
		if (this.getTitle() && (tt = this.$n('title')))
			this.domListen_(tt, 'onClick', '_doTitleClick');
	}

	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		zWatch.unlisten({onSize: this});
		const tt = this.$n('title');
		if (tt)
			this.domUnlisten_(tt, 'onClick', '_doTitleClick');
		super.unbind_(skipper, after, keepRod);
	}

	// will be called while click on title and title exists but no caption
	_doTitleClick(evt: zk.Event, popupOnly?: boolean): void {
		if (this._closable) this.setOpen(!this.isOpen());
		super.doClick_(evt, popupOnly);
	}

	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (child instanceof zul.wgt.Caption) {
			this.caption = child;
			this.rerender();
		}
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (child == this.caption) {
			this.caption = undefined;
			this.rerender();
		}
	}

	//@Override, Bug ZK-1524: caption children should not considered.
	override getChildMinSize_(attr: zk.FlexOrient, wgt: zk.Widget): number {
		if (!(wgt instanceof zul.wgt.Caption))
			return super.getChildMinSize_(attr, wgt);
		return 0; // FIXME: prior to TS migration, it returns nothing here.
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var cls = super.domClass_(no);
		if (!this._isDefault()) {
			if (cls) cls += ' ';
			cls += this.$s('3d');
		}

		if (!this.caption && !this.getTitle()) {
			if (cls) cls += ' ';
			cls += ' ' + this.$s('notitle');
		}

		if (!this._open && this._isDefault()) {
			if (cls) cls += ' ';
			cls += this.$s('collapsed');
		}
		return cls;
	}

	override afterAnima_(visible: boolean): void {
		super.afterAnima_(visible);
		this._afterOpen(visible);
	}

	_afterOpen(visible: boolean): void {
		if (!visible && this._isDefault())
			jq(this.$n()!).addClass(this.$s('collapsed'));

		var p = this.parent;
		if (p) {
			var parentHasFlex = (p.getHflex && p.getHflex() != 'min') || (p.getVflex && p.getVflex() != 'min');
			if (parentHasFlex) {
				// ZK-3248: parent should resize if parent itself has flex
				zUtl.fireSized(p);
			} else {
				// ZK-2138: parent should resize if parent has child with vflex
				for (var c = p.firstChild; c; c = c.nextSibling) {
					if (c == this)
						continue;
					var vflex = c.getVflex();
					if (vflex && vflex != 'min') {
						zUtl.fireSized(p);
						break;
					}
				}
			}
		}
		// ZK-2978: should appear zul.Scrollbar after setOpen
		this.doResizeScroll_();
	}
}