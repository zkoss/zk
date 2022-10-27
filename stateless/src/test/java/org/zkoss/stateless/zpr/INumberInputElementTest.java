/* INumberInputElementTest.java

	Purpose:

	Description:

	History:
		Wed Oct 27 12:08:11 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Doublespinner;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Spinner;

/**
 * Test for {@link INumberInputElement}
 *
 * @author katherine
 */
public class INumberInputElementTest extends StatelessTestBase {
	@Test
	public void withBox() {
		List children = new ArrayList<>();
		children.add(IIntbox.ofCols(10));
		children.add(IDoublebox.ofCols(10));
		children.add(IDoublespinner.of(10.1));
		children.add(ISpinner.of(10).withStep(2));
		children.add(ILongbox.of(100000L));
		children.add(IDecimalbox.of(new java.math.BigDecimal(10)));
		// check Richlet API case
		assertEquals(richlet(() -> IDiv.of(children)), zul(INumberInputElementTest::newDiv));

		// check Stateless file case
		assertEquals(composer(INumberInputElementTest::newDiv), zul(INumberInputElementTest::newDiv));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Div div = new Div();
					Intbox intbox = new Intbox();
					intbox.setCols(10);
					Doublebox doublebox = new Doublebox();
					doublebox.setCols(10);
					Doublespinner doublespinner = new Doublespinner(10.1);
					Spinner spinner = new Spinner(11);
					spinner.setStep(2);
					Longbox longbox = new Longbox(100000L);
					Decimalbox decimalbox = new Decimalbox(new java.math.BigDecimal(10));
					div.appendChild(intbox);
					div.appendChild(doublebox);
					div.appendChild(doublespinner);
					div.appendChild(spinner);
					div.appendChild(longbox);
					div.appendChild(decimalbox);
					return div;
				}, (IDiv<IAnyGroup> iDiv) -> iDiv.withChildren(children)), zul(INumberInputElementTest::newDiv));
	}

	private static Div newDiv() {
		Div div = new Div();
		Intbox intbox = new Intbox();
		intbox.setCols(10);
		Doublebox doublebox = new Doublebox();
		doublebox.setCols(10);
		Doublespinner doublespinner = new Doublespinner(10.1);
		Spinner spinner = new Spinner(10);
		spinner.setStep(2);
		Longbox longbox = new Longbox(100000L);
		Decimalbox decimalbox = new Decimalbox(new java.math.BigDecimal(10));
		div.appendChild(intbox);
		div.appendChild(doublebox);
		div.appendChild(doublespinner);
		div.appendChild(spinner);
		div.appendChild(longbox);
		div.appendChild(decimalbox);
		return div;
	}
}