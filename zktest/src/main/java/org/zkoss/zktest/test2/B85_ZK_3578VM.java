package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

/**
 * @author bob peng
 */
public class B85_ZK_3578VM {

	private boolean showColumn;
	private ListModelList<String> columnList;

	@Init
	public void init() {
		columnList = new ListModelList<String>();
		columnList.add("test");
	}

	public boolean isShowColumn() {
		return showColumn;
	}

	public void setShowColumn(boolean showColumn) {
		this.showColumn = showColumn;
	}

	@Command
	public void toggle() {
		showColumn = !showColumn;
		BindUtils.postNotifyChange(null, null, this, "showColumn");
		BindUtils.postNotifyChange(null, null, this, "columnList");
	}

	public ListModelList<String> getColumnList() {
		return columnList;
	}

	public void setColumnList(ListModelList<String> columnList) {
		this.columnList = columnList;
	}
}
