/* Panel.ts

	Purpose:

	Description:

	History:
		Mon Jan 12 18:31:03 2009, Created by jumperchen

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/

interface NodeInfo {
	/** @internal */
	_scrollTop: number;
	/** @internal */
	_pos?: string;
	/** @internal */
	_ppos?: string;
	/** @internal */
	_zIndex?: string;
	/** @internal */
	_pheight?: boolean;
}

export interface Dimension {
	width: string;
	height: string;
	left: string;
	top: string;
}

/**
 * Panel is a container that has specific functionality and structural components
 * that make it the perfect building block for application-oriented user interfaces.
 * The Panel contains bottom, top, and foot toolbars, along with separate header,
 * footer and body sections. It also provides built-in collapsible, closable,
 * maximizable, and minimizable behavior, along with a variety of pre-built tool
 * buttons that can be wired up to provide other customized behavior. Panels can
 * be easily embedded into any kind of ZUL component that is allowed to have children
 * or layout component. Panels also provide specific features like float and move.
 * Unlike {@link zul.wnd.Window}, Panels can only be floated and moved inside its parent
 * node, which is not using {@link _global_.jqzk#makeVParent} function. In other words,
 * if Panel's parent node is an relative position, the floated panel is only inside
 * its parent, not the whole page.
 * The second difference of {@link zul.wnd.Window} is that Panel is not an independent ID
 * space, so the ID of each child can be used throughout the panel.
 *
 * <p>Events:<br/>
 * onMove, onOpen, onZIndex, onMaximize, onMinimize, and onClose.<br/>
 *
 * @defaultValue {@link getZclass}: z-panel.
 *
 */
@zk.WrapClass('zul.wnd.Panel')
export class Panel extends zul.Widget {
	/** @internal */
	_border = 'none';
	/** @internal */
	_title = '';
	/** @internal */
	_open = true;
	/** @internal */
	_minheight = 100;
	/** @internal */
	_minwidth = 200;
	/** @internal */
	override _tabindex = 0;
	/** @internal */
	_nativebar = true;
	caption?: zul.wgt.Caption;
	panelchildren?: zul.wnd.Panelchildren;
	tbar?: zul.wgt.Toolbar;
	fbar?: zul.wgt.Toolbar;
	bbar?: zul.wgt.Toolbar;
	/** @internal */
	_tbar?: string;
	/** @internal */
	_bbar?: string;
	/** @internal */
	_fbar?: string;
	/** @internal */
	_skipper: zul.wnd.PanelSkipper;
	/** @internal */
	_sizable?: boolean;
	/** @internal */
	_movable?: boolean;
	/** @internal */
	_floatable?: boolean;
	/** @internal */
	_sizer?: zk.Draggable;
	/** @internal */
	_maximizable?: boolean;
	/** @internal */
	_minimizable?: boolean;
	/** @internal */
	_collapsible?: boolean;
	/** @internal */
	_closable?: boolean;
	/** @internal */
	_maximized?: boolean;
	/** @internal */
	_inWholeMode?: boolean;
	/** @internal */
	_oldNodeInfo?: NodeInfo;
	/** @internal */
	_lastSize?: { l: string; t: string; w: string; h: string };
	/** @internal */
	_minimized?: boolean;
	/** @internal */
	_shadow?: zk.eff.Shadow;
	/** @internal */
	_backupCursor?: string;

	constructor() {
		super(); // FIXME: params?
		this.listen({onMaximize: this, onClose: this, onMove: this, onSize: this.onSizeEvent}, -1000);
		this._skipper = new zul.wnd.PanelSkipper(this);
	}

	/**
	 * Sets the minimum height in pixels allowed for this panel.
	 * If negative, 100 is assumed.
	 * @defaultValue `100`.
	 * <p>Note: Only applies when {@link isSizable} = true.
	 */
	setMinheight(minheight: number): this { //TODO
		this._minheight = minheight;
		return this;
	}

	/**
	 * @returns (int) the minimum height.
	 * @defaultValue `100`.
	 */
	getMinheight(): number { //TODO
		return this._minheight;
	}

	/**
	 * Sets the minimum width in pixels allowed for this panel. If negative,
	 * 200 is assumed.
	 * @defaultValue `200`.
	 * <p>Note: Only applies when {@link isSizable} = true.
	 */
	setMinwidth(minwidth: number): this { //TODO
		this._minwidth = minwidth;
		return this;
	}

	/**
	 * @returns (int) the minimum width.
	 * @defaultValue `200`.
	 */
	getMinwidth(): number { //TODO
		return this._minwidth;
	}

	/**
	 * Sets whether the panel is sizable.
	 * If true, an user can drag the border to change the panel width.
	 * @defaultValue `false`.
	 */
	setSizable(sizable: boolean, opts?: Record<string, boolean>): this {
		const o = this._sizable;
		this._sizable = sizable;

		if (o !== sizable || opts?.force) {
			if (this.desktop) {
				if (sizable)
					this._makeSizer();
				else if (this._sizer) {
					this._sizer.destroy();
					this._sizer = undefined;
				}
			}
		}

		return this;
	}

	/**
	 * @returns whether the panel is sizable.
	 */
	isSizable(): boolean {
		return !!this._sizable;
	}

	/**
	 * Sets whether to move the panel to display it inline where it is rendered.
	 *
	 * @defaultValue `false`;
	 * <p>Note that this method only applied when {@link isFloatable} is true.
	 */
	setMovable(movable: boolean, opts?: Record<string, boolean>): this {
		const o = this._movable;
		this._movable = movable;

		if (o !== movable || opts?.force) {
			var last = this._lastSize; //Bug ZK-1500: remember last size before rerender
			this.rerender(this._skipper);
			if (last)
				this._lastSize = last;
		}

		return this;
	}

