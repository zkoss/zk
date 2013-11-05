/* B65_ZK_1998Composer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Nov 4, 2013 12:34:19 PM , Created by Vincent
}}IS_NOTE

Copyright (C) 2013 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
 */
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.GroupComparator;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Window;

/**
 * @author Vincent
 * 
 */
public class B65_ZK_1998Composer extends SelectorComposer<Window> {

	private static final long serialVersionUID = 20131104123419L;

	private class Food {
		private String category;
		private String name;
		private String topNutrients;

		public Food(String category, String name, String topNutrients) {
			setCategory(category);
			setName(name);
			setTopNutrients(topNutrients);
		}

		public String getCategory() {
			return category;
		}

		public void setCategory(String category) {
			this.category = category;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getTopNutrients() {
			return topNutrients;
		}

		public void setTopNutrients(String topNutrients) {
			this.topNutrients = topNutrients;
		}
	}

	private class FoodData {
		private List<Food> foods = new ArrayList<Food>();

		public List<Food> getAllFoods() {
			foods.add(new Food("Vegetables", "Asparagus", "Vitamin K"));
			foods.add(new Food("Vegetables", "Beets", "Folate"));
			foods.add(new Food("Seafood", "Salmon", "Tryptophan"));
			foods.add(new Food("Seafood", "Shrimp", "Tryptophan"));
			return foods;
		}
	}

	private class FoodGroupComparator implements GroupComparator<Food> {
		public int compareGroup(Food food1, Food food2) {
			return Math.abs(compare(food1, food2));
		}

		public int compare(Food food1, Food food2) {
			return food1.getCategory().compareTo(food2.getCategory());
		}
	}

	private class FoodGroupInfo {
		private Food firstChild;
		private int groupIndex;
		private int columnIndex;

		public FoodGroupInfo(Food firstChild, int groupIndex,
				int columnIndex) {
			this.firstChild = firstChild;
			this.groupIndex = groupIndex;
			this.columnIndex = columnIndex;
		}

		public Food getFirstChild() {
			return firstChild;
		}

		@SuppressWarnings("unused")
		public int getGroupIndex() {
			return groupIndex;
		}

		public int getColumnIndex() {
			return columnIndex;
		}
	}

	private class FoodGroupModel extends GroupsModelArray<Food, FoodGroupInfo, Object, Object> {
		private static final long serialVersionUID = 1L;

		public FoodGroupModel(Food[] data, Comparator<Food> compr) {
			super(data, compr);
		}

		protected FoodGroupInfo createGroupHead(Food[] groupData, int index,
				int column) {
			return new FoodGroupInfo(groupData[0], index, column);
		}

		protected Object createGroupFoot(Food[] groupData, int index, int column) {
			return groupData.length;
		}
	}

	private class FoodGroupRenderer implements ListitemRenderer<Object> {
		public void render(Listitem item, Object obj, int index)
				throws Exception {
			if (item instanceof Listgroup) {
				FoodGroupInfo groupInfo = (FoodGroupInfo) obj;
				Food food = groupInfo.getFirstChild();
				String groupTxt;
				switch (groupInfo.getColumnIndex()) {
				case 0:
					groupTxt = food.getCategory();
					break;
				case 1:
					groupTxt = food.getName();
					break;
				case 2:
					groupTxt = food.getTopNutrients();
					break;
				default:
					groupTxt = food.getCategory();
				}
				item.appendChild(new Listcell(groupTxt));
				item.setValue(obj);
			} else if (item instanceof Listgroupfoot) {
				Listcell cell = new Listcell();
				cell.appendChild(new Label("Total " + obj + " Items"));
				cell.setSpan(3);
				item.appendChild(cell);
			} else {
				Food data = (Food) obj;
				item.appendChild(new Listcell(data.getCategory()));
				item.appendChild(new Listcell(data.getName()));
				item.appendChild(new Listcell(data.getTopNutrients()));
				item.setValue(data);
			}
		}
	}

	@Wire
	private Listbox listbox;

	private GroupsModelArray<Food, FoodGroupInfo, Object, Object> groupModel;

	private ListitemRenderer<Object> groupRenderer = new FoodGroupRenderer();

	@Override
	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		org.zkoss.lang.Library.setProperty("org.zkoss.zul.listbox.rod", "false");

		Food[] foods = new FoodData().getAllFoods().toArray(new Food[] {});
		groupModel = new FoodGroupModel(foods, new FoodGroupComparator());
		groupModel.setMultiple(true);
		for (Food food : foods)
			groupModel.addToSelection(food);
		for (int i = 0, t = groupModel.getGroupCount(); i < t; i++) {
			groupModel.addToSelection(groupModel.getGroup(i));
			if (groupModel.hasGroupfoot(i))
				groupModel.addToSelection(groupModel.getGroupfoot(i));
		}
		listbox.setCheckmark(true);
		listbox.setModel(groupModel);
		listbox.setItemRenderer(groupRenderer);
	}
}
