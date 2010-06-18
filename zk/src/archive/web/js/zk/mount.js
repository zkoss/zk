/* mount.js

	Purpose:
		
	Description:
		
	History:
		Sat Oct 18 19:24:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/

//define a package and returns the package info (used in WpdExtendlet)
function zkpi(nm, wv) {
	return {n: nm, p: zk.$package(nm, false, wv)};
}

//ZK JSP: page creation (backward compatible)
function zkpb(pguid, dtid, contextURI, updateURI, reqURI, props) {
	zkx([0, pguid,
		zk.copy(props, {dt: dtid, cu: contextURI, uu: updateURI, ru: reqURI}),[]]);
}
//ZK JSP (useless; backward compatible)
zkpe = zk.$void;

//Initializes with version and options
function zkver(ver, build, ctxURI, updURI, modVers, opts) {
	zk.version = ver;
	zk.build = build;
	zk.contextURI = ctxURI;
	zk.updateURI = updURI;

	for (var nm in modVers)
		zk.setVersion(nm, modVers[nm]);

	zk.feature = {standard: true};
	zkopt(opts);
}

//Define a mold
function zkmld(wgtcls, molds) {
	if (!wgtcls.superclass) {
		zk.afterLoad(function () {zkmld(wgtcls, molds);});
		return;
	}

	var ms = wgtcls.molds = {};
	for (var nm in molds) {
		var fn = molds[nm];
		ms[nm] = typeof fn == 'function' ? fn: fn[0].molds[fn[1]];
	}		
}

//Run Ajax-as-a-service's main
function zkamn(pkg, fn) {
	zk.load(pkg, function () {
		setTimeout(function(){
			zk.afterMount(fn);
		}, 20);
	});
}

//Handles z$ea and z$al. It is used for customization if necessary.
//@since 5.0.2
function zkmprops(uuid, props) {
	//z$ea: value embedded as element's child nodes
	var v = zk.cut(props, "z$ea");
	if (v) {
		var embed = jq(uuid, zk)[0];
		if (embed) {
			var val = [], n;
			while (n = embed.firstChild) {
				val.push(n);
				embed.removeChild(n);
			}
			props[v] = val;
		}
	}

	//z$al: afterLoad
	v = zk.cut(props, "z$al");
	if (v) {
		zk.afterLoad(function () {
			for (var p in v)
				props[p] = v[p](); //must be func
		});
	}
}

(function () {
	var _wgts = [],
		_createInf0 = [], //create info
		_createInf1 = [], //create info
		_aftMounts = [], //afterMount
		_mntctx = {}, //the context
		_qfns = {}, //queued functions (such as dependent pages, owner != null)
		_paci = {s: 0, e: -1, f0: [], f1: []}; //for handling page's AU responses

	//Issue of handling page's AU responses
	//1. page's AU must be processed after all zkx(), while they might be added
	//  before zkx (such as test/test.zhtml), or multiple zkx (such jspTags.jsp)
	//2. mount.js:_startCheck must be called after processing page's AU
	//  (otherwise, /zkdemo/userguide will jump to #f1 causing additional step)
	//Note: it is better to block zAu but the chance to be wrong is low --
	//a timer must be started early and its response depends page's AU
	_paci.i = setInterval(function () {
		var stateless;
		if ((zk.booted && !zk.mounting) || (stateless = _stateless()))
			if (stateless || _paci.s == _paci.e) { //done
				clearInterval(_paci.i);
				var fs = _paci.f0.concat(_paci.f1);
				_paci = null;
				for (var f; f = fs.shift();)
					f();
			} else
				_paci.e = _paci.s;
	}, 25);
	//run after page AU cmds
	zk._apac = function (fn, bCmd) {
		if (_paci)
			return _paci[bCmd ? "f0": "f1"].push(fn);
		fn();
	};
	function _stateless() {
		var dts = zk.Desktop.all;
		for (var dtid in dts)
			if (dts[dtid].stateless) return true;
	}

/** @partial zk
 */
//@{
	/** Adds a function that will be executed after the mounting is done. By mounting we mean the creation of peer widgets.
	 * <p>By mounting we mean the creation of the peer widgets under the
	 * control of the server. To run after the mounting of the peer widgets,
	 * <p>The function is executed with <code>setTimeout(fn, 0)</code> if the mounting has been done. 
	 * @param Function fn the function to execute after mounted
	 * @see #mounting
	 * @see #afterLoad
	 */
	//afterMount: function () {}
