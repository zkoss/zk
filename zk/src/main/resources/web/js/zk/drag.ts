/* drag.ts

	Purpose:

	Description:

	History:
		Mon Nov 10 11:06:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

script.aculo.us dragdrop.js v1.7.0,
Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
	(c) 2005, 2006 Sammi Williams (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz)

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
export interface DraggableScrollOptions {
	scrollTo(left: number, top: number): void;
	scrollLeft: number;
	scrollTop: number;
	outerHTML: string;
}

export interface DraggableOptions {
	scrollSensitivity?: number;
	scrollSpeed?: number;
	initSensitivity?: number;
	delay?: number;
	fireOnMove?: boolean;
	reverteffect?(dg: Draggable, offset: zk.Offset): void;
	revert?: boolean | ((dg: Draggable, offset: zk.Offset, evt: zk.Event) => boolean);
	endeffect?(dg: Draggable, evt?: zk.Event): void;
	starteffect?(dg: Draggable, evt?: zk.Event): void;
	endghosting?(dg: Draggable, node: HTMLElement): void;
	change?(dg: Draggable, offset: zk.Offset, evt?: zk.Event): void;
	constraint?: string | ((dg: Draggable, offset: zk.Offset, evt: zk.Event) => zk.Offset);
	draw?(dg: Draggable, offset: zk.Offset, evt: zk.Event): void;
	ignoredrag?(dg: Draggable, offset?: zk.Offset, evt?: zk.Event): boolean;
	handle?: HTMLElement;
	scroll?: DraggableScrollOptions | HTMLElement | Window;
	overlay?: boolean;
	ghosting?(dg: Draggable, ofs: zk.Offset, evt: zk.Event): HTMLElement | undefined;
	stackup?: boolean;
	zIndex?: string;
	snap?: zk.Offset | ((dg: Draggable, offset: zk.Offset) => zk.Offset);
}

	var _dragging = {},
		_actTmout: undefined | number, //if not null, it means _activate() is called but not really activated
		_stackup: undefined | HTMLIFrameElement, _activedg: undefined | Draggable, _initPt: zk.Offset, _dnEvt: boolean | zk.Event | undefined,
		_lastPt: zk.Offset | undefined, _lastScrlPt: zk.Offset | undefined;

	/** @internal */
	function _activate(dg: Draggable, devt: JQuery.TriggeredEvent, pt: zk.Offset): void {
		_actTmout = setTimeout(function () {
			_actTmout = undefined;
			//bug: 3027322 & 2924049: Wrong target when dragging a sub div in IE browsers
			if (!_activedg || _activedg.node == dg.node)
				_activedg = dg;
		}, dg.opts.delay);
		_initPt = pt;
	}
	/** @internal */
	function _deactivate(): void {
		_activedg = undefined;
		if (_dnEvt) setTimeout(function () {_dnEvt = undefined;}, 0);
	}
	/** @internal */
	function _docmousemove(devt: JQuery.TriggeredEvent): void {
		if (!_activedg || _activedg.dead) return;

		var evt = jq.Event.zk(devt),
			pt = [evt.pageX, evt.pageY] as zk.Offset;

		// Mozilla-based browsers fire successive mousemove events with
		// the same coordinates, prevent needless redrawing (moz bug?)
		if (_lastPt && _lastPt[0] == pt[0]
		&& _lastPt[1] == pt[1])
			return;

		_lastPt = pt;
		_activedg._updateDrag(pt, evt);
		devt.stop();
			//test/dragdrop.zul: it seems less stall-dragging when dragging
			//IMG (but still happens if dragging fast)
	}
	/** @internal */
	function _docmouseup(devt: JQuery.TriggeredEvent): void {
		if (_actTmout) {
			clearTimeout(_actTmout);
			_actTmout = undefined;
		}
		var evt = jq.Event.zk(devt),
			adg = _activedg;
		if (!adg) {
			// B50-ZK-221: need to clear _dnEvt here
			if (evt.which == 1)
				_dnEvt = undefined;
			return;
		}

		_lastPt = _activedg = undefined;
		adg._endDrag(evt);
		if (evt.domStopped) devt.stop();
		// Bug B50-3285142: Drag fails to clear up ghost when widget is detached
		if (adg._suicide) {
			adg._suicide = false;
			adg.destroy();
		}
	}
	/** @internal */
	function _dockeypress(devt: JQuery.TriggeredEvent): void {
		if (_activedg) _activedg._keypress(devt);
	}

	//default effect//
	/** @internal */
	function _defStartEffect(dg: Draggable): void {
		var node: undefined | HTMLElement & {_$opacity?} = dg.node;
		if (node) {
			node._$opacity = jq(node).css('opacity');
			_dragging[node.toString()] = true;
		}
		if (node) {
			jq(node)
				.css( // from
					'opacity', node ? node._$opacity as string : 1
				)
				.animate({ // to
					opacity: 0.7
				}, { // properties
					duration: 200
				});
		}
	}
	/** @internal */
	function _defEndEffect(dg: Draggable): void {
		var node: undefined | HTMLElement & {_$opacity?} = dg.node,
			toOpacity = typeof node!._$opacity == 'number' ? node!._$opacity : 1.0;
		if (node) {
			jq(node)
				.css({ // from
					opacity: 0.7
				})
				.animate({ // to
					opacity: toOpacity
				}, { // properties
					duration: 200,
					queue: '_draggable',
					complete: function () {
						delete _dragging[node!.toString()];
					}
				});
		}
	}
	/** @internal */
	function _defRevertEffect(dg: Draggable, offset: zk.Offset): void {
		var dx: number | undefined, dy: number | undefined;
		if ((dx = offset[0]) && (dy = offset[1])) {
			var node = dg.node,
				orgpos = node?.style.position,
				dur = Math.sqrt(Math.abs(dy ^ 2) + Math.abs(dx ^ 2)) * 20;
			
			if (node) {
				jq(node)
					.animate({ // to
						left: `-=${dx}`,
						top: `-=${dy}`
					}, { // properties
						duration: dur,
						queue: '_draggable',
						complete: function () {
							node!.style.position = orgpos || '';
						}
					});
			}
		}
	}
	/** @internal */
	function _disableDragStart(evt: JQuery.TriggeredEvent): boolean {
		return jq.nodeName(evt.target as HTMLElement, 'input', 'textarea');
	}
