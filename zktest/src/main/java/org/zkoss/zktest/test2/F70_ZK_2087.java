package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Messagebox.Button;

public class F70_ZK_2087 extends SelectorComposer<Component> {
	
	@Listen("onClick = #btn")
    public void show(){
		java.util.Map<String, String> args = new java.util.HashMap<String, String>();
		args.put("sclass", "myMessagebox");
		Messagebox.show(
		    "Do you want to return without saving?",
		    "Confirmation",
		    new Button[]{Button.OK, Button.ABORT, Button.CANCEL},
		    new String[]{"Save and Return", "Abort and Return", "Cancel"},
		    null,
		    null,
		    null,
		    args
		);
    }
}
