/* bookmark.ts

	Purpose:

	Description:

	History:
		Mon Nov  3 19:34:25     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
*/
let _curbk = '', _initbk = '';

function getBookmark(): string {
	let nm = location.hash,
		j = nm.indexOf('#');
	nm = j >= 0 ? decodeURIComponent(nm.substring(j + 1)) : '';
	return nm || _initbk;
}

/** Checks whether the bookmark is changed. */
function checkBookmark(): void {
	const nm = getBookmark();
	if (nm != _curbk) {
		_curbk = nm;
		zAu.send(new zk.Event(undefined!, 'onBookmarkChange', nm), 1);
		zk.bmk.onURLChange();
	}
}

function _simplifyURL(url: string): string {
	let j = url.lastIndexOf(';');
	if (j >= 0) url = url.substring(0, j);
	j = url.lastIndexOf('#');
	if (j >= 0) url = url.substring(0, j);
	j = url.lastIndexOf('?');
	if (j >= 0) url = url.substring(0, j);
	return url;
}

function _toHash(nm: string, hashRequired?: boolean): string {
	nm = encodeURI(nm); //ZK-4141: Desktop.setBookmark escapes slash symbols wrongly
	return (!hashRequired && zk.webkit) || !nm ? nm : '#' + nm;
}

function _bookmark(nm: string, replace: boolean): void {
	if (_curbk != nm) {
		_curbk = nm; //to avoid loop back the server

		if (replace)
			location.replace(location.href.replace(/#.*/, '') + _toHash(nm, true));
		else
			location.hash = _toHash(nm);
		zk.bmk.onURLChange();
	}
}

let _startCheck: (() => void) | undefined = function () {
	_startCheck = undefined;
	checkBookmark();
	jq(window).on('hashchange', checkBookmark);
	// Kept for a workaround that history.pushState() never causes a hashchange event to be fired
	setInterval(checkBookmark, 250);
	//Though IE use bookmark.html, timer is still required
	//because user might specify URL directly
};
zk._apac(_startCheck); //see mount.js (after page AU cmds)

export let bmk = {
	checkBookmark,
	/** Sets a bookmark that user can use forward and back buttons */
	bookmark(nm: string, replace: boolean): void {
		if (_startCheck)
			_curbk = _initbk = nm;
		else
			(zk.bmk.bookmark = _bookmark)(nm, replace);
	},
	/** called when bookmark.html is loaded*/
	onIframeLoaded(src: string): void {
		// This method shouldn't do anything.
	},

	/** check if URL is changed */
	onURLChange(): void { //called by mount.js
		try {
			const ifr = window.frameElement as HTMLIFrameElement;
			if (!parent || parent == window || !ifr) //not iframe
				return;

			let l0 = parent.location, l1 = location,
				url = l0.protocol != l1.protocol || l0.host != l1.host
				|| l0.port != l1.port ? l1.href : l1.pathname,
				j = url.lastIndexOf(';'), k = url.lastIndexOf('?');
			if (j >= 0 && (k < 0 || j < k)) {
				const s = url.substring(0, j);
				url = k < 0 ? s : s + url.substring(k);
			}
			if (l1.hash && '#' != l1.hash) url += l1.hash;

			const $ifr = jq(ifr);
			if ($ifr.attr('z_xsrc') != ifr.src) {//the first zul page being loaded
				let ifrsrc = ifr.src, loc = location.pathname;
				$ifr.attr('z_xsrc', ifrsrc);

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
					$ifr.attr('z_xurl', url);
					return; //not notify if changed by server
				}
			}

			if (parent.onIframeURLChange && $ifr.attr('z_xurl') != url) {
				parent.onIframeURLChange(ifr.id, url);
				$ifr.attr('z_xurl', url);
			}
		} catch (e) { //due to JS sandbox, we cannot access if not from same host
			zk.debugLog((e as Error).message ?? e);
		}
	}
};
zk.bmk = bmk;