/* MainLayoutComposer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 12, 2008 3:10:06 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zkdemo.userguide;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.zkoss.lang.reflect.FusionInvoker;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.BookmarkEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zk.ui.event.KeyEvent;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Include;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Textbox;

/**
 * @author jumperchen
 * 
 */
public class MainLayoutComposer extends GenericForwardComposer implements
	MainLayoutAPI, ComposerExt {
	Textbox searchBox;

	Listbox itemList;

	Borderlayout main;
	
	Include xcontents;
	
	Div header;

	Button _selected;

	public MainLayoutComposer() {
		initKey();
	}

	private Map getCategoryMap() {
		return DemoWebAppInit.getCateMap();
	}

	private void initKey() {
		// We have to decide the key of Google Maps since we have a demo using
		// it.
		// This key is used by zkdemo/userguide/index.zul to generate a proper
		// script
		final Execution exec = Executions.getCurrent();
		final String sn = exec.getServerName();
		final int sp = exec.getServerPort();

		// To add more keys: http://www.google.com/apis/maps/signup.html

		String gkey = null;
		if (sn.indexOf("www.potix.com") >= 0) { // http://www.potix.com/
			gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxRYCo_4ZGj_-54kHesWSk0nMkbs4xTpq0zo9O75_ZqvsSLGY2YkC7jjNg";
		} else if (sn.indexOf("www.zkoss.org") >= 0) { // http://www.zkoss.org/
			gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxQXyylOlR69a1vFTcUcpV6DXdesOBSMEHfkewcSzwEwBT7UzVx8ep8vjA";
		} else if (sn.indexOf("zkoss.org") >= 0) { // http://www.zkoss.org/
			gkey = "ABQIAAAAakIm31AXAvNGFHV8i1Tx8RSF4KLGEmvBsS1z1zAsQZvbQceuNRQBsm65qGaXpTWjZsc2bl-hm2Vyfw";
		} else if (sn.indexOf("localhost") >= 0) { // localhost
			if (sp == 80) // http://localhost/
				gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxT2yXp_ZAY8_ufC3CFXhHIE1NvwkxRUITTZ-rzsyEVih16Hn3ApyUpSkA";
			else if (sp == 8080) // http://localhost:8080
				gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxTwM0brOpm-All5BF6PoaKBxRWWERSynObNOWSyMNmLGAMZAO1WkDUubA";
			else if (sp == 7799)
				gkey = "ABQIAAAAmGxmYR57XDAbAumS9tV5fxTT6-Op-9nAQgn7qnDG0QjE8aldaBRU1BQK2ADNWCt1BR2yg4ghOM6YIA";
		}

		if (gkey != null)
			exec.getDesktop().getSession().setAttribute("gmapsKey", gkey);
	}

	public void onCategorySelect(ForwardEvent event) {
		Button btn = (Button) event.getOrigin().getTarget();
		Listitem item = null;
		if (_selected != btn) {
			_selected = btn;
		} else {
			item = itemList.getSelectedItem();
		}
		String href = getCategory(_selected.getId()).getHref();
		if (href != null) {
			Executions.getCurrent().sendRedirect(href);
		} else {
			itemList.setModel(getSelectedModel());
			if (Executions.getCurrent().isBrowser("ie6-"))
				Clients.evalJavaScript("fixImage4IE6();");
			if (item != null) {
				itemList.renderAll();
				((Listitem)itemList.getFellow(item.getId())).setSelected(true);
			}
		}
	}
	public void onBookmarkChange$main(BookmarkEvent event) {
		String id = event.getBookmark();
		if (id != null) {
			final DemoItem[] items = getItems();
			for (int i = 0; i < items.length; i++) {
				if (items[i].getId().equals(id)) {
					_selected = (Button)main.getFellow(items[i].getCateId());
					itemList.setModel(getSelectedModel());
					itemList.renderAll();
					Listitem item = ((Listitem)itemList.getFellow(id));
					item.setSelected(true);
					itemList.invalidate();
					setSelectedCategory(item);
					xcontents.setSrc(((DemoItem) item.getValue()).getFile());
					item.focus();
					return;
				}
			}
		}
		
	}
	public void onSelect$itemList(SelectEvent event) {
		Listitem item = itemList.getSelectedItem();
		 
		if (item != null) {
			
			// sometimes the item is unloaded.
			if (!item.isLoaded()) {
				itemList.renderItem(item);
			}
			
			setSelectedCategory(item);
			xcontents.setSrc(((DemoItem) item.getValue()).getFile());
		}
	}

	public void onMainCreate(Event event) {
		final Execution exec = Executions.getCurrent();
		final String id = exec.getParameter("id");
		Listitem item = null;
		if (id != null) {
			try {
				final LinkedList list = new LinkedList();
				final DemoItem[] items = getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].getId().equals(id))
						list.add(items[i]);
				}
				if (!list.isEmpty()) {
					itemList.setModel(new ListModelList(list));
					itemList.renderAll();
					item = (Listitem) main.getFellow(id);
					setSelectedCategory(item);
				}
			} catch (ComponentNotFoundException ex) { // ignore
			}
		}

		if (item == null) {
			item = (Listitem) main.getFellow("f1");
			setSelectedCategory(item);
		}		
		xcontents.setSrc(((DemoItem) item.getValue()).getFile());

		itemList.selectItem(item);
	}
	private void setSelectedCategory(Listitem item) {
		DemoItem di = (DemoItem) item.getValue();
		_selected = (Button) main.getFellow(di.getCateId());
		String deselect = _selected != null ? "jq('#"+ _selected.getUuid() + 
		"').addClass('demo-seld').siblings().removeClass('demo-seld');" : "";
		if (Executions.getCurrent().isBrowser("ie6_")) {
			Clients.evalJavaScript(deselect + "fixImage4IE6();");
		} else {
			Clients.evalJavaScript(deselect);
		}
		item.getDesktop().setBookmark(item.getId());
	}
	public void onCtrlKey$searchBox(KeyEvent event) {
		int keyCode = event.getKeyCode();
		List items = itemList.getItems();
		if (items.isEmpty()) return;
		Listitem item = null;
		switch (keyCode) {
		case 38: // UP
			item = itemList.getItemAtIndex(items.size() -1);
			itemList.setSelectedItem(item);
			break;
		case 40: // DOWN
			item = itemList.getItemAtIndex(0);
			itemList.setSelectedItem(item);
			break;
		}
		if (item != null) {
			setSelectedCategory(item);
			xcontents.setSrc(((DemoItem) item.getValue()).getFile());
			item.focus();
		}
	}
	public void onChanging$searchBox(InputEvent event) {
		String key = event.getValue();
		LinkedList item = new LinkedList();
		DemoItem[] items = getItems();

		if (key.trim().length() != 0) {
			for (int i = 0; i < items.length; i++) {
				if (items[i].getLabel().toLowerCase()
						.indexOf(key.toLowerCase()) != -1)
					item.add(items[i]);
			}
			itemList.setModel(new ListModelList(item));
		} else itemList.setModel(new ListModelList(items));
		String deselect = _selected != null ? "onSelect('"+ _selected.getUuid() + "', true);" : "";
		if (Executions.getCurrent().isBrowser("ie6-")) {
			itemList.renderAll();
			Clients.evalJavaScript(deselect + "fixImage4IE6();");
		} else {
			Clients.evalJavaScript(deselect);
		}
		_selected = null;
	}

	private DemoItem[] getItems() {
		LinkedList items = new LinkedList();
		Category[] categories = getCategories();
		for (int i = 0; i < categories.length; i++) {
			items.addAll(categories[i].getItems());
		}
		return (DemoItem[]) items.toArray(new DemoItem[] {});
	}

	public Category[] getCategories() {
		return (Category[]) getCategoryMap().values()
				.toArray(new Category[] {});
	}

	public ListitemRenderer getItemRenderer() {
		return _defRend;
	}

	private static final ListitemRenderer _defRend = new ListitemRenderer() {
		public void render(Listitem item, Object data) {
			DemoItem di = (DemoItem) data;
			Listcell lc = new Listcell();
			item.setValue(di);
			lc.setHeight("30px");
			lc.setImage(di.getIcon());
			item.setId(di.getId());
			lc.setLabel(di.getLabel());
			lc.setParent(item);
		}
	};

	private Category getCategory(String cateId) {
		return (Category) getCategoryMap().get(cateId);
	}

	public ListModel getSelectedModel() {
		Category cate = _selected == null ? getCategories()[0] :
				getCategory(_selected.getId());
		return new ListModelList(cate.getItems());
	}

	// Composer Implementation
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		Events.postEvent("onMainCreate", comp, null);
	}

	public ComponentInfo doBeforeCompose(Page page, Component parent,
			ComponentInfo compInfo) {
		return compInfo;
	}

	public void doBeforeComposeChildren(Component comp) throws Exception {
		bindComponent(comp);
		Object obj = FusionInvoker.newInstance(new Object[] { comp, this });
		comp.setVariable("main", obj, true);
		main = (Borderlayout) comp;
	}

	public boolean doCatch(Throwable ex) throws Exception {
		ex.printStackTrace();
		return false;
	}

	public void doFinally() throws Exception {
	}
}
