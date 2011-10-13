/* B50_3142509Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 12:22:25 CST 2011 , Created by benbai
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
 * A test class for bug 3142509
 * @author benbai
 *
 */
@Tags(tags = "B50-3142509.zul,A,E,Listbox,Select")
class B50_3142509Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				<listbox mold="select" id="list">
					<listhead/>
					<listitem label="item1"/>
					<listitem label="item2" selected="true"/>
					<listitem label="item3"/>
					<listitem label="item4"/>
				</listbox>
				<button id="btn" label="Please click to select item 4, if item 3 that's bug" onClick="list.selectedIndex=3"/>
			</zk>
			

    }

    def executor = () => {
    	var (btn: Widget,
    	    list: Widget) = (
    	        engine.$f("btn"),
    	        engine.$f("list")
    	    );
		waitResponse();
		click(btn);
		waitResponse();
		if (ZK.is("ie6_") || ZK.is("ie7_") || ZK.is("ie8_"))
			verifyTrue("item4".equals(jq(list.$n()).find("option").get(Integer.parseInt(list.$n().get("selectedIndex"))).get("text")));
		else
		  verifyTrue("item4".equals(list.$n().get("value")));
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