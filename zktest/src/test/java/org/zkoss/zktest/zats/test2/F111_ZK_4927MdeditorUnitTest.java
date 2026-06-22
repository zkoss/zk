/* F111_ZK_4927MdeditorUnitTest.java

	Purpose:

	Description:

	History:
		Wed Nov 04 16:44:43 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zkex.zul.Mdeditor;

public class F111_ZK_4927MdeditorUnitTest {
	@Test
	public void modeDefaultsToSource() {
		assertEquals("source", new Mdeditor().getMode());
	}

	@Test
	public void modeAcceptsEveryLayout() {
		for (String mode : new String[] {"source", "preview", "split"}) {
			Mdeditor m = new Mdeditor();
			m.setMode(mode);
			assertEquals(mode, m.getMode());
		}
	}

	@Test
	public void modeNullFallsBackToSource() {
		Mdeditor m = new Mdeditor();
		m.setMode("split");
		m.setMode(null);
		assertEquals("source", m.getMode());
	}

	@Test
	public void modeRejectsWrongCase() {
		assertThrows(WrongValueException.class, () -> new Mdeditor().setMode("Split"));
	}

	@Test
	public void modeRejectsUnknown() {
		assertThrows(WrongValueException.class, () -> new Mdeditor().setMode("wysiwyg"));
	}

	@Test
	public void languageIsFixedToMarkdown() {
		Mdeditor m = new Mdeditor();
		assertEquals("markdown", m.getLanguage());
		m.setLanguage(null);
		assertEquals("markdown", m.getLanguage());
	}

	@Test
	public void languageRejectsAnotherGrammar() {
		assertThrows(UnsupportedOperationException.class, () -> new Mdeditor().setLanguage("java"));
	}
}
