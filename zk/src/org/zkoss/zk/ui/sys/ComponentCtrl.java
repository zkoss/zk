/* ComponentCtrl.java

	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:06:56     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.au.AuRequest;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.ShadowElement;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.ext.Scope;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.DefinitionNotFoundException;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.EventHandlerMap;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.util.Callback;

/**
 * An addition interface to {@link Component}
 * that is used for implementation or tools.
 *
 * <p>Application developers rarely need to access methods in this interface.
 *
 * @author tomyeh
 */
public interface ComponentCtrl {
	/**
	 * For page attached callback, use in {@link #addCallback(String, Callback)}
	 */
	public static String AFTER_PAGE_ATTACHED = "afterPageAttached";

	/**
	 * For page detached callback, use in {@link #addCallback(String, Callback)}
	 */
	public static String AFTER_PAGE_DETACHED = "afterPageDetached";

	/**
	 * For after child added callback, use in {@link #addCallback(String, Callback)}
	 */
	public static String AFTER_CHILD_ADDED = "afterChildAdded";

	/**
	 * For after child removed callback, use in {@link #addCallback(String, Callback)}
	 */
	public static String  AFTER_CHILD_REMOVED = "afterChildRemoved";

	/** Sets the component definition.
	 *
	 * <p>The component definition affects how a component behaves.
	 * Developers rarely need to call this method. If a wrong definition
	 * is assigned,
	 * the result is unpredictable (and hard to debug). It is mainly designed
	 * for developing tools.
	 *
	 * @exception IllegalArgumentException if compdef is null
	 */
	public void setDefinition(ComponentDefinition compdef);

	/** Sets the component definition by specifying the name.
	 *
	 * @param defname the name of the component definition
	 * @exception IllegalArgumentException if defname is null
	 * @exception DefinitionNotFoundException if the component definition not found
	 * @since 5.0.0
	 */
	public void setDefinition(String defname);

	/** Called before adding a child.
	 * If a component accepts only certain types of children, it shall
	 * override this method and throw an exception for an illegal child.
	 *
	 * @param child the child to be added (never null).
	 * @param insertBefore another child component that the new child
	 * will be inserted before it. If null, the new child will be the
	 * last child.
	 * @since 3.6.2
	 */
	public void beforeChildAdded(Component child, Component insertBefore);

	/** Called before removing a child.
	 * If a component denies a certain child to be removed, it shall
	 * override this method to avoid it.
	 *
	 * @param child the child to be removed (never null)
	 * @since 3.6.2
	 */
	public void beforeChildRemoved(Component child);

	/** Called before changing the parent.
	 * If a component can be a child of certain parents, it shall override
	 * this method and throws an exception for an illegal parent.
	 *
	 * @param parent the new parent. If null, it means detachment.
	 * @since 3.6.2
	 */
	public void beforeParentChanged(Component parent);

	/** Called when a child is added.
	 * If a component want to optimize the update, it might do something
	 * different. Otherwise, it does nothing.
	 *
	 * <p>Note: {@link #onChildAdded} is called in the request-processing
	 * phase.
	 *
	 * <p>It is not a good idea to throw an exception in this method, since
	 * it is in the middle of modifying the component tree.
	 * @since 3.5.0
	 */
	public void onChildAdded(Component child);

	/** Called when a child is removed.
	 * If a component want to optimize the update, it might do something
	 * different. Otherwise, it simply does nothing.
	 *
	 * <p>It is not a good idea to throw an exception in this method, since
	 * it is in the middle of modifying the component tree.
	 * @since 3.5.0
	 */
	public void onChildRemoved(Component child);

	/** Called when this component is attached to a page.
	 *
	 * <p>If a component is moved from one page to another,
	 * {@link #onPageAttached} is called with both pages.
	 * Note: {@link #onPageDetached} is not called in this case.
	 *
	 * <p>Note: this method is called even if the component is attached
	 * to a page implicitly thru, say, {@link Component#setParent}.
	 *
	 * <p>It is not a good idea to throw an exception in this method, since
	 * it is in the middle of modifying the component tree.
	 *
	 * @param newpage the new page (never null).
	 * @param oldpage the previous page, if any, or null if it didn't
	 * belong to any page.
	 * @since 3.5.0
	 */
	public void onPageAttached(Page newpage, Page oldpage);

