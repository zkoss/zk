/* ListboxModelVM.java

	Purpose:
		
	Description:
		
	History:
		Created by Dennis

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
 */

package org.zkoss.zktest.bind.issue;


/**
 * @author Dennis Chen
 * 
 */
public class B00992SubModel {
	org.zkoss.zul.SimpleListModel model;
	P selected;

	public B00992SubModel() {
		java.util.List data = new java.util.ArrayList();
		for (int i = 0; i < 1000; i++) {
			data.add(new P("" + i));
		}
		model = new org.zkoss.zul.SimpleListModel(data);
	}

	public org.zkoss.zul.SimpleListModel getModel() {
		return model;
	}

	public P getSelected() {
		return selected;
	}

	public void setSelected(P p) {
		selected = p;
	}

	public class P {
		String name;

		public P(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public String toString() {
			return name;
		}
	}

}
