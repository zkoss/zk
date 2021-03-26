/* RenderHttpServletResponse.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 17 00:59:09     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.portlet;

import java.util.Collection;

import javax.portlet.RenderResponse;

import org.zkoss.web.servlet.ServletOutputStreamWrapper;

import jakarta.servlet.http.HttpServletResponse;

/**
 * A facade of RenderResponse that implements HttpServletRespose.
 *
 * @author tomyeh
 */
public class RenderHttpServletResponse implements HttpServletResponse {
	private final RenderResponse _res;

	public static HttpServletResponse getInstance(RenderResponse res) {
		if (res instanceof HttpServletResponse)
			return (HttpServletResponse) res;
		return new RenderHttpServletResponse(res);
	}

	private RenderHttpServletResponse(RenderResponse res) {
		if (res == null)
			throw new IllegalArgumentException("null");
		_res = res;
	}

	//-- ServletResponse --//
	@Override
    public void flushBuffer() throws java.io.IOException {
		_res.flushBuffer();
	}

	@Override
    public int getBufferSize() {
		return _res.getBufferSize();
	}

	@Override
    public String getCharacterEncoding() {
		return _res.getCharacterEncoding();
	}

	@Override
    public String getContentType() {
		return _res.getContentType();
	}

	@Override
    public java.util.Locale getLocale() {
		return _res.getLocale();
	}

	@Override
    public jakarta.servlet.ServletOutputStream getOutputStream() throws java.io.IOException {
		return ServletOutputStreamWrapper.getInstance(_res.getPortletOutputStream());
	}

	@Override
    public java.io.PrintWriter getWriter() throws java.io.IOException {
		//Bug 1548478: content-type is required for some implementation (JBoss Portal)
		if (_res.getContentType() == null)
			_res.setContentType("text/html;charset=UTF-8");
		return _res.getWriter();
	}

	@Override
    public boolean isCommitted() {
		return _res.isCommitted();
	}

	@Override
    public void reset() {
		_res.reset();
	}

	@Override
    public void resetBuffer() {
		_res.resetBuffer();
	}

	@Override
    public void setBufferSize(int size) {
		_res.setBufferSize(size);
	}

	@Override
    public void setCharacterEncoding(String charset) {
	}

	@Override
    public void setContentLength(int len) {
	}

	@Override
    public void setContentType(String type) {
		_res.setContentType(type);
	}

	@Override
    public void setLocale(java.util.Locale loc) {
	}

	//-- HttpServletResponse --//
	@Override
    public void addCookie(jakarta.servlet.http.Cookie cookie) {
	}

	@Override
    public void addDateHeader(String name, long date) {
	}

	@Override
    public void addHeader(String name, String value) {
	}

	@Override
    public void addIntHeader(String name, int value) {
	}

	@Override
    public boolean containsHeader(String name) {
		return false;
	}

	/**
	 * @deprecated
	 */
	@Deprecated
    @Override
    public String encodeRedirectUrl(String url) {
		return encodeRedirectURL(url);
	}

	@Override
    public String encodeRedirectURL(String url) {
		return encodeURL(url); //try our best
	}

	/**
	 * @deprecated
	 */
	@Deprecated
    @Override
    public String encodeUrl(String url) {
		return encodeURL(url);
	}

	@Override
    public String encodeURL(String url) {
		return _res.encodeURL(url);
	}

	@Override
    public void sendError(int sc) {
	}

	@Override
    public void sendError(int sc, String msg) {
	}

	@Override
    public void sendRedirect(String location) {
	}

	@Override
    public void setDateHeader(String name, long date) {
	}

	@Override
    public void setHeader(String name, String value) {
	}

	@Override
    public void setIntHeader(String name, int value) {
	}

	@Override
    public void setStatus(int sc) {
	}

	/**
	 * @deprecated
	 */
	@Deprecated
    @Override
    public void setStatus(int sc, String sm) {
	}

	//Object//
	@Override
    public int hashCode() {
		return _res.hashCode();
	}

	@Override
    public boolean equals(Object o) {
		if (this == o)
			return true;
		RenderResponse val = o instanceof RenderResponse ? (RenderResponse) o
				: o instanceof RenderHttpServletResponse ? ((RenderHttpServletResponse) o)._res : null;
		return val != null && val.equals(_res);
	}

    @Override
    public void setContentLengthLong(long len) {

    }

    @Override
    public int getStatus() {
        return -1;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }
}
