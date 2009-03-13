/* Execution.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 17:55:01     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Iterator;
import java.util.Map;
import java.io.Reader;
import java.io.Writer;
import java.io.IOException;
import java.security.Principal;

import org.zkoss.xel.VariableResolver;
import org.zkoss.idom.Document;

import org.zkoss.web.servlet.Servlets;
import org.zkoss.web.servlet.http.Encodes;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.au.AuResponse;

/**
 * An execution of a client request (e.g., ServletRequest).
 * When a request sent from a client, the server constructs
 * a {@link Execution}
 * object to hold execution relevant info, and then serves the request
 * thru this execution.
 *
 * <p>A client request, e.g., HttpServletRequest, might consist of
 * multiple ZK request ({@link org.zkoss.zk.au.AuRequest}).
 * However, these ZK requests
 * must target the same desktop of pages ({@link Page}).
 *
 * <p>Because a request might come from HTTP or other protocol, Execution
 * also serves as an isolation layer.
 *
 * @author tomyeh
 * @see Page
 */
public interface Execution extends Scope {
	/** Returns the desktop for this execution.
	 * Each execution is against exactly one desktop.
	 */
	public Desktop getDesktop();

	/** Returns whether this execution is asynchronous updating the
	 * specified page (thru ZK Update Engine).
	 *
	 * @return whether the specified page is being asynchronous updated
	 * by this execution.
	 * If the specified page is null, this method returns
	 * whether this execution is an asynchronous update
	 * (rather than a request starting a new desktop).<br/>
	 * Note: since 5.0.0, isAsyncUpdate(null) return if the fisrt execution
	 * is caused by aysnchronous update (not just the current execution).
	 */
	public boolean isAsyncUpdate(Page page);

	/** Returns an array of String objects containing all of the values
	 * the given request parameter has, or null if the parameter does not exist.
	 */
	public String[] getParameterValues(String name);
	/** Returns the value of a request parameter as a String,
	 * or null if the parameter does not exist
	 */
	public String getParameter(String name);
	/** Returns a Map of the parameters of this request.
	 * Request parameters are extra information sent with the request.
	 */
	public Map getParameterMap();

	/** Returns the evaluator (never null).
	 * It is usually used to parse the expression into {@link org.zkoss.xel.Expression}
	 * or used with {@link org.zkoss.zk.xel.ExValue}.
	 * for performance improvement.
	 *
	 * @param page the page that this evaluator is associated.
	 * If null, the current page and then the first page is assumed.
	 * @param expfcls the implementation of {@link org.zkoss.xel.ExpressionFactory},
	 * or null to use the default ({@link org.zkoss.zk.ui.util.Configuration#getExpressionFactoryClass}.
	 * @since 3.0.0
	 */
	public Evaluator getEvaluator(Page page, Class expfcls);
	/** Returns the evaluator of the current execution.
	 * It is a shortcut of getEvaluator(comp != null ? comp.getPage(): null)
	 *
	 * @param comp the component to retrieve the page for the evaluator
	 * @param expfcls the implementation of {@link org.zkoss.xel.ExpressionFactory},
	 * or null to use the default ({@link org.zkoss.zk.ui.util.Configuration#getExpressionFactoryClass}.
	 * @since 3.0.0
	 */
	public Evaluator getEvaluator(Component comp, Class expfcls);

	/** Evluates the specified expression with ${link #getVariableResolver}
	 * and {@link Page#getFunctionMapper} of the page of the specified
	 * component.
	 *
	 * <p>The function mapper is retrieved from component's page's function
	 * mapper ({@link Page#getFunctionMapper}).
	 * If null, the current page, if any, is used to retrieve
	 * the mapper.
	 *
	 * <p>For better performance, you can use the instance returned by
	 *{@link #getEvaluator} to parse and cached the parsed expression.
	 * {@link org.zkoss.zk.xel.ExValue} is a utility class to simply
	 * the task.
	 *
	 * @param comp used as the self variable and to retrieve the function
	 * mapper. Ignored if null.
	 * @see #getVariableResolver
	 * @see #getEvaluator
	 */
	public Object evaluate(Component comp, String expr, Class expectedType);
	/** Evluates the specified expression with ${link #getVariableResolver}
	 * and {@link Page#getFunctionMapper} of the specified
	 * page.
	 *
	 * <p>The function mapper is retrieved from component's page's function
	 * mapper ({@link Page#getFunctionMapper}).
	 * If null, the current page, if any, is used to retrieve
	 * the mapper.
	 *
	 * <p>For better performance, you can use the instance returned by
	 *{@link #getEvaluator} to parse and cached the parsed expression.
	 * {@link org.zkoss.zk.xel.ExValue} is a utility class to simply
	 * the task.
	 *
	 * @param page used as the self variable and to retrieve the function
	 * mapper. Ignored if null.
	 * @see #getVariableResolver
	 * @see #getEvaluator
	 */
	public Object evaluate(Page page, String expr, Class expectedType);

