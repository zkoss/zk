/* B50_2923185Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 16:04:36 CST 2011 , Created by benbai
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
 * A test class for bug 2923185
 * @author benbai
 *
 */
@Tags(tags = "B50-2923185.zul,A,E,Datebox")
class B50_2923185Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<?page title="new page title" contentType="text/html;charset=UTF-8"?>
			<zk>
			1. Click the datebox button to open the Calendar
			<separator/>
			2. the textbox inside the Calendar popup should be the current hour and min (same with the datebox above). If not, it's error.
			<separator/>
			<datebox id="db" cols="20" locale="en_US" format="yyyy/MM/dd HH:mm" onCreate="self.value = new Date()"/>
			</zk>

    """

   // Run syntax 2
    runZTL(zscript,
        () => {
        var db: Widget = engine.$f("db");

        click(db.$n("btn"));
        var t1: String = db.$n("real").get("value").split(" ")(1);
        var t2: String = jq(db.$n("pp")).find(".z-timebox-inp").get(0).get("value");

        verifyTrue("the time in datebox should equalt to the time in child timebox",
            t1.equals(t2));
    }
   );

  }
}