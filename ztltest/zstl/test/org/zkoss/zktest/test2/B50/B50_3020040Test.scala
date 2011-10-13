/* B50_3020040Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 14:49:57 CST 2011 , Created by benbai
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
 * A test class for bug 3020040
 * @author benbai
 *
 */
@Tags(tags = "B50-3020040.zul,A,E,Listbox")
class B50_3020040Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
			<html><![CDATA[
			<ul>
			<li>Click "Unselect first", and then "item 2" and "item 3" shall remain selected</li>
			</ul>
			]]></html>
			
			<listbox id="box" multiple="true" checkmark="true">
			<listhead>
			<listheader label="Index"/>
			</listhead>
			<listitem label="item 1" selected="true"/>
			<listitem label="item 2" selected="true"/>
			<listitem label="item 3" selected="true"/>
			</listbox>
			<button id="btn" label="Unselect first" onClick="box.removeItemFromSelection(box.getItemAtIndex(0))"/>
			</zk>

    }

    def executor = () => {
    	var (btn: Widget,
    	    box: Widget) = (
    	        engine.$f("btn"),
    	        engine.$f("box")
    	    );
		waitResponse();

		click(btn);
		waitResponse();
		checkSelected(jq(box.$n("rows")).find(".z-listitem").get(1));
		checkSelected(jq(box.$n("rows")).find(".z-listitem").get(2));
    }
    def checkSelected(ele: Element) = {
    	verifyTrue(ele.get("className").contains("z-listitem-seld"));
		if (ZK.is("ie6_") || ZK.is("ie7_") || ZK.is("ie8_")) {
		  verifyTrue(jq(ele).find(".z-listitem-img-checkbox")
		      .css("background-position-x").contains("-26px"));
		} else {
		  verifyTrue(jq(ele).find(".z-listitem-img-checkbox")
		      .css("background-position").contains("-26px"));
		}
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