	/** Returns the variable resolver for this execution, or null if not
	 * available.
	 *
	 * <p>Note: the resolver is similar to PageContext's if this execution
	 * is caused by a HTTP request.
	 * @see #evaluate(Component,String,Class)
	 */
	public VariableResolver getVariableResolver();

	/** Queues an event to this execution.
	 * In other words, the event is placed to the event queue.
	 *
	 * <p>The priority of the event is assumed to be 0. Refer to
	 * {@link #postEvent(int, Event)}.
	 */
	public void postEvent(Event evt);
	/** Queues an event with the specified priority to this execution.
	 * In other words, the event is placed to the event queue
	 * with the specified prority.
	 *
	 * <p>The posted events are processed from the higher priority to the 
	 * lower one. If two events are posted with the same priority,
	 * the earlier the event being posted is processed earlier
	 * (first-in-first-out).
	 *
	 * <p>The priority posted by posted by {@link #postEvent(Event)} is
	 * 0.
	 * Applications shall not use the priority higher than 10,000 and
	 * lower than -10,000 since they are reserved for component
	 * development.
	 *
	 * @param priority the priority of the event.
	 * @since 3.0.7
	 */
	public void postEvent(int priority, Event evt);

	/** Whether to overwrite uri if both uri and params contain the same
	 * parameter.
	 */
	public static final int OVERWRITE_URI = Servlets.OVERWRITE_URI;
	/** Whether to ignore params if both uri and params contain the same
	 * parameter.
	 */
	public static final int IGNORE_PARAM = Servlets.IGNORE_PARAM;
	/** Whether to append params if both uri and params contain the same
	 * parameter. In other words, they both appear as the final query string.
	 */
	public static final int APPEND_PARAM = Servlets.APPEND_PARAM;
	/** Whether the specified parameters shall be passed thru the request's
	 * attribute called arg.
	 */
	public static final int PASS_THRU_ATTR = Servlets.PASS_THRU_ATTR;
	/** Includes a page.
	 *
	 * @param writer the output to write. If null, the response's default
	 * writer is used.
	 * @param page the page's uri; null to denote the same request
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * {@link #APPEND_PARAM} and {@link #PASS_THRU_ATTR}.
	 * It defines how to handle if both uri and params contains
	 * the same parameter.
	 * mode is used only if both uri contains query string and params is
	 * not empty.
	 */
	public void include(Writer writer, String page, Map params, int mode)
	throws IOException;
	/** A shortcut of include(null, page, null, 0).
	 */
	public void include(String page)
	throws IOException;
	/** Forwards to another page.
	 *
	 * <p>Note: this method can be called only when loading a page.
	 * Use {@link #sendRedirect(String)} instead if you want to change
	 * to another desktop when processing a request from the client.
	 *
	 * @param writer the output to write. If null, the response's default
	 * writer is used.
	 * @param page the page's uri; null to denote the same request
	 * @param mode one of {@link #OVERWRITE_URI}, {@link #IGNORE_PARAM},
	 * {@link #APPEND_PARAM} and {@link #PASS_THRU_ATTR}.
	 * It defines how to handle if both uri and params contains
	 * the same parameter.
	 * mode is used only if both uri contains query string and params is
	 * not empty.
	 */
	public void forward(Writer writer, String page, Map params, int mode)
	throws IOException;
	/** A shortcut of forward(null, page, null, 0).
	 */
	public void forward(String page)
	throws IOException;
	/** Returns whether the execution is voided.
	 * By void we mean the request is taken charged by other servlet.
	 * The execution shall not do anything more. In other words,
	 * the execution is avoided and won't generate any ouput.
	 *
	 * <p>The common cause of being voided is the invocation of
	 * {@link #forward}.
	 *
	 * @since 2.4.0
	 */
	public boolean isVoided();
	/** Sets whether the execution is voided.
	 * By void we mean the request is taken charged by other servlet.
	 *
	 * <p>If you invoke {@link #forward}, this method is called automatically
	 * with true. Thus, you rarely need to invoke this method, unless
	 * you forward to other servlet by use javax.servlet.RequestDispatcher
	 * directly.
	 *
	 * @since 2.4.1
	 */
	public void setVoided(boolean voided);

