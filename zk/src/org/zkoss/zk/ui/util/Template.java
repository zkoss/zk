/* Template.java

	Purpose:
		
	Description:
		
	History:
		Fri Jul  8 12:28:11 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.util;

import java.util.Map;

import org.zkoss.xel.VariableResolver;
import org.zkoss.zk.ui.Component;

/**
 * Represents a UI template that is used to create components.
 *
 * @author tomyeh
 * @since 6.0.0
 */
public interface Template {
	/** Creates the components defined in this template.
	 * @param parent the parent to assign the new component to.
	 * Unlike {@link org.zkoss.zk.ui.Execution#createComponents(PageDefinition, Component, Map)}
	 * (but similar to {@link org.zkoss.zk.ui.Execution#createComponents(PageDefinition, Map)}),
	 * if parent is null or the parent does not belong to any page,
	 * the created components won't be attached to any page too.
	 * @param insertBefore the component that the new components shall
	 * be inserted before. If null, the new components will be appended.
	 * If insertBefore.getParent() is not the same as parent, it is ignored.
	 * @param resolver the addition variable resolver used to render
	 * the template. Ignored if null.
	 * @param composer the addition composer used to control the lifecycle.
	 * Ignored if null.
	 * @exception NullPointerException if parent is null
	 */
	public Component[] create(Component parent, Component insertBefore,
	VariableResolver resolver, Composer composer);
	/** Returns a readonly map of the parameters that are assigned
	 * to the template.
	 * <p>Notice that if a parameter's value contains EL expression, it will
	 * be evaluated when this object is instantiated and assigned to a component
	 * (by use of {@link Component#setTemplate}).
	 */
	public Map<String, Object> getParameters();
}
