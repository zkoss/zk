/* GenericAutowireComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 11, 2008 10:56:06 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.zkoss.idom.Document;
import org.zkoss.lang.Classes;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.xel.Evaluator;

/**
 * <p>An abstract composer that you can extend and write intuitive onXxx 
 * event handler methods with "auto-wired" accessible variable objects such
 * as implicit objects, components, and external resolvable variables in a ZK 
 * zuml page; this class will registers onXxx events to the supervised 
 * component and wire all accessible variable objects to this composer by 
 * calling setXxx() method or set xxx field value directly per the variable 
 * name. Since 3.0.7, this composer has wired all implicit objects 
 * such as self, spaceOwner, page, desktop, session, application, 
 * componentScope, spaceScope, pageScope, desktopScope, sessionScope, 
 * applicationScope, and requestScope, so you can use them directly. Besides 
 * that, it also provides alert(String message) method, so you can call alert() 
 * without problems.</p>
 * 
 * <p>Notice that since this composer kept references to the components, single
 * instance composer object cannot be shared by multiple components.</p>
 *  
 * <p>The following is an example. The onOK event listener is registered into 
 * the target window, and the Textbox component with id name "mytextbox" is
 * injected into the "mytextbox" field automatically (so you can use 
 * mytextbox variable directly in onOK).</p>
 * 
 * <pre><code>
 * MyComposer.java
 * 
 * public class MyComposer extends GenericAutowireComposer {
 *     private Textbox mytextbox;
 *     
 *     public void onOK() {
 *         mytextbox.setValue("Enter Pressed");
 *         alert("Hi!");
 *     }
 * }
 * 
 * test.zul
 * 
 * &lt;window id="mywin" apply="MyComposer">
 *     &lt;textbox id="mytextbox"/>
 * &lt;/window>
 * </code></pre>
 * 
 * @author henrichen
 * @since 3.0.6
 * @see org.zkoss.zk.ui.Components#wireFellows
 */
