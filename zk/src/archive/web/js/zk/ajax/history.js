/* history.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov  3 19:34:25     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zHistory = {
	/** Sets a bookmark that user can use forward and back buttons */
	bookmark: function (nm) {
		if (zHistory._curbk != nm) {
			zHistory._curbk = nm; //to avoid loop back the server
			var encnm = encodeURIComponent(nm);
			location.hash = zk.safari || !encnm ? encnm: '#' + encnm;
			zHistory._bkIframe(nm);
			zHistory.onURLChange();
		}
	},
	/** Checks whether the bookmark is changed. */
	checkBookmark: function() {
		var nm = zHistory.getBookmark();
		if (nm != zHistory._curbk) {
			zHistory._curbk = nm;
			zAu.send(new zk.Event(null, "onBookmarkChange", nm), 50);
			zHistory.onURLChange();
		}
	},
	getBookmark: function () {
		var nm = location.hash;
		var j = nm.indexOf('#');
		return j >= 0 ? decodeURIComponent(nm.substring(j + 1)): '';
	},
	/** bookmark iframe */
	_bkIframe: zk.ie ? function (nm) {
		//Bug 2019171: we have to create iframe frist
		var url = zAu.comURI("/web/js/zk/html/history.html", null, true),
			ifr = zDom.$('zk_histy');
		if (!ifr) ifr = zDom.newFrame('zk_histy', url, "display:none");

		if (nm) url += '?' +encodeURIComponent(nm);
		ifr.src = url;
	}: zk.$void,
	/** called when history.html is loaded*/
	onHistoryLoaded: zk.ie ? function (src) {
		var j = src.indexOf('?');
		var nm = j >= 0 ? src.substring(j + 1): '';
		location.hash = nm ? /*zk.safari ? nm:*/ '#' + nm: '';
		zHistory.checkBookmark();
	}: zk.$void,

	/** check if URL is changed */
	onURLChange: function () {
		try {
			var ifr = window.frameElement;
			if (!parent || parent == window || !ifr) //not iframe
				return;

			var l0 = parent.location, l1 = location,
				url = l0.protocol != l1.protocol || l0.host != l1.host
				|| l0.port != l1.port ? l1.href: l1.pathname,
				j = url.lastIndexOf(';'), k = url.lastIndexOf('?');
			if (j >= 0 && (k < 0 || j < k)) {
				var s = url.substring(0, j);
				url = k < 0 ? s: s + url.substring(k);
			}
			if (l1.hash && "#" != l1.hash) url += l1.hash;

			if (zDom.getAttr(ifr, "z_xsrc") != ifr.src) {//the first zul page being loaded
				var ifrsrc = ifr.src, loc = location.pathname;
				zDom.setAttr(ifr, "z_xsrc", ifrsrc);

			//The first zul page might or might not be ifr.src
			//We have to compare ifr.src with location
			//Gecko/Opera/Safari: ifr.src is a complete URL (including http://)
			//IE: ifr.src has no http://hostname/ (actually, same as server's value)
			//Opera: location.pathname has bookmark and jsessionid
			//Tomcat: /path;jsessionid=xxx#abc?xyz
				ifrsrc = zHistory._simplifyURL(ifrsrc);
				loc = zHistory._simplifyURL(loc);
				if (ifrsrc.endsWith(loc)
				|| loc.endsWith(ifrsrc)) { //the non-zul page is ifr.src
					zDom.setAttr(ifr, "z_xurl", url);
					return; //not notify if changed by server
				}
			}

			if (parent.onIframeURLChange && zDom.getAttr(ifr, "z_xurl") != url) {
				parent.onIframeURLChange(ifr.id, url);
				zDom.setAttr(ifr, "z_xurl", url);
			}
		} catch (e) { //due to JS sandbox, we cannot access if not from same host
			if (zk.debugJS) zk.log("Unable to access parent frame");
		}
	},
	_simplifyURL: function (url) {
		var j = url.lastIndexOf(';');
		if (j >= 0) url = url.substring(0, j);
		j = url.lastIndexOf('#');
		if (j >= 0) url = url.substring(0, j);
		j = url.lastIndexOf('?');
		if (j >= 0) url = url.substring(0, j);
		return url;
	}
};

zHistory._curbk = "";
zk.afterMount(function () { // Bug 1847708
	zHistory.checkBookmark(); // We don't need to wait for the first time.
	setInterval("zHistory.checkBookmark()", 250);
	//Though IE use history.html, timer is still required 
	//because user might specify URL directly
});