	/** Returns whether this execution is included by some other pages.
	 */
	public boolean isIncluded();
	/** Returns whether the execution is forwarded from other pages.
	 * @since 2.4.0
	 */
	public boolean isForwarded();

	/** Converts the specified URI to an absolute URI, if uri is related
	 * and the current execution is not included ({@link #isIncluded}).
	 *
	 * <p>Note: an asynchrous update is processed by the update servlet.
	 * It is different from the servlet for rendering the ZUML page.
	 * In other words, a relative URI won't be interpreted correctly,
	 * so you have to invoke this method to convert them if necessary.
	 *
	 * <p>In addtions, RequestDispatcher.include doesn't handle related URI
	 * well.
	 *
	 * @param skipInclude whether not to convert to an absolute URI if
	 * the current page is included by another page.
	 * If you are not sure, you might specify false.
	 */
	public String toAbsoluteURI(String uri, boolean skipInclude);

	/** Encodes an URL.
	 *
	 * <p>It resolves "*" contained in URI, if any, to the proper Locale,
	 * and the browser code.
	 * Refer to {@link Servlets#locate(ServletContext, ServletRequest, String, Locator)}
	 * for details. 
	 */
	public String encodeURL(String uri);

	/** Returns a java.security.Principal object containing the name of the
	 * current authenticated user.
	 * If the user has not been authenticated, the method returns null.
	 */
	public Principal getUserPrincipal();
	/** Returns a boolean indicating whether the authenticated user is
	 * included in the specified logical "role". Roles and role membership
	 * can be defined using deployment descriptors.
	 * If the user has not been authenticated, the method returns false.
	 *
	 * @param role a String specifying the name of the role
	 */
	public boolean isUserInRole(String role);
	/** Returns the login of the user making this request, if the user has
	 * been authenticated, or null if the user has not been authenticated.
	 * Whether the user name is sent with each subsequent request depends on
	 * the browser and type of authentication.
	 */
	public String getRemoteUser();
	/** Returns the fully qualified name of the client or the last proxy
	 * that sent the request.
	 * If the engine cannot or chooses not to resolve the hostname
	 * (to improve performance), this method returns the dotted-string form of
	 * the IP address.
	 */
	public String getRemoteHost();
	/** @deprecated As of release 3.0.1, replaced by {@link #getRemoteHost}.
	 */
	public String getRemoteName();
	/**  Returns the Internet Protocol (IP) address of the client or last
	 * proxy that sent the request.
	 */
	public String getRemoteAddr();
	/** Returns the host name of the server to which the request was sent.
	 * It is the value of the part before ":" in the Host header value, if any,
	 * or the resolved server name, or the server IP address.
	 *
	 * @see #getLocalName
	 */
	public String getServerName();
	/** Returns the port number to which the request was sent.
	 * It is the value of the part after ":" in the Host header value, if any,
	 * or the server port where the client connection was accepted on.
	 *
	 * @see #getLocalPort
	 */
	public int getServerPort();
	/** Returns the host name of the Internet Protocol (IP) interface
	 * on which the request was received.
	 *
	 * <p>Note: it is the host name defined in the server. To retrieve the name
	 * in URL, use {@link #getServerName}.
	 *
	 * @see #getServerName
	 */
	public String getLocalName();
	/** Returns the Internet Protocol (IP) address of the interface on which
	 * the request was received.
	 */
	public String getLocalAddr();
	/** Returns the Internet Protocol (IP) port number of the interface on which
	 * the request was received.
	 *
	 * @see #getServerPort
	 */
	public int getLocalPort();

	/** Returns the name of the scheme used to make this request,
	 * for example, http, https, or ftp.
	 * Different schemes have different rules for constructing URLs,
	 * as noted in RFC 1738.
	 * @since 3.5.0
	 */
	public String getScheme();

