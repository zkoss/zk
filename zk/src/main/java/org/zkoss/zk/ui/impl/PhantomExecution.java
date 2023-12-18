/* PhantomExecution.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 16 13:23:51     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import org.zkoss.idom.Document;
import org.zkoss.util.CollectionsX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.metainfo.PageDefinition;

/**
 * A 'phantom' execution that is used when no request/response available.
 * For example, it is used when a session is invalidated, or activated.
 *
 * @author tomyeh
 */
/*package*/ class PhantomExecution extends AbstractExecution {
	private final SimpleScope _scope = new SimpleScope(this);
	private boolean _voided;

	public PhantomExecution(Desktop desktop) {
		super(desktop, null);
	}

	public void include(Writer out, String page, Map<String, ?> params, int mode) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void include(String page) throws IOException {
		throw new UnsupportedOperationException();
	}

	public void forward(Writer out, String page, Map<String, ?> params, int mode) throws IOException {
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

	public String locate(String path) {
		throw new UnsupportedOperationException();
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

	public PageDefinition getPageDefinitionDirectly(Reader reader, String ext) throws IOException {
		throw new UnsupportedOperationException();
	}

	public Double getBrowser(String name) {
		return null;
	}

	public String getBrowser() {
		return null;
	}

	public String getUserAgent() {
		return "mock";
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
		return _scope.getAttribute(name);
	}

	public boolean hasAttribute(String name) {
		return _scope.hasAttribute(name);
	}

	public Object setAttribute(String name, Object value) {
		return _scope.setAttribute(name, value);
	}

	public Object removeAttribute(String name) {
		return _scope.removeAttribute(name);
	}

	public Map<String, Object> getAttributes() {
		return _scope.getAttributes();
	}

	public boolean addScopeListener(ScopeListener listener) {
		return _scope.addScopeListener(listener);
	}

	public boolean removeScopeListener(ScopeListener listener) {
		return _scope.removeScopeListener(listener);
	}

	public String getHeader(String name) {
		return null;
	}

	public Iterable<String> getHeaders(String name) {
		return CollectionsX.emptyIterable();
	}

	public Iterable<String> getHeaderNames() {
		return CollectionsX.emptyIterable();
	}

	public void setResponseHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	public void setResponseHeader(String name, Date value) {
		throw new UnsupportedOperationException();
	}

	public void addResponseHeader(String name, String value) {
		throw new UnsupportedOperationException();
	}

	public void addResponseHeader(String name, Date value) {
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

	public Map<String, String[]> getParameterMap() {
		return Collections.emptyMap();
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
