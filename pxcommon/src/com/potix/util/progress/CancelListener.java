/* CancelListener.java

{{IS_NOTE
	$Id: CancelListener.java,v 1.2 2006/02/27 03:42:05 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Wed May  4 10:03:27     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.util.progress;

/**
 * Used with {@link ProgressCallbacks#addCancelListener} to listen when
 * a long operation is about to aborting.
 *
 * <p>Typical use:
 * <pre><code>ProgressCallbacks.addCancelListener(listener);
 *... //long operation
 *ProgressCallbacks.removeCancelListener(listener);</code></pre>
 *
 * <p>You don't need to put removeCancelListener in a finally block 
 * if you won't continue the execution once an exception occurs, because
 * listener is added a thread-local storage that will be cleaned up
 * once ProgressCallbacks.set(null) is called.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.2 $ $Date: 2006/02/27 03:42:05 $
 */
public interface CancelListener {
	/** Called when the operation is aborting.
	 * Once this method is called by progress-callback, the listener is also
	 * removed from {@link ProgressCallbacks}.
	 * In other words, it is called only once.
	 */
	public void onCancel() throws Exception;
}
