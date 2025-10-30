/* F86_ZK_4212Composer.java

		Purpose:
                
		Description:
                
		History:
				Tue Mar 19 10:25:04 CST 2019, Created by charlesqiu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.List;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.Column;
import org.zkoss.zul.Footer;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listfooter;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecell;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treefooter;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

public class F86_ZK_4212Composer extends SelectorComposer {
	@Wire
	private Grid grid;
	@Wire
	private Listbox listbox;
	@Wire
	private Tree tree;
	private int columnNum = 11;
	private int rowNum = 11;


	@Listen("onClick = #columns2")
	public void columns2() {
		grid.getFrozen().setColumns(2);
		listbox.getFrozen().setColumns(2);
		tree.getFrozen().setColumns(2);
	}

	@Listen("onClick = #rightColumns2")
	public void rightColumns2() {
		grid.getFrozen().setRightColumns(2);
		listbox.getFrozen().setRightColumns(2);
		tree.getFrozen().setRightColumns(2);
	}

	@Listen("onClick = #addColumn")
	public void addColumn() {
		String columnLabel = "Column " + columnNum;
		String footLabel = "Footer " + columnNum;
		String width = "100px";

		Column column = new Column(columnLabel);
		column.setWidth(width);
		grid.getColumns().appendChild(column);
		grid.getRows().<Row>getChildren().forEach(
				row -> row.appendChild(new Label("Cell " + (row.getIndex() + 1) + "-" + columnNum))
		);
		grid.getFoot().appendChild(new Footer(footLabel));

		Listheader listheader = new Listheader(columnLabel);
		listheader.setWidth(width);
		listbox.getListhead().appendChild(listheader);
		listbox.getItems().forEach(
				listitem -> listitem.appendChild(new Listcell("Cell " + (listitem.getIndex() + 1) + "-" + columnNum))
		);
		listbox.getListfoot().appendChild(new Listfooter(footLabel));


		Treecol treecol = new Treecol(columnLabel);
		treecol.setWidth(width);
		tree.getTreecols().appendChild(treecol);
		tree.getItems().forEach(
				treeitem -> {
					Treerow treerow = treeitem.getTreerow();
					treerow.appendChild(new Treecell("Cell " + (treeitem.getIndex() + 1) + "-" + columnNum));
				}
		);
		tree.getTreefoot().appendChild(new Treefooter(footLabel));

		columnNum++;
	}

	@Listen("onClick = #addRow")
	public void addRow() {
		Rows rows = grid.getRows();
		Row row = new Row();
		for (int i = 1; i < columnNum; i++)
			row.appendChild(new Label("Cell " + rowNum + "-" + i));
		rows.appendChild(row);

		Listitem listitem = new Listitem();
		for (int i = 1; i < columnNum; i++)
			listitem.appendChild(new Listcell("Cell " + rowNum + "-" + i));
		listbox.appendChild(listitem);

		Treerow treerow = new Treerow();
		Treeitem treeitem = new Treeitem();
		for (int i = 1; i < columnNum; i++)
			treerow.appendChild(new Treecell("Cell " + rowNum + "-" + i));
		treeitem.appendChild(treerow);
		tree.getTreechildren().appendChild(treeitem);

		rowNum++;
	}

	@Listen("onClick = #increaseLastColumnWidth")
	public void increaseLastColumnWidth() {
		List<Column> columns = grid.getColumns().getChildren();
		columns.get(columns.size() - 1).setWidth("200px");

		List<Listheader> listheaders = listbox.getListhead().getChildren();
		listheaders.get(listheaders.size() - 1).setWidth("200px");

		List<Treecol> treecols = tree.getTreecols().getChildren();
		treecols.get(treecols.size() - 1).setWidth("200px");
	}
}
