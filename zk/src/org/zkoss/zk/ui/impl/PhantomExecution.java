/* PhantomExecution.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Jul 16 13:23:51     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.io.Writer;
import java.io.Reader;
import java.io.IOException;

import org.zkoss.util.CollectionsX;
import org.zkoss.idom.Document;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.ext.ScopeListener;

/**
 * A 'phantom' execution that is used when no request/response available.
 * For example, it is used when a session is invalidated, or activated.
 *
 * @author tomyeh
 */
/*package*/ class PhantomExecution extends AbstractExecution {
	private boolean _voided;

	public PhantomExecution(Desktop desktop) {
		super(desktop, null);
	}

	public void include(Writer out, String page, Map params, int mode)
	throws IOException {
		throw new UnsupportedOperationException();
	}
	public void include(String page) throws IOException {
		throw new UnsupportedOperationException();
	}
	public void forward(Writer out, String page, Map params, int mode)
	throws IOException {
		throw new UnsupportedOperationException();
	}
	public void forward(String page) throws IOException {
		throw new UnsupportedOperationException();
	}
	public boolean isIncluded() {
		return false;
	}
	public boolean isForwarded() {
		return false;
	}

	public boolean isVoided() {
		return _voided;
	}
	public void setVoided(boolean voided) {
		_voided = voided;
	}

	public String encodeURL(String uri) {
		return uri;
	}
	public java.security.Principal getUserPrincipal() {
		return null;
	}
	public boolean isUserInRole(String role) {
		return false;
	}
	public String getRemoteUser() {
		return "n/a";
	}
	public String getRemoteHost() {
		return "n/a";
	}
	public String getRemoteAddr() {
		return "n/a";
	}
	public String getServerName() {
		return "n/a";
	}
	public int getServerPort() {
		return 0;
	}
	public String getLocalName() {
		return "n/a";
	}
	public String getLocalAddr() {
		return "n/a";
	}
	public int getLocalPort() {
		return 0;
	}
	public String getContextPath() {
		return "/";
	}
	public String getScheme() {
		return "n/a";
	}

	public PageDefinition getPageDefinition(String uri) {
		throw new UnsupportedOperationException();
	}
	public PageDefinition getPageDefinitionDirectly(String content, String ext) {
		throw new UnsupportedOperationException();
	}
	public PageDefinition getPageDefinitionDirectly(Document content, String ext) {
		throw new UnsupportedOperationException();
	}
	public PageDefinition getPageDefinitionDirectly(Reader reader, String ext)
	throws IOException {
		throw new UnsupportedOperationException();
	}

	public boolean isBrowser() {
		return false;
	}
	public boolean isBrowser(String type) {
		return false;
	}
	public boolean isRobot() {
		return false;
	}
	public boolean isExplorer() {
		return false;
	}
	public boolean isExplorer7() {
		return false;
	}
	public boolean isOpera() {
		return false;
	}
	public boolean isGecko() {
		return false;
	}
	public boolean isGecko3() {
		return false;
	}
	public boolean isSafari() {
		return false;
	}
	/** @deprecated As of release 5.0.0, MIL is no longer supported.
	 */
	public boolean isMilDevice() {
		return false;
	}
	public boolean isHilDevice() {
		return false;
	}
	public String getUserAgent() {
		return "mock";
	}

	public void setHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}
	public void setDateHeader(String name, long value) {
		throw new UnsupportedOperationException();
	}
	public void addHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}
	public void addDateHeader(String name, long value) {
		throw new UnsupportedOperationException();
	}
	public void setContentType(String contentType) {
		throw new UnsupportedOperationException();
	}

	public Object getNativeRequest() {
		return null;
	}
	public Object getNativeResponse() {
		return null;
	}
	public Object getAttribute(String name) {
		return null;
	}
	public boolean hasAttribute(String name) {
		return false;
	}
	public Object getAttribute(String name, boolean recurse) {
		return null;
	}
	public boolean hasAttribute(String name, boolean recurse) {
		return false;
	}
	public Object setAttribute(String name, Object value) {
		throw new UnsupportedOperationException();
	}
	public Object removeAttribute(String name) {
		throw new UnsupportedOperationException();
	}
	public Map getAttributes() {
		return Collections.EMPTY_MAP;
	}

	public boolean addScopeListener(ScopeListener listener) {
		throw new UnsupportedOperationException();
	}
	public boolean removeScopeListener(ScopeListener listener) {
		throw new UnsupportedOperationException();
	}

	public String getHeader(String name) {
		return null;
	}
	public Iterator getHeaders(String name) {
		return CollectionsX.EMPTY_ITERATOR;
	}
	public Iterator getHeaderNames() {
		return CollectionsX.EMPTY_ITERATOR;
	}
	public void setResponseHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}
	public void addResponseHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}
	public boolean containsResponseHeader(String name) {
		return false;
	}

	public String[] getParameterValues(String name) {
		return null;
	}
	public String getParameter(String name) {
		return null;
	}
	public Map getParameterMap() {
		return Collections.EMPTY_MAP;
	}
	public org.zkoss.xel.VariableResolver getVariableResolver() {
		return null;
	}
	public org.zkoss.zk.xel.Evaluator getEvaluator(Component page, Class expfcls) {
		return null;
	}
	public org.zkoss.zk.xel.Evaluator getEvaluator(Page page, Class expfcls) {
		return null;
	}
	public Object evaluate(Component comp, String expr, Class expectedType) {
		throw new UnsupportedOperationException();
	}
	public Object evaluate(Page page, String expr, Class expectedType) {
		throw new UnsupportedOperationException();
	}
}
