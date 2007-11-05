/* gmaps.js

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 12 14:29:16     2006, Created by henrichen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
/* Due to the Gmaps limitation that we cannot create script element dynamically
	So, it is user's job to declare <script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=xxx"
	in a page that is not dynamically loaded.
zk.load(
	"http://maps.google.com/maps?file=api&v=2&key="+getZKAttr($e("zk_gmapsKey"),"key"),
	null,
	function () {
		return typeof GMap2 != "undefined";
	});
*/

////
zkGmaps = {};

function Gmaps_newGPoly(gmaps, elm) {
	var opt = new Array();
	var color = getZKAttr(elm, "cr");
	opt["color"] = color;
	var weight = getZKAttr(elm, "wg");
	opt["weight"] = weight;
	var points = getZKAttr(elm, "pts");
	opt["points"] = points
	var levels = getZKAttr(elm, "lvs");
	opt["levels"] = levels;
	var zoomFactor = parseInt(getZKAttr(elm, "zf"));
	opt["zoomFactor"] = zoomFactor;
	var numLevels = parseInt(getZKAttr(elm, "nlvs"));
	opt["numLevels"] = numLevels;

	var gpoly = new GPolyline.fromEncoded(opt);
	gmaps.addOverlay(gpoly);
	gpoly._elm = elm;
	elm._gpoly = gpoly;
}

function Gmaps_newGMarker(markpane, gmaps, elm) {
	var gicon = new GIcon(G_DEFAULT_ICON);
	var iimg = getZKAttr(elm, "iimg");
	if (iimg) {
		gicon.image = iimg;
	}
	var isdw = getZKAttr(elm, "isdw");
	if (isdw) {
		gicon.shadow = isdw;
	}
	var isz = Gmaps_parseIntArray(getZKAttr(elm, "isz"));
	if (isz) {
		gicon.iconSize = new GSize(isz[0], isz[1]);
	}
	var isdwsz = Gmaps_parseIntArray(getZKAttr(elm, "isdwsz"));
	if (isdwsz) {
		gicon.shadowSize = new GSize(isdwsz[0], isdwsz[1]);
	}
	var ianch = Gmaps_parseIntArray(getZKAttr(elm, "ianch"));
	if (ianch) {
		gicon.iconAnchor = new GPoint(ianch[0], ianch[1]);
	}
	var iinfanch = Gmaps_parseIntArray(getZKAttr(elm, "iinfanch"));
	if (iinfanch) {
		gicon.infoWindowAnchor = new GPoint(iinfanch[0], iinfanch[1]);
	}
	var iprtimg = getZKAttr(elm, "iprtimg");
	if (iprtimg) {
		gicon.printImage = iprtimg;
	}
	var imozprtimg = getZKAttr(elm, "imozprtimg");
	if (imozprtimg) {
		gicon.mozPrintImage = imozprtimg;
	}
	var iprtsdw = getZKAttr(elm, "iprtsdw");
	if (iprtsdw) {
		gicon.printShadow = iprtsdw;
	}
	var itrpt = getZKAttr(elm, "itrpt");
	if (itrpt) {
		gicon.transparent = itrpt;
	}
		var iimgmap = getZKAttr(elm, "iimgmap");
		
	var imaxhgt = getZKAttr(elm, "imaxhgt");
	if (imaxhgt) {
		gicon.maxHeight = parseInt(imaxhgt);
	}
	var idrgcrsimg = getZKAttr(elm, "idrgcrsimg");
	if (idrgcrsimg) {
		gicon.dragCrossImage = idrgcrsimg;
	}
	var idrgcrssz = Gmaps_parseIntArray(getZKAttr(elm, "idrgcrssz"));
	if (idrgcrssz) {
		gicon.dragCrossSize = new GSize(idrgcrssz[0], idrgcrssz[1]);
	}
	var idrgcrsanch = Gmaps_parseIntArray(getZKAttr(elm, "idrgcrsanch"));
	if (idrgcrsanch) {
		gicon.dragCrossAnchor = new GPoint(idrgcrsanch[0], idrgcrsanch[1]);
	}
	var anch = Gmaps_getLatLng(getZKAttr(elm, "anch"));
	var opt = new Array();
	opt["icon"] = gicon;
	
	//20070809, Henri Chen: Google Maps strange behavior:
	//  When GMaps info window is open, the new GMarker will "stick" on the maps
	//  so dragging the Gmarker will drag the whole map.
	//  To workaround, close the infowindow then reopen it
	var mp = $outer(gmaps.getContainer());
	var info = mp._curInfo;
	if (info) {
	    var zktype = getZKAttr(info, "type");
	    if (zktype.lastIndexOf("mark") < 0) { //!gmapsz.gmaps.Gmark
			gmaps.closeInfoWindow();
		} else {
			info = null;
		}
	}

	var drag = getZKAttr(elm, "drag");
	if (drag) {
		opt["draggable"] = true;
	}
	var title = elm.getAttribute("title");
	if (title) {
		opt["title"] = title;
	}
	var gmark = new GMarker(new GLatLng(anch[0], anch[1]), opt);
	gmaps.addOverlay(gmark);
	var mk = markpane.lastChild;
	mk.id = elm.id+"!real";
	gmark._elm = elm;
	mk._gmark = gmark;
	if (drag) {
		gmark.enableDragging();
	}
	
	//20070809, Henri Chen: workaround Google Maps starange behavior; reopen the info window.
	if (info) {
		Gmaps_openInfo(mp, info);
	}
	gmark._dragend = GEvent.addListener(gmark, "dragend", Gmarker_ondrop);
	gmark._dblclick = GEvent.addListener(gmark, "dblclick", Gmarker_ondblclick);
}

