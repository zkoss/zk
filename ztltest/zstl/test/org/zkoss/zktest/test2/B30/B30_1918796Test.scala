/* B30_1918796Test.scala

	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 10:54:58 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2.B30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 *
 * @author jumperchen
 */
@Tags(tags = "B30-1918796.zul,A,E,Grid,Groupbox")
class B30_1918796Test extends ZTL4ScalaTestCase {
  def testCase() = {
    val zscript = {
      <zk xmlns:n="http://www.zkoss.org/2005/zk/native">
        <n:p>Grid header should disappear in this case.</n:p>
        <window width="250px">
          <groupbox visible="false" id="gpb">
            <caption label="caption"/>
            <grid>
              <columns>
                <column/>
              </columns>
              <rows>
                <row>Test:</row>
              </rows>
            </grid>
          </groupbox>
          <button label="Show Hide 2nd groupbox" onClick="gpb.setVisible(!gpb.isVisible())"/>
        </window>
      </zk>
    }
    runZTL(zscript, () => {
    	verifyFalse("grid should not be visible", jq("@grid").isVisible)
    	click(jq("@button"))
    	waitResponse
    	verifyTrue("grid should be visible", jq("@grid").isVisible)
    })
  }
}
