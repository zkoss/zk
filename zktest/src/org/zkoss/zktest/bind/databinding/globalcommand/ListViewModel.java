package org.zkoss.zktest.bind.databinding.globalcommand;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.NotifyChange;

public class ListViewModel {

	private List<String> items;
	private Date lastUpdate;
	private boolean visible = true;

	@GlobalCommand
	@NotifyChange({"items", "lastUpdate"})
	public void refresh() {
		items = ItemList.getList();
		lastUpdate = Calendar.getInstance().getTime();
	}

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public Date getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Date lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