function Gmaps_parseIntArray(n) {
	if (!n) return null;
	var a = n.split(",");
	for (var j = 0; j < a.length; ++j) {
		a[j] = parseInt(a[j]);
	}
	return a;
}
		
/** get expected center from server */
function Gmaps_getLatLng(n) {
	if (!n) return null;
	var a = n.split(",");
	var len = a.length;
	var lat = parseFloat(a[0]);
	var lng = parseFloat(a[1]);
	if (len < 3) {
		return [lat, lng];
	}
	var zoom = parseInt(a[2]);
	if (len < 4) {
		return [lat, lng, zoom];
	}
	return [lat, lng, zoom, a[3]];
}

/** open the specified info window */
function Gmaps_openInfo(mp, elm) {
    var zktype = getZKAttr(elm, "type");
    mp._opening = elm;
    
    if (zktype.lastIndexOf("mark") >= 0) { //gmapsz.gmaps.Gmark
        var mk = $real(elm);
        mk._gmark.openInfoWindow(elm);
    } else { //gmapsz.gmaps.Ginfo
        var anch = Gmaps_getLatLng(getZKAttr(elm, "anch"));
        mp._gmaps.openInfoWindow(new GLatLng(anch[0], anch[1]), elm);
    }
}

/** onMarkerDrop event handler */
function Gmarker_ondrop() {
	var gmark = this;
	var elm = gmark._elm;
	var uuid = elm.id;
	var comp = elm;
	var center = gmark.getPoint();
	var lat = center.lat();
	var lng = center.lng();
	zkau.send({uuid: uuid, cmd: "onMarkerDrop", data: [lat, lng]}, zkau.asapTimeout(comp, "onMarkerDrop"));
}

function Gmarker_ondblclick() {
	var gmark = this;
	var elm = gmark._elm;
	var mp = $e(getZKAttr(elm, "pid"));
	Gmaps_dodblclick(mp, gmark, null);
}	

/** onMapMove event handler */
function Gmaps_onmove() {
	var gmaps = this;
	var mp = $outer(gmaps.getContainer());
	var uuid = mp.id;
	var comp = mp;
	var center = gmaps.getCenter();
	var lat = center.lat();
	var lng = center.lng();
	zkau.send({uuid: uuid, cmd: "onMapMove", data: [lat, lng]}, zkau.asapTimeout(comp, "onMapMove"));
}

/** onMapZoom event hander */
function Gmaps_onzoom() {
	var gmaps = this;
	var mp = $outer(gmaps.getContainer());
	var uuid = mp.id;
	var comp = mp;
	var zoom = gmaps.getZoom();
	zkau.send({uuid: uuid, cmd: "onMapZoom", data: [zoom]}, zkau.asapTimeout(comp, "onMapZoom"));
}

/** when GInfoWindow of the Google Maps is opened */
function Gmaps_oninfoopen() {
	var gmaps = this;
	var mp = $outer(gmaps.getContainer());

    if (mp._opening != mp._curInfo) { //The opening is not the current opened info, the current opened must close.
	    var elm = mp._opening;
	    var uuid = mp.id;
	    var comp = mp;
	  	zkau.send({uuid: uuid, cmd: "onInfoChange", data: [elm.id]}, zkau.asapTimeout(comp, "onInfoChange"));
	    mp._curInfo = elm;
	}
	mp._opening = null;
}

