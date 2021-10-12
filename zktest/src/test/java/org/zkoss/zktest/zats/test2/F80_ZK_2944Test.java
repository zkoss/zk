/* F80_ZK_2944Test.java

	Purpose:
		
	Description:
		
	History:
		Mon, Nov  9, 2015  4:01:06 PM, Created by Christopher

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.zats.test2;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.impl.Parser;
import org.zkoss.zk.ui.select.impl.Token;
import org.zkoss.zk.ui.select.impl.Tokenizer;
import org.zkoss.zktest.zats.ZATSTestCase;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zuti.zul.Apply;
import org.zkoss.zuti.zul.If;

/**
 * 
 * @author Christopher
 */
public class F80_ZK_2944Test extends ZATSTestCase {

	@Test
	public void testTokenizer() {
		Tokenizer tokenizer = new Tokenizer();
		Parser parser = new Parser();
		
		List<String> sources = new ArrayList<String>();
		initSources(sources);
		
		for(String source : sources) {
			List<Token> tokens = tokenizer.tokenize(source);
			//uncomment these system out to see the output
//			System.out.println("======================");
//			System.out.println(source);
//			System.out.println(tokens);
			// replace [, ], and " that comes from java toString() so that equals() can work
			String parsed = parser.parse(tokens, source).toString().replaceAll("^\\[", "").replaceAll("\\]$", "").replaceAll("\"", "'");
//			System.out.println(parsed);
			Assert.assertTrue("Expecting: " + source + ", got: " + parsed, source.equals(parsed));
		}
	}
	
	private void initSources(List<String> sources) {
		sources.add("textbox");
		sources.add("#win");
		sources.add(".myclass");
		sources.add("label[value='My Label']");
		sources.add(":first-child");
		sources.add("window textbox");
		sources.add("window > textbox");
		sources.add("window + textbox");
		sources.add("window ~ textbox");
		sources.add("window > textbox.myclass:first-child");
		sources.add("*");
		sources.add("E");
		sources.add("E:root");
		sources.add("E:nth-child(2)");
		sources.add("E:empty");
		sources.add("E::first-line");
		sources.add("E::after");
		sources.add("E.warning");
		sources.add("E#myid");
		sources.add("E:not(*)");
		sources.add("E:not(E)");
		sources.add("E:not(E:root)");
//		sources.add("E:not(E:nth-child(2))"); // parser will throw error
		sources.add("E:not(E:empty)");
		sources.add("E:not(E::first-line)");
		sources.add("E:not(E::after)");
		sources.add("E:not(E.warning)");
		sources.add("E:not(E#myid)");
//		sources.add("E:not(E F)"); // parser will throw error
//		sources.add("E:not(E > F)"); // parser will throw error
//		sources.add("E:not(E + F)"); // parser will throw error
//		sources.add("E:not(E ~ F)"); // parser will throw error
		sources.add("E F");
		sources.add("E > F");
		sources.add("E + F");
		sources.add("E ~ F");
		sources.add(".E");
		sources.add("#E");
		sources.add("window label + button");
		sources.add("label#lb");
		sources.add("#win #btn");
		sources.add("#win + #btn");
		sources.add(".myclass button");
		sources.add("div.myclass button");
		sources.add("button[label='submit']");
		sources.add("div:root");
		sources.add("div:first-child");
		sources.add("div:last-child");
		sources.add("div:only-child");
		sources.add("div:empty");
		sources.add("div:nth-child(3)");
		sources.add("div:nth-child(even)");
		sources.add("div:nth-last-child(3)");
		sources.add("div:nth-last-child(even)");
		sources.add("window#win > * > textbox");
		sources.add("window#win + * + textbox");
		sources.add("grid, listbox, tree");
		sources.add("#win timebox, #win datebox");
		sources.add("grid#users");
		sources.add("grid#users row");
		sources.add("#win #hl > #btn");
		sources.add("window hlayout > button");
		sources.add("#win hlayout > button");
		sources.add("window #hl > #btn");
		sources.add("#win #hl > button, #win #hl > toolbarbutton");
		sources.add("#win #hl > #btn, #win #hl > #toolbtn");
		sources.add("#win + #hl > #btn, #win #hl > #btn");
		sources.add("#win hlayout > #btn, #win hlayout > #toolbtn");
		sources.add("#win::shadow");
		sources.add("#win::shadow > div");
		sources.add("#win::shadow > .warning");
		sources.add("#win::shadow if");
		sources.add("#win::shadow div.warning");
		sources.add("#win::shadow > if > label");
		sources.add("#win::shadow label[value='text']");
		sources.add(":host");
		sources.add(":host(div)");
		sources.add(":host(#id)");
		sources.add(":host(.class)");
//		sources.add(":host(window > div)"); // parser will throw error
//		sources.add(":host(window div)"); // parser will throw error
//		sources.add(":host(window + div)"); // parser will throw error
//		sources.add(":host(window ~ div)"); // parser will throw error
	}
	
