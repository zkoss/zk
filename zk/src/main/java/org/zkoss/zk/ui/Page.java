/* Page.java

	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 18:17:32     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.lang.ClassResolver;
import org.zkoss.xel.ExpressionFactory;
import org.zkoss.xel.Function;
import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.XelContext;
import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.InterpreterNotFoundException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinitionMap;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.util.Template;

/**
 * A page. A desktop consists of a set of pages.

 * <p>When a ZK request is asking to render a new page, a new page is
 * created and components that are created during this request all belong to
 * this page.
 *
 * <p>If a ZK request is asking an update, it must have at lease one UUID of
 * a component ({@link Component#getUuid}.
 * From this UUID, we know which page it belongs and activate it
 * to process the update.
 *
 <p>By activation, the system guarantees no concurrent access to pages
 * and components (so you don't need use synchronized for them).
 *
 * <h2>Desktop</h2>
 *
 * <p>In portal and some environments, a client request (e.g., ServletRequest)
 * might consists of several ZK requests ({@link org.zkoss.zk.au.AuRequest}).
 * While each ZK request might ask to create an independent page,
 * all these pages are grouped as a desktop, such that they are activated
 * and removed at the same time.
 * Moreover, pages in the same desktop could communicate to each other
 * (see <a href="#inter-page">Inter-page communication</a>).
 *
 * <p>A session, {@link Session}, might have multiple desktops of pages,
 * {@link Page}, while a page belongs to exactly one session.
 * A page, {@link Page}, might have many components, {@link Component}, while
 * a component belongs to exactly one page.
 *
 * <p>All components of the same desktop of pages are removed at the same time
 * if a page become 'obsolete'.
 *
 * <p>During each execution (${link Execution}), exactly one desktop of
 * pages are locked (a.k.a., activated). Though an execution serves
 * a client request (e.g., ServletRequest), a client request might consist
 * of multiple ZK request ({@link org.zkoss.zk.au.AuRequest}).
 * Each ZK request might target to a different page (of the same desktop).
 *
 * <h2><a name="#inter-page"></a>Inter-page Communication</h2>
 *
 * <p>To do inter-page communication, you could do:
 * <ol>
 * <li>Invoke methods of components from another page directly.</li>
 * <li>Use {@link Execution#postEvent} to post events to components from
 * another page.</li>
 * </ol>
 *
 * <p>They are the same as handling components from the same page.
 * However, invoking method directly for components from another page has
 * one restriction:<br>
 * It cannot <b>create</b> component.
 *
 * @author tomyeh
 */
public interface Page extends IdSpace, Scope, ClassResolver {
	/** Returns ID which is unique in the request (never null).
	 *
	 * <p>Note: it returns null when
	 * {@link org.zkoss.zk.ui.util.Initiator#doInit} is called.
	 */
	public String getId();

	/** Sets the identifier of this page.
	 *
	 * <p>Note: you can change the page's ID only in
	 * {@link org.zkoss.zk.ui.util.Initiator#doInit}
	 * or {@link org.zkoss.zk.ui.util.ExecutionInit#init}.
	 * Once the page is initialized (by {@link org.zkoss.zk.ui.sys.PageCtrl#init}),
	 * calling this
	 * method will cause an exception.
	 *
	 * @exception UiException if the page is initialized, i.e.,
	 * {@link org.zkoss.zk.ui.sys.PageCtrl#init} is called.
	 */
	public void setId(String id);

	/** Returns UUID (universal unique ID) which is unique in the whole
	 * session. The UUID is generated automatically and immutable.
	 *
	 * <p>It is mainly used for communication between client and server
	 * and you rarely need to access it.
	 */
	public String getUuid();

	/** Returns the title of the desktop this page belongs to
	 * (and evaluate it if it contains an expression).
	 * <p>Default: "".
	 */
	public String getTitle();

	/** Sets the title of the desktop this page belongs to
	 * (it might contain an expression).
	 */
	public void setTitle(String title);

	/** Returns the CSS style of this page, or empty if not specified.
	 */
	public String getStyle();

	/** Sets the CSS style of this page.
	 *
	 * <p>Note: Unlike {@link #setTitle}, you can change the style only in
	 * the lifecycle of the loading page.
	 */
	public void setStyle(String style);

	/**
	 * Return the meta viewport of this page, or "auto" if not specified.
	 * <p>Default: "auto".
	 * @since 6.5.0
	 */
	public String getViewport();

	/**
	 * Sets the viewport of this page.
	 * @since 6.5.0
	 */
	public void setViewport(String viewport);

