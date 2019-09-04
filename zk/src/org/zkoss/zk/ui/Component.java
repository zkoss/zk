/* Component.java

	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:03:47     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.au.AuService;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.util.Template;

/**
 * An UI component.
 * 
 * <p>
 * There are two kind of life-cycles: one is page creations and the other is
 * asynchronous updates.
 * 
 * <h3>The Page Creation</h3>
 * <p>
 * The page creation occurs when a page is about to render at the first time.
 * The detailed phases can be found in the devloper's guide.
 * 
 * <h3>The Asynchronous Update</h3>
 * <p>
 * The asynchronous update occurs when users does something on the browser, such
 * as changing the content of input, clicking buttons and so on. Such behaviors
 * are packed as requests, queue in the browser, and then send to the server at
 * the proper time. The detailed phases can be found in the developer's guide.
 * 
 * <h3>No Synchronization Required</h3>
 * <p>
 * To simplify the development of components and applications, invocations of
 * methods of components and event listener are all serialized. In other words,
 * application and component developers need not worry synchronization and other
 * thread issues (unless you are developing background thread to handle long
 * operations).
 * 
 * <p>
 * It also implies a limitation that you cannot access components belonging to
 * other desktops when processing an event.
 * 
 * <h3>Develop Component</h3>
 * <p>
 * It is not recommended to implements this interface while developing custom
 * component. Instead is is recommended to extend from {@link AbstractComponent}
 * or its specialized base component classes. Refer to <a href=
 * "http://books.zkoss.org/wiki/ZK_Component_Development_Essentials/Creating_a_simple_ZK_Component"
 * >Creating a simple ZK Component</a>
 * 
 * @author tomyeh
 */
public interface Component extends Scope, java.io.Serializable, Cloneable {
	/** Returns the widget class (a.k.a., the widget type), or null
	 * if not available.
	 * The widget class is a JavaScript class, including the package name.
	 * For example, "zul.wnd.Window".
	 * <p>Default: the widget class is decided by the component definition
	 * ({@link ComponentDefinition}) and the mold ({@link #getMold}).
	 * <p>To override in Java, you could invoke {@link #setWidgetClass}.
	 * To override in ZUML, you could use the client namespace as follows.
	 * <pre><code>
	&lt;window xmlns:w="http://www.zkoss.org/2005/zk/client"
	w:use="foo.MyWindow"&gt;
	&lt;/window&gt;
	 *</code></pre>
	 * <p>Note: for Ajax devices, the widget class must be non-null.
	 * @since 5.0.0
	 * @see #setWidgetClass
	 */
	public String getWidgetClass();

