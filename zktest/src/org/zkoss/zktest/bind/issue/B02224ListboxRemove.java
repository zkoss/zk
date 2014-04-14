package org.zkoss.zktest.bind.issue;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModelList;

public class B02224ListboxRemove {

	private static final String NUMBER = "number";
	private static final String TEXT = "text";
	
	private int numRows = 100;
	private int numColumns = 1;
	
	private ListModelList<RowInfo> rows;
	private List<ColumnInfo> columns;
	
	@Init
	public void init() {
		initModel();
	}

	private void initModel() {
		columns = new ArrayList<B02224ListboxRemove.ColumnInfo>();
		rows = new ListModelList<B02224ListboxRemove.RowInfo>();
		
		for(int columnIndex = 0; columnIndex < numColumns; columnIndex++) {
			String type = (columnIndex % 2) == 0 ? TEXT : NUMBER;
			ColumnInfo column = new ColumnInfo("col-" + columnIndex, type);
			columns.add(column);
		}
		
		for(int rowIndex = 0; rowIndex < numRows; rowIndex++) {
			RowInfo row = new RowInfo();
			List<CellInfo<?>> cells = row.getCells();
			for(int columnIndex = 0; columnIndex < numColumns; columnIndex++) {
				ColumnInfo column = columns.get(columnIndex);
				if(TEXT.equals(column.getType())) {
					cells.add(new CellInfo<String>(rowIndex + ", " + columnIndex, column));
				} else {
					cells.add(new CellInfo<Integer>(rowIndex * columnIndex, column));
				}
			}
			rows.add(row);
		}
	}
	
	@Command("narrowModel")
	@NotifyChange({"columns", "rows"})
	public void doNarrowModel() {
		numColumns = 0;
		initModel();
	}
	
	@Command("widenModel")
	@NotifyChange({"columns", "rows"})
	public void doWidenModel(@BindingParam("cols") int cols) {
		numColumns = cols;
		initModel();
	}
	
	public ListModelList<RowInfo> getRows() {
		return rows;
	}

	public List<ColumnInfo> getColumns() {
		return columns;
	}

	public class RowInfo {
		private List<CellInfo<?>> cells = new ArrayList<CellInfo<?>>();

		public List<CellInfo<?>> getCells() {
			return cells;
		}
	}
	
	public class CellInfo<T> {
		private T value;
		private ColumnInfo column;
		
		public CellInfo(T value, ColumnInfo column) {
			super();
			this.value = value;
			this.column = column;
		}
		
		public ColumnInfo getColumn() {
			return column;
		}
		
		public T getValue() {
			return value;
		}

		public void setValue(T value) {
			this.value = value;
			System.out.println(">>> set "+value+" on "+column.getLabel()+ " @ "+this);
		}
	}
	
	public class ColumnInfo {
		private String label;
		private String type;

		public ColumnInfo(String label, String type) {
			super();
			this.label = label;
			this.type = type;
		}
		
		public String getLabel() {
			return label;
		}
		
		public String getType() {
			return type;
		}
	}
}
