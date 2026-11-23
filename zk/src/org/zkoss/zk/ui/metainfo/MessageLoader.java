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

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.http.Wpds;
import org.zkoss.zk.ui.util.Clients;

/**
 * The interface for loading messages as String output.
 * 
 * @author simonpai
 * @since 5.0.11
 */
public interface MessageLoader {

	/**
	 * Load the message to output StringBuffer.
	 * <p>ZK will scan the <code>MessageLoader</code> setting and invoke this
	 * method to load the system messages in the following two circumstances.
	 * <ol>
	 * <li>{@link Wpds#outLocaleJavaScript(javax.servlet.ServletRequest, javax.servlet.ServletResponse)} (defined in zul/lang/zk.wpd)</li>
	 * <li>{@link Clients#reloadMessages(java.util.Locale)}</li>
	 * </ol>
	 * For the first circumstances, current Execution does not have access to
	 * {@link Desktop} and {@link Page} instance. Hence, programmer must notice
	 * that they cannot use many of Execution's API.
	 * </p>
	 * 
	 * @param out StringBuffer to output
	 * @param exec current Execution
	 */
	public void load(StringBuffer out, Execution exec) throws IOException;
}
