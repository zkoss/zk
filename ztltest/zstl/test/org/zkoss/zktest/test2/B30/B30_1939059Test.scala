/* B30_1939059Test.scala

	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 12:06:23 TST 2011, Created by jumperchen

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
@Tags(tags = "B30-1939059.zul,A,E,UI")
class B30_1939059Test extends ZTL4ScalaTestCase {
	def testCase() = {
		val zscript = {
			<zk xmlns:n="http://www.zkoss.org/2005/zk/native">
				<button label="Hi" onClick='alert("Hi")'/>
				<n:p>Click Hi and you shall see a message box (rather than JS error).</n:p>
			</zk>
		}
		runZTL(zscript, () => {
			click(jq("@button"))
			waitResponse
			verifyFalse(jq(".z-errbox").exists());
			verifyFalse(jq(".z-error").exists());
		})
	}
}
