/* UiFactory.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 15 09:14:58     2007, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmob;

import org.xml.sax.Attributes;

/**
 * UiFactory responsible for create a specific component.
 * 
 * @author henrichen
 *
 */
public interface UiFactory {
	/**
	 * create a component.
	 * @param parent the parent component
	 * @param tag the tag name
	 * @param attrs the attribute
	 */
	public Object create(Object parent, String tag, Attributes attrs, Context ctx);
	
	/**
	 * called after complete creating a component.
	 */
	public void afterCreate(Object parent, String tag, Object comp, Context ctx);
}
