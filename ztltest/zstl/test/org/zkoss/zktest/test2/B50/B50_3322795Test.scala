/* B50_3322795Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Oct 18 18:30:35 CST 2011 , Created by benbai
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
 * A test class for bug 3322795
 * @author benbai
 *
 */
@Tags(tags = "B50-3322795.zul,A,E,Doublespinner")
class B50_3322795Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			
			<zk>
				1. click on the right side up/down button several times
				<separator/>
				2. the value change is 1 each time, the value inside should be ... 9.7, 8.7, 7.7, 6.7,... 
				<separator/>
				
				<doublespinner id="ds" value="8.7" width="200px"></doublespinner>
			</zk>

    }

   // Run syntax 2
    runZTL(zscript,
        () => {
        var ds: Widget = engine.$f("ds");
        var value: Int = 8;
        for (i <- 1 until 5) {
        	value += 1;
        	click(ds.$n("btn-up"));
        	waitResponse();

        	verifyTrue("the value change is 1 each time",
        	    (value+".7").equals(ds.$n("real").get("value")));
        }
        for (j <- 1 until 10) {
        	value -= 1;
        	click(ds.$n("btn-down"));
        	waitResponse();
        	verifyTrue("the value change is 1 each time",
        	    (value+".7").equals(ds.$n("real").get("value")));
        }
    }
   );

  }
}