abstract public class GenericAutowireComposer extends GenericComposer {
	/** Implicit Object; the applied component itself. 
	 * @since 3.0.7
	 */ 
	protected Component self;
	/** Implicit Object; the space owner of the applied component.
	 * @since 3.0.7
	 */
	protected IdSpace spaceOwner;
	/** Implicit Object; the page.
	 * @since 3.0.7
	 */
	protected Page page;
	/** Implicit Object; the desktop.
	 * @since 3.0.7
	 */
	protected Desktop desktop;
	/** Implicit Object; the session.
	 * @since 3.0.7
	 */
	protected Session session;
	/** Implicit Object; the web application.
	 * @since 3.0.7
	 */
	protected WebApp application;
	/** Implicit Object; a map of attributes defined in the applied component.
	 * @since 3.0.7
	 */
	protected Map componentScope;
	/** Implicit Object; a map of attributes defined in the ID space contains the applied component.
	 * @since 3.0.7
	 */
	protected Map spaceScope;
	/** Implicit Object; a map of attributes defined in the page.
	 * @since 3.0.7
	 */
	protected Map pageScope;
	/** Implicit Object; a map of attributes defined in the desktop.
	 * @since 3.0.7
	 */
	protected Map desktopScope;
	/** Implicit Object; a map of attributes defined in the session.
	 * @since 3.0.7
	 */
	protected Map sessionScope;
	/** Implicit Object; a map of attributes defined in the web application.
	 * @since 3.0.7
	 */
	protected Map applicationScope;
	/** Implicit Object; a map of attributes defined in the request.
	 * @since 3.0.7
	 */
	protected Map requestScope;
	/** Implicit Object; the current execution.
	 * @since 3.0.7
	 */
	protected Execution execution;
/* GenericAutowireComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Jun 11, 2008 10:56:06 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.zkoss.idom.Document;
import org.zkoss.lang.Classes;
import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.IdSpace;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.xel.Evaluator;

/**
 * <p>An abstract composer that you can extend and write intuitive onXxx 
 * event handler methods with "auto-wired" accessible variable objects such
 * as implicit objects, components, and external resolvable variables in a ZK 
 * zuml page; this class will registers onXxx events to the supervised 
 * component and wire all accessible variable objects to this composer by 
 * calling setXxx() method or set xxx field value directly per the variable 
 * name. Since 3.0.7, this composer has wired all implicit objects 
 * such as self, spaceOwner, page, desktop, session, application, 
 * componentScope, spaceScope, pageScope, desktopScope, sessionScope, 
 * applicationScope, and requestScope, so you can use them directly. Besides 
 * that, it also provides alert(String message) method, so you can call alert() 
 * without problems.</p>
 * 
 * <p>Notice that since this composer kept references to the components, single
 * instance composer object cannot be shared by multiple components.</p>
 *  
 * <p>The following is an example. The onOK event listener is registered into 
 * the target window, and the Textbox component with id name "mytextbox" is
 * injected into the "mytextbox" field automatically (so you can use 
 * mytextbox variable directly in onOK).</p>
 * 
 * <pre><code>
 * MyComposer.java
 * 
 * public class MyComposer extends GenericAutowireComposer {
 *     private Textbox mytextbox;
 *     
 *     public void onOK() {
 *         mytextbox.setValue("Enter Pressed");
 *         alert("Hi!");
 *     }
 * }
 * 
 * test.zul
 * 
 * &lt;window id="mywin" apply="MyComposer">
 *     &lt;textbox id="mytextbox"/>
 * &lt;/window>
 * </code></pre>
 * 
 * @author henrichen
 * @since 3.0.6
 * @see org.zkoss.zk.ui.Components#wireFellows
 */
abstract public class GenericAutowireComposer extends GenericComposer {
	/** Implicit Object; the applied component itself. 
	 * @since 3.0.7
	 */ 
	protected Component self;
	/** Implicit Object; the space owner of the applied component.
	 * @since 3.0.7
	 */
	protected IdSpace spaceOwner;
	/** Implicit Object; the page.
	 * @since 3.0.7
	 */
	protected Page page;
	/** Implicit Object; the desktop.
	 * @since 3.0.7
	 */
	protected Desktop desktop;
	/** Implicit Object; the session.
	 * @since 3.0.7
	 */
	protected Session session;
	/** Implicit Object; the web application.
	 * @since 3.0.7
	 */
	protected WebApp application;
	/** Implicit Object; a map of attributes defined in the applied component.
	 * @since 3.0.7
	 */
	protected Map componentScope;
	/** Implicit Object; a map of attributes defined in the ID space contains the applied component.
	 * @since 3.0.7
	 */
	protected Map spaceScope;
	/** Implicit Object; a map of attributes defined in the page.
	 * @since 3.0.7
	 */
	protected Map pageScope;
	/** Implicit Object; a map of attributes defined in the desktop.
	 * @since 3.0.7
	 */
	protected Map desktopScope;
	/** Implicit Object; a map of attributes defined in the session.
	 * @since 3.0.7
	 */
	protected Map sessionScope;
	/** Implicit Object; a map of attributes defined in the web application.
	 * @since 3.0.7
	 */
	protected Map applicationScope;
	/** Implicit Object; a map of attributes defined in the request.
	 * @since 3.0.7
	 */
	protected Map requestScope;
	/** Implicit Object; the current execution.
	 * @since 3.0.7
	 */
	protected Execution execution;
	/** Implicit Object; the arg argument passed to the createComponents method. It is never null.
	 * @since 3.0.8
	 */
	protected Map arg;
	
	/**
	 * Auto wire accessible variables of the specified component into a 
	 * controller Java object; a subclass that 
	 * override this method should remember to call super.doAfterCompose(comp) 
	 * or it will not work.
	 */
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		//initialize implicit objects
		self = comp;
		spaceOwner = comp.getSpaceOwner();
		page = comp.getPage();
		desktop = comp.getDesktop();
		session = desktop.getSession();
		application = desktop.getWebApp();
		componentScope = comp.getAttributes();
		spaceScope = (spaceOwner instanceof Page) ? 
			pageScope : ((Component)spaceOwner).getAttributes();
		pageScope = page.getAttributes();
		desktopScope = desktop.getAttributes();
		sessionScope = session.getAttributes();
		applicationScope = application.getAttributes();
		requestScope = REQUEST_SCOPE;
		execution = EXECUTION;
		arg = desktop.getExecution().getArg();

		//wire variables to reference fields
		Components.wireVariables(comp, this);
	}
	
