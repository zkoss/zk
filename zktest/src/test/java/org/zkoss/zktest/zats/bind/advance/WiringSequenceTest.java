/* WiringSequenceTest.java

		Purpose:
		
		Description:
		
		History:
				Thu May 06 14:40:44 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.bind.advance;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class WiringSequenceTest extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();

		Assertions.assertArrayEquals(
			new String[] {
				"The variable resolver defined in the ZUML document."
			},
			desktop.getZkLog().toArray()
		);
	}

	@Test
	public void test2() {
		DesktopAgent desktop = connect("/bind/advance/WiringSequence-2.zul");

		Assertions.assertArrayEquals(
			new String[] {
				"The variable resolver annotated registered with the VariableResolver annotation."
			},
			desktop.getZkLog().toArray()
		);
	}
}
