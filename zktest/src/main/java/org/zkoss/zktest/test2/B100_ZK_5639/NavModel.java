package org.zkoss.zktest.test2.B100_ZK_5639;

import java.util.Set;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.DependsOn;
import org.zkoss.zul.ListModelList;

public abstract class NavModel<NavType> {
	private static final String CURRENT = "current";
	private ListModelList<NavType> items = new ListModelList<>();

	public void navigateTo(NavType item) {
		items.addToSelection(item);
		BindUtils.postNotifyChange(null, null, this, CURRENT);
	}
	
	public NavType getCurrent() {
		Set<NavType> selection = items.getSelection();
		return selection.iterator().next();
	}
	
	public void back() {
		navigateTo(items.get(getCurrentIndex() - 1));
	}
	
	public void next() {
		navigateTo(items.get(getCurrentIndex() + 1));
	}
	
	@DependsOn(CURRENT)
	public boolean isFirst() {
		return getCurrentIndex() == 0;
	}

	@DependsOn(CURRENT)
	public boolean isLast() {
		return getCurrentIndex() == items.size() - 1;
	}

	public ListModelList<NavType> getItems() {
		return items;
	}

	private int getCurrentIndex() {
		return items.indexOf(getCurrent());
	}
}
