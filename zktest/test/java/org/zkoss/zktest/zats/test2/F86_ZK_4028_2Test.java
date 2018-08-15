/* F86_ZK_4028_2Test.java

	Purpose:

	Description:

	History:
		Thu Aug 30 16:55:21 CST 2018, Created by rudyhuang

Copyright (C) 2018 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.util.Pair;
import org.zkoss.zuti.zul.NavigationLevel;
import org.zkoss.zuti.zul.NavigationModel;

/**
 * @author rudyhuang
 */
public class F86_ZK_4028_2Test {
	private NavigationModel<String> model = new NavigationModel<>();

	@Test
	public void testPut() {
		String val1 = model.put("AAA/BBB/CCC", "data1");
		String val2 = model.put("AAA/BBB/CCC", "data2");
		String val3 = model.put(new String[]{"AAA", "BBB", "CCC"}, "data3");

		Assert.assertNull(val1);
		Assert.assertEquals("data1", val2);
		Assert.assertEquals("data2", val3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutNull() {
		model.put((String) null, "data4");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutNull2() {
		model.put("", "data4");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutNull3() {
		model.put((String[]) null, "data4");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testPutNull4() {
		model.put(new String[]{}, "data4");
	}

	@Test
	public void testInsertBefore() {
		model.put("AAA/BBB/CCC", "data1");
		model.put("AAA/BBB/DDD", "data2");
		model.put("AAA/BBB/EEE", "data3");

		expectAnyException(() -> model.insertBefore("DDD", "EEE", "data"));
		expectAnyException(() -> model.insertBefore("AAA/BBB/DDD", "CCC", "data"));
		expectAnyException(() -> model.insertBefore("AAA/BBB/DDD", "EEE", "data"));
		expectAnyException(() -> model.insertBefore("DDD", "EEE", "data"));
		expectNoException(() -> model.insertBefore("AAA/BBB/DDD", "FFF", "data"));
		String[] keys = model.getChild().getChild().getChild().getItems()
				.stream()
				.map(Pair::getX)
				.toArray(String[]::new);
		Assert.assertArrayEquals(new String[] {"CCC", "FFF", "DDD", "EEE"}, keys);
	}

	// TODO: JUnit 4.13 will have Assert#asertThrows
	private void expectAnyException(Runnable func) {
		try {
			func.run();
			Assert.fail("it should throw an exception");
		} catch (Exception ignored) { }
	}

	private void expectNoException(Runnable func) {
		try {
			func.run();
		} catch (Exception e) {
			Assert.fail("it shouldn't throw an exception");
		}
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInsertBeforeNull() {
		model.insertBefore((String) null, "AAA", "data");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInsertBeforeNull2() {
		model.insertBefore("", "AAA", "data");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInsertBeforeNull3() {
		model.insertBefore((String[]) null, "AAA", "data");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInsertBeforeNull4() {
		model.insertBefore(new String[]{}, "AAA", "data");
	}

	@Test
	public void testRemove() {
		model.put("AAA/BBB/CCC", "data1");
		model.put("AAA/BBB/DDD", "data2");
		model.put("AAA/BBB/EEE", "data3");
		model.put("AAA/BBB/FFF", "data4");

		Assert.assertEquals("data4", model.remove(new String[]{"AAA", "BBB", "FFF"}));
		Assert.assertEquals("data2", model.remove(new String[]{"AAA", "BBB", "DDD"}));
		Assert.assertEquals("data1", model.remove(new String[]{"AAA", "BBB", "CCC"}));
		expectAnyException(() -> model.remove("AAA/BBB/ZZZ"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveNull() {
		model.remove((String) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveNull2() {
		model.remove((String[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveNull3() {
		model.remove("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testRemoveNull4() {
		model.remove(new String[] {});
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNavigateToByPathNull() {
		model.navigateToByPath((String) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNavigateToByPathNull2() {
		model.navigateToByPath("");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNavigateToByPathNull3() {
		model.navigateToByPath((String[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNavigateToByPathNull4() {
		model.navigateToByPath(new String[]{});
	}

	@Test
	public void testEmpty() {
		NavigationLevel<String> level1 = model.getChild();
		NavigationLevel<String> level2 = level1.getChild();

		List<Pair<String, String>> items = level1.getItems();
		String current = level1.getCurrent();
		String currentKey = level1.getCurrentKey();

		Assert.assertNotNull(level1);
		Assert.assertNull(level2);
		Assert.assertEquals(0, items.size());
		Assert.assertNull(current);
		Assert.assertNull(currentKey);
	}

	@Test
	public void testNavigate() {
		model.put("AAA/BBB", "data");
		model.put("AAA/BBB/CCC", "data1");
		model.put("AAA/BBB/DDD", "data2");
		model.put("AAA/BBB/EEE", "data3");
		model.put("AAA/BBB/FFF", "data4");

		NavigationLevel<String> lvl1 = model.getChild();
		lvl1.setContext(Collections.singletonMap("readonly", true)).navigateTo("AAA");

		NavigationLevel<String> lvl2 = lvl1.getChild();
		Assert.assertNull(lvl2.getCurrentKey());
		Assert.assertNull(lvl2.getCurrent());
		Assert.assertNull(lvl2.getContext());

		lvl2.navigateTo("BBB").setContext(Collections.singletonMap("readonly", false));
		Map<String, Object> contextAfter = lvl2.getContext();
		Assert.assertEquals("BBB", lvl2.getCurrentKey());
		Assert.assertEquals("data", lvl2.getCurrent());
		Assert.assertNotNull(contextAfter);
		Assert.assertEquals(false, contextAfter.get("readonly"));
		Assert.assertEquals(true, lvl1.getContext().get("readonly"));
	}

	@Test
	public void testNavigateToByPath() {
		model.put("AAA/BBB", "data");
		model.put("AAA/BBB/CCC", "data1");
		model.put("AAA/BBB/DDD", "data2");
		model.put("AAA/BBB/EEE", "data3");
		model.put("AAA/BBB/FFF", "data4");
		model.put("AAA/CCC", "data5");

		model.navigateToByPath(new String[] {"AAA", "BBB", "FFF"});
		model.navigateToByPath("AAA/CCC");
		NavigationLevel<String> lvl1 = model.getChild();
		NavigationLevel<String> lvl2 = lvl1.getChild();
		NavigationLevel<String> lvl3 = lvl2.getChild();
		Assert.assertEquals("CCC", lvl2.getCurrentKey());
		Assert.assertEquals("data5", lvl2.getCurrent());
		Assert.assertNull(lvl3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAppendNull1() {
		model.append((String) null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAppendNull2() {
		model.append("", null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAppendNull3() {
		model.append((String[]) null, null, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAppendNull4() {
		model.append(new String[] {}, null, null);
	}

	@Test
	public void testAppend() {
		model.put("AAA/BBB/CCC", "data1");
		model.put("AAA/BBB/DDD", "data2");
		model.put("AAA/BBB/EEE", "data3");

		expectAnyException(() -> model.append("DDD", "EEE", "data"));
		expectAnyException(() -> model.append("AAA/BBB/DDD", "CCC", "data"));
		expectAnyException(() -> model.append("AAA/BBB/DDD", "EEE", "data"));
		expectNoException(() -> model.append("AAA/BBB/DDD", "FFF", "data"));
		String[] keys = model.getChild().getChild().getChild().getItems()
				.stream()
				.map(Pair::getX)
				.toArray(String[]::new);
		Assert.assertArrayEquals(new String[] {"CCC", "DDD", "FFF", "EEE"}, keys);
	}
}
