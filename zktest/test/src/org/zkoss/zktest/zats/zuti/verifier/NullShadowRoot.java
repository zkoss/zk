/** NullShadowRoot.java.

	Purpose:
		
	Description:
		
	History:
		4:48:37 PM Nov 6, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.verifier;

import static org.junit.Assert.*;

/**
 * @author jumperchen
 *
 */
public class NullShadowRoot extends BasicVerifier {

	public void verify() {
		assertTrue("Shadow root should be empty.", getHost().getShadowRoots().isEmpty());
	}
}
