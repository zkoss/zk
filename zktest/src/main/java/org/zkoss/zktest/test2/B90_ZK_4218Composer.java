/* B90_ZK_4218Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 24 14:41:54 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Cell;
import org.zkoss.zul.Column;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

/**
 * @author rudyhuang
 */
public class B90_ZK_4218Composer extends SelectorComposer<Component> {
	protected int numberOfAdditionalCols = 3;

	private static final int NUMBER_OF_ROWS = 1000;

	@Wire
	private Grid grid;

	private ListModelList<String> model;

	private List<String> itemList;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		model = new ListModelList<>();
		grid.setModel(model);
		for (int i = 0; i < numberOfAdditionalCols; i++) {
			Column name = new Column("Name");
			name.setWidth("400px");
			grid.getColumns().getChildren().add(name);
		}
		RowRenderer renderer = new B90_ZK_4218RowRenderer(this);
		grid.setRowRenderer(renderer);

		setData();
		model.addAll(itemList.subList(0, 5));
	}

	private void setData() {
		List<String> list = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_ROWS; i++) {
			list.add("name" + i);
		}

		this.itemList = list;
	}

	@Listen("onClick = #showContent")
	public void showAll(Event event) {
		model.clear();
		model.addAll(itemList);
	}

	@Listen("onClick = #appendColumn")
	public void appendColumn(Event event) {
		Column col = new Column("Name");
		col.setWidth("400px");
		grid.getColumns().appendChild(col);
		for (Component row: grid.getRows().getChildren()) {
			Cell cell = new Cell();
			cell.appendChild(new Label("name " + numberOfAdditionalCols));
			row.appendChild(cell);
		}
		numberOfAdditionalCols++;
	}

	private static class B90_ZK_4218RowRenderer implements RowRenderer<Object> {
		private B90_ZK_4218Composer composer;

		public B90_ZK_4218RowRenderer(B90_ZK_4218Composer composer) {
			this.composer = composer;
		}

		public void render(final Row row, final Object data, int index) {
			for (int i = 0; i < composer.numberOfAdditionalCols; i++) {
				Cell cell = new Cell();
				cell.setParent(row);
				new Label("name " + i).setParent(cell);
			}
		}
	}
}
