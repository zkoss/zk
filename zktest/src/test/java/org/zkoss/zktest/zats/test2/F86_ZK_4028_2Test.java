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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

		Assertions.assertNull(val1);
		Assertions.assertEquals("data1", val2);
		Assertions.assertEquals("data2", val3);
	}

	@Test
	public void testPutNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.put((String) null, "data4"));
	}

	@Test
	public void testPutNull2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.put("", "data4"));
	}

	@Test
	public void testPutNull3() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.put((String[]) null, "data4"));
	}

	@Test
	public void testPutNull4() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.put(new String[]{}, "data4"));
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
		String[] keys = model.getRoot().getChild().getChild().getItems()
				.stream()
				.map(Pair::getX)
				.toArray(String[]::new);
		Assertions.assertArrayEquals(new String[] {"CCC", "FFF", "DDD", "EEE"}, keys);
	}

	// TODO: JUnit 4.13 will have Assert#asertThrows
	private void expectAnyException(Runnable func) {
		try {
			func.run();
			Assertions.fail("it should throw an exception");
		} catch (Exception ignored) { }
	}

	private void expectNoException(Runnable func) {
		try {
			func.run();
		} catch (Exception e) {
			Assertions.fail("it shouldn't throw an exception");
		}
	}

	@Test
	public void testInsertBeforeNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.insertBefore((String) null, "AAA", "data"));
	}

	@Test
	public void testInsertBeforeNull2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.insertBefore("", "AAA", "data"));
	}

	@Test
	public void testInsertBeforeNull3() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.insertBefore((String[]) null, "AAA", "data"));
	}

	@Test
	public void testInsertBeforeNull4() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.insertBefore(new String[]{}, "AAA", "data"));
	}

	@Test
	public void testRemove() {
		model.put("AAA/BBB/CCC", "data1");
		model.put("AAA/BBB/DDD", "data2");
		model.put("AAA/BBB/EEE", "data3");
		model.put("AAA/BBB/FFF", "data4");

		Assertions.assertEquals("data4", model.remove(new String[]{"AAA", "BBB", "FFF"}));
		Assertions.assertEquals("data2", model.remove(new String[]{"AAA", "BBB", "DDD"}));
		Assertions.assertEquals("data1", model.remove(new String[]{"AAA", "BBB", "CCC"}));
		expectAnyException(() -> model.remove("AAA/BBB/ZZZ"));
	}

	@Test
	public void testRemoveNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.remove((String) null));
	}

	@Test
	public void testRemoveNull2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.remove((String[]) null));
	}

	@Test
	public void testRemoveNull3() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.remove(""));
	}

	@Test
	public void testRemoveNull4() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.remove(new String[] {}));
	}

	@Test
	public void testNavigateToByPathNull() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.navigateToByPath((String) null));
	}

	@Test
	public void testNavigateToByPathNull2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.navigateToByPath(""));
	}

	@Test
	public void testNavigateToByPathNull3() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.navigateToByPath((String[]) null));
	}

	@Test
	public void testNavigateToByPathNull4() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.navigateToByPath(new String[]{}));
	}

	@Test
	public void testEmpty() {
		NavigationLevel<String> level1 = model.getRoot();
		NavigationLevel<String> level2 = level1.getChild();

		List<Pair<String, String>> items = level1.getItems();
		String current = level1.getCurrent();
		String currentKey = level1.getCurrentKey();

		Assertions.assertNotNull(level1);
		Assertions.assertNull(level2);
		Assertions.assertEquals(0, items.size());
		Assertions.assertNull(current);
		Assertions.assertNull(currentKey);
	}

	@Test
	public void testNavigate() {
		model.put("AAA/BBB", "data");
		model.put("AAA/BBB/CCC", "data1");
		model.put("AAA/BBB/DDD", "data2");
		model.put("AAA/BBB/EEE", "data3");
		model.put("AAA/BBB/FFF", "data4");

		NavigationLevel<String> lvl1 = model.getRoot();
		lvl1.setContext(Collections.singletonMap("readonly", true)).navigateTo("AAA");

		NavigationLevel<String> lvl2 = lvl1.getChild();
		Assertions.assertNull(lvl2.getCurrentKey());
		Assertions.assertNull(lvl2.getCurrent());
		Assertions.assertNull(lvl2.getContext());

		lvl2.navigateTo("BBB").setContext(Collections.singletonMap("readonly", false));
		Map<String, Object> contextAfter = lvl2.getContext();
		Assertions.assertEquals("BBB", lvl2.getCurrentKey());
		Assertions.assertEquals("data", lvl2.getCurrent());
		Assertions.assertNotNull(contextAfter);
		Assertions.assertEquals(false, contextAfter.get("readonly"));
		Assertions.assertEquals(true, lvl1.getContext().get("readonly"));
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
		NavigationLevel<String> lvl1 = model.getRoot();
		NavigationLevel<String> lvl2 = lvl1.getChild();
		NavigationLevel<String> lvl3 = lvl2.getChild();
		Assertions.assertEquals("CCC", lvl2.getCurrentKey());
		Assertions.assertEquals("data5", lvl2.getCurrent());
		Assertions.assertNull(lvl3);
	}

	@Test
	public void testAppendNull1() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.append((String) null, null, null));
	}

	@Test
	public void testAppendNull2() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.append("", null, null));
	}

	@Test
	public void testAppendNull3() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.append((String[]) null, null, null));
	}

	@Test
	public void testAppendNull4() {
		Assertions.assertThrows(IllegalArgumentException.class, () -> model.append(new String[] {}, null, null));
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
		String[] keys = model.getRoot().getChild().getChild().getItems()
				.stream()
				.map(Pair::getX)
				.toArray(String[]::new);
		Assertions.assertArrayEquals(new String[] {"CCC", "DDD", "FFF", "EEE"}, keys);
	}
}