	/** Called when this component is detached from a page.
	 *
	 * <p>If a component is moved from one page to another,
	 * {@link #onPageAttached} is called with both pages.
	 * Note: {@link #onPageDetached} is not called in this case.
	 * In other words, {@link #onPageDetached} is called only if a component
	 * is detached from a page (not belong to any other page).
	 *
	 * <p>Note: this method is called even if the component is detached
	 * to a page implicitly thru, say, {@link Component#setParent}.
	 *
	 * <p>It is not a good idea to throw an exception in this method, since
	 * it is in the middle of modifying the component tree.
	 *
	 * @param page the previous page (never null)
	 * @since 3.5.0
	 */
	public void onPageDetached(Page page);

	/** Returns the event listener of the specified name, or null
	 * if not found.
	 * @see Component#getWidgetListener
	 */
	public ZScript getEventHandler(String evtnm);

	/** Adds an event handler.
	 * Note: it is OK to add multiple event handlers to the same event.
	 * They are evaluated one-by-one.
	 * On the other hand, {@link Component#setWidgetListener} will
	 * overwrite the previous listener if the event name is the same.
	 * @see Component#setWidgetListener
	 */
	public void addEventHandler(String name, EventHandler evthd);

	/** Adds a map of event handlers which is shared by other components.
	 * In other words, this component shall have all event handlers
	 * defined in the specified map, evthds. Meanwhile, this component
	 * shall not modify evthds, since it is shared.
	 * The caller shall not change annots after the invocation, too
	 *
	 * @param evthds a map of event handler
	 * @see Component#setWidgetListener
	 */
	public void addSharedEventHandlerMap(EventHandlerMap evthds);

	/** Returns a readonly collection of event names (String), or
	 * an empty collection if no event name is registered.
	 *
	 * @see Component#getWidgetListenerNames
	 * @since 3.0.2
	 */
	public Set<String> getEventHandlerNames();

	/** Returned by {@link #getClientEvents} to indicate the event is important
	 * and the client must send it back even if no listener is registered.
	 */
	public static final int CE_IMPORTANT = 0x0001;
	/** Returned by {@link #getClientEvents} to indicate the event is 
	 * no deferrable, i.e., the event has to be sent back immediately.
	 * It is meaningful only used with {@link #CE_IMPORTANT}
	 */
	public static final int CE_NON_DEFERRABLE = 0x0002;
	/** Returned by {@link #getClientEvents} to indicate the event
	 * can be ignored by the server when the server is busy.
	 */
	public static final int CE_BUSY_IGNORE = 0x1000;
	/** Returned by {@link #getClientEvents} to indicate the event
	 * can be ignored by the server when the server receives the same AU
	 * requests but not processed yet.
	 * In other words, the server will remove the duplicate AU requests
	 * if it was queued. 
	 */
	public static final int CE_DUPLICATE_IGNORE = 0x2000;
	/** Returned by {@link #getClientEvents} to indicate the event
	 * an be ignored by the server when the server receives consecutive
	 * AU requests. In other words, the server will remove the first request
	 * if it receives an event listed in this map consecutively.
	 */
	public static final int CE_REPEAT_IGNORE = 0x4000;

	/** Returns a map of event information that the client might send to this component.
	 * The key of the returned map is a String instance representing the event name,
	 * and the value an integer representing the flags
	 * (a combination of {@link #CE_IMPORTANT}, {@link #CE_BUSY_IGNORE},
	 * {@link #CE_DUPLICATE_IGNORE} and {@link #CE_REPEAT_IGNORE}).
	 * <p>Default: return the collection of events
	 * added by {@link #getClientEvents}.
	 *
	 * @since 5.0.0
	 */
	public Map<String, Integer> getClientEvents();

	/** @deprecated As of release 6.0.0, replaced with
	 * {@link #getAnnotation(String, String)}.
	 */
	public Annotation getAnnotation(String annotName);