	@Test
	public void testHostNoParam() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host");
		Assert.assertTrue("expecting 3, got: " + comps.size(), comps.size() == 3);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("div1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(1).getId().equals("div2"));
		Assert.assertTrue(comps.get(2).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(2).getId().equals("div3"));
	}
	
	@Test
	public void testHostWithParamClass() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(.warning)");
		Assert.assertTrue("expecting 2, got: " + comps.size(), comps.size() == 2);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("div1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(1).getId().equals("div3"));
	}
	
	@Test
	public void testHostWithParamId() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(div)");
		Assert.assertTrue("expecting 3, got: " + comps.size(), comps.size() == 3);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("div1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(1).getId().equals("div2"));
		Assert.assertTrue(comps.get(2).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(2).getId().equals("div3"));
	}
	
	@Test
	public void testHostWithParamType() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(#div1)");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("div1"));
	}
	
	@Test
	public void testHostNonShadowSelect() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(#div1) #label2");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(0).getId().equals("label2"));
	}
	
	// cannot select shadow element without ::shadow
	@Test
	public void testHostFail() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(#div1) #if1");
		Assert.assertTrue("expecting 0, got: " + comps.size(), comps.size() == 0);
	}
	
	// cannot select non-shadow elements when shadow selector ::shadow appears
	@Test
	public void testHostShadowFail() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(#div1)::shadow #label1");
		Assert.assertTrue("expecting 0, got: " + comps.size(), comps.size() == 0);
	}
	
	// can select shadow element because the usage of ::shadow
	@Test
	public void testHostSuccess() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(#div1)::shadow #if1");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if1"));
	}
	
