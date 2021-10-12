package org.zkoss.zktest.test2;


import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;

public class B86_ZK_4016Listbox extends HtmlMacroComponent {
	@Listen("onCreate = #box")
	public void create(Event e) {
		Clients.log(this + "," + e.getName());
	}
}