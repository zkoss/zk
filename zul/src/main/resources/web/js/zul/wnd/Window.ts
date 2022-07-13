/* Window.ts

	Purpose:

	Description:

	History:
		Mon Nov 17 17:52:31     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
/** The window related widgets, such as window and panel.
 */
//zk.$package('zul.wnd');

export type WindowMode = 'embedded' | 'overlapped' | 'popup' | 'modal' | 'highlighted';

var _modals: zul.wnd.Window[] = [],
	_lastfocus: zk.Widget | null;

function _syncMaximized(wgt: zul.wnd.Window): void {
	if (!wgt._lastSize) return;
	var node = wgt.$n_(),
		floated = wgt._mode != 'embedded',
		$op = floated ? jq(node).offsetParent() : jq(node).parent(),
		zkop = $op.zk,
		s = node.style;

	s.width = jq.px0(zkop.clientWidthDoubleValue() - zkop.paddingWidth());
	s.height = jq.px0(zkop.clientHeightDoubleValue() - zkop.paddingHeight());
}

//drag move
function _startmove(dg: zk.Draggable): void {
	//Bug #1568393: we have to change the percetage to the pixel.
	var el = dg.node!;
	if (el.style.top && el.style.top.indexOf('%') >= 0)
			el.style.top = el.offsetTop + 'px';
	if (el.style.left && el.style.left.indexOf('%') >= 0)
			el.style.left = el.offsetLeft + 'px';

	//ZK-1309: Add a flag to identify is dragging or not in onFloatUp()
	//ZK-1662: refix ZK-1309
	//dg.control._isDragging = true;
	zWatch.fire('onFloatUp', dg.control); //notify all
}
function _ghostmove(dg: zk.Draggable, ofs: zk.Offset, evt: zk.Event): HTMLElement {
	var wnd = dg.control as zul.wnd.Window,
		el = dg.node!,
		$el = jq(el),
		$top = $el.find('>div:first'),
		top = $top[0],
		$fakeT = jq(top).clone(),
		fakeT = $fakeT[0],
		zcls = wnd.getZclass();
	_hideShadow(wnd);

	jq(document.body).prepend(
		'<div id="zk_wndghost" class="' + zcls + '-move-ghost" style="position:absolute;'
		+ 'top:' + ofs[1] + 'px; left:' + ofs[0] + 'px;'
		+ 'width:' + ($el.width()! + zk(el).padBorderWidth()) + 'px;'
		+ 'height:' + ($el.height()! + zk(el).padBorderHeight()) + 'px;'
		+ 'z-index:' + el.style.zIndex + '"><dl></dl></div>');
	dg._wndoffs = ofs;
	el.style.visibility = 'hidden';
	var h = el.offsetHeight - wnd._titleHeight();
	el = jq('#zk_wndghost')[0];

	var f = el.firstChild as HTMLElement;
	f.style.height = jq.px0(zk(f).revisedHeight(h));

	el.insertBefore(fakeT, el.lastChild);
	return el;
}

function _endghostmove(dg: zk.Draggable, origin: HTMLElement): void {
	var el = dg.node!; //ghost
	origin.style.top = jq.px(origin.offsetTop + el.offsetTop - dg._wndoffs![1]);
	origin.style.left = jq.px(origin.offsetLeft + el.offsetLeft - dg._wndoffs![0]);

	document.body.style.cursor = '';
	zWatch.fire('onMove'); //Bug ZK-1372: hide applet when overlapped
}
function _ignoremove(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): boolean {
	var el = dg.node!,
		wgt = dg.control as zul.wnd.Window,
		tar = evt.domTarget!;

	if (!tar.id)
		tar = tar.parentNode as HTMLElement;
	switch (tar) {
	case wgt.$n('close'):
	case wgt.$n('max'):
	case wgt.$n('min'):
		return true; //ignore special buttons
	}
	const wtar = zk.Widget.$(tar);
	if (wgt != wtar && wgt.caption != wtar)
		return true; //ignore child widget of caption, Bug B50-3166874
	if (!wgt.isSizable()
	|| (el.offsetTop + 4 < pointer[1] && el.offsetLeft + 4 < pointer[0]
	&& el.offsetLeft + el.offsetWidth - 4 > pointer[0]))
		return false; //accept if not sizable or not on border
	return true;
}
function _aftermove(dg: zk.Draggable, evt: zk.Event<zul.wnd.Dimension>): void {
	dg.node!.style.visibility = '';
	var wgt = dg.control as zul.wnd.Window;

	//ZK-1309: Add a flag to identify is dragging or not in onFloatUp()
	//ZK-1662: refix ZK-1309
	//delete wgt._isDragging;

	// Bug for ZK-385 clear position value after move
	// ZK-4007: shouldn't clear position if nocenter
	if (wgt._position && wgt._position != 'parent' && wgt._position != 'nocenter') {
		wgt._position = null;
	}
	wgt.zsync();
	wgt._fireOnMove(evt.data!);
	zk(wgt).redoCSS(-1, {'fixFontIcon': true});
}

