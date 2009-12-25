/* drag.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 10 11:06:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

script.aculo.us dragdrop.js v1.7.0,
Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
	(c) 2005, 2006 Sammi Williams (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz)

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _drags = [],
		_dragging = [],
		_stackup, _activedg, _timeout, _initPt, _initEvt,
		_lastPt, _lastScrlPt;

	function _activate(dg, devt, pt) {
		_timeout = setTimeout(function () { 
			_timeout = null; 
			_activedg = dg; 
		}, dg.opts.delay);
		_initPt = pt;
		_initEvt = jq.Event.zk(devt, dg.control);
	}
	function _deactivate() {
		_activedg = null;
		setTimeout(function(){_initEvt=null;}, 0);
	}

	function _docmousemove(devt) {
		if(!_activedg || _activedg.dead) return;

		var evt = jq.Event.zk(devt),
			pt = [evt.pageX, evt.pageY];
		// Mozilla-based browsers fire successive mousemove events with
		// the same coordinates, prevent needless redrawing (moz bug?)
		if(_lastPt && _lastPt[0] == pt [0]
		&& _lastPt[1] == pt [1])
			return;

		_lastPt = pt;
		_activedg._updateDrag(pt, evt);
		devt.stop();
			//test/dragdrop.zul: it seems less stall-dragging when dragging
			//IMG (but still happens if dragging fast)
	}
	function _docmouseup(devt) {
		if(_timeout) { 
			clearTimeout(_timeout); 
			_timeout = null; 
		}
		if(!_activedg) return;

		_lastPt = null;
		var evt;
		_activedg._endDrag(evt = jq.Event.zk(devt));
		_activedg = null;
		if (evt.domStopped) devt.stop();
	}
	function _dockeypress(devt) {
		if(_activedg) _activedg._keypress(devt);
	}

	//default effect//
	function _defStartEffect(dg) {
		var node = dg.node;
		node._$opacity = jq(node).css('opacity');
		_dragging[node] = true;
		new zk.eff.Opacity(node, {duration:0.2, from:node._$opacity, to:0.7}); 
	}
	function _defEndEffect(dg) {
		var node = dg.node,
			toOpacity = typeof node._$opacity == 'number' ? node._$opacity : 1.0;
		new zk.eff.Opacity(node, {duration:0.2, from:0.7,
			to:toOpacity, queue: {scope:'_draggable', position:'end'},
			afterFinish: function () { 
				_dragging[node] = false;
			}
		});
	}
	function _defRevertEffect(dg, offset) {
		var dx, dy;
		if ((dx=offset[0]) || (dy=offset[1])) {
			var node = dg.node,
				orgpos = node.style.position,
				dur = Math.sqrt(Math.abs(dy^2)+Math.abs(dx^2))*0.02;
			new zk.eff.Move(node, { x: -dx, y: -dy,
				duration: dur, queue: {scope:'_draggable', position:'end'},
				afterFinish: function () {node.style.position = orgpos;}});
		}
	}

/** A draggable object used to make a DOM element draggable. 
 */
