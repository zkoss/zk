/* SimpleInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep  6 12:25:20     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * Represents a node or a branch in the tree of the page definition.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public interface SimpleInfo {
	/** Update the info after this object is deserialized (by its parent).
	 *
	 * <p>This method is used only by {@link NodeInfo#readExternal}.
	 *
	 * @since 3.0.0
	 */
	public void didDeserialize(NodeInfo parent, EvaluatorRef evalr);
}
