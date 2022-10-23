/* ILabelTest.java

	Purpose:
		
	Description:
		
	History:
		2:21 PM 2021/10/1, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.zkoss.zephyr.action.ActionType.onClick;
import static org.zkoss.zephyr.action.ActionType.onSelect;

import java.util.Date;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.action.ActionType;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.ActionVariable;
import org.zkoss.zephyr.mock.ZephyrTestBase;
import org.zkoss.zephyr.util.ActionHandler;
import org.zkoss.zephyr.util.ActionHandler2;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Label;

/**
 * Test for {@link ILabel}
 * @author jumperchen
 */
public class ILabelTest extends ZephyrTestBase {
	@Test
	public void withLabel() {
		// check Richlet API case
		assertEquals(richlet(() -> ILabel.of("abc")), zul(ILabelTest::newLabel));

		// check Zephyr file case
		assertEquals(composer(ILabelTest::newLabel), zul(ILabelTest::newLabel));

		// check Zephyr file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Label("123"), (ILabel ilabel) -> ilabel.withValue("abc")),
				zul(ILabelTest::newLabel));
	}

	private static Label newLabel() {
		return new Label("abc");
	}

	private static ILabel instance = ILabel.of("dummy");

	@Test
	public void withActionAnnotation() {
		instance.withAction(this::doAction)
				.withActions(ActionHandler.of(this::doAction1),
						ActionHandler.of(this::doAction2))
				.withAction(this::doAction3);
	}

	@Test void withActionType() {
		instance.withAction(onClick(this::doAction))
				.withActions(onSelect(this::doAction1),
						onClick(() -> System.out.println("Should work")));
	}

	@Test void withActionLambda() {
		instance.withAction(onClick((a, b, c, d, e, f, g, h, i) -> System.out.println(
				"Should work"))).withActions(onSelect(
						(@ActionVariable(targetId = "id") String id) -> System.out.println(
								"Should work")),
				onClick(() -> System.out.println("Should work")));
	}

	@Test void withActionAnonymousClass() {
		try {
			instance.withAction(new ActionHandler2<Object, Object>() {
				public void accept(Object o, Object o2) throws Exception {
					System.out.println("Should not run into");
				}
			});
			fail("Should not run into this line");
		} catch (IllegalArgumentException ex) {
			// should throw exception
		} catch (Throwable t) {
			fail("Should not run into this line");
		}
		try {
			instance.withActions(onClick(new ActionType.OnClick2<Object, Object>() {
				public void accept(Object o, Object o2) throws Exception {
					System.out.println("Should not run into");
				}
			}));
			fail("Should not run into this line");
		} catch (IllegalArgumentException ex) {
			// should throw exception
		} catch (Throwable t) {
			fail("Should not run into this line");
		}
	}


	@Action(type = Events.ON_CLICK)
	private void doAction() {}

	@Action(type = Events.ON_CLICK)
	private void doAction1(String var1) {}

	@Action(type = Events.ON_CLICK)
	private void doAction2(int var1, String var2) {}

	@Action(type = Events.ON_CLICK)
	private void doAction3(Date var1, String var2, String var3) {}
}
