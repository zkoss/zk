/* anima.js

	Purpose:

	Description:

	History:
		Fri Jun 26 15:19:37     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function () {
	var _aftAnims = [], //used zk.afterAnimate
		_jqstop = jq.fx.stop;

	jq.fx.stop = function () {
		_jqstop();
		for (var fn; fn = _aftAnims.shift();)
			fn();
	};

	function _addAnique(id, data) {
		var ary = zk._anique[id];
		if (!ary)
			ary = zk._anique[id] = [];
		ary.push(data);
	}
	function _doAnique(id) {
		var ary = zk._anique[id];
		if (ary) {
			var al = ary.length;
			while (al) {
				var data = ary.shift();
				if (jq(data.el).is(':animated')) {
					ary.unshift(data);
					break;
				}
				zk(data.el)[data.anima](data.wgt, data.opts);
				al--;
			}

			if (!al)
				delete zk._anique[id];
		}
	}

	function _saveProp(self, set) {
		var ele = self.jq;
		for(var i = set.length; i--;)
			if(set[i] !== null) ele.data("zk.cache."+set[i], ele[0].style[set[i]]);
		return self;
	}
	function _restoreProp(self, set) {
		var ele = self.jq;
		for(var i = set.length; i--;)
			if(set[i] !== null) ele.css(set[i], ele.data("zk.cache."+set[i]));
		return self;
	}
	function _checkAnimated(self, wgt, opts, anima) {
		if (self.jq.is(':animated')) {
			_addAnique(wgt.uuid, {el: self.jq[0], wgt: wgt, opts: opts, anima: anima});
			return true;
		}
		return false;
	}
	function _checkPosition(self, css) {
		var pos = self.jq.css('position');
		if (!pos || pos == 'static')
			css.position = 'relative';
		return self;
	}

/** @partial zk
 */
zk.copy(zk, {
	/** Returns whether there is some animation taking place.
	 * If you'd like to have a function to be called only when no anitmation
	 * is taking place (such as waiting for sliding down to be completed),
	 * you could use {@link #afterMount}.
	 * @return boolean
	 * @see #afterAnimate
	 */
	animating: function () {
		return !!jq.timers.length;
	},
	/** Executes a function only when no animation is taking place.
	 * If there is some animation, the specified function will be queued
	 * and invoked after the animation is done.
	 * <p>If the delay argument is not specified and no animation is taking place,
	 * the function is executed with <code>setTimeout(fn, 0)</code>.
	 * @param Function fn the function to execute
	 * @param int delay how many milliseconds to wait before execute if
	 * there is no animation is taking place. If omiited, 0 is assumed.
	 * If negative, the function is executed immediately.
	 * @return boolean true if this method has been called before return (delay must
	 * be negative, and no animation); otherwise, undefined is returned.
	 * @see #animating
	 * @since 5.0.6
	 */
	afterAnimate: function (fn, delay) {
		if (zk.animating())
			_aftAnims.push(fn);
		else if (delay < 0) {
			fn();
			return true;
		} else
			setTimeout(fn, delay);
	},
	_anique: {}
});

/** @partial jqzk
 */
