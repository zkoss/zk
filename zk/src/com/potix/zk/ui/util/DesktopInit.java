/* DesktopInit.java

{{IS_NOTE
	$Id: DesktopInit.java,v 1.1 2006/03/30 10:55:56 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Mar 30 18:28:12     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.util;

import com.potix.zk.ui.Desktop;

/**
 * Used to initialize a desktop when it is created.
 *
 * <p>How this interface is used.
 * <ol>
 * <li>First, you specify a class that implements this interface
 * in WEB-INF/zk.xml as a listener.
 * </li>
 * <li>Then, even time ZK loader creates a new desktop, an instnace of
 * the specified class is instantiated and {@link #init} is called.</li>
 * </ol>
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1 $ $Date: 2006/03/30 10:55:56 $
 */
public interface DesktopInit {
	/** Called when a desktop is created and initialized.
	 */
	public void init(Desktop desktop);
}
