/* B85_ZK_3843Listbox.java

        Purpose:
                
        Description:
                
        History:
                Wed Jun 06 12:27:50 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

public class B85_ZK_3843Listbox extends HtmlMacroComponent {

    @Listen ("onClick = listitem")
    public void click() {
        Clients.showNotification("click a macro");
    }
}