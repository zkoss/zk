/* IButtonTest.java

	Purpose:

	Description:

	History:
		Tue Oct 05 16:23:28 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import org.junit.jupiter.api.Test;
import org.zkoss.stateless.action.ActionType;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.util.ActionHandler2;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.zkoss.stateless.action.ActionType.onClick;
import static org.zkoss.stateless.action.ActionType.onSelect;

public class IButtonTest extends StatelessTestBase {
	@Test
	public void withButton() {
		// check Richlet API case
		assertEquals(richlet(() -> IButton.of("abc").withHref("href")), zul(IButtonTest::newButton));

		// check Stateless file case
		assertEquals(composer(IButtonTest::newButton), zul(IButtonTest::newButton));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> new Button("123"), (IButton iButton) -> iButton.withLabel("abc").withHref("href")),
				zul(IButtonTest::newButton));
	}

	@Test
	public void withDir() {
		String dir = "horizontal";
		try {
			IButton.of("abc").withDir(dir);
		} catch (WrongValueException ex) {
			assertEquals(dir, ex.getMessage());
		}
	}

	private static Button newButton() {
		Button btn = new Button("abc");
		btn.setHref("href");
		return btn;
	}

	private static IButton instance = IButton.of("dummy");

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