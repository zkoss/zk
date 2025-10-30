/** BasicVerifier.java.

	Purpose:
		
	Description:
		
	History:
		4:49:23 PM Nov 6, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.verifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.HtmlShadowElement;
/**
 * @author jumperchen
 *
 */
public abstract class BasicVerifier implements Verifier {
	protected AbstractComponent host;
	public void verify(AbstractComponent host) {
		this.host = host;
		verify();
	}
	protected abstract void verify();
	
	protected AbstractComponent getHost() {
		return host;
	}
	
	protected void assertNotFirstChild(HtmlShadowElement shadow) {
		assertNotNull(shadow.getPreviousInsertion(), "The previous insertion shouldn't be null");
	}
	protected void assertNotLastChild(HtmlShadowElement shadow) {
		assertNotNull(shadow.getNextInsertion(), "The next insertion shouldn't be null");
	}
	protected void assertNotEmpty(HtmlShadowElement shadow) {
		assertNotNull(shadow.getFirstInsertion(), "The first insertion shouldn't be null");
		assertNotNull(shadow.getLastInsertion(), "The last insertion shouldn't be null");
	}
	protected void assertNotOnlySon(HtmlShadowElement shadow) {
		if (shadow.getPreviousInsertion() == null && shadow.getNextInsertion() == null) {
			fail("The shadow shouldn't be only son.");
		} 
	}
	protected void assertFirstChild(HtmlShadowElement shadow) {
		assertNull(shadow.getPreviousInsertion(), "The previous insertion should be null");
	}
	protected void assertLastChild(HtmlShadowElement shadow) {
		assertNull(shadow.getNextInsertion(), "The next insertion should be null");
	}
	protected void assertEmpty(HtmlShadowElement shadow) {
		assertNull(shadow.getFirstInsertion(), "The first insertion should be null");
		assertNull(shadow.getLastInsertion(), "The last insertion should be null");
	}
	protected void assertOnlySon(HtmlShadowElement shadow) {
		assertFirstChild(shadow);
		assertLastChild(shadow);
	}
}
