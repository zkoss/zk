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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Condition;

/**
 * An event handler of a component definition ({@link ComponentDefinition}).
 *
 * @author tomyeh
 */
public class EventHandler implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 20060622L;

	private final ZScript _zscript;
	private final Condition _cond;

	public EventHandler(ZScript zscript, Condition cond) {
		_zscript = zscript;
		_cond = cond;
	}

	/** Returns the zscript associated with this event handler.
	 */
	public ZScript getZScript() {
		return _zscript;
	}

	/** Returns the condition.
	 * @since 3.0.0
	 */
	public Condition getCondition() {
		return _cond;
	}

	//Condition//
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(page);
	}
}
