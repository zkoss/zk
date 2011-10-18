/* B50_3330762Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 17:09:43 CST 2011 , Created by benbai
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
 * A test class for bug 3330762
 * @author benbai
 *
 */
@Tags(tags = "B50-3330762.zul,A,E,Doublespinner")
class B50_3330762Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				<separator/>
					1. Input "1" in spinner and click button "test"
				<separator/>
					2. If an exception is thrown, it is a bug.
				<separator/>
					3. Click the up arrow, the doublespinner value should change to 1.1
				<separator/>
					4. Click button "test", if there is an exception, it is a bug.
				<separator/>
				<doublespinner id="ds" step="0.1" />
				<button id="btn" label="test" onClick='lb.value = "" + ds.value' />
				Doublespinner Value: <label id="lb" />
			</zk>

    }
 
   // Run syntax 2
    runZTL(zscript,
        () => {
        var ds: Widget = engine.$f("ds");
        var btn: Widget = engine.$f("btn");
        var lb: Widget = engine.$f("lb");
        var dsInp: Element = ds.$n("real");

        click(dsInp);
        dsInp.eval("value=1");
        blur(dsInp);
        clickThenCheck("1.0");

        click(ds.$n("btn-up"));
        clickThenCheck("1.1");

        def clickThenCheck(value: String) {
        	click(btn);
        	waitResponse();

        	verifyTrue("the value of label should equal to the value of doublespinner",
        	    value.equals(lb.$n().get("innerHTML")));
        	verifyFalse("should not have Exception",
        	    jq(".z-window-highlighted").exists());
        	verifyFalse("should not have Exception",
        	    jq(".z-window-modal").exists())
        }
    }
   );

  }
}