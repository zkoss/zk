/* anima.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 17 12:51:29     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
// anima //
/* Animation effects. It requires the component to have the <div><div>
 * structure.
 */
zAnima = {
	/** Number of pending animation */
	count: 0,

	isAnimating: function (n) {
		return n && n._$animating;
	},

	/** Make a component visible by increasing the opacity.
	 * 
	 * <p>Event: <code>beforeAppear</code> and <code>afterAppear</code>.
	 * The <code>beforeAppear</code> callback function is called before the
	 * effect begins. The <code>afterAppear</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param n component or its ID
	 */
	appear: function (widget, n, dur) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.appear(widget, n, dur);});
			} else {
				++zAnima.count;
				n._$animating = "show";
				zAnima._onVisible(widget, true); //callback first
				zEffect.appear(n, zAnima._getVisiOpts(widget, {
					duration:dur ? dur/1000: 0.6, name: "Appear"
				}));
			}
		}
	},
	/**
	 * Make a component visible by moving down.
	 * @param {Object} n
	 * @see #moveBy
	 */
	moveDown: function (widget, n, dur) {
		zAnima.moveBy(widget, n, 'top', dur);
	},
	/**
	 * Make a component visible by moving right.
	 * @param {Object} n
	 * @see #moveBy
	 */
	moveRight: function (widget, n, dur) {
		zAnima.moveBy(widget, n, 'left', dur);
	},
	/**
	 * Make a component visible by moving diagonal.
	 * @param {Object} n
	 * @see #moveBy
	 */
	moveDiagonal: function (widget, n, dur) {
		zAnima.moveBy(widget, n, null, dur);
	},
	/** Make a component visible by moving.
	 * 
	 * <p>Event: <code>beforeMoveBy</code> and <code>afterMoveBy</code>.
	 * The <code>beforeMoveBy</code> callback function is called before the
	 * effect begins. The <code>afterMoveBy</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * 
	 * @param n component or its ID
	 * @param pos the move position. "top" means from 0 to the original top, 
	 *  "left" means from 0 to the original left.
	 */
	moveBy: function (widget, n, pos, dur) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.moveBy(widget, n, pos, dur);});
			} else {
				++zAnima.count;
				n._$animating = "show";
				zAnima._onVisible(widget, true);
				if (!pos) pos = "topleft"

  				new zEffect.Move(n, 0, 0, zAnima._getVisiOpts(widget, {
					duration:dur ? dur/1000: 0.6, name: "MoveBy",
					afterSetup: function(effect) {
						if (pos.indexOf("left") > -1) {
							effect.opts.x = effect.originalLeft;
							effect.originalLeft = 0;
						}
						if (pos.indexOf("top") > -1) {
							effect.opts.y = effect.originalTop;
							effect.originalTop = 0;
						}
						zDom.show(effect.element);
					}
				}));
			}
		}
	},
	/** Make a component invisible by sliding in.
	 * <p>Event: <code>beforeSlideIn</code> and <code>afterSlideIn</code>.
	 * The <code>beforeSlideIn</code> callback function is called before the
	 * effect begins. The <code>afterSlideIn</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param n component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 */
	slideIn: function (widget, n, anchor, dur) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideIn(widget, n, anchor, dur);});
			} else {
				++zAnima.count;
				n._$animating = "show";
				zAnima._onVisible(widget, true); //callback first
				zEffect.slideIn(n, anchor, zAnima._getVisiOpts(widget, {
					duration:dur ? dur/1000: 0.4, name: "SlideIn"
				}));
			}
		}
	},
	/** Make a component invisible by sliding out.
	 * <p>Event: <code>beforeSlideOut</code> and <code>afterSlideIn</code>.
	 * The <code>beforeSlideIn</code> callback function is called before the
	 * effect begins. The <code>afterSlideOut</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param n component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 */
	slideOut: function (widget, n, anchor, dur) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideOut(widget, n, anchor, dur);});
			} else {
				++zAnima.count;
				n._$animating = "hide";
				if (widget) zWatch.fireDown("onHide", -1, widget); //callback first
				zEffect.slideOut(n, anchor, zAnima._getHideOpts(widget, {
					duration:dur ? dur/1000: 0.4, name: "SlideOut"
				}));
			}
		}
	},
	/** Make a component visible by sliding down.
	 * 
	 * <p>Event: <code>beforeSlideDown</code> and <code>afterSlideDown</code>.
	 * The <code>beforeSlideDown</code> callback function is called before the
	 * effect begins. The <code>afterSlideDown</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param n component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 */
	slideDown: function (widget, n, anchor, dur) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideDown(widget, n, anchor, dur);});
			} else {
				if (anchor && typeof anchor != "string") {
					dur = anchor.duration; // backward compatible	
					anchor = 't';
				}
				++zAnima.count;
				n._$animating = "show";
				zAnima._onVisible(widget, true); //callback first
				zEffect.slideDown(n, anchor, zAnima._getVisiOpts(widget, {
					duration:dur ? dur/1000: 0.4, name: "SlideDown", y :0
				}));
					//duration must be less than 0.5 since other part assumes it
			}
		}
	},
	/** Make a component invisible by sliding up.
	 * 
	 * <p>Event: <code>beforeSlideUp</code> and <code>afterSlideUp</code>.
	 * The <code>beforeSlideUp</code> callback function is called before the
	 * effect begins. The <code>afterSlideUp</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param n component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 */
	slideUp: function (widget, n, anchor, dur) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideUp(widget, n, anchor, dur);});
			} else {
				if (anchor && typeof anchor != "string") {
					dur = anchor.duration; // backward compatible	
					anchor = 't';
				}
				++zAnima.count;
				n._$animating = "hide";
				if (widget) zWatch.fireDown("onHide", -1, widget); //callback first
				zEffect.slideUp(n, anchor, zAnima._getHideOpts(widget, {
					duration:dur ? dur/1000: 0.4, name: "SlideUp"
				})); //duration must be less than 0.5 since other part assumes it
			}
		}
	},
	/** Make a component invisible by fading it out.
	 * 
	 * <p>Event: <code>beforeFade</code> and <code>afterFade</code>.
	 * The <code>beforeFade</code> callback function is called before the
	 * effect begins. The <code>afterFade</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param n component or its ID
	 */
	fade: function (widget, n, dur) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.fade(widget, n, dur);});
			} else {
				++zAnima.count;
				n._$animating = "hide";
				if (widget) zWatch.fireDown("onHide", -1, widget); //callback first
				zEffect.fade(n, zAnima._getHideOpts(widget, {
					duration:dur ? dur/1000: 0.55, name: "Fade"
				}));
			}
		}
	},
	/** Make a component invisible by puffing away.
	 * 
	 * <p>Event: <code>beforePuff</code> and <code>afterPuff</code>.
	 * The <code>beforePuff</code> callback function is called before the
	 * effect begins. The <code>afterPuff</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param n component or its ID
	 */
	puff: function (widget, n, dur) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.puff(widget, n, dur);});
			} else {
				++zAnima.count;
				n._$animating = "hide";
				if (widget) zWatch.fireDown("onHide", -1, widget); //callback first
				zEffect.puff(n, zAnima._getHideOpts(widget, {
					duration:dur ? dur/1000: 0.6, name: "Puff"
				}));
			}
		}
	},
	/** Make a component invisible by fading and dropping out.
	 * 
	 * <p>Event: <code>beforeDropOut</code> and <code>afterDropOut</code>.
	 * The <code>beforeDropOut</code> callback function is called before the
	 * effect begins. The <code>afterDropOut</code> callback function
	 * is called after the effect is finished.
	 * </p>
	 * @param n component or its ID
	 */
	dropOut: function (widget, n, dur) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.dropOut(widget, n, dur);});
			} else {
				++zAnima.count;
				n._$animating = "hide";
				if (widget) zWatch.fireDown("onHide", -1, widget); //callback first
				zEffect.dropOut(n, zAnima._getHideOpts(widget, {
					duration:dur ? dur/1000: 0.6, name: "DropOut"
				}));
			}
		}
	},

	// private //
	_onVisible: function (widget, visible) {
		if (widget) {
			var p = widget.parent;
			if (p) p.onChildVisible(widget, visible);
		}
	},
	_getVisiOpts: function (widget, opts) {
		opts.animaWidget = widget;
		return zk.$default(opts, zAnima._defVisiOpts);
	},
	_getHideOpts: function (widget, opts) {
		opts.animaWidget = widget;
		return zk.$default(opts, zAnima._defHideOpts);
	},
	_defVisiOpts: {
		beforeStart: function (ef) {
			var n = ef.element || ef.effects[0].element;
			if (n) zWatch.fire("before" + ef.name, -1, n, ef);
		},
		afterFinish: function (ef) {
			var n = ef.element || ef.effects[0].element;
			if (n) {
				--zAnima.count;
				n._$animating = null;
				if (zk.ie) zDom.rerender(n); // fixed a bug of the finished animation for IE

				zAnima._doAniQue(n);
				zWatch.fire("after" + ef.name, -1, n, ef);

				var o = ef.opts.animaWidget;
				if (o) 	zWatch.fireDown("onVisible", -1, o); //after visible
			}
		}
	},
	_defHideOpts: {
		beforeStart: function (ef) {
			var n = ef.element || ef.effects[0].element;
			if (n) zWatch.fire("before" + ef.name, -1, n, ef);
		},
		afterFinish: function (ef) {
			var n = ef.element || ef.effects[0].element;
			if (n) {
				zDom.hide(n); //hide later
				zAnima._onVisible(ef.opts.animaWidget, false); //after hide
				--zAnima.count;
				n._$animating = null;
				zAnima._doAniQue(n);
				zWatch.fire("after" + ef.name, -1, n, ef);
			}
		}
	},

	//animation queue
	_aniQue: {},
		//queue for waiting animating to clear: map(id, array(js_func_name))
	_addAniQue: function(n, fn) {
		var que = zAnima._aniQue,
			id = n.id ? n.id: n,
			ary = que[id];
		if (!ary) ary = que[id] = [];
		ary.push(fn);
	},
	_doAniQue: function (n) {
		var que = zAnima._aniQue,
			id = n.id ? n.id: n,
			ary = que[id], fn;
		if (ary) {
			while(!n._$animating && (fn = ary.shift()))
				fn();
			if (!ary.length) delete que[id];
		}
	}
};
