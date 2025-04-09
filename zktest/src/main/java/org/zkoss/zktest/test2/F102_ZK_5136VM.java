package org.zkoss.zktest.test2;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.SimpleListModel;

public class F102_ZK_5136VM {
	private ListModel model;
	private String searchText = "A";

	public F102_ZK_5136VM() {
		model = new SimpleListModel(new String[]{"North America", "South America", "Europe", "Asia", "Africa", "Oceania", "Antarctica"});
	}

	public ListModel getModel() {
		return model;
	}

	@Command
	@NotifyChange("searchText")
	public void clearSearchText() {
		searchText = "";
	}

	@Command
	@NotifyChange("searchText")
	public void updateSearchTextA() {
		searchText = "A";
	}

	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}
}