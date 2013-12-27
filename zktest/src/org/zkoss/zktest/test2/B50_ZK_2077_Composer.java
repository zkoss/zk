package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.ext.Selectable;

public class B50_ZK_2077_Composer extends GenericForwardComposer {

	Listbox lb;

	public ListModel getModel() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			list.add("Name " + i);
		}
		return new SimpleListModel(list);
	}

	public void onOK() throws InterruptedException {
		Listitem item = lb.getSelectedItem();
		Messagebox.show(item == null ? "null" : item.getLabel());
	}

}
