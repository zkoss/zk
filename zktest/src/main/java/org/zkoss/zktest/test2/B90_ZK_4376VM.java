/* B90_ZK_4376VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Oct 21 14:50:38 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author rudyhuang
 */
public class B90_ZK_4376VM {
	private List<Menu> menuList = new LinkedList<>();
	private boolean collapsed = false;

	@Init
	public void init() {
		initMenus();
	}

	private void initMenus() {
		Menu menuD = new Menu("Dashboard", "z-icon-home");
		menuD.setCounter(7);
		Menu menuE = new Menu("Ecommerce");
		menuE.setCounter(9);
		Menu menuP = new Menu("Project");
		List<Menu> subMenus = new ArrayList<>();
		subMenus.add(menuE);
		subMenus.add(menuP);
		menuD.setSubMenus(subMenus);
		menuList.add(menuD);

		Menu menuC = new Menu("Contact", "z-icon-envelope-o");
		menuC.setCounter(8);
		Menu menuT = new Menu("Typography", "z-icon-flag-o");
		menuT.setCounter(6);
		Menu menuUI = new Menu("UI Elements", "z-icon-flag-o");
		menuUI.setCounter(4);
		menuC.setSubMenus(Arrays.asList(menuT, menuUI));
		menuList.add(menuC);
	}

	@NotifyChange("collapsed")
	@Command
	public void notifyCollapse() {
		// Nothing
	}

	public List<Menu> getMenuList() {
		return menuList;
	}

	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	public static class Menu {
		private final String label;
		private final String icon;
		private int counter;
		private List<Menu> subMenus;

		public Menu(String label) {
			this(label, null);
		}

		public Menu(String label, String icon) {
			this.label = label;
			this.icon = icon;
		}

		public String getLabel() {
			return label;
		}

		public String getIcon() {
			return icon;
		}

		public int getCounter() {
			return counter;
		}

		public void setCounter(int counter) {
			this.counter = counter;
		}

		public List<Menu> getSubMenus() {
			return subMenus;
		}

		public void setSubMenus(List<Menu> subMenus) {
			this.subMenus = subMenus;
		}
	}
}
