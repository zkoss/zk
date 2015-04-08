/** HierarchyVerifier.java.

	Purpose:
		
	Description:
		
	History:
		5:02:30 PM Nov 6, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.verifier;

import java.util.List;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.ShadowElement;

/**
 * @author jumperchen
 *
 */
public class HierarchyVerifier extends BasicVerifier {

	protected void verify() {
		AbstractComponent host = getHost();
		List<Component> children = getHost().getChildren();
		List<ShadowElement> shadowRoots = host.getShadowRoots();
		switch (shadowRoots.size()) {
		case 0:
			// nothing to do.
			break;
		default:
			new ShadowTreeVerifier().verify(host);
		}
	}

}
