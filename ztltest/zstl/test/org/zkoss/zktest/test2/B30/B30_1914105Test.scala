/* B30_1914105Test.scala

	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 09:45:50 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2.B30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags
import org.zkoss.ztl.ZK
import org.openqa.selenium.Keys

/**
 *
 * @author jumperchen
 */
@Tags(tags = "B30-1914105.zul,B,E,Timebox")
class B30_1914105Test extends ZTL4ScalaTestCase {
  def testCase() = {
    val zscript = {
      <zk xmlns:n="http://www.zkoss.org/2005/zk/native">
        1.click readonly,
    	2.click timebox
    	3.you still can change the value by key.
        <n:h2>Readonly shall disable typing</n:h2>
        <n:ol>
          <n:li>When readonly, you can only click (cannot type)</n:li>
          <n:li>When disable, you cannot do anything</n:li>
        </n:ol>
        <timebox id="tb" locale="en_US" format="hh:mm:ss"/>
        <checkbox id="ck1" label="readonly" checked="tb.readonly" onCheck="tb.readonly = self.checked"/>
        <checkbox id="ck2" label="disabled" checked="tb.disabled" onCheck="tb.disabled = self.checked"/>
      </zk>
    }
    runZTL(zscript, () => {
      val tb = engine.$f("tb")
      val ck1 = engine.$f("ck1")
      val ck2 = engine.$f("ck2")
      sendKeys(tb.$n("real"), "12:00:00")
      verifyTrue("value should start with 12:00:00", jq(tb.$n("real")).`val`().startsWith("12:00:00"))

      // test readonly
      click(ck1.$n("real"))
      waitResponse()
      sendKeys(tb.$n("real"), "12:00:01")
      verifyEquals("value should be 12:00:00", jq(tb.$n("real")).`val`(), "12:00:00")

      mouseDownAt(jq(".z-timebox-btn-upper"), "1,1");
      mouseUp(jq(".z-timebox-btn-upper"));

      val value = jq(tb.$n("real")).`val`()

      verifyNotEquals("value should not be 12:00:00", value, "12:00:00")

      // test disabled
      click(ck2.$n("real"))
      waitResponse()

      mouseDownAt(jq(".z-timebox-btn-upper"), "1,1");
      mouseUp(jq(".z-timebox-btn-upper"));

      verifyEquals("value should be " + value, jq(tb.$n("real")).`val`(), value)
    })
  }
}