	/** Shortcut to call Messagebox.show(String).
	 * @since 3.0.7 
	 */
	private static Method SHOW;
	protected void alert(String m) {
		//zk.jar cannot depends on zul.jar; thus we call Messagebox.show() via
		//reflection. kind of weird :-).
		try {
			if (SHOW == null) {
				final Class mboxcls = Classes.forNameByThread("org.zkoss.zul.Messagebox");
				SHOW = mboxcls.getMethod("show", new Class[] {String.class});
			}
			SHOW.invoke(null, new Object[] {m});
		} catch (InvocationTargetException e) {
			throw UiException.Aide.wrap(e);
		} catch (Exception e) {
			//ignore
		}
	}
	
	//Proxy to read current execution
	private static final Exec EXECUTION = new Exec();
	private static class Exec implements Execution {
		private static final Execution exec() {
			return Executions.getCurrent();
		}
		
		public void addAuResponse(String key, AuResponse response) {
			exec().addAuResponse(key, response);
		}

		public Component createComponents(PageDefinition pagedef,
				Component parent, Map arg) {
			return exec().createComponents(pagedef, parent, arg);
		}

		public Component createComponents(String uri, Component parent, Map arg) {
			return exec().createComponents(uri, parent, arg);
		}

		public Component[] createComponents(PageDefinition pagedef, Map arg) {
			return exec().createComponents(pagedef, arg);
		}

		public Component[] createComponents(String uri, Map arg) {
			return exec().createComponents(uri, arg);
		}

		public Component createComponentsDirectly(String content,
				String extension, Component parent, Map arg) {
			return exec().createComponentsDirectly(content, extension, parent, arg);
		}

		public Component createComponentsDirectly(Document content,
				String extension, Component parent, Map arg) {
			return exec().createComponentsDirectly(content, extension, parent, arg);
		}

		public Component createComponentsDirectly(Reader reader,
				String extension, Component parent, Map arg) throws IOException {
			return exec().createComponentsDirectly(reader, extension, parent, arg);
		}

		public Component[] createComponentsDirectly(String content,
				String extension, Map arg) {
			return exec().createComponentsDirectly(content, extension, arg);
		}

		public Component[] createComponentsDirectly(Document content,
				String extension, Map arg) {
			return exec().createComponentsDirectly(content, extension, arg);
		}

		public Component[] createComponentsDirectly(Reader reader,
				String extension, Map arg) throws IOException {
			return exec().createComponentsDirectly(reader, extension, arg);
		}

		public String encodeURL(String uri) {
			return exec().encodeURL(uri);
		}

		public Object evaluate(Component comp, String expr, Class expectedType) {
			return exec().evaluate(comp, expr, expectedType);
		}

		public Object evaluate(Page page, String expr, Class expectedType) {
			return exec().evaluate(page, expr, expectedType);
		}

		public void forward(Writer writer, String page, Map params, int mode)
				throws IOException {
			exec().forward(writer, page, params, mode);
			
		}

		public void forward(String page) throws IOException {
			exec().forward(page);
		}

		public Map getArg() {
			return exec().getArg();
		}

		public Object getAttribute(String name) {
			return exec().getAttribute(name);
		}

		public Map getAttributes() {
			return exec().getAttributes();
		}

		public String getContextPath() {
			return exec().getContextPath();
		}

		public Desktop getDesktop() {
			return exec().getDesktop();
		}

		public Evaluator getEvaluator(Page page, Class expfcls) {
			return exec().getEvaluator(page, expfcls);
		}

		public Evaluator getEvaluator(Component comp, Class expfcls) {
			return exec().getEvaluator(comp, expfcls);
		}

		public String getLocalAddr() {
			return exec().getLocalAddr();
		}

		public String getLocalName() {
			return exec().getLocalName();
		}

		public int getLocalPort() {
			return exec().getLocalPort();
		}

		public Object getNativeRequest() {
			return exec().getNativeRequest();
		}

		public Object getNativeResponse() {
			return exec().getNativeResponse();
		}

		public PageDefinition getPageDefinition(String uri) {
			return exec().getPageDefinition(uri);
		}

		public PageDefinition getPageDefinitionDirectly(String content,
				String extension) {
			return exec().getPageDefinitionDirectly(content, extension);
		}

		public PageDefinition getPageDefinitionDirectly(Document content,
				String extension) {
			return exec().getPageDefinitionDirectly(content, extension);
		}

		public PageDefinition getPageDefinitionDirectly(Reader reader,
				String extension) throws IOException {
			return exec().getPageDefinitionDirectly(reader, extension);
		}

		public String getParameter(String name) {
			return exec().getParameter(name);
		}

		public Map getParameterMap() {
			return exec().getParameterMap();
		}