	/**
	 * @returns whether to move the panel to display it inline where it is rendered.
	 * @defaultValue `false`.
	 */
	isMovable(): boolean {
		return !!this._movable;
	}

	/**
	 * Sets whether to float the panel to display it inline where it is rendered.
	 *
	 * <p>Note that by default, setting floatable to true will cause the
	 * panel to display at default offsets, which depend on the offsets of
	 * the embedded panel from its element to <i>document.body</i> -- because the panel
	 * is absolute positioned, the position must be set explicitly by {@link setTop}
	 * and {@link setLeft}. Also, when floatable a panel you should always
	 * assign a fixed width, otherwise it will be auto width and will expand to fill
	 * to the right edge of the viewport.
	 */
	setFloatable(floatable: boolean, opts?: Record<string, boolean>): this {
		const o = this._floatable;
		this._floatable = floatable;

		if (o !== floatable || opts?.force) {
			var last = this._lastSize; //Bug ZK-1500: remember last size before rerender
			this.rerender(this._skipper);
			if (last)
				this._lastSize = last;
		}

		return this;
	}

	/**
	 * @returns whether to float the panel to display it inline where it is rendered.
	 * @defaultValue `false`.
	 */
	isFloatable(): boolean {
		return !!this._floatable;
	}

	/**
	 * Sets whether to display the maximizing button and allow the user to maximize
	 * the panel, when a panel is maximized, the button will automatically
	 * change to a restore button with the appropriate behavior already built-in
	 * that will restore the panel to its previous size.
	 * @defaultValue `false`.
	 *
	 * <p>Note: the maximize button won't be displayed if no title or caption at all.
	 */
	setMaximizable(maximizable: boolean, opts?: Record<string, boolean>): this {
		const o = this._maximizable;
		this._maximizable = maximizable;

		if (o !== maximizable || opts?.force) {
			var last = this._lastSize; //Bug ZK-1500: remember last size before rerender
			this.rerender(this._skipper);
			if (last)
				this._lastSize = last;
		}

		return this;
	}

	/**
	 * @returns whether to display the maximizing button and allow the user to maximize
	 * the panel.
	 * @defaultValue `false`.
	 */
	isMaximizable(): boolean {
		return !!this._maximizable;
	}

	/**
	 * Sets whether to display the minimizing button and allow the user to minimize
	 * the panel. Note that this button provides no implementation -- the behavior
	 * of minimizing a panel is implementation-specific, so the MinimizeEvent
	 * event must be handled and a custom minimize behavior implemented for this
	 * option to be useful.
	 *
	 * @defaultValue `false`.
	 * <p>Note: the maximize button won't be displayed if no title or caption at all.
	 */
	setMinimizable(minimizable: boolean, opts?: Record<string, boolean>): this {
		const o = this._minimizable;
		this._minimizable = minimizable;

		if (o !== minimizable || opts?.force) {
			var last = this._lastSize; //Bug ZK-1500: remember last size before rerender
			this.rerender(this._skipper);
			if (last)
				this._lastSize = last;
		}

		return this;
	}

	/**
	 * @returns whether to display the minimizing button and allow the user to minimize
	 * the panel.
	 * @defaultValue `false`.
	 */
	isMinimizable(): boolean {
		return !!this._minimizable;
	}

	/**
	 * Sets whether to show a toggle button on the title bar.
	 * @defaultValue `false`.
	 * <p>Note: the toggle button won't be displayed if no title or caption at all.
	 */
	setCollapsible(collapsible: boolean, opts?: Record<string, boolean>): this {
		const o = this._collapsible;
		this._collapsible = collapsible;

		if (o !== collapsible || opts?.force) {
			var last = this._lastSize; //Bug ZK-1500: remember last size before rerender
			this.rerender(this._skipper);
			if (last)
				this._lastSize = last;
		}

		return this;
	}

	/**
	 * @returns whether to show a toggle button on the title bar.
	 * @defaultValue `false`.
	 */
	isCollapsible(): boolean {
		return !!this._collapsible;
	}

	/**
	 * Sets whether to show a close button on the title bar.
	 * If closable, a button is displayed and the onClose event is sent
	 * if an user clicks the button.
	 *
	 * @defaultValue `false`.
	 *
	 * <p>Note: the close button won't be displayed if no title or caption at all.
	 */
	setClosable(closable: boolean, opts?: Record<string, boolean>): this {
		const o = this._closable;
		this._closable = closable;

		if (o !== closable || opts?.force) {
			var last = this._lastSize; //Bug ZK-1500: remember last size before rerender
			this.rerender(this._skipper);
			if (last)
				this._lastSize = last;
		}

		return this;
	}

	/**
	 * @returns whether to show a close button on the title bar.
	 */
	isClosable(): boolean {
		return !!this._closable;
	}

	/**
	 * Sets the border.
	 * Allowed values include `none` (default), `normal`,
	 * `rounded` and `rounded+`.
	 * For more information, please refer to
	 * <a href="http://books.zkoss.org/wiki/ZK_Component_Reference/Containers/Panel#Border">ZK Component Reference: Panel</a>.
	 * @param border - the border. If null or "0", "none" is assumed.
	 */
	setBorder(border: string, opts?: Record<string, boolean>): this {
		const o = this._border;
		this._border = border;

		if (o !== border || opts?.force) {
			var last = this._lastSize;
			this.rerender(); // no skipper, as body DOM depends on border
			if (last)
				this._lastSize = last;
		}

		return this;
	}

