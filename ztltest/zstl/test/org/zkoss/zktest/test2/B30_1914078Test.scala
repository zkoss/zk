/* B30_1914078Test.scala

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
package org.zkoss.zktest.test2

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags
import org.zkoss.ztl.JQuery
import org.zkoss.ztl.ZK

/**
 * @author jumperchen
 *
 */
@Tags(tags = "B30-1914078.zul,C,E,Window,Animation")
class B30_1914078Test extends ZTL4ScalaTestCase {
  def testPosition() = {
    val zscript = {
      <window title="Animation Effects" border="normal" xmlns:h="http://www.w3.org/1999/xhtml">
        <h:p>Repeatedly click on "Toggle Visible" Button. If the Window pops up from top-right corner, it is a bug.</h:p>
        <button label="Toggle Visible" onClick="win.visible = !win.visible"/>
        <window id="win" border="normal" width="200px" position="center" mode="overlapped" visible="false">
          <caption image="/test2/img/inet.png" label="Hi there!"/>
          <checkbox label="Hello, Effects!"/>
        </window>
      </window>
    }

    runZTL(zscript, () => {
      val btn = jq("@button");
      val win = jq("$win")
      val (width, height) = (jq("body").width() / 2, jq("body").height() / 2);
      for (i <- 0 until 6) {
        click(btn)
        if (i %2 == 0) {
        	waitResponse(true)
        	var (left, top) = (win.offsetLeft(), win.offsetTop())
        	verifyTrue(top > 100);
			verifyTrue(top < height);
			verifyTrue(left > 100);
			verifyTrue(left < width);
        }
      }
    })
  }
}