/* Space.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Sat Jan 14 00:40:57     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul;

import org.zkoss.zk.ui.WrongValueException;

/**
 * Space is a {@link Separator} with the orient default to "horizontal".
 *
 * <p>In other words, &lt;space&gt; is equivalent to &lt;separator orient="horizontal"&gt;
 *
 * @author tomyeh
 */
public class Space extends Separator implements org.zkoss.zul.api.Space {
	public Space() {
		try {
			setOrient("vertical");
		} catch (WrongValueException ex) {
			throw new InternalError(); //impossible
		}
	}
}
