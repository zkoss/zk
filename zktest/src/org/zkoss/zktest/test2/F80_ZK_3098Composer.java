package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

public class F80_ZK_3098Composer extends SelectorComposer<Component> {
	private ListModelList _model;
	@SuppressWarnings("unchecked")
	public ListModel getModel() {
		if (_model == null) {
			List l = new ArrayList();
			l.add("item 1");
			l.add("item 2");
			l.add("item 3");
			_model = new ListModelList(l);
		}
		return _model;
	}
	@Listen("onItemClick=#cbx")
	public void doSomething (Event event) {
		Clients.log("Click Item: " + event.getData());
	}
}