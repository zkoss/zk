/** ViewModelProxyObject.java.

 Purpose:

 Description:

 History:
		Wed Dec 26 11:00:32 CST 2017, Created by jameschu

 Copyright (C) 2017 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.bind.proxy;

/**
 * A simple view model proxy object
 * 
 * @author jameschu
 * @since 8.5.1
 */
public interface ViewModelProxyObject {
	public Object getOriginObject();
	public Class<?> getOriginClass();
}
