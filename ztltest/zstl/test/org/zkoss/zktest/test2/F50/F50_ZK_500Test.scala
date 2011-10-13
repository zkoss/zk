/* F50_ZK_500Test.scala

	Purpose:
		
	Description:
		
	History:
		Thu Oct 13 12:00:03 TST 2011, Created by jumperchen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2.F50

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 *
 * @author jumperchen
 */
@Tags(tags = "")
class F50_ZK_500Test extends ZTL4ScalaTestCase {
	def testCase() = {
		val zscript = {
			<zk>
				<vlayout>
					1. You should see 1,234.57
					<hlayout>
						<decimalbox locale="en_US" value="1234.567"/>
						<doublebox locale="en_US" value="1234.567"/>
						<doublespinner locale="en_US" value="1234.567"/>
					</hlayout>
					2. You should see 1,234
					<hlayout>
						<intbox locale="en_US" value="1234"/>
						<longbox locale="en_US" value="1234"/>
						<spinner locale="en_US" value="1234"/>
					</hlayout>
					3. You should see 01,123.57
					<hlayout>
						<decimalbox locale="en_US" format="00,000.00" value="1234.567"/>
						<doublebox locale="en_US" format="00,000.00" value="1234.567"/>
						<doublespinner locale="en_US" format="00,000.00" value="1234.567"/>
					</hlayout>
					4. You should see 01,234.00
					<hlayout>
						<intbox locale="en_US" format="00,000.00" value="1234"/>
						<longbox locale="en_US" format="00,000.00" value="1234"/>
						<spinner locale="en_US" format="00,000.00" value="1234"/>
					</hlayout>
					<separator/>
					<div style="border:1px solid blue">
						5. You should see 1234.567
						<hlayout>
							<decimalbox id="inp1" value="1234.567"/>
							<doublebox id="inp2" value="1234.567"/>
							<doublespinner id="inp3" value="1234.567"/>
						</hlayout>
						6. You should see 1234
						<hlayout>
							<intbox id="inp4" value="1234"/>
							<longbox id="inp5" value="1234"/>
							<spinner id="inp6" value="1234"/>
						</hlayout>
						7. Please<button label="click me">
						<attribute name="onClick"><![CDATA[
        inp1.setLocale("en_US");
      	inp2.setLocale("en_US");
      	inp3.setLocale("en_US");
      	inp4.setLocale("en_US");
      	inp5.setLocale("en_US");
      	inp6.setLocale("en_US");
						]]></attribute>
					</button>
						Then, the above 1234.567 will change to 1,234.57 and the 1234 will change to 1,234 (only inside the blue area)
					</div>
				</vlayout>
			</zk>
		}
		runZTL(zscript, () => {
			var value = "1,234.57"
			verifyEquals(value, jq("@decimalbox:eq(0)").`val`())
			verifyEquals(value, jq("@doublebox:eq(0)").`val`())
			verifyEquals(value, jq("@doublespinner:eq(0) input").`val`())
			
			value = "1,234"
			verifyEquals(value, jq("@intbox:eq(0)").`val`())
			verifyEquals(value, jq("@longbox:eq(0)").`val`())
			verifyEquals(value, jq("@spinner:eq(0) input").`val`())
			
			value = "01,234.57"
			verifyEquals(value, jq("@decimalbox:eq(1)").`val`())
			verifyEquals(value, jq("@doublebox:eq(1)").`val`())
			verifyEquals(value, jq("@doublespinner:eq(1) input").`val`())
			
			value = "01,234.00"
			verifyEquals(value, jq("@intbox:eq(1)").`val`())
			verifyEquals(value, jq("@longbox:eq(1)").`val`())
			verifyEquals(value, jq("@spinner:eq(1) input").`val`())
			
			value = "1234.567"
			verifyEquals(value, jq("@decimalbox:eq(2)").`val`())
			verifyEquals(value, jq("@doublebox:eq(2)").`val`())
			verifyEquals(value, jq("@doublespinner:eq(2) input").`val`())
			
			value = "1234"
			verifyEquals(value, jq("@intbox:eq(2)").`val`())
			verifyEquals(value, jq("@longbox:eq(2)").`val`())
			verifyEquals(value, jq("@spinner:eq(2) input").`val`())
			
			click(jq("@button"))
			waitResponse
			value = "1,234.57"
			verifyEquals(value, jq("@decimalbox:eq(2)").`val`())
			verifyEquals(value, jq("@doublebox:eq(2)").`val`())
			verifyEquals(value, jq("@doublespinner:eq(2) input").`val`())
			
			value = "1,234"
			verifyEquals(value, jq("@intbox:eq(2)").`val`())
			verifyEquals(value, jq("@longbox:eq(2)").`val`())
			verifyEquals(value, jq("@spinner:eq(2) input").`val`())
		})
	}
}
