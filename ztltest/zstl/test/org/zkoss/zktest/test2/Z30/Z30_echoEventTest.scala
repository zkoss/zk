/* Z30_echoEventTest.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 17:28:12 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug echoEvent
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-echoEvent.zul,B,E,Server,BI")
class Z30_echoEventTest extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
<window id="w" title="Test echoEvent">
	After clicked, you shall see "Execute..." and then, after 2 seconds, "Done.".

	<attribute name="onLater"><![CDATA[
	Thread.sleep(2000);
	new Label("Done.").setParent(w);
	]]></attribute>

	<button id="btn" label="echo">
	<attribute name="onClick"><![CDATA[
	new Label("Execute...").setParent(w);
	Events.echoEvent("onLater", w, null);
	]]></attribute>
	</button>
</window>

    }

    runZTL(zscript,
        () => {
        verifyEquals(jq("$w @label").length,1)
        click(jq("$btn"))
        sleep(1000);
        verifyEquals(jq("$w @label").length,2)
        waitResponse
        verifyEquals(jq("$w @label").length,3)
        
    }
   );
  }
}