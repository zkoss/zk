/* B50_3142583Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Oct 17 09:28:14 CST 2011 , Created by benbai
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
 * A test class for bug 3142583
 * @author benbai
 *
 */
@Tags(tags = "B50-3142583.zul,A,E,Include")
class B50_3142583Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
			<label multiline="true">
				<attribute name="value">
					1. this page should has two window
					2. the window inside "Main Window", it should has title "Included win", if it doesn't have title, this is wrong
				</attribute>
			</label>
			<window id="mainWin" title="Main Window" border="normal">
				<include id="include" src="/test2/B50-3142583_sub.zul" name="Included win">
				</include>
				<label id="msg" style="color:red;"></label>
				<zscript><![CDATA[
				Window included = (Window)include.getFellow("includedWin");
				String title = included.getTitle();
				if (!"Included win".equals(title))
					msg.setValue("Error, the window should has title");
				]]></zscript>
			</window>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var mainWin: Widget = engine.$f("mainWin");

        verifyTrue("should have two window in this page",
            jq(".z-window-embedded").length() == 2);

        var includedWin: Element = jq(".z-window-embedded").get(1);
        def checkParentWindow(ele: Element): Boolean =  {
        	if (ele.get("id").equals(mainWin.$n().get("id")))
        		return true;
        	else if (ele.parentNode().exists())
        	    return checkParentWindow(ele.parentNode());
        	else
        	    return false;
        }
        verifyTrue("includedWin should be the child of mainWin",
            checkParentWindow(includedWin));
        verifyTrue("indludedWin should have title 'Included win",
            jq(includedWin).find(".z-window-embedded-header").get(0)
            .get("innerHTML").contains("Included win"));
        waitResponse();

    }
   );

  }
}