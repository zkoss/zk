/* mount.js

	Purpose:
		
	Description:
		
	History:
		Sat Oct 18 19:24:38     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
var _zbt = zUtl.now(); //JS loaded
function zknewbg() {
	zkm.creating = zk.mounting = true;
	var t = 600 - (zUtl.now() - _zbt);
	zk.startProcessing(t > 0 ? t: 0);
}
function zknewe() {
	zkm.end(); //clean up if failed
}

function zkpgbg(pguid, style, dtid, contained, updateURI) {
	zk.mounting = true;
	var props = {};
	if (style) props.style = style;
	if (dtid) zkdtbg(dtid, updateURI)._pguid = pguid;
	zkm.push({type: "#p", uuid: pguid, contained: contained, props: props});
}
function zkbg(type, uuid, mold, props) {
	zk.mounting = true;
	zkm.push({type: type, uuid: uuid, mold: mold, props: props});
}
function zkdtbg(dtid, updateURI) {
	var dt = zk.Desktop.$(dtid);
	if (dt == null) dt = new zk.Desktop(dtid, updateURI);
	else if (updateURI) dt.updateURI = updateURI;
	zkm.curdt = dt;
	return dt;
}

//Init Only//
function zkver() {
	var args = arguments, len = args.length;
	zk.version = args[0];
	zk.build = args[1];

	for (var j = 2; j < len; j += 2)
		zPkg.setVersion(args[j], args[j + 1]);
}

function zkopt(opts) {
	for (var nm in opts) {
		var val = opts[nm];
		switch (nm) {
		case "pd": zk.procDelay = val; break;
		case "td": zk.tipDelay =  val; break;
		case "rd": zk.resendDelay = val; break;
		case "dj": zk.debugJS = val; break;
		case "kd": zk.keepDesktop = val; break;
		case "pf": zk.pfmeter = val; break;
		}
	}
}

function zkam(fn) {
	if (zk.mounting)
		return zkm._afMts.push(fn);
	fn();
}
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
			cfi.push([zkm.curdt, w]);
			cfi.creating = zkm.creating;
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
		zkm.creating = false;
	},

	sysInit: function() {
		zk.sysInited = true;
		zkm.sysInit = null; //free

		zEvt.listen(document, "keydown", zkm.docKeyDown);
		zEvt.listen(document, "keyup", zkm.docKeyUp);
		zEvt.listen(document, "keypress", zkm.docKeyPress);

		zEvt.listen(document, "mousedown", zkm.docMouseDown);
		zEvt.listen(document, "mouseup", zkm.docMouseUp);
		zEvt.listen(document, "mousemove", zkm.docMouseMove);
		zEvt.listen(document, "mouseover", zkm.docMouseOver);
		zEvt.listen(document, "mouseout", zkm.docMouseOut);

		zEvt.listen(document, "click", zkm.docClick);
		zEvt.listen(document, "dblclick", zkm.docDblClick);
		zEvt.listen(document, "contextmenu", zkm.docCtxMenu);

		zEvt.listen(window, "resize", zkm.docResize);

		zkm._oldUnload = window.onunload;
		window.onunload = zkm.wndUnload; //unable to use zk.listen

		zkm._oldBfUnload = window.onbeforeunload;
		window.onbeforeunload = zkm.wndBfUnload;
	},
	mount: function() {
		var cfi = zkm._crInf0;
		for (var j = cfi.length; --j >= 0;) {
			var inf = cfi[j];
			if (!inf.jsLoad) {
				inf.jsLoad = true;
				zkm.pkgLoad(inf[1]); //OK to load JS before document.readyState complete
				zkm.exec(zkm.mount);
				return;
			}
		}

		if (cfi.creating) { //creating a new page
			if (zk.sysInited)
				zkm.mtNew();
			else if (document.readyState) {
				var tid = setInterval(function(){
					if (/loaded|complete/.test(document.readyState)) {
						clearInterval(tid);
						zkm.mtNew();
					}
				}, 50);
			} else //gecko
				setTimeout(zkm.mtNew, 120);
				//don't count on DOMContentLoaded since the page might
				//be loaded by another ajax solution (i.e., portal)
				//Also, Bug 1619959: FF not fire it if in 2nd iframe
		} else { //AU
			var stub = cfi.stub;
			cfi.stub = null;
			zkm.mtAU(stub);
		}
	},
	/** mount for browser loading new page. */
	mtNew: function() {
		zk.afterLoad(zkm.mtNew0);
	},
	mtNew0: function() {
		if (!zk.sysInited) zkm.sysInit();

		var inf = zkm._crInf0.shift();
		if (inf) {
			zkm._crInf1.push([inf[0], zkm.create(null, inf[1])]);
			return zkm.exec(zkm.mtNew0);
		}

		zkm.mtNew1();
	},
	mtNew1: function() {
		if (zkm._crInf0.length)
			return; //another page started

		var inf = zkm._crInf1.shift();
		if (inf) {
			var wgt = inf[1];
			wgt.replaceHTML(wgt.uuid, inf[0]);

			if (zkm._crInf1.length)
				return zkm.exec(zkm.mtNew1);
		}

		zk.afterLoad(zkm.mtNew2); //bind might load packages
	},
	mtNew2: function () {
		if (zkm._crInf0.length || zkm._crInf1.length)
			return; //another page started

		var fn = zkm._afMts.shift();
		if (fn) {
			fn();
			zk.afterLoad(zkm.mtNew2); //fn might load packages
			return;
		}

		zk.mounting = false;
		zk.endProcessing();
	},

	/** mount for AU responses. */
	mtAU: function (stub) {
		zk.afterLoad(function () {zkm.mtAU0(stub);});
	},
	mtAU0: function (stub) {
		for (var cfi = zkm._crInf0, inf; inf = cfi.shift();)
			stub(zkm.create(null, inf[1]));
		zk.mounting = false;
	},

	/** create the widget tree. */
	create: function (parent, wginf) {
		var wgt, props = wginf.props;
		if (wginf.type == "#p") {
			wgt = new zk.Page(wginf.uuid, wginf.contained);
			wgt.inServer = true;
			if (parent) parent.appendChild(wgt);
		} else {
			var cls = zk.$import(wginf.type),
				uuid = wginf.uuid,
				wgt = new cls(uuid, wginf.mold);
			wgt.inServer = true;
			if (parent) parent.appendChild(wgt);

			//embedAs means value from element's text
			var embedAs = props.z_ea;
			if (embedAs) {
				var embed = zDom.$(uuid);
				if (embed) {
					var val = embed.innerHTML;
					if (embedAs.charAt(0) == '$') { //decode
						embedAs = embedAs.substring(1);
						val = zUtl.decodeXML(val);
					}
					props[embedAs] = val;
				}
			}
		}

		//assign properties
		zk.set(wgt, props);

		for (var j = 0, childs = wginf.children, len = childs.length;
		j < len; ++j)
			zkm.create(wgt, childs[j]);
		return wgt;
	},

	/** Loads package of a widget tree. */
	pkgLoad: function (w) {
		var type = w.type; j = type.lastIndexOf('.');
		if (j >= 0)
			zPkg.load(type.substring(0, j), zkm.curdt);
		for (var children = w.children, len = children.length, j = 0; j < len;++j)
			zkm.pkgLoad(children[j]);
	},

	/** exec and delay if too busy, so progressbox has a chance to show. */
	exec: function (fn) {
		var t = zUtl.now(), dt = t - _zbt;
		if (dt > 500) { //huge page
			_zbt = t;
			dt >>= 4;
			setTimeout(fn, dt < 50 ? dt: 50); //breathe
				//IE optimize the display if delay is too short
		} else
			fn();
	},

	_wgts: [],
	_crInf0: [], //create info
	_crInf1: [], //create info
	_afMts: [], //afterMount funcs

	//Event Handler//
	docMouseDown: function (evt) {
		var target = zEvt.target(evt),
			$Widget = zk.Widget,
			wgt = $Widget.$(evt, true);
		zk.lastPointer[0] = zEvt.x(evt);
		zk.lastPointer[1] = zEvt.y(evt);

		if (target != document.body && target != document.body.parentNode) //not click on scrollbar
			$Widget.domMouseDown(wgt); //null if mask

		if (wgt)
			wgt.doMouseDown_(new zk.Event(wgt, 'onMouseDown', zEvt.mouseData(evt, wgt.getNode())), evt);
	},
	docMouseUp: function (evt) {
		var wgt = zk.mouseCapture;
		if (wgt) zk.mouseCapture = null;
		else wgt = zk.Widget.$(evt);
		if (wgt)
			wgt.doMouseUp_(new zk.Event(wgt, 'onMouseUp', zEvt.mouseData(evt, wgt.getNode())), evt);
	},
	docMouseMove: function (evt) {
		var wgt = zk.mouseCapture;
		if (!wgt) wgt = zk.Widget.$(evt);
		if (wgt)
			wgt.doMouseMove_(new zk.Event(wgt, 'onMouseMove', zEvt.mouseData(evt, wgt.getNode())), evt);
	},
	docMouseOver: function (evt) {
		zk.currentPointer[0] = zEvt.x(evt);
		zk.currentPointer[1] = zEvt.y(evt);

		var wgt = zk.Widget.$(evt);
		if (wgt)
			wgt.doMouseOver_(new zk.Event(wgt, 'onMouseOver', zEvt.mouseData(evt, wgt.getNode())), evt);
	},
	docMouseOut: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt)
			wgt.doMouseOut_(new zk.Event(wgt, 'onMouseOut', zEvt.mouseData(evt, wgt.getNode())), evt);
	},
	docKeyDown: function (evt) {
		var wgt = zk.Widget.$(evt);
		if (wgt)
			wgt.doKeyDown_(new zk.Event(wgt, 'onKeyDown', zEvt.keyData(evt)), evt);
	},
	docKeyUp: function (evt) {
		var wgt = zk.keyCapture;
		if (wgt) zk.keyCapture = null;
		else wgt = zk.Widget.$(evt);
		if (wgt)
			wgt.doKeyUp_(new zk.Event(wgt, 'onKeyUp', zEvt.keyData(evt)), evt);
	},
	docKeyPress: function (evt) {
		var wgt = zk.keyCapture;
		if (!wgt) wgt = zk.Widget.$(evt);
		if (wgt)
			wgt.doKeyPress_(new zk.Event(wgt, 'onKeyPress', zEvt.keyData(evt)), evt);
	},
	docClick: function (evt) {
		if (zk.processing) return;
		if (!evt) evt = window.event;

		var wgt = zk.Widget.$(evt);
		if (wgt)
			wgt.doClick_(new zk.Event(wgt, 'onClick', zEvt.mouseData(evt, wgt.getNode()), {ctl:true}), evt);
			//no need to zEvt.stop()
		//don't return anything. Otherwise, it replaces event.returnValue in IE (Bug 1541132)
	},
	docDblClick: function (evt) {
		if (zk.processing) return;
		if (!evt) evt = window.event;

		var wgt = zk.Widget.$(evt);
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onDoubleClick', zEvt.mouseData(evt, wgt.getNode()), {ctl:true});
			wgt.doDoubleClick_(wevt, evt);

			if (wevt.stopped) {
				zEvt.stop(evt); //prevent browser default
				return false;
			}
		}
	},
	docCtxMenu: function (evt) {
		if (zk.processing) return;
		if (!evt) evt = window.event;

		zk.lastPointer[0] = zEvt.x(evt);
		zk.lastPointer[1] = zEvt.y(evt);

		var wgt = zk.Widget.$(evt);
		if (wgt) {
			var wevt = new zk.Event(wgt, 'onRightClick', zEvt.mouseData(evt, wgt.getNode()), {ctl:true});
			wgt.doRightClick_(wevt, evt);

			if (wevt.stopped) {
				zEvt.stop(evt); //prevent browser default
				return false;
			}
		}
		return !zk.ie || evt.returnValue;
	},
	docResize: function () {
		if (!zk.sysInited || zk.mounting)
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
		if (resz.lastTime && now < resz.lastTime)
			return; //ignore resize for a while (since zk.onSizeAt might trigger onsize)

		var delay = zk.ie ? 250: 50;
		resz.time = now + delay - 1; //handle it later
		setTimeout(zkm.docDidResize, delay);
	},
	docDidResize: function () {
		var resz = zkm._resz;
		if (!resz.time) return; //already handled

		var now = zUtl.now();
		if (zk.mounting || zPkg.loading || zAnima.count || now < resz.time) {
			setTimeout(zkm.docDidResize, 10);
			return;
		}

		resz.time = null; //handled
		resz.lastTime = now + 1000;
			//ignore following for a while if processing (in slow machine)

		zAu._onClientInfo();

		zWatch.fire('beforeSize'); //notify all
		zWatch.fire('onSize'); //notify all
		resz.lastTime = zUtl.now() + 8;
	},
	_resz: {},

	wndUnload: function () {
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
					var req = zUtl.newAjax(),
						content = "dtid="+dtid+"&cmd.0="+
							(bRmDesktop?"rmDesktop":"dummy"),
						dt = dts[dtid],
						uri = zAu.comURI(null, dt);
					req.open("POST", zk.ie ? uri+"?"+content: uri, true);
					req.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
					if (zk.pfmeter) zAu._pfsend(dtid, req, true);
					if (zk.ie) req.send(null);
					else req.send(content);
				}
			} catch (e) { //silent
			}
		}

		if (zkm._oldUnload) zkm._oldUnload.apply(window, arguments);
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
	_bfs: []
};
zke = zkpge = zkm.pop;

zk.beforeUnload = function (fn, opts) { //part of zk
	if (opts && opts.remove) zkm._bfs.$remove(fn);
	else zkm._bfs.push(fn);
};
