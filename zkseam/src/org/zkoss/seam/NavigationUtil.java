/* NavigationUtil.java
 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jul 25, 2007 10:03:38 AM , Created by Dennis Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.seam;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;

import org.zkoss.seam.jsf.ZKFacesContextImpl;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.EventListener;
/**
 * This Class helps developers do JSF's page navigation in ZK  
 * @author Dennis.Chen
 *
 */
public class NavigationUtil {

    private static final int REDIRECT = 0;

    /**package**/ static final int MODAL = 1;

    // public static final int OVERLAPPED = 2;
    /**package**/ static final int POPUP = 3;

    /**
     * navigate current ZK Execution ot uri,
     * if uri is null, then do nothing. Other wish , the navigation will depends on navigation rule wich specified by JSF
     * @param uri
     */
    static public void navigate(String uri) {
        navigateTo(uri, REDIRECT, null);
    }

    /**package**/static void navigate(String uri, int mode, EventListener listener) {
        navigateTo(uri, mode, listener);
    }
    /**package**/static void navigate(Void uri, int mode, EventListener listener) {
        navigateTo(null, mode, listener);
    }

    /**package**/ static void navigateTo(String uri, int mode, EventListener listener) {
        String style = null;
        String url = null;
        // TODO popup, or some else,
        try {
            if (uri == null) {
                return;
            } else if (isOutcomeViewId(uri)) {
                url = ConversationUtil.appendLongRunningConversation(uri);

                if (mode == REDIRECT) {
                    Executions.getCurrent().sendRedirect(url);
                } else {
                    new OpenWindow(url, mode, style, listener).open();
                }
            } else {
                FacesContext facesContext = FacesContext.getCurrentInstance();
                if (facesContext == null) {
                    throw new RuntimeException("Can't find FacesContext");
                }
                
                
                if (mode != REDIRECT) {
                    OpenWindow ow = new OpenWindow(null, mode, style, listener);
                    ZKFacesContextImpl.getCurrentZKInstance().setOpenWindow(ow);
                }
                
                
                
                NavigationHandler handler = null;

                handler = facesContext.getApplication().getNavigationHandler();

                // handler = new SeamNavigationHandler(handler);

                handler.handleNavigation(facesContext, null, uri);

            }
        } catch (RuntimeException x) {
            x.printStackTrace();
            throw x;
        }

    }

    private static boolean isOutcomeViewId(String outcome) {
        return outcome != null && outcome.startsWith("/");
    }

    

}
