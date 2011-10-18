/* Z30_forEachTest.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Fri Oct 14 17:49:11 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug forEach
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-forEach.zul,Z30,B,E,forEach,forEachBegin,BI")
class Z30_forEachTest extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = 
			"""
			<window>
				<zscript>
			List items = new java.util.AbstractList() {
				public int size() {
					return 100;
				}
				public Object get(int j) {
					return new Integer(j);
					}
			};
				int end = 50;
				</zscript>
				Show 5 to 50 and select 10.
				<listbox id="l" rows="50">
					<listitem forEach="${items}" forEachBegin="5" forEachEnd="${end}"
					selected="${forEachStatus.index == 10}">
					<listcell label="${each}"/>
					<listcell label="${each}"/>
					<listcell label="${each}"/>
					<listcell label="${each}"/>
					</listitem>
				</listbox>
			</window>

    """;

    runZTL(zscript,
        () => {
        var iter = jq("@row").iterator();
        
        var i:Int = 5; 
        while(iter.hasNext()){
          var row = iter.next();
          verifyEquals(row.find("@label:first").text(),""+i);
          i = i + 1 ;
        }
        verifyEquals(jq(".z-listitem-seld").find(".z-listcell-cnt:first").text(),"10");
    }
   );
  }
}