/**
 * A draggable object used to make a DOM element draggable.
 */
export class Draggable extends zk.Object {
	/** @internal */
	declare _isScrollChild?: boolean;
	declare delta: zk.Offset;
	declare dragging: boolean;
	/** @internal */
	declare _suicide?: boolean;
	declare dead?: boolean;
	declare lastScrolled?: Date;
	declare scrollSpeed: zk.Offset;
	declare offset: zk.Offset;
	declare scrollInterval?: number;
	/** @internal */
	declare _innerOfs: zk.Offset;
	declare stackup?: HTMLDivElement;
	/** @internal */
	declare _stackup?: HTMLIFrameElement;
	declare orgnode?: HTMLElement;
	declare z_scrl?: zk.Offset;
	declare z_orgpos?: string;
	declare orgZ?: number;
	declare orgScrlLeft?: number;
	declare orgScrlTop?: number;
	/** @internal */
	declare _clone?: Node;
	/** @internal */
	declare _lastDropTo?: zk.Widget;
	/** @internal */
	declare _lastDrop?: zk.Widget;
	/** @internal */
	declare _orgcursor?: string; // zk.Widget.prototype.uncloneDrag_
	/** @internal */
	declare _zszofs?: number; // zul.mesh.HeaderWidget
	/** @internal */
	declare _zmin?: number; // zul.mesh.HeaderWidget

	// zul.wnd.{Window, Panel}
	/** @internal */
	declare _wndoffs?: zk.Offset;
	declare z_szofs?: {
		top: string;
		left: string;
		width: string;
		height: string;
	};
	declare z_dir?: number;
	declare z_orgzi?: string;
	declare z_box?: {
		top: number;
		left: number;
		height: number;
		width: number;
		minHeight: number;
		minWidth: number;
	};

	// zul.layout.LayoutRegion
	/** @internal */
	declare _point?: zk.Offset;
	/** @internal */
	declare _rootoffs?: {
		maxs: number;
		left: number;
		right: number;
		top: number;
		bottom: number;
		mins: number;
	};

	// zul.WScroll
	/** @internal */
	declare _lastSteps: number;
	/** @internal */
	declare _timer: number;
	/** @internal */
	declare _epos?: HTMLElement;
	/** @internal */
	declare _scale: number;
	/** @internal */
	declare _endStep: number;
	/** @internal */
	declare _steps: number;
	/** @internal */
	declare _isVer: boolean;
	/** @internal */
	declare _start: number;
	/** @internal */
	declare _end: number;
	/** @internal */
	declare _lastPos?: number;

	// zkmax.big.Biglistbox
	/** @internal */
	declare _step: number;
	/** @internal */
	declare _diff: number;
	/** @internal */
	declare _ghost: HTMLElement;

