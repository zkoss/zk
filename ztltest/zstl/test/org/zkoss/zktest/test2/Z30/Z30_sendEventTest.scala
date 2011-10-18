/* Z30_sendEventTest.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 18:55:29 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug sendEvent
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-sendEvent.zul,Z30,A,E,Server")
class Z30_sendEventTest extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = {
			<vbox>
				sendEvent: You shall see TWO "done" lines after clicking the Test1 button.
				<button id="btn1" label="Test1" onClick='Events.sendEvent(new Event("onOK", self));b.value = a.value'
				onOK='a.value = "done"'/>
				<label id="a"/>
				<label id="b"/>
			
				postEvent: You shall see ONE "done" lines after clicking the Test2 button.
				<button id="btn2"  label="Test2" onClick='Events.postEvent(new Event("onOK", self));y.value = x.value'
				onOK='x.value = "done"'/>
				<label id="x"/>
				<label id="y"/>
			</vbox>

    }

    runZTL(zscript,
        () => {
          
          click(jq("$btn1"));
          waitResponse();
          
          verifyEquals(jq("$a").text(),"done");
          verifyEquals(jq("$b").text(),"done");
          
          
          click(jq("$btn2"));
          waitResponse();
          verifyEquals(jq("$x").text(),"done");
          verifyEquals(jq("$y").text(),"");
          
          
    });
  }
}