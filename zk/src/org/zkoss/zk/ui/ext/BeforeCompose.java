/* BeforeCompose.java

	Purpose:
		
	Description:
		
	History:
		Wed Nov  3 16:17:49 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.ext;

/**
 * Implemented by a component if it wants to know when ZK loader created it.
 * If this interface is implemented, {@link #beforeCompose} is called
 * before ZK loader sets any property or creates any child components.
 * <p>On the other hand, {@link AfterCompose#afterCompose} is called
 * after all properties are set and child components are created.
 * 
 * @author tomyeh
 * @since 5.0.6
 * @see AfterCompose
 */
public interface BeforeCompose {
	/** Called by ZK Loader before it sets any properties, and before
	 * creates any child component.
	 */
	public void beforeCompose();
}
