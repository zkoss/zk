/* OnCloseListener.java
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

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.OpenEvent;

/**package**/ abstract class OnCloseListener implements EventListener{

    public void onEvent(Event event) throws Exception {
        if(event instanceof OpenEvent){
            if(!((OpenEvent)event).isOpen()){
                onClose();
            }
        }
    }
    
    public abstract void onClose();

}