	/** Returns the annotation associated with the definition of the specified
	 * property, or null if not available.
	 *
	 * <p>If there are multiple annotation with the given name,
	 * this method will merge them before return. If you prefer to get all
	 * of them without merging, please use {@link #getAnnotations(String, String)} instead.
	 * For example,
	 * <pre><code>&lt;textbox value="@bind(abc, param1=foo1) @bind(xyz, param2=foo2)"&gt;</code></pre>
	 *
	 * <p>This method will return a single annotation with three attributes:
	 * value=xyz, param1=foo1 and param2=foo2. On the other hand,
	 * {@link #getAnnotations(String, String)} will return a two-element
	 * collections.
	 *
	 * <p>Notice that the property is <t>not</t> limited the 'real' property.
	 * For example, the following statement is correct though
	 * <code>button</code> doesn't have <code>setFoo</code> method.
	 * And, you can retrieve it by use of this method (<code>getAnnotation("foo", "bind")</code>)
	 *
	 * <pre><code>&lt;button foo="@bind(whatever=123)"/&gt;</code></pre>
	 *
	 * <p>Furthermore, you can declare it as <code>custom-attribute</code>.
	 * For example, the following is equivalent to the above.
	 *
	 * <pre><code>&lt;button>
	 *  &lt;custom-attribute foo="@bind(whatever=123}"&gt;
	 *&lt;/button></code></pre>
	 *
	 * @param propName the property name, e.g., "value".
	 * If null, this method returns the annotation(s) associated with this
	 * component (rather than a particular property).
	 * @param annotName the annotation name
	 * @see #getAnnotations(String, String)
	 */
	public Annotation getAnnotation(String propName, String annotName);

	/** Returns the annotations associated with the definition of the specified
	 * property. It never returns null.
	 *
	 * <p>Notice that the property is <t>not</t> limited the 'real' property.
	 * For example, the following statement is correct though
	 * <code>button</code> doesn't have <code>setFoo</code> method.
	 * And, you can retrieve it by use of this method (<code>getAnnotation("foo", "bind")</code>)
	 *
	 * <pre><code>&lt;button foo="@bind(whatever=123)"/&gt;</code></pre>
	 *
	 * <p>Furthermore, you can declare it as <code>custom-attribute</code>.
	 * For example, the following is equivalent to the above.
	 *
	 * <pre><code>&lt;button>
	 *  &lt;custom-attribute foo="@bind(whatever=123}"&gt;
	 *&lt;/button></code></pre>
	 *
	 * @param propName the property name, e.g., "value".
	 * If null, this method returns the annotation(s) associated with this
	 * component (rather than a particular property).
	 * @param annotName the annotation name
	 * @see #getAnnotation(String, String)
	 * @since 6.0.0
	 */
	public Collection<Annotation> getAnnotations(String propName, String annotName);

	/** @deprecated As of release 6.0.0, replaced with {@link #getAnnotations(String)}.
	 */
	public Collection<Annotation> getAnnotations();

	/** Returns a read-only collection of all annotations ({@link Annotation})
	 * associated with the specified property. It never returns null.
	 *
	 * @param propName the property name, e.g., "value".
	 * If null, this method returns the annotation(s) associated with this
	 * component (rather than a particular property).
	 */
	public Collection<Annotation> getAnnotations(String propName);

	/** Returns a read-only list of the names of the properties
	 * that are associated with the specified annotation (never null).
	 */
	public List<String> getAnnotatedPropertiesBy(String annotName);

	/** Returns a read-only list of the name of properties that
	 * are associated at least one annotation (never null).
	 */
	public List<String> getAnnotatedProperties();

	/** @deprecated As of release 6.0.0, replaced with
	 * {@link #addAnnotation(String, String, Map)}
	 */
	public void addAnnotation(String annotName, Map<String, String[]> annotAttrs);

	/** Adds an annotation to the specified property of this component.
	 *
	 * <p>If the given property is null, the annotation is associated
	 * to this component, rather than a particular property.
	 *
	 * <p>Unlike Java, you can add annotations dynamically, and each component
	 * has its own annotations.
	 *
	 * @param propName the property name.
	 * If null, the annotation is associated with the component (rather than
	 * a particular property).
	 * @param annotName the annotation name (never null, nor empty).
	 * @param annotAttrs a map of attributes, or null if no attribute at all.
	 * This method will make a copy of <code>annotAttrs</code>, so the caller
	 * can use it after the invocation.
	 */
	public void addAnnotation(String propName, String annotName, Map<String, String[]> annotAttrs);

	/** Notification that the session, which owns this component,
	 * is about to be passivated (a.k.a., serialized).
	 *
	 * <p>Note: only root components are notified by this method.
	 */
	public void sessionWillPassivate(Page page);

	/** Notification that the session, which owns this component,
	 * has just been activated (a.k.a., deserialized).
	 *
	 * <p>Note: only root components are notified by this method.
	 */
	public void sessionDidActivate(Page page);

