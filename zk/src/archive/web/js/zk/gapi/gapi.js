/* gapi.js

	Purpose:
		Google's AJAX APIs
		
	Description:
		http://code.google.com/apis/ajax/documentation/
		
		Used to access Google's service, e.g. Maps API, Search API, Feed API, etc..
		Since it requires a user to sign up and get a "googleAPIkey" to use it, 
		you have to	get such key on the Google's web site first:
		
		http://code.google.com/apis/maps/signup.html
		
		Then you specify the key in the ZK's page using following mechanism:
		
		<script content="zk.googleAPIkey='your-key-here'/>
		
		And that is all!
		
	History:
		Fri Dec 04 13:24:19     2009, Created by henrichen

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
(function() {
zk.gapi.GOOGLE_API_LOADING_TIMEOUT = 10000; //default to ten seconds
zk.gapi.loadAPIs = function(wgt, callback, msg, timeout) {
	var opts = {};
	opts['condition'] = function() {return window.google && window.google.load};
	opts['callback'] = function() {callback(); delete zk.gapi.LOADING;}
	opts['message'] = msg;
	if (!opts.condition()) {
		zk.gapi.waitUntil(wgt, opts);
		if (!zk.gapi.LOADING) { //avoid double loading Google Ajax APIs
			zk.gapi.LOADING = true;
			if (!opts.condition())
				zk.loadScript('http://www.google.com/jsapi?key='+zk.googleAPIkey);
		}
	} else
		callback();
};
zk.gapi.waitUntil = function(wgt, opts) {
	opts.inittime = opts.inittime || new Date().getTime();
	opts.timeout = opts.timeout || zk.gapi.GOOGLE_API_LOADING_TIMEOUT;
	initMask(wgt, opts);
	waitUntil(wgt, opts);
};
function waitUntil(wgt, opts) {
	if (!opts.condition()) {
		var timestamp0 = new Date().getTime();
		if ((timestamp0 - opts.inittime) < opts.timeout) {
			setTimeout(function() {waitUntil(wgt, opts);}, 100);
			return;
		}
	}
	opts.callback();
	clearMask(wgt, opts);
}
function initMask(wgt, opts) {
	var opt = {};
	opt['anchor'] = wgt;
	if (opts.message) opt['message'] = opts.message;
	opts['_mask'] = new zk.eff.Mask(opt);
}
function clearMask(wgt, opts) {
	if (opts._mask)
		opts._mask.destroy();
}
})();
