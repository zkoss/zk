/* LayoutRegion.ts

	Purpose:

	Description:

	History:
		Wed Jan  7 12:15:02     2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
function _setFirstChildFlex(wgt: LayoutRegion, flex: boolean, ignoreMin?: boolean): void {
	var cwgt = wgt.getFirstChild();
	if (cwgt) {
		if (flex) {
			wgt._fcvflex = cwgt.getVflex();
			wgt._fchflex = cwgt.getHflex();
			if (!ignoreMin || cwgt._vflex != 'min') // B50-ZK-237
				cwgt.setVflex('true');
			if (!ignoreMin || cwgt._hflex != 'min') // B50-ZK-237
				cwgt.setHflex('true');
		} else {
			cwgt.setVflex(wgt._fcvflex);
			cwgt.setHflex(wgt._fchflex);
			delete wgt._fcvflex;
			delete wgt._fchflex;
		}
	}
}

export interface LayoutRegionAmbit {
	x: number;
	y: number;
	w: number;
	h: number;
	ts?: number;
}

/**
 * A layout region in a border layout.
 * <p>
 * Events:<br/> onOpen, onSize, onSlide.<br/>
 */
@zk.WrapClass('zul.layout.LayoutRegion')
export class LayoutRegion extends zul.Widget {
	override parent!: zul.layout.Borderlayout | null;
	_open = true;
	_border = 'normal';
	_maxsize = 2000;
	_minsize = 0;
	_scrollbar?: zul.Scrollbar;
	_slidable = true;
	_closable = true;
	_nativebar = true;
	_margins = [0, 0, 0, 0];
	_cmargins = [3, 3, 3, 3]; //center
	sanchor?: string;

	_collapsible = false;
	_splittable = false;
	_flex = false;
	_autoscroll = false;
	_slide = false;
	_isSlide = false;
	_original?: [string, string];
	_fixBarHeight?: boolean;
	_title: string | null = null;
	_fcvflex?: string | boolean | null;
	_fchflex?: string | boolean | null;

