/* INumberInputElementRichlet.java

	Purpose:

	Description:

	History:
		Wed Feb 23 17:50:05 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.base_components;

import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDecimalbox;
import org.zkoss.zephyr.zpr.IDoublebox;
import org.zkoss.zephyr.zpr.IDoublespinner;
import org.zkoss.zephyr.zpr.IVlayout;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.INumberInputElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/NumberInputElement">INumberInputElement</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.INumberInputElement
 */
@RichletMapping("/base_components/iNumberInputElement")
public class INumberInputElementRichlet implements StatelessRichlet {

	@RichletMapping("/locale")
	public IComponent locale() {
		IDecimalbox iDecimalbox = IDecimalbox.of("2000.02").withFormat("#,###.00");
		IDoublebox iDoublebox = IDoublebox.of(2000.02).withFormat("#,###.00");
		IDoublespinner iDoublespinner = IDoublespinner.of(2000.02).withFormat("#,###.00");
		return IVlayout.of(
				iDecimalbox.withLocale("zh_TW"),
				iDecimalbox.withLocale("fr"),
				iDecimalbox.withLocale("it"),
				iDoublebox.withLocale("zh_TW"),
				iDoublebox.withLocale("fr"),
				iDoublebox.withLocale("it"),
				iDoublespinner.withLocale("zh_TW"),
				iDoublespinner.withLocale("fr"),
				iDoublespinner.withLocale("it")
		);
	}

	@RichletMapping("/roundingMode")
	public IComponent roundingMode() {
		IDecimalbox iDecimalbox = IDecimalbox.of("1.23").withFormat("0.#");
		IDoublebox iDoublebox = IDoublebox.of(1.23).withFormat("0.#");
		IDoublespinner iDoublespinner = IDoublespinner.of(1.23).withFormat("0.#");
		return IVlayout.of(
				iDecimalbox.withRoundingMode(0),
				iDecimalbox.withRoundingMode(1),
				iDoublebox.withRoundingMode(0),
				iDoublebox.withRoundingMode(1),
				iDoublespinner.withRoundingMode(0),
				iDoublespinner.withRoundingMode(1)
		);
	}
}