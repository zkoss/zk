/**
 * 
 */
package org.zkoss.zktest.test2;

import java.util.Map;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Initiator;

/**
 * @author jumperchen
 *
 */
public class B70_ZK_2649RedirectInit implements Initiator  {

	public void doInit(Page page, Map<String, Object> args) throws Exception {
		Executions.sendRedirect("B70-ZK-2649.zul");
	}

}
