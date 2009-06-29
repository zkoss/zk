/* Updatable.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Jul 21 18:50:03     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.ext;

/**
 * Implemented by the object returned by {@link org.zkoss.zk.ui.sys.ComponentCtrl#getExtraCtrl}
 * if a component supports special updates (other than async-update).
 *
 * <h2>Supported special updates</h2>
 * <h3>File upload</h3>
 * <ol>
 * <li>Component uses to use inner-frame or other mechanism to submit a file
 * to {@link org.zkoss.zk.au.http.DHtmlUpdateServlet}'s /upload.
 * <li>DHtmlUpdateServlet than store the result in desktop's attribute,
 * and ask client to do a standard async-update called updateResult.
 * <li>When client sends the updateResult request to the server, server invokes
 * {@link #setResult} to put the upload result to the component.
 * </ol>
 *
 * @author tomyeh
 */
public interface Updatable {
	/** Sets the result when it is updated from the client successfully.
	 */
	public void setProgress(String fid, long readSize, long totalSize);
	public void addMedia(Object obj);
}
