package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.Window;

public class B60_ZK_1702_ComboboxComposer extends GenericForwardComposer<Window> {
	private static final long serialVersionUID = 5973951998054613890L;
	private AnnotateDataBinder binder;
	private Item selItem;
	private List<Item> list;

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		binder = new AnnotateDataBinder(comp);
		comp.setAttribute("ctl", this);

		list = new ArrayList<Item>();
		list.add(new Item("Item 1"));
		list.add(new Item("Item 2"));
		list.add(new Item("Item 3"));
		binder.loadAll();
	}

	public void onSelectItem(Event event) {
		binder.loadAll();
	}

	public List<Item> getList() {
		return list;
	}

	public void setList(List<Item> list) {
		this.list = list;
	}

	public Item getSelItem() {
		return selItem;
	}

	public void setSelItem(Item selItem) {
		this.selItem = selItem;
	}

	public static class Item {
		private String name;

		public Item(String name) {
			super();
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}