	/** Returns the extra controls that tell ZK how to handle this component
	 * specially.
	 *
	 * <p>Application developers need NOT to access this method.
	 *
	 * <p>There are a set of extra controls: org.zkoss.zk.ui.ext.render.
	 *
	 * <p>The first package is used if the content of a component can be changed
	 * by the user at the client. It is so-called the client controls.
	 *
	 * <p>The second package is used to control how to render a component
	 * specially.
	 *
	 * <p>Override this method only if you want to return the extra
	 * controls.
	 *
	 * @return null if no special handling required. If the component
	 * requires some special controls, it could return an object that
	 * implements one or several interfaces in the org.zkoss.zk.ui.ext.render
	 * package.
	 * For example, {@link org.zkoss.zk.ui.ext.render.Cropper}.
	 */
	public Object getExtraCtrl();

	/**
	 * Returns the corresponding property access object from the given property
	 * name, if any.
	 * @param prop the name of the property
	 * @return null it means not to support for the property name.
	 * @since 8.0.0
	 */
	public PropertyAccess getPropertyAccess(String prop);

	/** Notifies that an {@link WrongValueException} instance is thrown,
	 * and {@link WrongValueException#getComponent} is the component
	 * causing the exception.
	 * It is a callback and the component can store the error message,
	 * show up the custom information, or even 'eat' the exception.
	 *
	 * @param ex the exception being thrown (never null)
	 * @return the exception to throw, or null to ignore the exception
	 * In most cases, just return ex
	 * @since 2.4.0
	 */
	public WrongValueException onWrongValue(WrongValueException ex);

	/** Render (a.k.a., redraw) this component and all its descendants.
	 *
	 * <p>It is called in the redrawing phase by the kernel, so it is too late
	 * to call {@link Component#invalidate()},
	 * {@link org.zkoss.zk.ui.AbstractComponent#smartUpdate}
	 * or {@link org.zkoss.zk.ui.AbstractComponent#response} in this method.
	 * @since 5.0.0
	 */
	public void redraw(Writer out) throws IOException;

	/** Handles an AU request.
	 *
	 * <p>Notice: don't invoke this method directly. Rather, invoke
	 * {@link DesktopCtrl#service(AuRequest, boolean)} instead.
	 * This method is designed to be overridden.
	 *
	 * <p>To send responses to the client, use
	 * {@link org.zkoss.zk.ui.AbstractComponent#smartUpdate},
	 * {@link org.zkoss.zk.ui.AbstractComponent#response}
	 * or {@link Component#invalidate()}.
	 * To handle the AU requests sent from the client, override this
	 * method.
	 *
	 * <p>Application developer can plug the custom service to handle
	 * the AU request by calling {@link Component#setAuService}.
	 *
	 * @param everError whether any error ever occurred before
	 * processing this request.
	 * @since 5.0.0
	 * @see Component#setAuService
	 */
	public void service(AuRequest request, boolean everError);

	/** Handles an event.
	 * This method will invoke the event handlers registered in a ZUML page,
	 * the event listeners registered in Java, and the event handlers
	 * declared as part of the component.
	 *
	 * @param event the event to handle
	 * @param scope the scope to evaluate the zscript, if any.
	 * (see also {@link Page#interpret}.
	 * @since 6.0.0
	 */
	public void service(Event event, Scope scope) throws Exception;

	/** Sets whether to disable the update of the client widgets of
	 * this component and its descendants.
	 * <p>By default, if a component is attached to a page, modifications that
	 * change the visual representation will be sent to the client to
	 * ensure the consistency.
	 * <p>Though rarely needed, you can disable the synchronization of
	 * the visual representation, if you prefer to update the client in a batch
	 *or the modification is caused by the client.
	 * <p><b>Notice</b>:
	 * <ul>
	 * <li>Once disabled, it not only affects the synchronization of
	 * this component but also all its descendants.</li>
	 * <li>The disable remains until the end of this execution
	 * (or the invocation of this method with false). In other words,
	 * it is enabled automatically in the next execution.</li>
	 * <li>The updates, if any, before calling this method will still be sent
	 * to the client.</li>
	 * <li>It does nothing, if there is no current execution.</li>
	 * </ul>
	 *
	 * <p>Also notice that, with {@link Component#invalidate},
	 * it is easy to synchronize the content of a component
	 * (and its descendants) to the client.
	 *
	 * @return whether it has been disabled before this invocation, i.e.,
	 * the previous disable status
	 * @since 3.6.2
	 */
	public boolean disableClientUpdate(boolean disable);

