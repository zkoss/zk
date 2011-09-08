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
 * @since 5.5.0
 */
public interface Template {
	/** Creates the components defined in this template.
	 * @param parent the parent to assign the new component to.
	 * @param insertBefore the component that the new components shall
	 * be inserted before. If null, the new components will be appended.
	 * If insertBefore.getParent() is not the same as parent, it is ignored.
	 * @param resolver the addition variable resolver used to render
	 * the template. Ignored if null.
	 * @exception NullPointerException if parent is null
	 */
	public Component[] create(Component parent, Component insertBefore,
	VariableResolver resolver);
	/** Returns a readonly map of the parameters that are assigned
	 * to the template.
	 * <p>Notice that if a parameter's value contains EL expression, it will
	 * be evaluated when this object is instantiated and assigned to a component
	 * (by use of {@link Component#setTemplate}).
	 */
	public Map getParameters();
}
