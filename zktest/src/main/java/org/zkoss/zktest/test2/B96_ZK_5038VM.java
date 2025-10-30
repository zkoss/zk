/* B96_ZK_5038VM.java

	Purpose:
		
	Description:
		
	History:
		4:05 PM 2021/11/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.Command;
import org.zkoss.zul.ListModelList;

/**
 * @author jumperchen
 */
public class B96_ZK_5038VM {
	private ListModelList model = new ListModelList();

	public B96_ZK_5038VM() {
		model.add(new MyData("aaa"));
		model.add(new MyData("bbb"));
		model.add(new MyData("ccc"));
		model.setMultiple(true);
	}

	@Command
	public void toggle(MyData data){
		data.setEdit(!data.isEdit());
		BindUtils.postNotifyChange(data, "edit");
	}

	public ListModelList getModel() {
		return model;
	}


	public static class MyData {
		private boolean edit;
		private String data;


		public MyData(String data) {
			this.data = data;
		}

		public boolean isEdit() {
			return edit;
		}

		public void setEdit(boolean edit) {
			this.edit = edit;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}
	}
}