	/**
	 * Sets whether to grow and shrink vertical/horizontal to fit their given
	 * space, so called flexibility.
	 * @param boolean flex
	 */
	setFlex(flex: boolean, opts?: Record<string, boolean>): this {
		const o = this._flex;
		this._flex = flex;

		if (o !== flex || (opts && opts.force)) {
			_setFirstChildFlex(this, flex);
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns whether to grow and shrink vertical/horizontal to fit their given
	 * space, so called flexibility.
	 *
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isFlex(): boolean {
		return this._flex;
	}

	/**
	 * Sets the border (either none or normal).
	 *
	 * @param String border the border. If null or "0", "none" is assumed.
	 */
	setBorder(border: string, opts?: Record<string, boolean>): this {
		const o = this._border;
		this._border = border;

		if (o !== border || (opts && opts.force)) {
			if (!border || '0' == border)
				this._border = 'none';

			if (this.desktop) {
				const n = this.$n('real');
				if (n)
					n._lastSize = null;
			}

			this.updateDomClass_();
		}

		return this;
	}

	/**
	 * Returns the border.
	 * <p>
	 * The border actually controls what CSS class to use: If border is null, it
	 * implies "none".
	 *
	 * <p>
	 * If you also specify the CSS class ({@link #setSclass}), it overwrites
	 * whatever border you specify here.
	 *
	 * <p>
	 * Default: "normal".
	 * @return String
	 */
	getBorder(): string {
		return this._border;
	}

	/**
	 * Sets the title.
	 * @param String title
	 */
	setTitle(title: string, opts?: Record<string, boolean>): this {
		const o = this._title;
		this._title = title;

		if (o !== title || (opts && opts.force)) {
			this.rerender();
		}

		return this;
	}

	/**
	 * Returns the title.
	 * <p>Default: null.
	 * @return String
	 */
	getTitle(): string | null {
		return this._title;
	}

	/**
	 * Sets whether enable the split functionality.
	 * @param boolean splittable
	 */
	setSplittable(splittable: boolean, opts?: Record<string, boolean>): this {
		const o = this._splittable;
		this._splittable = splittable;

		if (o !== splittable || (opts && opts.force)) {
			if (this.parent && this.desktop)
				this.parent.resize();
		}

		return this;
	}

	/**
	 * Returns whether enable the split functionality.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isSplittable(): boolean {
		return this._splittable;
	}

	/**
	 * Sets the maximum size of the resizing element.
	 * @param int maxsize
	 */
	setMaxsize(maxsize: number): this {
		this._maxsize = maxsize;
		return this;
	}

	/**
	 * Returns the maximum size of the resizing element.
	 * <p>
	 * Default: 2000.
	 * @return int
	 */
	getMaxsize(): number {
		return this._maxsize;
	}

	/**
	 * Sets the minimum size of the resizing element.
	 * @param int minsize
	 */
	setMinsize(minsize: number): this {
		this._minsize = minsize;
		return this;
	}

	/**
	 * Returns the minimum size of the resizing element.
	 * <p>
	 * Default: 0.
	 * @return int
	 */
	getMinsize(): number {
		return this._minsize;
	}

	/**
	 * Sets whether set the initial display to collapse.
	 *
	 * <p>It only applied when {@link #getTitle()} is not null.
	 * @param boolean collapsible
	 */
	setCollapsible(collapsible: boolean, opts?: Record<string, boolean>): this {
		const o = this._collapsible;
		this._collapsible = collapsible;

		if (o !== collapsible || (opts && opts.force)) {
			var btn = this.$n(this._open ? 'btn' : 'btned');
			if (btn && (!collapsible || this._closable))
				btn.style.display = collapsible ? '' : 'none';
		}

		return this;
	}

	/**
	 * Returns whether set the initial display to collapse.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isCollapsible(): boolean {
		return this._collapsible;
	}

	/**
	 * Sets whether enable overflow scrolling.
	 * @param boolean autoscroll
	 */
	setAutoscroll(autoscroll: boolean, opts?: Record<string, boolean>): this {
		const o = this._autoscroll;
		this._autoscroll = autoscroll;

		if (o !== autoscroll || (opts && opts.force)) {
			var cave = this.$n('cave');
			if (cave) {
				var bodyEl = this.isFlex() && this.getFirstChild() ?
					this.getFirstChild()!.$n_() : cave;
				if (autoscroll) {
					if (this._nativebar) {
						bodyEl.style.overflow = 'auto';
						bodyEl.style.position = 'relative';
						this.domListen_(bodyEl, 'onScroll');
						zWatch.listen({onResponse: this});
					} else {
						zWatch.listen({onSize: this});
					}
				} else {
					if (this._nativebar) {
						bodyEl.style.overflow = 'hidden';
						bodyEl.style.position = '';
						this.domUnlisten_(bodyEl, 'onScroll');
						zWatch.unlisten({onResponse: this});
					} else {
						zWatch.unlisten({onSize: this});
					}
				}
				this._fireSizedIfChildFlex();
			}
		}

		return this;
	}

	/**
	 * Returns whether enable overflow scrolling.
	 * <p>
	 * Default: false.
	 * @return boolean
	 */
	isAutoscroll(): boolean {
		return this._autoscroll;
	}

	/**
	 * Opens or collapses the splitter. Meaningful only if
	 * {@link #isCollapsible} is not false.
	 * @param boolean open
	 */
	setOpen(open: boolean, fromServer?: Record<string, boolean>, nonAnima?: boolean): this {
		const o = this._open;
		this._open = open;

		if (o !== open || (fromServer && fromServer.force)) {
			if (!this.$n() || !this.isCollapsible() || !this.parent || (!fromServer && !this._closable)) {
				return this; //nothing changed
			}

			nonAnima = this.parent._animationDisabled || nonAnima;

			var colled = this.$n('colled'),
				real = this.$n_('real');
			if (open) {
				// Bug 2994592
				if (fromServer) {

					// Bug 2995770
					if (!zk(this.$n()).isRealVisible()) {
						if (colled) {
							jq(real).show();
							jq(colled).hide();
						}
						return this;
					}
					var s = real.style;
					s.visibility = 'hidden';
					s.display = '';
					this._syncSize(true);
					s.visibility = '';
					s.display = 'none';
					this._open = true;
				}
				if (colled) {
					if (!nonAnima)
						zk(colled).slideOut(this, {
							anchor: this.sanchor,
							duration: 200,
							afterAnima: fromServer ? LayoutRegion.afterSlideOut :
								LayoutRegion._afterSlideOutX
						});
					else {
						jq(real).show();
						jq(colled).hide();
						zUtl.fireShown(this);
					}
				}
			} else {
				if (colled && !nonAnima)
					zk(real).slideOut(this, {
						anchor: this.sanchor,
						beforeAnima: LayoutRegion.beforeSlideOut,
						afterAnima: fromServer ? LayoutRegion.afterSlideOut :
							LayoutRegion._afterSlideOutX
					});
				else {
					if (colled)
						jq(colled).show();
					jq(real).hide();
				}
			}
			if (nonAnima) this.parent.resize();
			if (!fromServer && nonAnima) // B50-ZK-301: onOpen is fire after animation
				this.fire('onOpen', {open: open});
		}

		return this;
	}

	/**
	 * Returns whether it is open (i.e., not collapsed. Meaningful only if
	 * {@link #isCollapsible} is not false.
	 * <p>
	 * Default: true.
	 * @return boolean
	 */
	isOpen(): boolean {
		return this._open;
	}

	/**
	 * Slides down or up the region. Meaningful only if
	 * {@link #isCollapsible} is not false and {@link #isOpen} is false.
	 * @param boolean slide
	 * @since 8.0.2
	 */
	setSlide(slide: boolean, fromServer?: Record<string, boolean>, nonAnima?: boolean): this {
		const o = this._slide;
		this._slide = slide;

		if (o !== slide || (fromServer && fromServer.force)) {
			if (!this._isSlide) {
				this._isSlide = true;
				var real = this.$n_('real'),
					s = real.style;
				s.visibility = 'hidden';
				s.display = '';
				this._syncSize();
				this._original = [s.left, s.top];
				this._alignTo();
				s.zIndex = '100';

				if (this.$n('btn'))
					this.$n_('btn').style.display = 'none';
				s.visibility = '';
				s.display = 'none';
				jq(this.$n()!).addClass(this.$s('slide'));
				zk(real).slideDown(this, {
					anchor: this.sanchor,
					afterAnima: LayoutRegion.afterSlideDown
				});
			} else {
				if (nonAnima) LayoutRegion.afterSlideUp.call(this, this.$n_('real'));
				else {
					zk(this.$n('real')).slideUp(this, {
						anchor: this.sanchor,
						afterAnima: LayoutRegion.afterSlideUp
					});
				}
			}
			if (!fromServer) this.fire('onSlide', {slide: slide});
		}

		return this;
	}

	/**
	 * Returns whether it is slide down.
	 * <p>
	 * Default: false.
	 * @return boolean
	 * @since 8.0.2
	 */
	isSlide(): boolean {
		return this._slide;
	}

	/**
	 * Sets whether users can slide (preview) the region when clicked on a collapsed region.
	 * Meaningful only if {@link #isCollapsible()} is true and {@link #isOpen()} is false.
	 *
	 * @param boolean slidable whether users can slide (preview) the region.
	 * @since 8.5.2
	 */
	setSlidable(slidable: boolean): this {
		this._slidable = slidable;
		return this;
	}

	/**
	 * Returns whether users can slide (preview) the region when clicked on a collapsed region.
	 * In other words, if false, clicking on a collapsed region will open it instead of sliding.
	 * <p>Default: true.
	 *
	 * @return boolean whether users can slide (preview) the region.
	 * @since 8.5.2
	 */
	isSlidable(): boolean {
		return this._slidable;
	}

	/**
	 * Sets whether users can open or close the region.
	 * Meaningful only if {@link #isCollapsible()} is true.
	 *
	 * @param boolean closable whether users can open or close the region.
	 * @since 8.5.2
	 */
	setClosable(v: boolean, opts?: Record<string, boolean>): this {
		const o = this._closable;
		this._closable = v;

		if (o !== v || (opts && opts.force)) {
			if (this.desktop && this._collapsible) {
				jq(this.$n('btn')!).toggle(v);
				jq(this.$n('btned')!).toggle(v);
				jq(this.$n('splitbtn')!).toggleClass(this.$s('splitter-button-disabled'), !v);
			}
		}

		return this;
	}

	/**
	 * Returns whether users can open or close the region.
	 * In other words, if false, users are no longer allowed to
	 * change the open status (by clicking the button on the bar).
	 * <p>Default: true.
	 *
	 * @return boolean whether users can open or close the region.
	 * @since 8.5.2
	 */
	isClosable(): boolean {
		return this._closable;
	}

	//bug #3014664
	override setVflex(v: string | null): void { //vflex ignored for LayoutRegion
		if (v != 'min') v = 'false';
		super.setVflex(v);
	}

	//bug #3014664
	override setHflex(v: string | null): void { //hflex ignored for LayoutRigion
		if (v != 'min') v = 'false';
		super.setHflex(v);
	}

	/**
	 * Returns the collapsed margins, which is a list of numbers separated by comma.
	 *
	 * <p>
	 * Default: "3,3,3,3".
	 * @return String
	 */
	getCmargins(): string {
		return zUtl.intsToString(this._open ? this._margins : this._cmargins);
	}

	/**
	 * Sets the collapsed margins for the element "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 * @param String cmargins
	 */
	setCmargins(cmargins: string): void {
		if (this.getCmargins() != cmargins) {
			this._cmargins = zUtl.stringToInts(cmargins, 0)!;
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	}

	/**
	 * Returns the current margins.
	 * @return Dimension
	 */
	getCurrentMargins_(): zk.Dimension {
		return LayoutRegion._aryToObject(this._open ? this._margins : this._cmargins);
	}

	/**
	 * Returns the margins, which is a list of numbers separated by comma.
	 * <p>
	 * Default: "0,0,0,0".
	 * @return String
	 */
	getMargins(): string {
		return zUtl.intsToString(this._margins);
	}

	/**
	 * Sets margins for the element "0,1,2,3" that direction is
	 * "top,left,right,bottom"
	 * @param String margins
	 */
	setMargins(margins: string): void {
		if (this.getMargins() != margins) {
			this._margins = zUtl.stringToInts(margins, 0)!;
			if (this.parent && this.desktop)
				this.parent.resize();
		}
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (!no || !no.zclass) {
			var added = 'normal' == this.getBorder() ? '' : this.$s('noborder');
			if (added) scls += (scls ? ' ' : '') + added;
		}
		return scls;
	}

	override getZclass(): string {
		return this._zclass == null ? 'z-' + this.getPosition() : this._zclass;
	}

	//-- super --//
	override getMarginSize_(attr: string): number {
		return zk(this.$n('real')).sumStyles(attr == 'h' ? 'tb' : 'lr', jq.margins);
	}

	override setWidth(width: string): this {
		this._width = width;
		var real = this.$n('real');
		if (real) {
			real.style.width = width ? width : '';
			real._lastSize = null;
			this.parent!.resize();
		}
		return this;
	}

	override setHeight(height: string): this {
		this._height = height;
		var real = this.$n('real');
		if (real) {
			real.style.height = height ? height : '';
			real._lastSize = null;
			this.parent!.resize();
		}
		return this;
	}

	override setVisible(visible: boolean): this {
		if (this._visible != visible) {
			super.setVisible(visible);
			var real = this.$n('real'),
				colled = this.$n('colled');
			if (real) {
				if (this._visible) {
					if (this._open) {
						jq(real).show();
						if (colled)
							jq(colled).hide();
					} else {
						jq(real).hide();
						if (colled)
							jq(colled).show();
					}
				} else {
					jq(real).hide();
					if (colled)
						jq(colled).hide();
				}
				this.parent!.resize();
			}
		}
		return this;
	}

	//@Override to apply the calculated value on xxx-real element
	override setFlexSize_(sz: { width?: string | number; height?: string | number }, isFlexMin?: boolean): void {
		if (this._cssflex && this.parent!.getFlexContainer_() != null && !isFlexMin)
			return;
		var n = this.$n_('real'),
			ns = n.style;
		if (sz.height !== undefined) {
			if (sz.height == 'auto')
				ns.height = '';
			else if (sz.height == '')
				ns.height = this._height ? this._height : '';
			else {
				var cave = this.$n('cave'),
					cap = this.$n('cap'),
					hgh = cave && this._vflex != 'min' ? (cave.offsetHeight + cave.offsetTop)
						: (sz.height as number) - zk(n).marginHeight();
				if (cap) // B50-ZK-236: add header height
					hgh += cap.offsetHeight;
				ns.height = jq.px0(hgh);
			}
		}
		if (sz.width !== undefined) {
			if (sz.width == 'auto')
				n.style.width = '';
			else if (sz.width == '')
				n.style.width = this._width ? this._width : '';
			else {
				var wdh = (sz.width as number) - zk(n).marginWidth();
				n.style.width = jq.px0(wdh);
			}
		}
	}

	override updateDomClass_(): void {
		if (this.desktop) {
			var real = this.$n('real');
			if (real) {
				real.className = this.domClass_();
				if (this.parent)
					this.parent.resize();
			}
		}
	}

	override updateDomStyle_(): void {
		if (this.desktop) {
			var real = this.$n('real');
			if (real) {
				zk(real).clearStyles().jq.css(jq.parseStyle(this.domStyle_()));
				if (this.parent)
					this.parent.resize();
			}
		}
	}

	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (child instanceof zul.layout.Borderlayout) {
			this._flex = true;
			jq(this.$n()!).addClass(this.$s('nested'));
		}

		// Bug for B36-2841185.zul, resync flex="true"
		if (this.isFlex())
			_setFirstChildFlex(this, true, true);

		// reset
		const n = this.$n('real');
		if (n)
			n._lastSize = null;
		if (this.parent && this.desktop) {
			// B65-ZK-1076 for tabpanel, should fix in isRealVisible() when zk 7
			if (this.parent.isRealVisible({dom: true}))
				this.parent.resize();
		}
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);

		// check before "if (child.$instanceof(zul.layout.Borderlayout)) {"
		if (this.isFlex())
			_setFirstChildFlex(this, false);

		if (child instanceof zul.layout.Borderlayout) {
			this._flex = false;
			jq(this.$n_()).removeClass(this.$s('nested'));
		}

		// reset
		const n = this.$n('real');
		if (n)
			n._lastSize = null;
		if (this.parent && this.desktop && !this.childReplacing_) {
			// B65-ZK-1076 for tabpanel, should fix in isRealVisible() when zk 7
			if (this.parent.isRealVisible({dom: true}))
				this.parent.resize();
		}
	}

	override rerender(skipper?: zk.Skipper | number | null): void {
		super.rerender(skipper);
		if (this.parent) {
			this.parent.resize();
		}
	}

	override bind_(desktop?: zk.Desktop | null, skipper?: zk.Skipper | null, after?: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);
		if (this.getPosition() != zul.layout.Borderlayout.CENTER) {
			var split = this.$n('split');
			if (split) {
				this._fixSplit();
				var vert = this._isVertical(),
					LR = LayoutRegion;

				this._drag = new zk.Draggable(this, split, {
					constraint: vert ? 'vertical' : 'horizontal',
					ghosting: LR._ghosting,
					snap: LR._snap,
					zIndex: 12000,
					overlay: true,
					initSensitivity: 0,
					ignoredrag: LR._ignoredrag,
					endeffect: LR._endeffect
				});

				if (!this._open) {
					var colled = this.$n('colled'),
						real = this.$n('real');
					if (colled)
						jq(colled).show();
					jq(real!).hide();
				}

				if (!this._visible) {
					var colled = this.$n('colled'),
						real = this.$n('real');
					jq(real!).hide();
					if (colled)
						jq(colled).hide();
				}
			}
		}

		if (this._open && !this.isVisible()) this.$n_().style.display = 'none';

		if (this.isAutoscroll()) {
			if (this._nativebar) {
				var bodyEl = this.isFlex() && this.getFirstChild() ?
					this.getFirstChild()!.$n() : this.$n('cave');
				this.domListen_(bodyEl!, 'onScroll');
			} else {
				zWatch.listen({onSize: this});
			}
		}

		if (this.isFlex())
			_setFirstChildFlex(this, true, true);
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		if (this.isAutoscroll()) {
			if (this._nativebar) {
				var bodyEl = this.isFlex() && this.getFirstChild() ?
					this.getFirstChild()!.$n() : this.$n('cave');
				this.domUnlisten_(bodyEl!, 'onScroll');
			} else {
				zWatch.unlisten({onSize: this});
			}
		}

		this.destroyBar_();

		if (this.$n('split')) {
			if (this._drag) {
				this._drag.destroy();
				this._drag = null;
			}
		}

		if (this.isFlex())
			_setFirstChildFlex(this, false);

		super.unbind_(skipper, after, keepRod);
	}

