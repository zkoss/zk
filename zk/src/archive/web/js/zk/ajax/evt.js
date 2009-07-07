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

		this.opts = opts;
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

zWatch = {
	_visibleEvent: {
		onSize: true, onShow: true, onHide: true, beforeSize: true
	},
	listen: function (infs) {
		for (name in infs) {
			var wts = zWatch._wts[name],
				o = infs[name];
			if (wts) {
				var bindLevel = o.bindLevel;
				if (bindLevel != null) {
					for (var j = wts.length;;) {
						if (--j < 0) {
							wts.unshift(o);
							break;
						}
						if (bindLevel >= wts[j].bindLevel) { //parent first
							wts.splice(j + 1, 0, o);
							break;
						}
					}
				} else
					wts.push(o);
			} else
				wts = zWatch._wts[name] = [o];
		}
	},
	unlisten: function (infs) {
		var found = false;
		for (name in infs) {
			var wts = zWatch._wts[name];
			if (wts && wts.$remove(infs[name]))
				found = true;
		}
		return found;
	},
	unlistenAll: function (name) {
		delete zWatch._wts[name];
	},
	fire: function (name, opts, vararg) {
		var wts = zWatch._wts[name];
		if (wts && wts.length) {
			var args = [];
			for (var j = 2, l = arguments.length; j < l;)
				args.push(arguments[j++]);

			wts = wts.$clone(); //make a copy since unlisten might happen
			
			if (zWatch._visibleEvent[name])
				for (var j = wts.length; --j >= 0;)
					if (!zWatch._visible(wts[j]))
						wts.splice(j, 1);
						
			if (opts) {
				if (opts.timeout >= 0) {
					setTimeout(
					function () {
						var o;
						while (o = wts.shift()) {
							var fn = o[name];
							if (!fn)
								throw name + ' not defined in '+(o.className || o);
							fn.apply(o, args);
						}
					}, opts.timeout);
					return;
				}
			}

			var o;
			while (o = wts.shift()) {
				var fn = o[name];
				if (!fn)
					throw name + ' not defined in '+(o.className || o);
				fn.apply(o, args);
			}
		}
	},
	fireDown: function (name, opts, origin, vararg) {
		var wts = zWatch._wts[name];
		if (wts && wts.length) {
			zWatch._sync();

			var args = [origin]; //origin as 1st
			for (var j = 3, l = arguments.length; j < l;)
				args.push(arguments[j++]);

			var found, bindLevel = origin.bindLevel;
			if (bindLevel != null) {
				found = [];
				for (var j = wts.length, o; --j >= 0;) { //child first
					o = wts[j];
					var diff = bindLevel > o.bindLevel;
					if (diff > 0) break;//nor ancestor, nor this (&sibling)
					if (!diff && origin == o && zWatch._visible(o)) {
						found.unshift(o);
						break; //found this (and no descendant ahead)
					}
					if (zWatch._visibleChild(origin, o)) found.unshift(o); //parent first
				}
			} else {
				found = wts.$clone(); //make a copy since unlisten might happen
				if (zWatch._visibleEvent[name])
					for (var j = found.length; --j >= 0;)
						if (!zWatch._visible(found[j]))
							found.splice(j, 1);
			}

			if (opts && opts.timeout >= 0) {
				setTimeout(
				function () {
					var o;
					while (o = found.shift()) {
						var fn = o[name];
						if (!fn)
							throw name + ' not defined in '+(o.className || o);
						fn.apply(o, args);
					}
				}, opts.timeout);
				return;
			}

			var o;
			while (o = found.shift()) {
				var fn = o[name];
				if (!fn)
					throw name + ' not defined in '+(o.className || o);
				fn.apply(o, args);
			}
		}
	},
	_visible: function (c) {
		return c.getNode && c.getNode() && zk(c.getNode()).isRealVisible();
	},
	_visibleChild: function (p, c) {
		if (zWatch._visible(c))
			for (; c; c = c.parent)
				if (p == c) return true;
		
		return false;
	},
	onBindLevelMove: function () {
		zWatch._dirty = true;
	},
	_sync: function () {
		if (!zWatch._dirty) return;

		zWatch._dirty = false;
		for (var nm in zWatch._wts) {
			var wts = zWatch._wts[nm];
			if (wts.length && wts[0].bindLevel != null)
				wts.sort(zWatch._cmp);
		}
	},
	_cmp: function (a, b) {
		return a.bindLevel - b.bindLevel;
	},
	_wts: {}
};
zWatch.listen({onBindLevelMove: zWatch});
