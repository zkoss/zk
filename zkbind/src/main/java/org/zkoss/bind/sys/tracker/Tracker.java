/* BindTracker.java

	Purpose:
		
	Description:
		
	History:
		Jun 29, 2011 6:33:22 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.tracker;

import java.util.Set;

import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.LoadBinding;
import org.zkoss.zk.ui.Component;

/**
 * Bind tracker to maintain a binding dependency graph.
 * e.g. @{a.b.c, save-onchange:d.e.f} then it forms a dependency from d.e.f to a.b.c for save. 
 * @author henrichen
 * @since 6.0.0
 */
public interface Tracker {
	/**
	 * Add a tracking that associate a binding to a dot series under the specified {@link Component}
	 * @param comp the component with the associated binding
	 * @param series the dot series as an array of property name
	 * @param binding the associated binding
	 */
	public void addTracking(Component comp, String[] series, Binding binding);

	/**
	 * Add a depends-on tracking between the source property name series and depends-on property name series.
	 * @param srcComp the source component with the associated binding
	 * @param srcSeries the dot series as an array of source property name.
	 * @param srcBinding the associated binding
	 * @param dependsOnSeries the dot series as an array of dependsOn property name.
	 */
	public void addDependsOn(Component srcComp, String[] srcSeries, Binding srcBinding, Component dependsOnComp,
			String[] dependsOnSeries);

	/**
	 * Remove all tracking associated with the specified {@link Component}. 
	 * @param comp the associated component
	 */
	public void removeTrackings(Component comp);

	/**
	 * Remove all tracking associated with the specified {@link Component} set.
	 * @param comps the associated component set
	 * @since 7.0.2
	 */
	public void removeTrackings(Set<Component> comps);

	/**
	 * Tie a property to its corresponding value under the specified {@link Component}.
	 * @param comp the associated component
	 * @param base the base object of the property
	 * @param script the field script
	 * @param propName the resolved property name from the field script
	 * @param value the value of the property
	 * @param basePath the basePath script prior to the <tt>script</tt> field script (Since 8.0)
	 */
	public void tieValue(Object comp, Object base, Object script, Object propName, Object value, Object basePath);

	/**
	 * Returns all bindings that associated with the specified property.
	 * @param base base object
	 * @param propName property name
	 * @return all LoadBindings that associated with the specified property.
	 */
	public Set<LoadBinding> getLoadBindings(Object base, String propName);

	/**
	 * Returns all direct bindings that associated with the specified property.
	 * @param base base object
	 * @param propName property name
	 * @return all direct LoadBindings that associated with the specified property.
	 * @since 10.2.0
	 */
	public Set<LoadBinding> getDirectLoadBindings(Object base, String propName);

	/**
	 * Returns all bindings that associated with the kid bases of specified property.
	 * @param base base object
	 * @param propName property name
	 * @return all bindings that associated with the kid bases of specified property.
	 * @since 10.2.0
	 */
	public Set<LoadBinding> getKidBaseLoadBindings(Object base, String propName);

}