	/** @internal */
	static _drags: Draggable[] = [];
	/**
	 * The control object for this draggable.
	 */
	control?: zk.Object;
	/**
	 * The DOM element that represents the handle that the user can
	 * drag the whole element ({@link node}.
	 * It is either {@link node} or a child element of it.
	 */
	handle?: HTMLElement;
	/**
	 * The DOM element that is draggable (the whole element).
	 */
	node?: HTMLElement;
	/** The options of this draggable.
	 * <h3>Allowed options</h3>
	 * <blockquote>
	 * <h4>handle</h4>
	 * ```ts
	 * {@link DOMElement} handle
	 * ```
	 * <p>A child DOM element that the user can drag the whole element.
	 * Specify one if not the whole element (the node argument) can be dragged.
	 * It becomes the value of {@link handle} if specified.</p>
	 * @defaultValue `null` (i.e., {@link node} is used.
	 *
	 * <h4>snap</h4>
	 *```java
	 * {@link zk.Offset} snap;
	 * int snap;
	 * Offset snap({@link zk.Draggable} dg, {@link zk.Offset} pos);
	 * ```
	 * <p>Specifies how many pixels to snap the dragging. For example, if the snap is 10, then the dragging has no effect if the offset is only 4, and the dragging offset is considered as 10 if it was 5.
	 * <p>The first format specifies the snaps for the x and y coordinate, such as [5, 3]. The second format specifies the snap for both x and y coordinate. The third format is used to calculate the snap dynamically based on the current position.
	 * @defaultValue `null`
	 * <ul>
	 * <li>dg - the draggable object</li>
	 * <li>pos - the position of the element being dragged </li>
	 * </ul>

	 * <h4>starteffect</h4>
	 * ```java
	 * void starteffect({@link zk.Draggable} dg);
	 * ```
	 * <p>Specifies the effect to execute when the dragging is started. It usually generates an animation effect.</p>
	 * @defaultValue a default action. To disable it, pass {@link zk.$void}.
	 *
	 * <h4>endeffect</h4>
	 * ```java
	 * void endeffect({@link zk.Draggable} dg, {@link zk.Event} evt);
	 * ```
	 * <p>Specifies the effect to execute when the dragging is finished. It usually generates an animation effect.</p>
	 * @defaultValue a default action. To disable it, pass {@link zk.$void}.
	 * <ul>
	 * <li>evt - the DOM event. It is null if caused by scrolling.
	 * It is not null if caused by dragging. </li>
	 * </ul>
	 *
	 * <h4>reverteffect</h4>
	 * ```java
	 * void reverteffect({@link zk.Draggable} dg, int dx, int dy);
	 * ```
	 * <p>The function to do the revert effect.
	 * Notice that it is ignored if the revert option is false (or returns false). </p>
	 * @defaultValue `null`.
	 * <ul>
	 * <li>dx - the number of pixels that the element has been dragged in the X coordinate</li>
	 * <li>dy - the number of pixels that the element has been dragged in the Y coordinate</li>
	 * </ul>
	 *
	 * <h4>revert</h4>
	 * ```java
	 * boolean revert;
	 * boolean revert({@link zk.Draggable} dg, {@link zk.Offset} pointer, {@link zk.Event} evt);
	 * ```
	 * <p>The revert option could be a boolean, or a function that returns a boolean value. The boolean value decides whether to revert the dragging after dragged. If true, the element is reverted to its original location.
	 * @defaultValue `false`
	 * <p>To have a custom revert effect, you can specify a function as the #reverteffect option. It is usually an animation effect; see zEffect;
	 * <ul>
	 * <li>pointer - the offset of the mouse pointer</li>
	 * <li>evt - the DOM event. It is null if caused by scrolling.
	 * It is a mouse event if caused by dragging. </li>
	 * </ul>
	 *
	 * <h4>constraint</h4>
	 * ```java
	 * String constraint;
	 * {@link zk.Offset} constraint({@link zk.Draggable} dg, {@link zk.Offset} pos, {@link zk.Event} evt);
	 * ```
	 * <p>Specifies the constraint. The first format specifies either 'vertical' or 'horizontal' to indicate that it can be dragged only in the vertical or horizontal direction.
	 * <p>The second format specified a function that can modify the position dynamically. For example, you can limit the drag at the diagonal direction.
	 * <ul>
	 * <li>pos - the position of the element being dragged. It is the position going to assign to {@link node}'s left and top. </li>
	 * <li>evt - the mouse event.</li>
	 * </ul>
	 * <p>Returns real position, or null if the pos argument is correct
	 *
	 * <h4>ghosting</h4>
	 * ```java
	 * boolean ghosting;
	 * {@link DOMElement} ghosting({@link zk.Draggable dg}, {@link zk.Offset} pos, {@link zk.Event} evt);
	 * ```
	 * <p>Specified whether to make a copy of the element and then drag the copy instead of the element itself.
	 * <p>If true is specified (the first format), {@link node} is cloned and the cloned element will be dragged.
	 * <p>If a function is specified (the second format), the function is called and it shall create and return a DOM element (so called a ghost or a copy)that will be used for dragging. Furthermore, after dragging, `endghosting`, if specified, will be called to clean up.
	 * @defaultValue `null` (the element, {@link node}, will be dragged directly.
	 * <ul>
	 * <li>pos - the position of the new created element, i.e., the left-top corner. </li>
	 * </ul>
	 * <p>Example:
	 * ```ts
	 * var html = '<div style="left:' + pos[0] + 'px;top:' + pos[1] +'px"';
	 * //...
	 * }
	 * ```
	 * <p>Returns the ghost element. This element will become {@link node}, and
	 * the original node will be restored after the dragging is finished (also after function specified in `endghosting` is called).
	 *
	 * <h4>endghosting</h4>
	 * ```java
	 * void endghosting({@link zk.Draggable} dg, {@link DOMElement} origin);
	 * ```
	 * <p>Called after the dragging is finished to clean up what have been done by the function
	 * specified in `ghosting`.
	 * <p>It is optional since {@link zk.Draggable} will remove the DOM element
	 * created by the function specified in `ghosting`.
	 * <p>Notice that it is ignored if the `ghosting` option is not
	 * specified with a function.
	 * @defaultValue `null`
	 * <ul>
	 * <li>origin - the original element ({@link node}) before the function
	 * specified in `ghosting`. Notice {@link node} is switched to
	 * the ghost element during dragging, and restored after `endghosting` was called. </li>
	 * </ul>
	 *
	 * <h4>overlay</h4>
	 * ```java
	 * boolean overlay;
	 * ```
	 * <p>Specifies whether to create a DIV to cover the whole browser window when dragging. The DIV is helpful if the browser window contains iframe and other objects that will 'eat' the mousemove effect (and cause the dragging stopped abruptly).
	 * @defaultValue `false`
	 *
	 * <h4>stackup</h4>
	 * ```java
	 * boolean stackup;
	 * ```
	 * <p>Specifies whether to create a stackup (actually an iframe) that makes sure the element being dragging is on top of others.
	 * @defaultValue `false`
	 *
	 * <h4>zIndex</h4>
	 * ```java
	 * int zIndex;
	 * int zIndex({@link zk.Draggable} dg);
	 * ```
	 * <p>The z-index that will be assigned when dragging the element.
	 * If it is a function, it is assumed to be a function returning
	 * the z-index (or -1 if it prefer not to change z-index)
	 * @defaultValue <i>not assign any value to z-index</i>
	 *
	 * <h4>change</h4>
	 * ```java
	 * void change({@link zk.Draggable} dg, {@link zk.Offset} pointer, {@link zk.Event} evt);
	 * ```
	 * <p>Called after the dragging has changed the position of the element
	 * ({@link node}). It is called after the function specified
	 * in the snap and draw or constraint option.
	 * It is also called it has been scrolled.
	 * <ul>
	 * <li>pointer - the offset of the mouse pointer</li>
	 * <li>evt - the DOM event. It is null if cauased by scrolling.
	 * It is a mouse event if caused by dragging. </li>
	 * </ul>
	 *
	 * <h4>draw</h4>
	 * ```java
	 * void draw({@link zk.Draggable} dg, {@link zk.Offset} pos, {@link zk.Event} evt);
	 * ```
	 * <p>Used to override the default change of the element's position. If not specified, the constraint option is,
	 * if any, called and then {@link node}'s position (left and top) are changed. You can provide your own way to change the position.
	 * @defaultValue `null`
	 * <ul>
	 * <li>pos - the position of the element being dragged. It is the position going to assign to {@link node}'s left and top.</li>
	 * <li>evt - the mouse event</li>
	 * </ul>
	 *
	 * <h4>scroll</h4>
	 * ```java
	 * {@link DOMElement} scroll;
	 * String scroll; //DOM Element's ID
	 * ```
	 * <p>Specify which DOM element to consider its scrollLeft and scrollTop. In other words, it is the element with scrollbar that affects the location of the draggable element (zk.Draggable#node).
	 * @defaultValue `null`
	 * <p>Notice that scroll could be DOM element, including window, or its ID.
	 *
	 * <h4>scrollSensitivity</h4>
	 * ```java
	 * int scrollSensitivity;
	 * ```
	 * <p>The scroll sensitivity.
	 * @defaultValue `20`
	 *
	 * <h4>scrollSpeed</h4>
	 * ```java
	 * int scrollSpeed;
	 * ```
	 * <p>The scroll speed.
	 * @defaultValue `15`
	 * </blockquote>
	 */
	opts: DraggableOptions;