	@Test
	public void testApplyWithId() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "div::shadow#sh1");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh1"));
	}
	
	@Test
	public void testApplyWithShadowInTheBack() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "div > div::shadow");
		Assert.assertTrue("expecting 4, got: " + comps.size(), comps.size() == 4);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(1).getId().equals("sh2"));
		Assert.assertTrue(comps.get(2).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(2).getId().equals("sh3"));
		Assert.assertTrue(comps.get(3).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(3).getId().equals("sh4"));
	}
	
	@Test
	public void testApplyWithShadowWithClass() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "div div.warning::shadow");
		Assert.assertTrue("expecting 2, got: " + comps.size(), comps.size() == 2);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(1).getId().equals("sh4"));
	}
	
	@Test
	public void testApplyWithShadowInTheBackWithClass() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "div.warning::shadow");
		Assert.assertTrue("expecting 2, got: " + comps.size(), comps.size() == 2);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(1).getId().equals("sh4"));
	}
	
	@Test
	public void testApplyNoId() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "div::shadow");
		Assert.assertTrue("expecting 4, got: " + comps.size(), comps.size() == 4);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(1).getId().equals("sh2"));
		Assert.assertTrue(comps.get(2).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(2).getId().equals("sh3"));
		Assert.assertTrue(comps.get(3).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(3).getId().equals("sh4"));
	}
	
	
	
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "div > label");
		Assert.assertTrue("expecting 8, got: " + comps.size(), comps.size() == 8);
		Assert.assertTrue(comps.get(0).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(0).getId().equals("label1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(1).getId().equals("label2"));
		Assert.assertTrue(comps.get(2).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(2).getId().equals("label3"));
		Assert.assertTrue(comps.get(3).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(3).getId().equals("label4"));
		Assert.assertTrue(comps.get(4).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(4).getId().equals("label5"));
		Assert.assertTrue(comps.get(5).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(5).getId().equals("label6"));
		Assert.assertTrue(comps.get(6).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(6).getId().equals("label7"));
		Assert.assertTrue(comps.get(7).getClass().equals(Label.class));
		Assert.assertTrue(comps.get(7).getId().equals("label8"));
	}
	
	@Test
	public void testIf() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "div::shadow #if1");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if1"));
	}
	
	@Test
	public void testLabel() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "div::shadow #label1");
		Assert.assertTrue("expecting 0, got: " + comps.size(), comps.size() == 0);
	}

	@Test
	public void testSelectSingleShadowIf() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(#div1)::shadow if");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if1"));
	}
	
	@Test
	public void testSelectSingleShadowIfMultiplePseudoClasses() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(div):nth-child(1)::shadow");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh1"));
	}
	
	@Test
	public void testSelectNormalIdOnly() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "#div1 #inner1");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("inner1"));
	}
	
	@Test
	public void testSelectNormal() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "#parent > #div1 > div");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("inner1"));
	}
	
	@Test
	public void testSelectNormalPseudoClass() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "#parent > div:nth-child(1) > div");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("inner1"));
	}
	
	@Test
	public void testSelectMultipleShadowIf() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(div)::shadow if");
		Assert.assertTrue("expecting 5, got: " + comps.size(), comps.size() == 5);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if1"));
		Assert.assertTrue(comps.get(1).getClass().equals(If.class));
		Assert.assertTrue(comps.get(1).getId().equals("if2"));
		Assert.assertTrue(comps.get(2).getClass().equals(If.class));
		Assert.assertTrue(comps.get(2).getId().equals("if3"));
		Assert.assertTrue(comps.get(3).getClass().equals(If.class));
		Assert.assertTrue(comps.get(3).getId().equals("if4"));
		Assert.assertTrue(comps.get(4).getClass().equals(If.class));
		Assert.assertTrue(comps.get(4).getId().equals("if5"));
	}
	
	@Test
	public void testShadowDescendantType() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow if");
		Assert.assertTrue("expecting 5, got: " + comps.size(), comps.size() == 5);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if1"));
		Assert.assertTrue(comps.get(1).getClass().equals(If.class));
		Assert.assertTrue(comps.get(1).getId().equals("if2"));
		Assert.assertTrue(comps.get(2).getClass().equals(If.class));
		Assert.assertTrue(comps.get(2).getId().equals("if3"));
		Assert.assertTrue(comps.get(3).getClass().equals(If.class));
		Assert.assertTrue(comps.get(3).getId().equals("if4"));
		Assert.assertTrue(comps.get(4).getClass().equals(If.class));
		Assert.assertTrue(comps.get(4).getId().equals("if5"));
	}
	
	@Test
	public void testShadowDescendantId() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow #if1");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if1"));
	}
	
	@Test
	public void testShadowChildId() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow > #if1");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if1"));
	}
	
	@Test
	public void testShadowIdChildId() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow#sh2 > #if2");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if2"));
	}
	
	@Test
	public void testShadowChildType() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow > if");
		Assert.assertTrue("expecting 5, got: " + comps.size(), comps.size() == 5);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if1"));
		Assert.assertTrue(comps.get(1).getClass().equals(If.class));
		Assert.assertTrue(comps.get(1).getId().equals("if2"));
		Assert.assertTrue(comps.get(2).getClass().equals(If.class));
		Assert.assertTrue(comps.get(2).getId().equals("if3"));
		Assert.assertTrue(comps.get(3).getClass().equals(If.class));
		Assert.assertTrue(comps.get(3).getId().equals("if4"));
		Assert.assertTrue(comps.get(4).getClass().equals(If.class));
		Assert.assertTrue(comps.get(4).getId().equals("if5"));
	}
	
	@Test
	public void testShadowRootMultiple() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "#div2::shadow");
		Assert.assertTrue("expecting 2, got: " + comps.size(), comps.size() == 2);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh2"));
		Assert.assertTrue(comps.get(1).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(1).getId().equals("sh3"));
	}
	
	@Test
	public void testShadowRootMultipleHost() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, ":host(#div2)::shadow");
		Assert.assertTrue("expecting 2, got: " + comps.size(), comps.size() == 2);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh2"));
		Assert.assertTrue(comps.get(1).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(1).getId().equals("sh3"));
	}
	
	@Test
	public void testShadowRootId() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow#sh2");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh2"));
	}
	
	@Test
	public void testShadowRootMultipleSiblingAdjacent() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow#sh2 + apply");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh3"));
	}
	
	@Test
	public void testShadowRootMultipleSiblingGeneral() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow#sh2 ~ apply");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh3"));
	}
	
	@Test
	public void testShadowRootMultipleNoParam() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow");
		Assert.assertTrue("expecting 4, got: " + comps.size(), comps.size() == 4);
		Assert.assertTrue(comps.get(0).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(0).getId().equals("sh1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(1).getId().equals("sh2"));
		Assert.assertTrue(comps.get(2).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(2).getId().equals("sh3"));
		Assert.assertTrue(comps.get(3).getClass().equals(Apply.class));
		Assert.assertTrue(comps.get(3).getId().equals("sh4"));
	}
	
	@Test
	public void testShadowRootMultipleSiblingChild() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow#sh2 > apply");
		Assert.assertTrue("expecting 0, got: " + comps.size(), comps.size() == 0);
	}
	
	@Test
	public void testShadowRootMultipleSiblingDescendant() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "::shadow#sh2 apply");
		Assert.assertTrue("expecting 0, got: " + comps.size(), comps.size() == 0);
	}
	
	@Test
	public void testNonShadowAdjacentSibling() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "#div1 + div");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("div2"));
	}
	
	@Test
	public void testNonShadowGeneralSibling() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "#div1 ~ div");
		StringBuilder sb = new StringBuilder();
		for (Component comp : comps) {
			sb.append(comp.getId() + ", ");
		}
		Assert.assertTrue("expecting 2, got: " + comps.size() + " => " + sb.toString(), comps.size() == 2);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("div2"));
		Assert.assertTrue(comps.get(1).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(1).getId().equals("div3"));
	}
	
	@Test
	public void testNonShadowDescendant() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "#parent div");
		Assert.assertTrue("expecting 6, got: " + comps.size(), comps.size() == 6);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("div1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(1).getId().equals("inner1"));
		Assert.assertTrue(comps.get(2).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(2).getId().equals("div2"));
		Assert.assertTrue(comps.get(3).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(3).getId().equals("inner2"));
		Assert.assertTrue(comps.get(4).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(4).getId().equals("div3"));
		Assert.assertTrue(comps.get(5).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(5).getId().equals("inner3"));
	}
	
	@Test
	public void testNonShadowChild() {
		DesktopAgent desktop = connect();
		Component parent = desktop.query("#parent").as(Div.class);
		List<Component> comps = Selectors.find(parent, "#parent div");
		Assert.assertTrue("expecting 6, got: " + comps.size(), comps.size() == 6);
		Assert.assertTrue(comps.get(0).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(0).getId().equals("div1"));
		Assert.assertTrue(comps.get(1).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(1).getId().equals("inner1"));
		Assert.assertTrue(comps.get(2).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(2).getId().equals("div2"));
		Assert.assertTrue(comps.get(3).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(3).getId().equals("inner2"));
		Assert.assertTrue(comps.get(4).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(4).getId().equals("div3"));
		Assert.assertTrue(comps.get(5).getClass().equals(Div.class));
		Assert.assertTrue(comps.get(5).getId().equals("inner3"));
	}
	
	@Test
	public void testShadowIdOnly() {
		DesktopAgent desktop = connect();
		List<Component> comps = Selectors.find(desktop.query("#div2").as(Div.class), "#div2::shadow#sh2 #if3");
		Assert.assertTrue("expecting 1, got: " + comps.size(), comps.size() == 1);
		Assert.assertTrue(comps.get(0).getClass().equals(If.class));
		Assert.assertTrue(comps.get(0).getId().equals("if3"));
	}
}
