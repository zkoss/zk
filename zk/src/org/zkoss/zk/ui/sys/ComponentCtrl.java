/* ComponentCtrl.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon May 30 21:06:56     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.List;
import java.util.Collection;
import java.util.Set;
import java.util.Map;
import java.io.Writer;
import java.io.IOException;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.metainfo.EventHandlerMap;
import org.zkoss.zk.au.AuRequest;

/**
 * An addition interface to {@link Component}
 * that is used for implementation or tools.
 *
 * <p>Application developers rarely need to access methods in this interface.
 *
 * @author tomyeh
 */
public interface ComponentCtrl {
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
	/** @deprecated As of release 3.6.3, replaced with {@link #setDefinition}.
	 */
	public void setComponentDefinition(ComponentDefinition compdef);

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
	 * overwrite the pevious listener if the event name is the same.
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
	public Set getEventHandlerNames();

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
	public Map getClientEvents();

	/** Returns the annotation associated with the component,
	 * or null if not available.
	 *
	 * @param annotName the annotation name
	 */
	public Annotation getAnnotation(String annotName);
	/** Returns the annotation associated with the definition of the specified
	 * property, or null if not available.
	 *
	 * @param annotName the annotation name
	 * @param propName the property name, e.g., "value".
	 * @exception IllegalArgumentException if propName is null or empty
	 */
	public Annotation getAnnotation(String propName, String annotName);
	/** Returns a read-only collection of all annotations associated with this
	 * component (never null).
	 */
	public Collection getAnnotations();
	/** Returns a read-only collection of all annotations associated with the
	 * specified property (never null).
	 *
	 * @param propName the property name, e.g., "value".
	 * @exception IllegalArgumentException if propName is null or empty
	 */
	public Collection getAnnotations(String propName);
	/** Returns a read-only list of the names (String) of the properties
	 * that are associated with the specified annotation (never null).
	 */
	public List getAnnotatedPropertiesBy(String annotName);
	/** Returns a read-only list of the name (String) of properties that
	 * are associated at least one annotation (never null).
	 */
	public List getAnnotatedProperties();
	/** Add a map of annotations which is shared by other components.
	 * In other words, this component shall have all annotations
	 * defined in the specified map, annots. Meanwhile, this component
	 * shall not modify annots, since it is shared.
	 * The caller shall not change annots after the invocation, too
	 *
	 * @param annots a annotation map.
	 */
	public void addSharedAnnotationMap(AnnotationMap annots);
	/** Associates an annotation to this component.
	 *
	 * <p>Unlike Java, you can add annotations dynamically, and each component
	 * has its own annotations.
	 *
	 * @param annotName the annotation name (never null, nor empty).
	 * @param annotAttrs a map of attributes, or null if no attribute.
	 * The attribute must be in a pair of strings (String name, String value).
	 */
	public void addAnnotation(String annotName, Map annotAttrs);
	/** Adds an annotation to the specified proeprty of this component.
	 *
	 * @param propName the property name (never nul, nor empty).
	 * @param annotName the annotation name (never null, nor empty).
	 * @param annotAttrs a map of attributes, or null if no attribute at all.
	 * The attribute must be in a pair of strings (String name, String value).
	 */
	public void addAnnotation(String propName, String annotName, Map annotAttrs);

	/** Notification that the session, which owns this component,
	 * is about to be passivated (aka., serialized).
	 *
	 * <p>Note: only root components are notified by this method.
	 */
	public void sessionWillPassivate(Page page);
	/** Notification that the session, which owns this component,
	 * has just been activated (aka., deserialized).
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

	/** Render (aka., redraw) this component and all its descendants.
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
	 * <p>To send reponses to the client, use
	 * {@link org.zkoss.zk.ui.AbstractComponent#smartUpdate},
	 * {@link org.zkoss.zk.ui.AbstractComponent#response}
	 * or {@link Component#invalidate()}.
	 * To handle the AU requests sent from the client, override this
	 * method.
	 *
	 * <p>Application developer can plug the custom service to handle
	 * the AU request by {@link Component#setAuService}.
	 *
	 * @param everError whether any error ever occured before
	 * processing this request.
	 * @since 5.0.0
	 * @see Component#setAuService
	 */
	public void service(AuRequest request, boolean everError);

	/** Sets whether to disable the update of the client widgets of
	 * this component and its descendants.
	 * <p>By default, if a component is attached to a page, modications that
	 * change the visual representation will be sent to the client to
	 * ensure the consistency.
	 * <p>Though rarely needed, you can disable the synchronization of
	 * the visual representation, if you prefer to update the client batchly
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
}
