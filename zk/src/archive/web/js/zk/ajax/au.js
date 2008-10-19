/* au.js

{{IS_NOTE
	Purpose:
		ZK Client Engine
	Description:
		
	History:
		Mon Sep 29 17:17:37     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zkau = { //static methods
	/** Sets the URI for an error code.
	 * If the length of  arguments is 1, then the argument must be
	 * the error code, and this method returns its URI (or null
	 * if not available).
	 * <p>If the length is larger than 1, they must be paired and
	 * the first element of the pair must be the package name,
	 * and the second is the version.
	 */
	errorURI: function (code, uri) {
		var args = arguments, len = args.length;
		if (len == 1)
			return zkau._eru['e' + code];

		if (len > 2) {
			for (var j = 0; j < len; j += 2)
				zkau.errorURI(args[j], args[j + 1]);
			return;
		}

		zkau._eru['e' + code] = uri;
	},
	_eru: {}
};
