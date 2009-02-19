/* Textarea.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec 13 15:05:13     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zhtml;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zhtml.impl.AbstractTag;

/**
 * The TEXTAREA tag.
 * 
 * @author tomyeh
 */
public class Textarea extends Input {
	static {
		addClientEvent(Textarea.class, Events.ON_CHANGE, CE_IMPORTANT);
	}

	public Textarea() {
		super("textarea");
	}
}
