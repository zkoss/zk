/* Page.java

{{IS_NOTE
	$Id: Page.java,v 1.15 2006/05/26 03:13:42 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Jun  3 18:17:32     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui;

import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

import com.potix.zk.ui.util.Namespace;
import com.potix.zk.ui.event.EventListener;
import com.potix.zk.ui.metainfo.PageDefinition;

/**
 * A runtime instance of {@link PageDefinition}.

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
 * might consists of several ZK requests ({@link com.potix.zk.au.AuRequest}).
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
 * of multiple ZK request ({@link com.potix.zk.au.AuRequest}).
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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.15 $ $Date: 2006/05/26 03:13:42 $
 */
public interface Page extends IdSpace {
	//-- proxy to Desktop --//
	/** Returns the definition, or null if this page is not associated with
	 * any {@link PageDefinition}.
	 */
	public PageDefinition getDefinition();

	/** Returns ID which is unique in the request (never null).
	 *
	 * <p>Note: it returns null when
	 * {@link com.potix.zk.ui.util.Initiator#doInit} is called.
	 */
	public String getId();

	/** Returns the title of the desktop this page belongs to
	 * (and evaluate it if it contains an expression).
	 * <p>Default: "".
	 */
	public String getTitle();
	/** Sets the title of the desktop this page belongs to
	 * (it might contain an expression).
	 */
	public void setTitle(String title);

	/** Returns the desktop that this page belongs to.
	 *
	 * <p>Note: it returns null when
	 * {@link com.potix.zk.ui.util.Initiator#doInit} is called.
	 */
	public Desktop getDesktop();

	/** Returns a readonly list of the root components.
	 */
	public Collection getRoots();

	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same page.
	 */
	public static final int PAGE_SCOPE = Component.PAGE_SCOPE;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same desktop.
	 */
	public static final int DESKTOP_SCOPE = Component.DESKTOP_SCOPE;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same session.
	 */
	public static final int SESSION_SCOPE = Component.SESSION_SCOPE;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the whole application.
	 */
	public static final int APPLICATION_SCOPE = Component.APPLICATION_SCOPE;

	/** Returns all custom attributes of the specified scope.
	 * You could reference them thru componentScope, spaceScope, pageScope
	 * and desktopScope in BSH and EL.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 * @param scope {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Map getAttributes(int scope);
	/** Returns the value of the specified custom attribute in the specified scope.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 * @param scope {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Object getAttribute(String name, int scope);
	/** Sets the value of the specified custom attribute in the specified scope.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 * @param scope {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Object setAttribute(String name, Object value, int scope);
	/** Removes the specified custom attribute in the specified scope.
	 *
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 * @param scope {@link #APPLICATION_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #PAGE_SCOPE} or {@link #DESKTOP_SCOPE}.
	 */
	public Object removeAttribute(String name, int scope);

	/** Returns all custom attributes associated with this page.
	 */
	public Map getAttributes();
	/** Returns the value of the specified attribute associated with this page.
	 */
	public Object getAttribute(String name);
	/** Sets the value of the specified custom attribute associated with this page.
	 */
	public Object setAttribute(String name, Object value);
	/** Removes the specified attribute custom associated with the page.
	 */
	public Object removeAttribute(String name);

	/** Sets a variable that both the interpreter and EL can see it.
	 * The variable is defined in the scope of this page.
	 * In other words, it is visible to all components in this page, unless
	 * it is override by {@link Component#setVariable}.
	 */
	public void setVariable(String name, Object val);
	/** Returns the value of a variable defined in the BSH interpreter.
	 * The variable is defined in the scope of this page.
	 * In other words, it is visible to all components in this page, unless
	 * it is override by {@link Component#setVariable}.
	 */
	public Object getVariable(String name);
	/** Unsets a variable.
	 * @see #setVariable
	 */
	public void unsetVariable(String name);

	/** Resolves the variable that EL has.
	 *
	 * <p>This method is mainly used to access special variable, such as
	 * request parameters (if this page is requested by HTTP).
	 *
	 * <p>Note: components that are specified with an ID are already accessible
	 * by {@link #getVariable}.
	 */
	public Object resolveElVariable(String name);
 
	//-- event listener --//
	/** Adds an event listener to specified event for all components in
	 * this page.
	 *
	 * <p>Due to performance consideration, unlike {@link Component#addEventListener},
	 * you CANNOT ask the client to send the event back as soon as it detects
	 * this event for this listener registered by this method.
	 * In other words, {@link EventListener#isAsap} is ignored by this method.
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
	 * @see #recreate
	 * @see Execution#createComponents(String,Component,Map)
	 */
	public void removeComponents();
	/** Recreates the current page by removing all components first
	 * (thru {@link #removeComponents}) and then invoke
	 * {@link Execution#createComponents(PageDefinition, Component, Map)} with null.
	 * @param params a map of parameters that is accessible by the arg variable
	 * in EL, or by {@link Execution#getArg}.
	 * Ignored if null.
	 * @return the first component being created.
	 * @see Execution#createComponents(String,Component,Map)
	 */
	public Component recreate(Map params);

	/** Invalidates this page to cause all components to redraw.
	 */
	public void invalidate();

	/** Returns the class of the specified name.
	 * It's a shortcut to {@link Namespace#getClass} (of {@link #getNamespace}.
	 * Before delegating to the thread class loader, it also looks for
	 * the classes defined in the interpretor.
	 *
	 * @exception ClassNotFoundException if not found.
	 */
	public Class getClass(String clsnm) throws ClassNotFoundException;

	/** Returns the namespace used to store variables and functions
	 * belonging to this page.
	 * <p>Used only internally.
	 * @see #interpret
	 */
	public Namespace getNamespace();
	/** Interpret a BeanShell script against the specified componet.
	 * <p>Used only internally.
	 * @param comp the componet. If null, the evaluation takes place
	 * at this page.
	 */
	public void interpret(Component comp, String script);
}