//@};
	zk.afterMount = function (fn) { //part of zk
		if (fn)  {
			if (zk.mounting)
				return _aftMounts.push(fn); //normal
			if (zk.loading)
				return zk.afterLoad(fn);
			if (!jq.isReady)
				return jq(fn);
			setTimeout(fn, 0);
		}
	};

	function _curdt() {
		return _mntctx.curdt || (_mntctx.curdt = zk.Desktop.$());
	}
	//Load all required packages
	function mountpkg() {
		for (var j = _createInf0.length; j--;) {
			var inf = _createInf0[j];
			if (!inf.pked) { //mountpkg might be called multiple times before mount()
				inf.pked = true;
				pkgLoad(inf[0], inf[1]);
			}
		}
	}
	//Loads package of a widget tree. Also handle z$pk
	function pkgLoad(dt, wi) {
		//z$pk: packages to load
		var v = zk.cut(wi[2], "z$pk");
		if (v) zk.load(v);

		var type = wi[0];
		if (type) { //not page (=0)
			if (type === 1) //1: zhtml.Widget
				wi[0] = type = "zhtml.Widget";
			var j = type.lastIndexOf('.');
			if (j >= 0)
				zk.load(type.substring(0, j), dt);
		}

		for (var children = wi[3], j = children.length; j--;)
			pkgLoad(dt, children[j]);
	}
	//create and mount widget
	function mount() {
		var stub = _createInf0.stub;
		if (stub) { //AU
			_createInf0.stub = null;
			mtAU(stub);
		} else //browser loading
			mtBL();
			//note: jq(mtBL) is a bit slow (too late to execute)
			//note: <div/> must be generated before <script/>
	}
	//mount for browser loading
	function mtBL() {
		if (zk.loading) {
			zk.afterLoad(mtBL);
			return;
		}

		var inf = _createInf0.shift();
		if (inf) {
			_createInf1.push([inf[0], create(inf[3]||inf[0], inf[1], true), inf[2]]);
				//inf[3]: owner passed from zkx
				//inf[0]: desktop used as default parent if no owner
				//true: don't update DOM
	
			if (_createInf0.length)
				return run(mtBL);
		}

		mtBL0();
	}
	function mtBL0() {
		for (;;) {
			if (_createInf0.length)
				return; //another page started

			if (zk.loading) {
				zk.afterLoad(mtBL0);
				return;
			}

			var inf = _createInf1.shift();
			if (!inf) break;

			var wgt = inf[1];
			if (inf[2]) wgt.bind(inf[0]); //binding
			else wgt.replaceHTML('#' + wgt.uuid, inf[0]);
		}

		mtBL1();
	}
	function mtBL1() {
		if (_createInf0.length || _createInf1.length)
			return; //another page started

		zk.booted = true;
		zk.mounting = false;
		doAfterMount(mtBL1);
		_paci && ++_paci.s;
		zk.endProcessing();

		zk.bmk.onURLChange();
		if (zk.pfmeter) {
			var dts = zk.Desktop.all;
			for (var dtid in dts)
				zAu._pfdone(dts[dtid], dtid);
		}
	}

	/* mount for AU */
	function mtAU(stub) {
		if (zk.loading) {
			zk.afterLoad(function () {mtAU(stub);});
			return;
		}

		var inf = _createInf0.shift();
		if (inf) {
			stub(create(null, inf[1]));
			if (_createInf0.length)
				return run(function () {mtAU(stub);}); //loop back to check if loading
		}

		mtAU0();
	}
	function mtAU0() {
		zk.mounting = false;
		doAfterMount(mtAU0);

		zAu._doCmds(); //server-push (w/ afterLoad) and _pfdone
		doAfterMount(mtAU0);
	}
	function doAfterMount(fnext) {
		for (var fn; fn = _aftMounts.shift();) {
			fn();
			if (zk.loading) {
				zk.afterLoad(fnext); //fn might load packages
				return true; //wait
			}
		}
	}

	function doAuCmds(cmds) {
		if (cmds && cmds.length)
			zk._apac(function () {
				for (var j = 0; j < cmds.length; j += 2)
					zAu.process(cmds[j], cmds[j + 1]);
			}, true);
	}

	/* create the widget tree. */
	function create(parent, wi, ignoreDom) {
		var wgt,
			type = wi[0],
			uuid = wi[1],
			props = wi[2]||{};
		if (type === 0) { //page
			wgt = new zk.Page({uuid: uuid}, zk.cut(props, "ct"));
			wgt.inServer = true;
			if (parent) parent.appendChild(wgt, ignoreDom);
		} else {
			var cls = zk.$import(type),
				initOpts = {uuid: uuid},
				v = wi[4]; //mold
			if (!cls)
				throw 'Unknown widget: ' + type;
			if (v) initOpts.mold = v;
			wgt = new cls(initOpts);
			wgt.inServer = true;
			if (parent) parent.appendChild(wgt, ignoreDom);

			zkmprops(uuid, props);
		}

		for (var nm in props)
			wgt.set(nm, props[nm]);

		for (var j = 0, childs = wi[3], len = childs.length;
		j < len; ++j)
			create(wgt, childs[j]);
		return wgt;
	}

	/* run and delay if too busy, so progressbox has a chance to show. */
	function run(fn) {
		var t = zUtl.now(), dt = t - zk._t1;
		if (dt > 2500) { //huge page (the shorter the longer to load; but no loading icon)
			zk._t1 = t;
			dt >>= 6;
			setTimeout(fn, dt < 10 ? dt: 10); //breathe
				//IE optimize the display if delay is too short
		} else
			fn();
	}

  zk.copy(window, {
	//define a desktop
	zkdt: function (dtid, contextURI, updateURI, reqURI) {
		var dt = zk.Desktop.$(dtid);
		if (dt == null) {
			dt = new zk.Desktop(dtid, contextURI, updateURI, reqURI);
			if (zk.pfmeter) zAu._pfrecv(dt, dtid);
		} else {
			if (updateURI != null) dt.updateURI = updateURI;
			if (contextURI != null) dt.contextURI = contextURI;
			if (reqURI != null) dt.requestPath = reqURI;
		}
		_mntctx.curdt = dt;
		return dt;
	},

	//widget creations
	zkx: function (wi, delay, aucmds, js) {
		zk.mounting = true;

		if (js) jq.globalEval(js);
		doAuCmds(aucmds);

		if (wi) {
			var owner;
			if (wi[0] === 0) { //page
				var props = wi[2];
				zkdt(zk.cut(props, "dt"), zk.cut(props, "cu"), zk.cut(props, "uu"), zk.cut(props, "ru"))
					._pguid = wi[1];
				if (owner = zk.cut(props, "ow"))
					owner = zk.Widget.$(owner);
			}

			_createInf0.push([_curdt(), wi, _mntctx.binding, owner]);

			mountpkg();
		}

		if (delay) setTimeout(mount, 0); //Bug 2983792 (delay until non-defer script evaluated)
		else run(mount);
	},
	//widget creation called by au.js
	zkx_: function (args, stub) {
		_createInf0.stub = stub;
		zk._t1 = zUtl.now(); //so run() won't do unncessary delay
		zkx.apply(window, args);
	},

	//queue a function to invoke by zkqx
	//@param id unique ID to identify the function, usually, widget's uuid
	zkq: function (id, fn) {
		_qfns[id] = fn;
	},
	//execute the function queued by zkq
	zkqx: function (id) {
		var fn = _qfns[id];
		if (fn) {
			delete _qfns[id];
			fn(id);
		}	
	},

	//Run AU commands (used only with ZHTML)
	zkac: function () {
		doAuCmds(arguments);
	},

	//begin of mounting
	zkmb: function (binding) {
		zk.mounting = true;
		_mntctx.binding = binding;
		var t = 390 - (zUtl.now() - zk._t1); //zk._t1 defined in util.js
		zk.startProcessing(t > 0 ? t: 0);
	},
	//end of mounting
	zkme: function () {
		_wgts = [];
		_mntctx.curdt = null;
		_mntctx.binding = false;
	}
  });

})(window);

