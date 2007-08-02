/* HttpFacesContextFilter.java
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

import java.io.IOException;


import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.jboss.seam.contexts.Lifecycle;
import org.jboss.seam.core.Pages;
import org.zkoss.seam.jsf.ZKFacesContextImpl;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * You should not use this class directly.<br/>
 * The main purpose of this class is :
 * <ul>
 * <li>
 *  Initial Seam's Context
 * </li>
 * <li>
 *  Fire Seam's Page Event
 * </li> 
 * </ul> 
 * <br/>
 * Configuration in web.xml<br/>
 * <pre>
 * &lt;filter&gt;
 *     &lt;filter-name&gt;ZK FacesContext Filter&lt;/filter-name&gt;
 *     &lt;filter-class&gt;org.zkforge.seam.HttpFacesContextFilter&lt;/filter-class&gt;
 *     &lt;init-param&gt;
 *         &lt;param-name&gt;update-uri&lt;/param-name&gt;
 *         &lt;param-value&gt;/zkau&lt;/param-value&gt;
 *     &lt;/init-param&gt;
 * &lt;/filter&gt;
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;ZK FacesContext Filter&lt;/filter-name&gt;
 *     &lt;servlet-name&gt;zkLoader&lt;/servlet-name&gt;
 * &lt;/filter-mapping&gt; 
 * &lt;filter-mapping&gt;
 *     &lt;filter-name&gt;ZK FacesContext Filter&lt;/filter-name&gt;
 *     &lt;servlet-name&gt;auEngine&lt;/servlet-name&gt;
 * &lt;/filter-mapping&gt;  
 *   
 *   </pre>
 * 
 * @author Dennis.Chen
 *
 */
public class HttpFacesContextFilter implements Filter{

    
    public static String VIEWID_KEY = "org.zkoss.zkplus.seam.VIDK";
    private ServletContext servletContext;
    private String _updateURI = "/zkau";
    //static private ThreadLocal facesContext = new ThreadLocal();
    
    public void init(FilterConfig config) throws ServletException {
        servletContext = config.getServletContext();
        String uu = config.getInitParameter("update-uri");
        if(uu!=null){
            _updateURI = uu.trim();
        }
    }
    
    public void destroy() {
        servletContext = null;
    }
    

    private FacesContext initFacesContext(HttpServletRequest request, HttpServletResponse response) {
        
        String sp = request.getServletPath();
        boolean uiUpdate = false;
        if(sp!=null && sp.startsWith(_updateURI)){
            uiUpdate = true;
        }

        FacesContext context = new ZKFacesContextImpl(servletContext,request,response,uiUpdate);
                
        UIViewRoot view = new UIViewRoot();
        view.setViewId(findViewID(request,response));
        context.setViewRoot(view);
        
        return context;
    }
    
    
    private String findViewID(HttpServletRequest request, HttpServletResponse response){
        //find ViewId form desktop. if no desktop found, then use servletpath.
        final String dtid = request.getParameter("dtid");
        String viewId = null;
        if(dtid!=null){
            final Session sess = WebManager.getSession(servletContext,request);
            final WebApp wapp = sess.getWebApp();
            final WebAppCtrl wappc = (WebAppCtrl)wapp;
            Desktop desktop = wappc.getDesktopCache(sess).getDesktopIfAny(dtid);
            if(desktop!=null){
                viewId = (String)desktop.getRequestPath();
            }
        }
        if(viewId==null){
            viewId = request.getServletPath();
        }
        
        return viewId;
        
    }

    

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        if(needsInitialFacesContext((HttpServletRequest)request)){
            FacesContext context = initFacesContext((HttpServletRequest)request,(HttpServletResponse)response);
            //for call applyRequestParameterValues , (this will fire Page Event)
            //we must inital Page Scope, and set Phase to RESTORE_VIEW
            Lifecycle.resumePage();
            PhaseId prePhase = Lifecycle.getPhaseId();
            Lifecycle.setPhaseId(PhaseId.RESTORE_VIEW);
            Pages.instance().applyRequestParameterValues(context);
            Lifecycle.setPhaseId(prePhase);
            
            
            chain.doFilter(request,response);
            if(context instanceof ZKFacesContextImpl){
                ((ZKFacesContextImpl)context).release();
            }
        }else{
            chain.doFilter(request,response);
        }
        

        
    }

    /**
     * check if url doens't need faces context, for ex image or js resources.
     * @param request
     * @return
     */
    private boolean needsInitialFacesContext(HttpServletRequest request) {
        //String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();
        if(pathInfo!=null){
            if(pathInfo.startsWith("/web/")) return false;
        }
        return true;
    }

    
}
