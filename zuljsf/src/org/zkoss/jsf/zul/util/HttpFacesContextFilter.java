/* HttpFacesContextFilter.java
 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 	Aug. 22, 2007 11:10:32 AM , Created by Dennis Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.jsf.zul.util;

import java.io.IOException;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.http.WebManager;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * This class filter the zkLoader and au request, and initial a FacesContext for this request,
 * so, You can access FacesContext by FacesContext.getCurrentInstance();
 * <br/> Configuration in web.xml<br/>
 * 
 * <pre>
 * &lt;filter&gt;
 *     &lt;filter-name&gt;ZK FacesContext Filter&lt;/filter-name&gt;
 *     &lt;filter-class&gt;org.zkoss.jsf.zul.util.HttpFacesContextFilter&lt;/filter-class&gt;
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
 * </pre>
 * 
 * @author Dennis.Chen
 * 
 */
public class HttpFacesContextFilter implements Filter {

	public static String VIEWID_KEY = "org.zkoss.jsf.zul.VIDK";

	private ServletContext servletContext;

	private String _updateURI = "/zkau";

	// static private ThreadLocal facesContext = new ThreadLocal();

	public void init(FilterConfig config) throws ServletException {
		servletContext = config.getServletContext();
		String uu = config.getInitParameter("update-uri");
		if (uu != null) {
			_updateURI = uu.trim();
		}
	}

	public void destroy() {
		servletContext = null;
	}

	private String findViewID(HttpServletRequest request,
			HttpServletResponse response) {
		// find ViewId form desktop. if no desktop found, then use servletpath.
		final String dtid = request.getParameter("dtid");
		String viewId = null;
		if (dtid != null) {
			final Session sess = WebManager.getSession(servletContext, request);
			final WebApp wapp = sess.getWebApp();
			final WebAppCtrl wappc = (WebAppCtrl) wapp;
			Desktop desktop = wappc.getDesktopCache(sess).getDesktopIfAny(dtid);
			if (desktop != null) {
				viewId = (String) desktop.getRequestPath();
			}
		}
		if (viewId == null) {
			viewId = request.getServletPath();
		}

		return viewId;

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		if (needsInitialFacesContext((HttpServletRequest) request)) {
			initFacesContext((HttpServletRequest) request,
					(HttpServletResponse) response);
			chain.doFilter(request, response);
		} else {
			chain.doFilter(request, response);
		}

	}

	/**
	 * check if url doens't need faces context, for ex image or js resources.
	 * 
	 * @param request
	 * @return
	 */
	private boolean needsInitialFacesContext(HttpServletRequest request) {
		// String servletPath = request.getServletPath();
		String pathInfo = request.getPathInfo();
		if (pathInfo != null) {
			if (pathInfo.startsWith("/web/"))
				return false;
		}
		return true;
	}

	private abstract static class MyFacesContext extends FacesContext {
		public MyFacesContext(){
			
		}
		protected static void setFacesContextAsCurrentInstance(
				FacesContext facesContext) {
			FacesContext.setCurrentInstance(facesContext);
			
		}
	}

	private FacesContext initFacesContext(HttpServletRequest request,
			HttpServletResponse response) {

		FacesContext facesContext = FacesContext.getCurrentInstance();
		//already exist.
		if (facesContext != null)
			return facesContext;

		FacesContextFactory contextFactory = (FacesContextFactory) FactoryFinder.getFactory(FactoryFinder.FACES_CONTEXT_FACTORY);
		LifecycleFactory lifecycleFactory = (LifecycleFactory) FactoryFinder.getFactory(FactoryFinder.LIFECYCLE_FACTORY);
		Lifecycle lifecycle = lifecycleFactory.getLifecycle(LifecycleFactory.DEFAULT_LIFECYCLE);

		facesContext = contextFactory.getFacesContext(servletContext, request,
				response, lifecycle);

		
		
		// set to current instance.
		MyFacesContext.setFacesContextAsCurrentInstance(facesContext);

		UIViewRoot view = facesContext.getApplication().getViewHandler()
				.createView(facesContext, findViewID(request, response));
		facesContext.setViewRoot(view);

		return facesContext;
	}

}
