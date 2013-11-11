package org.zkoss.zktest.bind.issue;

import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

public class F01845TabboxModel {

	private ListModelList<TabInfo> tabInfos;
	
	private int counter = 1;
	
	private TabInfo selected;
	
	private int selectedIndex = 0;
	
	@Init
	public void init() {
		tabInfos = new ListModelList<TabInfo>();
		tabInfos.add(selected = new TabInfo("Detail " + counter++, "/bind/issue/F01845TabboxModelDetail.zul"));
		tabInfos.add(new TabInfo("Detail " + counter++, "/bind/issue/F01845TabboxModelDetail.zul"));
		
	}

	@Command("addTab")
//	@NotifyChange("tabInfos") //don't need to notify, because you are using efficient ListModelList
	public void onAddTab() {
		tabInfos.add(new TabInfo("Detail " + counter++, "/bind/issue/F01845TabboxModelDetail.zul"));
	}

	@Command("removeTab")
//	@NotifyChange("tabInfos") //don't need to notify, because you are using efficient ListModelList
	public void onRemoveTab() {
		if(tabInfos.size()>0){
			tabInfos.remove(tabInfos.size()-1);
		}
	}
	public List<TabInfo> getTabInfos() {
		return tabInfos;
	}

	public TabInfo getSelected() {
		return selected;
	}

	public void setSelected(TabInfo selected) {
		this.selected = selected;
	}

	public int getSelectedIndex() {
		return selectedIndex;
	}

	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}
	public static class TabInfo {

		String path;
		String title;

		public TabInfo(String title, String path) {
			super();
			this.title = title;
			this.path = path;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}
	}	
	
}
