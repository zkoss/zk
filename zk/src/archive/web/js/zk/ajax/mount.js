/* boot.js

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
	if (dtid) zkdtbg(dtid, updateURI).pguid = pguid;
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
		zPkg.version(args[j], args[j + 1]);
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
		return zkm._afMts.$add(fn);
	fn();
	return true;
}
zk.afterMount = zkam;

/** Used internally. */
zkm = {
	push: function(w) {
		w.children = [];
		if (zkm._wgts.length > 0)
			zkm._wgts[0].children.$add(w);
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

		zEvt.listen(document, "keydown", _zkDocKeyDown);

		zEvt.listen(document, "mousedown", _zkDocMouseDown);
		zEvt.listen(document, "mouseover", _zkDocMouseOver);
		zEvt.listen(document, "mouseout", _zkDocMouseOut);

		zEvt.listen(document, "click", _zkDocClick);
		zEvt.listen(document, "dblclick", _zkDocDblClick);
		zEvt.listen(document, "contextmenu", _zkDocCtxMenu);

		zEvt.listen(window, "scroll", _zkDocScroll);
		zEvt.listen(window, "resize", _zkDocResize);
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
				zkm.load();
			else if (document.readyState) {
				var tid = setInterval(function(){
					if (/loaded|complete/.test(document.readyState)) {
						clearInterval(tid);
						zkm.load();
					}
				}, 50);
			} else //gecko
				setTimeout(zkm.load, 120);
				//don't count on DOMContentLoaded since the page might
				//be loaded by another ajax solution (i.e., portal)
				//Also, Bug 1619959: FF not fire it if in 2nd iframe
		} else { //AU
			var stub = cfi.stub;
			cfi.stub = null;
			zkm.auNew(stub);
		}
	},
	/** create for browser loading. */
	load: function() {
		zPkg.afterLoad(zkm.load0);
	},
	load0: function() {
		if (!zk.sysInited) zkm.sysInit();

		var inf = zkm._crInf0.shift();
		if (inf) {
			zkm._crInf1.push([inf[0], zkm.create(null, inf[1])]);
			zkm.exec(zkm.load0);
			return;
		}

		zkm.load1();
	},
	load1: function() {
		var inf = zkm._crInf1.shift();
		if (inf) {
			var wgt = inf[1];
			wgt.replaceHTML(wgt.uuid, inf[0]);

			if (zkm._crInf1.length) {
				zkm.exec(zkm.load1);
				return;
			}
		}

		for (var fn, afcrs = zkm._afMts; fn = afcrs.shift();)
			fn();

		zk.mounting = false;
		zk.endProcessing();
	},

	/** create for AU cmds. */
	auNew: function (stub) {
		zPkg.afterLoad(function () {zkm.auNew0(stub);});
	},
	auNew0: function (stub) {
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
				wgt = new cls(uuid, wginf.mold),
				embedAs = cls.embedAs;
			wgt.inServer = true;
			if (parent) parent.appendChild(wgt);

			//embedAs means value from element's text
			if (embedAs && !props[embedAs]) {
				var embed = zDom.$(uuid);
				if (embed)
					props[embedAs] = embed.innerHTML;
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
	_afMts: [] //afterMount funcs
};
zke = zkpge = zkm.pop;

//Event Handler//
function _zkDocKeyDown(evt) {
	//TODO
}
function _zkDocMouseDown(evt) {
	var target = zEvt.target(evt);
	if (target != document.body && target != document.body.parentNode) { //not click on scrollbar
		var $Widget = zk.Widget;
		$Widget.domMouseDown($Widget.$(evt, true)); //null if mask
	}
}
function _zkDocMouseOver(evt) {
	//TODO
}
function _zkDocMouseOut(evt) {
	//TODO
}
function _zkDocClick(evt) {
	if (!evt) evt = window.event;

	if (zEvt.leftClick(evt)) {
		var wgt = zk.Widget.$(evt);
		for (; wgt; wgt = wgt.parent) {
			if (wgt.href) {
				zUtl.go(href, false, wgt.target, "target");
				return; //done
			}
			if (wgt.isListen('onClick')) {
				wgt.fire("onClick", zEvt.mouseData(evt, wgt.node), {ctl:true});
				return;
			}
		}
		//no need to Event.stop
	}
	//don't return anything. Otherwise, it replaces event.returnValue in IE (Bug 1541132)
}
function _zkDocDblClick(evt) {
	if (!evt) evt = window.event;

	var wgt = zk.Widget.$(evt);
	for (; wgt; wgt = wgt.parent)
		if (wgt.isListen('onDoubleClick')) {
			wgt.fire("onDoubleClick", zEvt.mouseData(evt, wgt.node), {ctl:true});
			return;
		}
}
function _zkDocCtxMenu(evt) {
}
function _zkDocScroll() {
}
function _zkDocResize() {
	if (!zk.sysInited || zk.mounting)
		return; //IE6: it sometimes fires an "extra" onResize in loading

	//Tom Yeh: 20051230:
	//1. In certain case, IE will keep sending onresize (because
	//grid/listbox may adjust size, which causes IE to send onresize again)
	//To avoid this endless loop, we ignore onresize a whilf if _reszfn
	//is called
	//
	//2. IE keeps sending onresize when dragging the browser's border,
	//so we have to filter (most of) them out

	var now = zUtl.now();
	if (_zkbResz.lastTime && now < _zkbResz.lastTime)
		return; //ignore resize for a while (since zk.onSizeAt might trigger onsize)

	var delay = zk.ie ? 250: 50;
	_zkbResz.time = now + delay - 1; //handle it later
	setTimeout(_zkDocDidResize, delay);
}
function _zkDocDidResize () {
	if (!_zkbResz.time) return; //already handled

	var now = zUtl.now();
	if (zk.loading || zAnima.count || now < _zkbResz.time) {
		setTimeout(_zkDocDidResize, 10);
		return;
	}

	_zkbResz.time = null; //handled
	_zkbResz.lastTime = now + 1000;
		//ignore following for a while if processing (in slow machine)

	zAu.clientInfoChange();

	zWatch.fire('beforeSize');
	zWatch.fire('onSize');
	_zkbResz.lastTime = zUtl.now() + 8;
};
var _zkbResz = {};