	/**
	 * @returns the border.
	 *
	 * @defaultValue `"none"`.
	 */
	getBorder(): string {
		return this._border;
	}

	/**
	 * Sets the title.
	 */
	setTitle(title: string, opts?: Record<string, boolean>): this {
		const o = this._title;
		this._title = title;

		if (o !== title || opts?.force) {
			if (this.caption) {
				this.caption.updateDomContent_(); // B50-ZK-313
			} else {
				var last = this._lastSize;
				this.rerender(this._skipper);
				if (last)
					this._lastSize = last;
			}
		}

		return this;
	}

	/**
	 * @returns the title.
	 * Besides this attribute, you could use {@link zul.wgt.Caption} to define
	 * a more sophisticated caption (aka., title).
	 * <p>If a panel has a caption whose label ({@link zul.wgt.Caption#getLabel})
	 * is not empty, then this attribute is ignored.
	 * @defaultValue empty.
	 */
	getTitle(): string {
		return this._title;
	}

	/**
	 * Opens or closes this Panel.
	 */
	setOpen(open: boolean, fromServer?: boolean, opts?: Record<string, boolean>): this {
		const o = this._open;
		this._open = open;

		if (o !== open || opts?.force) {
			var node = this.$n(),
				up = this.getCollapseOpenIconClass_(),
				down = this.getCollapseCloseIconClass_();
			if (node) {
				var $body = jq(this.$n_('body'));
				if ($body[0] && !$body.is(':animated')) {
					if (open) {
						jq(node).removeClass(this.$s('collapsed'));
						// `exp` might not exist
						jq(this.$n('exp')).attr('title', msgzul.PANEL_COLLAPSE)
						.children('.' + down).removeClass(down).addClass(up);
						$body.zk.slideDown(this);
					} else {
						// `exp` might not exist
						jq(node).addClass(this.$s('collapsed'));
						jq(this.$n('exp')).attr('title', msgzul.PANEL_EXPAND)
						.children('.' + up).removeClass(up).addClass(down);
						this._hideShadow();
						$body.zk.slideUp(this);
					}
					if (!fromServer) this.fire('onOpen', {open: open});
				}
			}
		}

		return this;
	}

	/**
	 * @returns whether this Panel is open.
	 * @defaultValue `true`.
	 */
	isOpen(): boolean {
		return this._open;
	}

	/**
	 * Sets whether the panel is maximized, and then the size of the panel will depend
	 * on it to show a appropriate size. In other words, if true, the size of the
	 * panel will count on the size of its offset parent node whose position is
	 * absolute (by {@link isFloatable}) or its parent node. Otherwise, its size
	 * will be original size. Note that the maximized effect will run at client's
	 * sizing phase not initial phase.
	 *
	 * @defaultValue `false`.
	 */
	setMaximized(maximized: boolean, fromServer?: boolean, opts?: Record<string, boolean>): this {
		const o = this._maximized;
		this._maximized = maximized;

		if (o !== maximized || opts?.force) {
			var node = this.$n();
			if (node) {
				var $n = zk(node),
					isRealVisible = $n.isRealVisible();
				if (!isRealVisible && maximized) return this;

				var l: string, t: string, w: string, h: string,
				s = node.style,
				up = this.getMaximizableIconClass_(),
				down = this.getMaximizedIconClass_();
				if (maximized) {
					jq(this.$n_('max')).addClass(this.$s('maximized'))
					.attr('title', msgzul.PANEL_RESTORE)
					.children('.' + up).removeClass(up).addClass(down);
					this._hideShadow();

					if (this._collapsible && !this._open) {
						$n.jq.removeClass(this.$s('collapsed'));
						var body = this.$n('body');
						if (body) body.style.display = '';
					}
					var floated = this.isFloatable(),
						$op = floated ? jq(node).offsetParent() : jq(node).parent(),
						sh = $op.zk.clientHeightDoubleValue();

					if (zk.isLoaded('zkmax.layout') && this.parent instanceof zkmax.layout.Portalchildren) {
						var layout = this.parent.parent!;
						if (layout.getMaximizedMode() == 'whole') {
							this._inWholeMode = true;
							var p = layout.$n_(), ps = p.style;
							sh = p.clientHeight;
							var oldinfo: NodeInfo = this._oldNodeInfo = { _scrollTop: (p.parentNode as HTMLElement).scrollTop };
							(p.parentNode as HTMLElement).scrollTop = 0;
							$n.makeVParent();
							zWatch.fireDown('onVParent', this);

							oldinfo._pos = s.position;
							oldinfo._ppos = ps.position;
							oldinfo._zIndex = s.zIndex;

							s.position = 'absolute';
							this.setFloating_(true);
							this.setTopmost();
							p.appendChild(node);
							ps.position = 'relative';
							if (!ps.height) {
								ps.height = jq.px0(sh);
								oldinfo._pheight = true;
							}
						}
					}
					var floated = this.isFloatable(),
						$op = floated ? jq(node).offsetParent() : jq(node).parent(),
						zkop = $op.zk;
					l = s.left;
					t = s.top;
					w = s.width;
					h = s.height;

					// prevent the scroll bar.
					s.top = '-10000px';
					s.left = '-10000px';

					var sw = zkop.clientWidthDoubleValue();

					if (!floated) {
						sw -= zkop.paddingWidth();
						sw = $n.revisedWidth(sw);
						sh -= zkop.paddingHeight();
						sh = $n.revisedHeight(sh);
					}

					s.width = jq.px0(sw);
					s.height = jq.px0(sh);

					if (!this._lastSize)
						this._lastSize = {l: l, t: t, w: w, h: h};

					// restore.
					s.top = '0';
					s.left = '0';

					// resync
					w = s.width;
					h = s.height;
				} else {
					var max = this.$n_('max'),
						$max = jq(max);
					$max.removeClass(this.$s('maximized'))
					.attr('title', msgzul.PANEL_MAXIMIZE)
					.children('.' + down).removeClass(down).addClass(up);
					if (this._lastSize) {
						s.left = this._lastSize.l;
						s.top = this._lastSize.t;
						s.width = this._lastSize.w;
						s.height = this._lastSize.h;
						this._lastSize = undefined;
					}
					l = s.left;
					t = s.top;
					w = s.width;
					h = s.height;
					if (this._collapsible && !this._open) {
						jq(node).addClass(this.$s('collapsed'));
						var body = this.$n('body');
						if (body) body.style.display = 'none';
					}
					var body = this.panelchildren ? this.panelchildren.$n() : undefined;
					if (body)
						body.style.width = body.style.height = '';

					if (this._inWholeMode) {
						$n.undoVParent();
						zWatch.fireDown('onVParent', this);

						const oldinfo = this._oldNodeInfo;
						node.style.position = oldinfo ? oldinfo._pos! : '';
						this.setZIndex((oldinfo ? oldinfo._zIndex! : ''), {fire: true});
						this.setFloating_(false);
						const p = this.parent!.parent!.$n_();
						p.style.position = oldinfo ? oldinfo._ppos! : '';
						(p.parentNode as HTMLElement).scrollTop = oldinfo ? oldinfo._scrollTop : 0;
						if (oldinfo?._pheight)
							p.style.height = '';
						this._oldNodeInfo = undefined;
						this._inWholeMode = false;
					}
				}
				if (!fromServer && isRealVisible) {
					this._visible = true;
					this.fire('onMaximize', {
						left: l,
						top: t,
						width: w,
						height: h,
						maximized: maximized,
						fromServer: fromServer
					});
				}
				if (isRealVisible) {
					// B50-ZK-324: always counts on onSize to do the work
					//this.__maximized = true;
					zUtl.fireSized(this);
				}
			}
		}

		return this;
	}

