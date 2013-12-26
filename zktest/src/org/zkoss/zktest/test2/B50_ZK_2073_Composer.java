package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.util.*;
import org.zkoss.zul.*;
import java.util.*;

public class B50_ZK_2073_Composer extends GenericForwardComposer {

	Listbox lb;

	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		if (lb != null) {
			initList();
		}
	}

	private void initList() {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			list.add("item " + i);
		}
		lb.setModel(new SimpleListModel(list));
	}

}
