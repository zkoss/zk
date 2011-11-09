/* EventHandler.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 19 15:17:41     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.EvaluatorRef;

/**
 * An event handler of a component definition ({@link ComponentDefinition}).
 *
 * @author tomyeh
 */
public class EventHandler extends ConditionValue {
	private static final long serialVersionUID = 20060622L;

	private final ZScript _zscript;

	public EventHandler(EvaluatorRef evalr, ZScript zscript, ConditionImpl cond) {
		super(evalr, cond);

		if (zscript == null)
			throw new IllegalArgumentException();
		_zscript = zscript;
	}
	public EventHandler(ZScript zscript) {
		this(null, zscript, null);
	}

	/** Returns the zscript associated with this event handler.
	 */
	public ZScript getZScript() {
		return _zscript;
	}
}
