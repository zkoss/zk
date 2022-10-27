/* DummyExec.java

	Purpose:

	Description:

	History:
		10:13 AM 2021/10/7, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.mock;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.security.Principal;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.zkoss.idom.Document;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelContext;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.ScopeListener;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.sys.ExecutionCtrl;
import org.zkoss.zk.ui.sys.ExecutionInfo;
import org.zkoss.zk.ui.sys.Visualizer;
import org.zkoss.zk.ui.util.Callback;
import org.zkoss.zk.xel.Evaluator;

/**
 * Mock {@link Execution} implementation
 * @author jumperchen
 */
public class DummyExec implements Execution, ExecutionCtrl {

	private Desktop _desktop;
	private Page _page;
	public DummyExec() {
		_desktop = new DummyDesktop(new DummyWebApp());
	}

	public Desktop getDesktop() {
		return _desktop;
	}

	public Session getSession() {
		return null;
	}

	public boolean isAsyncUpdate(Page page) {
		return true; // always true.
	}

	public String[] getParameterValues(String s) {
		return new String[0];
	}

	public String getParameter(String s) {
		return null;
	}

	public Map<String, String[]> getParameterMap() {
		return null;
	}

	public Evaluator getEvaluator(Page page,
			Class<? extends ExpressionFactory> aClass) {
		return null;
	}

	public Evaluator getEvaluator(Component component,
			Class<? extends ExpressionFactory> aClass) {
		return null;
	}

	public Object evaluate(Component component, String s, Class<?> aClass) {
		return null;
	}

	public Object evaluate(Page page, String s, Class<?> aClass) {
		return null;
	}

	public VariableResolver getVariableResolver() {
		return null;
	}

	public boolean addVariableResolver(VariableResolver variableResolver) {
		return false;
	}

	public boolean removeVariableResolver(VariableResolver variableResolver) {
		return false;
	}

	public boolean hasVariableResolver(VariableResolver variableResolver) {
		return false;
	}

	public boolean hasVariableResolver(
			Class<? extends VariableResolver> aClass) {
		return false;
	}

	public void postEvent(Event event) {

	}

	public void postEvent(int i, Event event) {

	}

	public void postEvent(int i, Component component, Event event) {

	}

	public void include(Writer writer, String s, Map<String, ?> map, int i)
			throws IOException {

	}

	public void include(String s) throws IOException {

	}

	public void forward(Writer writer, String s, Map<String, ?> map, int i)
			throws IOException {

	}

	public void forward(String s) throws IOException {

	}

	public String locate(String s) {
		return null;
	}

	public boolean isVoided() {
		return false;
	}

	public void setVoided(boolean b) {

	}

	public boolean isIncluded() {
		return false;
	}

	public boolean isForwarded() {
		return false;
	}

	public String toAbsoluteURI(String s, boolean b) {
		return null;
	}

	public String encodeURL(String s) {
		// do not thing just return for testing
		return s;
	}

	public Principal getUserPrincipal() {
		return null;
	}

	public boolean isUserInRole(String s) {
		return false;
	}

	public String getRemoteUser() {
		return null;
	}

	public String getRemoteHost() {
		return null;
	}

	public String getRemoteAddr() {
		return null;
	}

	public String getServerName() {
		return null;
	}

	public int getServerPort() {
		return 0;
	}

	public String getLocalName() {
		return null;
	}

	public String getLocalAddr() {
		return null;
	}

	public int getLocalPort() {
		return 0;
	}

	public String getScheme() {
		return null;
	}

	public String getContextPath() {
		return null;
	}

	public PageDefinition getPageDefinition(String s) {
		return null;
	}

	public PageDefinition getPageDefinitionDirectly(String s, String s1) {
		return null;
	}

	public PageDefinition getPageDefinitionDirectly(Document document,
			String s) {
		return null;
	}

	public PageDefinition getPageDefinitionDirectly(Reader reader, String s)
			throws IOException {
		return null;
	}

	public Component createComponents(PageDefinition pageDefinition,
			Component component, Map<?, ?> map) {
		return null;
	}

	public Component createComponents(String s, Component component,
			Map<?, ?> map) {
		return null;
	}

	public Component createComponents(PageDefinition pageDefinition,
			Component component, Component component1,
			VariableResolver variableResolver) {
		return null;
	}

	public Component createComponents(String s, Component component,
			Component component1, VariableResolver variableResolver) {
		return null;
	}

	public Component[] createComponents(String s, Component component,
			Component component1, VariableResolver variableResolver,
			Map<?, ?> map) {
		return new Component[0];
	}

	public Component[] createComponents(PageDefinition pageDefinition,
			Map<?, ?> map) {
		return new Component[0];
	}

	public Component[] createComponents(String s, Map<?, ?> map) {
		return new Component[0];
	}

	public Component[] createComponents(String s, Page page,
			VariableResolver variableResolver, Map<?, ?> map) {
		return new Component[0];
	}

