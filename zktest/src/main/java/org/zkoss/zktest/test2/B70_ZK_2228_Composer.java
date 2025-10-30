package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Init;

public class B70_ZK_2228_Composer {
	private final List<Row> rows = new ArrayList<Row>();

	@Init
	public void init() {
		for (int i = 0; i < 5 * 25; i++) {
			this.rows.add(new Row());
		}
	}

	public List<Row> getRows() {
		return this.rows;
	}

	public List<String> getHeaders() {
		final List<String> headers = new ArrayList<String>();
		for (int i = 1; i < 11; i++) {
			headers.add("Extra long column header label " + i);
		}
		return headers;
	}

	public class Row {

		private final List<String> columns = new ArrayList<String>();;

		public Row() {
			for (int i = 1; i < 11; i++) {
				this.columns.add("Column " + i);
			}
		}

		public List<String> getColumns() {
			return this.columns;
		}
	}
}
