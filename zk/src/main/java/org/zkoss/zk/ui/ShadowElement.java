/** ShadowElement.java.

	Purpose:
		
	Description:
		
	History:
		5:45:01 PM Oct 22, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zk.ui;

import java.util.List;

import org.zkoss.zk.ui.ext.AfterCompose;

/**
 * A shadow element can allow to have a set of shadow elements to work with a
 * component tree and it can also support with ZK MVVM to dynamically change the
 * content of a shadow element.
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public interface ShadowElement extends AfterCompose {

	/**
	 * Returns the owner component that hosts this shadow element.
	 */
	public Component getShadowHost();

	/**
	 * Returns a list of distributed components of the shadow tree, including
	 * its descendant.
	 * <p>
	 * It's better to manipulate with the component children from the host
	 * component.
	 * 
	 * @return a list of distributed components (removeable only)
	 */
	public <T extends Component> List<T> getDistributedChildren();

	/**
	 * Detach the relation points between the shadow host and this shadow
	 * element.
	 */
	public void detach();

	/**
	 * Detaches all child components and then recreate them.
	 * <p>
	 * It is used if you have assigned new values to dynamic properties and want
	 * to re-create child components to reflect the new values.
	 */
	public void recreate();
}
