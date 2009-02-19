/* Page.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 18:17:32     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.zkoss.xel.FunctionMapper;
import org.zkoss.xel.VariableResolver;
import org.zkoss.xel.Function;

import org.zkoss.zk.scripting.Interpreter;
import org.zkoss.zk.scripting.Namespace;
import org.zkoss.zk.scripting.InterpreterNotFoundException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.LanguageDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.ComponentDefinitionMap;

/**
 * A page. A desktop consists of a set of pages.

 * <p>When a ZK request is asking to render a new page, a new page is
 * created and components that are created duing this request all belong to
 * this page.
 *
 * <p>If a ZK requst is asking an update, it must have at lease one UUID of
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
 * Moreover, pages in the same desktop could communicate to eath other
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
 * pages are locked (aka., activated). Though an execution serves
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
public interface Page extends IdSpace {
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

	/** Returns UUID (universal unique ID) which is unquie in the whole
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

	/** Returns the desktop that this page belongs to.
	 *
	 * <p>Note: it returns null when
	 * {@link org.zkoss.zk.ui.util.Initiator#doInit} is called.
	 */
	public Desktop getDesktop();

	/** Returns a readonly list of the root components.
	 */
	public Collection getRoots();
	/** Returns the first root component.
	 * @since 3.5.2
	 */
	public Component getFirstRoot();
	/** Returns the last root component.
	 * @since 3.5.2
	 */
	public Component getLastRoot();

	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same page.
	 * <p>It is also known as the page attributes.
	 * <p>It is the same as {@link Page#getAttributes}.
	 */
	public static final int PAGE_SCOPE = Component.PAGE_SCOPE;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same desktop.
	 * <p>It is also known as the desktop attributes.
	 * <p>It is the same as {@link Desktop#getAttributes}.
	 */
	public static final int DESKTOP_SCOPE = Component.DESKTOP_SCOPE;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same session.
	 * <p>It is also known as the session attributes.
	 * <p>It is the same as {@link Session#getAttributes}.
	 */
	public static final int SESSION_SCOPE = Component.SESSION_SCOPE;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the whole application.
	 * <p>It is also known as the application attributes.
	 * <p>It is the same as {@link WebApp#getAttributes}.
	 */
	public static final int APPLICATION_SCOPE = Component.APPLICATION_SCOPE;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same request.
	 * <p>It is also known as the request attributes.
	 * <p>It is the same as {@link Execution#getAttributes}.
	 */
	public static final int REQUEST_SCOPE = Component.REQUEST_SCOPE;

	/** Returns all custom attributes of the specified scope.
	 * You could reference them thru componentScope, spaceScope, pageScope,
	 * requestScope and desktopScope in zscript and EL.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 * @param scope {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Map getAttributes(int scope);
	/** Returns the value of the specified custom attribute in the specified scope.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 * @param scope {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Object getAttribute(String name, int scope);
	/** Sets the value of the specified custom attribute in the specified scope.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 * @param scope {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Object setAttribute(String name, Object value, int scope);
	/** Removes the specified custom attribute in the specified scope.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 * @param scope {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #REQUEST_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Object removeAttribute(String name, int scope);

	/** Returns all custom attributes associated with this page.
	 */
	public Map getAttributes();
	/** Returns the value of the specified attribute associated with this page.
	 */
	public Object getAttribute(String name);
	/** Sets the value of the specified custom attribute associated with this page.
	 *
	 * <p>Note: The attribute is removed (by {@link #removeAttribute}
	 * if value is null, while {@link #setVariable} considers null as a legal value.
	 *
	 * @param value the value. If null, the attribute is removed.
	 */
	public Object setAttribute(String name, Object value);
	/** Removes the specified attribute custom associated with the page.
	 */
	public Object removeAttribute(String name);

	/** Sets a variable to the namespace ({@link #getNamespace}).
	 *
	 * <p>It is the same as getNamespace().setVariable(name, value, true).
	 *
	 * @see Component#setVariable
	 * @see Component#getNamespace
	 */
	public void setVariable(String name, Object val);
	/** Returns whether the specified variable is defined.
	 *
	 * <p>Note: null is a valid value for variable, so this method is used
	 * to know whether a variable is defined.
	 * On the other hand, {@link #setAttribute} actually remove
	 * an attribute (by {@link #removeAttribute} if value is null.
	 */
	public boolean containsVariable(String name);
	/** Returns the value of a variable defined in the namespace ({@link #getNamespace}).
	 *
	 * <p>It is the same as getNamespace().getVariable(name, true).
	 *
	 * <h3>Differences between {@link #getVariable} and {@link #getZScriptVariable}</h3>
	 *
	 * <p>{@link #getVariable} returns only variables defined by
	 * {@link #setVariable} (i.e., a shortcut to {@link Namespace#setVariable}).
	 * On the other hand, {@link #getZScriptVariable} returns these variables
	 * and those defined when executing zscripts.
	 *
	 * @see Component#getVariable
	 * @see Component#getNamespace
	 */
	public Object getVariable(String name);
	/** Unsets a variable from the namespace ({@link #getNamespace}).
	 *
	 * <p>It is the same as getNamespace().unsetVariable(name, true).
	 *
	 * @see Component#unsetVariable
	 * @see Component#getNamespace
	 */
	public void unsetVariable(String name);

	/** Returns the class of the specified name by searching
	 * the thread class loader and the classes defined in the
	 * loaded interpreters.
	 *
	 * <p>Note: if not defined in the interpeter, it will also look for
	 * the class by use of the current thread's class loader.
	 * See {@link #resolveClass}.
	 *
	 * @return the class, or null if not found
	 * @see #getLoadedInterpreters
	 */
	public Class getZScriptClass(String clsnm);
	/** Resolves the class of the specified name.
	 * It first looks at Classes and then all loaded interpreters
	 * {@link #getLoadedInterpreters}.
	 *
	 * <p>It is similar to {@link #getZScriptClass}, except
	 * <ol>
	 * <li>It searches the current thread's class loader first,
	 * and then, the loaded interpreters.</li>
	 * <li>It throws ClassNotFoundException if not found</li>
	 * </ol>
	 *
	 * @since 3.0.1
	 * @see #getZScriptClass
	 */
	public Class resolveClass(String clsnm) throws ClassNotFoundException;

	/** Returns the variable of the specified name by searching
	 * the loaded interpreters.
	 *
	 * @return the method, or null if not found
	 * @see #getLoadedInterpreters
	 * @since 3.0.0
	 */
	public Function getZScriptFunction(String name, Class[] argTypes);
	/** Returns the variable of the specified name by searching
	 * the logical scope of the specified namespace for all
	 * the loaded interpreters.
	 *
	 * <p>It is similar to {@link #getZScriptVariable(String)}, except
	 * it uses the specified namespace as a reference to identify the
	 * correct scope for searching the variable.
	 * If the interpreter does NOT support hierachical scopes,
	 * this method is the same as {@link #getZScriptVariable(String)}.
	 *
	 * @param ns the namespace used as a reference to identify the
	 * correct scope for searching the variable.
	 * It is ignored if the interpreter doesn't support hierachical scopes.
	 * Note: this method doesn't look for any variable stored in ns.
	 * @return the method, or null if not found
	 * @see #getLoadedInterpreters
	 * @since 2.4.1
	 */
	public Function getZScriptFunction(Namespace ns, String name, Class[] argTypes);
	/** Returns the variable of the specified name by searching
	 * the logical scope of the namespace of the specified component
	 * for all the loaded interpreters.
	 *
	 * <p>It is a shortcut: getZScriptFunction(comp.getNamespace(), name, argTypes);
	 * @since 3.0.0
	 */
	public Function getZScriptFunction(Component comp, String name, Class[] argTypes);

	/** Returns the value of the variable of the specified name by searching
	 * the loaded interpreters, if any.
	 *
	 * <h3>Differences between {@link #getVariable} and {@link #getZScriptVariable}</h3>
	 *
	 * <p>{@link #getVariable} returns variables defined by
	 * {@link #setVariable} (i.e., a shortcut to {@link Namespace#setVariable}).
	 * On the other hand, {@link #getZScriptVariable} returns the variables
	 * that are defined when executing zscripts.
	 *
	 * @return the value of the variable, or null if not found
	 * @see #getLoadedInterpreters
	 */
	public Object getZScriptVariable(String name);
	/** Returns the value of the variable of the specified name by searching
	 * the logical scope of the specified namespace for all
	 * the loaded interpreters, if any.
	 *
	 * <p>It is similar to {@link #getZScriptVariable(String)}, except
	 * it uses the specified namespace as a reference to identify the
	 * correct scope for searching the variable.
	 * If the interpreter does NOT support hierachical scopes,
	 * this method is the same as {@link #getZScriptVariable(String)}.
	 *
	 * @param ns the namespace used as a reference to identify the
	 * correct scope for searching the variable.
	 * It is ignored if the interpreter doesn't support hierachical scopes.
	 * Note: this method doesn't look for any variable stored in ns.
	 */
	public Object getZScriptVariable(Namespace ns, String name);
	/** Returns the value of the variable of the specified name by searching
	 * the logical scope of the namespace of the specified component
	 * for all the loaded interpreters, if any.
	 *
	 * <p>It is a shortcut: getZScriptVariable(comp.getNamespace(), name);
	 * @since 3.0.0
	 */
	public Object getZScriptVariable(Component comp, String name);

	/** Returns a variable that is visible to XEL expressions.
	 *
	 * <p>This method is mainly used to access special variable, such as
	 * request parameters (if this page is requested by HTTP).
	 *
	 * <p>Note: components that are specified with an ID are already accessible
	 * by {@link #getVariable}.
	 * @since 3.0.0
	 */
	public Object getXelVariable(String name);
 
	/** Adds a name resolver that will be used to resolve a variable
	 * by {@link #getVariable}.
	 *
	 * <p>Note: the variables resolved by the specified resolver are
	 * accessible to both zscript and EL expressions.
	 *
	 * @return wether the resolver is added successfully.
	 * Note: if the resolver was added before, it won't be added again
	 * and this method returns false.
	 */
	public boolean addVariableResolver(VariableResolver resolver);
	/** Removes a name resolve that was added by {@link #addVariableResolver}.
	 *
	 * @return false if resolved is not added before.
	 */
	public boolean removeVariableResolver(VariableResolver resolver);

	//-- event listener --//
	/** Adds an event listener to specified event for all components in
	 * this page.
	 *
	 * <p>Due to performance consideration, unlike {@link Component#addEventListener},
	 * all event listeners for the page are deferrable, no matter
	 * {@link org.zkoss.zk.ui.event.Deferrable} is implemented or not.
	 *
	 * @param evtnm what event to listen (never null)
	 * @return whether the listener is added; false if it was added before.
	 * @see Component#addEventListener
	 */
	public boolean addEventListener(String evtnm, EventListener listener);
	/** Removes an event listener.
	 * @return whether the listener is removed; false if it was never added.
	 */
	public boolean removeEventListener(String evtnm, EventListener listener);
	/** Returns whether the event listener is available.
	 */
	public boolean isListenerAvailable(String evtnm);
	/** Returns an iterator for iterating listener for the specified event.
	 */
	public Iterator getListenerIterator(String evtnm);

	//-- special control --//
	/** Removes all components in this page.
	 *
	 * @see Execution#createComponents(String,Component,Map)
	 */
	public void removeComponents();

	/** Invalidates this page to cause all components to redraw.
	 */
	public void invalidate();

	/** Returns the namespace used to store variables belonging to
	 * the ID space of this page.
	 *
	 * @see #interpret
	 */
	public Namespace getNamespace();
	/** Interpret a script of the specified scripting language against
	 * the specified namespace.
	 *
	 * @param zslang the scripting language. If null, {@link #getZScriptLanguage}
	 * is assumed.
	 * @param ns the namspace. If null, the current namespace is assumed.
	 * The current namespace is the event target's namespace
	 * ({@link org.zkoss.zk.ui.event.Event#getTarget}),
	 * if the thread is processing an event.
	 * Otherwise, the current namespace is this page's namespace
	 */
	public void interpret(String zslang, String script, Namespace ns);
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
	public Collection getLoadedInterpreters();

	/** Returns the default scripting language which is assumed when
	 * a zscript element doesn't specify any language.
	 *
	 * @return the default scripting language, say, Java. Never null.
	 */
	public String getZScriptLanguage();
	/** Sets the defafult scripting language which will be assumed
	 * if a zscript element doesn't specify any language.
	 *
	 * @exception InterpreterNotFoundException if no such language
	 */
	public void setZScriptLanguage(String zslang)
	throws InterpreterNotFoundException;

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
	public Class getExpressionFactoryClass();
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
	public void setExpressionFactoryClass(Class expfcls);

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
	public void addFunctionMapper(FunctionMapper mapper);

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
	 * @param recur whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(String name, boolean recur);
	/** Returns the component definition of the specified class, or null
	 * if not found.
	 *
	 * <p>Note: unlike {@link LanguageDefinition#getComponentDefinition},
	 * this method doesn't throw ComponentNotFoundException if not found.
	 * It just returns null.
	 *
	 * @param recur whether to look up the component from {@link #getLanguageDefinition}
	 */
	public ComponentDefinition getComponentDefinition(Class cls, boolean recur);
}