	/** @internal */
	_dragImg?: HTMLElement // zk.Widget.prototype.ghost
		| JQuery; // zul/sel/ItemWidget and zul/sel/Listitem

	/**
	 * @param control - the control object for this draggable.
	 * It can be anything, but it is usually a widget ({@link zk.Widget}).
	 * @param node - the DOM element that is made to be draggable.
	 * If omitted and control is a widget, {@link zk.Widget.$n} is assumed.
	 * @param opts - options. Refer to {@link opts} for allowed options.
	 */
	constructor(control: zk.Object & {node?: HTMLElement} | undefined, node: HTMLElement | undefined, opts: DraggableOptions) {
		super();
		if (!_stackup) {
		//IE: if we don't insert stackup at beginning, dragging is slow
			_stackup = jq.newStackup(undefined, 'z_ddstkup');
			document.body.appendChild(_stackup);
		}

		this.control = control;
		this.node = node = node ? jq(node, zk)[0] : control && (control.node || (control instanceof zk.Widget ? control.$n() as HTMLElement | undefined : undefined));
		if (!this.node)
			throw 'Handle required for ' + control;

		opts = zk.$default(opts, {
//No default z-index (since caller, such as window, might set it)
			scrollSensitivity: 20,
			scrollSpeed: 15,
			initSensitivity: 3,
			delay: 0,
			fireOnMove: true
		});

		if (opts.reverteffect == null)
			opts.reverteffect = _defRevertEffect;
		if (opts.endeffect == null) {
			opts.endeffect = _defEndEffect;
			if (opts.starteffect == null)
				opts.starteffect = _defStartEffect;
		}

		let handler: HTMLElement | undefined;
		if (opts.handle) handler = jq(opts.handle, zk)[0];
		if (!handler) handler = node;
		this.handle = handler;

		if (opts.scroll && !opts.scroll.scrollTo && !(opts.scroll as DraggableScrollOptions).outerHTML) {
			opts.scroll = jq(opts.scroll as HTMLElement, zk)[0];
			this._isScrollChild = jq.isAncestor(opts.scroll as HTMLElement, node);
		}

		this.delta = this._currentDelta();
		this.opts = opts;
		this.dragging = false;

		jq(this.handle).on('zmousedown', this.proxy(this._mousedown))
				// issue in test/dragdrop.zul for dragging image file
				.on('dragstart', _disableDragStart);

		//register
		if (Draggable._drags.length == 0)
			jq(document).on('zmouseup', _docmouseup)
				.on('zmousemove', _docmousemove)
				.keypress(_dockeypress);
		Draggable._drags.push(this);
	}
	/**
	 * Destroys this draggable object. This method must be called to clean up, if you don't want to associate the draggable feature to a DOM element.
	 */
	destroy(): void {
		if (this.dragging) {
			// Bug B50-3285142: Drag fails to clear up ghost when widget is detached
			// destroy later
			this._suicide = true;
			return;
		}
		jq(this.handle).off('zmousedown', this.proxy(this._mousedown))
				.off('dragstart', _disableDragStart);

		//unregister
		Draggable._drags.$remove(this);
		if (Draggable._drags.length == 0)
			jq(document).off('zmouseup', _docmouseup)
				.off('zmousemove', _docmousemove)
				.off('keypress', _dockeypress);
		if (_activedg == this) //just in case
			_activedg = undefined;

		this.node = this.control = this.handle = undefined;
		this.dead = true;
	}

