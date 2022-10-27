/* ISpanTest.java

	Purpose:

	Description:

	History:
		4:31 PM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Label;
import org.zkoss.zul.Span;

/**
 * Test for {@link ISpan}
 * @author jumperchen
 */
public class ISpanTest  extends StatelessTestBase {
	@Test
	public void withSpan() {
		// check Richlet API case
		assertEquals(richlet(() -> ISpan.of(getSpanChildren())), zul(ISpanTest::newSpan));

		// check Stateless file case
		assertEquals(composer(ISpanTest::newSpan), zul(ISpanTest::newSpan));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Span span = new Span();
					span.appendChild(new Label("2"));
					return span;
				}, (ISpan<IAnyGroup> iSpan) -> iSpan.withChildren(getSpanChildren())),
				zul(ISpanTest::newSpan));
	}

	private static Span newSpan() {
		Span span = new Span();
		Span span2 = new Span();
		Span span3 = new Span();
		span.appendChild(new Label("1"));
		span2.appendChild(span);
		span3.appendChild(span2);
		return span3;
	}

	private static ISpan getSpanChildren() {
		return ISpan.of(ISpan.of(ILabel.of("1")));
	}
}