package org.zkoss.jspdemo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zul.Window;

public class MyComposer implements Composer {
    public void doAfterCompose(Component comp) {    
        ((Window)comp).setTitle("My Title"); //do whatever initialization you want        
            //comp is Window since we will specify it to a window later            
    }    
}