/* bookmark.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov  3 19:34:25     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.bmk = (function () { //used internally
	var _curbk = "", _initbk = "";

	/** bookmark iframe */
	var _bkIframe = zk.ie && !zk.ie8 ? function (nm, oldnm) {
		//Bug 2019171: we have to create iframe frist
		var url = zk.ajaxURI("/web/js/zk/bookmark.html", {au:true,ignoreSession:true}),
			ifr = jq('#zk_histy')[0];
		if (!ifr)
			ifr = jq.newFrame('zk_histy',
				oldnm ? url + '?' +encodeURIComponent(oldnm): url);
				//create iframe first to have a bookmark (so BACK will go to init one)
				//side effect: # is append to the URL even if bookmark is empty
		if (nm) url += '?' +encodeURIComponent(nm);
		ifr.src = url;
	}: zk.$void;

	function getBookmark() {
		var nm = location.hash,
			j = nm.indexOf('#');
		nm = j >= 0 ? decodeURIComponent(nm.substring(j + 1)): '';
		return nm || _initbk;
	}
	/** Checks whether the bookmark is changed. */
	function checkBookmark() {
		var nm = getBookmark();
		if (nm != _curbk) {
			_curbk = nm;
			zAu.send(new zk.Event(null, "onBookmarkChange", nm), 50);
			zk.bmk.onURLChange();
		}
	}
	function _simplifyURL(url) {
		var j = url.lastIndexOf(';');
		if (j >= 0) url = url.substring(0, j);
		j = url.lastIndexOf('#');
		if (j >= 0) url = url.substring(0, j);
		j = url.lastIndexOf('?');
		if (j >= 0) url = url.substring(0, j);
		return url;
	}
	function _toHash(nm, hashRequired) {
		nm = encodeURIComponent(nm);
		return (!hashRequired && zk.safari) || !nm ? nm: '#' + nm;
	}
	function _bookmark(nm, replace) {
		if (_curbk != nm) {
			var oldnm = _curbk;
			_curbk = nm; //to avoid loop back the server

			if (replace)
				location.replace(location.href.replace(/#.*/, "") + _toHash(nm, true));
			else
				location.hash = _toHash(nm);
			_bkIframe(nm, oldnm);
			zk.bmk.onURLChange();
		}
	}

	var _startCheck = function () {
		_startCheck = null;
		checkBookmark();
		setInterval(checkBookmark, 250);
			//Though IE use bookmark.html, timer is still required 
			//because user might specify URL directly
	};
	zk._apac(_startCheck); //see mount.js (after page AU cmds)

  return {
	/** Sets a bookmark that user can use forward and back buttons */
	bookmark: function (nm, replace) {
		if (_startCheck)
			_curbk = _initbk = nm;
		else
			(zk.bmk.bookmark = _bookmark)(nm, replace);
	},
	/** called when bookmark.html is loaded*/
	onIframeLoaded: zk.ie ? function (src) {
		var j = src.indexOf('?'),
			nm = j >= 0 ? src.substring(j + 1): '';
		location.hash = nm ? /*zk.safari ? nm:*/ '#' + nm: '';
		checkBookmark();
	}: zk.$void,

	/** check if URL is changed */
	onURLChange: function () { //called by mount.js
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

			var $ifr = jq(ifr);
			if ($ifr.attr("z_xsrc") != ifr.src) {//the first zul page being loaded
				var ifrsrc = ifr.src, loc = location.pathname;
				$ifr.attr("z_xsrc", ifrsrc);

			//The first zul page might or might not be ifr.src
			//We have to compare ifr.src with location
			//Gecko/Opera/Safari: ifr.src is a complete URL (including http://)
			//IE: ifr.src has no http://hostname/ (actually, same as server's value)
			//Opera: location.pathname has bookmark and jsessionid
			//Tomcat: /path;jsessionid=xxx#abc?xyz
				ifrsrc = _simplifyURL(ifrsrc);
				loc = _simplifyURL(loc);
				if (ifrsrc.endsWith(loc)
				|| loc.endsWith(ifrsrc)) { //the non-zul page is ifr.src
					$ifr.attr("z_xurl", url);
					return; //not notify if changed by server
				}
			}

			if (parent.onIframeURLChange && $ifr.attr("z_xurl") != url) {
				parent.onIframeURLChange(ifr.id, url);
				$ifr.attr("z_xurl", url);
			}
		} catch (e) { //due to JS sandbox, we cannot access if not from same host
//			if (zk.debugJS) zk.log("Unable to access parent frame");
		}
	}
  };
})();
