/* LoadFormBinding.java

	Purpose:
		
	Description:
		
	History:
		Aug 9, 2011 5:48:26 PM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/

package org.zkoss.bind.sys;


/**
 * Binding for load a form.
 * @author henrichen
 * @since 6.0.0
 */
public interface LoadFormBinding extends FormBinding, LoadBinding {
	/**
	 * The attribute name of a loaded bean class, internal use only
	 * @since 6.0.1
	 */
	public static final String LOADED_BEAN_CLASS = "$LOADED_BEAN_CLASS$";
}
