/* ZkAuthenticationProcessingFilter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 21 16:17:28     2006, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.Executions;

import org.zkoss.lang.Objects;

import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.acegisecurity.context.SecurityContextHolder;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>This implementation process zk specific popup login page. If send user login sucessfullly, an
 * "onLoginOK" event would be fired to the component that fired the event and caused the login 
 * processing. Since it will be tedious to register "onLoginOK" handler on every possible component, it is
 * better to register an onLoginOK event handler on the concerned page.</p>
 * <p>Applicable to Acegi Security version 1.0.3</p>
 * @see ZkAuthenticationEntryPoint
 * @see ShowWindowEventListener
 * @author Henri
 */
public class ZkAuthenticationProcessingFilter extends AuthenticationProcessingFilter {
	/** If end user login sucessfully, an ON_LOGIN_OK is fired. Register an associated event 
	 * listener on the page and operate per the success. */
	private static final String ON_LOGIN_OK = "onLoginOK";
	/*package*/ static final String CURRENT_EVENT = "org.zkoss.zkplus.acegi.CURRENT_EVENT";
	private boolean _resendZkEvent = false; //default to false

	/** Whether re-send the ZK event that caused poping the login window after authentication successfully.
	 * It is default to false.
	 */
    public void setSendZkEventAfterSuccessfulAuthentication(boolean b) {
        _resendZkEvent = b;
    }

    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
    	final Event evt = (Event) request.getAttribute(CURRENT_EVENT);
    	return Objects.equals(getFilterProcessesUrl(), request.getAttribute("j_loginurl")) && 
    		ZkAuthenticationEntryPoint.ON_ACEGILOGIN.equals(evt.getName());
    			//must check the event name otherwise authentication would be called twice. 
    			//1st the login model window's button click event
    			//2nd the resumed onAcegiLogin thread resume and "cleanup".
    			//we now check only on 2nd event
    }
    
	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
    throws IOException {
    	if (url == null) {
    		return; //skip
    	}
		final Event evt = (Event) ((HttpServletRequest)request).getSession().getAttribute(ZkEventExceptionFilter.EVENT);
		final Component comp = evt.getTarget();
		final String updateURI = comp.getDesktop().getUpdateURI(null);
    	if (url.indexOf(updateURI) >= 0) { //saved request
    		Events.postEvent(new Event(ON_LOGIN_OK, comp, null)); //post onLoginOK event
    		if (_resendZkEvent) {
				((HttpServletRequest)request).getSession().removeAttribute(ZkEventExceptionFilter.EVENT);
	    		Events.postEvent(evt);
	    	}
    		return;
    	}

		//must redirect
        if (url.startsWith("http://") || url.startsWith("https://")) {
            Executions.getCurrent().sendRedirect(url);
            return;
        }

		//other url, assume login fail
		if (!comp.isListenerAvailable(ZkAuthenticationEntryPoint.ON_ACEGILOGIN, true)) {
			final EventListener listener = new ShowWindowEventListener();
			comp.setAttribute(ZkAuthenticationEntryPoint.ON_ACEGILOGIN, listener);
			comp.addEventListener(ZkAuthenticationEntryPoint.ON_ACEGILOGIN, listener);
		}
		
    	Events.postEvent(new Event(ZkAuthenticationEntryPoint.ON_ACEGILOGIN, comp, url));
	}

	protected String obtainPassword(HttpServletRequest request) {
        return (String) request.getAttribute(ACEGI_SECURITY_FORM_PASSWORD_KEY);
    }

    protected String obtainUsername(HttpServletRequest request) {
        return (String) request.getAttribute(ACEGI_SECURITY_FORM_USERNAME_KEY);
    }
}