function _doOverlapped(wgt: zul.wnd.Window): void {
	var pos = wgt._position,
		n = wgt.$n_(),
		$n = zk(n);
	if (!pos && (!n.style.top || !n.style.left)) {
		var xy = $n.revisedOffset();
		//ZK-1391: use revisedOffset() only if style doesn't specify left/top value
		if (!n.style.left) {
			n.style.left = jq.px(xy[0]);
		}
		if (!n.style.top) {
			n.style.top = jq.px(xy[1]);
		}
	} else if (pos == 'parent')
		_posByParent(wgt);

	$n.makeVParent();
	// B70-ZK-2067: Should pass widget as parameter not DOM element.
	zWatch.fireDown('onVParent', wgt);

	wgt.zsync();
	_updDomPos(wgt);
	wgt.setTopmost();
	_makeFloat(wgt);
}
function _doModal(wgt: zul.wnd.Window): void {
	var pos = wgt._position,
		n = wgt.$n(),
		$n = zk(n);
	if (pos == 'parent') _posByParent(wgt);

	$n.makeVParent();
	// B70-ZK-2067: Should pass widget as parameter not DOM element.
	zWatch.fireDown('onVParent', wgt);

	wgt.zsync();
	_updDomPos(wgt, true, false, true);

	//Note: modal must be visible
	var realVisible = wgt.isRealVisible();
	wgt.setTopmost();

	if (!wgt._mask) {
		var anchor = wgt._shadowWgt ? wgt._shadowWgt.getBottomElement() : null;
		wgt._mask = new zk.eff.FullMask({
			id: wgt.uuid + '-mask',
			anchor: anchor ? anchor : wgt.$n(),
			//bug 1510218: we have to make it as a sibling
			zIndex: wgt._zIndex as number,
			visible: realVisible
		});
		var tag = zk.ie < 11 ? 'a' : 'button';
		jq('#' + wgt.uuid + '-mask').append('<' + tag + ' id="' + wgt.uuid + '-mask-a" style="top:0;left:0" onclick="return false;" href="javascript:;" class="z-focus-a" aria-hidden="true" tabindex="-1"></' + tag + '>');
		wgt._anchor = jq('#' + wgt.uuid + '-mask-a')[0];
	}
	if (realVisible)
		_markModal(wgt);

	_makeFloat(wgt);
}
function _markModal(wgt: zul.wnd.Window): void {
	zk.currentModal = wgt;
	var wnd = _modals[0], fc = zk.currentFocus;
	if (wnd) wnd._lastfocus = fc;
	else _lastfocus = fc;
	_modals.unshift(wgt);

	//We have to use setTimeout:
	//1) au's focus uses wgt.focus(0), i.e.,
	//   focus might have been changed to its decendant (Z30-focus.zul)
	//2) setVisible might use animation
	setTimeout(function () {
		zk.afterAnimate(function () {
			if (!zUtl.isAncestor(wgt, zk.currentFocus!)) {
				if (zk.ie9_)
					wgt.focus(100);
				else
					wgt.focus();
			}
		}, -1);
	});
}
function _unmarkModal(wgt: zul.wnd.Window): void {
	_modals.$remove(wgt);
	if (zk.currentModal == wgt) {
		var wnd = zk.currentModal = _modals[0],
			fc = wnd ? wnd._lastfocus : _lastfocus;
		if (!wnd)
			_lastfocus = null;
		if (!fc || !fc.desktop)
			fc = wnd;
		if (fc) {
			if (wgt._updDOFocus === false)
				wgt._updDOFocus = fc; //let _updDomOuter handle it
			else
				fc.focus(0); // use timeout for the bug 3057311
				// use 0 instead of 10, otherwise it will cause this bug 1936366
		}
	}
	wgt._lastfocus = null;
}
/* Must be called before calling makeVParent. */
function _posByParent(wgt: zul.wnd.Window): void {
	var n = wgt.$n_(),
		ofs = zk(zk(n).vparentNode(true)).revisedOffset();
	wgt._offset = ofs;
	n.style.left = jq.px(ofs[0] + zk.parseInt(wgt._left));
	n.style.top = jq.px(ofs[1] + zk.parseInt(wgt._top));
}
function _updDomOuter(wgt: zul.wnd.Window, opts?: {sendOnMaximize?: boolean}): void {
	// B50-ZK-462
	wgt._notSendMaximize = !opts || !opts.sendOnMaximize;
	wgt._updDOFocus = false; //it might be set by unbind_
	try {
		var last = wgt._lastSize;
		wgt.rerender(wgt._skipper);
		if (last) {
			wgt._lastSize = last;

			// ZK-1826: should restore width and height
			var n = wgt.$n();
			if (n) {
				var s = n.style;

				// ZK-2041: should skip undefined value, or throws exception in ie8
				if (last.h)
					s.height = last.h;
				if (last.w)
					s.width = last.w;
				wgt._fixHgh(false);
			}
		}
		// NOTE: At this point, unbind_ would have set _updDOFocus to a zk.Widget.
		const cf = wgt._updDOFocus as unknown as zk.Widget | null | undefined;
		if (cf) //asked by unbind_
			cf.focus(10);
	} finally {
		delete wgt._updDOFocus;
		delete wgt._notSendMaximize;
	}
}
//minTop - whether to at most 100px
function _updDomPos(wgt: zul.wnd.Window, force?: boolean, posParent?: boolean, minTop?): void {
	if (!wgt.desktop || wgt._mode == 'embedded')
		return;

	var n = wgt.$n_(), pos = wgt._position;
	if (pos == 'parent') {
		if (posParent)
			_posByParent(wgt);
		return;
	}
	if (!pos && !force)
		return;

	var st = n.style;
	st.position = 'absolute'; //just in case
	var ol = st.left, ot = st.top;
	if (pos != 'nocenter')
		zk(n).center(pos);
	var sdw = wgt._shadowWgt;
	if (pos && sdw) {
		var opts = sdw.opts as Required<zk.eff.EffectStackupOptions>,
			l = n.offsetLeft,
			t = n.offsetTop;
		if (pos.indexOf('left') >= 0 && opts.left < 0)
			st.left = jq.px(l - opts.left);
		else if (pos.indexOf('right') >= 0 && opts.right > 0)
			st.left = jq.px(l - opts.right);
		if (pos.indexOf('top') >= 0 && opts.top < 0)
			st.top = jq.px(t - opts.top);
		else if (pos.indexOf('bottom') >= 0 && opts.bottom > 0)
			st.top = jq.px(t - opts.bottom);
	}

	if (minTop && !pos) { //adjust y (to upper location)
		var top = zk.parseInt(n.style.top), y = jq.innerY();
		if (y) {
			var y1 = top - y;
			if (y1 > 100) n.style.top = jq.px0(top - (y1 - 100));
		} else if (top > 100)
			n.style.top = '100px';
	}

	wgt.zsync();
	if (ol != st.left || ot != st.top)
		wgt._fireOnMove();
}

function _hideShadow(wgt: zul.wnd.Window): void {
	var shadow = wgt._shadowWgt;
	if (shadow) shadow.hide();
}
function _makeSizer(wgt: zul.wnd.Window): void {
	if (!wgt._sizer) {
		wgt.domListen_(wgt.$n_(), 'onMouseMove');
		wgt.domListen_(wgt.$n_(), 'onMouseOut');
		wgt._sizer = new zk.Draggable(wgt, null, {
			stackup: true,
			overlay: true, // ZK-817
			draw: Window._drawsizing,
			snap: Window._snapsizing,
			initSensitivity: 0,
			starteffect: Window._startsizing,
			ghosting: Window._ghostsizing,
			endghosting: Window._endghostsizing,
			ignoredrag: Window._ignoresizing,
			endeffect: Window._aftersizing});
	}
}
function _makeFloat(wgt: zul.wnd.Window): void {
	var handle = wgt.$n('cap');
	if (handle && !wgt._drag) {
		jq(handle).addClass(wgt.getZclass() + '-header-move');
		wgt._drag = new zk.Draggable(wgt, null, {
			handle: handle, stackup: true,
			fireOnMove: false,
			starteffect: Window._startmove,
			ghosting: Window._ghostmove,
			endghosting: Window._endghostmove,
			ignoredrag: Window._ignoremove,
			endeffect: Window._aftermove,
			zIndex: 99999 //Bug 2929590
		});
	}
}

function _isModal(mode: WindowMode): boolean {
	return mode == 'modal' || mode == 'highlighted';
}

