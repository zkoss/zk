/* Component.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:03:47     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;

import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.scripting.Namespace;

/**
 * An UI component.
 *
 * <p>There are two kind of lifecycles: one is page creations and the other
 * is asynchronous updates.
 *
 * <h3>The Page Creation</h3>
 * <p>The page creation occurs when a page is about to render at the first
 * time. The detailed phases can be found in the devloper's guide.
 *
 * <h3>The Asynchronous Update</h3>
 * <p>The asynchronous update occurs when users does something on the browser,
 * such as changing the content of input, clicking buttons and so on.
 * Such behaviors are packed as requests, queue in the browser, and then
 * send to the server at the proper time. The detailed phases
 * can be found in the developer's guide.
 *
 * <h3>No Synchronization Required</h3>
 * <p>To simplify the development of components and applications,
 * invocations of methods of components and event listener are all serialized.
 * In other words, application and component developers need not worry
 * synchronization and other thread issues (unless you are developing
 * background thread to handle long operations).
 *
 * <p>It also implies a limitation that you cannot access components
 * belonging to other desktops when processing an event.
 *
 * @author tomyeh
 */
public interface Component extends java.io.Serializable, Cloneable {
	/** Returns the widget class (aka., the widget type), or null
	 * if not available.
	 * The widget class is a JavaScript class, including the package name.
	 * For example, "zul.wnd.Window".
	 * <p>Note: for Ajax devices, the widget class must be non-null.
	 * @since 5.0.0
	 */
	public String getWidgetClass();

	/** Returns the component definition of this component (never null).
	 */
	public ComponentDefinition getDefinition();

	/** Returns the owner of the ID space that this component belongs to.
	 * The returned value could be a component, a page or null.
	 * If this component itself implements {@link IdSpace}, this method
	 * returns itself.
	 * If it has an ancestor that implements {@link IdSpace},
	 * the ancestor is returned.
	 * Otherwise, the page it belongs to is returned
	 *
	 * <p>Each ID space defines an independent set of IDs. No component
	 * in the same ID space could have the same ID.
	 * To get any component in the same ID space, you could use
	 * {@link #getFellow}.
	 * See {@link IdSpace} for more details.
	 *
	 * <p>The ID space relevant methods include {@link #getFellow},
	 * {@link #getAttribute} and {@link #getVariable}.
	 *
	 * @see #getNamespace
	 */
	public IdSpace getSpaceOwner();

	/** Returns the ID. If it is a root component (i.e., without parent),
	 * its ID must be unquie among root components of the same page.
	 *
	 * <p>If a component belongs to an ID space (see {@link IdSpace}),
	 * the ID must also be unique in the ID space it belongs.
	 * any its parent and ancestor implements {@link IdSpace}.
	 *
	 * <p>A page itself is also an ID space, so you could retrieve
	 * compnents in a page by use of {@link Page#getFellow}, unless
	 * the component is a descendant of another component that implements
	 * {@link IdSpace}. In this case, you have to retrieve the parent
	 * first (by use of {@link Page#getFellow} and then use {@link #getFellow}
	 * against the owner of the ID space.
	 *
	 * <p>In zscript and EL, a component with explicit ID can be accessed
	 * directly by the ID. In other word, a variable named by the ID is
	 * created automatically.
	 *
	 * @see Page
	 */
	public String getId();
	/** Sets the ID. The scope of uniqunes depends on whether this component
	 * is a root component. Refer to {@link #getId} for more details.
	 *
	 * <p>When a component is constructed, an ID is generated automatically.
	 * Thus, calling this method only you need to identify a component.
	 *
	 * @param id the identifier. It cannot be empty.
	 * If null, it means the previous ID is removed (aka., reset)
	 * and an anonymous ID is assigned.
	 * @see Page
	 */
	public void setId(String id);