	override afterChildMinFlexChanged_(wgt: zk.Widget, o: string): void {
		if (this.desktop) {
			var cave = this.$n('cave'),
				real = this.$n('real');
			if (cave)
				cave.style[o == 'h' ? 'height' : 'width'] = '';
			if (real)
				real.style[o == 'h' ? 'height' : 'width'] = '';
			// resize again
			this.parent!.resize();
		}
	}

	override onSize(): void {
		var wgt = this;

		// Bug ZK-2784 we should reset the height of the target before doing children's onSize
		if (this._fixBarHeight && this.firstChild) {
			var child = this.firstChild.$n();
			if (child)
				child.style.height = '';
		}

		setTimeout(function () {
			if (wgt.desktop) {
				// ZK-2217: should init scrollbar if the cave first child exists
				var cave = wgt.$n('cave');
				if (!wgt._scrollbar && !wgt._nativebar && cave && cave.firstChild)
					wgt._scrollbar = wgt.initScrollbar_();
				(wgt as LayoutRegion).refreshBar_();
			}
		}, 200);
	}

	initScrollbar_(): zul.Scrollbar {
		var wgt = this,
			embed = jq(wgt.$n_('real')).data('embedscrollbar') !== false, // change default value to true since 7.0.2
			cave = wgt.$n_('cave');
		return new zul.Scrollbar(cave, cave.firstChild as HTMLElement, {
			embed: embed,
			onScrollEnd: function () {
				wgt._doScroll();
			}
		});
	}

