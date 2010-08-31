/* fixie6.js

	Purpose:
		Fix all IE6 related code
	Description:
		Unlike domie6.js, it is included after all other JavaScript files,
		so it is used to fix any code included later than domie6.js
	History:
		Tue Aug 31 09:19:55 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
(function (window) {
	var _prevzkmx = zkmx;
	zkmx = function () { //Bug 3055849: jq() is required
		var args = arguments;
		jq(function () {
			_prevzkmx.apply(window, args);
		});
	}

})(window);
