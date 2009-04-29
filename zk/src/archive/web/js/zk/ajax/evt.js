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
zEvt = {
	target: function(evt) {
		evt = evt || window.event;
		return evt.target || evt.srcElement;
	},
	stop: function(evt) {
		evt = evt || window.event;
		if (evt.preventDefault) {
			evt.preventDefault();
			evt.stopPropagation();
		} else {
			evt.returnValue = false;
			evt.cancelBubble = true;
			if (!evt.shiftKey && !evt.ctrlKey)
				evt.keyCode = 0; //Bug 1834891
		}
	},

	leftClick: function(evt) {
		evt = evt || window.event;
		return evt.which == 1 || evt.button == 0 || evt.button == 1;
	},
	rightClick: function (evt) {
		evt = evt || window.event;
		return evt.which == 3 || evt.button == 2;
	},
	mouseData: function (evt, target) {
		evt = evt || window.event;
		var ofs = zDom.cmOffset(target ? target: zEvt.target(evt)),
			px = zEvt.x(evt), py = zEvt.y(evt);
		return zk.copy({
			x: px - ofs[0], y: py - ofs[1],
			pageX: px, pageY: py
			}, zEvt.metaData(evt));
	},
	keyData: function (evt) {
		evt = evt || window.event;
		return zk.copy({
			keyCode: zEvt.keyCode(evt),
			charCode: zEvt.charCode(evt)
			}, zEvt.metaData(evt));
	},
	metaData: function (evt) {
		evt = evt || window.event;
		var inf = {};
		if (evt.altKey) inf.altKey = true;
		if (evt.ctrlKey) inf.ctrlKey = true;
		if (evt.shiftKey) inf.shiftKey = true;
		if (zEvt.leftClick(evt)) inf.leftClick = true;
		if (zEvt.rightClick(evt)) inf.rightClick = true;
		return inf;
	},
	filterMetaData: function (data) {
		var inf = {}
		if (data.altKey) inf.altKey = true;
		if (data.ctrlKey) inf.ctrlKey = true;
		if (data.shiftKey) inf.shiftKey = true;
		if (data.leftClick) inf.leftClick = true;
		if (data.rightClick) inf.rightClick = true;
		return inf;
	},

	x: function (evt) {
		evt = evt || window.event;
		return evt.pageX || (evt.clientX +
			(document.documentElement.scrollLeft || document.body.scrollLeft));
  	},
	y: function(evt) {
		evt = evt || window.event;
		return evt.pageY || (evt.clientY +
			(document.documentElement.scrollTop || document.body.scrollTop));
	},
	pointer: function (evt) {
		return [zEvt.x(evt), zEvt.y(evt)];
	},

	charCode: function(evt) {
		evt = evt || window.event;
		return evt.charCode || evt.keyCode;
	},
	keyCode: function(evt) {
		evt = evt || window.event;
		var k = evt.keyCode || evt.charCode;
		return zk.safari ? (zEvt.safariKeys[k] || k) : k;
	},

	listen: zk.ie ? function (el, evtnm, fn) {
		el.attachEvent('on' + evtnm, fn);
	}: function (el, evtnm, fn) {
		el.addEventListener(evtnm, fn, false);
	},
	unlisten: zk.ie ? function (el, evtnm, fn) {
		try {
			el.detachEvent('on' + evtnm, fn);
		} catch (e) {
		}
	}: function (el, evtnm, fn) {
		el.removeEventListener(evtnm, fn, false);
	},
	fire: document.createEvent ? function (el, evtnm) {
		var evt = document.createEvent('HTMLEvents');
		evt.initEvent(evtnm, false, false);
		el.dispatchEvent(evt);
	}: function (el, evtnm) {
		el.fireEvent('on' + evtnm);
	},

	enableESC: function () {
		if (zDom._noESC) {
			zEvt.unlisten(document, "keydown", zDom._noESC);
			delete zDom._noESC;
		}
		if (zDom._onErrChange) {
			window.onerror = zDom._oldOnErr;
			if (zDom._oldOnErr) delete zDom._oldOnErr;
			delete zDom._onErrChange;
		}
	},
	disableESC: function () {
		if (!zDom._noESC) {
			zDom._noESC = function (evt) {
				evt = evt || window.event;
				if (evt.keyCode == 27) {
					zEvt.stop(evt);
					return false;//eat
				}
				return true;
			};
			zEvt.listen(document, "keydown", zDom._noESC);

			//FUTURE: onerror not working in Safari and Opera
			//if error occurs, loading will be never ended, so try to ignore
			//we cannot use zEvt.listen. reason: no way to get back msg...(FF)
			zDom._oldOnErr = window.onerror;
			zDom._onErrChange = true;
			window.onerror =
	function (msg, url, lineno) {
		//We display errors only for local class web resource
		//It is annoying to show error if google analytics's js not found
		var au = zAu.comURI();
		if (au && url.indexOf(location.host) >= 0) {
			var v = au.lastIndexOf(';');
			v = v >= 0 ? au.substring(0, v): au;
			if (url.indexOf(v + "/web/") >= 0) {
				msg = mesg.FAILED_TO_LOAD + url + "\n" + mesg.FAILED_TO_LOAD_DETAIL
					+ "\n" + mesg.CAUSE + msg+" (line "+lineno + ")";
				if (zk.error) zk.error(msg);
				else alert(msg);
				return true;
			}
		}
	};
		}
	}
};

