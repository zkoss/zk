/* OpenWindow.java
 {{IS_NOTE
 Purpose:
 
 Description:
 
 History:
 Jul 28, 2007 11:03:34 AM , Created by Dennis Chen
 }}IS_NOTE

 Copyright (C) 2007 Potix Corporation. All Rights Reserved.

 {{IS_RIGHT
 This program is distributed under GPL Version 2.0 in the hope that
 it will be useful, but WITHOUT ANY WARRANTY.
 }}IS_RIGHT
 */
package org.zkoss.seam;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;
/**
 * A Utility to handler popup or new window when navigation,
 * This Calss is under development..
 * @author Dennis.Chen
 *
 */
public class OpenWindow {

    
    String url;

    int mode;

    String style;

    EventListener listener;

    public OpenWindow(String url, int mode, String style,
            EventListener listener) {
        this.url = url;
        this.mode = mode;
        this.style = style;
        this.listener = listener;
    }

    public EventListener getListener() {
        return listener;
    }

    public void setListener(EventListener listener) {
        this.listener = listener;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void open() {

        //TODO not implement yet
        
        Window outter = new Window();
        Page page = (Page) Executions.getCurrent().getDesktop().getPages()
                .iterator().next();

        Iframe inner = new Iframe();
        inner.setSrc(url);
        inner.setStyle(style);
        //Component inner = Executions.createComponents(url, outter, new Hashtable());
        
        
        outter.setPage(page);
        inner.setParent(outter);
        
        if (style != null) {
            outter.setStyle(style);
        }
        if (listener != null) {
            outter.addEventListener("onOpen", listener);
        }

        if (mode == NavigationUtil.MODAL) {
            try {
                outter.doModal();
            } catch (Exception x) {
                x.printStackTrace();
            }
        } else if (mode == NavigationUtil.POPUP) {
            outter.doPopup();
        } else {
            throw new UiException("Unsupported window mode.");
        }
    }

}