/** when GInfoWindow of the Google Maps is closed */
function Gmaps_oninfoclose() {
	var gmaps = this;
	var mp = $outer(gmaps.getContainer());
    if (mp._opening != mp._curInfo) { //The opening is not the current opened info, the current opened must close.
		var cave = $e(mp.id + "!cave");
	    cave.appendChild(mp._curInfo);
	    var uuid = mp.id;
	    var comp = mp;
  
		zkau.send({uuid: uuid, cmd: "onInfoChange", data: null}, zkau.asapTimeout(comp, "onInfoChange"));
        mp._curInfo = null;
	}
}

/** when Google Maps is clicked */
function Gmaps_onclick(gmark, point) {
	var gmaps = this;
	var mp = $outer(gmaps.getContainer());
	var uuid = mp.id;
	var comp = mp;
	if (gmark && !point) {
		point = gmark.getPoint();
	}
	var lat = point.lat();
	var lng = point.lng();
	var markid = gmark && gmark._elm ? gmark._elm.id : null;
	
	zkau.send({uuid: uuid, cmd: "onMapClick", data: [markid, lat, lng]}, zkau.asapTimeout(comp, "onMapClick"));
}

function Gmaps_ondblclick(gmark, point) {
	var mp = $outer(this.getContainer());
	Gmaps_dodblclick(mp, gmark, point);
}
	
function Gmaps_dodblclick(mp, gmark, point) {
	var uuid = mp.id;
	var comp = mp;
	if (gmark && !point) {
		point = gmark.getPoint();
	}
	var lat = point.lat();
	var lng = point.lng();
	var markid = gmark && gmark._elm ? gmark._elm.id : null;
	
	zkau.send({uuid: uuid, cmd: "onMapDoubleClick", data: [markid, lat, lng]}, zkau.asapTimeout(comp, "onMapDoubleClick"));
}

function Gmaps_addListeners(gmaps) {
	gmaps._moveend = GEvent.addListener(gmaps, "moveend", Gmaps_onmove);
	gmaps._zoomend = GEvent.addListener(gmaps, "zoomend", Gmaps_onzoom);
	gmaps._infowindowclose = GEvent.addListener(gmaps, "infowindowclose", Gmaps_oninfoclose);
	gmaps._infowindowopen = GEvent.addListener(gmaps, "infowindowopen", Gmaps_oninfoopen);
	gmaps._click = GEvent.addListener(gmaps, "click", Gmaps_onclick);
	gmaps._dblclick = GEvent.addListener(gmaps, "dblclick", Gmaps_ondblclick);
}

function Gmaps_removeListeners(gmaps) {
	GEvent.removeListener(gmaps._moveend);
	gmaps._moveend = null;

	GEvent.removeListener(gmaps._zoomend);
	gmaps._zoomend = null;

	GEvent.removeListener(gmaps._infowindowclose);
	gmaps._infowindowclose = null;

	GEvent.removeListener(gmaps._infowindowopen);
	gmaps._infowindowopen = null;

	GEvent.removeListener(gmaps._click);
	gmaps._click = null;

	GEvent.removeListener(gmaps._dblclick);
	gmaps._dblclick = null;
}
	
