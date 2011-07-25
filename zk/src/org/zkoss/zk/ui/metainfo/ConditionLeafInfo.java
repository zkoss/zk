/* ConditionLeafInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 10:57:23 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;

/**
 * Used to implement a leaf node that allows the user to specify the if and unless clause
 * @author tomyeh
 * @since 5.1.0
 */
/*package*/ abstract class ConditionLeafInfo extends LeafInfo
implements Condition {
	/*package*/ ConditionImpl _cond;

	/*package*/ ConditionLeafInfo() {
	}
	/*package*/ ConditionLeafInfo(NodeInfo parent, ConditionImpl cond) {
		parent.appendChild(this);
		_cond = cond;
	}

	/** Sets the effectiveness condition.
	 */
	public void setCondition(ConditionImpl cond) {
		_cond = cond;
	}
	/** Tests if the condition is set
	 */
	public boolean withCondition() {
		return _cond != null;
	}

	//Condition//
	//@Override
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(_evalr, comp);
	}
	//@Override
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(_evalr, page);
	}
}
