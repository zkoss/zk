/* ValueReference.java

	Purpose:
		
	Description:
		
	History:
		Wed Sep 14 12:06:33     2011, Created by henri

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.xel;

/**
 * This represents a base model object and one of its properties (EL 2.2).
 * 
 * @author henri
 * @since 5.5
 */
public interface ValueReference {
	/**
	 * Returns the base model.
	 * @return the base model.
	 */
    public Object getBase();
    
    /**
     * Returns the property of the base model.
     * @return the property of the base model.
     */
    public Object getProperty();
}
