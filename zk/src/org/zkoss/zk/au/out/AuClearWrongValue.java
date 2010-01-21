/* AuClearWrongValue.java

	Purpose:
		
	Description:
		
	History:
		Thu May 25 18:17:07     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import java.util.List;
import java.util.Iterator;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to close the error messages belonging
 * the specified component, if any.
 *
 * <p>data[0]: the component's UUID.<br/>
 * data[1]: the UUID of the 2nd component, if any<br/>
 * ...
 * 
 * @author tomyeh
 * @since 5.0.0
 */
public class AuClearWrongValue extends AuResponse {
	/**
	 * @param comps a list of components
	 */
	public AuClearWrongValue(List comps) {
		super("clearWrongValue", toData(comps)); //component-independent
	}
	/**
	 * @param comps a list of components
	 */
	public AuClearWrongValue(Component[] comps) {
		super("clearWrongValue", toData(comps)); //component-independent
	}
	private static String[] toData(List comps) {
		final String[] uuids = new String[comps.size()];
		int j = 0;
		for (Iterator it = comps.iterator(); it.hasNext();)
			uuids[j++] = ((Component)it.next()).getUuid();
		return uuids;
	}
	private static String[] toData(Component[] comps) {
		final String[] uuids = new String[comps.length];
		for (int j = 0; j < comps.length; j++)
			uuids[j] = comps[j].getUuid();
		return uuids;
	}	
	/** Unlike other constructors, the object instantiated by this method
	 * depends on the specified comp.
	 * @param comp the component whose error box, if any, shall be closed.
	 */
	public AuClearWrongValue(Component comp) {
		super("clearWrongValue", comp, comp.getUuid()); //dependent
	}
}