	/**
	 * @returns whether the panel is maximized.
	 */
	isMaximized(): boolean {
		return !!this._maximized;
	}

	/**
	 * Sets whether the panel is minimized.
	 * @defaultValue `false`.
	 */
	setMinimized(minimized: boolean, fromServer?: boolean, opts?: Record<string, boolean>): this {
		const o = this._minimized;
		this._minimized = minimized;

		if (o !== minimized || opts?.force) {
			if (this._maximized)
				this.setMaximized(false);

			var node = this.$n();
			if (node) {
				var s = node.style;
				if (minimized) {
					zWatch.fireDown('onHide', this);
					jq(node).hide();
				} else {
					jq(node).show();
					zUtl.fireShown(this);
				}
				if (!fromServer) {
					this._visible = false;
					this.fire('onMinimize', {
						left: s.left,
						top: s.top,
						width: s.width,
						height: s.height,
						minimized: minimized
					});
				}
			}
		}

		return this;
	}

	/**
	 * @returns whether the panel is minimized.
	 * @defaultValue `false`.
	 */
	isMinimized(): boolean {
		return !!this._minimized;
	}

	//server use only
	getTbar(): string | undefined {
		return this._tbar;
	}

	//server use only
	setTbar(tbar: string, opts?: Record<string, boolean>): this {
		const o = this._tbar;
		this._tbar = tbar;

		if (o !== tbar || opts?.force) {
			this.tbar = zk.Widget.$<zul.wgt.Toolbar>(tbar);
			if (this.bbar == this.tbar)
				this.bbar = undefined;
			if (this.fbar == this.tbar)
				this.fbar = undefined;
			this.rerender();
		}

		return this;
	}

	//server use only
	getBbar(): string | undefined {
		return this._bbar;
	}

	//server use only
	setBbar(bbar: string, opts?: Record<string, boolean>): this {
		const o = this._bbar;
		this._bbar = bbar;

		if (o !== bbar || opts?.force) {
			this.bbar = zk.Widget.$<zul.wgt.Toolbar>(bbar);
			if (this.tbar == this.bbar)
				this.tbar = undefined;
			if (this.fbar == this.bbar)
				this.fbar = undefined;
			this.rerender();
		}

		return this;
	}

	//server use only
	getFbar(): string | undefined {
		return this._fbar;
	}

	//server use only
	setFbar(fbar: string, opts?: Record<string, boolean>): this {
		const o = this._fbar;
		this._fbar = fbar;

		if (o !== fbar || opts?.force) {
			this.fbar = zk.Widget.$<zul.wgt.Toolbar>(fbar);
			if (this.tbar == this.fbar)
				this.tbar = undefined;
			if (this.bbar == this.fbar)
				this.bbar = undefined;
			this.rerender();
		}

		return this;
	}

	override setVflex(vflex: boolean | string | undefined): this {
		super.setVflex(vflex);
		if (this.desktop) {
			if (this.panelchildren) {
				this.panelchildren.setVflex(vflex);
			}
		}
		return this;
	}

	override setHflex(hflex: boolean | string | undefined): this {
		super.setHflex(hflex);
		if (this.desktop) {
			if (this.panelchildren) {
				this.panelchildren.setHflex(hflex);
			}
		}
		return this;
	}

