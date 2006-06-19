/* EventHandler.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 19 15:17:41     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.util.Condition;

/**
 * An event handler of an instance definition ({@link InstanceDefinition}).
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class EventHandler implements Condition, java.io.Serializable {
	private final String _script;
	private final Condition _cond;
	public EventHandler(String script, Condition cond) {
		_script = script;
		_cond = cond;
	}
	public String getScript() {
		return _script;
	}
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(page);
	}
}
