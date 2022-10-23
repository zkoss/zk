package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.ListSubModel;

public class B65_ZK_1746ViewModel {
	private ListModel chosenboxModel = new BigDataListModel();
	private ListModel comboboxModel = new BigDataListModel();

	public ListModel getComboboxModel() {
		return comboboxModel;
	}

	public ListModel getChosenboxModel() {
		return chosenboxModel;
	}

	class BigDataListModel extends AbstractListModel implements ListSubModel {

		public Object getElementAt(int index) {
			String string = Integer.valueOf(index).toString(index, 36);
			Clients.log("getElementAt called for : " + index + " -> " + string);
			return string;
		}

		public int getSize() {
			return 50000;
		}

		public ListModel getSubModel(Object value, int nRows) {
			Clients.log(String.format("sublist called: \"%s\", %d", new Object[]{value, nRows}));
			ListModelList subList = new ListModelList();
			if ("".equals(value)) {
				return subList;
			}
			for (int i = 0; i < 10; i++) {
				long number = Long.parseLong((String) value + i, 36);
				if (number > getSize()) {
					break;
				}
				subList.add(getElementAt((int) number));
			}
			Clients.log(subList.toString());
			return subList;
		}
	}
}