	override setVisible(visible: boolean): this {
		if (this._visible != visible) {
			if (this._maximized) {
				this.setMaximized(false);
			} else if (this._minimized) {
				this.setMinimized(false);
			}
			super.setVisible(visible);
		}
		return this;
	}

	override setHeight(height?: string): this {
		super.setHeight(height);
		if (this.desktop)
			zUtl.fireSized(this);
		return this;
	}

	override setWidth(width?: string): this {
		super.setWidth(width);
		if (this.desktop)
			zUtl.fireSized(this);
		return this;
	}

	override setTop(top: string): this {
		this._hideShadow();
		super.setTop(top);
		this.zsync();
		return this;
	}

	override setLeft(left: string): this {
		this._hideShadow();
		super.setLeft(left);
		this.zsync();
		return this;
	}

	/** @internal */
	override updateDomStyle_(): void {
		super.updateDomStyle_();
		if (this.desktop)
			zUtl.fireSized(this);
	}

	/**
	 * Adds the toolbar of the panel by these names, "tbar", "bbar", and "fbar".
	 * "tbar" is the name of top toolbar, and "bbar" the name of bottom toolbar,
	 * and "fbar" the name of foot toolbar.
	 *
	 * @param name - "tbar", "bbar", and "fbar".
	 */
	addToolbar(name: string, toolbar: zul.wgt.Toolbar): boolean {
		switch (name) {
			case 'tbar':
				this.tbar = toolbar;
				break;
			case 'bbar':
				this.bbar = toolbar;
				break;
			case 'fbar':
				this.fbar = toolbar;
				break;
			default: return false; // not match
		}
		return this.appendChild(toolbar);
	}

	//event handler//
	onClose(): void {
		if (!this.inServer || !this.isListen('onClose', {asapOnly: true})) //let server handle if in server
			this.parent!.removeChild(this); //default: remove
	}

	onMove(evt: zk.Event & zul.wnd.Dimension): void {
		this._left = evt.left;
		this._top = evt.top;
	}

	onMaximize(evt: zk.Event<zul.wnd.Dimension>): void {
		var data = evt.data!;
		this._top = data.top;
		this._left = data.left;
		this._height = data.height;
		this._width = data.width;
	}

	onSizeEvent(evt: zk.Event<zul.wnd.Dimension>): void {
		var data = evt.data!,
			node = this.$n_(),
			s = node.style;

		this._hideShadow();
		if (data.width != s.width) {
			this._width = s.width = data.width;
		}
		if (data.height != s.height) {
			this._height = s.height = data.height;
			this._fixHgh(true);
		}

		if (data.left != s.left || data.top != s.top) {
			s.left = data.left;
			s.top = data.top;

			this.fire('onMove', zk.copy({
				left: node.style.left,
				top: node.style.top
			}, evt.data), {ignorable: true});
		}

		this.zsync();
		setTimeout(() => zUtl.fireSized(this));
	}

	// eslint-disable-next-line zk/javaStyleSetterSignature
	/** @internal */
	override setFlexSizeH_(flexSizeH: HTMLElement, zkn: zk.JQZK, height: number, isFlexMin?: boolean): void {
		if (isFlexMin) {
			height += this._titleHeight();
		}
		super.setFlexSizeH_(flexSizeH, zkn, height, isFlexMin);
	}

	// eslint-disable-next-line zk/javaStyleSetterSignature
	/** @internal */
	override setFlexSizeW_(flexSizeW: HTMLElement, zkn: zk.JQZK, width: number, isFlexMin?: boolean): void {
		if (isFlexMin && this.caption) {
			if (width == this.caption.$n_().offsetWidth) {
				width += zk(this.$n('head')).padBorderWidth();
			}
		}
		super.setFlexSizeW_(flexSizeW, zkn, width, isFlexMin);
	}

	beforeSize(): void {
		// Bug ZK-334: Tablelayout with hflex won't resize its width after resizing
		// have to clear width here if not listen to flex
		if (!this._flexListened)
			this.$n_('body').style.width = '';
	}

	/** @internal */
	override resetSize_(orient: zk.FlexOrient): void {
		// Bug ZK-334: Tablelayout with hflex won't resize its width after resizing
		// also reset the size of body
		super.resetSize_(orient);
		this.$n_('body').style[orient == 'w' ? 'width' : 'height'] = '';
	}

	//watch//
	override onSize(ctl: zk.ZWatchController): void {
		function syncMaximized(wgt: zul.wnd.Panel): void {
			if (!wgt._lastSize) return;
			var node = wgt.$n_(),
				$n = zk(node),
				floated = wgt.isFloatable(),
				$op = floated ? jq(node).offsetParent() : jq(node).parent(),
				zkop = $op.zk,
				s = node.style,
				sw = zkop.clientWidthDoubleValue();
			if (!floated) {
				sw -= zkop.paddingWidth();
				sw = $n.revisedWidth(sw);
			}
			s.width = jq.px0(sw);
			if (wgt._open) {
				var sh = zkop.clientHeightDoubleValue();
				if (!floated) {
					sh -= zkop.paddingHeight();
					sh = $n.revisedHeight(sh);
				}
				s.height = jq.px0(sh);
			}
		}

		this._hideShadow();
		if (this._maximized)
			syncMaximized(this);

		if (this.tbar)
			ctl.fireDown(this.tbar);
		if (this.bbar)
			ctl.fireDown(this.bbar);
		if (this.fbar)
			ctl.fireDown(this.fbar);
		this._fixHgh(true);
		this._fixWdh(); // B55-ZK-328
		this.zsync();
	}

	onHide(): void {
		this._hideShadow();
	}

