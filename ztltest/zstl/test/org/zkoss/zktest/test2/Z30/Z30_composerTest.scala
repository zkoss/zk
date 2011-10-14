/* Z30_composerTest.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 17:07:09 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

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
 * A test class for bug composer
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-composer.zul,A,E,Composer,apply,BI")
class Z30_composerTest extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
<window id="winO" title="Original" apply="org.zkoss.zktest.test2.MyComposer">
	You shall see the title starting with "Composer: ", and with a border.
	<window id="win1" title="You shall not see this" apply="org.zkoss.zktest.test2.VoidComposer">
	Something wrong if you saw this
	</window>
	<zscript>
	voidComposer = new org.zkoss.zktest.test2.VoidComposer();
	</zscript>
	<window id="win2"  title="You shall not see this 2" apply="org.zkoss.zktest.test2.MyComposer, ${voidComposer}">
	Something wrong if you saw this
	</window>
</window>

    }
    runZTL(zscript,
        () => {
        verifyEquals(jq("$win1").size,0)
        verifyEquals(jq("$win2").size,0)
        verifyEquals(widget(jq("$winO")).get("title"),"Composer: Original");
    }
   );
  }
}