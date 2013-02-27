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
 * the base interface of ExecutionInfo
 * @author dennis
 *
 */
public interface ExecutionInfo {

	/** the component of this info **/
	public Component getComponent();
	
	/** the type of this info **/
	public String getType();
	
	/** the note of this info **/
	public String getNote();
	
}
