/* InitiatorExt.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 15 11:40:42     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;

/**
 * An extra interface that can be implemented with {@link Initiator} to
 * have the better control.
 *
 * @author tomyeh
 * @see Initiator
 * @since 3.0.0
 */
public interface InitiatorExt {
	/** Called after all components are created (aka., composed),
	 * and before any event is processed.
	 *
	 * <p>Note: if this interface is implemented with {@link Initiator},
	 * {@link Initiator#doAfterCompose} won't be called.
	 * Rather, this method is called.
	 *
	 * <p>It won't be called if an un-caught exception occurs when creating
	 * components.
	 *
	 * @param page the page that new components are attached to. It is the same
	 * as {@link Initiator#doInit}'s page argument.
	 * @param comps the root components being created (never null, but the length
	 * might be zero).
	 * Note: It is not necessary the same as {@link Page#getRoots}, since
	 * this method might be called thru {@link org.zkoss.zk.ui.Executions#createComponents}.
	 * @since 3.0.0
	 */
	public void doAfterCompose(Page page, Component[] comps) throws Exception;
}
