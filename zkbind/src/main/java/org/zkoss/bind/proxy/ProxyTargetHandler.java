/* ProxyTargetHandler.java

	Purpose:
		
	Description:
		
	History:
		Tue Apr 19 16:10:31 CST 2016, Created by jameschu

Copyright (C) 2016 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.proxy;

/**
 * A proxy target handler to help creating proxy on correct origin data object.
 * 
 * <br/>
 * <b>Since 8.0.2</b> - You could set proxy target handler by setting listener class (it should implement this interface)
 * for example:<pre>{@code
<listener>
	<listener-class>foo.BarHandler</listener-class>
</listener>
 * }</pre>
 * Note: The handler instance is shared between all binders, it is not thread-safe, your implementation has to care the concurrent access issue.
 * @author jameschu
 * @since 8.0.2
 */
public interface ProxyTargetHandler {
	/**
	 * Get the corresponding origin data object.
	 * @param origin the origin data object
	 */
	public <T extends Object> T getOriginObject(T origin);

}
