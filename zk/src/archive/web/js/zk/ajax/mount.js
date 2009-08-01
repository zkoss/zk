/* mount.js

	Purpose:
		
	Description:
		
	History:
		Sat Oct 18 19:24:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zkm = {
	t: zUtl.now() //when JS loaded
};
zkreg = zk.Widget.register; //a shortcut for WPD loader

function zkblbg(binding) {
	zk.mounting = true;
	zkm.binding = binding;
	var t = 390 - (zUtl.now() - zkm.t);
	zk.startProcessing(t > 0 ? t: 0);
}

function zkpgbg(pguid, style, dtid, contained, contextURI, updateURI) {
	var props = {};
	if (style) props.style = style;
	if (dtid) zkdtbg(dtid, contextURI, updateURI)._pguid = pguid;
	zkm.push({type: "#p", uuid: pguid, contained: contained, props: props});
}
function zkbg(type, uuid, mold, props) {
	zkm.push({type: type, uuid: uuid, mold: mold, props: props});
}
function zkb2(uuid, type, props) { //zhtml
	zkm.push({type: type||'zhtml.Widget', uuid: uuid, props: props});
}
function zkdtbg(dtid, contextURI, updateURI) {
	var dt = zk.Desktop.$(dtid);
	if (dt == null) {
		dt = new zk.Desktop(dtid, contextURI, updateURI);
		if (zk.pfmeter) zAu._pfrecv(dt, dtid);
	} else {
		if (updateURI) dt.updateURI = updateURI;
		if (contextURI) dt.contextURI = contextURI;
	}
	zkm.curdt = dt;
	return dt;
}

//Init Only//
function zkver() {
	var args = arguments, len = args.length;
	zk.version = args[0];
	zk.build = args[1];
	zk.contextURI = args[2];
	zk.updateURI = args[3];

	for (var j = 4; j < len; j += 2)
		zk.setVersion(args[j], args[j + 1]);
}

function zkopt(opts) {
	zk.feature = {standard: true};

	for (var nm in opts) {
		var val = opts[nm];
		switch (nm) {
		case "pd": zk.procDelay = val; break;
		case "td": zk.tipDelay =  val; break;
		case "rd": zk.resendDelay = val; break;
		case "dj": zk.debugJS = val; break;
		case "kd": zk.keepDesktop = val; break;
		case "pf": zk.pfmeter = val; break;
		case "cd": zk.clickFilterDelay = val; break;
		case "ed":
			switch (val) {
			case 'e':
				zk.feature.enterprise = true;
			case 'p':
				zk.feature.professional = true;
			}
		}
	}
}

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

(function () { //zkm
	var _wgts = [],
		_createInf0 = [], //create info
		_createInf1 = [], //create info
		_aftMounts = []; //afterMount funcs

	zk.afterMount = function (fn) { //part of zk
		if (zk.mounting || !jq.isReady) {
			_aftMounts.push(fn);
			return true;
		}
		setTimeout(fn, 0);
	};

	function mount() {
		//1. load JS
		for (var j = _createInf0.length; j--;) {
			var inf = _createInf0[j];
			if (!inf.jsLoad) {
				inf.jsLoad = true;
				pkgLoad(inf[1]);
				run(mount);
				return;
			}
		}

		//2. create wgt
		var stub = _createInf0.stub;
		if (stub) { //AU
			_createInf0.stub = null;
			mtAU(stub);
		} else { //browser loading
			zk.bootstrapping = true;
			jq(mtBL);
		}
	}
	/** mount for browser loading */
	function mtBL() {
		if (zk.loading) {
			zk.afterLoad(mtBL);
			return;
		}

		var inf = _createInf0.shift();
		if (inf) {
			_createInf1.push([inf[0], create(inf[0], inf[1]), inf[2]]);
				//desktop as parent for browser loading
	
			if (_createInf0.length)
				return run(mtBL);
		}

		mtBL0();
	}
	function mtBL0() {
		if (_createInf0.length)
			return; //another page started

		if (zk.loading) {
			zk.afterLoad(mtBL0);
			return;
		}

		var inf = _createInf1.shift();
		if (inf) {
			var wgt = inf[1];
			if (inf[2]) wgt.bind(inf[0]); //binding
			else wgt.replaceHTML('#' + wgt.uuid, inf[0]);
			return run(mtBL0); //loop back to check if loading
		}

		setTimeout(mtBL1, 0);
			//use timeout since there might be multiple zkblbg
	}
	function mtBL1() {
		if (_createInf0.length || _createInf1.length)
			return; //another page started

		zk.mounting = zk.bootstrapping = false;
		doAfterMount(mtBL1);
		zk.endProcessing();

		zHistory.onURLChange();
		if (zk.pfmeter) {
			var dts = zk.Desktop.all;
			for (var dtid in dts)
				zAu._pfdone(dts[dtid], dtid);
		}
	}

	/** mount for AU */
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
				return;
			}
		}
	}

	/** create the widget tree. */
	function create(parent, wginf) {
		var wgt, props = wginf.props || {};
		if (wginf.type == "#p") {
			wgt = new zk.Page({uuid: wginf.uuid}, wginf.contained);
			wgt.inServer = true;
			if (parent) parent.appendChild(wgt);
		} else {
			var cls = zk.$import(wginf.type),
				uuid = wginf.uuid,
				initOpts = {uuid: uuid},
				v = wginf.mold;
			if (!cls)
				throw 'Unknown widget: ' + wginf.type;
			if (v) initOpts.mold = v;
			var wgt = new cls(initOpts);
			wgt.inServer = true;
			if (parent) parent.appendChild(wgt);

			//z_ea: value embedded as element's text
			v = props.z_ea;
			if (v) {
				delete props.z_ea;
				var embed = jq(uuid);
				if (embed.length) {
					var val = embed[0].innerHTML;
					if (v.charAt(0) == '$') { //decode
						v = v.substring(1);
						val = zUtl.decodeXML(val);
					}
					props[v] = val;
				}
			}

			//z_al: afterLoad
			v = props.z_al;
			if (v) {
				delete props.z_al;
				zk.afterLoad(function () {
					for (var p in v)
						props[p] = v[p](); //must be func
				});
			}
		}

		for (var nm in props)
			wgt.set(nm, props[nm]);

		for (var j = 0, childs = wginf.children, len = childs.length;
		j < len; ++j)
			create(wgt, childs[j]);
		return wgt;
	}

	/** Loads package of a widget tree. */
	function pkgLoad(w) {
		var type = w.type, i = type.lastIndexOf('.');
		if (i >= 0)
			zk.load(type.substring(0, i), zkm.curdt);

		//z_pk: pkgs to load
		var pkgs = w.z_pk;
		if (pkgs) {
			delete w.z_pk;
			zk.load(pkgs);
		}

		for (var children = w.children, len = children.length, j = 0; j < len;++j)
			pkgLoad(children[j]);
	}

	/** run and delay if too busy, so progressbox has a chance to show. */
	function run(fn) {
		var t = zUtl.now(), dt = t - zkm.t;
		if (dt > 300) { //huge page
			zkm.t = t;
			dt >>= 4;
			setTimeout(fn, dt < 35 ? dt: 35); //breathe
				//IE optimize the display if delay is too short
		} else
			fn();
	}

