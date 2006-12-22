/* ZkAuthenticationEntryPoint.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 21 16:11:38     2006, Created by Henri Chen
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.Component;

import org.acegisecurity.AuthenticationException;

/**
 * Used by {@link ZkExceptionTranslationHandler} to commence an authentication
 * scheme.
 *
 * @author Henri Chen
 */
public interface ZkAuthenticationEntryPoint {
    /**
     * Commences an authentication scheme. Return true would cause ZkExceptionTranslationHandler to repost
     * this event.
     *
     * @param comp the component that cause this invocation.
     * @param evt the event that cause this invocation.
     * @param authException the exception occured within the event of the component.
     *
     * @return true if end user press "OK", false if end user press "CANCEL".
     */
    public boolean commence(Component comp, Event evt, AuthenticationException authException);
}