//Bug ZK-1689: get relative position to parent.
function _getPosByParent(wgt: zul.wnd.Window, l: string, t: string): [string, string] {
	var pos = wgt._position,
		left = zk.parseInt(l),
		top = zk.parseInt(t),
		x = 0, y = 0;
	if (pos == 'parent') {
		var vp = zk(wgt.$n()).vparentNode();
		if (vp) {
			var ofs = zk(vp).revisedOffset();
			x = ofs[0];
			y = ofs[1];
		}
	}
	return [jq.px(left - x), jq.px(top - y)];
}

/**
 * A window.
 *
 * <p>Unlike other elements, each {@link Window} is an independent ID space.
 * It means a window and all its descendants forms a ID space and
 * the ID of each of them is unique in this space.
 * You could retrieve any of them in this space by calling {@link #$f}.
 *
 * <p>If a window X is a descendant of another window Y, X's descendants
 * are not visible in Y's space. To retrieve a descendant, say Z, of X,
 * you have to invoke Y.$f('X').$f('Z').
 *
 * <p>Events:<br/>
 * onMove, onOpen, onMaximize, onMinimize, and onClose.<br/>
 * Note: to have better performance, onOpen is sent only if a
 * non-deferrable event listener is registered.
 *
 * <p><code>onClose</code> is sent when the close button is pressed
 * (if {@link #isClosable} is true). The window has to detach or hide
 * the window.
 *
 * <p>On the other hand, <code>onOpen</code> is sent when a popup
 * window (i.e., {@link #getMode} is popup) is closed due to user's activity
 * (such as press ESC). This event is only a notification.
 * In other words, the popup is hidden before the event is sent to the server.
 * The application cannot prevent the window from being hidden.
 *
 * <p>Default {@link #getZclass}: z-window.
 */
@zk.WrapClass('zul.wnd.Window')
export class Window extends zul.ContainerWidget {
	_mode: WindowMode = 'embedded';
	_border = 'none';
	_minheight = 100;
	_minwidth = 200;
	_shadow = true;
	override _tabindex = 0;
	_nativebar = true;
	_title?: string;
	caption?: zul.wgt.Caption | null;
	_skipper: zul.wnd.Skipper;
	_closable?: boolean;
	_sizable?: boolean;
	_sizer?: zk.Draggable | null;
	_maximizable?: boolean;
	_minimizable?: boolean;
	_maximized?: boolean;
	_minimized?: boolean;
	_notSendMaximize?: boolean;
	_lastSize?: null | { l?: string; t?: string; w?: string; h?: string };
	_contentStyle?: string;
	_contentSclass?: string;
	_position?: string | null;
	_shadowWgt?: zk.eff.Shadow | null;
	_mask?: zk.eff.FullMask | null;
	_shallSize?: boolean;
	_anchor?: HTMLElement | null;
	_backupCursor?: string;
	_updDOFocus?: zk.Widget | boolean | null;
	_lastfocus?: zk.Widget | null;
	_offset?: zk.Offset;

	constructor(props: Record<string, unknown>) {
		super(props);
		this._fellows = {};
		this._lastSize = {};
		// NOTE: Prior to TS migration, super is called after _fellows/_lastSize
		// are initialized above, but it seems to not matter, as nowhere does super
		// nor do zkcml overrides use _fellows/_lastSize during initialization.

		this.listen({onMaximize: this, onClose: this, onMove: this, onSize: this.onSizeEvent, onZIndex: this}, -1000);
		this._skipper = new zul.wnd.Skipper(this);
	}

	/** Sets the mode to overlapped, popup, modal, embedded or highlighted.
	 *
	 * @param String name the mode which could be one of
	 * "embedded", "overlapped", "popup", "modal", "highlighted".
	 * Note: it cannot be "modal". Use {@link #doModal} instead.
	 */
	setMode(v: WindowMode, opts?: Record<string, boolean>): this {
		const o = this._mode;
		this._mode = v;

		if (o !== v || (opts && opts.force)) {
			_updDomOuter(this);
		}

		return this;
	}

	getMode(): WindowMode {
		return this._mode;
	}

	/**
	 * Sets the title.
	 * @param String title
	 */
	setTitle(title: string, opts?: Record<string, boolean>): this {
		const o = this._title;
		this._title = title;

		if (o !== title || (opts && opts.force)) {
			if (this.caption)
				this.caption.updateDomContent_(); // B50-ZK-313
			else
				_updDomOuter(this);
		}

		return this;
	}

	/**
	 * Returns the title.
	 * Besides this attribute, you could use {@link zul.wgt.Caption} to define
	 * a more sophisticated caption (aka., title).
	 * <p>If a window has a caption whose label ({@link zul.wgt.Caption#getLabel})
	 * is not empty, then this attribute is ignored.
	 * <p>Default: empty.
	 * @return String
	 */
	getTitle(): string | undefined {
		return this._title;
	}

	/**
	 * Sets the border (either none or normal).
	 * @param String border the border. If null or "0", "none" is assumed.
	 */
	setBorder(border: string, opts?: Record<string, boolean>): this {
		const o = this._border;
		this._border = border;

		if (o !== border || (opts && opts.force)) {
			_updDomOuter(this);
		}

		return this;
	}

	/**
	 * Returns the border.
	 * The border actually controls what the content style class is
	 * is used. In fact, the name of the border (except "normal")
	 * is generate as part of the style class used for the content block.
	 * Refer to {@link #getContentSclass} for more details.
	 *
	 * <p>Default: "none".
	 * @return String
	 */
	getBorder(): string {
		return this._border;
	}

	/**
	 * Sets whether to show a close button on the title bar.
	 * If closable, a button is displayed and the onClose event is sent
	 * if an user clicks the button.
	 *
	 * <p>Default: false.
	 *
	 * <p>Note: the close button won't be displayed if no title or caption at all.
	 * @param boolean closable
	 */
	setClosable(closable: boolean, opts?: Record<string, boolean>): this {
		const o = this._closable;
		this._closable = closable;

		if (o !== closable || (opts && opts.force)) {
			_updDomOuter(this);
		}

		return this;
	}

	/**
	 * Returns whether to show a close button on the title bar.
	 * @return boolean
	 */
	isClosable(): boolean | undefined {
		return this._closable;
	}

	/** Sets whether the window is sizable.
	 * If true, an user can drag the border to change the window width.
	 * <p>Default: false.
	 * @param boolean sizable
	 */
	setSizable(sizable: boolean, opts?: Record<string, boolean>): this {
		const o = this._sizable;
		this._sizable = sizable;

		if (o !== sizable || (opts && opts.force)) {
			if (this.desktop) {
				if (sizable)
					_makeSizer(this);
				else if (this._sizer) {
					this._sizer.destroy();
					this._sizer = null;
				}
			}
		}

		return this;
	}

	/** Returns whether the window is sizable.
	 * @return boolean
	 */
	isSizable(): boolean | undefined {
		return this._sizable;
	}