	/** `[left, right]` of this node. @internal */
	_currentDelta(): zk.Offset {
		var $node = jq(this.node);
		return [zk.parseInt($node.css('left')), zk.parseInt($node.css('top'))];
	}

	/** @internal */
	_startDrag(evt: zk.Event): void {
		zWatch.fire('onStartDrag', this, evt as unknown as zk.FireOptions);

		//disable selection
		zk(document.body).disableSelection(); // Bug #1820433
		jq.clearSelection(); // Bug #2721980
		if (this.opts.overlay) { // Bug #1911280 and 2986227
			var stackup = document.createElement('div');
			document.body.appendChild(stackup);
			stackup.className = 'z-dd-stackup';
			zk(stackup).disableSelection();
			var st = (this.stackup = stackup).style;
			st.width = jq.px0(jq(document).width());
			st.height = jq.px0(jq(document).height());
		}
		zk.dragging = this.dragging = true;

		var node = this.node!,
			opt;
		if (opt = this.opts.ghosting)
			if (typeof opt == 'function') {
				this.delta = this._currentDelta();
				this.orgnode = this.node;

				var $node = zk(this.node),
					ofs = $node.cmOffset();
				this.z_scrl = $node.scrollOffset(); // TODO: assert non null after assignment
				this.z_scrl[0] -= jq.innerX(); this.z_scrl[1] -= jq.innerY();
					//Store scrolling offset since _draw not handle DIV well
				ofs[0] -= this.z_scrl[0]; ofs[1] -= this.z_scrl[1];

				node = this.node = (opt as CallableFunction)(this, ofs, evt) as HTMLElement;
			} else {
				this._clone = jq(node).clone()[0];
				this.z_orgpos = node.style.position; //Bug 1514789
				if (this.z_orgpos != 'absolute')
					jq(node).absolutize();
				node.parentNode?.insertBefore(this._clone, node);
			}

		if (this.opts.stackup) {
			if (zk(_stackup).isVisible()) //in use
				this._stackup = jq.newStackup(node, node.id + '-ddstk');
			else {
				this._stackup = _stackup;
				this._syncStackup();
				node.parentNode?.insertBefore(_stackup!, node);
			}
		}

		this.orgZ = -1;
		if (opt = this.opts.zIndex) { //after ghosting
			if (typeof opt == 'function')
				opt = (opt as CallableFunction)(this) as number;
			if (opt >= 0) {
				this.orgZ = zk.parseInt(jq(node).css('z-index'));
				node.style.zIndex = String(opt);
			}
		}

		if (this.opts.scroll) {
			if (this.opts.scroll == window) {
				var where = this._getWndScroll(this.opts.scroll);
				this.orgScrlLeft = where.left;
				this.orgScrlTop = where.top;
			} else {
				this.orgScrlLeft = (this.opts.scroll as HTMLElement).scrollLeft;
				this.orgScrlTop = (this.opts.scroll as HTMLElement).scrollTop;
			}
		}

		if (this.opts.starteffect)
			this.opts.starteffect(this, evt);
	}
	/** @internal */
	_syncStackup(): void {
		if (this._stackup) {
			var node = this.node,
				st = this._stackup.style;
			if (node) {
				st.display = 'block';
				st.left = node.offsetLeft + 'px';
				st.top = node.offsetTop + 'px';
				st.width = node.offsetWidth + 'px';
				st.height = node.offsetHeight + 'px';
			}
		}
	}

