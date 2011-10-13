/* B50_3317743Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 15:00:55 CST 2011 , Created by benbai
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
 * A test class for bug 3317743
 * @author benbai
 *
 */
@Tags(tags = "B50-3317743.zul,A,E,Listbox,Tree")
class B50_3317743Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
				<div>1. Click on item 3.</div>
				<div>2. Go to next page.</div>
				<div>3. Click on item 8, but on the blank, NOT on the checkmark.</div>
				<div>4. Go back to page 1. If item 3 is deselected, it is a bug.</div>
				<zscript><![CDATA[
					ListModelList m = new ListModelList();
					for(int i = 0; i < 20; i++)
						m.add(i);
				]]></zscript>
				<listbox id="listbox" onClick="tb.setValue(event.getPageX().toString())" multiple="true" model="${m}" checkmark="true"
					mold="paging" onCreate="self.setPageSize(5)" />
    			<textbox id="tb" />
			</zk>
    }

    def executor = () => {
		var listbox: Widget = engine.$f("listbox");
		var tb: Widget = engine.$f("tb");
		waitResponse();

		if (ZK.is("ie9"))
		  clickAt(jq(listbox.$n("rows")).find(".z-listitem").get(3), "200,5");
		else
			Scripts.triggerMouseEventAt(getWebDriver(), jq(listbox.$n("rows")).find(".z-listitem").get(3), "click", "200,5");
		waitResponse();
		click(jq(listbox.$n("pgib")).find(".z-paging-next").get(0));
		waitResponse();
		if (ZK.is("ie9"))
		  clickAt(jq(listbox.$n("rows")).find(".z-listitem").get(3), "200,5");
		else
			Scripts.triggerMouseEventAt(getWebDriver(), jq(listbox.$n("rows")).find(".z-listitem").get(3), "click", "200,5");
		waitResponse();
		System.out.println(Integer.parseInt(tb.$n().get("value")));
		
		verifyTrue(Integer.parseInt(tb.$n().get("value")) > 200);

		click(jq(listbox.$n("pgib")).find(".z-paging-prev").get(0));
		waitResponse();
		verifyTrue(jq(listbox.$n("rows")).find(".z-listitem").get(3).get("className").contains("z-listitem-seld"));
		System.out.println(jq(jq(listbox.$n("rows")).find(".z-listitem").get(3)).find(".z-listitem-img-checkbox")
		      .css("background-position"));
		if (ZK.is("ie6_") || ZK.is("ie7_") || ZK.is("ie8_"))
		  verifyTrue(jq(jq(listbox.$n("rows")).find(".z-listitem").get(3)).find(".z-listitem-img-checkbox")
		      .css("background-position-x").contains("-26px"));
		else
		  verifyTrue(jq(jq(listbox.$n("rows")).find(".z-listitem").get(3)).find(".z-listitem-img-checkbox")
		      .css("background-position").contains("-26px"));
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