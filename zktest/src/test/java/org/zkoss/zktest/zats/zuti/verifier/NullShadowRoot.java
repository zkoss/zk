/** NullShadowRoot.java.

	Purpose:
		
	Description:
		
	History:
		4:48:37 PM Nov 6, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.verifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author jumperchen
 *
 */
public class NullShadowRoot extends BasicVerifier {

	public void verify() {
		assertTrue(getHost().getShadowRoots().isEmpty(),
				"Shadow root should be empty.");
	}
}
