package org.zkoss.zktest.test2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.Init;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.zul.*;


public class B80_ZK_3514VM {
	private List _list1;
	private ListModelList _modelList1;
	private List _list2;
	private ListModelList _modelList2;
	@Init
	public void init() {
		List list1 = prepareData(null, 500);
		List list2 = prepareData(null, 500);
		List list3 = prepareData(null, 500);
		List list4 = prepareData(null, 500);
		_list1 = list1;
		_modelList1 = new ListModelList(list2);
		_list2 = list3;
		_modelList2 = new ListModelList(list4);
	}

	private List prepareData(List list, int count) {
		if (list == null)
			list = new ArrayList();
		for (long l = 1; l <= count; l++) {
			PlacementView v = new PlacementView();
			v.setPlacementReferenceNo("test-" + l);
			v.setPlacementproductName("testvalue-" + l);
			list.add(v);
		}
		return list;
	}

	@Command
	@NotifyChange("list1")
	public void changeModel1$Listbox() {
		List list = prepareData(null, 20);
		_list1 = list;
	}

	@Command
	@NotifyChange("list1")
	public void clearListNull$Listbox() {
		_list1 = null;
	}

	@Command
	@NotifyChange("list1")
	public void clearList$Listbox() {
		_list1.clear();
	}

	@Command
	@NotifyChange("modelList1")
	public void changeModel2$Listbox() {
		List list = prepareData(null, 20);
		_modelList1 = new ListModelList(list);
	}

	@Command
	@NotifyChange("modelList1")
	public void clearModelListNull$Listbox() {
		_modelList1 = null;
	}

	@Command
	@NotifyChange("modelList1")
	public void clearModelList$Listbox() {
		_modelList1.clear();
	}


	@Command
	@NotifyChange("list2")
	public void changeModel1$Grid() {
		List list = prepareData(null, 20);
		_list2 = list;
	}

	@Command
	@NotifyChange("list2")
	public void clearListNull$Grid() {
		_list2 = null;
	}

	@Command
	@NotifyChange("list2")
	public void clearList$Grid() {
		_list2.clear();
	}

	@Command
	@NotifyChange("modelList2")
	public void changeModel2$Grid() {
		_modelList2 = new ListModelList(prepareData(null, 20));
	}

	@Command
	@NotifyChange("modelList2")
	public void clearModelListNull$Grid() {
		_modelList2 = null;
	}

	@Command
	@NotifyChange("modelList2")
	public void clearModelList$Grid() {
		_modelList2.clear();
	}

	public List getList1() {
		return _list1;
	}

	public ListModelList getModelList1() {
		return _modelList1;
	}

	public List getList2() {
		return _list2;
	}

	public ListModelList getModelList2() {
		return _modelList2;
	}

	public class PlacementView {
		String placementReferenceNo;
		String placementproductName;

		String getPlacementReferenceNo() {
			return placementReferenceNo;
		}

		public void setPlacementReferenceNo(String placementReferenceNo) {
			this.placementReferenceNo = placementReferenceNo;
		}

		public String getPlacementproductName() {
			return placementproductName;
		}

		public void setPlacementproductName(String placementproductName) {
			this.placementproductName = placementproductName;
		}

		public String getPlacementRefNoWithProductName() {
			return this.placementReferenceNo + "-" + this.placementproductName;
		}
	}

}
