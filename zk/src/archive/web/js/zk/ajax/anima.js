/* anima.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov 17 12:51:29     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
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
	 * @param opts a map of options.
	 * <dl>
	 * <dt>duration</dt><dd>milliseconds</dd>
	 * <dt>beforeAnima</dt><dd>A function to call before animation. 
	 * Not: <code>this</code> will be widget when it is called.</dd>
	 * <dt>afterAnima</dt><dd>A function to call after animation. 
	 * Not: <code>this</code> will be widget when it is called.</dd>
	 * </dl>
	 */
	appear: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.appear(widget, n, opts);});
			} else {
				n._$animating = "show";
				zEffect.appear(n,
					zAnima._mergeOpts(widget, opts,	{duration: 0.6}));
			}
		}
	},
	/**
	 * Make a component visible by moving down.
	 * @see #moveBy
	 */
	moveDown: function (widget, n, opts) {
		zAnima.moveBy(widget, n, zk.$defaut(opts, {dir: 't'}));
	},
	/**
	 * Make a component visible by moving right.
	 * @param {Object} n
	 * @see #moveBy
	 */
	moveRight: function (widget, n, opts) {
		zAnima.moveBy(widget, n, zk.$defaut(opts, {dir: 'l'}));
	},
	/**
	 * Make a component visible by moving diagonal.
	 * @param {Object} n
	 * @see #moveBy
	 */
	moveDiagonal: function (widget, n, opts) {
		zAnima.moveBy(widget, n, opts);
	},
	/** Make a component visible by moving.
	 * 
	 * @param opts a map of options. See {@link #appear}.
	 * In addition,
	 * <dl>
	 * <dt>dir</dt><dd>the direction: "t" means from 0 to the original top, 
	 *  "l" means from 0 to the original left, and 'lt' means from the left-top
	 * corner (default)</dd>
	 * </dl>
	 */
	moveBy: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.moveBy(widget, n, opts);});
			} else {
				n._$animating = "show";

				var dir = opts ? opts.dir: 0;
				if (!dir) dir = "lt"

  				new zk.eff.Move(n, 0, 0, zAnima._mergeOpts(widget, opts, {
					duration: 0.6,
					afterSetup: function(effect) {
						if (dir.indexOf('l') >= 0) {
							effect.opts.x = effect.originalLeft;
							effect.originalLeft = 0;
						}
						if (dir.indexOf('t') >= 0) {
							effect.opts.y = effect.originalTop;
							effect.originalTop = 0;
						}
						zDom.show(effect.node);
					}
				}));
			}
		}
	},
	/** Make a component invisible by sliding in.
	 * @param opts a map of options. See {@link #appear}.
	 * In addition,
	 * <dl>
	 * <dt>anchor</dt><dd>An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.</dd>
	 * </dl>
	 */
	slideIn: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideIn(widget, n, opts);});
			} else {
				n._$animating = "show";
				zEffect.slideIn(n, zAnima._mergeOpts(widget, opts, {duration: 0.4}));
			}
		}
	},
	/** Make a component invisible by sliding out.
	 * @param opts a map of options. See {@link #appear}.
	 * In addition,
	 * <dl>
	 * <dt>anchor</dt><dd>An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.</dd>
	 * </dl>
	 */
	slideOut: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideOut(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.slideOut(n, zAnima._mergeOpts(widget, opts, {duration: 0.4}));
			}
		}
	},
	/** Make a component visible by sliding down.
	 * 
	 * @param n component or its ID
	 * @param anchor An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.
	 */
	slideDown: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideDown(widget, n, opts);});
			} else {
				n._$animating = "show";
				zEffect.slideDown(n, zAnima._mergeOpts(widget, opts, {duration: 0.4, y:0}));
			}
		}
	},
	/** Make a component invisible by sliding up.
	 * @param opts a map of options. See {@link #appear}.
	 * In addition,
	 * <dl>
	 * <dt>anchor</dt><dd>An anchor point can be optionally passed to set the point of
	 * origin for the slide effect ('t', 'b', 'l', and 'r'). Default: 't'.</dd>
	 * </dl>
	 */
	slideUp: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.slideUp(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.slideUp(n, zAnima._mergeOpts(widget, opts, {duration:0.4}));
			}
		}
	},
	/** Make a component invisible by fading it out.
	 */
	fade: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.fade(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.fade(n,
					zAnima._mergeOpts(widget, opts, {duration: 0.55}));
			}
		}
	},
	/** Make a component invisible by puffing away.
	 */
	puff: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.puff(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.puff(n,
					zAnima._mergeOpts(widget, opts, {duration: 0.6}));
			}
		}
	},
	/** Make a component invisible by fading and dropping out.
	 * 
	 * @param n component or its ID
	 */
	dropOut: function (widget, n, opts) {
		n = zDom.$(n);
		if (n) {
			if (n._$animating) {
				zAnima._addAniQue(n,
					function () {zAnima.dropOut(widget, n, opts);});
			} else {
				n._$animating = "hide";
				zEffect.dropOut(n,
					zAnima._mergeOpts(widget, opts, {duration: 0.6}));
			}
		}
	},

	// private //
	_mergeOpts: function (widget, opts, defOpts) {
		opts = zk.$default(opts, {animaWidget: widget});
		if (opts.duration) opts.duration /= 1000;
		return zk.$default(zk.$default(opts, defOpts), zAnima._defOpts);
	},
	_defOpts: {
		beforeStart: function (ef) {
			var n = ef.node || ef._effects[0].node;
			if (n) {
				++zAnima.count;
				var opts = ef.opts,
					widget = opts.animaWidget;
				if (opts.beforeAnima) opts.beforeAnima.call(widget, n);
			}
		},
		afterFinish: function (ef) {
			var n = ef.node || ef._effects[0].node;
			if (n) {
				--zAnima.count;
				n._$animating = null;
				if (zk.ie && zDom.isVisible(n)) zDom.redoDOM(n); //fix an IE bug

				zAnima._doAniQue(n);

				var opts = ef.opts,
					widget = opts.animaWidget;
				if (opts.afterAnima) opts.afterAnima.call(widget, n);
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
