/* RenderHttpServletResponse.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Jan 17 00:59:09     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.web.portlet;

import javax.servlet.http.HttpServletResponse;
import javax.portlet.RenderResponse;

import com.potix.web.servlet.ServletOutputStreamWrapper;

/**
 * A facade of RenderResponse that implements HttpServletRespose.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.4 $ $Date: 2006/05/29 04:27:37 $
 */
public class RenderHttpServletResponse implements HttpServletResponse {
	private final RenderResponse _res;
	public static HttpServletResponse getInstance(RenderResponse res) {
		if (res instanceof HttpServletResponse)
			return (HttpServletResponse)res;
		return new RenderHttpServletResponse(res);
	}
	private RenderHttpServletResponse(RenderResponse res) {
		if (res == null)
			throw new IllegalArgumentException("null");
		_res = res;
	}

	//-- ServletResponse --//
	public void flushBuffer() throws java.io.IOException {
		_res.flushBuffer();
	}
	public int getBufferSize() {
		return _res.getBufferSize();
	}
	public String getCharacterEncoding() {
		return _res.getCharacterEncoding();
	}
	public String getContentType() {
		return _res.getContentType();
	}
	public java.util.Locale getLocale() {
		return _res.getLocale();
	}
	public javax.servlet.ServletOutputStream getOutputStream()
	throws java.io.IOException {
		return ServletOutputStreamWrapper.getInstance(_res.getPortletOutputStream());
	}
	public java.io.PrintWriter getWriter() throws java.io.IOException {
		return _res.getWriter();
	}
	public boolean isCommitted() {
		return _res.isCommitted();
	}
	public void reset() {
		_res.reset();
	}
	public void resetBuffer() {
		_res.resetBuffer();
	}
	public void setBufferSize(int size) {
		_res.setBufferSize(size);
	}
	public void setCharacterEncoding(String charset) {
	}
	public void setContentLength(int len) {
	}
	public void setContentType(String type) {
		_res.setContentType(type);
	}
	public void setLocale(java.util.Locale loc)  {
	}

	//-- HttpServletResponse --//
	public void addCookie(javax.servlet.http.Cookie cookie) {
		throw new UnsupportedOperationException();
	}
	public void addDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}
	public void addIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}
	public boolean containsHeader(String name) {
		return false;
	}
	/**
	 * @deprecated
	 */
	public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}
	public String encodeRedirectURL(String url) {
		throw new UnsupportedOperationException();
	}
	/**
	 * @deprecated
	 */
	public String encodeUrl(String url) {
		return encodeURL(url);
	}
	public String encodeURL(String url) {
		return _res.encodeURL(url);
	}
	public void sendError(int sc) {
		throw new UnsupportedOperationException();
	}
	public void sendError(int sc, String msg) {
		throw new UnsupportedOperationException();
	}
	public void sendRedirect(String location) {
		throw new UnsupportedOperationException(); //use ActionResponse instead
	}
	public void setDateHeader(String name, long date) {
		throw new UnsupportedOperationException();
	}
	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}
	public void setIntHeader(String name, int value) {
		throw new UnsupportedOperationException();
	}
	public void setStatus(int sc) {
		throw new UnsupportedOperationException();
	}
	/**
	 * @deprecated
	 */
	public void setStatus(int sc, String sm)  {
		throw new UnsupportedOperationException("deprecated");
	}
}
