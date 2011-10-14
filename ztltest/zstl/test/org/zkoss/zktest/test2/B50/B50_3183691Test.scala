/* B50_3183691Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 11:35:28 CST 2011 , Created by benbai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.B50

import org.zkoss.zstl.ZTL4ScalaTestCase
import scala.collection.JavaConversions._
import org.junit.Test;
import org.zkoss.ztl.Element;
import org.zkoss.ztl.JQuery;
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.util.Scripts;
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZK;
import org.zkoss.ztl.ZKClientTestCase;
import java.lang._

/**
 * A test class for bug 3183691
 * @author benbai
 *
 */
@Tags(tags = "B50-3183691.zul,B,E,Listbox,onSelect")
class B50_3183691Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
			<html><![CDATA[
			<ul>
			  <li>Click the click button and you shall see a listbox being open and dropped</li>
			</ul>
			]]></html>
			
			<button id="btn" label="click" xmlns:w="client" w:onClick="var lb = zk.Widget.$(jq('@listbox'));lb.fire('onSelect');lb.fire('onSelect');"/>
			<listbox width="200px">
			<attribute name="onSelect"><![CDATA[
			div.getChildren().clear();
			Executions.createComponentsDirectly(
			"<combobox id=\"cb\" open=\"true\"><comboitem label=\"item\" forEach=\"1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1\"/></combobox>",
			"zul",div,null);
			]]></attribute>
			<listitem label="item"/>
			<listitem label="item"/>
			</listbox>
			<div id="div" />
			</zk>

    }

    def executor = () => {
    	var btn: Widget = engine.$f("btn");
		waitResponse();

		click(btn);
		waitResponse();
		var cb: Widget = engine.$f("cb");
		verifyTrue(cb != null);
		verifyFalse("none".equals(cb.$n("pp").get("style.display")));
    }
   // Run syntax 1 
   runZTL(zscript, executor);
   
   // Run syntax 2
   /**
    runZTL(zscript,
        () => {
        var l1: Widget = engine.$f("l1");
        var l2: Widget = engine.$f("l2");
        waitResponse();
        var strClickBefor = getText(l1);
        click(l1);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l1));
        strClickBefor = getText(l2);
        click(l2);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l2));
    }
   );
    */
    /** create widget example
		var tree: Widget = engine.$f("tree");
		var listbox: Widget = engine.$f("listbox");
		waitResponse();
	*/
   /** trigger mouse event example
    Scripts.triggerMouseEventAt(getWebDriver(), inner1, "click", "5,5");
    */
   /** detect whether exception exists example
   		verifyFalse(jq(".z-window-highlighted").exists());
   		verifyFalse(jq(".z-window-modal").exists())
	*/
	/** detect browser
		if (ZK.is("ie6_") || ZK.is("ie7_"))
	*/
  }
}