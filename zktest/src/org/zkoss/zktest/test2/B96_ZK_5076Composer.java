package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

public class B96_ZK_5076Composer extends SelectorComposer<Component> {
	@Wire("#lb")
	private Listbox listbox;

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		setListbox();
	}

	private ListModelList<String> getModel() {
		ListModelList<String> model = new ListModelList<String>();
		model.add("item 1");
		model.add("item 2");
		return model;
	}

	private void setListbox() {
		ListModelList<String> model = getModel();
		model.addToSelection(model.get(0));
		listbox.setModel(model);
	}

	@Listen(Events.ON_CLICK + "=#btn")
	public void btnClick(Event event) {
		setListbox();
	}

	@Listen(Events.ON_SELECT + "=#lb")
	public void selectListbox(Event event) {
		Clients.log("event has occured in listbox: " + listbox.getSelectedItem().getLabel());
	}
}