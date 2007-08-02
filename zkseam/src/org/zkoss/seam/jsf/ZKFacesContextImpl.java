/* ZKFacesContextImpl.java
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
package org.zkoss.seam.jsf;

import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.myfaces.context.servlet.ServletFacesContextImpl;
import org.zkoss.seam.OpenWindow;
/**
 * This class is for integrate ZK with Seam, You should not use this class directly.<br/>
 * The main purpose of this class is ,
 * <ol>
 * <li>
 * Keep zkLoading or uiUpdate information of current request. 
 * </li>
 * <li> 
 * Set self the current instance of FacesContext
 * </li>
 * </ol>
 * 
 * @author Dennis.Chen
 *
 */
public class ZKFacesContextImpl extends ServletFacesContextImpl{

    
    private Application _application;
    
    boolean _uiUpdate = false;
    
    OpenWindow _openWindow = null;
    
    public ZKFacesContextImpl(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse,boolean uiUpdate) {
        super(servletContext, servletRequest, servletResponse);
        super.setExternalContext(new ZKExternalContextImpl(servletContext, servletRequest, servletResponse));
        Application sapp = super.getApplication();
        _application = new ZKApplicationImpl(sapp);
        _application.setViewHandler(new ZKViewHandlerImpl(sapp.getViewHandler()));
        _uiUpdate = uiUpdate;
    }
    
    public Application getApplication()
    {
        return _application;
    }
    
    public void setExternalContext(ZKExternalContextImpl extContext)
    {
        super.setExternalContext(extContext);
        FacesContext.setCurrentInstance(this);
    }
    
    public void release()
    {
        super.release();
        _application = null;
    }
    
    public boolean isUiUpdate() {
        return _uiUpdate;
    }
    
    
    public OpenWindow getOpenWindow(){
        return _openWindow;
    }
    
    public void setOpenWindow(OpenWindow ow){
        _openWindow = ow;
    }
    
    
    
    public static boolean isCurrentZKFacesContext(){
        if(FacesContext.getCurrentInstance() instanceof ZKFacesContextImpl){
            return true;
        }
        return false;
    }
    
    public static boolean isCurrentZKUiUpdate(){
        FacesContext context = FacesContext.getCurrentInstance();
        if( context instanceof ZKFacesContextImpl){
            return ((ZKFacesContextImpl)context).isUiUpdate();
        }
        return false;
    }
    
    
    public static ZKFacesContextImpl getCurrentZKInstance(){
        FacesContext context = FacesContext.getCurrentInstance();
        if( context instanceof ZKFacesContextImpl){
            return ((ZKFacesContextImpl)context);
        }
        return null;
    }

}
