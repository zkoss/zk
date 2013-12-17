package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Window;

public class B70_ZK_1829 extends SelectorComposer<Window> {
	
     public void doAfterCompose(Window win) throws Exception {
             super.doAfterCompose(win);

     		Selectors.find(win, "label");
     		Selectors.find(win, "#myId");
     		Selectors.find(win, "[label='zk']");
     		Selectors.find(win, "window > caption");
     		Selectors.find(win, "button, toolbarbutton");
     		Selectors.find(win, "window#win > label[value='zk']");
     		Selectors.find(win, "#button > .testSimple[message*='ndc[1]'][type='radio'].abv");
     		Selectors.find(win, ".aaa[a.b.c='sdf']");
     }

    @Listen("onOpen = #button.testSimple[message*='ndc[1]'][type='radio'].abv; onClick = button[label='Clear']")
    public void doTest() {
        //Do nothing.
    }
    
    @Listen("onClick = #myGrid > rows > row; onOpen = button[label='Submit']; onOK = textbox#password")
    public void doTest2() {
        //Do nothing.
    }
}
