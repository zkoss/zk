package org.zkoss.zktest.test2;

import java.util.AbstractList;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Window;

public class B60_ZK_1512_Composer extends SelectorComposer<Window> {

	private static final long serialVersionUID = -1255492599436132146L;
	@Wire("#lb")
	private Listbox lb;
	
	@Listen("onClick = #btn3")
	public void pop() {
		Window win = (Window) Executions.createComponents("B60-ZK-1512-1.zul", null, null);
		win.setClosable(true);
		win.doModal();
	}
	@Listen("onClick = #btn1")
	public void set() {
		List<Integer> items = new BigList(100);
		ListModelList<Integer> lml = new ListModelList<Integer>(items);
		lml.setMultiple(true);
		lml.addToSelection(items.get(2));
		lml.addToSelection(items.get(63));
		lb.setModel(lml);
	}
	@Listen("onClick = #btn2")
	public void clr() {
		List<Integer> items = new BigList(0);
      	ListModelList<Integer> lml = new ListModelList<Integer>(items);
      	lml.setMultiple(true);
      	lb.setModel(lml);
		//((ListModelList<?>)lb.getModel()).clear();
	}
	@Listen("onClick = #btn4")
	public void show() {
		Set select = ((org.zkoss.zul.ext.Selectable) lb.getModel()).getSelection();
		alert("Select Number = " + select.size());
	}
	
	public class BigList extends AbstractList<Integer> {
		private int size;
		public BigList(int sz) {
			if (sz < 0)
				throw new IllegalArgumentException("Negative not allowed: " + sz);
			size = sz;
		}
		public int size() {
			return size;
		}
		public Integer get(int j) {
			return Integer.valueOf(j);
		}
	}
}
