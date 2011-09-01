/* DesktopSerializationListener.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  7 13:36:48     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Desktop;

/**
 * Used to notify an object stored in a desktop, when the desktop
 * is going to be serialized or has been deserialized.
 *
 * <p>When a desktop is going to be serialized, it checks every
 * attribute ({@link Desktop#setAttribute})
 * to see whether this interface is implemented.
 * If implemented, {@link #willSerialize} will be called.
 * Similarly, {@link #didDeserialize} is called if the desktop has
 * been deserialized.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public interface DesktopSerializationListener {
	/** Called when a desktop is going to serialize this object.
	 */
	public void willSerialize(Desktop desktop);
	/** Called when a desktop has de-serialized this object back.
	 */
	public void didDeserialize(Desktop desktop);
}
