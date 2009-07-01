/* drag.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 10 11:06:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

script.aculo.us dragdrop.js v1.7.0,
Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
	(c) 2005, 2006 Sammi Williams (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz)

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.Draggable = zk.$extends(zk.Object, {
	$init: function (control, node, opts) {
		var Drag = zk.Draggable;
		if (!Drag._stackup) {
		//IE: if we don't insert stackup at beginning, dragging is slow
			var n = Drag._stackup = jq.newStackup(null, 'z_ddstkup');
			jq(n).hide();
			document.body.appendChild(n);
		}

		this.control = control;
		this.node = node = node ? jq(node, zk)[0]: control.node || control.getNode();
		if (!node)
			throw "Handle required for "+control;

		opts = zk.$default(opts, {
			zIndex: 1000,
			scrollSensitivity: 20,
			scrollSpeed: 15,
			initSensitivity: 3,
			delay: 0
		});

		if (opts.reverteffect == null)
			opts.reverteffect = Drag._defRevertEffect;
		if (opts.endeffect == null) {
			opts.endeffect = Drag._defEndEffect;
			if (opts.starteffect == null)
				opts.starteffect = Drag._defStartEffect;
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

		Drag._register(this);
	},
	destroy: function () {
		jq(this.handle).unbind("mousedown", this.proxy(this._mousedown));
		zk.Draggable._unregister(this);
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
			st.width = jq.pageWidth() + "px";
			st.height = jq.pageHeight() + "px";
		}
		zk.dragging = this.dragging = true;

		var node = this.node,
			Drag = zk.Draggable;
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
				this._clone = zk(node).cloneNode(true);
				this.z_orgpos = node.style.position; //Bug 1514789
				if (this.z_orgpos != 'absolute')
					jq(node).absolutize();
				node.parentNode.insertBefore(this._clone, node);
			}

		if (this.opts.stackup) {
			var defStackup = Drag._stackup;
			if (zk(defStackup).isVisible()) //in use
				this._stackup = jq.newStackup(node, node.id + '-ddstk');
			else {
				this._stackup = defStackup;
				this._syncStackup();
				node.parentNode.insertBefore(this._stackup, node);
			}
		}

		if(this.opts.zIndex) { //after ghosting
			this.originalZ = zk.parseInt(jq(node).css('z-index'));
			node.style.zIndex = this.opts.zIndex;
		}

		if(this.opts.scroll) {
			if (this.opts.scroll == window) {
				var where = this._getWindowScroll(this.opts.scroll);
				this.originalScrollLeft = where.left;
				this.originalScrollTop = where.top;
			} else {
				this.originalScrollLeft = this.opts.scroll.scrollLeft;
				this.originalScrollTop = this.opts.scroll.scrollTop;
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
		var Drag = zk.Draggable;
		if(!this.dragging) {
			var v = this.opts.initSensitivity
			if (v && (pt[0] <= Drag._initPt[0] + v
			&& pt[0] >= Drag._initPt[0] - v
			&& pt[1] <= Drag._initPt[1] + v
			&& pt[1] >= Drag._initPt[1] - v))
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
				with(this._getWindowScroll(this.opts.scroll)) { p = [ left, top, left+width, top+height ];}
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
			if (stackup == zk.Draggable._stackup) jq(stackup).hide();
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

		var d = this._currentDelta();
		if(revert && this.opts.reverteffect) {
			this.opts.reverteffect(this,
				[d[0]-this.delta[0], d[1]-this.delta[1]]);
		} else {
			this.delta = d;
		}

		if(this.opts.zIndex)
			node.style.zIndex = this.originalZ;

		if(this.opts.endeffect) 
			this.opts.endeffect(this, evt);

		zk.Draggable._deactivate(this);
		setTimeout("zk.dragging=false", 0);
			//we have to reset it later since event is fired later (after onmouseup)
	},

	_mousedown: function (devt) {
		var node = this.node,
			Drag = zk.Draggable,
			evt = jq.event.toEvent(devt);
		if(Drag._dragging[node] || evt.which != 1)
			return;

		// abort on form elements, fixes a Firefox issue
		var target = evt.domTarget,
			tag = target.tagName;
		if(tag=='INPUT' || tag=='SELECT' || tag=='OPTION' || tag=='BUTTON' || tag=='TEXTAREA')
			return;

		//Skip popup/dropdown (of combobox and others)
		for (var n = target; n && n != node; n = n.parentNode)
			if (jq(n).css('position') == 'absolute')
				return;

		var pt = [evt.pageX, evt.pageY];
		if (this.opts.ignoredrag && this.opts.ignoredrag(this, pt, evt)) {
			if (evt.domStopped) devt.stop();
			return;
		}

		var pos = zk(node).cmOffset();
		this.offset = [pt[0] - pos[0], pt[1] - pos[1]];

		Drag._activate(this, devt, pt);
		devt.stop();
	},
	_keypress: function (devt) {
		if(devt.keyCode == 27) {
			this._finishDrag(jq.event.toEvent(devt), false);
			devt.stop();
		}
	},

	_endDrag: function (evt) {
		if(this.dragging) {
			this._stopScrolling();
			this._finishDrag(evt, true);
			evt.stop();
		} else
			zk.Draggable._deactivate(this);
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
			pos[0] -= this.opts.scroll.scrollLeft-this.originalScrollLeft;
			pos[1] -= this.opts.scroll.scrollTop-this.originalScrollTop;
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
			style.left = p[0] + "px";
			style.top  = p[1] + "px";
		} else {
			if((!this.opts.constraint) || (this.opts.constraint=='horizontal'))
				style.left = p[0] + "px";
			if((!this.opts.constraint) || (this.opts.constraint=='vertical'))
				style.top  = p[1] + "px";
		}

		if(style.visibility=="hidden") style.visibility = ""; // fix gecko rendering
	},

	_stopScrolling: function () {
		if(this.scrollInterval) {
			clearInterval(this.scrollInterval);
			this.scrollInterval = null;
			zk.Draggable._lastScrlPt = null;
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
		var Drag = zk.Draggable,
			current = new Date(),
			delta = current - this.lastScrolled;
		this.lastScrolled = current;
		if(this.opts.scroll == window) {
			with (this._getWindowScroll(this.opts.scroll)) {
				if (this.scrollSpeed[0] || this.scrollSpeed[1]) {
				  var d = delta / 1000;
				  this.opts.scroll.scrollTo( left + d*this.scrollSpeed[0], top + d*this.scrollSpeed[1] );
				}
			}
		} else {
			this.opts.scroll.scrollLeft += this.scrollSpeed[0] * delta / 1000;
			this.opts.scroll.scrollTop  += this.scrollSpeed[1] * delta / 1000;
		}

		this._updateInnerOfs();
		if (this._isScrollChild) {
			Drag._lastScrlPt = Drag._lastScrlPt || Drag._lastPt;
			Drag._lastScrlPt[0] += this.scrollSpeed[0] * delta / 1000;
			Drag._lastScrlPt[1] += this.scrollSpeed[1] * delta / 1000;
			if (Drag._lastScrlPt[0] < 0)
				Drag._lastScrlPt[0] = 0;
			if (Drag._lastScrlPt[1] < 0)
				Drag._lastScrlPt[1] = 0;
			this._draw(Drag._lastScrlPt);
		}

		if(this.opts.change) {
			var devt = window.event ? jq.event.fix(window.event): null,
				evt = devt ? jq.event.toEvent(devt): null;
			this.opts.change(this,
				evt ? [evt.pageX, evt.pageY]: Drag._lastPt, evt);
		}
	},

	_updateInnerOfs: function () {
		this._innerOfs = [jq.innerX(), jq.innerY()];
	},
	_getWindowScroll: function (w) {
		var T, L, W, H;
		with (w.document) {
			if (w.document.documentElement && documentElement.scrollTop) {
				T = documentElement.scrollTop;
				L = documentElement.scrollLeft;
			} else if (w.document.body) {
				T = body.scrollTop;
				L = body.scrollLeft;
			}
			if (w.innerWidth) {
				W = w.innerWidth;
				H = w.innerHeight;
			} else if (w.document.documentElement && documentElement.clientWidth) {
				W = documentElement.clientWidth;
				H = documentElement.clientHeight;
			} else {
				W = body.offsetWidth;
				H = body.offsetHeight
			}
		}
		return {top: T, left: L, width: W, height: H};
	}

},{ //static
	_drags: [],
	_dragging: [],

	_register: function (dg) {
		var Drag = zk.Draggable;
		if(Drag._drags.length == 0)
			jq(document).mouseup(Drag._docmouseup)
				.mousemove(Drag._docmousemove)
				.keypress(Drag._dockeypress);
		Drag._drags.push(dg);
	},
	_unregister: function (dg) {
		var Drag = zk.Draggable;
		Drag._drags.$remove(dg);
		if(Drag._drags.length == 0)
			jq(document).unbind("mouseup", Drag._docmouseup)
				.unbind("mousemove", Drag._docmousemove)
				.unbind("keypress", Drag._dockeypress);
		if (Drag.activedg == dg) //just in case
			Drag.activedg = null;
	},

	_activate: function (dg, devt, pt) {
		var Drag = zk.Draggable;
		Drag._timeout = setTimeout(function () { 
			Drag._timeout = null; 
			Drag.activedg = dg; 
		}, dg.opts.delay);
		Drag._initPt = pt;
		Drag._initEvt = jq.event.toEvent(devt, dg.control);
	},
	_deactivate: function () {
		zk.Draggable.activedg = null;
		setTimeout("zk.Draggable._initEvt=null", 0);
	},

	ignoreMouseUp: function () { //called by mount
		return zk.dragging ? true: zk.Draggable._initEvt;
	},
	ignoreClick: function () { //called by mount
		return zk.dragging;
	},

	_docmousemove: function (devt) {
		var Drag = zk.Draggable,
			dg = Drag.activedg;
		if(!dg || dg.dead) return;

		var evt = jq.event.toEvent(devt),
			pt = [evt.pageX, evt.pageY];
		// Mozilla-based browsers fire successive mousemove events with
		// the same coordinates, prevent needless redrawing (moz bug?)
		if(Drag._lastPt && Drag._lastPt[0] == pt [0]
		&& Drag._lastPt[1] == pt [1])
			return;

		Drag._lastPt = pt;
		dg._updateDrag(pt, evt);
		if (evt.domStopped) devt.stop();
	},
	_docmouseup: function (devt) {
		var Drag = zk.Draggable,
			dg = Drag.activedg;
		if(Drag._timeout) { 
			clearTimeout(Drag._timeout); 
			Drag._timeout = null; 
		}
		if(!dg) return;

		Drag._lastPt = null;
		var evt;
		dg._endDrag(evt = jq.event.toEvent(devt));
		Drag.activedg = null;
		if (evt.domStopped) devt.stop();
	},
	_dockeypress: function (devt) {
		var Drag = zk.Draggable,
			dg = Drag.activedg;
		if(dg) dg._keypress(devt);
	},

	//default effect//
	_defStartEffect: function (dg) {
		var node = dg.node;
		node._$opacity = jq(node).css('opacity');
		zk.Draggable._dragging[node] = true;
		new zk.eff.Opacity(node, {duration:0.2, from:node._$opacity, to:0.7}); 
	},
	_defEndEffect: function (dg) {
		var node = dg.node,
			toOpacity = typeof node._$opacity == 'number' ? node._$opacity : 1.0;
		new zk.eff.Opacity(node, {duration:0.2, from:0.7,
			to:toOpacity, queue: {scope:'_draggable', position:'end'},
			afterFinish: function () { 
				zk.Draggable._dragging[node] = false;
			}
		});
	},
	_defRevertEffect: function (dg, offset) {
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
});
