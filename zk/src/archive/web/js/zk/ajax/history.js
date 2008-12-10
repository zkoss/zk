/* history.js

	Purpose:
		
	Description:
		
	History:
		Mon Nov  3 19:34:25     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under GPL Version 2.0 in the hope that
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
		var url = zk.getUpdateURI("/web/js/zk/html/history.html", true),
			ifr = $e('zk_histy');
		if (!ifr) ifr = zk.newFrame('zk_histy', url, "display:none");

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

	/** */
	onURLChange: function () {
		//TODO
	}
};

zHistory._curbk = "";
zkAfterBs(function () { // Bug 1847708
	zHistory.checkBookmark(); // We don't need to wait for the first time.
	setInterval("zHistory.checkBookmark()", 250);
	//Though IE use history.html, timer is still required 
	//because user might specify URL directly
});