/** Init */
zkGmaps.init = function (mp) {
	if (window.GMap2 == null) {
		$real(mp).innerHTML =
			'<p>To use <code>&lt;gmaps&gt;</code>, you have to specify the following statement in one of your page(s) that is not dynmically loaded:</p>'
			+'<code>&lt;script src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=key-assigned-by-google" type="text/javascript"/&gt;</code>';
		return; //failed
	}

	if (GBrowserIsCompatible()) {
		var gmaps = new GMap2($real(mp));
		mp._gmaps = gmaps;

		//init the GMap2
		var center = Gmaps_getLatLng(getZKAttr(mp, "init"));
		gmaps.setCenter(new GLatLng(center[0], center[1]), center[2]);

		//set map type
		var mapType = getZKAttr(mp, "mt");
		zkGmaps.setMapType(gmaps, mapType);
		
		//prepare controls
		if (center.length > 3) {
			var ctrls = center[3];
			if (ctrls.indexOf("s") >= 0) {
				zkGmaps.setAttr(mp, "z.sctrl", "true");
			}
			if (ctrls.indexOf("t") >= 0) {
				zkGmaps.setAttr(mp, "z.tctrl", "true");
			}
			if (ctrls.indexOf("l") >= 0) {
				zkGmaps.setAttr(mp, "z.lctrl", "true");
			}
		}
		
		//prepare GInfoWindow and GMarker of the Google Maps
		var info = null;
		var markpane = gmaps.getPane(G_MAP_MARKER_PANE);
		var cave = $e(mp.id + "!cave");
		for (j=0;j<cave.childNodes.length;j++) {
			var elm = cave.childNodes[j];
			if ($tag(elm) == "SPAN") {
				var zktype = getZKAttr(elm, "type");
				if (zktype.lastIndexOf("mark") >= 0) { //gmapsz.gmaps.Gmark
					Gmaps_newGMarker(markpane, gmaps, elm);
				}
				if (zktype.lastIndexOf("poly") >= 0) { //gmapsz.gmaps.Gpoly
					Gmaps_newGPoly(gmaps, elm);
				}
				if (getZKAttr(elm, "open")) {
					info = elm;
				}
			}
		}

		//prepare the event listener
		Gmaps_addListeners(gmaps);
		
		//register once the onunload handler
		if (!zkGmaps._reg) {
			zkGmaps._reg = true; //register once only
			
			//chain zkGmaps into onunload chain
			zkGmaps._oldDocUnload = window.onunload;
			window.onunload = zkGmaps._onDocUnload; //unable to use zk.listen
		}
		
		if (info) {
			Gmaps_openInfo(mp, info);
		}
	}
		//Note: HTML is rendered first (before gmaps.js), so we have to delay
		//until now
};
zkGmaps.cleanup = function (mp) {
	if (window.GMap2 != null) {
		Gmaps_removeListeners(mp._gmaps);
		mp._gmaps = null;
		mp._curInfo = null;
		mp._lctrl = null;
		mp._sctrl = null;
		mp._tctrl = null;
	}
};

/** Called by the server to set the attribute. */
zkGmaps.setAttr = function (mp, name, value) {
    var gmaps = mp._gmaps;
	switch (name) {
	case "z.center": 
		var center = Gmaps_getLatLng(value);
		gmaps.setCenter(new GLatLng(center[0], center[1]));
		return true;
	case "z.panTo":
		var panTo = Gmaps_getLatLng(value);
		gmaps.panTo(new GLatLng(panTo[0], panTo[1]));
		return true;
	case "z.zoom":
		gmaps.setZoom(parseInt(value));
		return true;
	case "z.lctrl":
		if (value == "true" && !mp._lctrl) {
			mp._lctrl = new GLargeMapControl();
			gmaps.addControl(mp._lctrl);
		} else if (value != "true" && mp._lctrl) {
			gmaps.removeControl(mp._lctrl);
			mp._lctrl = null;
		}
		return true;
	case "z.sctrl":			
		if (value == "true" && !mp._sctrl) {
			mp._sctrl = new GSmallMapControl();
			gmaps.addControl(mp._sctrl);
		} else if (value != "true" && mp._sctrl) {
			gmaps.removeControl(mp._sctrl);
			mp._sctrl = null;
		}
		return true;
	case "z.tctrl":
		if (value == "true" && !mp._tctrl) {
			mp._tctrl = new GMapTypeControl();
			gmaps.addControl(mp._tctrl);
		} else if (value != "true" && mp._tctrl) {
			gmaps.removeControl(mp._tctrl);
			mp._tctrl = null;
		}
		return true;
    case "z.open":
        var elm = $e(value);
        Gmaps_openInfo(mp, elm);
        return true;
    case "z.close":
        gmaps._opening = null;
        gmaps.closeInfoWindow();
        return true;
    case "z.mt":
    	zkGmaps.setMapType(gmaps, value);
    	return true;
	}
	return false;
};

/** Handles document.unload. */
zkGmaps._onDocUnload = function () {
	if (window.GMap2 != null) {
		GUnload();
		zkGmaps._reg = null;
		if (zkGmaps._oldDocUnload) zkGmaps._oldDocUnload.apply(document);
	}
};

/** set map type. */
zkGmaps.setMapType = function (gmaps, mapType) {
	if (mapType) {
		switch (mapType) {
		case "normal":
			gmaps.setMapType(G_NORMAL_MAP);
			break;
		case "satellite":
			gmaps.setMapType(G_SATELLITE_MAP);
			break;
		case "hybrid":
			gmaps.setMapType(G_HYBRID_MAP);
		}
	}
};

////
zkGinfo = {};

/** Init */
zkGinfo.init = function (elm) {
	var mp = $e(getZKAttr(elm, "pid"));
	//when Ginfo.setContent(), elm is recreated
	//always take out the display:none
	elm.style.display = "";
	if (mp._gmaps /*&& mp._gmaps._ginfo == null*/) { //add or recreate
		if (elm.parentNode.id != (mp.id + "!cave")) { //append next to the currently opened elm
			var cave = $e(mp.id + "!cave");
			cave.appendChild(elm);
		}
		if (getZKAttr(elm, "open")) {
			Gmaps_openInfo(mp, elm);
		}
	}
		//when page loading, the zkGinfo.init is called before zkGmaps.init
		//the gmaps is not ready yet, so we cannot openInfoWindow here
};

