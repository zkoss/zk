/* ZKExternalContextImpl.java
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

import java.io.IOException;

import javax.faces.FacesException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;
import org.apache.myfaces.context.servlet.ServletExternalContextImpl;
import org.zkoss.seam.ConversationUtil;
import org.zkoss.seam.OpenWindow;
import org.zkoss.zk.ui.Executions;
/**
 * This class is for integrate ZK with Seam, You should not use this class directly.<br/>
 * The main purpose of this class is :
 * <ul>
 * <li>
 *  Becaus Seam will call redirect() method when a navigation result must be redirected.
 *  When current request is a uiUpdate then use ZK's redirect mechanism to redirect it, other wise just send redirect.
 * </li>
 * </ul> 
 * 
 * @author Dennis.Chen
 *
 */
/**package**/ class ZKExternalContextImpl extends ServletExternalContextImpl{

    ServletContext _servletContext;
    ServletRequest _servletRequest;
    ServletResponse _servletResponse;
    
    
    
    public ZKExternalContextImpl(ServletContext servletContext, ServletRequest servletRequest, ServletResponse servletResponse) {
        super(servletContext, servletRequest, servletResponse);
        this._servletContext = servletContext;
        this._servletRequest = servletRequest;
        this._servletResponse = servletResponse;
    }

    @Override
    public void redirect(String url) throws IOException
    {
        url = ConversationUtil.appendLongRunningConversation(url);
        if(ZKFacesContextImpl.isCurrentZKUiUpdate()){
            //TODO How to Keep MESSAGE?? ( temp long conversation?)
            OpenWindow ow = ZKFacesContextImpl.getCurrentZKInstance().getOpenWindow();
            if(ow!=null){
                ow.setUrl(url);
                ow.open();
            }else{
                Executions.getCurrent().sendRedirect(url);
            }
        }else{
            if (_servletResponse instanceof HttpServletResponse)
            {
                ((HttpServletResponse)_servletResponse).sendRedirect(url);
                //FacesContext.getCurrentInstance().responseComplete();            
            }
            else
            {
                throw new IllegalArgumentException("Only HttpServletResponse supported");
            }
        }
    }
    
    public void dispatch(String requestURI) throws IOException, FacesException
    {
        throw new NotImplementedException("dispatch has not supported yet");
    }

    
    
    
    
    
}
