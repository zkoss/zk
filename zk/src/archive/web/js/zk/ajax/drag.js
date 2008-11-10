/* dragdrop.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 10 11:06:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

script.aculo.us dragdrop.js v1.7.0,
Copyright (c) 2005, 2006 Thomas Fuchs (http://script.aculo.us, http://mir.aculo.us)
	(c) 2005, 2006 Sammi Williams (http://www.oriontransfer.co.nz, sammi@oriontransfer.co.nz)

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.Draggable = zk.$extends(zk.Object, {
	$init: function(node, opts) {
		this.node = node = zDom.$(node);

		opts = zk.$default(opts, {
			handle: false,
			zIndex: 1000,
			revert: false,
			scroll: false,
			scrollSensitivity: 20,
			scrollSpeed: 15,
			snap: false, //false, or xy or [x,y] or function(x,y){ return [x,y] }
			delay: 0,
			overlay: false
		});

		if (opts.reverteffect == null)
			opts.reverteffect = function(node, top_offset, left_offset) {
				var orgpos = node.style.position, //Bug 1538506
					dur = Math.sqrt(Math.abs(top_offset^2)+Math.abs(left_offset^2))*0.02;
				new zk.Effect.move(node, { x: -left_offset, y: -top_offset,
					duration: dur, queue: {scope:'_draggable', position:'end'}});

				//Bug 1538506: a strange bar appear in IE
				setTimeout(function () {
					zk.Dragdrop.restorePosition(node, orgpos);
				}, dur * 1000 + 10);
			};
		if (opts.endeffect == null) {
			opts.endeffect = function(node) {
				var toOpacity = typeof node._opacity == 'number' ? node._opacity : 1.0;
				new zk.Effect.opacity(node, {duration:0.2, from:0.7,
					to:toOpacity, queue: {scope:'_draggable', position:'end'},
					afterFinish: function () { 
						zk.Draggable._dragging[node] = false;
					}
				});
			};
			if (opts.starteffect == null)
				opts.starteffect = function (node) {
					node._opacity = zDom.getOpacity(node);
					zk.Draggable._dragging[node] = true;
					new zk.Effect.opacity(node, {duration:0.2, from:node._opacity, to:0.7}); 
				}
		}


		if(opts.handle) this.handle = zDom.$(opts.handle);
		if(!this.handle) this.handle = this.node;

		if(opts.scroll && !opts.scroll.scrollTo && !opts.scroll.outerHTML) {
			opts.scroll = zDom.$(opts.scroll);
			this._isScrollChild = zUtl.isAncestor(opts.scroll, this.node);
		}

		this.delta = this._currentDelta();
		this.opts = opts;
		this.dragging = false;   

		this.handle.z_draggable = this;
		zEvt.listen(this.handle, "mousedown", zk.Draggable.onMouseDown);

		zk.Draggable.register(this);
	},
	/** Destroys this draggable object. */
	destroy: function() {
		this.handle.z_draggable = null;
		zEvt.unlisten(this.handle, "mousedown", zk.Draggable.onMouseDown);
		zk.Draggable.unregister(this);
	},

	/** [left, right] of this node. */
	_currentDelta: function() {
		return [zk.parseInt(zDom.getStyle(this.node,'left')),
			zk.parseInt(zDom.getStyle(this.node,'top'))];
	},

	startDrag: function(evt) {
		//disable selection
		zk.disableSelection(document.body); // Bug #1820433
		if (this.opts.overlay) { // Bug #1911280
			this.domOverlay = document.createElement("DIV");
			document.body.appendChild(this.domOverlay);
			zk.setOuterHTML(this.domOverlay, '<div class="z-dd-overlay" id="zk_dd_overlay"></div>');
			this.domOverlay = zDom.$("zk_dd_overlay");
			if (zk.gecko) this.domOverlay.style.MozUserSelect = "none";
			this.domOverlay.style.width = zDom.pageWidth() + "px";
			this.domOverlay.style.height = zDom.pageHeight() + "px";
		}
		this.dragging = true;

		if(this.opts.ghosting) {
			var ghosting = true;
			if (typeof this.opts.ghosting == 'function') ghosting = this.opts.ghosting(this, true, evt);
			if (ghosting) {
				this._clone = this.node.cloneNode(true);
				this.z_orgpos = this.node.style.position; //Bug 1514789
				if (this.z_orgpos != 'absolute')
					zDom.absolutize(this.node);
				this.node.parentNode.insertBefore(this._clone, this.node);
			}
		}

		if(this.opts.zIndex) { //after ghosting
			this.originalZ = zk.parseInt(zDom.getStyle(this.node,'z-index'));
			this.node.style.zIndex = this.opts.zIndex;
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
			this.opts.starteffect(this.node, this.handle);
	},

	updateDrag: function(evt, pointer) {
		if(!this.dragging) this.startDrag(evt);
		this._updateInnerOfs();

		this.draw(pointer, evt);
		if(this.opts.change) this.opts.change(this, pointer, evt); //Tom M Yeh, Potix: add pointer

		if(this.opts.scroll) {
			this.stopScrolling();

			var p;
			if (this.opts.scroll == window) {
				with(this._getWindowScroll(this.opts.scroll)) { p = [ left, top, left+width, top+height ]; }
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
			this.startScrolling(speed);
		}

		// fix AppleWebKit rendering
		if(navigator.appVersion.indexOf('AppleWebKit')>0) window.scrollBy(0,0);

		zEvt.stop(evt);
	},

	finishDrag: function(evt, success) {
		this.dragging = false;
		if (this.domOverlay) zDom.remove(this.domOverlay);
		delete this.domOverlay;

		//enable selection back and clear selection if any
		zDom.enableSelection(document.body);
		setTimeout(zDom.clearSelection, 0);
		if(this.opts.ghosting) {
			//Tom M. Yeh: Potix: ghosting is controllable
			var ghosting = true;
			if (typeof this.opts.ghosting == 'function') ghosting = this.opts.ghosting(this, false);
			if (ghosting) {
				if (this.z_orgpos != "absolute") { //Tom M. Yeh, Potix: Bug 1514789
					zDom.relativize(this.node);
					this.node.style.position = this.z_orgpos;
				}
				zDom.remove(this._clone);
				this._clone = null;
			}
		}

		var pointer = [zEvt.x(evt), zEvt.y(evt)]; //Tom M. Yeh, Potix: add pointer
		var revert = this.opts.revert;
		if(revert && typeof revert == 'function')
			revert = revert(this.node, pointer, evt); //Tom M. Yeh, Potix: add pointer

		var d = this._currentDelta();
		if(revert && this.opts.reverteffect) {
			this.opts.reverteffect(this.node, 
			d[1]-this.delta[1], d[0]-this.delta[0]);
		} else {
			this.delta = d;
		}

		if(this.opts.zIndex)
			this.node.style.zIndex = this.originalZ;

		if(this.opts.endeffect) 
			this.opts.endeffect(this.node, evt); //Tom M. Yeh, Potix: add evt

		zk.Draggable.deactivate(this);
	},

	mouseDown: function (evt) {
		if(zk.Draggable._dragging[this.node] || !zEvt.leftClick(evt))
			return;

		// abort on form elements, fixes a Firefox issue
		var src = zEvt.target(evt)
			tag = zDom.tag(src);
		if(tag=='INPUT' || tag=='SELECT' || tag=='OPTION' || tag=='BUTTON' || tag=='TEXTAREA')
			return;

		//Skip popup/dropdown (of combobox and others)
		for (var n = src; n && n != this.node; n = n.parentNode)
			if (zDom.getStyle(n, 'position') == 'absolute')
				return;

		var pointer = [zEvt.x(evt), zEvt.y(evt)];
		if (this.opts.ignoredrag && this.opts.ignoredrag(this.node, pointer, evt))
			return;

		var pos = zDom.cfOffset(this.node);
		this.offset = [pointer[0] - pos[0], pointer[1] - pos[1]];

		zk.Draggable.activate(this);

		//Bug 1845026
		//We need to ensure that the onBlur evt is fired before the onSelect evt for consistent among four browsers. 
		if (zk.currentFocus) {
			var f = zk.currentFocus.node;
			if (f && zEvt.target(evt) != f && typeof f.blur == "function")
				f.blur();
		}

		zEvt.stop(evt);

		//Mousedown is eaten above (so do the default behavior)
		//TODO: zkau.autoZIndex(src, false, true);
	},
	keyPress: function(evt) {
		if(zEvt.keyCode(evt) == zEvt.ESC) {
			this.finishDrag(evt, false);
			zEvt.stop(evt);
		}
	},

	endDrag: function(evt) {
		if(this.dragging) {
			this.stopScrolling();
			this.finishDrag(evt, true);
			zEvt.stop(evt);
		}
	},

	draw: function(point, evt) {
		var pos = zDom.cmOffset(this.node);
		if(this.opts.ghosting) {
			var r = zDom.scrollOffset(this.node);
			pos[0] += r[0] - this._innerOfs[0]; pos[1] += r[1] - this._innerOfs[1];
		}

		var d = this._currentDelta();
		pos[0] -= d[0]; pos[1] -= d[1];

		if(this.opts.scroll && (this.opts.scroll != window && this._isScrollChild)) {
			pos[0] -= this.opts.scroll.scrollLeft-this.originalScrollLeft;
			pos[1] -= this.opts.scroll.scrollTop-this.originalScrollTop;
		}

		var p = [0,1].map(function(i){ 
		return (point[i]-pos[i]-this.offset[i]) 
		}.bind(this));

		if(this.opts.snap)
			if(typeof this.opts.snap == 'function') {
				p = this.opts.snap(p[0],p[1],this);
			} else {
				if(this.opts.snap instanceof Array) {
					p = p.map( function(v, i) {
			  		return Math.round(v/this.opts.snap[i])*this.opts.snap[i] }.bind(this))
				} else {
					p = p.map( function(v) {
					return Math.round(v/this.opts.snap)*this.opts.snap }.bind(this))
				}
			}

		//Resolve scrolling offset when DIV is used
		if (this.z_scrl) {
			p[0] -= this.z_scrl[0]; p[1] -= this.z_scrl[1];
		}

		var style = this.node.style;
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

	stopScrolling: function() {
		if(this.scrollInterval) {
			clearInterval(this.scrollInterval);
			this.scrollInterval = null;
			zk.Draggable._lastScrollPointer = null;
		}
	},
	startScrolling: function(speed) {
		if(speed[0] || speed[1]) {
			this.scrollSpeed = [speed[0]*this.opts.scrollSpeed,speed[1]*this.opts.scrollSpeed];
			this.lastScrolled = new Date();
			this.scrollInterval = setInterval(this.scroll.bind(this), 10);
		}
	},

	scroll: function() {
		var current = new Date();
		var delta = current - this.lastScrolled;
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
			zk.Draggable._lastScrollPointer = zk.Draggable._lastScrollPointer || z$A(zk.Draggable._lastPointer);
			zk.Draggable._lastScrollPointer[0] += this.scrollSpeed[0] * delta / 1000;
			zk.Draggable._lastScrollPointer[1] += this.scrollSpeed[1] * delta / 1000;
			if (zk.Draggable._lastScrollPointer[0] < 0)
				zk.Draggable._lastScrollPointer[0] = 0;
			if (zk.Draggable._lastScrollPointer[1] < 0)
				zk.Draggable._lastScrollPointer[1] = 0;
			this.draw(zk.Draggable._lastScrollPointer);
		}

		if(this.opts.change) this.opts.change(this);
	},

	_updateInnerOfs: function () {
		this._innerOfs = [zDom.innerX(), zDom.innerY()];
	},
	_getWindowScroll: function(w) {
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
		return {top: T, left: L, width: W, height: H };
	}
},{ //static
	_drags: [],

	register: function(draggable) {
		var zdg = zk.Draggable;
		if(zdg._drags.length == 0) {
			zEvt.listen(document, "mouseup", zdg.onDocMouseUp);
			zEvt.listen(document, "mousemove", zdg.onDocMouseMove);
			zEvt.listen(document, "keypress", zdg.onDocKeypress);
		}
		zdg._drags.push(draggable);
	},
	unregister: function(draggable) {
		zdg._drags.remove(draggable);
		if(zdg._drags.length == 0) {
			zEvt.unlisten(document, "mouseup", zdg.onDocMouseUp);
			zEvt.unlisten(document, "mousemove", zdg.onDocMouseMove);
			zEvt.unlisten(document, "keypress", zdg.onDocKeypress);
		}
	},

	activate: function(draggable) {
		if(zk.opera || draggable.opts.delay) { 
			zdg._timeout = setTimeout(function() { 
				zk.Draggable._timeout = null; 
				window.focus(); 
				zk.Draggable.activeDraggable = draggable; 
			}, draggable.opts.delay); 
		} else {
			window.focus(); // allows keypress events if window isn't currently focused, fails for Safari
			zdg.activeDraggable = draggable;
		}
	},
	deactivate: function() {
		zdg.activeDraggable = null;
	},

	onDocMouseMove: function(evt) {
		if (!evt) evt = window.event;
		if(!zdg.activeDraggable) return;
		var pointer = [zEvt.x(evt), zEvt.y(evt)];
		// Mozilla-based browsers fire successive mousemove events with
		// the same coordinates, prevent needless redrawing (moz bug?)
		if(zdg._lastPointer && zdg._lastPointer[0] == pointer [0]
		&& zdg._lastPointer[1] == pointer [1])
			return;

		zdg._lastPointer = pointer;
		zdg.activeDraggable.updateDrag(evt, pointer);
	},
	onDocMouseUp: function(evt) {
		if (!evt) evt = window.event;
		if(zdg._timeout) { 
			clearTimeout(zdg._timeout); 
			zdg._timeout = null; 
		}
		if(!zdg.activeDraggable) return;

		zdg._lastPointer = null;
		zdg.activeDraggable.endDrag(evt);
		zdg.activeDraggable = null;
	},
	onDocKeypress: function(evt) {
		if (!evt) evt = window.event;
		if(zdg.activeDraggable)
			zdg.activeDraggable.keyPress(evt);
	},
	restorePostion: zk.ie ? function (el, pos) {
		//In IE, we have to detach and attach. We cannot simply restore position!!
		//Otherwise, a strange bar appear
		if (pos != 'absolute' && pos != 'relative') {
			var p = el.parentNode;
			var n = el.nextSibling;
			zk.remove(el);
			el.style.position = pos;
			if (n) p.insertBefore(el, n);
			else p.appendChild(el);
		} else
			el.style.position = pos;
	}: function () {
		el.style.position = pos;
	},
	onMouseDown: function (evt) {
		if (!evt) evt = window.event;
		for (var n = zEvt.target(evt); n; n = n.parentNode)
			if (n.z_draggable) {
				n.z_draggable.mouseDown(evt);
				return;
			}
	}
});
