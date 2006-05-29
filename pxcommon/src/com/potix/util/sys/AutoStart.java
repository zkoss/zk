/* AutoStart.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jun 19 12:05:12     2003, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.sys;

/**
 * {@link Singleton} and {@link PerDomainManager} will invoke
 * {@link #start} if the singleton being instantiated implements
 * this interface.
 *
 * <p>In other words, it is 'tag' interface that tell others that
 * this object shall be started automatically.
 *
 * <p>Rule of thumb to use AutoStart or constructor:
 * If no init codes call back the(), puting codes in the constructor
 * is good enough.
 * However, if the init codes do call back the(), you have to put
 * those code in {@link AutoStart#start}. The call to the() from
 * the same thread will return the correct instead, while other threads
 * will be blocked.
 *
 * <p>The role of AutoStart for {@link Singleton} and {@link PerDomainManager}
 * is similar to {@link com.potix.comp.Initial} for components.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @see Singleton
 */
public interface AutoStart {
	/** Starts the service. */
	public void start();
}
