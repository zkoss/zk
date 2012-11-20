package org.zkoss.zktest.test2;
import java.util.Date;

import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zkmax.ui.select.annotation.Subscribe;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

public class B65_ZK_1438_Inner extends SelectorComposer {
	
	@Wire
	Window win;
	
	@Listen("onCreate = #win")
	public void onCreate(){
		win.setTitle(""+new java.util.Date());
	}
	
	@Subscribe("myqueue")
	public void onQueuEvent(Event queueEvent){
		Listbox listbox = (Listbox)Path.getComponent("/listbox1");
		listbox.appendItem("L1 ["+this+"] get event: "+queueEvent.getName()+"@"+new Date().getTime(), null);
	}
	
	@Subscribe(value="myqueue")
	public void onQueuEvent2(Event queueEvent){
		Listbox listbox = (Listbox)Path.getComponent("/listbox2");
		listbox.appendItem("L2 ["+this+"] get event: "+queueEvent.getName()+"@"+new Date().getTime(), null);
	}
}