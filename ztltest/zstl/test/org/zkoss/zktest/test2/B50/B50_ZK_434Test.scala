/* B50_ZK_434Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 19 14:37:47 CST 2011 , Created by benbai
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.B50

import org.zkoss.zstl.ZTL4ScalaTestCase
import scala.collection.JavaConversions._
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.zkoss.ztl.Element;
import org.zkoss.ztl.JQuery;
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.util.Scripts;
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZK;
import org.zkoss.ztl.ZKClientTestCase;
import java.lang._

/**
 * A test class for bug ZK-434
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-434.zul,A,E,Timebox,Format,Delete")
class B50_ZK_434Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
			1. Please delete the "AM/PM" and then type 121212, it should appear "AM/PM" 12:12:12
			<separator/>
			2. Please delete the whole number area, for example 12:12:12, and then type 121212, it should appear "AM/PM" 12:12:12
			<separator/>
			<timebox id="tb" format="a h:m:s" width="150px" onCreate="self.value = new Date()" locale="en_US" />
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var tb: Widget = engine.$f("tb");
        var tbInp: Element = tb.$n("real");

        def toPos(pos: Int, inp: Element) {
            click(tbInp);
	        // move to the lift most side
        	for (i <- 1 to 10)
        		sendKeys(inp, Keys.LEFT);
	        // move to the lift most side
	        for (i <- 0 until pos)
	        	sendKeys(inp, Keys.RIGHT);
        }
        def toPixel(px: String, inp: Element) {
            click(tbInp);
            clickAt(inp, px); // left/right arrow keys not work on opera
        }
        def delete (inp: Element, delCnt: Int) {
        	for (i <- 1 to delCnt)
        	  sendKeys(inp, Keys.DELETE);
        }
        def inputThenVerify(inp: Element, value: String) {
        	sendKeys(inp, value);
        	verifyTrue("it should appear \"AM/PM\" 12:12:12 in timebox",
        	    inp.get("value").equals("AM 12:12:12") || inp.get("value").equals("PM 12:12:12"));
        }
        if (ZK.is("opera"))
            toPixel("3,5", tbInp);
        else
        	toPos(0, tbInp);
        delete(tbInp, 2);
        inputThenVerify(tbInp, "121212");
        if (ZK.is("opera")) {
            toPixel("25,5", tbInp);
            delete(tbInp, 6);
            toPixel("25,5", tbInp);
        } else {
        	toPos(3, tbInp);
        	delete(tbInp, 6);
        	toPos(3, tbInp);
        }
        inputThenVerify(tbInp, "121212");
    }
   );

  }
}