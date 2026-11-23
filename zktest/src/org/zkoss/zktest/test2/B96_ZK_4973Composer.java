package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;

public class B96_ZK_4973Composer extends GenericForwardComposer {

    private Tabbox tabbox;
    private Tab firstTab;

    public void doAfterCompose(Component comp) throws Exception {
        super.doAfterCompose(comp);
        tabbox = (Tabbox) comp.getFellow("myTabbox");
        firstTab = (Tab) comp.getFellow("myTab1");
    }

    public void onClick$btnYellow(Event e){
        firstTab.setSclass("yellow");
    }

    public void onClick$btnYellowRemove(Event e){
        firstTab.setSclass("");
    }

    public void onClick$btnInvalidate(Event e){
        tabbox.getSelectedTab().invalidate();
    }

    public void onClick$btnInvalidate2(Event e){
        tabbox.getSelectedTab().invalidate();
    }
}