		public String[] getParameterValues(String name) {
			return exec().getParameterValues(name);
		}

		public String getRemoteAddr() {
			return exec().getRemoteAddr();
		}

		public String getRemoteHost() {
			return exec().getRemoteHost();
		}

		/** @deprecated
		 */
		public String getRemoteName() {
			return exec().getRemoteName();
		}

		public String getRemoteUser() {
			return exec().getRemoteUser();
		}

		public String getServerName() {
			return exec().getServerName();
		}

		public int getServerPort() {
			return exec().getServerPort();
		}

		public String getScheme() {
			return exec().getScheme();
		}

		public String getUserAgent() {
			return exec().getUserAgent();
		}

		public Principal getUserPrincipal() {
			return exec().getUserPrincipal();
		}

		public VariableResolver getVariableResolver() {
			return exec().getVariableResolver();
		}

		public void include(Writer writer, String page, Map params, int mode)
				throws IOException {
			exec().include(writer, page, params, mode);
			
		}

		public void include(String page) throws IOException {
			exec().include(page);
		}

		public boolean isAsyncUpdate(Page page) {
			return exec().isAsyncUpdate(page);
		}

		public boolean isBrowser() {
			return exec().isBrowser();
		}

		public boolean isExplorer() {
			return exec().isExplorer();
		}

		public boolean isExplorer7() {
			return exec().isExplorer7();
		}

		public boolean isForwarded() {
			return exec().isForwarded();
		}

		public boolean isGecko() {
			return exec().isGecko();
		}

		public boolean isHilDevice() {
			return exec().isHilDevice();
		}

		public boolean isIncluded() {
			return exec().isIncluded();
		}

		public boolean isMilDevice() {
			return exec().isMilDevice();
		}

		public boolean isRobot() {
			return exec().isRobot();
		}

		public boolean isSafari() {
			return exec().isSafari();
		}

		public boolean isUserInRole(String role) {
			return exec().isUserInRole(role);
		}

		public boolean isVoided() {
			return exec().isVoided();
		}

		public void popArg() {
			exec().popArg();
		}

		public void postEvent(Event evt) {
			exec().postEvent(evt);
		}

		public void postEvent(int priority, Event evt) {
			exec().postEvent(priority, evt);
		}

		public void pushArg(Map arg) {
			exec().pushArg(arg);
		}

		public void removeAttribute(String name) {
			exec().removeAttribute(name);
		}

		public void sendRedirect(String uri) {
			exec().sendRedirect(uri);
		}

		public void sendRedirect(String uri, String target) {
			exec().sendRedirect(uri, target);
		}

		public void setAttribute(String name, Object value) {
			exec().setAttribute(name, value);
		}

		public void setVoided(boolean voided) {
			exec().setVoided(voided);
		}

		public String toAbsoluteURI(String uri, boolean skipInclude) {
			return exec().toAbsoluteURI(uri, skipInclude);
		}

		public void addResponseHeader(String name, String value) {
			exec().addResponseHeader(name, value);
		}

		public boolean containsResponseHeader(String name) {
			return exec().containsResponseHeader(name);
		}

		public String getHeader(String name) {
			return exec().getHeader(name);
		}

		public Iterator getHeaderNames() {
			return exec().getHeaderNames();
		}

		public Iterator getHeaders(String name) {
			return exec().getHeaders(name);
		}

		public void setResponseHeader(String name, String value) {
			exec().setResponseHeader(name, value);
		}
	}

	//Proxy to read current requestScope
	private static final RequestScope REQUEST_SCOPE = new RequestScope(); 
	private static class RequestScope implements Map {
		private static final Map req() {
			return Executions.getCurrent().getAttributes();
		}
		public void clear() {
			req().clear();
		}
		public boolean containsKey(Object key) {
			return req().containsKey(key);
		}
		public boolean containsValue(Object value) {
			return req().containsValue(value);
		}
		public Set entrySet() {
			return req().entrySet();
		}
		public Object get(Object key) {
			return req().get(key);
		}
		public boolean isEmpty() {
			return req().isEmpty();
		}
		public Set keySet() {
			return req().keySet();
		}
		public Object put(Object key, Object value) {
			return req().put(key, value);
		}
		public void putAll(Map arg0) {
			req().putAll(arg0);
		}
		public Object remove(Object key) {
			return req().remove(key);
		}
		public int size() {
			return req().size();
		}
		public Collection values() {
			return req().values();
		}
	}
}