	/** @internal */
	_fixHgh(ignoreRealVisible?: boolean): void { // TODO: should be handled by Panelchildren onSize already
		const pc = this.panelchildren;
		if (!pc || pc.z_rod || (!ignoreRealVisible && !this.isRealVisible())) return;
		var n = this.$n_(),
			body = pc.$n_(),
			hgh: string | number = n.style.height;
		if (!hgh && this._cssflex && this._vflex) // due to css flex, need to use offsetHeight
			hgh = n.offsetHeight;
		if (hgh && hgh != 'auto')
			body.style.height = jq.px0(this._offsetHeight(n));
	}

	/** @internal */
	_fixWdh(): void { // TODO: should be handled by Panelchildren onSize already
		var pc = this.panelchildren;
		if (!pc || pc.z_rod || !this.isRealVisible())
			return;
		var pcst = pc.$n()?.style,
			pcwd: string;
		if (pcst && (pcwd = pcst.width) && pcwd != 'auto') {
			var w = zk(this.$n()).contentWidth();
			pcst.width = `${w - zk(this.$n('body')).padBorderWidth()}px`;
		}
	}

	//whether rounded border is required
	/** @internal */
	_rounded(): boolean {
		return this._border.startsWith('rounded'); //rounded
	}

	//backward compatible with 5.0.6
	isFramable(): boolean {
		return this._rounded();
	}

	//whether inner border is required
	/** @internal */
	_bordered(): boolean {
		const v = this._border;
		return v != 'none' && v != 'rounded';
	}

	/** @internal */
	_offsetHeight(n: HTMLElement): number {
		var tHeight = this._titleHeight(),
			body = this.$n('body'),
			h = zk(tHeight ? n : body).contentHeight() - this._titleHeight();
		if (tHeight)
			h -= zk(body).padBorderHeight();

		var tb = this.tbar ? this.$n('tb') : undefined,
			bb = this.bbar ? this.$n('bb') : undefined,
			fb = this.fbar ? this.$n('fb') : undefined;
		if (tb) h -= tb.offsetHeight;
		if (bb) h -= bb.offsetHeight;
		if (fb) h -= fb.offsetHeight;
		return h;
	}

	/** @internal */
	_titleHeight(): number {
		var head = this.getTitle() || this.caption ? this.$n('head') : undefined;
		return head ? head.offsetHeight : 0;
	}

	onFloatUp(ctl: zk.ZWatchController): void {
		if (!this._visible || !this.isFloatable())
			return; //just in case

		for (var wgt: zk.Widget | undefined = ctl.origin as zk.Widget | undefined; wgt; wgt = wgt.parent) {
			if (wgt == this) {
				this.setTopmost();
				return;
			}
			if (wgt.isFloating_())
				return;
		}
	}

	/** @internal */
	_makeSizer(): void {
		if (!this._sizer) {
			this.domListen_(this.$n_(), 'onMouseMove');
			this.domListen_(this.$n_(), 'onMouseOut');
			this._sizer = new zk.Draggable(this, undefined, {
				stackup: true,
				draw: Panel._drawsizing,
				snap: Panel._snapsizing,
				starteffect: Panel._startsizing,
				ghosting: Panel._ghostsizing,
				endghosting: Panel._endghostsizing,
				ignoredrag: Panel._ignoresizing,
				endeffect: Panel._aftersizing});
		}
	}

	/** @internal */
	_initFloat(): void {
		var n = this.$n_();
		if (!n.style.top || !n.style.left) {
			var xy = zk(n).revisedOffset();
			n.style.left = jq.px(xy[0]);
			n.style.top = jq.px(xy[1]);
		}

		n.style.position = 'absolute';
		if (this.isMovable())
			this._initMove();

		this.zsync();

		if (this.isRealVisible())
			this.setTopmost();
	}

	/** @internal */
	_initMove(): void {
		var handle = this.$n('head');
		if (handle && !this._drag) {
			jq(handle).addClass(this.$s('header-move'));
			this._drag = new zk.Draggable(this, undefined, {
				handle: handle, stackup: true,
				starteffect: Panel._startmove,
				ignoredrag: Panel._ignoremove,
				endeffect: Panel._aftermove});
		}
	}

	override zsync(opts?: zk.Object): void {
		super.zsync(opts);

		if (!this.isFloatable()) {
			if (this._shadow) {
				this._shadow.destroy();
				this._shadow = undefined;
			}
		} else {
			var body = this.$n('body');
			if (body && zk(body).isRealVisible()) {
				if (!this._shadow)
					this._shadow = new zk.eff.Shadow(this.$n_(), {
						left: -4, right: 4, top: -2, bottom: 3
					});

				if (this._maximized || this._minimized || !this._visible) //since action might be applied, we have to check _visible
					this._hideShadow();
				else this._shadow.sync();
			}
		}
	}

	/** @internal */
	_hideShadow(): void {
		var shadow = this._shadow;
		if (shadow) shadow.hide();
	}

	/** @internal */
	override afterAnima_(visible: boolean): void {
		super.afterAnima_(visible);
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
	}

	
	/** @internal */
	override bind_(desktop: zk.Desktop | undefined, skipper: zk.Skipper | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		zWatch.listen({onSize: this, onHide: this});

		if (this._sizable)
			this._makeSizer();

		if (this.isFloatable()) {
			zWatch.listen({onFloatUp: this});
			this.setFloating_(true);
			this._initFloat();
			if (!zk.css3)
				jq.onzsync(this); //sync shadow if it is implemented with div
		}

		if (this._maximizable && this._maximized) {
			after.push(() => {
				this._maximized = false;
				this.setMaximized(true, true);
			});
		}
	}

