package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

public class B70_ZK_2463_GridViewModel {
	List<B70_ZK_2463_Head> heads;
	List<B70_ZK_2463_Column> columns;
	List<B70_ZK_2463_Row> rows;

	public List<B70_ZK_2463_Head> getHeads() {
		return heads;
	}

	public void setHeads(List<B70_ZK_2463_Head> heads) {
		this.heads = heads;
	}

	public List<B70_ZK_2463_Column> getColumns() {
		return columns;
	}

	public void setColumns(List<B70_ZK_2463_Column> columns) {
		this.columns = columns;
	}

	public List<B70_ZK_2463_Row> getRows() {
		return rows;
	}

	public void setRows(List<B70_ZK_2463_Row> rows) {
		this.rows = rows;
	}

	@Init
	public void init() {
		this.heads = new ArrayList<B70_ZK_2463_Head>();
		this.columns = new ArrayList<B70_ZK_2463_Column>();
		this.rows = new ArrayList<B70_ZK_2463_Row>();
		heads.add(new B70_ZK_2463_Head("", 1));
		heads.add(new B70_ZK_2463_Head("Gr1", 4));
		heads.add(new B70_ZK_2463_Head("Grp2", 3));
		for (int i = 0; i < 8; i++) {
			columns.add(new B70_ZK_2463_Column("col " + i));
		}
		for (int i = 0; i < 7; i++) {
			B70_ZK_2463_Row row = new B70_ZK_2463_Row();
			List<String> values = new ArrayList<String>();
			for (int v = 0; v < 8; v++) {
				values.add("MOCK VAL");
			}
			row.setValues(values);
			rows.add(row);
		}
	}

	@Command
	@NotifyChange({ "heads", "columns", "rows" })
	public void doAdd() {
		columns.add(new B70_ZK_2463_Column("col " + (columns.size() + 1)));
		heads.get(heads.size() - 1).setColspan(
				heads.get(heads.size() - 1).getColspan() + 1);
		for (B70_ZK_2463_Row row : rows)
			row.getValues().add("MOCK VAL");
	}
}