//Event Handler//
jq(function() {
	var _bfUploads = [],
		_reszInf = {},
		_oldBfUnload;

	/** @partial zk
	 */
	zk.copy(zk, {
		/** Adds a function that will be executed when the browser is about to unload the document. In other words, it is called when window.onbeforeunload is called.
		 *
		 * <p>To remove the function, invoke this method by specifying remove to the opts argument.
<pre><code>zk.beforeUnload(fn, {remove: true});</code></pre>
		 *
		 * @param Function fn the function to execute.
		 * The function shall return null if it is OK to close, or a message (String) if it wants to show it to the end user for confirmation. 
		 * @param Map opts [optional] a map of options. Allowed vlaues:<br/>
		 * <ul>
		 * <li>remove: whether to remove instead of add.</li>
		 * </ul>
		 */
		beforeUnload: function (fn, opts) { //part of zk
			if (opts && opts.remove) _bfUploads.$remove(fn);
			else _bfUploads.push(fn);
		}
	});

	function _doEvt(wevt) {
		var wgt = wevt.target;
		if (wgt && !wgt.$weave) {
			var en = wevt.name;
			if (en == 'onClick' || en == 'onRightClick') {
				wgt.doSelect_(wevt);
				if (wevt.stopped)
					en = null; //denote stop
			}
			if (en)
				wgt['do' + en.substring(2) + '_'].call(wgt, wevt);
			if (wevt.domStopped)
				wevt.domEvent.stop();
		}
	}
	
	function _docMouseDown(evt, wgt, noFocusChange) {
		zk.clickPointer[0] = evt.pageX;
		zk.clickPointer[1] = evt.pageY;

		if (!wgt) wgt = evt.target;

		var target = evt.domTarget,
			body = document.body,
			old = zk.currentFocus;
		if ((target != body && target != body.parentNode) ||
				(evt.pageX < body.clientWidth && evt.pageY < body.clientHeight)) //not click on scrollbar
			zk.Widget.mimicMouseDown_(wgt, noFocusChange); //wgt is null if mask
			
		_doEvt(evt);
		
		// bug #2799334 and #2635555, we have to enforce to trigger a focus event. IE only
		if (old && zk.ie)
			setTimeout(function () {
				try {
					var cf = zk.currentFocus;
					if (cf != old && !old.offsetWidth && !old.offsetHeight)
						cf.focus();
				} catch (e) {}
			});
	}
	
	function _evtProxy(evt) { //handle proxy
		var n;
		
		// Firefox 3.5 will cause an error.
		try {
			if (((n = evt.target) && (n = n.z$proxy)) ||
			((n = evt.originalTarget) && (n = n.z$proxy))) 
				evt.target = n;
		} catch (e) {}
	}
	function _docResize() {
		if (!_reszInf.time) return; //already handled

		var now = zUtl.now();
		if (zk.mounting || zk.loading || now < _reszInf.time || zk.animating()) {
			setTimeout(_docResize, 10);
			return;
		}

		_reszInf.time = null; //handled
		_reszInf.lastTime = now + 1000;
			//ignore following for a while if processing (in slow machine)

		if (!zk.light) zAu._onClientInfo();

		_reszInf.inResize = true;
		try {
			zWatch.fire('beforeSize'); //notify all
			zWatch.fire('onSize'); //notify all
			_reszInf.lastTime = zUtl.now() + 8;
		} finally {
			_reszInf.inResize = false;
		}
	}

	jq(document)
	.keydown(function (evt) {
		//seems overkill: _evtProxy(evt);
		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onKeyDown', evt.keyData(), null, evt);
			_doEvt(wevt);
			if (!wevt.stopped && wgt.afterKeyDown_) {
				wgt.afterKeyDown_(wevt);
    			if (wevt.domStopped)
    				wevt.domEvent.stop();
			}
		}

		if (evt.keyCode == 27
		&& (zk._noESC > 0 || (!zk.light && zAu.shallIgnoreESC()))) //Bug 1927788: prevent FF from closing connection
			return false; //eat
	})
	.keyup(function (evt) {
		//seems overkill: _evtProxy(evt);
		var wgt = zk.keyCapture;
		if (wgt) zk.keyCapture = null;
		else wgt = zk.Widget.$(evt, {child:true});
		_doEvt(new zk.Event(wgt, 'onKeyUp', evt.keyData(), null, evt));
	})
	.keypress(function (evt) {
		//seems overkill: _evtProxy(evt);
		var wgt = zk.keyCapture;
		if (!wgt) wgt = zk.Widget.$(evt, {child:true});
		_doEvt(new zk.Event(wgt, 'onKeyPress', evt.keyData(), null, evt));
	})
	.mousedown(function (evt) {
		_evtProxy(evt);
		var wgt = zk.Widget.$(evt, {child:true});
		_docMouseDown(
			new zk.Event(wgt, 'onMouseDown', evt.mouseData(), null, evt),
			wgt);
	})
	.mouseup(function (evt) {
		var e = zk.Draggable.ignoreMouseUp(), wgt;
		if (e === true)
			return; //ingore

		if (e != null) {
			_docMouseDown(e, null, true); //simulate mousedown

			//simulate focus if zk.Draggable invokes evt.stop
			if ((wgt = e.target) && wgt != zk.currentFocus)
				try {wgt.focus();} catch (e) {}
				//Bug 3017606/2988327: don't invoke window.blur,or browser might be min (IE/FF)
		}

		_evtProxy(evt);
		wgt = zk.mouseCapture;
		if (wgt) zk.mouseCapture = null;
		else wgt = zk.Widget.$(evt, {child:true});
		_doEvt(new zk.Event(wgt, 'onMouseUp', evt.mouseData(), null, evt));
	})
	.mousemove(function (evt) {
		_evtProxy(evt);
		zk.currentPointer[0] = evt.pageX;
		zk.currentPointer[1] = evt.pageY;

		var wgt = zk.mouseCapture;
		if (!wgt) wgt = zk.Widget.$(evt, {child:true});
		_doEvt(new zk.Event(wgt, 'onMouseMove', evt.mouseData(), null, evt));
	})
	.mouseover(function (evt) {
		_evtProxy(evt);
		zk.currentPointer[0] = evt.pageX;
		zk.currentPointer[1] = evt.pageY;

		_doEvt(new zk.Event(zk.Widget.$(evt, {child:true}), 'onMouseOver', evt.mouseData(), null, evt));
	})
	.mouseout(function (evt) {
		_evtProxy(evt);
		_doEvt(new zk.Event(zk.Widget.$(evt, {child:true}), 'onMouseOut', evt.mouseData(), null, evt));
	})
	.click(function (evt) {
		if (zk.Draggable.ignoreClick()) return;

		zjq._fixClick(evt);

		_evtProxy(evt);
		if (evt.which == 1)
			_doEvt(new zk.Event(zk.Widget.$(evt, {child:true}),
				'onClick', evt.mouseData(), {ctl:true}, evt));
			//don't return anything. Otherwise, it replaces event.returnValue in IE (Bug 1541132)
	})
	.dblclick(function (evt) {
		if (zk.Draggable.ignoreClick()) return;

		_evtProxy(evt);
		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onDoubleClick', evt.mouseData(), {ctl:true}, evt);
			_doEvt(wevt);
			if (wevt.domStopped)
				return false;
		}
	})
	.bind("contextmenu", function (evt) {
		_evtProxy(evt);
		zk.clickPointer[0] = evt.pageX;
		zk.clickPointer[1] = evt.pageY;

		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onRightClick', evt.mouseData(), {ctl:true}, evt);
			_doEvt(wevt);
			if (wevt.domStopped)
				return false;
		}
		return !zk.ie || evt.returnValue;
	});

	jq(window).resize(function () {
		if (zk.mounting)
			return; //IE6: it sometimes fires an "extra" onResize in loading

	//Tom Yeh: 20051230:
	//1. In certain case, IE will keep sending onresize (because
	//grid/listbox may adjust size, which causes IE to send onresize again)
	//To avoid this endless loop, we ignore onresize a while if this method
	//was called
	//
	//2. IE keeps sending onresize when dragging the browser's border,
	//so we have to filter (most of) them out

		var now = zUtl.now();
		if ((_reszInf.lastTime && now < _reszInf.lastTime) || _reszInf.inResize)
			return; //ignore resize for a while (since onSize might trigger onsize)

		var delay = zk.ie ? 250: 50;
		_reszInf.time = now + delay - 1; //handle it later
		setTimeout(_docResize, delay);
	})
	.scroll(function () {
		zWatch.fire('onScroll'); //notify all
	})
	.unload(function () {
		zk.unloading = true; //to disable error message

		//20061109: Tom Yeh: Failed to disable Opera's cache, so it's better not
		//to remove the desktop.
		//Good news: Opera preserves the most udpated content, when BACK to
		//a cached page, its content. OTOH, IE/FF/Safari cannot.
		//Note: Safari/Chrome won't send rmDesktop when onunload is called
		var bRmDesktop = !zk.opera && !zk.keepDesktop && !zk.light;
		if (bRmDesktop || zk.pfmeter) {
			try {
				var dts = zk.Desktop.all;
				for (var dtid in dts) {
					var dt = dts[dtid];
					jq.ajax(zk.$default({
						url: zk.ajaxURI(null, {desktop:dt,au:true}),
						data: {dtid: dtid, cmd_0: bRmDesktop?"rmDesktop":"dummy", opt_0: "i"},
						beforeSend: function (xhr) {
							if (zk.pfmeter) zAu._pfsend(dt, xhr, true);
						}
					}, zAu.ajaxSettings));
				}
			} catch (e) { //silent
			}
		}
	});

	_oldBfUnload = window.onbeforeunload;
	window.onbeforeunload = function () {
		if (!zk.skipBfUnload) {
			if (zk.confirmClose)
				return zk.confirmClose;

			for (var j = 0; j < _bfUploads.length; ++j) {
				var s = _bfUploads[j]();
				if (s) return s;
			}
		}

		if (_oldBfUnload) {
			var s = _oldBfUnload.apply(window, arguments);
			if (s) return s;
		}

		zk.unloading = true; //FF3 aborts ajax before calling window.onunload
		//Return nothing
	};
}); //jq()