	/** Returns the desktop of this component,
	 * or null if this component doesn't belong to any desktop.
	 *
	 * <p>When a component is created in an event listener, it
	 * is assigned to the current desktop automatically.
	 * If a component is created not in any event listener, it doesn't
	 * belong to any desktop and this method returns null.
	 * Once a component is attached to a desktop (thru {@link #setPage}
	 * or {@link #setParent}), it belongs to the desktop.
	 *
	 * <p>Notice: there is no way to detach a component from a desktop,
	 * once it is attached as described above.
	 * In other words, you cannot move a component (or page) from
	 * one desktop to another.
	 *
	 * <p>In summary, there are only two ways to handle components.
	 * <ol>
	 * <li>Handle them all in event listeners and don't access any components
	 * from other desktops. This is simplest and clearest.</li>
	 * <li>Creates components in another thread (other than event listener)
	 * and attach them to a page (and then desktop) upon an event is received.</li>
	 * </ol>
	 */
	public Desktop getDesktop();
	/** Returns the page that this component belongs to, or null if
	 * it doesn't belong to any page.
	 *
	 * <p>When a component is created (aka., constructed), it doesn't
	 * belong to any page. And, if a component doesn't belong to
	 * any page, they won't be displayed at the client.
	 *
	 * <p>When changing parent ({@link #setParent}), the child component's
	 * page will become the same as parent's. In other words, a component
	 * is added to a page automatically if it becomes a child of
	 * another component (who belongs to a page).
	 * 
	 * <p>For root components, you have to invoke {@link #setPage}
	 * explicityly.
	 *
	 * @see #setParent
	 * @see #setPage
	 */
	public Page getPage();
	/** Sets what page this component belongs to.
	 * If this component already belongs to the same page, nothing
	 * is changed.
	 *
	 * <p>For child components, the page they belong is maintained
	 * automatically. You need to invoke this method only for root 
	 * components.
	 *
	 * <p>Note: a component might be attached to a page due invocations
	 * other than this method. For example, a component is attached
	 * if its parent is attached.
	 * To know whether it is attached, override
	 * {@link org.zkoss.zk.ui.sys.ComponentCtrl#onPageAttached}
	 * rather than this method.
	 *
	 * @see org.zkoss.zk.ui.sys.ComponentCtrl#onPageAttached
	 * @see org.zkoss.zk.ui.sys.ComponentCtrl#onPageDetached
	 */
	public void setPage(Page page);
	/** Sets what page this component belongs to, and insert
	 * this component right before the reference component.
	 *
	 * <p>For child components, the page they belong is maintained
	 * automatically. You need to invoke this method only for root 
	 * components.
	 *
	 * <p>It is similar to {@link #setPage}, except this component
	 * will be placed before the reference component.
	 * If the reference component is null, this component is placed
	 * at the end of all root components.
	 *
	 * @param refRoot another root component used as a reference
	 * which this component will be placed before.
	 * If null, this component will be placed at the end of all
	 * root components (no matter whether it already belongs to the same page).
	 * @since 3.0.0
	 * @see #setPage
	 */
	public void setPageBefore(Page page, Component refRoot);

	/** Returns UUID (universal unique ID) which is unquie in the whole
	 * session. The UUID is generated automatically and immutable, unless
	 * {@link org.zkoss.zk.ui.ext.RawId} is also implemented.
	 *
	 * <p>It is mainly used for communication between client and server
	 * and you rarely need to access it.
	 *
	 * <p>If {@link org.zkoss.zk.ui.ext.RawId} is implemented as part of
	 * a component, UUID is the same as {@link #getId} if {@link #setId}
	 * is ever called. It is designed to migrate HTML pages to ZK, such
	 * that the element ID could remain the same.
	 */
	public String getUuid();

	/** Returns a component of the specified ID in the same ID space.
	 * Components in the same ID space are called fellows.
	 *
	 * <p>Unlike {@link #getFellowIfAny}, it throws an exception if not found.
	 *
	 * @exception ComponentNotFoundException is thrown if fellow not found
	 */
	public Component getFellow(String id);
	/** Returns a component of the specified ID in the same ID space, or null
	 * if not found.
	 * <p>Unlike {@link #getFellow}, it returns null if not found.
	 */
	public Component getFellowIfAny(String id);
	/** Returns all fellows in the same ID space of this component.
	 * The returned collection is readonly.
	 * @since 3.0.6
	 */
	public Collection getFellows();

