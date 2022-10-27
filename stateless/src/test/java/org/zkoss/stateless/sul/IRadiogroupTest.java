/* IRadiogroupTest.java

	Purpose:

	Description:

	History:
		Fri Dec 10 17:46:37 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Radiogroup;

/**
 * Test for {@link IRadiogroup}
 *
 * @author katherine
 */
public class IRadiogroupTest extends StatelessTestBase {
	@Test
	public void withRadiogroup() {
		// check Richlet API case
		IRadio r1 = IRadio.of("a").withSelected(true);
		IRadio r2 = IRadio.of("b");
		composer(IRadiogroupTest::newRadiogroup);
		assertEquals(trimRadiogroup(richlet(() -> IRadiogroup.of(r1, r2))), trimRadiogroup(zul(IRadiogroupTest::newRadiogroup)));


		// check Stateless file case
		assertEquals(trimRadiogroup(composer(IRadiogroupTest::newRadiogroup)), trimRadiogroup(zul(IRadiogroupTest::newRadiogroup)));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				trimRadiogroup(thenComposer(() -> {
					Radiogroup radiogroup = new Radiogroup();
					radiogroup.appendChild(new Radio("a1"));
					radiogroup.appendChild(new Radio("b"));
					return radiogroup;
				}, (IRadiogroup<IAnyGroup> iRadiogroup) -> iRadiogroup.withChildren(r1, r2))),
				trimRadiogroup(zul(IRadiogroupTest::newRadiogroup)));
	}

	@Test
	public void withRadiogroupDisable() {
		IRadio r1 = IRadio.of("a").withSelected(true);
		IRadio r2 = IRadio.of("b");
		assertEquals(trimRadiogroup(richlet(() -> IRadiogroup.of(r1, r2).withDisabled(true))), trimRadiogroup(zul(IRadiogroupTest::newRadiogroup2)));
		assertEquals(trimRadiogroup(composer(IRadiogroupTest::newRadiogroup2)), trimRadiogroup(zul(IRadiogroupTest::newRadiogroup2)));
	}

	private static Radiogroup newRadiogroup() {
		Radiogroup radiogroup = new Radiogroup();
		radiogroup.appendChild(new Radio("a"));
		radiogroup.appendChild(new Radio("b"));
		radiogroup.setSelectedIndex(0);
		return radiogroup;
	}

	private static Radiogroup newRadiogroup2() {
		Radiogroup radiogroup = new Radiogroup();
		Radio r1 = new Radio("a");
		r1.setSelected(true);
		radiogroup.appendChild(r1);
		radiogroup.appendChild(new Radio("b"));
		radiogroup.setDisabled(true);
		return radiogroup;
	}

	private String trimRadiogroup(String content) {
		return content.replaceAll("name:'[^']+'", "name:''")
				.replaceAll(",name:''", "")
				.replaceAll(",radiogroup:\\{[^}]*\\}", "");
	}
}