/* B50_3325041Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 17:28:45 CST 2011 , Created by benbai
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
 * A test class for bug 3325041
 * @author benbai
 *
 */
@Tags(tags = "B50-3325041.zul,A,E,Doublespinner,Doublebox,Decimalbox")
class B50_3325041Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {

			<zk>
				try to type floating number like "1.23" in the double spinner.
				<separator/>
				If floating number can't type in, it's a bug
				<separator/>
				click button next to input, see if the alerted value is the same as valued inputted. 
				<separator/>
				follow above instruction for decimalbox and doublebox too.
				<separator/>
				
				doublespinner : <doublespinner id="ds"/>
				<button id="btn1" label="test1" onClick='alert(ds.value)'/>
				<separator/> 
				decimalbox: <decimalbox id="db"></decimalbox>
				<button id="btn2" label="test2" onClick='alert(db.value)'/>
				<separator/> 
				doublebox: <doublebox id="doub"></doublebox>
				<button id="btn3" label="test3" onClick='alert(doub.value)'/>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var (ds: Widget,
    	     db: Widget,
    	     doub: Widget,
    	     btn1: Widget,
    	     btn2: Widget,
    	     btn3: Widget) = (
    	        engine.$f("ds"),
    	        engine.$f("db"),
    	        engine.$f("doub"),
    	        engine.$f("btn1"),
    	        engine.$f("btn2"),
    	        engine.$f("btn3")
    	    );

        inputClickCheck(ds.$n("real"), "1.23", btn1);
        inputClickCheck(db.$n(), "1.23", btn2);
        inputClickCheck(doub.$n(), "1.23", btn3);
        def inputClickCheck(inp: Element, value: String, btn: Widget) {
        	click(inp);
        	inp.eval("value="+value);
        	blur(inp);

        	var vinp: String = inp.get("value");
        	verifyTrue("the value should be inputed correctly",
        	    vinp.equals(value));
        	click(btn);
        	waitResponse();

        	var vmsg: String = jq(".z-messagebox-window").find(".z-label").get(0).get("innerHTML");
        	verifyTrue("the value in messagebox should equal to inputed value",
        	    vmsg.equals(value));
        	click(jq(".z-messagebox-window").find(".z-button").get(0));
        	waitResponse();
        }
    }
   );

  }
}