/* IScriptTest.java

	Purpose:

	Description:

	History:
		Fri Feb 25 15:26:53 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.stateless.mock.StatelessTestBase;
import org.zkoss.zul.Script;

/**
 * Test for {@link IScript}
 *
 * @author katherine
 */
public class IScriptTest extends StatelessTestBase {
	@Test
	public void withScript() {
		// check Richlet API case
		IScript iscript  = IScript.ofSrc("script.js").withPackages("zk.zuml, zul.inp").withContent("content");
		assertEquals(richlet(() -> iscript),
				zul(IScriptTest::newScript));
		assertEquals(richlet(() -> iscript.withDefer(true)),
				zul(IScriptTest::newScriptwithDefer));

		// check Stateless file case
		assertEquals(composer(IScriptTest::newScript), zul(IScriptTest::newScript));
		assertEquals(composer(IScriptTest::newScriptwithDefer), zul(IScriptTest::newScriptwithDefer));

		// check Stateless file and then recreate another immutable case
		assertEquals(
				thenComposer(() -> {
					Script script = new Script("content");
					script.setSrc("abc.js");
					script.setPackages("zk.zuml, zul.inp");
					return script;
				}, (IScript iScript) -> iScript.withSrc("script.js").withPackages("zk.zuml, zul.inp").withContent("content")),
				zul(IScriptTest::newScript));
		assertEquals(
				thenComposer(() -> {
					Script script = new Script("content");
					script.setSrc("abc.js");
					script.setPackages("zk.zuml, zul.inp");
					script.setDefer(true);
					return script;
				}, (IScript iScript) -> iScript.withSrc("script.js").withPackages("zk.zuml, zul.inp").withContent("content").withDefer(true)),
				zul(IScriptTest::newScriptwithDefer));
	}

	private static Script newScript() {
		Script script = new Script("content");
		script.setSrc("script.js");
		script.setPackages("zk.zuml, zul.inp");
		return script;
	}

	private static Script newScriptwithDefer() {
		Script script = new Script("content");
		script.setSrc("script.js");
		script.setPackages("zk.zuml, zul.inp");
		script.setDefer(true);
		return script;
	}
}