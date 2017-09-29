package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zk.ui.Component;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

/**
 * @author bob peng
 */
public class B85_ZK_3732VM {

	private ListModelList<String> model;

	public ListModelList<String> getModel() {
		return model;
	}

	public void setModel(ListModelList<String> model) {
		this.model = model;
	}

	@Init
	public void initSetup() {
		model = new ListModelList();
		model.add("a");
		model.add("b");
		model.add("c");
	}

	@Command
	public void click(@ContextParam(ContextType.VIEW) Component view) {
		model.add("1");
		Component c = view.getFirstChild();
		if (c instanceof Listbox) {
			c.detach();
		}
		model.clear();
	}
}
