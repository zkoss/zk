/* ServletRequestContext.java

	Purpose:
		
	Description:
		
	History:
		Fri Apr 16 16:56:46 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.au.http;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletRequest;

import org.apache.commons.fileupload.RequestContext;

/**
 * An implementation of RequestContext, for commons-fileupload.
 * A concrete servlet request object will be wrapped.
 *
 * @author rudyhuang
 * @since 9.6.0
 */
class ServletRequestContext implements RequestContext {
	private final ServletRequest _request;

	public ServletRequestContext(ServletRequest request) {
		this._request = request;
	}

	@Override
	public String getCharacterEncoding() {
		return _request.getCharacterEncoding();
	}

	@Override
	public String getContentType() {
		return _request.getContentType();
	}

	@Override
	public int getContentLength() {
		return _request.getContentLength();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return _request.getInputStream();
	}
}
