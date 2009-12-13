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

zk.copy(zk, {
	animating: function () {
		return !!jq.timers.length;
	},
	_anique: {}
});
zk.copy(zjq.prototype, {
	_saveProp: function (set) {
		var ele = this.jq;
		for(var i = set.length; i--;)
			if(set[i] !== null) ele.data("zk.cache."+set[i], ele[0].style[set[i]]);
		return this;
	},
	_restoreProp: function (set) {
		var ele = this.jq;
		for(var i = set.length; i--;)
			if(set[i] !== null) ele.css(set[i], ele.data("zk.cache."+set[i]));
		return this;
	},
	_checkAnimated: function (wgt, opts, anima) {
		if (this.jq.is(':animated')) {
			_addAnique(wgt.uuid, {el: this.jq[0], wgt: wgt, opts: opts, anima: anima});
			return true;
		}
		return false;
	},
	_checkPosition: function (css) {
		var pos = this.jq.css('position');
		if (!pos || pos == 'static')
			css.position = 'relative';
		return this;
	},
	_defAnimaOpts: function (wgt, opts, prop, mode) {
		var self = this;
		jq.timers.push(function() {
			if (mode == 'hide')
				zWatch.fireDown('onHide', wgt);
			if (opts.beforeAnima)
				opts.beforeAnima.call(wgt, self);
		});

		var aftfn = opts.afterAnima;
		opts.afterAnima = function () {
			if (mode == 'hide') {
				self.jq.hide();
			} else {
				if (zk.ie) zk(self.jq[0]).redoCSS(); // fixed a bug of the finished animation for IE
				zWatch.fireDown('onShow', wgt);
			}
			if (prop) self._restoreProp(prop);
			if (aftfn) aftfn.call(wgt, self.jq.context);
			setTimeout(function () {
				_doAnique(wgt.uuid);
			});
		};
		return this;
	},
	slideDown: function (wgt, opts) {
		if (this._checkAnimated(wgt, opts, 'slideDown'))
			return this;

		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'height', 'width', 'overflow', 'position'],
			anima = {},
			css = {overflow: 'hidden'},
			dims = this.dimension();

		opts = opts || {};
		this._saveProp(prop)._checkPosition(css);

		switch (anchor) {
		case 't':
			css.height = '0px';
			anima.height = jq.px0(dims.height);
			break;
		case 'b':
			css.height = '0px';
			css.top = jq.px(dims.top + dims.height);
			anima.height = jq.px0(dims.height);
			anima.top = jq.px(dims.top);
			break;
		case 'l':
			css.width = '0px';
			anima.width = jq.px0(dims.width);
			break;
		case 'r':
			css.width = '0px';
			css.left = jq.px(dims.left + dims.width);
			anima.width = jq.px0(dims.width);
			anima.left = jq.px(dims.left);
			break;
		}

		return this._defAnimaOpts(wgt, opts, prop).jq.css(css).show().animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 400,
			complete: opts.afterAnima
		});
	},
	slideUp: function (wgt, opts) {
		if (this._checkAnimated(wgt, opts, 'slideUp'))
			return this;
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'height', 'width', 'overflow', 'position'],
			anima = {},
			css = {overflow: 'hidden'},
			dims = this.dimension();

		opts = opts || {};
		this._saveProp(prop)._checkPosition(css);

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

		return this._defAnimaOpts(wgt, opts, prop, 'hide').jq.css(css).animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 400,
			complete: opts.afterAnima
		});
	},
	slideOut: function (wgt, opts) {
		if (this._checkAnimated(wgt, opts, 'slideOut'))
			return this;
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'position'],
			anima = {},
			css = {},
			dims = this.dimension();

		opts = opts || {};
		this._saveProp(prop)._checkPosition(css);

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

		return this._defAnimaOpts(wgt, opts, prop, 'hide').jq.css(css).animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 500,
			complete: opts.afterAnima
		});
	},
	slideIn: function (wgt, opts) {
		if (this._checkAnimated(wgt, opts, 'slideIn'))
			return this;
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'position'],
			anima = {},
			css = {},
			dims = this.dimension();

		opts = opts || {};
		this._saveProp(prop)._checkPosition(css);

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

		return this._defAnimaOpts(wgt, opts, prop).jq.css(css).show().animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 500,
			complete: opts.afterAnima
		});
	}
});
})();