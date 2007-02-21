/* Macro.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Feb 21 23:05:29     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

import org.zkoss.zk.ui.IdSpace;

/**
 * Represents a macro component.
 *
 * @author tomyeh
 */
public interface Macro extends AfterCompose, IdSpace, DynamicPropertied {
	/** Sets the macro URI.
	 * It affects only this component.
	 *
	 * <p>Note: this method calls {@link #recreate} automatically
	 * if uri is changed.
	 *
	 * @param uri the URI of this macro. If null, the default is used.
	 */
	public void setMacroURI(String uri);
	/** Detaches all child components and then recreate them by use of 
	 * {@link #afterCompose}.
	 *
	 * <p>It is used if you have assigned new values to dynamical properties
	 * and want to re-create child components to reflect the new values.
	 * Note: it is convenient but the performance is better if you can manipulate
	 * only the child components that need to be changed.
	 * Refer to the Developer's Guide for details.
	 */
	public void recreate();
}