	/**
	 * Sets whether to display the maximizing button and allow the user to maximize
	 * the window, when a window is maximized, the button will automatically
	 * change to a restore button with the appropriate behavior already built-in
	 * that will restore the window to its previous size.
	 * <p>Default: false.
	 *
	 * <p>Note: the maximize button won't be displayed if no title or caption at all.
	 * @param boolean maximizable
	 */
	setMaximizable(maximizable: boolean, opts?: Record<string, boolean>): this {
		const o = this._maximizable;
		this._maximizable = maximizable;

		if (o !== maximizable || (opts && opts.force)) {
			_updDomOuter(this);
		}

		return this;
	}

	/**
	 * Returns whether to display the maximizing button and allow the user to maximize
	 * the window.
	 * <p>Default: false.
	 * @return boolean
	 */
	isMaximizable(): boolean | undefined {
		return this._maximizable;
	}

	/**
	 * Sets whether to display the minimizing button and allow the user to minimize
	 * the window. Note that this button provides no implementation -- the behavior
	 * of minimizing a window is implementation-specific, so the MinimizeEvent
	 * event must be handled and a custom minimize behavior implemented for this
	 * option to be useful.
	 *
	 * <p>Default: false.
	 * <p>Note: the maximize button won't be displayed if no title or caption at all.
	 * @param boolean minimizable
	 */
	setMinimizable(minimizable: boolean, opts?: Record<string, boolean>): this {
		const o = this._minimizable;
		this._minimizable = minimizable;

		if (o !== minimizable || (opts && opts.force)) {
			_updDomOuter(this);
		}

		return this;
	}

	/**
	 * Returns whether to display the minimizing button and allow the user to minimize
	 * the window.
	 * <p>Default: false.
	 * @return boolean
	 */
	isMinimizable(): boolean | undefined {
		return this._minimizable;
	}

	/**
	 * Sets whether the window is maximized, and then the size of the window will depend
	 * on it to show a appropriate size. In other words, if true, the size of the
	 * window will count on the size of its offset parent node whose position is
	 * absolute (by not {@link #doEmbedded()}) or its parent node. Otherwise, its size
	 * will be original size. Note that the maximized effect will run at client's
	 * sizing phase not initial phase.
	 *
	 * <p>Default: false.
	 * @param boolean maximized
	 */
	setMaximized(maximized: boolean, fromServer?: boolean, opts?: Record<string, boolean>): this {
		const o = this._maximized;
		this._maximized = maximized;

		if (o !== maximized || (opts && opts.force)) {
			var node = this.$n();
			if (node) {
				var isRealVisible = this.isRealVisible();
				if (!isRealVisible && maximized) return this;

				var l: string, t: string, w: string, h: string,
					s = node.style,
					up = this.getMaximizableIconClass_(),
					down = this.getMaximizedIconClass_();
				if (maximized) {
					jq(this.$n_('max')).addClass(this.$s('maximized'))
						.attr('title', msgzul.PANEL_RESTORE)
						.children('.' + up).removeClass(up).addClass(down);

					var floated = this._mode != 'embedded',
						$op = floated ? jq(node).offsetParent() : jq(node).parent(),
						zkop = $op.zk;
					l = s.left;
					t = s.top;
					w = s.width;
					h = s.height;

					// prevent the scroll bar.
					s.top = '-10000px';
					s.left = '-10000px';

					s.width = jq.px0(zkop.clientWidthDoubleValue() - (!floated ? zkop.paddingWidth() : 0));
					s.height = jq.px0(zkop.clientHeightDoubleValue() - (!floated ? zkop.paddingHeight() : 0));
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
						s.left = this._lastSize.l!;
						s.top = this._lastSize.t!;
						s.width = this._lastSize.w!;
						s.height = this._lastSize.h!;
						this._lastSize = null;
					}
					l = s.left;
					t = s.top;
					w = s.width;
					h = s.height;

					var body = this.$n('cave');
					if (body)
						body.style.width = body.style.height = '';
				}
				if (!fromServer || isRealVisible) {
					this._visible = true;
					// B50-ZK-462: Window fire unexpected onMaximize event
					if (!this._notSendMaximize) {
						var p = _getPosByParent(this, l, t); //Bug ZK-1689
						this.fire('onMaximize', {
							left: p[0],
							top: p[1],
							width: w,
							height: h,
							maximized: maximized,
							fromServer: fromServer
						});
					}
				}
				if (isRealVisible)
					zUtl.fireSized(this);
			}
		}

