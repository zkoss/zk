/* B50_3316103Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 10:30:55 CST 2011 , Created by benbai
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
 * A test class for bug 3316103
 * @author benbai
 *
 */
@Tags(tags = "B50-3316103.zul,A,M,Combobox,Datebox,EE")
class B50_3316103Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				<div>
					This test case requires to specify the following in zk.xml:
					<![CDATA[
					<listener>
						<listener-class>org.zkoss.zkmax.au.InaccessibleWidgetBlockService$DesktopInit</listener-class>
					</listener>
					]]>
				</div>
				<div>
					1. Select an item from the Combobox. You should see text appearing on the right. Otherwise it is a bug.
				</div>
				<div>
					2. Pick a date in the Datebox. You should see text appearing on the right. Otherwise it is a bug.
				</div>
				<hlayout>
					<combobox id="cbx" readonly="true" onChange="label2.setValue(self.getRawText())">
						<comboitem label="1" />
						<comboitem label="2" />
						<comboitem label="3" />
					</combobox>
					<label id="label2" />
				</hlayout>
				<hlayout>
					<datebox id="dbx" readonly="true" onChange="label3.setValue(self.getRawText())" />
					<label id="label3" />
				</hlayout>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var (cbx: Widget,
    	    dbx: Widget,
    	    label2: Widget,
    	    label3: Widget) = (
    	        engine.$f("cbx"),
    	        engine.$f("dbx"),
    	        engine.$f("label2"),
    	        engine.$f("label3")
    	    );
        click(cbx.$n("btn"));
        click(jq(cbx.$n("pp")).find(".z-comboitem").get(1));
        waitResponse();
        verifyTrue("the value of combobox should equal to the label text next to it",
            cbx.$n("real").get("value").equals(label2.$n().get("innerHTML")));
        click(dbx.$n("btn"));
        click(jq(dbx.$n("pp")).find(".z-calendar-wkday").get(10));
        waitResponse();
        verifyTrue("the value of datebox should equal to the label text next to it",
            dbx.$n("real").get("value").equals(label3.$n().get("innerHTML")));
    }
   );

  }
}