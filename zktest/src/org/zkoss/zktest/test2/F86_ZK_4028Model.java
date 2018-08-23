/* F86_ZK_4028Model.java

	Purpose:
		
	Description:
		
	History:
		Fri Aug 17 15:50:08 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.HashMap;
import java.util.Map;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.NavigationModel;
import org.zkoss.zul.ListModel;

/**
 * @author rudyhuang
 */
public class F86_ZK_4028Model extends NavigationModel<F86_ZK_4028Step> {
	private boolean _hasCar = false;
	private boolean _hasHistorySupport = false;
	private F86_ZK_4028Step _carStep = new F86_ZK_4028Step("Rent Car", "z-icon-car", "/test2/F86-ZK-4028-stepn.zul");
	private HistoryStateGenerator<F86_ZK_4028Step, Map> _generator = new HistoryStateGenerator<F86_ZK_4028Step, Map>() {
		@Override
		public Map getState(F86_ZK_4028Step item, int index) {
			return createState(item.getText(), index);
		}

		@Override
		public String getTitle(F86_ZK_4028Step item, int index) {
			return item.getText();
		}

		@Override
		public String getUrl(F86_ZK_4028Step item, int index) {
			return String.format("#%s", item.getText().replaceAll("/ /", "")).toLowerCase();
		}
	};

	public F86_ZK_4028Model(ListModel<? extends F86_ZK_4028Step> model) {
		super(model);
	}

	public void switchCar() {
		if (_hasCar) {
			getInnerItems().remove(_carStep);
			_hasCar = false;
			// Once car has been deleted, step back to remain the original item
			if (getCurrentIndex() >= 2) this.back();
			BindUtils.postNotifyChange(null, null, this, "items");
		} else {
			// Only can be added in the first 2 steps
			if (getCurrentIndex() < 2) {
				getInnerItems().add(2, _carStep);
				_hasCar = true;
				BindUtils.postNotifyChange(null, null, this, "items");
			}
		}
	}

	public void switchHistorySupport() {
		_hasHistorySupport = !_hasHistorySupport;
		setHistoryStateGenerator(_hasHistorySupport ? _generator : null);
	}

	private static Map<String, Object> createState(String text, int index) {
		Map<String, Object> state = new HashMap<>();
		state.put("item", text);
		state.put("index", index);
		return state;
	}
}