	/** @internal */
	override unbind_(skipper?: zk.Skipper, after?: CallableFunction[], keepRod?: boolean): void {
		if (this._inWholeMode) {
			var node = this.$n(),
				oldinfo: NodeInfo | undefined;

			// ZK-1951, ZK-2045: Page becomes blank after detaching a modal window having an iframe loaded with PDF in IE > 9
			// A workaround is to hide the iframe before remove
			if (zk.ie11) {
				var $jq = jq(this.$n_()).find('iframe');
				if ($jq.length)
					$jq.hide().remove();
			}

			zk(node).undoVParent(); //no need to fire onVParent in unbind_

			var p = this.parent?.parent?.$n();
			if (p && (oldinfo = this._oldNodeInfo)) {
				p.style.position = oldinfo._ppos!;
				(p.parentNode as HTMLElement).scrollTop = oldinfo._scrollTop;
			}
			this._inWholeMode = false;
		}
		zWatch.unlisten({onSize: this, onHide: this, onFloatUp: this});
		this.setFloating_(false);

		if (!zk.css3) jq.unzsync(this);

		if (this._shadow) {
			this._shadow.destroy();
			this._shadow = undefined;
		}
		if (this._drag) {
			this._drag.destroy();
			this._drag = undefined;
		}
		// Bug ZK-1467: Resizable panels inside portallayout loses resizability after move
		if (this._sizer) {
			this._sizer.destroy();
			this._sizer = undefined;
		}
		this.domUnlisten_(this.$n_(), 'onMouseMove');
		this.domUnlisten_(this.$n_(), 'onMouseOut');
		super.unbind_(skipper, after, keepRod);
	}

	/** @internal */
	_doMouseMove(evt: zk.Event): void {
		if (this._sizer && zUtl.isAncestor(this, evt.target)) {
			var n = this.$n_(),
				c = Panel._insizer(n, zk(n).revisedOffset(), evt.pageX, evt.pageY),
				handle = this.isMovable() ? this.$n('head') : false;
			if (!this._maximized && this._open && c) {
				if (this._backupCursor == undefined)
					this._backupCursor = n.style.cursor;
				n.style.cursor = c == 1 ? 'n-resize' : c == 2 ? 'ne-resize' :
					c == 3 ? 'e-resize' : c == 4 ? 'se-resize' :
					c == 5 ? 's-resize' : c == 6 ? 'sw-resize' :
					c == 7 ? 'w-resize' : 'nw-resize';
				if (handle) jq(handle).removeClass(this.$s('header-move'));
			} else {
				n.style.cursor = this._backupCursor ?? '';
				if (handle) jq(handle).addClass(this.$s('header-move'));
			}
		}
	}

	/** @internal */
	_doMouseOut(evt: zk.Event): void {
		this.$n_().style.cursor = this._backupCursor || '';
	}

