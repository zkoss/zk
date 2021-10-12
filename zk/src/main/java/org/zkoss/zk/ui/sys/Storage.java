/* Storage.java

	Purpose:
		
	Description:
		
	History:
		3:15 PM 7/13/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui.sys;

import java.io.Serializable;

/**
 * A storage interface is used for ZK web application to store application data
 * with a string key and a data type value.
 * The implementation class for this interface must be a thread safe that application
 * developer can add, remove, or get the data without dealing with threads.
 * @author jumperchen
 * @since 8.0.0
 */
public interface Storage extends Serializable {
	/**
	 * Returns the value from the given key.
	 */
	public <T> T getItem(String key);

	/**
	 * Sets a data value with a given key
	 */
	public <T> void setItem(String key, T value);

	/**
	 * Removes the value from the given key, if any.
	 * @return a removed value or null
	 */
	public <T> T removeItem(String key);
}
