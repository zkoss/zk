/* ExampleTest.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 7, 2011 5:38:14 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.fun

import org.zkoss.zstl.ZTL4ScalaTestCase
import scala.collection.JavaConversions._
import org.junit.Test;
import org.zkoss.ztl.Element;
import org.zkoss.ztl.JQuery;
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZK;
import org.zkoss.ztl.ZKClientTestCase;
import java.lang._

/**
 * An example for ZTL to run with Scala
 * @author jumperchen
 *
 */
@Tags(tags = "Example")
class ExampleTest extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
      <vbox>
        Click "hello world", if you see the message change, it is ok.
        <zscript><![CDATA[
			public void doCreate(Event evt) {
				evt.target.setValue("    hello,\n  world");
			}
			public void doClick(Event evt) {
				evt.target.setValue("    I have\n  been   clicked");
			}
		]]></zscript>
        <label id="l1" onCreate="doCreate(event);" onClick="doClick(event)" style="font-family: monospace; white-space: pre;"/>
        <label id="l2" onCreate="doCreate(event);" onClick="doClick(event)" pre="true"/>
      </vbox>
    }
    
    def executor = ()=> {
        var l1: Widget = engine.$f("l1");
        var l2: Widget = engine.$f("l2");
        waitResponse();
        var strClickBefor = getText(l1);
        click(l1);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l1));
        strClickBefor = getText(l2);
        click(l2);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l2));
    }
   
   // Run syntax 1 
   runZTL(zscript, executor);
   
   // Run syntax 2
   /**
    runZTL(zscript,
        () => {
        var l1: Widget = engine.$f("l1");
        var l2: Widget = engine.$f("l2");
        waitResponse();
        var strClickBefor = getText(l1);
        click(l1);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l1));
        strClickBefor = getText(l2);
        click(l2);
        waitResponse();
        verifyNotEquals(strClickBefor, getText(l2));
    }
   );
    */
  }
}
