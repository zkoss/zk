/* B30_1936366Test.scala

	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 12:01:07 TST 2011, Created by jumperchen

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
@Tags(tags = "B30-1936366.zul,B,E,Textbox,Focus")
class B30_1936366Test extends ZTL4ScalaTestCase {
	def testCase() = {
		val zscript = {
			<zk xmlns:n="http://www.zkoss.org/2005/zk/native">
				<n:p>Click the "Warning" button, and then close it, and then you should see the focus on the input element.</n:p>
				<separator/>
				<window title="Messagebox demo" border="normal">
					<textbox id="focus"/>
					<button label="Warning" width="100px">
						<attribute name="onClick"><![CDATA[{
							Messagebox.show("Warning is pressed", "Warning", Messagebox.OK,
								Messagebox.EXCLAMATION, new EventListener() {
									public void onEvent(Event event) throws Exception {
										focus.focus();
									}
								});

						}]]></attribute>
					</button>
				</window>
			</zk>
		}
		runZTL(zscript, () => {
			click(jq("@button:eq(0)"))
			waitResponse
			click(jq("@button:eq(1)"))
			waitResponse
			val focus = engine $f ("focus")
			verifyTrue(jq(focus).hasClass("z-textbox-focus"))
		})
	}
}
