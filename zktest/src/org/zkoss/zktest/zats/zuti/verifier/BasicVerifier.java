/** BasicVerifier.java.

	Purpose:
		
	Description:
		
	History:
		4:49:23 PM Nov 6, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.verifier;

import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.HtmlShadowElement;
import static org.junit.Assert.*;
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
		assertNotNull("The previous insertion shouldn't be null", shadow.getPreviousInsertion());
	}
	protected void assertNotLastChild(HtmlShadowElement shadow) {
		assertNotNull("The next insertion shouldn't be null", shadow.getNextInsertion());
	}
	protected void assertNotEmpty(HtmlShadowElement shadow) {
		assertNotNull("The first insertion shouldn't be null", shadow.getFirstInsertion());
		assertNotNull("The last insertion shouldn't be null", shadow.getLastInsertion());
	}
	protected void assertNotOnlySon(HtmlShadowElement shadow) {
		if (shadow.getPreviousInsertion() == null && shadow.getNextInsertion() == null) {
			fail("The shadow shouldn't be only son.");
		} 
	}
	protected void assertFirstChild(HtmlShadowElement shadow) {
		assertNull("The previous insertion should be null", shadow.getPreviousInsertion());
	}
	protected void assertLastChild(HtmlShadowElement shadow) {
		assertNull("The next insertion should be null", shadow.getNextInsertion());
	}
	protected void assertEmpty(HtmlShadowElement shadow) {
		assertNull("The first insertion should be null", shadow.getFirstInsertion());
		assertNull("The last insertion should be null", shadow.getLastInsertion());
	}
	protected void assertOnlySon(HtmlShadowElement shadow) {
		assertFirstChild(shadow);
		assertLastChild(shadow);
	}
}