	refreshBar_(showBar?: boolean, scrollToTop?: boolean): void {
		var bar = this._scrollbar;
		if (bar && this._open) {
			var p = this.$n_('cave'),
				fc = this.firstChild,
				fch = fc ? fc.getHeight() : '',
				c = p.firstChild,
				ph = p.offsetHeight;

			while (c && c.nodeType == 3)
				c = c.nextSibling;
			if (c) {
				var cs = (c as HTMLElement).style;

				if (!fch || !fch.indexOf('px')) { // only recalculate size if no fixed height
					// force to recalculate size
					cs.height = '';
					if ((c as HTMLElement).offsetHeight) {
						// empty to force recalculate size
					}

					cs.height = jq.px((c as HTMLElement).scrollHeight >= ph ? (c as HTMLElement).scrollHeight : ph);
					this._fixBarHeight = true;
				} else {
					this._fixBarHeight = false;
				}
				bar.scroller = c as HTMLElement;
				bar.syncSize(showBar);
				if (scrollToTop)
					bar.scrollTo(0, 0);
			}
		}
	}

	destroyBar_(): void {
		var bar = this._scrollbar;
		if (bar) {
			bar.destroy();
			delete this._scrollbar;
		}
	}

	override doResizeScroll_(): void {
		super.doResizeScroll_();
		this.refreshBar_(true);
	}

