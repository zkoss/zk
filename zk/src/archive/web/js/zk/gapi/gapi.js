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
var timestamp;
if (zk.googleAPIsLoadingTimeout == null)
	zk.googleAPIsLoadingTimeout = 10000; //default to ten seconds

zk.gapi.loadAPIs = function(wgt, callback, msg) {
	if (!window.google || !window.google.load) {
		initMask(wgt, msg);
		wgt._gapi_callback = callback;
		loaded(wgt);
		zk.loadScript('http://www.google.com/jsapi?key='+zk.googleAPIkey);
		return;
	}
	wgt._gapi_callback = callback;
	loaded(wgt);
};
function initMask(wgt, msg) {
	var opt = {};
	opt['anchor'] = wgt;
	if (msg) opt['message'] = msg;
	wgt._mask = new zk.eff.Mask(opt);
	timestamp = new Date().getTime();
}
function clearMask(wgt) {
	if (wgt._mask) {
		wgt._mask.destroy();
		delete wgt._mask;
		timestamp = null; 
	}
}
function loaded(wgt) {
	if (!window.google || !window.google.load) {
		var timestamp0 = new Date().getTime();
		if ((timestamp0 - timestamp) < zk.googleAPIsLoadingTimeout) {
			var wgt0 = wgt;
			setTimeout(function() {loaded(wgt0)}, 100);
			return;
		}
	}
	wgt._gapi_callback();
	clearMask(wgt);
	if (wgt._gapi_callback)
		delete wgt._gapi_callback;
	if (zk.gapi.LOADING)
		delete zk.gapi.LOADING;
}
})();
