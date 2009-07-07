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
				zjq._afFix(img);
				jq(img).bind("propertychange", zjq._onpropchange);
			}
		}
	},
	_onpropchange: function () {
	 	if (!zjq._afPrint && event.propertyName == "src"
	 	&& this.src.indexOf('spacer.gif') < 0)
	 		zjq._afFix(this);
	},
	_afFix: function (img) {
		var ni = new Image(img.width, img.height);
		ni.onload = function() {
			img.width = ni.width;
			img.height = ni.height;
			ni = null;
		};
		ni.src = img.src; //store the original url (we'll put it back when it's printed)
		img._pngSrc = img.src; //add the AlphaImageLoader thingy
		zjq._addFilter(img);
	},
	_addFilter: function (img) {
		var filter = img.filters[zjq._AF_IMGLD];
		if (filter) {
			filter.src = img.src;
			filter.enabled = true;
		} else {
			img.runtimeStyle.filter = zUtl.format(zjq._AF_FILTER, img.src);
			zjq._afed.push(img);
		}
		img.src = jq.SPACER_GIF; //remove the real image
	},
	_rmFilter: function (img) {
		img.src = img._pngSrc;
		img.filters[zjq._AF_IMGLD].enabled = false;
	}
});

zk.override(jq.fn, zjq._fn, {
	clone: function () {
		var clone = zjq._fn.clone.apply(this, arguments), n, nc;
		for (var j = 0; j < this.length; ++j) {
			n = this[j];
			if (n.tagName == 'IMG' && n._pngSrc) {
				(nc = clone[j]).src = n._pngSrc;
				setTimeout(function() {zjq._afFix(nc);}, 0); //we have to wait
			}
		}
		return clone;
	}
});

jq(window).bind("beforeprint", function() {
		zjq._afPrint = true;
		for (var ns = zjq._afed, i = 0, len = ns.length; i < len; i++) {
			var n = ns[i];
			try {
				zjq._rmFilter(n);
			} catch (e) {
				ns.splice(i--, 1); //no longer avail
				len--;
			}
		}
	})
	.bind("afterprint", function() {
		for (var ns = zjq._afed, i = 0, len = ns.length; i < len; i++)
			zjq._addFilter(ns[i]);
		zjq._afPrint = false;
	});