	public Component createComponentsDirectly(String s, String s1,
			Component component, Map<?, ?> map) {
		return null;
	}

	public Component createComponentsDirectly(Document document, String s,
			Component component, Map<?, ?> map) {
		return null;
	}

	public Component createComponentsDirectly(Reader reader, String s,
			Component component, Map<?, ?> map) throws IOException {
		return null;
	}

	public Component createComponentsDirectly(String s, String s1,
			Component component, Component component1,
			VariableResolver variableResolver) {
		return null;
	}

	public Component createComponentsDirectly(Document document, String s,
			Component component, Component component1,
			VariableResolver variableResolver) {
		return null;
	}

	public Component createComponentsDirectly(Reader reader, String s,
			Component component, Component component1,
			VariableResolver variableResolver) throws IOException {
		return null;
	}

	public Component[] createComponentsDirectly(String s, String s1,
			Map<?, ?> map) {
		return new Component[0];
	}

	public Component[] createComponentsDirectly(Document document, String s,
			Map<?, ?> map) {
		return new Component[0];
	}

	public Component[] createComponentsDirectly(Reader reader, String s,
			Map<?, ?> map) throws IOException {
		return new Component[0];
	}

	public void sendRedirect(String s) {

	}

	public void sendRedirect(String s, String s1) {

	}

	public void sendRedirect(String s, boolean b) {

	}

	public Map<?, ?> getArg() {
		return null;
	}

	public void pushArg(Map<?, ?> map) {

	}

	public void popArg() {

	}

	public void addAuResponse(AuResponse auResponse) {

	}

	public void addAuResponse(String s, AuResponse auResponse) {

	}

	public Double getBrowser(String s) {
		return null;
	}

	public String getBrowser() {
		return null;
	}

	public boolean isBrowser() {
		return false;
	}

	public boolean isBrowser(String s) {
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

	public boolean isGecko() {
		return false;
	}

	public boolean isGecko3() {
		return false;
	}

	public boolean isSafari() {
		return false;
	}

	public boolean isOpera() {
		return false;
	}

	public boolean isHilDevice() {
		return false;
	}

	public String getUserAgent() {
		return null;
	}

	public Object getNativeRequest() {
		return null;
	}

	public Object getNativeResponse() {
		return null;
	}

	public Object getAttribute(String s) {
		return null;
	}

	public Object getAttribute(String s, boolean b) {
		return null;
	}

	public boolean hasAttribute(String s) {
		return false;
	}

	public boolean hasAttribute(String s, boolean b) {
		return false;
	}

	public Object setAttribute(String s, Object o) {
		return null;
	}

	public Object setAttribute(String s, Object o, boolean b) {
		return null;
	}

	public Object removeAttribute(String s) {
		return null;
	}

	public Object removeAttribute(String s, boolean b) {
		return null;
	}

	public boolean addScopeListener(ScopeListener scopeListener) {
		return false;
	}

	public boolean removeScopeListener(ScopeListener scopeListener) {
		return false;
	}

	public Map<String, Object> getAttributes() {
		return null;
	}

	public String getHeader(String s) {
		return null;
	}

	public Iterable<String> getHeaders(String s) {
		return null;
	}

	public Iterable<String> getHeaderNames() {
		return null;
	}

	public void setResponseHeader(String s, String s1) {

	}

	public void setResponseHeader(String s, Date date) {

	}

	public void addResponseHeader(String s, String s1) {

	}

	public void addResponseHeader(String s, Date date) {

	}

	public boolean containsResponseHeader(String s) {
		return false;
	}

	public void log(String s) {

	}

	public void log(String s, Throwable throwable) {

	}

	public Page getCurrentPage() {
		return _page;
	}

	public void setCurrentPage(Page page) {
		_page = page;
	}

	public PageDefinition getCurrentPageDefinition() {
		return null;
	}

	public void setCurrentPageDefinition(PageDefinition pageDefinition) {

	}

	public Event getNextEvent() {
		return null;
	}

	public boolean isActivated() {
		return false;
	}

	public void onActivate() {

	}

	public void onDeactivate() {

	}

	public void onBeforeDeactivate() {

	}

	public void addOnActivate(Callback callback) {

	}

	public void addOnDeactivate(Callback callback) {

	}

	public boolean isRecovering() {
		return false;
	}

	public Visualizer getVisualizer() {
		return null;
	}

	public void setContentType(String s) {

	}

	public void setDesktop(Desktop desktop) {

	}

	public void setRequestId(String s) {

	}

	public String getRequestId() {
		return null;
	}

	public Collection<AuResponse> getResponses() {
		return null;
	}

	public void setResponses(Collection<AuResponse> collection) {

	}

	public ExecutionInfo getExecutionInfo() {
		return null;
	}

	public void setExecutionInfo(ExecutionInfo executionInfo) {

	}

	public Object getExtraXelVariable(String s) {
		return null;
	}

	public Object getExtraXelVariable(XelContext xelContext, Object o,
			Object o1) {
		return null;
	}
}

