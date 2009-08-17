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
(function () {
	var _imgFiltered = [], //filtered img
		_IMGLD = 'DXImageTransform.Microsoft.AlphaImageLoader',
		_FILTER = "progid:" + _IMGLD + "(src='%1',sizingMethod='scale')",
		_inPrint,
		jq$super = {};

	function _onpropchange() {
	 	if (!_inPrint && event.propertyName == "src"
	 	&& this.src.indexOf('spacer.gif') < 0)
	 		_fix(this);
	}
	function _fix(img) {
		var ni = new Image(img.width, img.height);
		ni.onload = function() {
			img.width = ni.width;
			img.height = ni.height;
			ni = null;
		};
		ni.src = img.src; //store the original url (we'll put it back when it's printed)
		img._pngSrc = img.src; //add the AlphaImageLoader thingy
		_addFilter(img);
	}
	function _addFilter(img) {
		var filter = img.filters[_IMGLD];
		if (filter) {
			filter.src = img.src;
			filter.enabled = true;
		} else {
			img.runtimeStyle.filter = zUtl.format(_FILTER, img.src);
			_imgFiltered.push(img);
		}
		img.src = zk.SPACER_URI; //remove the real image
	}
	function _rmFilter(img) {
		img.src = img._pngSrc;
		img.filters[_IMGLD].enabled = false;
	}

zk.copy(zjq, {
	//override//
	_alphafix: function (n) {
		var regex = jq.IE6_ALPHAFIX;
		if (!regex) return;

		if (typeof regex == 'string')
			regex = jq.IE6_ALPHAFIX
				= new RegExp(zUtl.regexEscape(regex) + "$", "i");
		if (!zk.SPACER_URI)
			zk.SPACER_URI = zk.ajaxURI('web/img/spacer.gif', {au:true});
		var imgs = n.getElementsByTagName("img");
		for (var j = imgs.length; j--; ) {
			var img = imgs[j], src = img.src,
				k = src.lastIndexOf(';');
			if (k >= 0) src = src.substring(0, k);
			if (regex.test(img.src)) {
				_fix(img);
				jq(img).bind("propertychange", _onpropchange);
			}
		}
	}
});

zk.override(jq.fn, jq$super, {
	clone: function () {
		var clone = jq$super.clone.apply(this, arguments), n, nc;
		for (var j = 0; j < this.length; ++j) {
			n = this[j];
			if (n.tagName == 'IMG' && n._pngSrc) {
				(nc = clone[j]).src = n._pngSrc;
				setTimeout(function() {_fix(nc);}, 0); //we have to wait
			}
		}
		return clone;
	}
});

jq(window).bind("beforeprint", function() {
		_inPrint = true;
		for (var ns = _imgFiltered, i = 0, len = ns.length; i < len; i++) {
			var n = ns[i];
			try {
				_rmFilter(n);
			} catch (e) {
				ns.splice(i--, 1); //no longer avail
				len--;
			}
		}
	})
	.bind("afterprint", function() {
		for (var ns = _imgFiltered, i = 0, len = ns.length; i < len; i++)
			_addFilter(ns[i]);
		_inPrint = false;
	});

})();