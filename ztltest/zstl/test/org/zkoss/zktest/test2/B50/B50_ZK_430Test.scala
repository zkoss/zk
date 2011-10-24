/* B50_ZK_430Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct 19 12:24:08 CST 2011 , Created by benbai
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
 * A test class for bug ZK-430
 * @author benbai
 *
 */
@Tags(tags = "B50-ZK-430.zul,A,E,Timebox,Format")
class B50_ZK_430Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<zk>
			1. Please focus into the first timebox and then select all(ctrl+A) to delete the content.
			<separator/>
			2. Type 1212 into the timebox, it should be able to type and display "1212".
			<separator/>
			<timebox id="tb1" cols="14" format="HHmm" onCreate="self.value = new Date()" mold="rounded" locale="en_US" />
			<separator/>
			3. Please focus into the second timebox and then select all(ctrl+A) to delete the content.
			<separator/>
			4. Type 121212 into the timebox, it should be able to type and display "PM 12:12:12" or "AM 12:12:12" (depended on when you test it).
			<separator/>
			<timebox id="tb2" format="a HH:mm:ss" width="150px" onCreate="self.value = new Date()" locale="en_US" />
			<separator/>
			5. Please focus into the last timebox and put your cursor after "AM00" or "PM00" (depended on when you test it).
			<separator/>
			6. Press the UP or Down arrow key on the keyboard to change it, and then it should be only changed in the number area (not AM or PM)
			<separator/>
			<timebox id="tb3" format="aHH:mm:ss" width="150px" locale="en_US" />
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var (tb1: Widget,
    	     tb2: Widget,
    	     tb3: Widget) = (
    	        engine.$f("tb1"),
    	        engine.$f("tb2"),
    	        engine.$f("tb3")
    	    );
        var (tb1Inp: Element,
    	     tb2Inp: Element,
    	     tb3Inp: Element) = (
    	        tb1.$n("real"),
    	        tb2.$n("real"),
    	        tb3.$n("real")
    	    );

        def clearAndInput (ele: Element, value: String) {
        	ele.eval("focus()");
        	ele.eval("select()");
        	sendKeys(ele, Keys.DELETE);
        	sendKeys(ele, value);
        }
        clearAndInput(tb1Inp, "1212");
        verifyTrue("it should be able to type and display \"1212\" of first timebox.",
            "1212".equals(tb1Inp.get("value")));
        clearAndInput(tb2Inp, "121212");
        verifyTrue("Type 121212 into the timebox, it should be able to type and display \"PM 12:12:12\" or \"AM 12:12:12\" (depended on when you test it).",
            "AM 12:12:12".equals(tb2Inp.get("value")) || "PM 12:12:12".equals(tb2Inp.get("value")));
        click(tb3Inp);
        if (ZK.is("opera"))
        	clickAt(tb3Inp,"35,5"); // left/right arrow keys not work on opera
        else {
	        // move to the lift most side
	        for (i <- 1 to 10)
	        	sendKeys(tb3Inp, Keys.LEFT);
	        // move to the position after AM00/PM00
	        for (j <- 1 to 4)
	        	sendKeys(tb3Inp, Keys.RIGHT);
        }
        clickAndCheck(tb3.$n("btn-up"));
        clickAndCheck(tb3.$n("btn-up"));
        clickAndCheck(tb3.$n("btn-down"));
        clickAndCheck(tb3.$n("btn-down"));
        def clickAndCheck (ele: Element) {
        	var checkStr: String = "PM";
        	if (!tb3Inp.get("value").contains(checkStr))
        		checkStr = "AM";
        	click(ele);
        	verifyTrue("should change AM/PM",
        	    tb3Inp.get("value").contains(checkStr));
        }
    }
   );

  }
}