if (zk.safari)
	zEvt.safariKeys = {
		25: 9, 	   // SHIFT-TAB
		63232: 38, // up
		63233: 40, // down
		63234: 37, // left
		63235: 39, // right
		63272: 46, // delete
		63273: 36, // home
		63275: 35, // end
		63276: 33, // pgup
		63277: 34  // pgdn
	};

zk.Event = zk.$extends(zk.Object, {
	$init: function (target, name, data, opts, domEvent) {
		this.currentTarget = this.target = target;
		this.name = name;
		this.data = data;
		if (data && typeof data == 'object' && !data.$array)
			zk.copy(this, data);

		this.opts = opts;
		this.domEvent = domEvent = domEvent || window.event;
		if (domEvent) this.domTarget = zEvt.target(domEvent);
	},
	addOptions: function (opts) {
		this.opts = zk.copy(this.opts, opts);
	},
	stop: function (opts) {
		var b = !opts || !opts.revoke;
		if (!opts || opts.propagation || !opts.dom) this.stopped = b;
		if (!opts || opts.dom || !opts.propagation) this.domStopped = b;
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
	listen: function (name, o) {
		var wts = zWatch._wts[name];
		if (wts) {
			var bindLevel = o.bindLevel;
			if (bindLevel != null) {
				for (var j = wts.length;;) {
					if (--j < 0) {
						wts.unshift(o);
						break;
					}
					if (bindLevel >= wts[j].bindLevel) { //parent first
						wts.$addAt(j + 1, o);
						break;
					}
				}
			} else
				wts.push(o);
		} else
			wts = zWatch._wts[name] = [o];
	},
	unlisten: function (name, o) {
		var wts = zWatch._wts[name];
		return wts && wts.$remove(o);
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
			if (opts) {
				if (opts.visible)
					for (var j = wts.length; --j >= 0;)
						if (!zWatch._visible(wts[j]))
							wts.splice(j, 1);

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

			var found, bindLevel = origin.bindLevel,
				visibleOnly = opts && opts.visible;
			if (bindLevel != null) {
				found = [];
				for (var j = wts.length, o; --j >= 0;) { //child first
					o = wts[j];
					var diff = bindLevel > o.bindLevel;
					if (diff > 0) break;//nor ancestor, nor this (&sibling)
					if (!diff && origin == o) {
						found.unshift(o);
						break; //found this (and no descendant ahead)
					}

					var fn = visibleOnly ? zWatch._visibleChild: zUtl.isAncestor;
					if (fn(origin, o)) found.unshift(o); //parent first
				}
			} else
				found = wts.$clone(); //make a copy since unlisten might happen

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
	_visibleChild: function (p, c) {
		for (; c; c = c.parent) {
			if (p == c) return true; //check parent before visible
			if (c.isVisible && !c.isVisible(true)) break;
		}
		return false;
	},
	_visible: function (w) {
		for (; w; w = w.parent)
			if (w.isVisible && !w.isVisible(true)) return false;
		return true;
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
zWatch.listen('onBindLevelMove', zWatch);
