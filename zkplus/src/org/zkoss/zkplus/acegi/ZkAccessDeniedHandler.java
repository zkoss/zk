/* ZkAccessDeniedHandler.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 21 16:45:26     2006, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;

import org.acegisecurity.AccessDeniedException;

/**
 * Used by {@link ZkExceptionTranslationHandler} to handle an
 * <code>AccessDeniedException</code>.
 * 
 * @author Henri
 */
public interface ZkAccessDeniedHandler {
    /**
     * Handles an access denied failure.
     *
     * @param comp the component that cause this invocation.
     * @param evt the event that cause this invocation.
     * @param accessDeniedException the exception occured within the event of the component.
     */
    public void handle(Component comp, Event evt, AccessDeniedException accessDeniedException);
}
