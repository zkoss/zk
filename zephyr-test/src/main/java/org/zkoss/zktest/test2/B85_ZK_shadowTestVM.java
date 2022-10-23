/* B85_ZK_3630VM.java

        Purpose:
                
        Description:
                
        History:
                Fri Feb 09 5:31 PM:31 CST 2018, Created by klyve

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;
import org.zkoss.bind.annotation.*;
import org.zkoss.zul.ListModelList;

public class B85_ZK_shadowTestVM {

	public B85_ZK_shadowTestVM(){
		model.add(new InnerModel(true, "item1"));
		model.add(new InnerModel(true, "item2"));
		model.add(new InnerModel(true, "item3"));
		model.add(new InnerModel(false, "item4"));
		model.add(new InnerModel(false, "item5"));
		//model1.add("item1*");

	}

	public ListModelList<InnerModel> model = new ListModelList();

	public ListModelList<InnerModel> model1 = new ListModelList();

	public ListModelList<InnerModel> getModel() {
		return model;
	}

	@Command
	@NotifyChange("model")
	public void changeModel() {
		for (int i = 0; i < model.getSize(); i++) {
			model.get(i).setShow(true);
		}
	}
	@Command
	@NotifyChange("model")
	public void back() {
		for (int i = 3; i < model.getSize(); i++) {
			model.get(i).setShow(false);
		}
	}

	public class InnerModel {
		public boolean _show;
		public String _text;

		InnerModel(boolean show, String text) {
			this._show = show;
			this._text = text;
		}

		public void setShow(boolean show) {
			_show = show;
		}
		public void setText(String text) {
			_text = text;
		}
		public boolean getShow(){
			return _show;
		}

		public String getText(){
			return _text;
		}
	}
}