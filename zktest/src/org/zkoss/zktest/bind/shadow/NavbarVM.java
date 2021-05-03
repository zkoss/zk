/* NavbarVM.java

	Purpose:
		
	Description:
		
	History:
		Thu May 06 14:56:23 CST 2021, Created by rudyhuang

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.bind.shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import org.zkoss.bind.annotation.Init;

/**
 * @author rudyhuang
 */
public class NavbarVM {
	private List<Object> menuItems;

	@Init
	public void init() {
		final List<Menu> products = new ArrayList<>();
		final List<Menu> demos = new ArrayList<>();
		menuItems = new ArrayList<>();
		menuItems.add(new Menu("Home", "z-icon-home"));
		menuItems.add(new Menu("Products", "z-icon-clone", products));
		menuItems.add(new Menu("Demos", "z-icon-cube", demos));
		menuItems.add(new Menu("Downloads", "z-icon-download"));
		menuItems.add(new Menu("Community", "z-icon-facebook"));
		menuItems.add(new Menu("About", "z-icon-question"));

		products.add(new Menu("ZK Framework"));
		products.add(new Menu("Keikai Spreadsheet"));
		products.add(new Menu("ZK Charts"));
		products.add(new Menu("Quire.io"));

		demos.add(new Menu("ZK Demo"));
		demos.add(new Menu("ZK Sandbox"));
		demos.add(new Menu("ZK Fiddle"));
	}

	public List<Object> getMenuItems() {
		return menuItems;
	}

	public static class Menu {
		private String label;
		private String iconSclass;
		private List<Menu> subMenus;

		public Menu(String label) {
			this(label, null, null);
		}

		public Menu(String label, String iconSclass) {
			this(label, iconSclass, null);
		}

		public Menu(String label, String iconSclass, List<Menu> subMenus) {
			this.label = label;
			this.iconSclass = iconSclass;
			this.subMenus = subMenus;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getIconSclass() {
			return iconSclass;
		}

		public void setIconSclass(String iconSclass) {
			this.iconSclass = iconSclass;
		}

		public List<Menu> getSubMenus() {
			return subMenus;
		}

		public void setSubMenus(List<Menu> subMenus) {
			this.subMenus = subMenus;
		}

		@Override
		public String toString() {
			return new StringJoiner(", ", Menu.class.getSimpleName() + "[", "]")
					.add("label='" + label + "'")
					.add("iconSclass='" + iconSclass + "'")
					.toString();
		}
	}
}
