/* B50_3322909Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 14:32:56 CST 2011 , Created by benbai
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
 * A test class for bug 3322909
 * @author benbai
 *
 */
@Tags(tags = "B50-3322909.zul,A,E,Listbox,emptyMessage")
class B50_3322909Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				<n:pre xmlns:n="xhtml">
			
					1.click set setModel , remove items 
					2.click set setModel , remove items 
					3. in the second time , you will found the emptyMessage is not showing up.
					
					It only happened when you have a height on listbox
				</n:pre>
				<window>
			
					<listbox id="test" height="350px"
						emptyMessage="No items match your search">
						<listhead sizable="true">
							<listheader label="Type" width="520px"></listheader>
							<listheader label="Content" hflex="min"></listheader>
							<listheader label="Content" hflex="1"></listheader>
						</listhead>
					</listbox>
					<zscript><![CDATA[
				ArrayList list = new ArrayList();
				list.add("test");
				list.add("test3");
				list.add("test3");
				list.add("test3");
				list.add("test3");
				test.setModel(new ListModelList(list));
			]]></zscript>
					<hlayout>
						<button id="btn1" label="test set model (Add items)">
							<attribute name="onClick"><![CDATA[
				ArrayList list = new ArrayList();
				list.add("test");
				list.add("test3");
				test.setModel(new ListModelList(list));
			]]></attribute>
						</button>
						<button id="btn2" label="test remove items">
							<attribute name="onClick"><![CDATA[
				test.setModel(new ListModelList());
			]]></attribute>
						</button>
					</hlayout>
				</window>
			</zk>

    }

    def executor = () => {
    	var test: Widget = engine.$f("test");
		var btn1: Widget = engine.$f("btn1");
		var btn2: Widget = engine.$f("btn2");
		waitResponse();

		Scripts.triggerMouseEventAt(getWebDriver(), btn1, "click", "5,5");
		waitResponse();
		Scripts.triggerMouseEventAt(getWebDriver(), btn2, "click", "5,5");
		waitResponse();
		verifyTrue(Integer.parseInt(jq(test.$n("body")).get(0).get("scrollTop")) == 0
		    && jq(test.$n("empty")).find("tr").find("td").get(0).get("innerHTML").contains("No items match your search")
		);
		Scripts.triggerMouseEventAt(getWebDriver(), btn1, "click", "5,5");
		waitResponse();
		Scripts.triggerMouseEventAt(getWebDriver(), btn2, "click", "5,5");
		waitResponse();
		verifyTrue(Integer.parseInt(jq(test.$n("body")).get(0).get("scrollTop")) == 0
		    && jq(test.$n("empty")).find("tr").find("td").get(0).get("innerHTML").contains("No items match your search")
		);
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