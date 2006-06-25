/* Component.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:03:47     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui;

import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;

import com.potix.zk.ui.event.EventListener;
import com.potix.zk.ui.util.Namespace;
import com.potix.zk.au.AuResponse;

/**
 * A UI component.
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
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public interface Component extends java.io.Serializable, Cloneable {
	/** Returns the owner of the ID space that this component belongs to.
	 * It is either a component, a page or null.
	 * If it has an ancestor that implements {@link IdSpace}, it is returned.
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
	 * <p>In BSH and EL, a component with explicit ID can be accessed
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
	 *
	 * <p>For child components, the page they belong is maintained
	 * automatically. You need to invoke this method only for root 
	 * components.
	 */
	public void setPage(Page page);

	/** Returns UUID (universal unique ID) which is unquie in the whole
	 * desktop. The UUID is generated automatically and immutable, unless
	 * {@link com.potix.zk.ui.ext.RawId} is also implemented.
	 *
	 * <p>It is mainly used for communication between client and server
	 * and you rarely need to access it.
	 *
	 * <p>If {@link com.potix.zk.ui.ext.RawId} is implemented as part of
	 * a component, UUID is the same as {@link #getId} if {@link #setId}
	 * is ever called. It is designed to migrate HTML pages to ZK, such
	 * that the element ID could remain the same.
	 */
	public String getUuid();

	/** Returns a component of the specified ID in the same ID space.
	 * Components in the same ID space are called fellows.
	 *
	 * @exception ComponentNotFoundException is thrown if fellow not found
	 */
	public Component getFellow(String id);

	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes private to a component is searched.
	 */
	public static final int COMPONENT_SCOPE = 0;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same ID space.
	 */
	public static final int SPACE_SCOPE = 1;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same page.
	 */
	public static final int PAGE_SCOPE = 2;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same desktop.
	 */
	public static final int DESKTOP_SCOPE = 3;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the same session.
	 */
	public static final int SESSION_SCOPE = 4;
	/** Used with {@link #getAttribute} and relevants to denote
	 * custom attributes shared by the whole application.
	 */
	public static final int APPLICATION_SCOPE = 5;

	/** Returns all custom attributes of the specified scope.
	 * You could reference them thru componentScope, spaceScope, pageScope
	 * and desktopScope in BSH and EL.
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
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE}
	 * or {@link #APPLICATION_SCOPE}, 
	 */
	public Map getAttributes(int scope);
	/** Returns the value of the specified custom attribute in the specified scope.
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
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE}
	 * or {@link #APPLICATION_SCOPE}, 
	 */
	public Object getAttribute(String name, int scope);
	/** Sets the value of the specified custom attribute in the specified scope.
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
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE}
	 * or {@link #APPLICATION_SCOPE}, 
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
	 * {@link #PAGE_SCOPE}, {@link #DESKTOP_SCOPE}, {@link #SESSION_SCOPE}
	 * or {@link #APPLICATION_SCOPE}, 
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

	/** Sets a variable that both the interpreter and EL can see it.
	 * The variable is defined in the scope of the ID space containing
	 * this component ({@link #getSpaceOwner}).
	 * In other words, all components in the same ID space share the same
	 * set of variables (and functions).
	 *
	 * <p>The other scope to set a variable is {@link Page#setVariable}.
	 * If this component doesn't belong to any ID space, this method
	 * is the same as {@link Page#setVariable}.
	 *
	 * <p>Since variables (and functions) are associated with the ID space,
	 * the child ID space could see the variable in the parent
	 * ID space.
	 *
	 * <h3>When to use setVariable and setAttribute?</h3>
	 *
	 * First, setVariable supports only the ID space. Second, the variable is
	 * easy to reference in zsript and EL (such type the variable name directly),
	 * while attribute needs to specify the scope first, such as spaceScope.
	 * On the other hand, using attributes causes less name popultion.
	 * In general, if you could use attributes, don't use variable.
	 *
	 * @param local whether not to search any of the ancestor ID space defines
	 * the variable. If local is false and an ancesotor has defined a variable
	 * with the same name, the variable in the ancestor is changed directly.
	 * Otherwise, a new variable is created in the ID space containing
	 * this component.
	 */
	public void setVariable(String name, Object val, boolean local);
	/** Returns the value of a variable defined in the interpreter.
	 * The variable is defined in the ID space containing this component
	 *  ({@link #getSpaceOwner}).
	 * In other words, all components in the same ID space share the same
	 * set of variables.
	 *
	 * @param local whether not to search its ancestor.
	 * If false and the current ID space doen't define the variable,
	 * it searches up its ancestor (via {@link #getParent}) to see
	 * any of them has defined the specified variable.
	 */
	public Object getVariable(String name, boolean local);
	/** Unsets a variable from the current ID space.
	 * <p>Unlike {@link #setVariable}, this method removed only
	 * the variable defined in the ID space cotnaining this component.
	 */
	public void unsetVariable(String name);

	/** Returns the parent component, or null if this is the root component.
	 */
	public Component getParent();
	/** Sets the parent component.
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
	 * If this component is transaprent ({@link #isTransparent}), no
	* {@link #smartUpdate} is caleld, so transparent compoents usually have
	* to override this method for detail control.
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

	/** Called when a child is added.
	 * If a component want to optimize the update, it might do something
	 * different. Otherwise, it does nothing.
	 *
	 * <p>Note: {@link #onChildAdded} is called in the request-processing
	 * phase, while {@link #onDrawNewChild} is called in the redrawing phase.
	 * See {@link #onDrawNewChild} for more details.
	 */
	 public void onChildAdded(Component child);
	/** Called when a child is removed.
	 * If a component want to optimize the update, it might do something
	 * different. Otherwise, it simply does nothing.
	 */
	 public void onChildRemoved(Component child);

	/** Returns the mold for this component.
	 * <p>Default: "default"
	 *
	 * @see com.potix.zk.ui.metainfo.ComponentDefinition
	 */
	public String getMold();
	/** Sets the mold for this component.
	 *
	 * @param mold the mold. If null or empty, "default" is assumed.
	 * @see com.potix.zk.ui.metainfo.ComponentDefinition
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
	 * @param asap whether to check only ASAP listener.
	 * See {@link Component#addEventListener} for more description.
	 */
	public boolean isListenerAvailable(String evtnm, boolean asap);
	/** Returns an iterator for iterating listener for the specified event.
	 */
	public Iterator getListenerIterator(String evtnm);

	//-- drawing --//
	/** Invalidates this component by setting the dirty flag
	 * such that it will be redraw the whole content later.
	 *
	 * <p>It can be called only in the request-processing and event-processing
	 * phases; excluding the redrawing phase.
	 *
	 * <p>There are two ways to draw a component, one is to invoke
	 * {@link #invalidate(Component.Range)}, and the other is {@link #smartUpdate}.
	 * While {@link #invalidate(Component.Range)} causes the whole content to redraw,
	 * {@link #smartUpdate} let component developer control which part
	 * to redraw.
	 *
	 * <p>Once this method is called, all invocations to {@link #smartUpdate}
	 * will then be ignored, and {@link Component#redraw} will be invoked later.
	 *
	 * @param range {@link #INNER} or {@link #OUTER} (never null, nor other value).
	 * It controls whether only the inner elements or the whole component
	 * to redraw.
	 */
	public void invalidate(Component.Range range);
	/** Invalidate the whole component.
	 * A shortcut of invalidate(OUTER).
	 * @see #invalidate(Component.Range)
	 */
	public void invalidate();
	/** Smart-updates a property with the specified value.
	 * Called by component developers to do precise-update.
	 *
	 * <p>The second invocation with the same property will replace the previous
	 * call. In other words, the same property will be set only once in
	 * each execution.
	 *
	 * <p>This method has no effect if {@link #invalidate(Component.Range)} is ever invoked
	 * (during this execution).
	 *
	 * <p>It can be called only in the request-processing and event-processing
	 * phases; excluding the redrawing phase.
	 *
	 * <p>There are two ways to draw a component, one is to invoke
	 * {@link #invalidate(Component.Range)}, and the other is {@link #smartUpdate}.
	 * While {@link #invalidate(Component.Range)} causes the whole content to redraw,
	 * {@link #smartUpdate} let component developer control which part
	 * to redraw.
	 *
	 * @param value the new value. If null, it means removing the property.
	 */
	public void smartUpdate(String attr, String value);
	/** Causes a response (aka., a command) to be sent to the client.
	 *
	 * <p>If {@link AuResponse#getDepends} is not null, the response
	 * depends on the existence of the returned componet.
	 * In other words, the response is removed if the component is removed.
	 * If it is null, the response is component-independent and it is
	 * always sent to the client.
	 *
	 * <p>Unlike {@link #smartUpdate}, responses are sent to client if
	 * it is component independent or it is not removed.
	 * In other words, it is sent even if {@link #invalidate(Component.Range)} was called.
	 * Typical examples include setting the focus, selecting the text and so on.
	 *
	 * <p>It can be called only in the request-processing and event-processing
	 * phases; excluding the redrawing phase.
	 *
	 * @param key could be anything.
	 * The second invocation of this method
	 * in the same execution with the same key will override the previous one.
	 * However, if key is null, it won't override any other. All responses
	 * with key == null will be sent.
	 */
	public void response(String key, AuResponse response);

	/** AuRequest this component to render (aka., redraw) itself
	 * and its children.
	 *
	 * <p>It is called in the redrawing phase by the kernel, so it is too late
	 * to call {@link #invalidate(Component.Range)} or {@link #smartUpdate} in this method.
	 */
	public void redraw(Writer out) throws IOException;

	/** Called when a new-created child is drawn. It gives the parent
	 * a chance to fine-tune the output.
	 *
	 * <p>It is called in the redrawing phase by the kernel, so it is too late
	 * to call {@link #invalidate(Component.Range)} or {@link #smartUpdate} in this method.
	 *
	 * <p>Note: {@link #onChildAdded} is called in the request-processing
	 * phase, while {@link #onDrawNewChild} is called in the redrawing phase.
	 * Component developer might do one of the follows:
	 * <ul>
	 * <li>Nothing, if new child can be inserted directly.</li>
	 * <li>Overwrite {@link #onDrawNewChild} to add special tags, if
	 * new child needs to be added an exterior with some tags before
	 * insertion.<br>
	 * Morever, if you shall add id="${child.uuid}!chdextr" to the added
	 * exterior.</li>
	 * <li>Redraw the parent, if it is too complicated.
	 * How: overwrite {@link #onChildAdded} and calls {@link #invalidate(Component.Range)}</li>
	 * </ul>
	 *
	 * @param child the child being rendered
	 * @param out the rendered result of the child.
	 */
	public void onDrawNewChild(Component child, StringBuffer out)
	throws IOException;

	/** Returns whether this component allows to have any child.
	 */
	public boolean isChildable();
	/** Returns whether this component is transparent.
	 * By transparent we mean this component doesn't have any counterpart
	 * in the client.
	 * In other words, it doesn't generate any element in the client.
	 * In this case, invalidate this component implies invalidate all
	 * its children, and {@link #smartUpdate} is meaningless
	 * (and causes exception).
	 *
	 * <p>All its children are considered as children of its parent when
	 * inserting.
	 */
	public boolean isTransparent();

	/** Returns the class of the specified name.
	 * It's a shortcut to {@link Namespace#getClass} (of {@link #getNamespace}.
	 * Before delegating to the thread class loader, it also looks for
	 * the classes defined in the name space (part of the interpretor).
	 *
	 * <p>Note: a namespace per ID space.
	 *
	 * @exception ClassNotFoundException if not found.
	 */
	public Class getClass(String clsnm) throws ClassNotFoundException;
	/** Returns the namespace to store variables and functions belonging
	 * to the ID space of this component.
	 *
	 * <p>Note: a namespace per ID space.
	 */
	public Namespace getNamespace();

	/** Initializes the properties (aka. members) based on what are
	 * defined in the component definition.
	 *
	 * <p>This method is invoked automatically if a component is created
	 * by evaluating a ZUML page, i.e., if it is specified as an elemnt
	 * of a ZUML page.
	 *
	 * <p>On the other hand, if it is created manually (by program),
	 * developer might choose to invoke this method or not,
	 * depending whether he wants to
	 * initializes the component with the properties defined in
	 * {@link com.potix.zk.ui.metainfo.LanguageDefinition}.
	 */
	public void applyProperties();

	/** Clones the component.
	 * All of its children is cloned.
	 * Notice that the cloned component doesn't belong to any page, nor
	 * desktop. It doesn't have parent, either.
	 */
	public Object clone();

	/** Used with {@link #invalidate(Component.Range)} to denote the inner elements
	 * (excluding the enclosing tag).
	 */
	public static final Range INNER = new Range("INNER");
	/** Used with {@link #invalidate(Component.Range)} to denote the whole elemnt
	 * including the enclosing tag and the inner.
	 */
	public static final Range OUTER = new Range("OUTER");
	/** Used with {@link #invalidate(Component.Range)}.
	 * @see #INNER
	 * @see #OUTER
	 */
	public static class Range {
		private String _name;
		private Range(String name) {
			_name = name;
		}
		public String toString() {
			return _name;
		}
	}	
}