	/** Returns the next sibling, or null if it is the last child.
	 * @since 3.0.0
	 */
	public Component getNextSibling();
	/** Returns the previous sibling, or null if it is the first child.
	 * @since 3.0.0
	 */
	public Component getPreviousSibling();
	/** Returns the first child component, or null if no child at all.
	 * @since 3.0.0
	 */
	public Component getFirstChild();
	/** Returns the last child  component, or null if no child at all.
	 * @since 3.0.0
	 */
	public Component getLastChild();

	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes private to a component is searched.
	 * <p>It is also known as the component attributes.
	 * <p>It is the same as {@link Component#getAttributes}.
	 */
	public static final int COMPONENT_SCOPE = 0;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same ID space.
	 * <p>It is also known as the ID space attributes.
	 */
	public static final int SPACE_SCOPE = 1;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same page.
	 * <p>It is also known as the page attributes.
	 * <p>It is the same as {@link Page#getAttributes}.
	 */
	public static final int PAGE_SCOPE = 2;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same desktop.
	 * <p>It is also known as the desktop attributes.
	 * <p>It is the same as {@link Desktop#getAttributes}.
	 */
	public static final int DESKTOP_SCOPE = 3;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same session.
	 * <p>It is also known as the session attributes.
	 * <p>It is the same as {@link Session#getAttributes}.
	 */
	public static final int SESSION_SCOPE = 4;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the whole application.
	 * <p>It is also known as the application attributes.
	 * <p>It is the same as {@link WebApp#getAttributes}.
	 */
	public static final int APPLICATION_SCOPE = 5;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same request.
	 * <p>It is also known as the request attributes.
	 * <p>It is the same as {@link Execution#getAttributes}.
	 */
	public static final int REQUEST_SCOPE = 6;

	/** Returns all custom attributes of the specified scope.
	 * You could reference them thru componentScope, spaceScope, pageScope,
	 * requestScope and desktopScope in zscript and EL.
	 *
	 * <p>If scope is {@link #COMPONENT_SCOPE}, it means custom attributes private
	 * to this component.
	 * <p>If scope is {@link #SPACE_SCOPE}, it means custom attributes shared
	 * by components from the same ID space as this one's.
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 *
	 * @param scope {@link #COMPONENT_SCOPE}, {@link #SPACE_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #REQUEST_SCOPE} or {@link #APPLICATION_SCOPE}, 
	 */
	public Map getAttributes(int scope);
	/** Returns the value of the specified custom attribute in the specified scope,
	 * or null if not defined.
	 *
	 * <p>If scope is {@link #COMPONENT_SCOPE}, it means attributes private
	 * to this component.
	 * <p>If scope is {@link #SPACE_SCOPE}, it means custom attributes shared
	 * by components from the same ID space as this one's.
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 *
	 * @param scope {@link #COMPONENT_SCOPE}, {@link #SPACE_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #REQUEST_SCOPE} or {@link #APPLICATION_SCOPE}, 
	 */
	public Object getAttribute(String name, int scope);
	/** Sets the value of the specified custom attribute in the specified scope.
	 *
	 * <p>Note: The attribute is removed (by {@link #removeAttribute}
	 * if value is null, while {@link #setVariable} considers null as a legal value.
	 *
	 * <p>If scope is {@link #COMPONENT_SCOPE}, it means custom attributes private
	 * to this component.
	 * <p>If scope is {@link #SPACE_SCOPE}, it means custom attributes shared
	 * by components from the same ID space as this one's.
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 *
	 * @param scope {@link #COMPONENT_SCOPE}, {@link #SPACE_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #REQUEST_SCOPE} or {@link #APPLICATION_SCOPE}, 
	 * @param value the value. If null, the attribute is removed.
	 */
	public Object setAttribute(String name, Object value, int scope);
	/** Removes the specified custom attribute in the specified scope.
	 * <p>If scope is {@link #COMPONENT_SCOPE}, it means attributes private
	 * to this component.
	 * <p>If scope is {@link #SPACE_SCOPE}, it means custom attributes shared
	 * by components from the same ID space as this one's.
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktopas this one's.
	 *
	 * @param scope {@link #COMPONENT_SCOPE}, {@link #SPACE_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #REQUEST_SCOPE} or {@link #APPLICATION_SCOPE}, 
	 */
	public Object removeAttribute(String name, int scope);

	/** Returns all custom attributes associated with this component, i.e.,
	 * {@link #COMPONENT_SCOPE}.
	 */
	public Map getAttributes();
	/** Returns the custom attribute associated with this component, i.e.,
	 * {@link #COMPONENT_SCOPE}.
	 */
	public Object getAttribute(String name);
	/** Sets the custom attribute associated with this component, i.e.,
	 * {@link #COMPONENT_SCOPE}.
	 */
	public Object setAttribute(String name, Object value);
	/** Removes the custom attribute associated with this component, i.e.,
	 * {@link #COMPONENT_SCOPE}.
	 */
	public Object removeAttribute(String name);

	/** Sets a variable to the namespace.
	 *
	 * <p>This method is the same as
	 * getNamespace().setVariable(name, value, local).
	 *
	 * <p>Once a variable is set thru this method, it is visible to
	 * both the interpreter and EL.
	 *
	 * <p>Note: Exactly one namespace is allocated for each ID space.
	 * For example, if the space owner of this component is the page, then
	 * the returned namespace is the same as {@link Page#getNamespace}.
	 * Otherwise, it is the same as the namspace returned by the component
	 * owning this ID space.
	 *
	 * <h3>When to use setVariable and setAttribute?</h3>
	 *
	 * <p>First, only the ID space support {@link #setVariable} and so.
	 * Second, the variables can be referenced directly in zscript and EL
	 * expressions, while attributes are referenced thru the scope,
	 * such as spaceScope.
	 * On the other hand, using attributes causes less name popultion.
	 * In general, if you could use attributes, don't use variable.
	 *
	 * @param local whether not to search any of the ancestor namespace defines
	 * the variable. If local is false and an ancesotor has defined a variable
	 * with the same name, the variable in the ancestor is changed directly.
	 * Otherwise, a new variable is created in the namespace containing
	 * this component.
	 * @see #getSpaceOwner
	 * @see #getNamespace
	 */
	public void setVariable(String name, Object val, boolean local);
	/** Returns whether the specified variable is defined.
	 *
	 * <p>Note: null is a valid value for variable, so this method is used
	 * to know whether a variable is defined.
	 * On the other hand, {@link #setAttribute} actually remove
	 * an attribute (by {@link #removeAttribute} if value is null.
	 *
	 * @param local whether not to search its ancestor.
	 * If false and the current namespace doen't define the variable,
	 * it searches up its ancestor (via {@link #getParent}) to see
	 * any of them has defined the specified variable.
	 */
	public boolean containsVariable(String name, boolean local);
	/** Returns the value of a variable defined in the namespace,
	 * or null if not defined or the value is null.
	 *
	 * <p>This method is the same as getNamespace().getVariable(name, local).
	 *
	 * <h3>Differences between {@link #getVariable} and {@link Page#getZScriptVariable}</h3>
	 *
	 * <p>{@link #getVariable} returns only variables defined by
	 * {@link #setVariable} (i.e., a shortcut of {@link Namespace#setVariable}).
	 * On the other hand, {@link Page#getZScriptVariable} returns these variables
	 * and those defined when executing zscripts.
	 *
	 * @param local whether not to search its ancestor.
	 * If false and the current namespace doen't define the variable,
	 * it searches up its ancestor (via {@link #getParent}) to see
	 * any of them has defined the specified variable.
	 * @see #getSpaceOwner
	 * @see #getNamespace
	 */
	public Object getVariable(String name, boolean local);
	/** Unsets a variable defined in the namespace.
	 *
	 * <p>This method is the same as  getNamespace().getVariable(name, local).
	 *
	 * @param local whether not to search its ancestor.
	 * If false and the current namespace doen't define the variable,
	 * it searches up its ancestor (via {@link #getParent}) to see
	 * any of them has defined the specified variable.
	 * @see #getSpaceOwner
	 * @see #getNamespace
	 */
	public void unsetVariable(String name, boolean local);

	/** Returns the parent component, or null if this is the root component.
	 */
	public Component getParent();
	/** Sets the parent component.
	 *
	 * <p>Note: {@link #setParent} always calls back {@link #insertBefore}
	 * and/or {@link #removeChild},
	 * while {@link #insertBefore} and {@link #removeChild}
	 * always calls back {@link #setParent},
	 * if the parent is changed. Thus, you don't need to override 
	 * both {@link #insertBefore} and {@link #setParent}, if you want
	 * to customize the behavior.
	 */
	public void setParent(Component parent);
	/** Returns a live list of children.
	 * You could add or remove a child by manipulating the returned list directly.
	 */
	public List getChildren();
	/** Returns the root of this component.
	 */
	public Component getRoot();

	/** Returns whether this component is visible.
	 * @see Components#isRealVisible
	 */
	public boolean isVisible();
	/** Sets whether this component is visible.
	 *
	 * @return the previous visibility
	 */
	public boolean setVisible(boolean visible);

	/** Inserts a child before the reference child.
	 *
	 * <p>You could use {@link #setParent} or {@link #appendChild}
	 * instead of this method, unless
	 * you want to control where to put the child.
	 *
	 *
	 * <p>Note: {@link #setParent} always calls back {@link #insertBefore}
	 * and/or {@link #removeChild},
	 * while {@link #insertBefore} and {@link #removeChild}
	 * always calls back {@link #setParent},
	 * if the parent is changed. Thus, you don't need to override 
	 * both {@link #insertBefore} and {@link #setParent}, if you want
	 * to customize the behavior.
	 *
	 * @param newChild the new child to be inserted.
	 * @param refChild the child before which you want the new child
	 * being inserted. If null, the new child is append to the end.
	 * @return true if newChild is added successfully or moved;
	 * false if it already has the specified child and the order doesn't
	 * change.
	 */
	public boolean insertBefore(Component newChild, Component refChild);
	/** Appends a child.
	 * A shortcut to insertBefore(child, null).
	 *
	 * @see #insertBefore
	 */
	public boolean appendChild(Component child);

	/** Removes a child. The child is not actually removed.
	 * Rather, it is detached (see {@link #detach}) and it will be removed
	 * if it is no longer used.
	 *
	 * <p>You could use {@link #setParent} with null instead of this method.
	 *
	 * <p>Note: {@link #setParent} always calls back {@link #insertBefore}
	 * and/or {@link #removeChild},
	 * while {@link #insertBefore} and {@link #removeChild}
	 * always calls back {@link #setParent},
	 * if the parent is changed. Thus, you don't need to override 
	 * both {@link #insertBefore} and {@link #setParent}, if you want
	 * to customize the behavior.
	 *
	 * @return true if child is removed successfully; false if it doesn't
	 * have the specified child
	 */
	public boolean removeChild(Component child);

	/** Detaches this component such that it won't belong to any page.
	 * If you don't call {@link #setParent} or {@link #setPage} to
	 * attach it to any page, it will be removed automatically
	 * (from the client) after the current event is processed.
	 */
	public void detach();

	/** Returns the mold to render this component.
	 * <p>Default: "default"
	 *
	 * @see org.zkoss.zk.ui.metainfo.ComponentDefinition
	 */
	public String getMold();
	/** Sets the mold to render this component.
	 *
	 * @param mold the mold. If null or empty, "default" is assumed.
	 * @see org.zkoss.zk.ui.metainfo.ComponentDefinition
	 */
	public void setMold(String mold);

	//-- event listener --//
	/** Adds an event listener to specified event for this component.
	 * The second registration is ignored and false is returned.
	 *
	 * <p>You could register listener to all components in the same page
	 * by use of {@link Page#addEventListener}.
	 *
	 * @param evtnm what event to listen (never null)
	 * @return whether the listener is added; false if it was added before
	 * @see Page#addEventListener
	 */
	public boolean addEventListener(String evtnm, EventListener listener);
	/** Removes an event listener.
	 * @return whether the listener is removed; false if it was never added.
	 */
	public boolean removeEventListener(String evtnm, EventListener listener);
	/** Returns whether the event listener is available.
	 *
	 * <p>Unlike {@link org.zkoss.zk.ui.event.Events#isListened},
	 * this method checks only the event listener registered by
	 * {@link #addEventListener}.
	 *
	 * @param asap whether to check only non-deferrable listener,
	 * i.e., not implementing {@link org.zkoss.zk.ui.event.Deferrable},
	 * or {@link org.zkoss.zk.ui.event.Deferrable#isDeferrable} is false.
	 * @see org.zkoss.zk.ui.event.Deferrable
	 * @see org.zkoss.zk.ui.event.Events#isListened
	 * @see Component#addEventListener
	 */
	public boolean isListenerAvailable(String evtnm, boolean asap);
	/** Returns an iterator for iterating listener for the specified event.
	 */
	public Iterator getListenerIterator(String evtnm);

	/** Adds a forward condition to forward the event received
	 * by this component to another component.
	 *
	 * <p>Default: no forward condition at all.
	 *
	 * <p>Once the condition is added, a event called <code>targetEvent</code>
	 * is posted to the <code>target</code> compoennt,
	 * when this component receives the <code>orginalEvent</code> event.
	 *
	 * @param originalEvent the original event that was received
	 * by this component. If null, "onClick" is assumed.
	 * @param target the target component to receive the event.
	 * If null, the space owner {@link #getSpaceOwner} is assumed.
	 * If null and the space owner is the page, the root component is assumed.
	 * @param targetEvent the target event that the target component
	 * will receive.
	 * If null, it is the same as the original event.
	 * @return whether it is added successfully.
	 * It returns false if the conditioin was always added before.
	 * @since 3.0.0
	 * @see #removeForward(String, Component, String)
	 * @see #addForward(String, Component, String, Object)
	 */
	public boolean addForward(
	String originalEvent, Component target, String targetEvent);
	/** Adds a forward condition to forward the event received
	 * by this component to another component, specified with a path.
	 *
	 * <p>Note: the target component is retrieved from the path, each time
	 * the event is received. Thus, you can reference to a component
	 * that is created later.
	 *
	 * @param originalEvent the original event that was received
	 * by this component. If null, "onClick" is assumed.
	 * @param targetPath the target component's path related to this component.
	 * If ".", this component is assumed.
	 * If null, the space owner is assumed.
	 * If null and the space owner is the page, the root component is assumed.
	 * @param targetEvent the target event that the target component
	 * will receive.
	 * If null, it is the same as the original event.
	 * @return whether it is added successfully.
	 * It returns false if the conditioin was always added before.
	 * @see #addForward(String, Component, String)
	 * @see #removeForward(String, String, String)
	 * @since 3.0.0
	 */
	public boolean addForward(
	String originalEvent, String targetPath, String targetEvent);
	/** Adds a forward condition to forward the event received
	 * by this component to another component with extra event data.
	 *
	 * @param eventData the extra data that can be retrieve by
	 * {@link org.zkoss.zk.ui.event.ForwardEvent#getData}.
	 * @see #addForward(String, Component, String)
	 * @since 3.0.6
	 */
	public boolean addForward(String originalEvent, Component target,
	String targetEvent, Object eventData);
	/** Adds a forward condition to forward the event received
	 * by this component to another component of the specified path
	 * with extra event data.
	 *
	 * @param eventData the extra data that can be retrieve by
	 * {@link org.zkoss.zk.ui.event.ForwardEvent#getData}.
	 * @see #addForward(String, String, String)
	 * @since 3.0.6
	 */
	public boolean addForward(String originalEvent, String targetPath,
	String targetEvent, Object eventData);
	/** Removes a forward condition that was added by
	 * {@link #addForward(String, Component, String)}.
	 * If no such forward condition exists, nothing happens but return false.
	 *
	 * @param originalEvent the oringal event that was received
	 * by this component.
	 * It must be the same as the one passed to {@link #addForward(String, Component, String)}.
	 * @param target the target component to receive the event.
	 * It must be the same as the one passed to {@link #addForward(String, Component, String)}.
	 * @param targetEvent the target event that the target component will receive.
	 * It must be the same as the one passed to {@link #addForward(String, Component, String)}.
	 * @return whether the forward is removed successfully.
	 * It returns false if the forward condition is not found
	 * @see #addForward(String, Component, String)
	 * @since 3.0.0
	 */
	public boolean removeForward(
	String originalEvent, Component target, String targetEvent);
	/** Removes a forward condition that was added by
	 * {@link #addForward(String, String, String)}.
	 * If no such forward condition exists, nothing happens but return false.
	 *
	 * @param originalEvent the oringal event that was received
	 * by this component.
	 * It must be the same as the one passed to {@link #addForward(String, Component, String)}.
	 * @param targetPath the target component's path related to this component.
	 * If ".", this component is assumed.
	 * If null, the space owner is assumed.
	 * If null and the space owner is the page, the root component is assumed.
	 * @param targetEvent the target event that the target component will receive.
	 * It must be the same as the one passed to {@link #addForward(String, Component, String)}.
	 * @return whether the forward is removed successfully.
	 * It returns false if the forward condition is not found
	 * @see #addForward(String, String, String)
	 * @since 3.0.0
	 */
	public boolean removeForward(
	String originalEvent, String targetPath, String targetEvent);

	//-- drawing --//
	/** Returns if this component needs to be redrawn at the client.
	 * <p>Application developers rarely need to call this method.
	 * <p>Note:
	 * <ol>
	 * <li>It always returns true if it doesn't belong to any page
	 * (since redraw is required if it is attached to a page later).</li>
	 * <li>It always returns true if the current execution is not an
	 * asynchroous update (so redrawn is always required).</li>
	 * <li>If its parent is invalidated, this component will be redrawn
	 * too, but this method returns false if {@link #invalidate}
	 * was not called against this component.</li>
	 * </ol>
	 * @since 3.0.5
	 */
	public boolean isInvalidated();
	/** Invalidates this component by setting the dirty flag
	 * such that it will be redraw the whole content of this
	 * component and its decendances later.
	 * And, the widget associated with this component and all its
	 * descendant at the client will be deleted and recreated, too.
	 *
	 * <p>If the application is totally controlled by the server side
	 * (i.e., you don't write client codes), you rarely need to access
	 * this method.
	 *
	 * <p>It can be called only in the request-processing and event-processing
	 * phases. However, it is NOT allowed in the rendering phase.
	 */
	public void invalidate();

	/** Returns the namespace to store variables and functions belonging
	 * to the ID space of this component.
	 *
	 * <p>Exactly one namespace is allocated for each ID space.
	 * For example, if the space owner of this component is the page, then
	 * the returned namespace is the same as {@link Page#getNamespace}.
	 * Otherwise, it is the same as the namspace returned by the component
	 * owning this ID space.
	 *
	 * <p>Namspace is another part of an ID space. It holds only variables
	 * defined thru {@link #setVariable} (and {@link Namespace#setVariable}.
	 *
	 * <p>Note: The namespace doesn't include any variable defined by
	 * executing zscripts. To retrieve them, use {@link Page#getZScriptVariable}.
	 *
	 * @see #getSpaceOwner
	 */
	public Namespace getNamespace();

	/** Initializes the properties (aka. members) and custom-attributes
	 * based on what are defined in the component definition.
	 *
	 * <p>This method is invoked automatically if a component is created
	 * by evaluating a ZUML page, i.e., if it is specified as an elemnt
	 * of a ZUML page.
	 *
	 * <p>On the other hand, if it is created manually (by program),
	 * developer might choose to invoke this method or not,
	 * depending whether he wants to
	 * initializes the component with the properties and custom-attributes
	 * defined in the ZUML page ({@link org.zkoss.zk.ui.metainfo.PageDefinition})
	 * and the language definition ({@link org.zkoss.zk.ui.metainfo.LanguageDefinition}).
	 */
	public void applyProperties();

	/*** Sets or remove an event listener of the peer widget.
	 * If there is an event listener associated with the same event,
	 * the previous one will be replaced and returned.
	 *
	 * @param evtnm the event name, such as onClick
	 * @param script the script to handle the event. If null, the event
	 * handler is removed.
	 * @return the previous script if any
	 * @since 5.0.0
	 */
	public String setWidgetListener(String evtnm, String script);
	/** Returns the script of the client event, or null if not found.
	 * @since 5.0.0
	 */
	public String getWidgetListener(String evtnm);
	/** Returns a readonly collection of event names (String) that
	 * the listener of the peer widget are assigned, or
	 * an empty collection if none is registered.
	 * @since 5.0.0
	 */
	public Set getWidgetListenerNames();

	/** Clones the component.
	 * All of its children is cloned.
	 * Notice that the cloned component doesn't belong to any page, nor
	 * desktop. It doesn't have parent, either.
	 */
	public Object clone();
}
