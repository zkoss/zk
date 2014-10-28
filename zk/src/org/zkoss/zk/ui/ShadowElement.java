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
import org.zkoss.zk.ui.ext.DynamicPropertied;

/**
 * A shadow element can hold a set of shadow elements to work with a component tree
 * and it can support with ZK MVVM to dynamically change the content of a shadow
 * element.
 * 
 * @author jumperchen
 * @since 8.0.0
 */
public interface ShadowElement extends AfterCompose, DynamicPropertied {
	
	/**
	 * Returns the owner component that hosts this shadow element.
	 */
	public Component getShadowHost();
	
	/**
	 * Returns a list of distributed components of the shadow tree.
	 * Including its descendant.
	 * @return a list of distributed components 
	 */
	public <T extends Component> List<T> getDistributedChildren();
	
	/**
	 * Detach the relation points between the shadow host and this shadow element.
	 */
	public void detach();
	
	/**
	 * Destroys all. That means to remove all of the distributed children which
	 * inserted to the shadow host. And detach the relation points between shadow
	 * host and this shadow element, the detach relation points is the same as
	 * invoking {@link ShadowElement#detach()}.
	 */
	public void destroy();
}
