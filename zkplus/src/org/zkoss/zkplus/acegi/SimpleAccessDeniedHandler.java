/* SimpleAccessDeniedHandler.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Dec 21 16:50:11     2006, Created by Henri
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zkplus.acegi;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;

import org.zkoss.zul.Window;

import org.acegisecurity.AccessDeniedException;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>The default {@link ZkAccessDeniedHandler} implementation that would show
 * a modal window for denied message. Therefore it expects:</p>
 * <ul>
 * <li>The error page must be a zul defined page (*.zul).</li>
 * <li>The error page must enclosed with a window component so it can be doModal().</li>
 * </ul>
 * 
 * @author Henri
 */
public class SimpleAccessDeniedHandler implements ZkAccessDeniedHandler {
	private String _errorPage;
	
	public void setErrorPage(String url) {
		_errorPage = url;
	}
	
    /**
     * <p>This implmentation will pass the component, event, and the accessDenied exception that cause this error
     * via the args map. Therefore, you can show some context information on your error page by using the passed 
     * in arguments.<p>
     * <ul>
     * <li>component: used to reference the component.</li>
     * <li>event: used to reference the event.</li>
     * <li>exception: used to reference the accessDenied exception.</li>
     * </ul>
     */
	public void handle(Component comp, Event evt, AccessDeniedException accessDeniedException) {
    	final Execution exec = Executions.getCurrent();
    	final Page page = comp.getPage();
    	final Map args = new HashMap();
    	args.put("component", comp);
    	args.put("event", evt);
    	args.put("exception", accessDeniedException);
    	
    	final Component modalwin = exec.createComponents(_errorPage, null, args);
    	if (!(modalwin instanceof Window)) {
    		throw UiException.Aide.wrap(accessDeniedException, "The error page must enclosed with a Window component. Check definition of the \"errorPage\":"+_errorPage);
    	}
    	modalwin.setPage(page);
    	try {
    		((Window)modalwin).doModal();
    	} catch(java.lang.InterruptedException ex) {
    		//ignore
    	}
    	
    	modalwin.detach();
    }
}
