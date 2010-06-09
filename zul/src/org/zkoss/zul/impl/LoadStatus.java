/* LoadStatus.java
{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 11, 2009 10:53:01 AM, Created by henrichen
}}IS_NOTE

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

/**
 * Denote the status whether a on-demand-loading item is loaded. 
 * @author henrichen
 *
 */
public interface LoadStatus {
	/**
	 * Sets whether the on-demand-loading item is loaded.
	 * @param loaded true to set the item as loaded.
	 */
	public void setLoaded(boolean loaded);
	/**
	 * Returns whether the on-demand-loading item is loaded.
	 * @return loaded true to set the item as loaded.
	 */
	public boolean isLoaded();
	
	/**
	 * Sets the index of the loaded item (so client known row's index).
	 * @param index the loaded item
	 */
	public void setIndex(int index);
}
