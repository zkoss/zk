/* F110_ZK_6096Test.java

		Purpose:

		Description:

		History:
				Tue May 19 16:10:10 CST 2026, Created by peakerlee

Copyright (C) 2026 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

public class F110_ZK_6096Test extends WebDriverTestCase {
	@Test
	public void testStubonlyDescendants() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$probe"));
		waitResponse();
		assertEquals("g1div g1lbl g2divStub g3div g3lbl g4lbl g5div g5lbl", jq("$result").text());

		click(jq("$append"));
		waitResponse();
		click(jq("$probe"));
		waitResponse();
		assertEquals("g1div g1lbl g2divStub g3div g3lbl g4lbl g4newLbl g5div g5lbl g5newLbl", jq("$result").text());

		click(jq("$g1btn"));
		waitResponse();
		assertEquals("g1btn clicked", jq("$result").text());
		click(jq("$g3btn"));
		waitResponse();
		assertEquals("g3btn clicked", jq("$result").text());
		assertFalse(hasError());
	}

	@Test
	public void testPaging() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g6grid").find(".z-paging-next"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("r3", jq("$g6grid").find("@row").eq(0).text().trim());
		click(jq("$g7grid").find(".z-paging-next"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("s3", jq("$g7grid").find("@row").eq(0).text().trim());
	}

	@Test
	public void testShadowHost() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g8add"));
		waitResponse();
		click(jq("$g8remove"));
		waitResponse();
		assertFalse(hasError());
		JQuery textboxes = jq("$g8").find("@textbox");
		assertEquals(2, textboxes.length());
		assertEquals("b", textboxes.eq(0).val());
		assertEquals("c", textboxes.eq(1).val());
	}

	@Test
	public void testGridRod() {
		connect("/test2/F110-ZK-6096.zul");
		assertEquals(10, jq("$g9grid").find("@label").length());
		assertFalse(jq("$g9label80").exists());
		click(jq("$g9jump80"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("rod-row-80", jq("$g9label80").text());
		click(jq("$g9jump0"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("rod-row-0", jq("$g9label0").text());
	}

	@Test
	public void testListboxRod() {
		connect("/test2/F110-ZK-6096.zul");
		assertEquals(10, jq("$g10listbox").find("@label").length());
		assertFalse(jq("$g10label80").exists());
		click(jq("$g10jump80"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("rod-item-80", jq("$g10label80").text());
		click(jq("$g10jump0"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("rod-item-0", jq("$g10label0").text());
	}

	@Test
	public void testBoundLabel() {
		connect("/test2/F110-ZK-6096.zul");
		assertEquals("old", jq("$g11load").text());
		assertEquals("old", jq("$g11programmatic").text());
		click(jq("$g11change"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("new", jq("$g11load").text());
		assertEquals("new", jq("$g11programmatic").text());
		click(jq("$g11probe"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("g11static", jq("$g11stubs").text());
	}

	@Test
	public void testBoundLabelWildcard() {
		connect("/test2/F110-ZK-6096.zul");
		assertEquals("old", jq("$g12input").val());
		assertEquals("old", jq("$g12load").text());
		eval("jq('$g12input').val('')");
		type(jq("$g12input"), "typed");
		click(jq("$g12probe"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("typed", jq("$g12load").text());
		assertEquals("g12container g12static", jq("$g12stubs").text());
		click(jq("$g12change"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("new", jq("$g12input").val());
		assertEquals("new", jq("$g12load").text());
	}

	@Test
	public void testWindowIdSpace() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g13window").find("@button"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("Window; same owner: true; static stub: true",
				jq("$g13window").find(".idspace-result").text());
	}

	@Test
	public void testMacroIdSpace() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g14macro").find("@button"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("Non-inline macro; same owner: true; static stub: true",
				jq("$g14macro").find(".idspace-result").text());
	}

	@Test
	public void testGridSorting() {
		connect("/test2/F110-ZK-6096.zul");
		assertEquals(3, jq("$g15grid").find("@row").length());
		assertEquals("c", jq("$g15grid").find("@row").eq(0).text().trim());
		click(jq("$g15probe"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("static stub: true", jq("$g15result").text());

		click(jq("$g15column"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("a", jq("$g15grid").find("@row").eq(0).text().trim());
		assertEquals("b", jq("$g15grid").find("@row").eq(1).text().trim());
		assertEquals("c", jq("$g15grid").find("@row").eq(2).text().trim());

		click(jq("$g15column"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("c", jq("$g15grid").find("@row").eq(0).text().trim());
		assertEquals("b", jq("$g15grid").find("@row").eq(1).text().trim());
		assertEquals("a", jq("$g15grid").find("@row").eq(2).text().trim());
	}

	@Test
	public void testGridSortingEnabledAfterRender() {
		connect("/test2/F110-ZK-6096.zul");
		assertEquals(3, jq("$g16grid").find("@row").length());
		assertEquals("c", jq("$g16grid").find("@row").eq(0).text().trim());
		click(jq("$g16enable"));
		waitResponse();
		assertFalse(hasError());

		click(jq("$g16column"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("a", jq("$g16grid").find("@row").eq(0).text().trim());
		assertEquals("b", jq("$g16grid").find("@row").eq(1).text().trim());
		assertEquals("c", jq("$g16grid").find("@row").eq(2).text().trim());

		click(jq("$g16column"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("c", jq("$g16grid").find("@row").eq(0).text().trim());
		assertEquals("b", jq("$g16grid").find("@row").eq(1).text().trim());
		assertEquals("a", jq("$g16grid").find("@row").eq(2).text().trim());
	}

	@Test
	public void testModelGridRod() {
		connect("/test2/F110-ZK-6096.zul");
		assertEquals(10, jq("$g17grid").find("@label").length());
		assertFalse(jq("$g17label80").exists());
		click(jq("$g17jump80"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("model-row-80", jq("$g17label80").text());
		click(jq("$g17jump0"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("model-row-0", jq("$g17label0").text());
		click(jq("$g17update"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("model: true; cropper: true; row label: Label", jq("$g17result").text());
		assertEquals("updated-model-row-0", jq("$g17label0").text());
	}

	@Test
	public void testDynamicNestedPolicies() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g18appendContainer"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("protected-first", jq("$g18first").text());
		click(jq("$g18appendLabel"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("protected-second", jq("$g18second").text());
		click(jq("$g18update"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("protected originals: true; control stubs: true", jq("$g18result").text());
		assertEquals("updated-first", jq("$g18first").text());
		assertEquals("updated-second", jq("$g18second").text());
	}

	@Test
	public void testNativeStubonlyBoundaryAppend() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g19append"));
		waitResponse();
		click(jq("$g19update"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("native stub: true; originals: true", jq("$g19result").text());
		assertEquals("updated-native-first", jq("$g19first").text());
		assertEquals("updated-native-second", jq("$g19second").text());
	}

	@Test
	public void testNativeStubonlyBoundaryInvalidate() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g19invalidate"));
		waitResponse();
		click(jq("$g19update"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("native stub: true; originals: true", jq("$g19result").text());
		assertEquals("updated-native-first", jq("$g19first").text());
	}

	@Test
	public void testEmptyNames() {
		connect("/test2/F110-ZK-6096.zul");
		click(jq("$g20probe"));
		waitResponse();
		assertFalse(hasError());
		assertEquals("empty: null; live: true; comma stub: true", jq("$g20result").text());
	}
}
