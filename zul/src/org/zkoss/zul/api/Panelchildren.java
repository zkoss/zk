/* Panelchildren.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 22 14:45:31     2008, Created by Flyworld
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zul.api;

/**
 * Panelchildren is used for {@link Panel} component to manage each child who
 * will be shown in the body of Panel. Note that the size of Panelchildren is
 * automatically calculated by Panel so both {@link #setWidth(String)} and
 * {@link #setHeight(String)} are read-only.
 * 
 * <p>
 * Default {@link #getZclass}: z-panel-children.
 * 
 * @author jumperchen
 * @since 3.5.2
 */
public interface Panelchildren extends org.zkoss.zul.impl.api.XulElement {

}