	/** Returns the portion of the request URI that indicates the context of
	 * the current execution. The path starts with a "/" character but does not end with
	 * a "/" character. For servlets in the default (root) context,
	 * this method returns "".
	 *
	 * <p>If the client is not using HTTP to access, this method return "";
	 *
	 * @see Page#getRequestPath
	 */
	public String getContextPath();

	//-- page utilities --//
	/** Returns the page definition from the page file specified by an URI.
	 *
	 * <p>Implemetation Notes: this method must invoke
	 * {@link org.zkoss.zk.ui.sys.UiFactory#getPageDefinition(org.zkoss.zk.ui.sys.RequestInfo, String)}
	 *
	 * @param uri the URI of the page file.
	 *
	 * @see #getPageDefinitionDirectly(String, String)
	 * @see #getPageDefinitionDirectly(Document, String)
	 * @see #getPageDefinitionDirectly(Reader, String)
	 */
	public PageDefinition getPageDefinition(String uri);
	/** Converts the specified page content to a page definition.
	 *
	 * <p>Implemetation Notes: this method must invoke
	 * {@link org.zkoss.zk.ui.sys.UiFactory#getPageDefinitionDirectly(org.zkoss.zk.ui.sys.RequestInfo, String, String)}
	 *
	 * @param content the raw content of the page. It must be in ZUML.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @see #getPageDefinitionDirectly(Document, String)
	 * @see #getPageDefinitionDirectly(Reader, String)
	 * @see #getPageDefinition
	 */
	public PageDefinition getPageDefinitionDirectly(String content, String extension);
	/** Converts the specified page content, in DOM, to a page definition.
	 *
	 * @param content the raw content of the page in DOM.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @see #getPageDefinitionDirectly(String, String)
	 * @see #getPageDefinitionDirectly(Reader, String)
	 * @see #getPageDefinition
	 */
	public PageDefinition getPageDefinitionDirectly(Document content, String extension);
	/** Reads the raw content from a reader and converts it into
	 * a page definition.
	 *
	 * @param reader used to input the raw content of the page. It must be in ZUML.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @see #getPageDefinitionDirectly(String, String)
	 * @see #getPageDefinitionDirectly(Document, String)
	 * @see #getPageDefinition
	 */
	public PageDefinition getPageDefinitionDirectly(Reader reader, String extension)
	throws IOException;

	/** Creates components from the specified page definition.
	 * The created components become the child of the specified parent,
	 * or become the root components of the current page if parent is specified
	 * as null.
	 *
	 * @param pagedef the page definition to use. It cannot be null.
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * In other words, the new component will be the root component
	 * of the current page if parent is null.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @return the first component being created.
	 * @see #createComponents(String, Component, Map)
	 */
	public Component createComponents(PageDefinition pagedef,
	Component parent, Map arg);
	/** Creates components from a page file specified by an URI.
	 * The created components become the child of the specified parent,
	 * or become the root components of the current page if parent is specified
	 * as null.
	 *
	 * <p>It loads the page definition from the specified URI (by
	 * use {@link #getPageDefinition} ), and then
	 * invokes {@link #createComponents(PageDefinition,Component,Map)}
	 * to create components.
	 *
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * In other words, the new component will be the root component
	 * of the current page if parent is null.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Component, Map)
	 * @see #createComponentsDirectly(String, String, Component, Map)
	 * @see #createComponentsDirectly(Document, String, Component, Map)
	 * @see #createComponentsDirectly(Reader, String, Component, Map)
	 */
	public Component createComponents(String uri, Component parent,
	Map arg);
	/** Creates components from the raw content specified by a string.
	 * The created components become the child of the specified parent,
	 * or become the root components of the current page if parent is specified
	 * as null.
	 *
	 * <p>The raw content is parsed to a page defintion by use of
	 * {@link #getPageDefinitionDirectly(String, String)}, and then
	 * invokes {@link #createComponents(PageDefinition,Component,Map)}
	 * to create components.
	 *
	 * @param content the raw content of the page. It must be in ZUML.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * In other words, the new component will be the root component
	 * of the current page if parent is null.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Component, Map)
	 * @see #createComponents(String, Component, Map)
	 * @see #createComponentsDirectly(Document, String, Component, Map)
	 * @see #createComponentsDirectly(Reader, String, Component, Map)
	 */
	public Component createComponentsDirectly(String content, String extension,
	Component parent, Map arg);
	/** Creates components from the raw content specified by a DOM tree.
	 * The created components become the child of the specified parent,
	 * or become the root components of the current page if parent is specified
	 * as null.
	 *
	 * <p>The raw content is parsed to a page defintion by use of
	 * {@link #getPageDefinitionDirectly(Document, String)}, and then
	 * invokes {@link #createComponents(PageDefinition,Component,Map)}
	 * to create components.
	 *
	 * @param content the raw content in DOM.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * In other words, the new component will be the root component
	 * of the current page if parent is null.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Component, Map)
	 * @see #createComponents(String, Component, Map)
	 * @see #createComponentsDirectly(Document, String, Component, Map)
	 * @see #createComponentsDirectly(Reader, String, Component, Map)
	 */
	public Component createComponentsDirectly(Document content, String extension,
	Component parent, Map arg);
	/** Creates components from the raw content read from the specified reader.
	 * The created components become the child of the specified parent,
	 * or become the root components of the current page if parent is specified
	 * as null.
	 *
	 * <p>The raw content is loaded and parsed to a page defintion by use of
	 * {@link #getPageDefinitionDirectly(Reader,String)}, and then
	 * invokes {@link #createComponents(PageDefinition,Component,Map)}
	 * to create components.
	 *
	 * @param reader the reader to retrieve the raw content in ZUML.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @param parent the parent component, or null if you want it to be
	 * a root component. If parent is null, the page is assumed to be
	 * the current page, which is determined by the execution context.
	 * In other words, the new component will be the root component
	 * of the current page if parent is null.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Component, Map)
	 * @see #createComponents(String, Component, Map)
	 * @see #createComponentsDirectly(Document, String, Component, Map)
	 * @see #createComponentsDirectly(String, String, Component, Map)
	 */
	public Component createComponentsDirectly(Reader reader, String extension,
	Component parent, Map arg) throws IOException;

