/* ProgressCallback.java

{{IS_NOTE
	$Id: ProgressCallback.java,v 1.3 2006/02/27 03:42:06 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Fri Sep 19 17:12:31     2003, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2003 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.progress;

/**
 * The callback to denote the progress.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.3 $ $Date: 2006/02/27 03:42:06 $
 */
public interface ProgressCallback {
	/** Notifies the caller that something is progressing.
	 */
	public void onProgress() throws Exception;
	/** Called by an operation to show an message.
	 * It is optinal. If not implemented, simply does nothing.
	 */
	public void onMessage(String msg) throws Exception;
}
