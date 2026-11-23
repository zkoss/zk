/* F86_ZK_2756VM.java

	Purpose:
		
	Description:
		
	History:
		Mon Sep 03 13:02:04 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zul.SimpleGroupsModel;

/**
 * @author rudyhuang
 */
public class F86_ZK_2756VM {
	private SimpleGroupsModel<String, String, String, String> model;
	private String selected;

	public F86_ZK_2756VM() {
		model = new SimpleGroupsModel<>(new String[][] {
				new String[] {
						"2015",
						"2016"
				},
				new String[] {
						"2015-Spring",
						"2015-Fall",
						"2016-Spring",
						"2016-Fall",
				},
				new String[] {
						"2015-Q1",
						"2015-Q2",
						"2015-Q3",
						"2015-Q4",
						"2016-Q1",
						"2016-Q2",
						"2016-Q3",
						"2016-Q4"
				}
		}, new String[]{"Fiscal Year", "Planning Seasons", "Fiscal Quarters"});
	}

	public SimpleGroupsModel<String, String, String, String> getModel() {
		return model;
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}
}