/** Called by the server to remove self. */
zkGinfo.cleanup = function (elm) {
    //move it to  !cave and let zk javascript engine remove it later.
	var mp = $e(getZKAttr(elm, "pid"));
    
	var cave = $e(mp.id + "!cave");
    cave.appendChild(elm);
};

/** Called by the server to set the attribute. */
zkGinfo.setAttr = function (elm, name, value) {
	var mp = $e(getZKAttr(elm, "pid"));
	switch (name) {
	case "z.anch":
		setZKAttr(elm, "anch", value);
		if (mp._curInfo == elm) {
			Gmaps_openInfo(mp, elm);
		}
		return true;
	case "z.content":
		zk.setInnerHTML(elm, value);
		return true;
	}
		
	return false;
};


////
zkGmark = {};

/** Init */
zkGmark.init = function (elm) {
	//always take out the display:none
	elm.style.display = "";

	var mk = $real(elm);
	if (mk == elm) { //new added marker
		var mp = $e(getZKAttr(elm, "pid"));
		if (mp._gmaps) { //Google Maps is ready
			var markpane = mp._gmaps.getPane(G_MAP_MARKER_PANE);
			Gmaps_newGMarker(markpane, mp._gmaps, elm);
			if (elm.parentNode.id != (mp.id + "!cave")) {
				var cave = $e(mp.id + "!cave");
				cave.appendChild(elm);
			}
			if (getZKAttr(elm, "open")) {
				Gmaps_openInfo(mp, elm);
			}
		}
	}
		//when page loading, the zkGmark.init is called before zkGmaps.init
		//the gmaps is not ready yet, so we cannot addOverlay here.
};

/** Called by the server to remove self. */
zkGmark.cleanup = function (elm) {

	var mp = $e(getZKAttr(elm, "pid"));
	var mk = $real(elm);
	if (mk) {
		var gmark = mk._gmark;
		if (gmark) {
			if (gmark._dragend) {
				GEvent.removeListener(gmark._dragend);
				gmark._dragend = null;
			}
			if (gmark._dblclick) {
				GEvent.removeListener(gmark._dblclick);
				gmark._dblclick = null;
			}
			if (mp._curInfo == elm) { //currently opened info, close it first
				gmark.closeInfoWindow();
			}
			if (mp._gmaps)
				mp._gmaps.removeOverlay(gmark);
		}
	}

	//put it back to cave and ZK client engine will remove it for you.
	var cave = $e(mp.id + "!cave");
    cave.appendChild(elm);
};

/** Called by the server to set the attribute. */
zkGmark.setAttr = function (elm, name, value) {
	var mk = $real(elm);
	var gmark = mk._gmark;
	switch (name) {
	case "z.anch":
		setZKAttr(elm, "anch", value);
		var mp = $e(getZKAttr(elm, "pid"));
		var anch = Gmaps_getLatLng(value);
		gmark.setPoint(new GLatLng(anch[0], anch[1]));
		
		if (mp._curInfo == elm) {
			Gmaps_openInfo(mp, elm);
		}

		return true;
	case "z.content":
		zk.setInnerHTML(elm, value);
		return true;
    case "z.iimg":
    	gmark.setImage(value);
    	return true;
    case "z.drag":
    	if (value) {
    		gmark.enableDragging();
    	} else {
    		gmark.disableDragging();
    	}
    	return true;
	}
		
	return false;
};

////
zkGpoly = {};

/** Init */
zkGpoly.init = function (elm) {
	//always display:none
	elm.style.display = "none";

	var mp = $e(getZKAttr(elm, "pid"));
	if (mp._gmaps) { //Google Maps is ready
		Gmaps_newGPoly(mp._gmaps, elm);
		var cave = $e(mp.id + "!cave");
		cave.appendChild(elm);
	}
		//when page loading, the zkGpoly.init is called before zkGmaps.init
		//the gmaps is not ready yet, so we cannot addOverlay here.
};

/** Called by the server to remove self. */
zkGpoly.cleanup = function (elm) {
	var mp = $e(getZKAttr(elm, "pid"));
	var gpoly = elm._gpoly;
	if (gpoly) {
		if (mp._gmaps)
			mp._gmaps.removeOverlay(gpoly);
	}
};

