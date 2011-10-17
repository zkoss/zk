/* B30_1943594Test.scala

	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 12:11:10 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2.B30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;
import org.zkoss.ztl.util.Scripts

/**
 *
 * @author jumperchen
 */
@Tags(tags = "B30-1943594.zul,A,E,Combobox,Datebox,Calendar")
class B30_1943594Test extends ZTL4ScalaTestCase {
	def testCase() = {
		val zscript = {
			<window border="none" width="100%" xmlns="http://www.zkoss.org/2005/zul">
				<html><![CDATA[
				<ul>
				<li>
				Click dropdown and select an item. A exception dialog shall show up.
				Click OK to close the  dialog. Then, the dialog shall <b>NOT</b> re-appear again.</li>
				<li>Click combobox's dropdown button, then click other place of this page.
				A exception dialog shall show up.Click OK to close the dialog. Then, the dialog shall <b>not</b>
				re-appear again.</li>
				</ul>
				]]></html>
				<a label=" "/>
				<zscript><![CDATA[
					public class MyCombo extends Combobox implements EventListener{
						public MyCombo () {
							addEventListener("onBlur", this);
						}
						public void onEvent(Event event) throws Exception {
							if ("onBlur".equals(event.getName())) {
								throw new java.io.NotSerializableException();
							}
						}
					}
					public class MyDate extends Datebox implements EventListener{
						public MyDate () {
							addEventListener("onBlur", this);
						}
						public void onEvent(Event event) throws Exception {
							if ("onBlur".equals(event.getName())) {
								throw new java.io.NotSerializableException();
							}
						}
					}
				]]></zscript>
				<combobox autodrop="true" use="MyCombo">
					<comboitem label="Value 1"/>
					<comboitem label="Value 2"/>
					<comboitem label="Value 3"/>
					<comboitem label="Value 4"/>
					<comboitem label="Value 5"/>
				</combobox>
				<label id="a" value="test"/>
				<datebox use="MyDate"/>
			</window>
		}
		runZTL(zscript, () => {
			
			// test combobox
			click(jq(".z-combobox-btn"))
			waitResponse
			click(jq("@comboitem:eq(0)"))
			click(jq("$a"))
			waitResponse
			verifyTrue(jq(".z-messagebox").exists())
			Scripts.triggerMouseEventAt(getWebDriver(), jq("@button"), "click", "2,2")
			waitResponse
			verifyFalse(jq(".z-messagebox").exists())
			
			// test datebox
			click(jq(".z-datebox-btn"))
			waitResponse
			click(jq("$a"))
			waitResponse
			verifyTrue(jq(".z-messagebox").exists())
			click(jq("@button"))
			waitResponse
			verifyFalse(jq(".z-messagebox").exists())
		})
	}
}