	onResponse(): void {
		this._fireSizedIfChildFlex();
	}

	_fireSizedIfChildFlex(): void {
		// only fire when child has h/vflex
		for (var w = this.firstChild; w; w = w.nextSibling) {
			if ((w._nvflex && w._nvflex > 0) || (w._nhflex && w._nhflex > 0)) {
				zUtl.fireSized(this);
				break;
			}
		}
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var target = evt.domTarget;
		if (!target.id)
			target = target.parentNode as HTMLElement;
		switch (target) {
			case this.$n('colled'):
			case this.$n('title'):
				if (this._slidable) {
					this.setSlide(!this._isSlide);
					break;
				}
			// fall through if not sildable
			case this.$n('btn'):
			case this.$n('btned'):
			case this.$n('splitbtn'):
				if (!this.isCollapsible() || this._isSlide || zk.animating() || !this._closable)
					return;
				if (!this._open) {
					var s = this.$n_('real').style;
					s.visibility = 'hidden';
					s.display = '';
					this._syncSize(true);
					s.visibility = '';
					s.display = 'none';
				}
				this.setOpen(!this._open);
				break;
		}
		super.doClick_(evt, popupOnly);
	}

	_docClick(evt: JQuery.TriggeredEvent): void {
		var target = evt.target as HTMLElement;
		if (this._isSlide && !jq.isAncestor(this.$n('real'), target)) {
			var btned = this.$n('btned');
			if (this._closable && (btned == target || btned == target.parentNode)) {
				this.setSlide(false, undefined, true);
				this.setOpen(true, undefined, true);
				this.$n_('real').style.zIndex = ''; //reset
			} else if ((LayoutRegion.uuid(target) != this.uuid) || !zk.animating()) {
				this.setSlide(false);
			}
		}
	}

