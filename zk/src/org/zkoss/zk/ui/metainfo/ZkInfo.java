/* ZkInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri May 30 19:13:58     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.metainfo.impl.ComponentDefinitionImpl;

/**
 * Represents the zk element in a ZUML page.
 *
 * @author tomyeh
 * @since 3.1.0
 */
public class ZkInfo extends ComponentInfo {
	/** A special definition to represent the zk component. */
	/*package*/ final static ComponentDefinition ZK =
		new ComponentDefinitionImpl(null, null, "zk", Component.class);;

	/** Constructs a ZK info.
	 */
	public ZkInfo(NodeInfo parent) {
		super(parent, ZK);
	}
}
