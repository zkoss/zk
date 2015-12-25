/* B70_ZK_3021VM.java

	Purpose:
		
	Description:
		
	History:
		4:08 PM 12/25/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author jumperchen
 */
public class B70_ZK_3021VM {

	private List<MainBean> listMainBeans = Arrays.asList(new MainBean());

	public List<MainBean> getListMainBeans() {
		return listMainBeans;
	}

	public static class MainBean {
		private String name = "MAIN BEAN";

		private List<SecondaryBean> listChildrens = new ArrayList<SecondaryBean>();

		public MainBean() {
			for (int i =0; i < 2; i ++) {
				listChildrens.add(new SecondaryBean(i));
			}
		}

		public String getName() {
			return name;
		}

		public List<SecondaryBean> getListChildrens() {
			return listChildrens;
		}
	}

	public static class SecondaryBean {

			private String name;

			public SecondaryBean(int i) {
			name = "Secondary-" + i;
		}

		public String getName() {
			return name;
		}
	}


}