	/** Sets the widget class (a.k.a., the widget type).
	 * The widget class is a JavaScript class, including the package name.
	 * For example, "zul.wnd.Window".
	 * @param clsnm the widget's class name at the client side.
	 * If null (or empty), the default one is used (see {@link #getWidgetClass}).
	 * @since 5.0.2
	 */
	public void setWidgetClass(String clsnm);

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
	 * <p>The ID space related methods include {@link #getFellow},
	 * {@link #getAttribute} and {@link #getAttributeOrFellow}.
	 */
	public IdSpace getSpaceOwner();

	/** Returns the ID.
	 *
	 * <p>Default: "" (an empty string; it means no ID at all).
	 *
	 * <p>If a component belongs to an ID space (see {@link IdSpace}),
	 * the ID must also be unique in the ID space it belongs.
	 * any its parent and ancestor implements {@link IdSpace}.
	 * If it is a root component (i.e., without any parent),
	 * its ID must be unique among root components of the same page.
	 *
	 * <p>A page itself is also an ID space, so you could retrieve
	 * components in a page by use of {@link Page#getFellow}, unless
	 * the component is a descendant of another component that implements
	 * {@link IdSpace}. In this case, you have to retrieve the parent
	 * first (by use of {@link Page#getFellow} and then use {@link #getFellow}
	 * against the owner of the ID space.
	 *
	 * <p>In zscript and EL, a component with explicit ID can be accessed
	 * directly by the ID.
	 *
	 * @see Path
	 */
	public String getId();

	/** Sets the ID. The scope of uniqueness depends on whether this component
	 * is a root component. Refer to {@link #getId} for more details.
	 *
	 * <p>Default: "" (an empty string; it means no ID at all).
	 *
	 * @param id the identifier.
	 * You could specify null or an empty string to remove ID.
	 * @see org.zkoss.zk.ui.select.Selectors
	 */
	public void setId(String id);

	/** Returns the desktop of this component,
	 * or null if this component doesn't belong to any desktop.
	 *
	 * <p>When a component is created in an event listener, it
	 * is assigned to the current desktop automatically.
	 * If a component is created not in any event listener, it doesn't
	 * belong to any desktop and this method returns null.
	 * Once a component is attached to a desktop (by use of {@link #setPage}
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
	 * <p>When a component is created (a.k.a., constructed), it doesn't
	 * belong to any page. And, if a component doesn't belong to
	 * any page, they won't be displayed at the client.
	 *
	 * <p>When changing parent ({@link #setParent}), the child component's
	 * page will become the same as parent's. In other words, a component
	 * is added to a page automatically if it becomes a child of
	 * another component (who belongs to a page).
	 * 
	 * <p>For root components, you have to invoke {@link #setPage}
	 * Explicitly.
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
	 * <p>If you would like to monitor if a component is attached or detached
	 * from a page, you could register a desktop listener implementing
	 * {@link org.zkoss.zk.ui.util.UiLifeCycle}.
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

	/** Returns UUID (universal unique ID) which is unique in the whole
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
	 * Components in the same ID space assigned with ID are called fellows.
	 *
	 * <p>Unlike {@link #getFellowIfAny}, it throws an exception if not found.
	 *
	 * @exception ComponentNotFoundException is thrown if fellow not found
	 */
	public Component getFellow(String id);

	/** Returns a component of the specified ID in the same ID space.
	 * It is the same as getSpaceOwner().getFellow(id, recurse);
	 *
	 * <p>Unlike {@link #getFellowIfAny(String, boolean)}, it throws {@link ComponentNotFoundException}
	 * if not found.
	 *
	 * @exception ComponentNotFoundException is thrown if
	 * this component doesn't belong to any ID space
	 * @param recurse whether to look up the parent ID space for the
	 * existence of the fellow
	 * @since 5.0.0
	 */
	public Component getFellow(String id, boolean recurse) throws ComponentNotFoundException;

	/** Returns a component of the specified ID in the same ID space, or null
	 * if not found.
	 * <p>Unlike {@link #getFellow}, it returns null if not found.
	 */
	public Component getFellowIfAny(String id);

	/** Returns a component of the specified ID in the same ID space, or null
	 * if not found.
	 * It is the same as getSpaceOwner().getFellowIfAny(id, recurse);
	 *
	 * <p>Unlike {@link #getFellow(String, boolean)}, it returns null
	 * if not found.
	 *
	 * @param recurse whether to look up the parent ID space for the
	 * existence of the fellow
	 * @since 5.0.0
	 */
	public Component getFellowIfAny(String id, boolean recurse);

	/** Returns all fellows in the same ID space of this component.
	 * Notice that only components that are assigned with ID are considered
	 * as fellows.
	 * The returned collection is read-only.
	 * @since 3.0.6
	 */
	public Collection<Component> getFellows();

	/** Returns whether there is a fellow named with the specified component ID
	 * in the same ID space as this component.
	 * It is the same as getSpaceOwner().hasFellow(id, recurse);
	 *
	 * @param recurse whether to look up the parent ID space for the
	 * existence of the fellow
	 * @since 5.0.0
	 */
	public boolean hasFellow(String id, boolean recurse);

	/** Returns whether a fellow exists in the same ID space of this component.
	 * @since 5.0.0
	 */
	public boolean hasFellow(String compId);

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

	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes private to a component is searched.
	 * <p>It is also known as the component attributes.
	 * <p>It is the same as {@link Component#getAttributes}.
	 */
	public static final int COMPONENT_SCOPE = 0;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the same ID space.
	 * <p>It is also known as the ID space attributes.
	 */
	public static final int SPACE_SCOPE = 1;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the same page.
	 * <p>It is also known as the page attributes.
	 * <p>It is the same as {@link Page#getAttributes}.
	 */
	public static final int PAGE_SCOPE = 2;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the same desktop.
	 * <p>It is also known as the desktop attributes.
	 * <p>It is the same as {@link Desktop#getAttributes}.
	 */
	public static final int DESKTOP_SCOPE = 3;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the same session.
	 * <p>It is also known as the session attributes.
	 * <p>It is the same as {@link Session#getAttributes}.
	 */
	public static final int SESSION_SCOPE = 4;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the whole application.
	 * <p>It is also known as the application attributes.
	 * <p>It is the same as {@link WebApp#getAttributes}.
	 */
	public static final int APPLICATION_SCOPE = 5;
	/** Used with {@link #getAttribute(String, int)} and related to denote
	 * custom attributes shared by the same request.
	 * <p>It is also known as the request attributes, or execution attributes.
	 * <p>It is the same as {@link Execution#getAttributes}.
	 */
	public static final int REQUEST_SCOPE = 6;

	/** Returns all custom attributes of the specified scope.
	 * You could reference them by use of componentScope, spaceScope, pageScope,
	 * requestScope and desktopScope in zscript and EL.
	 *
	 * <p>If scope is {@link #COMPONENT_SCOPE}, it means custom attributes private
	 * to this component.
	 * <p>If scope is {@link #SPACE_SCOPE}, it means custom attributes shared
	 * by components from the same ID space as this one's.
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktops this one's.
	 *
	 * @param scope one of {@link #COMPONENT_SCOPE}, {@link #SPACE_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #REQUEST_SCOPE} or {@link #APPLICATION_SCOPE}, 
	 */
	public Map<String, Object> getAttributes(int scope);

	/** Returns all custom attributes associated with this component, i.e.,
	 * {@link #COMPONENT_SCOPE}.
	 */
	public Map<String, Object> getAttributes();

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
	 * by components from the same desktop as this one's.
	 *
	 * @param scope one of {@link #COMPONENT_SCOPE}, {@link #SPACE_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #REQUEST_SCOPE} or {@link #APPLICATION_SCOPE}, 
	 */
	public Object getAttribute(String name, int scope);

	/** Returns the custom attribute associated with this component, i.e.,
	 * {@link #COMPONENT_SCOPE}.
	 */
	public Object getAttribute(String name);

	/** Returns if the custom attribute is associate with this component.
	 * <p>If scope is {@link #COMPONENT_SCOPE}, it means attributes private
	 * to this component.
	 * <p>If scope is {@link #SPACE_SCOPE}, it means custom attributes shared
	 * by components from the same ID space as this one's.
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktop as this one's.
	 *
	 * <p>Notice that <code>null</code> is a valid value, so you can
	 * tell if an attribute is associated by examining the return value
	 * of {@link #getAttribute}.
	 * @param scope one of {@link #COMPONENT_SCOPE}, {@link #SPACE_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #REQUEST_SCOPE} or {@link #APPLICATION_SCOPE}, 
	 * @since 5.0.0
	 */
	public boolean hasAttribute(String name, int scope);

	/** Returns if the custom attribute is associate with this component.
	 * <p>Notice that <code>null</code> is a valid value, so you can
	 * tell if an attribute is associated by examining the return value
	 * of {@link #getAttribute}.
	 * @since 5.0.0
	 */
	public boolean hasAttribute(String name);

	/** Sets the value of the specified custom attribute in the specified scope.
	 *
	 * <p>Note: The attribute is removed (by {@link #removeAttribute}
	 * if value is null.
	 *
	 * <p>If scope is {@link #COMPONENT_SCOPE}, it means custom attributes private
	 * to this component.
	 * <p>If scope is {@link #SPACE_SCOPE}, it means custom attributes shared
	 * by components from the same ID space as this one's.
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktop as this one's.
	 *
	 * @param scope one of {@link #COMPONENT_SCOPE}, {@link #SPACE_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #REQUEST_SCOPE} or {@link #APPLICATION_SCOPE}, 
	 * @param value the value. If null, the attribute is removed.
	 */
	public Object setAttribute(String name, Object value, int scope);

	/** Sets the custom attribute associated with this component, i.e.,
	 * {@link #COMPONENT_SCOPE}.
	 */
	public Object setAttribute(String name, Object value);

	/** Removes the specified custom attribute in the specified scope.
	 * <p>If scope is {@link #COMPONENT_SCOPE}, it means attributes private
	 * to this component.
	 * <p>If scope is {@link #SPACE_SCOPE}, it means custom attributes shared
	 * by components from the same ID space as this one's.
	 * <p>If scope is {@link #PAGE_SCOPE}, it means custom attributes shared
	 * by components from the same page as this one's.
	 * <p>If scope is {@link #DESKTOP_SCOPE}, it means custom attributes shared
	 * by components from the same desktop as this one's.
	 *
	 * @param scope {@link #COMPONENT_SCOPE}, {@link #SPACE_SCOPE},
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE},
	 * {@link #REQUEST_SCOPE} or {@link #APPLICATION_SCOPE}, 
	 */
	public Object removeAttribute(String name, int scope);

	/** Removes the custom attribute associated with this component, i.e.,
	 * {@link #COMPONENT_SCOPE}.
	 */
	public Object removeAttribute(String name);

	/** Returns the custom attribute associated with this component,
	 * or the fellow of this component; or null if not found.
	 *
	 * <p>Notice that it doesn't check any variable defined in
	 * {@link org.zkoss.xel.VariableResolver}
	 * (of {@link Page#addVariableResolver}).
	 *
	 * @param recurse whether to look up the parent component for the
	 * existence of the attribute.<br/>
	 * Notice that, if recurse is false and this component is not an ID
	 * space owner, it won't look at the fellow.<br/>
	 * If recurse is true, it will look up all parents, page, desktop,
	 * session and application until found. If any of them is a space owner,
	 * the fellows will be searched.
	 * @since 5.0.0
	 */
	public Object getAttributeOrFellow(String name, boolean recurse);

	/** Returns the shadow variable associated with this component or its parent
	 * component; or null if not found.
	 *
	 * <p>Notice that it doesn't check any variable defined in
	 * {@link org.zkoss.xel.VariableResolver}
	 * (of {@link Page#addVariableResolver}).
	 *
	 * @param recurse whether to look up the parent component for the
	 * existence of the shadow variable.<br/>
	 * If recurse is true, it will look up all parents until found.
	 * If any of them is a shadow host.
	 * @since 8.0.0
	 */
	public Object getShadowVariable(String name, boolean recurse);

	/** Returns the shadow variable enclosed with the base component, which
	 * associated with this component or its parent component; or null if not found.
	 *
	 * <p>Notice that it doesn't check any variable defined in
	 * {@link org.zkoss.xel.VariableResolver}
	 * (of {@link Page#addVariableResolver}).
	 *
	 * @param baseChild the base component to seek the variable.
	 * @param recurse whether to look up the parent component for the
	 * existence of the shadow variable.<br/>
	 * If recurse is true, it will look up all parents until found.
	 * If any of them is a shadow host.
	 * @see Component#getShadowVariable(String, boolean)
	 * @since 8.0.1
	 */
	public Object getShadowVariable(Component baseChild, String name, boolean recurse);

	/** Returns if a custom attribute is associated with this component,
	 * or the fellow of this component.
	 *
	 * <p>Notice that it doesn't check any variable defined in
	 * {@link org.zkoss.xel.VariableResolver}
	 * (of {@link Page#addVariableResolver}).
	 *
	 * @param recurse whether to look up the parent component for the
	 * existence of the attribute.<br/>
	 * Notice that, if recurse is false and this component is not an ID
	 * space owner, it won't look at the fellow.<br/>
	 * If recurse is true, it will look up all parents, page, desktop,
	 * session and application until found. If any of them is a space owner,
	 * the fellows will be searched.
	 * @since 5.0.0
	 */
	public boolean hasAttributeOrFellow(String name, boolean recurse);

	/** Returns whether this component is stub-only.
	 * By stub-only, we mean we don't need to maintain the states of
	 * the component at the server side.
	 * <p>There are three possible values: "true", "false", and "inherit",
	 * and "ignore-native".
	 * <p>Notice that the native components will be stub-ized, no matter
	 * this property is set. Though rarely required, you could control
	 * whether to stub-ize the native components with
	 * a component attribute called {@link org.zkoss.zk.ui.sys.Attributes#STUB_NATIVE}.
	 * @since 5.0.4
	 */
	public String getStubonly();

	/** Sets whether this component is stub-only.
	 * By stub-only, we mean we don't need to maintain the states of
	 * the component at the server side.
	 * <p>Default: "inherit" (i.e., the same as the parent's stub-only,
	 * and "false" is assumed if none of parents is specified with stub-only).
	 * <p>If a component is set to stub-only, the application running at
	 * the server shall not access it anymore after rendered to the client.
	 * The ZK loader will try to minimize the memory footprint by merging
	 * stub-only components and replacing with light-weight components.
	 * <p>However, the event listeners and handlers are preserved, so
	 * they will be invoked if the corresponding event is received.
	 * Since the original component is gone, the event is the more generic
	 * format: an instance of {@link org.zkoss.zk.ui.event.Event}
	 * (rather than MouseEvent or others).
	 * <p>If a component is stub-only, the application usually access it only
	 * at the client since all widgets are preserved at the client (so are events).
	 * <p>This method is available only for ZK EE.
	 * @param stubonly whether it is stub-only. The allowed values include
	 * "true", "false" and "inherit".
	 * @since 5.0.4
	 */
	public void setStubonly(String stubonly);

	/** Sets whether this component is stub-only.
	 * It is the same as <code>setStubonly(stubonly ? "true": "false")</code>.
	 * @since 6.0.0
	 */
	public void setStubonly(boolean stubonly);

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
	 * By live we mean the developer could add or remove a child by manipulating the returned list directly.
	 */
	public <T extends Component> List<T> getChildren();

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
	 * <p>Note: {@link #setParent} always calls back {@link #insertBefore}
	 * and/or {@link #removeChild},
	 * while {@link #insertBefore} and {@link #removeChild}
	 * always calls back {@link #setParent},
	 * if the parent is changed. Thus, you don't need to override 
	 * both {@link #insertBefore} and {@link #setParent}, if you want
	 * to customize the behavior.
	 *
	 * <p>If you would like to monitor if a component is attached or detached
	 * from a page, you could register a desktop listener implementing
	 * {@link org.zkoss.zk.ui.util.UiLifeCycle}.
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
	 * <p>If you would like to monitor if a component is attached or detached
	 * from a page, you could register a desktop listener implementing
	 * {@link org.zkoss.zk.ui.util.UiLifeCycle}.
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

	/** Returns the mold used to render this component.
	 * <p>Default: "default"
	 * <p>Since 5.0, the default can be overridden by specify a library property.
	 * For example, if the component's class name is org.zkoss.zul.Button,
	 * then you can override the default mold by specifying the property
	 * called "org.zkoss.zul.Button.mold" with the mold you want
	 * in zk.xml. For example,
	<pre><code>&lt;library-property>
	&lt;name>org.zkoss.zul.Button.mold&lt;/name>
	&lt;value>trendy&lt;/value>
	&lt;/library-property></code></pre>
	 * <p>Notice that it doesn't affect the deriving classes. If you want
	 * to change the deriving class's default mold, you have to specify
	 * them explicitly, too.
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
	/** Adds an event listener to specified event name for this component
	 * with the given priority.
	 *
	 * <p>You could register listener to all components in the same page
	 * by use of {@link Page#addEventListener}.
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
	 * @param priority the priority of the event. The higher the priority is, the earlier it
	 * is invoked.<br/>
	 * <b>Notice:</b>
	 * <ul>
	 * <li>If the priority equals or is greater than 1000, the event listener will
	 * be invoked before the event handlers registered in a ZUML page
	 * (i.e., the onXxx attribute declared in ZUML).
	 * On the other hand, if the priority is lower than 1000, it is invoked
	 * after the ZUML's event handler.</li>
	 * <li>The priority registered by {@link #addEventListener(String, EventListener)} is 0.</li>
	 * <li>Applications shall not use the priority higher than 10,000 and
	 * lower than -10,000 since they are reserved for component development.</li>
	 * </ul>
	 * @param evtnm what event to listen (never null)
	 * @return whether the listener is added successfully
	 * @see Page#addEventListener
	 * @since 6.0.0
	 */
	public boolean addEventListener(int priority, String evtnm, EventListener<? extends Event> listener);

	/** Adds an event listener to specified event name for this component.
	 * The second registration is ignored and false is returned.
	 * The priority is assumed to 0.
	 *
	 * <p>You could register listener to all components in the same page
	 * by use of {@link Page#addEventListener}.
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
	 * @see Page#addEventListener
	 */
	public boolean addEventListener(String evtnm, EventListener<? extends Event> listener);

	/** Removes an event listener.
	 * @return whether the listener is removed; false if it was never added.
	 */
	public boolean removeEventListener(String evtnm, EventListener<? extends Event> listener);

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

	/** @deprecated As of release 6.0, replaced with {@link #getEventListeners}.
	 * Returns an iterator for iterating the event listeners for the given event.
	 */
	public Iterator<EventListener<? extends Event>> getListenerIterator(String evtnm);

	/** Returns an iterable collection of the event listeners for the given event.
	 * <p>Note: it is OK to invoke {@link #addEventListener} or {@link #removeEventListener}
	 * when iterating through the event listeners with the returned collection.
	 * <p>To remove an event listener from the returned iterable collection,
	 * you could invoke {@link Iterable#iterator}'s {@link Iterator#remove}.
	 * @since 6.0.0
	 * @since 6.0.0
	 */
	public Iterable<EventListener<? extends Event>> getEventListeners(String evtnm);

	/** Adds a forward condition to forward the event received
	 * by this component to another component.
	 *
	 * <p>Default: no forward condition at all.
	 *
	 * <p>Once the condition is added, a event called <code>targetEvent</code>
	 * is posted to the <code>target</code> component,
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
	 * It returns false if the condition was always added before.
	 * @since 3.0.0
	 * @see #removeForward(String, Component, String)
	 * @see #addForward(String, Component, String, Object)
	 */
	public boolean addForward(String originalEvent, Component target, String targetEvent);

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
	 * It returns false if the condition was always added before.
	 * @see #addForward(String, Component, String)
	 * @see #removeForward(String, String, String)
	 * @since 3.0.0
	 */
	public boolean addForward(String originalEvent, String targetPath, String targetEvent);

	/** Adds a forward condition to forward the event received
	 * by this component to another component with extra event data.
	 *
	 * @param eventData the extra data that can be retrieve by
	 * {@link org.zkoss.zk.ui.event.ForwardEvent#getData}.
	 * @see #addForward(String, Component, String)
	 * @since 3.0.6
	 */
	public boolean addForward(String originalEvent, Component target, String targetEvent, Object eventData);

	/** Adds a forward condition to forward the event received
	 * by this component to another component of the specified path
	 * with extra event data.
	 *
	 * @param eventData the extra data that can be retrieve by
	 * {@link org.zkoss.zk.ui.event.ForwardEvent#getData}.
	 * @see #addForward(String, String, String)
	 * @since 3.0.6
	 */
	public boolean addForward(String originalEvent, String targetPath, String targetEvent, Object eventData);

	/** Removes a forward condition that was added by
	 * {@link #addForward(String, Component, String)}.
	 * If no such forward condition exists, nothing happens but return false.
	 *
	 * @param originalEvent the original event that was received
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
	public boolean removeForward(String originalEvent, Component target, String targetEvent);

	/** Removes a forward condition that was added by
	 * {@link #addForward(String, String, String)}.
	 * If no such forward condition exists, nothing happens but return false.
	 *
	 * @param originalEvent the original event that was received
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
	public boolean removeForward(String originalEvent, String targetPath, String targetEvent);

	//-- drawing --//
	/** Returns if this component needs to be redrawn at the client.
	 * <p>Application developers rarely need to call this method.
	 * <p>Note:
	 * <ol>
	 * <li>It always returns true if it doesn't belong to any page
	 * (since redraw is required if it is attached to a page later).</li>
	 * <li>It always returns true if the current execution is not an
	 * asynchronous update (so redrawn is always required).</li>
	 * <li>If its parent is invalidated, this component will be redrawn
	 * too, but this method returns false if {@link #invalidate}
	 * was not called against this component.</li>
	 * </ol>
	 * @since 3.0.5
	 */
	public boolean isInvalidated();

	/** Invalidates this component by setting the dirty flag
	 * such that it will be redraw the whole content of this
	 * component and its dependencies later.
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

	/** Initializes the properties (a.k.a. members) 
	 * based on what are defined in the component definition.
	 *
	 * <p>This method is invoked automatically if a component is created
	 * by evaluating a ZUML page, i.e., if it is specified as an element
	 * of a ZUML page.
	 *
	 * <p>On the other hand, if it is created manually (by program),
	 * developer might choose to invoke this method or not,
	 * depending whether he wants to
	 * initializes the component with the properties
	 * defined in the ZUML page ({@link org.zkoss.zk.ui.metainfo.PageDefinition})
	 * and the language definition ({@link org.zkoss.zk.ui.metainfo.LanguageDefinition}).
	 *
	 * <p>Notice that, since 5.0.7, custom-attributes are applied automatically
	 * in the constructor of {@link AbstractComponent}, so they are always
	 * available no matter this method is called or not.
	 */
	public void applyProperties();

	/*** Sets or removes an event listener of the peer widget.
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

	/** Returns a read-only collection of event names (String) that
	 * the listener of the peer widget are assigned, or
	 * an empty collection if none is registered.
	 * @since 5.0.0
	 */
	public Set<String> getWidgetListenerNames();

	/*** Sets or removes a method or a property of the peer widget (at the client).
	 * If there is a method or a property associated with the same name,
	 * the previous one will be replaced and returned.
	 * <p>For example,
	 * <pre><code>comp.setWidgetOverride("setValue", "function (value) {}"); //override a method
	 *comp.setWidgetOverride("myfield", "new Date()"); //override a property
	 *</code></pre>
	 *
	 * <p>If there is no previous method or property, the method/property will
	 * be assigned directly.
	 *
	 * <p>Notice that, unlike {@link #setWidgetListener}, if the method has been sent
	 * to the client for update, it cannot be removed by calling this method
	 * with a null value.
	 * In other words, invoking this method with a null value only removes
	 * the method overrides if it has not YET been to sent to the client.
	 *
	 * <p>The previous method/property can be accessed by this.$xxx. For example
	 *<pre><code>function (value, fromServer) {
	 *  this.$setValue(value, fromServer);
	 *  if (this.desktop) {
	 *    this._cnt = !this._cnt;
	 *    this.setStyle('background:'+(this._cnt ? 'red':'green'));
	 *  }
	 *}</code></pre>
	 *
	 * <p>Notice that, since it is not extending, so this.$super references
	 * the superclass's method, rather than the <i>old</i> method.
	 *
	 * @param name the property name to override,
	 * such as <code>setValue</code> and <code>miles</code>.
	 * @param script the method definition, such as <code>function (arg) {...}</code>,
	 * or a value, such as <code>123</code> and <code>new Date()</code>.
	 * If not null, this method will be added to the peer widget.
	 * If there was a method with the same name, it will be renamed to
	 * <code>$<i>name</i></code> so can you call it back.
	 * <pre><code>&lt;label w:setValue="function (value) {
	 *  this.$setValue(value); //old method
	 *}"/&gt;</code></pre>
	 * If null, the previous method, if any, will be restored.
	 * @return the previous script if any
	 * @since 5.0.0
	 */
	public String setWidgetOverride(String name, String script);

	/** Returns the script of the method definition to override
	 * widget's method, or null if not found.
	 * @since 5.0.0
	 */
	public String getWidgetOverride(String name);

	/** Returns a read-only collection of the property names (String) that
	 * shall be overridden, or an empty collection if none is registered.
	 * @since 5.0.0
	 */
	public Set<String> getWidgetOverrideNames();

	/**
	 * @deprecated As released of ZK 8.0.0, please use {@link #setClientAttribute(String, String)}
	 * instead.
	 */
	public String setWidgetAttribute(String name, String value);

	/** 
	 * @deprecated As released of ZK 8.0.0, please use {@link #getClientAttribute(String)}
	 * instead.
	 */
	public String getWidgetAttribute(String name);

	/*** Sets or removes a DOM attribute of the peer widget (at the client).
	 * ZK pass the attributes directly to the DOM attribute generated
	 * at the client.
	 * <p>Notice that {@link #setWidgetOverride} or {@link #setWidgetListener}
	 * are used to customize the peer widget, while {@link #setWidgetAttribute}
	 * customizes the DOM element of the peer widget directly.
	 *
	 * <p>Unlike {@link #setWidgetOverride} or {@link #setWidgetListener},
	 * {@link #setWidgetAttribute} has no effect if the widget has been
	 * generated at the client, unless {@link #invalidate} is called.
	 *
	 * @param name the attribute name to generate to the DOM element,
	 * such as <code>onload</code>.
	 * Unlike {@link #setWidgetOverride}, the name might contain
	 * no alphanumeric characters, such as colon and dash.
	 * @param value the value of the attribute. It could be anything
	 * depending on the attribute.
	 * If null, the attribute will be removed. Make sure to specify an empty
	 * string if you want an attribute with an empty value.
	 * @return the previous value if any
	 * @since 8.0.0
	 */
	public String setClientAttribute(String name, String value);

	/** Returns the value of a DOM attribute
	 * @since 8.0.0
	 */
	public String getClientAttribute(String name);

	/*** Sets a DOM data attribute of the peer widget (at the client).
	 * If it has previous value, the component will invalidate.
	 * ZK pass the attributes directly to the DOM attribute generated
	 * at the client.
	 * <p>Notice that the parameter - name would be expanded by adding the prefix
	 * "data-" automatically.
	 * @param name the attribute name to generate to the DOM element,
	 * such as <code>mask</code>.
	 * @param value the value of the attribute. It could be anything
	 * depending on the attribute.
	 * If null, the attribute will be removed. Make sure to specify an empty
	 * string if you want an attribute with an empty value.
	 * @return the previous value if any
	 * @since 8.0.0
	 * @see #setClientDataAttribute(String, String)
	 */
	public String setClientDataAttribute(String name, String value);

	/** Returns the value of a DOM data attribute
	 * <p>Notice that the parameter - name would be expanded by adding the prefix
	 * "data-" automatically.
	 * @since 8.0.0
	 * @see #getClientDataAttribute(String)
	 */
	public String getClientDataAttribute(String name);

	/** Returns a read-only collection of additions DOM attributes that shall be
	 * generated. That is, they are the attributes added by {@link #setWidgetAttribute}.
	 * @since 5.0.3
	 */
	public Set<String> getWidgetAttributeNames();

	/** Returns the template of the given name, or null if not available.
	 * @since 6.0.0
	 * @see #setTemplate
	 */
	public Template getTemplate(String name);

	/** Sets a UI template which could be retrieved later
	 * with {@link #getTemplate}.
	 * @param name the template's name. It cannot be empty or null.
	 * @param template the template to assign. If it is null, the previous
	 * template, if any, will be removed
	 * @return the previous template, if any
	 */
	public Template setTemplate(String name, Template template);

	/** Returns a readonly set of the names of all templates.
	 * @since 6.0.0
	 */
	public Set<String> getTemplateNames();

	/** Sets an AU service to process the AU request before the component's
	 * default handling.
	 * This method is used if you want to send some custom request
	 * from client (by your application).
	 * <p>Default: null.
	 * <p>If you want to provide an AU service for the AU requests
	 * targeting the desktop. Use {@link Desktop#addListener}.
	 * <p>See also <a href="http://books.zkoss.org/wiki/ZK_Client-side_Reference/Communication/AU_Requests/Server-side_Processing">How to process AU requests with JSON</a>.
	 * @since 5.0.0
	 */
	public void setAuService(AuService service);

	/** Returns an AU service to process the AU request before the component's
	 * default handling.
	 * <p>Default: null
	 * @since 5.0.0
	 */
	public AuService getAuService();

	/** Returns the AU tag for this widget.
	 * The AU tag tag is used to tag the AU requests sent by the peer widget.
	 * For instance, if the AU tag is <code>xxx,yyy</code> and the desktop's
	 * request path ({@link Desktop#getRequestPath}) is <code>/foo.zul</code>, then
	 * the URL of the AU request will contain <code>/_/foo.zul/xxx,yyy</code>,.
	 * <p>Default: null (no AU tag for this widget).
	 * @since 6.0.0
	 * @see #setAutag
	 */
	public String getAutag();

	/** Sets the AU tag for this widget.
	 * The AU tag tag is used to tag the AU requests sent by the peer widget.
	 * @param tag the AU tag. Both an empty string and null are considered as null,
	 * i.e., no AU tag for this widget.
	 * @since 6.0.0
	 * @see #getAutag
	 */
	public void setAutag(String tag);

	/** Clones the component.
	 * All of its children and descendants are cloned.
	 * Also, ID are preserved.
	 * @return the new component. Notice that it doesn't belong to any page, nor
	 * desktop. It doesn't have a parent, either.
	 */
	public Object clone();

	/** Find the first component that matches the given CSS3 selector.
	 * @param selector the CSS3 selector. For example, comp.query("#id div").
	 * @return the first matched component, or null if not found
	 * @since 6.0.0
	 * @see #queryAll
	 */
	public Component query(String selector);

	/** Returns an iterable object for components that match the given CSS3 selector.
	 * <p>Notice: this method traverses the whole component tree, only if
	 * you iterate through the whole iterable object.
	 * In other words, the performance is good, and you can iterate it
	 * find the object that matches your criteria.
	 *
	 * @param selector the CSS3 selector. For example, comp.queryAll("#id div").
	 * @return a list of all matched component, or an empty list if none is found.
	 * @since 6.0.0
	 * @see #query
	 */
	public Iterable<Component> queryAll(String selector);
}
