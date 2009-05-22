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
			var n = Drag._stackup = zDom.newStackup(null, 'z_ddstkup');
			zDom.hide(n);
			document.body.appendChild(n);
		}

		this.control = control;
		this.node = node = node ? zDom.$(node): control.node || control.getNode();
		if (!node)
			throw "Handle required for "+control;

		opts = zk.$default(opts, {
			zIndex: 1000,
			scrollSensitivity: 20,
			scrollSpeed: 15,
			delay: 0
		});

		if (opts.reverteffect == null)
			opts.reverteffect = Drag._defRevertEffect;
		if (opts.endeffect == null) {
			opts.endeffect = Drag._defEndEffect;
			if (opts.starteffect == null)
				opts.starteffect = Drag._defStartEffect;
		}

		if(opts.handle) this.handle = zDom.$(opts.handle);
		if(!this.handle) this.handle = node;

		if(opts.scroll && !opts.scroll.scrollTo && !opts.scroll.outerHTML) {
			opts.scroll = zDom.$(opts.scroll);
			this._isScrollChild = zUtl.isAncestor(opts.scroll, node);
		}

		this.delta = this._currentDelta();
		this.opts = opts;
		this.dragging = false;   

		zEvt.listen(this.handle, "mousedown", this.proxy(this._mousedown));

		Drag._register(this);
	},
	destroy: function () {
		zEvt.unlisten(this.handle, "mousedown", this.proxy(this._mousedown));
		zk.Draggable._unregister(this);
		this.node = this.control = this.handle = null;
		this.dead = true;
	},

	/** [left, right] of this node. */
	_currentDelta: function () {
		return [zk.parseInt(zDom.getStyle(this.node, 'left')),
			zk.parseInt(zDom.getStyle(this.node, 'top'))];
	},

	_startDrag: function (evt) {
		//disable selection
		zDom.disableSelection(document.body); // Bug #1820433
		zDom.clearSelection(); // Bug #2721980
		if (this.opts.stackup) { // Bug #1911280
			this.stackup = document.createElement("DIV");
			document.body.appendChild(this.stackup);
			this.stackup = zDom.setOuterHTML(this.stackup,
				'<div class="z-dd-stackup"></div>');
			zDom.disableSelection(this.stackup);
			var st = this.stackup.style;
			st.width = zDom.pageWidth() + "px";
			st.height = zDom.pageHeight() + "px";
		}
		zk.dragging = this.dragging = true;

		var node = this.node,
			Drag = zk.Draggable;
		if(this.opts.ghosting)
			if (typeof this.opts.ghosting == 'function') {
				this.delta = this._currentDelta();
				this.orgnode = this.node;

				var ofs = zDom.cmOffset(this.node);
				this.z_scrl = zDom.scrollOffset(this.node);
				this.z_scrl[0] -= zDom.innerX(); this.z_scrl[1] -= zDom.innerY();
					//Store scrolling offset since _draw not handle DIV well
				ofs[0] -= this.z_scrl[0]; ofs[1] -= this.z_scrl[1];

				node = this.node = this.opts.ghosting(this, ofs, evt);
			} else {
				this._clone = node.cloneNode(true);
				this.z_orgpos = node.style.position; //Bug 1514789
				if (this.z_orgpos != 'absolute')
					zDom.absolutize(node);
				node.parentNode.insertBefore(this._clone, node);
			}

		if (this.opts.stackup) {
			var defStackup = Drag._stackup;
			if (zDom.isVisible(defStackup)) //in use
				this._stackup = zDom.newStackup(node, node.id + '$ddstk');
			else {
				this._stackup = defStackup;
				this._syncStackup();
				node.parentNode.insertBefore(this._stackup, node);
			}
		}

		if(this.opts.zIndex) { //after ghosting
			this.originalZ = zk.parseInt(zDom.getStyle(node, 'z-index'));
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

	_updateDrag: function (pointer, evt) {
		if(!this.dragging) this._startDrag(evt);
		this._updateInnerOfs();

		this._draw(pointer, evt);
		if (this.opts.change) this.opts.change(this, pointer, evt);
		this._syncStackup();

		if(this.opts.scroll) {
			this._stopScrolling();

			var p;
			if (this.opts.scroll == window) {
				with(this._getWindowScroll(this.opts.scroll)) { p = [ left, top, left+width, top+height ];}
			} else {
				p = zDom.viewportOffset(this.opts.scroll);
				p[0] += this.opts.scroll.scrollLeft + this._innerOfs[0];
				p[1] += this.opts.scroll.scrollTop + this._innerOfs[1];
				p.push(p[0]+this.opts.scroll.offsetWidth);
				p.push(p[1]+this.opts.scroll.offsetHeight);
			}

			var speed = [0,0];
			if(pointer[0] < (p[0]+this.opts.scrollSensitivity)) speed[0] = pointer[0]-(p[0]+this.opts.scrollSensitivity);
			if(pointer[1] < (p[1]+this.opts.scrollSensitivity)) speed[1] = pointer[1]-(p[1]+this.opts.scrollSensitivity);
			if(pointer[0] > (p[2]-this.opts.scrollSensitivity)) speed[0] = pointer[0]-(p[2]-this.opts.scrollSensitivity);
			if(pointer[1] > (p[3]-this.opts.scrollSensitivity)) speed[1] = pointer[1]-(p[3]-this.opts.scrollSensitivity);
			this._startScrolling(speed);
		}

		// fix AppleWebKit rendering
		if(navigator.appVersion.indexOf('AppleWebKit')>0) window.scrollBy(0,0);

		evt.stop();
	},

	_finishDrag: function (evt, success) {
		this.dragging = false;
		if (this.stackup) {
			zDom.remove(this.stackup);
			delete this.stackup;
		}

		//enable selection back and clear selection if any
		zDom.enableSelection(document.body);
		setTimeout(zDom.clearSelection, 0);

		var stackup = this._stackup;
		if (stackup) {
			if (stackup == zk.Draggable._stackup) zDom.hide(stackup);
			else zDom.remove(stackup);
			delete this._stackup;
		}

		var node = this.node;
		if(this.opts.ghosting)
			if (typeof this.opts.ghosting == 'function') {
				if (this.opts.endghosting)
					this.opts.endghosting(this, this.orgnode);
				if (this.node != this.orgnode) {
					zDom.remove(this.node);
					this.node = this.orgnode;
				}
				delete this.orgnode;
			} else {
				if (this.z_orgpos != "absolute") { //Bug 1514789
					zDom.relativize(node);
					node.style.position = this.z_orgpos;
				}
				zDom.remove(this._clone);
				this._clone = null;
			}

		var pointer = [evt.pageX, evt.pageY];
		var revert = this.opts.revert;
		if(revert && typeof revert == 'function')
			revert = revert(this, pointer, evt);

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
			evt = zEvt.toEvent(devt);
		if(Drag._dragging[node] || !evt.leftClick)
			return;

		// abort on form elements, fixes a Firefox issue
		var target = evt.domTarget
			tag = zDom.tag(target);
		if(tag=='INPUT' || tag=='SELECT' || tag=='OPTION' || tag=='BUTTON' || tag=='TEXTAREA')
			return;

		//Skip popup/dropdown (of combobox and others)
		for (var n = target; n && n != node; n = n.parentNode)
			if (zDom.getStyle(n, 'position') == 'absolute')
				return;

		var pointer = [evt.pageX, evt.pageY];
		if (this.opts.ignoredrag && this.opts.ignoredrag(this, pointer, evt)) {
			if (evt.domStopped) zEvt.stop(devt);
			return;
		}

		var pos = zDom.cmOffset(node);
		this.offset = [pointer[0] - pos[0], pointer[1] - pos[1]];

		Drag._activate(this);

		//Bug 1845026
		//We need to ensure that the onBlur evt is fired before the onSelect evt for consistent among four browsers. 
		if (zk.currentFocus) {
			var f = zk.currentFocus.getNode();
			if (f && target != f && typeof f.blur == "function") {
				f.blur();
				if (this.dead) return;
			}
		}

		zEvt.stop(devt);

		var c = this.control;
		if (c && !c.$instanceof(zk.Widget)) c = null;
		zk.Widget._domMouseDown(c); //since event is stopped
	},
	_keypress: function (devt) {
		if(zEvt.keyCode(devt) == zEvt.ESC) {
			this._finishDrag(zEvt.toEvent(devt), false);
			zEvt.stop(devt);
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
			pos = zDom.cmOffset(node);
		if(this.opts.ghosting) {
			var r = zDom.scrollOffset(node);
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
			var devt = window.event,
				evt = devt ? zEvt.toEvent(devt): null;
			this.opts.change(this,
				evt ? [evt.pageX, evt.pageY]: Drag._lastPt, evt);
		}
	},

	_updateInnerOfs: function () {
		this._innerOfs = [zDom.innerX(), zDom.innerY()];
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
		if(Drag._drags.length == 0) {
			zEvt.listen(document, "mouseup", Drag._docmouseup);
			zEvt.listen(document, "mousemove", Drag._docmousemove);
			zEvt.listen(document, "keypress", Drag._dockeypress);
		}
		Drag._drags.push(dg);
	},
	_unregister: function (dg) {
		var Drag = zk.Draggable;
		Drag._drags.$remove(dg);
		if(Drag._drags.length == 0) {
			zEvt.unlisten(document, "mouseup", Drag._docmouseup);
			zEvt.unlisten(document, "mousemove", Drag._docmousemove);
			zEvt.unlisten(document, "keypress", Drag._dockeypress);
		}
		if (Drag.activedg == dg) //just in case
			Drag.activedg = null;
	},

	_activate: function (dg) {
		var Drag = zk.Draggable;
		if(zk.opera || dg.opts.delay) { 
			Drag._timeout = setTimeout(function () { 
				zk.Draggable._timeout = null; 
				window.focus(); 
				zk.Draggable.activedg = dg; 
			}, dg.opts.delay); 
		} else {
			window.focus(); // allows keypress events if window isn't currently focused, fails for Safari
			Drag.activedg = dg;
		}
	},
	_deactivate: function () {
		zk.Draggable.activedg = null;
	},

	_docmousemove: function (devt) {
		var Drag = zk.Draggable,
			dg = Drag.activedg;
		if(!dg || dg.dead) return;

		var evt = zEvt.toEvent(devt),
			pointer = [evt.pageX, evt.pageY];
		// Mozilla-based browsers fire successive mousemove events with
		// the same coordinates, prevent needless redrawing (moz bug?)
		if(Drag._lastPt && Drag._lastPt[0] == pointer [0]
		&& Drag._lastPt[1] == pointer [1])
			return;

		Drag._lastPt = pointer;
		dg._updateDrag(pointer, evt);
		if (evt.domStopped) zEvt.stop(devt);
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
		dg._endDrag(evt = zEvt.toEvent(devt));
		Drag.activedg = null;
		if (evt.domStopped) zEvt.stop(devt);
	},
	_dockeypress: function (devt) {
		var Drag = zk.Draggable,
			dg = Drag.activedg;
		if(dg) dg._keypress(devt);
	},

	//default effect//
	_defStartEffect: function (dg) {
		var node = dg.node;
		node._$opacity = zDom.getStyle(node, 'opacity');
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
