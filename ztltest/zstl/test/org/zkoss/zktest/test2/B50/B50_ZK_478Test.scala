/* B50_ZK_478Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 19 18:36:39 CST 2011 , Created by benbai
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
 * A test class for bug ZK-478
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-478.zul,")
class B50_ZK_478Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				<div>The Imagemap size should be 300px by 300px.</div>
				<imagemap id="imap" width="300px" height="300px" src="/test2/img/icon_browser.png" />
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var imap: Widget = engine.$f("imap");

        verifyTrue("the image size should be 300 x 300",
            (jq(imap.$n("real")).outerHeight() == 300) && (jq(imap.$n("real")).outerWidth() == 300));
    }
   );

  }
}