	/** @internal */
	_updateDrag(pt: zk.Offset, evt: zk.Event): void {
		if (!this.dragging) {
			let v = this.opts.initSensitivity;
			if (v && pt[0] <= _initPt[0] + v && pt[0] >= _initPt[0] - v
			&& pt[1] <= _initPt[1] + v && pt[1] >= _initPt[1] - v)
				return;
			this._startDrag(evt);
		}
		this._updateInnerOfs();

		this._draw(pt, evt);
		if (this.opts.change) this.opts.change(this, pt, evt);
		this._syncStackup();

		if (this.opts.scroll) {
			this._stopScrolling();

			var p: zk.Offset | [number, number, number, number];
			if (this.opts.scroll == window) {
				var o = this._getWndScroll(this.opts.scroll);
				p = [o.left, o.top, o.left + o.width, o.top + o.height];
			} else {
				const scrollElem = this.opts.scroll as HTMLElement;
				p = zk(scrollElem).viewportOffset();
				p[0] += scrollElem.scrollLeft + this._innerOfs[0];
				p[1] += scrollElem.scrollTop + this._innerOfs[1];
				p.push(p[0] + scrollElem.offsetWidth);
				p.push(p[1] + scrollElem.offsetHeight);
			}

			var speed = [0, 0] as zk.Offset,
				v = this.opts.scrollSensitivity!;
			if (pt[0] < (p[0] + v)) speed[0] = pt[0] - (p[0] + v);
			if (pt[1] < (p[1] + v)) speed[1] = pt[1] - (p[1] + v);
			if (pt[0] > (p[2]! - v)) speed[0] = pt[0] - (p[2]! - v);
			if (pt[1] > (p[3]! - v)) speed[1] = pt[1] - (p[3]! - v);
			this._startScrolling(speed);
		}

		// fix AppleWebKit rendering
		if (zk.agent.indexOf('AppleWebKit') > 0) window.scrollBy(0, 0);

		evt.stop();
	}

	/** @internal */
	_finishDrag(evt: zk.Event, success: boolean): void {
		this.dragging = false;
		if (this.stackup) {
			jq(this.stackup).remove();
			delete this.stackup;
		}

		//enable selection back and clear selection if any
		zk(document.body).enableSelection();
		setTimeout(jq.clearSelection, 0);

		var stackup = this._stackup;
		if (stackup) {
			if (stackup == _stackup) jq(stackup).hide();
			else jq(stackup).remove();
			delete this._stackup;
		}

		var node = this.node!;
		if (this.opts.ghosting)
			if (typeof this.opts.ghosting == 'function') {
				if (this.opts.endghosting)
					this.opts.endghosting(this, this.orgnode!);
				if (node != this.orgnode) {
					jq(node).remove();
					this.node = this.orgnode!;
				}
				delete this.orgnode;
			} else {
				if (this.z_orgpos != 'absolute') { //Bug 1514789
					zk(this.node).relativize();
					node.style.position = this.z_orgpos!;
				}
				jq(this._clone!).remove();
				this._clone = undefined;
			}

		var pt: zk.Offset = [evt.pageX, evt.pageY],
			revert = this.opts.revert,
			revertResult = false;
		if (revert && typeof revert == 'function')
			revertResult = revert(this, pt, evt);

		var d = this._currentDelta(),
			d2 = this.delta;
		if (revertResult && this.opts.reverteffect) {
			this.opts.reverteffect(this,
				[d[0] - this.delta[0], d[1] - this.delta[1]]);
		} else {
			this.delta = d;
		}

		if (this.orgZ != -1)
			node.style.zIndex = this.orgZ as unknown as string;

		if (this.opts.endeffect)
			this.opts.endeffect(this, evt);

		var wgt = this.control;
		if (this.opts.fireOnMove && (wgt instanceof zk.Widget)) {
			if (d[0] != d2[0] || d[1] != d2[1]) {
				wgt.fire('onMove', zk.copy({
					left: node.style.left,
					top: node.style.top
				}, evt.data), {ignorable: true});
			}
		}
		_deactivate();
		var self = this;
		setTimeout(function () {
			zk.dragging = false;
			zWatch.fire('onEndDrag', self, evt as unknown as zk.FireOptions);
		}, zk.ios ? 500 : 0);
			//we have to reset it later since event is fired later (after onmouseup)
	}

