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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.acegisecurity.AccessDeniedException;
import org.acegisecurity.ui.AccessDeniedHandler;
import org.acegisecurity.ui.AccessDeniedHandlerImpl;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

/**
 * Used by ExceptionTranslationFilter to handle an
 * <code>AccessDeniedException</code>. This handler will popup the specified error page so it
 * must be a Window that can be doModel.
 * <p>Applicable to Acegi Security version 1.0.3</p>
 * @author Henri
 * @deprecated As of release 7.0.0
 */
public class ZkAccessDeniedHandler implements AccessDeniedHandler {
	private static final String ON_ACCESSDENIED = "onAccessDenied";
	private String _errorPage;

	public void setErrorPage(String url) {
		_errorPage = url;
	}

	public String getErrorPage() {
		return _errorPage;
	}

	public void handle(ServletRequest request, ServletResponse response, AccessDeniedException accessDeniedException)
			throws IOException, ServletException {

		// Put exception into request scope (perhaps of use to a view)
		((HttpServletRequest) request).setAttribute(AccessDeniedHandlerImpl.ACEGI_SECURITY_ACCESS_DENIED_EXCEPTION_KEY,
				accessDeniedException);

		final Component comp = (Component) request.getAttribute(ZkEventExceptionFilter.COMPONENT);
		if (!comp.isListenerAvailable(ON_ACCESSDENIED, true)) {
			final EventListener<Event> listener = new ShowWindowEventListener<Event>();
			comp.setAttribute(ON_ACCESSDENIED, listener);
			comp.addEventListener(ON_ACCESSDENIED, listener);
		}
		final String url = getErrorPage();
		Events.postEvent(new Event(ON_ACCESSDENIED, comp, url != null ? url : "~./accessDenied.zul"));
	}
}
