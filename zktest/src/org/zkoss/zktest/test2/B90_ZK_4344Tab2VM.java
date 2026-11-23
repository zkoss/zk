/* B90_ZK_4344VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Oct 30 12:50:57 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.GlobalCommand;
import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.ListModelList;

/**
 * @author rudyhuang
 */
public class B90_ZK_4344Tab2VM {
	private ListModelList<TabInfo> tabInfos = new ListModelList<>();
	private TabInfo selectedTab;

	@Init
	void init() {
		addTab2_1();
	}

	//TABPANELS

	@GlobalCommand("addTab2_1")
	public void addTab2_1() {
		TabInfo tab2_1 = new TabInfo("tab_2_1", "Tab 2_1", "B90-ZK-4344-tab2-1.zul");
		createAndSelectTabInfo(tab2_1);
	}

	@GlobalCommand("addTab2_2")
	public void addTab2_2() {
		TabInfo tab2_2 = new TabInfo("tab_2_2", "Tab 2_2", "B90-ZK-4344-tab2-2.zul");
		createAndSelectTabInfo(tab2_2);
	}

	public TabInfo getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(TabInfo selectedTab) {
		this.selectedTab = selectedTab;
	}

	public ListModelList<TabInfo> getTabInfos() {
		return tabInfos;
	}

	public void createAndSelectTabInfo(TabInfo tabInfo) {
		TabInfo tab = tabsContains(tabInfos, tabInfo.getId());
		if (tab == null) {
			tab = tabInfo;
			tabInfos.add(tab);
		}
		setSelectedTab(tab);
		BindUtils.postNotifyChange(null, null, this, "selectedTab");
	}

	protected TabInfo tabsContains(ListModelList<TabInfo> tabInfos, String id) {
		if (tabInfos != null && id != null) {
			for (TabInfo tab : tabInfos) {
				if (id.equals(tab.getId())) {
					return tab;
				}
			}
		}
		return null;
	}

	@Command("closeTab")
	public void onCloseTab(@BindingParam("page") TabInfo tabInfo) {
		int indx = tabInfos.indexOf(tabInfo);
		tabInfos.remove(tabInfo);
		if (tabInfo == selectedTab && tabInfos != null) {
			selectedTab = !tabInfos.isEmpty() ? tabInfos.get(indx - 1) : null;
		}
		BindUtils.postNotifyChange(null, null, this, "selectedTab");
	}

	public static class TabInfo {
		protected String id;
		protected String label;
		protected String contentSrc;

		public TabInfo(String id, String label, String contentSrc) {
			this.id = id;
			this.label = label;
			this.contentSrc = contentSrc;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getLabel() {
			return this.label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getContentSrc() {
			return this.contentSrc;
		}

		public void setContentSrc(String contentSrc) {
			this.contentSrc = contentSrc;
		}
	}
}