	_syncSize(inclusive?: boolean): void {
		var layout = this.parent!,
			el = layout.$n_(),
			width = el.offsetWidth,
			height = el.offsetHeight,
			center = {
				x: 0,
				y: 0,
				w: width,
				h: height
			};

		this._open = true;

		for (var region: LayoutRegion | undefined, rs = ['north', 'south', 'west', 'east'],
				 j = 0, k = rs.length; j < k; ++j) {
			region = layout[rs[j]] as LayoutRegion | undefined;
			if (region && (zk(region.$n()).isVisible()
				|| zk(region.$n('colled')).isVisible())) {
				var ignoreSplit = region == this,
					ambit = region._ambit(ignoreSplit),
					LR = LayoutRegion;
				switch (rs[j]) {
					case 'north':
					case 'south':
						ambit.w = width - ambit.w;
						if (rs[j] == 'north')
							center.y = ambit.ts!;
						else
							ambit.y = height - ambit.y;
						center.h -= ambit.ts!;
						if (ignoreSplit) {
							ambit.w = this.$n_('colled').offsetWidth;
							if (inclusive) {
								var cmars = LR._aryToObject(this._cmargins);
								ambit.w += cmars.width;
							}
							layout._resizeWgt(region, ambit, true);
							this._open = false;
							return;
						}
						break;
					default:
						ambit.y += center.y;
						ambit.h = center.h - ambit.h;
						if (rs[j] == 'east')
							ambit.x = width - ambit.x;
						else center.x += ambit.ts!;
						center.w -= ambit.ts!;
						if (ignoreSplit) {
							ambit.h = this.$n_('colled').offsetHeight;
							if (inclusive) {
								var cmars = LR._aryToObject(this._cmargins);
								ambit.h += cmars.height;
							}
							layout._resizeWgt(region, ambit, true);
							this._open = false;
							return;
						}
						break;
				}
			}
		}
	}

	_alignTo(): void {
		var from = this.$n_('colled'),
			to = this.$n_('real'),
			ts = to.style,
			BL = zul.layout.Borderlayout;
		switch (this.getPosition()) {
			case BL.NORTH:
				ts.top = jq.px(from.offsetTop + from.offsetHeight);
				ts.left = jq.px(from.offsetLeft);
				break;
			case BL.SOUTH:
				ts.top = jq.px(from.offsetTop - to.offsetHeight);
				ts.left = jq.px(from.offsetLeft);
				break;
			case BL.WEST:
				ts.left = jq.px(from.offsetLeft + from.offsetWidth);
				ts.top = jq.px(from.offsetTop);
				break;
			case BL.EAST:
				ts.left = jq.px(from.offsetLeft - to.offsetWidth);
				ts.top = jq.px(from.offsetTop);
				break;
		}
	}

	_doScroll(): void {
		zWatch.fireDown('onScroll', this);
		zWatch.fire('_onSyncScroll', this); // ZK-4408: for Popup only
	}

	_fixSplit(): void {
		var split = this.$n('split');
		if (split)
			split.style.display = this._splittable ? 'block' : 'none';
	}

	_fixFontIcon(): void {
		zk(this).redoCSS(-1, {
			'fixFontIcon': true,
			'selector': '.z-borderlayout-icon'
		});
	}

	_isVertical(): boolean {
		var BL = zul.layout.Borderlayout;
		return this.getPosition() != BL.WEST
			&& this.getPosition() != BL.EAST;
	}

	// returns the ambit of the specified cmp for region calculation.
	_ambit(ignoreSplit?: boolean): LayoutRegionAmbit {
		var ambit: LayoutRegionAmbit, mars = this.getCurrentMargins_(),
			region = this.getPosition();
		if (region && !this._open) {
			var colled = this.$n('colled');
			ambit = {
				x: mars.left,
				y: mars.top,
				w: colled ? colled.offsetWidth : 0,
				h: colled ? colled.offsetHeight : 0
			};
			ignoreSplit = true;
		} else {
			var pn = this.parent!.$n_(),
				w = this.getWidth() ?? '',
				h = this.getHeight() ?? '',
				pert: number;
			ambit = {
				x: mars.left,
				y: mars.top,
				w: (pert = w.indexOf('%')) > 0 ?
					Math.max(
						Math.floor(pn.offsetWidth * zk.parseInt(w.substring(0, pert)) / 100),
						0) : this.$n_('real').offsetWidth,
				h: (pert = h.indexOf('%')) > 0 ?
					Math.max(
						Math.floor(pn.offsetHeight * zk.parseInt(h.substring(0, pert)) / 100),
						0) : this.$n_('real').offsetHeight
			};
		}
		var split = ignoreSplit ? {
			offsetHeight: 0,
			offsetWidth: 0
		} : this.$n('split') ?? {offsetHeight: 0, offsetWidth: 0};
		if (!ignoreSplit) this._fixSplit();

		this._ambit2(ambit, mars, split);
		return ambit;
	}

	_ambit2(ambit: LayoutRegionAmbit, mars: zk.Dimension, split: { offsetWidth; offsetHeight }): void {
		// empty
	}

	setBtnPos_(ambit: LayoutRegionAmbit, ver: boolean): void {
		var sbtn = this.$n_('splitbtn');
		if (ver)
			sbtn.style.marginLeft = jq.px0(((ambit.w - sbtn.offsetWidth) / 2));
		else
			sbtn.style.marginTop = jq.px0(((ambit.h - sbtn.offsetHeight) / 2));
	}

