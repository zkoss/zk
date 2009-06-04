/* NamespaceSerializationListener.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun  4 12:18:22     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.scripting;

/**
 * Used to notify an object stored in a namespace, when the namespace
 * is going to be serialized or has been deserialized.
 *
 * <p>When a namespace is going to be serialized, it checks every
 * variable to see whether this interface is implemented.
 * If implemented, {@link #willSerialize} will be called.
 * Similarly, {@link #didDeserialize} is called if the namespace has
 * been deserialized.
 * 
 * @author tomyeh
 * @since 3.6.2
 */
public interface NamespaceSerializationListener {
	/** Called when a namespace is going to serialize this object.
	 * @param comp the namespace's owner
	 */
	public void willSerialize(Namespace ns);
	/** Called when a namespace has de-serialized this object back.
	 * @param comp the namespace's owner
	 */
	public void didDeserialize(Namespace ns);
}
