package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Button;
import org.zkoss.zul.Doublespinner;

public class B3330762 extends GenericForwardComposer {
	private Doublespinner ds;
	private Button btn;
	
	public void onClick$btn(Event evt){
		System.out.println(ds.getValue());
	}
}
