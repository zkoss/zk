package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Window;

public class B65_ZK_2420_Composer extends SelectorComposer<Window> {

	@Wire
	private Window win;
	@Wire
	private Tabbox tabbox1;
	@Wire
	private Listbox fileList;

	public void doAfterCompose(Window comp) throws Exception {
		super.doAfterCompose(comp);
		ListModelList<String> model = new ListModelList<String>();
		for (int i = 0; i < 200; i++)
			model.add("" + i);
		fileList.setModel(model);
	}
	
	@Listen("onClick = #btn1")
	public void unload() {
//		tabbox1.detach();
		fileList.detach();
	}
	
	@Listen("onClick = #btn2")
	public void back() {
//		tabbox1.setParent(win);
		fileList.setParent(win);
	}
}