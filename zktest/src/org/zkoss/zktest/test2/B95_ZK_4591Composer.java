/* B95_ZK_4591Composer.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 13 09:47:04 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;

/**
 * @author rudyhuang
 */
public class B95_ZK_4591Composer extends GenericForwardComposer<Component> {
	private Div thearea;

	public void onClick$btn() throws InterruptedException {
		// Clear existing content
		//
		thearea.getChildren().clear();

		// New content
		//
		StringBuilder zuldata = new StringBuilder();
		zuldata.append("<borderlayout vflex=\"1\">");
		zuldata.append("<center>");
		zuldata.append("<borderlayout vflex=\"1\">");
		zuldata.append("<center>");

		zuldata.append("<tabbox width=\"100%\" vflex=\"1\">");
		zuldata.append("<tabs hflex=\"min\">");

		for (int i = 0; i < 100; ++i) {
			zuldata.append("<tab label=\"Tab ").append(i).append("\" />");
		}

		zuldata.append("</tabs>");
		zuldata.append("<tabpanels>");
		zuldata.append("</tabpanels>");
		zuldata.append("</tabbox>");
		zuldata.append("</center>");
		zuldata.append("</borderlayout>");
		zuldata.append("</center>");
		zuldata.append("</borderlayout>");

		// Load new content
		//
		Executions.createComponentsDirectly(zuldata.toString(), null, thearea, null);
	}
}
