/* Provider.java

	Purpose:
		
	Description:
		
	History:
		Fri Dec  2 12:08:40 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.http;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A resource provider.
 * @author tomyeh
 */
/*package*/ class Provider { //don't use private since WpdContent needs it
	private AbstractExtendlet _extlet;
	/*package*/ final HttpServletRequest request;
	/*package*/ final HttpServletResponse response;

	/*package*/ Provider(AbstractExtendlet extlet, HttpServletRequest request, HttpServletResponse response) {
		_extlet = extlet;
		this.request = request;
		this.response = response;
	}
	InputStream getResourceAsStream(String path, boolean locate)
	throws IOException, ServletException {
		return _extlet.getResourceAsStream(this.request, path, locate);
	}
	URL getResource(String path) throws IOException {
		return _extlet.getResource(path);
	}
}
