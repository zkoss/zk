/* domie6.js

	Purpose:
		Fix the alpha transparency issue found in IE6
	Description:
 		IE 5.5/6 (not 7) has a bug that failed to render PNG with
		alpha transparency. 
		See
		http://code.google.com/p/ie7-js/
	History:
		Thu May 14 18:05:58 2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
zk.copy(zjq, {
	_afed: [], //filtered img
	_AF_IMGLD: _zkf = 'DXImageTransform.Microsoft.AlphaImageLoader',
	_AF_FILTER: "progid:" + _zkf + "(src='%1',sizingMethod='scale')",

	//override//
	_alphafix: function (n) {
		var regex = jq.IE6_ALPHAFIX;
		if (!regex) return;

		if (typeof regex == 'string')
			regex = jq.IE6_ALPHAFIX
				= new RegExp(zUtl.regexEscape(regex) + "$", "i");
		if (!jq.SPACER_GIF)
			jq.SPACER_GIF = zAu.comURI('web/img/spacer.gif');

		var imgs = n.getElementsByTagName("img");
		for (var j = imgs.length; --j >= 0; ) {
			var img = imgs[j], src = img.src,
				k = src.lastIndexOf(';');
			if (k >= 0) src = src.substring(0, k);
			if (regex.test(img.src)) {
				jq._afFix(img);
				jq(img).bind("propertychange", jq._onpropchange);
			}
		}
	},
	_onpropchange: function () {
	 	if (!jq._afPrint && event.propertyName == "src"
	 	&& this.src.indexOf('spacer.gif') < 0)
	 		jq._afFix(this);
	},
	_afFix: function (img) {
		var ni = new Image(img.width, img.height);
		ni.onload = function() {
			img.width = ni.width;
			img.height = ni.height;
			ni = null;
		};
		ni.src = img.src; //store the original url (we'll put it back when it's printed)
		img.pngSrc = img.src; //add the AlphaImageLoader thingy
		jq._addFilter(img);
	},
	_addFilter: function (img) {
		var filter = img.filters[jq._AF_IMGLD];
		if (filter) {
			filter.src = img.src;
			filter.enabled = true;
		} else {
			img.runtimeStyle.filter =
				zUtl.format(jq._AF_FILTER, img.src);
			jq._afed.push(img);
		}
		img.src = jq.SPACER_GIF; //remove the real image
	},
	_rmFilter: function (img) {
		img.src = img.pngSrc;
		img.filters[jq._AF_IMGLD].enabled = false;
	}
});

jq(window).bind("beforeprint", function() {
		jq._afPrint = true;
		for (var ns = jq._afed, i = 0, len = ns.length; i < len; i++)
			jq._rmFilter(ns[i]);
	})
	.bind("afterprint", function() {
		for (var ns = jq._afed, i = 0, len = ns.length; i < len; i++)
			jq._addFilter(ns[i]);
		jq._afPrint = false;
	});
