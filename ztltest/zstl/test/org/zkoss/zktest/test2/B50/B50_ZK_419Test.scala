/* B50_ZK_419Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 19 11:40:47 CST 2011 , Created by benbai
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
 * A test class for bug ZK-419
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-419.zul,A,E,Tabbox,Accordiion")
class B50_ZK_419Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				<window title="new page title" border="normal">
				1. Please click the "add" button 3~5 times to see if the "new" tab is added one by one, the bug is fixed.
				<separator/>
				2. Click the "invalidate" button, the result is the same as before.
				
					<tabbox mold="accordion" id="tabBoxAdd">
						<tabs id="tabsAdd"></tabs>
						<tabpanels id="tabpanelsAdd"></tabpanels>
					</tabbox>
					<button id="btn1" label="add" onClick='add()'></button>
					<zscript><![CDATA[
						void add() {
							Tab tab = new Tab("new");
							tabsAdd.appendChild(tab);
							Tabpanel tabpanel = new Tabpanel();
							tabpanelsAdd.appendChild(tabpanel);
						}
					]]></zscript>
					<button id="btn2" label="invalidate" onClick="tabBoxAdd.invalidate()"/>
				</window>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var btn1: Widget = engine.$f("btn1");
        var btn2: Widget = engine.$f("btn2");
        var max: Int = 5;

        for (i <- 1 to max) {
            click(btn1);
            waitResponse();
            verifyTrue("new tab should added one by one while click add button",
                jq(".z-tabpanel-accordion-outer").length() == i);
        }
        click(btn2);
        waitResponse();
        verifyTrue("should keep the same after click invalidate button",
                jq(".z-tabpanel-accordion-outer").length() == max);
    }
   );

  }
}