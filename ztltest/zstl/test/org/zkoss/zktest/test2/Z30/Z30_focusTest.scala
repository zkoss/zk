/* Z30-focusTest.scala

	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 16:23:10 TST 2011, Created by TonyQ

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 *
 * @author jumperchen
 */
@Tags(tags = "Z30-focus.zul,C,E,Window,Textbox")
class Z30_focusTest extends ZTL4ScalaTestCase {
	def testCase() = {
		val zscript = {
		  <window title="Test of focus">
<html><![CDATA[
<ol>
<li>Click "Try 1" and the following window shall become modal
and the focus remains on the first textbox</li>
<li>Click "Restore to embedded"</li>
<li>Click "Try 2' the focus shall remains on the second modal</li>
</ol>
]]></html>
	<window id="w" title="Window 1" border="normal" width="300px">
		<textbox id="t1" value="First"/>
		<textbox id="t2" value="Second"/>
		<button label="Restore to embedded" onClick="w.doEmbedded()"/>
	<button id="tb1" label="Try 1" onClick="t1.focus();w.doModal()"/>
	<button id="tb2" label="Try 2" onClick="t2.focus();w.doModal()"/>
	</window>
</window>
		}
		runZTL(zscript, () => {
			click(jq("$tb1"))
			waitResponse
			verifyEquals(this.getEval("zk.currentFocus.id"),"t1");
			click(jq("$tb2"))
			waitResponse()
			verifyEquals(this.getEval("zk.currentFocus.id"),"t2");
			waitResponse
		})
	}
}
