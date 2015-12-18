/** ManipulateInsertion2Test.java.

	Purpose:
		
	Description:
		
	History:
		5:23:24 PM Nov 10, 2014, Created by jumperchen

Copyright (C) 2014 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.zuti.mvvm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.zkoss.zats.mimic.ComponentAgent;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zktest.zats.zuti.ZutiBasicTestCase;
import org.zkoss.zktest.zats.zuti.verifier.HierarchyVerifier;

/**
 * @author jumperchen
 *
 */
public class ManipulateInsertion2Test extends ZutiBasicTestCase {
	@Test
	public void testResult() {
		DesktopAgent desktop = connect();

		ComponentAgent host = desktop.query("#host");
		assertTrue(host.getChildren().size() == 13);
		
		AbstractComponent hostCmp = host.as(Div.class);
		List<AbstractComponent> children = hostCmp.getChildren();
		for (AbstractComponent cmp : children)
			assertEquals(cmp.toString(), ((Label)cmp.getFirstChild()).getValue());
		
		checkVerifier(hostCmp, HierarchyVerifier.class);
	}
	
	@Test
	public void testMvvm() {
		DesktopAgent desktop = connect();

		ComponentAgent host = desktop.query("#host");
		assertTrue(host.getChildren().size() == 13);
		
		int total = getShadowSize(host);
		assertEquals("Only 11 shadow element available", 11, total);
		AbstractComponent hostCmp = host.as(Div.class);
		List<AbstractComponent> children = hostCmp.getChildren();
		for (AbstractComponent cmp : children)
			assertEquals(cmp.toString(), ((Label)cmp.getFirstChild()).getValue());
		
		checkVerifier(hostCmp, HierarchyVerifier.class);
		
		ComponentAgent button = desktop.query("#changeVisible");
		button.click();
		
		assertTrue(host.getChildren().size() == 3);
		children = hostCmp.getChildren();
		for (AbstractComponent cmp : children)
			assertEquals(cmp.toString(), ((Label)cmp.getFirstChild()).getValue());
		
		total = getShadowSize(host);
		assertEquals("Only 11 shadow element available", 8, total);
	}
	
	@Test
	public void testRemoveFromFirst() {
		DesktopAgent desktop = connect();

		AbstractComponent hostCmp = desktop.query("#host").as(Div.class);
		ComponentAgent removeFirst = desktop.query("#removeFirst");
		
		for (int i = 0, j = hostCmp.getChildren().size(); i < j; i++) {
			int prev = hostCmp.getChildren().size();
			removeFirst.click();
			assertNotSame((Integer)prev, (Integer)hostCmp.getChildren().size());
			checkVerifier(hostCmp, HierarchyVerifier.class);
		}
	}
	
	@Test
	public void testRemoveFromFirstThenLast() {
		DesktopAgent desktop = connect();

		AbstractComponent hostCmp = desktop.query("#host").as(Div.class);
		ComponentAgent removeFirst = desktop.query("#removeFirst");
		ComponentAgent removeLast = desktop.query("#removeLast");
		
		int total = hostCmp.getChildren().size();
		int mid = total / 2;
		for (int i = 0, j = mid ; i < j; i++) {
			int prev = hostCmp.getChildren().size();
			removeFirst.click();
			assertNotSame((Integer)prev, (Integer)hostCmp.getChildren().size());
			checkVerifier(hostCmp, HierarchyVerifier.class);
		}
		for (int i = 0, j = total - mid ; i < j; i++) {
			removeLast.click();
			checkVerifier(hostCmp, HierarchyVerifier.class);
		}
	}
	
	@Test
	public void testRemoveFromLast() {
		DesktopAgent desktop = connect();

		AbstractComponent hostCmp = desktop.query("#host").as(Div.class);
		ComponentAgent removeLast = desktop.query("#removeLast");
		
		for (int i = 0, j = hostCmp.getChildren().size(); i < j; i++) {
			int prev = hostCmp.getChildren().size();
			removeLast.click();
			assertNotSame((Integer)prev, (Integer)hostCmp.getChildren().size());
			checkVerifier(hostCmp, HierarchyVerifier.class);
		}
	}
	
	@Test
	public void testRemoveFromLastThenFirst() {
		DesktopAgent desktop = connect();

		AbstractComponent hostCmp = desktop.query("#host").as(Div.class);
		ComponentAgent removeFirst = desktop.query("#removeFirst");
		ComponentAgent removeLast = desktop.query("#removeLast");
		
		int total = hostCmp.getChildren().size();
		int mid = total / 2;
		for (int i = 0, j = mid ; i < j; i++) {
			int prev = hostCmp.getChildren().size();
			removeLast.click();
			assertNotSame((Integer)prev, (Integer)hostCmp.getChildren().size());
			checkVerifier(hostCmp, HierarchyVerifier.class);
		}
		for (int i = 0, j = total - mid ; i < j; i++) {
			int prev = hostCmp.getChildren().size();
			removeFirst.click();
			assertNotSame((Integer)prev, (Integer)hostCmp.getChildren().size());
			checkVerifier(hostCmp, HierarchyVerifier.class);
		}
	}
	
	@Test
	public void testRemoveFromRandom() {
		DesktopAgent desktop = connect();

		AbstractComponent hostCmp = desktop.query("#host").as(Div.class);
		ComponentAgent removeFirst = desktop.query("#removeFirst");
		ComponentAgent removeLast = desktop.query("#removeLast");
		Random rnd = new Random();
		
		for (int i = 0, j = hostCmp.getChildren().size(); i < j; i++) {
			int prev = hostCmp.getChildren().size();
			if (rnd.nextBoolean()) {
				removeFirst.click();
			} else {
				removeLast.click();
			}
			assertNotSame((Integer)prev, (Integer)hostCmp.getChildren().size());
			checkVerifier(hostCmp, HierarchyVerifier.class);
		}
	}
	
	@Test
	public void testInsertionOperation() {
		DesktopAgent desktop = connect();

		AbstractComponent hostCmp = desktop.query("#host").as(Div.class);
		
		for (String btnId : Arrays.asList("#addBeforeFirst", "#addBeforeFirst",
				"#addBeforeLast", "#addAfterLast")) {
			int prev = hostCmp.getChildren().size();
			desktop.query(btnId).click();
			assertNotSame((Integer)prev, (Integer)hostCmp.getChildren().size());
			checkVerifier(hostCmp, HierarchyVerifier.class);
		}
	}

	
	@Test
	public void testMixinOperation() {
		DesktopAgent desktop = connect();

		AbstractComponent hostCmp = desktop.query("#host").as(Div.class);

		ComponentAgent removeFirst = desktop.query("#removeFirst");
		ComponentAgent removeLast = desktop.query("#removeLast");
		Random rnd = new Random();
		
		for (String btnId : Arrays.asList("#addBeforeFirst", "#addBeforeFirst", "#addBeforeLast", "#addAfterLast")) {
			ArrayList<ComponentAgent> oldChild = new ArrayList(hostCmp.getChildren());
			desktop.query(btnId).click();
			if (rnd.nextBoolean()) {
				System.out.println("true");
				removeFirst.click();
			} else {
				System.out.println("false");
				removeLast.click();
			}

			assertNotSame(oldChild, hostCmp.getChildren());
			checkVerifier(hostCmp, HierarchyVerifier.class);
		}
	}
}
