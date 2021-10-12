/* B80_ZK_2880ViewModel.java

	Purpose:
		
	Description:
		
	History:
		11:35 AM 9/18/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

/**
 * @author jumperchen
 */
public class B80_ZK_2880ViewModel{
	private List<ListElement> list;

	public B80_ZK_2880ViewModel() {
		list = new ArrayList<ListElement>();
		list.add(new ListElement("A"));
		list.add(new ListElement("B"));
		list.add(new ListElement("C"));
		list.add(new ListElement("D"));
	}

	public List<ListElement> getList() {
		return list;
	}

	public void setList(List<ListElement> list) {
		this.list = list;
	}

	@Command
	@NotifyChange("list")
	public void buttonClick() {
		getList().clear();

		int number = Double.valueOf(Math.random() * 8).intValue() + 3;
		int start = Double.valueOf(Math.random() * 26).intValue();

		for (int i = 0; i < number; i++) {
			int index = (start + i) % 26;
			String letter = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".substring(index, index + 1);
			getList().add(new ListElement(letter));
		}
	}

	public class ListElement {
		private String name;

		public ListElement(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
}