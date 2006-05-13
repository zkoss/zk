/* Selectable.java

{{IS_NOTE
	$Id: Selectable.java,v 1.1 2006/03/31 03:20:44 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Thu Jun 16 18:14:14     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.ext;

import java.util.Set;

import com.potix.zk.ui.UiException;

/**
 * Used to decorate a {@link com.potix.zk.ui.Component} object that
 * it allows users to change the selection from the client.
 *
 * <p>{@link com.potix.zk.ui.event.SelectEvent} will be sent after {@link #selectItemsByClient}
 * is called
 * to notify application developers that it is called by user
 * (rather than by codes).
 * 
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.1 $ $Date: 2006/03/31 03:20:44 $
 * @see com.potix.zk.ui.event.SelectEvent
 */
public interface Selectable {
	/** Set the selection to a set of specified items.
	 * <p>This method is designed to be used by engine.
	 * Don't invoke it directly. Otherwise, the client and server
	 * might mismatch.
	 */
	public void selectItemsByClient(Set selectedItems);
}
