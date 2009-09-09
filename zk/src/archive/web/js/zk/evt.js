/* evt.js

	Purpose:
		ZK Event and ZK Watch
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
		_dirty,
		_Gun = zk.$extends(zk.Object, {
			$init: function (name, infs, args, org) {
				this.name = name;
				this.infs = infs;
				this.args = args;
				this.origin = org;
			},
			fire: function (ref) {
				var inf,
					name = this.name,
					infs = this.infs,
					args = this.args;
				if (ref) {
					for (var j = 0, l = infs.length; j < l; ++j)
						if (_target(inf = infs[j]) == ref) {
							infs.splice(j, 1);
							_invoke(name, inf, args);
						}
				} else
					while (inf = infs.shift())
						_invoke(name, inf, args);
			},
			fireDown: function (ref) {
				if (!ref || ref.bindLevel == null)
					this.fire(ref);

				(new _Gun(this.name, _visiChildSubset(this.infs, ref), this.args, this.origin))
				.fire();
			}
		});

	function _invoke(name, inf, args) {
		var o = _target(inf);
		_fn(inf, o, name).apply(o, args);
	}
	//Returns if c is visible
	function _visible(name, c) {
		var n;
		return c.$n && (n=c.$n()) && zk(n).isRealVisible(name!='onShow');
		//if onShow, we don't check visibility since window uses it for
		//non-embedded window that becomes invisible because of its parent
	}
	//Returns if c is a visible child of p
	function _visibleChild(name, p, c) {
		if (_visible(name, c))
			for (; c; c = c.parent)
				if (p == c) return true;
		return false;
	}
	//Returns subset of infs that are visible childrens of p
	function _visiChildSubset(infs, p) {
		var bindLevel = p.bindLevel;
		if (bindLevel == null)
			return _visiSubset(infs);

		var found = [];
		for (var j = infs.length; j--;) { //child first
			var inf = infs[j],
				o = _target(inf),
				diff = bindLevel > o.bindLevel;
			if (diff) break;//nor ancestor, nor this (&sibling)
			if (p == o && _visible(name, o)) {
				found.unshift(inf);
				break; //found this (and no descendant ahead)
			}
			if (_visibleChild(name, p, o))
				found.unshift(inf); //parent first
		}
		return found;
	}
	function _visiSubset(infs) {
		infs = infs.$clone(); //make a copy since unlisten might happen
		if (_visiEvts[name])
			for (var j = infs.length; j--;)
				if (!_visible(name, _target(infs[j])))
					infs.splice(j, 1);
		return infs;
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
	fire: function (name, org, opts) {
		this._fire(name, org, opts, arguments);
	},
	fireDown: function (name, org, opts) {
		this._fire(name, org, zk.copy(opts,{down:true}), arguments);
	},
	_fire: function (name, org, opts, vararg) {
		var wts = _watches[name];
		if (wts && wts.length) {
			var down = opts && opts.down;
			if (down) _sync();

			var args = [],
				gun = new _Gun(name,
					down ? _visiChildSubset(wts, org): _visiSubset(wts),
					args, org);
			args.push(gun);
			for (var j = 3, l = vararg.length; j < l;)
				args.push(vararg[j++]);

			if (opts && opts.timeout >= 0)
				setTimeout(function () {gun.fire();}, opts.timeout);
			else
				gun.fire();
		}
	},
	onBindLevelMove: function () {
		_dirty = true;
	}
  };
})();
zWatch.listen({onBindLevelMove: zWatch});
