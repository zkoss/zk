/* SimpleAuthenticationEntryPoint.java

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

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.Textbox;

import org.zkoss.zul.Window;

import org.acegisecurity.AuthenticationException;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.ui.webapp.AuthenticationProcessingFilter;
import org.acegisecurity.context.SecurityContextHolder;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>The default {@link ZkAuthenticationEntryPoint} implementation that would show
 * a modal window for login. Therefore it expects:</p>
 * <ul>
 * <li>The login page must be a zul defined page (*.zul).</li>
 * <li>The login page must enclosed with a window component so it can be doModal().</li>
 * </ul>
 *
 * @author Henri
 */
public class SimpleAuthenticationEntryPoint implements ZkAuthenticationEntryPoint, InitializingBean {
	public static final String RESULT = "org.zkoss.zkplus.acegi.RESULT";
	
    private String _loginFormUrl;
    
    public void afterPropertiesSet() throws Exception {
        Assert.hasLength(_loginFormUrl, "loginFormUrl must be specified");
    }

    public void setLoginFormUrl(String url) {
    	_loginFormUrl = url;
    }
    
    /**
     * <p>This implmentation will pass the component, event, and the authentication exception that cause this login
     * via the args map. Therefore, you can show some context information on your login page by using the passed 
     * in arguments.<p>
     * <ul>
     * <li>component: used to reference the component.</li>
     * <li>event: used to reference the event.</li>
     * <li>exception: used to reference the authentication exception.</li>
     * </ul>
     */
    public boolean commence(Component comp, Event evt, AuthenticationException reason) {
    	final Execution exec = Executions.getCurrent();
    	final Page page = comp.getPage();
    	final Map args = new HashMap();
    	args.put("component", comp);
    	args.put("event", evt);
    	args.put("exception", reason);
    	
    	final Component modalwin = exec.createComponents(_loginFormUrl, null, args);
    	if (!(modalwin instanceof Window)) {
    		throw UiException.Aide.wrap(reason, "The login page must enclosed with a Window component. Check definition of the \"loginFormUrl\":"+_loginFormUrl);
    	}
    	modalwin.setPage(page);
    	try {
	    	((Window)modalwin).doModal();
    	} catch(java.lang.InterruptedException ex) {
    		//ignore
    	}

    	final Boolean result = (Boolean) modalwin.getAttribute(RESULT);
    	return result != null && result.booleanValue();
    }
    
    /**
     * <p>Utility to collect the username and password, and setup the Authentication Token. This is a
     * ZK's counter part of the Acegi's AuthenticationProcessingFilter</p>
     * @param modalwin the modal login window.
     */
    public static void login(Window modalwin) {
    	Textbox tbxuser = (Textbox)modalwin.getFellow(AuthenticationProcessingFilter.ACEGI_SECURITY_FORM_USERNAME_KEY);
		String username = tbxuser.getValue();

		Textbox tbxpass = (Textbox)modalwin.getFellow(AuthenticationProcessingFilter.ACEGI_SECURITY_FORM_PASSWORD_KEY);
        String password = tbxpass.getValue();
        
        if (username == null) {
            username = "";
        }

        if (password == null) {
            password = "";
        }

        UsernamePasswordAuthenticationToken authResult = 
        	new UsernamePasswordAuthenticationToken(username, password);

        // Place the last username attempted into HttpSession for views
        modalwin.getDesktop().getSession().setAttribute(AuthenticationProcessingFilter.ACEGI_SECURITY_LAST_USERNAME_KEY, username);
		SecurityContextHolder.getContext().setAuthentication(authResult);
		
		//put RESULT into modal window
//		modalwin.setAttribute(RESULT, Boolean.TRUE);
		
		//close the modal window
		modalwin.detach();
	}
    	
}
