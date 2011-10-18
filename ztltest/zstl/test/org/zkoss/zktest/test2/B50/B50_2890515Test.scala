/* B50_2890515Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 16:26:16 CST 2011 , Created by benbai
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
 * A test class for bug 2890515
 * @author benbai
 *
 */
@Tags(tags = "B50-2890515.zul,A,E,Datebox,Calendar,Constraint,Before")
class B50_2890515Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
			Please choose a date, for example, 20091102. And foucs in and out the
			datebox it should not show an error.
			<datebox id="dtbx" constraint="before 20091102" format="yyyyMMdd"/>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var dtbx: Widget = engine.$f("dtbx");

        click(dtbx.$n("real"));
        dtbx.$n("real").eval("value='20091102'");
        blur(dtbx.$n("real"));
        waitResponse();

        verifyFalse("should not show error with date before (and include) 2009/11/02",
            jq(".z-errbox").exists());

        click(dtbx.$n("real"));
        dtbx.$n("real").eval("value='20091103'");
        blur(dtbx.$n("real"));
        waitResponse();

        verifyTrue("should show error with date after 2009/11/02",
            jq(".z-errbox").exists());
    }
   );

  }
}