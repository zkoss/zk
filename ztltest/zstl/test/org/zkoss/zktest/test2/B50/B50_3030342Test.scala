/* B50_3030342Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 14:36:27 CST 2011 , Created by benbai
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
 * A test class for bug 3030342
 * @author benbai
 *
 */
@Tags(tags = "##Out of Date##B50-3030342.zul,A,E,Listbox,ROD")
class B50_3030342Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
			<html>
			<![CDATA[
			<ol>
				<li>Check "David", "Thomas" and "Steven" in the listbox</li>
			</ol>
			]]>
			</html>
				<listbox id="listbox" width="100px">
					<attribute name="onCreate"><![CDATA[
						List list = new ArrayList();
						list.add("David");
						list.add("Thomas");
						list.add("Steven");
						
						listbox.setModel(new ListModelList(list));
					]]></attribute>
				</listbox>
			</zk>

    }

    def executor = () => {
    	var listbox: Widget = engine.$f("listbox");
		waitResponse();

		verifyTrue(jq(listbox.$n("rows")).find(".z-listcell-cnt").get(0).get("innerHTML").contains("David"));
		verifyTrue(jq(listbox.$n("rows")).find(".z-listcell-cnt").get(1).get("innerHTML").contains("Thomas"));
		verifyTrue(jq(listbox.$n("rows")).find(".z-listcell-cnt").get(2).get("innerHTML").contains("Steven"));
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