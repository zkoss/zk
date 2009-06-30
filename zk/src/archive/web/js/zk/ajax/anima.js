/* anima.js

	Purpose:
		
	Description:
		
	History:
		Fri Jun 26 15:19:37     2009, Created by jumperchen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.copy(zk, {
	animating: function () {
		return !!jq.timers.length;
	}
});
zk.copy(zjq.prototype, {
	_unit: 'px',
	_saveProp: function (ele, set) {
		for(var i = set.length; i--;)
			if(set[i] !== null) ele.data("zk.cache."+set[i], ele[0].style[set[i]]);
	},
	_restoreProp: function (ele, set) {
		for(var i = set.length; i--;)
			if(set[i] !== null) ele.css(set[i], ele.data("zk.cache."+set[i]));
	},
	_defAnimaOpts: function (wgt, opts, prop, mode) {
		var self = this;
		if (jq.isFunction(opts.beforeAnima))
			jq.timers.push(function() {
				opts.beforeAnima.call(wgt, self);
			});
		
		var aftfn = opts.afterAnima;
		opts.afterAnima = function () {
			if (mode == 'hide') self.jq.hide();
			if (prop) self._restoreProp(self.jq, prop);
			if (aftfn) aftfn.call(wgt, self.jq.context);
		};
	},
	slideDown: function (wgt, opts) {
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'height', 'width', 'overflow'],
			anima = {},
			css = {overflow: 'hidden'},
			dims = this.dimension();
			
		opts = opts || {};
		this._saveProp(this.jq, prop);
		switch (anchor) {
		case 't':
			css.height = '0px';
			anima.height = dims.height + this._unit;
			break;
		case 'b':
			css.height = '0px';
			css.top = dims.top + dims.height + this._unit;
			anima.height = dims.height + this._unit;
			anima.top = dims.top + this._unit;
			break;
		case 'l':
			css.width = '0px';
			anima.width = dims.width + this._unit;
			break;
		case 'r':
			css.width = '0px';
			css.left = dims.left + dims.width + this._unit;
			anima.width = dims.width + this._unit;
			anima.left = dims.left + this._unit;
			break;
		}
			
		this._defAnimaOpts(wgt, opts, prop);
		return this.jq.css(css).show().animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 400,
			complete: opts.afterAnima
		});
	},
	slideUp: function (wgt, opts) {
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left', 'height', 'width', 'overflow'],
			anima = {},
			css = {overflow: 'hidden'},
			dims = this.dimension();
			
		opts = opts || {};
		this._saveProp(this.jq, prop);
		switch (anchor) {
		case 't':
			anima.height = 'hide';
			break;
		case 'b':
			css.height = dims.height + this._unit;
			anima.height = 'hide';
			anima.top = dims.top + dims.height + this._unit;
			break;
		case 'l':
			anima.width = 'hide';
			break;
		case 'r':
			css.width = dims.width + this._unit;
			anima.width = 'hide';
			anima.left = dims.left + dims.width + this._unit;
			break;
		}
			
		this._defAnimaOpts(wgt, opts, prop, 'hide');
		return this.jq.css(css).animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 400,
			complete: opts.afterAnima
		});
	},
	slideOut: function (wgt, opts) {		
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left'],
			anima = {},
			dims = this.dimension();
			
		opts = opts || {};
		this._saveProp(this.jq, prop);
		switch (anchor) {
		case 't':
			anima.top = dims.top - dims.height + this._unit;
			break;
		case 'b':
			anima.top = dims.top + dims.height + this._unit;
			break;
		case 'l':
			anima.left = dims.left - dims.width + this._unit;
			break;
		case 'r':
			anima.left = dims.left + dims.width + this._unit;
			break;
		}	
		this._defAnimaOpts(wgt, opts, prop, 'hide');
		return this.jq.animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 500,
			complete: opts.afterAnima
		});
	},
	slideIn: function (wgt, opts) {		
		var anchor = opts ? opts.anchor || 't': 't',
			prop = ['top', 'left'],
			anima = {},
			css = {display: 'block'},
			dims = this.dimension();
			
		opts = opts || {};
		this._saveProp(this.jq, prop);
		switch (anchor) {
		case 't':
			css.top = dims.top - dims.height + this._unit;
			anima.top = dims.top + this._unit;
			break;
		case 'b':
			css.top = dims.top + dims.height + this._unit;
			anima.top = dims.top + this._unit;
			break;
		case 'l':
			css.left = dims.left - dims.width + this._unit;
			anima.left = dims.left + this._unit;
			break;
		case 'r':
			css.left = dims.left + dims.width + this._unit;
			anima.left = dims.left + this._unit;
			break;
		}
			
		this._defAnimaOpts(wgt, opts, prop);
		return this.jq.css(css).animate(anima, {
			queue: false, easing: opts.easing, duration: opts.duration || 500,
			complete: opts.afterAnima
		});
	}
});
