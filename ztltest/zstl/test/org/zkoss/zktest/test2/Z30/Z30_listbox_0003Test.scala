/* Z30_listbox_0003Test.scala

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Oct 17 16:41:44 CST 2011 , Created by TonyQ
}}IS_NOTE

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zktest.test2.Z30

import org.zkoss.zstl.ZTL4ScalaTestCase
import org.zkoss.ztl.Tags;

/**
 * A test class for bug listbox-0003
 * @author TonyQ
 *
 */
@Tags(tags = "Z30-listbox-0003.zul,Z30,A,E,Listbox")
class Z30_listbox_0003Test extends ZTL4ScalaTestCase {
	
  def testClick() = {
    val zscript = """
			<window>
			Test dynamically select the second listitem.
			<listbox rows="3" width="400px">
				<listhead>
					<listheader label="Population"/>
					<listheader label="Percentage"/>
					<listheader label="Description"/>
				</listhead>
				<listitem id="li0" value="A">
					<listcell label="A. Graduate"/>
					<listcell label="20%"/>
					<listcell label="More or less"/>
				</listitem>
				<listitem id="li1" value="B">
					<listcell label="B. College"/>
					<listcell label="23%"/>
					<listcell label="More or less"/>
				</listitem>
				<listitem value="C" selected="true">
					<listcell label="C. High School"/>
					<listcell label="40%"/>
					<listcell label="More or less"/>
				</listitem>
				<listitem value="D">
					<listcell label="D. Junior High School"/>
					<listcell label="10%"/>
					<listcell label="More or less"/>
				</listitem>
				<listitem value="E">
					<listcell label="F. Others"/>
					<listcell label="17%"/>
					<listcell label="More or less"/>
				</listitem>
			</listbox>
			<button id="btn1" label="Select 2nd" onClick="li1.selected = true"/>
			</window>
	""";
   
    runZTL(zscript,
        () => {
    
        verifyEquals(jq(".z-listitem-seld .z-listcell:first").text(),"C. High School"); 
          
	    click(jq("$btn1"));
	    waitResponse();
	    
	    verifyEquals(jq(".z-listitem-seld .z-listcell:first").text(),"B. College");          
    }
   );
  }
}