	/** Returns the request path of this page, or "" if not available.
	 * <p>It is the same as the servlet path
	 * (javax.servlet.http.HttpServletRequest's getServletPath), if ZK is running
	 * at a servlet container.
	 *
	 * <p>Note: {@link Desktop#getRequestPath} returns the request path
	 * that causes the desktop to create. And, there might be multiple
	 * pages in the same desktop.
	 *
	 * @see Execution#getContextPath
	 * @see Desktop#getRequestPath
	 */
	public String getRequestPath();

	/** Returns whether the desktop is still alive.
	 * It returns false once it is destroyed.
	 * @see org.zkoss.zk.ui.sys.PageCtrl#destroy
	 * @since 5.0.3
	 */
	public boolean isAlive();

	/** Returns the desktop that this page belongs to.
	 *
	 * <p>Note: it returns null when
	 * {@link org.zkoss.zk.ui.util.Initiator#doInit} is called.
	 */
	public Desktop getDesktop();

	/** Returns a readonly list of the root components.
	 */
	public Collection<Component> getRoots();

	/** Returns the first root component.
	 * @since 3.5.2
	 */
	public Component getFirstRoot();

	/** Returns the last root component.
	 * @since 3.5.2
	 */
	public Component getLastRoot();

	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the same page.
	 * <p>It is also known as the page attributes.
	 * <p>It is the same as {@link Page#getAttributes}.
	 */
	public static final int PAGE_SCOPE = Component.PAGE_SCOPE;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the same desktop.
	 * <p>It is also known as the desktop attributes.
	 * <p>It is the same as {@link Desktop#getAttributes}.
	 */
	public static final int DESKTOP_SCOPE = Component.DESKTOP_SCOPE;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the same session.
	 * <p>It is also known as the session attributes.
	 * <p>It is the same as {@link Session#getAttributes}.
	 */
	public static final int SESSION_SCOPE = Component.SESSION_SCOPE;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the whole application.
	 * <p>It is also known as the application attributes.
	 * <p>It is the same as {@link WebApp#getAttributes}.
	 */
	public static final int APPLICATION_SCOPE = Component.APPLICATION_SCOPE;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the same request.
	 * <p>It is also known as the request attributes, or execution attributes.
	 * <p>It is the same as {@link Execution#getAttributes}.
	 */
	public static final int REQUEST_SCOPE = Component.REQUEST_SCOPE;

	/** Returns all custom attributes of the specified scope.
	 * You could reference them directly, or
	 * by use of componentScope, spaceScope, pageScope,
	 * requestScope and desktopScope in zscript and EL.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktops as this one's.
	 * @param scope one of {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Map<String, Object> getAttributes(int scope);

	/** Returns all custom attributes associated with this page.
	 */
	public Map<String, Object> getAttributes();

	/** Returns the value of the specified custom attribute in the specified scope.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktops as this one's.
	 * @param scope one of {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Object getAttribute(String name, int scope);

	/** Returns the value of the specified attribute associated with this page.
	 */
	public Object getAttribute(String name);

	/** Returns if an attribute exists.
	 * <p>Notice that <code>null</code> is a valid value, so you need this
	 * method to really know if an attribute is defined.
	 * @since 5.0.0
	 */
	public boolean hasAttribute(String name, int scope);

	/** Returns if an attribute exists.
	 * <p>Notice that <code>null</code> is a valid value, so you need this
	 * method to really know if an attribute is defined.
	 * @since 5.0.0
	 */
	public boolean hasAttribute(String name);

	/** Sets the value of the specified custom attribute in the specified scope.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktops as this one's.
	 * @param scope one of {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Object setAttribute(String name, Object value, int scope);

	/** Sets the value of the specified custom attribute associated with this page.
	 * @param value the value.
	 */
	public Object setAttribute(String name, Object value);

	/** Removes the specified custom attribute in the specified scope.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktops as this one's.
	 * @param scope one of {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Object removeAttribute(String name, int scope);

	/** Removes the specified attribute custom associated with the page.
	 */
	public Object removeAttribute(String name);

	/** Returns the custom attribute associated with this page,
	 * or the fellow of this page; or null if no found.
	 *
	 * <p>Notice that this method will NOT check for any variable defined in
	 * the variable resolver ({@link #addVariableResolver}).
	 * You have to invoke {@link #getXelVariable(XelContext,Object,Object,boolean)}
	 * or {@link #getXelVariable(String)} manually.
	 *
	 * @param recurse whether to look up the desktop/session for the
	 * existence of the attribute.
	 * @since 5.0.0
	 */
	public Object getAttributeOrFellow(String name, boolean recurse);

	/** Returns if a custom attribute is associated with this page,
	 * or a fellow of this page.
	 *
	 * <p>Notice that this method will NOT check for any variable defined in
	 * the variable resolver ({@link #addVariableResolver}).
	 * You have to invoke {@link #getXelVariable(XelContext,Object,Object,boolean)}
	 * or {@link #getXelVariable(String)} manually.
	 *
	 * @param recurse whether to look up the desktop/session for the
	 * existence of the attribute.
	 * @since 5.0.0
	 */
	public boolean hasAttributeOrFellow(String name, boolean recurse);

	/** Resolves the class of the specified name.
	 * It first looks at {@link ClassResolver} (registered with
	 * {@link #addClassResolver}). If not found, it looks at the current
	 * thread's class loader. And then, it looks for classes defined
	 * in any loaded interpreters ({@link #getLoadedInterpreters}).
	 *
	 * @param clsnm the class name. It does not have to be a fully qualified name
	 * (i.e., it could have no package name), if the class is imported by
	 * use of the import directive (such as &lt;?import class="com.foo.*"?&gt;).
	 * @since 3.0.1
	 */
	public Class<?> resolveClass(String clsnm) throws ClassNotFoundException;

	/** Adds a class resolver to this page.
	 * @param resolver the class resolver to be added.
	 * Currently it supports only {@link org.zkoss.lang.ImportedClassResolver}.
	 * @since 6.0.0
	 * @see #resolveClass
	 */
	public boolean addClassResolver(ClassResolver resolver);

	/** Returns the class of the specified name by searching
	 * the classes defined in the loaded interpreters ({@link #getLoadedInterpreters}).
	 *
	 * <p>Note that: since ZK 6, this method will <b>not</b> search 
	 * the current thread's class loader.
	 * In other words, you'd like to look for a class from a given page, use {@link #resolveClass} instead.
	 *
	 * <p>Also notice that it won't throw an exception if not found. Rather,
	 * it returns null.
	 *
	 * @param clsnm the fully qualified class name. Unlike {@link #resolveClass},
	 * this method does not support the imported class (by {@link #addClassResolver}).
	 * @return the class, or null if not found
	 * @see #resolveClass
	 * @see #getLoadedInterpreters
	 */
	public Class<?> getZScriptClass(String clsnm);

	/** Returns the function of the specified name by searching
	 * the loaded interpreters.
	 *
	 * @return the method, or null if not found
	 * @see #getLoadedInterpreters
	 * @since 3.0.0
	 */
	public Function getZScriptFunction(String name, Class[] argTypes);

	/** Returns the function of the specified name by searching
	 * the logical scope of the specified component
	 * in all the loaded interpreters.
	 *
	 * @param comp the component to start the search. If null, this
	 * method searches only the page's attributes.
	 * In other words, if comp is null, this method is the same as
	 * {@link #getZScriptFunction(String, Class[])}.
	 * @since 3.0.0
	 */
	public Function getZScriptFunction(Component comp, String name, Class[] argTypes);

	/** Returns the value of the variable of the specified name by searching
	 * the loaded interpreters, if any.
	 *
	 * @return the value of the variable, or null if not found
	 * @see #getLoadedInterpreters
	 */
	public Object getZScriptVariable(String name);

	/** Returns the value of the variable of the specified name by searching
	 * the logical scope of the specified component
	 * in all the loaded interpreters, if any.
	 *
	 * @param comp the component as the context to look for the variable
	 * defined in an interpreter. If null, the context is assumed to
	 * be this page.
	 * @since 3.0.0
	 */
	public Object getZScriptVariable(Component comp, String name);

	/** Returns a variable that is visible to XEL expressions.
	 * It is a shortcut of <code>getXelVariable(null, null, name, false)</code>.
	 *
	 * <p>This method is mainly used to access special variable, such as
	 * request parameters (if this page is requested by HTTP).
	 * @see #getXelVariable(XelContext, Object, Object, boolean)
	 * @since 3.0.0
	 */
	public Object getXelVariable(String name);

	/** Returns a variable that is visible to XEL expressions.
	 * <p>Unlike {@link #getXelVariable(String)}, this method
	 * can utilize {@link org.zkoss.xel.VariableResolverX} if you'd like
	 * to retrieve a property of another object.
	 * @param ctx the XEL context
	 * @param base the base object. If null, it looks for a top-level variable.
	 * If not null, it looks for a member of the base object (such as getter).
	 * @param name the property to retrieve.
	 * @param ignoreExec whether to ignore the current execution
	 * ({@link Execution#getVariableResolver}.
	 * If true, it invokes only the variable resolvers define in this page
	 *({@link #addVariableResolver}).
	 * If false, it will first check the execution, so the implicit objects
	 * such as <code>page</code> and <code>desktop</code> will be resolved.
	 * @see #getXelVariable(String)
	 * @since 5.0.0
	 */
	public Object getXelVariable(XelContext ctx, Object base, Object name, boolean ignoreExec);

	/** Adds a variable resolver that will be used to resolve a variable
	 * by {@link #getXelVariable}.
	 *
	 * <p>The new added variable resolver has the higher priority.
	 * <p>Note: the variables resolver by the specified resolver are
	 * accessible to both zscript and EL expressions.
	 *
	 * @return whether the resolver is added successfully.
	 * Note: if the resolver was added before, it won't be added again
	 * and this method returns false.
	 */
	public boolean addVariableResolver(VariableResolver resolver);

	/** Removes a variable resolver that was added by {@link #addVariableResolver}.
	 *
	 * @return false if the resolver is not added before.
	 */
	public boolean removeVariableResolver(VariableResolver resolver);

	/** Returns if the specified variable resolver has been registered
	 * @see #addVariableResolver
	 * @since 5.0.3
	 */
	public boolean hasVariableResolver(VariableResolver resolver);

	//-- event listener --//
	/** Adds an event listener to specified event for all components in
	 * this page.
	 *
	 * <p>Due to performance consideration, unlike {@link Component#addEventListener},
	 * all event listeners for the page are deferrable, no matter
	 * {@link org.zkoss.zk.ui.event.Deferrable} is implemented or not.
	 *
	 * <h2>Version Difference</h2>
	 * <p>ZK 5.0 and earlier, the second registration is ignored if an event
	 * listener has been registered twice.
	 * However, since 6.0.0 and later, it won't be ignored. If a listener has
	 * been registered multiple times, it will be invoked multiple times.
	 * <p>If you prefer to ignore the second registration, you could specify
	 * a library property called "org.zkoss.zk.ui.EventListener.duplicateIgnored"
	 * to true.
	 *
	 * @param evtnm what event to listen (never null)
	 * @return whether the listener is added successfully
	 * @see Component#addEventListener
	 */
	public boolean addEventListener(String evtnm, EventListener<? extends Event> listener);

	/** Removes an event listener.
	 * @return whether the listener is removed; false if it was never added.
	 */
	public boolean removeEventListener(String evtnm, EventListener<? extends Event> listener);

	/** Returns whether the event listener is available.
	 */
	public boolean isListenerAvailable(String evtnm);

	/** Returns an iterable collection of the event listeners for the given event.
	 * <p>Note: it is OK to invoke {@link #addEventListener} or {@link #removeEventListener}
	 * when iterating through the event listeners with the returned collection.
	 * <p>To remove an event listener from the returned iterable collection,
	 * you could invoke {@link Iterable#iterator}'s {@link Iterator#remove}.
	 * @since 6.0.0
	 */
	public Iterable<EventListener<? extends Event>> getEventListeners(String evtnm);

	//-- special control --//
	/** Removes all components in this page.
	 *
	 * @see Execution#createComponents(String,Component,Map)
	 */
	public void removeComponents();

	/** Invalidates this page to cause all components to redraw.
	 */
	public void invalidate();

	/** Interprets a script in the specified scripting language in
	 * the context of the specified scope.
	 *
	 * @param zslang the scripting language. If null, {@link #getZScriptLanguage}
	 * is assumed.
	 * @param scope the scope used as the context.
	 * Since a component is a scope, you can pass a component as the context.
	 * By context we mean the attribute of the scope, its space owner,
	 * spacer owner's space owner, page and desktop will be searched.
	  *If null, this page is assumed.
	 * @since 5.0.0
	 */
	public void interpret(String zslang, String script, Scope scope);

	/** Returns the interpreter of the specified scripting language.
	 *
	 * <p>The interpreter will be loaded and initialized,
	 * if it is not loaded yet.
	 *
	 * @param zslang the scripting language. If null, {@link #getZScriptLanguage}
	 * is assumed.
	 * @exception InterpreterNotFoundException if not found.
	 */
	public Interpreter getInterpreter(String zslang);

	/** Returns all loaded interpreters.
	 */
	public Collection<Interpreter> getLoadedInterpreters();

	/** Returns the default scripting language which is assumed when
	 * a zscript element doesn't specify any language.
	 *
	 * @return the default scripting language, say, Java. Never null.
	 */
	public String getZScriptLanguage();

	/** Sets the default scripting language which will be assumed
	 * if a zscript element doesn't specify any language.
	 *
	 * @exception InterpreterNotFoundException if no such language
	 */
	public void setZScriptLanguage(String zslang) throws InterpreterNotFoundException;

	/** Returns the implementation of the expression factory that
	 * is used by this page, or null if
	 * {@link org.zkoss.zk.ui.util.Configuration#getExpressionFactoryClass}
	 * is used.
	 *
	 * <p>Default: null.
	 *
	 * @see #setExpressionFactoryClass
	 * @since 3.0.4
	 */
	public Class<? extends ExpressionFactory> getExpressionFactoryClass();

	/** Sets the implementation of the expression factory that
	 * is used by this page.
	 *
	 * @param expfcls the class that implements
	 * {@link org.zkoss.xel.ExpressionFactory},
	 * If null, {@link org.zkoss.zk.ui.util.Configuration#getExpressionFactoryClass}
	 * is used.
	 * @see #getExpressionFactoryClass
	 * @since 3.0.4
	 */
	public void setExpressionFactoryClass(Class<? extends ExpressionFactory> expfcls);

	/** Returns if this page is a complete page.
	 * By complete we mean the page has everything that the client expects.
	 * For example, for HTML browsers, the page will generate
	 * the HTML, HEAD and BODY tags.
	 *
	 * <p>It is meaningful only if it is the top-level page (i.e.,
	 * not included by the <code>include</code> component).
	 *
	 * <p>Default: false. It means ZK loader will enclose
	 * the page content with HTML/HEAD/BODY if necessary (such as
	 * not included by other Servlet).
	 *
	 * <p>If you have a page that has a complete HTML page and
	 * it is included by other page, you have to specify the complete flag
	 * to be true.
	 * @since 3.0.4
	 */
	public boolean isComplete();

	/** Sets if the page is a complete page.
	 *
	 * <p>Default: false. It means a page is complete if and only if it
	 * is <em>not</em> included by other page.
	 *
	 * @param complete whether the page is complete.
	 * If true, this page is assumed to be complete no matter it is included
	 * or not. If false, this page is assumed to be complete if it is
	 * not included by other page.
	 * @see #isComplete
	 * @since 3.0.4
	 */
	public void setComplete(boolean complete);

	//metainfo//
	/** Returns the function mapper for resolving XEL functions, or null if
	 * not available.
	 * The function mapper represents all function mappers being added.
	 *
	 * @since 3.0.0
	 */
	public FunctionMapper getFunctionMapper();

	/** Adds the function mapper in addition to the current one.
	 *
	 * <p>The new added function mapper has the higher priority.
	 * {@link #getFunctionMapper} will return the new
	 *
	 * @param mapper the new function mapper (null to ignore).
	 */
	public boolean addFunctionMapper(FunctionMapper mapper);

	/** Removes a function mapper that was added by {@link #addFunctionMapper}.
	 *
	 * @return false if the mapper is not added before.
	 * @since 5.0.8
	 */
	public boolean removeFunctionMapper(FunctionMapper mapper);

	/** Returns if the specified function mapper has been registered
	 * @see #addFunctionMapper
	 * @since 5.0.8
	 */
	public boolean hasFunctionMapper(FunctionMapper mapper);

	/** Returns the language definition that this page belongs to (never null).
	 */
	public LanguageDefinition getLanguageDefinition();

	/** Returns the map of component definitions (never null).
	 *
	 * <p>Note: the map is shared among all instance of the same page definition.
	 * Any component definition added (by {@link ComponentDefinitionMap#add})
	 * are visible to all other instances of the same page definition.
	 * Thus, it is NOT a good idea to change its content.
	 */
	public ComponentDefinitionMap getComponentDefinitionMap();

	/** Returns the component definition of the specified name, or null
	 * if not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 *
	 * @param recurse whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(String name, boolean recurse);

	/** Returns the component definition of the specified class, or null
	 * if not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 *
	 * @param recurse whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(Class<? extends Component> cls, boolean recurse);

	/**
	 * Adds page scope template
	 * @param name
	 * @param template
	 * @since 8.0.0
	 */
	public void addTemplate(String name, Template template);

	/**
	 * Removes page scope template
	 * @param name
	 * @since 8.0.0
	 */
	public void removeTemplate(String name);

	/**
	 * Gets page scope template by name
	 * @param name
	 * @since 8.0.0
	 */
	public Template getTemplate(String name);
}