	/** Creates components that don't belong to any page
	 * from the specified page definition.
	 *
	 * @param pagedef the page definition to use. It cannot be null.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @return the first component being created.
	 * @see #createComponents(String, Map)
	 * @since 2.4.0
	 */
	public Component[] createComponents(PageDefinition pagedef, Map arg);
	/** Creates components that don't belong to any page
	 * from a page file specified by an URI.
	 *
	 * <p>It loads the page definition from the specified URI (by
	 * use {@link #getPageDefinition} ), and then
	 * invokes {@link #createComponents(PageDefinition,Component,Map)}
	 * to create components.
	 *
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Map)
	 * @see #createComponentsDirectly(String, String, Map)
	 * @see #createComponentsDirectly(Document, String, Map)
	 * @see #createComponentsDirectly(Reader, String, Map)
	 * @since 2.4.0
	 */
	public Component[] createComponents(String uri, Map arg);
	/** Creates components that don't belong to any page
	 * from the raw content specified by a string.
	 *
	 * <p>The raw content is parsed to a page defintion by use of
	 * {@link #getPageDefinitionDirectly(String, String)}, and then
	 * invokes {@link #createComponents(PageDefinition,Component,Map)}
	 * to create components.
	 *
	 * @param content the raw content of the page. It must be in ZUML.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Map)
	 * @see #createComponents(String, Map)
	 * @see #createComponentsDirectly(Document, String, Map)
	 * @see #createComponentsDirectly(Reader, String, Map)
	 * @since 2.4.0
	 */
	public Component[] createComponentsDirectly(String content, String extension,
	Map arg);
	/** Creates components that don't belong to any page
	 * from the raw content specified by a DOM tree.
	 *
	 * <p>The raw content is parsed to a page defintion by use of
	 * {@link #getPageDefinitionDirectly(Document, String)}, and then
	 * invokes {@link #createComponents(PageDefinition,Component,Map)}
	 * to create components.
	 *
	 * @param content the raw content in DOM.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Map)
	 * @see #createComponents(String, Map)
	 * @see #createComponentsDirectly(Document, String, Map)
	 * @see #createComponentsDirectly(Reader, String, Map)
	 * @since 2.4.0
	 */
	public Component[] createComponentsDirectly(Document content, String extension,
	Map arg);
	/** Creates components that don't belong to any page
	 * from the raw content read from the specified reader.
	 *
	 * <p>The raw content is loaded and parsed to a page defintion by use of
	 * {@link #getPageDefinitionDirectly(Reader,String)}, and then
	 * invokes {@link #createComponents(PageDefinition,Component,Map)}
	 * to create components.
	 *
	 * @param reader the reader to retrieve the raw content in ZUML.
	 * @param extension the default extension if the content doesn't specify
	 * an language. In other words, if
	 * the content doesn't specify an language, {@link LanguageDefinition#getByExtension}
	 * is called.
	 * If extension is null and the content doesn't specify a language,
	 * the language called "xul/html" is assumed.
	 * @param arg a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @see #createComponents(PageDefinition, Map)
	 * @see #createComponents(String, Map)
	 * @see #createComponentsDirectly(Document, String, Map)
	 * @see #createComponentsDirectly(String, String, Map)
	 * @since 2.4.0
	 */
	public Component[] createComponentsDirectly(Reader reader, String extension,
	Map arg) throws IOException;

