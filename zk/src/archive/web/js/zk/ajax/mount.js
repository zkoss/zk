/* mount.js

	Purpose:
		
	Description:
		
	History:
		Sat Oct 18 19:24:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
var _zkmt = zUtl.now(); //JS loaded

zkreg = zk.Widget.register; //a shortcut for WPD loader

function zkblbg(binding) {
	zk.mounting = true;
	zkm.binding = binding;
	var t = 390 - (zUtl.now() - _zkmt);
	zk.startProcessing(t > 0 ? t: 0);
}

function zkpgbg(pguid, style, dtid, contained, updateURI) {
	var props = {};
	if (style) props.style = style;
	if (dtid) zkdtbg(dtid, updateURI)._pguid = pguid;
	zkm.push({type: "#p", uuid: pguid, contained: contained, props: props});
}
function zkbg(type, uuid, mold, props) {
	zkm.push({type: type, uuid: uuid, mold: mold, props: props});
}
function zkb2(uuid, type, props) { //zhtml
	zkm.push({type: type||'zhtml.Widget', uuid: uuid, props: props});
}
function zkdtbg(dtid, updateURI) {
	var dt = zk.Desktop.$(dtid);
	if (dt == null) {
		dt = new zk.Desktop(dtid, updateURI);
		if (zk.pfmeter) zAu.pfrecv(dt, dtid);
	} else if (updateURI) dt.updateURI = updateURI;
	zkm.curdt = dt;
	return dt;
}

//Init Only//
function zkver() {
	var args = arguments, len = args.length;
	zk.version = args[0];
	zk.build = args[1];
	zk.updateURI = args[2];

	for (var j = 3; j < len; j += 2)
		zPkg.setVersion(args[j], args[j + 1]);
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
function zkam(fn) {
	if (zk.mounting || !jq.isReady) {
		zkm._afMts.push(fn);
		return true;
	}
	setTimeout(fn, 0);
};
zk.afterMount = zkam; //part of zk

/** Used internally. */
zkm = {
	push: function(w) {
		w.children = [];
		if (zkm._wgts.length > 0)
			zkm._wgts[0].children.push(w);
		zkm._wgts.unshift(w);
	},
	pop: function() {
		var w = zkm._wgts.shift();
		if (!zkm._wgts.length) {
			var cfi = zkm._crInf0;
			cfi.push([zkm.curdt, w, zkm.binding]);
			cfi.stub = zAu.stub;
			zAu.stub = null;
			zkm.exec(zkm.mount);
		}
	},
	top: function() {
		return zkm._wgts[0];
	},
	end: function() {
		zkm._wgts = [];
		zkm._curdt = null;
		zkm.binding = false;
	},

	mount: function() {
		//1. load JS
		var cfi = zkm._crInf0;
		for (var j = cfi.length; j--;) {
			var inf = cfi[j];
			if (!inf.jsLoad) {
				inf.jsLoad = true;
				zkm.pkgLoad(inf[1]);
				zkm.exec(zkm.mount);
				return;
			}
		}

		//2. create wgt
		var stub = cfi.stub;
		if (stub) { //AU
			cfi.stub = null;
			zkm.mtAU(stub);
		} else { //browser loading
			zk.bootstrapping = true;
			jq(zkm.mtBL);
		}
	},
	/** mount for browser loading */
	mtBL: function() {
		if (zPkg.loading) {
			zk.afterLoad(zkm.mtBL);
			return;
		}

		var crInf0 = zkm._crInf0,
			inf = crInf0.shift();
		if (inf) {
			zkm._crInf1.push([inf[0], zkm.create(inf[0], inf[1]), inf[2]]);
				//desktop as parent for browser loading
	
			if (crInf0.length)
				return zkm.exec(zkm.mtBL);
		}

		zkm.mtBL0();
	},
	mtBL0: function() {
		if (zkm._crInf0.length)
			return; //another page started

		if (zPkg.loading) {
			zk.afterLoad(zkm.mtBL0);
			return;
		}

		var crInf1 = zkm._crInf1,
			inf = crInf1.shift();
		if (inf) {
			var wgt = inf[1];
			if (inf[2]) wgt.bind(inf[0]); //binding
			else wgt.replaceHTML('#' + wgt.uuid, inf[0]);
			return zkm.exec(zkm.mtBL0); //loop back to check if loading
		}

		setTimeout(zkm.mtBL1, 0);
			//use timeout since there might be multiple zkblbg
	},
	mtBL1: function () {
		if (zkm._crInf0.length || zkm._crInf1.length)
			return; //another page started

		zk.mounting = zk.bootstrapping = false;
		zkm._afmt(zkm.mtBL1);
		zk.endProcessing();

		zHistory.onURLChange();
		if (zk.pfmeter) {
			var dts = zk.Desktop.all;
			for (var dtid in dts)
				zAu.pfdone(dts[dtid], dtid);
		}
	},

	/** mount for AU */
	mtAU: function (stub) {
		if (zPkg.loading) {
			zk.afterLoad(function () {zkm.mtAU(stub);});
			return;
		}

		var crInf0 = zkm._crInf0,
			inf = crInf0.shift();
		if (inf) {
			stub(zkm.create(null, inf[1]));
			if (crInf0.length)
				return zkm.exec(function () {zkm.mtAU(stub);}); //loop back to check if loading
		}

		zkm.mtAU0();
	},
	mtAU0: function () {
		zk.mounting = false;
		zkm._afmt(zkm.mtAU0);

		zAu.doCmds(); //server-push (w/ afterLoad) and pfdone
		zkm._afmt(zkm.mtAU0);
	},
	_afmt: function (fnext) {
		for (var fn; fn = zkm._afMts.shift();) {
			fn();
			if (zPkg.loading) {
				zk.afterLoad(fnext); //fn might load packages
				return;
			}
		}
	},

	/** create the widget tree. */
	create: function (parent, wginf) {
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

			//z_al: evaluated after load
			v = props.z_al;
			if (v) {
				delete props.z_al;
				for (var p in v)
					props[p] = v[p](); //must be func
			}
		}

		for (var nm in props)
			wgt.set(nm, props[nm]);

		for (var j = 0, childs = wginf.children, len = childs.length;
		j < len; ++j)
			zkm.create(wgt, childs[j]);
		return wgt;
	},

	/** Loads package of a widget tree. */
	pkgLoad: function (w) {
		var type = w.type, i = type.lastIndexOf('.');
		if (i >= 0)
			zPkg.load(type.substring(0, i), zkm.curdt);

		//z_pk: pkgs to load
		var pkgs = w.z_pk;
		if (pkgs) {
			delete w.z_pk;
			pkgs = pkgs.split(',');
			for (var j = 0, l = pkgs.length; j < l;)
				zPkg.load(pkgs[j++].trim());
		}

		for (var children = w.children, len = children.length, j = 0; j < len;++j)
			zkm.pkgLoad(children[j]);
	},

	/** exec and delay if too busy, so progressbox has a chance to show. */
	exec: function (fn) {
		var t = zUtl.now(), dt = t - _zkmt;
		if (dt > 300) { //huge page
			_zkmt = t;
			dt >>= 4;
			setTimeout(fn, dt < 35 ? dt: 35); //breathe
				//IE optimize the display if delay is too short
		} else
			fn();
	},

	_wgts: [],
	_crInf0: [], //create info
	_crInf1: [], //create info
	_afMts: [], //afterMount funcs

	//Event Handler//
	_doEvt: function (wevt) {
		var wgt = wevt.target,
			post = wevt.name.substring(2) + '_';
		if (wgt.inDesign) {
			var f = wgt['doDesign' + post];
			if (f) f.call(wgt, wevt);
		} else
			wgt['do' + post].call(wgt, wevt);

		if (wevt.domStopped)
			wevt.domEvent.stop();
	},
	docMouseDown: function (evt) {
		var wgt = zk.Widget.$(evt, {child:true});
		zkm._docMouseDown(
			new zk.Event(wgt, 'onMouseDown', evt.mouseData(), null, evt),
			wgt);
	},
	_docMouseDown: function (evt, wgt, fake) {
		zk.lastPointer[0] = evt.pageX;
		zk.lastPointer[1] = evt.pageY;

		if (!wgt) wgt = evt.target;

		var target = evt.domTarget;
		if (target != document.body && target != document.body.parentNode) //not click on scrollbar
			zk.Widget._domMouseDown(wgt, fake); //wgt is null if mask

		if (wgt)
			zkm._doEvt(evt);
	},
	docMouseUp: function (evt) {
		var e = zk.Draggable.ignoreMouseUp();
		if (e === true)
			return; //ingore
		if (e != null) {
			zkm._docMouseDown(e, null, true); //simulate mousedown
			zkm._simFocus(e.target); //simulate focus
		}

		var wgt = zk.mouseCapture;
		if (wgt) zk.mouseCapture = null;
		else wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			zkm._doEvt(new zk.Event(wgt, 'onMouseUp', evt.mouseData(), null, evt));
	},
	_simFocus: function (wgt) {
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
	},

	docMouseMove: function (evt) {
		zk.currentPointer[0] = evt.pageX;
		zk.currentPointer[1] = evt.pageY;

		var wgt = zk.mouseCapture;
		if (!wgt) wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			zkm._doEvt(new zk.Event(wgt, 'onMouseMove', evt.mouseData(), null, evt));
	},
	docMouseOver: function (evt) {
		zk.currentPointer[0] = evt.pageX;
		zk.currentPointer[1] = evt.pageY;

		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			zkm._doEvt(new zk.Event(wgt, 'onMouseOver', evt.mouseData(), null, evt));
	},
	docMouseOut: function (evt) {
		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			zkm._doEvt(new zk.Event(wgt, 'onMouseOut', evt.mouseData(), null, evt));
	},
	docKeyDown: function (evt) {
		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onKeyDown', evt.keyData(), null, evt);
			zkm._doEvt(wevt);
			if (!wgt.inDesign && !wevt.stopped && wgt.afterKeyDown_)
				wgt.afterKeyDown_(wevt);
		}

		if (evt.keyCode == 27
		&& (zkm._noESC > 0 || zAu.shallIgnoreESC())) //Bug 1927788: prevent FF from closing connection
			return false; //eat
	},
	docKeyUp: function (evt) {
		var wgt = zk.keyCapture;
		if (wgt) zk.keyCapture = null;
		else wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			zkm._doEvt(new zk.Event(wgt, 'onKeyUp', evt.keyData(), null, evt));
	},
	docKeyPress: function (evt) {
		var wgt = zk.keyCapture;
		if (!wgt) wgt = zk.Widget.$(evt, {child:true});
		if (wgt)
			zkm._doEvt(new zk.Event(wgt, 'onKeyPress', evt.keyData(), null, evt));
	},
	docClick: function (evt) {
		if (zk.processing || zk.Draggable.ignoreClick()) return;

		if (evt.which == 1) {
			var wgt = zk.Widget.$(evt, {child:true});
			if (wgt)
				zkm._doEvt(new zk.Event(wgt, 'onClick', evt.mouseData(), {ctl:true}, evt));
			//don't return anything. Otherwise, it replaces event.returnValue in IE (Bug 1541132)
		}		
	},
	docDblClick: function (evt) {
		if (zk.processing || zk.Draggable.ignoreClick()) return;

		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onDoubleClick', evt.mouseData(), {ctl:true}, evt);
			zkm._doEvt(wevt);
			if (wevt.domStopped)
				return false;
		}
	},
	docCtxMenu: function (evt) {
		if (zk.processing) return;

		zk.lastPointer[0] = evt.pageX;
		zk.lastPointer[1] = evt.pageY;

		var wgt = zk.Widget.$(evt, {child:true});
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onRightClick', evt.mouseData(), {ctl:true}, evt);
			zkm._doEvt(wevt);
			if (wevt.domStopped)
				return false;
		}
		return !zk.ie || evt.returnValue;
	},
	docScroll: function () {
		zWatch.fire('onScroll'); //notify all
	},
	docResize: function () {
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

		var now = zUtl.now(), resz = zkm._resz;
		if ((resz.lastTime && now < resz.lastTime) || resz._inResize)
			return; //ignore resize for a while (since onSize might trigger onsize)

		var delay = zk.ie ? 250: 50;
		resz.time = now + delay - 1; //handle it later
		setTimeout(zkm.docDidResize, delay);
	},
	docDidResize: function () {
		var resz = zkm._resz;
		if (!resz.time) return; //already handled

		var now = zUtl.now();
		if (zk.mounting || zPkg.loading || now < resz.time || zk.animating()) {
			setTimeout(zkm.docDidResize, 10);
			return;
		}

		resz.time = null; //handled
		resz.lastTime = now + 1000;
			//ignore following for a while if processing (in slow machine)

		zAu._onClientInfo();

		resz._inResize = true;
		try {
			zWatch.fire('beforeSize'); //notify all
			zWatch.fire('onSize'); //notify all
			resz.lastTime = zUtl.now() + 8;
		} finally {
			resz._inResize = false;
		}
	},
	_resz: {},

	docUnload: function () {
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
						url: zAu.comURI(null, dt),
						data: {dtid: dtid, 'cmd_0': bRmDesktop?"rmDesktop":"dummy"},
						beforeSend: function (xhr) {
							if (zk.pfmeter) zAu._pfsend(dt, xhr, true);
						}
					}, jq.zkAjax));
				}
			} catch (e) { //silent
			}
		}
	},
	wndBfUnload: function () {
		if (!zk.skipBfUnload) {
			if (zk.confirmClose)
				return zk.confirmClose;

			for (var bfs = zkm._bfs, j = 0; j < bfs.length; ++j) {
				var s = bfs[j]();
				if (s) return s;
			}
		}

		if (zkm._oldBfUnload) {
			var s = zkm._oldBfUnload.apply(window, arguments);
			if (s) return s;
		}

		zk.unloading = true; //FF3 aborts ajax before calling window.onunload
		//Return nothing
	},
	_bfs: [],
	_noESC: 0
};
zkble = zkm.end;
zke = zkpge = zkm.pop;

zk.copy(zk, {
	beforeUnload: function (fn, opts) { //part of zk
		if (opts && opts.remove) zkm._bfs.$remove(fn);
		else zkm._bfs.push(fn);
	},
	disableESC: function () {
		++zkm._noESC;
	},
	enableESC: function () {
		--zkm._noESC;
	}
});

jq(function() {
	jq(document).keydown(zkm.docKeyDown)
		.keyup(zkm.docKeyUp)
		.keypress(zkm.docKeyPress)
		.mousedown(zkm.docMouseDown)
		.mouseup(zkm.docMouseUp)
		.mousemove(zkm.docMouseMove)
		.mouseover(zkm.docMouseOver)
		.mouseout(zkm.docMouseOut)
		.click(zkm.docClick)
		.dblclick(zkm.docDblClick)
		.bind("contextmenu", zkm.docCtxMenu);

	jq(window).resize(zkm.docResize)
		.scroll(zkm.docScroll)
		.unload(zkm.docUnload);

	zkm._oldBfUnload = window.onbeforeunload;
	window.onbeforeunload = zkm.wndBfUnload;
});
