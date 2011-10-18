/* B50_3309256Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 11:24:05 CST 2011 , Created by benbai
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
 * A test class for bug 3309256
 * @author benbai
 *
 */
@Tags(tags = "B50-3309256.zul,A,E,Datebox,Timebox,Locale,Format")
class B50_3309256Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
			If the text of the input element contains single quote ('), that is a bug.
			<datebox id="dbx" format="long" locale="zh_TW" width="140px" onCreate="self.value = new Date()"/>
			<timebox id="tbx" format="long" locale="zh_TW" width="140px" onCreate="self.value = new Date()"/>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var dbx: Widget = engine.$f("dbx");
        var tbx: Widget = engine.$f("tbx");

        verifyTrue("the text of the input element should be ready (length >= 8",
            dbx.$n("real").get("value").length() >= 8);
        verifyTrue("the text of the input element should be ready (length >= 8",
            tbx.$n("real").get("value").length() >= 8);
        verifyFalse("If the text of the input element should not contains single quote (')",
            dbx.$n("real").get("value").contains("'"));
        verifyFalse("If the text of the input element should not contains single quote (')",
            tbx.$n("real").get("value").contains("'"));
    }
   );

  }
}