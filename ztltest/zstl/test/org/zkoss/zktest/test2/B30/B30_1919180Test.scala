/* B30_1919180Test.scala

	Purpose:
		
	Description:
		
	History:
		Wed Oct 12 11:01:09 TST 2011, Created by jumperchen

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
@Tags(tags = "B30-1919180.zul,B,E,Grid")
class B30_1919180Test extends ZTL4ScalaTestCase {
	def testCase() = {
		val zscript = {
			<zk xmlns:n="http://www.zkoss.org/2005/zk/native">
				<n:ol>
					<n:li>Click add row(end)</n:li>
					<n:li>Click setColumnWidth1</n:li>
					<n:li>Click setColumnWidth2</n:li>
					<n:li>Click setColumnWidth3</n:li>
					<n:li>Do 2,3,4 again</n:li>
				</n:ol>
				<zscript><![CDATA[
          void addRow1(){
            Row row = new Row();
            row.setParent(rows);
            new Label("Label x").setParent(row);
            new Textbox().setParent(row);
            new Datebox().setParent(row);
          }
          ;
        ]]></zscript>
				<vbox>
					<div width="500px">
						<button label="add row(end)" onClick="addRow1()"/>
						<button label="setColumnWidth1" onClick='col1.setWidth("50px")'/>
						<button label="setColumnWidth2" onClick='col1.setWidth("150px")'/>
						<button label="setColumnWidth3" onClick='col1.setWidth("300px")'/>
					</div>
					<grid id="g1" width="400px">
						<columns id="cols" sizable="true">
							<column label="Type 50px" id="col1" align="center" width="50px"/>
							<column label="Content" id="col2" align="right"/>
							<column label="AA-BB" id="col3"/>
						</columns>
						<rows id="rows">
						</rows>
					</grid>
				</vbox>
			</zk>

		}
		runZTL(zscript, () => {
			val g1 = engine $f "g1"
			val $col1hd = jq(engine $f "col1" $n "hdfaker")
			val $col1bd = jq(engine $f "col1" $n "bdfaker")
			val $col2hd = jq(engine $f "col2" $n "hdfaker")
			val $col2bd = jq(engine $f "col2" $n "bdfaker")
			val $col3hd = jq(engine $f "col3" $n "hdfaker")
			val $col3bd = jq(engine $f "col3" $n "bdfaker")

			verifyEquals(50, $col1hd.outerWidth())
			verifyEquals(50, $col1bd.outerWidth())

			var halfWidth = (400 - 50) / 2
			verifyEquals(halfWidth, $col2hd.outerWidth())
			verifyEquals(halfWidth, $col2bd.outerWidth())
			verifyEquals(halfWidth, $col3hd.outerWidth())
			verifyEquals(halfWidth, $col3bd.outerWidth())

			// add row
			click(jq("@button:eq(0)"))
			val btn1 = jq("@button:eq(1)")
			val btn2 = jq("@button:eq(2)")
			val btn3 = jq("@button:eq(3)")

			for ((btn, size) <- List((btn2, 150), (btn3, 300), (btn1, 50))) {
				click(btn)
				waitResponse
				verifyEquals(size, $col1hd.outerWidth())
				verifyEquals(size, $col1bd.outerWidth())

				halfWidth = (400 - size) / 2
				verifyEquals(halfWidth, $col2hd.outerWidth())
				verifyEquals(halfWidth, $col2bd.outerWidth())
				verifyEquals(halfWidth, $col3hd.outerWidth())
				verifyEquals(halfWidth, $col3bd.outerWidth())
			}
		})
	}
}