		return this;
	}

	/**
	 * Returns whether the window is maximized.
	 * @return boolean
	 */
	isMaximized(): boolean | undefined {
		return this._maximized;
	}

	/**
	 * Sets whether the window is minimized.
	 * <p>Default: false.
	 * @param boolean minimized
	 */
	setMinimized(minimized: boolean, fromServer?: boolean, opts?: Record<string, boolean>): this {
		const o = this._minimized;
		this._minimized = minimized;

		if (o !== minimized || (opts && opts.force)) {
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
					this.zsync();
					var p = _getPosByParent(this, s.left, s.top); //Bug ZK-1689
					this.fire('onMinimize', {
						left: p[0],
						top: p[1],
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
	 * Returns whether the window is minimized.
	 * <p>Default: false.
	 * @return boolean
	 */
	isMinimized(): boolean | undefined {
		return this._minimized;
	}

	/**
	 * Sets the CSS style for the content block of the window.
	 * <p>Default: null.
	 * @param String contentStyle
	 */
	setContentStyle(contentStyle: string, opts?: Record<string, boolean>): this {
		const o = this._contentStyle;
		this._contentStyle = contentStyle;

		if (o !== contentStyle || (opts && opts.force)) {
			_updDomOuter(this);
		}

		return this;
	}

	/**
	 * Returns the CSS style for the content block of the window.
	 * @return String
	 */
	getContentStyle(): string | undefined {
		return this._contentStyle;
	}

	/**
	 * Sets the style class used for the content block.
	 * @param String contentSclass
	 */
	setContentSclass(contentSclass: string, opts?: Record<string, boolean>): this {
		const o = this._contentSclass;
		this._contentSclass = contentSclass;

		if (o !== contentSclass || (opts && opts.force)) {
			_updDomOuter(this);
		}

		return this;
	}

	/**
	 * Returns the style class used for the content block.
	 * @return String
	 */
	getContentSclass(): string | undefined {
		return this._contentSclass;
	}

	/** Sets how to position the window at the client screen.
	 * It is meaningless if the embedded mode is used.
	 *
	 * @param String pos how to position. It can be null (the default), or
	 * a combination of the following values (by separating with comma).
	 * <dl>
	 * <dt>center</dt>
	 * <dd>Position the window at the center. {@link #setTop} and {@link #setLeft}
	 * are both ignored.</dd>
	 * <dt>left</dt>
	 * <dd>Position the window at the left edge. {@link #setLeft} is ignored.</dd>
	 * <dt>right</dt>
	 * <dd>Position the window at the right edge. {@link #setLeft} is ignored.</dd>
	 * <dt>top</dt>
	 * <dd>Position the window at the top edge. {@link #setTop} is ignored.</dd>
	 * <dt>bottom</dt>
	 * <dd>Position the window at the bottom edge. {@link #setTop} is ignored.</dd>
	 * <dt>parent</dt>
	 * <dd>Position the window relative to its parent.
	 * That is, the left and top ({@link #getTop} and {@link #getLeft})
	 * is an offset to his parent's let-top corner.</dd>
	 * </dl>
	 * <p>For example, "left,center" means to position it at the center of
	 * the left edge.
	 */
	setPosition(pos: string, opts?: Record<string, boolean>): this {
		const o = this._position;
		this._position = pos;

		if (o !== pos || (opts && opts.force)) {
			_updDomPos(this, false, this._visible);
		}

		return this;
	}

	/** Returns how to position the window at the client screen.
	 * It is meaningless if the embedded mode is used.
	 *
	 * <p>Default: null which depends on {@link #getMode}:
	 * If overlapped or popup, {@link #setLeft} and {@link #setTop} are
	 * assumed. If modal or highlighted, it is centered.
	 * @return String
	 */
	getPosition(): string | null | undefined {
		return this._position;
	}

	/**
	 * Sets the minimum height in pixels allowed for this window.
	 * If negative, 100 is assumed.
	 * <p>Default: 100.
	 * <p>Note: Only applies when {@link #isSizable()} = true.
	 * @param int minheight
	 */
	setMinheight(minheight: number): this { //TODO
		this._minheight = minheight;
		return this;
	}

	/**
	 * Returns the minimum height.
	 * <p>Default: 100.
	 * @return int
	 */
	getMinheight(): number { //TODO
		return this._minheight;
	}

	/**
	 * Sets the minimum width in pixels allowed for this window. If negative,
	 * 200 is assumed.
	 * <p>Default: 200.
	 * <p>Note: Only applies when {@link #isSizable()} = true.
	 * @param int minwidth
	 */
	setMinwidth(minwidth: number): this { //TODO
		this._minwidth = minwidth;
		return this;
	}

	/**
	 * Returns the minimum width.
	 * <p>Default: 200.
	 * @return int
	 */
	getMinwidth(): number { //TODO
		return this._minwidth;
	}

	/** Sets whether to show the shadow of an overlapped/popup/modal
	 * window. It is meaningless if it is an embedded window.
	 * <p>Default: true.
	 * @param boolean shadow
	 */
	setShadow(shadow: boolean, opts?: Record<string, boolean>): this {
		const o = this._shadow;
		this._shadow = shadow;

		if (o !== shadow || (opts && opts.force)) {
			if (this._shadow) {
				this.zsync();
			} else if (this._shadowWgt) {
				this._shadowWgt.destroy();
				this._shadowWgt = null;
			}
		}

		return this;
	}

	/** Returns whether to show the shadow of an overlapped/popup/modal
	 * window. It is meaningless if it is an embedded window.
	 * @return boolean
	 */
	isShadow(): boolean | null {
		return this._shadow;
	}

	/** Re-position the window based on the value of {@link #getPosition}.
	 * @since 5.0.3
	 */
	repos(): void {
		_updDomPos(this, false, this._visible);
	}

	/** Makes this window as overlapped with other components.
	 */
	doOverlapped(): void {
		this.setMode('overlapped');
	}

	/** Makes this window as popup, which is overlapped with other component
	 * and auto-hiden when user clicks outside of the window.
	 */
	doPopup(): void {
		this.setMode('popup');
	}

	/** Makes this window as highlited. The visual effect is
	 * the similar to the modal window.
	 */
	doHighlighted(): void {
		this.setMode('highlighted');
	}

	/** Makes this window as a modal dialog.
	 * It will automatically center the window (ignoring {@link #getLeft} and
	 * {@link #getTop}).
	 */
	doModal(): void {
		this.setMode('modal');
	}

	/** Makes this window as embeded with other components (Default).
	 */
	doEmbedded(): void {
		this.setMode('embedded');
	}

	override afterAnima_(visible: boolean): void { //mode="highlighted" action="hide:slideDown"
		super.afterAnima_(visible);
		this.zsync();
	}

	override zsync(opts?: Record<string, unknown>): void {
		super.zsync(opts);
		if (this.desktop) {
			if (this._mode == 'embedded') {
				if (this._shadowWgt) {
					this._shadowWgt.destroy();
					this._shadowWgt = null;
				}
			} else if (this._shadow) {
				if (!this._shadowWgt)
					this._shadowWgt = new zk.eff.Shadow(this.$n_(),
						{left: -4, right: 4, top: -2, bottom: 3});
				if (this._maximized || this._minimized || !this._visible) //since action might be applied, we have to check _visible
					_hideShadow(this);
				else
					this._shadowWgt.sync();
			}
			if (this._mask) { //ZK-1079
				var n = (this._shadowWgt && this._shadowWgt.getBottomElement()) || this.$n(); //null if ff3.5 (no shadow/stackup)
				if (n) this._mask.sync(n);
			}
		}
	}

	//event handler//
	onClose(): void {
		if (!this.inServer) //let server handle if in server
			this.parent!.removeChild(this); //default: remove
	}

	onMove(evt: zk.Event & zul.wnd.Dimension): void {
		this._left = evt.left;
		this._top = evt.top;
		if (this._visible) //notify children onMove
			zWatch.fireDown('onMove', this);
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

		_hideShadow(this);
		if (data.width != s.width) {
			s.width = data.width;

			// ZK-2363
			this._width = s.width;
		}
		if (data.height != s.height) {
			s.height = data.height;
			this._fixHgh();

			// ZK-2363
			this._height = s.height;
		}

		if (data.left != s.left || data.top != s.top) {
			s.left = data.left;
			s.top = data.top;
			this._fireOnMove(evt.keys);
		}

		this.zsync();
		var self = this;
		setTimeout(function () {
			zUtl.fireSized(self);
		});
	}

	onZIndex(evt: zk.Event): void {
		this.zsync();
	}

	//watch//
	onResponse(evt: zk.Event): void {
		this.onZIndex(evt);
	}

	onCommandReady(): void {
		if (this.desktop && this._shallSize) {
			this._shallSize = false;
			for (var w = this.firstChild; w; w = w.nextSibling) {
				if (w._nvflex || w._nhflex) {
					zUtl.fireSized(this);
					break;
				}
			}
		}
	}

	onShow(ctl: zk.ZWatchController): void {
		var w = ctl.origin;
		if (this != w && this._mode != 'embedded'
		&& this.isRealVisible({until: w, dom: true})) {
			zk(this.$n()).cleanVisibility();
			this.zsync();
		}
	}

	onHide(ctl: zk.ZWatchController): void {
		var w = ctl.origin;
		if (this != w && this._mode != 'embedded'
		&& this.isRealVisible({until: w, dom: true})) {
		//Note: dom:true, since isVisible might be wrong when onHide is called.
		//For example, tab sets selected and then fire onHide, and tabpanel's
		//isVisible returns false (since unselected). Thus, it is better to
		//count on DOM only
			this.$n_().style.visibility = 'hidden';
			this.zsync();
		}
	}

	override onSize(): void {
		_hideShadow(this);
		if (this._maximized)
			_syncMaximized(this);
		this._fixHgh(true);
		if (this._mode == 'modal')
			_updDomPos(this, true, false, true); // B70-ZK-2892
		else
			_updDomPos(this);
		this.zsync();
	}

	onFloatUp(ctl: zk.ZWatchController): void {
		/*
			* ZK-1309: If window already has mask, ignore onFloatUp routine.
			* The reason is prevent zindex of window change(in `setTopmost()`) when dragging,
			* it will let full-mask is not visible.
			*/
		if (!this._visible || this._mode == 'embedded' || this._mask)
			return; //just in case

		var wgt: zk.Widget | null = ctl.origin;
		if (this._mode == 'popup') {
			for (let floatFound = false; wgt; wgt = wgt.parent) {
				if (wgt == this) {
					if (!floatFound) this.setTopmost();
					return;
				}
				floatFound = floatFound || wgt.isFloating_();
			}
			this.setVisible(false);
			this.fire('onOpen', {open: false});
		} else
			for (; wgt; wgt = wgt.parent) {
				if (wgt == this) {
					this.setTopmost();
					return;
				}
				if (wgt.isFloating_())
					return;
			}
	}

	_fixHgh(ignoreVisible?: boolean/* speed up */): void {
		if (ignoreVisible || this.isRealVisible()) {
			var n = this.$n_(),
				hgh: number | string = n.style.height,
				cave = this.$n_('cave'),
				cvh = cave.style.height;
			if (!hgh && this._cssflex && this._vflex) // due to css flex, need to use offsetHeight
				hgh = n.offsetHeight;
			if (hgh && hgh != 'auto') {
				cave.style.height = jq.px0(this._offsetHeight(n));
			} else if (cvh && cvh != 'auto') {
				cave.style.height = '';
			}
		}
	}

	_offsetHeight(n: HTMLElement): number {
		return zk(n).offsetHeight() - this._titleHeight() - zk(n).padBorderHeight();
	}

	_titleHeight(): number {
		var cap = this.getTitle() || this.caption ? this.$n('cap') : null;
		return cap ? cap.offsetHeight : 0;
	}

	_fireOnMove(keys?: zul.wnd.Dimension): void {
		var s = this.$n_().style,
			p = _getPosByParent(this, s.left, s.top); //Bug ZK-1689
		this.fire('onMove', zk.copy({
			left: p[0],
			top: p[1]
		}, keys), {ignorable: true});

	}

	//super//
	override setVisible(visible: boolean | undefined): void {
		if (this._visible != visible) {
			if (this._maximized) {
				this.setMaximized(false);
			} else if (this._minimized) {
				this.setMinimized(false);
			}

			var modal = _isModal(this._mode),
				p = this.parent;
			if (visible) {
				_updDomPos(this, modal, true, modal);
				if (modal && (!p || p.isRealVisible())) {
					this.setTopmost();
					_markModal(this);
				}
			} else if (modal)
				_unmarkModal(this);

			super.setVisible(visible);

			if (!visible)
				this.zsync();

			if (this.isFloating_() && p && !p.isRealVisible()) {
				var n = this.$n_();
				if (this._visible && n.style.display == 'none') {
					this.setDomVisible_(n, false, {visibility: true});
					this.setDomVisible_(n, true, {display: true});
				} else if (!this._visible && n.style.display != 'none') {
					this.setDomVisible_(n, false, {display: true});
				}
			}
		}
	}

	override setHeight(height: string | null): void {
		super.setHeight(height);
		if (this.desktop)
			zUtl.fireSized(this);
				// Note: IE6 is broken, because its offsetHeight doesn't update.
	}

	override setWidth(width: string | null): void {
		super.setWidth(width);
		if (this.desktop)
			zUtl.fireSized(this);
	}

	override setTop(top: string): void {
		_hideShadow(this);
		super.setTop(top);
		this.zsync();

	}

	override setLeft(left: string): void {
		_hideShadow(this);
		super.setLeft(left);
		this.zsync();
	}

	override setZIndex(zIndex: number, opts: {floatZIndex?: boolean; fire?: boolean}): void {
		var old = this._zIndex;
		super.setZIndex(zIndex, opts);
		if (old != zIndex)
			this.zsync();
	}

	override setZindex(zIndex: number, opts: {floatZIndex?: boolean; fire?: boolean}): void {
		this.setZIndex(zIndex, opts);
	}

	override focus_(timeout?: number): boolean {
		var cap = this.caption;
		if (!zk.mobile) { //Bug ZK-1314: avoid focus on input widget to show keyboard on ipad
			for (var w = this.firstChild; w; w = w.nextSibling)
				//B65-ZK-1797: avoid focusing on removed widge when enable client ROD
				if (w.desktop && w != cap && w.focus_(timeout))
					return true;
		}
		if (cap && cap.focus_(timeout)) {
			return true;
		} else if (this._anchor) {
			this._anchor.focus();
			return true;
		}
		return false;
	}

	override domClass_(no?: zk.DomClassOptions): string {
		var cls = super.domClass_(no),
			bordercls = this._border;

		bordercls = 'normal' == bordercls ? '' :
			'none' == bordercls ? 'noborder' : bordercls;

		if (bordercls)
			cls += ' ' + this.$s(bordercls);

		if (!(this.getTitle() || this.caption))
			cls += ' ' + this.$s('noheader');

		cls += ' ' + this.$s(this._mode);
		return cls;
	}

	override onChildVisible_(child: zk.Widget): void {
		super.onChildVisible_(child);
		if (this.desktop) {
			this._shallSize = true;
		}
	}

	override onChildAdded_(child: zk.Widget): void {
		super.onChildAdded_(child);
		if (child instanceof zul.wgt.Caption) {
			this.caption = child;
			this.rerender(this._skipper); // B50-ZK-275
		}
	}

	override onChildRemoved_(child: zk.Widget): void {
		super.onChildRemoved_(child);
		if (child == this.caption) {
			this.caption = null;
			this.rerender(this._skipper); // B50-ZK-275
		}
		if (this.desktop) {
			this._shallSize = true;
		}
	}

	override insertChildHTML_(child: zk.Widget, before?: zk.Widget | null, desktop?: zk.Desktop | null): void {
		if (!(child instanceof zul.wgt.Caption)) // B50-ZK-275
			super.insertChildHTML_(child, before, desktop);
	}

	override domStyle_(no?: zk.DomStyleOptions): string {
		var style = super.domStyle_(no);
		if ((!no || !no.visible) && this._minimized)
			style = 'display:none;' + style;
		if (this._mode != 'embedded')
			style = 'position:absolute;' + style;
		return style;
	}

	override bind_(desktop: zk.Desktop | null | undefined, skipper: zk.Skipper | null | undefined, after: CallableFunction[]): void {
		super.bind_(desktop, skipper, after);

		var mode = this._mode;
		zWatch.listen({onSize: this});

		if (mode != 'embedded') {
			zWatch.listen({onFloatUp: this, onHide: this, onShow: this});
			this.setFloating_(true);

			if (_isModal(mode)) _doModal(this);
			else _doOverlapped(this);
		}

		if (this._sizable)
			_makeSizer(this);

		if (this._maximizable && this._maximized) {
			var self = this;
			after.push(function () {
				self._maximized = false;
				self.setMaximized(true, true);
			});
		}

		if (this._mode != 'embedded' && (!zk.css3)) {
			jq.onzsync(this); //sync shadow if it is implemented with div
			zWatch.listen({onResponse: this});
		}
		zWatch.listen({onCommandReady: this});
	}

	override detach(): void {
		// ZK-3910: the side effect of the removing iframe, the ckeditor would throw the error message
		// at console. It should unbind ckeditor before remove it. Put here can works well on every browser.
		this.unbindChildren_();
		// ZK-2247: remove iframe to prevent load twice
		if (zk.ie > 8 || zk.chrome) {
			var $jq = jq(this.$n_()).find('iframe');
			if ($jq.length)
				$jq.hide().remove();
		}
		super.detach();
	}

	override unbind_(skipper?: zk.Skipper | null, after?: CallableFunction[], keepRod?: boolean): void {
		var node = this.$n_();
		zk(node).beforeHideOnUnbind();
		node.style.visibility = 'hidden'; //avoid unpleasant effect

		if (!zk.css3) jq.unzsync(this);

		//we don't check this._mode here since it might be already changed
		if (this._shadowWgt) {
			this._shadowWgt.destroy();
			this._shadowWgt = null;
		}
		if (this._drag) {
			this._drag.destroy();
			this._drag = null;
		}
		if (this._sizer) {
			this._sizer.destroy();
			this._sizer = null;
		}

		if (this._mask) {
			if (this._anchor)
				this._anchor = null;
			this._mask.destroy();
			this._mask = null;
		}

		// ZK-1951, ZK-2045: Page becomes blank after detaching a modal window having an iframe loaded with PDF in IE > 9
		// A workaround is to hide the iframe before remove
		if (zk.ie > 9) {
			var $jq = jq(this.$n_()).find('iframe');
			if ($jq.length)
				$jq.hide().remove();
		}
		zk(node).undoVParent(); //no need to fire onVParent in unbind_
		zWatch.unlisten({
			onFloatUp: this,
			onSize: this,
			onShow: this,
			onHide: this,
			onResponse: this,
			onCommandReady: this
		});
		this.setFloating_(false);

		_unmarkModal(this);

		this.domUnlisten_(this.$n_(), 'onMouseMove');
		this.domUnlisten_(this.$n_(), 'onMouseOut');
		super.unbind_(skipper, after, keepRod);
	}

	_doMouseMove(evt: zk.Event): void {
		if (this._sizer && evt.target == this) {
			var n = this.$n_(),
				c = Window._insizer(n, zk(n).revisedOffset(), evt.pageX, evt.pageY),
				handle = this._mode == 'embedded' ? false : this.$n('cap');
			if (!this._maximized && c) {
				if (this._backupCursor == undefined)
					this._backupCursor = n.style.cursor;
				n.style.cursor = c == 1 ? 'n-resize' : c == 2 ? 'ne-resize' :
					c == 3 ? 'e-resize' : c == 4 ? 'se-resize' :
					c == 5 ? 's-resize' : c == 6 ? 'sw-resize' :
					c == 7 ? 'w-resize' : 'nw-resize';
				if (handle) jq(handle).removeClass(this.$s('header-move'));
			} else {
				n.style.cursor = this._backupCursor || ''; // bug #2977948
				if (handle) jq(handle).addClass(this.$s('header-move'));
			}
		}
	}

	_doMouseOut(evt: zk.Event): void {
		this.$n_().style.cursor = this._backupCursor || '';
	}

	override doClick_(evt: zk.Event, popupOnly?: boolean): void {
		var n: HTMLElement | null = evt.domTarget!;
		if (!n.id)
			n = n.parentNode as HTMLElement | null;
		if (n) { //If node does not exist, should propagation event directly
			switch (n) {
			case this.$n('close'):
				this.fire('onClose');
				break;
			case this.$n('max'):
				this.setMaximized(!this._maximized);
				break;
			case this.$n('min'):
				this.setMinimized(!this._minimized);
				break;
			default:
				super.doClick_(evt, popupOnly);
				return;
			}
			evt.stop();
		}
		super.doClick_(evt, popupOnly);
	}

	//@Override, children minimum flex might change window dimension, have to re-position. bug #3007908.
	override afterChildrenMinFlex_(orient: zk.FlexOrient): void {
		super.afterChildrenMinFlex_(orient);
		if (_isModal(this._mode)) //win hflex="min"
			_updDomPos(this, true, false, true); //force re-position since window width might changed. //B70-ZK-2891
	}

	//@Override, children minimize flex might change window dimension, have to re-position. bug #3007908.
	override afterChildrenFlex_(cwgt?: zk.Widget): void {
		super.afterChildrenFlex_(cwgt);
		if (_isModal(this._mode))
			_updDomPos(this, true, false, true); //force re-position since window width might changed. //B70-ZK-2891
	}

	//@Override, Bug ZK-1524: caption children should not considered.
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

	//@Override, related to Bug ZK-1799
	override getContentEdgeWidth_(width: number): number {
		if (this.caption && (width == this.caption.$n_().offsetWidth)) {
			// use caption's edge width

			var p = this.$n_(),
				fc = this.caption,
				c: Node | null | undefined = fc ? fc.$n() : p.firstChild,
				zkp = zk(p),
				w = zkp.padBorderWidth();

			if (c) {
				c = c.parentNode;
				while (c && c.nodeType == 1 && p != c) {
					var zkc = zk(c);
					w += zkc.padBorderWidth() + zkc.sumStyles('lr', jq.margins);
					c = c.parentNode;
				}
				return w;
			}
			return 0;
		} else {
			return super.getContentEdgeWidth_(width);
		}
	}

	override setFlexSizeH_(n: HTMLElement, zkn: zk.JQZK, height: number, isFlexMin?: boolean): void {
		if (isFlexMin) {
			height += this._titleHeight();
		}
		super.setFlexSizeH_(n, zkn, height, isFlexMin);
	}

	//@Override, do not count size of floating window in flex calculation. bug #3172785.
	override ignoreFlexSize_(type: zk.FlexOrient): boolean {
		return this._mode != 'embedded';
	}

	getClosableIconClass_(): string {
		return 'z-icon-times';
	}

	getMaximizableIconClass_(): string {
		return 'z-icon-expand';
	}

	getMaximizedIconClass_(): string {
		return 'z-icon-compress';
	}

	getMinimizableIconClass_(): string {
		return 'z-icon-minus';
	}

	//static
	// drag sizing (also referenced by Panel.js)
	static _startsizing(dg: zk.Draggable, evt?: zk.Event): void {
		_hideShadow(dg.control as zul.wnd.Window); //ZK-3877: startsizing is the better event to hideShadow
		zWatch.fire('onFloatUp', dg.control); //notify all
	}

	static _snapsizing(dg: zk.Draggable, pos: zk.Offset): zk.Offset {
		const z_dir = dg.z_dir!;
			// snap y only when dragging upper boundary/corners
		var px = (z_dir >= 6 && z_dir <= 8) ? Math.max(pos[0], 0) : pos[0],
			// snap x only when dragging left boundary/corners
			py = (z_dir == 8 || z_dir <= 2) ? Math.max(pos[1], 0) : pos[1];
		return [px, py];
	}

	static _ghostsizing(dg: zk.Draggable, ofs: zk.Offset, evt: zk.Event): HTMLElement | undefined {
		var wnd = dg.control as zul.wnd.Window,
			el = dg.node!;
		_hideShadow(wnd);
		wnd.setTopmost();
		var $el = jq(el);
		jq(document.body).append(
			'<div id="zk_ddghost" class="' + wnd.getZclass() + '-resize-faker"'
			+ ' style="position:absolute;top:'
			+ ofs[1] + 'px;left:' + ofs[0] + 'px;width:'
			+ $el.zk.offsetWidth() + 'px;height:' + $el.zk.offsetHeight()
			+ 'px;z-index:' + el.style.zIndex + '"><dl></dl></div>');
		return jq('#zk_ddghost')[0];
	}

	static _endghostsizing(dg: zk.Draggable, origin: HTMLElement): void {
		var el = dg.node!; //ghostvar org = zkau.getGhostOrgin(dg);
		if (origin) {
			var offset = zk(origin).cmOffset(),
				s = origin.style,
				offsetX = offset[0] - parseInt(s.left),
				offsetY = offset[1] - parseInt(s.top);
			dg.z_szofs = {
				top: jq.px(el.offsetTop - offsetY),
				left: jq.px(el.offsetLeft - offsetX),
				height: jq.px0(zk(el).revisedHeight(el.offsetHeight)),
				width: jq.px0(zk(el).revisedWidth(el.offsetWidth))
			};
		}
	}

	static _insizer(node: HTMLElement, ofs: zk.Offset, x: number, y: number): number | undefined {
		var r = ofs[0] + node.offsetWidth, b = ofs[1] + node.offsetHeight;
		if (x - ofs[0] <= 5) {
			if (y - ofs[1] <= 5)
				return 8;
			else if (b - y <= 5)
				return 6;
			else
				return 7;
		} else if (r - x <= 5) {
			if (y - ofs[1] <= 5)
				return 2;
			else if (b - y <= 5)
				return 4;
			else
				return 3;
		} else {
			if (y - ofs[1] <= 5)
				return 1;
			else if (b - y <= 5)
				return 5;
		}
	}

	static _ignoresizing(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): boolean {
		var el = dg.node!,
			wgt = dg.control as zul.wnd.Window;
		if (wgt._maximized || evt.target != wgt) return true;

		var offs = zk(el).revisedOffset(),
			v = Window._insizer(el, offs, pointer[0], pointer[1]);
		if (v) {
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

	static _aftersizing(dg: zk.Draggable, evt: zk.Event): void {
		var wgt = dg.control!,
			data = dg.z_szofs;
		if (wgt._hflex) wgt.setHflex_(null);
		if (wgt._vflex) wgt.setVflex_(null);
		wgt.fire('onSize', zk.copy(data, evt.keys), {ignorable: true});
		dg.z_szofs = null;
	}

	static _drawsizing(dg: zk.Draggable, pointer: zk.Offset, evt: zk.Event): void {
		const z_dir = dg.z_dir!,
			z_box = dg.z_box!,
			style = dg.node!.style;
		if (z_dir == 8 || z_dir <= 2) {
			var h = z_box.height + z_box.top - pointer[1];
			if (h < z_box.minHeight) {
				pointer[1] = z_box.height + z_box.top - z_box.minHeight;
				h = z_box.minHeight;
			}
			style.height = jq.px0(h);
			style.top = jq.px(pointer[1]);
		}
		if (z_dir >= 4 && z_dir <= 6) {
			var h = z_box.height + pointer[1] - z_box.top;
			if (h < z_box.minHeight)
				h = z_box.minHeight;
			style.height = jq.px0(h);
		}
		if (z_dir >= 6 && z_dir <= 8) {
			var w = z_box.width + z_box.left - pointer[0];
			if (w < z_box.minWidth) {
				pointer[0] = z_box.width + z_box.left - z_box.minWidth;
				w = z_box.minWidth;
			}
			style.width = jq.px0(w);
			style.left = jq.px(pointer[0]);
		}
		if (z_dir >= 2 && z_dir <= 4) {
			var w = z_box.width + pointer[0] - z_box.left;
			if (w < z_box.minWidth)
				w = z_box.minWidth;
			style.width = jq.px0(w);
		}
	}

	static _startmove = _startmove;
	static _ghostmove = _ghostmove;
	static _endghostmove = _endghostmove;
	static _ignoremove = _ignoremove;
	static _aftermove = _aftermove;
}

@zk.WrapClass('zul.wnd.Skipper')
export class Skipper extends zk.Skipper {
	_w: Window;

	constructor(wnd: zul.wnd.Window) {
		super();
		this._w = wnd;
	}

	override restore(wgt: zk.Widget, skip: HTMLElement | null | undefined): void {
		super.restore(wgt, skip);
		var w = this._w;
		if (w._mode != 'embedded') {
			_updDomPos(w); //skipper's size is wrong in bind_
			w.zsync();
		}
	}
}

/** @class zul.wnd.WindowRenderer
 * The renderer used to render a window.
 * It is designed to be overriden
 * @since 5.0.5
 */
export let WindowRenderer = {
	/** Returns whether to check the border's height.
	 *
	 * @param zul.wnd.Window wgt the window
	 */
	shallCheckBorder(wgt: zul.wnd.Window): boolean {
		return wgt._mode != 'popup'
			&& (wgt._mode != 'embedded' || wgt.getBorder() != 'none');
	}
};
zul.wnd.WindowRenderer = WindowRenderer;