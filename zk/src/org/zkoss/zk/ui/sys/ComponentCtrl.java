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

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.metainfo.ComponentDefinition;
import org.zkoss.zk.ui.metainfo.Annotation;
import org.zkoss.zk.ui.metainfo.ZScript;
import org.zkoss.zk.ui.metainfo.EventHandler;
import org.zkoss.zk.ui.metainfo.AnnotationMap;
import org.zkoss.zk.ui.metainfo.EventHandlerMap;
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

	/** Returns the command of the specified command ID, or null if not found.
	 * It searches only the command specific to this component.
	 * For global commands, use {@link org.zkoss.zk.au.AuRequest#getCommand}
	 * instead.
	 *
	 * @since 3.0.5
	 * @see org.zkoss.zk.au.ComponentCommand
	 */
	public Command getCommand(String cmdId);
}
