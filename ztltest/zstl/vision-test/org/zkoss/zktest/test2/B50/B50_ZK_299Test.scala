/* B50_ZK_299Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Oct 18, 2011 11:08:34 AM , Created by jumperchen
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
import org.zkoss.ztl.Widget;
import org.zkoss.ztl.ZK;
import org.zkoss.ztl.ZKClientTestCase;
import java.lang._

/**
 *
 * @author jumperchen
 *
 */
@Tags(tags = "B50-ZK-299.zul,B,E,Grid,Detail,Chrome,VisionTest")
class B50_ZK_299Test extends ZTL4ScalaTestCase {
	def resetTheme(theme: String) {
		runZTL({
			"""<zk>
				<zscript><![CDATA[
					org.zkoss.zkplus.theme.Themes.setTheme(Executions.getCurrent(), """" + theme + """");
				]]></zscript>
			</zk>"""
		},
			() => {
				refresh();
			})
	}

	def testDetailTheme() = {
		val zscript = {
			<zk>
				the detail's icon (+/-) should display correctly in each theme
				<grid width="600px">
					<rows>
						<row>
							<detail>
								<vbox>
									<label value="Item Specifics - Item Condition"/>
									<hbox>
										<label value="Condition:"/>
										<label value="Used"/>
									</hbox>
									<hbox>
										<label value="Brand:"/>
										<label value="Apple"/>
									</hbox>
									<hbox>
										<label value="Technology:"/>
										<label value="DVI"/>
									</hbox>
								</vbox>
							</detail>
							<label value="Apple 20-inch Aluminum Cinema Display M9177/A"/>
							<label value="US $202.50"/>
							<label value="tulsa, ok, United States"/>
						</row>
						<row>
							<detail fulfill="onOpen">
								<vbox>
									<label value="Item Specifics"/>
									<hbox>
										<label value="Condition:"/>
										<label value="Used"/>
									</hbox>
									<hbox>
										<label value="Brand:"/>
										<label value="Kyocera"/>
									</hbox>
									<hbox>
										<label value="Phone Type:"/>
										<label value="Phones without Service Contrac"/>
									</hbox>
									<hbox>
										<label value="Product Type:"/>
										<label value="Cell Phones"/>
									</hbox>
								</vbox>
							</detail>
							<label value="Kyocera Strobe K612B MetroPCS Metro PCS Cell Phone L@@K"/>
							<label value="US $74.99"/>
							<label value="Speedy Shipping, USA, United States"/>
						</row>
					</rows>
				</grid>
			</zk>
		}

		try {
			runZTL(
				<zk></zk>,
				() => {
					for (theme <- List("breeze", "classicblue", "sapphire", "silvertail")) {
						runRawZscript({
							"""<zk>
								<zscript><![CDATA[
									org.zkoss.zkplus.theme.Themes.setTheme(Executions.getCurrent(), """" + theme + """");
								]]></zscript>
							</zk>"""
						})
						refresh()
						waitForPageToLoad("10000")
						waitResponse

						runRawZscript(zscript.toString())
						waitResponse
						verifyImage()
					}
				});
		} finally {
			resetTheme("breeze")
		}

	}

	def runRawZscript(zscript: String) {
		runZscript(
			zscript
				trim ()
				replace ("\\", "\\\\")
				replace ("'", "\\'")
				replaceAll ("\r", "")
				replaceAll ("\n", "\\\\n"))
	}
}
