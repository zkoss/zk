package org.zkoss.zktest.test2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.ListModelList;

public class B70_ZK_2211_ViewModel implements Serializable {
	ListModelList _model;

	public ListModelList getModel() {
		if (_model == null) {
			List l = new ArrayList();
			for (int i = 0; i < 2000; i++) {
				l.add("item" + i);
			}
			_model = new ListModelList(l);
		}
		return _model;
	}
}