	/** Sends a temporary redirect response to the client using the specified
	 * redirect location URL.
	 *
	 * <p>It is the same as sendRedirect(url, null).
	 *
	 * <p>After calling this method, the caller shall end the processing
	 * immediately (by returning). All pending requests and events will
	 * be dropped.
	 *
	 * @param uri the URI to redirect to, or null to reload the same page
	 */
	public void sendRedirect(String uri);
	/** Sends a temporary redirect response to the client using the specified
	 * redirect location URL and redirect to the specified browser window.
	 *
	 * <p>After calling this method, the caller shall end the processing
	 * immediately (by returning). All pending requests and events will
	 * be dropped.
	 *
	 * <p>Note: if you specify target other than null and "_self", it'll
	 * fail if the browser prevent the server to popup a window.
	 *
	 * @param uri the URI to redirect to, or null to reload the same page
	 * @param target the name of the browser window that send-redirect will
	 * load the sepcified URI, or null if the current browser window
	 * is used.
	 */
	public void sendRedirect(String uri, String target);

	/** Returns the parameters (aka., arg) if {@link #pushArg} is called recently,
	 * or an empty map if not available.
	 *
	 * <p>The use of parameters is application dependent.
	 * ZK only provides the way to store ({@link #pushArg} and to
	 * restore {@link #popArg}. And, let the parameters being accessible in
	 * EL by referring it as the arg variable.
	 *
	 * <p>Currently, {@link #createComponents(String,Component,Map)}
	 * and similar methods use this mechanism to let caller customize a page
	 * definition.
	 *
	 * <p>Notice that {@link #createComponents(String,Component,Map)}
	 * pops arg after creating components, and before processing any event.
	 * In other words, it is not aviable for event listener, including onCreate.
	 * However, {@link org.zkoss.zk.ui.event.CreateEvent#getArg} preserves
	 * the map for its event listeners.
	 */
	public Map getArg();
	/** Pushes the parameters (aka., arg) that EL could refer it by the arg variable.
	 * Remember to call {@link #popArg} in the finally clause.
	 * @see #getArg
	 */
	public void pushArg(Map arg);
	/** Pops the parameters (aka., arg) that is pushed by {@link #pushArg}.
	 * <p>It never throws an exception.
	 * @see #getArg
	 */
	public void popArg();

	/** Adds an asynchronous response ({@link AuResponse}) which will be
	 * sent to client at the end of the execution.
	 *
	 * <p>If {@link AuResponse#getDepends} is not null, the response
	 * depends on the returned componet. In other words, the response
	 * is removed if the component is removed.
	 * If it is null, the response is component-independent.
	 *
	 * @param key could be anything. The second invocation of this method
	 * in the same execution with the same key will override the previous one.
	 * In other words, the previous one will be dropped.
	 * If null is specified, the response is simply appended to the end
	 * without overriding any previous one.
	 */
	public void addAuResponse(String key, AuResponse resposne);

