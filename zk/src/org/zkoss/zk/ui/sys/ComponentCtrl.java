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

import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.metainfo.EventHandlerMap;
import org.zkoss.zk.ui.util.DeferredValue;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.au.Command;

/**
 * An addition interface to {@link org.zkoss.zk.ui.Component}
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
	public void setComponentDefinition(ComponentDefinition compdef);

	/** Called when a child is added.
	 * If a component want to optimize the update, it might do something
	 * different. Otherwise, it does nothing.
	 *
	 * <p>Note: {@link #onChildAdded} is called in the request-processing
	 * phase, while {@link #onDrawNewChild} is called in the redrawing phase.
	 * See {@link #onDrawNewChild} for more details.
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

	/** Called when a new-created child is about to render.
	 * It gives the parent a chance to fine-tune the output.
	 * Note: it won't be called if the parent is rendered, too.
	 * In other words, it is called only if the child is attached dynamically.
	 *
	 * <p>It is called in the redrawing phase by the kernel, so it is too late
	 * to call {@link Component#invalidate()} or {@link #smartUpdate} in this method.
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
	 * How: overwrite {@link #onChildAdded} and calls {@link Component#invalidate()}</li>
	 * </ul>
	 *
	 * @param child the child being rendered
	 * @param out the rendered result of the child.
	 * @since 3.5.0
	 */
	public void onDrawNewChild(Component child, StringBuffer out)
	throws java.io.IOException;

	/** Smart-updates a property with the specified value.
	 * Called by component developers to do precise-update.
	 *
	 * <p>The second invocation with the same property will replace the previous
	 * call. In other words, the same property will be set only once in
	 * each execution.
	 *
	 * <p>This method has no effect if {@link Component#invalidate()} is ever invoked
	 * (in the same execution), since {@link Component#invalidate()} assumes
	 * the whole content shall be redrawn and all smart updates to
	 * this components can be ignored,
	 *
	 * <p>Once this method is called, all invocations to {@link #smartUpdate}
	 * will then be ignored, and {@link Component#redraw} will be invoked later.
	 *
	 * <p>It can be called only in the request-processing and event-processing
	 * phases; excluding the redrawing phase.
	 *
	 * <p>There are two ways to draw a component, one is to invoke
	 * {@link Component#invalidate()}, and the other is {@link #smartUpdate}.
	 * While {@link Component#invalidate()} causes the whole content to redraw,
	 * {@link #smartUpdate} let component developer control which part
	 * to redraw.
	 *
	 * @param value the new value. If null, it means removing the property.
	 * @since 3.5.0
	 * @see #smartUpdateDeferred
	 * @see #smartUpdateValues
	 */
	public void smartUpdate(String attr, String value);
	/** Smart-updates a property with a deferred value.
	 * A deferred value is used to encapsulate a value that shall be retrieved
	 * only in the rendering phase.
	 * In other words, {@link DeferredValue#getValue} won't be called until
	 * the rendering phase. On the other hand, this method is usually called
	 * in the event processing phase.
	 *
	 * <p>For some old application servers (example, Webshpere 5.1),
	 * {@link Execution#encodeURL} cannot be called in the event processing
	 * thread. So, the developers have to use {@link DeferredValue}
	 * or disable the use of the event processing thread
	 * (by use of <code>disable-event-thread</code> in zk.xml).
	 *
	 * @since 3.5.0
	 * @see #smartUpdate
	 * @see #smartUpdateValues
	 */
	public void smartUpdateDeferred(String attr, DeferredValue value);
	/** Smart-updates a property with an array of values.
	 *
	 * @param values an array of values. Any of them must be an instance
	 * of String or {@link DeferredValue}.
	 * @since 3.5.0
	 * @see #smartUpdate
	 * @see #smartUpdateDeferred
	 */
	public void smartUpdateValues(String attr, Object[] values);
	/** Causes a response to be sent to the client.
	 *
	 * <p>If {@link AuResponse#getDepends} is not null, the response
	 * depends on the existence of the returned componet.
	 * In other words, the response is removed if the component is removed.
	 * If it is null, the response is component-independent and it is
	 * always sent to the client.
	 *
	 * <p>Unlike {@link #smartUpdate}, responses are sent even if
	 * {@link Component#invalidate()} was called.
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
	 * @since 3.5.0
	 */
	public void response(String key, AuResponse response);

	/** Returns the event handler of the specified name, or null
	 * if not found.
	 */
	public ZScript getEventHandler(String evtnm);
	/** Adds an event handler.
	 */
	public void addEventHandler(String name, EventHandler evthd);
	/** Adds a map of event handlers which is shared by other components.
	 * In other words, this component shall have all event handlers
	 * defined in the specified map, evthds. Meanwhile, this component
	 * shall not modify evthds, since it is shared.
	 * The caller shall not change annots after the invocation, too
	 *
	 * @param evthds a map of event handler
	 */
	public void addSharedEventHandlerMap(EventHandlerMap evthds);
	/** Returns a readonly collection of event names (String), or
	 * an empty collection if no event name is registered.
	 *
	 * @since 3.0.2
	 */
	public Set getEventHandlerNames();

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
	 * <p>There are two set of extra controls: org.zkoss.zk.ui.ext.client
	 * and org.zkoss.zk.ui.ext.render.
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
	 * and org.zkoss.zk.ui.ext.client packages.
	 * For example, {@link org.zkoss.zk.ui.ext.render.Cropper}
	 * and {@link org.zkoss.zk.ui.ext.client.InputableX}.
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

	/** Returns the component-specific command of the specified command ID,
	 * or null if not found.
	 * It searches only the command specific to this component.
	 * For global commands, use {@link org.zkoss.zk.au.AuRequest#getCommand}
	 * instead.
	 *
	 * @since 3.0.5
	 * @see org.zkoss.zk.au.ComponentCommand
	 */
	public Command getCommand(String cmdId);
}
