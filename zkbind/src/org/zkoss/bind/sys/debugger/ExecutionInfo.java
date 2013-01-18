/* ExecutionInfo.java

	Purpose:
		
	Description:
		
	History:
		2013/1/18 Created by dennis

Copyright (C) 2012 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.sys.debugger;

import org.zkoss.zk.ui.Component;

/**
 * @author dennis
 *
 */
public interface ExecutionInfo {

	public Component getComponent();
	public String getType();
	public String getSubject();
	
}
