/* evt.js

	Purpose:
		DOM Event, ZK Event and ZK Watch
	Description:
		
	History:
		Thu Oct 23 10:53:17     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.Event = zk.$extends(zk.Object, {
	$init: function (target, name, data, opts, domEvent) {
		this.currentTarget = this.target = target;
		this.name = name;
		this.data = data;
		if (data && typeof data == 'object' && !data.$array)
			zk.$default(this, data);

		this.opts = opts||{};
		if (this.domEvent = domEvent)
			this.domTarget = domEvent.target;
	},
	addOptions: function (opts) {
		this.opts = zk.copy(this.opts, opts);
	},
	stop: function (opts) {
		var b = !opts || !opts.revoke;
		if (!opts || opts.propagation) this.stopped = b;
		if (!opts || opts.dom) this.domStopped = b;
		if (opts && opts.au) this.auStopped = b;
	}
},{
	BS:		8,
	TAB:	9,
	ENTER:	13,
	SHIFT:	16,
	CTRL:	17,
	ALT:	18,
	ESC:	27,
	PGUP:	33,
	PGDN:	34,
	END:	35,
	HOME:	36,
	LFT:	37,
	UP:		38,
	RGH:	39,
	DN:		40,
	INS:	45,
	DEL:	46,
	F1:		112
});

zWatch = (function () {
	var _visiEvts = {onSize: true, onShow: true, onHide: true, beforeSize: true},
		_watches = {}, //Map(watch-name, [watch objects]
		_dirty;

	function _visible(name, c) {
		var n;
		return c.$n && (n=c.$n()) && zk(n).isRealVisible(name!='onShow');
		//if onShow, we don't check visibility since window uses it for
		//non-embedded window that becomes invisible because of its parent
	}
	function _visibleChild(name, p, c) {
		if (_visible(name, c))
			for (; c; c = c.parent)
				if (p == c) return true;
		return false;
	}
	function _target(inf) {
		return inf.$array ? inf[0]: inf;
	}
	function _fn(inf, o, name) {
		var fn = inf.$array ? inf[1]: o[name];
		if (!fn)
			throw name + ' not defined in '+(o.className || o);
		return fn;
	}
	function _sync() {
		if (!_dirty) return;

		_dirty = false;
		for (var nm in _watches) {
			var wts = _watches[nm];
			if (wts.length && _target(wts[0]).bindLevel != null)
				wts.sort(_cmpLevel);
		}
	}
	function _cmpLevel(a, b) {
		return _target(a).bindLevel - _target(b).bindLevel;
	}

  return {
	listen: function (infs) {
		for (name in infs) {
			var wts = _watches[name],
				inf = infs[name];
			if (wts) {
				var bindLevel = _target(inf).bindLevel;
				if (bindLevel != null) {
					for (var j = wts.length;;) {
						if (--j < 0) {
							wts.unshift(inf);
							break;
						}
						if (bindLevel >= _target(wts[j]).bindLevel) { //parent first
							wts.splice(j + 1, 0, inf);
							break;
						}
					}
				} else
					wts.push(inf);
			} else
				wts = _watches[name] = [inf];
		}
		return this;
	},
	unlisten: function (infs) {
		for (name in infs) {
			var wts = _watches[name];
			wts && wts.$remove(infs[name]); //$remove handles $array
		}
		return this;
	},
	unlistenAll: function (name) {
		delete _watches[name];
	},
	fire: function (name, opts, vararg) {
		var wts = _watches[name];
		if (wts && wts.length) {
			var args = [];
			for (var j = 2, l = arguments.length; j < l;)
				args.push(arguments[j++]);

			wts = wts.$clone(); //make a copy since unlisten might happen
			
			if (_visiEvts[name])
				for (var j = wts.length; j--;)
					if (!_visible(name, _target(wts[j])))
						wts.splice(j, 1);
						
			if (opts) {
				if (opts.timeout >= 0) {
					setTimeout(
					function () {
						var inf;
						while (inf = wts.shift()) {
							var o = _target(inf);
							_fn(inf, o, name).apply(o, args);
						}
					}, opts.timeout);
					return;
				}
			}

			var inf;
			while (inf = wts.shift()) {
				var o = _target(inf);
				_fn(inf, o, name).apply(o, args);
			}
		}
	},
	fireDown: function (name, opts, origin, vararg) {
		var wts = _watches[name];
		if (wts && wts.length) {
			_sync();

			var args = [origin]; //origin as 1st
			for (var j = 3, l = arguments.length; j < l;)
				args.push(arguments[j++]);

			var found, bindLevel = origin.bindLevel;
			if (bindLevel != null) {
				found = [];
				for (var j = wts.length; j--;) { //child first
					var inf = wts[j],
						o = _target(inf),
						diff = bindLevel > o.bindLevel;
					if (diff) break;//nor ancestor, nor this (&sibling)
					if (origin == o && _visible(name, o)) {
						found.unshift(inf);
						break; //found this (and no descendant ahead)
					}
					if (_visibleChild(name, origin, o))
						found.unshift(inf); //parent first
				}
			} else {
				found = wts.$clone(); //make a copy since unlisten might happen
				if (_visiEvts[name])
					for (var j = found.length; j--;)
						if (!_visible(name, found[j]))
							found.splice(j, 1);
			}

			if (opts && opts.timeout >= 0) {
				setTimeout(
				function () {
					var inf;
					while (inf = found.shift()) {
						var o = _target(inf);
						_fn(inf, o, name).apply(o, args);
					}
				}, opts.timeout);
				return;
			}

			var inf;
			while (inf = found.shift()) {
				var o = _target(inf);
				_fn(inf, o, name).apply(o, args);
			}
		}
	},
	onBindLevelMove: function () {
		_dirty = true;
	}
  };
})();
zWatch.listen({onBindLevelMove: zWatch});
