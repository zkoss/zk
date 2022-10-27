/* IBandboxTest.java

	Purpose:

	Description:

	History:
		Fri Oct 08 14:39:39 CST 2021, Created by katherine

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
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Bandbox;
import org.zkoss.zul.Bandpopup;
import org.zkoss.zul.Label;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.zkoss.stateless.action.ActionType.onClick;
import static org.zkoss.stateless.action.ActionType.onSelect;

/**
 * Test for {@link IBandbox}
 *
 * @author katherine
 */
public class IBandboxTest extends StatelessTestBase {
	@Test
	public void withBandbox() {
		// check Richlet API case
		assertEquals(richlet(() -> IBandbox.of("abc", IBandpopup.of(bandpopupChildren()))), zul(IBandboxTest::newBandbox));
		// check Stateless file case
		assertEquals(composer(IBandboxTest::newBandbox), zul(IBandboxTest::newBandbox));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Bandbox bandbox = new Bandbox("abc");
					Bandpopup bandpopup = new Bandpopup();
					bandpopup.appendChild(new Label("123"));
					bandbox.appendChild(bandpopup);
					return bandbox;
				}, (IBandbox iBandbox) -> iBandbox.withValue("abc").withChild(IBandpopup.of(ILabel.of("abc")))),
				zul(IBandboxTest::newBandbox));
	}
	@Test
	public void withMultiline() {
		try {
			IBandbox.of("abc", IBandpopup.of(bandpopupChildren())).withMultiline(true);
		} catch (UnsupportedOperationException ex) {
			assertEquals("Bandbox doesn't support multiline", ex.getMessage());
		}
	}
	@Test
	public void withRows() {
		int rows = 2;
		try {
			IBandbox.of("abc", IBandpopup.of(bandpopupChildren())).withRows(rows);
		} catch (UnsupportedOperationException ex) {
			assertEquals("Bandbox doesn't support multiple rows, " + rows, ex.getMessage());
		}
	}

	private static Bandbox newBandbox() {
		Bandbox bandbox = new Bandbox("abc");
		Bandpopup bandpopup = new Bandpopup();
		bandpopup.appendChild(new Label("abc"));
		bandbox.appendChild(bandpopup);
		return bandbox;
	}

	private static List bandpopupChildren() {
		List children = new ArrayList();
		children.add(ILabel.of("abc"));
		return children;
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