/* F103_ZK_5265_VM.java

        Purpose:
                
        Description:
                
        History:
                Tue Dec 02 15:32:54 CST 2025, Created by peakerlee

Copyright (C) 2025 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;

public class F103_ZK_5265_VM {
    private String bindingValue = "Type here to trigger binding script";

    @Command
    @NotifyChange("bindingValue")
    public void onBindingChange() {
        String script = String.format("console.log('9. Data binding script - Value changed to: %s');", bindingValue);
        Clients.evalJavaScript(script);
    }

    public String getBindingValue() {
        return bindingValue;
    }

    public void setBindingValue(String bindingValue) {
        this.bindingValue = bindingValue;
    }
}
