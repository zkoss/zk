package org.zkoss.zktest.test2;


import org.zkoss.bind.annotation.Init;
import org.zkoss.zul.SimpleListModel;


public class B65_ZK_2021 {

	private SimpleListModel<String> comboModel;

	
	@Init
	public void init() {
		comboModel = new SimpleListModel<String>(B65_ZK_2021_Dictionary.getDirectory()) {

			@Override
			protected boolean inSubModel(Object key, Object value) {
				return key == null || value.toString().startsWith(key.toString());
			}
			
			@Override
			protected int getMaxNumberInSubModel(int nRows) {
				return 3000;
			}
		};
	}
	
	public SimpleListModel<String> getComboModel() {
		return comboModel;
	}

}
