/* ISelectorsTest.java

	Purpose:
		
	Description:
		
	History:
		2:38 PM 2022/8/18, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.ui;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.ui.ISelector;
import org.zkoss.stateless.ui.ISelectors;
import org.zkoss.stateless.sul.IBorderlayout;
import org.zkoss.stateless.sul.ICaption;
import org.zkoss.stateless.sul.ICenter;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.IFrozen;
import org.zkoss.stateless.sul.IHlayout;
import org.zkoss.stateless.sul.INorth;
import org.zkoss.stateless.sul.IPanel;
import org.zkoss.stateless.sul.ISouth;
import org.zkoss.stateless.sul.ITree;
import org.zkoss.stateless.sul.IVlayout;
import org.zkoss.stateless.sul.IWindow;

/**
 * @author jumperchen
 */
public class ISelectorsTest {
	private static IComponent tree1 = IDiv.of(
			IWindow.ofId("A").withCaption(ICaption.ofId("A0")),
			IPanel.ofId("B"),
			IBorderlayout.ofId("C")
					.withNorth(INorth.ofId("C0"))
					.withCenter(ICenter.ofId("C1"))
					.withSouth(ISouth.ofId("C2")
							.withChild(ITree.ofId("C20")
									.withFrozen(IFrozen.ofId("C200")
					))),
			IVlayout.ofId("D").withChildren(IHlayout.ofId("D0")));
	@Test
	public void testFindById() {
		assertEquals(ISelectors.findById(tree1, "C1"), ICenter.ofId("C1"));
	}

	@Test
	public void testSelector() {
		ISelector selector = ISelectors.select(tree1);
		ITree c20 = selector.get("C20");
		int[] c20Path = new int[] {2,2,0};

		// check by path
		assertArrayEquals(selector.getPath(c20), c20Path);

		// check root
		assertEquals(selector.get(new int[0]), tree1);

		// check by id
		assertEquals(selector.get(c20.getId()), c20);

		// check parent
		assertEquals((IComponent) selector.getParent(c20), selector.get(new int[]{2, 2}));

		// check ancestor
		assertEquals(selector.getAncestor(c20),
				Arrays.asList(tree1, selector.get(new int[] {2}),
						selector.get(new int[] {2, 2})));
	}
}
