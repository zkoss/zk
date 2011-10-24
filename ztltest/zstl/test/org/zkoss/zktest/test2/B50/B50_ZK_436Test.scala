/* B50_ZK_436Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 19 15:13:24 CST 2011 , Created by benbai
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
 * A test class for bug ZK-436
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-436.zul,A,E,IE,Iframe")
class B50_ZK_436Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
			You shouldn't see a "false" text in the page (IE only)
			<iframe/>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var bodyHTML: String = jq("body").get(0).get("innerHTML");
        var first: Int = bodyHTML.indexOf("false");
        var last: Int = bodyHTML.lastIndexOf("false");
        var first2: Int = bodyHTML.indexOf("\"false\"");

        verifyTrue("the only \"false\" should be the one in description",
            (first == last) && (first - first2 == 1));
    }
   );

  }
}