zk.copy(zkm, { //Use internally
	push: function(w) {
		w.children = [];
		if (_wgts.length)
			_wgts[0].children.push(w); //last child of top
		_wgts.unshift(w); //become top
	},
	pop: function() {
		var w = _wgts.shift();
		if (!_wgts.length) {
			_createInf0.push([zkm.curdt, w, zkm.binding]);
			_createInf0.stub = zAu.stub;
			zAu.stub = null;
			run(mount);
		}
	},
	top: function() {
		return _wgts[0];
	},
	end: function() {
		_wgts = [];
		zkm.curdt = null;
		zkm.binding = false;
	}
});
})(); //zkm

//Event Handler//
jq(function() {
	var _bfUploads = [],
		_reszInf = {},
		_oldBfUnload;

	zk.copy(zk, {
		beforeUnload: function (fn, opts) { //part of zk
			if (opts && opts.remove) _bfUploads.$remove(fn);
			else _bfUploads.push(fn);
		}
	});

	function _doEvt(wevt) {
		var wgt = wevt.target,
			post = wevt.name.substring(2) + '_';
		if (wgt.inDesign) {
			var f = wgt['doDesign' + post];
			if (f) f.call(wgt, wevt);
		} else
			wgt['do' + post].call(wgt, wevt);

		if (wevt.domStopped)
			wevt.domEvent.stop();
	}
	function docMouseDown(evt) {
		_evtProxy(evt);
		var wgt = zk.Widget.$(evt, {child:true});
		_docMouseDown(
			new zk.Event(wgt, 'onMouseDown', evt.mouseData(), null, evt),
			wgt);
	}
	function _docMouseDown(evt, wgt, fake) {
		zk.lastPointer[0] = evt.pageX;
		zk.lastPointer[1] = evt.pageY;

		if (!wgt) wgt = evt.target;

		var target = evt.domTarget;
		if (target != document.body && target != document.body.parentNode) //not click on scrollbar
			zk.Widget._domMouseDown(wgt, fake); //wgt is null if mask

		if (wgt)
			_doEvt(evt);
	}
	function docMouseUp(evt) {
		var e = zk.Draggable.ignoreMouseUp();
		if (e === true)
			return; //ingore
		if (e != null) {
			_docMouseDown(e, null, true); //simulate mousedown
			_simFocus(e.target); //simulate focus
		}

		_evtProxy(evt);
		var wgt = zk.mouseCapture;
		if (wgt) zk.mouseCapture = null;
		else wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			_doEvt(new zk.Event(wgt, 'onMouseUp', evt.mouseData(), null, evt));
	}
	function _simFocus(wgt) {
		var cf = zk.currentFocus;
		if (!cf || wgt == cf)
			return;

		for (var fcfn = zk.Widget.prototype.focus, focused;; wgt = wgt.parent) {
			if (!wgt) {
				window.blur();
				if (cf == zk.currentFocus)
					for (var n = cf.$n(),
					tns = ['INPUT','A','BUTTON'], j = tns.length; --j >=0;) {
						var ns = n.getElementsByTagName(tns[j]);
						if (ns.length > 0) {
							zk(ns[0]).focus();
							break;
						}
					}
				break;
			}
			if (!focused || wgt.focus !== fcfn) {
				focused = true;
				wgt.focus();
				if (cf != zk.currentFocus)
					break;
			}
		}
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
	function docMouseMove(evt) {
		_evtProxy(evt);
		zk.currentPointer[0] = evt.pageX;
		zk.currentPointer[1] = evt.pageY;

		var wgt = zk.mouseCapture;
		if (!wgt) wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			_doEvt(new zk.Event(wgt, 'onMouseMove', evt.mouseData(), null, evt));
	}
	function docMouseOver(evt) {
		_evtProxy(evt);
		zk.currentPointer[0] = evt.pageX;
		zk.currentPointer[1] = evt.pageY;

		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			_doEvt(new zk.Event(wgt, 'onMouseOver', evt.mouseData(), null, evt));
	}
	function docMouseOut(evt) {
		_evtProxy(evt);
		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			_doEvt(new zk.Event(wgt, 'onMouseOut', evt.mouseData(), null, evt));
	}
	function docKeyDown(evt) {
		//seems overkill: _evtProxy(evt);
		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onKeyDown', evt.keyData(), null, evt);
			_doEvt(wevt);
			if (!wgt.inDesign && !wevt.stopped && wgt.afterKeyDown_)
				wgt.afterKeyDown_(wevt);
		}

		if (evt.keyCode == 27
		&& (zk._noESC > 0 || zAu.shallIgnoreESC())) //Bug 1927788: prevent FF from closing connection
			return false; //eat
	}
	function docKeyUp(evt) {
		//seems overkill: _evtProxy(evt);
		var wgt = zk.keyCapture;
		if (wgt) zk.keyCapture = null;
		else wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			_doEvt(new zk.Event(wgt, 'onKeyUp', evt.keyData(), null, evt));
	}
	function docKeyPress(evt) {
		//seems overkill: _evtProxy(evt);
		var wgt = zk.keyCapture;
		if (!wgt) wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			_doEvt(new zk.Event(wgt, 'onKeyPress', evt.keyData(), null, evt));
	}
	function docClick(evt) {
		if (zk.processing || zk.Draggable.ignoreClick()) return;

		_evtProxy(evt);
		if (evt.which == 1) {
			var wgt = zk.Widget.$(evt, {child:true});
			if (wgt)
				_doEvt(new zk.Event(wgt, 'onClick', evt.mouseData(), {ctl:true}, evt));
			//don't return anything. Otherwise, it replaces event.returnValue in IE (Bug 1541132)
		}
	}
	function docDblClick(evt) {
		if (zk.processing || zk.Draggable.ignoreClick()) return;

		_evtProxy(evt);
		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onDoubleClick', evt.mouseData(), {ctl:true}, evt);
			_doEvt(wevt);
			if (wevt.domStopped)
				return false;
		}
	}
	function docCtxMenu(evt) {
		if (zk.processing) return;

		_evtProxy(evt);
		zk.lastPointer[0] = evt.pageX;
		zk.lastPointer[1] = evt.pageY;

		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onRightClick', evt.mouseData(), {ctl:true}, evt);
			_doEvt(wevt);
			if (wevt.domStopped)
				return false;
		}
		return !zk.ie || evt.returnValue;
	}
	function docScroll() {
		zWatch.fire('onScroll'); //notify all
	}
	function docResize() {
		if (!jq.isReady || zk.mounting)
			return; //IE6: it sometimes fires an "extra" onResize in loading

	//Tom Yeh: 20051230:
	//1. In certain case, IE will keep sending onresize (because
	//grid/listbox may adjust size, which causes IE to send onresize again)
	//To avoid this endless loop, we ignore onresize a while if docResize
	//was called
	//
	//2. IE keeps sending onresize when dragging the browser's border,
	//so we have to filter (most of) them out

		var now = zUtl.now();
		if ((_reszInf.lastTime && now < _reszInf.lastTime) || _reszInf.inResize)
			return; //ignore resize for a while (since onSize might trigger onsize)

		var delay = zk.ie ? 250: 50;
		_reszInf.time = now + delay - 1; //handle it later
		setTimeout(docDidResize, delay);
	}
	function docDidResize() {
		if (!_reszInf.time) return; //already handled

		var now = zUtl.now();
		if (zk.mounting || zk.loading || now < _reszInf.time || zk.animating()) {
			setTimeout(docDidResize, 10);
			return;
		}

		_reszInf.time = null; //handled
		_reszInf.lastTime = now + 1000;
			//ignore following for a while if processing (in slow machine)

		zAu._onClientInfo();

		_reszInf.inResize = true;
		try {
			zWatch.fire('beforeSize'); //notify all
			zWatch.fire('onSize'); //notify all
			_reszInf.lastTime = zUtl.now() + 8;
		} finally {
			_reszInf.inResize = false;
		}
	}

	function docUnload() {
		zk.unloading = true; //to disable error message

		//20061109: Tom Yeh: Failed to disable Opera's cache, so it's better not
		//to remove the desktop.
		//Good news: Opera preserves the most udpated content, when BACK to
		//a cached page, its content. OTOH, IE/FF/Safari cannot.
		//Note: Safari won't send rmDesktop when onunload is called
		var bRmDesktop = !zk.opera && !zk.keepDesktop;
		if (bRmDesktop || zk.pfmeter) {
			try {
				var dts = zk.Desktop.all;
				for (var dtid in dts) {
					var dt = dts[dtid];
					jq.ajax(zk.$default({
						url: zk.ajaxURI(null, {desktop:dt,au:true}),
						data: {dtid: dtid, 'cmd_0': bRmDesktop?"rmDesktop":"dummy"},
						beforeSend: function (xhr) {
							if (zk.pfmeter) zAu._pfsend(dt, xhr, true);
						}
					}, zAu.ajaxSettings));
				}
			} catch (e) { //silent
			}
		}
	}
	function wndBfUnload() {
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
	}

	jq(document).keydown(docKeyDown)
		.keyup(docKeyUp)
		.keypress(docKeyPress)
		.mousedown(docMouseDown)
		.mouseup(docMouseUp)
		.mousemove(docMouseMove)
		.mouseover(docMouseOver)
		.mouseout(docMouseOut)
		.click(docClick)
		.dblclick(docDblClick)
		.bind("contextmenu", docCtxMenu);

	jq(window).resize(docResize)
		.scroll(docScroll)
		.unload(docUnload);

	_oldBfUnload = window.onbeforeunload;
	window.onbeforeunload = wndBfUnload;
}); //jq()

zkble = zkm.end;
zke = zkpge = zkm.pop;