	/** @internal */
	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var maxBtn = this.$n('max'),
			minBtn = this.$n('min'),
			// eslint-disable-next-line zk/noNull
			n: HTMLElement | null = evt.domTarget;
		if (!n.id)
			// eslint-disable-next-line zk/noNull
			n = n.parentNode as HTMLElement | null;
		switch (n) {
		case this.$n('close'):
			this.fire('onClose');
			break;
		case maxBtn:
			this.setMaximized(!this._maximized);
			break;
		case minBtn:
			this.setMinimized(!this._minimized);
			break;
		case this.$n('exp'):
			var body = this.$n('body'),
				open = body ? zk(body).isVisible() : this._open;

			// force to open
			if (!open == this._open)
				this._open = open;
			this.setOpen(!open);
			break;
		default:
			super.doClick_(evt, popupOnly);
			return;
		}
		evt.stop();
	}

	/** @internal */
	override domClass_(no?: zk.DomClassOptions): string {
		var scls = super.domClass_(no);
		if (!no || !no.zclass) {
			var added = this._bordered() ? '' : this.$s('noborder');
			if (added) scls += (scls ? ' ' : '') + added;
			added = this._open ? '' : this.$s('collapsed');
			if (added) scls += (scls ? ' ' : '') + added;

			if (!(this.getTitle() || this.caption))
				scls += ' ' + this.$s('noheader');

			if (!this._rounded())
				scls += ' ' + this.$s('noframe');
		}
		return scls;
	}

	/** @internal */
	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (child instanceof zul.wgt.Caption)
			this.caption = child;
		else if (child instanceof zul.wnd.Panelchildren) {
			this.panelchildren = child;
			this.panelchildren.setVflex(this.getVflex());
			this.panelchildren.setHflex(this.getHflex());
		} else if (child instanceof zul.wgt.Toolbar) {
			if (this.firstChild == child || (this.nChildren == (this.caption ? 2 : 1)))
				this.tbar = child;
			else if (this.lastChild == child && child.previousSibling instanceof zul.wgt.Toolbar)
				this.fbar = child;
			else if (child.previousSibling instanceof zul.wnd.Panelchildren)
				this.bbar = child;
		}
		this.rerender();
	}

	/** @internal */
	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (child == this.caption)
			this.caption = undefined;
		else if (child == this.panelchildren)
			this.panelchildren = undefined;
		else if (child == this.tbar)
			this.tbar = undefined;
		else if (child == this.bbar)
			this.bbar = undefined;
		else if (child == this.fbar)
			this.fbar = undefined;
		if (!this.childReplacing_)
			this.rerender();
	}

	/** @internal */
	override onChildVisible_(child: zk.Widget): void {
		super.onChildVisible_(child);
		if ((child == this.tbar || child == this.bbar || child == this.fbar) && this.$n())
			this._fixHgh();
	}

	//@Override, Bug ZK-1524: caption children should not considered.
	/** @internal */
	override getChildMinSize_(attr: zk.FlexOrient, wgt: zk.Widget): number {
		var including = true;
		if (wgt == this.caption) {
			if (attr == 'w') {
				including = !!(wgt.$n_().style.width);
			} else {
				including = !!(wgt.$n_().style.height);
			}
		}
		if (including) {
			return super.getChildMinSize_(attr, wgt);
		} else {
			return 0;
		}
	}

	/** @internal */
	override isExcludedHflex_(): boolean {
		if (zk.isLoaded('zkmax.layout') && this.parent instanceof zkmax.layout.Portalchildren) {
			var p = this.parent;
			if (p.parent)
				return p.parent.isVertical();
		}
		return false;
	}

	/** @internal */
	override isExcludedVflex_(): boolean {
		if (zk.isLoaded('zkmax.layout') && this.parent instanceof zkmax.layout.Portalchildren) {
			var p = this.parent;
			if (p.parent)
				return !(p.parent.isVertical());
		}
		return false;
	}

	/** @internal */
	getCollapseOpenIconClass_(): string {
		return 'z-icon-angle-up';
	}

	/** @internal */
	getCollapseCloseIconClass_(): string {
		return 'z-icon-angle-down';
	}

	/** @internal */
	getClosableIconClass_(): string {
		return 'z-icon-times';
	}

	/** @internal */
	getMaximizableIconClass_(): string {
		return 'z-icon-expand';
	}

	/** @internal */
	getMaximizedIconClass_(): string {
		return 'z-icon-compress';
	}

	/** @internal */
	getMinimizableIconClass_(): string {
		return 'z-icon-minus';
	}

	//drag
	/** @internal */
	static _startmove(dg: zk.Draggable): void {
		(dg.control as zul.wnd.Panel)._hideShadow();
		//Bug #1568393: we have to change the percetage to the pixel.
		var el = dg.node!;
		if (el.style.top && el.style.top.includes('%'))
				el.style.top = el.offsetTop + 'px';
		if (el.style.left && el.style.left.includes('%'))
				el.style.left = el.offsetLeft + 'px';
		//zkau.closeFloats(cmp, handle);
	}

	/** @internal */
	static _ignoremove(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): boolean {
		var wgt = dg.control as zk.Widget,
			tar = evt.domTarget;
		if (!tar.id)
			tar = tar.parentNode as HTMLElement;

		switch (tar) {
		case wgt.$n('close'):
		case wgt.$n('max'):
		case wgt.$n('min'):
		case wgt.$n('exp'):
			return true; //ignore special buttons
		}
		return false;
	}

	/** @internal */
	static _aftermove(dg: zk.Draggable, evt?: zk.Event): void {
		var wgt = (dg.control as zk.Widget);
		wgt.zsync();
		zk(wgt).redoCSS(-1, {'fixFontIcon': true});
	}

	// drag sizing
	/** @internal */
	static _startsizing = zul.wnd.Window._startsizing;
	/** @internal */
	static _ghostsizing = zul.wnd.Window._ghostsizing;
	/** @internal */
	static _endghostsizing = zul.wnd.Window._endghostsizing;
	/** @internal */
	static _insizer = zul.wnd.Window._insizer;

	/** @internal */
	static _ignoresizing(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): boolean {
		var el = dg.node!,
			wgt = dg.control as zul.wnd.Panel;

		if (wgt._maximized || !wgt._open) return true;

		var offs = zk(el).revisedOffset(),
			v = Panel._insizer(el, offs, pointer[0], pointer[1]);
		if (v) {
			wgt._hideShadow();
			dg.z_dir = v;
			dg.z_box = {
				top: offs[1], left: offs[0], height: el.offsetHeight,
				width: el.offsetWidth, minHeight: zk.parseInt(wgt.getMinheight()),
				minWidth: zk.parseInt(wgt.getMinwidth())
			};
			dg.z_orgzi = el.style.zIndex;
			return false;
		}
		return true;
	}

	/** @internal */
	static _snapsizing = zul.wnd.Window._snapsizing;
	/** @internal */
	static _aftersizing = zul.wnd.Window._aftersizing;
	/** @internal */
	static _drawsizing = zul.wnd.Window._drawsizing;
}

@zk.WrapClass('zul.wnd.PanelSkipper')
export class PanelSkipper extends zk.Skipper {
	/** @internal */
	_p: zul.wnd.Panel;

	constructor(p: zul.wnd.Panel) {
		super();
		this._p = p;
	}

	override skip(wgt: zk.Widget, skipId?: string): HTMLElement | undefined {
		const skip = jq(skipId || (wgt.uuid + '-body'), zk)[0];
		if (skip) {
			skip.parentNode!.removeChild(skip);
				//don't use jq to remove, since it unlisten events
			return skip;
		}
	}

	override restore(wgt: zk.Widget, skip: HTMLElement | undefined): void {
		super.restore(wgt, skip);
		this._p.zsync();
	}
}

/** @class zul.wnd.PanelRenderer
 * The renderer used to render a panel.
 * It is designed to be overriden
 * @since 5.0.5
 */
export var PanelRenderer = {
	/**
	 * Check the panel whether to render the rounded frame.
	 *
	 * @param wgt - the window
	 */
	isFrameRequired(wgt: zul.wnd.Panel): boolean {
		return true;
	}
};
zul.wnd.PanelRenderer = PanelRenderer;