/* F103_ZK_5265_Composer.java

        Purpose:
                
        Description:
                
        History:
            Fri Nov 29 16:57:21 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;


import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.sys.DesktopCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zkmax.au.websocket.WebSocketServerPush;


public class F103_ZK_5265_Composer extends SelectorComposer<Component>{
    @Override
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        Desktop desktop = comp.getDesktop();

        if (!desktop.isServerPushEnabled()) {
            ((DesktopCtrl)desktop).enableServerPush(new WebSocketServerPush());
        }

        String initScript =
            "console.log('5. Composer initialized - Server-side script injection'); " +
            "window.composerInitialized = true; " +
            "window.composerTimestamp = new Date().toISOString();";

        Clients.evalJavaScript(initScript);
    }

    @Listen("onClick=#serverPushBtn")
    public void onClick(Event evt) {
        Clients.evalJavaScript("console.log('10. Websocket Server Push');");
    }

    @Listen("onCheck=#statusCheckbox")
    public void onCheckStatusChange(Event evt) {
        org.zkoss.zul.Checkbox checkbox = (org.zkoss.zul.Checkbox) evt.getTarget();
        boolean checked = checkbox.isChecked();

        String logMessage = String.format(
            "console.log('6. Checkbox status changed to: %s');",
            checked ? "checked" : "unchecked"
        );
        Clients.evalJavaScript(logMessage);
    }
}