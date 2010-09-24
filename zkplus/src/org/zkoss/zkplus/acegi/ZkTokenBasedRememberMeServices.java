/* ZkTokenBasedRememberMeServices.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 28 09:26:17     2006, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import org.acegisecurity.ui.rememberme.TokenBasedRememberMeServices;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>get rememberme from request's attribute rather than request's parameter.</p>
 * <p>Applicable to Acegi Security version 1.0.3</p>
 * @author Henri
 */
public class ZkTokenBasedRememberMeServices extends TokenBasedRememberMeServices {
    protected boolean rememberMeRequested(HttpServletRequest request, String param) {
    	if (isAlwaysRemember()) {
    		return true;
    	}
    	
    	if (param == null) {
    		return false;
    	}
    	
    	Boolean yesno = (Boolean) request.getAttribute(param);
    	return yesno != null && yesno.booleanValue();
    }
}