	/** Returns whether the client is a browser.
	 * No matter the client is Ajax, MIL or whatever, it returns true.
	 * It returns false only if this is a 'fake' execution (aka., request).
	 */
	public boolean isBrowser();
	/** Returns whether it is a browser of the specified type.
	 *
	 * @param type the type of the browser.
	 * Allowed values include "robot", "ie", "ie6", "ie6-", "ie7", "ie8", "ie8-"
	 * "ie7-", "gecko", "gecko2", "gecko3", "gecko2-", "gecko3-"
	 * "opara", "safari",
	 * "mil", "hil", "mil-". Otherwise, it matches whether the type exist or not.<br/>
	 * Note: "ie6-" means Internet Explorer 6 only; not Internet Explorer 7
	 * or other.
	 * @since 3.5.1
	 */
	public boolean isBrowser(String type);
	/** Returns whether the client is a robot (such as Web crawlers).
	 *
	 * <p>Because there are too many robots, it returns true if the user-agent
	 * is not recognized.
	 */
	public boolean isRobot();
	/** Returns whether the client is Internet Explorer.
	 * If true, it also implies {@link #isExplorer7} is true.
	 */
	public boolean isExplorer();
	/** Returns whether the client is Internet Explorer 7 or later.
	 */
	public boolean isExplorer7();
	/** Returns whether the client is Gecko based, such as Mozilla, Firefox and Camino.
	 */
	public boolean isGecko();
	/** Returns whether the browser is Gecko 3 based, such as Firefox 3.
	 * @since 3.5.0
	 */
	public boolean isGecko3();
	/** Returns whether the client is Safari.
	 */
	public boolean isSafari();
	/** Returns whether the client is Opera.
	 * @since 3.5.1
	 */
	public boolean isOpera();
	/** Returns whether the client is a mobile device supporting MIL
	 * (Mobile Interactive Language).
	 * @since 2.4.1
	 */
	public boolean isMilDevice();
	/** Returns whether the client is a mobile device supporting HIL
	 * (Handset Interactive Language).
	 *
	 * <p>Note: ZK Mobile for Android supports both MIL and HIL.
	 * That is, both {@link #isHilDevice} and {@link #isMilDevice}
	 * return true.
	 *
	 * @since 3.0.2
	 */
	public boolean isHilDevice();

	/** Returns the user-agent header, which indicates what the client is,
	 * or an empty string if not available.
	 *
	 * <p>Note: it doesn't return null, so it is easy to test what
	 * the client is with {@link String#indexOf}.
	 *
	 * @since 3.0.2
	 */
	public String getUserAgent();

	/** Returns the native request, or null if not available.
	 *
	 * <p>The returned object depends on the Web container.
	 * If it is based Java servlet container, an instance of
	 * javax.servlet.ServletRequest is returned.
	 */
	public Object getNativeRequest();
	/** Returns the native response, or null if not available.
	 *
	 * <p>The returned object depends on the Web container.
	 * If it is based Java servlet container, an instance of
	 * javax.servlet.ServletResponse is returned.
	 */
	public Object getNativeResponse();

	/** Returns the value of the specified request attribute.
	 */
	public Object getAttribute(String name);
	/** Sets the value of the specified request attribute.
	 *
	 * @param value the value. If null, the attribute is removed.
	 */
	public void setAttribute(String name, Object value);
	/** Removes the specified request attribute.
	 */
	public void removeAttribute(String name);
	/** Returns a map of request attributes associated with this session.
	 */
	public Map getAttributes();

	/** Returns the value of the specified header as a {@link String},
	 * or null if not found.
	 * @since 3.5.0
	 */
	public String getHeader(String name);
	/** Returns all the values of the specified header as a {@link Iterator}
	 * of {@link String} objects.
	 *
	 * <p>If the request did not include any headers of the specified name,
	 * this method returns an empty {@link Iterator}.
	 * If the container does not allow access to header information,
	 * it returns null.
	 *
	 * @since 3.5.0
	 */
	public Iterator getHeaders(String name);
	/** Returns all header names this request contains.
	 * If the request has no headers, this method returns an empty
	 * {@link Iterator}.
	 * If the container does not allow access to header information,
	 * it returns null.
	 * @since 3.5.0
	 */
	public Iterator getHeaderNames();

	/** Sets a response header with the give name and value.
	 * If the header had already been set, the new value overwrites the previous one.
	 * @since 3.5.0
	 * @see #containsResponseHeader
	 */
	public void setResponseHeader(String name, String value);
	/** Adds a response header with the give name and value.
	 *  This method allows response headers to have multiple values.
	 * @since 3.5.0
	 */
	public void addResponseHeader(String name, String value);
	/** Returns whether the named response header has already been set.
	 * @since 3.5.0
	 */
	public boolean containsResponseHeader(String name);
}
