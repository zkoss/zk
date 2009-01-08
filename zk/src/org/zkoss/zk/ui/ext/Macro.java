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
 * Implemented with {@link org.zkoss.zk.ui.Component} to represent
 * a macro component.
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
	/** Returns the macro URI.
	 * <p>If {@link #setMacroURI} wasn't called, it returns the URI
	 * defined in the macro definition.
	 * @since 3.0.9
	 */
	public String getMacroURI();
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
	/** Returns whether this is an inline macro.
	 * The only way to create an inline macro is by use of
	 * {@link org.zkoss.zk.ui.metainfo.ComponentDefinition#newInstance}.
	 *
	 * @since 2.4.0
	 */
	public boolean isInline();
}
