/* ZKViewHandlerImpl.java
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
import java.util.Locale;

import javax.faces.FacesException;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
/**
 * This class is for integrate ZK with Seam, You should not use this class directly.<br/>
 * The main purpose of this class is 
 * <ol>
 * <li>
 * Override getActionURL mehotd, because Seam call this method to decide navigate url.
 * the default result will append the servlet path before the url. So I must avoid that when 
 * current request is a ZK's uiUpdate request.
 * </li>
 * <ol>
 * 
 * @author Dennis.Chen
 *
 */
/**package**/ class ZKViewHandlerImpl extends ViewHandler {
    private ViewHandler viewHandler;

    public ZKViewHandlerImpl(ViewHandler viewHandler) {
        this.viewHandler = viewHandler;
    }

    @Override
    public Locale calculateLocale(FacesContext facesContext) {
        return viewHandler.calculateLocale(facesContext);
    }

    @Override
    public String calculateRenderKitId(FacesContext ctx) {
        return viewHandler.calculateRenderKitId(ctx);
    }

    @Override
    public UIViewRoot createView(FacesContext ctx, String viewId) {
        return viewHandler.createView(ctx, viewId);
    }
    
    @Override
    public String getActionURL(FacesContext ctx, String viewId) {
        if(ZKFacesContextImpl.isCurrentZKUiUpdate()){
            return viewId;
        }else{
            return viewHandler.getActionURL(ctx, viewId);
        }
    }

    @Override
    public String getResourceURL(FacesContext ctx, String path) {
        return viewHandler.getResourceURL(ctx, path);
    }

    @Override
    public void renderView(FacesContext ctx, UIViewRoot viewRoot)
            throws IOException, FacesException {
        viewHandler.renderView(ctx, viewRoot);
    }

    @Override
    public UIViewRoot restoreView(FacesContext ctx, String viewId) {
        return viewHandler.restoreView(ctx, viewId);
    }

    @Override
    public void writeState(FacesContext ctx) throws IOException {
        viewHandler.writeState(ctx);
    }
}
