package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.ConventionWires;
import org.zkoss.zul.Include;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Toolbarbutton;

public class B60_ZK_1160_TabController extends BindComposer<Component> {
	private static final long serialVersionUID = -4450762302282471893L;

	private Tabbox tabMain;
	private Toolbarbutton btnPerson;
	private List<String> persons;

	public B60_ZK_1160_TabController() {
		super();
		this.persons = new ArrayList<String>();
		this.persons.add("Mike");
		this.persons.add("Jonny");
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		ConventionWires.wireVariables(comp, this);
		btnPerson.addEventListener(Events.ON_CLICK, new OnClickMenuIten());
	}

	public void addTab() {
		Tab tab = new Tab();
		tab.setClosable(true);
		tab.setSelected(true);
		tab.setLabel("person");
		Include inc = new Include("B60-ZK-1160_Person.zul");
		inc.setProgressing(true);
		inc.setWidth("100%");
		inc.setHeight("100%");
		Tabpanel tabpan = new Tabpanel();
		tabpan.appendChild(inc);
		tabMain.getTabs().appendChild(tab);
		tabMain.getTabpanels().appendChild(tabpan);
	}

	public List<String> getPersons() {
		return persons;
	}

	private class OnClickMenuIten implements EventListener<Event> {
		public void onEvent(Event event) throws Exception {
			addTab();
		}
	}
}
