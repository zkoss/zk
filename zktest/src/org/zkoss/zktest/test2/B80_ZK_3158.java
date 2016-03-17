/** B80_ZK_3143.java.

 Purpose:

 Description:

 History:
 		Wed Mar 16 17:12:46 CST 2016, Created by jameschu

 Copyright (C) 2015 Potix Corporation. All Rights Reserved.
 */
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WebApps;
import org.zkoss.zk.ui.metainfo.PageDefinition;
import org.zkoss.zk.ui.metainfo.PageDefinitions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zk.ui.sys.WebAppCtrl;

/**
 * @author jameschu
 *
 */
public class B80_ZK_3158 extends SelectorComposer<Component> {
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
	}

	@Listen("onClick=#btn")
	public void handleBtn(){
		PageDefinition pageDefinitionDirectly = PageDefinitions.getPageDefinitionDirectly(WebApps.getCurrent(), null, "<div></div>", "zul");
		Component[] root = ((WebAppCtrl)WebApps.getCurrent()).getUiEngine().createComponents(Executions.getCurrent(), pageDefinitionDirectly, null, null, null, null, null);

		pageDefinitionDirectly = PageDefinitions.getPageDefinitionDirectly(WebApps.getCurrent(), null, "<div><div><if test='${true}'><label value='test'/></if></div></div>", "zul");
		((WebAppCtrl)WebApps.getCurrent()).getUiEngine().createComponents(Executions.getCurrent(), pageDefinitionDirectly, null, root[0], null, null, null);
		root[0].setPage(this.getPage());
	}

}
