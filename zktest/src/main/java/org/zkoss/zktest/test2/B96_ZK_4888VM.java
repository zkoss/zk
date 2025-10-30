/* B96_ZK_4888VM.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 16:05:16 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.*;
import org.zkoss.zul.ListModelList;

import java.util.ArrayList;

public class B96_ZK_4888VM {
	private ListModelList<TestModel> listModel;

	private TestModel selectedItem;

	private ArrayList<ColumnInfo> allColumns = new ArrayList<ColumnInfo>();

	@Init
	public void init() {
		setupModel();
		initColumns();
	}

	private void setupModel() {
		listModel = new ListModelList<TestModel>();
		listModel.add(new TestModel("1", "2", "3", "4", "5", "6"));
		listModel.add(new TestModel("1", "2", "3", "4", "5", "6"));
		listModel.add(new TestModel("1", "2", "3", "4", "5", "6"));
		listModel.add(new TestModel("1", "2", "3", "4", "5", "6"));
		listModel.add(new TestModel("1", "2", "3", "4", "5", "6"));
		listModel.add(new TestModel("1", "2", "3", "4", "5", "6"));
		listModel.add(new TestModel("1", "2", "3", "4", "5", "6"));
	}

	private void initColumns() {
		ColumnInfo columnInfo = new ColumnInfo("test1", "Column 1", "default");
		allColumns.add(columnInfo);

		columnInfo = new ColumnInfo("test2", "Column 2", "default");
		allColumns.add(columnInfo);

		columnInfo = new ColumnInfo("test3", "Column 3", "default");
		allColumns.add(columnInfo);

		columnInfo = new ColumnInfo("test4", "Column 4", "default");
		allColumns.add(columnInfo);

		columnInfo = new ColumnInfo("test5", "Column 5", "default");
		allColumns.add(columnInfo);

		columnInfo = new ColumnInfo("test6", "Column 6", "default");
		allColumns.add(columnInfo);
	}

	@Command
	@NotifyChange("*")
	public void refreshData() {
		setupModel();
	}

	public ListModelList<TestModel> getListModel() {
		return listModel;
	}

	public TestModel getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(TestModel selectedItem) {
		this.selectedItem = selectedItem;
	}

	public ArrayList<ColumnInfo> getAllColumns() {
		return allColumns;
	}

	public class ColumnInfo {
		private String value;
		private String label;
		private String templateName;

		public ColumnInfo(String value, String label, String templateName) {
			this.value = value;
			this.label = label;
			this.templateName = templateName;
		}

		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getTemplateName() {
			return templateName;
		}
		public void setTemplateName(String templateName) {
			this.templateName = templateName;
		}
	}

	public class TestModel {
		private String test1;
		private String test2;
		private String test3;
		private String test4;
		private String test5;
		private String test6;

		public TestModel(String test1,String test2,String test3,String test4,String test5,String test6) {
			this.test1 = test1;
			this.test2 = test2;
			this.test3 = test3;
			this.test4 = test4;
			this.test5 = test5;
			this.test6 = test6;
		}

		public String getTest1() {
			return test1;
		}

		public void setTest1(String test1) {
			this.test1 = test1;
		}

		public String getTest2() {
			return test2;
		}

		public void setTest2(String test2) {
			this.test2 = test2;
		}

		public String getTest3() {
			return test3;
		}

		public void setTest3(String test3) {
			this.test3 = test3;
		}

		public String getTest4() {
			return test4;
		}

		public void setTest4(String test4) {
			this.test4 = test4;
		}

		public String getTest5() {
			return test5;
		}

		public void setTest5(String test5) {
			this.test5 = test5;
		}

		public String getTest6() {
			return test6;
		}

		public void setTest6(String test6) {
			this.test6 = test6;
		}
	}
}