	/** @internal */
	_mousedown(devt: JQuery.TriggeredEvent): void {
		var node = this.node,
			evt = jq.Event.zk(devt),
			target = devt.target as HTMLElement;
		if (_actTmout || (node && _dragging[node.toString()]) || evt.which != 1
			|| (zk.webkit && jq.nodeName(target, 'select'))
			|| (zk(target).isInput() && this.control != zk.Widget.$(target)))
			return;
			// Bug B50-3147909: Safari has issue with select and draggable
			// Now select element is not draggable in Chrome and Safari

		var pt = [evt.pageX, evt.pageY] as zk.Offset;

		if (this.opts.ignoredrag && this.opts.ignoredrag(this, pt, evt)) {
			if (evt.domStopped) devt.stop();
			return;
		}

		// Bug ZK-427
		// and ZK-484 (get the pos variable after invoking ignoredrag function)
		var zkn = zk(node),
			pos = zkn.cmOffset(),
			ofs: zk.Offset = [pt[0] - pos[0], pt[1] - pos[1]],
			jqBorders = jq.borders, v;

		// ZK-488 node.clientWidth and node.clientHeight are 0 if no scrollbar on IE9
		if ((v = node?.clientWidth) && ofs[0] > (v + zkn.sumStyles('l', jqBorders)) && ofs[0] < (node.offsetWidth - zkn.sumStyles('r', jqBorders))
		|| (v = node?.clientHeight) && ofs[1] > (v + zkn.sumStyles('t', jqBorders)) && ofs[1] < (node.offsetHeight - zkn.sumStyles('b', jqBorders))) //scrollbar
			return;

		this.offset = ofs;
		_activate(this, devt, pt);

		if (!zk.mobile) {
			_dnEvt = jq.Event.zk(devt, this.control as zk.Widget);
			//simulate mousedown later (mount.js's invocation of ignoreMouseUp)
		}
	}
	/** @internal */
	_keypress(devt: JQuery.TriggeredEvent): void {
		if (devt.keyCode == 27) {
			this._finishDrag(jq.Event.zk(devt), false);
			devt.stop();
		}
	}

	/** @internal */
	_endDrag(evt: zk.Event): void {
		if (this.dragging) {
			this._stopScrolling();
			this._finishDrag(evt, true);
			evt.stop();
		} else
			_deactivate();
	}

	/** @internal */
	_draw(point: zk.Offset, evt?: zk.Event): void {
		var node = this.node!,
			$node = zk(node),
			pos = $node.cmOffset(),
			opts = this.opts;
		if (opts.ghosting) {
			var r = $node.scrollOffset();
			pos[0] += r[0] - this._innerOfs[0]; pos[1] += r[1] - this._innerOfs[1];
		}

		var d = this._currentDelta(),
			scroll = opts.scroll;
		pos[0] -= d[0]; pos[1] -= d[1];

		if (scroll && (scroll != window && this._isScrollChild)) {
			pos[0] -= (scroll as HTMLElement).scrollLeft - this.orgScrlLeft!;
			pos[1] -= (scroll as HTMLElement).scrollTop - this.orgScrlTop!;
		}

		var p: zk.Offset = [point[0] - pos[0] - this.offset[0],
			point[1] - pos[1] - this.offset[1]],
			snap = opts.snap;

		if (snap)
			if (typeof snap == 'function') {
				p = snap(this, p);
			} else {
				if (Array.isArray(snap)) {
					p = [Math.round(p[0] / snap[0]) * snap[0],
						Math.round(p[1] / snap[1]) * snap[1]];
				} else {
					p = [Math.round(p[0] / snap) * snap,
						Math.round(p[1] / snap) * snap];
				}
			}

		//Resolve scrolling offset when DIV is used
		if (this.z_scrl) {
			p[0] -= this.z_scrl[0]; p[1] -= this.z_scrl[1];
		}

		var style = node.style;
		if (typeof opts.draw == 'function') {
			opts.draw(this, this.snap_(p, opts), evt!);
		} else if (typeof opts.constraint == 'function') {
			var np = opts.constraint(this, p, evt!); //return null or [newx, newy]
			if (np) p = np;
			p = this.snap_(p, opts);
			style.left = jq.px(p[0]);
			style.top = jq.px(p[1]);
		} else {
			p = this.snap_(p, opts);
			if ((!opts.constraint) || (opts.constraint == 'horizontal'))
				style.left = jq.px(p[0]);
			if ((!opts.constraint) || (opts.constraint == 'vertical'))
				style.top = jq.px(p[1]);
		}

		if (style.visibility == 'hidden') style.visibility = ''; // fix gecko rendering
	}

