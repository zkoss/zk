/* SessionSerializationListener.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun  7 14:50:16     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Session;

/**
 * Used to notify an object stored in a session, when the session
 * is going to be serialized or has been deserialized.
 *
 * <p>When a session is going to be serialized, it checks every
 * attribute ({@link Session#setAttribute})
 * to see whether this interface is implemented.
 * If implemented, {@link #willSerialize} will be called.
 *
 * @author tomyeh
 * @since 2.4.0
 */
public interface SessionSerializationListener {
	/** Called when a session is going to serialize this object.
	 */
	public void willSerialize(Session session);
}