	/** Returns the map of event handlers and listeners defined in this component.
	 * This method is rarely used, but it is useful if  you'd like to retrieve
	 * the behavior of the event handling of this component (and if you don't
	 * have the reference to the component)
	 * @since 6.0.0
	 */
	public EventListenerMap getEventListenerMap();

	/**
	 * Returns a set of shadow elements, if any.
	 * @since 8.0.0
	 */
	public <T extends ShadowElement> List<T> getShadowRoots();

	/**
	 * Removes the given shadow root from this host. (Shadow developer use only)
	 * @param shadow a shadow element
	 * @return true if child is removed successfully; false if it doesn't
	 * have the specified child
	 * @since 8.0.0
	 */
	public boolean removeShadowRoot(ShadowElement shadow);

	/**
	 * Adds the given shadow root from this host. (Shadow developer use only)
	 * @param shadow a shadow element
	 * @return true if child is added successfully
	 * @since 8.0.0
	 */
	public boolean addShadowRoot(ShadowElement shadow);

	/**
	 * Adds the given shadow root from this host. (Shadow developer use only)
	 * @param shadow a shadow element
	 * @param insertBefore the shadow before which you want the new child
	 * @return true if child is added successfully
	 * @since 8.0.0
	 */
	public boolean addShadowRootBefore(ShadowElement shadow, ShadowElement insertBefore);

	/**
	 * Set to enable the component with binding annotation. (Internal or component developer use only.)
	 * @since 8.0.0
	 */
	public void enableBindingAnnotation();

	/**
	 * Set to disable the component with binding annotation. (Internal or component developer use only.)
	 * @since 8.0.0
	 */
	public void disableBindingAnnotation();

	/**
	 * Returns whether the component itself has binding annotation or not. (Internal or component developer use only.)
	 * @return true if the component itself has binding annotation
	 * @since 8.0.0
	 */
	public boolean hasBindingAnnotation();

	/**
	 * Returns whether the component and its children have binding annotation or not. (Internal or component developer use only.)
	 * @return true if the component and its children have binding annotation
	 * @since 8.0.0
	 */
	public boolean hasSubBindingAnnotation();

	/**
	 * Returns the count of the component's subtree binding annotation. (Internal or component developer use only.)
	 * @return 0 if the component and its children have no binding annotation , more than 0 if they have binding annotation
	 * @since 8.0.0
	 */
	public int getSubBindingAnnotationCount();

	/**
	 * Adds a callback at component redraw phase.
	 * @param callback
	 * @since 8.0.0
	 * @deprecated as of release 8.0.2, use {@link #addCallback} with specific name "redraw"
	 */
	public boolean addRedrawCallback(Callback<ContentRenderer> callback);

	/**
	 * Removes a callback for component redraw phase.
	 * @param callback
	 * @since 8.0.0
	 * @deprecated as of release 8.0.2, use {@link #removeCallback} with specific name "redraw"
	 */
	public boolean removeRedrawCallback(Callback<ContentRenderer> callback);

	/**
	 * Returns all of callbacks for component redraw phase
	 * @since 8.0.0
	 * @deprecated as of release 8.0.2, use {@link #getCallback} with specific name "redraw"
	 */
	public Collection<Callback<ContentRenderer>> getRedrawCallback();

	/**
	 * Adds a callback at component in specific name
	 * @param name
	 * @param callback
	 * @since 8.0.2
	 */
	public boolean addCallback(String name, Callback callback);

	/**
	 * Removes a callback for component by specific name.
	 * @param name
	 * @param callback
	 * @since 8.0.2
	 */
	public boolean removeCallback(String name, Callback callback);

	/**
	 * Returns all of callbacks by specific name
	 * @param name
	 * @since 8.0.2
	 */
	public Collection<Callback> getCallback(String name);

	/**
	 * Returns the shadow element under this shadow host.
	 * @param id
	 * @return ShadowElement or null
	 * @since 8.0.1
	 */
	public ShadowElement getShadowFellowIfAny(String id);

	/**
	 * Invalidates this component by setting the dirty flag
	 * such that it will be redraw the partial content of this
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
	 *
	 * @param subId the redrawn node subid
	 * @since 9.0.0
	 */
	public void invalidatePartial(String subId);
}