	/** @internal */
	_stopScrolling(): void {
		if (this.scrollInterval) {
			clearInterval(this.scrollInterval);
			this.scrollInterval = undefined;
			_lastScrlPt = undefined;
		}
	}
	/** @internal */
	_startScrolling(speed: zk.Offset): void {
		if (speed[0] || speed[1]) {
			const scrollSpeed = this.opts.scrollSpeed!;
			this.scrollSpeed = [speed[0] * scrollSpeed, speed[1] * scrollSpeed];
			this.lastScrolled = new Date();
			this.scrollInterval = setInterval(this.proxy(this._scroll), 10);
		}
	}

	/** @internal */
	_scroll(): void {
		var current = new Date(),
			delta = current.valueOf() - this.lastScrolled!.valueOf();
		this.lastScrolled = current;
		if (this.opts.scroll == window) {
			if (this.scrollSpeed[0] || this.scrollSpeed[1]) {
				var o = this._getWndScroll(this.opts.scroll),
					d = delta / 1000;
				this.opts.scroll.scrollTo(o.left + d * this.scrollSpeed[0],
					o.top + d * this.scrollSpeed[1]);
			}
		} else if (this.opts.scroll instanceof HTMLElement) {
			this.opts.scroll.scrollLeft += this.scrollSpeed[0] * delta / 1000;
			this.opts.scroll.scrollTop += this.scrollSpeed[1] * delta / 1000;
		}

		this._updateInnerOfs();
		if (this._isScrollChild) {
			_lastScrlPt = _lastScrlPt ?? _lastPt!;
			_lastScrlPt[0] += this.scrollSpeed[0] * delta / 1000;
			_lastScrlPt[1] += this.scrollSpeed[1] * delta / 1000;
			if (_lastScrlPt[0] < 0)
				_lastScrlPt[0] = 0;
			if (_lastScrlPt[1] < 0)
				_lastScrlPt[1] = 0;
			this._draw(_lastScrlPt);
		}

		if (this.opts.change) {
			var devt = window.event ? jq.event.fix(window.event) : undefined,
				evt = devt ? jq.Event.zk(devt) : undefined;
			this.opts.change(this,
				evt ? [evt.pageX, evt.pageY] : _lastPt!, evt);
		}
	}

	/** @internal */
	_updateInnerOfs(): void {
		this._innerOfs = [jq.innerX(), jq.innerY()];
	}
	/** @internal */
	_getWndScroll(w: Window): zk.Dimension {
		var T: number | undefined, L: number | undefined, W: number | undefined, H: number | undefined,
			doc = w.document,
			de = doc.documentElement;
		if (de && de.scrollTop) {
			T = de.scrollTop;
			L = de.scrollLeft;
		} else if (w.document.body) {
			T = doc.body.scrollTop;
			L = doc.body.scrollLeft;
		}
		if (w.innerWidth) {
			W = w.innerWidth;
			H = w.innerHeight;
		} else if (de && de.clientWidth) {
			W = de.clientWidth;
			H = de.clientHeight;
		} else {
			W = doc.body.offsetWidth;
			H = doc.body.offsetHeight;
		}
		return {top: T!, left: L!, width: W, height: H};
	}

	/**
	 * Snaps the dragging position.
	 * It is the default snapping. For individual snapping, use
	 * the snap or constraint options of {@link zk.Draggable}.
	 * Notice this method is always called no matter if the snap or constraint
	 * options are specified.
	 * @defaultValue `pos` (i.e., not changing at all)
	 * @param pos - the offset of the dragging position
	 * @returns the offset after snapped
	 * @internal
	 */
	snap_(pos: zk.Offset, opts: DraggableOptions): zk.Offset {
		if (!opts.snap && pos[1] < 0)
			pos[1] = 0;
		return pos;
	}

	static ignoreMouseUp(): boolean | zk.Event | undefined { //called by mount
		return zk.dragging ? true : _dnEvt;
	}
	static ignoreClick(): boolean { //called by mount
		return !!zk.dragging;
	}
	/**
	 * @deprecated since 8.0.2
	 * not to remove the function for backward compatibility (just in case)
	 */
	static ignoreStop(target: HTMLElement): boolean { //called by mount
		return false;
	}
}
zk.Draggable = Draggable;