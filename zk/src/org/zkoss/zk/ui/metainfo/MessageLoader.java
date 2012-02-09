/* MessageLoader.java

{{IS_NOTE
 Purpose:
  
 Description:
  
 History:
  Feb 9, 2012 11:45:22 AM , Created by simonpai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.io.IOException;

import org.zkoss.zk.ui.Execution;

/**
 * The interface for loading messages as String output.
 * @author simonpai
 * @since 5.0.11
 */
public interface MessageLoader {
	
	/**
	 * Load the message to output StringBuffer
	 * @param out StringBuffer to output
	 * @param exec current Execution
	 */
	public void load(StringBuffer out, Execution exec) throws IOException;
	
}
