/* B104_ZK_5608Composer.java

        Purpose:
                
        Description:
                
        History:
                Wed Jan 21 11:01:25 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Messagebox;

public class B104_ZK_5608Composer extends SelectorComposer {
    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
    }

    public void onClick$btn(Event e) throws InterruptedException{
        Messagebox.show("Hi btn");
    }
}
