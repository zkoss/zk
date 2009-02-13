/* ComponentSerializationListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  7 13:36:59     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Component;

/**
 * Used to notify an object stored in a component, when the component
 * is going to be serialized or has been deserialized.
 *
 * <p>When a component is going to be serialized, it checks every
 * attribute ({@link Component#setAttribute})
 * and listener ({@link Component#addEventListener})
 * to see whether this interface is implemented.
 * If implemented, {@link #willSerialize} will be called.
 * Similarly, {@link #didDeserialize} is called if the component has
 * been deserialized.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public interface ComponentSerializationListener {
	/** Called when a component is going to serialize this object.
	 */
	public void willSerialize(Component comp);
	/** Called when a component has de-serialized this object back.
	 */
	public void didDeserialize(Component comp);
}
