/* anima.ts

	Purpose:

	Description:

	History:
		Fri Jun 26 15:19:37     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
export interface Anima {
	anima: string;
	el: HTMLElement;
	wgt: zk.Widget;
	opts?: zk.SlideOptions;
}

var _aftAnims: CallableFunction[] = [], //used zk.afterAnimate
	_jqstop = jq.fx.stop;

jq.fx.stop = function () {
	_jqstop();
	for (var fn: undefined | CallableFunction; fn = _aftAnims.shift();)
		fn();
};

function _addAnique(id: string, data: Anima): void {
	var ary = zk._anique[id];
	if (!ary)
		ary = zk._anique[id] = [];
	ary.push(data);
}
function _doAnique(id: string): void {
	var ary = zk._anique[id];
	if (ary) {
		var al = ary.length, data: Anima | undefined;
		while (data = ary.shift()) {
			if (jq(data.el).is(':animated')) {
				ary.unshift(data);
				break;
			}
			(zk(data.el)[data.anima] as CallableFunction)(data.wgt, data.opts);
			al--;
		}

		if (!al)
			delete zk._anique[id];
	}
}

function _saveProp(self: zk.JQZK, set: string[]): zk.JQZK {
	const ele = self.jq;
	for (var i = set.length; i--;)
		if (set[i] != null) ele.data('zk.cache.' + set[i], ele[0].style[set[i]] as string);
	return self;
}
function _restoreProp(self: zk.JQZK, set: string[]): zk.JQZK {
	var ele = self.jq;
	for (var i = set.length; i--;)
		if (set[i] != null) ele.css(set[i], ele.data('zk.cache.' + set[i]) as string);
	return self;
}
function _checkAnimated(self: zk.JQZK, wgt: zk.Widget, opts: zk.SlideOptions | undefined, anima: string): boolean {
	if (self.jq.is(':animated')) {
		_addAnique(wgt.uuid, {el: self.jq[0], wgt: wgt, opts: opts, anima: anima});
		return true;
	}
	return false;
}
function _checkPosition(self: zk.JQZK, css: Record<string, string>): zk.JQZK {
	var pos = self.jq.css('position');
	if (!pos || pos == 'static')
		css.position = 'relative';
	return self;
}

/**
 * @returns whether there is some animation taking place.
 * If you'd like to have a function to be called only when no animation
 * is taking place (such as waiting for sliding down to be completed),
 * you could use {@link zk.afterMount}.
 * @see {@link zk.afterAnimate}
 */
export function animating(): boolean {
	return !!jq.timers.length;
}
zk.animating = animating;
/**
 * Executes a function only when no animation is taking place.
 * If there is some animation, the specified function will be queued
 * and invoked after the animation is done.
 * <p>If the delay argument is not specified and no animation is taking place,
 * the function is executed with `setTimeout(fn, 0)`.
 * @param fn - the function to execute
 * @param delay - (int) how many milliseconds to wait before execute if
 * there is no animation is taking place. If omitted, 0 is assumed.
 * If negative, the function is executed immediately.
 * @returns true if this method has been called before return (delay must
 * be negative, and no animation); otherwise, undefined is returned.
 * @see {@link zk.animating}
 * @since 5.0.6
 */
export function afterAnimate(fn: () => void, delay?: number): boolean | void {
	if (zk.animating())
		_aftAnims.push(fn);
	else if (delay !== undefined && delay < 0) {
		fn();
		return true;
	} else
		setTimeout(fn, delay);
}
zk.afterAnimate = afterAnimate;

export var _anique: Record<string, Anima[]> = {};
zk._anique = _anique;