zk.copy(zjq.prototype, {
	/** Slides down (show) of the matched DOM element(s).
	 * @param Widget wgt the widget that owns the DOM element
	 * @param Map opts the options. Ignored if not specified.
	 * @return jqzk
	 * Allowed options:
	 * <dl>
	 * <dt>anchor</dt>
	 * <dd>The anchor position which can be <code>t</code>, <code>b</code>,
	 * <code>l</code>, and <code>r</code>. Default: <code>t</code>.</dd>
	 * <dt>easing</dt>
	 * <dd>The name of the easing effect that you want to use (plugin required). There are two built-in values, "linear" and "swing".</dd>
	 * <dt>duration</dt>
	 * <dd>The duration of animation (unit: milliseconds). Default: 400</dd>
	 * <dt>afterAnima</dt>
	 * <dd>The function to invoke after the animation.</dd>
	 * </dl>
	 */
	slideDown: function (wgt, opts) {
		if (_checkAnimated(this, wgt, opts, 'slideDown'))
			return this;

		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'height', 'width', 'overflow', 'position'],
			anima = {},
			css = {overflow: 'hidden'},
			dims = this.dimension();

		opts = opts || {};
		_checkPosition(_saveProp(this, prop), css);

		switch (anchor) {
		case 't':
			css.height = '0';
			anima.height = jq.px0(dims.height);
			break;
		case 'b':
			css.height = '0';
			css.top = jq.px(dims.top + dims.height);
			anima.height = jq.px0(dims.height);
			anima.top = jq.px(dims.top);
			break;
		case 'l':
			css.width = '0';
			anima.width = jq.px0(dims.width);
			break;
		case 'r':
			css.width = '0';
			css.left = jq.px(dims.left + dims.width);
			anima.width = jq.px0(dims.width);
			anima.left = jq.px(dims.left);
			break;
		}

		return this.defaultAnimaOpts(wgt, opts, prop, true)
			.jq.css(css).show().animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 250,
			complete: opts.afterAnima
		});
	},
	/** Slides up (hide) of the matched DOM element(s).
	 * @param Widget wgt the widget that owns the DOM element
	 * @param Map opts the options. Ignored if not specified.
	 * @return jqzk
	 * Allowed options:
	 * <dl>
	 * <dt>anchor</dt>
	 * <dd>The anchor position which can be <code>t</code>, <code>b</code>,
	 * <code>l</code>, and <code>r</code>. Default: <code>t</code>.</dd>
	 * <dt>easing</dt>
	 * <dd>The name of the easing effect that you want to use (plugin required). There are two built-in values, "linear" and "swing".</dd>
	 * <dt>duration</dt>
	 * <dd>The duration of animation (unit: milliseconds). Default: 400</dd>
	 * <dt>afterAnima</dt>
	 * <dd>The function to invoke after the animation.</dd>
	 * </dl>
	 */
	slideUp: function (wgt, opts) {
		if (_checkAnimated(this, wgt, opts, 'slideUp'))
			return this;
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'height', 'width', 'overflow', 'position'],
			anima = {},
			css = {overflow: 'hidden'},
			dims = this.dimension();

		opts = opts || {};
		_checkPosition(_saveProp(this, prop), css);

		switch (anchor) {
		case 't':
			anima.height = 'hide';
			break;
		case 'b':
			css.height = jq.px0(dims.height);
			anima.height = 'hide';
			anima.top = jq.px(dims.top + dims.height);
			break;
		case 'l':
			anima.width = 'hide';
			break;
		case 'r':
			css.width = jq.px0(dims.width);
			anima.width = 'hide';
			anima.left = jq.px(dims.left + dims.width);
			break;
		}

		return this.defaultAnimaOpts(wgt, opts, prop)
			.jq.css(css).animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 250,
			complete: opts.afterAnima
		});
	},
	/** Slides out (hide) of the matched DOM element(s).
	 * @param Widget wgt the widget that owns the DOM element
	 * @param Map opts the options. Ignored if not specified.
	 * @return jqzk
	 * Allowed options:
	 * <dl>
	 * <dt>anchor</dt>
	 * <dd>The anchor position which can be <code>t</code>, <code>b</code>,
	 * <code>l</code>, and <code>r</code>. Default: <code>t</code>.</dd>
	 * <dt>easing</dt>
	 * <dd>The name of the easing effect that you want to use (plugin required). There are two built-in values, "linear" and "swing".</dd>
	 * <dt>duration</dt>
	 * <dd>The duration of animation (unit: milliseconds). Default: 400</dd>
	 * <dt>afterAnima</dt>
	 * <dd>The function to invoke after the animation.</dd>
	 * </dl>
	 */
	slideOut: function (wgt, opts) {
		if (_checkAnimated(this, wgt, opts, 'slideOut'))
			return this;
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'position'],
			anima = {},
			css = {},
			dims = this.dimension();

		opts = opts || {};
		_checkPosition(_saveProp(this, prop), css);

		switch (anchor) {
		case 't':
			anima.top = jq.px(dims.top - dims.height);
			break;
		case 'b':
			anima.top = jq.px(dims.top + dims.height);
			break;
		case 'l':
			anima.left = jq.px(dims.left - dims.width);
			break;
		case 'r':
			anima.left = jq.px(dims.left + dims.width);
			break;
		}

		return this.defaultAnimaOpts(wgt, opts, prop)
			.jq.css(css).animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 350,
			complete: opts.afterAnima
		});
	},
	/** Slides in (show) of the matched DOM element(s).
	 * @param Widget wgt the widget that owns the DOM element
	 * @param Map opts the options. Ignored if not specified.
	 * @return jqzk
	 * Allowed options:
	 * <dl>
	 * <dt>anchor</dt>
	 * <dd>The anchor position which can be <code>t</code>, <code>b</code>,
	 * <code>l</code>, and <code>r</code>. Default: <code>t</code>.</dd>
	 * <dt>easing</dt>
	 * <dd>The name of the easing effect that you want to use (plugin required). There are two built-in values, "linear" and "swing".</dd>
	 * <dt>duration</dt>
	 * <dd>The duration of animation (unit: milliseconds). Default: 400</dd>
	 * <dt>afterAnima</dt>
	 * <dd>The function to invoke after the animation.</dd>
	 * </dl>
	 */
	slideIn: function (wgt, opts) {
		if (_checkAnimated(this, wgt, opts, 'slideIn'))
			return this;
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'position'],
			anima = {},
			css = {},
			dims = this.dimension();

		opts = opts || {};
		_checkPosition(_saveProp(this, prop), css);

		switch (anchor) {
		case 't':
			css.top = jq.px(dims.top - dims.height);
			anima.top = jq.px(dims.top);
			break;
		case 'b':
			css.top = jq.px(dims.top + dims.height);
			anima.top = jq.px(dims.top);
			break;
		case 'l':
			css.left = jq.px(dims.left - dims.width);
			anima.left = jq.px(dims.left);
			break;
		case 'r':
			css.left = jq.px(dims.left + dims.width);
			anima.left = jq.px(dims.left);
			break;
		}

		return this.defaultAnimaOpts(wgt, opts, prop, true)
			.jq.css(css).show().animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 350,
			complete: opts.afterAnima
		});
	},
	_updateProp: function(prop) { //used by Bandpopup.js
		_saveProp(this, prop);
	},
	/** Initializes the animation with the default effect, such as
	 * firing the onSize watch.
	 * <p>Example:<br/>
	 * <code>zk(n).defaultAnimaOpts(wgt, opts, prop, true).jq.css(css).show().animate(...);</code>
	 * @param Widget wgt the widget
	 * @param Map opts the options. Ignored if not specified.
	 * It depends on the effect being taken
	 * @param Array prop an array of properties, such ['top', 'left', 'position'].
	 * @param boolean visible whether the result of the animation will make
	 * the DOM element visible
	 * @return jqzk
	 * @since 5.0.6
	 */
	defaultAnimaOpts: function (wgt, opts, prop, visible) {
		var self = this;
		jq.timers.push(function() {
			if (!visible)
				zWatch.fireDown('onHide', wgt);
			if (opts.beforeAnima)
				opts.beforeAnima.call(wgt, self);
		});

		var aftfn = opts.afterAnima;
		opts.afterAnima = function () {
			if (prop) _restoreProp(self, prop);
			if (visible) {
				if (zk.ie) zk(self.jq[0]).redoCSS(); // fixed a bug of the finished animation for IE
				zUtl.fireShown(wgt);
			} else {
				self.jq.hide();
			}
			if (aftfn) aftfn.call(wgt, self.jq.context);
			wgt.afterAnima_(visible);
			setTimeout(function () {
				_doAnique(wgt.uuid);
			});
		};
		return this;
	}
});
})();