	_reszSplt(ambit: LayoutRegionAmbit): LayoutRegionAmbit {
		var split = this.$n_('split'),
			sbtn = this.$n('splitbtn');
		if (zk(split).isVisible()) {
			if (zk(sbtn).isVisible()) {
				this.setBtnPos_(ambit, this._isVertical());
			}
			zk.copy(split.style, this._reszSp2(ambit, {
				w: split.offsetWidth,
				h: split.offsetHeight
			}));
		}
		return ambit;
	}

	_reszSp2(ambit: LayoutRegionAmbit, split: { w; h }): Partial<{ left; top; width; height }> {
		// empty;
		return {};
	}

	getIconClass_(collapsed?: boolean): string {
		var BL = zul.layout.Borderlayout;
		switch (this.getPosition()) {
			case BL.NORTH:
				return collapsed ? 'z-icon-angle-double-down' : 'z-icon-angle-double-up';
			case BL.SOUTH:
				return collapsed ? 'z-icon-angle-double-up' : 'z-icon-angle-double-down';
			case BL.WEST:
				return collapsed ? 'z-icon-angle-double-right' : 'z-icon-angle-double-left';
			case BL.EAST:
				return collapsed ? 'z-icon-angle-double-left' : 'z-icon-angle-double-right';
		}
		return ''; // no icon
	}

	titleRenderer_(out: string[]): void {
		if (this._title) {
			var uuid = this.uuid,
				pos = this.getPosition(),
				noCenter = pos != zul.layout.Borderlayout.CENTER,
				parent = this.parent!;

			out.push('<div id="', uuid, '-cap" class="', this.$s('header'), '">');
			if (noCenter) {
				out.push('<i id="', uuid, '-btn" class="', parent.$s('icon'),
					' ', this.getIconClass_(), '"');
				if (!this._collapsible || !this._closable)
					out.push(' style="display:none;"');
				out.push('></i>');
			}
			out.push(zUtl.encodeXML(this._title), '</div>');
		}
	}

	getPosition(): string {
		return '';
	}

	getFirstChild(): zk.Widget | null {
		return this.firstChild;
	}

	static _aryToObject(array: number[]): zk.Dimension {
		return {
			top: array[0],
			left: array[1],
			width: array[1] + array[2],
			height: array[0] + array[3]
		};
	}

	// invokes border layout's renderer before the component slides out
	static beforeSlideOut(this: LayoutRegion, _n: zk.JQZK): void {
		var s = this.$n_('colled').style;
		s.display = '';
		s.visibility = 'hidden';
		s.zIndex = '1';
		this.parent!.resize();
	}

	static _afterSlideOutX(this: LayoutRegion, n: HTMLElement): void {
		// B50-ZK-301: fire onOpen after animation
		LayoutRegion.afterSlideOut.call(this, n, true);
		this._fixFontIcon();
	}

	// a callback function after the component slides out.
	static afterSlideOut(this: LayoutRegion, n: HTMLElement, fireOnOpen?: unknown): void {
		if (this._open)
			zk(this.$n('real')).slideIn(this, {
				anchor: this.sanchor,
				afterAnima: fireOnOpen ? LayoutRegion._afterSlideInX : LayoutRegion.afterSlideIn
			});
		else {
			var colled = this.$n_('colled'),
				s = colled.style;
			s.zIndex = ''; // reset z-index refered to the beforeSlideOut()
			s.visibility = '';
			zk(colled).slideIn(this, {
				anchor: this.sanchor,
				duration: 200,
				// B50-ZK-301: fire onOpen after animation
				afterAnima: fireOnOpen ? function (this: zk.Widget & {_open: boolean}, _n: HTMLElement) {
					this.fire('onOpen', {open: this._open});
				} : zk.$void
			});
		}
		this._fixFontIcon();
	}

	static _afterSlideInX(this: LayoutRegion, n: HTMLElement): void {
		// B50-ZK-301: fire onOpen after animation
		LayoutRegion.afterSlideIn.call(this, n);
		this.fire('onOpen', {open: this._open});
		this._fixFontIcon();
	}

	// recalculates the size of the whole border layout after the component sildes in.
	static afterSlideIn(this: LayoutRegion, _n: HTMLElement): void {
		this.parent!.resize();
		this._fixFontIcon();
	}

	// a callback function after the collapsed region slides down
	static afterSlideDown(this: LayoutRegion, _n: HTMLElement): void {
		jq(document).on('click', this.proxy(this._docClick));
		this._fixFontIcon();
	}

	// a callback function after the collapsed region slides up
	static afterSlideUp(this: LayoutRegion, n: HTMLElement): void {
		var s = n.style;
		s.left = this._original![0];
		s.top = this._original![1];
		n._lastSize = null;// reset size for Borderlayout
		s.zIndex = '';
		if (this.$n('btn'))
			this.$n_('btn').style.display = this._closable ? '' : 'none';
		jq(this.$n_()).removeClass(this.$s('slide'));
		jq(document).off('click', this.proxy(this._docClick));
		this._isSlide = false;
		this._fixFontIcon();
	}