export namespace anima_global {
	/** @partial jqzk
	 */
	export class zjq extends zk.JQZK {
		/**
		 * Get the value of animation speed assigned through client attribute "data-animationspeed"
		 * @param defaultValue - default value if widget doesn't have this attribute.
		 * <p>
		 * Allowed values:
		 * <dl>
		 * <dt>`Integer`</dt>
		 * <dd>It can be any integer value</dd>
		 * <dt>`String`</dt>
		 * <dd>This value can be "slow" or "fast", which is the same as jQuery Animation</dd>
		 * </dl>
		 * </p>
		 * @returns an Integer or String.
		 * @since 7.0.3
		 */
		getAnimationSpeed(defaultValue?: 'slow' | 'fast' | number): 'slow' | 'fast' | number {
			var animationSpeed = jq(this.$().$n()).closest('[data-animationspeed]').data('animationspeed') as string | number,
				jqSpeed = jq.fx.speeds;

			if (typeof animationSpeed === 'string') {
				if (jqSpeed[animationSpeed])
					return jqSpeed[animationSpeed];
				else
					animationSpeed = parseInt(animationSpeed);
			}

			return typeof animationSpeed === 'number' && !isNaN(animationSpeed) ? animationSpeed : (defaultValue === 0 ? 0 : defaultValue || jqSpeed._default);
		}
		/**
		 * Slides down (show) of the matched DOM element(s).
		 * @param wgt - the widget that owns the DOM element
		 * @param opts - the options. Ignored if not specified.
		 * Allowed options:
		 * <dl>
		 * <dt>anchor</dt>
		 * <dd>The anchor position which can be `t`, `b`,
		 * `l`, and `r`. Default: `t`.</dd>
		 * <dt>easing</dt>
		 * <dd>The name of the easing effect that you want to use (plugin required). There are two built-in values, "linear" and "swing".</dd>
		 * <dt>duration</dt>
		 * <dd>The duration of animation (unit: milliseconds). Default: 400</dd>
		 * <dt>afterAnima</dt>
		 * <dd>The function to invoke after the animation.</dd>
		 * </dl>
		 */
		slideDown(wgt: zk.Widget, opts?: zk.SlideOptions): this {
			if (_checkAnimated(this, wgt, opts, 'slideDown'))
				return this;

			var anchor = opts ? opts.anchor || 't' : 't',
				prop = ['top', 'left', 'height', 'width', 'overflow', 'position', 'border', 'margin', 'padding'],
				anima: Record<string, string> = {},
				css: Record<string, string> = {overflow: 'hidden'},
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

			this._createWrapper(this.defaultAnimaOpts(wgt, opts, prop, true).jq)
				// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
				.css(css).show().animate(anima, {
				queue: false, easing: opts.easing, duration: this.getAnimationSpeed(opts.duration === 0 ? 0 : opts.duration || 250),
				always: opts.afterAnima
			} as JQuery.EffectsOptions<HTMLElement>);
			return this;
		}
		/**
		 * Slides up (hide) of the matched DOM element(s).
		 * @param wgt - the widget that owns the DOM element
		 * @param opts - the options. Ignored if not specified.
		 * Allowed options:
		 * <dl>
		 * <dt>anchor</dt>
		 * <dd>The anchor position which can be `t`, `b`,
		 * `l`, and `r`. Default: `t`.</dd>
		 * <dt>easing</dt>
		 * <dd>The name of the easing effect that you want to use (plugin required). There are two built-in values, "linear" and "swing".</dd>
		 * <dt>duration</dt>
		 * <dd>The duration of animation (unit: milliseconds). Default: 400</dd>
		 * <dt>afterAnima</dt>
		 * <dd>The function to invoke after the animation.</dd>
		 * </dl>
		 */
		slideUp(wgt: zk.Widget, opts?: zk.SlideOptions): this {
			if (_checkAnimated(this, wgt, opts, 'slideUp'))
				return this;

			var anchor = opts ? opts.anchor || 't' : 't',
				prop = ['top', 'left', 'height', 'width', 'overflow', 'position', 'border', 'margin', 'padding'],
				anima: Record<string, string> = {},
				css: Record<string, string> = {overflow: 'hidden'},
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

			this._createWrapper(this.defaultAnimaOpts(wgt, opts, prop).jq)
				// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
				.css(css).animate(anima, {
				queue: false, easing: opts.easing, duration: this.getAnimationSpeed(opts.duration === 0 ? 0 : opts.duration || 250),
				always: opts.afterAnima
			} as JQuery.EffectsOptions<HTMLElement>);
			return this;
		}
		/**
		 * Slides out (hide) of the matched DOM element(s).
		 * @param wgt - the widget that owns the DOM element
		 * @param opts - the options. Ignored if not specified.
		 * Allowed options:
		 * <dl>
		 * <dt>anchor</dt>
		 * <dd>The anchor position which can be `t`, `b`,
		 * `l`, and `r`. Default: `t`.</dd>
		 * <dt>easing</dt>
		 * <dd>The name of the easing effect that you want to use (plugin required). There are two built-in values, "linear" and "swing".</dd>
		 * <dt>duration</dt>
		 * <dd>The duration of animation (unit: milliseconds). Default: 400</dd>
		 * <dt>afterAnima</dt>
		 * <dd>The function to invoke after the animation.</dd>
		 * </dl>
		 */
		slideOut(wgt: zk.Widget, opts?: zk.SlideOptions): this {
			if (_checkAnimated(this, wgt, opts, 'slideOut'))
				return this;

			var anchor = opts ? opts.anchor || 't' : 't',
				prop = ['top', 'left', 'position', 'border', 'margin', 'padding'],
				anima: Record<string, string> = {},
				css: Record<string, string> = {},
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

			this._createWrapper(this.defaultAnimaOpts(wgt, opts, prop).jq)
				// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
				.css(css).animate(anima, {
				queue: false, easing: opts.easing, duration: this.getAnimationSpeed(opts.duration === 0 ? 0 : opts.duration || 350),
				always: opts.afterAnima
			} as JQuery.EffectsOptions<HTMLElement>);
			return this;
		}
		/**
		 * Slides in (show) of the matched DOM element(s).
		 * @param wgt - the widget that owns the DOM element
		 * @param opts - the options. Ignored if not specified.
		 * Allowed options:
		 * <dl>
		 * <dt>anchor</dt>
		 * <dd>The anchor position which can be `t`, `b`,
		 * `l`, and `r`. Default: `t`.</dd>
		 * <dt>easing</dt>
		 * <dd>The name of the easing effect that you want to use (plugin required). There are two built-in values, "linear" and "swing".</dd>
		 * <dt>duration</dt>
		 * <dd>The duration of animation (unit: milliseconds). Default: 400</dd>
		 * <dt>afterAnima</dt>
		 * <dd>The function to invoke after the animation.</dd>
		 * </dl>
		 */
		slideIn(wgt: zk.Widget, opts?: zk.SlideOptions): this {
			if (_checkAnimated(this, wgt, opts, 'slideIn'))
				return this;

			var anchor = opts ? opts.anchor || 't' : 't',
				prop = ['top', 'left', 'position', 'border', 'margin', 'padding'],
				anima: Record<string, string> = {},
				css: Record<string, string> = {},
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

			this._createWrapper(this.defaultAnimaOpts(wgt, opts, prop, true).jq)
				// eslint-disable-next-line @typescript-eslint/consistent-type-assertions
				.css(css).show().animate(anima, {
				queue: false, easing: opts.easing, duration: this.getAnimationSpeed(opts.duration === 0 ? 0 : opts.duration || 350),
				always: opts.afterAnima
			} as JQuery.EffectsOptions<HTMLElement>);
			return this;
		}
		/** @internal */
		_updateProp(prop: string[]): void { //used by Bandpopup.js
			_saveProp(this, prop);
		}
		/**
		 * Initializes the animation with the default effect, such as
		 * firing the onSize watch.
		 * <p>Example:<br/>
		 * `zk(n).defaultAnimaOpts(wgt, opts, prop, true).jq.css(css).show().animate(...);`
		 * @param wgt - the widget
		 * @param opts - the options. Ignored if not specified.
		 * It depends on the effect being taken
		 * @param prop - an array of properties, such `['top', 'left', 'position']`.
		 * @param visible - whether the result of the animation will make
		 * the DOM element visible
		 * @since 5.0.6
		 */
		defaultAnimaOpts(wgt: zk.Widget, opts: zk.SlideOptions, prop: string[], visible?: boolean): this {
			var self = this;
			jq.timers.push(function () {
				if (!visible)
					zWatch.fireDown('onHide', wgt);
				if (opts.beforeAnima)
					opts.beforeAnima.call(wgt, self);
			} as JQuery.TickFunction<HTMLElement>);

			var aftfn = opts.afterAnima;
			opts.afterAnima = function () {
				self._removeWrapper(self.jq);
				if (prop) _restoreProp(self, prop);
				if (visible) {
					/*
					* fixed a bug of the finished animation for IE
					* refix for ZK-568: Open combobox then select last item. reopen combobox then you should see selected item without scroll
					*/
					var zkie = zk.ie;
					// ZK_3789: refine ZK-3695, fire down onRestore after the wrapper was removed
					zWatch.fireDown('onRestore', wgt);
					if (zkie == 10) zk(self.jq[0]).redoCSS();
					zUtl.fireShown(wgt);
				} else {
					self.jq.hide();
				}
				if (aftfn) aftfn.call(wgt, self.jq[0]);
				wgt.afterAnima_(!!visible);
				setTimeout(function () {
					_doAnique(wgt.uuid);
				});
			};
			return this;
		}
		// Wraps the content of a element with an inner wrapper that copies position properties to avoid jumpy animation.
		// The methods are borrowed from jquery-ui ui/effect.js, MIT license.
		/** @internal */
		_createWrapper(element: JQuery): JQuery {
		// If the element is already wrapped, return it
			var wrapped = element.children('.ui-effects-wrapper');
			if (wrapped.length) {
				return element;
			}

			var innerHeight = element.height(),
				innerWidth = element.width(),
				outerHeight = element.outerHeight(true),
				outerWidth = element.outerWidth(true);
			// No padding, border, or margin, no need to wrap.
			if (innerHeight == outerHeight && innerWidth == outerWidth) {
				return element;
			}

			// Wrap the element
			var props = zk.copy({
					boxSizing: 'border-box',
					height: '100%',
					width: '100%'
				}, element.css([
					'marginLeft', 'marginRight', 'marginTop', 'marginBottom',
					'paddingLeft', 'paddingRight', 'paddingTop', 'paddingBottom',
					'borderLeftWidth', 'borderRightWidth', 'borderTopWidth', 'borderBottomWidth',
					'borderLeftColor', 'borderRightColor', 'borderTopColor', 'borderBottomColor',
					'borderLeftStyle', 'borderRightStyle', 'borderTopStyle', 'borderBottomStyle'
				])) as Record<string, string>,
				wrapper = jq('<div></div>')
					.addClass('ui-effects-wrapper')
					.css(props),
				active = document.activeElement ?? document.body;

			element.wrapInner(wrapper);

			if (active && (element[0] === active || jq.contains(element[0], active))) {
				jq(active).trigger('focus');
			}

			return element.css({
				border: 'none',
				margin: 0,
				padding: 0
			});
		}
		/** @internal */
		_removeWrapper(element: JQuery): JQuery {
			var active = document.activeElement,
				wrapped = element.children('.ui-effects-wrapper');
			if (wrapped.length) {
				var children = wrapped.contents();
				children.length ? children.unwrap() : wrapped.remove();
				if (active && (element[0] === active || jq.contains(element[0], active))) {
					jq(active).trigger('focus');
				}
			}
			return element;
		}
	}
}
zk.copy(window, anima_global);