zk.Draggable = zk.$extends(zk.Object, {
	/** The control object for this draggable.
	 * @type Object
	 */
	//control: null,
	/** The DOM element that represents the handle that the user can
	 * drag the whole element ({@link #node}.
	 * It is either {@link #node} or a child element of it.
	 * @type DOMElement
	 */
	//handle: null,
	/** The DOM element that is draggable (the whole element).
	 * @type DOMElement
	 */
	//node: null,
	/** The options of this draggable.
	 * <h3>Allowed options</h3>
	 * <blockquote>
	 * <h4>handle</h4>
	 * <pre><code>{@link DOMElement} handle</code></pre>
	 * <p>A child DOM element that the user can drag the whole element. 
	 * Specify one if not the whole element (the node argument) can be dragged.
	 * It becomes the value of {@link #handle} if specified.</p>
	 * <p>Default: null (i.e., {@link #node} is used.
	 *
	 * <h4>snap</h4>
	 *<pre><code>{@link Offset} snap;
int snap;
Offset snap({@link zk.Draggable} dg, {@link Offset} pos);
</code></pre>
	 * <p>Specifies how many pixels to snap the dragging. For example, if the snap is 10, then the dragging has no effect if the offset is only 4, and the dragging offset is considered as 10 if it was 5.
	 * <p>The first format specifies the snaps for the x and y coordinate, such as [5, 3]. The second format specifies the snap for both x and y coordinate. The third format is used to calculate the snap dynamically based on the current position.
	 * <p>Default: null 
	 * <ul>
	 * <li>dg - the draggable object</li>
	 * <li>pos - the position of the element being dragged </li>
	 * </ul>

	 * <h4>starteffect</h4>
	 * <pre><code>void starteffect({@link zk.Draggable} dg);</code></pre>
	 * <p>Specifies the effect to execute when the dragging is started. It usually generates an animation effect.</p>
	 * <p>Default: a default action. To disable it, pass {@link zk#$void}.
	 *
	 * <h4>endeffect</h4>
	 * <pre><code>void endeffect({@link zk.Draggable} dg, {@link zk.Event} evt);</code></pre>
	 * <p>Specifies the effect to execute when the dragging is finished. It usually generates an animation effect.</p>
	 * <p>Default: a default action. To disable it, pass {@link zk#$void}.
	 * <ul>
	 * <li>evt - the DOM event. It is null if cauased by scrolling.
	 * It is not null if caused by dragging. </li>
	 * </ul>
	 *
	 * <h4>reverteffect</h4>
	 * <pre><code>void reverteffect({@link zk.Draggable} dg, int dx, int dy);</code></pre>
	 * <p>The function to do the revert effect.
	 * Notice that it is ignored if the revert option is false (or returns false). </p>
	 * <p>Default: null.
	 * <ul>
	 * <li>dx - the number of pixels that the element has been dragged in the X coordinate</li>
	 * <li>dy - the number of pixels that the element has been dragged in the Y coordinate</li>
	 * </ul>
	 *
	 * <h4>revert</h4>
	 * <pre><code>boolean revert;
boolean revert({@link zk.Draggable} dg, {@link Offset} pointer, {@link zk.Event} evt);
	 * <p>The revert option could be a boolean, or a function that returns a boolean value. The boolean value decides whether to revert the dragging after dragged. If true, the element is reverted to its original location.
	 * <p>Default: false 
	 * <p>To have a custom revert effect, you can specify a function as the #reverteffect option. It is usually an animation effect; see zEffect;
	 * <ul>
	 * <li>pointer - the offset of the mouse pointer</li>
	 * <li>evt - the DOM event. It is null if cauased by scrolling.
	 * It is a mouse event if caused by dragging. </li>
	 * </ul>
	 *
	 * <h4>constraint</h4>
	 * <pre><code>String constraint;
{@link Offset} constraint({@link zk.Draggable} dg, {@link Offset} pos, {@link zk.Event} evt);</code></pre>
	 * <p>Specifies the constraint. The first format specifies either 'vertical' or 'horizontal' to indicate that it can be dragged only in the vertical or horizontal direction.
	 * <p>The second format specified a function that can modify the position dynamically. For example, you can limit the drag at the diagonal direction. 
	 * <ul>
	 * <li>pos - the position of the element being dragged. It is the position going to assign to {@link #node}'s left and top. </li>
	 * <li>evt - the mouse event.</li>
	 * </ul>
	 * <p>Returns real position, or null if the pos argument is correct
	 *
	 * <h4>ghosting</h4>
	 * <pre><code>boolean ghosting;
{@link DOMElement} ghosting({@link zk.Draggable dg}, {@link Offset} pos, {@link zk.Event} evt);</code></pre>
	 * <p>Specified whether to make a copy of the element and then drag the copy instead of the element itself.
	 * <p>If true is specified (the first format), {@link #node} is cloned and the cloned element will be dragged.
	 * <p>If a function is specified (the second format), the function is called and it shall create and return a DOM element (so called a ghost or a copy)that will be used for dragging. Furthermore, after dragging, <code>endghosting</code>, if specified, will be called to clean up.
	 * <p>Default: null (the element, {@link #node}, will be dragged directly. 
	 * <ul>
	 * <li>pos - the position of the new created element, i.e., the left-top corner. </li>
	 * </ul>
	 * <p>Example:
<pre><code>
var html = '<div style="left:' + pos[0] + 'px;top:' + pos[1] +'px"';
...
</code></pre>
	 * <p>Returns the ghost element. This element will become {@link #node}, and
	 * the original node will be restored after the dragging is finished (also after function specified in <code>endghosting</code> is called). 
	 *
	 * <h4>endghosting</h4>
	 * <pre><code>void endghosting({@link zk.Draggable} dg, {@link DOMElement} origin);</code></pre>
	 * <p>Called after the dragging is finished to clean up what have been done by the function
	 * specified in <code>ghosting</code>.
	 * <p>It is optional since {@link zk.Draggable} will remove the DOM element
	 * created by the function specified in <code>ghosting</code>.
	 * <p>Notice that it is ignored if the <code>ghosting</code> option is not
	 * specified with a function.
	 * <p>Default: null 
	 * <ul>
	 * <li>origin - the original element ({@link #node}) before the function
	 * specified in <code>ghosting</code>. Notice {@link #node} is switched to
	 * the ghost element during dragging, and restored after <code>endghosting</code> was called. </li>
	 * </ul>
	 * 
	 * <h4>overlay</h4>
	 * <pre><code>boolean overlay;</code></pre>
	 * <p>Specifies whether to create a DIV to cover the whole browser window when dragging. The DIV is helpful if the browser window contains iframe and other objects that will 'eat' the mousemove effect (and cause the dragging stopped abruptly).
	 * <p>Default: false
	 *
	 * <h4>stackup</h4>
	 * <pre><code>boolean stackup;</code></pre>
	 * <p>Specifies whether to create a stackup (actually an iframe) that makes sure the element being dragging is on top of others.
	 * <p>Default: false 
	 *
	 * <h4>zIndex</h4>
	 * <pre><code>int zIndex;</code></pre>
	 * <p>The z-index that will be assigned when dragging the element.
	 * Default: <i>not assign any value to z-index</i>
	 *
	 * <h4>change</h4>
	 * <pre><code>void change({@link zk.Draggable} dg, {@link Offset} pointer, {@link zk.Event} evt);</code></pre>
	 * <p>Called after the dragging has changed the position of the element
	 * ({@link #node}). It is called after the function specified
	 * in the snap and draw or constraint option.
	 * It is also called it has been scrolled. 
	 * <ul>
	 * <li>pointer - the offset of the mouse pointer</li>
	 * <li>evt - the DOM event. It is null if cauased by scrolling.
	 * It is a mouse event if caused by dragging. </li>
	 * </ul>
	 *
	 * <h4>draw</h4>
	 * <pre><code>void draw({@link zk.Draggable} dg, {@link Offset} pos, {@link zk.Event} evt);</code></pre>
	 * <p>Used to override the default change of the element's position. If not specified, the constraint option is,
	 * if any, called and then {@link #node}'s position (left and top) are changed. You can provide your own way to change the position.
	 * <p>Default: null 
	 * <ul>
	 * <li>pos - the position of the element being dragged. It is the position going to assign to {@link #node}'s left and top.</li>
	 * <li>evt - the mouse event</li>
	 * </ul>
	 *
	 * <h4>scroll</h4>
	 * <pre><code>{@link DOMElement} scroll;
String scroll; //DOM Element's ID</code></pre>
	 * <p>Specify which DOM element to consider its scrollLeft and scrollTop. In other words, it is the element with scrollbar that affects the location of the draggable element (zk.Draggable#node).
	 * <p>Default: null 
	 * <p>Notice that scroll could be DOM element, including window, or its ID.
	 *
	 * <h4>scrollSensitivity</h4>
	 * <pre><code>int scrollSensitivity;</code></pre>
	 * <p>The scroll sensitivity.
	 * <p>Default: 20 
	 *
	 * <h4>scrollSpeed</h4>
	 * <pre><code>int scrollSpeed;</code></pre>
	 * <p>The scroll speed.
	 * <p>Default: 15 
	 * </blockquote>
	 * @type Map
	 */
	//opts: null,
	/** Constructor.
	 * @param Object control the control object for this draggable.
	 * I can be anything, but it is usually a widget ({@link zk.Widget}).
	 * @param DOMElement node [optional] the DOM element that is made to be draggable.
	 * If omitted and control is a widget, {@link zk.Widget#$n} is assumed.
	 * @param Map opts [optional] options. Refer to {@link #opts} for allowed options.
	 */
	$init: function (control, node, opts) {
		if (!_stackup) {
		//IE: if we don't insert stackup at beginning, dragging is slow
			jq(_stackup = jq.newStackup(null, 'z_ddstkup')).hide();
			document.body.appendChild(_stackup);
		}

		this.control = control;
		this.node = node = node ? jq(node, zk)[0]: control.node || (control.$n ? control.$n() : null);
		if (!node)
			throw "Handle required for "+control;

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

		if(opts.handle) this.handle = jq(opts.handle, zk)[0];
		if(!this.handle) this.handle = node;

		if(opts.scroll && !opts.scroll.scrollTo && !opts.scroll.outerHTML) {
			opts.scroll = jq(opts.scroll, zk)[0];
			this._isScrollChild = zUtl.isAncestor(opts.scroll, node);
		}

		this.delta = this._currentDelta();
		this.opts = opts;
		this.dragging = false;   

		jq(this.handle).mousedown(this.proxy(this._mousedown));

		//register
		if(_drags.length == 0)
			jq(document).mouseup(_docmouseup)
				.mousemove(_docmousemove)
				.keypress(_dockeypress);
		_drags.push(this);
	},
	/** Destroys this draggable object. This method must be called to clean up, if you don't want to associate the draggable feature to a DOM element.
	 */
	destroy: function () {
		jq(this.handle).unbind("mousedown", this.proxy(this._mousedown));

		//unregister
		_drags.$remove(this);
		if(_drags.length == 0)
			jq(document).unbind("mouseup", _docmouseup)
				.unbind("mousemove", _docmousemove)
				.unbind("keypress", _dockeypress);
		if (_activedg == this) //just in case
			_activedg = null;

		this.node = this.control = this.handle = null;
		this.dead = true;
	},

	/** [left, right] of this node. */
	_currentDelta: function () {
		var $node = jq(this.node);
		return [zk.parseInt($node.css('left')), zk.parseInt($node.css('top'))];
	},

	_startDrag: function (evt) {
		//disable selection
		zk(document.body).disableSelection(); // Bug #1820433
		jq.clearSelection(); // Bug #2721980
		if (this.opts.stackup) { // Bug #1911280
			var stackup = document.createElement("DIV");
			document.body.appendChild(stackup);
			stackup.className = "z-dd-stackup";
			zk(stackup).disableSelection();
			var st = (this.stackup = stackup).style;
			st.width = jq.px0(jq.pageWidth());
			st.height = jq.px0(jq.pageHeight());
		}
		zk.dragging = this.dragging = true;

		var node = this.node;
		if(this.opts.ghosting)
			if (typeof this.opts.ghosting == 'function') {
				this.delta = this._currentDelta();
				this.orgnode = this.node;

				var $node = zk(this.node),
					ofs = $node.cmOffset();
				this.z_scrl = $node.scrollOffset();
				this.z_scrl[0] -= jq.innerX(); this.z_scrl[1] -= jq.innerY();
					//Store scrolling offset since _draw not handle DIV well
				ofs[0] -= this.z_scrl[0]; ofs[1] -= this.z_scrl[1];

				node = this.node = this.opts.ghosting(this, ofs, evt);
			} else {
				this._clone = jq(node).clone()[0];
				this.z_orgpos = node.style.position; //Bug 1514789
				if (this.z_orgpos != 'absolute')
					jq(node).absolutize();
				node.parentNode.insertBefore(this._clone, node);
			}

		if (this.opts.stackup) {
			if (zk(_stackup).isVisible()) //in use
				this._stackup = jq.newStackup(node, node.id + '-ddstk');
			else {
				this._stackup = _stackup;
				this._syncStackup();
				node.parentNode.insertBefore(_stackup, node);
			}
		}

		if(this.opts.zIndex) { //after ghosting
			this.orgZ = zk.parseInt(jq(node).css('z-index'));
			node.style.zIndex = this.opts.zIndex;
		}

		if(this.opts.scroll) {
			if (this.opts.scroll == window) {
				var where = this._getWndScroll(this.opts.scroll);
				this.orgScrlLeft = where.left;
				this.orgScrlTop = where.top;
			} else {
				this.orgScrlLeft = this.opts.scroll.scrollLeft;
				this.orgScrlTop = this.opts.scroll.scrollTop;
			}
		}

		if(this.opts.starteffect)
			this.opts.starteffect(this, evt);
	},
	_syncStackup: function () {
		if (this._stackup) {
			var node = this.node,
				st = this._stackup.style;
			st.display = 'block';
			st.left = node.offsetLeft + "px";
			st.top = node.offsetTop + "px";
			st.width = node.offsetWidth + "px";
			st.height = node.offsetHeight + "px";
		}
	},

	_updateDrag: function (pt, evt) {
		if(!this.dragging) {
			var v = this.opts.initSensitivity;
			if (v && (pt[0] <= _initPt[0] + v
			&& pt[0] >= _initPt[0] - v
			&& pt[1] <= _initPt[1] + v
			&& pt[1] >= _initPt[1] - v))
				return;
			this._startDrag(evt);
		}
		this._updateInnerOfs();

		this._draw(pt, evt);
		if (this.opts.change) this.opts.change(this, pt, evt);
		this._syncStackup();

		if(this.opts.scroll) {
			this._stopScrolling();

			var p;
			if (this.opts.scroll == window) {
				var o = this._getWndScroll(this.opts.scroll);
				p = [o.left, o.top, o.left + o.width, o.top + o.height];
			} else {
				p = zk(this.opts.scroll).viewportOffset();
				p[0] += this.opts.scroll.scrollLeft + this._innerOfs[0];
				p[1] += this.opts.scroll.scrollTop + this._innerOfs[1];
				p.push(p[0]+this.opts.scroll.offsetWidth);
				p.push(p[1]+this.opts.scroll.offsetHeight);
			}

			var speed = [0,0],
				v = this.opts.scrollSensitivity;
			if(pt[0] < (p[0]+v)) speed[0] = pt[0]-(p[0]+v);
			if(pt[1] < (p[1]+v)) speed[1] = pt[1]-(p[1]+v);
			if(pt[0] > (p[2]-v)) speed[0] = pt[0]-(p[2]-v);
			if(pt[1] > (p[3]-v)) speed[1] = pt[1]-(p[3]-v);
			this._startScrolling(speed);
		}

		// fix AppleWebKit rendering
		if(navigator.appVersion.indexOf('AppleWebKit')>0) window.scrollBy(0,0);

		evt.stop();
	},

	_finishDrag: function (evt, success) {
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

		var node = this.node;
		if(this.opts.ghosting)
			if (typeof this.opts.ghosting == 'function') {
				if (this.opts.endghosting)
					this.opts.endghosting(this, this.orgnode);
				if (node != this.orgnode) {
					jq(node).remove();
					this.node = this.orgnode;
				}
				delete this.orgnode;
			} else {
				if (this.z_orgpos != "absolute") { //Bug 1514789
					zk(this.node).relativize();
					node.style.position = this.z_orgpos;
				}
				jq(this._clone).remove();
				this._clone = null;
			}

		var pt = [evt.pageX, evt.pageY];
		var revert = this.opts.revert;
		if(revert && typeof revert == 'function')
			revert = revert(this, pt, evt);

		var d = this._currentDelta(),
			d2 = this.delta;
		if(revert && this.opts.reverteffect) {
			this.opts.reverteffect(this,
				[d[0]-this.delta[0], d[1]-this.delta[1]]);
		} else {
			this.delta = d;
		}

		if(this.opts.zIndex)
			node.style.zIndex = this.orgZ;

		if(this.opts.endeffect) 
			this.opts.endeffect(this, evt);

		var wgt = this.control;
		if (this.opts.fireOnMove && zk.Widget.isInstance(wgt)) {
			if (d[0] != d2[0] || d[1] != d2[1]) {
				wgt.fire('onMove', zk.copy({
					left: node.style.left,
					top: node.style.top
				}, evt.data), {ignorable: true});
			}
		}
		_deactivate(this);
		setTimeout(function(){zk.dragging=false;}, 0);
			//we have to reset it later since event is fired later (after onmouseup)
	},

	_mousedown: function (devt) {
		var node = this.node,
			evt = jq.Event.zk(devt);
		if(_dragging[node] || evt.which != 1)
			return;

		var pt = [evt.pageX, evt.pageY];
		if (this.opts.ignoredrag && this.opts.ignoredrag(this, pt, evt)) {
			if (evt.domStopped) devt.stop();
			return;
		}

		var pos = zk(node).cmOffset();
		this.offset = [pt[0] - pos[0], pt[1] - pos[1]];

		_activate(this, devt, pt);
		if (!zk.ie) devt.stop();
			//test/dragdrop.zul
			//IE: if stop, onclick won't be fired in IE (unable to select)
			//FF3: if not stop, IMG cannot be dragged
			//Opera: if not stop, 'easy' to become selecting text
	},
	_keypress: function (devt) {
		if(devt.keyCode == 27) {
			this._finishDrag(jq.Event.zk(devt), false);
			devt.stop();
		}
	},

	_endDrag: function (evt) {
		if(this.dragging) {
			this._stopScrolling();
			this._finishDrag(evt, true);
			evt.stop();
		} else
			_deactivate(this);
	},

	_draw: function (point, evt) {
		var node = this.node,
			$node = zk(node),
			pos = $node.cmOffset();
		if(this.opts.ghosting) {
			var r = $node.scrollOffset();
			pos[0] += r[0] - this._innerOfs[0]; pos[1] += r[1] - this._innerOfs[1];
		}

		var d = this._currentDelta();
		pos[0] -= d[0]; pos[1] -= d[1];

		if(this.opts.scroll && (this.opts.scroll != window && this._isScrollChild)) {
			pos[0] -= this.opts.scroll.scrollLeft-this.orgScrlLeft;
			pos[1] -= this.opts.scroll.scrollTop-this.orgScrlTop;
		}

		var p = [point[0]-pos[0]-this.offset[0],
			point[1]-pos[1]-this.offset[1]];

		if(this.opts.snap)
			if(typeof this.opts.snap == 'function') {
				p = this.opts.snap(this, p);
			} else {
				if(this.opts.snap instanceof Array) {
					p = [Math.round(p[0]/this.opts.snap[0])*this.opts.snap[0],
						Math.round(p[1]/this.opts.snap[1])*this.opts.snap[1]];
				} else {
					p = [Math.round(p[0]/this.opts.snap)*this.opts.snap,
						Math.round(p[1]/this.opts.snap)*this.opts.snap];
				}
			}

		//Resolve scrolling offset when DIV is used
		if (this.z_scrl) {
			p[0] -= this.z_scrl[0]; p[1] -= this.z_scrl[1];
		}

		var style = node.style;
		if (typeof this.opts.draw == 'function') {
			this.opts.draw(this, p, evt);
		} else if (typeof this.opts.constraint == 'function') {
			var np = this.opts.constraint(this, p, evt); //return null or [newx, newy]
			if (np) p = np;
			style.left = jq.px(p[0]);
			style.top  = jq.px(p[1]);
		} else {
			if((!this.opts.constraint) || (this.opts.constraint=='horizontal'))
				style.left = jq.px(p[0]);
			if((!this.opts.constraint) || (this.opts.constraint=='vertical'))
				style.top  = jq.px(p[1]);
		}

		if(style.visibility=="hidden") style.visibility = ""; // fix gecko rendering
	},

	_stopScrolling: function () {
		if(this.scrollInterval) {
			clearInterval(this.scrollInterval);
			this.scrollInterval = null;
			_lastScrlPt = null;
		}
	},
	_startScrolling: function (speed) {
		if(speed[0] || speed[1]) {
			this.scrollSpeed = [speed[0]*this.opts.scrollSpeed,speed[1]*this.opts.scrollSpeed];
			this.lastScrolled = new Date();
			this.scrollInterval = setInterval(this.proxy(this._scroll), 10);
		}
	},

	_scroll: function () {
		var current = new Date(),
			delta = current - this.lastScrolled;
		this.lastScrolled = current;
		if(this.opts.scroll == window) {
			if (this.scrollSpeed[0] || this.scrollSpeed[1]) {
				var o = this._getWndScroll(this.opts.scroll),
					d = delta / 1000;
				this.opts.scroll.scrollTo(o.left + d*this.scrollSpeed[0],
					o.top + d*this.scrollSpeed[1]);
			}
		} else {
			this.opts.scroll.scrollLeft += this.scrollSpeed[0] * delta / 1000;
			this.opts.scroll.scrollTop  += this.scrollSpeed[1] * delta / 1000;
		}

		this._updateInnerOfs();
		if (this._isScrollChild) {
			_lastScrlPt = _lastScrlPt || _lastPt;
			_lastScrlPt[0] += this.scrollSpeed[0] * delta / 1000;
			_lastScrlPt[1] += this.scrollSpeed[1] * delta / 1000;
			if (_lastScrlPt[0] < 0)
				_lastScrlPt[0] = 0;
			if (_lastScrlPt[1] < 0)
				_lastScrlPt[1] = 0;
			this._draw(_lastScrlPt);
		}

		if(this.opts.change) {
			var devt = window.event ? jq.event.fix(window.event): null,
				evt = devt ? jq.Event.zk(devt): null;
			this.opts.change(this,
				evt ? [evt.pageX, evt.pageY]: _lastPt, evt);
		}
	},

	_updateInnerOfs: function () {
		this._innerOfs = [jq.innerX(), jq.innerY()];
	},
	_getWndScroll: function (w) {
		var T, L, W, H,
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
			H = doc.body.offsetHeight
		}
		return {top: T, left: L, width: W, height: H};
	}

},{//static
	ignoreMouseUp: function () { //called by mount
		return zk.dragging ? true: _initEvt;
	},
	ignoreClick: function () { //called by mount
		return zk.dragging;
	}
});
})();
