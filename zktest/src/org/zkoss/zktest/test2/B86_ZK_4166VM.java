package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.SimpleListModel;

public class B86_ZK_4166VM {
	private List beansList = new ArrayList();

	public List getBeansLeak() {
		return beansList;
	}

	public void setBeansLeak(List beans) {
		this.beansList = beans;
	}

	@NotifyChange({ "beansLeak"})
	@Command
	public void refreshCmd(@BindingParam("lb") Listbox comp) throws Exception{
		beansList = new ArrayList();
		beansList.add("one");
		beansList.add("two");
		beansList.add("three");
		beansList.add("four");
		beansList.add("five");
		showSize(comp);
	}

	public void showSize(Listbox lb) throws Exception {
		HashMap attribute = (HashMap)lb.getAttribute("$RENDERED_COMPONENTS$");
		if(attribute != null)
			Clients.log("Map size: " + attribute.size());
	}
}
