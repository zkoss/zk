package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zk.ui.ext.*;
import org.zkoss.zk.au.*;
import org.zkoss.zk.au.out.*;
import org.zkoss.zul.*;

public class B60_ZK_1176_TestComposer extends GenericForwardComposer {

	private Button btn1, btn2;
	private Toolbarbutton tbBtn1, tbBtn2;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		
		EventListener<Event> listener1 = new EventListener<Event>() {
			public void onEvent(final Event event) throws Exception {
				Thread.sleep(1000);
				// Somewhere during the business logic, need to stop the re-enabling 
				// caused by self-autodisable onResponse
				((Button) event.getTarget()).setDisabled(true);
				Thread.sleep(1000);
			}
		};
		
		EventListener<Event> listener2 = new EventListener<Event>() {
			public void onEvent(final Event event) throws Exception {
				Thread.sleep(2000);
				// onClick finishes normally after simulated processing time
			}
		};

		btn1.addEventListener(Events.ON_CLICK, listener1);
		btn2.addEventListener(Events.ON_CLICK, listener2);
		
		tbBtn1.addEventListener(Events.ON_CLICK, listener1);
		tbBtn2.addEventListener(Events.ON_CLICK, listener2);
	}
}