/* GenericInitiator.java

	Purpose:
		
	Description:
		
	History:
		Fri Oct  1 22:06:13 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.util;

import java.util.Map;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;

/**
 * A skeletal implementation that provides does-nothing for all methods,
 * such that the derving class needs only to implement the required method.
 * @author tomyeh
 * @since 5.0.5
 */
abstract public class GenericInitiator implements Initiator, InitiatorExt {
	public void doInit(Page page, Map args) throws Exception {
	}
	public void doAfterCompose(Page page, Component[] comps) throws Exception {
		doAfterCompose(page);
	}
	/** It is used only for backward compatibility. Don't use it.
	 */
	public void doAfterCompose(Page page) {
	}
	/** Does nothing but returns false so the exception will be thrown.
	 */
	public boolean doCatch(Throwable ex) throws Exception {
		return false;
	}
	public void doFinally() throws Exception {
	}
}
