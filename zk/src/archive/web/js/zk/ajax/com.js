/* com.js

{{IS_NOTE
	Purpose:
		The low-level communication utilities
	Description:
		
	History:
		Mon Oct  6 17:12:03     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
zkCom = {
	/** Returns the update URI.
	 * @param uri the URI. If null, the base URI is returned.
	 * @param dtid the desktop ID. If null, the first desktop is used.
	 * @param ignoreSessId whether to handle the session ID in the base URI
	 */
	getUpdateURI: function (uri, dtid, ignoreSessId) {
		var au = zk.Desktop.of(dtid).updateURI;
		if (!uri) return au;

		if (uri.charAt(0) != '/') uri = '/' + uri;

		var j = au.lastIndexOf(';'), k = au.lastIndexOf('?');
		if (j < 0 && k < 0) return au + uri;

		if (k >= 0 && (j < 0 || k < j)) j = k;
		var prefix = au.substring(0, j);

		if (ignoreSessId)
			return prefix + uri;

		var suffix = au.substring(j);
		var l = uri.indexOf('?');
		return l >= 0 ?
			k >= 0 ?
			  prefix + uri.substring(0, l) + suffix + '&' + uri.substring(l+1):
			  prefix + uri.substring(0, l) + suffix + uri.substring(l):
			prefix + uri + suffix;
	}
};