	//drag
	static _ignoredrag(dg: zk.Draggable, _pointer: zk.Offset, evt: zk.Event): boolean {
		var target = evt.domTarget,
			wgt = dg.control as LayoutRegion,
			split = wgt.$n_('split');
		if (!target || (split != target && !jq.contains(split, target))) return true;
		if (wgt.isSplittable() && wgt._open) {
			var BL = zul.layout.Borderlayout,
				pos = wgt.getPosition(),
				maxs = wgt.getMaxsize(),
				mins = wgt.getMinsize(),
				ol = wgt.parent!,
				real = wgt.$n_('real'),
				mars = zul.layout.LayoutRegion._aryToObject(wgt._margins),
				pbw = zk(real).padBorderWidth(),
				lr = pbw + (pos == BL.WEST ? mars.left : (mars.width - mars.left)),
				tb = pbw + (pos == BL.NORTH ? mars.top : (mars.height - mars.top)),
				min = 0;
			switch (pos) {
				case BL.NORTH:
				case BL.SOUTH: {
					let r = ol.center ?? (pos == BL.NORTH ? ol.south : ol.north);
					if (r) {
						if (BL.CENTER == r.getPosition()) {
							maxs = Math.min(maxs, (real.offsetHeight + r.$n_('real').offsetHeight) - min);
						} else {
							maxs = Math.min(maxs, ol.$n_().offsetHeight
								- r.$n_('real').offsetHeight - r.$n_('split').offsetHeight
								- wgt.$n_('split').offsetHeight - min);
						}
					} else {
						maxs = ol.$n_().offsetHeight - wgt.$n_('split').offsetHeight;
					}
					break;
				}
				case BL.WEST:
				case BL.EAST: {
					let r = ol.center ?? (pos == BL.WEST ? ol.east : ol.west);
					if (r) {
						if (BL.CENTER == r.getPosition()) {
							maxs = Math.min(maxs, (real.offsetWidth
								+ r.$n_('real').offsetWidth) - min);
						} else {
							maxs = Math.min(maxs, ol.$n_().offsetWidth
								- r.$n_('real').offsetWidth - r.$n_('split').offsetWidth
								- wgt.$n_('split').offsetWidth - min);
						}
					} else {
						maxs = ol.$n_().offsetWidth - wgt.$n_('split').offsetWidth;
					}
					break;
				}
			}
			var ofs = zk(real).cmOffset();
			dg._rootoffs = {
				maxs: maxs,
				mins: mins,
				top: ofs[1] + tb,
				left: ofs[0] + lr,
				right: real.offsetWidth,
				bottom: real.offsetHeight
			};
			return false;
		}
		return true;
	}

	static _endeffect(dg: zk.Draggable, evt: zk.Event): void {
		var wgt = dg.control as LayoutRegion;
		if (wgt._isVertical())
			wgt.setHeight(dg._point![1] + 'px');
		else
			wgt.setWidth(dg._point![0] + 'px');

		// Bug #1939859
		wgt.$n_().style.zIndex = '';

		dg._rootoffs = dg._point = null;

		wgt.parent!.resize();
		wgt.fire('onSize', zk.copy({
			width: wgt.$n_('real').style.width,
			height: wgt.$n_('real').style.height
		}, evt.data));
	}

	static _snap(dg: zk.Draggable, pointer: zk.Offset): zk.Offset {
		var wgt = dg.control as LayoutRegion,
			x = pointer[0],
			y = pointer[1],
			BL = zul.layout.Borderlayout,
			split = wgt.$n_('split'),
			b = dg._rootoffs!, w, h;
		switch (wgt.getPosition()) {
			case BL.NORTH:
				if (y > b.maxs + b.top) y = b.maxs + b.top;
				if (y < b.mins + b.top) y = b.mins + b.top;
				w = x;
				h = y - b.top;
				break;
			case BL.SOUTH:
				if (b.top + b.bottom - y - split.offsetHeight > b.maxs) {
					y = b.top + b.bottom - b.maxs - split.offsetHeight;
					h = b.maxs;
				} else if (b.top + b.bottom - b.mins - split.offsetHeight <= y) {
					y = b.top + b.bottom - b.mins - split.offsetHeight;
					h = b.mins;
				} else h = b.top - y + b.bottom - split.offsetHeight;
				w = x;
				break;
			case BL.WEST:
				if (x > b.maxs + b.left) x = b.maxs + b.left;
				if (x < b.mins + b.left) x = b.mins + b.left;
				w = x - b.left;
				h = y;
				break;
			case BL.EAST:
				if (b.left + b.right - x - split.offsetWidth > b.maxs) {
					x = b.left + b.right - b.maxs - split.offsetWidth;
					w = b.maxs;
				} else if (b.left + b.right - b.mins - split.offsetWidth <= x) {
					x = b.left + b.right - b.mins - split.offsetWidth;
					w = b.mins;
				} else w = b.left - x + b.right - split.offsetWidth;
				h = y;
				break;
		}
		dg._point = [w, h];
		return [x, y];
	}

	static _ghosting(dg: zk.Draggable, ofs: zk.Offset, evt: zk.Event): HTMLElement {
		var el = dg.node!, $el = zk(el);
		jq(document.body).prepend('<div id="zk_layoutghost" class="z-splitter-ghost" style="font-size:0;line-height:0;background:#AAA;position:absolute;top:'
			+ ofs[1] + 'px;left:' + ofs[0] + 'px;width:'
			+ $el.offsetWidth() + 'px;height:' + $el.offsetHeight()
			+ 'px;cursor:' + el.style.cursor + ';"></div>');
		return jq('#zk_layoutghost')[0];
	}
}
