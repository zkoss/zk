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
	/** Returns the URI to communicate with the server.
	 * @param uri the URI. If null, the base URI is returned.
	 * @param dtid the desktop ID. If null, the first desktop is used.
	 * @param ignoreSessId whether to handle the session ID in the base URI
	 */
	comURI: function (uri, dtid, ignoreSessId) {
		var au = zk.Desktop.$(dtid).updateURI;
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
	},

	//Error Handling//
	/** Confirms the user how to handle an error.
	 * Default: it shows up a message asking the user whether to retry.
	 */
	confirmRetry: function (msgCode, msg2) {
		var msg = mesg[msgCode];
		return zk.confirm((msg?msg:msgCode)+'\n'+mesg.TRY_AGAIN+(msg2?"\n\n("+msg2+")":""));
	},
	/** Handles the error caused by processing the response.
	 * @param msgCode the error message code.
	 * It is either an index of mesg (e.g., "FAILED_TO_PROCESS"),
	 * or an error message
	 * @param msg2 the additional message (optional)
	 * @param cmd the command (optional)
	 * @param ex the exception (optional)
	 */
	onError: function (msgCode, msg2, cmd, ex) {
		var msg = mesg[msgCode];
		zk.error((msg?msg:msgCode)+'\n'+(msg2?msg2:"")+(cmd?cmd:"")+(ex?"\n"+ex.message:""));
	},
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
	_eru: {},

	//Internal Variables//
	_cmdsQue: [], //response commands in XML
	_evts: {}, //(dtid, Array()): events that are not sent yet
	_onsends: [], //JS called before 	_sendNow
	_seqId: 1 //1-999
};
