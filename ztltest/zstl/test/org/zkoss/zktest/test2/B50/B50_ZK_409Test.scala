/* B50_ZK_409Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 19 11:05:38 CST 2011 , Created by benbai
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
 * A test class for bug ZK-409
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-409.zul,A,E,IdSpace")
class B50_ZK_409Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
			<zscript><![CDATA[
			class MyDiv extends org.zkoss.zul.Div implements IdSpace {
			}
			]]></zscript>
			<div use="MyDiv"/>
			<button id="btn" label="Click me, then close the messabebox, and click me again, you should not see any error" width="150px">
			<attribute name="onClick">
			Messagebox.show("Question is pressed. Are you sure?", "Question", Messagebox.OK | Messagebox.CANCEL, Messagebox.QUESTION);
			</attribute>
			</button>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var btn: Widget = engine.$f("btn");

        click(btn);
        waitResponse();
        click(jq(".z-window-highlighted").find(".z-window-highlighted-close"));
        waitResponse();
        click(btn);
        waitResponse();

        // for exception alert
   		verifyFalse("should not have modal window for exception",
   		    jq(".z-window-modal").exists())
   		// for js error
   		verifyFalse("should not have any js error",
   		    jq(".z-error").exists());
   		var hlWins: JQuery = jq(".z-window-highlighted");
   		verifyTrue( "only one highlighted window and contains expected message",
   		    (hlWins.length() == 1) && (hlWins.find(".z-messagebox").find(".z-label")
   		        .get(0).get("innerHTML").contains("Question is pressed. Are you sure?")) );
    }
   );

  }
}