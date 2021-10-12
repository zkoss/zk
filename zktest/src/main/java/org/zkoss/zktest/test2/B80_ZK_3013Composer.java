/* B80_ZK_3013Composer.java

	Purpose:
		
	Description:
		
	History:
		Wed Dec 30 10:00:28 CST 2015, Created by Jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.*;

/**
 * @author jameschu
 */
public class B80_ZK_3013Composer extends SelectorComposer<Window> {

	ListModelList<String> gridModel = new ListModelList();
	ListModelList<String> comboModel = new ListModelList();
	ListModelList<String> gridModel2 = new ListModelList();
	ListModelList<String> comboModel2 = new ListModelList();

	public ListModelList<String> getGridModel() {
		return gridModel;
	}

	public ListModelList<String> getComboModel() {
		return comboModel;
	}

	public ListModelList<String> getGridModel2() {
		return gridModel2;
	}

	public ListModelList<String> getComboModel2() {
		return comboModel2;
	}

	public RowRenderer getRowRenderer() {
		return rowRenderer;
	}

	@Override
	public ComponentInfo doBeforeCompose(Page page, Component parent, ComponentInfo compInfo) {

		return super.doBeforeCompose(page, parent, compInfo);

	}

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		// template
		gridModel.add("dummyTemplate");
		comboModel.add("aaa");
		comboModel.add("bbb");
		comboModel.addToSelection("aaa");

		//renderer
		gridModel2.add("dummyRenderer");
		comboModel2.add("aaa");
		comboModel2.add("bbb");
		comboModel2.addToSelection("aaa");
	}

	public RowRenderer rowRenderer = new RowRenderer() {
		@Override
		public void render(Row row, Object obj, int index) throws Exception {
			final Combobox cb = new Combobox();
			cb.setModel(comboModel2);
			cb.addEventListener("onChange", new EventListener() {
				public void onEvent(Event e) {
					if (cb.getDesktop() == null)
						Clients.log("error");
					gridModel2.set(0, "dummyRenderer");
				}
			});
			cb.addEventListener("onSelect", new EventListener() {
				public void onEvent(Event e) {
					if (cb.getDesktop() == null)
						Clients.log("error");
				}
			});
			row.appendChild(new Label(obj + " " + comboModel2.getSelection()));
			row.appendChild(cb);
		}
	};
}