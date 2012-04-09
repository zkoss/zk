package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.sys.IdGenerator;

public class F60_882_IDGenerator implements IdGenerator {

	public String nextComponentUuid(Desktop desktop, Component comp,
			ComponentInfo compInfo) {

		String id = null;
		if(compInfo != null){
			for (org.zkoss.zk.ui.metainfo.Property pro : compInfo.getProperties()) {
				if (pro != null && "id".equals(pro.getName())) {
					id = (String) pro.getValue(comp);
					break;
				}
			}
		}

		return id == null ? null : id + "_Hi";
	}

	public String nextPageUuid(Page page) {
		return null;
	}

	public String nextDesktopId(Desktop desktop) {
		return null;
	}

}
