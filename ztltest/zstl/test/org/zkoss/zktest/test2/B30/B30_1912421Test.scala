/* B30_1912421Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 11, 2011 3:55:21 PM , Created by jumperchen
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.B30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * @author jumperchen
 *
 */
@Tags(tags = "B30-1912421.zul,B,E,Window,Button")
class B30_1912421Test extends ZTL4ScalaTestCase {
  def testClick() = {
    val zscript = {
      <zk xmlns:n="http://www.zkoss.org/2005/zk/native">
        <n:p>1.Click the button</n:p>
        <n:p>2.Overlapped window shall NOT disappears</n:p>
        <window id="win" title="I am here" border="normal" width="200px" mode="overlapped">
          never gone
          <button label="Please Click Me" onClick='win.invalidate()'/>
        </window>
      </zk>
    }
    runZTL(zscript, () => {
      verifyTrue(jq("$win").isVisible());
      click(jq("@button"));
      waitResponse();
      verifyTrue(jq("$win").isVisible());
    })
  }
}